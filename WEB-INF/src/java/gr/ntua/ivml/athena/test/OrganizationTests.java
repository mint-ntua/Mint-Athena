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
import gr.ntua.ivml.athena.persistent.Organization;
import gr.ntua.ivml.athena.persistent.User;

import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;

public class OrganizationTests extends TestCase {
	public static final Logger log = Logger.getLogger(OrganizationTests.class);
	
	public void notestSave() {
		Organization o1, o2, o3;
		o1 = new Organization();
		o1.setName( "Arne" );
		o2 = new Organization();
		o2.setName("Marlene");
		o3 = new Organization();
		o3.setName("Iolie");
		o2.setParentalOrganization(o1);
		o3.setParentalOrganization(o1);
		Transaction t = DB.getSession().beginTransaction();
		DB.getOrganizationDAO().makePersistent(o1);
		DB.getOrganizationDAO().makePersistent(o2);
		DB.getOrganizationDAO().makePersistent(o3);
		t.commit();
		DB.getSession().flush();
		DB.getSession().clear();
		DB.newSession();
		Organization o = DB.getOrganizationDAO().findByName("Arne");
		assertNotNull(o);
		List<Organization> l = o.getDependantOrganizations();
		
		assertEquals( 2, l.size());
		o1 = l.get(0);
		assertTrue( o1.getName().equals("Marlene")|| o1.getName().equals("Iolie"));
		DB.getOrganizationDAO().makeTransient(o);
	}
	
	public void testFindPrimary() {
		List<Organization> l = DB.getOrganizationDAO().findPrimary();
		for( Organization o: l ) {
			log.info( "Name: " + o.getName() );
		}
	}
	
	public void testGetUsers() {
		Organization o = DB.getOrganizationDAO().findById(1l, false);
		
		List<User> l = o.getUsers();
		assertTrue( l.size() == 2 );
		o = DB.getOrganizationDAO().findById(4l, false);
		l = o.getUsers();
		assertTrue( l.size() == 1 );
	}
}
