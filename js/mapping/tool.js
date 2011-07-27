function hideAllPanels() {
    for(var i in elementPanels) {
        elementPanels[i].hide();
    }
    
    conditionPanel.hide();
}

/*function to reset the rest of the target buttons*/
function resetRest(btn){
	buttonGroup1.set("checkedButton", null);
	buttonGroup2.set("checkedButton", null);
	buttonGroup3.set("checkedButton", null);
    var arr1=buttonGroup1.getButtons();

	for(var i=0; i<arr1.length; i++) {
         bt=arr1[i];
         bt.set("checked", false);
	}     

	
    var arr2=buttonGroup2.getButtons();
    for(var i=0; i<arr2.length; i++) {
         bt=arr2[i];
         bt.set("checked", false);
      }	
    var arr3=buttonGroup3.getButtons();
    for(var i=0; i<arr3.length; i++) {
         bt=arr3[i];
         bt.set("checked", false);
      }	  
    btn.set("checked", true);  
  }

function showGroupElements(group) {
	hideAllPanels();
	
	/*
    for(var i in targetDefinition.groups) {
        var g = targetDefinition.groups[i];
        elementPanels["panel_" + g.contents.id].hide();
    }
    */
    
	selectedPanel = elementPanels["panel_" + group.contents.id];
    selectedPanel.show();
    setPanelListeners(group.contents);
}

function showTemplateElements() {
	hideAllPanels();
	
	/*
    for(var i in targetDefinition.groups) {
        var g = targetDefinition.groups[i];
        elementPanels["panel_" + g.contents.id].hide();
    }
    */
    
	selectedPanel = elementPanels["panel_" + targetDefinition.template.id];
	selectedPanel.show();
    setTemplatePanelListeners(targetDefinition.template);
}

function togglePanel(panel) {
    var p = elementPanels["panel_" + panel];
    if(p.cfg.config.visible.value) {
        p.hide();
    } else {
        p.show();
    }
}

function showAnnotation(id) {
//function showAnnotation(item, annotation) {
	ajaxGetDocumentation(id);
//    annotationsPanel.setHeader(item);
//    annotationsPanel.setBody(annotation);
//    annotationsPanel.show();
}

function getDocumentationResponse(reponse)
{
	var d = "no documentation";
	if(response != undefined && response.documentation != undefined) { 
		d = response.documentation.replace(/^\n/g, "").replace(/\n/g, "<br>").trim();
	}
    annotationsPanel.setHeader(response.title);
    annotationsPanel.setBody(d);
    annotationsPanel.show();
}

function setXPathMapping(sourceEl, targetEl) {
    var source = sourceEl.id;
    var target = targetEl.id;
    var tokens = target.split(".");
    
    var id = tokens[0];
    var index = tokens[1];
    
    if(index == "condition") {
    		ajaxSetConditionXPath(id, source);
    } else if(index == "structural"){
    		if(confirm("This mapping will repeat this wrapping element for each occurence of the selected tree node (XPath). Use this if each occurence of the selected tree node (XPath) refers to a seperate entity instead of an alternative represantation. Proceed ?")) {
    			ajaxSetXPathMapping(source, id, -1);
    		}
    } else if(index == "default") {
	    ajaxSetXPathMapping(source, id, -1);
    } else {
	    ajaxSetXPathMapping(source, id, index);
    }
}

function setXPathMappingResponse(response) {
    updateMappingsHighlight(response);
}

function removeMappings(target, index) {
    ajaxRemoveMappings(target, index);
}

function removeMappingsResponse(response) {
    updateMappingsHighlight(response);
}

function additionalMappings(target, index) {
    ajaxAdditionalMappings(target, index);
}

function additionalMappingsResponse(response) {
    updateMappingsHighlight(response);
}

function updateMappingsHighlight(response) {
	updateMappings(response);
    ajaxGetHighlightedElements();
}

