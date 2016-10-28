<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title" value="Load NDR Collection" />
<html>
<head>
	<%@ include file="/baseHTMLIncludes.jsp" %>
	<title><st:pageTitle title="${title}" /></title>
</head>
<body bgcolor=white>
<st:pageHeader currentTool="manage" toolLabel="${title}" />

<st:pageMessages />

<blockquote>
	<h3><a href="${contextPath}/manage/collections.do?command=showIndexingMessages">Click here to see indexing progress</a></h3>
</blockquote>

</body>
</html>
