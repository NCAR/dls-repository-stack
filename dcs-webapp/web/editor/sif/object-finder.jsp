<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">SIF Object Finder</c:set>

<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>

<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<%@ include file="/baseHTMLIncludes.jsp" %>
<link rel="stylesheet" href="${contextPath}/lib/autoform-styles.css" type="text/css">
<script type="text/javascript">
function pageInit () {
	/* $('insert-button').observe ('click', insertRef); */
	$$("a.refId").each ( function (refId) {
		refId.observe ('click', insertRef);
	});
}

function insertRef (event) {
	var refId = event.element().id;
	var objType = event.element().up ('div').className;
	// alert ("insertRef: " + refId);
	event.stop();	
	var mdeDoc = opener.document;
	var elementPath = "${sif.elementPath}";
	var name = "valueOf(" + elementPath + ")"
	var target = null;
	try {
		target = opener.document.sef.elements[name];
	} catch (error) { alert ("error: " + error) }
	if (target) {
		target.value = refId;
		var refTypeSelect = $(mdeDoc.getElementById('${sif.refTypeSelectId}'));
		if (refTypeSelect) {
			// alert ("refTypeSelect found!");
			refTypeSelect.select ("option").each ( function (option) {
				option.selected = (option.value == objType);
				});
		}
		return;
	}
	else {
		alert ("target not found (" + name + ")");
	}
	window.close()
}

Event.observe (window, 'load', pageInit);
</script>
</head>
<body>

<%@ include file="pageHeader.jspf" %>

<div align="right">
	<html:form action="editor/sif.do">
		Search:
		<html:text property="searchString" styleId="searchString" size="30"/>
		<html:hidden property="recId" />
		<html:hidden property="elementId" />
		<html:hidden property="command" value="find" />
		<c:forEach var="sifType" items="${sif.sifTypes}">
			<input type="hidden" name="sifTypes" value="${sifType}" />
		</c:forEach>
		<html:submit value="  Go  "/>
	</html:form>
</div>

<%-- <h4>Element Id</h4>
<div>elementPath: <c:out value="${sif.elementPath}" /></div>
<div>elementID: ${sif.elementId}</div>
<div>refTypeSelectId: ${sif.refTypeSelectId}</div> --%>

<c:forEach var="objectType" items="${sif.objectMap}">
	<c:set var="xmlFormat" value="${objectType.key}" />
	<h4>${xmlFormat}</h4>
	<c:forEach var="reader" items="${objectType.value}">
		<div style="margin-bottom:8px">
			<b>${reader.title}</b>
			<div class="${reader.formatName}">
				<a href="#" class="refId" id="${reader.refId}">${reader.refId}</a>
			</div>
			<c:if test="${reader.xmlFormat == 'sif_activity'}">
				<div>${reader.preamble}</div>
			</c:if>
			<c:if test="${reader.xmlFormat != 'sif_activity'}">
				<div>${reader.description}</div>
			</c:if>
		</div>
	
	</c:forEach>
</c:forEach>


</body>

</html:html>
