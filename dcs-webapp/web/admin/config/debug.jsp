<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%-- <%@ page import="edu.ucar.dls.repository.*" %> --%>
<%@ page import="edu.ucar.dls.ndr.request.SimpleNdrRequest" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Debug</c:set>
<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<%@ include file="/admin/adminHTMLIncludes.jsp" %> 

<% 
	pageContext.setAttribute ("ndrVerbose", SimpleNdrRequest.getVerbose());
	pageContext.setAttribute ("ndrDebug", SimpleNdrRequest.getDebug());
%>

</head>

<body text="#000000" bgcolor="#ffffff">

<st:pageHeader currentTool="Settings" toolLabel="${title}" />

<st:pageMessages okPath="frameworks.do" />

<html:form action="/admin/admin" method="GET">

<p/>
<div>Enter a record ID and hit debug to see dcs_data record</div>
	<html:text property="dcsId" />
	<html:submit property="command" value="debug" />
	<p>
	<input type="button" value="flush DcsDataRecords to disk" 
		onClick="location='admin.do?command=debug&flush=true'" />
	<p>	
	<div>verbose:${ndrVerbose}</div>
	<div>debug: ${ndrDebug}</div>
	<input type="button" value="verbose" 
		onClick="location='admin.do?command=debug&ndrVerbose=true'" />
	<input type="button" value="quiet" 
		onClick="location='admin.do?command=debug&ndrVerbose=false'" />
</html:form>		

<c:if test="${not empty daf.dcsXml}">
	<h3>${daf.dcsId}</h3>
	<pre><c:out value="${daf.dcsXml}" escapeXml="true"/></pre>
</c:if>

</body>
</html:html>

