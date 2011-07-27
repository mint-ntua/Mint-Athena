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

package gr.ntua.ivml.athena.util;

import javax.mail.*;          //JavaMail packages
import javax.mail.internet.*; //JavaMail Internet packages


import java.util.*;           //Java Util packages


public class MailSender {
  
   public String host="";
   public String adminmail="";
	
    
   public MailSender( ) {
      // Load properties 
	
	   this.host= Config.get("mailHost");
	   this.adminmail=Config.get("adminMail") ;	
		   
     
   }
 
   
   /*method called when mail is sent*/
  
  public String send(String p_from, String p_subject, String p_message, String p_to) {
   String l_result = "";
    
    // Name of the Host machine where the SMTP server is running
    
    // Gets the System properties
    Properties l_props = System.getProperties();


    // Puts the SMTP server name to properties object
    l_props.put("mail.smtp.host", host);

    // Get the default Session using Properties Object
    Session l_session = Session.getDefaultInstance(l_props, null);

    l_session.setDebug(true); // Enable the debug mode
 
    try {
      
       MimeMessage l_msg = new MimeMessage(l_session); // Create a New message
       
      l_msg.setFrom(new InternetAddress(p_from)); // Set the From address
      
      // Setting the "To recipients" addresses
      l_msg.setRecipients(Message.RecipientType.TO,
                                  InternetAddress.parse(p_to, false));
    
      l_msg.setSubject(p_subject); // Sets the Subject
 
      // Create and fill the first message part
     
      l_msg.setContent(p_message, "text/html; charset=ISO-8859-1");

     
      // Set the Date: header
      l_msg.setSentDate(new Date());

      // Send the message
      Transport.send(l_msg);
      // If here, then message is successfully sent.
      // Display Success message
      l_result = l_result + "<B>Success!</B>"+
                 "<BR><B>Mail sent to </B>: "+p_to+"<BR>";
      //if CCed then, add html for displaying info
      
      l_result = l_result+"<BR>"; 
    } catch (MessagingException mex) { // Trap the MessagingException Error
        // If here, then error in sending Mail. Display Error message.
       
        l_result = l_result + "<B>Error : </B><BR><HR> "+
                   mex.toString()+"<BR>";
        System.out.println(mex.getMessage());
    } catch (Exception e) {
       
        // If here, then error in sending Mail. Display Error message.
        l_result = l_result + "<B>Error : </B><BR><HR> "+
                   e.toString()+"<BR>";

        System.out.println(e.getMessage());
    }//end catch block
  
   
      return l_result;
    
  } // end of method send
  
  //send with many ccs

  public String sendToMany(String p_from, String p_subject, String p_message, String send_to, String ccs) {
	   String l_result = "";
	    
	    // Name of the Host machine where the SMTP server is running
	    
	    // Gets the System properties
	    Properties l_props = System.getProperties();


	    // Puts the SMTP server name to properties object
	    l_props.put("mail.smtp.host", host);

	    // Get the default Session using Properties Object
	    Session l_session = Session.getDefaultInstance(l_props, null);

	    l_session.setDebug(true); // Enable the debug mode
	 
	    try {
	            MimeMessage l_msg = new MimeMessage(l_session); // Create a New message
			       
			      l_msg.setFrom(new InternetAddress(p_from)); // Set the From address
			      
			      // Setting the "To recipients" addresses
			     
			      l_msg.setRecipients(Message.RecipientType.TO,
			                                  InternetAddress.parse(send_to, false));
			      
			      
			      l_msg.setRecipients(Message.RecipientType.CC,
                          InternetAddress.parse(ccs, false));
			     
			      
			      l_msg.setSubject(p_subject); // Sets the Subject
			 
			      l_msg.setContent(p_message, "text/html; charset=ISO-8859-1");
			      
			     		
			      // Set the Date: header
			      l_msg.setSentDate(new Date());
		
			      // Send the message
			      Transport.send(l_msg);
			      // If here, then message is successfully sent.
			      // Display Success message
			      l_result += l_result + "<B>Success!</B>"+
			                 "<BR><B>Mail sent to </B>: ";
			      l_result += send_to+"<BR>"; 
			     
		
		          l_result+=ccs+"<BR>";
			     
			      //if CCed then, add html for displaying info
			      
			      l_result += l_result+"<BR>";
			      
	    } catch (MessagingException mex) { // Trap the MessagingException Error
	        // If here, then error in sending Mail. Display Error message.
	       
	        l_result = l_result + "<B>Error : </B><BR><HR> "+
	                   mex.toString()+"<BR>";
	        System.out.println(mex.getMessage());
	    } catch (Exception e) {
	       
	        // If here, then error in sending Mail. Display Error message.
	        l_result = l_result + "<B>Error : </B><BR><HR> "+
	                   e.toString()+"<BR>";

	        System.out.println(e.getMessage());
	    }//end catch block
	  
	   
	      return l_result;
	    
	  } // end of method send


  
}

