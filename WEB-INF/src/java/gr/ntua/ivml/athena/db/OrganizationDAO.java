package gr.ntua.ivml.athena.db;


import gr.ntua.ivml.athena.persistent.Organization;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.UniqueConstraint;

public class OrganizationDAO extends DAO<Organization, Long> {
	
	public List<Organization> findPrimary() {
		List<Organization> result = Collections.emptyList();
		try {
			result = getSession().createQuery(" from Organization where parentalOrganization is null" ).list();
		} catch( Exception e ) {
			log.error( "Problems: ", e );
		}
		return result;
	}
	
	public	Organization findByName( String name ) {
		Organization result = null;
		try {
			result = (Organization) getSession()
				.createQuery(" from Organization where shortName=:name" )
				.setString("name", name )
				.uniqueResult();
		} catch( Exception e ) {
			log.error( "Problems: ", e );
		}
		return result;
	}

	public List<Organization> findByCountry( String country ) {
		List<Organization> result = null;
		result = getSession()
			.createQuery("from Organization where country=:country " 
						+" order by englishName" )
			.setString("country", country ) 
			.list();
		return result;
	}
	
	public List<Organization> findAll() {
		List<Organization> result = null;
		result = getSession()
			.createQuery("from Organization " 
						+" order by englishName" )
			.list();
		return result;
	}
}
