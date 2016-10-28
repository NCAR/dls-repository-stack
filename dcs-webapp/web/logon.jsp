<%@ include file="/JSTLTagLibIncludes.jsp" %>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Logon Form</c:set>
<c:set var="guestUser" value="${applicationScope['guestUser']}" />
<html:html locale="true">
<head>
<%-- <link rel='stylesheet' type='text/css' href='${contextPath}/styles.css'> --%>
<title>${title}</title>

</head>
<body>
<%-- <%@ include file="/baseHTMLIncludes.jsp" %> --%>

<script type="text/javascript">

function doLoginAsGuest () {
		var params = {dest:"${param.dest}", username:"${guestUser.username}", password:"${guestUser.password}"};
		href = "${contextPath}/logon.do?" + $H(params).toQueryString();
		window.location = href;
}
	
/* Event.observe (window, 'load', function (evnt) { 
	if ($('gestLoginButton'))
		Event.observe ($('gestLoginButton'), 'click', doLoginAsGuest, false); 
}); */

</script>
</head>

<body text="#000000" bgcolor="#ffffff">

<h1>OBSOLETE</h1>

<%-- <st:pageHeader currentTool="" toolLabel="${title}"/> --%>

<st:pageMessages />

<%-- <c:if test="${not empty param.dest}">
<div>${param.dest} requires authentication</div>
</c:if> --%>

<c:if test="${not empty guestUser && sf:hasRole (guestUser, param.requiredRole)}">
<div style="margin:0px 0px 30px 20px">
	<input type="button" id="gestLoginButton" value="Login as Guest" /> to gain limited access to the
	functionality in this application.
	</div>
</c:if>

<%-- <c:if test="${not empty applicationScope['guestUser']}">
	${.username}: ${applicationScope['guestUser'].password}
</c:if> --%>

<html:form action="/logon" focus="username">
<table border="0" width="100%">
	<html:hidden name="infoForm" property="dest" value="${param.dest}" />
	<html:hidden name="infoForm" property="requiredRole" value="${param.requiredRole}" />
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
      <html:password property="password" size="16" maxlength="16"
                    redisplay="false"/>
    </td>
  </tr>

  <tr>
    <td align="right">
      <html:submit property="submit" value="Submit"/>
    </td>
    <td align="left">
      <html:reset/>
    </td>
  </tr>

</table>

</html:form>

</body>
</html:html>
