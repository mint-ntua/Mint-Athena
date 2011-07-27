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

import gr.ntua.ivml.athena.persistent.XMLNode;
import gr.ntua.ivml.athena.persistent.XmlObject;
import gr.ntua.ivml.athena.persistent.XpathHolder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.hibernate.ScrollableResults;
import org.hibernate.StatelessSession;
import org.xml.sax.ContentHandler;

public class XMLNodeDAO extends DAO<XMLNode, Long> {

	/**
	 * suffers from long time skipping to the offset "from", has to sort everything
	 * and then start skipping nodes.
	 * @param xp
	 * @param from
	 * @param count
	 * @return
	 */
	public List<XMLNode> getByXpathHolder( XpathHolder xp, long from, long count ) {
		return getSession().createSQLQuery( "select * from xml_node_" +
				xp.getXmlObject().getDbID() +
				" where xpath_summary_id = :xpath and xml_object_id = :xo " +
				"order by xml_node_id asc")
			.addEntity( XMLNode.class )
			.setEntity("xpath", xp)
			.setEntity( "xo", xp.getXmlObject())
			.setMaxResults((int)count)
			.setFirstResult((int)from)
			.list();
	}
	
	/**
	 * Maybe this performs better, pass the last node you were working on and this
	 * should be quick.
	 * @param xp
	 * @param start
	 * @param count
	 * @return
	 */
	public List<XMLNode> getByXpathHolder( XpathHolder xp, XMLNode start, long count ) {
		return getSession().createSQLQuery( "select * from xml_node_" +
				xp.getXmlObject().getDbID() +
				" where xpath_summary_id = :xpath and xml_object_id = :xo " +
				" and xml_node_id>:nodeId" +
				" order by xml_node_id asc")
			.addEntity( XMLNode.class )
			.setEntity("xpath", xp)
			.setEntity( "xo", xp.getXmlObject())
			.setLong("nodeId", start.getNodeId())
			.setMaxResults((int)count)
			.list();
	}
	
	/**
	 * The normal getById performs very badly, provide the xmlObject to find the
	 * node much quicker.
	 * @param id
	 * @param obj
	 * @return
	 */
	public XMLNode getByIdObject( XmlObject obj, Long id ) {
		return (XMLNode) getSession().createSQLQuery( "select * from xml_node_" +
				obj.getDbID() +
				" where xml_node_id = :nodeId and xml_object_id = :xo " )
			.addEntity( XMLNode.class )
			.setEntity( "xo", obj )
			.setLong("nodeId", id )
			.uniqueResult();
	}
	
	/**
	 * The normal getById performs very badly, provide the xmlObject to find the
	 * node much quicker.
	 * @param id
	 * @param obj
	 * @return
	 */
	public XMLNode getByIdObject( Long xmlObj, Long id ) {
		return (XMLNode) getSession().createSQLQuery( "select * from xml_node_" +
				xmlObj +
				" where xml_node_id = :nodeId and xml_object_id = :xo " )
			.addEntity( XMLNode.class )
			.setLong( "xo", xmlObj )
			.setLong("nodeId", id )
			.uniqueResult();
	}
	
	

	/**
	 * Looks at content for simple nodes and checksums for subtrees.
	 * @param xp
	 * @return
	 */
	public long countDistinct( XpathHolder xp ) {
		Long val;
		if( xp.name.equals("text()") || xp.name.startsWith("@"))
				val = (Long)  getSession()
				.createQuery( "select count( distinct content ) from XMLNode where xpathHolder = :xpath and xmlObject = :xo")
				.setEntity("xpath", xp)
				.setEntity("xo", xp.getXmlObject())
				.uniqueResult();
		else
			val = (Long)  getSession()
			.createQuery( "select count( distinct checksum ) from XMLNode where xpathHolder = :xpath and xmlObject = :xo ")
			.setEntity("xpath", xp)
			.setEntity("xo", xp.getXmlObject())
			.uniqueResult();
			
		return val.longValue();
	}
	
	
	/**
	 * This version merges all elements of the same name, regardless where they appear.
	 * Counting really only works for attributes and text() nodes.
	 * @param xo
	 * @param elementName
	 * @return
	 */
	public long countDistinct( XmlObject xo, String elementName ) {
		Long val;
		if( elementName.startsWith("@")) {
			val = (Long)  getSession()
			.createQuery( "select count( distinct content ) from XMLNode " + 
						"where xmlObject = :xo and xpathHolder.name = :name " )
			.setEntity("xo", xo )
			.setString( "name", elementName )
			.uniqueResult();
		} else {
			val = (Long)  getSession()
			.createQuery( "select count( distinct content ) from XMLNode " + 
					"where xmlObject = :xo and xpathHolder.parent.name = :name " + 
					"and xpathHolder.name = 'text()'")
			.setEntity("xo", xo )
			.setString( "name", elementName )
			.uniqueResult();
	}
		return val.longValue();
	}
	

