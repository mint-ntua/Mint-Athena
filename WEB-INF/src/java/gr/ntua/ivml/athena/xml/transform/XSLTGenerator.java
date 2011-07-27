/*
This file is part of mint-athena. mint-athena services compose a web based platform that facilitates aggregation of cultural heritage metadata.
   Copyright (C) <2009-2011> Anna Christaki, Arne Stabenau, Costas Pardalis, Fotis Xenikoudakis, Nikos Simou, Nasos Drosopoulos, Vasilis Tzouvaras

   mint-athena program is free software: you can redistribute it and/or
modify
   it under the terms of the GNU Affero General Public License as
   published by the Free Software Foundation, either version 3 of the
   License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Affero General Public License for more details.

   You should have received a copy of the GNU Affero General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package gr.ntua.ivml.athena.xml.transform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import net.sf.json.*;

public class XSLTGenerator {
	private String root = "";
	private String match = "";

	private Stack<String> xpathPrefix = new Stack<String>();
	private Map<String, String> importNamespaces;
	
	public void setItemLevel(String root) {
		this.root = root;
	}
	
	public String getItemLevel() {
		return this.root;
	}
	
	public void setTemplateMatch(String match) {
		this.match = match;
	}
	
	public String getTemplateMatch() {
		return this.root;
		/*
		String[] tokens = this.root.split("/");
		if(tokens.length == 0) {
			return "/";
		} else {
			return "/" + tokens[tokens.length - 1];
		}
		*/
	}
	
	public String generateFromFile(String jsonFile) {
		File targetDefinitionFile = new File(jsonFile);
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
			
			return generateFromString(targetDefinitionContents.toString());
		}
		
		return null;
	}
	
	public String generateFromString(String jsonstring) {
		JSONObject mapping = (JSONObject) JSONSerializer.toJSON(jsonstring);

		if(mapping != null) {
			String xslt = generateFromJSONObject(mapping);
			return xslt;
		}
		
		return null;
	}
	
	public String generateFromJSONObject(JSONObject mapping) {
		String result = "";
		
		JSONArray groups = mapping.getJSONArray("groups");

		result += "<?xml version=\"1.0\"?>";
		String stylesheetNamespace = "";
		StringBuilder sb = new StringBuilder();

		JSONObject namespaces = new JSONObject();
		if(mapping.has("namespaces")) {			
			namespaces = mapping.getJSONObject("namespaces");
			
			for(Object o: namespaces.keySet()) {
				String key = (String) o;
				String value = namespaces.getString(key);
				
				sb.append("xmlns:" + key + "=\"" + value + "\" ");
			}
			
		}
		
		if(this.importNamespaces != null) {
			Iterator<String> i = this.importNamespaces.keySet().iterator();
			while(i.hasNext()) {
				String key = i.next();
				String value = this.importNamespaces.get(key);
				
				// stored differently than in json mapping -> key is value
				if(!namespaces.has(value)) {
					sb.append("xmlns:" + value + "=\"" + key + "\" ");
				}
			}
		}
		
		stylesheetNamespace = sb.toString();

		result += "<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" " +
			"xmlns:xalan=\"http://xml.apache.org/xalan\" " + stylesheetNamespace + ">";
		
		// result += "<xsl:output omit-xml-declaration=\"yes\" />";
		//default template - only proccess item level element and wrap it in defined wrap
		result += "<xsl:template match=\"/\">";
		
		if(mapping.has("wrap")) {
			String wrap = ((mapping.getJSONObject("wrap")).has("prefix")?(mapping.getJSONObject("wrap")).getString("prefix") + ":":"") + mapping.getJSONObject("wrap").getString("element");
			result += "<" + wrap + ">";
		}
	    result += "<xsl:apply-templates select=\"" + this.getTemplateMatch() + "\"/>";
		if(mapping.has("wrap")) {
			String wrap = ((mapping.getJSONObject("wrap")).has("prefix")?(mapping.getJSONObject("wrap")).getString("prefix") + ":":"") + mapping.getJSONObject("wrap").getString("element");
			result += "</" + wrap + ">";
		}
	    result += "</xsl:template>";
		
	    // start item template
		result += "<xsl:template match=\"" + this.getTemplateMatch() + "\">";
				
		JSONObject template = mapping.getJSONObject("template");
		result += this.generateTemplate(groups, template);
		
		result += "</xsl:template>";
		
		result += "</xsl:stylesheet>";

		return result;
	}
	
	private String generateTemplate(JSONArray groups, JSONObject template) {
		String result = "";
		
		if(template.has("name")) {
			xpathPrefix.push(this.getTemplateMatch());
			
			String name = template.getString("name");
			String type = template.getString("type");
			if(template.has("prefix")) {
				name = template.getString("prefix") + ":" + name; 
			}
	
			if(!type.equals("group")) {				
				JSONArray attributes = null;
				JSONArray mappings = null;

				if(template.has("mappings")) {
					mappings = template.getJSONArray("mappings");
					if(template.has("attributes")) {
						attributes = template.getJSONArray("attributes");
					}

					if(mappings.size() > 0) {
						if(mappings.size() == 1) { 
							result += generateWithMappings(name, attributes, mappings);
						} else {
							result += generateWithMappingsConcat(name, attributes, mappings);
						}		
					}
				}

				if(template.has("children") && template.getJSONArray("children").size() > 0) {
					result += "<" + name + ">";
					
					if(template.has("attributes")) {
						attributes = template.getJSONArray("attributes");
						result += this.generateAttributes(attributes);				
					}

					Iterator ci = template.getJSONArray("children").iterator();
					while(ci.hasNext()) {
						JSONObject child = (JSONObject) ci.next();
						result += this.generateTemplate(groups, child);
					}
					result += "</" + name + ">";
				}
			} else {
				Iterator gi = groups.iterator();
				while(gi.hasNext()) {
					JSONObject group = (JSONObject) gi.next();
					String gname = group.getString("element");
					if(template.getString("name").equals(gname)) {
						JSONObject content = group.getJSONObject("contents");
						result += generate(content);
					}
				}
			}
			
			xpathPrefix.pop();
		}

		return result;
	}

	private String generate(JSONObject item) {
		String name = item.getString("name");
		String prefix = null;
		if(item.has("prefix")) {
			name = item.getString("prefix") + ":" + name;
		}
		
		JSONArray attributes = null;
		JSONArray children = null;
		JSONArray mappings = null;
		JSONObject condition = null;
		boolean single = false;
				
		if(!descendantHasMappings(item)) {
			return "";
		}
				
		if(item.has("attributes")) {
			attributes = item.getJSONArray("attributes");			
		}
		
		if(item.has("children")) {
			children = item.getJSONArray("children");			
		}
		
		if(item.has("mappings")) {
			mappings =  item.getJSONArray("mappings");
		}
		
		if(item.has("condition")) {
			condition = item.getJSONObject("condition");
			/*
				if(!condition.has("xpath") ||
						!condition.has("value") ||
						condition.getString("xpath").length() == 0 ||
						condition.getString("value").length() == 0) {
					condition = null;
				}
			*/
		}
		
		if(item.has("maxOccurs") && item.getInt("maxOccurs") == 1) {
			single = true;
		}
		
		return generate(name, attributes, children, mappings, condition, single);
	}
	
	private String generate(String name, JSONArray attributes, JSONArray children, JSONArray mappings, JSONObject condition, boolean single) {
		String result = "";

		if(mappings != null && mappings.size() > 0) {
			if(children != null && children.size() > 0) {
				result += generateStructuralWithMappings(name, attributes, children, mappings, condition);
			} else {
				String conditionStart = null;
				if(condition != null) {
					conditionStart = generateConditionStart(condition);					
				}
				
				if(conditionStart != null) {
					result += conditionStart;
				}
				
				if(mappings.size() == 1) { 
					result += generateWithMappings(name, attributes, mappings, condition, single);
				} else {
					result += generateWithMappingsConcat(name, attributes, condition, mappings);
				}				

				if(conditionStart != null) {
					result += "</xsl:if>";
				}
			}
			
		} else {		
			result += generateWithNoMappings(name, attributes, children);
		}
				
		return result;
	}
	
	private String generateConditionStart(JSONObject condition) {
		String conditionTest = null;
		
		if(condition != null) {
//			String conditionXPath = condition.getString("xpath");
//			String conditionValue = condition.getString("value");
//			String testXPath = (normaliseXPath(conditionXPath));
			conditionTest = generateConditionTest(condition);
			if(conditionTest != null && conditionTest.length() > 0) {
				return "<xsl:if test=\"" + conditionTest + "\">";
			}
		}		
		return null;
	}
	
	private String logicalOpXSLTRepresentation(String logicalop) {
		if(logicalop != null) {
			if(logicalop.equalsIgnoreCase("AND")) {
				return "and";
			} else if(logicalop.equalsIgnoreCase("OR")) {
				return "or";
			}
		}
		
		 return "and";
	}
	
	private boolean isUnaryOperator(String operator) {
		if(operator.equals("EXISTS") || operator.equals("NOTEXISTS")) {
			return true;
		} else {
			return false;
		}
	}
	
	private String relationalOpXSLTRepresentation(String relationalop) {
		if(relationalop != null) {
			if(relationalop.equalsIgnoreCase("EQ")) {
				return "=";
			} else if(relationalop.equalsIgnoreCase("NEQ")) {
				return "!=";
			}
		}
		
		return "=";
	}
	
	private String generateConditionTest(JSONObject condition) {
		String result = "";
		
		if(condition != null) {
			if(condition.has("clauses") && condition.has("logicalop")) {
				String logicalop = condition.getString("logicalop");
				String clauseTest = "";
				JSONArray clauses = condition.getJSONArray("clauses");
				Iterator i = clauses.iterator();
				while(i.hasNext()) {
					JSONObject clause = (JSONObject) i.next();
					String test = generateConditionTest(clause);
					if(test.length() > 0) {
						if(clauseTest.length() > 0) {
							clauseTest += " " + logicalOpXSLTRepresentation(logicalop) + " ";
						}
	
						clauseTest += "(" + test + ")";
					}
				}
				
				result += clauseTest;
			} else {
				String conditionOp = "=";
				if(condition.has("relationalop")) {
					conditionOp = relationalOpXSLTRepresentation(condition.getString("relationalop"));
				}
				
				if(!condition.has("relationalop") || !isUnaryOperator(condition.getString("relationalop"))) {
					if(condition.has("xpath") && condition.has("value")) {
						String conditionXPath = condition.getString("xpath");
						String conditionValue = condition.getString("value");
						if(conditionXPath.length() > 0) {
							String testXPath = (normaliseXPath(conditionXPath));
							result += testXPath + " " + conditionOp + " '" + StringEscapeUtils.escapeXml(conditionValue).replace("\'", "&apos;") + "'";
						}
					}
				} else {
					if(condition.has("xpath")) {
						String conditionXPath = condition.getString("xpath");
						if(conditionXPath.length() > 0) {
							String testXPath = (normaliseXPath(conditionXPath));
							if(conditionOp.equals("EXISTS")) {
								result += testXPath;								
							} else if (conditionOp.equals("NOTEXISTS")) {
								result += "not(" + testXPath + ")";
							} 
						}
					}
				}
			}
		}		
		
		return result;
	}
	
	private String generateStructuralWithMappings(String name, JSONArray attributes, JSONArray children, JSONArray mappings, JSONObject condition) {
		String result = "";
		
		Iterator emi = mappings.iterator();
		while(emi.hasNext()) {
			JSONObject elementMapping = (JSONObject) emi.next();
			String elementMappingType = (String) elementMapping.getString("type");
			String elementMappingValue = (String) elementMapping.getString("value");
			String expath = "";
			String conditionStart = null;
			
			if(elementMappingType.equalsIgnoreCase("xpath")) {
				expath = (normaliseXPath(elementMappingValue));
				result += "<xsl:for-each select=\"" + expath + "\">";
				xpathPrefix.push(elementMappingValue);

				conditionStart = generateConditionStart(condition);
				if(conditionStart != null) {
					result += conditionStart;
				}
				result += "<" + name + ">";
			}
			
			result += generateAttributes(attributes, elementMappingValue);
			result += generateChildren(children);
			
			if(elementMappingType.equalsIgnoreCase("xpath")) {
				xpathPrefix.pop();
				result += "</" + name + ">";
				
				if(conditionStart != null) {
					result += "</xsl:if>";
				}

				result += "</xsl:for-each>";
			}

		}

		return result;
	}

	private String generateChildren(JSONArray children) {
		String result = "";

		if(children != null && children.size() > 0) {
			Iterator ci = children.iterator();
			while(ci.hasNext()) {
				JSONObject child = (JSONObject) ci.next();
				result += generate(child);
			}
		}

		return result;
	}
	
	private String generateAttributes(JSONArray attributes) {
		return generateAttributes(attributes, null);
	}
	
	private String generateAttributes(JSONArray attributes, String normaliseBy) {
		String result = "";
		 
		if(attributes != null && attributes.size() > 0) {
			Iterator ai = attributes.iterator();
			while(ai.hasNext()) {
				JSONObject attribute = (JSONObject) ai.next();
				String name = attribute.getString("name");
				String prefix = null;
				if(attribute.has("prefix")) {
					prefix = attribute.getString("prefix");
				}

				if(attribute.has("mappings")) {
					JSONObject condition = null;
					if(attribute.has("condition")) {
						condition = attribute.getJSONObject("condition");
					}
					
					String conditionStart = null;
					if(condition != null) {
						conditionStart = generateConditionStart(condition);
					}
					
					if(conditionStart != null) {
						result += conditionStart;
					}
					
					JSONArray amappings = attribute.getJSONArray("mappings");
					
					if(amappings != null && amappings.size() > 0) {
						JSONObject attributeMapping = (JSONObject) amappings.get(0);
						String attributeMappingType = (String) attributeMapping.getString("type");
						String attributeMappingValue = (String) attributeMapping.getString("value");
						String axpath = "";
						
						result += "<xsl:attribute name=\"" + ((prefix != null)?prefix + ":":"") + name.substring(1) + "\">";
						result += generateAttributeMappings(amappings);
/*						
						if(attributeMappingType.equalsIgnoreCase("xpath")) {
//							System.out.println("******* Normalise XPATH == " + normaliseBy);

							if(normaliseBy == null) {
								axpath = normaliseXPath(attributeMappingValue);
							} else {
								axpath = normaliseAttributeXPath(normaliseBy, attributeMappingValue);
							}
	
							axpath = normaliseXPath(attributeMappingValue);
							result += "<xsl:value-of select=\"" + axpath+ "\"/>";
						} else if(attributeMappingType.equalsIgnoreCase("constant")){
							result += StringEscapeUtils.escapeXml(attributeMappingValue);
						}
*/
						
						result += "</xsl:attribute>";
					} else if(attribute.has("default")) {
						result += "<xsl:attribute name=\"" + ((prefix != null)?prefix + ":":"") + name.substring(1) + "\">";
						result += attribute.getString("default");
						result += "</xsl:attribute>";
					}
					/*
					 else if(attribute.has("minOccurs")) {
					 	String minOccurs = attribute.getString("minOccurs");
					 	if(minOccurs.length() > 0) {
							result += "<xsl:attribute name=\"" + ((prefix != null)?prefix + ":":"") + name.substring(1) + "\">";
							result += "</xsl:attribute>";
					 	}
					 } 
					 */
					
					
					if(conditionStart != null) {
						result += "</xsl:if>";
					}					
				}
			}
		}
		
		return result;
	}
	
	private String generateWithNoMappings(String name, JSONArray attributes, JSONArray children) {
		String result = "<" + name + ">";
		
		result += generateAttributes(attributes);
		result += generateChildren(children);		
		
		result += "</" + name + ">";
		
		return result;
	}
	
	private String generateWithMappings(String name, JSONArray attributes, JSONArray mappings) {
		return generateWithMappings(name, attributes, mappings, false);
	}
	
	private String generateWithMappings(String name, JSONArray attributes, JSONArray mappings, boolean single) {
		return generateWithMappings(name, attributes, mappings, null, single);
	}
	
	private String generateWithMappings(String name, JSONArray attributes, JSONArray mappings, JSONObject condition, boolean single) {	
		String result = "";
		
		Iterator emi = mappings.iterator();
		while(emi.hasNext()) {
			JSONObject elementMapping = (JSONObject) emi.next();
			String elementMappingType = (String) elementMapping.getString("type");
			String elementMappingValue = (String) elementMapping.getString("value");
			String expath = "";
			
			if(elementMappingType.equalsIgnoreCase("xpath")) {
				expath = (normaliseXPath(elementMappingValue));	
				xpathPrefix.push(elementMappingValue);
				String conditionTest = this.generateConditionTest(condition);
				if(conditionTest.length() > 0) { expath += "[" + conditionTest + "]"; }
				result += "<xsl:for-each select=\"" + expath + "\">";
				if(single) {
					result += "<xsl:if test=\"position() = 1\">";
				}
				result += "<" + name + ">";
			} else if(elementMappingType.equalsIgnoreCase("constant")) {
				result += "<" + name + ">";					
			}
			
			result += generateAttributes(attributes, elementMappingValue);
			
			if(elementMappingType.equalsIgnoreCase("xpath")) {
				xpathPrefix.pop();
				
				if(elementMapping.has("func")) {
					result += applyXPathFunction(elementMapping.getJSONObject("func"));
				} else {
					result += "<xsl:value-of select=\".\"/>";
				}
				
				result += "</" + name + ">";
				if(single) {
					result += "</xsl:if>";
				}
				result += "</xsl:for-each>";
			} else if(elementMappingType.equalsIgnoreCase("constant")) {
				result += StringEscapeUtils.escapeXml(elementMappingValue);
				result += "</" + name + ">";
			}

		}

		return result;
	}

	private String applyXPathFunction(JSONObject func)
	{
		String result = "<xsl:value-of select=\".\"/>";
		if(func.has("call") && func.has("arguments")) {
			String call = func.getString("call");
			JSONArray args = func.getJSONArray("arguments");
			JSONArray arguments = new JSONArray();
			for(int a = 0; a < args.size(); a++) {
				arguments.add(args.getString(a));
			}
			
			if(call.equalsIgnoreCase("substring")) {
				result = "<xsl:value-of select=\"substring(.," + arguments.getString(0) + "," + arguments.getString(1) + ")\"/>";
			} else if(call.equalsIgnoreCase("substring-after")) {
				result = "<xsl:value-of select=\"substring-after(.,'" + arguments.getString(0) +  "')\"/>";
			} else if(call.equalsIgnoreCase("substring-before")) {
				result = "<xsl:value-of select=\"substring-before(.,'" + arguments.getString(0) +  "')\"/>";
			} else if(call.equalsIgnoreCase("split")) {
				// how can you split in xsl ???
				String varname = "split";
				result = "<xsl:variable name=\"" + varname + "\" select=\"tokenize(.,'" + arguments.getString(0) + "')\"/>";
				result += "<xsl:value-of select=\"$" + varname + "[" + arguments.getString(1) +"]\"/>";
			} else if(call.equalsIgnoreCase("custom")) {
				result = "<xsl:value-of select=\"" + arguments.getString(0) + "\"/>";
			} else {
				result = "<xsl:value-of select=\".\"/>";
			}
			
		}

		return result;
	}
	
	/*
	private String generateWithMappings(String name, JSONArray attributes, JSONArray mappings, boolean single) {
		String result = "";
		
		Iterator emi = mappings.iterator();
		while(emi.hasNext()) {
			JSONObject elementMapping = (JSONObject) emi.next();
			String elementMappingType = (String) elementMapping.getString("type");
			String elementMappingValue = (String) elementMapping.getString("value");
			String expath = "";
			
			if(elementMappingType.equalsIgnoreCase("xpath")) {
				expath = (normaliseXPath(elementMappingValue));	
				result += "<xsl:for-each select=\"" + expath + "\">";
				if(single) {
					result += "<xsl:if test=\"position() = 1\">";
				}
				result += "<" + name + ">";
				xpathPrefix.push(elementMappingValue);
			} else if(elementMappingType.equalsIgnoreCase("constant")) {
				result += "<" + name + ">";					
			}
			
			result += generateAttributes(attributes, elementMappingValue);
			
			if(elementMappingType.equalsIgnoreCase("xpath")) {
				xpathPrefix.pop();
				result += "<xsl:value-of select=\".\"/>";
				result += "</" + name + ">";
				if(single) {
					result += "</xsl:if>";
				}
				result += "</xsl:for-each>";
			} else if(elementMappingType.equalsIgnoreCase("constant")) {
				result += StringEscapeUtils.escapeXml(elementMappingValue);
				result += "</" + name + ">";
			}

		}

		return result;
	}
	*/
	
	private String generateAttributeMappings(JSONArray mappings) {
		String result = "";
		
		Iterator emi = mappings.iterator();
		while(emi.hasNext()) {
			JSONObject elementMapping = (JSONObject) emi.next();
			String elementMappingType = (String) elementMapping.getString("type");
			String elementMappingValue = (String) elementMapping.getString("value");
			String expath = "";
			
			if(elementMappingType.equalsIgnoreCase("xpath")) {
				expath = (normaliseXPath(elementMappingValue));	
				result += "<xsl:for-each select=\"" + expath + "\">";
				xpathPrefix.push(elementMappingValue);
			}
						
			if(elementMappingType.equalsIgnoreCase("xpath")) {
				xpathPrefix.pop();
				result += "<xsl:if test=\"position() = 1\">";
				result += "<xsl:value-of select=\".\"/>";
				result += "</xsl:if>";
				result += "</xsl:for-each>";
			} else if(elementMappingType.equalsIgnoreCase("constant")) {
				result += StringEscapeUtils.escapeXml(elementMappingValue);
			}

		}

		return result;
	}

	private String generateWithMappingsConcat(String name, JSONArray attributes, JSONArray mappings) {
		return generateWithMappingsConcat(name, attributes, null, mappings);
	}

	private String generateWithMappingsConcat(String name, JSONArray attributes, JSONObject condition, JSONArray mappings) {
		String result = "";
		
		result += "<" + name + ">";
		result += generateAttributes(attributes);
		
		Iterator emi = mappings.iterator();
		while(emi.hasNext()) {
			JSONObject elementMapping = (JSONObject) emi.next();
			String elementMappingType = (String) elementMapping.getString("type");
			String elementMappingValue = (String) elementMapping.getString("value");
			String expath = "";
			
			if(elementMappingType.equalsIgnoreCase("xpath")) {
				expath = (normaliseXPath(elementMappingValue));
				expath = (normaliseXPath(elementMappingValue));	
				xpathPrefix.push(elementMappingValue);
				String conditionTest = this.generateConditionTest(condition);
				if(conditionTest.length() > 0) { expath += "[" + conditionTest + "]"; }
				result += "<xsl:for-each select=\"" + expath + "\">";
				
				if(elementMapping.has("func")) {
					result += applyXPathFunction(elementMapping.getJSONObject("func"));
				} else {
					result += "<xsl:value-of select=\".\"/>";
				}
				
				result += "</xsl:for-each>";
				xpathPrefix.pop();
			} else if(elementMappingType.equalsIgnoreCase("constant")) {
				result += StringEscapeUtils.escapeXml(elementMappingValue);					
			}
		}
		
		result += "</" + name + ">";

		return result;
	}
	
	private boolean descendantHasMappings(JSONObject item) {
		JSONArray mappings = item.getJSONArray("mappings");
		if(mappings != null && mappings.size() > 0) {
			//System.out.println(item.get("name") + "hasMappings");
			return true;
		} else {
			if(item.has("children")) {
				JSONArray children = item.getJSONArray("children");
				if(children != null && children.size() > 0) {
					Iterator ci = children.iterator();
					while(ci.hasNext()) {
						JSONObject child = (JSONObject) ci.next();
						if(this.descendantHasMappings(child)) {
							//System.out.println(item.get("name") + "has child " + child.get("name") + " that has Mappings");
							return true;
						}
					}
				}
			}

			if(item.has("attributes")) {
				JSONArray attributes = item.getJSONArray("attributes");
				if(attributes != null && attributes.size() > 0) {
					Iterator ai = attributes.iterator();
					while(ai.hasNext()) {
						JSONObject attribute = (JSONObject) ai.next();
						if(this.descendantHasMappings(attribute)) {
							//System.out.println(item.get("name") + "has attribute " + attribute.get("name") + " that has Mappings");
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}

	private String normaliseXPath(String string) {
		String result = string;
		//string.replaceFirst(this.root + "/", "");
		if(!xpathPrefix.empty()) {
			String prefix = xpathPrefix.peek();
			if(result.indexOf(prefix) == 0) {
				result = result.replaceFirst(prefix, "");
				
				if(result.startsWith("/")) {
					result = result.replaceFirst("/", "");
				}
				
				if(result.length() == 0) {
					result = ".";
				}
			} else {
				String[] tokens1 = string.split("/"); 
				String[] tokens2 = prefix.split("/"); 

				int commonStartIndex = -1;
				for(int i = 0; i < tokens1.length; i++) {
					if(tokens2.length > i) {
						if(tokens1[i].equals(tokens2[i])) {
							commonStartIndex++;
						}
					}
				}
				
				if(commonStartIndex >= 0) {
					result = "";
					for(int i = 0; i < tokens2.length - commonStartIndex - 1; i++) {
						if(result.length() > 0 && !result.endsWith("/")) { result += "/"; }
						result += "..";
					}
					
					for(int i = commonStartIndex + 1; i < tokens1.length; i++) {
						if(result.length() > 0 && !result.endsWith("/")) { result += "/"; }
						result += tokens1[i];
					}
				}
			}
		}
		
		return result;
	}
	
	private String normaliseAttributeXPath(String parentXPath, String attributeXPath) {
		String result = attributeXPath;

		result = result.replaceFirst(parentXPath + "/", "");
				
		return result;
	}

	public void setImportNamespaces(Map<String, String> namespaces) {
		this.importNamespaces = namespaces;		
	}
}
