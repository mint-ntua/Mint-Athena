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

package gr.ntua.ivml.athena.util;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ConnectionTester;

public class ConnectionCheckoutLog implements ConnectionTester {
	public static final Logger log = Logger.getLogger( ConnectionCheckoutLog.class);
	
	@Override
	public int activeCheckConnection(Connection arg0) {
		// TODO Auto-generated method stub
		log.info( "Checkout occured");
		if( log.isDebugEnabled()) {
			Exception e= new Exception();
			e.fillInStackTrace();
			log.debug( "Trace\n" + StringUtils.filteredStackTrace(e, "gr.ntua.ivml.athena"));
		}
		return CONNECTION_IS_OKAY;
	}

	@Override
	public int statusOnException(Connection arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		return CONNECTION_IS_INVALID;
	}

}
