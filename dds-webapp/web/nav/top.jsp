<%-- Import the DLESE GUI class that renders the Tree Menus --%>
<%@ page import="org.dlese.dpc.gui.OPMLtoHTMLTreeMenu" %>
<%@ page import="edu.ucar.dls.webapps.tools.GeneralServletTools" %>

<c:choose>
	<%-- If the DLESE library is up and running in this domain, display DLESE banners, etc: --%>
	<c:when test="${isDeploayedAtDL}">
		<%-- DLESE template top (banner menu) Note, pass in param &treeCache=off/on to turn on/off caching of the menu view --%>
		<c:import url="${domain}/dlese_shared/templates/content_top.html"/>
		<table class="dlese_treeMenuTable"><tr>
			   <td class="dlese_treeMenuTableCellMenu" valign="top">
			   		<%-- Pass in the requestURI, so it may be used in the menu logic --%>
					<c:set var="requestURI" value="${f:requestURI(pageContext.request)}"/>
					<%	
						try {
							String requestURI = (String) pageContext.getAttribute("requestURI");
							boolean ignoreQueryStringsInRequest = true;
							String urlToMenu = request.getContextPath() + 
										"/nav/dds_menu_dlese_deploy_opml.jsp?requestURI=" + requestURI;
							String treeMenuHtml = OPMLtoHTMLTreeMenu.getMenuHTML(urlToMenu, request, ignoreQueryStringsInRequest );
							pageContext.setAttribute("urlToMenu", urlToMenu );
							pageContext.setAttribute("treeMenuHtml", treeMenuHtml );
						} catch (Throwable t) {
							pageContext.setAttribute("treeMenuError", t );
						}
					%>
					<c:if test="${not empty treeMenuError}">
						<nobr>[ Menu error ]</nobr>
						<!-- Error with tree menu: 
							${treeMenuError}
						-->
					</c:if>
					<c:if test="${empty treeMenuHtml}">
						<nobr>[ Menu empty ]</nobr>
						<!--  URL to tree menu (DDS DLESE deploy): ${urlToMenu} -->
					</c:if>
					${treeMenuHtml}
			   </td>
			   <td class="dlese_treeMenuTableCellText">
	</c:when>
	
	<%-- If this is a stand-alone DDS installation, display those menus --%>
	<c:otherwise>
		<%-- The titleBar is updated with document.title via js --%>
		<div id="titleBar">Digital Discovery System (DDS)</div>
		<script>updateTitleBar();</script>
		<a href="${pageContext.request.contextPath}/index.jsp"><img id="ddsLogo" border="0" alt="Digital Discovery System (DDS)" title="Digital Discovery System (DDS)" src="${pageContext.request.contextPath}/images/dds_logo_sm.gif"/></a>
		<div id="ddsCustomBanner">${initParam.globalInstallationBanner}</div>
		<table class="dlese_treeMenuTable" border="0" cellpadding="0" cellspacing="0"><tr>
			   <td class="dlese_treeMenuTableCellMenu" valign="top">
			   		<%-- Pass in the requestURI, so it may be used in the menu logic --%>
					<c:set var="requestURI" value="${f:requestURI(pageContext.request)}"/>
					<%
						try {
							String requestURI = (String) pageContext.getAttribute("requestURI");
							boolean ignoreQueryStringsInRequest = true;
							String urlToMenu = request.getContextPath() + "/nav/dds_menu_opml.jsp?requestURI=" + requestURI;
							
							// If deployed at UCARConnect, DLESE or NCAR Library, force connection through port 80 instead of 443 (ssl) - when /admin is behind ssl:
							if(request.getServerName().endsWith("uc.dls.ucar.edu") || request.getServerName().endsWith("dlese.org") || request.getServerName().endsWith("nldr.library.ucar.edu"))
								urlToMenu = "http://" + request.getServerName() + request.getContextPath() + "/nav/dds_menu_opml.jsp?requestURI=" + requestURI;
							pageContext.setAttribute("urlToMenu", urlToMenu );
							String treeMenuHtml = OPMLtoHTMLTreeMenu.getMenuHTML(urlToMenu, request, ignoreQueryStringsInRequest );
							pageContext.setAttribute("treeMenuHtml", treeMenuHtml );
						} catch (Throwable t) {
							pageContext.setAttribute("treeMenuError", t );
						}							
					%>
					<c:if test="${not empty treeMenuError}">
						<nobr>[ Menu error ]</nobr>
						<!-- Error with tree menu: 
							${treeMenuError}
						-->
					</c:if>
					<c:if test="${empty treeMenuHtml}">
						<nobr>[ Menu empty ]</nobr>
						<!--  URL to tree menu (DDS stand-alone deploy): ${urlToMenu} -->
					</c:if>					
					${treeMenuHtml}						  
			   </td>
			   <td class="dlese_treeMenuTableCellText">
	</c:otherwise>
</c:choose>
		   


