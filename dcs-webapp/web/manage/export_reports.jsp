<%@ include file="/JSTLTagLibIncludes.jsp" %>
<resp:setHeader name="Cache-Control">cache</resp:setHeader>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<html:html>
<head>
<title><st:pageTitle title="Export Reports" /></title>

<%@ include file="/baseHTMLIncludes.jsp" %>

<style type='text/css'>
.changes-item {
	margin-left:20px
}
.changes-box {
	margin-left:50px;
	/* border:thin purple solid; */
	padding:5px 0 5px 0
	}
.changes-header {
	font-weight:bold;
	padding-top:0px
}
</style>

<script>
	// Override the method below to set the focus to the appropriate field.
	function sf(){}

</script>
</head>
<body>

<st:pageHeader currentTool="manage" toolLabel="Export Reports" />


<st:breadcrumbs>
	<a href="manage-home.jsp">Manage</a>
	<st:breadcrumbArrow />
	<a href="collections.do">Collections</a>
	<st:breadcrumbArrow />
	<span class="current">Export Reports</span>
</st:breadcrumbs>

<div align="center" style="padding:10px">
		<input type="button" value="Export a collection"
			onclick="window.location='${contextPath}/manage/collections.do?command=export&setup=true'" />
			
		<input type="button" value="Return to manage collections"	
			onclick="window.location='${contextPath}/manage/collections.do?command=export&exit=true'" />
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
					<blockquote>[ <a href="collections.do?command=export&exit=true">OK</a> ]</blockquote>							
				</html:messages>
			</ul>
			<html:messages id="msg" property="showExportMessagingLink"> 
				<blockquote>			
					<bean:write name="msg"/> [ <a href="collections.do?command=export&showExportMessages=true">Refresh exporting status messages</a> ]			
				</blockquote>
				<blockquote>[ <a href="collections.do?command=export&setup=true">OK</a> ]</blockquote>
			</html:messages>
		</td>
	  </tr>
	</table>		
	</logic:messagesPresent>
	
	<c:if test="${empty csForm.archivedReports}"><p>There are no archived export reports to show.</p></c:if>
	
	<c:if test="${not empty csForm.archivedReports}">
		<p>There are ${fn:length(csForm.archivedReports)} export reports available. Click on the collection name in the table 
		below to see a more detailed report.</p>
	<div align="center">
	<table bgcolor="#666666" cellpadding="6" cellspacing="1" border="0">
		<tr align="center" bgcolor="#CCCEE6">
			<td align="left" ><b>Collection</b></div></td>
			<td><b>Time Stamp</b></td>
			<td><b>Statuses</b></td>
			<td><b>Records<br/>Processed</b></td>
			<td><b>Exported</b></td>
			<td><b>NOT<br/>Exported</b></td>
			<td><b>Actions</b></td>
		</tr>
		<c:forEach var="report" items="${csForm.archivedReports}">
			<tr align="center" class="${csForm.exportReport == report ? 'admin_yellow' : 'admin_blue1'}">
				<td align="left"><a href="collections.do?command=export&report=true&collection=${report.collection}#report"
						title="see full report">${report.name}</a></td> 
				<td><fmt:formatDate value="${report.timeStamp}" pattern="yyyy-MM-dd h:mm a"  /></td>
				<%-- <td>${report.displayStatuses}</td> --%>
				
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
				
				<td><bean:write name="report" property="prop(numToExport)" /></td>
				<td><bean:write name="report" property="prop(numExported)" /></td>
				<td><bean:write name="report" property="prop(numNotExported)" /></td>
				<td>
