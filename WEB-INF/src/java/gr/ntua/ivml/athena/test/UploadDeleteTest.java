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

import java.io.File;

import gr.ntua.ivml.athena.concurrent.Queues;
import gr.ntua.ivml.athena.concurrent.UploadIndexer;
import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.User;
import gr.ntua.ivml.athena.util.Config;
import junit.framework.TestCase;

public class UploadDeleteTest extends TestCase {

	// test delete a finished upload
	// sometimes it deletes dependents and sometimes it doesn't, sigh
	public void testFinished() {
		DB.getSession().beginTransaction();
		UploadIndexer ui = startUpload();
		Queues.join(ui);
		Queues.join(ui);
		DB.commit();
		DB.getSession().clear();
		DB.getSession().beginTransaction();

		DataUpload du = DB.getDataUploadDAO().findById(ui.getDataUpload().getDbID(), false);

		boolean result = DB.getDataUploadDAO().makeTransient(du);
		DB.commit();
		assertTrue( "makeTransient returned false", result );
		DB.getSession().clear();
		DataUpload du2 = DB.getDataUploadDAO().getById(du.getDbID(), false);
		assertNull( "DataUpload not deleted! " + du.getDbID(), du2 );
		DB.closeSession();
	}
	
	// remove the uploadindexer and clean db
	public void notestInterrupting() {
		DB.getSession().beginTransaction();
		UploadIndexer ui = startUpload();
		DB.getSession().beginTransaction();
		DataUpload du = DB.getDataUploadDAO().findById(ui.getDataUpload().getDbID(), false);
		boolean result = DB.getDataUploadDAO().makeTransient(du);
		DB.commit();
		assertTrue( "makeTransient returned false", result );
		DB.getSession().clear();
		DataUpload du2 = DB.getDataUploadDAO().getById(du.getDbID(), false);
		assertNull( "DataUpload not deleted! " + du.getDbID(), du2 );

		// now with some delay
		ui = startUpload();
		DB.getSession().beginTransaction();

		du = DB.getDataUploadDAO().findById(ui.getDataUpload().getDbID(), false);
		try {
			Thread.sleep( 3000 );
		} catch(Exception e ) {}
		result = DB.getDataUploadDAO().makeTransient(du);
		DB.commit();
		assertTrue( "makeTransient returned false", result );
		DB.getSession().clear();
		du2 = DB.getDataUploadDAO().getById(du.getDbID(), false);
		assertNull( "DataUpload not deleted! " + du.getDbID(), du2 );
		DB.closeSession();
	}
	
	
	private UploadIndexer startUpload() {
		// use an appropriate zip file
		String zipFilename = Config.get( "testZip");
		assertNotNull("testZip needs configuration", zipFilename);
		assertTrue( zipFilename + " not readable", new File( zipFilename ).canRead());
		// Use a test user
		User u = DB.getUserDAO().findById(1000l, false);
		
		// create a data upload
		DataUpload du = DataUpload.create( u, "example.zip", "" );
		DB.getDataUploadDAO().makePersistent(du);
		DB.commit();
		// use the upload indexer to put it in the database
		UploadIndexer ui = new UploadIndexer(du, UploadIndexer.SERVERFILE);
		ui.setServerFile(zipFilename);
		Queues.queue(ui, "net");
		return ui;
	}
}
