<%@ include file="../../TagLibIncludes.jsp" %>


<%-- The following is necessary to make BASIC authorization/401 work properly in Tomcat: --%>
<% 
	response.addHeader("WWW-Authenticate", "BASIC realm=\"UCAR Harvest Repository Manager\"");
	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
%>

<html>
<head>
	<title>Unauthorized (401)</title>
	<%@ include file="/head.jsp" %>
</head>

<body>
	<c:set var="pageViewContext" value="errorPages"/>
	<%@ include file="/top.jsp" %>

	<!-- CONTENT GOES HERE -->
	<h1>Unauthorized</h1>
	
	<p>You are not authorized to perform this action or view this page.</p>

	<br/><br/><br/>
	
	<%@ include file="/bottom.jsp" %>
</body>
</html>
