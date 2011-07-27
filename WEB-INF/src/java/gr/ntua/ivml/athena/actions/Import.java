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
import gr.ntua.ivml.athena.concurrent.Queues;
import gr.ntua.ivml.athena.concurrent.UploadIndexer;
import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.harvesting.RepositoryValidator;
import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.User;
import gr.ntua.ivml.athena.util.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({
	  @Result(name="input", location="import.jsp"),
	  @Result(name="error", location="import.jsp"),
	  @Result(name="success", location="${url}", type="redirect" )
	})

public class Import extends GeneralAction  {

	protected final Logger log = Logger.getLogger(getClass());
	public File httpUp;
	public String contentType;
	public String filename;
	public String athenaFtpServer;
	public String serverFilename;
	public List<FTPFile> ftpFiles = new ArrayList<FTPFile> ();
	public String method = "httpupload";
	public String uploadUrl;
	public String oai;
	public long uploaderOrg;
	public String oaiset;
	public String namespace;
	public Date fromDate;
	public Date toDate;
	public String fromdate;
	public String todate;
	public String url="ImportSummary.action";
	public Boolean isLido;
	public Boolean isLido10;
	
	private DataUpload du;
	
	
	@Action("Import")
    public String execute() throws Exception {
    	log.info( "Import action");
    	// check permits
    	// created DataUpload empty object
    	du = new DataUpload();
    	du.setUploader( user );
    	if(user.getOrganization()==null && uploaderOrg == 0){
    		addActionError("Choose the organization you are importing for!");
			return ERROR;
    	}
    	else if(user.getOrganization()!=null){
    	  du.setOrganization(user.getOrganization());
    	}
    	
    	UploadIndexer upI = null;
    	if( uploaderOrg > 0){
    		du.setOrganization(DB.getOrganizationDAO().findById(uploaderOrg, false));
    	}
    	if( !user.can( "change data", du.getOrganization() )) { log.debug("1");
    			throw new IllegalAccessException("Parameter manipulation");}
    	// fill in specifics
    	if( "httpupload".equals( method )) {
    		if(this.httpUp==null){
    			addActionError("Http upload cannot be empty!");
    			return ERROR;
    		}
    		upI = handleHttpUpload();
    	}
    	else if( "ftpupload".equals( method )){
    		if(this.getFlist().equalsIgnoreCase("0")){
    			addActionError("No FTP files selected!");
    			return ERROR;
    		}
    		upI = handleFtpUpload();
    	}
    	else if( "urlupload".equals( method )){
    		if(this.getUploadUrl()==null || this.getUploadUrl().length()==0){
    			addActionError("Remote link cannot be empty!");
    			return ERROR;
    		}
    		upI = handleUrlUpload();
    	}
    	else if( "OAIurl".equals( method )){
    		if(this.getOai()==null || this.getOai().length()==0){
    			addActionError("Oai url cannot be empty!");
    			return ERROR;
    		}
    		if(!RepositoryValidator.isValid(this.getOai())){
    			addActionError("Oai url is invalid!");
    			return ERROR;
    		
    		}
    		if(this.fromdate!=null && this.fromdate.length()>0){
    			java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyy-MM-dd");
    			try {
    				this.fromDate = new Date(sdf.parse(fromdate).getTime());
    			
    			} catch( Exception pe ) {
    				addActionError("Please give 'From Date' in the correct format!");
    				return ERROR;
    				
    			}
    			
	    		
    		}
    		if(this.todate!=null && this.todate.toString().length()>0){
    			java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyy-MM-dd");
    			try {
    				this.toDate = new Date(sdf.parse(todate).getTime());
    			
    			} catch( Exception pe ) {
    				addActionError("Please give 'To Date' in the correct format!");
    				return ERROR;
    			}
	    		
    		}
    		if(this.getNamespace()==null || this.getNamespace().length()==0){
    			addActionError("Oai namespace prefix cannot be empty!");
    			return ERROR;		
    		}
    		upI = handleOaiUpload();
    	}
    	else if( "SuperUser".equals( method )) {
    		if(this.getServerFilename()==null || this.getServerFilename().length()==0){
    			addActionError("Server filename cannot be empty!");
    			return ERROR;
    		}
    		upI = handleServerUpload();
    	}
    	else {
    		log.error("Unknown method" );
    		addActionError("Specify an import method!");
    		return ERROR;
    	}
    	DB.commit();
    	if( upI != null ) {
    		Queues.queue(upI, "net" );
    		this.url+="?orgId="+this.du.getOrganization().getDbID();
    		System.out.println("url is:"+this.url);
    		return "success";
    	} else {
    		return "error";
    	}
    	
    }

	/**
	 * Try to put the uploaded file into a DataUpload object
	 * @return
	 * @throws Exception
	 */
	private UploadIndexer handleHttpUpload() throws Exception {
		du.setHttpUpload(true);
		du.setOriginalFilename(filename);
		
		if(this.isLido){
			du.setSchemaName("LIDO");
		}

		if(this.isLido10){
			du.setSchemaName("LIDO10");
		}

		DB.getDataUploadDAO().makePersistent(du);
		File newTmpFile = File.createTempFile("AthenaUpload", "cpy");
		IOUtils.copy( new FileInputStream(httpUp), new FileOutputStream( newTmpFile ) );
		UploadIndexer upI = new UploadIndexer( du, UploadIndexer.HTTPUPLOAD );
		upI.tmpFile = newTmpFile;
		return upI;
	}
	
