var targetDefinition = null;

var templateButton = "";
var groupButtons = [];
var elementPanels = [];
var ddListeners = [];

var selectedPanel = "";

var loadingPanel = "";
var annotationsPanel = "";
var tranformPanel = "";
var summaryPanel = "";
var tooltipPanel = "";
var constantValuePanel = "";
var conditionPanel = "";
var functionPanel = "";

var inputTree;
var thesaurusInputTree;
var parent_tree_nodes = [];
var initcomplete=false;

var getElementsByClassName = function (className, tag, elm){
	if (document.getElementsByClassName) {
		getElementsByClassName = function (className, tag, elm) {
			elm = elm || document;
			var elements = elm.getElementsByClassName(className),
				nodeName = (tag)? new RegExp("\\b" + tag + "\\b", "i") : null,
				returnElements = [],
				current;
			for(var i=0, il=elements.length; i<il; i+=1){
				current = elements[i];
				if(!nodeName || nodeName.test(current.nodeName)) {
					returnElements.push(current);
				}
			}
			return returnElements;
		};
	}
	else if (document.evaluate) {
		getElementsByClassName = function (className, tag, elm) {
			tag = tag || "*";
			elm = elm || document;
			var classes = className.split(" "),
				classesToCheck = "",
				xhtmlNamespace = "http://www.w3.org/1999/xhtml",
				namespaceResolver = (document.documentElement.namespaceURI === xhtmlNamespace)? xhtmlNamespace : null,
				returnElements = [],
				elements,
				node;
			for(var j=0, jl=classes.length; j<jl; j+=1){
				classesToCheck += "[contains(concat(' ', @class, ' '), ' " + classes[j] + " ')]";
			}
			try	{
				elements = document.evaluate(".//" + tag + classesToCheck, elm, namespaceResolver, 0, null);
			}
			catch (e) {
				elements = document.evaluate(".//" + tag + classesToCheck, elm, null, 0, null);
			}
			while ((node = elements.iterateNext())) {
				returnElements.push(node);
			}
			return returnElements;
		};
	}
	else {
		getElementsByClassName = function (className, tag, elm) {
			tag = tag || "*";
			elm = elm || document;
			var classes = className.split(" "),
				classesToCheck = [],
				elements = (tag === "*" && elm.all)? elm.all : elm.getElementsByTagName(tag),
				current,
				returnElements = [],
				match;
			for(var k=0, kl=classes.length; k<kl; k+=1){
				classesToCheck.push(new RegExp("(^|\\s)" + classes[k] + "(\\s|$)"));
			}
			for(var l=0, ll=elements.length; l<ll; l+=1){
				current = elements[l];
				match = false;
				for(var m=0, ml=classesToCheck.length; m<ml; m+=1){
					match = classesToCheck[m].test(current.className);
					if (!match) {
						break;
					}
				}
				if (match) {
					returnElements.push(current);
				}
			}
			return returnElements;
		};
	}
	
	return getElementsByClassName(className, tag, elm);
};


function init(upload, mapping, output) {
    initGUIPanels();
    ajaxInitMappings(upload, mapping, output);
}

function initMappingsResponse(response) {
    targetDefinition = response.targetDefinition;
    initGroupButtons();
    initElementPanels(); 
    initSourceTree(response.sourceTree);
    inputTree.expandAll();
    ajaxGetHighlightedElements();
    
    
 
    if(targetDefinition.xsd == 'lido-v0.9-proxy.xsd') {
    	var p = YAHOO.util.Dom.get("lido09Notice");
    	p.innerHTML = "(LIDO 0.9)";
    }
    
    
}

