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
import gr.ntua.ivml.athena.persistent.XpathHolder;
import gr.ntua.ivml.athena.util.Transform;

import gr.ntua.ivml.athena.util.Import;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({
	@Result(name="error", location="transfromStatus.jsp"),
	@Result(name="success", location="transformStatus.jsp")
})

public class TransformStatus extends GeneralAction{

	protected final Logger log = Logger.getLogger(getClass());
    
	private String importId;
    private Import imp;
    private String orgId;
    private String userId;
    
    public Transform trans=null;
	
	@Action(value="TransformStatus")
	public String execute() throws Exception {
		log.debug("TransformStatus controller");
		
		return SUCCESS;
	}

	public Transform getTrans(){
		return this.trans;
	}
	
   public long getDbId(){
	   return this.imp.getDbID();
   }
	
	public String getImportId(){
		return this.importId;
	}

	public String getOrgId(){
		return this.orgId;
	}
	
	public String getUserId(){
		return this.userId;
	}
	
	public Import getImp(){
		return this.imp;
	}
	
	public String getStatus(){
		return this.trans.getStatus();
		
	}
	
	
	public boolean isLocked() {
		
		// instead check if transform is locked
		return getImp().isLocked(getUser(), getSessionId());
	}
	
    public boolean isStale() {
		
		// instead check if transform is locked
		return trans.isStale();
	}
	
	
	
	public String getMessage(){
		return trans.getMessage();
	}
	
	
	public String getStatusIcon(){
		
		return this.trans.getStatusIcon();
	}
	
	
	public void setImportId(String id){
		
		this.importId=id;
		
		DataUpload du=DB.getDataUploadDAO().getById(Long.parseLong(id), false);
		if(du!=null){
		this.orgId=""+(du.getOrganization().getDbID());
		this.userId=""+du.getUploader().getDbID();
		
		this.imp=new Import(du);
		trans=this.imp.getTrans();
		}
	}
	
	public boolean isRootDefined(){
		return this.imp.isRootDefined();
		
	}
	
	
}