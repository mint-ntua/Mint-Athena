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

package gr.ntua.ivml.athena.xml;

import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.XpathHolder;
import gr.ntua.ivml.athena.util.TraversableI;
import gr.ntua.ivml.athena.xml.Handler.Node;
import gr.ntua.ivml.athena.xml.util.ElementValueMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class TreeGenerationParser {
	private SAXParserFactory factory;
	private UniqueXPathHandler handler;
	private SAXParser parser;
	private int counter;
	private ArrayList<ElementValueMap> res;
    private String treeId = "";
	
	
	public TreeGenerationParser(){
		counter = 0;
		factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
        factory.setValidating(false);
       try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	public String parse(File file, String treeId){
		this.treeId = treeId;
		this.counter = 0;
		StringBuffer res = new StringBuffer();
		res.append( "<div id=\"treemenu_" + treeId +"\">" );
		ArrayList<Node> roots = new ArrayList<Node>();
		handler = new UniqueXPathHandler(true, roots);
		try {
			parser.parse(file, handler);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		traverse( roots, res );
		
		res.append( "</div>" );
		return res.toString();
	}
	
	public ArrayList<Node> parseElements(File file) {
		ArrayList<Node> roots = new ArrayList<Node>();
		handler = new UniqueXPathHandler(true, roots);
		try {
			parser.parse(file, handler);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return roots;
	}
	
	public String parseUpload( DataUpload du ) {
		this.treeId = "1";
		this.counter = 0;
		StringBuffer res = new StringBuffer();
		res.append( "<div id=\"treemenu_" + treeId +"\">" );

		traverseXpathHolder( du.getRootXpath().getChildren(), res );
		res.append( "</div>" );
		return res.toString();
		
	}
	
	
	public int getElementCount(){
		return this.counter;
	}
	
	private void traverse(List<? extends TraversableI> children, StringBuffer output ){
		if( children.isEmpty()) return;
		// TODO: sort the children, attributes first
		output.append("<ul>" );
		for( TraversableI t: children ) {
			Node tmpNode = (Node) t; 
			this.counter++;
			String divName = tmpNode.getName();
			String className = "xmlelement";
			if(divName.startsWith("@")) {
				className = "xmlattribute";
			}
			output.append( 
			 "<li id=\"node_" + counter +
			 "\"> <div id=\"tree_" + this.treeId +
			 "_node_" + counter +"\" class=\"" + className + 
			 "\">" + divName + "</div>" );
			traverse( t.getChildren(), output );
			output.append( "</li>\n" );
		}
		output.append( "</ul>\n" );
	}


	private void traverseXpathHolder(List<? extends TraversableI> children, StringBuffer output ){
		if( children.isEmpty()) return;
		// TODO: sort the children, attributes first
		output.append("<ul>" );
		for( TraversableI t: children ) {
			XpathHolder xp = (XpathHolder) t;
			this.counter++;
			String divName = xp.getName();
			String className = "xmlelement";
			if(divName.startsWith("@")) {
				className = "xmlattribute";
			}
			output.append( 
			 "<li id=\"node_" + counter +
			 "\"> <div id=\"tree_" + this.treeId +
			 "_node_" + counter +"\" class=\"" + className + 
			 "\">" + divName + "</div>"  );
			traverseXpathHolder( t.getChildren(), output );
			output.append( "</li>\n" );
		}
		output.append( "</ul>\n" );
	}
}
