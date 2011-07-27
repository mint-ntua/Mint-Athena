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

import java.util.List;

import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.Organization;
import gr.ntua.ivml.athena.persistent.Thesaurus;
import gr.ntua.ivml.athena.persistent.ThesaurusAssignment;
import gr.ntua.ivml.athena.persistent.XMLNode;
import gr.ntua.ivml.athena.persistent.XpathHolder;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;

public class ThesaurusDAO extends DAO<Thesaurus, Long> {

	public static final Logger log = Logger.getLogger( ThesaurusDAO.class );
	
	public List<Thesaurus> findByOrganization( Organization o) {
		List<Thesaurus> l = getSession().createQuery( "from Thesaurus where organization = :org order by title ASC" )
			.setEntity("org", o)
			.list();
		return l;
	}
	
	
	//Should try to optimize this...
	public List<Thesaurus> findByRootOrganization( Organization o) {
		Organization root = o;
		while(root.getParentalOrganization() != null) {
			root.getParentalOrganization();
		}
		List<Thesaurus> result;
		return findByOrganizationAndDependants(root);
	}
	
	public List<Thesaurus> findByOrganizationAndDependants(Organization o) {
		List<Thesaurus> result;
		result = findByOrganization(o);
		List<Organization> deps = o.getDependantRecursive();
		for(Organization org: deps) {
			result.addAll(findByOrganization(org));
		}
		
		return result;
	}
	
	public List findDistinctXpathsByThesaurusId(Thesaurus t) {
		return getSession().createQuery("select distinct ta.xpath.xpath from ThesaurusAssignment ta where ta.thesaurus=:t")
		.setEntity("t",t)
		.list();
	}

	public boolean delete(Long id) {
		Thesaurus ta = findById(id, false);
		if(ta == null)
			return false;
		
		getSession().delete(ta);
		return true;
	}
}