	/**
	 * This version merges all elements of the same name, regardless where they appear.
	 * Counting really only works for attributes and text() nodes.
	 * @param xo
	 * @param elementName
	 * @return
	 */
	public float getAvgLength( XmlObject xo, String elementName ) {
		Double val;
		log.debug( "start query elem='"+elementName+"' "+xo.getDbID() );
		if( elementName.startsWith("@")) {
			val = (Double)  getSession()
			.createQuery( "select avg( length (content) ) from XMLNode " + 
						"where xmlObject = :xo and xpathHolder.name = :name " )
			.setEntity("xo", xo )
			.setString( "name", elementName )
			.uniqueResult();
		} else {
			val = (Double)  getSession()
			.createQuery( "select avg(length( content )) from XMLNode " + 
					"where xmlObject = :xo and xpathHolder.parent.name = :name " + 
					"and xpathHolder.name = 'text()'")
			.setEntity("xo", xo )
			.setString( "name", elementName )
			.uniqueResult();
		}
		log.debug( "end query");
		return val.floatValue();
	}
	

	
	/**
	 * Find at most limit nodes with the same content.
	 * @param xp
	 * @param limit
	 * @return
	 */
	public List<XMLNode> getDuplicates( XpathHolder xp, int limit ) {
		return Collections.emptyList();
	}
	
	
	/**
	 * Given name and xmlObject, make a hashmap for each value and how many distinct
	 * appearances it has in that element / attribute. Since element is non
	 * unique, merges elements/ attributes from different parts of the schema with
	 * the same name (eg "name", "type"(element) or "@type"(attribute) )
	 * @param xo
	 * @param elementName
	 * @param limit
	 * @return
	 */
	public Map<String, Integer> getCountByValue( XmlObject xo, String elementName, int limit ) {
		List<Object[]> l;
		if( elementName.startsWith("@")) {
			l = (List<Object[]>) getSession()
			.createQuery( "select content, count(*) from XMLNode " + 
						"where xmlObject = :xo and xpathHolder.name = :name group by content" )
			.setEntity("xo", xo )
			.setString( "name", elementName )
			.setMaxResults(limit)
			.list();
		} else {
			l = (List<Object[]>) getSession()
			.createQuery( "select content, count( * ) from XMLNode " + 
					"where xmlObject = :xo and xpathHolder.parent.name = :name " + 
					"and xpathHolder.name = 'text()' group by content")
			.setEntity("xo", xo )
			.setString( "name", elementName )
			.setMaxResults(limit)
			.list();
		}
		
			Map<String, Integer> res = new HashMap<String,Integer>();
			for( Object[] objArr: l ) {
				if( objArr[0] != null )
					res.put( objArr[0].toString().trim(), ((Long) objArr[1]).intValue());
			}
			return res;
	}

	/**
	 * Ordered List of value, frequency for given xpath. Only given number of 
	 * values listed.
	 * @param xp
	 * @param limit
	 * @return
	 */
	public List<Object[]> getCountByValue( XpathHolder xp, int limit ) {
		if( xp.name.equals("text()") || xp.name.startsWith("@")) {
			List<Object[]> l = (List<Object[]>) getSession()
			.createQuery( "select content, count( * ) " + 
						"from XMLNode where xpathHolder = :xpath " + 
						"and xmlObject = :xo " +
						"group by content " +
						"order by count(*) desc")
			.setEntity("xpath", xp)
			.setEntity("xo", xp.getXmlObject())
			.setMaxResults(limit)
			.list();
			return l;
		} else 
			return Collections.emptyList();
	}
	
