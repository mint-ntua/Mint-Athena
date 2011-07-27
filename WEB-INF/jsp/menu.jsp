


<body class="yui-skin-sam">

 <div id="header"> &nbsp;</div>
<div id="menu" class="ddcolortabs">
<ul>
<% if( user == null ) {  %>

<li><a href="Login_input.action"><span>Login</span></a></li>
<li><a href="Register_input.action"><span>Register</span></a></li>
<%}%>
<% if( user != null ) {  %>
<li><a href="Home"><span>Home</span></a></li>
<li><a href="Profile"><span>My Profile</span></a></li>
<% if( user.hasRight(User.ADMIN)) {  %>
<li><a href="Management_input.action"><span>Administration</span></a></li>
<%} %>
<% if( user.hasRight(User.ADMIN) || user.hasRight(User.MODIFY_DATA) || user.hasRight(User.PUBLISH) ) {  %>
<li><a href="Import_input.action"><span>Import</span></a></li>
<%}if( user.hasRight(User.ADMIN) || user.hasRight(User.VIEW_DATA) || user.hasRight(User.PUBLISH) || user.hasRight(User.MODIFY_DATA)) {  %> 
<li><a href="ImportSummary"><span>Overview</span></a></li>

<%} %> 
<li><a href="ReportSummary"><span>Data Report</span></a></li>


<li><a href="Logout.action"><span>Logout</span></a></li>
<%}%>
</ul>
</div>
<div class="ddcolortabsline">&nbsp;</div>

