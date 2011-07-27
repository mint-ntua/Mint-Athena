<%@ include file="top.jsp"%>
<%int count=2;
if( user.hasRight(User.ADMIN))
  count=3;

%>


<script src="js/mapping/lib/yui/yahoo/yahoo-min.js"></script>
<script src="js/mapping/lib/yui/event/event-min.js"></script>
<script src="js/mapping/lib/yui/dom/dom-min.js"></script>
<script src="js/mapping/lib/yui/element/element-min.js"></script>
<script src="js/mapping/lib/yui/dragdrop/dragdrop-min.js"></script>
<script src="js/mapping/lib/yui/resize/resize-min.js"></script>
<script src="js/mapping/lib/yui/animation/animation-min.js"></script>
<script src="js/mapping/lib/yui/button/button-min.js"></script>
<script src="js/mapping/lib/yui/container/container-min.js"></script> 
<script src="js/mapping/lib/yui/connection/connection-min.js"></script>
<script src="js/mapping/lib/yui/yahoo-dom-event/yahoo-dom-event.js"></script>
<script src="js/mapping/lib/yui/tabview/tabview-min.js"></script> 
 <script type="text/javascript" src="js/oaiRequest.js"></script>
 
 <!-- Combo-handled YUI CSS files: --> 
<link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/container/assets/skins/sam/container2.css">
<link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/tabview/assets/skins/sam/tabview.css">
<link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/button/assets/skins/sam/button.css" />

<style type="text/css">
.tdLabel {
	color: #333333;
	width: 90px;
}
</style>

<script type="text/javascript">
ddtabmenu.definemenu("menu", <%=count%>) 
YAHOO.namespace("example.container");


YAHOO.example.container.wait= new YAHOO.widget.Panel("wait",  
                                                    { width: "300px",
                                                      fixedcenter: true, 
                                                      close: false, 
                                                      draggable: false, 
                                                      zindex:4,
                                                      modal: true,
                                                      visible: false
                                                    } 
                                                );
    
YAHOO.example.container.wait.setHeader("Please wait...");
YAHOO.example.container.wait.setBody("<img src=\"images/rel_interstitial_loading.gif\"/>");
</script>

<h1>
<p>Import</p>
</h1>
<div id="panel_container" class="yui-skin-sam">
	<script>
		var container = YAHOO.util.Dom.get("panel_container");
		YAHOO.example.container.wait.render(container);
	</script>
