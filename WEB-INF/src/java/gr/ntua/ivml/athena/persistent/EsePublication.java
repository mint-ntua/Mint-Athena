package gr.ntua.ivml.athena.persistent;

import gr.ntua.ivml.athena.concurrent.Queues;
import gr.ntua.ivml.athena.concurrent.Ticker;
import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.persistent.Transformation.MyZipOutputStream;
import gr.ntua.ivml.athena.util.Config;
import gr.ntua.ivml.athena.xml.SchemaValidator;
import gr.ntua.ivml.athena.xml.transform.XMLFormatter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Enumeration;
import java.util.Iterator;

import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

import org.apache.commons.io.FileUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import de.schlichtherle.util.zip.ZipEntry;
import de.schlichtherle.util.zip.ZipFile;

/**
 * Subclass with the logic for the lido athena logic.
 * Uses superclass where convenient. 
 * 
 * The mapping will rely on the name of the targetSchema, see
 * Publication.hbm.xml
 * @author Arne Stabenau 
 *
 */
public class EsePublication extends Publication  {

	File toProcess;
	String xsl09, xsl10;
	
	gr.ntua.ivml.athena.xml.transform.XSLTransform t = new gr.ntua.ivml.athena.xml.transform.XSLTransform();			

	private static class Counter {
		int count = 0;
		void inc() { count += 1; }
		int get() { return count; }
		void reset() { count = 0; };
	};
	
	@Override
	public Iterator<NodeContainer> itemize() throws Exception {
		return new PathIterator( getTransformations(), "/lidoWrap/lido");
	}
	
	/**
	 * Convert from Lido to ESE.
	 */
	
