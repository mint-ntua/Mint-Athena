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

import gr.ntua.ivml.athena.concurrent.Ticker;
import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.util.Config;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import de.schlichtherle.util.zip.ZipEntry;
import de.schlichtherle.util.zip.ZipOutputStream;

/**
 * This class summarizes all information needed to export a selection of
 * DataUploads. Need to encapsulate special target schema logic
 * Subclass? How do I do the Hibernate stuff ??
 * 
 * @author Arne Stabenau 
 *
 */
public class Publication {
	public static class NodeContainer {
		public static final int LIDOVS09 = 1;
		public static final int LIDOVS10 = 2;
		
		public XMLNode node;
		public int schema;	
	}
	
	
	public static class PathIterator implements Iterator<NodeContainer> {
		private static final XMLNode[] templatePage = new XMLNode[0];
		
		List<Transformation> transformations;
		Iterator<Transformation> iterTransform;
		String path;
		XpathHolder currentHolder;
		
		Transformation currentTransformation;
		XMLNode nextItem;
		XMLNode[] page;
		int nextInPage;
		
		
		public PathIterator( List<Transformation> l, String path ) {
			transformations = l;
			iterTransform = transformations.iterator();
			this.path = path;
			//next();
			nextItem = nextInPage();
		}
		
		@Override
		public boolean hasNext() {
			return nextItem != null;
		}

		
		private boolean nextHolder() {
			if( iterTransform.hasNext() ) {
				currentTransformation = iterTransform.next();
				XmlObject xo = currentTransformation.getParsedOutput();
				currentHolder =  xo.getRoot().getByRelativePath(path);
				if( currentHolder != null )
					log.debug( "Current transformation has " + currentHolder.getCount() + " items." );
			} else {
				currentHolder = null;
			}
			return currentHolder != null;
		}

		/**
		 * Retrieve next page from current holder or first from next
		 * @return if there is stuff left
		 */
		private boolean nextPage() {
			List<XMLNode> l = null;
			if( page != null )
				l= currentHolder.getNodes( page[page.length-1], 100);
			if(( page== null ) || ( l.size() == 0 ))  {
				nextHolder();
				if( currentHolder == null ) return false;
				l= currentHolder.getNodes( 0, 100);
				if( l.size() == 0 ) throw new RuntimeException( "Unexpected result, should have nodes");				
			}
			page = l.toArray(templatePage);
			nextInPage = 0;			
			return true;
		}
		
		private XMLNode nextInPage() {
			XMLNode result=null;
			if(( page==null ) || ( nextInPage==page.length )) {
				if( ! nextPage()) return null;
			} 
			result = page[nextInPage];
			nextInPage+=1;
			return result;
		}
		
		@Override
		public NodeContainer next() {
			NodeContainer result = new NodeContainer();
			result.node = nextItem;
			if( currentTransformation.getDataUpload().isLido10())
				result.schema = NodeContainer.LIDOVS10;
			else
				result.schema = NodeContainer.LIDOVS09;
			nextItem = nextInPage();
			return result;
		}

		@Override
		public void remove() {
			throw new NoSuchMethodError();
		}
		
	}
	
	public static final Logger log = Logger.getLogger( Publication.class );
	public static final int ERROR=-1;
	public static final int OK=0;
	public static final int IDLE=1;
	public static final int CONSOLIDATE=2;
	public static final int VERSION=3;
	public static final int POSTPROCESS=4;
	
	Long dbID;
	
	// all affected DataUpload objects
	List<DataUpload> inputUploads = new ArrayList<DataUpload>();
	
	// example stats on the this publication, more could be collected
	long itemCount;
	
	// which user did the publication
	User publishingUser;
	Organization publishingOrganization;
	
	// status information on the progress of publication
	String statusMessage;
	int statusCode;
	String report;
	
	// when the publication was initiated
	Date lastProcess;
	
	// the final output in zipped form
	// either one or many files, possible millions
	BlobWrap zippedOutput;
	
	// name of output. With this the correct Transformations are selected
	String targetSchema;

	// transient only valid while in progress
	File workdir;
	File tmpFile;
	
	public Long getDbID() {
		return dbID;
	}

	public void setDbID(Long dbID) {
		this.dbID = dbID;
	}

	public List<DataUpload> getInputUploads() {
		return inputUploads;
	}

	public void setInputUploads(List<DataUpload> inputUploads) {
		this.inputUploads = inputUploads;
	}

	public long getItemCount() {
		return itemCount;
	}

	public void setItemCount(long itemCount) {
		this.itemCount = itemCount;
	}

