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


import gr.ntua.ivml.athena.persistent.XmlObject;

public class XmlObjectDAO extends DAO<XmlObject, Long> {
	public XmlObject findByNodeId( Long nodeId ) {
		XmlObject result = (XmlObject) getSession().createSQLQuery( "select xml_object_id from xml_node_master where xml_node_id = :node")
		.addEntity( XmlObject.class)	
		.setLong("node", nodeId )
		.uniqueResult();
		return result;
	}

}
