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

import java.util.Date;

public class Lock {
	Long dbID;
	String userLogin;
	Date aquired;
	String httpSessionId;
	String objectType;
	long objectId;
	String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getDbID() {
		return dbID;
	}
	public void setDbID(Long dbID) {
		this.dbID = dbID;
	}
	public String getUserLogin() {
		return userLogin;
	}
	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
	public Date getAquired() {
		return aquired;
	}
	public void setAquired(Date aquired) {
		this.aquired = aquired;
	}
	public String getHttpSessionId() {
		return httpSessionId;
	}
	public void setHttpSessionId(String httpSessionId) {
		this.httpSessionId = httpSessionId;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public long getObjectId() {
		return objectId;
	}
	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}	

	public String getReadableType() {
		if( objectType == null ) return "EMPTY";
		if( objectType.endsWith("DynamicTerm")) return "List";
		if( objectType.endsWith("AssetContainer")) return "Digital asset";
		if( objectType.endsWith("PhysicalMedium")) return "Physical medium";
		return "Unknown";
	}
	/**
	 * How old in seconds is this lock ...
	 * @return
	 */
	public int getAge() {
		long ageMil = (new Date()).getTime() - aquired.getTime();
		int ageSec = (int) ageMil/1000;
		return ageSec;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append( "User: " + userLogin + "\n" );
		sb.append( "Session: " + httpSessionId + "\n" );
		sb.append( "DbID: " + objectId + "\n" );
		sb.append( "Type: " + objectType + "\n" );
		sb.append( "Name: " + name +"\n" );
		return sb.toString();
	}
}