</div>
<div id="waitpanel"></div>
<s:form name="impform" action="Import" cssClass="athform" theme="mytheme"
	enctype="multipart/form-data" method="POST" style="width:800px;">
	<fieldset>
	<p>Select your import method:</p>
	<ol>
		<li><s:radio name="mth" list="%{#{'httpupload':'Http Upload'}}"
			onclick='$("input[name=mth]~ :input").attr( "disabled", true ); $("#Import_httpup").attr( "disabled",false)' />
		<s:file name="httpup" theme="simple"  size="60px;" /><font
			style="font-size: 10px;"><i>Only zip, xml, and excel files
		allowed</i></font></li>
		<li><s:radio name="mth"
			list="%{#{'ftpupload':'NTUA FTP Upload'}}"
			onclick='$("input[name=mth]~ :input").attr( "disabled", true ); $("#Import_flist").attr( "disabled",false)' /><s:select
			name="flist" headerKey="0" headerValue="-- Select file--"
			list="ftpFiles" listKey="name" listValue="name" disabled="true"
			theme="simple" /> <font style="font-size: 10px;"><i>NTUA
		FTP:<s:property value="ntuaFtpServer" /></i></font></li>
		<li><s:radio name="mth"
			list="%{#{'urlupload':'Remote FTP/HTTP Upload'}}"
			onclick='$("input[name=mth]~ :input").attr( "disabled", true ); $("#Import_uploadUrl").attr( "disabled",false)' /><s:textfield
			name="uploadUrl" size="60px;" disabled="true" /> <font
			style="font-size: 10px;"><i>Give URL to remote ftp/http
		server</i></font></li>
		<li><s:radio name="mth" list="%{#{'OAIurl':'OAI URL'}}"
			onclick='$("input[name=mth]~ :input").attr( "disabled", true ); $("#Import_oai").attr( "disabled",false);$("#Import_fromdate").attr( "disabled",false);$("#Import_todate").attr( "disabled",false);$("#Import_oaiset").attr( "disabled",false);$("#Import_namespace").attr( "disabled",false)' />
			<s:textfield name="oai" size="60px;" disabled="true" theme="simple"/> <font style="font-size: 10px;"><i>Give
		link to OAI repository</i></font> 
		<a href="javascript:ajaxOAIValidate(document.impform.oai.value,'validate')"><img src="images/oaiurl.png" width="16"></img>&nbsp;check oai url</a> 
		<br/><span id="oai_ch"></span>
		 <br/>
		<s:textfield name="fromdate" label="From Date (YYYY-MM-DD)" style="margin-left:23px;margin-top:5px;" disabled="true"/>&nbsp;&nbsp;&nbsp;&nbsp;<s:textfield name="todate" label="To Date (YYYY-MM-DD)" style="margin-left:-10px;" disabled="true"/>
		 <br/>
		 
		 <span id="oaiset_span">
		 <s:textfield name="oaiset" label="OAI SET" size="60px;" style="margin-left:23px;margin-top:5px;" disabled="true" />
		 </span>
		 <a href="javascript:ajaxOAIValidate(document.impform.oai.value,'fetchsets')"><img width="18"  src="images/oaiset.png">&nbsp;fetch OAI sets</a>
		 <br/>
		 <span id="oains_span">
		 <s:textfield name="namespace" label="Namespace Prefix" size="60px;" style="margin-left:23px;margin-top:5px;" disabled="true"/>
		 </span>
		  <a href="javascript:ajaxOAIValidate(document.impform.oai.value,'fetchns')"><img width="18"  src="images/oaiset.png">&nbsp;fetch OAI namespaces</a>
		 <br/>
		</li>
		<% if( user.can( "server file access" )) { %>
		<li><s:radio name="mth" list="%{#{'SuperUser':'Server filename'}}"
			onclick='$("input[name=mth]~ :input").attr( "disabled", true ); $("#Import_serverFilename").attr( "disabled",false)' /><s:textfield
			name="serverFilename" size="60px;" disabled="true"/> <font style="font-size: 10px;"><i>Server file path for upload</i></font></li>
		<% } %>
		<!--  Alternative uploader organization for uploaders of parent organizations or superusers -->

		<s:if test="%{user.accessibleOrganizations.size>1}">
			<li><s:select label="Upload for Organization" name="uploaderOrg"
				headerKey="0" headerValue="-- Which Organization --"
				list="user.accessibleOrganizations" listKey="dbID" listValue="name"
				required="true" /> <font style="font-size: 10px;"><i>Parent
			organization upload support</i> </font></li>
		</s:if>
		<li><s:checkbox name="isLido" id="isLido"/>	This is a LIDO 0.9 import</li>
		<li><s:checkbox name="isLido10" id="isLido10"/>	This is a LIDO 1.0 import</li>
		  <s:if test="hasActionErrors()">
	    <li>
		<s:iterator value="actionErrors">
			<span class="errorMessage"><s:property escape="false" /> </span>
		</s:iterator>
	</li>
	</s:if>

	</ol>
	<p align="left">	<a class="button" href="#" onclick="this.blur();document.impform.submit();"><span>Submit</span></a>  
				<a class="button" href="#" onclick="this.blur();document.impform.reset();"><span>Reset</span></a>  
			</p>
</fieldset>
</s:form>
<script type="text/javascript">

<%if(request.getParameter("mth")!=null){%>
    var mthr=document.getElementsByName('mth');
	for (var i=0; i<mthr.length; i++)  {
		if (mthr[i].checked)  {
		
		mthr[i].disabled=false;
		mthr[i].click();
		}
	} 
<%}%>
</script>
<%@ include file="footer.jsp"%>
