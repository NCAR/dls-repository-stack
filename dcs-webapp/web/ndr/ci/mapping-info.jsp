<%-- $Id: mapping-info.jsp,v 1.5 2009/08/27 00:13:31 ostwald Exp $ --%>
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="mappingInfo" value="${ciForm.mappingInfo}" />
<c:set var="title">Mapping Info</c:set>
<c:set var="baseNDRGetUrl" value="${ciForm.ndrApiUrl}/get" />
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>

<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

	<%@ include file="/manage/manageHTMLIncludes.jsp" %>
	<link rel="stylesheet" href="${contextPath}/ndr/includes/json-styles.css" type="text/css">
	<link rel="stylesheet" href="${contextPath}/ndr/includes/ndr-browser-styles.css" type="text/css">
	
<style type="text/css">
.heading {
	margin:20px 0px 3px 0px;
	font-weight:bold;
	font-size:130%;
	padding-top:3px;
	color:#333366;
	border-top:#333366 1px solid;
}
	
.ndr-link-line {
	margin:20px 0px 10px 0px;
	/* background-color:#e3e4f1; */
	border-top:black solid 0.5px;
}

.ndr-button .ndr-link:active {
	background-color:#ff9999;
}

.ndr-button {
    text-decoration: none;
	font-weight:bold;
	font-size:110%;
	padding:2px 4px 4px 4px;
	background-color:#e3e4f1;
	z-index:10;
	border:thin black solid;
}
.mismatch {
	border:1px solid red;
	background-color:#faafaf;
}
.error-msg {
	padding:10px;
	margin:10px 30px 10px 30px;
	border:thin red solid;
	color:red;
}
</style>


	<script type="text/javascript">
		<%@ include file="../includes/NDRGlobals.jsp" %>
	</script>
	<script type="text/javascript" src="${contextPath}/ndr/includes/JsonViewer.js"></script>
	<script type="text/javascript" src="${contextPath}/ndr/includes/NDRObject.js"></script>
	<script type="text/javascript" src="${contextPath}/ndr/includes/NDRListMembersObject.js"></script>
	<script type="text/javascript" src="${contextPath}/ndr/includes/NDRBrowser.js"></script>
	
<script type="text/javascript">

// initialize the ndr-links
function pageInit () {
	var mismatches = $A();
	<c:forEach var="mismatch" items="${mappingInfo.mismatches}">
	try {
		$("${mismatch}").addClassName ("mismatch");
	} catch (error) {
		log (error);
	}
	</c:forEach>

	$$(".ndr-link").each (function (obj) {
		// alert (obj.href);
		Event.observe (obj, "click", ndrclick, false);
	});
	
	$$(".ndr-link").each (function (obj) {
		// alert (obj.href);
		Event.observe (obj, "click", ndrclick, false);
	});
}

Event.observe (window, 'load', pageInit);

</script>
	
</head>

<body text="#000000" bgcolor="#ffffff">

<st:pageHeader currentTool="ndr" toolLabel="${title}" />

<%@ include file="ci_context.jspf" %>
<st:pageMessages okPath="${contextPath}/ndr/ci.do" />

<table width="100%">
<tr>
	<td align="left"><h2 style="margin:0px;font-size:150%">${ciForm.mappingInfo.id}</h2></td>
	<td align="right">
		<input type="button" value="Back to Mappings" onclick="window.location='ci.do';">
			&nbsp;&nbsp;
		<input type="button" value="Update Mapping" 
					 onclick="window.location='ci.do?command=mappingInfo&id=${ciForm.mappingInfo.id}&update';">
	</td>
</tr>
</table>


<div class="heading">Mismatches</div>
<div>${mappingInfo.mismatches}</div>

<div class="heading">Comparison Info</div>

		<c:if test="${mappingInfo.hasError}">
			<div class="error-msg">WARNING: this mapping experienced an error (some data may be missing)
				<ul>${mappingInfo.errorMessage}</ul>
			</div>
		</c:if>

<table id="dcs-table" cellpadding="3">
<tr class="header-row">
	<td>&nbsp;</td><td>NCS</td><td>NDR</td>
</tr>
<tr class="dcs-table-row">
	<td>&nbsp;</td><td>${mappingInfo.id}</td><td>${mappingInfo.aggregatorHandle}</td>
</tr>
<tr class="dcs-table-row" id="title">
	<td>title</td><td>${mappingInfo.ncsTitle}</td><td>${mappingInfo.ndrTitle}</td>
</tr>
<tr class="dcs-table-row" id="resourceUrl">
	<td>resource url</td>
	<td><a href="${mappingInfo.ncsResourceUrl}" target="reswin">${mappingInfo.ncsResourceUrl}</td>
	<td><a href="${mappingInfo.ndrResourceUrl}" target="reswin">${mappingInfo.ndrResourceUrl}</td>
</tr>
</table>

<div class="heading">NDR Collection Objects</div>

<c:if test="${not empty mappingInfo.resourceHandle}">
	<div class="ndr-link-line">
		<a href="${baseNDRGetUrl}/${mappingInfo.resourceHandle}" 
			 class="ndr-link ndr-button">Resource</a>
	</div>
</c:if>

<c:if test="${not empty mappingInfo.metadataHandle}">
	<div class="ndr-link-line">
		<a href="${baseNDRGetUrl}/${mappingInfo.metadataHandle}" 
			 class="ndr-link ndr-button">Metadata</a>
	</div>
</c:if>
	
<c:if test="${not empty mappingInfo.metadataProviderHandle}">
	<div class="ndr-link-line">
		<a href="${baseNDRGetUrl}/${mappingInfo.metadataProviderHandle}" 
			 class="ndr-link ndr-button">Metadata Provider</a>
	</div>
</c:if>
	
<c:if test="${not empty mappingInfo.aggregatorHandle}">	   
	<div class="ndr-link-line">
		<a href="${baseNDRGetUrl}/${mappingInfo.aggregatorHandle}" 
			 class="ndr-link ndr-button">Aggregator</a>
	</div>
</c:if>

</body>
</html:html>

