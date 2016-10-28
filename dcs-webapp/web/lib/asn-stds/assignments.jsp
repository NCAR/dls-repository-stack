<%@ include file="/lib/includes.jspf" %>
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<html>
	<head>
		<link rel="stylesheet" href="../styles.css" type="text/css">
		<title>Casaa Assigments</title>
	</head>
<body bgcolor=white>
<h3>Casaa Assigments - ${sef.recId}</h3>
<p>This page lists the educational content standard assignments for a specific record that have been 
saved in the CASAA Suggestion Service. Standards assignments are saved when the status for the record is set to it's "Final" status.</p>
<hr>
<%@ include file="/lib/recordSummary.jspf" %>
<c:set var="suggestionService" value="${sef.suggestionService}"/>

<c:if test="${not empty suggestionService.savedAssignments}">
	<c:set var="stds" value="${suggestionService.savedAssignments}" />
	<b>There are ${fn:length(stds)} saved assignments</b>
	<c:forEach var="stdWrapper" items="${stds}" varStatus="i">
		<p>${i.count} - ${stdWrapper.text} <span style="font-size:80%">(${stdWrapper.identifier})</span></p>
	</c:forEach>
</c:if>

<c:if test="${empty suggestionService.savedAssignments}">
	<b>There are NO saved assignments</b>
</c:if>

</body>
</html>