function updateMappings(response) {
    var id = response.id;
    var name = response.name;
    var panelindex = "";
    if(name.indexOf("@") === 0) {
    		panelindex = "panel_attributes_" + id;
    } else {
		panelindex = "panel_" + id;
	    elementPanels[panelindex].attachedItem = response;
    }    
    
    var element = YAHOO.util.Dom.get(id);
    if(element == null) alert("updateMappings for null element: " + id);
//    var mapping = element.getElementsByClassName("mapping")[0]; fix for ie below
    var elems=getElementsByClassName("mapping","",element);
    var mapping=elems[0];
    
    mapping.innerHTML = generateMappingsTable(response);
    setPanelListeners(response);
    
    //enableConstantValueEditingForClass("no_mapping");
    //enableConstantValueEditingForClass("constant_mapping");
    //enableConstantValueEditingForClass("condition_mapping");
    //enableConstantValueEditingForClass("empty_mapping");
    //enableConstnatValueEditingForClass("constantValue");

    //var noMapping = mapping.getElementsByClassName("no_mapping"); fix for ie below
    var noMapping=getElementsByClassName("no_mapping","",mapping);

    for(var nm in noMapping) {
    	enableConstantValueEditingForElement(noMapping[nm]);
    }
    
   // var constantMapping = mapping.getElementsByClassName("constant_mapping"); fix for ie below
    var constantMapping=getElementsByClassName("constant_mapping","",mapping);

    for(var nm in constantMapping) {
    	enableConstantValueEditingForElement(constantMapping[nm]);
    }
        
    //var emptyMapping = mapping.getElementsByClassName("empty_mapping"); fix for ie below
    var emptyMapping=getElementsByClassName("empty_mapping","",mapping);

    for(var nm in emptyMapping) {
    	enableConstantValueEditingForElement(emptyMapping[nm]);
    }    
}

function updateMappingsRecursive(response) {
	updateMappings(response);
	
	if(response.attributes != undefined) {
		for(var i in response.attributes) {
			updateMappingsRecursive(response.attributes[i]);
		}
	}

	if(response.children != undefined) {
		for(var i in response.children) {
			updateMappingsRecursive(response.children[i]);
		}
	}
}


function showTooltip(nodeId) {
	var element = YAHOO.util.Dom.get(nodeId);
	var xpath = element.getAttribute('xpath');
	
	tooltipPanel.setBody('<center>Loading help for <b>' + xpath + '</b>...<br/><img src="js/mapping/lib/yui/carousel/assets/ajax-loader.gif" /></center>');
	tooltipPanel.show();
	ajaxGetTooltip(nodeId);
}

function getTooltipResponse(response) {
    tooltipPanel.setBody(response.tooltip);
   	var columns;
	var source;
	var table;
   	
   	columns = [
            {key:"Value",label:"Value",sortable:false,width: "300px"},
            {key:"Frequency",label:"Frequency",sortable:false, width: "150px"}
            ];
	
	source = new YAHOO.util.DataSource(YAHOO.util.Dom.get("valuesTable"));
    source.responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE;
    source.responseSchema = {
                    fields: [{key:"Value"},
                             {key:"Frequency"}
                    ]};
    
    table = new YAHOO.widget.ScrollingDataTable("valuesTableContainer",
    	columns, source, {
    		caption:"Available value distribution for current element.",
    		width: "450px",
    		height:"26em"
    	});

   	columns = [
               {key:"Mapping",label:"Mapping",sortable:false, width: "150px"}
               ];
   	
   	source = new YAHOO.util.DataSource(YAHOO.util.Dom.get("mappingInfoTable"));
       source.responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE;
       source.responseSchema = {
                       fields: [{key:"Mapping"}
                       ]};
       
       table = new YAHOO.widget.ScrollingDataTable("mappingInfoTableContainer",
       	columns, source, {
       		caption:"Mappings for this element.",
       		width: "450px",
       		height:"28em"
       	});
      
       
    var tabs = new YAHOO.widget.TabView("tooltipTabs");
}

function duplicateNode(itemId) {
	if(confirm("You are about to duplicate this item. Proceed ?")) {
		ajaxDuplicateNode(itemId);
	}
}

