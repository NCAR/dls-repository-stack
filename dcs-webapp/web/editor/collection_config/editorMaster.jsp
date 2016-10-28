
<%-- This is the editorMaster file for Collection configuration editors --%>
<%@ include file="/lib/includes.jspf" %>
<c:set var="title">${sef.collectionName} Settings</c:set>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<html>
	<head>
		<%@ include file="/editor/standAloneEditorHTMLIncludes.jsp" %>
		
		<script type="text/javascript">
			<!--
			var submitAction="collection_config.do";
	
 			function doSaveConfig () {
				document.sef.command.value = "save";
				return doSubmit();
			}
			// -->
		</script>
		<title><st:pageTitle title="Collection Configuration Editor (${sef.collectionName})" /></title>
	</head>
<body bgcolor=white onload="init('${sef.hash}','${sef.guardedExitPath}')">
<noscript>You must enable JavaScript to use this application!</noscript>
<a name="top"></a>

<%-- <%@ include file="page-header.jspf" %> --%>
<st:pageHeader toolLabel="Collection Configuration" currentTool="settings"  editorMode="true"/>

<st:breadcrumbs>
	<a href="javascript:guardedExit('${contextPath}/admin/admin.do')">Settings</a>
	<st:breadcrumbArrow />
	<a  href="javascript:guardedExit('${contextPath}/admin/admin.do?page=collections')">Collections Settings</a>
	<st:breadcrumbArrow />
	<span class="current">${sef.collectionName}</span>
</st:breadcrumbs>


<%@ include file="navMenu.jspf" %>

<st:editorPageMessages/>

<html:form action="/editor/collection_config.do" method="Post">
	<input type="hidden" name="currentPage" value="${sef.currentPage}"/>
	<input type="hidden" name="nextPage" value=""/>
	<input type="hidden" name="command" value="validate"/>
	<input type="hidden" name="pathArg" value=""/>
	<input type="hidden" name="recId" value="${sef.recId}"/>
	<input type="hidden" name="confirmedExit" value=""/> 
	
	<p>Edit settings for the <b>${sef.collectionName}</b> collection:
	<li><i>statusFlags</i> - the status labels and descriptions available to assign to records in this collection</li>
	<li><i>exportDirectory</i> - specifies the default directory to which records are exported.
	<blockquote>
		NOTE: The exportDirectory value is <i>relative</i> to the <i>Base Export Eirectory</i>, which is configured to be <br />
			<img src="${contextPath}/images/file_folder2.gif" width="12" height="12"> ${sef.baseExportDir}. 
			To change the <i>Base Export Directory</i>, edit the context parameter &quot;exportBaseDir&quot; in server.xml or web.xml.
			</blockquote>
	</li>
	</p>
	
<jsp:include page="${sef.currentPageEncoded}.jsp"/>

</html:form>

<%@ include file="navMenu.jspf" %>

</body>
</html>
