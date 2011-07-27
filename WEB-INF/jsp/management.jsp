<%@ include file="top.jsp"%>
<%@page import="gr.ntua.ivml.athena.persistent.Organization;"%>
<script type="text/javascript">
ddtabmenu.definemenu("menu", 2) //tab 3 selected


</script>

<% java.util.List us=(java.util.List)request.getAttribute("users");
   java.util.List os=(java.util.List)request.getAttribute("orgs");
   String uaction=(String)request.getAttribute("uaction");
   if(uaction==null){uaction="";}
   User u=(User)request.getAttribute("seluser");
   Organization selorg=(Organization)request.getAttribute("selorg");
 
%>
<h1>
<p>Administration</p>
</h1>
	<% if( request.getAttribute( "actionmessage" ) != null ) {  %>
		<p></p>
		<div id="message" style="width: 500px;"><%=(String) request.getAttribute( "actionmessage" )%></div>

		<% }%>
		   <s:if test="hasActionErrors()">
				<s:iterator value="actionErrors">
					<span class="errorMessage"><s:property escape="false" /> </span>
				</s:iterator>
			</s:if>
 
<div id="help">
<p>Select a user login to view all the user details:</p>
</div>


<table>
	<tr>
		<td width="300" valign="top">
		<div style="height: 30px;"><a
			href="Management.action?uaction=createuser"><img
			src="images/edit.gif" width="16" height="16" border="0" alt="edit"
			title="edit" /> Create new user</a>&nbsp;&nbsp; <a
			href="Management.action?uaction=createorg"><img
			src="images/edit.gif" width="16" height="16" border="0" alt="edit"
			title="edit" /> Create new organization</a></div>

		<div style="overflow: auto; height: 200px; width: 500px;">
		<table cellspacing="0" cellpadding="0" bgcolor="#EEEEEE"
			style="background: url(images/grey2.gif) left bottom repeat-x;"
			width="100%">



			<% for(int i=0;i<us.size();i++){
    	User o=(User)us.get(i);
    	%>

			<!-- for each user -->
			<tr>
				<td colspan="7" height="1" bgcolor="silver"></td>
			</tr>
			<tr>
				<td></td>
				<td width="20"><%if(user.getDbID()!=o.getDbID()){%><a
					href="javascript:confirmDelete('Management.action?uaction=deluser&id=<%=o.getDbID() %>','Are you sure you want to delete this user?')">
					
				<img src="images/trash.gif" width="20" height="20" alt="delete"
					title="delete" /></a><%} %></td>
				<td width="20"><%if(user.getDbID()!=o.getDbID()){%><a
					href="Management.action?uaction=edituser&id=<%=o.getDbID() %>"><img
					src="images/edit.gif" width="16" height="16" border="0" alt="edit"
					title="edit" /></a><%} %></td>
				<td><b>Login:</b></td>
				<td><a
					href="Management.action?uaction=showuser&id=<%=o.getDbID() %>"><%=o.getLogin() %></a></td>
				<td><b>Name: </b></td>
				<td><%=o.getLastName() %>, <%=o.getFirstName() %></td>
			</tr>

			<%} %>

		</table>
		</div>
	
		</td>
		<td width="500" align="center" rowspan="2" valign="top"><!--user details showing here if userid param was passed-->
		<%
		
