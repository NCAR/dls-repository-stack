<%@ page language="java" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:url value="${ initParam.siteSearchBaseUrl }" var="forwardRequest">
	<c:param name="query" value="${ param.q }" />
	<c:param name="hitsPerPage" value="10" />
	<c:param name="hitsPerSite" value="0" />
	<c:param name="over" value="3" />
	<c:choose>
		<c:when test="${ not empty param.s }">
			<c:param name="start" value="${ param.s }" />
		</c:when>
		<c:otherwise>
			<c:param name="start" value="0" />
		</c:otherwise>
	</c:choose>
</c:url>
<c:catch var="searchError">
	<c:import url="${ forwardRequest }" />
</c:catch>

<c:if test="${ not empty searchError }">
	<c:set var="domain"><%= "http://" 
		+ request.getServerName() + ":" 
		+ request.getServerPort() %></c:set>
	<html>
		<head>	
			<title>DLESE Search Error</title>
			<c:import url="${domain}/dlese_shared/templates/header.html" /> 
		</head>
		<body>
			<c:import url="${domain}/dlese_shared/templates/top_content.html"/>
		
			<p>We're sorry, but our site search is currently unavailable...
			</p>
			
			<!-- ${ searchError } -->
			
			<c:import url="${domain}/dlese_shared/templates/bottom_content.html"/>
		</body>
	</html>
</c:if>

