<%@ include file="top.jsp" %>  
<%@page import="java.util.List"%>
<%@page import="gr.ntua.ivml.athena.persistent.Organization"%>
<%@page import="gr.ntua.ivml.athena.persistent.DataUpload"%>
<%@page import="gr.ntua.ivml.athena.db.DB" %>

<script type="text/javascript" src="js/animatedcollapse.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>

<script type="text/javascript">
ddtabmenu.definemenu("menu", 0); //tab 1 selected
</script>

<% String sessionId = request.getSession().getId();
%>
<script> document.title = "Athena Home"</script>


<br>	
  <div id="openBox" style="clear: both; width:590px; overflow: auto;padding:10px;">
  <h1>ATHENA Project Ingestion Server</h1>
  
  <p>You are currently logged in as user <a href="Profile"><%
  if(user!=null)
  {
	  out.print(user.getLogin());
  }%></a>
  
    <% 
  String role="";
  if(user.hasRight(User.SUPER_USER)){
	  role="superuser";
  }
  else if(user.getOrganization()!=null){
	  
	  if(user.hasRight(User.ADMIN))
	 	 role="administrator";
	  else if((user.hasRight(User.PUBLISH)))
		  role+="annotator, publisher";
	  else if(user.hasRight(User.MODIFY_DATA))
		  role="annotator";
	  else if(user.hasRight(User.VIEW_DATA)){role="data viewer";}
	  else{role="no role";}
  }%> 
  
  (role: <b style="color:  #8cb85c;"><%=role %></b>)
 </p>  
  <br>

<!--
<b style="color:  #8cb85c;">IMPORTANT</b><br>   
The system has migrated to <b style="color:  #8cb85c;">LIDO</b> version 0.9. <br>
All new mappings will use the latest version while you can still work with older ones. Final transformations will be performed with 0.9 mappings for the official server.<br>
Read release notes for more information.
<br><br>
-->
<b style="color:  #8cb85c;">SUPPORT & FEEDBACK</b><br>   
The system is using LIDO 0.9 for mappings and offers the ability to directly import both versions 0.9 and 1.0.<br/>
For more details regarding LIDO visit <a href="http://www.lido-schema.org">http://www.lido-schema.org</a><br/>
Information and training materials for the use of the system and LIDO can be found at <a href='http://www.athenaeurope.org/index.php?en/159/training'>http://www.athenaeurope.org/index.php?en/159/training</a><br>
Support and Feedback mailing list: athena-helpdesk@amitie.it<br>
Contact: <a href="mailto:athena-admin@image.ntua.gr">athena-admin@image.ntua.gr</a><br>
Ingestion software development: <a href="http://mint.image.ece.ntua.gr">http://mint.image.ece.ntua.gr</a>
<br>

<br />

<a id="displayText" href="javascript:animatedcollapse.toggle('rn');"><h3>READ latest</h3></a>

<script type="text/javascript">animatedcollapse.addDiv('rn');</script> 
<div id="rn" style="display: none">
</div>
<br/>

<a id="displayText" href="javascript:animatedcollapse.toggle('rn2co90');"><h3>Release notes - 26 Mar 2010</h3></a>

<script type="text/javascript">animatedcollapse.addDiv('rn2co90');</script> 
<div id="rn2co90" style="display: none">

<br>
<b style="color:  #8cb85c;">LIDO:</b><br>
<ul style="padding-left: 14px;">
<li><b style="color:  #8cb85c;">new</b> mappings use LIDO version 0.9</li>
<li>existing mappings are still functional</li>
<li>mapping labels include target schema information</li>
</ul>

<br />
<b style="color:  #8cb85c;">EUROPEANA PREVIEW:</b><br>
<ul style="padding-left: 14px;">
<li>you can access an HTML preview of the produced ESE record <img src=images/webview.png width="18" /> <img src=images/athenatrans.gif width="14" /></li>
<li>lido2ese transformation is still under revision, please submit errors/comments</li>
</ul>

<br>
<b style="color:  #8cb85c;">OVERVIEW:</b><br>
<ul style="padding-left: 14px;">
<li><b style="color:  #8cb85c;">Import:</b><br>
<ul style="padding-left: 14px;"><li>HTTP upload limit raised</li></ul>
</li>

<li><b style="color:  #8cb85c;">Import Tree:</b><br>
<ul style="padding-left: 14px;"><li>fixed a bug that was producing wrong statistics for some elements <img src=images/i-icon-nofill.gif width="12" /></li></ul>
</li>

<li><b style="color:  #8cb85c;">Locks:</b><br>
<ul style="padding-left: 14px;">
<li>imports and mappings are locked when they are used for editing or transformation; multiple browser tabs or windows affect availability <img src=images/locked.png width="12" /></li>
<li>you can now manage conflicting locks in the overview tab <img src=images/info.png width="14" /></li>
</ul>
</li>

</ul>
<br>
<b style="color:  #8cb85c;">MAPPING EDITOR:</b><br>
<ul style="padding-left: 14px;">
<li>graphical changes for improved browsing</li>
<li>the icons for duplication <img src=images/add.png width="12" /> and deletion <img src=images/close.png width="12" /> were moved to the right part for improved navigation <img src=images/expand_grey.png width="12" /></li>
<li>a dialogue was added for element duplication <img src=images/add.png width="14" /></li>
<li>element attributes can now be accessed from the <img src=images/expand.png width="14" /> icon</li>
<li>non-english character sets for constant values are back</li>
<li>drag&drop visual indications</li>
<li>structural element target area text has changed to 'structural'</li>
</ul>