function initGUIPanels() {
    loadingPanel = new YAHOO.widget.Panel("wait",  
		{ width:"240px", 
		  fixedcenter:true, 
		  close:false, 
		  draggable:false, 
		  zindex:1,
          underlay: "shadow",
		  modal:true,
		  visible:false
		} 
	);
    
    loadingPanel.setHeader("Loading, please wait...");
    loadingPanel.setBody('<center><img src="js/mapping/lib/yui/carousel/assets/ajax-loader.gif" /></center>');
    
    annotationsPanel = new YAHOO.widget.Panel("annotations",
		{ width:"480px", 
		  fixedcenter:true, 
		  close:true, 
		  draggable:true, 
		  zindex:1,
          underlay: "shadow",
		  modal:true,
		  visible:false
		} 
    );
    
    annotationsPanel.setHeader("Annotation");
    annotationsPanel.setBody("Annotation");

    tooltipPanel = new YAHOO.widget.Panel("tooltips",
		{ width:"480px", 
		  height:"440px",
		  fixedcenter:true, 
		  close:true, 
		  draggable:true, 
		  zindex:1,
          underlay: "shadow",
		  modal:true,
		  visible:false
		} 
    );
    
    tooltipPanel.setHeader("Help");
    tooltipPanel.setBody('<center><img src="js/mapping/lib/yui/carousel/assets/ajax-loader.gif" /></center>');
    
    tooltipPanel = new YAHOO.widget.Panel("tooltips",
		{ width:"580px", 
		  height:"500px",
		  fixedcenter:true, 
		  close:true, 
		  draggable:true, 
		  zindex:1,
          underlay: "shadow",
		  modal:true,
		  visible:false
		} 
    );
    
    tooltipPanel.setHeader("Help");
    tooltipPanel.setBody('<center><img src="js/mapping/lib/yui/carousel/assets/ajax-loader.gif" /></center>');
    
    transformPanel = new YAHOO.widget.Panel("transform",
            { width:"900px",
              height:"500px",
    		  fixedcenter:true, 
    		  close:true, 
    		  draggable:true, 
    		  zindex:1,
              underlay: "shadow",
    		  modal:true,
    		  visible:false
    		} 
    	);
        
    var transformBody = "";
    
    transformBody += "Transforming...";
    
    transformPanel.setHeader("Transform");
    transformPanel.setBody(transformBody);

    summaryPanel = new YAHOO.widget.Panel("summary",
            { width:"900px",
              height:"500px",
    		  fixedcenter:true, 
    		  close:true, 
    		  draggable:true, 
    		  zindex:1,
              underlay: "shadow",
    		  modal:true,
    		  visible:false
    		} 
    	);
        
    var summaryBody = "";
    
    summaryBody += "Summary...";
    
    summaryPanel.setHeader("Summary");
    summaryPanel.setBody(summaryBody);

    constantValuePanel = new YAHOO.widget.Panel("constantValue",
        { width:"300px",
          height:"100px",
		  fixedcenter:true, 
		  close:true, 
		  draggable:true, 
		  zindex:1,
          underlay: "shadow",
		  modal:true,
		  visible:false
		} 
	);

    var constantValuePanelBody = "";
    
	constantValuePanelBody += '<input type="text" id="constant" name="constant"/>';
	constantValuePanelBody += '<br/><br/>';
    constantValuePanelBody += '<input id="panel_save" type="button" value="Ok" onClick="javascript:submitConstantValue()"/>';
    constantValuePanelBody += '<input id="panel_cancel" type="button" value="Cancel" onCLick="javascript:constantValuePanel.hide()"/>';
    
    constantValuePanel.setHeader("Set a constant value for this field");
    constantValuePanel.setBody(constantValuePanelBody);

    functionPanel = new YAHOO.widget.Panel("function",
            { width:"400px",
              height:"150px",
    		  fixedcenter:true, 
    		  close:true, 
    		  draggable:true, 
    		  zindex:1,
              underlay: "shadow",
    		  modal:true,
    		  visible:false
    		} 
    	);
    
    functionPanel.setHeader("Apply function to xpath value");
    functionPanel.setBody("<div/>");
        
    conditionPanel = new YAHOO.widget.Module("conditionModule",
        { width:"600px",
          height:"500px",
		  fixedcenter:true, 
		  close:false, 
		  draggable:false, 
		  zindex:1,
		  modal:false,
		  visible:false
		} 
	);
    
    var conditionPanelBody = "";
    conditionPanelBody += "<table style='width: 100%'>";
    // header
    conditionPanelBody += "<tr>";
    conditionPanelBody += "<td style='text-align: center'>";
    conditionPanelBody += "<h3>Condition Editor: <div id='condition_editor_title'></div></h3>";
    conditionPanelBody += "</td>";
    conditionPanelBody += "</tr>";
    // body
    conditionPanelBody += "<tr>";
    conditionPanelBody += "<td>";
    conditionPanelBody += "<div id='condition_editor_content'></div>";
    conditionPanelBody += "</td>";
    conditionPanelBody += "</tr>";
    // footer
    conditionPanelBody += "<tr>";
    conditionPanelBody += "<td>";
    conditionPanelBody += "<a href='javascript:hideConditionPanel()'>Back</a>";
    conditionPanelBody += "</td>";
    conditionPanelBody += "</tr>";
    conditionPanelBody += "</table>";
    
    conditionPanel.setHeader("");
    conditionPanel.setBody(conditionPanelBody);

    var ec = YAHOO.util.Dom.get("editor_container");
    if(ec == null) ec = document.body;
    
    loadingPanel.render(ec);
    annotationsPanel.render(ec);
    tooltipPanel.render(ec);
    transformPanel.render(ec);
    summaryPanel.render(ec);
    functionPanel.render(ec);
    constantValuePanel.render(ec);
    
    var mc = YAHOO.util.Dom.get("mappings_container");
    conditionPanel.render(mc);
}

