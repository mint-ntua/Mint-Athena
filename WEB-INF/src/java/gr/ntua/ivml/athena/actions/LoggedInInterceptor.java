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

package gr.ntua.ivml.athena.actions;

import gr.ntua.ivml.athena.db.DB;
import gr.ntua.ivml.athena.persistent.User;
import gr.ntua.ivml.athena.util.Config;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.StrutsStatics;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * The LoggedInInterceptor should be part of the stack for pages
 * that are only accessible for users that are logged in!
 * It redirects to the "logon" result which should allow
 * you to log in. It should not however use this Interceptor (loop!)
 * 
 * @author arne
 *
 */
public class LoggedInInterceptor extends AbstractInterceptor {
	public static final Logger log = Logger.getLogger( LoggedInInterceptor.class );
	@Override
	public String intercept(ActionInvocation invocation ) throws Exception {
		GeneralAction ga = (GeneralAction) invocation.getAction();
		log.debug( "Name " + invocation.getInvocationContext().getName());
		HttpServletRequest request = (HttpServletRequest) invocation.getInvocationContext().get(StrutsStatics.HTTP_REQUEST);
		HttpSession httpSession = request.getSession();
		ga.setSessionId(httpSession.getId());
		Map<String, Object> s = invocation.getInvocationContext().getSession();
		User u = (User) s.get( "user" );
		if( u == null ){
			 if( Config.get( "autoAdminLogin") != null ) {
				 u = DB.getUserDAO().findById(1000l, false);
			 } else {
				 return "logon";				 
			 }
		} else {
		
			// not good enough, not deep: DB.getSession().update(u);
			u = DB.getUserDAO().findById(u.getDbID(), false);
		}
		ga.setUser( u );
		s.put( "user", u);

		String result =  invocation.invoke();
		return result;
	}
}
