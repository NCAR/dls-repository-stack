<%-- Source the necessary tag libraries --%>
<%@ page language="java" isErrorPage="true" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://dls.ucar.edu/tags/dlsELFunctions" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<head>
<title>Internal server error (500)</title>

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

<st:pageHeader currentTool="" toolLabel="Internal server error" />

<p>We're sorry, the server encountered a problem and could not complete your request.</p>

</body></html>


