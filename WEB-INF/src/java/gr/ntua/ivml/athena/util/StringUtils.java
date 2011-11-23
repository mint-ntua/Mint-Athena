package gr.ntua.ivml.athena.util;

/**
 * Class with tools to construct Strings.
 * Static import this functions to construct bigger strings.
 * 
 * @author Arne Stabenau 
 *
 */
public class StringUtils {
	/**
	 * Join the two parameter strings with the join string
	 * if they are not empty. If either is empty there will
	 * be no join string added.
	 * @param arg1
	 * @param join
	 * @param arg2
	 * @return
	 */
	public static String join( String arg1, String join, String arg2) {
		if(( arg1 != null ) && (arg1.trim().length() > 0) &&
				( arg2 != null ) && ( arg2.trim().length() > 0 ))
			return arg1 + join + arg2;
		else
			return arg1 + arg2;
	}
	
	public static String joinAll( String join, Object... args ) {
		String result = "";
		for( Object opt : args ) {
			if( opt != null )
			result = join( result, join, opt.toString() );
		}
		return result;
	}

	/**
	 * Return first argument if there is something to return,
	 *  else return second argument if there is something to return
	 *  and so forth, or return empty String.
	 * @param o1
	 * @param defaultString
	 * @return
	 */
	public static String getDefault( Object... args ) {
		String res = "";
		for( Object arg: args ) {
			if( arg != null)
				if( arg.toString().trim().length() > 0 ) {
					res = arg.toString();
					break;
				}
		}
		return res;
	}
	
	/**
	 * Prefix only if there is something to prefix, otherwise return empty string.
	 * Good for constructing "?param1=.." style argument strings
	 * hc.condPrefix( "?",parameters )
	 * @param prefix
	 * @param val
	 * @return
	 */
	public static String condPrefix( String prefix, String val ) {
		if(( val != null ) && ( val.trim().length() > 0  ))
			return prefix+val;
		else
			return "";
	}
	
	public static String condAppend( String val, String append ) {
		if(( val != null ) && ( val.trim().length() > 0  ))
			return val+append;
		else
			return "";
	}
	
	
	/**
	 * Concat only if all arguments are present and have non null length
	 * @param args
	 * @return
	 */
	public static String concatIfAll( Object... args ) {
		StringBuffer sb = new StringBuffer();
		for( Object obj: args ) {
			if( obj == null ) return "";
			String s = obj.toString().trim();			
			if( s.length() == 0 ) return "";
			sb.append( s );
		}
		return sb.toString();
	}
	
	public static StringBuffer filteredStackTrace( Throwable t, String filter ) {
		StringBuffer sb = new StringBuffer();
		StackTraceElement se[] = t.getStackTrace();
		for( StackTraceElement ste: se ) {
			if( ! ste.getClassName().startsWith(filter )) continue;
			sb.append( "  at " + ste.getClassName());
			sb.append( "." + ste.getMethodName());
			sb.append( "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")");
			sb.append( "\n" );
		}
		return sb;
	}

	public static boolean empty(String val) {
		return(( val == null) || ( val.trim().length()==0 ));
	}
}
