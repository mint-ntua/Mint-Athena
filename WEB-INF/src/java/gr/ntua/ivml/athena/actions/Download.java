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
import gr.ntua.ivml.athena.persistent.Organization;
import gr.ntua.ivml.athena.persistent.Publication;
import gr.ntua.ivml.athena.persistent.Transformation;

import java.io.*;


import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;

import java.io.InputStream;

import java.util.List;

/**
 * Action for download page.
 
 */

public class Download extends GeneralAction {

	protected final Logger log = Logger.getLogger(getClass());
	public String filename;
	private InputStream inputStream;
	private String contentType;
	private String contentDisposition;
	private DataUpload du;
	private long orgId;
	private boolean transformed=false;
	private boolean published=false;

	public void setDbId(String dbId){
		this.du=DB.getDataUploadDAO().getById(Long.parseLong(dbId), false);
		
	}
	
	public InputStream getInputStream()
	{
		return inputStream;
	}
	
	public void setOrgId(long orgid){
		this.orgId=orgid;
	}
	
	
	public void setTransformed(boolean transformed){
		this.transformed=transformed;
	}
	
	public void setPublished(boolean published){
		this.published=published;
	}

	
	public void setInputStream(InputStream is){
		inputStream=is;
	}
	
	public void setContentType(String ct){
	   this.contentType=ct;	
	}
	
	public String getContentType(){
		return(contentType);
	}
	
	
	public void setContentDisposition(String cd){
		   this.contentDisposition=cd;	
		}
	
	public String getContentDisposition(){
		return(contentDisposition);
	}
	
	

	public void setFilename(){
		if(published)
		{   Organization o=DB.getOrganizationDAO().findById(this.orgId, false);
		    String fname=o.getName();
		    fname=fname.replace(' ','_');
			this.filename=fname+"_ESE.zip";
		}
		else{
			if(du.isOaiHarvest()){
					this.filename=du.getOriginalFilename().replace(' ','_')+".zip";
				}else{
					if(du.getOriginalFilename().indexOf(".xml")>-1){
					   this.filename=(du.getOriginalFilename().substring(0, du.getOriginalFilename().indexOf(".xml"))).replace(' ','_')+".zip";
				    }
				    else{this.filename=du.getOriginalFilename().replace(' ','_');}
				
				}
	   }
	}
	  
	public String getFilename(){
		 return(this.filename);
	}


	
	@Action(value="Download")
	public String execute() throws Exception {
		setFilename();
		String fs=System.getProperty("file.separator");
		String newname=filename.substring(filename.lastIndexOf(fs)+1, filename.length());
		this.setContentDisposition("attachment; filename=" + newname);
		if(transformed==false && published==false){
			this.setContentType("application/x-zip-compressed");
			this.setInputStream(du.getDownloadStream());	
		}
        else if (transformed==true)
		{   
        	this.setContentType("application/x-zip-compressed");
			List<Transformation> lt = DB.getTransformationDAO().findByUpload(du);
	    	Transformation tr=lt.get(0);
	    	
			this.setInputStream(tr.getDownloadStream());
		}
        else if (published==true)
		{   
        	this.setContentType("application/x-zip-compressed");
        	Organization o=DB.getOrganizationDAO().findById(this.orgId, false);
        	Publication p=DB.getPublicationDAO().findByOrganization(o);
				    	
			this.setInputStream(p.getDownloadStream());
		}
		return SUCCESS; 
	}
	

}
	  
