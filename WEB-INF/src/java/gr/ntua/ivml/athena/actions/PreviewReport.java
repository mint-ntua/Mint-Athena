
package gr.ntua.ivml.athena.actions;

import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.db.LockManager;
import gr.ntua.ivml.athena.util.Import;
import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.Organization;
import gr.ntua.ivml.athena.persistent.Publication;
import gr.ntua.ivml.athena.persistent.User;

import java.util.ArrayList;
import java.util.Collections;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({
	@Result(name="error", location="previewReport.jsp"),
	@Result(name="success", location="previewReport.jsp")
})

public class PreviewReport extends GeneralAction{

	protected final Logger log = Logger.getLogger(getClass());

	
	private long orgId;
    private String report="";
    private Organization o=null;
    private Publication pub=null;
    private String pstatus="NOT DONE";
    
  	  
		
	public Publication getPub(){
		if(o!=null){
		 pub=DB.getPublicationDAO().findByOrganization(o);
		 setPstatus();
		 setReport();
		}
		return pub;
	}
	
	public void setReport(){
		report=pub.getStatusMessage()+"<br/>";
		
		if( pub.getReport() != null ) {
			report+=pub.getReport();
			report=report.replaceAll("URL:\\(PreviewError", "<a onclick=\"javascript:ajaxErrorPreview\\(");

			report=report.replaceAll("\\?nodeId=","");
			report=report.replaceAll("&transformationId=",",");
			report=report.replaceAll("&uploadId=",",");
			report=report.replaceAll("&errorSrc=",",'");
			report=report.replaceAll("\\) had problems:","\\');\"  href=\"#\">(show Item)</a> had problems:<br/>");
			report=report.replaceAll("\\n","<br/>");
		}
	}
	
	
	public String getReport(){
		return report;
	}
	
	public String getPstatus(){
			return pstatus;
		
		
	}
	
	public void setPstatus(){
		if(pub!=null){
			
			 if(pub.getStatusCode()==0){
		    	pstatus="OK";
				
			}
			else if(pub.getStatusCode()==-1){
				pstatus="ERROR";
				
			}
			else if(pub.getStatusCode()==1){
				pstatus="IDLE";
				
			}
			else if(pub.getStatusCode()==2){
				pstatus="CONSOLIDATE";
				
			}
			else if(pub.getStatusCode()==3){
				pstatus="VERSION";
				
			}
			else if(pub.getStatusCode()==4){ 
				pstatus="POSTPROCESS";
				
			}
		}
		
		
	}
	
	

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
		this.o=DB.getOrganizationDAO().findById(orgId, false);
		this.getPub();
	}

   
	public Organization getO(){
		return this.o;
	}
	
	@Action(value="PreviewReport")
	public String execute() throws Exception {
      return SUCCESS;	

	}
}