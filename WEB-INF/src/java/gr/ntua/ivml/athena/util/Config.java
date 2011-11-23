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
 