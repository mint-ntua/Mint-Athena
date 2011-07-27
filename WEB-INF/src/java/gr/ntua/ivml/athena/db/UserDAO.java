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


import java.util.List;

import gr.ntua.ivml.athena.persistent.User;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.hibernate.type.Type;

public class UserDAO extends DAO<User, Long> {
	public User getByLoginPassword( String login, String password ) {
		User u;
		u = new User();
		u.encryptAndSetLoginPassword(login, password);
		Example e = Example.create( u );
		e.setPropertySelector( new Example.PropertySelector() {
			public boolean include( Object value, String name, Type type ) {
				if( name.equals( "login") || name.equals( "md5Password" ))
					return true;
				else
					return false;
			}
		});
		Session s = getSession();
		Transaction t = s.beginTransaction();
		u = (User) s.createCriteria(User.class)
		.add( e )
		.uniqueResult();
		t.commit();
		return u;
	}
	

	
	public User getByLogin( String login) {
		List<User> users = getSession().createQuery( "from User where login=:login")
		.setString( "login", login )
		.setFetchSize(1)
		.list();
		
		if((users == null) || (users.size() == 0))
			return null;
		
		return users.get(0);
	}
	
	public boolean isLoginAvailable( String login ) {
		Long l = (Long) getSession().createQuery( "select count(*) from User where login=:login")
		.setString( "login", login )
		.iterate().next();
		return ( l.longValue() == 0l );
	}
 }
