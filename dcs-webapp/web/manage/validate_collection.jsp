<%@ include file="/JSTLTagLibIncludes.jsp" %>
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<resp:setHeader name="Cache-Control">no-cache</resp:setHeader>

<html:html>
<head>
<title><st:pageTitle title="Validation Report" /></title>

<%@ include file="/baseHTMLIncludes.jsp" %>
<script type="text/javascript" src="${contextPath}/lib/progress-indicator/progress-script.js" ></script>

<script>
// Override the method below to set the focus to the appropriate field.
function sf(){}

function doSubmit() {
	document.forms['csForm'].submit();
	return true;
}
	
function doSetCollection (collection) {
	if (collection)
		location="collections.do?command=validate&setup=true&collection=" + collection;
}
	
function doValidateCollection (collection) {
	if (collection == "")
		alert ("Please Select a collection");
	else
		doSubmit()
}
	


function pageInit () {
	if ("${csForm.isValidating}" == "true") {
	
		var finalize = function () {
			window.location = "collections.do?command=validate&showValidationMessages=true";
			// alert ("done");
		}
		var progressUrl = "collections.do?command=validate&progress=t";
		
		new PeriodicalExecuter(function(pe) {
			updateProgress (pe, progressUrl, finalize);
		}, 3); 
	}
}

Event.observe (window, 'load', pageInit);
	
		
</script>
</head>
<body>

<st:pageHeader currentTool="manage" toolLabel="Validate Collection" />

<st:breadcrumbs>
	<a href="manage-home.jsp">Manage</a>
	<st:breadcrumbArrow />
	<a href="collections.do">Collections</a>
	<st:breadcrumbArrow />
	<span class="current">Validate</span>
	<c:if test="${not empty csForm.dcsSetInfo}">
		<st:breadcrumbArrow />
		${csForm.dcsSetInfo.name}
	</c:if>
</st:breadcrumbs>

<div align="center" style="padding:10px">
		<input type="button" value="See archived reports"
			onclick="window.location='${contextPath}/manage/collections.do?command=validate&report=true'" />
			
<%-- 		<input type="button" value="Return to manage collections"	
			onclick="window.location='${contextPath}/manage/collections.do?command=validate&exit=true'" /> --%>
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
					<blockquote>[ <a href="collections.do?command=validate&setup=true">OK</a> ]</blockquote>								
				</html:messages>
			</ul>
			<html:messages id="msg" property="showValidationMessagingLink"> 
				<blockquote>
					<c:if test="${csForm.isValidating}">
						<blockquote>As of <dt:format pattern="yyyy-MM-dd h:mm:ss a"><dt:currentTime/></dt:format>,
						validation is progressing normally.</blockquote>
					</c:if>
					[
					<c:choose>
						<c:when test="${not empty csForm.validationReport}">
							<a href="collections.do?command=validate&report=true&collection=${csForm.dcsSetInfo.setSpec}#report">Validation 
								Complete - <b>See Report</b></a>
						</c:when>
						<c:otherwise>
							<a href="collections.do?command=validate&showValidationMessages=true">Refresh 
								validation status messages</a>
						</c:otherwise>
					</c:choose>
					|  <a href="collections.do?command=validate&setup=true">Clear messages</a> ]<br>
				</blockquote>					
			</html:messages>
		</td>
	  </tr>
	</table>		
	</logic:messagesPresent>
	
	
	<html:form action="/manage/collections" method="GET">
		<html:hidden property="command" value="validate" />
		<c:if test="${csForm.isValidating}">
			<h3>Validation in progress</h3>		
			<p>
				<c:if test="${sessionBean.id != csForm.validatingSession}">
					Another user is currently validating a collection.</c:if>
				<%@ include file="/lib/progress-indicator/progress-widget.jsp" %>
			</p>
			
			<p><c:choose>
				<c:when test="${sessionBean.id == csForm.validatingSession}">
					<html:submit property="stopValidating" value="Stop validating" />
				</c:when>
				<c:otherwise>
					<input type="button" value="Check validation progress" 
									onclick="location='collections.do?command=validate&showValidationMessages=true'" />
				</c:otherwise>
			</c:choose>			
			</p>

		</c:if>
		
		<c:if test="${not csForm.isValidating}">
					<html:hidden property="validateCollection" value="true" />
					<h3>Select a collection to validate</h3>
					<select name="collection"  onchange="doSetCollection (this.form.collection.value)">
						<option value=""> -- Select Collection --</option>
						<c:forEach var="info" items="${csForm.sets}">
							<option value="${info.setSpec}" 
								<c:if test="${info.setSpec == csForm.dcsSetInfo.setSpec}">selected="true"</c:if>
								>${info.name} (${info.numIndexed} records)</option>
						</c:forEach>
					</select>
				
				<c:if test="${not empty csForm.dcsSetInfo}">
				
					<h3 style="margin-top:10px;">Select operation</h3>
						<table cellpadding="0" cellspacing="2">

							<tr valign="bottom">
								<td style="white-space:nowrap;padding-left:18px">
									<div class="input-label">
										<input id="ignore_cached_validation" name="ignoreCachedValidation"  type="radio" value="true" checked />
										<label for="ignore_cached_validation">Validate</label><br/>
									 </span>
								</td>
								<td>
									<p style="margin-left:20px">
										Validate all records from scratch, provide current results.</p>
								</td>
							</tr>
							<tr valign="bottom">
								<td style="white-space:nowrap;;padding-left:18px">
									<div class="input-label">
										<%-- <input name="ignoreCachedValidation" type="checkbox" value="true" /> --%>
										<input id="use_cached_validation" name="ignoreCachedValidation" type="radio"  value="false" />
										<label for="use_cached_validation">Report</label>
									 </span>
								</td>
								<td>
									<p style="margin-left:20px">Report cached results for record validation.</p>
								</td>
							</tr>
						</table>
				
					<%-- statuses --%>
					<h3 style="margin-top:10px;">Record statuses to be validated</h3>
						All records will be validated if no statuses are selected
					
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
						<p><input type="button" value="Validate collection" 
							   onclick="doValidateCollection(this.form.collection.value)" />
						</p>
				</c:if>
		</c:if>
	</html:form>

<%-- 	<h1>validateCollection: ${param["validateCollection"]}</h1>
	
	<input type="button" value="progress" onclick="updateProgress()" />
	<span style="border:thin red solid;padding:5px;"><span id="progress">???</span></span> --%>
	
<%-- 		
	<input type="button" value="Return to Collections" onclick="location='collections.do?command=validate&exit=true'"/>
	
	<hr>
	<c:if test="${not empty csForm.archivedReports}">
		There are ${fn:length(csForm.archivedReports)} validation reports available
		<ul>
		<c:forEach var="report" items="${csForm.archivedReports}">
		<li>${report.name} - 
			<fmt:formatDate value="${report.timeStamp}" pattern="yyyy-MM-dd h:mm a"  /><br>
			${report.summary}<br/> 
			<a href="collections.do?command=validate&report=true&collection=${report.collection}">see full report</a></li>
		</c:forEach>
		</ul>
	</c:if>
	
	
	<c:if test="${not empty csForm.validationReport}">
		<h3>Validation Report</h3>
		<PRE>${csForm.validationReport.report}</PRE>
	</c:if>
		
 --%>
</body>
</html:html>
