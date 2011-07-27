<table border="0" cellspacing="0" cellpadding="0" width="100%">
										<tr>
										<td align="right">
										<s:if test="lido==true && status=='OK'">
										<img id="context<s:property value="dbID"/>" src="images/athenatrans.gif" width="16" height="18" style="vertical-align:middle;" title="<s:property value="status"/>">
										</s:if>
										<s:else>
										<img id="context<s:property value="dbID"/>" src='<s:property value="statusIcon"/>' style="vertical-align:middle;" title="<s:property value="status"/>">
										</s:else>
										<script>
										var tooltip<s:property value="dbID"/> = new YAHOO.widget.Tooltip("tooltip<s:property value="dbID"/>", { context:"context<s:property value="dbID"/>" , text:"<s:property value="formattedMessage"/>", width:"400px"} );
										
										</script>
										</td> 
										<td width="20"><s:if test="status=='OK'">
										<a onclick="javascript:ajaxItemPanel(0, 10, <s:property value="orgId"/>, <s:property value="dbID"/>,<s:property value="userId"/>);" href="#" class="" title="show items">
										<img style="vertical-align: middle; " src="images/items.png"></a></s:if>
										</td>
										
										
										<td width="20"><s:if test="status=='OK'">
											<img id="stats<s:property value="dbID"/>" title="Import statistics" src="images/stats2.png" width="18" style="vertical-align:middle;">
											<script>
											var helloWorld = function(e) { 
											    var ur = "Stats?uploadId=<s:property value="dbID"/>";
											    myRef = window.open(""+ur,"mywin","left=20,top=20,width=1024,height=510,toolbar=0,resizable=1");
											}
											YAHOO.util.Event.addListener("stats<s:property value="dbID"/>", "click", helloWorld);
											</script>
											<!--<a href="stats.action?uploadId=<s:property value="importId"/>" title="Statistics" > <img src="images/stats.png" style="vertical-align:middle;"></a>-->
										    </s:if>
										</td>
										<td width="20"><s:if test="status=='OK' && (user.getAthenaRole().equalsIgnoreCase('ADMIN') || user.getAthenaRole().equalsIgnoreCase('SUPERUSER')  || uploader==user.dbID)">
										     <a href="download.action?dbId=<s:property value="dbID"/>" title="Download import" > <img src="images/download2.png" width="18" style="vertical-align:middle;"></a>
										     </s:if>
										     <s:else>&nbsp;</s:else>
										</td>
										</tr>
										</table>