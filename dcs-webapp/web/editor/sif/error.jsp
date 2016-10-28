<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">SIF Object Error</c:set>

<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>

<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<%@ include file="/baseHTMLIncludes.jsp" %>
<script type="text/javascript">
function pageInit () {

}

Event.observe (window, 'load', pageInit);
</script>
</head>
<body>
<%--  --%>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr valign="middle">
		<td align="left">
			<div class="tool-label">Error</div>
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

<logic:messagesPresent> 
<table width="100%" cellpadding="5" cellspacing="0">	
	<tr bgcolor="ffffff"> 
		<td>
			<ul>
				<html:messages id="msg" property="message"> 
					<li><bean:write name="msg"/></li>									
				</html:messages>
				<html:messages id="msg" property="error"> 
					<li><font color=red>Error: <bean:write name="msg"/></font></li>									
				</html:messages>
			</ul>
		</td>
	</tr>
</table>
</logic:messagesPresent>


</body>

</html:html>