var buttonGroup1;
var buttonGroup2;
var buttonGroup3;
function initGroupButtons() {
    var template_groups_container = YAHOO.util.Dom.get("template_groups_container");
    var descriptive_groups_container = YAHOO.util.Dom.get("descriptive_groups_container");
    var administrative_groups_container = YAHOO.util.Dom.get("administrative_groups_container");

     buttonGroup1 = new YAHOO.widget.ButtonGroup({ 
        id:  "buttongroup1", 
        name:  "radiofield", 
        container:  "template_groups_container" });

     buttonGroup2 = new YAHOO.widget.ButtonGroup({ 
        id:  "buttongroup2", 
        name:  "radiofield", 
        container:  "descriptive_groups_container" });

     buttonGroup3 = new YAHOO.widget.ButtonGroup({ 
        id:  "buttongroup3", 
        name:  "radiofield", 
        container:  "administrative_groups_container" });

    // process  template button
    if(targetDefinition.template != undefined) {
	    var template = targetDefinition.template;
	    var button = new YAHOO.widget.Button({
	        id: "btnTemplate",
	        label: "Template",
	        type: "radio",
	        checked: false
	    });
	    button.group = group;
	
	    function onTemplateButtonClick(p_oEvent) {
	        showTemplateElements();
	        resetRest(this);
	    }
	    button.on("click", onTemplateButtonClick);
	    buttonGroup1.addButton(button);
	    templateButton = button;
    }
    
    // process group buttons
    var btncount = 0;
    for(var i in targetDefinition.groups) {
        var group = targetDefinition.groups[i];
        var button = new YAHOO.widget.Button({
            id: "btn" + group.name,
            label: group.name,
            type: "radio",
            checked: false
        });
        button.group = group;

        function onButtonClick(p_oEvent) {
            showGroupElements(this.group);
            resetRest(this);
                        
        }
        button.on("click", onButtonClick);
        
        if(btncount < 4) {
        	buttonGroup2.addButton(button);
        } else {
        	buttonGroup3.addButton(button);
        }
        groupButtons[group.name] = button;
        
        btncount = btncount + 1;
    }
}

function initElementPanels() {
	// process template panel
	if(targetDefinition.template != undefined) {
		generateItemPanel(targetDefinition.template, "mappings_container");
		setTemplatePanelListeners(targetDefinition.template);
		hideAllPanels();
	}
	
	// process group panels
    for(var i in targetDefinition.groups) {
        var group = targetDefinition.groups[i];
        var panel = null;
        var item = group.contents;
 
        generateItemPanel(item, "mappings_container");
        setPanelListeners(group, group.contents);
        hideAllPanels();
    }
    
    //enableConstantValueEditingForClass("constantValue");
    enableConstantValueEditingForClass("empty_mapping");
    enableConstantValueEditingForClass("no_mapping");
    enableConstantValueEditingForClass("constant_mapping");
}

function generateItemPanel(item, container) {
	if(item.type == 'group') return;
	
	if(container != "mappings_container") { 
		generateAttributePanel(item, container + "_attributes");
	}
	
	generateElementPanel(item, container);
}

