<%-- $Id: browse_ndr_collections.jsp,v 1.8 2009/03/23 07:01:12 ostwald Exp $ --%>
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">NDR Collection Browser</c:set>
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>

<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<%@ include file="/manage/manageHTMLIncludes.jsp" %>
	<link rel="stylesheet" href="includes/json-styles.css" type="text/css">
	<link rel="stylesheet" href="includes/ndr-browser-styles.css" type="text/css">

	<script type="text/javascript">
		<%@ include file="includes/NDRGlobals.jsp" %>
	</script>
	<script type="text/javascript" src="includes/JsonViewer.js"></script>
	<script type="text/javascript" src="includes/NDRObject.js"></script>
	<script type="text/javascript" src="includes/NDRBrowser.js"></script>

<script>

function pageInit () {
				
	$$(".ndr-link").each (function (obj) {
		obj.observe ("click", ndrclick);
		});
		
	var objType = "${param['type']}";
	objType = (objType == "" ? "metadataProvider" : objType);
	$(objType + '-button').addClassName ("selected");
	
	$('obj-type-buttons').select(".obj-type-button").each ( function (obj) {
		if (!obj.hasClassName ("selected")) {
			var button = $(obj);
			button.observe ("click", function (evnt) {
				var type = button.id.sub ("-button", "")
				window.location = "${contextPath}/ndr/ndr.do?command=browse&type="+type
			});
			button.observe ("mouseover", function (evnt) { 
				// button.setStyle ({cursor:"pointer"}) 
				Event.element(evnt).addClassName ("over");
			});
			button.observe ("mouseout", function (evnt) { 
				// button.setStyle ({cursor:"default"}) 
				Event.element(evnt).removeClassName ("over");
			});
		}
	});
}

Event.observe (window, 'load', pageInit, false);

</script>
<style type="text/css">
.buttons-table {
	border-collapse: collapse;
	margin: 15px 0px 10px 0px;
}

.obj-type-button {
	font-size: 100%;
	font-weight: bold;
	color: #333366; 
	background-color:#E3E4F1;
	padding:5px 10px 5px 10px;
	border:1px solid #333366;
}

.obj-type-button.selected {
	background-color:#FFFFCC;
}

.obj-type-button.over {
	color:white;
	cursor:pointer;
}

</style>
</head>

<body text="#000000" bgcolor="#ffffff">

<st:pageHeader currentTool="ndr" toolLabel="${title}" />

<%-- <h3>Browse NDR Collections</h3> --%>

<%@ include file="ndr_context.jspf" %>

<div align="center">
	<table id="obj-type-buttons" class="buttons-table">
		<tr>
			<td id="metadataProvider-button" class="obj-type-button">MetadataProviders</td>
			<td id="aggregator-button" class="obj-type-button">Aggregators</td>
			<%-- <td id="pc-resource-button" class="obj-type-button">PC Resources</td> --%>
		</tr>
	</table>
</div>

<c:forEach var="handle" items="${ndrForm.browserHandles}" >
	<div class="mdp-handle">
		<a href="${ndrForm.ndrApiBaseUrl}/get/${handle}" class="ndr-link">${handle}</a>
	</div>
</c:forEach>

<%-- <%@ include file="/lib/util/all-scopes.jspf" %> --%>

</body>
</html:html>