function duplicateNodeResponse(itemId, response) {
/*
	var p = elementPanels["panel_" + response.parent];
	//var d = document.createElement("p");
	//it does not work for ie, replaced with div
	var d = document.createElement("div");
*/
	
/*
	var html = generateComplexChildContent(response.duplicate);
	d.innerHTML = html;
	p.appendToBody(d);
    generateItemPanel(response.duplicate, response.duplicate.id + "_container");
    setPanelListeners(response.duplicate);
    updateMappingsRecursive(response.duplicate);
*/

	var p = YAHOO.util.Dom.get(response.original + "_container").parentNode;
	var html = generateComplexChildContent(response.duplicate);
	var d = document.createElement("div");
	d.innerHTML = html;
	p.appendChild(d);
	
    generateItemPanel(response.duplicate, response.duplicate.id + "_container");
    setPanelListeners(response.duplicate);
    updateMappingsRecursive(response.duplicate);

	elementPanels["panel_" + response.duplicate.id].hide();
	elementPanels["panel_attributes_" + response.duplicate.id].hide();
}

function removeNode(itemId) {
	ajaxRemoveNode(itemId);
}

function removeNodeResponse(itemId, response) {
    var item_hd = YAHOO.util.Dom.get(itemId + "_hd");
    var item_container = YAHOO.util.Dom.get(itemId + "_container");
    var item_attributes_container = YAHOO.util.Dom.get(itemId + "_container_attributes");
    var parent = item_hd.parentNode;
    
    parent.removeChild(item_hd);
    parent.removeChild(item_container);
    parent.removeChild(item_attributes_container);
}

function constantValueEditHandler(e){
	YAHOO.util.Event.preventDefault(e);
	YAHOO.util.Event.stopPropagation(e);
	
	var target = (e.srcElement) ? e.srcElement : e.target;

    setupConstantValuePanel(target);
    constantValuePanel.show();
}

function setupConstantValuePanel(target) {
    constantValuePanel.target = target;
    YAHOO.util.Dom.get("constant").value = target.innerHTML;
}

function submitConstantValue() {
    var element = constantValuePanel.target;
    var target = element.id;
    var value = YAHOO.util.Dom.get("constant").value;
    value=encodeURIComponent(value);
    
    if(target.match("^clause") == "clause") {
    		var tokens = target.split(".");
    		tokens.shift();
    		var path = tokens.join(".");
    		
    		ajaxSetConditionClauseKey(conditionPanel.item.id, path, "value", value);
    } else {
	    var tokens = target.split(".");    
	    var id = tokens[0];
	    var index = tokens[1];
	    
	    if(index == "condition") {
	    		ajaxSetConditionValue(id, value);
	    } else {	    
		    if(index == "default") {
		        index = -1;
		    }
		
		    ajaxSetConstantValue(id, index, value);
	    }
    }
}

function setConstantValueResponse(response) {
    updateMappingsHighlight(response);
    constantValuePanel.hide();
}

function submitEnumerationValue(id) {
	var element = YAHOO.util.Dom.get("enumeration" + id);
	var dropdownIndex = element.selectedIndex;
	var dropdownValue = element[dropdownIndex].value;
	if(dropdownValue == null) { dropdownValue = ""; }

	ajaxSetEnumerationValue(id, dropdownValue);
}

function setEnumerationValueResponse(response) {
	updateMappingsHighlight(response);
}

function addConditionResponse(response) {
	updateMappingsHighlight(response);
}

function removeConditionResponse(response) {
	updateMappingsHighlight(response);
}

function setConditionXPathResponse(response) {
	updateMappingsHighlight(response);
}

function removeConditionXPathResponse(response) {
	updateMappingsHighlight(response);
}

function setConditionValueResponse(response) {
	updateMappingsHighlight(response);
    constantValuePanel.hide();
}

function removeConditionValueResponse(response) {
	updateMappingsHighlight(response);
}

function enableConstantValueEditingForElement(element) {
	YAHOO.util.Event.addListener(element, 'dblclick', constantValueEditHandler);
	YAHOO.util.Event.addListener(element, 'mouseover', showAsEditable);
	YAHOO.util.Event.addListener(element, 'mouseout', showAsNotEditable);
}

function enableConstantValueEditingForClass(className) {
    var element = YAHOO.util.Dom.get("mappings_container");
    var elems = getElementsByClassName(className,"",element);

    for(var e in elems) {
    		enableConstantValueEditingForElement(elems[e]);
    }
}

function previewTransform()
{
	transformPanel.setBody('<center><img src="js/mapping/lib/yui/carousel/assets/ajax-loader.gif" /></center>');
	transformPanel.show();
	ajaxPreviewTransform();
}

