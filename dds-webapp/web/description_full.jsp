<%@ page language="java" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="domain"><%= "http://" + request.getServerName() + ":" + request.getServerPort() %></c:set>
<c:url value="${ domain }/library/view_resource.do" var="forwardRequest">
	<c:if test="${ not empty param.description }">		
		<c:param name="description" value="${ param.description }" />
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
			<c:import url="${domain}/dlese_shared/templates/header_refs.html" /> 
		</head>
		<body>
			<c:import url="${domain}/dlese_shared/templates/content_top.html"/>
		
			<p>We're sorry, but our search is currently unavailable...
			</p>
			
			<!-- ${ searchError } -->
			
			<c:import url="${domain}/dlese_shared/templates/content_bottom.html"/>
		</body>
	</html>
</c:if>