function generateAttributePanel(item, container) 
{
    var panelid = "panel_attributes_" + item.id;
    var panel = new YAHOO.widget.Module(
        panelid, {
        close: false,
        visible: true, 
        width: "400px",
        effect:{effect:YAHOO.widget.ContainerEffect.FADE,duration:0.25},
        draggable: false
    });
            
    var content = "";
    
    content += "<table style='width: 100%; height: 100%'>";
    content += "<tr>";
    if(container != 'mappings_container') { content += "<td style='width: 100%'><div class='elementident'></div></td>"; }
	content += toolEmptyButtonTD();
    content += "<td class='elementattributes'>";
    content += "<div>";
    for(var a in item.attributes) {
        var attribute = item.attributes[a];
        content += generateAttributeContent(attribute);
    }
    content += "</div>";
    content += "</td>";
	content += toolEmptyButtonWithStyleTD('background-color: #CCDDCC');
	content += toolEmptyButtonWithStyleTD('background-color: #CCDDCC');
	content += toolEmptyButtonWithStyleTD('background-color: #CCDDCC');
	content += toolEmptyButtonWithStyleTD('background-color: #CCDDCC');
    content += "</tr>";
    content += "</table>";
    
    panel.setBody(content);
    panel.render(YAHOO.util.Dom.get(container));
    elementPanels[panelid] = panel;

    return panel;
}

function generateElementPanel(item, container, ident) 
{
    var panelid = "panel_" + item.id;
    var panel = new YAHOO.widget.Module(
        panelid, {
        close: false,
        visible: true, 
        width: "400px",
        effect:{effect:YAHOO.widget.ContainerEffect.FADE,duration:0.25},
        draggable: false
    });
    
    panel.attachedItem = item;
            
    var content = "";
    
    content += "<table style='width: 100%; height: 100%'>";
    content += "<tr>";
    if(container != 'mappings_container') { content += "<td><div class='elementident'></div></td>"; }

    content += "<td>";
    content += "<div>";
    for(var c in item.children) {
        var child = item.children[c];

        if(child.children != undefined || child.attributes != undefined) {
	        content += generateComplexChildContent(child);
        } else if(child.type == "string") {
            content += generateElementContent(child);
        }
    }
    content += "</div>";
    content += "</td>";
    content += "</tr>";
    content += "</table>";
    
    panel.setBody(content);
    panel.render(YAHOO.util.Dom.get(container));
    elementPanels[panelid] = panel;

    for(var c in item.children) {
        var child = item.children[c];
        if(child.children != undefined || child.attributes != undefined) {
        	generateItemPanel(child, child.id + "_container");
        }
    }
    
    return panel;
}

function generateComplexChildContent(child) {
			var childid = child.id;
			var content = "";
			
			var style = "";
			
			if(child.type != "string") {
				style = "style=\"background-color:#dddddd\"";
			}
			
            content += "<div " + style + " id='" + childid + "_hd' class='elementhd'><table style=\"height: 100%\"><tr>";

            // open/close panel
            if(child.type == "string") {
            	content += toolEmptyButtonTD();
            } else {
                content += toolButtonTD("<img onclick='javascript:togglePanel(\"" + childid + "\")' width='14px' height='14px' src='images/expand_grey.png'/>");
            }
            
            // element content
            if(child.type == "string") {
                content += "<td class='elementcontent'>" + generateElementContent(child) + "</td>";
            } else {
//                content += "<td style='vertical-align: top'>" + child.name + "</td>";
                content += "<td class='elementcontent'>" + generateNonElementContent(child) + "</td>";
            }
            
            //content += "<td style='vertical-align: middle; width: 100%' ><div style='float:right'>";
            
            // remove duplicate node
            if(child.maxOccurs == -1 && child.duplicate != undefined && child.fixed == undefined) {
            	var s = "";
            	s += "<a href=\"javascript:removeNode('" + childid + "')\" style=\"vertical-align: center; border: 0px solid transparent\">";
                s += "<img width='14px' height='14px' src='images/close.png'/>";
            	s += "</a>";
            	content += toolButtonTD(s);
            } else {
            	content += toolEmptyButtonTD();
            }
            
            // duplicate button
            if(child.maxOccurs == -1 && child.fixed == undefined) { //if(child.type != "string" && child.maxOccurs == -1) {
            	var s = "";
            	s += "<a href=\"javascript:duplicateNode('" + childid + "')\" style=\"vertical-align: center; border: 0px solid transparent\">";
                s += "<img width='14px' height='14px' src='images/add.png'/>";
            	s += "</a>";
                content += toolButtonTD(s);
            } else {
            	content += toolEmptyButtonTD();
            }
            
            // attributes button
            if(child.attributes != undefined && child.attributes.length > 0) {
            	content += toolButtonTD("<img onclick='javascript:togglePanel(\"attributes_" + childid + "\")' width='14px' height='14px' src='images/expand.png'/>");
            } else {
            	content += toolEmptyButtonTD();
            }
            
            /*
            if(child.type == "string") {
            	content += toolButtonTD("<img onclick='javascript:togglePanel(\"" + childid + "\")' width='14px' height='14px' src='images/expand.png'/>");
            } else {
            	content += toolEmptyButtonTD();
            }
            */
  
            // annotation
//            content += toolButtonTD("<a style='border 0px solid transparent' href='javascript:showAnnotation(\"" + child.name + "\", \"" + child.annotation + "\")'><img src=\"images/help.png\" width='16px' height='16px'/></a>");
            content += toolButtonTD("<a style='border 0px solid transparent' href='javascript:showAnnotation(\"" + child.id + "\")'><img src=\"images/help.png\" width='16px' height='16px'/></a>");
            
            content += "</tr></table></div>";
            content += "<div class='el' id='" + childid + "_container_attributes'></div>";
            content += "<div class='el' id='" + childid + "_container'></div>";
            
			return content;
}