	public File postProcess( File input ) throws Exception {
		File result = File.createTempFile("PubPostProcess", ".zip");
		MyZipOutputStream postProcessOutput = new MyZipOutputStream(new FileOutputStream(result));
		setStatusCode(POSTPROCESS);
		setStatusMessage("Starting post process" );
		DB.commit();
		
		File xslFile = new File( Config.getRealPath(Config.get( "lido_to_ese_xsl" )));
		xsl09 = FileUtils.readFileToString( xslFile , "UTF-8");
		xslFile = new File( Config.getRealPath( Config.get( "lido1.0_to_ese_xsl" )));
		xsl10 = FileUtils.readFileToString( xslFile , "UTF-8");
		
		ZipFile bz=null;
		try {
			bz = new ZipFile( input );
			int count = bz.size();
			log.info( "Postprocessing " + count + " items started.");
			int eseItems = 0;
			int lidoItems = 0;
			int failed = 0;
			Enumeration<ZipEntry> entries = bz.entries();
			StringBuilder processReport = new StringBuilder();
			if( report != null ) processReport.append(report);
			
			long errorNodeId = -1l;
			String errorSrc = "";
			// report every 20 seconds on progress
			Ticker t = new Ticker( 20 );

			// prepare the validating parser and handler 
			Schema eseSchema = SchemaValidator.getEseSchema();
			ValidatorHandler vh = eseSchema.newValidatorHandler();
			
			final Counter eseCount = new Counter();
			ContentHandler eseCountHandler = new DefaultHandler() {
				public void startElement( String uri, String localName, String qName, Attributes atts ) {
					if( "record".equals( localName ) || "record".equals( qName )) eseCount.inc();
				}
			};
			
			vh.setContentHandler(eseCountHandler);
			
			XMLReader parser = org.xml.sax.helpers.XMLReaderFactory.createXMLReader(); 
			parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			parser.setContentHandler(vh);

			
			while( entries.hasMoreElements() ) {
				ZipEntry ze = (de.schlichtherle.util.zip.ZipEntry) entries.nextElement();
				InputStream zis = bz.getInputStream(ze);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				try {
					if( ze.isDirectory() ) continue;
					if( !ze.getName().endsWith("xml")) continue;
					lidoItems += 1;
					String num = ze.getName();
					String xsl;
					if( num.startsWith("lido09_"))
						xsl = xsl09;
					else
						xsl = xsl10;
					
					num = num.substring(7, num.length()-4);
					errorNodeId= Long.parseLong(num);
					errorSrc = "Lido_to_Ese transformation";
					transformEntry(ze.getName(), zis, bos,xsl);
					errorSrc = "Ese validation";

					InputSource ins = new InputSource();
					ins.setByteStream(new ByteArrayInputStream(bos.toByteArray()));

					// check ese is valid
					// I hope this throws when things are not valid
					parser.parse( ins );
					
					postProcessOutput.putNextEntry(new ZipEntry( ze.getName()));
					bos.writeTo(postProcessOutput);
					postProcessOutput.close();
					eseItems += eseCount.get();
					// some quick way out if things don't go well
					if(( lidoItems > 100 ) && (eseItems == 0 )) {
						setReport( processReport.toString());
						DB.commit();						
						throw new Exception( "Publication aborted, no ese records are produced.");
					}
				} catch( Exception e ) {
					failed += 1;
					if( processReport.length() < 50000 ) {
						if( errorNodeId != -1l ) {
							String[] ids = resolveNode( errorNodeId );
							processReport.append( "\nItem " + ze.getName() );
							processReport.append( " URL:(PreviewError?nodeId="+errorNodeId+"&transformationId="+ids[1]);
							processReport.append( "&uploadId="+ids[2]+"&errorSrc="+errorSrc +")" );
							processReport.append( " had problems: \n" );
							processReport.append( e.getMessage() + "\n");
						} else {
							// not related to a specific node, we are done with an error
							setReport( processReport.toString());
							DB.commit();
							throw e;
						}
					}
					if(( lidoItems == 100 ) && (eseItems == 0 )) {
						setReport( processReport.toString());
						DB.commit();						
						throw new Exception( "Publication aborted after 100 consecutive failures.");
					}
				} finally {
					eseCount.reset();					
				}
				if( t.isSet() ) {
					t.reset();
					setStatusMessage( "Postprocessed " + lidoItems + " items of " + count + " (failed " + failed + ")");
					log.debug( "Postprocessed " + lidoItems + " items of " + count + " (failed " + failed + ")");
					DB.commit();
				}
			}
			if( eseItems > 0 ) {
				processReport.append( "\nTransformed " + lidoItems + " lido records to " + eseItems + " ese records.\n" );
				setItemCount(eseItems);
				if( failed != 0 ) {
					processReport.append( failed + " items were excluded due to problems.\n" );
				}
				postProcessOutput.putNextEntry( new ZipEntry( "lido_to_ese_report.txt" ));
				postProcessOutput.write( processReport.toString().getBytes("UTF-8"));
				postProcessOutput.close();
				postProcessOutput.finished();
				postProcessOutput = null;
				log.info( "Finished creating " + result.getAbsolutePath());
				setStatusMessage( "Postprocessed " + lidoItems + " items.");
				setReport( processReport.toString());
				DB.commit();
			} else {
				setReport( processReport.toString());
				throw new Exception( "No item could be transformed!" ); 
			}
			// not sure this is needed
			t.cancel();
			return result;
		} catch( Exception e ) {
				log.error( "General post processing problem ", e );
				if( getStatusCode() != ERROR ) {
					setStatusMessage(e.getMessage());
					setStatusCode(ERROR);
					DB.commit();
				}
				throw e;
		} finally {
			if( bz!= null ) bz.close();
			if( postProcessOutput != null ) postProcessOutput.finished();
		}
	}

	public void transformEntry(String name, InputStream is, OutputStream bos, String xsl) throws Exception {
		// transform into pipe (pos) and format from pipe (pis) into the zipped output (os)
		final OutputStream os = bos;

		PipedOutputStream pos = new PipedOutputStream();
		final PipedInputStream pis = new PipedInputStream( pos );
		Runnable formatter = new Runnable() {
			public void run() {
				XMLFormatter.format(pis, os);
			}
		};
		
		try {
			Queues.queue(formatter, "now");
			t.transform(is, xsl, pos );
			pos.flush();
			pos.close();
			Queues.join( formatter );
			
		} finally {
			os.close();
			pis.close();
			pos.close();
		}		
	}
	
	/**
	 * Get the relevant data for this node
	 *  - xml object id
	 *  - transformation_id
	 *  - upload_id
	 * @param nodeId
	 * @return Strings with the ids
	 */
	String[] resolveNode( long nodeId ) {
		String[] res = new String[3];
		res[0] = "-1";
		res[1] = "-1";
		res[2] = "-1";
		XmlObject xo = DB.getXmlObjectDAO().findByNodeId(nodeId);
		if( xo != null ) {
			res[0] = xo.getDbID().toString();
			Transformation tr = DB.getTransformationDAO().findByXmlObject( xo );
			if( tr != null ) {
				res[1] = tr.getDbID().toString();
				DataUpload du = tr.getDataUpload();
				res[2] = du.getDbID().toString();
			}
			
		} 
		
		return res;
	}
}
