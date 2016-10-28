<%-- $Id: mappings.jsp,v 1.4 2008/11/08 00:16:55 ostwald Exp $ --%>
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">NCS / NDR Mappings</c:set>
<c:set var="baseNDRGetUrl" value="${ndrApiUrl}/get/" />
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>

<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

	<%@ include file="/manage/manageHTMLIncludes.jsp" %>
	<link rel="stylesheet" href="${contextPath}/ndr/includes/json-styles.css" type="text/css">
	<link rel="stylesheet" href="${contextPath}/ndr/includes/ndr-browser-styles.css" type="text/css">

</head>
<style type="text/css">
.error-msg {
	color:red;
}
</style>

<script type="text/javascript">
function showAll () {
	$$('.match').each (function (match) {
		match.show();
	});
}
function showMismatches () {
	// alert ("show mismatches not implemented");
	$$('.match').each (function (match) {
		match.hide();
	});
}


Event.observe (window, 'load', function (evnt) {
	showMismatches(); 
	});

</script>
<body text="#000000" bgcolor="#ffffff">

<st:pageHeader currentTool="ndr" toolLabel="${title}" />

<%@ include file="ci_context.jspf" %>

<st:pageMessages okPath="${contextPath}/ndr/ndr.do" />

<h3>Mappings between NCS Collect Records and NDR Collections</h3>

<div style="margin:10px 0px 10px 0px">
<table width="100%">
	<tr>
		<td align="left">
			<input type="button" onclick="showAll()" value="show all" />&nbsp;&nbsp;&nbsp;
			<input type="button" onclick="showMismatches()" value="show only mismatches" />
		</td>
		<td align="right">
			<input type="button" onclick="window.location='${contextPath}/ndr/ci.do?command=resetMappings'"
					   value="Reset Mappings" />
		</td>
	</tr>
</table>
</div>

<table id="dcs-table" cellpadding="3">
<tr class="header-row">
	<td>Record Id</td>
	<td>Record Title</td>
	<td>Items</td>
	<td>Aggregator</td>
	<td>Mismatches</td>
</tr>
<c:forEach var="mapping" items="${ciForm.mappingsManager.mappings}" >
<tr class="dcs-table-row 
		   ${not empty mapping.oaiIngest ? 'oai-ingest' : ''}
		   ${empty mapping.mismatches ? 'match' : ''}">
	<td><a href="${contextPath}/ndr/ci.do?command=mappingInfo&id=${mapping.id}">${mapping.id}</td>
	<td>${mapping.ncsTitle}
		<c:if test="${mapping.hasError}">
			<div class="error-msg">${mapping.errorMessage}</div>
		</c:if>
	</td>
	<td align="right">${not empty mapping.itemcount ? mapping.itemcount : '??' }</td>
	<td>${mapping.aggregatorHandle}</td>
	<td>${mapping.mismatches}</td>
</tr>
</c:forEach>
</table>
</body>
</html:html>

