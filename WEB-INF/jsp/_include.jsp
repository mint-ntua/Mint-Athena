<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="gr.ntua.ivml.athena.persistent.User" %>
<%@ page import="gr.ntua.ivml.athena.db.DB" %>

<%! public final Logger log = Logger.getLogger(this.getClass());%>

<%
log.debug( "Output rendered" );

User user=(User) request.getSession().getAttribute("user");
if( user != null ) {
	user = DB.getUserDAO().findById(user.getDbID(), false );
}
%>