function generateElementContent(item) {
    var content = "";
    var id = item.id;

    content += "<div id='" + id + "' class='mappingTarget'>";
    content += "<table style='width: 100%; height: 100%'>";
    content += "<tr>";

    // element name
    var displayName = item.name;
    if(item.label != undefined) {
    	displayName = item.label;
    }
    content += "<td class='elementname'><div class='element' id='" + item.name + "Id'>" + displayName + ":</div></td>";
    content += "<td class='elementcontent'><div class='mapping' id='" + item.name + "Mapping'>";
    
    content += generateMappingsTable(item);
    
    content += "</div></td>";
    content += "</tr>";
    content += "</table>";
    content += "</div>";
    
    return content;
}

function generateAttributeContent(item) {
    var content = "";
    var id = item.id;

    content += "<div style='width:100%'>";
    content += "<div id='" + id + "' class='mappingTarget'>";
    content += "<table style='width: 100%; height: 100%'>";
    content += "<tr>";

    var displayName = item.name;
    if(item.label != undefined) {
    	displayName = item.label;
    }

    // element name
    content += "<td class='elementname'><div class='element' id='" + item.name + "Id'>" + displayName + ":</div></td>";
    content += "<td class='elementcontent'><div class='mapping' id='" + item.name + "Mapping'>";
    
    content += generateMappingsTable(item);
    
    content += "</div></td>";
    content += "</tr>";
    content += "</table>";
    content += "</div>";
    content += "</div>";
    
    return content;
}

function generateNonElementContent(item) {
    var content = "";
    var id = item.id;

    content += "<div id='" + id + "' class='mappingTarget'>";
    content += "<table style='width: 100%; height: 100%'>";
    content += "<tr>";

    var displayName = item.name;
    if(item.label != undefined) {
    	displayName = item.label;
    }

//    content += "<td style='vertical-align: top'><div style='float:right'><a  style='border: 0px solid transparent' href='javascript:removeMappings(\"" + item.id + "\")'><img src='images/close.png' width='14px' height='14px'/></a></div></td>";
    content += "<td class='elementname'><div class='element' id='" + item.name + "Id'>" + displayName + ":</div></td>";
    content += "<td class='elementcontent'><div class='mapping' id='" + item.name + "Mapping'>";
    
    content += generateMappingsTable(item);
    
    content += "</div></td>";
    content += "</tr>";
    content += "</table>";
    content += "</div>";
    
    return content;
}

