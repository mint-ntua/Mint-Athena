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
