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

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;

import javax.xml.transform.stream.*;

import java.io.*;


import net.sf.saxon.FeatureKeys;


import org.xml.sax.*;

import org.w3c.dom.*;




public class XSLTransform {
	public String transform(String xml, String xsl) throws TransformerException {
		String result = "";
		System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
	
	    StringWriter out = new StringWriter();

	    
	    TransformerFactory tFactory = TransformerFactory.newInstance();
	   
	    tFactory.setAttribute( FeatureKeys.DTD_VALIDATION, false );
	    
	    StreamSource xmlSource = new StreamSource(new StringReader(xml));
	    StreamSource xslSource = new StreamSource(new StringReader(xsl));
	    StreamResult xmlResult = new StreamResult(out);
	    
	    Transformer transformer = tFactory.newTransformer(xslSource);
	    
	      transformer.transform(xmlSource, xmlResult);
	    result = out.toString();
		
		return result;
	}

	
	/**
	 * Alternative method of transformation. Needed for big files! Don't want to have them
	 * in Strings. 
	 * @param xml
	 * @param xsl
	 * @param out
	 * @throws TransformerException
	 */
	public void transformStream(InputStream xml, String xsl,OutputStream out) throws TransformerException {

		TransformerFactory tFactory = net.sf.saxon.TransformerFactoryImpl.newInstance();
	    StreamSource xmlSource = new StreamSource(xml);
	    StreamSource xslSource = new StreamSource(new StringReader(xsl));
	    StreamResult xmlResult = new StreamResult(out);
	    
	    Transformer transformer = tFactory.newTransformer(xslSource);
	    transformer.transform(xmlSource, xmlResult);
	}
	
	//using DOM, disabling validation
	
	public void transform(InputStream xml, String xsl,OutputStream out ) throws Exception {
		System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setAttribute("http://xml.org/sax/features/namespaces", true);
        factory.setAttribute("http://xml.org/sax/features/validation", false);
        factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        factory.setNamespaceAware(true);
        factory.setIgnoringElementContentWhitespace(false);
        factory.setIgnoringComments(false);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(xml));

        Source source = new DOMSource(document);

		TransformerFactory tFactory = net.sf.saxon.TransformerFactoryImpl.newInstance();
        

	    StreamSource xslSource = new StreamSource(new StringReader(xsl));
	    StreamResult xmlResult = new StreamResult(out);
	
	    
	    Transformer transformer = tFactory.newTransformer(xslSource);
	    transformer.transform(source, xmlResult);
	}
	
	
	 
}
