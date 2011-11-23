package gr.ntua.ivml.athena.test;

import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.db.UserDAO;
import gr.ntua.ivml.athena.persistent.User;

import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.hibernate.Session;

public class TestDbConnection extends TestCase {
	Logger log = Logger.getLogger( TestDbConnection.class );
	
	public void testConnection() {
		Session s = DB.getSession();
		UserDAO ud = DB.getUserDAO();
		List<User> l = ud.findAll();
		log.info( "Got " + l.size() + " users");
	}
}
