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

package gr.ntua.ivml.athena.test;

import gr.ntua.ivml.athena.concurrent.Queues;
import gr.ntua.ivml.athena.concurrent.UploadIndexer;
import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.db.GlobalPrefixStore;
import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.User;
import gr.ntua.ivml.athena.persistent.XmlObject;
import gr.ntua.ivml.athena.persistent.XpathHolder;
import gr.ntua.ivml.athena.util.Config;

import java.io.File;

import org.hibernate.Transaction;

import junit.framework.TestCase;

public class TestUploadIndexer extends TestCase {
	public void notestUploadIndexer() {
		// use an appropriate zip file
		GlobalPrefixStore gps;
		
		String zipFilename = Config.get( "testZip");
		assertNotNull("testZip needs configuration", zipFilename);
		assertTrue( zipFilename + " not readable", new File( zipFilename ).canRead());
		// Use a test user
		User u = DB.getUserDAO().findById(1000l, false);
		
		// create a data upload
		DataUpload du = DataUpload.create( u, "example.zip", "" );
		du.setSchemaName( "SomeLidoClone");
		
		DB.getDataUploadDAO().makePersistent(du);
		
		// use the upload indexer to put it in the database
		UploadIndexer ui = new UploadIndexer(du, UploadIndexer.SERVERFILE);
		ui.setServerFile(zipFilename);
		Queues.queue(ui, "net" );
		Queues.join( ui );
		Queues.join( ui );
	}
	
	public void testNoCounts() {
		DB.newSession();
		Transaction tx = DB.getSession().beginTransaction();
		User u = DB.getUserDAO().findById(1000l, false);
		
		// create a data upload
		DataUpload du = DataUpload.create( u, "noTextCount.zip", "" );
		DB.getDataUploadDAO().makePersistent(du);
		DB.commit();
		// use the upload indexer to put it in the database
		UploadIndexer ui = new UploadIndexer(du, UploadIndexer.SERVERFILE);
		ui.setServerFile("WEB-INF/data/beethoven_i.zip");
		Queues.queue(ui, "net" );
		Queues.join( ui );
		Queues.join( ui );
	
		assertTrue( true );
		DB.getSession().refresh(du);
		XmlObject xml = du.getXmlObject();
		XpathHolder xp1 = xml.getRoot();
		XpathHolder xp2 = xp1.getByRelativePath("/Dokument/Bildnis/DocID");
		assertEquals( "Node expected to be there 10 times", 10, xp2.getCount() );
		xp2 = xp2.getByRelativePath("text()" );
		assertEquals( "Node expected to be there 10 times", 10, xp2.getCount() );
		DB.commit();
		DB.getDataUploadDAO().makeTransient(du);
		DB.closeSession();
	}
}
