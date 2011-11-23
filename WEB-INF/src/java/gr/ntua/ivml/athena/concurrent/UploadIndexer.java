package gr.ntua.ivml.athena.concurrent;

import gr.ntua.ivml.athena.db.AsyncNodeStore;
import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.harvesting.SingleHarvester;
import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.Mapping;
import gr.ntua.ivml.athena.persistent.ReportI;
import gr.ntua.ivml.athena.persistent.Transformation;
import gr.ntua.ivml.athena.persistent.XpathHolder;
import gr.ntua.ivml.athena.persistent.DataUpload.EntryProcessor;
import gr.ntua.ivml.athena.util.Config;
import gr.ntua.ivml.athena.util.NodeReader;
import gr.ntua.ivml.athena.xml.SchemaValidator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.hibernate.StatelessSession;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

/**
 * UploadIndexer accompanies an upload from the moment the user initiates it
 * @author Arne Stabenau 
 *
 */
public class UploadIndexer implements Runnable, ReportI {
	public static final int FTPSERVER = 1;
	public static final int URLUPLOAD = 2;
	public static final int OAIHARVEST = 3;
	public static final int SERVERFILE = 4;
	public static final int HTTPUPLOAD = 5;

	DataUpload du;
	int method;
	public String filename;
	public File tmpFile;
	public String set;
	public String ns;
	public Date from;
	public Date to;
	
	
	// the indexer has a immediate phase and a queued phase now
	// all heavy db stuff is happening in the queued phase
	public boolean preQueue;
	
	
	public static final Logger log = Logger.getLogger(UploadIndexer.class );
	
	public UploadIndexer( DataUpload du, int method ) {
		this.method = method;
		this.du = du;
		this.preQueue = true;
	}
	
	public UploadIndexer(DataUpload du, int method, String set, String ns, Date from, Date to){
		this.method = method;
		this.du = du;
		this.set = set;
		this.ns = ns;
		this.from = from;
		this.to = to;
		this.preQueue = true;
	}
	
	public DataUpload getDataUpload() {
		return du;
	}
	
	public void setServerFile( String filename ) {
		this.filename = filename;
		this.tmpFile = new File( filename );
		
	}
	/**
	 * Depending on the method and the file,
	 * different things are done. 
	 * The UploadIndexer needs to run twice. First in a preQueue phase, then
	 * in a queue to do the database heavy parts. Enqueueing happens
	 * once the data is as blob in the database.
	 *
	 */
	public void run() {
		// http uploads are on file system already
		// ftp downloads need transfer from ftp server
		// url uploads need to be downloaded
		// oai harvests need to harvest ...
		// need to start transaction
		DB.newSession();
		DB.getSession().beginTransaction();
		du = DB.getDataUploadDAO().findById(du.getDbID(), false);
		DB.logPid();
		try {
			if( preQueue ) {
				check();
				aquire();
				
				check();
				upload();
				EntryProcessor ep = new EntryProcessor( ) {
					public void  processEntry(de.schlichtherle.util.zip.ZipEntry ze, InputStream is) throws Exception {
						if( ze.isDirectory()) return;
						String entryName = ze.getName();
						if( !entryName.endsWith(".xml")) return;
						// makes this process interruptible
						Thread.sleep(0);
						//InputSource ins = new InputSource();
						//ins.setByteStream(is);
						 StringBuilder sb = new StringBuilder();
				            String line;
			            try {
			                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			                while ((line = reader.readLine()) != null) {
			                    sb.append(line).append("\n");
			                }
			            } finally {
			                is.close();
			            }
			            
			            StringReader read = new StringReader(sb.toString());
			            Source source = new StreamSource(read);
			            SchemaValidator.validate(source, SchemaValidator.LIDO);
			            /*try{
							SchemaValidator.validate(source, SchemaValidator.LIDO);
						}catch(SAXException ex){
							System.out.println(" Not valid because ");
				            System.out.println(ex.getMessage()); 
						}*/
					}
				};

				EntryProcessor ep10 = new EntryProcessor( ) {
					public void  processEntry(de.schlichtherle.util.zip.ZipEntry ze, InputStream is) throws Exception {
						if( ze.isDirectory()) return;
						String entryName = ze.getName();
						if( !entryName.endsWith(".xml")) return;
						// makes this process interruptible
						Thread.sleep(0);
						//InputSource ins = new InputSource();
						//ins.setByteStream(is);
						 StringBuilder sb = new StringBuilder();
				            String line;
			            try {
			                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			                while ((line = reader.readLine()) != null) {
			                    sb.append(line).append("\n");
			                }
			            } finally {
			                is.close();
			            }
			            
			            StringReader read = new StringReader(sb.toString());
			            Source source = new StreamSource(read);
			            SchemaValidator.validate(source, SchemaValidator.LIDO10);
			            /*try{
							SchemaValidator.validate(source, SchemaValidator.LIDO);
						}catch(SAXException ex){
							System.out.println(" Not valid because ");
				            System.out.println(ex.getMessage()); 
						}*/
					}
				};
				
				if(du.isLido()) {
					du.processAllEntries(ep);
				} else if(du.isLido10()) {
					du.processAllEntries(ep10);
				}
				check();
				du.setStatus(DataUpload.QUEUED );
			} else {
				parse();
				check();
				AsyncNodeStore.index(du.getXmlObject(), this, DB.getStatelessSession().connection());
				du.updateStatus(DataUpload.OK);
				du.setMessage(du.getNodeCount()+ " nodes imported and indexed" );
				store();
				if(du.isLido() || du.isLido10()){
				   setLidoLevelLabel();
				   lidoTransform();
				}
			}
		} catch( InterruptedException e ) {
			log.info( "UploadIndexer interrupted, data will become invalid!" );
			preQueue = false;
		} catch( Exception e2 ) {
			log.error( "UploadIndexer failed on DataUpload " + du.getDbID(), e2 );
			if( du.getStatus() != DataUpload.ERROR) {
				du.setMessage("Upload indexer failed with "+e2.getMessage());
				du.setStatus(DataUpload.ERROR);
			}
			store();
			preQueue = false;
		} finally {
			try {
				DB.getSession().getTransaction().commit();
			} catch( Exception e2 ) {
				log.error( "Transaction cannot be commited!", e2 );
			}
			DB.closeSession();
			DB.closeStatelessSession();
		}
		if( preQueue ) {
			// requeue this job on for the indexing job
			preQueue= false;
			Queues.queue(this, "db");
		}
	}
	