if(u!=null && (uaction.equalsIgnoreCase("edituser") || uaction.equalsIgnoreCase("saveuser") || uaction.equalsIgnoreCase("createuser"))){ 
%>
		<div>
		<table border="0"  cellpadding="0" cellspacing="0" width="372">
		<tr><td>
		<table width="372" cellpadding="0" cellspacing="0" height="13"
			bgcolor="#8CB85C">
			<tr>
				<td height="12" width="13" valign="top" align="left"><img
					src="images/org_top_left.png" width="13" height="12"></td>
				<td height="10"><font style="color: #FFFFFF"><b>Edit User
				info</b></font></td>
				<td height="12" width="13" valign="top" align="right"><img
					src="images/org_top_right.png" width="12" height="13"></td>
			</tr>
		</table>
		</td></tr>
		<tr><td style="background: url(images/grey.gif);" align="left">
		<s:form name="u_manage" id="u_manage" action="Management" cssClass="athform" theme="mytheme" style="width:370px;border: solid 1px silver;margin:0;padding:0;align:left;">
			<fieldset>
			<ol>
			   <s:if test="%{uaction.equals('createuser') or seluser.dbID==null}">
				<li><s:textfield name="seluser.login"  label="Username" required="true"  cssStyle="width:200px;"/>
				</li></s:if>
				<s:else>
					<li><s:textfield name="seluser.login"  label="Username" readonly="true" cssStyle="width:200px;"/>
				</li>
				
				</s:else>
				<s:if test="%{uaction.equals('createuser') or seluser.dbID==null}">
				<li><s:password name="password" label="Password" required="true" cssStyle="width:200px;"/>
				</li>
				</s:if>
				<s:else>
				<li><s:password name="password" label="New Password" cssStyle="width:200px;"/>
				</li>
				</s:else>
				<s:if test="%{uaction.equals('createuser') or seluser.dbID==null}">
				<li><s:password name="passwordconf" label="Password Confirmation" required="true" cssStyle="width:200px;"
					/></li>
				</s:if>
				<s:else>
				<li><s:password name="passwordconf" label="New Password Confirmation" cssStyle="width:200px;"
					/></li>
				
				</s:else>
				<li><s:textfield name="seluser.firstName" label="First Name" cssStyle="width:200px;"
					required="true" /></li>
				<li><s:textfield name="seluser.lastName" label="Last Name" cssStyle="width:200px;"
					required="true" /></li>
				<li><s:textfield name="seluser.email" label="Email" required="true" cssStyle="width:200px;"/></li>
				<li><s:textfield name="seluser.workTelephone" label="Contact phone num" cssStyle="width:200px;"/></li>
				<li><s:textfield name="seluser.jobRole" label="Job role" cssStyle="width:200px;"/>
				</li>
				<li><s:select label="Select Organization" name="orgid"
					headerKey="0" headerValue="-- Please Select --" listKey="dbID"
					listValue="name" list="allOrgs" value="%{seluser.organization.{dbID}}" cssStyle="width:200px;"
					/></li>
				<li>
				<s:if test="%{user.getAthenaRole()=='superuser'}">
				<s:select label="Athena role" name="seluser.athenaRole" cssStyle="width:200px;"  
				list="#{'':'--no role--', 'superuser':'superuser', 'admin':'admin', 'annotator':'annotator', 'annotator, publisher':'annotator, publisher', 'data viewer':'data viewer'}"
				    value="%{seluser.getAthenaRole()}"/>
				 </s:if>
                 <s:else>
				<s:select label="Athena role" name="seluser.athenaRole"   cssStyle="width:200px;"
				list="#{'':'--no role--','admin':'admin', 'annotator':'annotator', 'annotator, publisher':'annotator, publisher', 'data viewer':'data viewer'}"
				    value="%{seluser.getAthenaRole()}"/>
				
				 </s:else>
				</li>
				<li><s:checkbox name="notice" id="notice"/> 
					<s:if test="%{uaction.equals('createuser') or seluser.dbID==null}">
			           Notify user by email for account creation
			        </s:if>
			        <s:else>
			           Notify user by email for account changes
			        </s:else>
				</li>
			</ol>
			<p align="left">
			
				<a class="button" href="#" onclick="this.blur();document.u_manage.submit();"><span>Submit</span></a>  
				<a class="button" href="#" onclick="this.blur();document.u_manage.reset();"><span>Reset</span></a>  
				<input type="hidden" name="uaction" value="saveuser"/>
				<s:if test="%{seluser.dbID!=null}">
				<s:hidden name="seluser.dbID" value="%{seluser.dbID}"/>				
			     </s:if>
				</p>
		
		
		    <p align="right"><a href="Management.action?uaction=edituser&id=<%=u.getDbID() %>"><img
					src="images/edit.gif" width="16" height="16" border="0" alt="edit"
					title="edit" /> Edit user</a></p>
			 <p align="right"><a href="Management.action?uaction=deluser&id=<%=u.getDbID() %>"><img
					src="images/trash.gif" width="20" height="20" alt="delete user"
					title="delete user" />Delete user</a></p>		
			</fieldset>
		</s:form>
		
		</td></tr>
		</table>	
		</div>
		
		<%}%> 

    	<%if(u!=null && uaction.equalsIgnoreCase("showuser")){ %>
		<div>
		<table border="0"  cellpadding="0" cellspacing="0" width="320">
		<tr><td>
		<table width="372" cellpadding="0" cellspacing="0" height="13"
			bgcolor="#8CB85C">
			<tr>
				<td height="12" width="13" valign="top" align="left"><img
					src="images/org_top_left.png" width="13" height="12"></td>
				<td height="10"><font style=" color: #FFFFFF"><b>User
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
				<li><s:textfield name="seluser.login" label="Username"  readonly="true" cssStyle="width:200px;"/>
				</li>
				
				<li><s:textfield name="seluser.firstName" label="First Name" readonly="true" cssStyle="width:200px;"
					/></li>
				<li><s:textfield name="seluser.lastName" label="Last Name" readonly="true" cssStyle="width:200px;"
					/></li>
				<li><s:textfield name="seluser.email" label="Email" readonly="true" cssStyle="width:200px;"/></li>
				<li><s:textfield name="seluser.workTelephone" label="Contact phone num" readonly="true" cssStyle="width:200px;"/></li>
				<li><s:select label="Organization" name="seluser.organization" cssStyle="width:200px;"
					headerKey="0" headerValue="-- No Organization --" listKey="dbID"
					listValue="name" list="allOrgs" value="%{seluser.organization.{dbID}}"
					disabled="true"/></li>
				<li><s:textfield name="seluser.jobRole" label="Job role" readonly="true" cssStyle="width:200px;"/>
				</li>
				<li>
			
				<s:select label="Athena role" name="seluser.athenaRole"  cssStyle="width:200px;"
				list="#{'':'--no role--', 'superuser':'superuser', 'admin':'admin', 'annotator':'annotator', 'annotator, publisher':'annotator, publisher', 'data viewer':'data viewer'}"
				    value="%{seluser.athenaRole}"
					disabled="true"/>
				</li>
				<li><s:textfield name="seluser.accountCreated" label="Acount created" readonly="true" value="%{getText('format.date',{seluser.accountCreated})}" cssStyle="width:200px;"/></li>
			</ol><%if(user.getDbID()!=u.getDbID()){%>
		    <p align="right"><a href="Management.action?uaction=edituser&id=<%=u.getDbID() %>"><img
					src="images/edit.gif" width="16" height="16" border="0" alt="edit"
					title="edit" /> Edit user</a></p>
			 <p align="right"><a href="Management.action?uaction=deluser&id=<%=u.getDbID() %>"><img
					src="images/trash.gif" width="20" height="20" alt="delete user"
					title="delete user" />Delete user</a></p>
					<%} %>			
			</fieldset>
		</s:form>
		
		</td></tr>
		</table>	
		</div>
		
		<%}%> <!-- end user details -->
		
		<%
		if(selorg!=null && (uaction.equalsIgnoreCase("editorg") || uaction.equalsIgnoreCase("saveorg") || uaction.equalsIgnoreCase("createorg"))){ 
		%>
		<div>
		<table border="0"  cellpadding="0" cellspacing="0" width="320">
		<tr><td>
		<table width="320" cellpadding="0" cellspacing="0" height="13"
			bgcolor="#8CB85C">
			<tr>
				<td height="12" width="13" valign="top" align="left"><img
					src="images/org_top_left.png" width="13" height="12"></td>
				<td height="10"><font style="color: #FFFFFF"><b>Edit Organization
				info</b></font></td>
				<td height="12" width="13" valign="top" align="right"><img
					src="images/org_top_right.png" width="12" height="13"></td>
			</tr>
		</table>
		</td></tr>
		<tr><td style="background: url(images/grey.gif);" align="left">
		<s:form name="org_manage" action="Management" cssClass="athform" theme="mytheme" style="width:318px;border: solid 1px silver;margin:0;padding:0;align:left;">
			<fieldset>
			<ol>
			    <li>
			      <s:select label="Country" name="selorg.country"  
				list="#{'':'--no country--', 'Austria':'Austria', 'Belgium':'Belgium', 'Bulgaria':'Bulgaria', 
				'Cyprus':'Cyprus', 'Czech Rep.':'Czech Rep.', 'Denmark':'Denmark', 'Estonia':'Estonia'
				, 'Finland':'Finland', 'France':'France', 'Germany':'Germany', 'Greece':'Greece'
				, 'Hungary':'Hungary', 'Ireland':'Ireland',	'Israel':'Israel', 'Italy':'Italy', 'Latvia':'Latvia',
				  'Lithuania':'Lithuania','Luxembourg':'Luxembourg','Malta':'Malta',
				'Netherlands':'Netherlands','Poland':'Poland','Portugal':'Portugal',
				'Romania':'Romania','Russia':'Russia','Slovakia':'Slovakia','Slovenia':'Slovenia',
				'Spain':'Spain','Sweden':'Sweden','Switzerland':'Switzerland','United Kingdom':'United Kingdom',
				'Europe':'Europe','International':'International'
				}"  value="%{selorg.country}" required="true"/>
			    </li>
				<li><s:textfield name="selorg.englishName"  cssStyle="width:290px" label="English name" required="true" />
				</li>
				<li><s:textfield name="selorg.originalName" cssStyle="width:290px"  label="Name" required="true" />
				</li>
				<li><s:textfield name="selorg.shortName"  label="Organization acronym"/>
				</li>
				<li>
				<s:select label="Type" name="selorg.type"  
				list="#{'':'--not specified--', 'Museum and Gallery':'Museum and Gallery','Library':'Library','Archive':'Archive',
				 'Audio Visual Organization':'Audio Visual Organization','Research and educational organisation':'Research and educational organisation',
				 'Cross-domain organisation':'Cross-domain organisation','Publisher':'Publisher',
				 'Heritage site':'Heritage site','Other':'Other'}"  value="%{selorg.type}" required="true"/>
				 </li>
				<li><s:textfield name="selorg.address" label="Address" cssStyle="width:290px" 
					/></li>
					<li><s:textfield name="selorg.urlPattern"  cssStyle="width:290px" label="Organization url"/>
				</li>
				<li><s:textarea name="selorg.description" cssStyle="width:290px" label="Organization description" />
				</li>
				<%
				
				if(user.getAthenaRole().equalsIgnoreCase("superuser") || user.getOrganization()==null || (uaction.equalsIgnoreCase("editorg") && selorg.getParentalOrganization()==null)){ %>
				<li><s:select label="Select parent organization" name="parentorg" cssStyle="width:290px"
					headerKey="0" headerValue="-- No parent --" listKey="dbID"
					listValue="name+', '+country" list="connOrgs" value="%{selorg.parentalOrganization.{dbID}}" 
					/></li><%}else{ %>
			    <li><s:select label="Select parent organization" name="parentorg"  cssStyle="width:290px" listKey="dbID"
					listValue="name+', '+country" list="connOrgs" value="%{selorg.parentalOrganization.{dbID}}" 
					/></li><%} %>
				<li><s:select label="Select primary contact user" name="primaryuser"
					headerKey="0" headerValue="-- Please Select --" listKey="dbID"
					listValue="login" list="adminusers" value="%{selorg.primaryContact.{dbID}}" required="true"
					/></li>
			
			</ol>
			<p align="left">	
				<a class="button" href="#" onclick="this.blur();document.org_manage.submit();"><span>Submit</span></a>  
				<a class="button" href="#" onclick="this.blur();document.org_manage.reset();"><span>Reset</span></a>  
	
				<input type="hidden" name="uaction" value="saveorg"/>
				<s:if test="%{selorg.dbID!=null}">
				<s:hidden name="selorg.dbID"/>				
			     </s:if>
				</p>
		
			 <p align="right"><a href="Management.action?uaction=delorg&id=<%=selorg.getDbID() %>"><img
					src="images/trash.gif" width="20" height="20" alt="delete organization"
					title="delete organization" />Delete organization</a></p>		
			</fieldset>
		</s:form>
		
		</td></tr>
		</table>	
		</div>
		
		<%}%> 
		
		<%
		if(selorg!=null && uaction.equalsIgnoreCase("showorg")){ 
		%>
		<div>
		<table border="0"  cellpadding="0" cellspacing="0" width="320">
		<tr><td>
		<table width="320" cellpadding="0" cellspacing="0" height="13"
			bgcolor="#8CB85C">
			<tr>
				<td height="12" width="13" valign="top" align="left"><img
					src="images/org_top_left.png" width="13" height="12"></td>
				<td height="10"><font style=" color: #FFFFFF"><b>Organization
				info</b></font></td>
				<td height="12" width="13" valign="top" align="right"><img
					src="images/org_top_right.png" width="12" height="13"></td>
			</tr>
		</table>
		</td></tr>
		<tr><td style="background: url(images/grey.gif);" align="left">
		<s:form action="Management" cssClass="athform" theme="mytheme" style="width:318px;border: solid 1px silver;margin:0;padding:0;align:left;">
			<fieldset>
			<ol>
			    <li>
			      <s:select label="Country" name="selorg.country"  
				list="#{'':'--no country--', 'Austria':'Austria', 'Belgium':'Belgium', 'Bulgaria':'Bulgaria', 
				'Cyprus':'Cyprus', 'Czech Rep.':'Czech Rep.', 'Denmark':'Denmark', 'Estonia':'Estonia'
				, 'Finland':'Finland', 'France':'France', 'Germany':'Germany', 'Greece':'Greece'
				, 'Hungary':'Hungary', 'Ireland':'Ireland', 'Israel':'Israel', 'Italy':'Italy', 'Latvia':'Latvia',
				  'Lithuania':'Lithuania','Luxembourg':'Luxembourg','Malta':'Malta',
				'Netherlands':'Netherlands','Poland':'Poland','Portugal':'Portugal',
				'Romania':'Romania','Russia':'Russia','Slovakia':'Slovakia','Slovenia':'Slovenia',
				'Spain':'Spain','Sweden':'Sweden','Switzerland':'Switzerland','United Kingdom':'United Kingdom',
				'Europe':'Europe','International':'International'
				}"  value="%{selorg.country}" disabled="true"/>
			    </li>
				<li><s:textfield name="selorg.englishName"  cssStyle="width:290px" label="English name" readonly="true" />
				</li>
				<li><s:textfield name="selorg.originalName"  cssStyle="width:290px" label="Name" readonly="true" />
				</li>
				<li><s:textfield name="selorg.shortName"  label="Organization acronym"  readonly="true" />
				</li>
				<li>
				<s:select label="Type" name="selorg.type"  
				list="#{'':'--not specified--', 'Museum and Gallery':'Museum and Gallery','Library':'Library','Archive':'Archive',
				 'Audio Visual Organization':'Audio Visual Organization','Research and educational organisation':'Research and educational organisation',
				 'Cross-domain organisation':'Cross-domain organisation','Publisher':'Publisher',
				 'Heritage site':'Heritage site','Other':'Other'}"  value="%{selorg.type}" disabled="true"/>
				 </li>
				<li><s:textfield name="selorg.address" label="Address" cssStyle="width:290px" 
					readonly="true" /></li>
					<li><s:textfield name="selorg.urlPattern" cssStyle="width:290px"  label="Organization url"  readonly="true" />
				</li>
				<li><s:textarea name="selorg.description" cssStyle="width:290px" label="Organization description"  readonly="true" />
				</li>
				<li><s:select label="Select parent organization" name="parentorg"  cssStyle="width:290px" 
					headerKey="0" headerValue="-- No parent --" listKey="dbID"
					listValue="name+', '+country" list="connOrgs" value="%{selorg.parentalOrganization.{dbID}}" disabled="true"
					/></li>
				<li><s:select label="Select primary contact user" name="primaryuser"
					headerKey="0" headerValue="-- Please Select --" listKey="dbID"
					listValue="login" list="adminusers" value="%{selorg.primaryContact.{dbID}}"
					disabled="true"/></li>
			</ol>
		    
		
		    <p align="right"><a href="Management.action?uaction=editorg&id=<%=selorg.getDbID() %>"><img
					src="images/edit.gif" width="16" height="16" border="0" alt="edit"
					title="edit" /> Edit organization</a></p>
			 <p align="right"><a href="Management.action?uaction=delorg&id=<%=selorg.getDbID() %>"><img
					src="images/trash.gif" width="20" height="20" alt="delete organization"
					title="delete organization" />Delete organization</a></p>		
			</fieldset>
		</s:form>
		
		</td></tr>
		</table>	
		</div>
		
		<%}%> 
		</td>

	</tr>
	<%   if(os==null || os.size()==0) {%>
		<tr>
		<td width="300" valign="top">
		<div id="help">
		<p>No organizations found. <a href="Management.action?uaction=createorg">Create a new organization.</a></p>
		</div>
		</td>
		</tr>
	
	<%}else{ %>
	<tr>
		<td width="300" valign="top">
		<div id="help">
		<p>Select an organization to view all its details:</p>
		</div>
		<div style="overflow: auto; height: 200px; width: 500px;">
		<table cellspacing="0" cellpadding="0" bgcolor="#EEEEEE"
			style=" background: url(images/grey2.gif)"
			width="100%">

			<%String html="";%>
			<%for(int i=0;i<os.size();i++){
    	Organization o=(Organization)os.get(i);%>

			<!-- for each organization -->
			<tr>
				<td colspan="5" height="1" bgcolor="silver"></td>
			</tr>
			<tr>
				<td width="20"><img src="images/spacer.gif" width="1"></td>
				<td width="20"><a
					href="javascript:confirmDelete('Management.action?uaction=delorg&id=<%=o.getDbID() %>','Are you sure you want to delete this organisation?')">
				<img src="images/trash.gif" width="20" height="20" alt="delete"
					title="delete" /></a></td>
				<td width="20"><a
					href="Management.action?uaction=editorg&id=<%=o.getDbID() %>"><img
					src="images/edit.gif" width="16" height="16" border="0" alt="edit"
					title="edit" /></a></td>
				<td><b>Name:&nbsp;</b></td>
				<td width="90%"><a
					href="Management.action?uaction=showorg&id=<%=o.getDbID() %>"><%=o.getName()%></a></td>
			</tr>
			<%=printOrg(o,2,null)%>
			<%} %>

		</table>
		</div>
		</td>
		<td width="500">&nbsp;
		</td>

	</tr>
	<%} %>
