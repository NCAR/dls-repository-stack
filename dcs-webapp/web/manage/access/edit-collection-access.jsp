<%@ include file="/JSTLTagLibIncludes.jsp" %>

<%@ page import="edu.ucar.dls.schemedit.security.access.ActionPath" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title" value="Assign Collection Access"></c:set>
<html:html locale="true">
<head>
<title><st:pageTitle title="${title}" /></title>
<%@ include file="/manage/access/accessHTMLIncludes.jsp" %>
<script type="text/javascript">

function pageInit (evnt) {
	itemTableInit();
	if (${not empty caForm.username}) 
		highlight ($("${caForm.username}"));
	}
	
function selectall (role) {
	var className = role + "-access-radio";
		$$('.'+className).each (function (radio) {
			radio.checked = true;
			});
	}

function clearall (role) {
	var className = role + "-access-radio";
		$$('.'+className).each (function (radio) {
				if (radio.checked) {
					$(radio.name+'_none').checked = true;
				}
		});
	}
	
function addUserHandler (evnt) {
	var username = $F('add_user_select');
	if (username == "")
			alert ("Please select an existing user");
 	else {
		// alert ("send me back here (col: ${caForm.set.name}, with user: " + username + ")");
		var params = {
			username 		: username,
			command  		: "edit",
			collection  : "${caForm.set.setSpec}"
			}
		  window.location="${contextPath}/manage/collectionAccessManager.do?" + $H(params).toQueryString();
	}
}
	
function newUserHandler (evnt) {
	// Event.stop (evnt);
	var params = {
		command  : "create",
		referer  : "${contextPath}/manage/collectionAccessManager.do"
	}
	var href = "${contextPath}/user/userInfo.do?" + $H(params).toQueryString();
	// alert (href);
	window.location = href;
}
	
Event.observe (window, 'load', pageInit);
</script>
</head>
<body bgcolor="white" style="font-family:arial;font-size:80%">

<st:pageHeader currentTool="manage" toolLabel="${title}" />

<%-- breadcrumbs --%>
<st:breadcrumbs>
	<a href="../manage-home.jsp">Manage</a>
	<st:breadcrumbArrow />
	Assign User Access
	<st:breadcrumbArrow />
	<span class="current">${caForm.set.name}</span>
</st:breadcrumbs>

<st:pageMessages />

<div>Assign access to the current collection. To add a user not on the list, either 
create a <span class="doc-em">new user</span>, 
or add an <span class="doc-em">existing user</span>.
</div>

<table style="padding:20px 0px 3px 0px" width="100%">
	<tr valign="bottom">
		<td><div class="table-title">Collection: ${caForm.set.name}</div></td>
		<td align="right">
			<div align="right">
				<input type="button" value="Create new user" 
						onclick="newUserHandler()" />
			
				<c:if test="${not sf:hasRole (sessionScope['user'], 'admin') && not empty caForm.managableUsers}" >
							<span width="100px"/>
							<select id="add_user_select" onchange="addUserHandler()">
								<option value="">-- add existing user --</option>
								<c:forEach var="user" items="${caForm.managableUsers}">
									<option value="${user.username}">${user.lastName}, ${user.firstName} (${user.username})</option>
								</c:forEach>
							</select>
						</div>
				</c:if>
			</div>
		</td>
	</tr>
</table>

<html:form action="/manage/collectionAccessManager">

<html:hidden property="collection" />

<table class="item-table">
	<tr class="header-row">
		<td width="60px">None
			<div class="action-link">[ <a href="javascript:selectall('none')">all</a> ]</div>
		</td>
		<td width="60px">Cataloger
			<div class="action-link">[ <a href="javascript:selectall('cataloger')">all</a> |
			<a href="javascript:clearall('cataloger')">none</a> ]</div>
		</td>
		<td width="60px">Manager
			<div class="action-link">[ <a href="javascript:selectall('manager')">all</a> |
			<a href="javascript:clearall('manager')">none</a> ]</div>
		</td>
		<td width="20px">&nbsp;</td>
		<td align="left">
			<span class="table-title">User</span>
			&nbsp;&nbsp;
			Lastname, FirstName (Login)
		</td>
	</tr>
 	<c:forEach var="userRoleBean" items="${caForm.collectionRoles}">
		<c:set var="user" value="${userRoleBean.user}" />
		<c:set var="colRole" value="${userRoleBean.role}" />
		<c:if test="${colRole != 'admin'}">
			<tr class='item-table-row'>
				<td align="center">
					<input type="radio" class="none-access-radio" 
								 name="role_${user.username}" id="role_${user.username}_none" 
								 value="none"
						<c:if test="${colRole == 'none'}"> checked="checked"</c:if> />
				</td>	
				<td align="center">
					<input type="radio" class="cataloger-access-radio" 
								 name="role_${user.username}"
								 value="cataloger"
						<c:if test="${colRole == 'cataloger'}"> checked="checked"</c:if> />
				</td>
				<td align="center">
					<input type="radio" class="manager-access-radio" 
								 name="role_${user.username}" value="manager"
						<c:if test="${colRole == 'manager'}"> checked="checked"</c:if> />
				</td>
				<td width="20px">&nbsp;</td>
				<td  id="${user.username}"><b>${user.lastName}, ${user.firstName}</b> (${user.username})</td>
			</tr>
		</c:if>
	</c:forEach>
</table>
<table width="100%">
	<tr>

	
		<td align="center" style="white-space:nowrap;">
			<div style="margin-right:50px;">
					<html:submit property="command" value="save"/>
					<html:cancel/>
					<html:reset/>
			</div>
		</td>
	</tr>
</table>

</html:form>

</body>
</html:html>
