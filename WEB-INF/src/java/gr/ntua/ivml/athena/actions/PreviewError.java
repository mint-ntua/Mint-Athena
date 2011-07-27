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
import gr.ntua.ivml.athena.persistent.Transformation;
import gr.ntua.ivml.athena.persistent.XMLNode;
import gr.ntua.ivml.athena.util.Config;
import gr.ntua.ivml.athena.xml.transform.XMLFormatter;
import gr.ntua.ivml.athena.xml.transform.XSLTransform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.util.ServletContextAware;


@Results({
	  @Result(name="error", location="xmlPreviewError.jsp"),
	  @Result(name="success", location="xmlPreviewError.jsp" )
	})

public class PreviewError extends GeneralAction implements ServletContextAware {

	protected final Logger log = Logger.getLogger(getClass());
    private String nodeId;
	private Transformation tr;
	private String transformationId;
	private String errorSrc;
	private String error;
	
	private ServletContext sc;
	private String uploadId;
	private boolean truncated=false;
	private String eseXml="";
	
	
	public String getNodeId(){
		return nodeId;
	}
	
	public void setNodeId(String nodeId){
		this.nodeId=nodeId;
	}
	
	public String getUploadId(){
		return uploadId;
	}
	
	public void setUploadId(String uploadId){
		this.uploadId=uploadId;
	}
	
	public String getTransformationId(){
		return transformationId;
	}
	
	public void setTransformationId(String transformationId){
		this.transformationId=transformationId;
	}
	
	public boolean isTruncated(){
		return this.truncated;
	}
	
	public XMLNode getNode() {
		XMLNode result = null;
   	
		if(( getNodeId() != null ) && 
				( getNodeId().trim().length() > 0 )) {
			try {
				long nodeId = Long.parseLong(getNodeId());
			
				result = DB.getXMLNodeDAO().getById(nodeId, false);
				if(result.getSize()>20000){
					truncated=true;
				}
			} catch( Exception e ) {
				log.error( e );
			}
		}
		return result;
	}
	

	
	public Transformation getTr() {
		tr=DB.getTransformationDAO().findById(Long.parseLong(this.getTransformationId()), false);
		return tr;
	}
	
	
	
	/**
	 * return XML for transformation node
	 */
	public String getItemPreview() {
		if( ! hasItemPreview() ) return "";
		StringWriter xmlWriter = new StringWriter();
		XMLNode node = getNode();
		node.toXmlWrapped(new PrintWriter(xmlWriter));

		String xml = xmlWriter.toString();
		xml = XMLFormatter.format(xml); 

		return xml;
	}
	
	public boolean hasItemPreview() {
		return getNode()!=null;
	}
	

	
	public String getEseXsl() {
		DataUpload u=DB.getDataUploadDAO().getById(Long.parseLong(this.getUploadId()), false);
		String eseFilePath = "";
		if(!u.isLido10()) {
			eseFilePath = sc.getRealPath(Config.get( "lido_to_ese_xsl"));
		} else {
			eseFilePath = sc.getRealPath(Config.get( "lido1.0_to_ese_xsl"));
		}
		File eseFile = new File(eseFilePath);
		StringBuilder eseContents = new StringBuilder();
   		try {
      		BufferedReader input =  new BufferedReader(new FileReader(eseFile));
      		try {
	    	    String line = null; //not declared within while loop
	    	    while (( line = input.readLine()) != null){	    	
					eseContents.append(line);
					eseContents.append(System.getProperty("line.separator"));
        		}
	   		} finally {
        		input.close();
      		}
    	}
    	catch (IOException ex){
      		log.error( ex );
		}
    	 return eseContents.toString();
	}

	public String getEseXml() {
	  return eseXml;	
	
	}

	public void setEseXml() {
		String result = "";
		String eseXsl = getEseXsl();
		XSLTransform t = new XSLTransform();

		try {
			if(eseXsl != null && eseXsl.length() > 0) {
				result = t.transform(getItemPreview(), getEseXsl());
				result = XMLFormatter.format( result );
			}
		} catch( Exception e ) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter( sw );
			e.printStackTrace(pw);
			result = sw.toString();
		}
		eseXml=result;
		
	}
	
	@Action(value="PreviewError")
    public String execute() throws Exception {
		    setEseXml();
		    
    		return SUCCESS;   	
    }



	public void setError(String error) {
		this.error = error;
	}


	public String getError() {
		return error;
	}

	public void setErrorSrc(String error) {
		this.errorSrc = error;
	}


	public String getErrorSrc() {
		return errorSrc;
	}

	public String getUploadName() {
		if(this.getUploadId()!=null){
			DataUpload u=DB.getDataUploadDAO().getById(Long.parseLong(this.getUploadId()), false);
			return u.getOriginalFilename();
		}
		return "";
	}
	

	@Override
	public void setServletContext(ServletContext sc) {
		this.sc = sc;
		
	}	
	
	
}