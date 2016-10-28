<%@ include file="/JSTLTagLibIncludes.jsp" %>
<jsp:useBean id="svf" class="edu.ucar.dls.xml.schema.action.form.SchemaViewerForm"  scope="session"/>
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
	<script language="JavaScript" src="javascript.js"></script>
	<%-- Indent the check box sub-menus by this many pixels --%>
	<c:set var="indentAmount" value="18"/>
	<title>Schema Viewer - Type Definitions</title>
</head>
<body bgcolor="white">
<a name="top">
<table border="0" width="100%">
	<tr valign="left" width="150px">
		<td align="left">
			<img src="../images/DLESE_logo_sm.gif">
		</td>
		<td align="center">
			<h1>Schema Viewer - Type Definitions</h1>
		</td>
		<td align="right"><div class="nav-link"><a href="schema.do">Schema Viewer</a></div></td>
	</tr>
</table>

<%-- Error messages --%>
<logic:messagesPresent>
	<p>
		<font color=red><b>Oops! - Errors were found in your input.</b> 
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

<html:form action="/schema/schema.do" method="post" >

<div style="margin-top:20px" align="center">
<table width="66%">
	<tr valign="top">
		<td>
			<div class="collapsible-heading">Select Framework (s)</div>
			<c:forEach var="xmlFormat" items="${svf.frameworks}">
				<div class="selectable">
				<html:multibox property="selectedFrameworks" styleId="${xmlFormat}_id" value="${xmlFormat}"/>
					<label for="${xmlFormat}_id">${xmlFormat}</label>
				</div>
			</c:forEach>	
			<div class="nav-link" style="margin-top:3px;font-size:80%">
				[ 
				<a href=javascript:SelectAllFrameworks()>select all</a>
				|
				<a href=javascript:clearFrameworkSelections()>clear all</a>
				]
			</div>
		</td>
		<td>
			<div class="collapsible-heading">Select a report</div>
			<c:forEach var="fn" items="${svf.reportFunctions}">
				<div class="selectable">
				<html:radio styleId="${fn}_id" property="reportFunction" value="${fn}" />
				<label for="${fn}_id">${fn}</label>
				</div>
			</c:forEach>
		</td>
	</tr>
	<tr><td colspan="2" align="center">
		<html:submit value="report" property="command" />
	</td></tr>
</table>
</div>
</html:form>

<c:if test="${not empty svf.report}">
	<hr>
	<h2>Report</h2>
	<div class="nav-link">
		<span class="collapsible-heading">Selected Frameworks:</span>
		<c:forEach var="frame" items="${svf.selectedFrameworks}">
				<span style="margin:0px 3px 0px 3px"><a href="#${frame}">${frame}</a></span>
		</c:forEach>
	</div>
	
	<div style="margin-bottom:20px">
		<span class="collapsible-heading">Report Type:</span>
		<span class="selectable">${svf.reportFunction}</span>
	</div>		
	
	<c:forEach var="reportSegment" items="${svf.report}" varStatus="i">
		<a name="${reportSegment.key}">
		<table width="100%">
			<tr>
				<td><div style="font-weight:bold;font-size:120%">${reportSegment.key}</h4></td>
				<td align="right">
					<c:if test="${not i.first}">
						<div class="nav-link"><a href="#top">To Top</a></div>
					</c:if>
				</td>
			</tr>
		</table>
		<ul>
			<c:if test="${not empty reportSegment.value}">
				<c:forEach var="def" items="${reportSegment.value}">
					<li>
						<div  class="collapsible-heading">${def.qualifiedName}</div>
						<div class="nav-link"><a href="${def.location}" target="_blank">${def.location}</a></div>
						<pre><c:out value="${def.elementAsXml}"/></pre>
					</li>
				</c:forEach>
			</c:if>
			<c:if test="${empty reportSegment.value}"><li>
				<div class="plain">no <i>${svf.reportFunction}</i> found</div>
			</li></c:if>
		</ul>
	</c:forEach>
	<div align="right"><div  class="nav-link"><a href="#top">To Top</a></div></div>
</c:if>


</body>
</html>
