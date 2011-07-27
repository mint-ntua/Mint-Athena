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

package gr.ntua.ivml.athena.persistent;

import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.xml.Statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XmlObject {
	Long dbID;

	public Long getDbID() {
		return dbID;
	}

	public void setDbID(Long dbID) {
		this.dbID = dbID;
	}
	
	public XpathHolder getRoot() {
		return DB.getXpathHolderDAO().getRoot(this);
	}
	
	public Statistics getStats() {
		return new Statistics( this );
	}
	
	/**
	 * list all the namespaces and prefixes that were used in the xml.
	 * The empty prefix may appear twice, once for the 'default' namespace
	 * if one is used and once for <no namespace>
	 * @return
	 */
	public List<String[]> listUriAndPrefix() {
		List<Object[]> l = DB.getXpathHolderDAO().listNamespaces(this);
		List<String[]> result = new ArrayList<String[]>( l.size());
		for( Object[] oa: l ) {
			String[] s2 = new String[2];
			s2[0] = (oa[0]==null?"":oa[0].toString().trim());
			s2[1] = (oa[1]==null?"":oa[1].toString().trim());
			result.add( s2 );
		}
		return result;
	}
	
	public Collection<String> listNamespaces() {
		Set<String> uris = new HashSet<String>();
		List<Object[]> l = DB.getXpathHolderDAO().listNamespaces(this);
		for( Object[] oa: l ) {
			String uri = (oa[0]==null?"":oa[0].toString().trim());
			uris.add( uri );
		}
		return uris;
	}
	
	public List<XpathHolder> getByNamespace( String uri ) {
		return DB.getXpathHolderDAO().getByUri(this, uri);
	}
	
	/**
	 * Key is xpath dbID value is avg length and count distinct
	 * @return
	 */
	public Map<Long, Object[]> getAllStats() {
		Map<Long, Object[]> stats = DB.getXMLNodeDAO().getStatsForXpaths(this);
		return stats;
	}

}
