<%@ include file="/JSTLTagLibIncludes.jsp" %>
<resp:setHeader name="Cache-Control">cache</resp:setHeader>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<html:html>
<head>
<title><st:pageTitle title="Validation Reports" /></title>

<%@ include file="/baseHTMLIncludes.jsp" %>
<script>
	// Override the method below to set the focus to the appropriate field.
	function sf(){}

</script>
</head>
<body>

<st:pageHeader currentTool="manage" toolLabel="Validation Reports" />

<st:breadcrumbs>
	<a href="manage-home.jsp">Manage</a>
	<st:breadcrumbArrow />
	<a href="collections.do">Collections</a>
	<st:breadcrumbArrow />
	<span class="current">Validation Reports</span>
</st:breadcrumbs>

<div align="center" style="padding:10px">
		<input type="button" value="Validate"
			onclick="window.location='${contextPath}/manage/collections.do?command=validate&setup=true'" />
			
		<input type="button" value="Return to manage collections"	
			onclick="window.location='${contextPath}/manage/collections.do?command=validate&exit=true'" />
</div>

	<%-- ######## Display messages, if present ######## --%>
	<logic:messagesPresent> 		
	<table width="90%" bgcolor="#000000" cellspacing="1" cellpadding="8">
	  <tr bgcolor="ffffff"> 
		<td>
			<b>Messages:</b>
			<ul>
				<html:messages id="msg" property="message"> 
					<li><bean:write name="msg"/></li>									
				</html:messages>
				<html:messages id="msg" property="error"> 
					<li><font color=red>Error: <bean:write name="msg"/></font></li>									
				</html:messages>
				<html:messages id="msg" property="authError"> 
					<li><font color=red>Error: <bean:write name="msg"/></font></li>
					<blockquote>[ <a href="collections.do?command=validate&exit=true">OK</a> ]</blockquote>							
				</html:messages>
			</ul>
		</td>
	  </tr>
	</table>		
	</logic:messagesPresent>
	
	<c:choose>
		<c:when test="${empty csForm.archivedReports}">
			<p>There are no archived validation reports to show</p>
		</c:when>
		
		<c:otherwise>
			<p>There are ${fn:length(csForm.archivedReports)} validation reports available. Click on the collection name in the table 
			below to see a more detailed report.</p>
			<div align="center">
			<table bgcolor="#666666" cellpadding="6" cellspacing="1" border="0">
				<tr align="center" bgcolor="#CCCEE6">
					<td align="left" ><b>Collection</b></div></td>
					<td><b>Time Stamp</b></td>
					<td><b>Statuses</b></td>
					<td><b>Records<br/>Processed</b></td>
					<td><b>Valid</b></td>
					<td><b>Invalid</b></td>
					<td><b>Actions</b></td>
				</tr>
				<c:forEach var="report" items="${csForm.archivedReports}">
					<tr align="center" class="${csForm.validationReport == report ? 'admin_yellow' : 'admin_blue1'}">
						<td align="left"><a href="collections.do?command=validate&report=true&collection=${report.collection}#report"
								title="see full report">${report.name}</a></td> 
						<td><fmt:formatDate value="${report.timeStamp}" pattern="yyyy-MM-dd h:mm a"  /></td>
						<td>
							<c:choose>
								<c:when test="${not empty report.statuses}">
									<c:forEach var="statusValue" items="${report.statuses}" varStatus="i">
										<div>${sf:getStatusLabel(statusValue, report.collection, sessionBean)}<c:if
											test="${not i.last}">, </c:if></div>
									</c:forEach>
								</c:when>
								<c:otherwise>All Statuses</c:otherwise>
							</c:choose>
						</td>				
						<td><bean:write name="report" property="prop(numToValidate)" /></td>
						<td><bean:write name="report" property="prop(numValid)" /></td>
						<td><bean:write name="report" property="prop(numNotValid)" /></td>
						<td>
								<a href="${contextPath}/manage/collections.do?command=validate&setup=true&collection=${report.collection}"
									 title="validate this collection">Validate</a>
						</td>
					</tr>
				</c:forEach>
			</table>
			</div>
		</c:otherwise>
	</c:choose>
	
	<c:if test="${not empty csForm.validationReport}">
	<br /><br />
		<a name="report"></a><h3>Validation Report</h3>
		<div align="right"><st:upArrow  contextPath="${contextPath}"/></div>
		<div class="searchResultValues"><em>Collection:</em> <b>${csForm.validationReport.name}</b></div>
		<div class="searchResultValues"><em>CollectionKey:</em> ${csForm.validationReport.collection}</div>
		<div class="searchResultValues"><em>Timestamp:</em> 
			<fmt:formatDate value="${csForm.validationReport.timeStamp}" 
							pattern="yyyy-MM-dd h:mm a"  /></div>
		
		<div class="searchResultValues"><em>Statuses Validated: </em> 
			<c:choose>
				<c:when test="${not empty csForm.validationReport.statuses}">
					<c:forEach var="statusValue" items="${csForm.validationReport.statuses}" varStatus="i">
							<c:if test="${i.last && fn:length(csForm.validationReport.statuses) > 1}"> and </c:if>
							${sf:getStatusLabel(statusValue, csForm.validationReport.collection, sessionBean)}<c:if
							test="${not i.last && fn:length(csForm.validationReport.statuses) > 2}">, </c:if>
					</c:forEach>
				</c:when>
				<c:otherwise>All Statuses</c:otherwise>
			</c:choose>
		</div>
		
		<div><h4>Summary</h4>
			<ul>
				<bean:write name="csForm" property="validationReport.prop(numValidated)" /> records processed in ${csForm.validationReport.processingTime}<br />
				<b><bean:write name="csForm" property="validationReport.prop(numValid)" /> were valid</b><br>
				<bean:write name="csForm" property="validationReport.prop(numNotValid)" /> were invalid<br>
				<logic:greaterThan name="csForm" property="validationReport.prop(numNotValidated)" value="0"><br>
					<font color="red"><bean:write name="csForm" property="validationReport.prop(numNotValidated)" /> records could not be 
						processed due to system errors</font>
				</logic:greaterThan>
			</ul>
		</div>
		
		<c:if test="${csForm.validationReport.invalidRecCount > 0}">
			<h4>Problem Records</h4>
			<c:forEach var="entry" items="${csForm.validationReport.entries}" varStatus="i">
				<c:if test="${entry.isInvalid}">
					<b>${entry.id}</b>
					<blockquote>
						<c:if test="${entry.isInvalid}"><p>${entry.validationReport}</p></c:if>
					</blockquote>
				</c:if>
			</c:forEach>
			<div align="right"><st:upArrow contextPath="${contextPath}"/></div>
		</c:if>
	</c:if>
	
</body>
</html:html>
