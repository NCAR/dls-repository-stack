<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>

<%-- Determine if this DDS is deploayed at the DLESE DL --%>
<c:catch var="library_not_available">
	<c:import var="temp" url="${domain}/library/"/>
</c:catch>
<c:set var="isDeploayedAtDL" value="${empty library_not_available}"/>
<c:set var="isAdminPage" value="${fn:contains(f:requestURI(pageContext.request),'/admin/')}"/>

<c:choose>
	<%-- If the DLESE library is up and running in this domain, redirect to it: --%>
	<c:when test="${isDeploayedAtDL}">
		<c:redirect url="${domain}/library/" />
	</c:when>
	<c:when test="${!fn:endsWith(pageContext.request.requestURL,'index.jsp')}">
		<c:redirect url="/index.jsp" />
	</c:when>
	
	<%-- Otherwise show the standard DDS overview links and navigation: --%>
	<c:otherwise>
		<%@ include file="/dds_overview.jsp" %>
	</c:otherwise>
</c:choose>



