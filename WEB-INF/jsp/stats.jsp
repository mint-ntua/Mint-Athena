<%@ include file="_include.jsp"%>
<%@ page language="java" errorPage="error.jsp"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="gr.ntua.ivml.athena.xml.Statistics" %>
<%@page import="gr.ntua.ivml.athena.persistent.DataUpload" %>
<%@page import="gr.ntua.ivml.athena.persistent.XpathHolder" %>
<%@page import="gr.ntua.ivml.athena.persistent.XmlObject" %>
<%@page import="gr.ntua.ivml.athena.db.DB" %>
<%@page import="java.util.LinkedHashMap" %>
<%@page import="java.util.HashMap" %>
<%@page import="java.util.Map" %>
<%@page import="java.io.IOException" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<title>Athena Statistics</title>
<style type="text/css">
/*margin and padding on body element
  can introduce errors in determining
  element position and are not recommended;
  we turn them off as a foundation for YUI
  CSS treatments. */
body {
	margin:0;
	padding:0;
}
#toggle {
    text-align: center;
    padding: 1em;
}
#toggle a {
    padding: 0 5px;
    border-left: 1px solid black;
}
#tRight {
    border-left: none !important;
}
</style>
<!-- Combo-handled YUI CSS files: -->
    <link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/reset-fonts-grids/reset-fonts-grids.css"/>
    <link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/resize/assets/skins/sam/resize.css"/>
    <link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/layout/assets/skins/sam/layout.css"/>
<!-- Combo-handled YUI JS files: -->
    
    <link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/fonts/fonts-min.css" />
    <link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/tabview/assets/skins/sam/tabview.css" />
    <link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/datatable/assets/skins/sam/datatable.css" />
    <link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/container/assets/skins/sam/container.css" />
    <script type="text/javascript" src="js/mapping/lib/yui/utilities/utilities.js"></script>
    <script type="text/javascript" src="js/mapping/lib/yui/container/container-min.js"></script>
    <script type="text/javascript" src="js/mapping/lib/yui/calendar/calendar-min.js"></script>
    <script type="text/javascript" src="js/mapping/lib/yui/resize/resize-min.js"></script>
    <script type="text/javascript" src="js/mapping/lib/yui/editor/simpleeditor-min.js"></script>
    <script type="text/javascript" src="js/mapping/lib/yui/layout/layout-min.js"></script>
    <script type="text/javascript" src="js/mapping/lib/yui/yahoo-dom-event/yahoo-dom-event.js"></script>
    <script type="text/javascript" src="js/mapping/lib/yui/element/element-min.js"></script>
    <script type="text/javascript" src="js/mapping/lib/yui/tabview/tabview-min.js"></script>
    <script type="text/javascript" src="js/mapping/lib/yui/datasource/datasource-min.js"></script>
    <script type="text/javascript" src="js/mapping/lib/yui/datatable/datatable-min.js"></script>
    <script type="text/javascript" src="js/mapping/lib/yui/connection/connection-min.js"></script>
    <script type="text/javascript" src="js/mapping/lib/yui/animation/animation-min.js"></script>
    <script type="text/javascript" src="js/mapping/lib/yui/dragdrop/dragdrop-min.js"></script>
    <script type="text/javascript" src="js/mapping/lib/yui/container/container-min.js"></script>
    <script type="text/javascript" src="js/statistics/ajax.js"></script>
    <script type="text/javascript" src="js/statistics/prototype.js"></script>
    <script type="text/javascript" src="js/statistics/ProtoChart.js"></script>
    <script type="text/javascript" src="js/statistics/excanvas-compressed.js"></script>
    <script type="text/javascript" src="js/statistics/excanvas.js"></script>
    <script type="text/javascript" src="js/statistics/data.js"></script>
    
    <link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/reset-fonts-grids/reset-fonts-grids.css" />
	<link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/resize/assets/skins/sam/resize.css" />
	<link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/layout/assets/skins/sam/layout.css" />
	<link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/button/assets/skins/sam/button.css" />
	<link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/container/assets/skins/sam/container-skin.css" />
	<link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/container/assets/skins/sam/container.css" />
	<link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/assets/skins/sam/container.css" />
	<link rel="stylesheet" type="text/css" href="js/mapping/lib/yui/treeview/assets/skins/sam/treeview.css" />
    <link rel="stylesheet" type="text/css" href="css/mapping/editor.css" />
	<link rel="stylesheet" type="text/css" href="css/statistics.css" />


