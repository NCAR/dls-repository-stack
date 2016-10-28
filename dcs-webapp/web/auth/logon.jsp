<%@ include file="/JSTLTagLibIncludes.jsp" %>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Login</c:set>
<c:set var="guestUser" value="${applicationScope['guestUser']}" />
<html:html locale="true">
<head>
<script type="text/javascript" src="prototype.js"></script>
<title><st:pageTitle title="${title}" /></title>

<%-- <%@ include file="/baseHTMLIncludes.jsp" %> --%>
<link rel='stylesheet' type='text/css' href='auth-styles.css'>
<script type="text/javascript">

function doLoginAsGuest () {
		var params = {dest:"${param.dest}", username:"guest", password:"guest"};
		href = "${contextPath}/auth/logon.do?" + $H(params).toQueryString();
		window.location = href;
}
	
Event.observe (window, 'load', function (evnt) { 
	if ($('gestLoginButton'))
		Event.observe ($('gestLoginButton'), 'click', doLoginAsGuest, false); 
});

</script>
</head>

<body>

<c:if test="${not empty pageBanner}">${pageBanner}</c:if>

<%-- <%@ include file="header.jspf" %> --%>
<st:authPageHeader toolLabel="${title}" logoPath="${applicationScope.logo}"/>

<st:pageMessages />

<c:if test="${not empty guestUser && sf:hasRole (guestUser, param.requiredRole)}">
<div style="margin-top:15px">
	<input type="button" id="gestLoginButton" value="Login as Guest" /> to gain limited access to the
	functionality in this application.
	</div>
</c:if>

<c:if test="${not empty user || not empty param.requiredRole}">
	<div align="center" style="margin-top:15px" >

		<c:if test="${not empty user}">
				<div>You are currently Logged in as: ${user.username} (${user.fullName})</div>
		</c:if>
		<c:if test="${not empty param.requiredRole}" >
			<div>The page you are attempting to access requires a role of <b>${param.requiredRole}</b></div>
		</c:if>
	</div>
</c:if>

<html:form action="/auth/logon" focus="username">
<div align="center" style="margin-top:25px;">
<table border="0" cellpadding="4">
	<html:hidden name="infoForm" property="dest" value="${param.dest}" />
	<html:hidden name="infoForm" property="requiredRole" value="${param.requiredRole}" />
	<tr>
		<td colspan="2">
			<c:if test="${sf:listContains (loginModules,'edu.ucar.dls.schemedit.security.login.UcasLogin')}">
				<div><i>Please enter your UCAS username and password</i></div>
			</c:if>
			<c:if test="${sf:listContains (loginModules,'edu.ucar.dls.schemedit.security.login.LdapLogin')}">
				<div><i>Please enter your NSDL login and password</i></div>
			</c:if>
		</td>
	</tr>
  <tr>
    <th align="right">
      <bean:message key="prompt.username"/>
    </th>
    <td align="left">
      <html:text property="username" size="16" maxlength="16"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.password"/>
    </th>
    <td align="left">
      <html:password property="password" size="16"
                    redisplay="false"/>
    </td>
  </tr>

  <tr>
		<td>&nbsp;</td>
    <td align="left">
			<html:submit property="submit" value="Submit"/>
    </td>
  </tr>

	<%-- Links for NSDL Account support --%>
	<c:if test="${instanceName == 'MGR' && sf:listContains (loginModules,'edu.ucar.dls.schemedit.security.login.LdapLogin')}">
		<tr>
			<td colspan="2">
				<a href="https://nsdl.org/Authentication/user/reset_password_request">Reset your password</a>
				| 
				<a href="https://nsdl.org/Authentication/user/create?return_to=http://ncs.nsdl.org${contextPath}"/>create a new account</a>
			</td>
		</tr>
	</c:if>
	
</table>
</div>

</html:form>



</body>
</html:html>
