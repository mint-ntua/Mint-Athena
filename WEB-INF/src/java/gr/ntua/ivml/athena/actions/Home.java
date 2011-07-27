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


package gr.ntua.ivml.athena.actions;

import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.persistent.Organization;

import java.util.ArrayList;
import java.util.List;


import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({
	  @Result(name="input", location="home.jsp"),
	  @Result(name="error", location="home.jsp"),
	  @Result(name="success", location="home.jsp")
	})

public class Home extends GeneralAction{

	protected final Logger log = Logger.getLogger(getClass());
	
      private List<Organization> allOrgs;
      private List<String> countries=new ArrayList<String>();
      
      @Action(value="Home")
      public String execute() throws Exception {
    	  log.debug("Home controller");
  		return SUCCESS;
      }

      public List<Organization> getAllOrgs(){
      	allOrgs =DB.getOrganizationDAO().findAll();
      	return allOrgs;
      } 
      
      public List<String> getCountries(){
    		countries = new ArrayList<String>(java.util.Arrays.asList("Austria", "Belgium", "Bulgaria","Cyprus", "Czech Rep.", "Denmark", "Estonia",
    				"Finland", "France", "Germany", "Greece","Hungary", "Ireland", "Italy", "Israel", "Latvia",
    				  "Lithuania","Luxembourg","Malta","Netherlands","Poland","Portugal",
    				"Romania","Russia","Slovakia","Slovenia",
    				"Spain","Sweden", "Switzerland", "United Kingdom", "Europe", "International"));
    		return countries;
        }
      
  	
  	String orgId;
  	
  	public String getOrgId() {
  		return orgId;
  	}


  	public void setOrgId(String orgId) {
  		this.orgId = orgId;
  	}


  	public List<Organization> getOrganizations() {
  		return  user.getAccessibleOrganizations();
  	}
}