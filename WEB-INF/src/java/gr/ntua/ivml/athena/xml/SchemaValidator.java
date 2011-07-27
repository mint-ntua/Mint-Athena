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

package gr.ntua.ivml.athena.xml;

import gr.ntua.ivml.athena.util.Config;

import java.io.*;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;

public class SchemaValidator {
	
	public static final int LIDO = 1;
	public static final int ESE = 2;
	public static final int LIDOPROXY = 3;
	public static final int LIDO10 = 4;
	
	private static SchemaFactory factory;
	
	private static Schema eseSchema;
	private static Schema lidoSchema;
	private static Schema lido10Schema;
	private static Schema lidoProxySchema;
	
	
	static{
		factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		 File eseSchemaLocation = new File(Config.getRealPath("WEB-INF/jsp/ESE-V3.3.xsd"));
		 File lidoSchemaLocation = new File(Config.getRealPath("WEB-INF/jsp/lido-draft-v0.9.xsd"));
		 File lido10SchemaLocation = new File(Config.getRealPath("WEB-INF/jsp/lido-v1.0.xsd"));
	     //File lidoProxySchemaLocation = new File(Config.getRealPath("WEB-INF/jsp/lido-v0.9-proxy.xsd"));
		 try {
				eseSchema = factory.newSchema(eseSchemaLocation);
				lidoSchema = factory.newSchema(lidoSchemaLocation);
				lido10Schema = factory.newSchema(lido10SchemaLocation);
				//lidoProxySchema = factory.newSchema(lidoProxySchemaLocation);
				
				
			} catch (SAXException e) {
				e.printStackTrace();
			}

	}
	
	public static void validate(Source source, int schemaSource) throws SAXException, IOException{
			if(schemaSource == LIDO){
				Validator lidoValidator = lidoSchema.newValidator();
				lidoValidator.validate(source);
			}else if(schemaSource == LIDO10){
				Validator lidoValidator = lido10Schema.newValidator();
				lidoValidator.validate(source);
			}else if(schemaSource == ESE){
				Validator eseValidator = eseSchema.newValidator();
				eseValidator.validate(source);
			}else if(schemaSource == LIDOPROXY){
				//Validator lidoProxyValidator;
				//lidoProxyValidator.validate(source);
			}
	}
	
	public static Schema getEseSchema() {
		return eseSchema;
	}
}
