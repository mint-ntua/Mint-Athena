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

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoStats {
	private Mongo mon = null;
	private DB db = null;
	private DBCollection coll = null;
	private BasicDBObject stats = null;
	
	public MongoStats(){
		try {
			this.mon = new Mongo("oreo.image.ntua.gr", 27017);
			this.db = mon.getDB("athena");
			this.coll = this.db.getCollection("registry");
			BasicDBObject doc = new BasicDBObject();
			doc.put("type", "globalStats");
			this.stats = (BasicDBObject)this.coll.findOne(doc);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
	
	public int findEseByOrg(String orgname){
		int res = -1;
		res = this.getValueFromRegistryGlobalStats(orgname);
		return res;
	}
	
	private String removeDots(String value){ return value.replace(".", "");}
	
	public int getValueFromRegistryGlobalStats(String org){
		int res = -1;
		if(this.stats.get(this.removeDots(org)) != null){
			res = this.stats.getInt(this.removeDots(org));
		}else{
			res = 0;
		}
		return res;
	}
	
	private int getOrgSetSize(String org){
		DBCursor cur = null;
		BasicDBObject doc = new BasicDBObject();
		doc.put("SetSpec", org);
		cur = this.coll.find(doc);
		return cur.size();
	}
}
