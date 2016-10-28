<%-- manageLink.tag	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<script type="text/javascript">

var mmCount = 0;
var manage_menu;
var mm_initialized = 0;

function mm_mouseOver (event) {
	mmCount = mmCount + 1;
	}
	
function mm_mouseOut (event) {
	mmCount = mmCount -1;
	}
	
function showManageMenu (event) {
	mm_init();
	// manage_menu.show() will not work
	manage_menu.setStyle({display:"block"});
	}
	
function mm_init () {
	if (mm_initialized == 0) {
		manage_menu = $("manage-menu");
		manage_menu.observe ("mouseover", mm_mouseOver, false);
		manage_menu.observe ("mouseout", mm_mouseOut, false);
		manage_menu.hide ();
		mm_initialized = 1;
	}
}

function mm_doc_mouseDown (event) {
	mm_init();
	
	if (mmCount == 0) {
		manage_menu.hide();
		}
}
	
Event.observe (document, "mousedown", mm_doc_mouseDown, false);
</script>

<c:set var="authorized" value="${sf:isAuthorized ('manage', sessionBean)}" />
<a href="admin.do" id="manage-link">Manage</a>

<div id="manage-menu" style="display:none" class="dropdown-menu-box">
	<div class="dropdown-menu-item">
		<a href="${contextPath}/manage/manage-home.jsp">Manage home</a>
	</div>
	<div class="dropdown-menu-item">
		<a href="${contextPath}/manage/collections.do">Manage Collections</a>
	</div>
	<div class="dropdown-menu-item">
		<a href="${contextPath}/manage/userManager.do">Manage Users</a>
	</div>
	<div class="dropdown-menu-item">
		<a href="${contextPath}/manage/collectionAccessManager.do">Assign Collection Access</a>
	</div>
</div>
<script type="text/javascript">
var manageLink = $('manage-link');

if (${not authorized}) {
	manageLink.setStyle ({color:"gray"});
}

manageLink.observe ('click', function (evnt) {
	// stop the link to prevent settings link href from trying to fire (in response to click)
	Event.stop (evnt);
	try {
	if (${authorized}) {
		showManageMenu (evnt);
		return;
		}
	
	var confirmStr = (${applicationScope["authenticationEnabled"]}) ?
		"You are not authorized to access Manage, would you like to login as a different user?" :
		"Please use caution when managing!";
	
	if (confirm (confirmStr)) 
		window.location = "${contextPath}/manage/manage-home.jsp";
		} catch (error) {
		 alert (error.toString());
		 }
});
</script>
