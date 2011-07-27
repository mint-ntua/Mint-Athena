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

package gr.ntua.ivml.athena.harvesting.io;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import gr.ntua.ivml.athena.harvesting.xml.schema.OAIPMHtype;
import gr.ntua.ivml.athena.harvesting.xml.schema.ObjectFactory;

public class FileImporter{

	private String baseDir;
	private String providerName;
	private JAXBContext jc;
	private Marshaller m;
	private ObjectFactory fact;
	public FileImporter(){
		this.baseDir = "c:\\oai\\";
		
		try {
			this.jc = JAXBContext.newInstance( "gr.ntua.ivml.athena.harvesting.xml.schema" );
			fact = new ObjectFactory();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public void save(OAIPMHtype item, String format) {
		try {
			String ident = item.getGetRecord().getRecord().getHeader().getIdentifier();
			String[] splits = ident.split(":");
			String itemName = splits[splits.length-1];
			providerName = splits[splits.length-2];
			System.out.println(this.baseDir+this.providerName+"\\"+format+"\\"+itemName+".xml");
			//m.marshal(item, new File(this.baseDir+this.providerName+"\\"+format+"\\"+itemName+".xml"));
			System.out.println(item.getRequest().getIdentifier());
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					  Boolean.TRUE);
			new File(this.baseDir+this.providerName+"\\"+format).mkdirs();
			
			// m.marshal(fact.createOAIPMH(item), new File(this.baseDir+this.providerName+"\\"+format+"\\"+itemName+".xml"));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
