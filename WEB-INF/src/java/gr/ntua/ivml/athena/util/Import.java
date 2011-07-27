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

import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.Publication;
import gr.ntua.ivml.athena.persistent.XpathHolder;
import gr.ntua.ivml.athena.util.Transform;
import gr.ntua.ivml.athena.persistent.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Import {
	private DataUpload du;
	private String message="";
	private String status="";
	private String formattedMessage="";
	private String statusIcon="";
	private Transform trans;
	private Publish pub;

	
	public Import( DataUpload du ) {
			this.du = du;
		
	}
		
	public boolean isLido(){
		   return du.isLido() || du.isLido10();	
			
		}
		
	public boolean isLido10(){
		   return du.isLido10();	
			
		}
		
	public Publish getPub(){
		if(pub==null){
		   this.pub=new gr.ntua.ivml.athena.util.Publish(du.getDbID());
		}	
		return pub;
	}
	
	public Transform getTrans(){
		if(trans==null){
		  this.trans=new gr.ntua.ivml.athena.util.Transform(du.getDbID());
		}
		return trans;
	}
	
	public long getUploader(){
		return this.du.getUploader().getDbID();
	}
	
	public String getName() {
		return du.getOriginalFilename();
		
	}
	
	public String getSize() {
		if( du.getUploadSize() > 0 )
			return Long.toString( du.getUploadSize());
		else 
			return "";
	}
	
    public String getFormattedMessage(){
		
		this.formattedMessage=this.getMessage();
		
		this.formattedMessage=formattedMessage.replace("\n", "\\n");
		return this.formattedMessage;
		
	}
	
	
	public String getDate() {
		Date d = du.getUploadDate();
		if( d == null ) return "";
		else
		return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(d);
	}
	
	public String getStatus() {
		 this.status=du.getStatusText();
		 return this.status;
	}
	
	public String getMessage() {
		return du.getMessage().replaceAll("\n", "\\\\n");
	}
	
	public String getStatusIcon(){
		if(this.getStatus().equalsIgnoreCase("OK")){
			this.statusIcon="images/ok.png";
		}
		else if(this.getStatus().equalsIgnoreCase("ERROR")){
			this.statusIcon="images/problem.png";
		}
		else{
			this.statusIcon="images/loader.gif";
		}
		return this.statusIcon;
	}
	
	
	public long getDbID() {
		return du.getDbID();
	}
	public int getNoOfFiles() {
		return du.getNoOfFiles();
	}
	
	public String getSizeDescription() {
		long size = du.getUploadSize();
		StringBuffer msg = new StringBuffer();
		
		//TODO change to byte conversion
		
		if( size > 0 ) {
			int mag = 0;
			while( size >=  1000) {
				size = size / 10;
				mag++;
			}
			char[] oMag = { 'K', 'M', 'G' };
			if( mag > 0 ) msg.append( oMag[ (mag-1)/3 ]);
			msg.insert(0, size );
			// and now the dot
			if( mag%3 != 0 ) msg.insert( mag%3, ".");
		} else {
			// no upload size .. bummer
		}
		if( du.getNoOfFiles() > 1 ) {
			if( msg.length()>0)
				msg.append( " in " );
			msg.append( du.getNoOfFiles());
			if(getOai().length()>0){
			  msg.append(" responses");
			}else{
			  msg.append(" files");
							
			}
			
		}
		return msg.toString();
	}
	
	public boolean isZip() {
		return (this.getName().endsWith("zip") || this.getName().endsWith("rar"));
	}
	
	public boolean isExcel() {
		return "xls".equals( du.getStructuralFormat());
	}
	
	public String getOai() {
		if( du.isOaiHarvest()) 
		
			return du.getSourceURL();	
		else return "";
	}
	
	public String getFullOai() {
		if( du.isOaiHarvest()) 
				return du.getSourceURL();
			
		else return "";
	}
	
	public boolean isLocked( User u, String sessionId ) {
		return !DB.getLockManager().canAccess( u, sessionId, du );
	}
	
	public boolean isRootDefined(){
		XpathHolder level_xp = this.du.getItemXpath();
		if(level_xp == null || level_xp.getXpathWithPrefix(true).length()==0) 
			return false;
		else 
		 return true;
		
	}
	
}
