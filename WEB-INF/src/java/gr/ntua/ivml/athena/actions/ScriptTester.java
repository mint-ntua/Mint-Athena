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

package gr.ntua.ivml.athena.actions;

import gr.ntua.ivml.athena.persistent.User;
import gr.ntua.ivml.athena.util.Config;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({
	  @Result(name="input", location="script_test.jsp"),
	  @Result(name="success", location="script_test.jsp"),
	  @Result(name="error", location="Home", type="redirectAction")
	})

public class ScriptTester extends GeneralAction {
	
	
	public static final Logger log = Logger.getLogger( ScriptTester.class );
	private String script;
	private String result;
	private String stdOut;
	
	private static Map<String, String> library=null;
	private String scriptlet;
	
	public String getStdOut() {
		return stdOut;
	}

	public void setStdOut(String stdOut) {
		this.stdOut = stdOut;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Action("Script")
	public String execute( ) throws Exception {
		
		// check if we just want a scriptlet
		if( ! "/".equals(getScriptlet())) {
			if( getLib().containsKey(getScriptlet())) {
				File scriptFile = new File( getScriptlet());
				script = FileUtils.readFileToString(scriptFile, "UTF-8" );
			}
			return "success";
		}
		Binding binding = new Binding();
		if( !user.hasRight(User.ALL_RIGHTS)) return "error";
		binding.setVariable("user", user);
		binding.setVariable( "log", log );
		String head = "import gr.ntua.ivml.athena.db.DB\n";
		PrintStream originalOut = System.out;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		GroovyShell shell = new GroovyShell(binding);
		try {
			System.setOut(new PrintStream( buffer));
			Object o = shell.evaluate(head+ script);
			result = (o==null?null:o.toString());
		} catch( Exception e ) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter( sw );
			e.printStackTrace( pw );
			result = sw.toString();
		} finally {
			System.setOut(originalOut);
			stdOut= buffer.toString();
		}
		return "success";
	}
	
	public String getScript() {
		return script;
	}
	
	public void setScript( String script ) {
		this.script = script;
	}
	
	@Action("Script_input")
	@Override
	public String input() throws Exception {
		return super.input();
	}
	
	public Map<String, String> getLib() {
		// open dir
		// read each file
		// extract first comment line 
		// add map comment and filename
	
		if( library != null ) return library;
		try {
			File dir = new File( Config.getRealPath("WEB-INF/script_library"));
			library = new HashMap<String,String>();
			
			for( File script: dir.listFiles()) {
				String content = FileUtils.readFileToString( script , "UTF-8");
				Matcher m = Pattern.compile("^//[\\s]*(.*)$", Pattern.MULTILINE).matcher(content);
				if( m.find()) {
					library.put( script.getAbsolutePath(), m.group(1));
				}
			}
		} catch( Exception e ) {
			log.error( "Script library reading failed", e );
		}
		return library;
	}
	
	public String getScriptlet() {
		return scriptlet;
	}
	
	public void setScriptlet( String value ) {
		this.scriptlet = value;
	}
}
