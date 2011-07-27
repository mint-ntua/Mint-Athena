<%@ include file="top.jsp"%>

<script type="text/javascript">
ddtabmenu.definemenu("menu", 0) //tab 0 selected

</script>
<h1>
<p>Password Reminder</p>
Please specify your username. A new password will be sent to you via email briefly.
</h1>
<s:form name="reminder" action="Reminder" cssClass="athform" theme="mytheme">
	<fieldset>
	<ol>
		<li><s:textfield name="username" label="Username" required="true" /></li>
	</ol>
	<p align="left">
	
	<a class="button" href="#" onclick="this.blur();reminder.submit()"><span>Submit</span></a>  
	</p>



	<s:if test="hasActionErrors()">
		<s:iterator value="actionErrors">
			<span class="errorMessage"><s:property escape="false" /> </span>
		</s:iterator>
	</s:if></fieldset>

</s:form>

If you have any questions about your account please contact <a href="mailto:athena-admin@image.ntua.gr">athena-admin@image.ntua.gr</a>.
<%@ include file="footer.jsp"%>
