<%-- $Id: editor-admin.jsp,v 1.4 2008/10/14 17:41:03 ostwald Exp $ --%>
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title" value="${adminForm.frameworkName} Settings" />
<%-- Admin.jsp
	admin for any framework
	- use SchemEditAdminForm
	- bean has been populated by SchemEditAdminAction
	-  
--%>

<html>
<head>
	<%@ include file="/baseHTMLIncludes.jsp" %>
	<script language="JavaScript" src="editing/admin-support.js"></script>
	<title><st:pageTitle title="${title}" /></title>
</head>
<body bgcolor=white> 

<st:pageHeader toolLabel="${title}" currentTool="settings" />

<st:breadcrumbs>
	<a href="../admin/admin.do">Settings</a>
	<st:breadcrumbArrow />
	<a  href="../admin/frameworks.do">Metadata Frameworks</a>
	<st:breadcrumbArrow />
	<span class="current">${title}</span>
</st:breadcrumbs>

<%-- <h2>${adminForm.frameworkName} Editor Settings</h2> --%>

<st:pageMessages />

<h3>Schema</h3>

<p>
	This framework is currently configured from a schema file located at<br>
	<b><a href="${adminForm.workingSchemaURI}" target="_blank">${adminForm.workingSchemaURI}</a></b>
</p>

<p>
	<input type="button" value="Reload schema" onclick="doReloadSchema()" />
			 Reload the framework schema and regenerate editor pages.
</p>

<BR/>
		 
<h3>Fields Files</h3>
<p>The fields files contain the best practices and defintions for the framework's fields.</p>

<p>
<input type="button" value="Reload FieldInfo" onclick="doReloadFieldInfo()" />
	  Reload best practices and Field Definitions.
</p>
	   
<br />
	   
<h3>Change Configuration</h3>
<p>
	Use this form to change the schemaURI for the framework, or to change the way the editor pages are rendered.
</p>
<html:form action="/admin/editor_admin.do" method="Post" enctype="multipart/form-data" onsubmit="doUpdate()">
	<input type="hidden" name="xmlFormat" value="${adminForm.xmlFormat}"/>
	<input type="hidden" name="command" value=""/>
	<table border="0" bgcolor="#cccccc" cellspacing="1" cellpadding="3">
		<tr bgcolor="white">
			<td>
				Schema URI
			</td>
			<td>
				<st:fieldMessage propertyId="schemaURI" />
				<html:text property="schemaURI" size="80"/>
			</td>
	    </tr>
 		<tr bgcolor="white">
			<td>
				Renderer
			</td>
			<td>
				<select name="renderer">
				<c:forEach var="item" items="${adminForm.renderers}">
					<option value="${item}" <c:if test="${item == adminForm.renderer}">SELECTED</c:if> >
					${item}</option>
				</c:forEach>
				</select>
			</td>
	    </tr>
		
		<tr bgcolor="white"><td>&nbsp;</td>
			<td align="center">
				<input type="button" value="exit" onclick="document.location = '${contextPath}/admin/frameworks.do'" />
				<input type="button" value="update" onclick="doUpdate()" />
			</td>
		</tr>
			
	</table>
</html:form>

</body>
</html>
