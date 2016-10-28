<%-- Source the necessary tag libraries --%>
<%@ page language="java" isErrorPage="true" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://dls.ucar.edu/tags/dlsELFunctions" %>

<html>
<head>
	<title>Internal server error (500)</title>
	<%@ include file="/head.jsp" %>
	
	<%-- Output Java exception details, if available --%>
	<% if (exception != null) { %>
		<!-- Server debugging information:  
		<% exception.printStackTrace(new java.io.PrintWriter(out)); %> -->
	<% } else { %>
		<!--  No stack trace available. -->
	<% } %>	
	
</head>

<body>
	<c:set var="pageViewContext" value="errorPages"/>
	<%@ include file="/top.jsp" %>

<h1>Internal server error</h1>

<p>We're sorry, the server encountered a problem and could not complete your request.</p>

<br/><br/><br/>
<%@ include file="/bottom.jsp" %>

</body>
</html>