	private void setLidoLevelLabel(){
		String xpath ="/lidoWrap/lido";
		String label="/lidoWrap/lido/descriptiveMetadata/objectIdentificationWrap/titleWrap/titleSet/appellationValue/text()";
		XpathHolder xplvl = du.getXmlObject().getRoot().getByRelativePath(xpath);
		XpathHolder xplbl = du.getXmlObject().getRoot().getByRelativePath(label);
		
		if(xplvl == null) {
			xpath ="/lido";
			label="/lido/descriptiveMetadata/objectIdentificationWrap/titleWrap/titleSet/appellationValue/text()";
			xplvl = du.getXmlObject().getRoot().getByRelativePath(xpath);
			xplbl = du.getXmlObject().getRoot().getByRelativePath(label);			
		}
		du.setItemXpath(xplvl);
		du.setItemLabelXpath(xplbl);
		DB.getDataUploadDAO().makePersistent(du);
	}
	
	
	private void lidoTransform(){
	    Transformation t = new Transformation();
	    t.setDataUpload(du); 
	    t.setParsedOutput( du.getXmlObject());
	    //set Mapping for this tranformation the hardcoded LidoToLido in db with id=1
	    Mapping lido2lido=DB.getMappingDAO().getById(new Long(1), false);
	    //t.setMapping( some fake mapping );
	    t.setMapping(lido2lido);
	    t.setZippedOutput( du.getData() );
	    t.setStatusCode(0);
	    t.setJsonMapping("");
	    t.setUser(du.getUploader());
	    DB.getTransformationDAO().makePersistent(t);
	}

	
	private void queueThisJob() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Get the data into the filesystem.
	 * @return
	 */
	private void aquire() throws Exception {
		if( method == FTPSERVER ) 
			aquireFtp();
		else if( method == URLUPLOAD) 
			aquireUrl();
		else if( method == OAIHARVEST )
			aquireOAI();
	}
	
