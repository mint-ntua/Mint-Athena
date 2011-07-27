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

package gr.ntua.ivml.athena.db;


import gr.ntua.ivml.athena.persistent.Organization;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.UniqueConstraint;

public class OrganizationDAO extends DAO<Organization, Long> {
	
	public List<Organization> findPrimary() {
		List<Organization> result = Collections.emptyList();
		try {
			result = getSession().createQuery(" from Organization where parentalOrganization is null" ).list();
		} catch( Exception e ) {
			log.error( "Problems: ", e );
		}
		return result;
	}
	
	public	Organization findByName( String name ) {
		Organization result = null;
		try {
			result = (Organization) getSession()
				.createQuery(" from Organization where shortName=:name" )
				.setString("name", name )
				.uniqueResult();
		} catch( Exception e ) {
			log.error( "Problems: ", e );
		}
		return result;
	}

	public List<Organization> findByCountry( String country ) {
		List<Organization> result = null;
		result = getSession()
			.createQuery("from Organization where country=:country " 
						+" order by englishName" )
			.setString("country", country ) 
			.list();
		return result;
	}
	
	public List<Organization> findAll() {
		List<Organization> result = null;
		result = getSession()
			.createQuery("from Organization " 
						+" order by englishName" )
			.list();
		return result;
	}
}
