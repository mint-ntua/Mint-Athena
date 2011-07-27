<%@ include file="_include.jsp"%>

<%@page import="java.io.*"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>

<%@page import="gr.ntua.ivml.athena.db.*"%>
<%@page import="gr.ntua.ivml.athena.persistent.*"%>
<%@page import="gr.ntua.ivml.athena.mapping.*"%>
<%@page import="gr.ntua.ivml.athena.xml.transform.*"%>

<div class="yui-skin-sam" style="width: 100%; height: 100%">

<s:if test="error.length()>0">
<div><s:property value="message"/></div><br/>
	<div="previewTabs" class="yui-navset">
	    <ul class="yui-nav"> 
	        <li class="selected"><a href="#tab1"><em>Error</em></a></li> 
	    </ul>
	   	<div class="yui-content"> 
	        <div><p><div style="width: 100%; height: 400px; overflow-x: auto; overflow-y: auto">
	        	<textarea class='xml' style='width: 100%' rows='25' columns='50' readonly><s:property value="htmlError"/></textarea>
	        </div></p></div>
	    </div>              
	</div>
<s:else>
	<div><s:property value="message"/></div><br/>
	<div id="previewTabs" class="yui-navset"> 
	    <ul class="yui-nav"> 
	        <li class="selected"><a href="#tab1"><em>Input XML</em></a></li> 
	        <li><a href="#tab2"><em>Generated XSL</em></a></li> 
	        <li><a href="#tab3"><em>Output XML (Lido)</em></a></li> 
	        <li><a href="#tab3"><em>Output XML (ESE)</em></a></li> 
	    </ul>             
	    <div class="yui-content"> 
	     <%if(input_xml.length()>10000){ %>
	        <div><p><div style="width: 95%; height: 385px;">
					  			<textarea  name='code' style='width: 100%; height: 375px; background: #FFFFFF;' rows='22' columns='50' readonly><%=StringEscapeUtils.escapeHtml(input_xml)%></textarea>
				
					    </div></p>
					    </div>
	        <%}else{ %>
	          <div><p><div style="width: 100%; height: 385px; overflow-x: auto; overflow-y: auto">
	        	<textarea name='code' class='xml' style='width: 100%' rows='25' columns='50' readonly><%=
	        		StringEscapeUtils.escapeHtml(input_xml)
	        	%></textarea>
	        </div></p></div>
	        <%} %>
	        <div><p><div style="width: 100%; height: 385px; overflow-x: auto; overflow-y: auto">
	        	<textarea name='code' class='xml' style='width: 100%' rows='25' columns='50' readonly><%=
	        		StringEscapeUtils.escapeHtml(xsl)
	        	%></textarea>
	        </div></p></div>
	        <div><p><div style="width: 100%; height: 385px; overflow-x: auto; overflow-y: auto">
	        	<textarea name='code' class='xml' style='width: 100%' rows='25' columns='50' readonly><%=
	        		StringEscapeUtils.escapeHtml(output_xml)
	        	%></textarea>
	        </div></p></div>
	        <div><p><div style="width: 100%; height: 385px; overflow-x: auto; overflow-y: auto">
	        	<textarea name='code' class='xml' style='width: 100%' rows='25' columns='50' readonly><%=
	        		StringEscapeUtils.escapeHtml(output_ese)
	        	%></textarea>
	        </div></p></div>
	    </div> 
	</div> 
</s:else>
</s:if>
</div>