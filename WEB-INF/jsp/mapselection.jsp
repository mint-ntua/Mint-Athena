<%@ include file="_include.jsp"%>
<%@page import="java.util.List"%>
<%@page import="gr.ntua.ivml.athena.persistent.Mapping"%>
<%@page import="gr.ntua.ivml.athena.persistent.Organization"%>
<link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/button/assets/skins/sam/button.css" />
<link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/treeview/assets/skins/sam/treeview.css" />



<script type="text/javascript" src="js/mapping/lib/yui/yahoo/yahoo-min.js"></script>
<script type="text/javascript" src="js/mapping/lib/yui/event/event-min.js"></script>
<script type="text/javascript" src="js/mapping/lib/yui/dom/dom-min.js"></script>
<script type="text/javascript" src="js/mapping/lib/yui/treeview/treeview-min.js"></script>
<script type="text/javascript" src="js/mapping/lib/yui/element/element-min.js"></script>
<script type="text/javascript" src="js/mapping/lib/yui/dragdrop/dragdrop-min.js"></script>
<script type="text/javascript" src="js/mapping/lib/yui/resize/resize-min.js"></script>
<script type="text/javascript" src="js/mapping/lib/yui/animation/animation-min.js"></script>
<script type="text/javascript" src="js/mapping/lib/yui/layout/layout-min.js"></script>
<script type="text/javascript" src="js/mapping/lib/yui/button/button-min.js"></script>
<script type="text/javascript" src="js/mapping/lib/yui/container/container-min.js"></script>
<script type="text/javascript" src="js/mapping/lib/yui/connection/connection-min.js"></script>
<script type="text/javascript" src="js/mapping/lib/yui/yahoo-dom-event/yahoo-dom-event.js"></script>



<style type="text/css">
.imagebacked {
background-image: url('images/lock.gif');

background-position:left; 
color: grey;

background-repeat: no-repeat;
}
form.athform label {
	
		float:left;
	
		}
</style>
        

<div style="width: 100%; height: 100%; margin-top:-20px;">

<h2>Mappings</h2>
<s:if test="noitem==true">

	<s:iterator value="actionErrors">
				<font style="color:red;"><s:property escape="false" /> </font>
	</s:iterator>

</s:if>
<s:else>
<s:if test="editMapping!=null && editMapping>0 && mapsel!=null && !mapsel.equalsIgnoreCase('0') && missedMaps.size()>0 && hasActionErrors()">
  
	<s:iterator value="actionErrors">
				<font style="color:red;"><s:property escape="false" /> </font>
	</s:iterator>
	
	<div style="width: 100%; height: 250px; margin-top:5px; overflow: auto; background:#ffffff;">
	<%int count=0; %>
	<s:iterator value="missedMaps">
	<%count++; %>
				<span style="background: url('images/formdiv3.gif') repeat-x scroll left bottom transparent;"><font style="0.9em;color:grey;"><b><%=count%>.</b>&nbsp;<s:property escape="false" /> </font></span><br/>
		</s:iterator>
	</div>		
	<p align="left">
		<a class="button" href="#" onclick="this.blur();restartMapping('<s:property value="mapsel"/>',<s:property value="editMapping"/>); "><span>Cancel</span></a> 
		<s:if test="mapsel.equalsIgnoreCase('createnew')"> 
		<a class="button" href="#" onclick="this.blur();mappingRedirect(<%=request.getAttribute("editMapping") %>); "><span>Continue anyway</span></a> 
		</s:if>
		</p>
	
