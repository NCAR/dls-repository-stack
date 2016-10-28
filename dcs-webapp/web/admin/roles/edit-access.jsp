<%@ include file="../../JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="textarea_width" value="40" />
<c:set var="title" value="Security Settings"></c:set>
<html:html locale="true">
<head>

<%@ include file="/admin/adminHTMLIncludes.jsp" %>

<link rel='stylesheet' type='text/css' href='../../lib/access-styles.css'>

<script>
	function deletePath (path) {
		if (confirm ("do you really want to delete \"" + path + "\"?")) {
			// alert ("deleting...");
			href = "accessManager.do?path="+path;
			href += "&command=delete";
			window.location = href;
		}
	}
	
function toggleActionsVisiblity (baseId, count) {
	for (var i=0;i<parseInt(count);i++)
		toggleVisibility (baseId + "_" + i.toString(10));
}
	
function toggleVisibility( elementID ) {
	$(elementID).toggle();
}
	
</script>

<title><st:pageTitle title="${title}" /></title>
<%-- <html:base/> --%>
</head>
<body bgcolor="white">
<st:pageHeader currentTool="settings" toolLabel="${title}" />


<p>Set Role Permissions for <i>url-patterns</i>. The <i>guarded actions</i> associated with a url-pattern 
correspond to individual Struts Actions. To set a role for a specific Action, click the <i>to url-pattern</i> button
on the right end of the row for the guarded action.</p>

<st:pageMessages />

<c:if test="${authenticationEnabled}" >
	
<html:form action="/admin/roles/accessManager" >
	
<%-- new path - not always visible --%>
<c:if test="${amForm.newPath}">
	<div class="table-banner"><div class="title">New Path</div></div>
	<html:hidden property="newPath"/>	
	<div style="margin-left:50px;">
		<table border="0" cellpadding="5" cellspacing="1" bgcolor="#ffffff">
		
			<tr bgcolor="#ffffff">
				<td class="input-label">path</td>
				<td class="input"><html:text property="path"/>

				</td>
			</tr>
			<tr bgcolor="#ffffff">
				<td class="input-label">description</td>
				<td class="input"><html:textarea property="description"/></td>
			</tr>
			<tr bgcolor="#ffffff">
				<td class="input-label">required role</td>
				<td class="input">
					<html:select property="role">
						<html:optionsCollection property="roleOptions"/>
					</html:select>
				</td>
			</tr>			
			<tr bgcolor="#ffffff">
				<td>&nbsp;</td>
				<td colspan="2" align="center">
					<html:submit  styleClass="action-button" property="command" value="save"/>
					<html:cancel  styleClass="action-button"/>
				</td>
			</tr>
		</table>
	</div>
</c:if>

<%-- main table --%>
<table class="table-banner" cellspacing="0" cellpadding="0">
	<tr>
		<td><div class="table-banner-title">Guarded Paths</div></td>
		<td width="90%">		
				<c:if test="${not amForm.newPath}">
					<input type="button" value="new path" 
						 onclick="location='accessManager.do?command=create'" />
				</c:if>
		</td>

	</tr>
</table>

<div align="center">
	<%-- <table width="100%" border="0" cellpadding="3" cellspacing="1" bgcolor="#999999"> --%>
	<table class="main-table" cellpadding="3" cellspacing="1">
		<tr class="table-header-row">
			<td class="table-header-cell">url-pattern</td>
			<td class="table-header-cell">guarded actions</td>
			<td class="table-header-cell">description</td>
			<td class="table-header-cell">required role</td>
			<td class="table-header-cell">&nbsp;</td>
		</tr>
	<c:forEach var="guardedPath" items="${amForm.guardedPaths}" varStatus="i">
	<c:set var="currentPath" value="${guardedPath.path == amForm.path}" />
		<tr class="${(currentPath) ? 'highlight-primary-row' : 'primary-row'}">
			<td>
				<div class="url-pattern"><a name="${guardedPath.path}"></a>${guardedPath.path}</div>

			</td>
			<td align="center">
				<c:set var="aCnt" value="${fn:length(guardedPath.actions)}" />
				<c:if test="${not guardedPath.isActionMapping && aCnt > 0}">
					<html:link 	href="javascript:toggleVisibility('guarded_path_${i.index}');" 
										title="Click to show/hide"><b>${aCnt}</b><img 
												src='../../images/btnExpand.gif' 
												alt="Click to show/hide" border="0" hspace="5" width="11" hieght="11" /></b></html:link>
				
				
					 <%-- <b>${aCnt}</b> <html:link style="font-size:85%" href="javascript:toggleVisibility('guarded_path_${i.index}')">show/hide</html:link> --%>
				</c:if>
			</td>
			<c:if test="${currentPath}">
				<html:hidden property="path" />
				<td>
					<html:textarea cols="${textarea_width}" property="description" />
				</td>
				<td>
					<html:select property="role" value="${guardedPath.role}">
						<html:optionsCollection property="roleOptions"/>
					</html:select>
				</td>
				<td>
					<html:submit styleClass="action-button" property="command" value="save"/>
					<html:cancel styleClass="action-button"/>
				</td>
			</c:if>
			<c:if test="${not currentPath}">
				<td>${(empty guardedPath.description) ? '&nbsp;' : guardedPath.description}</td>
				<td align="center">${(empty guardedPath.role) ? '<i>none</i>' : guardedPath.role}</td>
				<td align="center" style="white-space:nowrap">
					<input type="button" value="edit" class="action-button"
						onclick="location='accessManager.do?path=${guardedPath.path}&command=edit#${guardedPath.path}'" />
					<input type="button" value="delete" class="action-button"
						onclick="deletePath('${guardedPath.path}')" />
				</td>
			</c:if>
		</tr>
			<tr class="secondary-row" >
			<td colSpan="5">
				<c:if test="${not guardedPath.isActionMapping && not empty guardedPath.actions}">
				<div id="guarded_path_${i.index}" style="width:100%;display:none;padding:0px">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<c:forEach var="actionPath" items="${guardedPath.actions}">
					<tr>
						<td class="secondary-row-cell">
							<div class="indented">${actionPath.path}
							(${(empty guardedPath.role) ? '<i>none</i>' : guardedPath.role})
							</div>
						</td>
						<td class="secondary-row-cell">${(empty actionPath.description) ? '&nbsp;' : actionPath.description}</td>
						<td  class="secondary-row-cell" align="right">
							<%-- <html:link href="accessManager.do?command=create&path=${actionPath.path}.do">create url-pattern</html:link> --%>
							<c:set var="effective_path">${actionPath.path}.do</c:set>
							<c:if test="${effective_path != guardedPath.path}">
							<input type="button" value="to url-pattern" class="action-button" 
								onclick="location='accessManager.do?command=create&path=${actionPath.path}.do&description=${fn:escapeXml(actionPath.description)}#${actionPath.path}'"/>	
								</c:if>
						</td>
					</tr>
				</c:forEach>
				</table>
				</div>
				</c:if>
			</td>
			</tr>

	</c:forEach>
	</table>
</div>

</html:form>

</c:if>

</body>
</html:html>