<%-- 					<input type="button" value="new export"  class="record-action-button"
						onclick="location='collections.do?command=export&setup=true&collection=${report.collection}'"
						title="export this collection" /> --%>
						<a href="${contextPath}/manage/collections.do?command=export&setup=true&collection=${report.collection}"
							title="export this collection" >New export</a>
				</td>
			</tr>
		</c:forEach>
	</table>
	</div>
	</c:if>
	
	
	<c:if test="${not empty csForm.exportReport}">
	<br /><br /><a name="report"></a>
		<h3>Export Report</h3>
		<div align="right"><st:upArrow  contextPath="${contextPath}"/></div>
		<div class="searchResultValues"><em>Collection:</em> <b>${csForm.exportReport.name}</b></div>
		<div class="searchResultValues"><em>CollectionKey:</em> ${csForm.exportReport.collection}</div>		
		<div class="searchResultValues"><em>Timestamp:</em> 
			<fmt:formatDate value="${csForm.exportReport.timeStamp}" 
							pattern="yyyy-MM-dd h:mm a"  />
		</div>
		<div class="searchResultValues"><em>Statuses Exported: </em> 
			<c:choose>
				<c:when test="${not empty csForm.exportReport.statuses}">
					<c:forEach var="statusValue" items="${csForm.exportReport.statuses}" varStatus="i">
							<c:if test="${i.last && fn:length(csForm.exportReport.statuses) > 1}"> and </c:if>
							${sf:getStatusLabel(statusValue, csForm.exportReport.collection, sessionBean)}<c:if
							test="${not i.last && fn:length(csForm.exportReport.statuses) > 2}">, </c:if>
					</c:forEach>
				</c:when>
				<c:otherwise>All Statuses</c:otherwise>
			</c:choose>
		</div>
			
		<div class="searchResultValues"><em>Export Directory:</em> ${csForm.exportReport.props.destDir}</div>
		
		<div><h4>Summary</h4>
			<ul><b>${csForm.exportReport.numExported} records were exported</b><br />
				<bean:write name="csForm" property="exportReport.prop(numToExport)" /> records processed in ${csForm.exportReport.processingTime}<br />
				${csForm.exportReport.invalidRecCount} were invalid and could not be exported<br />
			</ul>
		</div>
		
		<c:set var="indentPixels" value="20"/>
		
<%-- 		<div style="border:thin green solid;padding:0 0 10pt 0">
			<h4>Properties</h4>
			<c:forEach items="${csForm.exportReport.props}" var="prop">
				<div>${prop.key}: ${prop.value}</div>
			</c:forEach>
		</div> --%>
		
		<h4>Changes to the Export Directory</h4>
		A report on what files have been added, changed, or deleted from the export directory. Note: This information
		is most useful if the last export and the one before it operated over the same statuses!
		<c:set var="deletedRecs" value="${fn:split (csForm.exportReport.props.filesDeleted, ',')}" />
		<div class="changes-box">
			<div class="changes-header">Deleted Records (${csForm.exportReport.props.numDeleted})</div>
			<div>Records that were in the previous export but were not in this one</div>
			<c:choose>
				<c:when test="${csForm.exportReport.props.numDeleted != '0'}">
					<c:forEach items="${deletedRecs}" var="rec">
						<div class="changes-item">${rec}</div>
					</c:forEach>
				</c:when>
				<c:otherwise><div class="changes-item"><i>none</i></div></c:otherwise>
			</c:choose>
		</div>
		
		<c:set var="filesOverWritten" value="${fn:split (csForm.exportReport.props.filesOverWritten, ',')}" />
		<div class="changes-box">
			<div class="changes-header">Existing Records Overwritten (${csForm.exportReport.props.numOverWritten})</div>
			These records have changed since the last export.
			<c:choose>
				<c:when  test="${csForm.exportReport.props.numOverWritten != '0'}">
					<c:forEach items="${filesOverWritten}" var="rec">
						<div class="changes-item">${rec}</div>
					</c:forEach>
				</c:when>
				<c:otherwise><div class="changes-item"><i>none</i></div></c:otherwise>
			</c:choose>
		</div>
		
		<c:set var="filesNewlyWritten" value="${fn:split (csForm.exportReport.props.filesNewlyWritten, ',')}" />
		<div class="changes-box">
			<div class="changes-header">Newly Written Records (${csForm.exportReport.props.numNewlyWritten})</div>
			These records were not in the previous export.
			<c:choose>
				<c:when  test="${csForm.exportReport.props.numNewlyWritten != '0'}">
					<c:forEach items="${filesNewlyWritten}" var="rec">
						<div class="changes-item">${rec}</div>
					</c:forEach>
				</c:when>
				<c:otherwise><div class="changes-item"><i>none</i></div></c:otherwise>
			</c:choose>
		</div>
		
		
		<c:if test="${csForm.exportReport.invalidRecCount > 0}">
			<h4>Problem Records</h4>
			<c:forEach var="entry" items="${csForm.exportReport.entries}" varStatus="i">
				<c:if test="${entry.isInvalid}">
					<b>${entry.id}</b>
					<blockquote>
						<c:if test="${entry.isInvalid}"><p>${entry.validationReport}</p></c:if>
					</blockquote>
				</c:if>
			</c:forEach>
		</c:if>
		<div align="right"><st:upArrow  contextPath="${contextPath}"/></div>
	</c:if>
	
</body>
</html:html>
