<%@ include file="top.jsp"%>
<script type="text/javascript">
ddtabmenu.definemenu("menu", 1) //tab 1 selected
</script>
<script type="text/javascript" language="javascript" src="js/pwd_meter.js"></script>
<h1>
<p>Register</p>
</h1>
<s:form name="regform" action="Register" cssClass="athform" theme="mytheme" acceptcharset="UTF-8">
	<fieldset>
	<ol>
		<li><s:textfield name="username" label="Username" required="true" />
		</li>
		<li><table border="0" cellpadding="0" cellspacing="0"><tr><td><s:password name="password" label="Password" required="true" onkeyup="chkPass(this.value);" /></td><td><div id="scorebar" style="float: left">No password</div></td></tr></table>
		</li>
		<li><s:password name="passwordconf" label="Password Confirmation"
			required="true" /></li>
		<li><s:textfield name="firstName" label="First Name"
			required="true" /></li>
		<li><s:textfield name="lastName" label="Last Name"
			required="true" /></li>
		<li><s:textfield name="email" label="Email" required="true" /></li>
		<li><s:textfield name="tel" label="Contact phone num" /></li>
		<li><s:textfield name="jobrole" label="Job role"/>
		</li>
		<li><s:select label="Select Organization" name="orgsel"
			headerKey="0" headerValue="-- Please Select --" listKey="dbID"
			listValue="name+', '+country" list="orgs" /></li>
	</ol>

	<p align="left"><a class="button" href="#" onclick="this.blur();document.regform.submit();"><span>Submit</span></a>  
				<a class="button" href="#" onclick="this.blur();document.regform.reset();"><span>Reset</span></a>  
			</p>

	<s:if test="hasActionErrors()">
		<s:iterator value="actionErrors">
			<span class="errorMessage"><s:property escape="false" /> </span>
		</s:iterator>
	</s:if></fieldset>
</s:form>

<%@ include file="footer.jsp"%>
