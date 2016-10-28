<%@ page language="java" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="domain"><%= "http://" + request.getServerName() + ":" + request.getServerPort() %></c:set>
<c:url value="${ domain }/library/query.do" var="forwardRequest">
	<c:if test="${ not empty param.q }">		
		<c:param name="q" value="${ param.q }" />
	</c:if>
	<c:if test="${ not empty param.s }">		
		<c:param name="s" value="${ param.s }" />
	</c:if>
	<c:if test="${ not empty param.over }">		
		<c:param name="over" value="${ param.over }" />
	</c:if>
	<c:if test="${ not empty param.re }">		
		<c:param name="re" value="${ param.re }" />
	</c:if>
	<c:if test="${ not empty param.gr }">		
		<c:param name="gr" value="${ param.gr }" />
	</c:if>
	<c:if test="${ not empty param.cs }">		
		<c:param name="cs" value="${ param.cs }" />
	</c:if>
	<c:if test="${ not empty param.su }">		
		<c:param name="su" value="${ param.su }" />
	</c:if>
</c:url>
<c:catch var="searchError">
	<%-- Redirect DDS v3.2 and earlier URLs to new DSV equivalent --%>
	<c:redirect url="${ forwardRequest }" />
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
		
			<p>We're sorry, but our search is currently unavailable...
			</p>
			
			<!-- ${ searchError } -->
			
			<c:import url="${domain}/dlese_shared/templates/bottom_content.html"/>
		</body>
	</html>
</c:if>


