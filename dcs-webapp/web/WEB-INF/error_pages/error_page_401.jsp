<%-- Source the necessary tag libraries --%>
<%@ page language="java" isErrorPage="true" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://dls.ucar.edu/tags/dlsELFunctions" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<%-- The following is necessary to make BASIC authorization/401 work properly in Tomcat: --%>
<% 
	response.addHeader("WWW-Authenticate", "BASIC realm=\"DCS\"");
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
<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>

<%@ include file="/baseHTMLIncludes.jsp" %>
<link rel='stylesheet' type='text/css' href='${contextPath}/styles.css'>

</head>
<body>

<st:pageHeader currentTool="" toolLabel="Unauthorized" />

<p>You must be authorized in order to view this page!</p>


</body></html>


