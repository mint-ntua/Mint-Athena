var mappingDefinitionPanel = new YAHOO.widget.Panel("mappingdefinition",
	{ width: "800px",
	  height: "400px",
	  fixedcenter: true,
	  constraintoviewport: true,
	  close: true,
	  draggable: false,
	  zindex: 4,
	  modal: true,
	  visible: false
	}
);

mappingDefinitionPanel.setHeader("Define/Edit Mappings");
mappingDefinitionPanel.setBody("<center>Loading...<br/><img src=\"images/rel_interstitial_loading.gif\"/></center>");
mappingDefinitionPanel.hideEvent.subscribe(mappingDefinitionPanelClose); 

var mappingDefinitionPanelOrgId = null;
var mappingDefinitionPanelUploadId = -1;
var mappingDefinitionPanelUserId = -1;
function mappingDefinitionPanelClose() {
	
}

function mappingRedirect(mapId) {
	mappingDefinitionPanelClose();
	window.document.location.href="DoMapping?uploadId="+mappingDefinitionPanelUploadId +"&mapid="+mapId;

}

function restartMapping(mapsel,mapid) {
	if(mapsel=='createnew'){
		
		ajaxMappingSelectionRequest('discardnewmap', '', 0,0,mapid,0,false,false);
	}
	else{
    	ajaxMappingDefinitionRequest(mappingDefinitionPanelUploadId, mappingDefinitionPanelOrgId, mappingDefinitionPanelUserId);
	}
}

function ajaxMappingDefinitionRequest(uploadId, orgId, userId) {
	mappingDefinitionPanelOrgId = orgId;  
	mappingDefinitionPanelUploadId=uploadId;
	mappingDefinitionPanelUserId=userId;
	mappingDefinitionPanel.setBody("<center>Loading...<br/><img src=\"images/rel_interstitial_loading.gif\"/></center>");
    
	//mappingDefinitionPanel.render(document.body);
	mappingDefinitionPanel.show();
   YAHOO.util.Connect.asyncRequest('POST', 'Mapselection_input.action',
        {
            success: function(o) {
    	        mappingDefinitionPanel.setBody(o.responseText);
    	        
 	           $('#Mapselection_templateSel, #Mapselection_editMapping, #Mapselection_shareMapping, #Mapselection_deleteMapping').sSelect({ddMaxHeight: '300px'});
 	      	        
    	     },
            
            failure: function(o) {
            	mappingDefinitionPanel.setBody("<h1>Error</h1>");
            }
        }, "uploadId=" + uploadId);
    
   
}

function radioval(){
	var val = 0;
	
	for( i = 0; i < document.mapform.mapsel.length; i++ )
	{
	if( document.mapform.mapsel[i].checked == true ){
	val = document.mapform.mapsel[i].value;
	break;
	}
	}

	return val;
	}

function continueWithErrors(editMapping){
	 mappingDefinitionPanel.hide();
 	 
     mappingRedirect(editMapping);
}

function ajaxMappingSelectionRequest(mapsel, mapName, editMapping,templateSel,deleteMapping,shareMapping,sch,nosch) {
	mappingDefinitionPanel.setBody("<center>Loading...<br/><img src=\"images/rel_interstitial_loading.gif\"/></center>");
	mappingDefinitionPanel.show();
		
    YAHOO.util.Connect.asyncRequest('POST', 'Mapselection.action',
        {
            success: function(o) {
    			if(o.responseText.indexOf('editredirect')==-1){
    			     mappingDefinitionPanel.setBody(o.responseText);
    			     
    		          $('#Mapselection_templateSel, #Mapselection_editMapping, #Mapselection_shareMapping, #Mapselection_deleteMapping').sSelect({ddMaxHeight: '300px'});
    		       				}
    	        else
    	        {   mappingDefinitionPanel.hide();
    	        	mappingDefinitionPanel.setBody(o.responseText);
        	        mappingRedirect(document.getElementById("editredirect").innerHTML);	}
                
            },
            
            failure: function(o) {
            	mappingDefinitionPanel.setBody("<h1>Error</h1>");
            }
        }, "mapsel=" + mapsel+"&mapName="+mapName+"&editMapping="+editMapping+"&templateSel="+templateSel+"&deleteMapping="+deleteMapping+"&shareMapping="+shareMapping+"&shareCheck="+sch+"&noshareCheck="+nosch+"&uploadId="+mappingDefinitionPanelUploadId);
    
   
}