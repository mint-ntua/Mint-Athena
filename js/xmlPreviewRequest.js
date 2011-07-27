var xmlPreviewPanel = new YAHOO.widget.Panel("xmlpreview",
	{ width: "800px",
	  height: "520px",
	  fixedcenter: true,
	  constraintoviewport: true,
	  close: true,
	  draggable: false,
	  zindex: 4,
	  modal: true,
	  visible: false
	}
);
xmlPreviewPanel.setHeader("XML Preview");
xmlPreviewPanel.setBody("<center>Loading...<br/><img src=\"images/rel_interstitial_loading.gif\"/></center>");
var nodeId=0;
var uploadId=0;

function ajaxXmlPreview(upload, node)
{   nodeId=node;
    uploadId=upload;
    xmlPreviewPanel.setHeader("XML Preview");
   xmlPreviewPanel.setBody("<center>Loading...<br/><img src=\"images/rel_interstitial_loading.gif\"/></center>");
    xmlPreviewPanel.show();
    YAHOO.util.Connect.asyncRequest('POST', 'XMLPreview.action',
        {
            success: function(o) {
                xmlPreviewPanel.setBody(o.responseText);
                $('#XMLPreview_selMapping').sSelect({ddMaxHeight: '300px'});
            	
                dp.SyntaxHighlighter.HighlightAll('code');
                
                
                var tabs = new YAHOO.widget.TabView("previewTabs");
            },
            
            failure: function(o) {
                alert("preview input for (uploadId: " + uploadId + ", nodeId: " + nodeId + ") failed");
            }
        }, "uploadId=" + uploadId + "&nodeId=" + nodeId);
}

function ajaxXmlTransform(selMapping)
{   
	//alert("upid:"+uploadId+",nodeid:"+nodeId+",selmap:"+selMapping);
	xmlPreviewPanel.setHeader("XML Preview based on Mappings"); 
    xmlPreviewPanel.setBody("<center>Loading...<br/><img src=\"images/rel_interstitial_loading.gif\"/></center>");
    xmlPreviewPanel.show();
    YAHOO.util.Connect.asyncRequest('POST', 'XMLPreview.action',
        {
            success: function(o) {
                xmlPreviewPanel.setBody(o.responseText);
                $('#XMLPreview_selMapping').sSelect({ddMaxHeight: '300px'});
            	
                dp.SyntaxHighlighter.HighlightAll('code');
                
                columns = [{key:"Missing XPath",label:"Missing XPaths",sortable:false,width: "300px"}];
               	
               	source = new YAHOO.util.DataSource(YAHOO.util.Dom.get("missingTable"));
                   source.responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE;
                   source.responseSchema = {fields: [{key:"Missing XPath"}]};
                   
                   table = new YAHOO.widget.ScrollingDataTable("missingContainer",columns, source, {caption:"Missing XPaths.",width: "790px"});   
               
                
            	columns = [{key:"Invalid XPath",label:"Invalid XPaths",sortable:false,width: "300px"}];
               	
               	source = new YAHOO.util.DataSource(YAHOO.util.Dom.get("invalidTable"));
                   source.responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE;
                   source.responseSchema = {fields: [{key:"Invalid XPath"}]};
               	table = new YAHOO.widget.ScrollingDataTable("invalidContainer",columns, source, {caption:"Invalid XPaths.",width: "790px"});
                
                var tabs = new YAHOO.widget.TabView("previewTabs");
            },
            
            failure: function(o) {
                alert("preview transform for (uploadId: " + uploadId + ", nodeId: " + nodeId + ") failed");
            }
        }, "uploadId=" + uploadId + "&nodeId=" + nodeId+"&selMapping="+selMapping);
}

function ajaxXmlInput(uploadId, nodeId)
{      xmlPreviewPanel.setHeader("XML Preview - Input");

    xmlPreviewPanel.setBody("<center>Loading...<br/><img src=\"images/rel_interstitial_loading.gif\"/></center>");
    xmlPreviewPanel.show();
    YAHOO.util.Connect.asyncRequest('POST', 'xmlPreviewInput.action',
        {
            success: function(o) {
                xmlPreviewPanel.setBody(o.responseText);
                dp.SyntaxHighlighter.HighlightAll('code');
                var tabs = new YAHOO.widget.TabView("previewTabs");
            },
            
            failure: function(o) {
                alert("preview transform for (uploadId: " + uploadId + ", nodeId: " + nodeId + ") failed");
            }
        }, "uploadId=" + uploadId + "&nodeId=" + nodeId);
}


function ajaxXmlTransformed(uploadId, nodeId)
{      xmlPreviewPanel.setHeader("XML Transformed");

    xmlPreviewPanel.setBody("<center>Loading...<br/><img src=\"images/rel_interstitial_loading.gif\"/></center>");
    xmlPreviewPanel.show();
    YAHOO.util.Connect.asyncRequest('POST', 'xmlPreviewFinal.action',
        {
            success: function(o) {
                xmlPreviewPanel.setBody(o.responseText);
                dp.SyntaxHighlighter.HighlightAll('code');
                var tabs = new YAHOO.widget.TabView("previewTabs");
            },
            
            failure: function(o) {
                alert("preview final transform for (uploadId: " + uploadId + ", nodeId: " + nodeId + ") failed");
            }
        }, "uploadId=" + uploadId + "&nodeId=" + nodeId);
}