<%!

	/**
	 * Recursively print stats for every xpath underneath the given one.
	*/
     public void xpathTableRecurse(XpathHolder xp,
			Map<Long, Object[]> stats, JspWriter out, int level ) throws IOException {

	// ignore text() nodes, they are handled from parent
		if (xp.isTextNode())
			return;
	// output uri, name, len, uniqu, freq
		out.println("<tr>");
		String name = xp.getNameWithPrefix(true);
		
		out.println("<td> " );
		// indent according to place in tree
		for( int i=0; i<level*2; i++ )
			{out.print("&nbsp;" );
			 
			}
		if(xp.getChildren().size()==1 && (xp.getChildren().get(0).isTextNode())){
			out.print("&nbsp;&nbsp;<img src='images/leaf.gif'/>&nbsp;" );
		}	
		else if( xp.getChildren().size()>0 && name.length()>0){
		 out.print("&nbsp;<img src='css/images/foldertrans.png'/>&nbsp;" );}
		
		else if(xp.getChildren().size()==0 || xp.isAttributeNode() || xp.getTextNode()!=null){out.print("&nbsp;&nbsp;<img src='images/leaf.gif'/>&nbsp;");}
		out.println( name + "</td>");
		if (xp.isAttributeNode()) {
			// attribute stuff
			Object[] nums = stats.get(xp.getDbID());
			Float avg = (Float) nums[0];
			Long count = (Long) nums[1];
			out.println("<td>" + count.longValue() + "</td> <td> "
					+ xp.getCount() + "</td> <td> " + avg.floatValue() + "</td>");
		} else {
			XpathHolder text = xp.getTextNode();
			if (text == null) {
				// parent stuff without stats
				// empty cells
				out.println( "<td> </td> <td> </td> <td> </td>");
			} else {
				// text node stuff
				Object[] nums = stats.get(text.getDbID());
				Float avg = (Float) nums[0];
				Long count = (Long) nums[1];
				out.println("<td>" + count.longValue() + "</td> <td> "
						+ xp.getCount() + "</td> <td>" + avg.floatValue() + "</td>");
			}
		}
		// some extra data
		out.print( "<td>" + xp.getDbID() + "</td>" );
		out.print( "<td>" + (xp.getParent()==null?0:xp.getParent().getDbID()) + "</td>" );
		out.println("</tr>");
		for (XpathHolder child : xp.getChildren())
			xpathTableRecurse(child, stats, out, level+1);
	}%>
	
	
	   <%
        	try {
        		String uploadId = request.getParameter("uploadId");
        		DataUpload dataUpload = DB.getDataUploadDAO().findById(
        				Long.parseLong(uploadId), false);
        		Statistics stats = dataUpload.getXmlObject().getStats();
        		LinkedHashMap<String, String> res = stats.getNameSpaces();
        %>
</head>
<body>
		<div id="stats_container" class="yui-skin-sam" style="padding: 5px">

        <form name="stats" method="post" action="">
            <%
            	out.print("<input type=\"hidden\" id=\"count\" name=\"count\" value=\""
            						+ res.keySet().size() + "\"/>\n");
            		int itemNumber = 0;
            		Iterator<String> it = res.keySet().iterator();
            		while (it.hasNext()) {
            			ArrayList<String> elems = (ArrayList) stats.getElements(it.next());
            			DB.commit();
            			itemNumber += elems.size();
            			Iterator<String> elIt = elems.iterator();
            			while (elIt.hasNext()) {
            				itemNumber += stats.getAttributes(elIt.next()).size();
            			}
            		}
            		out.print("<input type=\"hidden\" id=\"sparks\" name=\"sparks\" value=\""
            						+ itemNumber + "\"/>\n");
            		out.print("<input type=\"hidden\" id=\"uploadId\" name=\"uploadId\" value=\""
            						+ uploadId + "\"/>\n");
            %>
            <input type="hidden" id="fieldName" name="fieldName" value=""/>
        </form>
		
        <div id="top">

        </div>

        <div id="center">
           <div id="wrap">

           </div>
        </div>
        
        <div id="left">
            <div id="namespaces">
                <table id="pref">
                    <thead>
                        <tr>
                            <th>prefix</th>
                            <th>namespace</th>
                        </tr>
                    </thead>
                    <tbody>
                    		<s:iterator id="n" value="namespaces">
                    		<tr>
                    			<td><s:property value="prefix"/></td>
                    			<td><a href="<s:property value="uri"/>"> <s:property value="uri"/></a></td>
                    		</tr>
                        </s:iterator>
                    </tbody>
                </table>
            </div>
            <div id="legend">
           <!-- <table style="border-bottom-style: ridge; border-top-style: ridge;padding: 3px 3px 3px 3px;">
            <tr><big><b>Color Codes</b></big></tr>
            	
            		<tr><td><li style="list-style-type: circle;"><font color="#FF0000">Empty - </font>everytime the path was found, it contained no values.</li></td></tr>
            		<tr><td><li style="list-style-type: circle;"><font color="#00FF00">Identifier - </font>every time the path was found, it contained a different value.</li></td></tr>
            	
            </table>-->
            </div>
            
        </div>

        <div id="demo" class="yui-navset">
                 <div class="yui-content" id="tablesContent">
                	<div id="tab_1">
                		<table id="table_1" >
						<% XmlObject xo = (XmlObject) request.getAttribute( "xmlObject" );
						   xpathTableRecurse( xo.getRoot(), xo.getAllStats(), out, 0 ); 
						%>
						</table>
                </div>
            </div>
	</div>
            <div id="panel">
                <div class="hd" id="headerTitle">Element Value Statistics</div>
                <div id ="rawData" class="bd" style="overflow:auto">
                    <table>
                     <tr>
                    <td><div id="matrix" style="width:550px;height:300px"> </div></td>
                    <td></td>
                    </tr>
                    </table>
                </div>
                <div class="ft">Athena</div>
            </div>
       
	       <script>
            //var tabView = new YAHOO.widget.TabView('demo');

            var myColumnDefs = [{key:"prefix",label:"prefix",sortable:false},{key:"namespace",label:"namespace",sortable:false}];
            var myDataSource = new YAHOO.util.DataSource(YAHOO.util.Dom.get("pref"));
            myDataSource.responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE;
            myDataSource.responseSchema = {fields: [{key:"prefix"},{key:"namespace", formatter:YAHOO.widget.DataTable.formatLink}]};
            var myDataTable = new YAHOO.widget.DataTable("namespaces", myColumnDefs, myDataSource,{caption:"Available Namespaces in current upload"});


                            var columnDefs = [{key:"element",label:"element",sortable:false,sortOptions:{defaultDir:YAHOO.widget.DataTable.CLASS_DESC},resizeable:true},
                                    {key:"frequency",label:"frequency",sortable:false,sortOptions:{defaultDir:YAHOO.widget.DataTable.CLASS_DESC},resizeable:true},
                                    {key:"unique",label:"unique",sortable:false,sortOptions:{defaultDir:YAHOO.widget.DataTable.CLASS_DESC},resizeable:true},
                                    {key:"length",label:"length",sortable:false,sortOptions:{defaultDir:YAHOO.widget.DataTable.CLASS_DESC},resizeable:true}];
                var dataSource = new YAHOO.util.DataSource(YAHOO.util.Dom.get("table_1"));
                dataSource.responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE;
                            dataSource.responseSchema = {fields: [{key:"element"},
                             {key:"unique"},
                             {key:"frequency"},
                             {key:"length"},
                             {key:"pathId" },
                             {key:"parentPathId" }]};

                var dataTable = new YAHOO.widget.ScrollingDataTable("tab_1", columnDefs, dataSource, {height:"31em"});

                String.prototype.trim = function() {
                    return this.replace(/^\s+|\s+$/g,"");
                }

                
                function showData(args){
                   var record = dataTable.getRecord( args.target );
                  temp=record.getData("frequency");
                  if(temp.trim().length==0) return;
    				document.getElementById("headerTitle").innerHTML = "Statistics for the Element " + record.getData( "element" ).replace( /&nbsp;/g, "" );
                    var callback = function( o ) {
                      document.getElementById("matrix").innerHTML = o.responseText;
                   		var columnDefs = [{key:"Value",label:"Value",sortable:false},{key:"Frequency",label:"Frequency",sortable:false}];
                    		
                   		var dataSource = new YAHOO.util.DataSource(YAHOO.util.Dom.get("ajaxTable"));
                   	            dataSource.responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE;
                   	            dataSource.responseSchema = {fields: [{key:"Value"},{key:"Frequency"}]};
                 	    var dataTable = new YAHOO.widget.ScrollingDataTable("ajaxTableContainer", columnDefs, dataSource,{caption:"Available value distribution for the current element.", height:"20em"});
                 	   oPanel.show();    
                    };
                    ajaxSimple( "elementStats", "pathId="+record.getData( "pathId" ), callback );
                    
                }
                
                dataTable.subscribe("rowMouseoverEvent", dataTable.onEventHighlightRow);
                dataTable.subscribe("rowMouseoutEvent", dataTable.onEventUnhighlightRow);
                dataTable.subscribe("rowClickEvent", this.dataTable.onEventSelectRow);
                dataTable.subscribe("rowDblclickEvent", showData);
			var achildren = null;
			
            

            var layout = new YAHOO.widget.Layout("stats_container", {units:[{position: 'left', header: 'Namespaces', width: 400, height:800, resize: false, body: 'left', gutter: '0px', collapse: true, close: true, collapseSize: 50, scroll: true, animate: true },
                                                 { position: 'center', header: 'Element Statistics', gutter: '0px',body: 'center', width: 900, height:800, resize: true, scroll: true }]});
            
            layout.on('render', function() {
                var tabView = new YAHOO.widget.TabView('demo');
                var l = document.getElementById("wrap");
                tabView.appendTo(l);
                oPanel = new YAHOO.widget.Panel("panel",{ width:"550px", height:"380px",visible:false, constraintoviewport:true, modal:true, fixedcenter:true, effect:{effect:YAHOO.widget.ContainerEffect.FADE,duration:0.25} });
                oPanel.render();
            }, layout, true);
            layout.render();
        </script>
        <form name="variables" action="" method="post">
            <input type="hidden" name="q1" value=""/>
        </form>
</div>
<%
	} catch (Throwable t) {
		t.printStackTrace();
	}
%>
<%@ include file="footer.jsp"%>
