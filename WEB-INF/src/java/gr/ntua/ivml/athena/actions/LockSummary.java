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
import gr.ntua.ivml.athena.persistent.Lock;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import java.util.ArrayList;
import java.util.List;

@Results({
	  @Result(name="input", location="locksummary.jsp"),
	  @Result(name="error", location="locksummary.jsp"),
	  @Result(name="success", location="locksummary.jsp" )
	})

public class LockSummary extends GeneralAction {
	  
	  protected final Logger log = Logger.getLogger(getClass());

	  private List<Lock> locks;
	  private String lockaction="";
	  private ArrayList lockCheck=new ArrayList();

	public void setLockaction(String lockaction) {
		this.lockaction = lockaction;
	}
	
	
	public List getLocks(){

		locks = DB.getLockManager().findByUser(this.user);
		List<Lock> newlocks=new ArrayList<Lock>();
		for(Lock l: locks){
			if(l.getHttpSessionId()!=null && l.getHttpSessionId().indexOf("offlineTransformation")==0){
			}else{newlocks.add(l);}
		}
		//locks = DB.getLockManager().findBySession( this.sessionId);
		
		return newlocks;
	}
	
	public void setLockCheck(String lockCheck){
		this.lockCheck=new ArrayList();
		if(lockCheck.trim().length()>0){
			String[] chstr=lockCheck.split(",");
			
		   java.util.Collection c=java.util.Arrays.asList(chstr);
		   this.lockCheck.addAll(c);
		}
	}
	
	
	@Action(value="LockSummary")
    public String execute() throws Exception {
		if(lockaction.equalsIgnoreCase("delete")){
			try{
				
				 for(int i=0;i<lockCheck.size();i++)
				 { 
					 Lock l=DB.getLockManager().getByDbID(Long.parseLong((String)lockCheck.get(i)));
					 boolean res=DB.getLockManager().releaseLock(l);
							
				 }
			}catch (Exception e){}
		}
	    return SUCCESS;
    }

	
}