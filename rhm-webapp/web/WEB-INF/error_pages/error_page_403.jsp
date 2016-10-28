<%@ include file="../../TagLibIncludes.jsp" %>

<html>
<head>
	<title>Forbidden (403)</title>
	<%@ include file="/head.jsp" %>
</head>

<body>
	<c:set var="pageViewContext" value="errorPages"/>
	<%@ include file="/top.jsp" %>

	<!-- CONTENT GOES HERE -->
	<h1>Forbidden</h1>
	
	<p>You are not authorized to perform this action or view this page.</p>

	<br/><br/><br/>
	
	<%@ include file="/bottom.jsp" %>
</body>
</html>
