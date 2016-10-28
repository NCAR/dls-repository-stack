<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title" value="NDR Sync Report" />
<c:set var="baseNDRGetUrl" value="${ndrForm.ndrApiBaseUrl}/get" />
<html>
<head>
	<%@ include file="/baseHTMLIncludes.jsp" %>
	<link rel="stylesheet" href="includes/json-styles.css" type="text/css">
	<link rel="stylesheet" href="includes/ndr-browser-styles.css" type="text/css">

	<script type="text/javascript">
		<%@ include file="includes/NDRGlobals.jsp" %>
	</script>
	<script type="text/javascript" src="includes/JsonViewer.js"></script>
	<script type="text/javascript" src="includes/NDRObject.js"></script>
	<script type="text/javascript" src="includes/NDRBrowser.js"></script>
	<script type="text/javascript">
	
/*
	called on page load, attach a listener to "ndr-link"s
*/
function pageInit () {
	$$(".ndr-link").each (function (obj) {
		Event.observe (obj, "click", ndrclick, false);
		});
	}
	
Event.observe (window, "load", pageInit, false);
	</script>
	<title><st:pageTitle title="${title}" /></title>
</head>
<body bgcolor=white>
<st:pageHeader currentTool="manage" toolLabel="${title}" />

<st:pageMessages okPath="${contextPath}/ndr/ndr.do" okText="Back to NDR Admin"/>

<c:if test="${not empty ndrForm.syncReport}">
	<h3>${ndrForm.syncReport.collectionName} - has been synced with NDR</h3>
	<br/>
	
	<c:forEach var="entry" items="${ndrForm.syncReport.entryList}" >
		<div><b>${entry.id}</b></div>
		<ul>
			<c:choose>
				<c:when test="${entry.isError}">
					<li>ERROR - ${entry.errorMsg}</li>
				</c:when>
				<c:when test="${not empty entry.command}">
					<li>${entry.command} - 
						<%-- <a href="${baseNDRGetUrl}${item.metadataHandle}" class="ndr-link">${item.metadataHandle}</a> --%>
						<a href="${baseNDRGetUrl}/${entry.handle}" class="ndr-link">${entry.handle}</a>
					</li>
					
				</c:when>
			</c:choose>
	
		</ul>
	</c:forEach>
</c:if>
	
</body>
</html>
