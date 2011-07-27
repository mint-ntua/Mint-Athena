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


import gr.ntua.ivml.athena.xml.util.XPathUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.xml.sax.SAXException;

public class UniqueXPathHandler extends Handler{
	
	//private LinkedHashMap<String, String> res;
	private ArrayList<Node> rootNodes;
	public UniqueXPathHandler(boolean t, ArrayList<Node> roots){
		super(t);
		this.rootNodes = roots;
	}
	
	@Override
    public void attribute(String uri, String name, String qname, String value) {       
            cursor = cursor.descend(uri, name, qname, 0);
            //res.put(this.cursor.getPath(), value);
            //res.add(new ElementValueMap(this.cursor.getPath(), value));
            cursor = cursor.ascend();    
    }

    public void endElement(String uri, String name, String qname) throws SAXException {
        String value = getText();
        //res.put(this.cursor.getPath(), value);
        if(XPathUtils.getDepth(this.cursor.getPath()) == 1){
        	this.rootNodes.add(this.cursor);
        	//System.out.println(this.cursor.getPath());
        }
        /*if(value.compareTo("") != 0){
        	res.add(new ElementValueMap(this.cursor.getPath(), value));
        }*/
        cursor = cursor.ascend();
    }
}
