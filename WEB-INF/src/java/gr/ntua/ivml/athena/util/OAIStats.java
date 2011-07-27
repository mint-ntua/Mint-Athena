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

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import gr.ntua.ivml.athena.xml.FullBean;

public class OAIStats {

	private Connection conn = null;
	private static String username = "videoactive";
	private static String password = "videoactive";
	private static String url = "jdbc:mysql://147.102.11.37/vaoai";
	private   PreparedStatement s;

	public OAIStats(){
			try {
				Class.forName ("com.mysql.jdbc.Driver").newInstance();
				conn = DriverManager.getConnection (url, username, password);	
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
			} catch (SQLException e) {
				e.printStackTrace();
			}

	}
	
	public int findEseByOrg(String orgname){
		int esenum=0;
		ResultSet rs = null;
	  try{
		s = conn.prepareStatement("SELECT count(*) FROM athenaoai_records where oai_set=?");
		s.setString(1, orgname);
		rs=s.executeQuery();
		 while (rs.next()) {
		        esenum += rs.getInt(1);
		       }
		s.close();
	  } 
	  catch (SQLException e) {
			 e.printStackTrace();
	  } 
	  return esenum; 
	}
	
}
