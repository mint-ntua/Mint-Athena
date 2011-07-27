<%@page import="java.util.HashMap"%>
<%@page import="java.util.Collection"%>
<%@page import="net.sf.json.*"%>
<%@page import="gr.ntua.ivml.athena.mapping.*"%>
<jsp:useBean id='mappings' class='gr.ntua.ivml.athena.mapping.MappingManager' scope='session'/>
<%
	request.setCharacterEncoding("UTF-8");
	String command = request.getParameter("command");
	out.clear();
	response.setContentType("text/plain; charset=UTF-8");
	
	if(command != null) {
		if(command.equals("init")) {
			String input = request.getParameter("upload");
			String mapping = request.getParameter("mapping");
			String output = request.getParameter("output");
			if(input != null && output != null && mapping != null) {
				String path = this.getServletContext().getRealPath(output);
				mappings.init(input, mapping, path);
				JSONObject target = new JSONObject()
					.element("sourceTree", mappings.getInputSchema().printTree())
					.element("targetDefinition", mappings.getTargetDefinition());
				out.println(target);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("getTargetDefinition")) {
				out.println(mappings.getTargetDefinition());
		}

		if(command.equals("getElementDescription")) {
			String element = request.getParameter("element");
			if(element != null) {
				out.println(mappings.getElementDescription(element));
			} else {
				out.println(new JSONObject().element("error", "ajax command " + command + ": no element"));
			}
		}

		if(command.equals("getTooltip")) {
			String element = request.getParameter("element");
			if(element != null) {
				String tooltip = "";
				
				try {
					tooltip = mappings.getElementTooltip(element);
				} catch(Exception e) {
					tooltip = "Error loading data...";
					e.printStackTrace();
				}
								
				out.println(new JSONObject()
					.element("tooltip", tooltip)
					.element("element", element)
				);
			} else {
				out.println(new JSONObject().element("error", "ajax command tooltip: no element"));
			}
		}
		
		if(command.equals("setXPathMapping")) {
			String source = request.getParameter("source");
			String target = request.getParameter("target");
			int index = Integer.parseInt(request.getParameter("index"));
			
			if(source != null & target != null) {
				JSONObject result = mappings.setXPathMapping(source, target, index);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("removeMappings")) {
			String target = request.getParameter("target");
			int index = Integer.parseInt(request.getParameter("index"));
			
			if(target != null) {
				JSONObject result = mappings.removeMappings(target, index);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("addCondition")) {
			String target = request.getParameter("target");
			
			if(target != null) {
				JSONObject result = mappings.addCondition(target);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("removeCondition")) {
			String target = request.getParameter("target");
			
			if(target != null) {
				JSONObject result = mappings.removeCondition(target);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("setConditionXPath")) {
			String target = request.getParameter("target");
			String value = request.getParameter("value");
			
			if(target != null) {
				JSONObject result = mappings.setConditionXPath(target, value);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("setConditionValue")) {
			String target = request.getParameter("target");
			String value = request.getParameter("value");
			
			if(target != null) {
				JSONObject result = mappings.setConditionValue(target, value);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("removeConditionXPath")) {
			String target = request.getParameter("target");
			
			if(target != null) {
				JSONObject result = mappings.removeConditionXPath(target);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("removeConditionValue")) {
			String target = request.getParameter("target");
			
			if(target != null) {
				JSONObject result = mappings.removeConditionValue(target);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("duplicateNode")) {
			String id = request.getParameter("id");
			
			if(id != null) {
				JSONObject result = mappings.duplicateNode(id);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("removeNode")) {
			String id = request.getParameter("id");
			
			if(id != null) {
				JSONObject result = mappings.removeNode(id);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("setConstantValueMapping")) {
			String id = request.getParameter("id");
			String value = request.getParameter("value");
			int index = Integer.parseInt(request.getParameter("index"));
			
			if(id != null) {
				JSONObject result = mappings.setConstantValueMapping(id, value, index);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}

		if(command.equals("setEnumerationValueMapping")) {
			String id = request.getParameter("id");
			String value = request.getParameter("value");
			
			if(id != null) {
				JSONObject result = mappings.setEnumerationValueMapping(id, value);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}

		if(command.equals("additionalMappings")) {
			String id = request.getParameter("id");
			int index = Integer.parseInt(request.getParameter("index"));
			
			if(id != null) {
				JSONObject result = mappings.additionalMappings(id, index);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("previewTransform")) {
			JSONObject result = mappings.previewTransform();
			out.println(result);
		}
		
		if(command.equals("mappingSummary")) {
			JSONObject result = mappings.mappingSummary();
			out.println(result);
		}
		
		if(command.equals("getHighlightedElements")) {
			JSONObject result = new JSONObject();
			
			JSONArray mapped = new JSONArray();
			JSONArray missing = new JSONArray();
			JSONArray normal = new JSONArray();
			
			JSONObject targetDefinition = mappings.getTargetDefinition();
			JSONObject tree_usage = mappings.mappingElementsUsedInMapping();

			mapped.addAll(MappingSummary.getIdsForElementsWithMappingsInside(targetDefinition));
			missing.addAll(MappingSummary.getMissingMappingsIds(targetDefinition));
			normal.addAll(MappingSummary.getIdsForElementsWithNoMappingsInside(targetDefinition));
			
			Collection<String> mandatory = MappingSummary.explicitMandatoryIds(targetDefinition);
			for(String id: mandatory) {
				if(!mapped.contains(id) && !missing.contains(id)) {
					missing.add(id);
				}
			}
			
			result = result.element("mapped", mapped).element("missing", missing).element("normal", normal);
			result = result.element("used", tree_usage.getJSONArray("used")).element("not_used", tree_usage.getJSONArray("not_used")).element("parent_used", tree_usage.getJSONArray("parent_used"));
			
			out.println(result);
		}
		
		if(command.equals("getDocumentation")) {
			JSONObject result = new JSONObject();
			String id = request.getParameter("id");

			if(id != null) {
				result = mappings.getDocumentation(id);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("initComplexCondition")) {
			JSONObject result = new JSONObject();
			String id = request.getParameter("id");

			if(id != null) {
				result = mappings.initComplexCondition(id);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("addConditionClause")) {
			JSONObject result = new JSONObject();
			String id = request.getParameter("id");
			String path = request.getParameter("path");
			String complex = request.getParameter("complex");
			boolean iscomplex = (complex != null);

			if(id != null) {
				result = mappings.addConditionClause(id, path, iscomplex);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("removeConditionClause")) {
			JSONObject result = new JSONObject();
			String id = request.getParameter("id");
			String path = request.getParameter("path");

			if(id != null) {
				result = mappings.removeConditionClause(id, path);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("setConditionClauseKey")) {
			JSONObject result = new JSONObject();
			String id = request.getParameter("id");
			String path = request.getParameter("path");
			String key = request.getParameter("key");
			String value = request.getParameter("value");

			if(id != null) {
				result = mappings.setConditionClauseKey(id, path, key, value);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("setConditionClauseXPath")) {
			JSONObject result = new JSONObject();
			String id = request.getParameter("id");
			String path = request.getParameter("path");
			String source = request.getParameter("source");

			if(id != null) {
				result = mappings.setConditionClauseXPath(id, path, source);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("removeConditionClauseKey")) {
			JSONObject result = new JSONObject();
			String id = request.getParameter("id");
			String path = request.getParameter("path");
			String key = request.getParameter("key");

			if(id != null) {
				result = mappings.removeConditionClauseKey(id, path, key);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}		
		
		if(command.equals("setXPathFunction")) {
			String id = request.getParameter("id");
			int index = Integer.parseInt(request.getParameter("index"));
			String data = request.getParameter("data");
			
			if(id != null & data != null) {
				JSONObject result = mappings.setXPathFunction(id, index, data);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}
		
		if(command.equals("clearXPathFunction")) {
			String id = request.getParameter("id");
			int index = Integer.parseInt(request.getParameter("index"));
			
			if(id != null) {
				JSONObject result = mappings.clearXPathFunction(id, index);
				out.println(result);
			} else {
				out.println(new JSONObject().element("error", "error:" + command + ": argument missing"));
			}
		}		
	} else {
		out.println(new JSONObject().element("error", "error: no command"));
	}
%>
