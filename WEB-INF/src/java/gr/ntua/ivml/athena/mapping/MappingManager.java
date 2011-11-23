package gr.ntua.ivml.athena.mapping;

import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.Mapping;
import gr.ntua.ivml.athena.persistent.XMLNode;
import gr.ntua.ivml.athena.persistent.XpathHolder;
import gr.ntua.ivml.athena.util.TraversableI;
import gr.ntua.ivml.athena.xml.transform.XMLNodeTransform;
import gr.ntua.ivml.athena.xml.xsd.XSDParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class MappingManager {	
	private static final Logger log = Logger.getLogger( MappingManager.class);
	
	private XSDParser xsdParser;
	String dataUploadId = null;
	String mappingId = null;
	Mapping mapping = null;
	
	private Schema inputSchema;
	private Schema outputSchema;
	
	File targetDefinitionFile = null;
	String inputFileName = "input.xml";

	JSONObject targetDefinition = null;
	JSONObject templateCache = null;
	HashMap<String, JSONObject> elementCache = new HashMap<String, JSONObject>();
	HashMap<String, JSONObject> parentCache = new HashMap<String, JSONObject>();
	HashMap<String, JSONObject> groupsCache = new HashMap<String, JSONObject>();
	
	JSONObject documentation = null;
	
	private String getDocumentationForKey(String key) {
		if(documentation == null) {
			File file = new File(targetDefinitionFile.getParent() + "/documentation.json");
				
			StringBuffer contents = new StringBuffer();
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				if(reader != null) {
					String line = null;
					while((line = reader.readLine()) != null) {
						contents.append(line).append(System.getProperty("line.separator"));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			documentation = (JSONObject) JSONSerializer.toJSON(contents.toString());
		}
		
		String result = "";
		if(documentation.has(key)) {
			result = documentation.getString(key);
		} else {
			result = "No documentation for '" + key + "'";
		}
		
		return result;
	}
	
	DataUpload dataUpload = null;

	public MappingManager() {
	}
	
	public XSDParser getXSDParser()
	{
		if(this.xsdParser == null) {
			String schemaFileName = targetDefinitionFile.getParent() + "/" + targetDefinition.getString("xsd");
			this.xsdParser = new XSDParser(schemaFileName);				
		}
		
		return this.xsdParser;
	}
	
	public void init(String uploadId, String mId, String output) {
		this.dataUploadId = uploadId;
		this.mappingId = mId;
		this.templateCache = null;
		this.groupsCache.clear();
		this.elementCache.clear();
		this.parentCache.clear();
		
		log.debug("read input schema from dataUpload: " + this.dataUploadId);
		this.setInputSchema(this.dataUploadId);
		
		log.debug("read target definition: " + output);
		targetDefinitionFile = new File(output);
		if(targetDefinitionFile != null) { this.inputFileName = targetDefinitionFile.getParent() + "/input.xml"; }
		if(this.dataUpload != null) {
			String savedMappings = null;
			log.debug("get mapping object: " + mappingId);
			this.mapping = DB.getMappingDAO().getById(Long.parseLong(mappingId), false);
			
			if(mapping != null) {
				log.debug("get saved mappings");

				savedMappings = this.mapping.getJsonString();
			} else {
				log.error("mapping object is null");
			}
			
			log.debug("savedMappings: " + savedMappings);
			if(savedMappings != null) {
				targetDefinition = (JSONObject) JSONSerializer.toJSON(savedMappings);
				
				// check mapping version, if older then convert to newer version
				/*
				if(!MappingVersionControl.checkVersion(targetDefinition)) {
					String version = "-";
					if(targetDefinition.has("version")) { version = targetDefinition.getString("version"); }
					log.debug("Mapping definition version: " + version + " != " + MappingVersionControl.CURRENT_VERSION + " (current). Mapping will be converted");
					targetDefinition = MappingVersionControl.convertToCurrent(targetDefinition);
				}
				*/
				
				// cache groups
				JSONArray groups = targetDefinition.getJSONArray("groups");
				Iterator i = groups.iterator();
				while(i.hasNext()) {
					JSONObject group = (JSONObject) i.next();
					JSONObject contents = group.getJSONObject("contents");
					String element = group.getString("element");
					this.groupsCache.put(element, contents);
					this.cacheElements(contents);
				}
			} else {
				if(targetDefinitionFile != null) {
					StringBuffer targetDefinitionContents = new StringBuffer();
					try {
						BufferedReader reader = new BufferedReader(new FileReader(targetDefinitionFile));
						if(reader != null) {
							String line = null;
							while((line = reader.readLine()) != null) {
								targetDefinitionContents.append(line).append(System.getProperty("line.separator"));
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					targetDefinition = (JSONObject) JSONSerializer.toJSON(targetDefinitionContents.toString());
				}

				// initialise namespaces
				if(this.targetDefinition.has("namespaces")) {
					JSONObject object = this.targetDefinition.getJSONObject("namespaces");
					HashMap<String, String> map = new HashMap<String, String>();
					for(Object entry : object.keySet()) {
						String key = (String) entry;
						String value = object.getString(key);
						map.put(value, key);
					}
					
					this.getXSDParser().setNamespaces(map);
				}
				
				// initialise mapping definition
				this.targetDefinition = this.getTargetDefinition();

				// set namespaces
				JSONObject namespaces = new JSONObject();
				this.dataUpload = DB.getDataUploadDAO().getById(Long.parseLong(this.dataUploadId), false);
				XpathHolder xp = this.dataUpload.getRootXpath();
				
				// dataupload namespaces
				Map<String, String> map = xp.getNamespaces(true);			
				for(Entry<String, String> entry: map.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					// key is the xpath, value is the prefix
					namespaces = namespaces.element(value, key);
				}
				
				// xsd schema namespaces
				Map<String, String> acc = this.getXSDParser().getNamespaces();
				for(Entry<String, String> entry: acc.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					
					namespaces = namespaces.element(value, key);
				}
				
				this.targetDefinition = this.targetDefinition.element("namespaces", namespaces);
				
				// generate custom content - should be moved to an Athena subclass
				generateCustomContent();
			}
			
			// cache template
			JSONObject template = null;
			if(!this.targetDefinition.has("template") || this.targetDefinition.getJSONObject("template").isEmpty()) {
				template = this.buildTemplate(this.targetDefinition.getJSONObject("item").getString("element"));
				this.targetDefinition = this.targetDefinition.element("template", template);
			} else {
				template = targetDefinition.getJSONObject("template");
			}
			
			this.templateCache = template;
			this.cacheElements(this.templateCache);
			
			this.saveMappings();
		}
	}
	
	public void setArrayFixed(JSONArray array, boolean fixed) {
		Iterator i = array.iterator();
		while(i.hasNext()) {
			JSONObject object = (JSONObject) i.next();
			object = this.setFixedRecursive(object, fixed);
		}
	}
	
	public JSONObject setFixed(JSONObject object, boolean fixed) {
		if(fixed) {
			if(!object.has("fixed")) {
				object = object.element("fixed", "");
			}
		} else {
			if(object.has("fixed")) {
				object.remove("fixed");
			}
		}
		
		return object;
	}
	
	public JSONObject setFixedRecursive(JSONObject object, boolean fixed) {
		this.setFixed(object, fixed);
		if(object.has("attributes")) {
			this.setArrayFixed(object.getJSONArray("attributes"), fixed);
		}
		
		if(object.has("children")) {
			this.setArrayFixed(object.getJSONArray("children"), fixed);
		}
		
		return object;
	}
	
	private void generateCustomContent()
	{
		JSONObject europeanaClassification = this.duplicateObjectWithXPath("/objectClassificationWrap/classificationWrap/classification");
		if(europeanaClassification != null) {
			this.setFixed(europeanaClassification, true);
			europeanaClassification.element("label", "classification (europeana)");
			JSONArray children = europeanaClassification.getJSONArray("children");
			Iterator c = children.iterator();
			while(c.hasNext()) {
				JSONObject object = (JSONObject) c.next();
				if(object.getString("name").equals("term")) {
					JSONObject term = elementCache.get(object.getString("id"));
					JSONArray enumerations = new JSONArray();
					enumerations.add("IMAGE");
					enumerations.add("SOUND");
					enumerations.add("TEXT");
					enumerations.add("VIDEO");
					term.element("enumerations", enumerations);
					term.element("mandatory", "europeana classification");
				}
			}

			JSONArray attributes = europeanaClassification.getJSONArray("attributes");
			Iterator i = attributes.iterator();
			while(i.hasNext()) {
				JSONObject object = (JSONObject) i.next();
				if(object.getString("name").equals("@type")) {
					this.setFixed(object, true);
					JSONArray mappings = new JSONArray();
					mappings.add(new JSONObject().element("type", "constant").element("value", "europeana:type"));
					object.element("mappings", mappings);
				}
			}
		}
		
		JSONObject michael = this.duplicateObjectWithXPath("/objectClassificationWrap/classificationWrap/classification");
		if(michael != null) {
			this.setFixed(michael, true);
			michael.element("label", "classification (michael)");
			
			JSONArray children = michael.getJSONArray("children");
			Iterator c = children.iterator();
			while(c.hasNext()) {
				JSONObject object = (JSONObject) c.next();
				if(object.getString("name").equals("term")) {
					JSONObject term = elementCache.get(object.getString("id"));
					if(term.has("enumerations")) { term.remove("enumerations"); }
					if(term.has("mandatory")) { term.remove("mandatory"); }
				}
			}

			JSONArray attributes = michael.getJSONArray("attributes");
			Iterator i = attributes.iterator();
			while(i.hasNext()) {
				JSONObject object = (JSONObject) i.next();
				if(object.getString("name").equals("@type")) {
					this.setFixed(object, true);
					JSONArray mappings = new JSONArray();
					mappings.add(new JSONObject().element("type", "constant").element("value", "michael_collection"));
					object.element("mappings", mappings);
				}
			}
		}
		
		JSONObject isShownBy = this.duplicateObjectWithXPath("/resourceWrap/resourceSet");
		if(isShownBy != null) {
			this.setFixed(isShownBy, true);
			isShownBy.element("label", "resourceSet (master)");
			JSONArray children = isShownBy.getJSONArray("children");
			Iterator i = children.iterator();
			while(i.hasNext()) {
				JSONObject object = (JSONObject) i.next();
				if(object.getString("name").equals("linkResource")) {
					JSONObject linkResource = elementCache.get(object.getString("id"));
					linkResource.element("label", "linkResource (master)");
					JSONArray attributes = linkResource.getJSONArray("attributes");
					Iterator a = attributes.iterator();
					while(a.hasNext()) {
						JSONObject attr = (JSONObject) a.next();
						if(attr.getString("name").equals("@type")) {
							this.setFixed(attr, true);
							JSONArray mappings = new JSONArray();
							mappings.add(new JSONObject().element("type", "constant").element("value", "image_master"));
							attr.element("mappings", mappings);
						}
					}					
				}
			}
		}
		
		JSONObject eseObject = this.duplicateObjectWithXPath("/resourceWrap/resourceSet");
		if(eseObject != null) {
			this.setFixed(eseObject, true);
			eseObject.element("label", "resourceSet (thumb)");
			JSONArray children = eseObject.getJSONArray("children");
			Iterator i = children.iterator();
			while(i.hasNext()) {
				JSONObject object = (JSONObject) i.next();
				if(object.getString("name").equals("linkResource")) {
					JSONObject linkResource = elementCache.get(object.getString("id"));
					linkResource.element("label", "linkResource (thumb)");
					JSONArray attributes = linkResource.getJSONArray("attributes");
					Iterator a = attributes.iterator();
					while(a.hasNext()) {
						JSONObject attr = (JSONObject) a.next();
						if(attr.getString("name").equals("@type")) {
							this.setFixed(attr, true);
							JSONArray mappings = new JSONArray();
							mappings.add(new JSONObject().element("type", "constant").element("value", "image_thumb"));
							attr.element("mappings", mappings);
						}
					}					
				}
			}
		}
		
		// add default lidoRecId = athena:0000
		JSONArray templateChildren = this.targetDefinition.getJSONObject("template").getJSONArray("children");
		Iterator t = templateChildren.iterator();
		while(t.hasNext()) {
			JSONObject child = (JSONObject) t.next();
			if(child.getString("name").equals("lidoRecID")) {
				JSONObject m = new JSONObject().element("type", "constant").element("value", "athena:000000000");
				child.getJSONArray("mappings").add(m);
				
				JSONArray attributes = child.getJSONArray("attributes");
				Iterator a = attributes.iterator();
				while(a.hasNext()) {
					JSONObject attribute = (JSONObject) a.next();
					if(attribute.getString("name").equals("@type")) {
						JSONObject am = new JSONObject().element("type", "constant").element("value", "athena");
						attribute.getJSONArray("mappings").add(am);
					}
				}
			}
		}
		
		// add @type = 'local (default)'
		ArrayList<String> fields = new ArrayList<String>();
		fields.add("actorID");
		fields.add("conceptID");
		fields.add("eventID");
		fields.add("legalBodyID");
		fields.add("lidoRecID");
		fields.add("objectID");
		fields.add("placeID");
		fields.add("recordInfoID");
		fields.add("recordRelID");
		fields.add("recordID");
		fields.add("resourceID");
		fields.add("workID");
		
	    	JSONMappingHandler handler = new JSONMappingHandler(this.targetDefinition);
	    	Iterator fi = fields.iterator();
	    	while(fi.hasNext()) {
	    		String field = (String) fi.next();
//	    		System.out.println("field: " + field);
		    	ArrayList handlers = handler.getHandlersForName(field);
		    	Iterator hi = handlers.iterator();
		    	while(hi.hasNext()) {
		    		JSONMappingHandler h = (JSONMappingHandler) hi.next();
				JSONMappingHandler fieldObject = new JSONMappingHandler(elementCache.get(h.getString("id")));
	    			ArrayList aHandlers = fieldObject.getHandlersForName("@type");
	    			Iterator ai = aHandlers.iterator();
	    			while(ai.hasNext()) {
	    				JSONMappingHandler a = (JSONMappingHandler) ai.next();
	    				a.setString("default", "local (default)");
//	    				System.out.println(a.getString("id") + " was " + elementCache.get(a.getString("id")));
//	    				System.out.println(a.getString("id") + " = " + a.object);
	    				elementCache.put(a.getString("id"), a.object);
//					elementCache.get(object.getString("id"));
	    			}
		    	}
		}
	}

	public JSONObject getElementDescription(String element) {
		//log.debug("requested element description: " + element);
		if(this.groupsCache.containsKey(element)) {
//			log.debug("Reading JSON Object " + element + " from cache!");
			return groupsCache.get(element);
		} else {
			JSONObject result = this.getXSDParser().getElementDescription(element);
			this.groupsCache.put(element, result);
			this.cacheElements(result);
			return this.groupsCache.get(element);
		}
	}
	
	private void cacheElements(JSONObject object) {
		String id = this.generateUniqueId();
		object.put("id", id);
		this.elementCache.put(id, object);

		if(object.has("attributes")) {
			JSONArray attributes = object.getJSONArray("attributes");
			for(int i = 0; i < attributes.size(); i++) {
				JSONObject a = (JSONObject) attributes.get(i);
				this.cacheElements(a);
				this.parentCache.put(a.getString("id"), object);
			}
		}
		
		if(object.has("children")) {
			JSONArray children = object.getJSONArray("children");
			for(int i = 0; i < children.size(); i++) {		
				JSONObject a = (JSONObject) children.get(i);
				this.cacheElements(a);
				this.parentCache.put(a.getString("id"), object);
			}
		}
	}
	
	private int elementid = 0;
	private String generateUniqueId() {
		elementid++;
		return "" + elementid;
	}	

	public JSONObject getTargetDefinition() {
		JSONArray groups = this.targetDefinition.getJSONArray("groups");
		Iterator i = groups.iterator();
		while(i.hasNext()) {
			JSONObject item = (JSONObject) i.next();
			String element = item.getString("element");
			item.put("contents", this.getElementDescription(element));
		}
		
		if(!this.targetDefinition.has("template") || this.targetDefinition.getJSONObject("template").isEmpty()) {
			JSONObject template = this.buildTemplate(this.targetDefinition.getJSONObject("item").getString("element"));
			this.templateCache = template;
			this.cacheElements(this.templateCache);
		}

		this.targetDefinition.put("template", this.templateCache);

		return this.targetDefinition;
	}
	
	private JSONObject buildTemplate(String root) {
		log.debug("building template element: " + root);
	    JSONArray groups = this.targetDefinition.getJSONArray("groups");
		return this.getXSDParser().buildTemplate(groups, root);
	}
	
	public Schema getOutputSchema() { return outputSchema; }
	public void setOutputSchema(String outputSchema) {
		if(outputSchema == null) {
			this.outputSchema = null;
			return;
		}
		
		this.outputSchema = new Schema("2");
		this.outputSchema.initFromFile(outputSchema);
	}
	
	public Schema getInputSchema() { return inputSchema; }
	public void setInputSchema(String uploadId) {
		if(uploadId == null) {
			this.inputSchema = null;
			return;
		}
		
		this.dataUpload = DB.getDataUploadDAO().findById(Long.parseLong(uploadId), false);
		this.inputSchema = new Schema("1");
		this.inputSchema.initFromUpload(this.dataUpload);
	}
	
	//for item/label set tooltip
	public void setDataUploadId(String uploadId){
		this.dataUploadId=uploadId;
	}
	
	public String getItemLevelElementTooltip(String element) {
		StringBuffer out = new StringBuffer();
		MappingElement input = this.inputSchema.getMappingElement(element);
		XpathHolder xp = input.getXPathHolder();
		if(xp != null) {
			StringWriter writer = new StringWriter();
			String prefix = xp.getUriPrefix();
			long count = 0;
			long dcount = 0;
			
			count = input.getXPathHolder().getCount();
			dcount = input.getXPathHolder().getDistinctCount();
			
		  	out.append("<table width='100%' class='tooltipTable'>");
			out.append("<tr><td><b>Namespace:</b></td><td style='padding-x: 5px'>" + prefix + "</td></tr>");
			out.append("<tr><td><b>Name:</b></td><td style='padding-x: 5px'>" + input.getName() + "</td></tr>");
			out.append("<tr><td><b>Count:</b></td><td style='padding-x: 5px'>" + count + "</td></tr>");
			out.append("<tr><td><b>Distinct Count:</b></td><td style='padding-x: 5px'>" + dcount + "</td></tr>");
			out.append("<tr><td colspan='2'><b>Example:</b></td></tr>");
			out.append("<tr><td colspan='2' style=\"width: 100%\">");
			
			DB.getSession().refresh(this.dataUpload);
            out.append("<div style=\"width: 100%; overflow-y: auto\" id=\"exampleTableContainer\">");
            out.append("<table id=\"exampleTable\">");
            out.append("<thead>");
            out.append("<tr>");
            out.append("<th>Value</th>");
            out.append("<th>Frequency</th>");
            out.append("</tr>");
            out.append("</thead>");
            out.append("<tbody>");
 
    		if (xp.getTextNode() != null)
    			xp = xp.getTextNode();
    		if (xp.isAttributeNode() || xp.isTextNode()) {
    			List<Object[]> elements = xp.getCountByValue(30);
    			if(elements.isEmpty()) { 
    				out.append("<tr><td></td><td><i>This element has no values.</i></td></tr>");
    			} else {
	    			for (Object[] oa : elements) {
	    				String value = (String) oa[0];
	    				Long valueCount = (Long) oa[1];
	    				out.append( "<tr>" );
	    				out.append( "<td> " + value + "</td>" );
	    				out.append( "<td> " + valueCount + "</td>" );
	    				out.append( "</tr>" );
	    			}
    			}
    		}
            
            out.append("</tbody>");
            out.append("</table>");
            out.append("</div>");
		
			out.append("</td></tr>");
			//changed to work with new stats
			out.append("<tr><td colspan='2'><a href=\"#\" onclick=\"javascript:window.open('Stats.action?uploadId=" + this.dataUploadId + "','mywin','left=20,top=20,width=1024,height=510,toolbar=0,resizable=1');\">Statistics page</a></td></tr>");
			out.append("</table>");
	
		} else {
			out.append("<b>Error</b>: Node name " + element + " not found");
		}
		
		return out.toString();
	}

	
	
	public String getElementTooltip(String element) {
		StringBuffer out = new StringBuffer();
		MappingElement input = this.inputSchema.getMappingElement(element);
		XpathHolder xp = input.getXPathHolder();
		
		if(xp != null) {
			StringWriter writer = new StringWriter();
			String prefix = xp.getUriPrefix();
			long count = 0;
			long dcount = 0;
			
			count = input.getXPathHolder().getCount();
			dcount = input.getXPathHolder().getDistinctCount();
			
			// start tab code
			out.append("<div id=\"tooltipTabs\" class=\"yui-navset\">");
			out.append("<ul class=\"yui-nav\">");
			out.append("<li class=\"selected\"><a href=\"#tab1\"><em>Values</em></a></li>"); 
		    out.append("<li><a href=\"#tab2\"><em>Mapping</em></a></li>");
		    out.append("</ul>");
		    
		    out.append("<div class=\"yui-content\">");
		    
			// start table html code
		    out.append("<div><p><div style=\"width: 100%; height: 400px; overflow-x: auto; overflow-y: auto\">");
		    out.append("<div style=\"width: 100%; overflow-y: auto\" id=\"valuesTableInfoContainer\">");

			out.append("<table width='100%' class='tooltipTable'>");
			out.append("<tr><td><b>Namespace:</b></td><td style='padding-x: 5px'>" + prefix + "</td></tr>");
			out.append("<tr><td><b>Name:</b></td><td style='padding-x: 5px'>" + input.getName() + "</td></tr>");
			out.append("<tr><td><b>Count:</b></td><td style='padding-x: 5px'>" + count + "</td></tr>");
			out.append("<tr><td><b>Distinct Count:</b></td><td style='padding-x: 5px'>" + dcount + "</td></tr>");
			out.append("<tr><td colspan='2'><b>Example:</b></td></tr>");
			out.append("<tr><td colspan='2' style=\"width: 100%\">");
			
			DB.getSession().refresh(this.dataUpload);
            out.append("<div style=\"width: 100%; overflow-y: auto\" id=\"valuesTableContainer\">");
            out.append("<table id=\"valuesTable\">");
            out.append("<thead>");
            out.append("<tr>");
            out.append("<th>Value</th>");
            out.append("<th>Frequency</th>");
            out.append("</tr>");
            out.append("</thead>");
            out.append("<tbody>");
 
    		if (xp.getTextNode() != null)
    			xp = xp.getTextNode();
    		if (xp.isAttributeNode() || xp.isTextNode()) {
    			List<Object[]> elements = xp.getCountByValue(30);
    			if(elements.isEmpty()) { 
    				out.append("<tr><td></td><td><i>This element has no values.</i></td></tr>");
    			} else {
	    			for (Object[] oa : elements) {
	    				String value = (String) oa[0];
	    				Long valueCount = (Long) oa[1];
	    				out.append( "<tr>" );
	    				out.append( "<td> " + value + "</td>" );
	    				out.append( "<td> " + valueCount + "</td>" );
	    				out.append( "</tr>" );
	    			}
    			}
    		}
            
            out.append("</tbody>");
            out.append("</table>");
            out.append("</div>");
		
			out.append("</td></tr>");
			//changed to work with new stats
			out.append("<tr><td colspan='2'><a href=\"#\" onclick=\"javascript:window.open('Stats.action?uploadId=" + this.dataUploadId + "','mywin','left=20,top=20,width=1024,height=510,toolbar=0,resizable=1');\">Statistics page</a></td></tr>");
			out.append("</table>");
		    out.append("</div>");
		    out.append("</div></p></div>");
			// end table html code

		    // start mapping html code
		    out.append("<div><p><div style=\"width: 100%; height: 400px; overflow-x: auto; overflow-y: auto\">");
		    out.append("<div style=\"width: 100%; overflow-y: auto\" id=\"mappingInfoContainer\">");

			out.append("<table width='100%' class='tooltipTable'>");
//			out.append("<tr><td><b>Namespace:</b></td><td style='padding-x: 5px'>" + prefix + "</td></tr>");
//			out.append("<tr><td><b>Name:</b></td><td style='padding-x: 5px'>" + input.getName() + "</td></tr>");
//			out.append("<tr><td><b>Count:</b></td><td style='padding-x: 5px'>" + count + "</td></tr>");
//			out.append("<tr><td><b>Distinct Count:</b></td><td style='padding-x: 5px'>" + dcount + "</td></tr>");
//			out.append("<tr><td colspan='2'><b>Example:</b></td></tr>");
			out.append("<tr><td colspan='2' style=\"width: 100%\">");
			
			DB.getSession().refresh(this.dataUpload);
            out.append("<div style=\"width: 100%; overflow-y: auto\" id=\"mappingInfoTableContainer\">");
            out.append("<table id=\"mappingInfoTable\">");
            out.append("<thead>");
            out.append("<tr>");
            out.append("<th>XPath</th>");
            out.append("</tr>");
            out.append("</thead>");
            out.append("<tbody>");
 
			Collection<String> mappings = MappingSummary.mappingsWithXPath(this.getTargetDefinition(), input.getXPathHolder().getXpathWithPrefix(true));
			Iterator<String> it = mappings.iterator();
			if(!it.hasNext()) {
				out.append("<tr><td><i>This element is not used in any mapping.</i></td></tr>");
			} else {
				while(it.hasNext()) {
					String value = it.next();
					out.append( "<tr>" );
					out.append( "<td> " + value + "</td>" );				
					out.append( "</tr>" );
				}
			}
            
            out.append("</tbody>");
            out.append("</table>");
            out.append("</div>");
		
			out.append("</td></tr>");
			//changed to work with new stats
			out.append("<tr><td colspan='2'><a href=\"#\" onclick=\"javascript:window.open('Stats.action?uploadId=" + this.dataUploadId + "','mywin','left=20,top=20,width=1024,height=510,toolbar=0,resizable=1');\">Statistics page</a></td></tr>");
			out.append("</table>");
		    out.append("</div>");
		    out.append("</div></p></div>");
			// end table html code
		    // end mapping html code
		    out.append("</div>");
		    out.append("</div>");
			// end tab code
		} else {
			out.append("<b>Error</b>: Node name " + element + " not found");
		}
		
		return out.toString();
	}


	public JSONObject setXPathMapping(String source, String target, int index) {
		MappingElement sourceElement = this.inputSchema.getMappingElement(source);
		JSONObject targetElement = this.elementCache.get(target);
		String xpath = sourceElement.getXPath();
		
		setXPathMapping(xpath, targetElement, index);
		saveMappings();
		
		return targetElement;
	}
	
	public void setXPathMapping(String xpath, JSONObject target, int index) {
		JSONArray mappings = target.getJSONArray("mappings");
		JSONObject mapping = null;

		if(index > -1) {
			mapping = mappings.getJSONObject(index);
			mapping.put("type", "xpath");
			mapping.put("value", xpath);
		} else {
			mapping = new JSONObject();
			mapping.put("type", "xpath");
			mapping.put("value", xpath);
			mappings.add(mapping);
		}
		
		if(mapping != null) {
		}

		//mappings.clear();
	}

	public JSONObject setXPathFunction(String id, int index, String data) {
		JSONObject target = this.elementCache.get(id);
		JSONArray mappings = target.getJSONArray("mappings");
		JSONObject mapping = null;
		JSONObject function = (JSONObject) JSONSerializer.toJSON(data);

		if(index > -1) {
			mapping = mappings.getJSONObject(index);
			mapping.put("func", function);
		}

		saveMappings();
		
		return target;
	}
	
	public JSONObject clearXPathFunction(String id, int index) {
		JSONObject target = this.elementCache.get(id);
		JSONArray mappings = target.getJSONArray("mappings");
		JSONObject mapping = null;

		if(index > -1) {
			mapping = mappings.getJSONObject(index);
			mapping.remove("func");
		}

		saveMappings();
		
		return target;
	}
	
	public JSONObject setConstantValueMapping(String target, String value, int index) {
		JSONObject targetElement = this.elementCache.get(target);
		if(targetElement == null) {
			System.out.println("*** Could not find " + targetElement + " in element cache!");
		}

		setConstantValueMapping(targetElement, value, index);
		saveMappings();
		
		return targetElement;
	}
	
	public JSONObject setEnumerationValueMapping(String target, String value) {
		JSONObject targetElement = this.elementCache.get(target);
		if(targetElement == null) {
			System.out.println("*** Could not find " + targetElement + " in element cache!");
		}
		
		setEnumerationValueMapping(targetElement, value);
		saveMappings();
		
		return targetElement;
	}
	
	public void setConstantValueMapping(JSONObject target, String value, int index) {
		JSONArray mappings = target.getJSONArray("mappings");
		JSONObject mapping = null;

		if(index > -1) {
			mapping = mappings.getJSONObject(index);
			mapping.put("type", "constant");
			mapping.put("value", value);
		} else {
			mapping = new JSONObject();
			mapping.put("type", "constant");
			mapping.put("value", value);
			mappings.add(mapping);
		}
	}

	public void setEnumerationValueMapping(JSONObject target, String value) {
		JSONArray mappings = target.getJSONArray("mappings");
		JSONObject mapping = null;

		mappings.clear();
		if(value != null && value.length() > 0) {
			mapping = new JSONObject();
			mapping.put("type", "constant");
			mapping.put("value", value);
			mappings.add(mapping);
		}
	}

	public JSONObject addCondition(String target) {
		JSONObject targetElement = this.elementCache.get(target);
		
		targetElement.put("condition", new JSONObject().element("xpath", "").element("value", ""));
		saveMappings();
		
		return targetElement;
	}
	
	public JSONObject removeCondition(String target) {
		JSONObject targetElement = this.elementCache.get(target);
		
		targetElement.remove("condition");
		saveMappings();
		
		return targetElement;
	}
	
	public JSONObject setConditionXPath(String target, String value) {
		JSONObject targetElement = this.elementCache.get(target);
		MappingElement sourceElement = this.inputSchema.getMappingElement(value);
		String xpath = sourceElement.getXPath();
		
		if(targetElement.has("condition")) {
			log.debug("Set condition xpath for " + target + " to " + xpath);
			targetElement.getJSONObject("condition").put("xpath", xpath);
			saveMappings();			
		}
		
		return targetElement;		
	}
	
	public JSONObject removeConditionXPath(String target) {
		JSONObject targetElement = this.elementCache.get(target);
		
		if(targetElement.has("condition")) {
			log.debug("remove condition xpath for " + target);
			targetElement.getJSONObject("condition").put("xpath", "");
			saveMappings();			
		}
		
		return targetElement;		
	}
		
	public JSONObject setConditionValue(String target, String value) {
		JSONObject targetElement = this.elementCache.get(target);
		
		if(targetElement.has("condition")) {
			log.debug("Set condition value for " + target + " to " + value);
			targetElement.getJSONObject("condition").put("value", value);
			saveMappings();			
		}
		
		return targetElement;		
	}
	
	public JSONObject removeConditionValue(String target) {
		JSONObject targetElement = this.elementCache.get(target);
		
		if(targetElement.has("condition")) {
			log.debug("remove condition value for " + target);
			targetElement.getJSONObject("condition").put("value", "");
			saveMappings();			
		}
		
		return targetElement;		
	}
		
	public JSONObject removeMappings(String target, int index) {
		JSONObject targetElement = this.elementCache.get(target);

		removeMappings(targetElement, index);
		
		saveMappings();

		return targetElement;
	}
	
	public void  removeMappings(JSONObject target, int index) {
		JSONArray mappings = target.getJSONArray("mappings");
		
		if(index > -1) {
			mappings.remove(index);
		}
	}
	
	public JSONObject additionalMappings(String target, int index) {
		JSONObject targetElement = this.elementCache.get(target);
		JSONArray mappings = targetElement.getJSONArray("mappings");

		JSONObject empty = new JSONObject()
			.element("type", "empty")
			.element("value", "");
		
		if(index > -1) {
			mappings.add(index + 1, empty);
		}
		
		saveMappings();
		
		return targetElement;
	}
	
	public JSONObject objectForTargetXPath(String xpath) {
		//System.out.println("objectForTargetXPath: " + xpath);

		if(xpath.startsWith("/")) { xpath = xpath.replaceFirst("/", ""); }
		String[] tokens = xpath.split("/");
		if(tokens.length > 0) {
			JSONObject result = null;
			JSONObject group = this.groupsCache.get(tokens[0]);
			System.out.println("objectForTargetXPath token: " + tokens[0]);

			if(group != null) {
//				System.out.println("group: " + group.getString("name"));
//				JSONObject content = group.getJSONObject("contents");
				result = this.objectForTargetXPath(group, xpath);
				if(result != null) return result;
			}
		}

		return null;
	}
	
	public JSONObject objectForTargetXPath(JSONArray array, String xpath) {
		Iterator i = array.iterator();
		while(i.hasNext()) {
			JSONObject object = (JSONObject) i.next();
			JSONObject result = this.objectForTargetXPath(object, xpath);
			if(result != null) return result;
		}
		return null;
	}
	
	public JSONObject objectForTargetXPath(JSONObject object, String xpath) {
		System.out.println("objectForTargetXPath: " + object.getString("name") + " - "  + xpath);

		if(xpath.startsWith("/")) { xpath = xpath.replaceFirst("/", ""); }
		String[] tokens = xpath.split("/");
		if(tokens.length > 0) {
			log.debug("looking path:" + xpath + " in object:" + object);
			if(object.has("name")) {
				if(tokens[0].equals(object.getString("name"))) {
					if(tokens.length == 1) {
						return object;
					} else {
						String path = tokens[1];
						for(int i = 2; i < tokens.length; i++) {
							path += "/" + tokens[i];
						}
	
						if(path.startsWith("@")) {
							if(object.has("attributes")) {
								return this.objectForTargetXPath(object.getJSONArray("attributes"), path);
							}
						} else {
							if(object.has("children")) {
								return this.objectForTargetXPath(object.getJSONArray("children"), path);
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	public JSONObject duplicateObjectWithXPath(String xpath) {
		System.out.println("duplicate object: " + xpath);
		JSONObject result = null;
		JSONObject object = this.objectForTargetXPath(xpath);

		if(object != null) {
			result = this.duplicateNode(object.getString("id"));
			result = this.elementCache.get(result.getJSONObject("duplicate").getString("id"));
		}
		
		return result;
	}
	
	public JSONObject duplicateNode(String id) {
		JSONObject targetElement = this.elementCache.get(id);
		JSONObject parent = this.parentCache.get(id);

		JSONObject duplicate = duplicateJSONObject(targetElement);

		JSONArray children = parent.getJSONArray("children");
		if(children != null) {
			int index = -1;
			for(int i = 0; i < children.size(); i++) {
				JSONObject child = (JSONObject) children.get(i);
				if(child.getString("id").equals(id)) {
					index = i;
					break;
				}
			}
			
			if(index >= 0) {
				children.add(index, duplicate);
				duplicate = children.getJSONObject(index);
			}
		} else {
			JSONArray array = new JSONArray();
			array.add(duplicate);
			parent.put("children", array);
			children = parent.getJSONArray("children");
			duplicate = children.getJSONObject(0);
		}
		
		this.cacheElements(duplicate);	
		String duplicateId = duplicate.getString("id");
		this.parentCache.put(duplicateId, parent);

		this.saveMappings();

		return new JSONObject()
			.element("parent", parent.getString("id"))
			.element("original", id)
			.element("duplicate", duplicate);
	}
	
	public JSONObject removeNode(String id) {
		JSONObject result = new JSONObject();
		JSONObject targetElement = this.elementCache.get(id);
		JSONObject parent = this.parentCache.get(id);

		JSONArray children = parent.getJSONArray("children");
		if(children != null && !children.isEmpty()) {
			int targetIndex = -1;
			int targetCount = 0;
			for(int i = 0; i < children.size(); i++) {
				JSONObject child = (JSONObject) children.get(i);
				if(child.getString("id").equals(id)) {
					targetIndex = i;
				}
			}

			if(targetIndex >= 0) {
				children.remove(targetIndex);
				this.elementCache.remove(id);
				this.parentCache.remove(id);
			}
			
			result = result.element("id", id);
		} else {
			result = result.element("error", "could not find target element");
		}
		
		this.saveMappings();
		
		return result;
	}
	
	private JSONObject duplicateJSONObject(JSONObject source) {
		String json = source.toString();
		JSONObject out = null;
		
		out = (JSONObject) JSONSerializer.toJSON(json);
		out.put("duplicate", "");
		clearAllMappings(out);

		return out;
	}
	
	private void clearAllMappings(JSONObject object) {
		JSONArray mappings = object.getJSONArray("mappings");
		mappings.clear();
		
		if(object.has("attributes")) {
			JSONArray attributes = object.getJSONArray("attributes");
			for(int i = 0; i < attributes.size(); i++) {
				JSONObject a = (JSONObject) attributes.get(i);
				clearAllMappings(a);
			}
		}
		
		if(object.has("children")) {
			JSONArray children = object.getJSONArray("children");
			for(int i = 0; i < children.size(); i++) {		
				JSONObject a = (JSONObject) children.get(i);
				clearAllMappings(a);
			}
		}
	}
	
	public JSONObject previewTransform()
	{
		DataUpload du = DB.getDataUploadDAO().findById(Long.parseLong(this.dataUploadId), false);
		XMLNodeTransform transform = new XMLNodeTransform();

		JSONObject preview = transform.previewDataUploadTransformByMapping(du, this.getTargetDefinition().toString());
		
		log.debug(preview);
		return preview;
	}
	
	public JSONObject mappingElementsUsedInMapping()
	{
		JSONObject result = new JSONObject();
		JSONArray used = new JSONArray();
		JSONArray not_used = new JSONArray();
		JSONArray parent_used = new JSONArray();

		
		JSONObject mappings = this.getTargetDefinition();		
		Map<String, MappingElement> map = this.inputSchema.getMap();
		Collection<String> list = MappingSummary.getMappedXPathList(mappings);

		Iterator<String> keys = map.keySet().iterator();
		
		while(keys.hasNext()) {
			String id = keys.next();
			String xpath = map.get(id).getXPath();
			for(String xp: list){
				if(xp.length()>xpath.length()&& xp.indexOf(xpath)>-1){
			      parent_used.add(id);
			      break;  
				}
			}
			if(list.contains(xpath)) {
				used.add(id);
				
			} else {
				not_used.add(id);
			}
		}
		return result.element("used", used).element("not_used", not_used).element("parent_used",parent_used);
	}
	
	public JSONObject getDocumentation(String id) {
		JSONObject result = new JSONObject();
		JSONObject targetElement = this.elementCache.get(id);
		
		String key = targetElement.getString("name");
		result.element("title", key);
		result.element("documentation", this.getDocumentationForKey(key));
		
		return result;
	}
	
	public JSONObject initComplexCondition(String id) {
		String defaultLogicalOp = "AND";
		boolean conditionInit = false;
		
		JSONObject targetElement = this.elementCache.get(id);
		
		if(targetElement.has("condition")) {
			JSONObject condition = targetElement.getJSONObject("condition");
			if(!condition.has("logicalop")) {
				condition.element("logicalop", defaultLogicalOp);
				JSONArray clauses = new JSONArray();
				JSONObject clause = new JSONObject();
				if(condition.has("xpath") && condition.getString("xpath").length() > 0) { clause.element("xpath", condition.getString("xpath")); }
				if(condition.has("value") && condition.getString("value").length() > 0) { clause.element("value", condition.getString("value")); }
				if(condition.has("relationalop")) { clause.element("relationalop", condition.getString("=")); }
				clauses.add(clause);
				condition.element("clauses", clauses);
				
				conditionInit = true;
			}
		} else {
			targetElement.element("condition", new JSONObject().element("logicalop", defaultLogicalOp).element("clauses", new JSONArray()));
			conditionInit = true;
		}

		if(conditionInit) {
			saveMappings();
		}
		
		return targetElement.getJSONObject("condition");
	}
	
	public JSONObject addConditionClause(String id, String path, boolean complex)
	{
		JSONObject result = new JSONObject();
		JSONObject targetElement = this.elementCache.get(id);
		if(targetElement.has("condition")) {
			JSONObject condition = targetElement.getJSONObject("condition");
			this.addConditionClause(condition, path, complex);
			result = condition;
			
			saveMappings();
		}
		
		return result;
	}
	
	private void addConditionClause(JSONObject condition, String path, boolean complex) {
		if(condition.has("clauses")) {
			addConditionClause(condition.getJSONArray("clauses"), path, complex);
		}
	}
	
	private void addConditionClause(JSONArray clauses, String path, boolean complex) {
		JSONObject clause = new JSONObject();
		
		if(complex) {
			clause.element("logicalop", "AND");
			JSONArray array = new JSONArray();
			array.add(new JSONObject());
			clause.element("clauses", array);
		}
		
		if(path.length() == 0) {
			clauses.add(clause);
		} else {
			if(path.contains(".")) {
				String[] parts = path.split("\\.", 2);
				System.out.println("'" + path + "' '" + parts[0] + "' '" + parts[1] + "'");
				int index = Integer.parseInt(parts[0]);
				addConditionClause(clauses.getJSONObject(index), parts[1], complex);
			} else {
				int index = Integer.parseInt(path);
				addConditionClause(clauses.getJSONObject(index), "", complex);
			}
		}
	}
	
	public JSONObject removeConditionClause(String id, String path)
	{
		JSONObject result = new JSONObject();
		JSONObject targetElement = this.elementCache.get(id);
		if(targetElement.has("condition")) {
			JSONObject condition = targetElement.getJSONObject("condition");
			this.removeConditionClause(condition, path);
			result = condition;
			
			saveMappings();
		}
		
		return result;
	}
	
	private void removeConditionClause(JSONObject condition, String path) {
		if(condition.has("clauses")) {
			removeConditionClause(condition.getJSONArray("clauses"), path);
		}
	}
	
	private void removeConditionClause(JSONArray clauses, String path) {
		if(path.length() > 0) {
			if(path.contains(".")) {
				String[] parts = path.split("\\.", 2);
				int index = Integer.parseInt(parts[0]);
				if(parts[1].length() > 0) {
					removeConditionClause(clauses.getJSONObject(index), parts[1]);
				} else {
					clauses.remove(index);
				}
			} else {
				int index = Integer.parseInt(path);
				clauses.remove(index);
			}
		}
	}
	
	public JSONObject setConditionClauseKey(String id, String path, String key, String value)
	{
		JSONObject result = new JSONObject();
		JSONObject targetElement = this.elementCache.get(id);
		if(targetElement.has("condition")) {
			JSONObject condition = targetElement.getJSONObject("condition");
			this.setConditionClauseKey(condition, path, key, value);
			result = condition;
			
			saveMappings();
		}
		
		return result;
	}
	
	public JSONObject setConditionClauseXPath(String id, String path, String source)
	{
		JSONObject result = new JSONObject();
		JSONObject targetElement = this.elementCache.get(id);
		if(targetElement.has("condition")) {
			MappingElement sourceElement = this.inputSchema.getMappingElement(source);
			String value = sourceElement.getXPath();
			JSONObject condition = targetElement.getJSONObject("condition");
			this.setConditionClauseKey(condition, path, "xpath", value);
			result = condition;
			
			saveMappings();
		}
		
		return result;
	}
	
	
	private void setConditionClauseKey(JSONObject condition, String path, String key, String value) {
		if(path.length() == 0) {
			if(condition.has(key)) { condition.remove(key); }
			condition.element(key, value);
		} else {
			if(condition.has("clauses")) {
				JSONArray clauses = condition.getJSONArray("clauses");
				if(path.contains(".")) {
					String[] parts = path.split("\\.", 2);
					int index = Integer.parseInt(parts[0]);
					setConditionClauseKey(clauses.getJSONObject(index), parts[1], key, value);
				} else {
					int index = Integer.parseInt(path);
					setConditionClauseKey(clauses.getJSONObject(index), "", key, value);
				}
			}			
		}
	}
	
	public JSONObject removeConditionClauseKey(String id, String path, String key)
	{
		JSONObject result = new JSONObject();
		JSONObject targetElement = this.elementCache.get(id);
		if(targetElement.has("condition")) {
			JSONObject condition = targetElement.getJSONObject("condition");
			this.removeConditionClauseKey(condition, path, key);
			result = condition;
			
			saveMappings();
		}
		
		return result;
	}
	
	private void removeConditionClauseKey(JSONObject condition, String path, String key) {
		if(path.length() == 0) {
			condition.remove(key);
		} else {
			if(condition.has("clauses")) {
				JSONArray clauses = condition.getJSONArray("clauses");
				if(path.contains(".")) {
					String[] parts = path.split("\\.", 2);
					int index = Integer.parseInt(parts[0]);
					removeConditionClauseKey(clauses.getJSONObject(index), parts[1], key);
				} else {
					int index = Integer.parseInt(path);
					removeConditionClauseKey(clauses.getJSONObject(index), "", key);
				}
			}			
		}
	}
	
	public JSONObject mappingSummary()
	{
		JSONObject object = new JSONObject();
		DataUpload du = DB.getDataUploadDAO().findById(Long.parseLong(this.dataUploadId), false);
		String mappings = this.getTargetDefinition().toString();
		
		Collection<String> missing = MappingSummary.getMissingMappings(mappings);
		Collection<String> invalid = MappingSummary.getInvalidXPaths(du, mappings);
		Map<String, String> mapped = MappingSummary.getMappedItems(mappings);
//		Map<String, String> summary = MappingSummary.getSummary(mappings);
//		JSONObject tree_usage = this.mappingElementsUsedInMapping();

		object = object.element("missing", missing);
		object = object.element("invalid", invalid);
		object = object.element("mapped", mapped);
//		object = object.element("used", tree_usage.getJSONArray("used"));
//		object = object.element("not_used", tree_usage.getJSONArray("not_used"));
		//object = object.element("summary", summary);
		
		//log.debug(object);
		
		return object;
	}

	private void saveMappings() {
		if(this.mappingId != null) {
			DB.getSession().beginTransaction();
			String targetDefinitionString = this.getTargetDefinition().toString();
			Mapping map = DB.getMappingDAO().getById(Long.parseLong(this.mappingId), false);
			
			if(map == null) {
				log.error("No mapping object loaded!");
			} else {			
				map.setJsonString(targetDefinitionString);
			}
			
			DB.commit();
			log.debug("Mapping definition saved");
		}
	}
}