	/**
	 * Harvests from DataUpload url. Result in DataUpload.tmpFile at the end,
	 * so ready for upload.
	 * @throws Exception
	 */
	private void aquireOAI() throws Exception {
		du.updateStatus(DataUpload.HARVEST);
		this.store();
		SingleHarvester harvester = null;
		if( ((this.from == null) || (this.to == null)) && (this.set == null) ){
			harvester = new SingleHarvester(du.getSourceURL(), null, null, this.ns, null);
		}else if((this.from == null) || (this.to == null)){
			harvester = new SingleHarvester(du.getSourceURL(), null, null, this.ns, this.set);
		}else{
			harvester = new SingleHarvester(du.getSourceURL(), this.from.toString(), this.to.toString(), this.ns, this.set);
		}
		harvester.setReporter(this);
		du.setOriginalFilename(du.getSourceURL());
		this.store();
		//this.tmpFile = new File(du.getOriginalFilename());
		//log.error("filename:"+ du.getOriginalFilename());
		
		try{
			harvester.harvest();
			this.tmpFile = new File(harvester.getFileName());
		}catch(Exception e){
			log.error("oai error:", e);
			du.setMessage("oai harvesting encountered an error:" + e.getMessage());
			du.updateStatus(DataUpload.ERROR);
			store();
			throw e;
		}
	}
	
	
	/**
	 * Given a DataUpload, retrieve file from ftp server. 
	 * Store it on du.tmpFile.
	 * into 
	 */
	private void aquireFtp() throws Exception {
		try {
			du.updateStatus(DataUpload.HARVEST);
			store();
			tmpFile = File.createTempFile("AthenaFtp", "" );
			FileOutputStream fos = new FileOutputStream( tmpFile );
			FTPClient f= new FTPClient();
		    f.connect(Config.get("athenaFtpServer"));
		    f.login(Config.get("ftpUser"), Config.get("ftpPassword"));
		    f.setFileType(FTP.BINARY_FILE_TYPE);
		    if( ! f.retrieveFile(du.getOriginalFilename(), fos )) {
		    	log.error( "There was a problem retreiving file");
		    	throw new Exception( "Retrieve failed ");
		    }
		    fos.close();
		    log.info( "FTPed " + filename + " with " + tmpFile.length() + " bytes.");
		} catch( Exception e ) {
			log.error( "FTP file retreive or storing didnt succeed", e );
			du.setMessage("FTP retrieve failed: " + e.getMessage());
			du.updateStatus(DataUpload.ERROR);
			store();
			throw e;
		}	
	}
	
	private void aquireUrl() throws Exception {
		try {
			du.updateStatus(DataUpload.HARVEST);
			store();
			tmpFile = File.createTempFile("AthenaUrl", "" );
			InputStream is = new URL( du.getSourceURL()).openStream();
			FileOutputStream fos = new FileOutputStream( tmpFile );
			IOUtils.copy( is, fos);
			is.close();
			fos.flush();
			fos.close();
		} catch( Exception e ) {
			log.error( "URL download failed", e  );
			du.setMessage("URL download failed: " + e.getMessage());
			store();
			throw e;
		}
	}
	
	
	/**
	 * Move data into the BLOB
	 * @return
	 */
	private void upload() throws Exception {
		
		du.setMessage("" );
		du.updateStatus(DataUpload.UPLOAD);
		store();
		du.upload(tmpFile);
		store();
		// get a clean du
		DB.getSession().clear();
		du = DB.getDataUploadDAO().findById(du.getDbID(), false);
	
		log.info( "Delete " + tmpFile.getName());
		if( method != SERVERFILE )
			tmpFile.delete();
	}
	
	private void parse() throws Exception {
		du.setMessage("" );
		du.updateStatus(DataUpload.PARSE);
		store();
		NodeReader nr = new NodeReader( du );
		nr.readNodes();
		DB.getSession().clear();
		du = DB.getDataUploadDAO().findById(du.getDbID(), false);
	}
	
	
	/**
	 * shortcut for typing, should be inlined 
	 */
	private final void store() {
		DB.commit();
	}
	
	/**
	 * Make the thread more interruptible
	 * same as sleep(0) ?
	 * @throws Exception
	 */
	private final void check() throws InterruptedException {
		if( Thread.currentThread().isInterrupted())
			throw new InterruptedException( "Thread interrupted!" );
	}
	
	private void doSQL( Connection c, String sql ) throws SQLException {
		Statement st;
		st = c.createStatement();
		st.executeUpdate( sql  );
		st.close();
		c.commit();
	}

	@Override
	public void report(String msg) {
		du.setMessage(msg);
		DB.commit();
	}

	@Override
	public void reportError() {
		du.setStatus(DataUpload.ERROR);
	}

}
