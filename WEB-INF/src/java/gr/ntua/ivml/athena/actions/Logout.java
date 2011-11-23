
package gr.ntua.ivml.athena.actions;

import gr.ntua.ivml.athena.db.DB;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;

public class Logout extends GeneralAction implements SessionAware {

	protected final Logger logger = Logger.getLogger(getClass());

    private Map session;

    
      public String execute() throws Exception {
		getSession().clear();
		DB.getLockManager().releaseLocks(getSessionId());
		return SUCCESS;
      }

     public void setSession(Map session) {
        this.session = session;
      }
      
      public Map getSession() {
        return session;
      }
}