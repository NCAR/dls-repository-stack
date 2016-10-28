<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title" value="NDR Export Report" />
<html>
<head>
	<%@ include file="/baseHTMLIncludes.jsp" %>
	<link rel="stylesheet" href="includes/json-styles.css" type="text/css">
	<link rel="stylesheet" href="includes/ndr-browser-styles.css" type="text/css">

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
		
	// $('debug').hide()
	}
	
Event.observe (window, "load", pageInit, false);
	</script>
	<title><st:pageTitle title="${title}" /></title>
</head>
<body bgcolor=white>
<st:pageHeader currentTool="manage" toolLabel="${title}" />

<st:pageMessages okPath="" />

<%-- <div id="debug"></div> --%>

<div id="response" style="display:none"></div>
<c:if test="${not empty ndrForm.exportReport}" >
<h3>Interactions with NDR</h3>
<ul>
<c:forEach var="entry" items="${ndrForm.exportReport.entryList}" varStatus="i">

	<li style="margin-bottom:10px">
	<c:choose>
		<c:when test="${entry.isNew}"><b style="color:green">created</b></c:when>
		<c:when test="${entry.isModify}"><b style="color:blue">modified</b></c:when>
		<c:otherwise><b style="color:red">falure</b></c:otherwise>
	</c:choose>
	<c:if test="${not empty entry.id}">
		- <b>${entry.id}</b>
	</c:if>
	- <span style="font-size:.7em">${entry.changeDate}</span>
	<div style="margin-left:15px">

		<c:choose>
			<c:when test="${not empty entry.ndrObjects}">
				<c:forEach var="obj" items="${entry.ndrObjects}">
					<div id="reponse_${i.index}_${obj.type}"><b>${obj.type}</b> - 
						<a href="${obj.uri}" class="ndr-link">${obj.uri}</a></div>
					
				</c:forEach>
			</c:when>
			<c:otherwise>${entry.statusNote}</c:otherwise>
		</c:choose>
	</div>
	</li>
</c:forEach>
</ul>
</c:if>


</body>
</html>
