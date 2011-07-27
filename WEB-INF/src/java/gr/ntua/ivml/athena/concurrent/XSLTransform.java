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

package gr.ntua.ivml.athena.concurrent;

import gr.ntua.ivml.athena.db.AsyncNodeStore;
import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.db.GlobalPrefixStore;
import gr.ntua.ivml.athena.db.LockManager;
import gr.ntua.ivml.athena.harvesting.util.XMLDbHandler;
import gr.ntua.ivml.athena.persistent.Lock;
import gr.ntua.ivml.athena.persistent.ReportI;
import gr.ntua.ivml.athena.persistent.Transformation;
import gr.ntua.ivml.athena.persistent.XMLNode;
import gr.ntua.ivml.athena.persistent.XmlObject;
import gr.ntua.ivml.athena.persistent.XpathHolder;
import gr.ntua.ivml.athena.persistent.DataUpload.EntryProcessor;
import gr.ntua.ivml.athena.util.NodeStoreI;
import gr.ntua.ivml.athena.util.StringUtils;
import gr.ntua.ivml.athena.xml.transform.XMLFormatter;
import gr.ntua.ivml.athena.xml.transform.XSLTGenerator;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import de.schlichtherle.util.zip.ZipEntry;

public class XSLTransform implements Runnable, NodeStoreI, EntryProcessor, ReportI {
	public final Logger log = Logger.getLogger(XSLTransform.class );
	private static int sessionCounter = 0;

	private Transformation tr;
	private int uniqueSession = -1;
	
	long nodeCount, lastNodeCount, lastReport;
	int  reportCounter;
	int fileCounter, fileCount;
	
	int entryCount;
	AsyncNodeStore ans;
	Connection c;
	XMLReader parser;
	String xsl;

	gr.ntua.ivml.athena.xml.transform.XSLTransform t = new gr.ntua.ivml.athena.xml.transform.XSLTransform();			

	
	public XSLTransform(){}
	
	public XSLTransform(Transformation tr){
		this.tr = tr;
	}



	/**
	 * lock upload and mapping of the transformation or fail if U can't acquire the locks.
	 */
	public void run() {
		
		log.info( "Offline transform started");
		// this might be a used session, the thread is reused
		Session s = DB.newSession();
		StatelessSession ss = DB.getStatelessSession();
		c = ss.connection();
		
		s.beginTransaction();
		try {
			tr = DB.getTransformationDAO().getById(tr.getDbID(), false);
			// new version of the transformation for this session
			if( tr == null ) {
				log.error( "Total desaster, Transformation unavailable, no reporting to UI!!!");
				return;
			}
			log.info( "Transforming " + tr.getDataUpload().getOriginalFilename() + " with " + tr.getMapping().getName());
			// get some locks
			if( ! aquireLocks()) {
				releaseLocks();
				tr.setStatusCode(Transformation.ERROR);
				tr.setStatusMessage("Couldn't aquire locks" );
				DB.commit();
				return;
			}

			transform();
			readNodes();
			AsyncNodeStore.index(tr.getParsedOutput(), this, DB.getStatelessSession().connection());
			tr.setStatusCode(Transformation.OK);
		} catch( Exception e ) {
			// already handled, but needed to skip readNodes or index if transform or readNodes fails
		} catch( Throwable t ) {
			log.error( "uhh", t );
		} finally {
			try {
				// make sure the locks have clean session
				DB.closeStatelessSession();
				DB.getStatelessSession();
				releaseLocks();
				tr.setEndTransform(new Date());
				tr.clearTmpFile();
				DB.commit();
				DB.closeSession();
				DB.closeStatelessSession();
			} catch( Exception e2 ) {
				log.error( "Problem in releasing locks and closing sessions!!", e2 );
			}
		}
	}

		
	private void transform() throws Exception {
		try {
			String mappings = tr.getMapping().getJsonString();
			XSLTGenerator xslt = new XSLTGenerator();

			xslt.setItemLevel(tr.getDataUpload().getItemXpath().getXpathWithPrefix(true));
			xslt.setTemplateMatch(tr.getDataUpload().getItemXpath().getXpathWithPrefix(true));
			xslt.setImportNamespaces(tr.getDataUpload().getItemXpath().getNamespaces(true));
			//xslt.setNamespaces(ftr.getDataUpload().getRootXpath().getNamespaces(true));
			xsl = XMLFormatter.format(xslt.generateFromString(mappings));
			log.debug( "XSL: " + xsl );
			// main item retrieval loop
			tr.startOutput();
			DB.commit();
			fileCount = tr.getDataUpload().getNoOfFiles();
			if( fileCount < 1) fileCount = 1;
			
			// the method to transform each entry in the source is this.processEntry
			tr.getDataUpload().processAllEntries(this);
			tr.finishOutput();
			DB.commit();
		} catch( Exception e) {
			log.error( "Problem during XSLT phase." ,e );
			if( tr.getStatusCode() != Transformation.ERROR ) {
				tr.setStatusCode(Transformation.ERROR);
				tr.setStatusMessage(e.getMessage());
				tr.setEndTransform(new Date());
				DB.commit();
			}
			throw e;
		}
	}
		
	private void releaseLocks() {
		LockManager lm = DB.getLockManager();
		
		Lock l = lm.isLocked(tr.getMapping());
		if(( l!= null ) && l.getUserLogin().equals(tr.getUser().getLogin()) &&
				l.getHttpSessionId().equals(sessionId())) {
			lm.releaseLock(l);
		}
		l = lm.isLocked(tr.getDataUpload());
		if((l!=null) && l.getUserLogin().equals(tr.getUser().getLogin()) &&
				l.getHttpSessionId().equals(sessionId())) {
			lm.releaseLock(l);
		}
	}

