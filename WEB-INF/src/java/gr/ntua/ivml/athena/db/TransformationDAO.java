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
import gr.ntua.ivml.athena.persistent.Transformation;
import gr.ntua.ivml.athena.persistent.XmlObject;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class TransformationDAO extends DAO<Transformation, Long> {
	public static final Logger log = Logger.getLogger(TransformationDAO.class);
	static {
		Session ss = DB.getSession();
		ss.beginTransaction();
		try {
		List<Transformation> l = ss.createQuery( "from Transformation where statusCode != :ok and statusCode != :err")
			.setInteger("ok", Transformation.OK)
			.setInteger("err", Transformation.ERROR)
			.list();
		int count = 0;
		for( Transformation t: l ) {
			t.setStatusCode(t.ERROR);
			t.setStatusMessage("Failed due to server restart!");
			count+=1;
			DB.commit();
		}
		log.info( "Failed " + count + " Transformations due to restart.");
		} catch( Exception e ) {
			log.error( "Exception in Transformation failing", e );
		} finally {
			DB.closeSession();
		}
	}
	
	public List<Transformation> findByUpload( DataUpload du ) {
		return getSession().createQuery("from Transformation where dataUpload=:du")
		.setEntity("du", du)
		.list();
	}

	public Transformation findByXmlObject(XmlObject xo) {
		return (Transformation) getSession().createQuery( "from Transformation where parsedOutput = :xo ")
		.setEntity("xo", xo)
		.uniqueResult();
	}
}
