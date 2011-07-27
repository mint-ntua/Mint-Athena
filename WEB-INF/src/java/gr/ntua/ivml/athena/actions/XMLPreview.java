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
import gr.ntua.ivml.athena.persistent.Mapping;
import gr.ntua.ivml.athena.persistent.Organization;
import gr.ntua.ivml.athena.persistent.Transformation;
import gr.ntua.ivml.athena.persistent.XMLNode;
import gr.ntua.ivml.athena.persistent.XmlObject;
import gr.ntua.ivml.athena.persistent.XpathHolder;
import gr.ntua.ivml.athena.util.Config;
import gr.ntua.ivml.athena.xml.transform.XMLFormatter;
import gr.ntua.ivml.athena.xml.transform.XSLTGenerator;
import gr.ntua.ivml.athena.xml.transform.XSLTransform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.util.ServletContextAware;


@Results({
	  @Result(name="input", location="xmlPreviewRequest.jsp"),
	  @Result(name="error", location="xmlPreviewRequest.jsp"),
	  @Result(name="success", location="xmlPreviewRequest.jsp" ),
	  @Result(name="previewInput", location="xmlPreviewInput.jsp" )
	})

public class XMLPreview extends GeneralAction implements ServletContextAware {

	protected final Logger log = Logger.getLogger(getClass());
	private long selMapping;
	private String uploadId;
	private String nodeId;
	private Mapping mapping;
	private String error;
	private ServletContext sc;
	private boolean truncated=false;
	
	private List<Mapping> maplist= new ArrayList<Mapping>();
	
	public List<Mapping> getMaplist() {
		try{
	
	    
        List<Mapping> alllist= DB.getMappingDAO().findAllOrderOrg();
        for(int i=0;i<alllist.size();i++){
          //now add the shared ones if not already in list
        	Mapping em=alllist.get(i);
           
        	//if shared and not locked add to template list
        	if(em.isShared() && !em.isLocked(getUser(), getSessionId())){
        		
        		maplist.add(em);
        	}
        	else if(!em.isShared() && !em.isLocked(getUser(), getSessionId())){
        	//if not shared but belongs to accessible org
        	 Organization org=em.getOrganization();
        	//need to check accessible and their parents
        	 List<Organization> deporgs=user.getAccessibleOrganizations();
             for(int j=0;j<deporgs.size();j++){
        	    if(deporgs.get(j).getDbID()==org.getDbID()){
        	    	//mapping org belongs in deporgs so add
        	    	if(!maplist.contains(em)){
        	    	maplist.add(em);}
        	    	break;
        	    }
        	    Organization parent=deporgs.get(j).getParentalOrganization();
        	    while(parent!=null && parent.getDbID()>0){
        	    	
	        	    if(parent.getDbID()==org.getDbID()){
	        	    	//mapping org belongs to parent of accessible so add
	        	    	if(!maplist.contains(em)){
	        	    	maplist.add(em);}
	        	    	break;
	        	    }
	        	    parent=parent.getParentalOrganization();
	        	    //traverse all parents OMG
	            }
        	}
         }
		}
		}
		catch (Exception ex){
			log.debug(" ERROR GETTING MAPPINGS:"+ex.getMessage());
		}
		if( maplist.isEmpty() ) maplist=Collections.emptyList();
		
		return maplist;
	}

	
	public void setselMapping(long selMapping) {
		this.selMapping = selMapping;
	}

	public long getselMapping(){
		return selMapping;
	}
	

	public String getUploadId(){
		return uploadId;
	}
	
	public void setUploadId(String uploadId){
		this.uploadId=uploadId;
	}
	
	public String getNodeId(){
		return nodeId;
	}
	
