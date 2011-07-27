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
import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.User;
import gr.ntua.ivml.athena.util.Import;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({
	@Result(name="error", location="importStatus.jsp"),
	@Result(name="success", location="importStatus.jsp")
})

public class ImportStatus extends GeneralAction{

	protected final Logger log = Logger.getLogger(getClass());
    
	private String importId;
   
    private Import imp;
    private String orgId;
    private String userId;
    private String uploader;
	
	@Action(value="ImportStatus")
	public String execute() throws Exception {
		log.debug("ImportStatus controller for id:"+importId);		
		return "success";
	}

	public String getImportId(){
		return this.importId;
	}
	
	public long getDbID(){
		return this.imp.getDbID();
	}

	public String getOrgId(){
		return this.orgId;
	}
	
	public String getUserId(){
		return this.userId;
	}
	
	public long getUploader(){
		return this.imp.getUploader();
	}
	
	public Import getImp(){
		return this.imp;
	}
	
	public String getStatus(){
		return this.imp.getStatus();
		
	}
	
	public boolean isLocked() {
		return getImp().isLocked(getUser(), getSessionId());
	}
	
	
	
	public String getFormattedMessage(){
		
		return this.imp.getFormattedMessage();
		
	}
	
	
	public String getStatusIcon(){
		
		return this.imp.getStatusIcon();
	}
	
	public boolean isLido(){
		return this.imp.isLido();
	}
	
	public boolean isLido10(){
		return this.imp.isLido10();
	}
	
	public void setImportId(String id){
		DataUpload du=null;
		this.importId=id;
		du=DB.getDataUploadDAO().getById(Long.parseLong(id), false);
		if(du!=null){
		
		  this.orgId=""+(du.getOrganization().getDbID());
		  this.userId=""+du.getUploader().getDbID();
		  this.imp=new Import(du);
		  
		}
		
	}
	
	
}