var anitem = "";
//var enum = "";
function generateMappingsTable(item) {
    var content = "";
    anitem = item;
    
    var condition_xpath = "";
    var condition_value = "";
    
    if(item.condition != undefined) {
    	condition_xpath = item.condition.xpath;
    	condition_value = item.condition.value;
    }
    
    content += "<table style='width: 100%; height: 100%'>";
    content += "<tr>";
    
    if(item.fixed != undefined) {
    	content += toolEmptyButtonTD();
    	content += "<td>";
	    if(item.mappings.length > 0) {
	    	content += "<table>";
	        for(var i in item.mappings) {
	        	var type = item.mappings[i].type;
	        	var value = item.mappings[i].value;
	        	content += "<tr><td>" + value + "</td></tr>";
	        }
	        content += "</table>";
	    }
    	content += "</td>";
    } else {
	    // condition button
	    // check used to be: (item.children == undefined || item.children.length == 0) && (item.mappings.length > 0)
	    // changed it to allow conditions for structural mappings (on elements without children)
	    if((item.mappings.length > 0)) {
		    if(item.condition == undefined) {
		        content += toolButtonTD("<a style='border 0px solid transparent' href='javascript:ajaxAddCondition(\"" + item.id + "\")'><img src=\"images/condition_disabled.png\" width='16px' height='16px'/></a>");
		    } else {
		        content += toolButtonTD("<a style='border 0px solid transparent' href='javascript:ajaxRemoveCondition(\"" + item.id + "\")'><img src=\"images/condition.png\" width='16px' height='16px'/></a>");
		    }
	    }
	    
	    // mapping content
	    content += "<td class='elementcontent'>";
	    content += "<table style='width: 100%; height: 100%'>";
	    if(item.mappings.length > 0) {
	    	// condition content
	        if(item.condition != undefined) {
		        	content += "<tr><td colspan='3'>";
		        	content += "<table style='width: 100%; height: 100%'><tr>";
		        	if(!(item.condition.logicalop == undefined)) {
		        		content += "<td colspan='3'>";
		        		content += "if(...) - click icon on the right to see condition";
		        		content += "</td>";
		        	} else {
//			        	content += "<td style='vertical-align:middle; width: 20px'>if &nbsp</td>";
			        	content += "<td>if &nbsp</td>";
			        	
			        	if(condition_xpath == "") {
			                content += "<td><div id='" + item.id + ".condition.xpath' target='" + item.id +"' class='mapping_value'>condition input</div></td>";
			        	} else {
			        		var condition_xpath_element = condition_xpath.split("/").pop();
			        		var condition_tooltip = condition_xpath;
			
			    		    content += "<td style='vertical-align: middle'><div style='float:right'><a  style='border: 0px solid transparent' href='javascript:ajaxRemoveConditionXPath(\"" + item.id + "\")'><img src='images/close.png' width='14px' height='14px'/></a></div></td>";
			            	content += "<td><div>" + condition_xpath_element + "</div></td>";
			            	
			    			if(condition_tooltip.length > 0) {
			    		    	var conditionTooltip = new YAHOO.widget.Tooltip("conditionTooltip" + item.id, { context:"" + item.id + ".condition.xpath", text: condition_tooltip, showdelay: 300 } );
			    		    }
			        	}
			        	
//			        	content += "<td style='vertical-align: middle; width:20px'>&nbsp = &nbsp</td>";
			        	content += "<td>&nbsp = &nbsp</td>";
			
			        	if(condition_value == "") {
			                content += "<td><div id='" + item.id + ".condition' target='" + item.id +"' class='mapping_value; no_mapping'>condition value</div></td>";
			        	} else {
			    		    content += "<td style='vertical-align: middle'><div style='float:right'><a  style='border: 0px solid transparent' href='javascript:ajaxRemoveConditionValue(\"" + item.id + "\")'><img src='images/close.png' width='14px' height='14px'/></a></div></td>";
			               content += "<td><div id='" + item.id + ".condition' target='" + item.id +"' class='mapping_value; no_mapping'>" + condition_value + "</div></td>";
			        	}
		        	}
		        	// expand condition
	    		    content += "<td style='vertical-align: middle'><div style='float:right'><a  style='border: 0px solid transparent' href='javascript:showConditionPanel(\"" + item.id + "\")'><img src='images/more.png' width='14px' height='14px'/></a></div></td>";
		        	
		        	content += "</tr></table>";
		        	content += "</td></tr>";
	        }
	        
	        if(item.enumerations != null && item.enumerations.length > 0) {
	        	// enumeration content
	        	content += "<tr>";
	        	content += toolEmptyButtonTD();
	        	content += toolEmptyButtonTD();
	        	content += "<td class='elementcontent'>";
	        	content += generateItemEnumerationSelect(item);
	        	content += "</td></tr>";
	        } else {
		        for(var i in item.mappings) {
		            var index = i;
		        	var type = item.mappings[i].type;
		        	var value = "";
		        	var tooltip = "";
		            var class_value = "mapping_value; " + type + "_mapping";
		        	
		        	if(type == "xpath") {
		        		value = item.mappings[i].value;
		        		tooltip = value;
		        		value = value.split("/").pop();
		            } else if(type == "constant") {
		                value = item.mappings[i].value;
		        	} else {
		        		value = item.mappings[i].value;
		        	}
		
		            content += "<tr>";
		            
				    content += toolButtonTD("<div style='float:right'><a  style='border: 0px solid transparent' href='javascript:removeMappings(\"" + item.id + "\", \"" + index + "\")'><img src='images/close.png' width='14px' height='14px'/></a></div>");
				    
				    if(item.type == "string" || item.name.indexOf("@") === 0) {
				    	content += toolButtonTD("<div style='float:right'><a  style='border: 0px solid transparent' href='javascript:additionalMappings(\"" + item.id + "\", \"" + index + "\")'><img src='images/add.png' width='14px' height='14px'/></a></div>");
				    } else {
				    	content += toolEmptyButtonTD();
				    }
		
				    if(type == "xpath") {
				    		if(item.mappings[i].func == undefined) {
				    			content += toolButtonTD("<div style='float:right'><a  style='border: 0px solid transparent' href='javascript:setXPathFunction(\"" + item.id + "\", \"" + index + "\")'><img src='images/function-icon.png' width='20px' height='20px'/></a></div>");
				    		} else {
				    			content += toolButtonTD("<div style='float:right'><a  style='border: 0px solid transparent' href='javascript:setXPathFunction(\"" + item.id + "\", \"" + index + "\")'><img src='images/function-icon-selected.png' width='20px' height='20px'/></a></div>");
				    		}
				    } else {
				    		content += toolEmptyButtonTD();
				    }
		        	content += "<td class='elementcontent'>";
		        	if(type == "xpath") {
		        		value = item.mappings[i].value;
		        		tooltip = value;
		        		value = value.split("/").pop();
		
		                content += "<div id='" + item.id + "." + index + "' index=\"" + index + "\"  class='" + class_value + "' style='padding: 2px'>" + value + "</div>";
		            } else if(type == "constant") {
		                value = item.mappings[i].value;
		
		                content += "<div id='" + item.id + "." + index + "' index=\"" + index + "\" class='" + class_value + "' style='padding: 2px'>" + value + "</div>";
		        	} else if(type == "empty") {
		                content += "<div id='" + item.id + "." + index + "' index=\"" + index + "\" class='" + class_value + "' style='padding: 2px'>unmapped</div>";
		        	}
		        	content += "</td>";
		            
		
					if(tooltip.length > 0) {
				    	var xpathMappingTooltip = new YAHOO.widget.Tooltip("xpathMappingTooltip" + item.id, { context:"" + item.id + "." + index, text: tooltip, showdelay: 300 } );
				    }
				     
				    content += "</tr>";
		        }
	        }
	    } else {
	        if(item.enumerations != null && item.enumerations.length > 0) {
	        	content += "<tr>";
		    	content += toolEmptyButtonTD();
		    	content += toolEmptyButtonTD();
		    	content += toolEmptyButtonTD();
		    	content += toolEmptyButtonTD();
	        	content += "<td class='elementcontent'>";
	        	content += generateItemEnumerationSelect(item);
	        	content += "</td>";
	        	content += "</tr>";
	        } else {
		        content += "<tr>";
		    	content += toolEmptyButtonTD();
		    	content += toolEmptyButtonTD();
		    	content += toolEmptyButtonTD();
		    	content += toolEmptyButtonTD();
		        
		        if(item.type == undefined || item.type =="string") {
		        	content += "<td class='elementcontent'><div id='" + item.id + ".default' index=\"-1\" target='" + item.id +"' class='mapping_value; no_mapping'>unmapped</div></td>";
		        } else if(item.maxOccurs != 1) {
		        	content += "<td class='elementcontent'><div id='" + item.id + ".structural' index=\"-1\" target='" + item.id +"' class='mapping_value'>structural</div></td>";
		        }
	
		        content += "</tr>";
	        }
	    }
	    content += "</table>";
	
	    content += "</td>";
    }
    
    content += "</tr>";
    content += "</table>";
    
    return content;
}

