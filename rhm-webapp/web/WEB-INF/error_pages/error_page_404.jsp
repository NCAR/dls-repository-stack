<%@ include file="/TagLibIncludes.jsp" %>

<html:html>

<head>
<title>Page not found (404)</title>

<%@ include file="/head.jsp" %>
</head>

<body>
<c:set var="pageViewContext" value="errorPages"/>
<%@ include file="/top.jsp" %>


	<!-- CONTENT GOES HERE -->
	<h1>Page not found</h1>
	
	<p>The page you requested cannot be found.  Please check the
	address you have indicated in your Web browser and try again.</p>

<br/><br/><br/>

<%@ include file="/bottom.jsp" %>
</body>
</html:html>