</s:if>
<s:elseif test="editMapping==null || editMapping==0 || (editMapping!=null && editMapping>0 && hasActionErrors() && missedMaps.size()==0)">
<s:form name="mapform" action="Mapselection" cssClass="athform" theme="mytheme" enctype="multipart/form-data" style="width:100%;margin-top:-10px;">
	<fieldset style="background-image: url(../images/spacer.gif);background-repeat: none;">
	<p>&nbsp;Define new or edit existing mappings:</p>
	<ol>
	    <li></li>
	    	<%
	    	 List<Mapping> templateMappings=(List)request.getAttribute("templateMappings");
	    	 List<Mapping> accMappings=(List)request.getAttribute("accessibleMappings");
	    	 List<Boolean> locks=(List)request.getAttribute("lockedmaps");
	    	  String sel=""; %>	
	    	
	    <li><div style="float:left;"><s:radio name="mapsel" list="%{#{'createnew':'Create new mapping'}}" cssStyle="float:left;"
			onclick='$("input[name=mapsel]~ :input").attr( "disabled", "disabled" ); $("#Mapselection_templateSel").attr( "disabled","" ); $("#Mapselection_mapName").attr( "disabled","" );' />
			</div>
			  <select id="Mapselection_templateSel" name="templateSel">
			  	<option value="0">-- No template --</option>
			  	<%Organization lastorg=new Organization();
			  	  int i=0;
			  	  for(Mapping tempmap:templateMappings){
				   
				   if((Long)request.getAttribute("templateSel")-tempmap.getDbID()==0.0){
					   sel="selected";
				   }
				   else{sel="";}
				   Organization current=tempmap.getOrganization();
				   if(lastorg!=null && current!=null && !lastorg.equals(current)){
					   if(i>0){%>
			    	     </optgroup>  
			           <%}
					   lastorg=current;
					   %>
				         <optgroup label="<%=lastorg.getEnglishName() %>">
				       <%
				     
				   }
				   
				   String cssclass="";
				  
				   if(tempmap.isFinished()){
					   cssclass+="finished";
				   }
				   if(tempmap.isShared()){
					   cssclass+=" shared";
				   }
				  %> 
				 <option value="<%=tempmap.getDbID() %>" class="<%=cssclass %>" <%=sel%>><%=tempmap.getName() %></option>
				   
				  
				
				  <% 
				  i++;
			  	  }%>
			  	  <%if(templateMappings.size()>0){ %>
			  	          </optgroup>  
			      <%} %>
			  	
                </select>
			
	
			<div style="margin-top:30px;">
		<s:textfield
			name="mapName" label="Mapping name" size="60px;margin-top:2px;" /> <font style="font-size: 10px;"><i>Give
		the name of the new mapping</i></font>
		</div>
		</li>

		<li><div style="margin-top: 5px; height: 20px;"><div style="float:left;"><s:radio name="mapsel" list="%{#{'editmaps':'Edit mappings'}}" cssStyle="float:left;"
			onclick='$("input[name=mapsel]~ :input").attr( "disabled", "disabled" ); $("#Mapselection_editMapping").attr( "disabled","" )' />
		   </div>
			 
			<select name="editMapping" id="Mapselection_editMapping" style="width:200px;">  
			<option value="0">-- select mapping --</option>
			<%lastorg=new Organization();sel="";i=0;
			  for(Mapping tempmap:accMappings){
			   boolean lock=(Boolean)locks.get(i);
			   if((Long)request.getAttribute("editMapping")-tempmap.getDbID()==0.0){
				   sel="selected";
			   }
			   else{sel="";}
			   Organization current=tempmap.getOrganization();
			   if(lastorg!=null && current!=null && !lastorg.equals(current)){
				   if(i>0){%>
		    	     </optgroup>  
		           <%}
				   lastorg=current;
				   %>
			         <optgroup label="<%=lastorg.getEnglishName() %>">
			       <%
			     
			   }
			   String cssclass="";
			   if(lock){
				   cssclass+="locked";
			   }
			   if(tempmap.isFinished()){
				   cssclass+=" finished";
			   }
			   if(tempmap.isShared()){
				   cssclass+=" shared";
			   }
			  %> 
			 <option value="<%=tempmap.getDbID() %>" class="<%=cssclass %>" <%=sel%>><%=tempmap.getName() %></option>
			     <% 
			     i++;}
			     %>
			   <%if(accMappings.size()>0){ %>
			  	          </optgroup>  
			      <%} %>
			</select></div>
		</li>
		<li><div style="margin-top: 5px; height: 20px;"><div style="float:left;"><s:radio name="mapsel" list="%{#{'sharemaps':'Share mappings'}}" cssStyle="float:left;"
			onclick='$("input[name=mapsel]~ :input").attr( "disabled", "disabled" ); $("#Mapselection_shareMapping").attr( "disabled","" )' />
		</div>
		<select name="shareMapping" id="Mapselection_shareMapping" style="width:200px;">  
			<option value="0">-- select mapping --</option>
		
			<%sel=""; lastorg=new Organization();
			   i=0;
			  for(Mapping tempmap:accMappings){
			   boolean lock=(Boolean)locks.get(i);
			   if(request.getAttribute("shareMapping")!=null && (Long)request.getAttribute("shareMapping")-tempmap.getDbID()==0.0){
				   sel="selected";
			   }
			   else{sel="";}
			   Organization current=tempmap.getOrganization();
			   if(lastorg!=null && current!=null && !lastorg.equals(current)){
				   if(i>0){%>
		    	     </optgroup>  
		           <%}
				   lastorg=current;
				   %>
			         <optgroup label="<%=lastorg.getEnglishName() %>">
			       <%
			     
			   }
			   String cssclass="";
			   if(lock){
				   cssclass+="locked";
			   }
			   if(tempmap.isFinished()){
				   cssclass+=" finished";
			   }
			   if(tempmap.isShared()){
				   cssclass+=" shared";
			   }
			  %> 
			 <option value="<%=tempmap.getDbID() %>" class="<%=cssclass %>" <%=sel%>><%=tempmap.getName() %></option>
			  <% i++;}%>
			    <%if(accMappings.size()>0){ %>
			  	          </optgroup>  
			      <%} %>
			   </select>
			     <s:checkbox label="sh_ckeck" name="shareCheck" value="aBoolean" fieldValue="true" theme="simple" onclick="javascript:if(this.checked)document.getElementById('Mapselection_noshareCheck').checked=false;"/>Share selected
			      <s:checkbox label="sh_ckeck" name="noshareCheck" value="aBoolean" fieldValue="false" theme="simple" onclick="javascript:if(this.checked)document.getElementById('Mapselection_shareCheck').checked=false;"/>Stop sharing
			     </div>
		</li>
		<li><div style="margin-top: 5px; height: 20px;"><div style="float:left;"><s:radio name="mapsel" list="%{#{'deletemaps':'Delete mappings'}}" cssStyle="float:left;"
			onclick='$("input[name=mapsel]~ :input").attr( "disabled", "disabled" ); $("#Mapselection_deleteMapping").attr( "disabled","" )' />
		</div>
		<select name="deleteMapping" id="Mapselection_deleteMapping" style="width:200px;">  
			<option value="0">-- select mapping --</option>
		
			<%sel=""; lastorg=new Organization();
			  i=0;
			  for(Mapping tempmap:accMappings){
			   boolean lock=(Boolean)locks.get(i);
			   if((Long)request.getAttribute("deleteMapping")-tempmap.getDbID()==0.0){
				   sel="selected";
			   }
			   else{sel="";}
			   Organization current=tempmap.getOrganization();
			   if(lastorg!=null && current!=null && !lastorg.equals(current)){
				   if(i>0){%>
		    	     </optgroup>  
		           <%}
				   lastorg=current;
				   %>
			         <optgroup label="<%=lastorg.getEnglishName() %>">
			       <%
			     
			   }
			   String cssclass="";
			   if(lock){
				   cssclass+="locked";
			   }
			   if(tempmap.isFinished()){
				   cssclass+=" finished";
			   }
			   if(tempmap.isShared()){
				   cssclass+=" shared";
			   }
			  %> 
			 <option value="<%=tempmap.getDbID() %>" class="<%=cssclass %>" <%=sel%>><%=tempmap.getName() %></option>
			  <%
			   i++;}%>
			    <%if(accMappings.size()>0){ %>
			  	          </optgroup>  
			      <%} %>
			   </select></div>
		</li>
   <s:if test="hasActionErrors()">
   <li>
		<s:iterator value="actionErrors">
			<span class="errorMessage"><s:property escape="false" /> </span>
		</s:iterator>
	</li>
	</s:if>
	</ol>
	<p align="left">
	
	<a class="button" href="#" onclick="this.blur();val1=radioval();ajaxMappingSelectionRequest(radioval(), document.mapform.mapName.value, document.mapform.editMapping.options[document.mapform.editMapping.selectedIndex].value, document.mapform.templateSel.options[document.mapform.templateSel.selectedIndex].value,document.mapform.deleteMapping.options[document.mapform.deleteMapping.selectedIndex].value,document.mapform.shareMapping.options[document.mapform.shareMapping.selectedIndex].value,document.mapform.shareCheck.checked,document.mapform.noshareCheck.checked); "><span>Submit</span></a>  
	</p>
  
	</fieldset>
	<p><img src="images/locked.png" style="float:left; width:16px; margin-left: -5px;margin-right: 2px;"/><font size="0.9em;"><i>: Locked mappings </i></font>
	&nbsp;<img src="images/shared.png" style="float:center; width:22px; margin-left: -5px;margin-right: 2px;"/><font size="0.9em;"><i>: Shared mappings </i></font>
	&nbsp;<img src="images/complete.png" style=" width:16px; margin-left: -5px;margin-right: 2px;"/><font size="0.9em;"><i>: LIDO complete mappings</i></font></p>
</s:form>
</s:elseif>
<s:elseif test="editMapping!=null && editMapping>0 && mapsel!=null && !mapsel.equalsIgnoreCase('0') && missedMaps.size()==0">
	<s:if test="!hasActionErrors()">
	<div id="editredirect"><%=request.getAttribute("editMapping") %></div>	
	</s:if>

</s:elseif>
</s:else>
</div>

