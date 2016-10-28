<%@ include file="TagLibIncludes.jsp" %>
<c:set var="uuid" value="${param.uuid}"/>
<c:set var="startingOffset" value="${empty param.s ? '0' : param.s}"/>
<c:set var="numRecordsToDisplay" value="200"/>
<%pageContext.setAttribute("linefeed", "\n"); %>
<c:catch var="sqlDbError">
	<sql:setDataSource 
		var="logsDB"
		user="${initParam.harvestDbUser}" 
		password="${initParam.harvestDbPwd}"
		url="${initParam.harvestDbUrl}/harvest?zeroDateTimeBehavior=convertToNull"
		driver="com.mysql.jdbc.Driver" />
		
	<sql:query var="logEntries" dataSource="${logsDB}">
		SELECT * 
		FROM `ingest_log` 
		WHERE uuid = '${uuid}'
		ORDER BY `ingest_log`.`log_id` DESC
		LIMIT 2000
	</sql:query>
</c:catch>

<c:set var="totalReturned" value="${empty logEntries ? 0 : logEntries.rowCount}"/>

<%-- ------ Begin paging logic ------ --%>

<c:set value="doLogDisplay('${startingOffset + numRecordsToDisplay}')" var="nextJs" />
<c:set value="doLogDisplay('${startingOffset - numRecordsToDisplay}')" var="prevJs"/>

<c:set var="pager">
	<div style="padding-bottom: 5px">
		<c:if test="${(startingOffset) > 0}">
			<nobr><a href='#logs' onclick="${prevJs}">&lt;&lt; Prev</a> &nbsp;</nobr>
		</c:if>
		log
		<c:out value="${startingOffset +1}"/> 
		-
		<c:if test="${startingOffset + numRecordsToDisplay > totalReturned}">
			<c:out value="${totalReturned}"/>
		</c:if>
		<c:if test="${startingOffset + numRecordsToDisplay <= totalReturned}">
			<c:out value="${startingOffset + numRecordsToDisplay}"/>
		</c:if>						
		out of <c:out value="${totalReturned}"/>			
		
		<c:if test="${(startingOffset + numRecordsToDisplay) < totalReturned}">
			<nobr>&nbsp; <a href='#logs' onclick="${nextJs}">Next &gt;&gt;</a></nobr>
		</c:if>
	</div>
</c:set>

<%-- ------ end paging logic ------ --%>

<%-- <p>Harvest logs for UUID: ${uuid}</p> --%>

<div style="margin-top:2px">	
	<c:choose>
		<c:when test="${not empty logEntries && totalReturned > 0}">
			${pager}
			<table border="0" cellpadding="5" cellspacing="1">
				<thead>
					<tr class="headrow" style="text-align:center">
						<td>ID/Level</td>
						<td>Message</td>
					</tr>
				</thead>
				<tbody>
					<c:set var="curCount" value="${1}"/>
					<c:forEach var="logRow" items="${logEntries.rows}">
						<c:if test="${ (curCount > startingOffset) && (curCount < startingOffset+numRecordsToDisplay+1) }">					
							<tr class="bodyrow bodyrowLogs">
								<td>${logRow.message_level}<br/><nobr>${logRow.log_timestamp}</nobr></td>
								<td class="log_message">${fn:replace(logRow.message_text, linefeed, "<br/>")}</td>
							</tr>
						</c:if>
						<c:set var="curCount" value="${curCount+1}"/>
					</c:forEach>
				</tbody>
			</table>
			<br/>
			${pager}
		</c:when>
		<c:otherwise>
			<p>No logs for this harvest have been recorded yet.</p>
		</c:otherwise>
	</c:choose>
	
	<c:if test="${not empty sqlDbError}">
		<!--	
			<div>Error:</div>
			<pre>${sqlDbError}</pre> 
		-->
	</c:if>

</div>




