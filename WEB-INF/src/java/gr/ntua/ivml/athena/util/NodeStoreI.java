package gr.ntua.ivml.athena.util;

import gr.ntua.ivml.athena.persistent.XMLNode;
/**
 * This interface is used by the SAX event handler XMLDbHandler. Used to create Ids for the
 * Nodes and store them.
 * 
 * @author Arne Stabenau 
 *
 */
public interface NodeStoreI {
	public void store( XMLNode n ) throws Exception;

	/**
	 * Allocating node ids in packs of 1000. The sequence will support this.
	 * Whoever has x000 can use ids x000 until x999.
	 * @return
	 */
	public long[] newIds();

}

