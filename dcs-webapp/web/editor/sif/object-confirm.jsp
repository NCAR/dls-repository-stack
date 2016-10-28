<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Create Record Confirmation</c:set>

<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>

<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<%@ include file="/baseHTMLIncludes.jsp" %>
<link rel="stylesheet" href="${contextPath}/lib/autoform-styles.css" type="text/css">
<script type="text/javascript">
function pageInit () {
	insertRef()
}

function insertRef () {
	var newRecId = '${sif.newRecId}';
	var newRecType = '${sif.selectedType}';
	// log ("selectedType (should have value): " + newRecType);
	var mdeDoc = opener.document;
	var elementPath = "${sif.elementPath}";
	var name = "valueOf(" + elementPath + ")"
	var target = null;
	try {
		target = opener.document.sef.elements[name];
	} catch (error) { alert ("error: " + error) }
	if (target) {
		target.value = newRecId;
		var refTypeSelect = $(mdeDoc.getElementById('${sif.refTypeSelectId}'));
		// log ("refTypeSelect (should have value): " + refTypeSelect);
		if (newRecType && refTypeSelect) {
			// alert ("refTypeSelect found!");
			refTypeSelect.select ("option").each ( function (option) {
				option.selected = (option.value == newRecType);
				});
		}
		return;
	}
	else {
		alert ("error: target not found (" + name + ")");
	}
	// window.close()
}

Event.observe (window, 'load', pageInit);
</script>
</head>
<body>
<%@ include file="pageHeader.jspf" %>
<%--  
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr valign="middle">
		<td align="left">
			<div class="tool-label">Record Creator</div>
		</td>
		<td align="center" valign="top" width="98%">
			<div class="dcs-instance">${applicationScope.instanceName}</div>
		</td>
		<td align="center" nowrap="1">
			<img src="${contextPath}/images/${applicationScope.logo}" height="32" width="132">
			<div class="system-label">Collection System</div>
		</td>
	</tr>
	<tr><td bgcolor="#333366" height="2px" colspan="3"></td></tr>
</table>
--%>
<h3>SIF Record Created</h3>

<div>New record id: ${sif.newRecId}</div>

<div align="center"><input type="button" value="close" onclick="window.close()"/></div>

</body>

</html:html>
