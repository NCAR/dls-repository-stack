<%@ page language="java" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="domain"><%= "http://" 
		+ request.getServerName() + ":" 
		+ request.getServerPort() %></c:set>
		
<c:url value="${ domain }/news_opportunities/search.jsp" var="forwardRequest">
	<c:param name="q" value="${ param.q }" />
	<c:param name="s" value="${ param.s }" />
	<c:param name="announcementType" value="${ param.announcementType }" />
</c:url>
<c:catch var="searchError">
	<c:import url="${ forwardRequest }" />
</c:catch>

<c:if test="${ not empty searchError }">
	<html>
		<head>	
			<title>DLESE Search Error</title>
			<c:import url="${domain}/dlese_shared/templates/header.html" /> 
		</head>
		<body>
			<c:import url="${domain}/dlese_shared/templates/top_content.html"/>
		
			<p>We're sorry, but our news search is currently unavailable...
			</p>
			
			<!-- ${ searchError } -->
			
			<c:import url="${domain}/dlese_shared/templates/bottom_content.html"/>
		</body>
	</html>
</c:if>
	

