<%@ include file="/JSTLTagLibIncludes.jsp" %>

<%@ page import="edu.ucar.dls.schemedit.security.access.ActionPath" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title" value="Assign User Access"></c:set>
<html:html locale="true">
<head>
<title><st:pageTitle title="${title}" /></title>
<%@ include file="/manage/access/accessHTMLIncludes.jsp" %>

<script type="text/javascript">
<!--
function selectall (role) {
	var className = role + "-access-radio";
		$$('.'+className).each (function (radio) {
			radio.checked = true;
			});
	}

function clearall (role) {
	var className = role + "-access-radio";
		$$('.'+className).each (function (radio) {
				// radio.checked = false;
				if (radio.checked) {
					$(radio.name+'_none').checked = true;
				}
		});
	}
	
Event.observe (window, 'load', itemTableInit);
	
//-->
</script>

<%-- <html:base/> --%>
</head>
<body bgcolor="white" style="font-family:arial;font-size:80%">

<st:pageHeader currentTool="manage" toolLabel="${title}" />

<%-- breadcrumbs --%>
<st:breadcrumbs>
	<a href="../manage-home.jsp">Manage</a>
	<st:breadcrumbArrow />
	<a  href="../userManager.do">Manage Users</a>
	<st:breadcrumbArrow />
	Assign User Access
	<st:breadcrumbArrow />
	<span class="current">${umForm.user.username}</span>
</st:breadcrumbs>

<st:pageMessages />

<div>Assign access for this user to the collections you manage.</div>

<html:form action="/manage/userManager" >
<html:hidden property="username" />

<div class="table-title" style="padding:10px 0px 3px 0px">
	User: ${umForm.user.firstName} ${umForm.user.lastName} (${umForm.user.username})
</div>

<table class="item-table">
	<tr class="header-row" valign="top">
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
		<td align="left">Collection Name</td>
</tr>

<c:forEach var="crBean" items="${umForm.userRoleMap[umForm.username]}">
	<tr class="item-table-row">
		<td align="center">
			<input type="radio" class="none-access-radio" 
						 name="role_${crBean.collectionKey}" id="role_${crBean.collectionKey}_none" 
						 value="none"
				<c:if test="${crBean.role == 'none'}"> checked="checked"</c:if> />
		</td>
		<td align="center">
			<input type="radio" class="cataloger-access-radio" 
						 name="role_${crBean.collectionKey}"
						 value="cataloger"
				<c:if test="${crBean.role == 'cataloger'}"> checked="checked"</c:if> />
		</td>
		<td align="center">
			<input type="radio" class="manager-access-radio" 
						 name="role_${crBean.collectionKey}" value="manager"
				<c:if test="${crBean.role == 'manager'}"> checked="checked"</c:if> />
		</td>
		<td width="20px">&nbsp;</td>
		<td style="white-space:nowrap">
			${crBean.collectionName}  <%-- (${crBean.collectionKey}) --%>
		</td>
	</tr>
</c:forEach>

</table>
<table width="100%">
	<tr>
		<td align="center" style="white-space:nowrap;">
			<div style="margin-right:50px;">
					<html:submit  property="command" value="save"/>
					<html:cancel />
					<html:reset />
			</div>
		</td>
	</tr>
</table>

</html:form>

</body>
</html:html>
