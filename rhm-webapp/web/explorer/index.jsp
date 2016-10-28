<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/TagLibIncludes.jsp" %>
<html>
<head>
	<title>Explore OAI data providers</title>
	<%@ include file="/head.jsp" %>
</head>
<body>
	<%@ include file="/top.jsp" %>
	
	<h1>OAI Explorer</h1>
	
	<c:set var="explorerContext" value="standAlone"/>
	<%@ include file="/oai_explorer.jsp" %>
	
	<%@ include file="/bottom.jsp" %>
</body>
</html>