	public void setNodeId(String nodeId){
		this.nodeId=nodeId;
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
				DataUpload du = getDataUpload();
				if( du == null ) {
					log.warn( "Extremely slow operation getXMLNodeDAO().getById triggered.");
					result = DB.getXMLNodeDAO().getById(nodeId, false);
				} else {
					XmlObject xo = du.getXmlObject();
					result = DB.getXMLNodeDAO().getByIdObject(xo, nodeId );
				}
				if(result.getSize()>20000){
					truncated=true;
				}
			} catch( Exception e ) {
				log.error( e );
			}
		}
		return result;
	}
	
	public DataUpload getDataUpload() {
		DataUpload result = null;
		if(( getUploadId() != null ) && 
				( getUploadId().trim().length() > 0 )) {
			try {
				long uploadId = Long.parseLong(getUploadId());
				result = DB.getDataUploadDAO().getById(uploadId, false);
			} catch( Exception e ) {
				log.error( e );
			}
		}
		return result;
	}
	
	
	public Mapping getMapping() {
		Mapping result = null;
		if( getselMapping() >0l ) {
			try {
				result = DB.getMappingDAO().getById(getselMapping(), false);
			} catch( Exception e ) {
				log.error( e );
			}
		}
		if( result == null ) return mapping;
		return result;
	}
	
	public void setMapping( Mapping m ) {
		mapping = m;
	}
	
	/**
	 * Returns XML for selected Node.
	 * @return
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
	
	/**
	 * Return transformed XML for selected node.
	 * Needs a selected mapping or DataUpload with
	 * finished transformation and a node.
	 * @return
	 */
	public String getTransformPreview() {
		String transformedItem=null;
		if( !hasTransformPreview()) return "No transformed View available"; 
		XSLTransform t = new XSLTransform();
		try {
		transformedItem = t.transform(getItemPreview(), getLidoXsl());
		transformedItem = XMLFormatter.format(transformedItem);
		} catch( Exception e ) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter( sw );
			e.printStackTrace(pw);
			transformedItem = sw.toString();
		}
		return transformedItem; 
	}
	
	public boolean hasTransformPreview() {
		if( getNode() == null ) return false;
		if( getMapping() != null ) return true;
		if( getDataUpload() == null ) return false;
		
		// maybe the upload has been successfully transformed
		List<Transformation> l = DB.getTransformationDAO().findByUpload( getDataUpload());
		for( Transformation t: l ) {
			if( t.getStatusCode() == Transformation.OK ) {
				setMapping( t.getMapping());
				return true;
			}
		}
		return false;
	
	}
	
	

	/**
	 * Provide the Lido XSL for output. Needs to be able to find
	 * the Upload.
	 * @return
	 */
	public String getLidoXsl() {
		DataUpload du = getDataUpload();
		XSLTGenerator xslt = new XSLTGenerator();
		XpathHolder itemPath = du.getItemXpath();
		
		xslt.setItemLevel(itemPath.getXpathWithPrefix(true));
		xslt.setTemplateMatch(itemPath.getXpathWithPrefix(true));
		xslt.setImportNamespaces(itemPath.getNamespaces(true));
		
		String mappings = getMapping().getJsonString();
		String xsl = XMLFormatter.format(xslt.generateFromString(mappings));
		return xsl;
	}
	
	public String getEseXsl(String eseFilePath) {
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
		String result = "";
		DataUpload du = getDataUpload();
		String eseFilePath = "";
		if(!du.isLido10()) {
			eseFilePath = sc.getRealPath(Config.get( "lido_to_ese_xsl"));
		} else {
			eseFilePath = sc.getRealPath(Config.get( "lido1.0_to_ese_xsl"));			
		}
		String eseXsl = getEseXsl(eseFilePath);
		XSLTransform t = new XSLTransform();

		try {
			if(eseXsl != null && eseXsl.length() > 0) {
				result = t.transform(getTransformPreview(), eseXsl);
				result = XMLFormatter.format( result );
			}
		} catch( Exception e ) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter( sw );
			e.printStackTrace(pw);
			result = sw.toString();
		}
		return result;
	}

	
	@Action(value="XMLPreview")
    public String execute() throws Exception {
    		return SUCCESS;   	
    }

	@Action(value="xmlPreviewInput")
	public String previewInput() throws Exception {
		log.debug( "Action: xmlPreviewInput");
		if( uploadId == null ) setError( "Missing uploadId parameter" );
		if( nodeId == null) setError( "Missing nodeId parameter" );
		return "previewInput";
	}
		
		
	@Action("XMLPreview_input")
	@Override
	public String input() throws Exception {
		return super.input();
	}


	public void setError(String error) {
		this.error = error;
	}


	public String getError() {
		return StringEscapeUtils.escapeHtml(error);
	}


	@Override
	public void setServletContext(ServletContext sc) {
		this.sc = sc;
		
	}	
	
}