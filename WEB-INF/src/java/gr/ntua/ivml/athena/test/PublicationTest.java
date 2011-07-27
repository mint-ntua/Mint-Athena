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
import gr.ntua.ivml.athena.persistent.Organization;
import gr.ntua.ivml.athena.persistent.Publication;

import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

public class PublicationTest extends TestCase {
	public static final Logger log = Logger.getLogger(PublicationTest.class);
	
	public void testDb() {
		// this is not needed in the webapp, automatic ..
		log.info( "Check yourself if this works, no asserts in here" );
		DB.getSession().beginTransaction();
		
		Publication p = new Publication();
		Organization org = getOrg();
		p.setPublishingOrganization(org);
		p.setTargetSchema("some_lido");
		for( DataUpload du: org.getDataUploads()) {
			p.addUpload(du);
		}
		DB.getPublicationDAO().makePersistent(p);
		// you don't need this in the webapp, happens automatically
		
		p = new Publication();
		p.setPublishingOrganization(org);
		p.setTargetSchema("some_other_schema");
		for( DataUpload du: org.getDataUploads()) {
			p.addUpload(du);
		}
		DB.getPublicationDAO().makePersistent(p);
		DB.commit();
		DB.closeSession();
	}

	public void testRet() {
		Publication res = null;
		DB.getSession().beginTransaction();
		
		for( Publication p: DB.getPublicationDAO().findAll()) {
			try {
				p.upToDateCheck();
				res = p;
			} catch( Exception e ) {
				log.info( e );
			}
		}
		
		res.process();
		DB.closeSession();
	}
	
	
	private Organization getOrg() {
		Organization result = null;
		for( Organization org: DB.getOrganizationDAO().findAll() ) {
			List<DataUpload> l = org.getDataUploads();
			if( l.size() >= 2 ) {
				result = org;
				break;
			}
		}
		return result;
	}
}