function previewTransformResponse(response)
{
	var content = '';
	
	content += '<div id="previewTabs" class="yui-navset">'; 
    content += '<ul class="yui-nav">'; 
    content += '<li class="selected" title="active"><a href="#tab1"><em>Input</em></a></li>'; 
    content += '<li><a href="#tab2"><em>XSL</em></a></li>'; 
    content += '<li><a href="#tab3"><em>Output</em></a></li>'; 
    content += '<li><a href="#tab4"><em>ESE</em></a></li>';
    content += '<li><a href="#tab5"><em>LIDO validation</em></a></li>';
    content += '<li><a href="#tab6"><em>ESE validation</em></a></li>';
    content += '</ul>';
    if(response.input.length>10000){
    	content += '<div class="yui-content">'; 
        content += '<div><p><div style="width: 95%; height: 385px;">';
        content += '<textarea  name="code" style="width: 100%; height: 375px; background: #FFFFFF;" rows="22" columns="50" readonly>';
        content += response.input;
        content += '</textarea>';
        content += '</div></p></div>';
        	
    	
    }else{
	    content += '<div class="yui-content">'; 
	    content += '<div><p><div style="width: 100%; height: 370px; overflow-x: auto; overflow-y: auto">';
	    content += '<textarea name="code" class="xml" style="width: 100%" rows="25" columns="50" readonly>';
	    content += response.input;
	    content += '</textarea>';
	    content += '</div></p></div>';
	    }
    content += '<div><p><div style="width: 100%; height: 370px; overflow-x: auto; overflow-y: auto">';
    content += '<textarea name="code" class="xml" style="width: 100%" rows="25" columns="50" readonly>';
    content += response.xsl;
    content += '</textarea>';
    content += '</div></p></div>';
    content += '<div><p><div style="width: 100%; height: 370px; overflow-x: auto; overflow-y: auto">';
    content += '<textarea name="code" class="xml" style="width: 100%" rows="25" columns="50" readonly>';
    content += response.output;
    content += '</textarea>';
    content += '</div></p></div>';
    
    content += '<div><p><div style="width: 100%; height: 370px; overflow-x: auto; overflow-y: auto">';
    content += '<textarea name="code" class="xml" style="width: 100%" rows="25" columns="50" readonly>';
    content += response.eseXml;
    content += '</textarea>';
    content += '</div></p></div>';
    
    content += '<div><p><div style="width: 100%; height: 370px; overflow-x: auto; overflow-y: auto">';
    content += '<div class="dp-highlighter" style="background-color:#FFF;">';
    content += response.lidovalidation;
    content += '</div>';
    content += '</div></p></div>';
    
    content += '<div><p><div style="width: 100%; height: 370px; overflow-x: auto; overflow-y: auto">';
    content += '<div class="dp-highlighter" style="background-color:#FFF;">';
    content += response.esevalidation;
    content += '</div>';
    content += '</div></p></div>';
    
    content += '</div></div>';
    
    transformPanel.setBody(content);
    dp.SyntaxHighlighter.HighlightAll('code');
    var tabs = new YAHOO.widget.TabView("previewTabs");
}

function mappingSummary()
{
	summaryPanel.setBody('<center><img src="js/mapping/lib/yui/carousel/assets/ajax-loader.gif" /></center>');
	summaryPanel.show();
	ajaxMappingSummary();
}

