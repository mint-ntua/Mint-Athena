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

package gr.ntua.ivml.athena.xml.util;

import java.util.HashMap;
import java.util.Map;

public class Namespaces {

	 
    Map<String,String> namespaces;
    
    public Namespaces() {
        this.namespaces = new HashMap<String,String>();
    }
    
    public Namespaces(Map<String,String> n) {
        this.namespaces = n;
    }
    
    public String getNamespacePrefix(String uri, String qname, int type) {
        
        String prefix = XPathUtils.getNamespacePrefix(qname);
        if (!(type == 0 && "".equals(prefix))) {
            String namespaceURI = (String) namespaces.get(prefix);
            if (namespaceURI != null) {
                if (!namespaceURI.equals(uri)) {
                    int i = 0;
                    while (true) {
                        String newPrefix = prefix + i++;
                        namespaceURI = (String) namespaces.get(newPrefix);
                        if (namespaceURI == null) {
                            namespaces.put(newPrefix, uri);
                            prefix = newPrefix;
                            break;
                        } else if (namespaceURI.equals(uri)) {
                            prefix = newPrefix;
                            break;
                        }
                    }
                }
            } else {
                namespaces.put(prefix, uri);
            }
        }
        return prefix;
    }
}
