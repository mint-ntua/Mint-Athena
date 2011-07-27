   <table width="100%" cellspacing="0" cellpadding="0">
   <tr><td>
   <div class="vertalign">
		    	 <s:if test="trans.status=='IDLE' || trans.status=='WRITING' || trans.status=='UPLOADING' || trans.status=='INDEXING'">
			 	  <br><br>Transforming...
				 </s:if>
				  <s:elseif test="trans.status=='OK'">
	        	   <div class="imgteaser">

						<a href="javascript:ajaxItemLevelLabelRequest(<s:property value="trans.dbID"/>, <s:property value="orgId"/>,<s:property value="userId"/>,true)">
							<img src="images/AddRootItem.png" width="21" style="vertical-align:middle;" alt="Set item root & label elements">
							<span class="desc">
								Item Root
							</span>
						</a>
					 </div>
				   <div class="iroot">
					 <s:if test="rootDefined">
					   <img id="root_stat<s:property value="dbID"/>" src='images/okblue.png' style="vertical-align:middle;margin-left: 0px;" title="Item root defined">
					    <script>
					    var tooltiproot<s:property value="dbID"/> = new YAHOO.widget.Tooltip("tooltiproot<s:property value="dbID"/>", { context:"root_stat<s:property value="dbID"/>" , text:"Item root defined", width:"400px"} );
					
					    </script>
					   </s:if>
					 <s:else>
					    <img id="root_stat<s:property value="dbID"/>" src='images/help.png' width="20" style="vertical-align:middle;margin-left: 0px;" title="Item root not defined">
					    <script>
					    var tooltiproot<s:property value="dbID"/> = new YAHOO.widget.Tooltip("tooltiproot<s:property value="dbID"/>", { context:"root_stat<s:property value="dbID"/>" , text:"Item root undefined", width:"400px"} );
					
					    </script>
					
					  </s:else>
					</div>	 
					
	        	 </s:elseif>
				 <s:else>
				 
				 <div class="imgteaser">

						<a href="javascript:ajaxItemLevelLabelRequest(<s:property value="trans.dbID"/>, <s:property value="orgId"/>,<s:property value="userId"/>,false)">
							<img src="images/AddRootItem.png" width="21"  alt="Set item root & label elements">
							<span class="desc">
								Item Root
							</span>
						</a>
				 </div>
				  <div class="iroot">
					 <s:if test="rootDefined">
					   <img id="root_stat<s:property value="dbID"/>" src='images/okblue.png' style="vertical-align:middle;margin-left: 0px;" title="Item root defined">
					    <script>
					    var tooltiproot<s:property value="dbID"/> = new YAHOO.widget.Tooltip("tooltiproot<s:property value="dbID"/>", { context:"root_stat<s:property value="dbID"/>" , text:"Item root defined", width:"400px"} );
					
					    </script>
					   </s:if>
					 <s:else>
					    <img id="root_stat<s:property value="dbID"/>" src='images/help.png' width="20" style="vertical-align:middle;margin-left: 0px;" title="Item root not defined">
					    <script>
					    var tooltiproot<s:property value="dbID"/> = new YAHOO.widget.Tooltip("tooltiproot<s:property value="dbID"/>", { context:"root_stat<s:property value="dbID"/>" , text:"Item root undefined", width:"400px"} );
					
					    </script>
					
					  </s:else>
					</div>	 
					
				 </s:else>
				 
				   
				
					<s:if test="trans.status=='IDLE' || trans.status=='WRITING' || trans.status=='UPLOADING' || trans.status=='INDEXING'"> 
					<span><img src="images/lock.gif" style="vertical-align:middle;"/></span>
					</s:if>
					<s:else>
					   <div class="imgteaser">    
					     <a href="javascript:ajaxThesauriLevelLabelRequest(<s:property value="trans.dbID"/>, <s:property value="orgId"/>,<s:property value="userId"/>)" title="Specify Thesauri"><img src="images/thesaurus.png">
					      <span class="desc"> 
					        Thesauri
					      </span>
					     </a>
					   </div>
					   <div class="imgteaser">			
					      <a href="javascript:ajaxMappingDefinitionRequest(<s:property value="trans.dbID"/>, <s:property value="orgId"/>,<s:property value="userId"/>)" title="Open mapping editor"><img src="images/test-matching.gif" width="28">
					       <span class="desc"> 
					       Mapping
					      </span>
					      </a>
					   </div>
					  </s:else>
				
			
				
				
				 <span style="display:none">
				<s:property value="trans.status"/>
				</span>
				
				 <%-- <font color="red"><b>3.</b></font>--%>
				 <div class="imgteaser">	
				 <s:if test="trans.status=='OK' || trans.status=='ERROR' || trans.status=='NOT DONE' || trans.isStale==true">
				  		
				 <a href="javascript:ajaxtransformRequest(<s:property value="trans.dbID"/>, <s:property value="orgId"/>,<s:property value="userId"/>)" title="Transform import"><img src="images/xsl.png" width="28">
				 <span class="desc"> 
					      Transform
					 </span>
				 </a>
				
				  </s:if>
				 </div>
				  <div class="trans">  
					 <s:if test="trans.status!='NOT DONE'">
					   <img id="context_stat<s:property value="trans.dbID"/>" src='<s:property value="trans.statusIcon"/>' style="margin-right:0px;" title="TRANSFORMATION <s:property value="trans.status"/>">
					    <script>
					    var tooltiptrans<s:property value="trans.dbID"/> = new YAHOO.widget.Tooltip("tooltiptrans<s:property value="trans.dbID"/>", { context:"context_stat<s:property value="dbID"/>" , text:"<s:property value="trans.message"/>", width:"400px"} );
					    </script>
					   </s:if>
				  <s:if test="trans.status=='OK' && (user.getAthenaRole().equalsIgnoreCase('ADMIN') || user.getAthenaRole().equalsIgnoreCase('SUPERUSER')  || uploader==user.dbID)">
						     <a href="download.action?dbId=<s:property value="trans.dbID"/>&transformed=true"> <img src="images/download2b.png" width="18" style="padding-left: 0px;" title="Download transformed items"></a>
						     <a href="javascript:ajaxDeleteTransform(<s:property value="trans.dbID"/>);"><img src="images/trash_can.png" width="20" style="padding-left: 0px; margin-top: -2px;" title="Delete tranformation"></a>
					   </s:if>
				    </div>
				 
	</div>
	</td></tr>
	
	</table>