var resp;
function mappingSummaryResponse(response)
{
	resp = response;
   	var columns;
   	var source;
   	var table;
   	var content = "";

	content += '<div id="summaryTabs" class="yui-navset">'; 
    content += '<ul class="yui-nav">'; 
    content += '<li class="selected"><a href="#tab1"><em>Mapped</em></a></li>'; 
    content += '<li><a href="#tab2"><em>Missing</em></a></li>'; 
    content += '<li><a href="#tab3"><em>Invalid</em></a></li>'; 
    //content += '<li><a href="#tab4"><em>Summary</em></a></li>'; 
    content += '</ul>';
    
    content += '<div class="yui-content">';
    
    content += '<div><p><div style="width: 100%; height: 400px; overflow-x: auto; overflow-y: auto">';
    content += "<div style=\"width: 100%; overflow-y: auto\" id=\"mappingContainer\">";
    content += "<table id=\"mappingTable\">";
    content += "<thead>";
    content += "<tr>";
    content += "<th>Source</th>";
    content += "<th>Target</th>";
    content += "</tr>";
    content += "</thead>";
    content += "<tbody>";

    for(var i in response.mapped) {
    	content += "<tr>" ;
    	content += "<td> " + i + "</td>";
    	content += "<td> " + response.mapped[i] + "</td>";
    	content += "</tr>";
    }

	content += "</tbody>";
	content += "</table>";
	content += "</div>";
    content += '</div></p></div>';

    content += '<div><p><div style="width: 100%; height: 400px; overflow-x: auto; overflow-y: auto">';
    content += "<div style=\"width: 100%; overflow-y: auto\" id=\"missingContainer\">";
    content += "<table id=\"missingTable\">";
    content += "<thead>";
    content += "<tr>";
    content += "<th>Missing XPath</th>";
    content += "</tr>";
    content += "</thead>";
    content += "<tbody>";
    
    if(response.missing[0] == undefined) {
    	content += "<tr>";
    	content += "<td><i>No required xpath mapping is missing.</i></td>";
    	content += "</tr>";
    } else {
	    for(var i in response.missing) {
	    	content += "<tr>" ;
	    	content += "<td> " + response.missing[i] + "</td>";
	    	content += "</tr>";
	    }
    }
    
	content += "</tbody>";
	content += "</table>";
	content += "</div>";
    content += '</div></p></div>';
    
    content += '<div><p><div style="width: 100%; height: 400px; overflow-x: auto; overflow-y: auto">';
    content += "<div style=\"width: 100%; overflow-y: auto\" id=\"invalidContainer\">";
    content += "<table id=\"invalidTable\">";
    content += "<thead>";
    content += "<tr>";
    content += "<th>Invalid XPaths</th>";
    content += "</tr>";
    content += "</thead>";
    content += "<tbody>";
    
    if(response.invalid[0] == undefined) {
    	content += "<tr>";
    	content += "<td><i>All xpaths used in this mapping exist in the data upload.</i></td>";
    	content += "</tr>";
    } else {
	    for(var i in response.invalid) {
	    	content += "<tr>" ;
	    	content += "<td> " + response.invalid[i] + "</td>";
	    	content += "</tr>";
	    }
    }
    
	content += "</tbody>";
	content += "</table>";
	content += "</div>";
    content += '</div></p></div>';
    
    content += '</div></div>';
	
   	summaryPanel.setBody(content);
   	
   	columns = [{key:"Missing XPath",label:"Missing XPath",sortable:false,width: "300px"}];
	
	source = new YAHOO.util.DataSource(YAHOO.util.Dom.get("missingTable"));
    source.responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE;
    source.responseSchema = {fields: [{key:"Missing XPath"}]};
    
    table = new YAHOO.widget.ScrollingDataTable("missingContainer",
    	columns, source, {
    		caption:"Missing XPaths.",
    		width: "840px",
    		height:"29em"
    	});

   	columns = [{key:"Invalid XPath",label:"Invalid XPath",sortable:false,width: "300px"}];
   	
   	source = new YAHOO.util.DataSource(YAHOO.util.Dom.get("invalidTable"));
       source.responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE;
       source.responseSchema = {fields: [{key:"Invalid XPath"}]};
       
   table = new YAHOO.widget.ScrollingDataTable("invalidContainer",columns, source, {caption:"Invalid XPaths.",width: "840px",height:"29em"});

 	columns = [{key:"Source",label:"Source",sortable:false,width: "300px"},{key:"Target",label:"Target",sortable:false,width: "300px"}];
   	
   	source = new YAHOO.util.DataSource(YAHOO.util.Dom.get("mappingTable"));
       source.responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE;
       source.responseSchema = {fields: [{key:"Source"}, {key:"Target"}]};
       
   table = new YAHOO.widget.ScrollingDataTable("mappingContainer",
   	columns, source, {
   		caption:"Mapping.",
		width: "840px",
   		height:"29em"
   	});
   
   var tabs = new YAHOO.widget.TabView("summaryTabs");
}

