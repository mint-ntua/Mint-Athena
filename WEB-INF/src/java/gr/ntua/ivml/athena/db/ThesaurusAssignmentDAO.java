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
import gr.ntua.ivml.athena.persistent.Thesaurus;
import gr.ntua.ivml.athena.persistent.ThesaurusAssignment;
import gr.ntua.ivml.athena.persistent.XpathHolder;

public class ThesaurusAssignmentDAO  extends DAO<ThesaurusAssignment, Long> {

	public List<ThesaurusAssignment> getByThesaurus(Thesaurus t) {
		return getSession().createQuery("from ThesaurusAssignment where thesaurus=:thesaurus")
		.setEntity("thesaurus", t)
		.list();
	}
	
	public List<ThesaurusAssignment> getByThesaurusAndDataUpload(Thesaurus t, DataUpload du) {
		return getSession().createQuery("from ThesaurusAssignment where thesaurus=:thesaurus and dataUpload=:du")
		.setEntity("thesaurus", t)
		.setEntity("du", du)
		.list();
	}
	
	/**
	 * Checks if there's an assign for the specified xpath and thesaurus that have been applied on mapping specified
	 * by the data upload.
	 * @param xpath the xpath to check if is assigned to a thesaurus
	 * @param t the thesaurus
	 * @param du the mapping
	 * @return true if exists, false if not
	 */
	public boolean existsAssignment(XpathHolder xpath, Thesaurus t, DataUpload du) {
		List<ThesaurusAssignment> list = getSession().createQuery("from ThesaurusAssignment where thesaurus=:thesaurus and dataUpload=:du and xpath=:xpath")
		.setEntity("thesaurus", t)
		.setEntity("du", du)
		.setEntity("xpath", xpath)
		.list();
		if((list == null) || (list.size() == 0))
			return false;
		
		return true;
	}
	
	public boolean delete(Long id) {
		ThesaurusAssignment ta = findById(id, false);
		if(ta == null)
			return false;
		
		getSession().delete(ta);
		return true;
	}
}
