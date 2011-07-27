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
import gr.ntua.ivml.athena.db.MappingDAO;
import gr.ntua.ivml.athena.persistent.Mapping;
import gr.ntua.ivml.athena.persistent.Organization;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

public class MappingTest extends TestCase {
	public void testDb() {
		MappingDAO md = DB.getMappingDAO();
		assertNotNull( md );
		// make a new Mapping
		Mapping m = new Mapping();
		Organization o1 = DB.getOrganizationDAO().getById(1l, false);
		m.setOrganization( o1 );
		m.setCreationDate(new Date());
		m.setName( "Some Name");
		md.makePersistent(m);
		DB.getSession().clear();
		List<Mapping> l = md.findByOrganization(o1);
		assertTrue( l.size() > 0  );
		for( Mapping ma: l ) {
			md.makeTransient(ma);
		}
		DB.getSession().clear();
		m = new Mapping();
		m.setCreationDate(new Date());
		m.setName( "Some Name");
		m.setOrganization(null);
		md.makePersistent(m);
		l = md.findByOrganization( null );
		assertTrue( l.size() > 0  );
		
	}
}