<br>
<b style="color:  #8cb85c;">GENERAL:</b><br>
<ul style="padding-left: 14px;">
<li>xml engine upgrade, update, performance tweak</li>
<li>improved browser compatibility, please report any issues</li>
</ul>


</div>

<br>
<a id="displayText" href="javascript:animatedcollapse.toggle('rn2b62');"><h3>Release notes - 11 Mar 2010 <i>v.2b62</i> </h3></a>

<script type="text/javascript">animatedcollapse.addDiv('rn2b62');</script> 
<div id="rn2b62" style="display: none">

<br>
<b style="color:  #8cb85c;">IMPORT:</b><br>
<ul style="padding-left: 14px;">
<li>OAI-PMH Harvesting: baseURL, protocol and response validation <img src=images/oaiurl.png width="18"></img></li>
<li>OAI-PMH Harvesting:support for OAI sets <img src=images/oaiset.png width="18"></img></li>
<li>XML Parsing:improved logging</li>
</ul>

<br>
<b style="color:  #8cb85c;">OVERVIEW:</b><br>
<ul style="padding-left: 14px;">
<li><b style="color:  #8cb85c;">Item Level/Label (step 1):</b><br>
<ul style="padding-left: 14px;"><li>you can assign attributes as item label</li></ul>
</li>


<li><b style="color:  #8cb85c;">Statistics:</b><br>
<ul style="padding-left: 14px;">
<li>statistics page for imports <img src=images/stats2.png width="18"></img></li>
<li>element statistics on input tree <img src=images/i-icon-nofill.gif width="16"></img></li>
</ul>
</li>

<li><b style="color:  #8cb85c;">Mappings Management:</b><br>
<ul style="padding-left: 14px;">
<li>new list for mappings<br></li>
<li>you can share mappings (read only, for 'use as template' option)<br></li>
<li>when loading an existing mapping (edit/use as template), system checks against the selected import structure before proceeding to the editor (currently, all xpaths within a mapping need to be present in the import structure)<br></li>
<li>check for complete mappings (if all mandatory mappings to LIDO exist, mapping appears with appropriate icon <img src=images/complete.png width="18"></img> in the mapping list)</li> 
</ul>
</li>

<li><b style="color:  #8cb85c;">Item Transformation Preview:</b><br>
<ul style="padding-left: 14px;">
<li>list of missing mappings to mandatory elements</li>
<li>list of invalid xpaths in mappings</li>
</ul>
</li>
</ul>

<br>
<b style="color:  #8cb85c;">MAPPING EDITOR:</b><br>
<ul style="padding-left: 14px;">
<li>input element information popup <img src=images/i-icon-nofill.gif width="16"> includes a list of mappings for which the element was used</li>
<li>drag&drop visual indications</li>
<li>you can assign constant values to attributes</li>
<li>mappings summary</li>
<li>preview transformation (by default on first import item)</li>
<li>visual indications on the target schema (green: element has been mapped to, red: element is mandatory and mapping is missing)</li>
<li>visual indications on the input schema (blue: element has been used in mappings - click element info <img src=images/i-icon-nofill.gif width="16"> for more details)</li>
<li>you can apply conditional mapping to structural elements</li>
<li>you can now access all higher level elements & properties of the target schema</li>
</ul>

<br>
<b style="color:  #8cb85c;">GENERAL:</b><br>
<ul style="padding-left: 14px;">
<li>new reporting page (statistics about users, organizations, imported and transformed items - access through Data Report tab for administrators, summary available on Home screen)</li>
<li>import and transformation queueing</li>
<li>several bug fixes and performance tweaks</li>
<li>improved browser compatibility (mozilla firefox is still recommended)</li>
</ul>


</div>

<script type="text/javascript">
animatedcollapse.ontoggle=function($, divobj, state){ //fires each time a DIV is expanded/contracted
	//$: Access to jQuery
	//divobj: DOM reference to DIV being expanded/ collapsed. Use "divobj.id" to get its ID
	//state: "block" or "none", depending on state
}

animatedcollapse.init()
</script>

<!--
<h5 align="right"><i>ver. 2co90</i></h5>
-->
   </div>
	
	<BR>
 
  <div id="openBox" style="float: left; width:275px; overflow: auto;padding:10px;">
  <h3>
  User roles:</h3>
  <ul style="margin-left:10px;padding:10px;">
  <li>Administrator: This user can create/update/delete users and children organizations for the organization he is administering. He/she can also perform uploads and all available data handling functions provided by the system.</li>
  <li>Annotator: This user can upload data for his/her organization (and any children organizations) and perform all available data handling functions (view items, delete items, mappings etc) provided by the system, apart from final publishing of data.
  </li>
   <li>Annotator & Publisher: This user has all the righs of an annotator as well as rights to perform final publishing of data.
  </li> 
   <li>Data Viewer: This user only has viewing righs for his organization (and any of its children organizations).
  </li>
  <li>No role: A user that has registered for an organization but has not yet been assigned any rights.</li>
  </ul>
   </div>
  
   <div id="openBox" style="float: left; width:275px; overflow: auto;padding:10px;">
   <h3>
   Registered organizations:</h3>
  <div style="overflow: auto; height: 320px;padding:10px;">
   <s:iterator value="allOrgs">
 
	  <p><s:property value="englishName"/> (<s:property value="country"/>)</p>
   </s:iterator>

   </div>
   </div>
   
 
  <BR>
<div style="clear: both; width:580px; overflow: auto;padding:10px;"></div>

<%@ include file="footer.jsp" %>  
