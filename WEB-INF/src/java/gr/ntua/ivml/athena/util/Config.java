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

package gr.ntua.ivml.athena.util;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

/**
 * Class to read and reread a property file. Is static unsynced and stupid, 
 * but easy to use.
 * 
 * @author Arne Stabenau
 *
 */
public class Config {
	public static Properties properties = new Properties( System.getProperties());
	private static long lastRead;
	private static final long UPDATE_INTERVAL = 2000l;
	private static final String PROPS = "athena.properties";
	public static final Logger log = Logger.getLogger( Config.class );
	public static ServletContext context;
	
	public static String get( String key ) {
		checkAndRead();
		return properties.getProperty(key);
	}
	
	public static String get( String key, String defaultValue ) {
		checkAndRead();
		return properties.getProperty( key, defaultValue );
	}
	
	private static void checkAndRead() {
		if( lastRead==0l) readProps();
		else if(( System.currentTimeMillis() - lastRead ) > UPDATE_INTERVAL )
			readProps();
	}
	
	private static void readProps() {
	    try {
	    	InputStream inputStream = Config.class.getClassLoader().getResourceAsStream(PROPS);
	        properties.load(inputStream);
	        lastRead = System.currentTimeMillis();
	    } catch( Exception e) {
	    	log.error( "Can't read properties", e );
	    	throw new Error( "Configuration file " + PROPS + " not found in CLASSPATH", e);
	    }
	}
	
	public static void setContext( ServletContext sc ) {
		context = sc;
	}
	
	public static ServletContext getContext( ) {
		return context;
	}

	public static String getRealPath( String path  ) {
		if( context == null ) {
			log.warn("Calling getRealPath( path )  with no context set.");
			return path;
		}
		return context.getRealPath( path );
	}
}
 