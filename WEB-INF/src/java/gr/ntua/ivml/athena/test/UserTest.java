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

import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.User;

import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

public class UserTest extends TestCase {
	public static final Logger log = Logger.getLogger( UserTest.class ) ;
	
	
	public void setUp() {
		getExample();
	}
	
	public void testGetDataUploads() {
		User u = DB.getUserDAO().findById(1000l, false );
		List<DataUpload> ld = u.getUploads();
		assertTrue( ld.size() > 0 );
		
	}
	
	
	public DataUpload getExample() {
		DataUpload du = DB.getDataUploadDAO().simpleGet("originalFilename='example.zip'");
		assertNotNull( "DataUpload 'example.zip' not uploaded", du );
		return du;
	}
}
