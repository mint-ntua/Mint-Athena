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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;


import gr.ntua.ivml.athena.db.DB;

import gr.ntua.ivml.athena.mapping.MappingSummary;
import gr.ntua.ivml.athena.persistent.Organization;
import gr.ntua.ivml.athena.persistent.User;
import gr.ntua.ivml.athena.persistent.Mapping;
import gr.ntua.ivml.athena.persistent.XpathHolder;

import gr.ntua.ivml.athena.persistent.DataUpload;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;


@Results({
	  @Result(name="input", location="mapselection.jsp"),
	  @Result(name="error", location="mapselection.jsp"),
	  @Result(name="success", location="mapselection.jsp" )
	})

public class Mapselection extends GeneralAction  {

	protected final Logger log = Logger.getLogger(getClass());
	public String mapName;
	public String mapsel;
	private long editMapping;
	private long shareMapping;
	private long deleteMapping;
	private long templateSel;
	private long uploadId;
	private boolean shareCheck=false;
	private boolean noshareCheck=false;
	private Collection<String> missedMaps=new ArrayList<String>();
	private boolean noitem=false;
	private List lockedmaps=new ArrayList();
	private List<Mapping> accessibleMappings=new ArrayList<Mapping>();
	private List<Mapping> templateMappings=new ArrayList<Mapping>();

