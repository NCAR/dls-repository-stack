<%@ include file="/JSTLTagLibIncludes.jsp" %>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Logout</c:set>
<c:set var="guestUser" value="${applicationScope['guestUser']}" />
<html:html locale="true">
<head>
<script type="text/javascript" src="prototype.js"></script>
<%-- <link rel='stylesheet' type='text/css' href='${contextPath}/styles.css'> --%>
<title><st:pageTitle title="${title}" /></title>

<%-- <%@ include file="/baseHTMLIncludes.jsp" %> --%>
<link rel='stylesheet' type='text/css' href='auth-styles.css'>

</head>

<body>

<st:authPageHeader toolLabel="${title}" logoPath="${applicationScope.logo}"/>

<st:pageMessages />

<div align="center">
		<div style="margin:15px 0px 10px 0px;"
			<span class="logout-confirm">You have successfully logged out</span>
		</div>
		<a href="${contextPath}/auth/logon.jsp">Click here to log back in</a>
</div>



</body>
</html:html>