function generateItemEnumerationSelect(item) {
	var selectedValue = "";
	var content = "";
	
	content += "<select id=\"enumeration" + item.id + "\" onchange=\"submitEnumerationValue('" + item.id + "')\">";

	if(item.mappings.length == 0) {
		content += "<option value=\"\" selected></option>";
	} else {
		selectedValue = item.mappings[0].value;
		content += "<option value=\"\"></option>";
	}		
	
	for(var e in item.enumerations) {
		if(selectedValue == item.enumerations[e]) {
    		content += "<option value=\"" + item.enumerations[e] + "\" selected>" + item.enumerations[e] + "</option>";
		} else {
    		content += "<option value=\"" + item.enumerations[e] + "\">" + item.enumerations[e] + "</option>";
		}
	}
	content += "</select>";	
	
	return content;
}

function generateElementId(parent, item, element) {
    return parent + "/" + item.name;
}

function setPanelListeners(item) {
    var id = item.id;
    
    if(item.mappings != null && item.mappings.length > 0) {
        for(var m in item.mappings) {
            var elid = id + "." + m;
            ddListeners[elid] = new DDSend(elid, "mapping_input");
            ddListeners[elid].subscribe("b4MouseDownEvent", function() { return false; } );
        }
    } else {
    	var elid = id;
    	if(item.type == "string" || item.type == undefined) {
	        elid += ".default";
    	} else {
    		elid += ".structural";
    	}

    	ddListeners[elid] = new DDSend(elid, "mapping_input");
        ddListeners[elid].subscribe("b4MouseDownEvent", function() { return false; } );
    }
    
    if(item.condition != null) {
    	var elid = id + ".condition.xpath";
        ddListeners[elid] = new DDSend(elid, "mapping_input");
        ddListeners[elid].subscribe("b4MouseDownEvent", function() { return false; } );
    }
    

    if(item.attributes != undefined) {
        for(var i in item.attributes) {
            setPanelListeners(item.attributes[i]);
        }
    }

    if(item.children != undefined) {
        for(var i in item.children) {
            setPanelListeners(item.children[i]);
        }
    }
}

