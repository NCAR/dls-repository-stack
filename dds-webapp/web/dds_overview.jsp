<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%--DDS Home Page--%>
<html>
<head>
	<title>Digital Discovery System</title>
	
	<c:set var="description">The Digital Discovery System (DDS) is an 
		XML repository and search server that is built on Lucene and runs in Tomcat.
		The repository provides a Search API and Update API that may be used to support a wide
		variety of applications.</c:set>
	
	<META NAME="description" CONTENT="${description}"/>				
	
	<%@ include file="/nav/head.jsp" %>
</head>
<body>
<c:if test="${isDeploayedAtDL}">
	<div class="dlese_sectionTitle" id="dlese_sectionTitle">
	    DDS<br/>Software
	</div>
	<div id="ddsLogo">
		<a href="${pageContext.request.contextPath}/dds_overview.jsp"><img border="0" alt="Digital Discovery System (DDS)" title="Digital Discovery System (DDS)" src="${pageContext.request.contextPath}/images/dds_logo_sm.gif"/></a>
	</div>
</c:if>
	<%@ include file="/nav/top.jsp" %>
					
	<div class="bodyContent">
		<p>${description}</p>

        <br/>
		<c:if test="${!isDeploayedAtDL}">
			<p><a href="dds_search.jsp">Search the Repository</a> - 
				Enter text to search the repository.</p>
		</c:if>
		<c:set var="cmLink">
			<c:choose>
				<c:when test="${isDeploayedAtDL}">
					<a href="javascript:void(0)" onclick="$('cmMsg').toggle()">Manage the Repository</a>
				</c:when>
				<c:otherwise>
					<a href="admin/admin.do">Manage the Repository</a>
				</c:otherwise>
			</c:choose>
		</c:set>
		<div style="position:relative">
			<p>${cmLink} - View and manage collections and items in the repository and change system settings.</p>
			<div id="cmMsg" style="display:none">
				<div style="position:absolute; top: 22px; left: 10px; width: 300px; height: 60px; padding: 5px; background-color: #FFFABF; opacity: 0.8; filter:alpha(opacity=80); z-index: 2;"></div>
				<div style="position:absolute; top: 32px; left: 20px; width: 280px; height: 40px; padding: 5px; background-color: #FFFCDF; border: solid 1px #aaa; z-index: 3;">
					Download and install DDS to view the Repository management features.
					<a href="javascript:void(0)" onclick="$('cmMsg').toggle()">close</a>
				</div>
			</div>
		</div>

		<p><a href="services/ddsws1-1/index.jsp">Search API</a> - 
		    Use a REST-RPC API to search the repository.</p>

		<p><a href="services/ddsupdatews1-1/ddsupdatews_api_documentation.jsp">Update API</a> -
            Use a REST-RPC API to update the repository.</p>

		<p><a href="services/examples/ddsws1-1/index.jsp">Search Client</a> -
			View and download client code that can be used to implement a variety of search applications.</p>

		<%--<h2>Installing and Configuring DDS</h2>--%>
		<%--<%@ include file="/docs/install_configure_summary_include.jsp" %>--%>

		<h2>More information</h2>
		<p><a href="docs/about_dds.jsp">About DDS</a> - 
		Read about development, licensing, and recent changes in the DDS project.</p>	
		
	</div>
	
	<%@ include file="/nav/bottom.jsp" %>
</body>
</html>




