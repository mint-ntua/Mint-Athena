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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.util.ServletContextAware;

import net.sf.json.JSONObject;
import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.XMLNode;
import gr.ntua.ivml.athena.util.Config;
import gr.ntua.ivml.athena.xml.SchemaValidator;

public class XMLNodeTransform{
	
	//private ServletContext sc;
	public JSONObject previewDataUploadTransformByMapping(DataUpload du, String mappings) {
		List<XMLNode> list = du.getItemXpath().getNodes(0, 1);
		if(list != null && !list.isEmpty()) {
			XMLNode node = list.get(0);
			return this.transformByMapping(du, node, mappings);
		} else {
			return new JSONObject().element("error", "Could not get xml nodes from dataupload");
		}
	}
	
	public JSONObject transformByMapping(Long duid, Long nodeid, String mappings) {
		DataUpload du = DB.getDataUploadDAO().getById(duid, false);
		XMLNode node = DB.getXMLNodeDAO().getById(nodeid, false);
		
		return this.transformByMapping(du, node, mappings);
	}

	public JSONObject transformByMapping(DataUpload du, XMLNode node, String mappings) {
		JSONObject object = new JSONObject();
		String error = null;
		String input = "";
		String xsl = "";
		String output = "";
		String eseXml="";
		String lidovalidation="";
		String esevalidation="";
		StringWriter xmlWriter = new StringWriter();
		
		//XMLNode treeNode= DB.getXMLNodeDAO().getDOMTree( node );
		//treeNode.toXml(new PrintWriter(xmlWriter));
		node.toXmlWrapped(new PrintWriter(xmlWriter));
		//node.toXml(new PrintWriter(xmlWriter));

		input = xmlWriter.toString();	
		input = input.replaceFirst("xmlns=\"[^\"]*\"", "");	
		input = XMLFormatter.format(input);
		
		XSLTGenerator xslt = new XSLTGenerator();
		XSLTransform t = new XSLTransform();
		
		xslt.setItemLevel(du.getItemXpath().getXpathWithPrefix(true));
		xslt.setTemplateMatch(node.getXpathHolder().getXpathWithPrefix(true));
		//xslt.setNamespaces(node.getXpathHolder().getNamespaces(true));
		xsl = xslt.generateFromString(mappings);
		try {
			xsl = XMLFormatter.format(xsl);
		} catch(Exception e) {			
		}
		
		try {
			output = t.transform(input, xsl);
			output = XMLFormatter.format(output);
			//do ese and lido validations here
			byte currentXMLBytes[] = output.getBytes();
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(currentXMLBytes); 
			StreamSource lidoXml = new StreamSource( byteArrayInputStream);
			
			try{
				SchemaValidator.validate(lidoXml, SchemaValidator.LIDO );
				lidovalidation="Successfull LIDO 0.9 validation!";
			} catch (Exception ex){
				lidovalidation=ex.getMessage();
			}
			
			//now ese
			
			
			StringBuilder eseContents = new StringBuilder();
			String ese="";
	   		try {
	   			File eseFile = new File(Config.getRealPath(Config.get( "lido_to_ese_xsl")));
				BufferedReader inputese =  new BufferedReader(new FileReader(eseFile));
	      		try {
	      			String line = null;
	      			while (( line = inputese.readLine()) != null){	    	
						eseContents.append(line);
						eseContents.append(System.getProperty("line.separator"));
	      			}
		   		} finally {
		   			inputese.close();
	      		}
	   		} catch (Exception ex){
	   			ex.printStackTrace();
	   			esevalidation="Ese output could not be built";	
			}
	    	
		    	ese=eseContents.toString();
		    	eseXml = t.transform(output, ese);
		    	eseXml = XMLFormatter.format(eseXml);
		    	if(eseXml.length()>0){
			    	byte eseXMLBytes[] = eseXml.getBytes();
					byteArrayInputStream = new ByteArrayInputStream(eseXMLBytes); 
					
					StreamSource eseXmlSrc = new StreamSource( byteArrayInputStream);
					try{
					  SchemaValidator.validate(eseXmlSrc, SchemaValidator.ESE );
					  esevalidation="Successfull ESE validation!";
					}
					catch (Exception ex){
					  esevalidation=ex.getMessage();
					}
		    	}				
		} catch(Exception e) {
			StringWriter result = new StringWriter();
	    		PrintWriter printWriter = new PrintWriter(result);
	    		e.printStackTrace(printWriter);
	    		error = result.toString();
	    		object = object.element("error", error);
		}
		
		input = StringEscapeUtils.escapeHtml(input);
		xsl = StringEscapeUtils.escapeHtml(xsl);
		output = StringEscapeUtils.escapeHtml(output);
		object = object.element("input", input);
		object = object.element("xsl", xsl);
		object = object.element("output", output);
		object = object.element("eseXml", eseXml);
		object = object.element("lidovalidation", lidovalidation);
		object = object.element("esevalidation", esevalidation);
		
		return object;
	}

}
