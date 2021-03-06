package gr.ntua.ivml.athena.test;

import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.XMLNode;
import gr.ntua.ivml.athena.persistent.XpathHolder;

import java.io.PrintWriter;

import junit.framework.TestCase;

public class ExportXmlTest extends TestCase {

	public void testNodeExport() {
		DataUpload du = getExample();
		XpathHolder xp = du.getXmlObject().getRoot();
		XpathHolder xp2 = xp.getByRelativePath("/OAI-PMH/GetRecord/record");
		XMLNode xm = xp2.getNodes(10, 1).get(0);
		xm.toXml(new PrintWriter( System.out ));		
	}
	
	public DataUpload getExample() {
		DataUpload du = DB.getDataUploadDAO().simpleGet("originalFilename='example.zip'");
		assertNotNull( "DataUpload 'example.zip' not uploaded", du );
		return du;
	}
}
