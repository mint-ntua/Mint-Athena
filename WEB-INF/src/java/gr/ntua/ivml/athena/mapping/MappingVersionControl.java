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

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

public class MappingVersionControl {
	private static final Logger log = Logger.getLogger( MappingVersionControl.class);
	public static final String CURRENT_VERSION = "1.0";
	public static boolean checkVersion(JSONObject object) {
		String version = "";
		if(object.has("version")) { version = object.getString("version"); }
		
		if(version.equalsIgnoreCase(CURRENT_VERSION)) {
			return true;
		}

		return false;
	}
	
	public static JSONObject convertToCurrent(JSONObject object) {
		JSONObject result = object;
		
		if(!checkVersion(object)) {
			String oldVersion = "-";
			if(object.has("version")) { oldVersion = object.getString("version"); }
			
			// convert to next version
			result = convert(result);
			
			String newVersion = "-";
			if(object.has("version")) { newVersion = object.getString("version"); }
			
			// if conversion was successful, repeat
			if(!oldVersion.equals(newVersion)) {
				log.debug("Mapping converted from version " + oldVersion + " to " + newVersion);
				result = convertToCurrent(result);
			} else {
				log.error("Mapping conversion from " + oldVersion + " failed!");
			}
		}
		
		return result;
	}
	
	private static JSONObject convert(JSONObject object) {
		JSONObject result = object;
		String version = "";
		if(object.has("version")) { version = object.getString("version"); }

		if(version.equalsIgnoreCase("")) {
			// first mapping version had no version info
			result = result.element("version", "1.0");
			// first mapping version had static template, set root to lidoWrap and remove the template so that the mapping manager can rebuild it
			result = result.element("root", "lidoWrap");
			result = result.discard("template");
		}
		
		return result;
	}
}
