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

package gr.ntua.ivml.athena.db;

import gr.ntua.ivml.athena.persistent.BlobWrap;
import gr.ntua.ivml.athena.persistent.DataUpload;
import gr.ntua.ivml.athena.persistent.Lock;
import gr.ntua.ivml.athena.persistent.Mapping;
import gr.ntua.ivml.athena.persistent.Organization;
import gr.ntua.ivml.athena.persistent.Publication;
import gr.ntua.ivml.athena.persistent.Thesaurus;
import gr.ntua.ivml.athena.persistent.ThesaurusAssignment;
import gr.ntua.ivml.athena.persistent.Transformation;
import gr.ntua.ivml.athena.persistent.User;
import gr.ntua.ivml.athena.persistent.XMLNode;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

public class DB {
	private static SessionFactory sessionFactory;
	private static TestSetup testSetup;
	private static boolean schemaSetup;
	static final Logger log = Logger.getLogger( DB.class );
	
	//threadlocal, but I rig it myself ..
	private static Hashtable<Long, Session> sessions = new Hashtable<Long,Session>();

	private static ThreadLocal<StatelessSession> statelessSessions = new ThreadLocal<StatelessSession>();
	

	static {
		initSession();
	}

	private static void initSession() {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			// or, like here, from hibernate.properties
			sessionFactory = new AnnotationConfiguration()
			.addClass( User.class )
			.addClass( Organization.class )
			.addClass( DataUpload.class )
			.addClass( BlobWrap.class )
			.addClass( XMLNode.class )
			.addClass( Lock.class )
			.addClass( Mapping.class )
			.addClass( Thesaurus.class )
			.addClass( ThesaurusAssignment.class )
			.addClass( Transformation.class )
			.addClass( Publication.class )
			.buildSessionFactory( );
			
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			log.error("Initial SessionFactory creation failed." , ex);
			throw new ExceptionInInitializerError(ex);
		}
		log.info( "SessionFactory instatiated" );
		
		// load the classes, they do some cleanup
		getLockManager();
		getDataUploadDAO();
		getTransformationDAO();
		new GlobalPrefixStore();
	}

	/*
	 * Should try and open session when request comes in
	public static Session getSession() {
		return sessionFactory.openSession();
	}	
	 */
	public static void setSession( Session s ) {
		long threadId = Thread.currentThread().getId();
		sessions.put( threadId, s );		
	}

	public static void removeSession() {
		long threadId = Thread.currentThread().getId();
		sessions.remove( threadId );
	}
	
	public static Session getSession() {
		long threadId = Thread.currentThread().getId();
		Session s;
		s = sessions.get( threadId );
		if((s == null ) || ( !s.isOpen())) {
			s = sessionFactory.openSession();
			log.info( "Session created!");
			sessions.put( threadId, s );
		}
		return s;
	}	
	
	public static StatelessSession getStatelessSession() {
		StatelessSession ss = statelessSessions.get();
		if( ss == null ) {
			try {
			Connection c  = ((SessionFactoryImpl)sessionFactory).getConnectionProvider().getConnection();
			ss = sessionFactory.openStatelessSession(c);
			log.info( "StatelessSession created!");
			statelessSessions.set( ss );
			} catch( SQLException se ) {
				log.error( "No stateless Session", se );
			}
		}
		return ss;
	}
	
	public static void closeStatelessSession() {
		StatelessSession ss = statelessSessions.get();
		if( ss != null ) {
			try {
				ss.connection().close();
			} catch( SQLException e ) {
				log.error( e );
			}
			ss.close();
			statelessSessions.set( null );
		}
	}
	
	public static Session newSession() {
		closeSession();
		return getSession();
	}
	
	
	public static void closeSession() {
		long threadId = Thread.currentThread().getId();
		Session s = sessions.get( threadId );
		if( s != null ) {
			s.close();
			sessions.remove(threadId );
		}
	}
	
	public static void logPid() {
		Session s = getSession();
		Connection c = s.connection();
		logPid(c );
	}
	
	public static void logPid( Connection c ) {
		try {
		Statement st = c.createStatement();
		st.execute("select pg_backend_pid()");
		ResultSet rs = st.getResultSet();
		rs.next();
		log.debug( "Thread: " + Thread.currentThread().getName() + " pid = " + rs.getInt(1));
		} catch( Exception e ) {
			log.debug( "Cant log transaction id " + e.getMessage());
		}
	}
	
	

	
	// test to write out current transaction (and create new one)
	public static void commit() {
		getSession().flush();
		getSession().getTransaction().commit();
		getSession().beginTransaction();
	}
	
	public static LockManager getLockManager() {
		return new LockManager();
	}
	
	public static UserDAO getUserDAO() {
		return (UserDAO) instantiateDAO( UserDAO.class );
	}

	public static TransformationDAO getTransformationDAO() {
		return (TransformationDAO) instantiateDAO( TransformationDAO.class );
	}

	public static XMLNodeDAO getXMLNodeDAO() {
		return (XMLNodeDAO) instantiateDAO( XMLNodeDAO.class );
	}

	public static XpathHolderDAO getXpathHolderDAO() {
		return (XpathHolderDAO) instantiateDAO( XpathHolderDAO.class );
	}

	public static OrganizationDAO getOrganizationDAO() {
		return (OrganizationDAO) instantiateDAO( OrganizationDAO.class );
	}
	
	public static XmlObjectDAO getXmlObjectDAO() {
		return (XmlObjectDAO) instantiateDAO( XmlObjectDAO.class );
	}
	
	public static DataUploadDAO getDataUploadDAO() {
		return (DataUploadDAO) instantiateDAO( DataUploadDAO.class );
	}

	public static MappingDAO getMappingDAO() {
		return (MappingDAO) instantiateDAO( MappingDAO.class );
	}

	public static PublicationDAO getPublicationDAO() {
		return (PublicationDAO) instantiateDAO( PublicationDAO.class );
	}	

	public static ThesaurusDAO getThesaurusDAO() {
		return (ThesaurusDAO) instantiateDAO( ThesaurusDAO.class );
	}
	
	public static ThesaurusAssignmentDAO getThesaurusAssignmentDAO() {
		return (ThesaurusAssignmentDAO) instantiateDAO( ThesaurusAssignmentDAO.class );
	}


	private static DAO instantiateDAO(Class<? extends DAO> daoClass) {
        try {
            DAO dao = (DAO)daoClass.newInstance();
            return dao;
        } catch (Exception ex) {
            throw new RuntimeException("Can not instantiate DAO: " + daoClass, ex);
        }
    }

	public static void testSetup() {
		if( testSetup != null ) return;
		testSetup = new TestSetup();
	}
	
	private static StringBuffer readFile( String file ) throws IOException  {
		StringBuffer sb = new StringBuffer();
		InputStream is = DB.class.getClassLoader().getResourceAsStream(file);
		BufferedReader br = new BufferedReader( new InputStreamReader( is, Charset.forName( "UTF-8" )));
		String buffer;
		while((buffer = br.readLine()) !=  null) { 
			sb.append( buffer );
			sb.append( "\n" );
		}
		
		return sb;
	}
	
	public static void doSQL( String filename ) {
		try {
			StringBuffer sb = readFile( filename );
			Transaction t = DB.getSession().beginTransaction();
			DB.getSession().createSQLQuery(sb.toString()).executeUpdate();
			t.commit();
		} catch( Exception e ) {
			throw new IOError( e );
		}
	}

	public static void flush() {
		getSession().flush();
	}
	
	public static void initSchema() {
		doSQL( "createSchema.sql" );
	}
}