function setTemplatePanelListeners(item) {
    var id = item.id;
    
    if(item.mappings != null && item.mappings.length > 0) {
        for(var m in item.mappings) {
            var elid = id + "." + m;
            ddListeners[elid] = new DDSend(elid, "mapping_input");
            ddListeners[elid].subscribe("b4MouseDownEvent", function() { return false; } );
        }
    } else {
    	var elid = id;
    	if(item.type == "string" || item.type == undefined) {
	        elid += ".default";
    	} else {
    		elid += ".structural";
    	}

    	ddListeners[elid] = new DDSend(elid, "mapping_input");
        ddListeners[elid].subscribe("b4MouseDownEvent", function() { return false; } );
    }
    
    if(item.condition != null) {
    	var elid = id + ".condition.xpath";
        ddListeners[elid] = new DDSend(elid, "mapping_input");
        ddListeners[elid].subscribe("b4MouseDownEvent", function() { return false; } );
    }
    

    if(item.attributes != undefined) {
        for(var i in item.attributes) {
            setTemplatePanelListeners(item.attributes[i]);
        }
    }

    if(item.children != undefined) {
        for(var i in item.children) {
            setTemplatePanelListeners(item.children[i]);
        }
    }
}

function initSourceTree(treeDefinition) {
    var iTreeEl = YAHOO.util.Dom.get("sourceTree");
    iTreeEl.innerHTML = treeDefinition;
    inputTree = new YAHOO.widget.TreeView("treemenu_1");
    if(inputTree != null) {
        inputTree.render();
        inputTree.subscribe("expandComplete", initSourceTreeListeners);
        initRootNodeListeners();
    } else {
        alert("There is no input tree!");
    }
}

function initRootNodeListeners() {
	var roots = inputTree.getRoot().children;
    if(roots == null) return;
    for(var i = 0; i < roots.length; i++) {
        var contentEl = roots[i].getContentEl();
        if(contentEl != null) {
            // var targets = contentEl.getElementsByClassName("xmlelement","",""); fix for ie below
            var targets=getElementsByClassName("xmlelement","div",contentEl);
            if(targets.length > 0) {
                initNodeListener(targets[0].id, "mapping_input");
            }
        }
    }
}

function initSourceTreeListeners(node, b, c, d) {

      var i = 0;
      var n = 0;
      for(n=0; n < node.children.length; n++) {
          var yuiId = node.children[n].contentElId;
          var yuiEl = YAHOO.util.Dom.get(yuiId);
          var nodeId = yuiEl.childNodes[0].id;
          var found = false;
            
          initNodeListener(nodeId, "mapping_input");
      }
}

function initNodeListener(id, target) {
    var el = YAHOO.util.Dom.get(id);
    if(el != null) {
        ddListeners[id] = new DDSend(id, target);
    }
}