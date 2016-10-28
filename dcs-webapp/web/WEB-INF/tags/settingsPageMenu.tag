<%-- settingsPageMenu.tag
	- renders a menu of settings pages
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="currentPage" %>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>

<table class="nav-menu-box"><tr><td>
<table cellspacing="1" cellpadding="3" border="0"  width="100%" bgcolor="black"> 
<tr align="center">
		<c:if test="${empty currentPage}">
			<td class="nav-menu-item-selected"><div class="nav-menu-text">Settings home</div></td>
		</c:if>
		<c:if test="${not empty currentPage}">
			<td class="nav-menu-item"><div class="nav-menu-text"><a href="${contextPath}/admin/admin.do">Settings home</a></div></td>
		</c:if>
		
		<c:if test="${currentPage == 'collections'}">
			<td class="nav-menu-item-selected"><div class="nav-menu-text">Collection Settings</div></td>
		</c:if>
		<c:if test="${currentPage != 'collections'}">
			<td class="nav-menu-item"><div class="nav-menu-text"><a href="${contextPath}/admin/admin.do?page=collections">Collection Settings</a></div></td>
		</c:if>

		<c:if test="${currentPage == 'editors'}">
			<td class="nav-menu-item-selected"><div class="nav-menu-text">Metadata Editors</div></td>
		</c:if>
		<c:if test="${currentPage != 'editors'}">
			<td class="nav-menu-item"><div class="nav-menu-text"><a href="${contextPath}/admin/admin.do?page=editors">Metadata Editors</a></div></td>
		</c:if>
		
		<c:if test="${currentPage == 'sessions'}">
			<td class="nav-menu-item-selected"><div class="nav-menu-text">Sessions</div></td>
		</c:if>
		<c:if test="${currentPage != 'sessions'}">
			<td class="nav-menu-item"><div class="nav-menu-text"><a href="${contextPath}/admin/admin.do?page=sessions">Sessions</a></div></td>
		</c:if>	
		
		<c:if test="${currentPage == 'services'}">
			<td class="nav-menu-item-selected"><div class="nav-menu-text">Web Services</div></td>
		</c:if>
		<c:if test="${currentPage != 'services'}">
			<td class="nav-menu-item"><div class="nav-menu-text"><a href="${contextPath}/admin/admin.do?page=services">Web Services</a></div></td>
		</c:if>			
		
		<c:if test="${currentPage == 'index'}">
			<td class="nav-menu-item-selected"><div class="nav-menu-text">Index Settings</div></td>
		</c:if>
		<c:if test="${currentPage != 'index'}">
			<td class="nav-menu-item"><div class="nav-menu-text"><a href="${contextPath}/admin/admin.do?page=index">Index Settings</a></div></td>
		</c:if>		

		<c:if test="${currentPage == 'security'}">
			<td class="nav-menu-item-selected">
				<div class="nav-menu-text">Security Settings</div></td>
		</c:if>
		<c:if test="${currentPage != 'security'}">
			<td class="nav-menu-item"><div class="nav-menu-text">
				<a href="${contextPath}/admin/roles/accessManager.do">Security Settings</a></div></td>
		</c:if>		
		
	</tr>
</table>
</td></tr></table>
