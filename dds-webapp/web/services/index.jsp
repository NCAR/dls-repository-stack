<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>

<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>


<html>
<head>
<title>Services and APIs: Overview</title>
	
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<META NAME="keywords" CONTENT="Digital Learning Sciences, DLS">
<META NAME="description" CONTENT="Digital Discovery System Web Services Portal">
<META NAME="creator" CONTENT="John Weatherley">
<META NAME="organization" CONTENT="Digital Learning Sciences (DLS)">
<META NAME="doctype" CONTENT="DLS webpage">

<script language="JavaScript" src="dds_services_script.js"></script>

	<%-- DLESE template header (CSS styles and JavaScript references) --%>
	<%@ include file="/nav/head.jsp" %> 
	
	

</head>
<body bgcolor="#FFFFFF" text="#000000" link="#220066" vlink="#006600" alink="#220066">

<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Services and<br/> APIs
</div>

<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>

  <h1><a name="services"></a>Services and APIs</h1>
  
<p>
	<c:if test="${isDeploayedAtDL}">
		The DLESE library is powered by the <a href="${pageContext.request.contextPath}/dds_overview.jsp">Digital Discovery System</a> (DDS).
	</c:if>
	The Web-based services and APIs that 
	are available for accessing the DDS repository and search capabilities are summarized below.
</p>

  <h2><a name="ddsws"></a>Search API</h2>
  <p>The <a href="ddsws1-1/index.jsp">Search API</a> is a RESTful API for searching and retreiving items that reside in a DDS repository, and is available from the <a href="${pageContext.request.contextPath}/services/dds_software.jsp">Digital Discovery System</a> (DDS) and the <a href="/dds/services/dcs_software.jsp">Digital Collection System</a> (DCS).
  Service requests are expressed as HTTP argument/value pairs and responses may be returned as XML or JSON.</p>
     
  <h2><a name="ddsupdatews"></a>Update API</h2>
  <p>
  	The <a href="ddsupdatews1-1/ddsupdatews_api_documentation.jsp">Update API</a> (DDSUpdateWS) is a REST-RPC API for making updates to a DDS repository.
	Authorized clients may use the service to create, update and delete collections and items within a DDS repository.
	<c:if test="${isDeploayedAtDL}">
		Note that this service is disabled for the DLESE library.
	</c:if>
  </p>

  <h2><a name="jshtml"></a>JavaScript Search Service</h2>
  <p>The <a href="jshtml1-1/index.jsp">JavaScript Search Service</a> (JSHTML) lets Web developers place repository search for <a href="http://www.dlese.org/Metadata/adn-item/">ADN resources</a> into Web pages with JavaScript, and is available from the <a href="/dds/services/dds_software.jsp">Digital Discovery System</a> (DDS). The service is designed to be used in real-time, high-availability Web sites to provide interactive search and discovery interfaces for repository resources. </p>  
  
  	<%-- DLESE's OAI data provider --%>
<%-- 	<c:if test="${isDeploayedAtDL}">
		<a name="oai"></a>
		<h2>Open Archive Initiative Data Provider</h2>
		<p>The <a href="oaiDataProvider/index.jsp">DLESE OAI Data Provider</a> exposes the DLESE metadata catalog via the OAI Protocol for Metadata Harvesting, version 2.0. </p>
		<p>The <a href="http://openarchives.org/" target="_blank">Open Archives Initiative Protocol for Metadata Harvesting</a> (OAI-PMH) is a widely used, standard library protocol for sharing and distributing metadata. The OAI protocol can be used to harvest the entire library catalog or harvest individual collections from the catalog. </p>
	</c:if> --%>
	
  @DCS_SERVICE_JSP@
 
  <p>&nbsp;</p>

<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
      

<div style="padding-top:800px">&nbsp;</div>
</body>
</html>


