<%@ include file="_include.jsp"%>
<%@page import="java.io.*"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="gr.ntua.ivml.athena.db.*"%>
<%@page import="gr.ntua.ivml.athena.persistent.*"%>
<%@page import="gr.ntua.ivml.athena.mapping.*"%>
<%@page import="gr.ntua.ivml.athena.xml.transform.*"%>
<jsp:useBean id="fullDoc" class="gr.ntua.ivml.athena.xml.FullBean"/>
<div class="yui-skin-sam" style="width: 100%; height: 100%">
<%
String uploadId = request.getParameter("uploadId");
String nodeId = request.getParameter("nodeId");
if(uploadId == null) {
%><h1>Error: Missing uploadId parameter</h1><%
} else if(nodeId == null) {
%><h1>Error: Missing nodeId parameter</h1><%
} else {
	String error = null;
	StringWriter xmlWriter = new StringWriter();
	String input_xml = "";
	String xsl = "";
	String ese = "";
	String output_xml = "";
	String output_ese = "";
	String mess="";
	boolean truncated=false;
	DataUpload du = DB.getDataUploadDAO().findById(Long.parseLong(uploadId), false);
	boolean isLido=du.isLido() || du.isLido10();
	java.util.List trlist=DB.getTransformationDAO().findByUpload(du);
	if(trlist.size()==0){
		error = "Transformation no longer exists";			
	}
	else{
		Transformation tr=(Transformation)trlist.get(trlist.size()-1);
		Mapping mp=tr.getMapping();
		if(!isLido)
		 mess="Transformed using Mappings " +mp.getName();
		if(mess.length()>0){%>
			<script>  
			xmlPreviewPanel.setHeader("XML Transformed"+<%=mess%>);</script>
		<%}
		String mappings=tr.getJsonMapping();
	

	if(mappings == null && (!isLido)) {
		error = "The mappings used for this transformation are no longer available.";
	} else {
		XMLNode node = DB.getXMLNodeDAO().findById(Long.parseLong(nodeId), false);
		if(node.getSize()>20000){
			truncated=true;
		}
		//XMLNode treeNode= DB.getXMLNodeDAO().getDOMTree( node );
		//treeNode.toXml(new PrintWriter(xmlWriter));
        node.toXmlWrapped(new PrintWriter(xmlWriter));
        
		input_xml = xmlWriter.toString();	
		input_xml = input_xml.replaceFirst("xmlns=\"[^\"]*\"", "");	
		input_xml = XMLFormatter.format(input_xml); 
		
		XSLTGenerator xslt = new XSLTGenerator();
		XSLTransform t = new XSLTransform();
		if(!isLido){
				xslt.setItemLevel(du.getItemXpath().getXpathWithPrefix(true));
				xslt.setTemplateMatch(node.getXpathHolder().getXpathWithPrefix(true));
				xslt.setImportNamespaces(du.getItemXpath().getNamespaces(true));
				xsl = XMLFormatter.format(xslt.generateFromString(mappings));
				
		}
		
		String eseFilePath = "";
		if(du.isLido10()) {
			eseFilePath = this.getServletContext().getRealPath(gr.ntua.ivml.athena.util.Config.get( "lido1.0_to_ese_xsl"));
		} else {
			eseFilePath = this.getServletContext().getRealPath(gr.ntua.ivml.athena.util.Config.get( "lido_to_ese_xsl"));
		}		
		File eseFile = new File(eseFilePath);
		log.debug("Using XSL: " + eseFilePath);
		StringBuilder ese_contents = new StringBuilder();
   		try {
      		BufferedReader input =  new BufferedReader(new FileReader(eseFile));
      		try {
	    	    String line = null; //not declared within while loop
	    	    while (( line = input.readLine()) != null){	    	
					ese_contents.append(line);
					ese_contents.append(System.getProperty("line.separator"));
        		}
	   		} finally {
        		input.close();
      		}
    	}
    	catch (IOException ex){
      		ex.printStackTrace();
		}
    	
    	ese = ese_contents.toString();
					
		try {
			if(!isLido){
				output_xml = t.transform(input_xml, xsl);
				output_xml = XMLFormatter.format(output_xml);
			}else{
			    output_xml=input_xml;	
			}
			if(ese != null && ese.length() > 0) {
				output_ese = t.transform(output_xml, ese);
				fullDoc=gr.ntua.ivml.athena.xml.ESEToFullBean.getFullBean(output_ese);
			
				output_ese = XMLFormatter.format(output_ese);
				}
		} catch(Exception e) {
			//Writer result = new StringWriter();
    		//PrintWriter printWriter = new PrintWriter(result);
    		//e.printStackTrace(printWriter);
//			output_xml = result.toString();
		}
	}
	}
	if(error != null) {
%>
<div><%=mess %></div><br/>
	<div="previewTabs" class="yui-navset">
	    <ul class="yui-nav"> 
	        <li class="selected"><a href="#tab1"><em>Error</em></a></li> 
	    </ul>
	   	<div class="yui-content"> 
	        <div><p><div style="width: 100%; height: 400px; overflow-x: auto; overflow-y: auto">
	        	<textarea class='xml' style='width: 100%' rows='25' columns='50' readonly><%=
	        		StringEscapeUtils.escapeHtml(error)
	        	%></textarea>
	        </div></p></div>
	    </div>              
	</div>
<%
	} else {
%>
	<div><%=mess %></div><br/>
	
	 <%if(truncated==true){%>
        <div style="font-color:red;">This item is too large to display and has been truncated. This could result in mappings and transformation not showing fully on this preview.
        </div>
       <%}%>  
	<div id="previewTabs" class="yui-navset"> 
	    <ul class="yui-nav"> 
	        <li class="selected"><a href="#tab1"><em>Input XML <%if(isLido){%>LIDO<%} %></em></a></li> 
	        <%if(!isLido){ %>
	        <li><a href="#tab2"><em>Generated XSL</em></a></li> 
	        <li><a href="#tab3"><em>Output XML (Lido)</em></a></li>
	        <%} %> 
	        <li><a href="#tab3"><em>Output XML (ESE)</em></a></li> 
	        <li><a href="#tab4"><em>Europeana</em></a></li> 
	    </ul>             
	    <div class="yui-content"> 
	    <%if(input_xml.length()>10000){ %>
	    	<div><p><div style="width: 95%; height: 350px;">
					  			<textarea  name='code' style='width: 100%; height: 340px; background: #FFFFFF;' rows='22' columns='50' readonly><%=StringEscapeUtils.escapeHtml(input_xml)%></textarea>
				
					    </div></p>
					    </div>
	    
	    <%}else{ %>
	      <div><p><div style="width: 100%; height: 360px; overflow-x: auto; overflow-y: auto">
	        	<textarea name='code' class='xml' style='width: 100%' rows='25' columns='50' readonly><%=
	        		StringEscapeUtils.escapeHtml(input_xml)
	        	%></textarea>
	        </div></p></div>
	    
	    <%} %>
	     <%if(!isLido){ %>
	     <%if(xsl.length()>10000){ %>
	        <div><p><div style="width: 100%; height: 360px; overflow-x: auto; overflow-y: auto">
	        	<textarea name='code' style='width: 100%; height: 340px; background: #FFFFFF;' rows='25' columns='50' readonly><%=
	        		StringEscapeUtils.escapeHtml(xsl)
	        	%></textarea>
	        </div></p></div>
	       <%}else{ %>
	        <div><p><div style="width: 100%; height: 360px; overflow-x: auto; overflow-y: auto">
	        	<textarea name='code' class='xml' style='width: 100%' rows='25' columns='50' readonly><%=
	        		StringEscapeUtils.escapeHtml(xsl)
	        	%></textarea>
	        </div></p></div>
	       <%} %>
	        <%if(output_xml.length()>10000){ %>
	        <div><p><div style="width: 100%; height: 360px; overflow-x: auto; overflow-y: auto">
	        	<textarea name='code' style='width: 100%; height: 340px; background: #FFFFFF;' rows='25' columns='50' readonly><%=
	        		StringEscapeUtils.escapeHtml(output_xml)
	        	%></textarea>
	        </div></p></div>
	        <%}else{ %>
	         <div><p><div style="width: 100%; height: 360px; overflow-x: auto; overflow-y: auto">
	        	<textarea name='code' class='xml' style='width: 100%' rows='25' columns='50' readonly><%=
	        		StringEscapeUtils.escapeHtml(output_xml)
	        	%></textarea>
	        </div></p></div>
	        <%}
	        }//islIDO%>
	        <div><p><div style="width: 100%; height: 360px; overflow-x: auto; overflow-y: auto">
	        	<textarea name='code' class='xml' style='width: 100%' rows='25' columns='50' readonly><%=
	        		StringEscapeUtils.escapeHtml(output_ese)
	        	%></textarea>
	        </div></p></div>
	         <div><p><div style="width: 100%; height: 360px; overflow-x: auto; overflow-y: auto; background-color:#FFFFFF;">
	               <%@ include file="eseview.jsp"%>
	    
   	        </div></p></div>
	    </div> 
	</div> 
	
</div>

<%
	}
}
%>
