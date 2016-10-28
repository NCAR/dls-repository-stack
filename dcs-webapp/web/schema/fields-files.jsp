<%@ include file="/JSTLTagLibIncludes.jsp" %>
<jsp:useBean id="fff" class="edu.ucar.dls.xml.schema.action.form.FieldFilesForm"  scope="session"/>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title" value="Fields Files" />
<c:set var="command" value="${param.command}" />
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglibs-application.tld" prefix="app" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<link rel="stylesheet" href="styles.css" type="text/css">
	<link rel="stylesheet" href="fields-styles.css" type="text/css">
	<script type="text/javascript" src="${contextPath}/lib/javascript/prototype.js"></script>
	<script type="text/javascript" src="${contextPath}/lib/javascript/effects.js"></script>
	<script type="text/javascript" src="${contextPath}/lib/javascript/ncsGlobals.js"></script>
	<script type="text/javascript" src="toggler.js"></script>
	<script language="JavaScript" src="field-files-support.js"></script>
	<script language="JavaScript" src="${contextPath}/lib/best-practices-link-support.js"></script>
	<%-- Indent the check box sub-menus by this many pixels --%>
	<title>${title}</title>
	<script language="JavaScript">
	function pageInit () {
	
		initializeTogglers();
		
		initializeFileListingSortBy ('${fff.sortListingBy}');
	
		var allFrameworks = new Array();
		<c:forEach var="fmt" items="${fff.frameworks}">
			allFrameworks.push("${fmt}");
		</c:forEach>
	
		var fieldsFrameworks = new Array();
		<c:forEach var="fmt" items="${fff.fieldsFrameworks}">
			fieldsFrameworks.push("${fmt}");
		</c:forEach>
		
		var currentFormat = null;
		<c:if test="${not empty fff.framework}">
			currentFormat = "${fff.framework.xmlFormat}";
		</c:if>
		
		initializeFrameworkPicker (currentFormat, allFrameworks, fieldsFrameworks);
	}
	
	Event.observe (window, 'load', pageInit);
	
	</script>
</head>
<body bgcolor="white">
<a name="top">
<table border="0" width="100%">
	<tr valign="left" width="150px">
		<td align="left">
			<img src="../images/DLESE_logo_sm.gif">
		</td>
		<td align="center">
			<h1>${title}</h1>
		</td>
		<td align="right"><div class="nav-link"><a href="javascript:doSchemaViewer()">Schema Viewer</a></div></td>
	</tr>
</table>

<%-- Error messages --%>
<logic:messagesPresent>
	<p>
		<font color=red><b>Errors!</b> 
			<html:messages id="msg"><li><bean:write name="msg"/></li></html:messages></font>
	</p>
  <hr>
</logic:messagesPresent>

<%-- messages --%>
<logic:messagesPresent  message="true">
	<p>
		<font color=blue><b>
			<html:messages id="msg"  message="true"><li><bean:write name="msg"/></li></html:messages>
		</b></font>
	</p>
  <HR>
</logic:messagesPresent>

<%-- <div class="debug">
	<div>command: ${command}</div>
	<div>fields frameworks</div>
	<ul>
		<c:forEach var="activeFormat" items="${fff.fieldsFrameworks}">
			<li>${activeFormat}</li>
		</c:forEach>
	</ul>
</div> --%>

<html:form action="/schema/schema.do" method="Post">
<table width="100%">
	<tr>
		<td align="left"><h1>Framework: ${fff.framework.xmlFormat}</h1></td>
		<td align="right">
			<div class="control">
				<div class="collapsible-heading">Select Framework</div>
				<select name="frameworkSelect" id="frameworkSelect" >
				<option value="">-- select a framework --</option>
				</select>
			</div>
		</td>
	</tr>
	<c:if test="${not empty fff.framework}">
		<tr>
			<td>
				<div class="path">Schema URI: <a class="field-link" href="${fff.framework.schemaURI}">${fff.framework.schemaURI}</a></div>
				<div class="path">Fields List: <a class="field-link" href="${fff.directoryUri}">${fff.directoryUri}</a></div>
				<%-- <div>	
					<input type="text" id="file-list" size="60" value="${fff.directoryUri}" />
				</div> --%>
			</td>
			<td align="right">
				<input type="button" value="Reload framework" onclick="doReload()"/>
			</td>
		</tr>
	</c:if>
</table>
</html:form>

<c:if test="${not empty fff.directoryUri}">
	<div style="margin:10px 0px 10px 0px;text-align:center">
			[ <a href="javascript:doCommand('list')">List Fields Files</a> |
			 <a href="javascript:doCommand('check')">Field File Checker</a> ]
	</div>
</c:if>

<c:choose>
	<c:when test="${command == 'list'}">
		<%@ include file="field-files-listing.jsp" %>
	</c:when>
	<c:when test="${command == 'check'}">
		<%@ include file="field-files-checker.jsp" %>
	</c:when>
	<c:otherwise>
		<%-- <div>command (${command}) was not handled</div> --%>
	</c:otherwise>
</c:choose>


</body>
</html>
