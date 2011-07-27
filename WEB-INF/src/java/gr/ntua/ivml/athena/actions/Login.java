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
import gr.ntua.ivml.athena.persistent.Organization;
import gr.ntua.ivml.athena.persistent.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.dispatcher.ServletActionRedirectResult;
import org.apache.struts2.interceptor.SessionAware;

@Results({
	  @Result(name="input", location="login.jsp"),
	  @Result(name="error", location="login.jsp"),
	  @Result(name="success", location="Home.action", type="redirectAction" )
	})

public class Login extends GeneralAction implements SessionAware{

	protected final Logger log = Logger.getLogger(getClass());
	
	 
    private String username;
    private Map session;
    
  @Action(value="Login",interceptorRefs=@InterceptorRef("defaultStack")) 
    public String execute() throws Exception {
    	
    	User user;
    	if( username== null || username.length()==0) {
    		addFieldError("username", "Username is required");
    		
    	    	}
		if( password== null || password.length()==0) {
    		addFieldError("password","Password is required");
    	
    	}
		
    	if(!getFieldErrors().isEmpty()){
    		return ERROR;
    		
    	}
		user=DB.getUserDAO().getByLoginPassword(getUsername(),getPassword());
		if(user!=null){
			if(!user.isAccountActive()){
				addActionError("account is no longer active");
				return ERROR;
			}
			else if(user.getPasswordExpires()!=null && user.getPasswordExpires().getTime()<(new Date().getTime())){
				addActionError("your password has expired");
				return ERROR;
			}
			else{
				log.debug( "Login successful for user:"+user.getLogin() );
				user.getJobRole();
				getSession().put("user", user);	
				}
	   }
		else
		{    addActionError("wrong login/password combination");
			return ERROR;
		}
		return SUCCESS;
      }


 @Override
    @Action(value="Login_input",interceptorRefs=@InterceptorRef("defaultStack"))  
    public String input() throws Exception {
    	return super.input();
    }
    
    public String getUsername() {
    	
        return username;
    }

    public void setUsername(String username) {
    	
        this.username = username;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
    public void setSession(Map session) {
        this.session = session;
      }
      
      public Map getSession() {
        return session;
      }
      
      
}