</table>



<%@ include file="footer.jsp"%>
<%!private StringBuffer printOrg(Organization parent, int depth, StringBuffer html){
	if( html == null ) html = new StringBuffer();
   if(parent.getDependantOrganizations()!=null){
		
	for(Organization org: parent.getDependantOrganizations() ) {
				html.append( "<tr><td colspan=\"5\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><td colspan=\"5\" height=\"1\" bgcolor=\"silver\"></td></tr>"  
		     +"<tr><td><img src=\"images/spacer.gif\" width=\""+depth*10+"\" height=\"10\"/></td><td width=\"20\">"
	        +"<a href=\"javascript:confirmDelete('Management.action?uaction=delorg&id="+org.getDbID()+"','Are you sure you want to delete this organisation?')\">"
	        +"<img src=\"images/trash.gif\" width=\"20\" height=\"20\" alt=\"delete\" title=\"delete\"/></a></td>"
			 +"<td width=\"20\"><a href=\"Management.action?uaction=editorg&id="+org.getDbID()+"\"><img src=\"images/edit.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"edit\" title=\"edit\"/></a></td>"
			 +"<td><b>Name:&nbsp;</b></td>"

			 +"<td width=\"90%\"><a href=\"Management.action?uaction=showorg&id="+org.getDbID()+"\">"+org.getName()+"</a></td></tr></table></td></tr>");
			 html=printOrg(org,depth+3,html);
		}
	}
	return html;
}

%>

