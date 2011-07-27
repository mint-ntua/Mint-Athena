 <%@ taglib prefix="s" uri="/struts-tags" %>
 <span style="display:none">
	<s:property value="pub.status"/>
 </span>
 <table border="0" cellspacing="0" cellpadding="0" width="100%"><tr><td><br/></td></tr>
		         <tr>
		         
		    	 <s:if test="pub.status=='IDLE' || pub.status=='CONSOLIDATE' || pub.status=='VERSION' || pub.status=='POSTPROCESS'">
		    	 <td align="center" vertical-align="middle">
			 	  <br>Publishing...
				 	 <span><img src="images/lock.gif" style="vertical-align:middle;"/></span> 
					<img id="pub_stat<s:property value="pub.dbID"/>" src='<s:property value="pub.statusIcon"/>' style="vertical-align:middle;margin-left: -7px;" title="PUBLICATION <s:property value="pub.status"/>">
					<script>
				    	var tooltiptrans<s:property value="pub.dbID"/> = new YAHOO.widget.Tooltip("tooltiptrans<s:property value="pub.dbID"/>", { context:"pub_stat<s:property value="pub.dbID"/>" , text:"<s:property value="pub.message"/>", width:"400px"} );
					</script>
					<br><br>
					</td>
				 </s:if>
				  <s:elseif test="pub.status=='OK'  && (user.getAthenaRole().equalsIgnoreCase('ADMIN') || user.getAthenaRole().equalsIgnoreCase('SUPERUSER')  || uploader==user.dbID)">
				  <td align="left" vertical-align="middle">
				     <a href="download.action?dbId=<s:property value="trans.dbID"/>&transformed=true" title="Download tranformed items" > <img src="images/lidodown.png" width="30" height="23" style="vertical-align:middle;margin-left: -3px;">Download LIDO</a>
				     <br/>
				     <span style="padding-left:5px;font-size:0.9em;">*Published import. To edit this import you must first unpublish it. </span>
				   </td>
				  </s:elseif> 
				  <s:elseif test="pub.status=='ERROR'">
				  <td align="left" vertical-align="middle">
				    <img id="pub_stat<s:property value="pub.dbID"/>" src='<s:property value="pub.statusIcon"/>' style="vertical-align:middle;margin-left: -7px;" title="PUBLICATION <s:property value="pub.status"/>">
					<script>
					var tooltiptrans<s:property value="pub.dbID"/> = new YAHOO.widget.Tooltip("tooltiptrans<s:property value="pub.dbID"/>", { context:"pub_stat<s:property value="pub.dbID"/>" , text:"<s:property value="pub.message"/>", width:"400px"} );
					
					</script> Publication failed. Please try again.
					</td>
				  </s:elseif>
				  <s:elseif test="pub.status=='NOT DONE'">
				  <td align="left" vertical-align="middle">
				    <img id="pub_stat<s:property value="pub.dbID"/>" src='<s:property value="pub.statusIcon"/>' style="vertical-align:middle;margin-left: -7px;" title="PUBLICATION <s:property value="pub.status"/>">
					<script>
					var tooltiptrans<s:property value="pub.dbID"/> = new YAHOO.widget.Tooltip("tooltiptrans<s:property value="pub.dbID"/>", { context:"pub_stat<s:property value="pub.dbID"/>" , text:"<s:property value="pub.message"/>", width:"400px"} );
					
					</script> Publication failed. Please try again.
					</td>
				  </s:elseif>
				
								
				</tr>			                    		              
  </table>