<%-- Source the necessary tag libraries --%>
<%@ page language="java" isErrorPage="true" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="f" uri="http://dls.ucar.edu/tags/dlsELFunctions" %>

<%-- The following is necessary to make BASIC authorization/401 work properly in Tomcat: --%>
<% 
	response.addHeader("WWW-Authenticate", "BASIC realm=\"DLESE\"");
	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
%>

<head>
<title>Unauthorized (401)</title>

<%-- Output Java exception details, if available --%>
<% if (exception != null) { %>
	<!-- Server debugging information:  
	<% exception.printStackTrace(new java.io.PrintWriter(out)); %> -->
<% } else { %>
	<!--  No stack trace available. -->
<% } %>

<%-- Determine my current domain URL --%>
<c:set var="domain" value="${fn:replace(f:serverUrl(pageContext.request),'https','http')}"/>

	<%-- DLESE template header (CSS styles and JavaScript references) --%>
	<%@ include file="/nav/head.jsp" %> 

</head>
<body onload="dlese_pageOnLoad()">

<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
	Unauthorized
</div>

<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>



<h1>Unauthorized</h1>

<p>You must be authorized in order to view this page.

<%-- Include the nav and bottom html --%>
<%@ include file="/nav/bottom.jsp" %>

</body></html>


