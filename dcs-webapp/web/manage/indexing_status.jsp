	<c:set var="showIndexingMessages">${param.command == 'showIndexingMessages'}</c:set>
	<%-- ####### Display messages, if present ####### --%>
	<logic:messagesPresent>
	
	
	
	<table width="99%" bgcolor="#000000" cellspacing="1" cellpadding="0">
	  <tr bgcolor="ffffff"> 
		<td style="padding:8px">
			
				<%-- <html:messages id="msg" property="showIndexMessagingLink"> 
					Indexing messages are displayed below<br><br>
				</html:messages>	 --%>		
				
				<c:if test="${showIndexingMessages}">
					<b>Actions:</b>
					<ul>
						<html:messages id="msg" property="showIndexMessagingLink">
							<form action="collections.do">
								<li class="actions">
									<bean:write name="msg"/>
									<button class="smallButton" type="submit">Refresh indexing status</button> - Refresh the indexing messages.
									<input name="command" value="showIndexingMessages" type="hidden"/>
									<%-- <input name="page" value="index" type="hidden"/> --%>
								</li>
							</form>							
						</html:messages>
						<form>
							<li class="actions"><button class="smallButton" type="submit"
												 onclick="window.location='collections.do';return false">Close messages</button></li>
						</form>
					</ul>
				</c:if>			

				<b>${showIndexingMessages ? 'Indexing status messages:' : 'Messages'}</b>
				<c:if test="${showIndexingMessages}">
					<div class="msgList">
				</c:if>
					<html:messages id="msg" property="message"> 
						<div><ul><li><bean:write name="msg"/></li></ul></div>							
					</html:messages>
					<html:messages id="msg" property="error"> 
						<div><ul><li style="color:red;">Error: <bean:write name="msg"/></li></ul></div>									
					</html:messages>
					<%-- <html:messages id="msg" property="showIndexMessagingLink"> 
						<logic:greaterThan name="raf" property="numIndexingErrors" value="0">
							<li>Some records had errors during indexing.
							<a href="report.do?q=error:true&s=0&report=Files+that+could+not+be+indexed+due+to+errors">See 
							list of errors</a></li>
						</logic:greaterThan>
					</html:messages>  --%>				
				<c:if test="${showIndexingMessages}">
					</div>
				</c:if>
				
				<c:if test="${!showIndexingMessages}">
					<b>Actions:</b>
					
					<ul>
						<c:if test="${param.command == 'reindexCollection'}">
							<form action="collections.do">
								<li class="actions">
									<button class="smallButton" type="submit">Check indexing status</button> - View indexing status messages.
									<input name="command" value="showIndexingMessages" type="hidden"/>
								</li>
							</form>							
						</c:if>
						<form>
							<li class="actions"><button class="smallButton" type="submit">Close messages</button></li>
						</form>						
					</ul>
				</c:if>	
		</td>
	  </tr>
	</table>
	
	<br><br>
	</logic:messagesPresent>	