function getHighlightedElementsResponse(response) {
	for(var normal in response.normal) {
		highlight(response.normal[normal], 'black');
	}

	for(var mapped in response.mapped) {
		highlight(response.mapped[mapped], 'green');
	}
	
	for(var missing in response.missing) {
		highlight(response.missing[missing], 'red');
	}


	for(var used in response.used) {
		try {
		  	var el = YAHOO.util.Dom.get(response.used[used]);
			
			el.style.color = "blue";
			
	
		} catch(e) {
		}
	}
	parent_tree_nodes = response.parent_used;
	
	
	for(var not_used in response.not_used) {
		try {
			var el = YAHOO.util.Dom.get(response.not_used[not_used]);
			el.style.color = "black";
		} catch(e) {
		}
	}
	
	 if(initcomplete==false){
	     inputTree.collapseAll();
		 for(var used in parent_tree_nodes) {
	    	
			try {
			 	var el = YAHOO.util.Dom.get(parent_tree_nodes[used]);
			 	n=inputTree.getNodeByElement(el);
				
			  	n.expand();
				
		
			} catch(e) {
			}
		}
	initcomplete=true;
	 }
	   
  }

function highlight(id, color) {
	var el = YAHOO.util.Dom.get(id);
	if(el != null && el != undefined) {
	    var title=getElementsByClassName("element","div",el);
	    title[0].style.color = color;
		
	}
}

