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

package gr.ntua.ivml.athena.util;

import java.util.List;
import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.Mapping;
import gr.ntua.ivml.athena.persistent.Transformation;


public class Transform {

   
	private long importId;
    private String statusIcon="";
    private String status="NOT DONE";
    private boolean isStale=false;
    private Import imp;
    private Mapping mp;
    private String message="";
  
    public Transformation tr=null;
    
   
	

	public void setTr(){
		DataUpload du=DB.getDataUploadDAO().getById(getImp().getDbID(),false);
		List<Transformation> lt = DB.getTransformationDAO().findByUpload(du);
		if(lt!=null && lt.size()!=0){
		 
			this.tr=lt.get(0);
			mp=tr.getMapping();
			
			this.setStatus();
			this.setMessage();
			this.setStatusIcon();
			this.isStale=tr.isStale();
		}
		else{}
		
		}
	
	public long getImportId(){
		return this.importId;
	}
	
	public long getDbID(){
		return this.imp.getDbID();
	}

	public boolean isStale(){
		return isStale;
	}
	
	public Import getImp(){
		return this.imp;
	}
	
	public void setStatus(){
		if(tr!=null){
		 if(tr.getStatusCode()==0){
		    	status="OK";
				
			}
			else if(tr.getStatusCode()==-1){
				status="ERROR";
				
			}
			else if(tr.getStatusCode()==1){
				status="IDLE";
				
			}
			else if(tr.getStatusCode()==2){
				status="WRITING";
				
			}
			else if(tr.getStatusCode()==3){
				status="UPLOADING";
				
			}
			else if(tr.getStatusCode()==4){
				status="INDEXING";
				
			}
			else{
				status="UNKNOWN";
				
			}
		}
		
		
	}
	
	public String getStatus(){
		
		return this.status;
	}
	
	

	
	public void setMessage(){
		
		if(tr!=null){
	      this.message=tr.getStatusMessage();
	      //MESSAGE NEEDS TO BE FIXED IN DB
	      if(tr.getStatusCode()==0){
	    	  this.message="Transformed using mappings "+mp.getName()+".";
	      }
	      if(tr.isStale()){this.message+=" Transformation is now stale due to mappings change. Please transform again.";}
	     }
		
	}
	
	public String getMessage(){
		return this.message;
	}
	
	
	public void setStatusIcon(){
		//instead of checking import check transformation 
		if(tr!=null){
	    if(tr.getStatusCode()==0){
	    	
			statusIcon="images/okblue.png";
		}
		else if(tr.getStatusCode()==-1){
			
			statusIcon="images/problem.png";
		}
		else if(tr.getStatusCode()==1){
		
			statusIcon="images/loader.gif";
		}
		else if(tr.getStatusCode()==2){
			
			statusIcon="images/loader.gif";
		}
		else if(tr.getStatusCode()==3){
			
			statusIcon="images/loader.gif";
		}
		else if(tr.getStatusCode()==4){
			
			statusIcon="images/loader.gif";
		}
	    if(tr.isStale()){
	    	
	    	statusIcon="images/redflag.png";
	    }
		}
		
	}
	
	public String getStatusIcon(){
		return this.statusIcon;
	}
	
	
	public Transform(long id){
		
		this.importId=id;
		
		DataUpload du=DB.getDataUploadDAO().getById(id, false);
		if(du!=null){
			this.imp=new Import(du);
		    this.setTr();
		}
	}
	
	
}