	public void findTemplateMappings() {
		List<Mapping> maplist= new ArrayList();
		try{
	
	    
        List<Mapping> alllist= DB.getMappingDAO().findAllOrderOrg();
        for(int i=0;i<alllist.size();i++){
          //now add the shared ones if not already in list
        	Mapping em=alllist.get(i);
            boolean lock=em.isLocked(getUser(), getSessionId());
        	//if shared and not locked add to template list
        	if(em.isShared() && !lock){
        		
        		maplist.add(em);
        	}
        	else if(!em.isShared() && !lock){
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
		templateMappings=maplist;
		
	}
	
	public List<Mapping> getTemplateMappings(){
		return this.templateMappings;
	}
	
	public boolean getNoitem(){
		return noitem;
	}
	
	public void findAccessibleMappings() {
		List<Mapping> maplist= new ArrayList();
		try{
		
			
			//if user is admin or superuser then get his accessibleOrgs	
			if(user.getAthenaRole().equalsIgnoreCase("ADMIN") || user.getAthenaRole().equalsIgnoreCase("SUPERUSER")){
		        List<Organization> deporgs=user.getAccessibleOrganizations();
		        for(Organization org:deporgs){
		        	maplist.addAll(DB.getMappingDAO().findByOrganization(org));
		        }
		        
		       
			}
			else if(user.getAthenaRole().indexOf("annotator")>-1){
	        //if he is annotator then only access to his orgs mappings
	        
	           Organization uorg=user.getOrganization();
	           maplist.addAll(DB.getMappingDAO().findByOrganization(uorg));
		      
		        
			}
		}
		catch (Exception ex){
			log.debug(" ERROR GETTING MAPPINGS:"+ex.getMessage());
		}
		accessibleMappings=maplist;
		
	}
	
	public List<Mapping> getAccessibleMappings(){
		return accessibleMappings;
	}
	
	public List getLockedmaps(){
		return lockedmaps;
	}
	
	public void findlocks(List<Mapping> maplist){
		log.debug("checking locks");
		for(int i=0;i<maplist.size();i++){
			Mapping m=maplist.get(i);
			if(m.isLocked(user, sessionId)){
				lockedmaps.add(true);
			}
			else{lockedmaps.add(false);}
		}
	}

	
	
	
	public boolean checkName(String newname) {
		List<Mapping> maplist= new ArrayList();
		boolean exists=false;
		try{
		Organization org=user.getOrganization();
        for(Mapping m: DB.getMappingDAO().findByOrganization(org)){
        	if(m.getName().equalsIgnoreCase(newname)){exists=true;break;}
        }
         
        }
		catch (Exception ex){
			log.debug(" ERROR GETTING MAPPINGS:"+ex.getMessage());
		}
		return exists;
	}
	
	public Collection getMissedMaps(){
		return this.missedMaps;
	}
	
	public void setEditMapping(long editMapping) {
		this.editMapping = editMapping;
	}

	public void setShareCheck(boolean sch) {
		this.shareCheck = sch;
	}
	
	public boolean getShareCheck() {
		return this.shareCheck; 
	}
	
	public boolean getNoshareCheck() {
		return this.noshareCheck; 
	}

	public void setNoshareCheck(boolean sch) {
		this.noshareCheck = sch;
		}
	
	public long getEditMapping(){
		return editMapping;
	}
	

	public void setDeleteMapping(long deleteMapping) {
		this.deleteMapping = deleteMapping;
	}

	public long getDeleteMapping(){
		return deleteMapping;
	}
	
	public void setShareMapping(long shareMapping) {
		this.shareMapping = shareMapping;
	}

	public long getShareMapping(){
		return shareMapping;
	}
	
	public long getUploadId(){
		return uploadId;
	}
	
	public void setUploadId(long uploadId){
		this.uploadId=uploadId;
	}
	
	public void setUploadId(String uploadId){
		this.uploadId=Long.parseLong(uploadId);
	}
	
	public long getTemplateSel(){
		return templateSel;
	}
	
	
	public void setTemplateSel(long templateSel) {
		this.templateSel = templateSel;
	}
	
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}
	
	public String getMapsel() {
		return mapsel;
	}
	
	public void setMapsel( String mapsel ) {
		this.mapsel = mapsel;
	}
	
	@Action(value="Mapselection")
    public String execute() throws Exception {
		
	  
		if( "createnew".equals( mapsel )) 
    	{   this.setEditMapping(0);
    		if(mapName==null || mapName.length()==0){
    			initLists();
    			addActionError("Specify a mapping name!");
    			return ERROR;
    		}
    		Mapping mp=new Mapping();
    		mp.setCreationDate(new java.util.Date());
    		if(checkName(mapName)==true){
    			initLists();
    			addActionError("Mapping name already exists!");
    			return ERROR;
    
    		}
    		mp.setName(mapName);
    		mp.setOrganization(DB.getDataUploadDAO().findById(uploadId, false).getOrganization());
    		//mp.setOrganization(user.getOrganization());
    		if(getTemplateSel()>0){
    			long templateId = getTemplateSel();
    			Mapping temp = DB.getMappingDAO().getById(templateId, false);
    			
    			mp.setJsonString(temp.getJsonString());
    			
    		}
    		
    		
    		//save mapping name to db and commit
    		
    		DB.getMappingDAO().makePersistent(mp);
    		DB.commit();
    		
    		setEditMapping(mp.getDbID());
    		if(getTemplateSel()>0){
    			if(MappingSummary.getInvalidXPaths(DB.getDataUploadDAO().findById(uploadId, false), mp)!=null){
    				this.missedMaps=MappingSummary.getInvalidXPaths(DB.getDataUploadDAO().findById(uploadId, false), mp);
    			}
    			if(missedMaps.size()==0){
    				return "success";
    			}
    			else{
    				initLists();
    				addActionError("This import does not contain the following xpaths which appear in <i>'"+mp.getName()+"'</i> template mappings you are trying to use");
    				
        			return ERROR;
    			}
    		}
    	}
    	else if( "editmaps".equals( mapsel ))
    	{ 
    		if(this.getEditMapping()>0){
    			//check if mapping is locked here
    			Mapping em=DB.getMappingDAO().findById(getEditMapping(), false);
    			//check if current user has access to mappings
    			if(em.isLocked(getUser(), getSessionId())){
    				initLists();
    				addActionError("The selected mappings are currently in use by another user. Please try to edit them again later");
        			return ERROR;
    			}
    			//check if this import corresponds to mappings
    			if(MappingSummary.getInvalidXPaths(DB.getDataUploadDAO().findById(uploadId, false), em)!=null){
    				this.missedMaps=MappingSummary.getInvalidXPaths(DB.getDataUploadDAO().findById(uploadId, false), em);
    			}
    			if(missedMaps.size()==0){
    				return "success";
    			}
    			else{
    				
    				initLists();
    				addActionError("This import does not contain the following xpaths which appear in <i>'"+em.getName()+"'</i> mappings you are trying to use. Press 'cancel' to go back and select different mappings for edit.");
    				
        			return ERROR;
    			}
    		  }
    		else{
    			initLists();
    			addActionError("Choose the mappings you want to edit!");
    			return ERROR;
    		}
    	}
    	else if( "sharemaps".equals( mapsel ))
    	{ 	
    		this.setEditMapping(0);
    		if(this.getShareMapping()>0){
    			//check if mapping is locked here
    			
    			Mapping em=DB.getMappingDAO().findById(getShareMapping(), false);
    			//check if current user has access to mappings
    			if(em.isLocked(getUser(), getSessionId())){
    				initLists();
    				addActionError("The selected mappings are currently locked by another user. Please try to share them again later");
        			return ERROR;
    			}
    			if(this.getNoshareCheck()==false && this.getShareCheck()==false){
    				initLists();
    				addActionError("Please choose share state for selected mappings using the checkboxes next to it.");
        			return ERROR;
    			}
    			else if(this.getNoshareCheck()==true){
    			   em.setShared(false);
    			}
    			else if(this.getShareCheck()==true){
    				em.setShared(true);
    			}
    			DB.commit();
    			refreshUser();
    			initLists();
    			addActionError("Mappings share state successfully altered!");
        		return ERROR;
    			
    		}
    		else{
    			initLists();
    			addActionError("Choose the mappings you want to share!");
    			return ERROR;
    		}
    	}
    	else if( "deletemaps".equals( mapsel ))
    	{   this.setEditMapping(0);
    		if(this.getDeleteMapping()>0){
    			boolean success=false;
    			Mapping mp=DB.getMappingDAO().getById(getDeleteMapping(), true);
    			if(mp.isLocked(getUser(), getSessionId())){
    				initLists();
    				addActionError("The selected mappings are currently in use by another user.");
          			return ERROR;
    			  }
    			 success=DB.getMappingDAO().makeTransient(mp);
    			 DB.commit();
    			if(success){
    				initLists();
    				addActionError("Mappings successfully deleted!");
        			return ERROR;
    		
    			}
    			else{
    				refreshUser();
    				initLists();
    		    	addActionError("Unable to delete selected Mappings. Mappings are in use!");
        			return ERROR;
    			}
    			}
    			
    		else{
    			initLists();
    			addActionError("Choose the mappings you want to delete!");
    			return ERROR;
    		}
    	}
    	else if( "discardnewmap".equals( mapsel ))
    	{   this.setEditMapping(0);
    		if(this.getDeleteMapping()>0){
    			boolean success=false;
    			Mapping mp=DB.getMappingDAO().getById(getDeleteMapping(), true);
    			if(mp.isLocked(getUser(), getSessionId())){
    				return ERROR;
    			  }
    			else{
    			 success=DB.getMappingDAO().makeTransient(mp);
    			 DB.commit();
    			if(success){
    				initLists();
    				return ERROR;
    			}
    			else{
    				initLists();
    				refreshUser();
    		    	return ERROR;
    			}
    			}
    	     }
      	}
    	else {
    		log.error("Unknown action" );
    		addActionError("Specify a mapping action!");
    		initLists();
    		return ERROR;
    	}
    		return "success";
    	
    }

		
	public void initLists(){
		this.findTemplateMappings();
		this.findAccessibleMappings();
		this.findlocks(this.accessibleMappings);
	}
	
	@Action("Mapselection_input")
	@Override
	public String input() throws Exception {
		log.debug("in input crap");
		if( (user.getOrganization() == null && !user.hasRight(User.SUPER_USER)) || !user.hasRight(User.MODIFY_DATA)) {
    		log.debug("No mapping rights");
    		throw new IllegalAccessException( "No mapping rights!" );
    	}
	    DataUpload du=DB.getDataUploadDAO().findById(uploadId, false);
		XpathHolder level_xp = du.getItemXpath();
		if(level_xp == null || level_xp.getXpathWithPrefix(false).length()==0) {
				this.noitem=true;
				addActionError("You must first define the Item Level and Item Label by choosing step 1.");
				return ERROR;
			}
		initLists();
		return super.input();
	}	
	
}