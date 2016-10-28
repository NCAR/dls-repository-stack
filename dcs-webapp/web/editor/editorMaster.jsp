<%@ include file="/lib/includes.jspf" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<%-- <res:setHeader name="Cache-Control">cache</res:setHeader> --%>

<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>

<html>
	<head>
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<%@ include file="/editor/editorHTMLIncludes.jsp" %>
		<script type="text/javascript">
			<!--
			var submitAction="edit.do";
			
			<c:forEach var="page" items="${sef.pageList.pages}">
				pages.push (new Page ('${page.name}', '${page.mapping}'));
			</c:forEach>
			
			currentPage = '${sef.currentPage}';
			
			document.observe ("dom:loaded", function () {
				init('${sef.hash}','${sef.guardedExitPath}');
			});
			
			// -->
		</script>

		<title><st:pageTitle title="Metadata Editor" /></title>
	</head>
<body bgcolor="white">

<noscript>You must enable JavaScript to use this application!</noscript>
<div id="to-top-icon"><st:upArrow contextPath="${contextPath}"/></div>

<st:pageHeader currentTool="editor" toolLabel="Metadata Editor"  editorMode="true"/>

<%@ include file="/lib/topNavMenu.jspf" %>


<st:editorPageMessages/>
<html:form styleId="metadata-form" action="/editor/edit.do" method="Post">
	<input type="hidden" name="currentPage" value="${sef.currentPage}"/>
	<input type="hidden" name="nextPage" value=""/>
	<input type="hidden" name="command" value="validate"/>
	<input type="hidden" name="pathArg" value=""/>
	<input type="hidden" name="recId" value="${sef.recId}"/>
	<input type="hidden" name="xmlFormat" value="${sef.xmlFormat}"/>
	<input type="hidden" name="confirmedExit" value=""/>


<jsp:include page="${sef.xmlFormat}/${sef.currentPageEncoded}.jsp"/>

</html:form>

<%@ include file="/lib/navMenu.jspf" %>

</body>
</html>
