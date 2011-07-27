<%@ page isErrorPage="true"%>
   
<%@ include file="top.jsp" %>  

  <h3>Error Message</h3>
    <s:actionerror/>
    <p>
      <s:if test="exception!=null"><s:property value="%{exception.message}"/></s:if>
    </p>
    <hr/>
    
<%@ include file="footer.jsp" %>  





