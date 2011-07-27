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

package gr.ntua.ivml.athena.persistent;

import java.util.Date;

public class ThesaurusAssignment {
	private Long dbID;
	//Date the thesaurus was assigned to the node;
	private Date assignDate;
	private User user;
	private Thesaurus thesaurus;
	private DataUpload dataUpload;
	private XpathHolder xpath;
	
	public Long getDbID() {
		return dbID;
	}
	
	public void setDbID(Long dbID) {
		this.dbID = dbID;
	}
	
	public Date getAssignDate() {
		return assignDate;
	}
	public void setAssignDate(Date assignDate) {
		this.assignDate = assignDate;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Thesaurus getThesaurus() {
		return thesaurus;
	}
	
	public void setThesaurus(Thesaurus thesaurus) {
		this.thesaurus = thesaurus;
	}
	
	public XpathHolder getXpath() {
		return xpath;
	}
	
	public void setXpath(XpathHolder xpath) {
		this.xpath = xpath;
	}

	public DataUpload getDataUpload() {
		return dataUpload;
	}

	public void setDataUpload(DataUpload dataUpload) {
		this.dataUpload = dataUpload;
	}
}
