<%@ include file="top.jsp"%>

<script type="text/javascript">
ddtabmenu.definemenu("menu", 0) //tab 0 selected

</script>

<table width="500">
  
   <tr><td>
  <h1>ATHENA Project Ingestion Server</h1>
  <h5>Funded by the European Commision within the eContentPlus programme</h5>
  </td> </tr>
  
  <tr>
    <td>
<h2>
Login
</h2>

<s:form name="login" action="Login" cssClass="athform" theme="mytheme" style="width:300px;">
	<fieldset>
	<ol>
		<li><s:textfield name="username" label="Username" required="true" />
		<s:fielderror>
			<s:param value="%{username}" />
		</s:fielderror></li>
		<li><s:password name="password" label="Password" required="true" />
		<s:fielderror>
			<s:param value="%{password}" />
		</s:fielderror></li>
	</ol>
	<p align="left">
	
	<a class="button" href="#" onclick="this.blur();login.submit()"><span>Submit</span></a>  
	</p>



	<s:if test="hasActionErrors()">
		<s:iterator value="actionErrors">
			<span class="errorMessage"><s:property escape="false" /> </span>
		</s:iterator>
	</s:if></fieldset>
</s:form>

</td>
</tr>
</table>
<a href="Reminder.action">Forgot your password?</a>
<%@ include file="footer.jsp"%>
