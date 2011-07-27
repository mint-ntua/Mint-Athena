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


import gr.ntua.ivml.athena.persistent.Organization;
import gr.ntua.ivml.athena.persistent.Publication;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;

public class PublicationDAO extends DAO<Publication, Long> {
	public static final Logger log = Logger.getLogger( PublicationDAO.class );
	
	static {
		Session ss = DB.getSession();
		try {
			ss.beginTransaction();
			List<Publication> l = ss.createQuery( "from Publication where statusCode != :ok and statusCode != :err")
			.setInteger("ok", Publication.OK)
			.setInteger("err", Publication.ERROR)
			.list();
			int count = 0;
			for(Publication p: l ) {
				p.setStatusCode(Publication.ERROR);
				p.setStatusMessage("Failed due to server restart!");
				p.getInputUploads().clear();
				count+=1;
				DB.commit();
			}
			log.info( "Failed " + count + " Publications due to restart.");
		} catch( Exception e ) {
			log.error( "Exception in Publication failing", e );
		} finally {
			DB.closeSession();
		}
	}

	public Publication findByOrganization( Organization org ) {
		return (Publication) getSession().createQuery( "from Publication where publishingOrganization=:org")
			.setEntity("org", org)
			.uniqueResult();
	}
	 
}
