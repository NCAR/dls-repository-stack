<%-- EXPERIMENTAL pop-up user info --%>
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.schemedit.security.access.ActionPath" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Pop-up User Information</c:set>

<html:html locale="true">
<head>
<title><st:pageTitle title="${title}" /></title>
<%@ include file="/baseHTMLIncludes.jsp" %>
<link rel='stylesheet' type='text/css' href='${contextPath}/lib/access-styles.css'>
<link rel='stylesheet' type='text/css' href='${contextPath}/user/edit-user-styles.css'>

<script type="text/javascript">


function pageInit () {
	log ("hello world");
}

Event.observe (window, 'load', pageInit, false);

</script>
</head>
<body>
<st:pageHeader currentTool="" toolLabel="${title}" />

<st:pageMessages />

	<div class="table-banner"><div class="title">${userForm.fullname}</div></div>

	<div style="margin-left:10px;">
		<table width="100%" border="0" cellpadding="5" cellspacing="1" bgcolor="#ffffff">
			<tr valign="top">
				<td width="40%">
					<table cellpadding="5">
			

						<tr bgcolor="#ffffff">
							<td class="input-label">username</td>
							<td class="input">${userForm.username}</td>
							</td>
						</tr>

						
						<tr bgcolor="#ffffff">
							<td class="input-label">first name</td>
							<td class="input">${userForm.firstname}</td>
						</tr>
						
						<tr bgcolor="#ffffff">
							<td class="input-label">last name</td>
							<td class="input">${userForm.lastname}</td>
						</tr>
						

						
						<tr bgcolor="#ffffff">
							<td class="input-label">institution</td>
							<td class="input">${userForm.institution}</td>
						</tr>
						
						<tr bgcolor="#ffffff">
							<td class="input-label">department</td>
							<td class="input">department</td>
						</tr>
						
						<tr bgcolor="#ffffff">
							<td class="input-label">email</td>
							<td class="input">${userForm.email}</td>
						</tr>	
						
						
						<tr bgcolor="#ffffff">
							<td colspan="2">
							<h1>have to get the sessionBean(s) for named user ...</h1>
							</td>
						</tr>							
						
						<tr bgcolor="#ffffff">
							<td class="input-label">idle time</td>
							<td class="input">${sessionBean.timeSinceLastAccessed}</td>
						</tr>	
						
						<c:set var="lockedRecords" value="${sessionBean.lockedRecords}" />
						<c:if test="${not empty lockedRecords}">
							<c:choose>
								<c:when test="${fn:length(lockedRecords) == 1}">One Locked record</c:when>
								<c:when test="${fn:length(lockedRecords) > 1}">${fn:length(lockedRecords)} Locked records</c:when>
							</c:choose>
						</c:if>
						
					</table>
				</td>
				
			</tr>
		</table>
	</div>

	
</body>
</html:html>
