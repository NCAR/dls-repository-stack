<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Access Settings</c:set>

<html:html>
<head>
	<title><st:pageTitle title="${title}" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	
	<%@ include file="/baseHTMLIncludes.jsp" %>
</head>

<body text="#000000" bgcolor="#ffffff">

<st:pageHeader toolLabel="${title}" currentTool="Settings" />

<table width="100%" border="0" align="center">
  <tr> 
    <td> 
		<st:settingsPageMenu  currentPage="access"/>		
  
		<st:pageMessages />
		
	</td>
  </tr>
	<tr>	
		<td>	
			<ul>
				<li><html:link page="/admin/roles/accessManager.do">Access Manager</html:link></li>
				<li><html:link page="/admin/roles/userManager.do">User Manager</html:link></li>
				<li><html:link page="/admin/roles/rolesManager.do">Collection Manager</html:link></li>
			</ul>
		</td>
	</tr>

</table>
	
</body>
</html:html>

