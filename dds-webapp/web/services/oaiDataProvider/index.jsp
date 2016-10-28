<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.RepositoryManager" %>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>

<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>

<c:set value="OAI Data Provider" var="title"/>	

<html>
<head>
	<title>${title}</title>
	
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<META NAME="description" CONTENT="OAI Data Provider">
	
	<%--  Template header (CSS styles and JavaScript references) --%>
	<%@ include file="/nav/head.jsp" %>	
</head>

<c:set var="myBaseUrl" value="${initParam.oaiBaseUrlDisplay}"/>
<c:if test="${!isDeploayedAtDL || empty myBaseUrl}">
	<c:set var="myBaseUrl"><%=((RepositoryManager)getServletContext().getAttribute("repositoryManager")).getProviderBaseUrl(request)%></c:set>
</c:if>

<c:set value="${isDeploayedAtDL ? 'DLESE' : 'DDS'}" var="displayContext"/>	


<body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066">
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   OAI<br/>
   Data Provider </div>

<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>

		   
<h1>${title}</h1>
  
	<p> 
		Items in the DDS repository are available to harvest using the <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm">Open Archives Initiative Protocol for Metadata Harvesting</a> (OAI-PMH), 
		which allows the repository to interoperate with other OAI-enabled repository systems.
		
		<c:if test="${!isDeploayedAtDL}">
			All items may be harvested as a whole or each collection may be harvested individually as
			an OAI <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Set">set</a>.
		</c:if>
	</p>  
  
  <p>
  The OAI data provider for this repository 
  is located at the following <em>Base URL</em>:</p>
    
  <ul>
  	<li>OAI-PMH Base URL = <code>${myBaseUrl}</code></li>
  </ul>
  
  <h3>Tools</h3>
  
  <ul>
    <li><a href="oai_explorer.jsp">Explorer</a> - Explore the ${displayContext} repository by issuing OAI requests using a Web interface.</li>
  </ul>   
   
  <c:if test="${isDeploayedAtDL}">
	   <ul>
		<li>
			<a href="../joai_software.jsp">jOAI Software</a> - Harvest metadata from ${displayContext} and other data providers using the open source jOAI software.
		</li>
	  </ul>
	
	  <h3>Contact us </h3>
		<p>If you have questions or comments regarding this service, please send e-mail to <a href="mailto:support@dlese.org">support@dlese.org</a>.
  </c:if>

<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
  
</body>
</html>