	public User getPublishingUser() {
		return publishingUser;
	}

	public void setPublishingUser(User publishingUser) {
		this.publishingUser = publishingUser;
	}

	public Organization getPublishingOrganization() {
		return publishingOrganization;
	}

	public void setPublishingOrganization(Organization publishingOrganization) {
		this.publishingOrganization = publishingOrganization;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public Date getLastProcess() {
		return lastProcess;
	}

	public void setLastProcess(Date lastProcess) {
		this.lastProcess = lastProcess;
	}

	
	
	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public BlobWrap getZippedOutput() {
		return zippedOutput;
	}

	public void setZippedOutput(BlobWrap zippedOutput) {
		this.zippedOutput = zippedOutput;
	}

	public String getTargetSchema() {
		return targetSchema;
	}

	public void setTargetSchema(String targetSchema) {
		this.targetSchema = targetSchema;
	}
	
	/**
	 * Call this to check if the publication is still valid.
	 * It should check whether changes in the input data (transformations, mappings)
	 * are not reflected here.
	 * 
	 * return true if Publication is still valid. 
	 */
	public boolean validate() {
		// go through all relevant transformations and check if any have 
		// dates after the process of this Publication.
		
		return true;
	}
	
	/**
	 * Check if the current state is still valid.
	 * Check if a new processing round has to be done.
	 * Do it (version, apply changes, pullup of changes, consolidate in one xml-object )
	 */
	public void process() {
		File consolidated = null;
		File processed = null;
		try {
			if( !upToDateCheck()) {
				version();
				applyChanges();
				consolidated = consolidate();

				processed = postProcess( consolidated );

				writeBack( processed );
				setLastProcess(new Date());
				setStatusCode(OK);
				setStatusMessage("Processed and ready for download");
			}
		} catch( Exception e ) {
			if( getStatusCode() != ERROR ) {
				setStatusCode(ERROR);
				setStatusMessage("Publication processing failed with: " + e.getMessage());
			}
			// didn't work, remove transformations from upload
			getInputUploads().clear();
			log.error( "processing of Publication failed.", e );
		} finally {
			if( consolidated != null ) consolidated.delete();
			if( processed != null ) processed.delete();
			DB.commit();
		}
	}
	
	/**
	 * Convenience function to remove an upload. No processing is started.
	 * @param du
	 */
	public void removeUpload( DataUpload du ) {
		Iterator<DataUpload> i = getInputUploads().iterator();
		while( i.hasNext() ) {
			DataUpload du2 = i.next();
			if( du2.getDbID() == du.getDbID()) {
				i.remove();
				return;
			}
		}
	}
	
	/**
	 * 
	 * @param du
	 */
	public boolean containsUpload( DataUpload du ) {
		Iterator<DataUpload> i = getInputUploads().iterator();
		while( i.hasNext() ) {
			DataUpload du2 = i.next();
			if( du2.getDbID() == du.getDbID()) {
				return true;
				
			}
		}
		return false;
	}
	
	/**
	 * Convenience function to remove an upload, no reprocessing is started.
	 * @param du
	 */
	public void addUpload( DataUpload du ) {
		getInputUploads().add( du );
	}
	
	public List<Transformation> getTransformations() throws Exception {
		ArrayList<Transformation> al = new ArrayList<Transformation>();
		// input uploads need sorting
		List<DataUpload> l = getInputUploads();
		Collections.sort(l, new Comparator<DataUpload>() {
			public int compare( DataUpload a, DataUpload b ) {
				if( a.getUploadDate().before(b.getUploadDate())) return -1;
				if( a.getUploadDate().after( b.getUploadDate())) return 1;
				return 0;
			}
		});
		for( DataUpload du: l ) {
			boolean hasTransformation = false;
			for( Transformation tr: du.getTransformations()) {
				String target1 = tr.getMapping().getTargetSchema();
				if( target1 != null ) {
					if( target1.equals( getTargetSchema())) {
						if( tr.getStatusCode() == Transformation.OK) {
							al.add( tr );
							hasTransformation = true;
						}
					}
				} else { 
					if( getTargetSchema() == null ) {
						if( tr.getStatusCode() == Transformation.OK) {
							al.add( tr );
							hasTransformation = true;
						}						
					}
				}
			}
			if( ! hasTransformation ) throw new Exception( "Upload has no suitable Transformation" );
		}
		return al;
	}

	// Section with real work
	
	/**
	 * All involved Transformations happened before the last process date.
	 * @return
	 */
	public boolean upToDateCheck() throws Exception {
		List<Transformation> l = getTransformations();
		Date lastProcess = getLastProcess();
		if( lastProcess == null ) return false;
		for( Transformation tr: l ) {
			if( ! tr.getEndTransform().before( lastProcess ))
				return false;
		}
		// all happened before last process, we are up to date
		return true;
	}
	
	/**
	 * Create the List of items with available newer versions.
	 * @throws Exception
	 */
	public void version() throws Exception {
		// do nothing for now
	}
	
	/**
	 * Apply the changeset to the latest version of an item.
	 * @throws Exception
	 */
	public void applyChanges() throws Exception {
		// do nothing for now
	}
	
	/**
	 * Create the output XML object / files (not sure yet)
	 * @throws Exception
	 */
	public File consolidate() throws Exception {
		long counter = 0l;
		File consolidated = File.createTempFile("consolidated", ".zip");
		log.debug( "Consolidate into " + consolidated.getAbsolutePath());
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream( consolidated ));
		Iterator<NodeContainer> i = itemize();
		// every 20 seconds is set
		Ticker t = new Ticker( 20 );
		while( i.hasNext() ) {
			NodeContainer nc = i.next();
			
			XMLNode n = nc.node;
			XMLNode tree = DB.getXMLNodeDAO().wrappedDOMTree(n);
			counter += 1;
			String entryname;
			if( nc.schema == NodeContainer.LIDOVS09) 
				entryname = "lido09_"+n.getNodeId() + ".xml";
			else 
				entryname = "lido10_"+n.getNodeId() + ".xml";
			zos.putNextEntry(new ZipEntry(entryname ));
			PrintWriter pw = new PrintWriter( new OutputStreamWriter(zos, "UTF8" ) {
				public void close() {};
			} );
			tree.toXml(pw);
			pw.flush();
			zos.closeEntry();
			if( t.isSet() ) {
				t.reset();
				log.debug( "Consolidated " + counter + " items for " + getPublishingOrganization().getName() );
				setStatusMessage("Consolidated " + counter + " items." );
				DB.commit();
			}
			DB.getSession().evict(n);
		}
		zos.flush();
		zos.close();
		t.cancel();
		log.debug( "Consolidated " + counter + " items for " + getPublishingOrganization().getName() );
		return consolidated;
	}
	