	private UploadIndexer handleServerUpload() throws Exception {
		du.setAdminUpload(true);
		du.setOriginalFilename(serverFilename);
		if(this.isLido){
			du.setSchemaName("LIDO");
		}
		if(this.isLido){
			du.setSchemaName("LIDO10");
		}
		File tmpFile = new File( serverFilename );
		if( !tmpFile.exists() || !tmpFile.canRead() || tmpFile.length()== 0l ) {
			du.setStatus(DataUpload.ERROR);
			du.setMessage("Upload failed, file not found, not readable or empty" );
		}
		DB.getDataUploadDAO().makePersistent(du);
		UploadIndexer upI = new UploadIndexer( du, UploadIndexer.SERVERFILE );
		upI.tmpFile = tmpFile;
		return upI;
	}
	
	/**
	 * Put the filename in du.originalFilename and let the UploadIndexer Thread handle everything.
	 * @return
	 * @throws Exception
	 */
	private UploadIndexer handleFtpUpload() throws Exception {
		du.setOriginalFilename(filename);
		if(this.isLido){
			du.setSchemaName("LIDO");
		}
		if(this.isLido10){
			du.setSchemaName("LIDO10");
		}
		DB.getDataUploadDAO().makePersistent(du);
		
		UploadIndexer upI = new UploadIndexer( du, UploadIndexer.FTPSERVER );
		return upI;
		
	}
	
	private UploadIndexer handleUrlUpload() throws Exception {
		du.setSourceURL(uploadUrl);
		URL url = new URL( uploadUrl );
		du.setOriginalFilename(url.getFile());
		if(this.isLido){
			du.setSchemaName("LIDO");
		}
		if(this.isLido10){
			du.setSchemaName("LIDO10");
		}

		DB.getDataUploadDAO().makePersistent(du);
		
		UploadIndexer upI = new UploadIndexer( du, UploadIndexer.URLUPLOAD );
		return upI;
	}
	
	private UploadIndexer handleOaiUpload() throws Exception {
		// create the data upload object anyway and session it
		// probably redirect to the oai schedule page
		du.setSourceURL(getOai());
		du.setOaiHarvest(true);
		if(this.isLido){
			du.setSchemaName("LIDO");
		}
		if(this.isLido10){
			du.setSchemaName("LIDO10");
		}

		DB.getDataUploadDAO().makePersistent(du);
		String set = null;
		if(!this.oaiset.equals("")){
			set = this.oaiset;
		}else{
			set = null;
		}
		UploadIndexer upI = new UploadIndexer( du, UploadIndexer.OAIHARVEST, set, this.namespace, this.fromDate, this.toDate);
		//UploadIndexer upI = new UploadIndexer( du, UploadIndexer.OAIHARVEST );
		return upI;
	}
	
	@Action("Import_input")
	@Override
	public String input() throws Exception {
    	if( user.getOrganization() == null && !user.hasRight(User.SUPER_USER)) {
    		throw new IllegalAccessException( "No import rights!" );
    	}

		return super.input();
	}
	

	// setters for form interaction with hhtp upload
	public void setHttpup(File file) {
		log.debug( "File upload set in action");
		this.httpUp = file;
	}

	public void setHttpupContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public void setHttpupFileName(String filename) {
		this.filename = filename;
	}
	// end httpupload
	
	
	public List<FTPFile> getFtpFiles() {
		log.debug( "entering getFtpFiles");
		ftpFiles.clear();
		try {
			   FTPClient f= new FTPClient();
			    f.connect(Config.get("athenaFtpServer"));
			    f.login(Config.get("ftpUser"), Config.get("ftpPassword"));
			    FTPFile[] allFiles = f.listFiles("");
			    for( FTPFile file: allFiles ) 
			    	if( !file.isDirectory()) ftpFiles.add( file );
			    	else log.debug( "Listed dir " + file.getName());
		} catch( Exception e ) {
			log.error( "FTP read Dir didnt succeed", e );
		}
		return ftpFiles;
	}
	
		
	public String getServerFilename() {
		return serverFilename;
	}

	public void setServerFilename(String serverFilename) {
		this.serverFilename = serverFilename;
	}

	public String getOai() {
		return oai;
	}

	public void setOai(String oai) {
		this.oai = oai;
	}

	public String getOaiset() {
		return oaiset;
	}

	public void setOaiset(String oaiset) {
		this.oaiset = oaiset;
	}
	
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public String getFromdate() {
		return fromdate;
	}

	public void setFromdate(String fromdate) {
		this.fromdate=fromdate;
		
	}
	
	
	public String getTodate() {
		return todate;
	}

	public void setTodate(String todate) {
		this.todate=todate;
		
	}
	
	public String getMth() {
		return method;
	}
	
	public void setMth( String method ) {
		this.method = method;
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
		 
	}

	public String getFlist() {
		return filename;
	}

	public void setFlist( String name ) {
		filename = name;
	}
	

	public Boolean getIsLido() {
        return this.isLido;
    }

	public Boolean getIsLido10() {
        return this.isLido10;
    }

	  public void setIsLido(Boolean isLido) {
		    this.isLido=isLido;
	    }
	
	  public void setIsLido10(Boolean isLido) {
		    this.isLido10=isLido;
	    }
	
	/**
	 * Setter for the Organization for which you want to upload
	 * @param uploaderOrg
	 */
	public void setUploaderOrg( long uploaderOrg ) {
		this.uploaderOrg = uploaderOrg;
	}
	
	public long getUploaderOrg() {
		return uploaderOrg;
	}
}