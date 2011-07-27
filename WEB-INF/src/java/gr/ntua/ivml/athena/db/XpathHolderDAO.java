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

package gr.ntua.ivml.athena.db;

import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.XmlObject;
import gr.ntua.ivml.athena.persistent.XpathHolder;

import java.util.HashMap;
import java.util.List;

public class XpathHolderDAO extends DAO<XpathHolder, Long> {

	public XpathHolder getRoot( DataUpload du ) {
		return (XpathHolder) getSession().createQuery( " from XpathHolder where xmlObject = :xo and parent = null")
			.setEntity("xo", du.getXmlObject())
			.uniqueResult();
	}
	
	public XpathHolder getRoot(XmlObject xo ) {
		return (XpathHolder) getSession().createQuery( " from XpathHolder where xmlObject = :xo and parent = null")
			.setEntity("xo", xo )
			.uniqueResult();
	}

	public XpathHolder getByRelativePath( XpathHolder root, String path ) {
		return (XpathHolder) getSession().createQuery( " from XpathHolder where xpath = :x and xmlObject = :xo")
		.setEntity("xo", root.getXmlObject() )
		.setString( "x", root.getXpath()+path)
		.uniqueResult();
	}

	/**
	 * List of used namespaces and their prefix
	 */
	public List<Object[]> listNamespaces( XmlObject xo ) {
	       List<Object[]> result = DB.getSession()
       	.createQuery( "select uriPrefix, uri " + 
       					"from XpathHolder " + 
       					"where xmlObject = :xo  " +
       					" and uri is not null " +
       					"group by uriPrefix,uri" )
       	.setEntity("xo", xo)
       	.list();
	       return result;
	}
	
	/**
	 * Get names of Elements for given namespace prefix
	 * @param xo
	 * @param namespace
	 * @return
	 */
	public List<String> getElementsByNamespace( XmlObject xo, String namespacePrefix ) {
	        List<String> result = DB.getSession()
	        	.createQuery( "select name from XpathHolder where xmlObject = :xo and uriPrefix = :uri group by name")
	    	.setEntity("xo", xo)
	    	.setString( "uri", namespacePrefix )
	    	.list();
	        return result;
	}
	
	public List<String> getElementsByNamespaceUri( XmlObject xo, String uri ) {
		if( uri == null ) uri = "";
		
        List<String> result = DB.getSession()
        	.createQuery( "select name from XpathHolder where xmlObject = :xo " + 
        			"and uri = :uri and name!='text()' and substring(name,1,1) != '@' group by name")
    	.setEntity("xo", xo)
    	.setString( "uri", uri )
    	.list();
        return result;
}

	public List<XpathHolder> getByName( XmlObject xo, String name ) {
		List<XpathHolder> result = DB.getSession()
			.createQuery( "from XpathHolder where xmlObject = :xo and name = :name")
			.setEntity("xo", xo)
			.setString( "name", name )
			.list();
		return result;
	}
	
	/**
	 * Get the xpaths that belong to the given Uri and XmlObject.
	 * 
	 * @param xo
	 * @param uri
	 * @return
	 */
	public List<XpathHolder> getByUri( XmlObject xo, String uri ) {
		List<XpathHolder> result = DB.getSession()
		.createQuery( "from XpathHolder where xmlObject = :xo and uri = :uri")
		.setEntity("xo", xo)
		.setString( "uri", uri )
		.list();
	return result;
		
	}
}