	/**
	 * Do some processing after the consolidation??
	 * @throws Exception
	 */
	public File postProcess( File input ) throws Exception {
		return input;
	}
	
	/**
	 * Not sure what I need this yet. Somehow the ZIP approach to store item XML
	 * might be performance desaster, I don't trust it will last. Final version
	 * probably is memory mapped big file with index file. 
	 * @return
	 * @throws IOException
	 */
	public static File createTempDirectory() throws IOException {
		final File temp;

		temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

		if(!(temp.delete()))
		{
			throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
		}

		if(!(temp.mkdir()))
		{
			throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
		}

		return (temp);
	}

	
	/**
	 * Write all input items into the workdir in some form. It could be a million, so files in one dir
	 * is not a good approach ( doesn't scale ).
	 *  - try zip archive??
	 *  - subdir approach ?
	 *  - one file with index file ?
	 *  
	 * @throws Exception
	 */
	public Iterator<NodeContainer> itemize() throws Exception {
		return null;
	}
	
	/**
	 * This method write a line with different mapping of item and item parts
	 * to sortable (float, string) values. Sorting by these values and comparing
	 * adjacient values establishes how similar the items
	 * are for that indexed value and this similarity will be used for item to item
	 * scoring.
	 * 
	 * Standard is to write item_id and checksum out, so that duplication can be eliminated.
	 * 
	 * @param tree
	 * @param out
	 */
	public void writeIndexLine( XMLNode tree, PrintWriter out ) {
		out.write( tree.getNodeId()+"\t" );
		out.write( tree.getChecksum() + "\n");
	}
	
	/**
	 * Score two values in given column against each other. If they are considered too far
	 * for scoring throw Exception to advance the scoring window.
	 * 
	 * Default is identity scoring (1 if identical, Exception if not)
	 * @param column
	 * @param item1Value
	 * @param item2Value
	 * @return
	 * @throws Exception
	 */
	public float partialScore( int column, String item1Value, String item2Value ) throws
		Exception {
		if( item1Value.equals( item2Value )) return 1f;
		throw new Exception();
	}
	
	/**
	 * If a column is numeric, the sorting will happen numerically otherwise
	 * lexically. Usually indices will be numeric unless the score is an identity check.
	 * 
	 * The default is the identity check index.
	 * @param column
	 * @return
	 */
	public boolean isNumericIndex( int column ) {
		return false;
	}
	
