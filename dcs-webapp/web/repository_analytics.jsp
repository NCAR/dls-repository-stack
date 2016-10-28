<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Repository Analytics</c:set>

<html:html>
<head>
<title><st:pageTitle title="${title}" /></title>
<meta charset="utf-8">
<%@ include file="/baseHTMLIncludes.jsp" %>
<%@ include file="/analytics/head.jsp" %>
<script>
	// Override the method below to set the focus to the appropriate field.
	function sf(){}	
</script>
</head>
<body>

<st:pageHeader currentTool="Settings" toolLabel="${title}" />

<br>
<%@ include file="/analytics/body.jsp" %>
	
</body>
</html:html>
