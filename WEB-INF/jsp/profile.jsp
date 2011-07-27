<%@ include file="top.jsp" %> 
<%@page import="gr.ntua.ivml.athena.persistent.Organization;"%>
<script type="text/javascript">
ddtabmenu.definemenu("menu", 1) //tab 2 selected

</script>

<%  String uaction=(String)request.getAttribute("uaction");
   if(uaction==null){uaction="";}
  

%>
<h1>
<p>My profile</p>
</h1>
	<% if( request.getAttribute( "actionmessage" ) != null ) {  %>
		<p></p>
		<div id="message" style="width: 500px;"><%=(String) request.getAttribute( "actionmessage" )%></div>

		<% }%>
		   <s:if test="hasActionErrors()">
				<s:iterator value="actionErrors">
					<span id="message" style="width: 500px;"><s:property escape="false" /> </span>
				</s:iterator>
			</s:if>
 
<div id="help">
<p>Your registered details follow. Click on "edit details" to update them:</p>
</div>


<table>
	<tr>
		<td width="400" align="center" rowspan="2" valign="top">
		<%
         if(uaction.equalsIgnoreCase("edituser") || uaction.equalsIgnoreCase("saveuser") || uaction.equalsIgnoreCase("savepass")){ 
        %>
		
		<table border="0"  cellpadding="0" cellspacing="0" width="372">
		<tr><td>
		<table width="372" cellpadding="0" cellspacing="0" height="13"
			bgcolor="#8CB85C">
			<tr>
				<td height="12" width="13" valign="top" align="left"><img
					src="images/org_top_left.png" width="13" height="12"></td>
				<td height="10"><font style="font-size: 0.9em; color: #FFFFFF"><b>Edit details</b></font></td>
				<td height="12" width="13" valign="top" align="right"><img
					src="images/org_top_right.png" width="12" height="13"></td>
			</tr>
		</table>
		</td></tr>
		<tr><td style="background: url(images/grey.gif);" align="left">
		<s:form name="profileform" action="Profile" cssClass="athform" theme="mytheme" style="width:370px;border: solid 1px silver;margin:0;padding:0;align:left;">
			<fieldset>
			<ol>
				<li><s:textfield name="current_user.login"  label="Login" readonly="true" cssStyle="width:200px;"/>
				</li>
				<li><s:textfield name="current_user.firstName" label="First Name" cssStyle="width:200px;"
					required="true" /></li>
				<li><s:textfield name="current_user.lastName" label="Last Name" cssStyle="width:200px;"
					required="true" /></li>
				<li><s:textfield name="current_user.email" label="Email" required="true" cssStyle="width:200px;"/></li>
				<li><s:textfield name="current_user.workTelephone" label="Contact phone num" cssStyle="width:200px;"/></li>
				<li><s:textfield name="current_user.jobRole" label="Job role" cssStyle="width:200px;"/>
				</li>
				<li><s:select label="Select Organization" name="orgid"
					headerKey="0" headerValue="-- Please Select --" listKey="dbID" cssStyle="width:200px;"
					listValue="name" list="allOrgs" value="%{current_user.organization.{dbID}}"
					/></li>
				</ol>
				<p align="left"><a class="button" href="#" onclick="this.blur();document.profileform.submit();"><span>Submit</span></a>  
				<a class="button" href="#" onclick="this.blur();document.profileform.reset();"><span>Reset</span></a>  
			
				<input type="hidden" name="uaction" value="saveuser"/>
				
				<s:hidden name="id" value="%{id}"/>				
	
				</p>
			</fieldset>
		</s:form>
			<s:form name="passform" action="Profile" cssClass="athform" theme="mytheme" style="width:370px;border: solid 1px silver;margin:0;padding:0;align:left;">
			<fieldset>
			<ol>
	
				<li><b>Reset Password</b></li>
				<li><s:password name="pass" label="New Password" cssStyle="width:200px;"/>
				</li>
				<li><s:password name="passconf" label="New Password Confirmation" cssStyle="width:200px;"
					/></li>
			</ol>
			<p align="left">	<a class="button" href="#" onclick="this.blur();document.passform.submit();"><span>Submit</span></a>  
				<a class="button" href="#" onclick="this.blur();document.passform.reset();"><span>Reset</span></a>  
			
				<input type="hidden" name="uaction" value="savepass"/>
				
				<s:hidden name="id" value="%{id}"/>				
	
				</p>
			</fieldset>
		</s:form>
		
		</td></tr>
		</table>	
		
		
		<%}else{ %>
		<table border="0"  cellpadding="0" cellspacing="0" width="372">
		<tr><td>
		<table width="372" cellpadding="0" cellspacing="0" height="13"
			bgcolor="#8CB85C">
			<tr>
				<td height="12" width="13" valign="top" align="left"><img
					src="images/org_top_left.png" width="13" height="12"></td>
				<td height="10"><font style="font-size: 0.9em; color: #FFFFFF"><b>Registered
				info</b></font></td>
				<td height="12" width="13" valign="top" align="right"><img
					src="images/org_top_right.png" width="12" height="13"></td>
			</tr>
		</table>
		</td></tr>
		<tr><td style="background: url(images/grey.gif);" align="left">
		<s:form cssClass="athform" theme="mytheme" style="width:370px;border: solid 1px silver;margin:0;padding:0;">
			<fieldset>
			<ol>
				<li><s:textfield name="current_user.login" label="Username"  readonly="true" cssStyle="width:200px;"/>
				</li>
				
				<li><s:textfield name="current_user.firstName" label="First Name" readonly="true" cssStyle="width:200px;"
					/></li>
				<li><s:textfield name="current_user.lastName" label="Last Name" readonly="true" cssStyle="width:200px;"
					/></li>
				<li><s:textfield name="current_user.email" label="Email" readonly="true" cssStyle="width:200px;"/></li>
				<li><s:textfield name="current_user.workTelephone" label="Contact phone num" readonly="true" cssStyle="width:200px;"/></li>
				<li>
				<s:textfield name="current_user.organization" value="%{current_user.organization.name}" label="Organization" readonly="true" cssStyle="width:200px;"/>
				</li>
				<li><s:textfield name="current_user.jobRole" label="Job role" readonly="true" cssStyle="width:200px;"/>
				</li>
				<li>
				<s:textfield name="current_user.athenaRole" label="Athena role" readonly="true" value="%{current_user.athenaRole}" cssStyle="width:200px;"
					/>
				</li>
				<li><s:textfield name="current_user.accountCreated" label="Acount created" readonly="true" cssStyle="width:200px;"/>
			</ol>
		    <p align="right"><a href="Profile.action?uaction=edituser"><img
					src="images/edit.gif" width="16" height="16" border="0" alt="edit"
					title="edit" /> Edit details</a></p>
			</fieldset>
		</s:form>
		
		</td></tr>
		</table>	
		
		<%}%> <!-- end user details -->
		
				</td>

	</tr>
	</table>



<%@ include file="footer.jsp"%>