	private boolean aquireLocks() {
		String login = tr.getUser().getLogin();
		LockManager lm = DB.getLockManager();
		if( lm.aquireLock(tr.getUser(),sessionId(), tr.getMapping()))
			if( lm.aquireLock(tr.getUser(), sessionId(), tr.getDataUpload()))
				return true;
		return false;			
	}

	private String sessionId() {
		if( uniqueSession < 0 ) uniqueSession = getUniqueSessionNumber();
		return "offlineTransformation" + uniqueSession;
	}
	
	synchronized private static int getUniqueSessionNumber() {
		sessionCounter += 1;
		return sessionCounter;
	}
	
	/**
	 * This part iterates over all transformed entries and parses them.
	 * The xmlObject is then put back into the DB.
	 */
	public void readNodes() throws Exception {
		XmlObject xml=null;
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
			xml = new XmlObject();
			root.xmlObject = xml;
			// TODO: This will create orphan XML objects on failed uploads!!
			tr.setStatusCode(Transformation.INDEXING);
			DB.getXmlObjectDAO().makePersistent(xml);
			ans = new AsyncNodeStore( xml );
			EntryProcessor ep = new EntryProcessor( ) {
				public void  processEntry(de.schlichtherle.util.zip.ZipEntry ze, InputStream is) throws Exception {
					if( ze.isDirectory()) return;
					// makes this process interruptible
					Thread.sleep(0);
					InputSource ins = new InputSource();
					ins.setByteStream(is);
					entryCount+=1;
					parser.parse( ins );
				}
			};
			tr.processAllEntries(ep);
			DB.commit();
			DB.getSession().clear();
			ans.finish();
			DB.getSession().refresh(tr);
			if( tr.getParsedOutput() != null ) {
				DB.getSession().delete(tr.getParsedOutput());
			}
			tr.setParsedOutput(xml);
			DB.getTransformationDAO().makePersistent(tr);
			DB.commit();
		} catch( Exception e ) {
			log.error( "Parsing / indexing of Transformation failed. ", e );
			if( tr.getStatusCode() != Transformation.ERROR) {
				tr.setStatusMessage( "Node Reader failed with: " + e.getMessage()+"\n" );
				tr.setStatusCode(Transformation.ERROR);
			}
			DB.commit();
			// TODO: Safe to delete the XML object here ??
			DB.getXmlObjectDAO().makeTransient(xml);
			DB.getSession().clear();
			ans.abort();	
			throw e;
		}
	}
	
	public void store( XMLNode n ) throws Exception {
		long currentTime = System.currentTimeMillis();
		if(( currentTime - lastReport ) > 20000 ) {
			int nodeRate = (int) ((nodeCount-lastNodeCount)*1000/(currentTime - lastReport));
			StringBuffer msg = new StringBuffer();
			if( entryCount>1 ) msg.append(" Files: " + entryCount );
			if( nodeCount>1 ) msg.append( " Nodes: "+ nodeCount );
			msg.append( " Rate: "+nodeRate+ " nodes/sec" );
			tr.setStatusMessage(msg.toString());
			log.info( tr.getDataUpload().getDbID() + " " + msg );
			DB.getTransformationDAO().makePersistent(tr);
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
	
	@Override
	/**
	 * Entry processing to do xsl transform on each of the input files
	 * in an upload.
	 */
	public void processEntry(ZipEntry ze, InputStream is)
	throws Exception {
		fileCounter+=1;
		// check if we want a report
		// we only generate 20 progress reports
		// but only every 10 seconds
		int report = fileCounter*20/fileCount;
		if( report > reportCounter ) {
			if( System.currentTimeMillis() - lastReport > 10000l ) {
				tr.setStatusMessage("Processed  " + fileCounter +
						" of " + fileCount + "  files.");
				DB.commit();
				lastReport = System.currentTimeMillis();
			}
			reportCounter+=1;
		}
		if( ze.isDirectory() ) return;
		if( !ze.getName().endsWith("xml")) return;
		tr.nextOutputFile();
		// transform into pipe (pos) and format from pipe (pis) into the zipped output (os)
		final OutputStream os = tr.getStreamToOutput();
		PipedOutputStream pos = new PipedOutputStream();
		final PipedInputStream pis = new PipedInputStream( pos );
		Runnable formatter = new Runnable() {
			public void run() {
				XMLFormatter.format(pis, os);
			}
		};
		
		try {
			Queues.queue(formatter, "now");
			// use the queues .. Future<?> f = threadPool.submit(formatter);
			log.debug("Zip entry size: "+ze.getSize());
			//if >10 MB 
			if(ze.getSize()>10485760)
				t.transformStream(is, xsl, pos);
			else{
			  t.transform(is, xsl, pos );}
			pos.flush();
			pos.close();
			Queues.join( formatter );
			// use the queues f.get();
		} catch( Exception e ) {
			log.error( "Error during transformation", e );
			log.error( "Problem in XSLT transformation" ,e );
			tr.setStatusCode(Transformation.ERROR);
			tr.setStatusMessage(e.getMessage());
			tr.setEndTransform(new Date());
			DB.commit();
			throw e;
		} finally {
			os.close();
			pis.close();
			pos.close();
		}
	}

	@Override
	public void report(String msg) {
		tr.setStatusMessage(msg);
	}

	@Override
	public void reportError() {
		// TODO Auto-generated method stub
		tr.setStatusCode(Transformation.ERROR);
	}
}