	/**
	 * First creates the scores for each attribute that needs to be scored.
	 * Then combines the scores for each pair. 
	 */
	public void buildScoringMatrix() {
		/*
		 * for column=1 to index count
		 *   sort index file by column
		 *   create scoring file
		 *   for line in index file
		 *     addLineToWindow( window, line, scoreColumn, scoreWriter )
		 * sort scoring files on first and second id
		 * open each scoring file and walk through, calling the 
		 * score accumulating function with all scores for one pair of 
		 * ids.
		 */
		
	}
	
	/**
	 * Scores added line against all other lines in the window and removes
	 * lines from the window that are no longer in scoring range.
	 * Writes scores to partialScores and assumes tab delimited files with
	 * node id on first position (0-column)
	 * @param window
	 * @param line
	 * @param scoreColumn
	 * @param partialScores
	 */
	private void addLineToWindow( LinkedList<String[]> window, String line, int scoreColumn, PrintWriter partialScores ) {
		String[] fields = line.split("\\t");
		window.add(fields);
		ListIterator<String[]> i = window.listIterator();
		while( i.nextIndex() < window.size()-1 ) {
			String comp[] = i.next();
			try {
				float score = partialScore( scoreColumn, fields[scoreColumn], comp[scoreColumn]);
				long id1 = Long.parseLong(fields[0]);
				long id2 = Long.parseLong( comp[0] );
				if( id1 < id2 )
					partialScores.println( fields[0]+ "\t"+ comp[0] + "\t" + score );
				else
					partialScores.println( comp[0]+ "\t"+ fields[0] + "\t" + score );
			} catch( Exception e ) {
				// this line is out of the window
				i.remove();
			}
		}		
	}
	
	
	/**
	 * How many partial scores do you want to produce? Default is 
	 * 1 for the checksum identity of item.
	 * @return
	 */
	public int getIndexCount() {
		return 1;
	}
	
	/**
	 * The given file (which needs to be a ZIP archive) is written back as 
	 * BLOB to the database.
	 * @param result
	 */
	public void writeBack( File result ) {
		try {
		zippedOutput = new BlobWrap();
		zippedOutput.data = Hibernate.createBlob( new FileInputStream( result ), (int) result.length());
		setStatusCode(OK);
		DB.commit();
		// result.delete();
		} catch( Exception e  ) {
			log.error( "Writeback failed!", e );
			try {
				setStatusCode(ERROR);
				setStatusMessage(e.getMessage());
				DB.commit();
			} catch( Exception e2 ) {
				log.error( "Status update failed as well!!", e2 );
			}
		}
	}
	
	public File getTmpFile(){
		return this.tmpFile;
	}
	
	public void unloadToTmpFile() {
		try {
			tmpFile = File.createTempFile("unloadPublication", ".zip");
			tmpFile.deleteOnExit();
			log.info( "Unloading to " + tmpFile.getAbsolutePath());
			FileOutputStream fos = new FileOutputStream( tmpFile );
			BufferedOutputStream bos = new BufferedOutputStream( fos,4096 );
			
			InputStream is = getZippedOutput().getData().getBinaryStream();
			IOUtils.copy(is, bos);
			is.close();
			bos.flush();
			bos.close();
			DB.commit();
		} catch( Exception e ) {
			log.error( "Cannot copy BLOB to tmp file", e );
		}
	}
	
	/**
	 * Returns a stream to a zip archive. Please cleanup after finished with the Stream. 
	 * @return
	 */
	public InputStream getDownloadStream() {
		InputStream is = null;		
		if( tmpFile == null )
			unloadToTmpFile();
		try {
			is = new FileInputStream(tmpFile);
		} catch( Exception e ) {
			log.error( "File unload problem", e);
		}
		return is;
	}

	/**
	 * delete the tmp file after using the Download Stream. This will be automated later.
	 */
	public void cleanup() {
		tmpFile.delete();
	}

}

/*
 * How should the process work?
 *  a) Collect all the items from the transformations, building an index of each item, which should allow for the following:
 *    - access each item 
 *    - score items against each other, the index might contain many columns with scores on certain metrics
 *      scores between items are only build from neighboring items in the index (avoid n^2 complexity)
 *    - the collection is happening as XML in files! - current approach, one ZIP archive, but this might not work for
 *      millions of items
 *    
 *  b) .. skip other steps so far ..
 *  c) post process by XSL transform to ESE
 *  d) final result is uploaded as ZIP archive to database. 
*/