<%@ include file="/JSTLTagLibIncludes.jsp" %>
<resp:setHeader name="Cache-Control">cache</resp:setHeader>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<html:html>
<head>
<title><st:pageTitle title="Export Collection Form" /></title>

<%@ include file="/baseHTMLIncludes.jsp" %>

<script>
	// Override the method below to set the focus to the appropriate field.
	function sf(){}
	
	function showInstructions () {
		$( "export-instructions" ).show();
		$( "more-link" ).hide();
	}
	
	function doSubmit() {
		document.forms['csForm'].submit();
		// alert (document.forms['csForm'].sc.value)
		return true;
		}
		
	function doSetCollection (collection) {
			location="collections.do?command=export&setup=true&collection=" + collection;
		}
		
	function doExportCollection (collection) {
		if (collection == "")
			alert ("Please Select a collection");
		else
			return doSubmit();
		}

</script>
</head>
<body>

<st:pageHeader currentTool="manage" toolLabel="Export Collection" />

<st:breadcrumbs>
	<a href="manage-home.jsp">Manage</a>
	<st:breadcrumbArrow />
	<a href="collections.do">Collections</a>
	<st:breadcrumbArrow />
	<span class="current">export</span>
	<c:if test="${not empty csForm.dcsSetInfo}">
		<st:breadcrumbArrow />
		${csForm.dcsSetInfo.name}
	</c:if>
</st:breadcrumbs>

<%-- <div align="center" style="padding-bottom:10px">
		[ <a href="collections.do?command=export&report=true">Archived Reports</a> |
		<a href="collections.do?command=export&exit=true">Exit (to collections)</a> }
</div> --%>

