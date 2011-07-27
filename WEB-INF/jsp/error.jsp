<%@ page import="gr.ntua.ivml.athena.db.DB" %>
<%@ page isErrorPage="true"%>

<%
 // on error maybe the session is screwed up, better get a new one
 DB.closeSession();
 DB.getSession(); 
%>   
<%@ include file="top.jsp" %>  

 <table width="100%" border="0" cellspacing="0" cellpadding="0" align=center>
        <tr> 
          <td> 
            <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">

              <tr> 
                <td width="90%" height="1"></td>
              </tr>
            </table>
          </td>
        </tr>
        <tr> 
          <td height="400" valign="top"><table width="100%" border="0" cellspacing="2" cellpadding="4" align="center">
   
    <tr> 
          <td height=10 class="digital"> 
          <%String uri = (String) request.getAttribute("javax.servlet.error.request_uri");

          if (uri == null) {
           uri = request.getRequestURI(); 
          }

          out.println("Error accessing " + uri + "<BR><BR>"); 
          %>
          </td>
        </tr>
  <tr> 
    <td class="tstyle">Sorry, an error occurred processing your 
      request. Please go back and try again. <BR>Error Details:<%=exception.getMessage()%>
    </td>
  </tr>
</table></td>
        </tr>
      </table>

    
<%@ include file="footer.jsp" %>  





