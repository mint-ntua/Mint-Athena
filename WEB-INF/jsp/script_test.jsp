<%@ include file="top.jsp"%>
<script type="text/javascript">
ddtabmenu.definemenu("menu", 1) //tab 1 selected

</script>
<s:form action="Script" cssClass="athform" theme="mytheme">
<div>
<h1>
<p>Script</p>
</h1>
<s:select label="Scripts"
       name="scriptlet"
       headerKey="/" headerValue="Select scriptlet"
       list="lib"
       value="%{'/'}"
       onchange="$('form').submit()"
       
/>
<br/>

	<s:textarea name="script" rows="10" cols="60" label="Groovy script"  />

	<p align="left"><input type="submit" value="submit"
		class="inputButton" /><input type="reset" value="reset"
		class="inputButton" /></p>

</div>
</s:form>

The script output was: <br/>
<pre>
<s:property value="stdOut" />
</pre>

<s:if test="result!=null" >
The script returned: </br>
<pre>
<s:property value="result" />
</pre>
</s:if>

<%@ include file="footer.jsp"%>