	/**
	 * Quickly build a dom tree. Use per item, not for trees with more than 
	 * 2000 nodes (or about that). The XMLNodes are not in the Hibernate session 
	 * and don't lazy load their parent or anything else. They should not need to, though.
	 * The attached XpathHolders are in the session and behave normally.
	 * @param parent
	 * @return
	 */
	public XMLNode getDOMTree( XMLNode parent ) {
		StatelessSession ss = DB.getStatelessSession();
		List<XMLNode> l;
		int maxNodes = 10000;
		if( parent.getSize()<maxNodes) maxNodes = (int)  parent.getSize();
		Stack<XMLNode> stack = new Stack<XMLNode>();
		HashMap<Long, XpathHolder> xpathCache = new HashMap<Long, XpathHolder>();
		l =  ss.createSQLQuery("select * from xml_node_" +
				parent.getXmlObject().getDbID() + 
		" where xml_node_id >= :parentId order by xml_node_id" )
		.addEntity(XMLNode.class )
		.setLong("parentId", parent.getNodeId() )
		.setMaxResults(maxNodes)
		.list();
		// now every node has the wrong parent and XpathHolder and no Children..
		for( XMLNode x: l ) {
			// find the right place in stack
			x.setChildren( new ArrayList<XMLNode>());
			while( !stack.isEmpty() ) {
				if( stack.peek().getNodeId() != x.getParentNode().getNodeId())
					stack.pop();
				else
					break;
			}
			if( !stack.isEmpty()) {
				x.setParentNode(stack.peek());
				stack.peek().getChildren().add( x );
			}
			stack.push(x);
			// now the xpathholder
			XpathHolder path = xpathCache.get(x.getXpathHolder().getDbID());
			if( path == null ) {
				path = DB.getXpathHolderDAO().findById(x.getXpathHolder().getDbID(), false);
				xpathCache.put( path.getDbID(), path);
			}
			x.setXpathHolder(path);
			x.setXmlObject(parent.getXmlObject());
		}
		if( l.size() > 0 ) return l.get(0);
		else return null;
	}

	/**
	 * Retrieve the given node with subtree and all parent elements. No actual value wrapping is done in 
	 * this method! Only empty element wrapping to adjust for straight xpaths.
	 * @param parent
	 * @return
	 */
	public XMLNode wrappedDOMTree( XMLNode parent ) {
		XMLNode result = getDOMTree(parent);
		do {
			XpathHolder parentPath = result.getXpathHolder().getParent();
			if( parentPath == null ) break; //shouldnt happen
			if( parentPath.getParent() == null ) break; // this should happen
			XMLNode newParent = new XMLNode();
			newParent.getChildren().add( result );
			result.setParentNode(newParent);
			newParent.setXmlObject(result.getXmlObject());
			newParent.setXpathHolder(parentPath);
			result = newParent;
		} while( true );
		return result;
	}
	/**
	 * Very specifically get other siblings of the parent that have other xpaths, not the same as this
	 * @param tree
	 * @return
	 */
	public List<XMLNode> quickOtherSiblings( XMLNode tree ) {
		List<XMLNode> l = Collections.emptyList();
		try {
			l = getSession().createSQLQuery("select * from xml_node_" +
					tree.getXmlObject().getDbID() + 
					" where parent_node_id = :parent" + 
					" and (xpath_summary_id != :path " +
					" or xml_node_id = :id ) " + 
					"order by xml_node_id" )
			.addEntity(XMLNode.class )
			.setEntity("parent", tree.getParentNode() )
			.setEntity( "path", tree.getXpathHolder() )
			.setEntity( "id", tree )
			.list();
		} catch( Exception e ) {
			log.error( "Query failed with ", e );
		}
		return l;
	}
	
	
	public float getAvgLength(XpathHolder xpathHolder) {
		Double val;
		if( xpathHolder == null ) return -1f;
		if( ! ( xpathHolder.isTextNode() || xpathHolder.isAttributeNode())) {
			xpathHolder = xpathHolder.getTextNode();
			if( xpathHolder == null ) return -1f;			
		}
		val = (Double)  getSession()
		.createQuery( "select avg( length (content) ) from XMLNode " + 
		"where xmlObject = :xo and xpathHolder = :xp " )
		.setEntity("xo", xpathHolder.getXmlObject() )
		.setEntity( "xp", xpathHolder )
		.uniqueResult();
		return val.floatValue();
	}

	public Map<Long, Object[]> getStatsForXpaths( XmlObject xo ) {
		List<Object[]> queryResult = getSession()
			.createSQLQuery( "select xpath_summary_id, avg( length( content )), count( distinct content )" +
					" from xml_node_" + xo.getDbID() + " group by xpath_summary_id")
			.list();
		Map<Long, Object[]> result = new HashMap<Long, Object[]>();
		for( Object[] oa: queryResult ) {
			if( oa[0] == null ) continue;
			Long xp = ((Integer) oa[0]).longValue();
			BigDecimal f = (BigDecimal) oa[1];
			BigInteger count = (BigInteger) oa[2];
			Object[] val = new Object[2];
			val[0] = (f==null?-1f:f.floatValue());
			val[1] = (count==null?-1l:count.longValue());
			result.put( xp, val);
		}
		return result;
	}
	
	public void serialize( XmlObject xml, ContentHandler ch ) {
		ScrollableResults nodes = getSession()
			.createQuery( "from XMLNode where xmlObject=:xo order by xml_node_id" )
			// and other conditions like deleted nodes or other stuff
			.scroll();
	}
}
