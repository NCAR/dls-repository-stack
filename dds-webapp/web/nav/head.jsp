<%@ include file="/JSTLTagLibIncludes.jsp" %>

<%-- Set some vars used in the JSPs (also needed for the below catch to work) --%>
<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>

<%-- Determine if this DDS is deploayed at the DLESE DL --%>
<c:catch var="library_not_available">
	<c:import var="temp" url="${domain}/library/"/>
</c:catch>
<c:set var="isDeploayedAtDL" value="${empty library_not_available}"/>
<c:set var="isAdminPage" value="${fn:contains(f:requestURI(pageContext.request),'/admin/')}"/>

<script type="text/javascript" language="javascript" src="https://ajax.googleapis.com/ajax/libs/prototype/1.7.2.0/prototype.js"></script>

<c:choose>
	<%-- If the DLESE library is up and running in this domain, display DLESE banners, etc: --%>
	<c:when test="${isDeploayedAtDL}">
		<c:import url="${domain}/dlese_shared/templates/header_refs.html" />
		<link rel="stylesheet" type="text/css" href='${pageContext.request.contextPath}/dds_styles_dlese_deployment.css'>
	</c:when>
	
	<%-- If this is a stand-alone DDS installation, display those menus and styles --%>
	<c:otherwise>
		<link rel="stylesheet" type="text/css" href='${pageContext.request.contextPath}/nav/dds_styles_menu.css'>
		<link rel="stylesheet" type="text/css" href='${pageContext.request.contextPath}/dds_styles.css'>
		<script type='text/javascript' src='${pageContext.request.contextPath}/js/dds_scripts.js'></script>	
	</c:otherwise>
</c:choose>




