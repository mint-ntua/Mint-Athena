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
import gr.ntua.ivml.athena.mapping.MappingSummary;
import gr.ntua.ivml.athena.persistent.Lock;
import gr.ntua.ivml.athena.persistent.Mapping;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;




public class LockRelease extends GeneralAction  implements 
ServletRequestAware,ServletResponseAware{
	  
	  private HttpServletRequest request;
	  private HttpServletResponse response;
	  protected final Logger log = Logger.getLogger(getClass());

	  private long lockId;
		 
	  private long mapping;
			
	  
	  public void setServletRequest(HttpServletRequest request){
	    this.request = request;
	  }

	  public HttpServletRequest getServletRequest(){
	    return request;
	  }

	  public void setServletResponse(HttpServletResponse response){
	    this.response = response;
	  }

	  public HttpServletResponse getServletResponse(){
	    return response;
	  }

	
	public long getLockId() {
		return lockId;
	}

	public void setLockId(long lockId) {
		this.lockId = lockId;
	}
	
	
	public void setMapping(long mapping) {
		this.mapping = mapping;
	}

	public long getMapping(){
		return mapping;
	}
	
	

	
	@Action(value="LockRelease")
    public String execute() throws Exception {
		    boolean res=false;
		    
			Mapping em=DB.getMappingDAO().findById(getMapping(), false);
				
			if(em.getJsonString()!=null){	
				
				if(em.getJsonString().isEmpty()==false && (MappingSummary.getMissingMappings(em)==null || MappingSummary.getMissingMappings(em).size()==0)){
	    		 em.setFinished(true);
			     }
			    else{em.setFinished(false);}
			}else{em.setFinished(false);}  
			DB.getMappingDAO().makePersistent(em);
			DB.commit();
			Lock l=DB.getLockManager().getByDbID(getLockId());
			res=DB.getLockManager().releaseLock(l);
			
			return res+"";
    }

	
}