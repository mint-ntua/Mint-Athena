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