function urlescape(value) {
//	var v = escape(value).replace(/\+/g, "%2B").replace(/\//g, "%2F");
	var v = value.replace(/\+/g, "%2B").replace(/\//g, "%2F");
	return v;
}

function showConditionPanel(id) {
	if(selectedPanel != "") {
		selectedPanel.hide();
	}
	
	var p = elementPanels["panel_" + id];
	var item = p.attachedItem;
	conditionPanel.item = item;

	var t = YAHOO.util.Dom.get("condition_editor_title");
	t.innerHTML = (item.label != undefined)?item.label:item.name;	
	
	/*
	if(item.condition == undefined || item.condition.logicalop == undefined) {
		ajaxInitComplexCondition(item);
	} else {
		initComplexConditionResponse(item.condition);
	}
	*/
	
	ajaxInitComplexCondition(item);
}

function initComplexConditionResponse(condition) {
	updateConditionPanelBody(condition);
	conditionPanel.show();
}

function updateConditionPanelBody(condition)
{
	var b = YAHOO.util.Dom.get("condition_editor_content");
	b.innerHTML = generateConditionPanelBody(condition);
	
    var xpaths = getElementsByClassName("mapping_value; clause_xpath","",b);
    for(var x in xpaths) {
    		var xdiv = xpaths[x];
        var elid = xdiv.id;
        ddListeners[elid] = new DDSend(elid, "mapping_input");
        ddListeners[elid].subscribe("b4MouseDownEvent", function() { return false; } );
    }
    
    enableConstantValueEditingForConditionValueClass("clause_value");
}

function generateConditionPanelBody(condition) {
	return generateConditionTable(condition, "");
}

function hideConditionPanel() {
	conditionPanel.hide();

	if(selectedPanel != "") {
		selectedPanel.show();
	}
}

function setXPathFunction(id, index)
{
	var p = elementPanels["panel_" + id];
	var item = p.attachedItem;
	functionPanel.item = item;
	functionPanel.targetid = id;
	functionPanel.targetindex = index;
	setupFunctionPanel();
	functionPanel.show();
}

function setupFunctionPanel()
{
	var functionPanelBody = "";
    
    functionPanelBody += '<div style="width: 100%; height: 100%>';
    functionPanelBody += '<div id="function_panel_header">';
    functionPanelBody += setupFunctionSelect();
    functionPanelBody += '</div>';
    functionPanelBody += '<div style="height:60%; width: 100%" id="function_panel_content">';
    functionPanelBody += '</div>';
    functionPanelBody += '<div style="height:20%; width:100%" id="function_panel_control">';
    functionPanelBody += '<input id="function_panel_save" type="button" value="Apply" onClick="javascript:submitXPathFunction()"/>';
    functionPanelBody += '<input id="function_panel_clear" type="button" value="Clear" onClick="javascript:clearXPathFunction()"/>';
    functionPanelBody += '<input id="function_panel_cancel" type="button" value="Cancel" onClick="javascript:functionPanel.hide()"/>';
    functionPanelBody += '</div>';
    functionPanelBody += '</div>';
    
    functionPanel.setBody(functionPanelBody);
    
    setupFunctionArguments(false);
}

var functions = [
                 {
                	 	name: "",
                	 	description: "no function",
                	 	arguments:[]
                 },
                 {
                	 	name: "substring",
                	 	description: "substring",
                	 	arguments: [
                	 	            { description: "string from index:" },
                	 	            { description: "to index:" }
                	 	            ]
                 },
                 {
                	 	name: "substring-after",
                	 	description: "substring after",
                	 	arguments: [
                	 	            { description: "select part of string after:" }
                	 	            ]
                 },
                 {
                	 	name: "substring-before",
                	 	description: "substring before",
                	 	arguments: [
                	 	            { description: "select part of string before:" }
                	 	            ]
                 },
                 {
                	 	name: "split",
                	 	description: "split",
                	 	arguments: [
                	 	            { description: "split string using delimeter:" },
                	 	            { description: "and select part:" }
                	 	            ]
                 },	
                 {
                	 	name: "custom",
                	 	description: "custom function",
                	 	arguments: [
                	 	            { description: "function:" }
                	 	            ],
                	 	warning: "Warning: use custom function only if you know exactly what you want to do"
                 },
                ];

function setupFunctionSelect()
{
	var result = "";
	
	result += "<select id=\"function_panel_select\" onchange=\"setupFunctionArguments(true)\">";
	for(var f in functions) {
		var selected = "";
		var fname = functions[f].name;
		var fdescription = functions[f].description;
		var func = functionPanel.item.mappings[functionPanel.targetindex].func;
		
		if(func != undefined) {
			if(func.call == fname) {
				selected = "selected";
			}
		}
		
		result += "<option value='" + fname + "' " + selected + ">" + fdescription + "</option>";
	}
	result += "</select>";
	
	return result;
}

function setupFunctionArguments(erase)
{
	var result = "";
	
	var element = YAHOO.util.Dom.get("function_panel_select");
	var dropdownIndex = element.selectedIndex;
	var select = element[dropdownIndex].value;
	if(select == null) { select = "no function"; }
	
	result += "<table>";
	
	for(var f in functions) {
		var fname = functions[f].name;
		
		if(fname == select) {
			var fcount = 0;
			var farguments = functions[f].arguments;

			if(functions[f].warning != undefined) {
				result += "<tr><td colspan='2'><i><div style='color: red; font-size:80%'>" + functions[f].warning + "</i><br/></div></td></tr>"
			}
			
			for(var fa in farguments) {
				if(erase) {
					result += "<tr><td>" + farguments[fa].description + "</td><td><input id='farg" + fcount + "' type='text'/></td></tr>";
				} else {
					result += "<tr><td>" + farguments[fa].description + "</td><td><input id='farg" + fcount + "' type='text' value='" + functionPanel.item.mappings[functionPanel.targetindex].func.arguments[fcount] + "'/></td></tr>";
				}
				fcount += 1;                             
			}
		}
	}
		
	result += "</table>";
	
    var arguments = YAHOO.util.Dom.get("function_panel_content");
    arguments.innerHTML = result;
}

function submitXPathFunction()
{	
	var element = YAHOO.util.Dom.get("function_panel_select");
	var dropdownIndex = element.selectedIndex;
	var select = element[dropdownIndex].value;
	if(select == null || select == "no function") { return; }
	
	var functionObj = {
			call : select,
			arguments: []
	};

	for(var f in functions) {
		var fname = functions[f].name;
		
		if(fname == select) {
			var fcount = 0;
			var farguments = functions[f].arguments;
			for(var fa in farguments) {
				functionObj.arguments.push(YAHOO.util.Dom.get("farg" + fcount).value);
				fcount += 1;                             
			}
		}
	}
		
	var data = encodeURIComponent(YAHOO.lang.JSON.stringify(functionObj));
	var id = functionPanel.targetid;
	var index = functionPanel.targetindex;
	
	ajaxSetXPathFunction(id, index, data);
}

function clearXPathFunction()
{
	var id = functionPanel.targetid;
	var index = functionPanel.targetindex;
	ajaxClearXPathFunction(id, index);
}

function clearXPathFunctionResponse()
{
	updateMappings(response);
	functionPanel.hide();
}

function setXPathFunctionResponse()
{
	updateMappings(response);
	functionPanel.hide();
}