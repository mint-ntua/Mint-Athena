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

package gr.ntua.ivml.athena.mapping;
import java.util.ArrayList;
import java.util.Iterator;

import net.sf.json.*;

public class JSONMappingHandler {
	JSONObject object = null;
	JSONMappingHandler(JSONObject mapping) {
		if(mapping == null) {
			throw new NullPointerException();
		} else {
			this.object = mapping;
		}
	}
	
	public String toString() {
		return object.toString();
	}
	public boolean isTopLevelMapping()
	{
		if(object.has("template")) {
			return true;
		}
		return false;
	}
	public boolean isElement()
	{
		if(object.has("name")) {
			if(!object.getString("name").startsWith("@")) {
				return true;
			}
		}
		
		return false;
	}
	public boolean isAttribute()
	{
		if(object.has("name")) {
			if(object.getString("name").startsWith("@")) {
				return true;
			}
		}
		
		return false;
	}
	public JSONObject getGroup(String name) {
		if(object.has("groups")) {
			JSONArray groups = object.getJSONArray("groups");
			Iterator i = groups.iterator();
			while(i.hasNext()) {
				JSONObject group = (JSONObject) i.next();
				if(group.has("element")) {
					if(group.getString("element").compareTo(name) == 0) {
						return group;
					}
				}
			}
		}

		return null;
	}
	public JSONMappingHandler getGroupHandler(String name) {
		JSONObject group = this.getGroup(name);
		if(group != null) {
			JSONObject contents = group.getJSONObject("contents");
			return new JSONMappingHandler(contents);
		}
		
		return null;
	}
	public JSONArray getAttributes() {
		if(object.has("attributes")) {
			return object.getJSONArray("attributes");
		}
		
		return null;
	}
	public JSONArray getChildren() {
		if(object.has("children")) {
			return object.getJSONArray("children");
		}
		
		return null;
	}
	public void setString(String key, String value) {
		object.element(key, value);
	}
	public void setObject(String key, JSONObject value) {
		object.element(key, value);
	}
	public void setArray(String key, JSONArray value) {
		object.element(key, value);
	}
	public String getString(String key) {
		if(object.has(key)) {
			return object.getString(key);
		}
		
		return null;
	}
	public String getOptString(String key) {
		if(object.has(key)) {
			return object.getString(key);
		}
		
		return "";
	}
	public JSONObject getObject(String key) {
		if(object.has(key)) {
			return object.getJSONObject(key);
		}
		
		return null;
	}
	public JSONMappingHandler getHandler(String key) {
		if(object.has(key)) {
			return new JSONMappingHandler(object.getJSONObject(key));
		}
		
		return null;
	}
	public JSONArray getArray(String key) {
		if(object.has(key)) {
			return object.getJSONArray(key);
		}
		
		return null;
	}

	public ArrayList<JSONMappingHandler> getHandlersForPath(String path) {
		if(this.isTopLevelMapping()) {
			if(path.startsWith("/")) { path = path.replaceFirst("/", ""); }
			String[] tokens = path.split("/", 2);
			if(tokens.length > 0) {
				JSONObject group = this.getGroup(tokens[0]);
				if(group != null) {
					JSONObject contents = group.getJSONObject("contents");
					return JSONMappingHandler.getHandlersForPath(contents, path);
				}
			}
		} else {
			return JSONMappingHandler.getHandlersForPath(object, path);
		}

		return new ArrayList<JSONMappingHandler>();	
	}
	private static ArrayList<JSONMappingHandler> getHandlersForPath(JSONObject object, String path) {
		ArrayList<JSONMappingHandler> result = new ArrayList<JSONMappingHandler>();
		if(path.startsWith("/")) { path = path.replaceFirst("/", ""); }
		String[] tokens = path.split("/", 2);
		if(tokens.length > 0) {
			if(object.has("name")) {
				if(tokens[0].equals(object.getString("name"))) {
					if(tokens.length == 1) {
						result.add(new JSONMappingHandler(object));
					} else {
						String tail = tokens[1];
						if(tail.startsWith("@")) {
							if(object.has("attributes")) {
								return JSONMappingHandler.getHandlersForPath(object.getJSONArray("attributes"), tail);
							}
						} else {
							if(object.has("children")) {
								return JSONMappingHandler.getHandlersForPath(object.getJSONArray("children"), tail);
							}
						}
					}
				}
			}
		}
		
		return result;
	}
	private static ArrayList<JSONMappingHandler> getHandlersForPath(JSONArray array, String path) {
		ArrayList<JSONMappingHandler> result = new ArrayList<JSONMappingHandler>();
		Iterator i = array.iterator();
		while(i.hasNext()) {
			JSONObject o = (JSONObject) i.next();
			result.addAll(JSONMappingHandler.getHandlersForPath(o, path));
		}
		return result;
	}
	public ArrayList<JSONMappingHandler> getHandlersForName(String name) {
		ArrayList<JSONMappingHandler> result = new ArrayList<JSONMappingHandler>();
		if(this.isTopLevelMapping()) {
			JSONArray groups = object.getJSONArray("groups");
			Iterator i = groups.iterator();
			while(i.hasNext()) {
				JSONObject group = (JSONObject) i.next();
				JSONObject contents = group.getJSONObject("contents");
				result.addAll(JSONMappingHandler.getHandlersForName(contents, name));
			}
		} else {
			if(this.getOptString("name").compareTo(name) == 0) {
				result.add(this);
			}

			result.addAll(JSONMappingHandler.getHandlersForName(this.getAttributes(), name));
			result.addAll(JSONMappingHandler.getHandlersForName(this.getChildren(), name));
		}

		return result;	
	}
	private static ArrayList<JSONMappingHandler> getHandlersForName(JSONObject object, String name) {
		return new JSONMappingHandler(object).getHandlersForName(name);
	}
	private static ArrayList<JSONMappingHandler> getHandlersForName(JSONArray array, String name) {
		ArrayList<JSONMappingHandler> result = new ArrayList<JSONMappingHandler>();
		if(array != null) {
			Iterator i = array.iterator();
			while(i.hasNext()) {
				JSONObject o = (JSONObject) i.next();
				result.addAll(JSONMappingHandler.getHandlersForName(o, name));
			}
		}
		return result;
	}
}
