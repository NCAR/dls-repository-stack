<%-- $Id: editors_settings.jsp,v 1.4 2008/10/14 17:41:03 ostwald Exp $ --%>
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Metadata Frameworks</c:set>
<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<%@ include file="/admin/adminHTMLIncludes.jsp" %>

</head>

<body text="#000000" bgcolor="#ffffff">

<st:pageHeader toolLabel="${title}" currentTool="settings" />

<h1 style="color:red;font-size:200%" align="center">OBSOLETE!</h1>

<table width="100%" border="0" align="center">
  <tr> 
    <td> 
  
		<st:pageMessages okPath="admin.do?page=editors" />
				
	<tr>
		<td colspan=2>
		<p>
			The following metadata frameworks and indicated versions are supported.
			<br />
			To change the XML schema supporting a metadata framework, click on the name of the framework.
			<br />
			To change configurations associated with a metadata framework, click the appropriate edit link.
		</p>
			
		
		<div style="margin-left:50px">
			<table bgcolor="#666666" cellpadding="6" cellspacing="1" border="0">
				<tr bgcolor='#CCCEE6'>
					<td align="center" nowrap><b>Framework</b></td>
					<td align="center" nowrap><b>Format</b></td>
					<td align="center" nowrap><b>Version</b></td>
					<td align="center" nowrap><b>Edit Settings</b></td>
					<td align="center" nowrap><b>Reload</b></td>
				</tr>
				<c:forEach var="entry" items="${daf.frameworks}">
					<tr class='dcs-table-row'> 
						<c:set var="framework" value="${entry.value}" />
						<td><a href="../admin/editor_admin.do?xmlFormat=${framework.xmlFormat}" 
										title="edit settings"><b>${framework.name}</b></a></td>
						<td align="center">${framework.xmlFormat}</td>
						<td align="center">
							<c:if test="${not empty framework.version}">${framework.version}</c:if>
							<c:if test="${empty framework.version}">&nbsp;</c:if>
						</td>
						<td align="center">
							<a href="../editor/framework_config.do?recId=${framework.xmlFormat}&command=edit&src=local">Edit</a>
						</td>
						<td align="center">
							<a href="../admin/editor_admin.do?xmlFormat=${framework.xmlFormat}&command=loadFramework">Reload</a>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		</td>
	</tr>
	</table>

<c:if test="${not empty daf.unloadedFrameworks}">
	<h4>Warning - the following frameworks are avaliable but not currently loaded</h4>
	<div style="margin-left:50px">
			<select name="format-to-load" id="format-to-load">
				<option value="">-- Select a format --</option>
				<c:forEach var="xmlFormat" items="${daf.unloadedFrameworks}">
					<option value="${xmlFormat}">${xmlFormat}</option>	
				</c:forEach>
			</select>
<%-- 		<table bgcolor="#666666" cellpadding="6" cellspacing="1" border="0">
			<tr bgcolor='#CCCEE6'>
				<td align="center" nowrap><b>Framework</b></td>
				<td align="center" nowrap><b>Format</b></td>
				<td align="center" nowrap><b>Version</b></td>
			</tr>
			<c:forEach var="entry" items="${daf.unloadedFrameworks}">
				<tr class='dcs-table-row'> 
					<c:set var="framework" value="${entry.value}" />
					<td><b>${framework.name}</b></td>
					<td align="center">${framework.xmlFormat}</td>
					<td align="center">
						<c:if test="${not empty framework.version}">${framework.version}</c:if>
						<c:if test="${empty framework.version}">&nbsp;</c:if>
					</td>
				</tr>
			</c:forEach>
		</table> --%>
	</div>
</c:if>

</body>
</html:html>

