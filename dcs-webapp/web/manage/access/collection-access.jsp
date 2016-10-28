<%@ include file="/JSTLTagLibIncludes.jsp" %>

<%@ page import="edu.ucar.dls.schemedit.security.access.ActionPath" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title" value="Assign Collection Access"></c:set>
<html:html locale="true">
<head>
<title><st:pageTitle title="${title}" /></title>

<%@ include file="/manage/access/accessHTMLIncludes.jsp" %>

<script type="text/javascript">

// images must be defined so they are accessible to item-table.js
var openedImg = "${contextPath}/images/btnExpand.gif"
var closedImg = "${contextPath}/images/btnExpandClsd.gif"

function pageInit() {

	itemTableInit();
	
	// highlight current collection, if appropriate
	var collection = "${caForm.collection}";
	if (collection != "") {
		toggleVisibility (collection);
		highlight ($(collection+"_widget"));
		$(collection+"_widget").scrollTo()
	}
	
		// initialize handler for "edit-item-button
	$$(".edit-item-button").each ( function (button) 
		{ editItemInit (button); });
	
		// this code disables edit buttons when one is clicked. we can use it if we ever
		// decided to edit collection access "in place", rather than going to a new page.
		// DUH - why not just disable the button?
/* 	$$(".open-collection-widget").each ( function (widget) {
		widget.observe ('click', function (event) {
			Event.stop(event);
			// $$(".edit-access-button").each (function (button) {
				// button.disable();
				// });
			var setSpec = widget.id.substr ("widget_".length);
			toggleVisibility (setSpec);
			});
		}); */
}

// initialize the "editItem" button to respond to the "click" event
function editItemInit (button) {
		var collection = button.id.substr ("edit-item-".length);
		button.observe ('click', function (evnt) { 
			Event.stop (evnt);
			var params = {
				command  : "edit",
				collection : collection
			}
			var href = "${contextPath}/manage/collectionAccessManager.do?" + $H(params).toQueryString();
			// alert (href);
			window.location = href;
		} );
}

Event.observe (window, 'load', pageInit);
</script>
</head>
<body bgcolor="white" style="font-family:arial;font-size:80%">

<st:pageHeader currentTool="manage" toolLabel="${title}" />

<p>View the access to the collections for which you have <span class="doc-em">Manager</span> privilege.
To change the users that have access to a particular collection, 
select the <span class="doc-em">Edit access</span> link for that collection.
</p>

<st:pageMessages />

<c:if test="${authenticationEnabled}">

<html:form action="/manage/collectionAccessManager" styleId="access-form">
<div style="margin-top:10px;">
<table>
	<tr>
		<td align="left">
			<input type="button" value="Create a collection" 
					onclick="window.location='${contextPath}/manage/collections.do?command=new';" />
			</div>
		</td>
		<td width="50px">&nbsp;</td>
		<td align="left" style="white-space:nowrap">
				<div class="action-link">[ <a href="#" id="open-items-button">open all</a> |
				<a href="#" id="close-items-button">close all</a> ]</div>
		</td>
	</tr>
</table>
</div>

<div>
<table class="item-table">
	<tr class="header-row">
		<td colspan="2" align="left">
			<span class="table-title">Collection Name</span>
		</td>
	</tr>
	<c:forEach var="set" items="${caForm.sets}">
		
		<tr class='item-table-row'>
			<td>							
					<html:link styleId="${set.setSpec}_widget"
							href=""
							styleClass="open-close-widget" 
							title="Click to show/hide">
					<img class="widget-img"
							src='${contextPath}/images/btnExpandClsd.gif' 
							alt="Click to show/hide"  />
					<b>${set.name}</b>
				</html:link></td>
			<td align="right">
					<a href="#" id="edit-item-${set.setSpec}" 
								 class="edit-item-button">Edit access</a>
			</td>
		</tr>

		<%-- Inner table Info Form --%>
		<tr  id="${set.setSpec}_id" style="display:none">
			<td colspan="2">
					<table class="inner-table">
						<c:set var="hasUser" value="${false}" />
						<c:forEach var="userRoleBean" items="${caForm.collectionAccessMap[set.setSpec]}">
							<c:set var="user" value="${userRoleBean.user}" />
							<c:set var="colRole" value="${userRoleBean.role}" />
							<c:if test="${colRole != 'admin' && colRole != 'none'}">
								<tr>
									<c:set var="hasUser" value="${true}" />
									<td>
										<div class="indented">
											<a href="${contextPath}/manage/userManager.do?username=${user.username}"
												 title="See all collections accessible to this user"
													class="manage-link">${user.lastName}, ${user.firstName} (${user.username})</a></div>
									</td>
									<td width="300px" align="center"><div class="${colRole}-role">${colRole}</div></td>
								</tr>
							</c:if>
						</c:forEach>
						<c:if test="${not hasUser}">
							<tr>
								<td>
									<div class="indented"><i>This collection is accessible by no users.</i></div>
								</td>
							</tr>
						</c:if>
					</table>
			</td>
		</tr>
	</c:forEach>
</table>
</div>
</html:form>

</c:if>

</body>
</html:html>
