<%@ include file="JSTLTagLibIncludes.jsp" %>
<html>
<head>
<c:choose>
	<c:when test="${authenticationEnabled == 'true'}">
		<meta http-equiv="refresh" content="0;url=auth/logon.jsp">
	</c:when>
	<c:otherwise>
		<meta http-equiv="refresh" content="0;url=browse/home.do">
	</c:otherwise>
</c:choose>
</head>
<body/>
</html>
<html>

