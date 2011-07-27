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

package gr.ntua.ivml.athena.actions;

import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.XmlObject;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({
	  @Result(name="error", location="stats.jsp"),
	  @Result(name="success", location="stats.jsp")
	})


public class Stats extends GeneralAction {

	protected final static Logger log = Logger.getLogger(Stats.class);
	
	public class Namespace {
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		String prefix;
		String uri;

		public String getPrefix() {
			if( prefix.equals(""))
				if( uri.equals(""))
					return "<empty>";
				else
					return "<default>";
			else 
				return prefix;
		}
		
		public String getUri() { return uri; }
	}

	private List<Namespace> namespaces = null;
	String uploadId;
	String xmlObjectId;
	
	public String getXmlObjectId() {
		return xmlObjectId;
	}

	public void setXmlObjectId(String xmlObjectId) {
		this.xmlObjectId = xmlObjectId;
	}

	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	XmlObject xo;	
	public List<Namespace> getNamespaces() {
		if( namespaces == null ) {
			namespaces = new ArrayList<Namespace>();
			XmlObject xo = getXmlObject();
			if( xo != null) {
				for( String[] s2: xo.listUriAndPrefix() ) {
					Namespace n  = new Namespace();
					n.uri = s2[1];
					n.prefix = s2[0];
					namespaces.add( n ) ;
				}
			}
			
		}
		log.debug( "Namespaces " + namespaces.size());
		return namespaces;
	}
	

	public XmlObject getXmlObject() {
		if( xo == null ) {
			try {
			if(( getUploadId() != null ) &&
					( getUploadId().length() > 0 )) {
				DataUpload du = DB.getDataUploadDAO().getById(
					Long.parseLong(getUploadId()), false);
				if( du != null) xo = du.getXmlObject();
			} else if(( getXmlObjectId() != null) && 
					( getXmlObjectId().length() > 0 )) {
				xo = DB.getXmlObjectDAO().getById(
						Long.parseLong(getXmlObjectId()), false );
			}
			} catch( Exception e ) {
				log.error( "Cannot construct XmlObject." , e );
			}
		}
		return xo;
	}
	
	
	
	@Action(value="Stats")
	public String execute() throws Exception {
		return SUCCESS;
	}

}
