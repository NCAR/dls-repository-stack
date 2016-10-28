
<%-- This is the editorMaster file for Framework-config editors --%>
<%@ include file="/lib/includes.jspf" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="editedFormat"><%= (String)request.getAttribute ("editedFormat") %></c:set>
<html>
	<head>
		<%@ include file="/editor/standAloneEditorHTMLIncludes.jsp" %>
		<script type="text/javascript">
			<!--
			var submitAction="framework_config.do";
			// -->
		</script>
		<title><st:pageTitle title="Framework Configuration Editor (${editedFormat})" /></title>
	</head>
<body bgcolor=white onload="init('${sef.hash}','${sef.guardedExitPath}')">
<noscript>You must enable JavaScript to use this application!</noscript>
<a name="top"></a>
<%-- <%@ include file="page-header.jspf" %> --%>

<st:pageHeader currentTool="settings" toolLabel="Edit Configuration" editorMode="true"/>

<st:breadcrumbs>
	<a href="javascript:guardedExit('${contextPath}/admin/admin.do')">Settings</a>
	<st:breadcrumbArrow />
	<a  href="javascript:guardedExit('${contextPath}/admin/frameworks.do')">Metadata Frameworks</a>
		<st:breadcrumbArrow />
	Edit Configuration
	<st:breadcrumbArrow />
	<span class="current">${editedFormat}</span>
</st:breadcrumbs>

<%@ include file="navMenu.jspf" %>
<div class="framework-name">${editedFormat} framework</div>
<st:editorPageMessages/>

<html:form action="/editor/framework_config.do" method="Post">
	<input type="hidden" name="currentPage" value="${sef.currentPage}"/>
	<input type="hidden" name="nextPage" value=""/>
	<input type="hidden" name="command" value="validate"/>
	<input type="hidden" name="pathArg" value=""/>
	<input type="hidden" name="recId" value="${sef.recId}"/>
	<input type="hidden" name="confirmedExit" value=""/>
	
<c:catch var="renderError">
<jsp:include page="${sef.currentPageEncoded}.jsp"/>
</c:catch>
<c:if test="${not empty renderError}">
There was a problem rendering this page. Check to see that the xml record is well-formed for the 
schema defined by the ${sef.xmlFormat} framework
</c:if>
</html:form>
<%@ include file="navMenu.jspf" %>

</body>
</html>
