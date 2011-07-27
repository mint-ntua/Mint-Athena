/*
This file is part of mint-athena. mint-athena services compose a web based platform that facilitates aggregation of cultural heritage metadata.
   Copyright (C) <2009-2011> Anna Christaki, Arne Stabenau, Costas Pardalis, Fotis Xenikoudakis, Nikos Simou, Nasos Drosopoulos, Vasilis Tzouvaras

   mint-athena program is free software: you can redistribute it and/or
modify
   it under the terms of the GNU Affero General Public License as
   published by the Free Software Foundation, either version 3 of the
   License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Affero General Public License for more details.

   You should have received a copy of the GNU Affero General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package gr.ntua.ivml.athena.util;

import gr.ntua.ivml.athena.db.AsyncNodeStore;
import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.db.GlobalPrefixStore;
import gr.ntua.ivml.athena.harvesting.util.XMLDbHandler;
import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.XMLNode;
import gr.ntua.ivml.athena.persistent.XmlObject;
import gr.ntua.ivml.athena.persistent.XpathHolder;
import gr.ntua.ivml.athena.persistent.DataUpload.EntryProcessor;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.zip.ZipEntry;

import org.apache.log4j.Logger;
import org.hibernate.StatelessSession;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class NodeReader implements NodeStoreI {
	DataUpload du;
	Connection c;
	long lastReport;
	long lastNodeCount;
	long nodeCount;
	long entryCount;
	int tmpUploadId; 
	String entryName;
	AsyncNodeStore ans;
	XMLReader parser;
	StatelessSession s;
	
	
	public  final Logger log = Logger.getLogger( NodeReader.class );
	public NodeReader( DataUpload du ) {
		this.du = du;

		lastReport = System.currentTimeMillis();
		nodeCount = 0;
		entryCount = 0;
	}
	
	public void readNodes() throws Exception {
		this.s = DB.getStatelessSession();
		this.c = s.connection();
		try {
			parser = org.xml.sax.helpers.XMLReaderFactory.createXMLReader(); 
			parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			XMLDbHandler handler = new XMLDbHandler( this );
			XpathHolder root = new XpathHolder();
			root.name = "";
			root.parent = null;
			root.xpath = "";
			handler.setRoot(root);
			parser.setContentHandler(handler);
			XmlObject xml = new XmlObject();
			root.xmlObject = xml;
			// TODO: This will create orphan XML objects on failed uploads!!
			DB.getXmlObjectDAO().makePersistent(xml);
			ans = new AsyncNodeStore( xml );
			EntryProcessor ep = new EntryProcessor( ) {
				public void  processEntry(de.schlichtherle.util.zip.ZipEntry ze, InputStream is) throws Exception {
					if( ze.isDirectory()) return;
					entryName = ze.getName();
					if( !entryName.endsWith(".xml")) return;
					// makes this process interruptible
					Thread.sleep(0);
					InputSource ins = new InputSource();
					ins.setByteStream(is);
					nextEntry();
					parser.parse( ins );
				}
			};
			du.processAllEntries(ep);
			DB.commit();
			DB.getSession().clear();
			ans.finish();
			DB.getSession().refresh(du);
			du.setNodeCount(nodeCount);	
			du.setXmlObject(xml);
			DB.getDataUploadDAO().makePersistent(du);
			DB.commit();
		} catch( Exception e ) {
			log.error( "Parsing / storing of DataUpload failed. ", e );
			if( du.getStatus() != DataUpload.ERROR) {
				du.setMessage( "Node Reader failed with: " + e.getMessage()+"\n" );
				du.setStatus(DataUpload.ERROR);
			}
			DB.commit();
			// TODO: Safe to delete the XML object here ??
			DB.getSession().clear();
			ans.abort();
			throw e;
		}
	}
	
	public void nextEntry() {
		entryCount++;
	}
	
	public void store( XMLNode n ) throws Exception {
		long currentTime = System.currentTimeMillis();
		if(( currentTime - lastReport ) > 20000 ) {
			int nodeRate = (int) ((nodeCount-lastNodeCount)*1000/(currentTime - lastReport));
			du.setNodeCount(nodeCount);
			StringBuffer msg = new StringBuffer();
			if( entryCount>1 ) msg.append(" Files: " + entryCount );
			if( nodeCount>1 ) msg.append( " Nodes: "+ nodeCount );
			msg.append( " Rate: "+nodeRate+ " nodes/sec" );
			du.setMessage(msg.toString());
			log.info( du.getOriginalFilename() + " " + msg );
			DB.getDataUploadDAO().makePersistent(du);
			DB.commit();
			lastReport = currentTime;
			lastNodeCount = nodeCount;
		}
		if( n.getXpathHolder() != null ) {
			if( !DB.getSession().contains(n.getXpathHolder())) {
				DB.getSession().save( n.getXpathHolder());
				// commit not needed to get dbID of pathHolder
				// DB.commit();
			}
			// this updates the global prefix store
			if( !StringUtils.empty( n.getXpathHolder().getUri()))
				GlobalPrefixStore.createPrefix(n.getXpathHolder().getUri(), n.getXpathHolder().getUriPrefix());
		}
		// store the node asynchronous from reading, multithreading ...
		ans.store(n);
		nodeCount++;
	}
	
	/**
	 * Allocating node ids in packs of 1000. The sequence will support this.
	 * Whoever has x000 can use ids x000 until x999.
	 * @return
	 */
	public long[] newIds() {
		return AsyncNodeStore.getIds(c);
	}
	
}