<div align="center" style="padding:10px">
<%-- 		[ <a href="collections.do?command=export&setup=true">Export</a> |
		<a href="collections.do?command=export&exit=true">Exit (to collections)</a> ] --%>
		
		<input type="button" value="See archived reports"
			onclick="window.location='${contextPath}/manage/collections.do?command=export&report=true'" />
			
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
					<c:if test="${csForm.isExporting}">
						<blockquote>As of <dt:format pattern="h:mm:ss a yyyy-MM-dd"><dt:currentTime/></dt:format>,
						export is progressing normally.</blockquote>
					</c:if>
					[
				 	<c:choose>
						<c:when test="${not empty csForm.exportReport}">
							<a href="collections.do?command=export&report=true&collection=${csForm.exportReport.collection}#report">Export 
								Complete - <b>See Report</b></a>
						</c:when>
						<c:otherwise>
							<a href="collections.do?command=export&showExportMessages=true">Refresh 
								exporting status messages</a>
						</c:otherwise>
					</c:choose>
					| <a href="collections.do?command=export&setup=true">Clear messages</a> ]</blockquote>
			</html:messages>
		</td>
	  </tr>
	</table>		
	</logic:messagesPresent>
	
	<html:form action="/manage/collections" method="GET">

		<html:hidden property="command" value="export" />
		
		<c:if test="${csForm.isExporting}">
			<h3>Export in Progress</h3>
			<%-- 
				what session is exporting?
				if it is the current session, then present stop button
				else, present refresh button, and maybe a link to exporting status messages?
			
			--%>
		
			<p><c:choose>
				<c:when test="${sessionBean.id == csForm.exportingSession}">You are currently 
				exporting the "${csForm.exportingSet.name}" collection.</c:when>
				<c:otherwise>Another user is currently exporting the "${csForm.exportingSet.name}" collection.
				This operation must complete before a new export operation can begin.</c:otherwise>
			</c:choose>
			</p>
			<%-- <p><a href="collections.do?command=export&showExportMessages=true">Check/refresh exporting status messages</a></p> --%>
			<p><c:choose>
				<c:when test="${sessionBean.id == csForm.exportingSession}">
					<html:submit property="stopExporting" value="Stop exporting" />
				</c:when>
				<c:otherwise>
					<input type="button" value="Check export progress" onclick="location='collections.do?command=export&showExportMessages=true'">
				</c:otherwise>
			</c:choose>
		</c:if>
		
		<c:if test="${not csForm.isExporting}">
			<div id="more-link" style="display:block">
				<a href="javascript:showInstructions()">Click here </a> for exporting instructions.
			</div>
			<div id="export-instructions" style="display:none">
			<p>To export,
			<ol>
				<li>Select the record statuses to be exported.</li>
				<li>Enter a destination path on your local machine, <i>relative to the base export directory</i>, to which the 
					records are to be copied.<br />
					NOTE: The base export directory is set as: ${csForm.exportBaseDir}. 
				</li>
				<li>Click the export button.<br />
					NOTE: <i>Only valid records are exported.</i>
				</li>
			</ol>
			</div>
			</p>
			
			<html:hidden property="exportCollection" value="true" />
			<h3>Collection to Export</h3>
			<select name="collection" onchange="doSetCollection (this.form.collection.value)" (>
				<option value=""> -- Select Collection --</option>
				<c:forEach var="info" items="${csForm.sets}">
					<option value="${info.setSpec}" 
						<c:if test="${info.setSpec == csForm.dcsSetInfo.setSpec}">selected="true"</c:if>
						>${info.name} (${info.numIndexed} records)</option>
				</c:forEach>
			</select>
			<c:if test="${not empty csForm.dcsSetInfo}">
				<%-- statuses --%>
				<div style="margin-top:10px;">
					<span class="input-label">Record statuses to be exported</span>
					(all records will be exported if no statuses are selected)
				</div>
				<div style="margin-left:20px">
				<c:choose>
					<c:when test="${empty csForm.statusOptions}"><i>no statuses</i></c:when>
					<c:otherwise>
						<c:forEach var="status" items="${csForm.statusOptions}" varStatus="i">
							<input type="checkbox" id="${status.value}_status_id" name="sss" 
							value="${status.value}"
							<c:if test="${sf:arrayContains(csForm.sss, status.value)}">checked</c:if> />
							<label for="${status.value}_status_id" 
								 title="${f:jsEncode(status.description)}">${status.label}</label><br/>				
						</c:forEach>
					</c:otherwise>
				</c:choose>
				</div>
				
					<%-- Export path --%>
				<div style="margin-top:10px;">
					<span class="input-label">Destination path</span>
					(relative to ${csForm.exportBaseDir})
				</div>
				<div align="left">
					<html:text property="destPath" size="70"/><br>
				</div>
				<div style="margin-top:10px;">
					<input type="button" value="Export ${csForm.dcsSetInfo.name}" 
							 onclick="doExportCollection(this.form.collection.value)"/>
				</div>
			</c:if>
		</c:if>
	</html:form>

<%-- 	<c:if test="${not empty csForm.archivedReports}">
		There are ${fn:length(csForm.archivedReports)} export reports available
		<ul>
		<c:forEach var="report" items="${csForm.archivedReports}">
		<li><%@ include file="export_report_summary.jspf" %>
			<a href="collections.do?command=export&report=true&collection=${report.collection}">see full report</a></li>
		</c:forEach><br />
		</ul>
	</c:if>
	 --%>
	
<%-- 	<c:if test="${empty csForm.exportReport}">There is no report to show</c:if>
	<c:if test="${not empty csForm.exportReport}">
		<h3>Export Report</h3>
		
		<div class="searchResultValues"><em>Collection:</em> ${csForm.exportReport.name}</div>
			
		<div class="searchResultValues"><em>CollectionKey:</em> ${csForm.exportReport.collection}</div>
		
		<div><em>Summary</em>
			<ul>
				<bean:write name="csForm" property="report.prop(numToExport)" /> records processed in ${csForm.exportReport.processingTime}<br />
				${csForm.exportReport.invalidRecCount} were invalid and could not be exported<br>
				${csForm.exportReport.badCharRecCount} contained bad characters 
			</ul>
		</div>
		
		<h4>Problem Records</h4>
		<c:forEach var="entry" items="${csForm.exportReport.entries}" varStatus="i">
			<c:if test="${entry.hasBadChars || entry.isInvalid}">
				<b>${entry.id}</b>
				<blockquote>
					<c:if test="${entry.hasBadChars}">${entry.badCharReport}</c:if>
					
					<c:if test="${entry.isInvalid}"><p><pre>${entry.validationReport}</pre></p></c:if>
				</blockquote>
			</c:if>
		</c:forEach>
	</c:if> --%>
		

	
</body>
</html:html>
