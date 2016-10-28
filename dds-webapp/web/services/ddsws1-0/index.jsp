<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>
<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>


<html><!-- InstanceBegin template="/Templates/services.dwt" codeOutsideHTMLIsLocked="true" -->
<head>

<!-- InstanceBeginEditable name="head" -->
	<META NAME="keywords" CONTENT="DLESE,digital library for earth system education">

	<META NAME="description" CONTENT="DLESE Discovery System Search Service Portal">

	<META NAME="creator" CONTENT="John Weatherley">

	<META NAME="organization" CONTENT="DLESE Program Center">

	<META NAME="doctype" CONTENT="DLESE webpage">

<!-- InstanceEndEditable -->
<script language="JavaScript" src="../../../Templates/dds_services_script.js"></script>

	<%-- DLESE template header (CSS styles and JavaScript references) --%>
	<%@ include file="/nav/head.jsp" %> 
	
	

</head>   
<body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066">
<!-- InstanceBeginEditable name="sectionTitle" -->
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Search<br/>
   Web Service
</div>
<!-- InstanceEndEditable -->
<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>
<!-- InstanceBeginEditable name="body" -->

<h1>Search Web Service</h1>

  <p><b>A new version of this service is available</b>. 
		The documentation here is for a previous version. 
		Please refer to <a href="../ddsws/index.jsp">Current Search Web Service</a> for new application development.	</p>

  	<p>The DLESE Search Web Service (DDSWS v1.0) is a REpresentational State Transfer (REST) style service API that provides programmatic access to the DLESE library metadata in XML form.</p>

  <p>The service is designed to be used in real-time, high-availability Web sites and applications  to provide interactive search and discovery interfaces for library resources. The service is also intended to support research that involves the metadata and content in the library. The service provides a rich search API and gives full access to the metadata in DLESE's collections. Metadata is available for educational resources, annotations, news &amp; opportunities and collections. The service supports textual searching over metadata and content, searching by date and field, sorting by date and field, discovery of the controlled vocabularies that are part of the library including grade ranges, subjects, resources types and content standards, checking for the existence of a URL, and other functionality. </p>

  <p>The service is illustrated and described formally here: </p>

  

  <ul>

    <li><a href="service_specification.jsp">Search Web Service documentation</a> - The documentation describes the service in detail, with service examples and information about each of the  requests and search APIs.</li>

  </ul>  



  <ul>

    <li><a href="service_explorer.jsp">Search Web Service Explorer </a>- The Search Web Service explorer allows you to issue each of the available requests to the service and view the XML response using your web browser.</li>

</ul>

  <p>To access the primary DLESE library repository using this service (DDSWS v1.0), use the following <em>Base URL</em>:</p>

  <ul>

    <li><em>Base URL</em> = ${contextUrl}/services/ddsws1-0</li>

  </ul>

  <h3>Contact us </h3>

	<p>If you have questions or comments regarding this service, please send e-mail to <a href="mailto:support@dlese.org">support@dlese.org</a>.



	

	

	  <%-- <a name="sp"></a>

	<h1>Search page output explorer</h1>

	<p> 

		You can view the the JavaScript search page service

		in it's original <a href="${contextUrl}/services/jshtml1-1/tpl/js_search_server.jsp">HTML output</a> 

		or as <a href="${contextUrl}/services/jshtml1-1/tpl/js_search_server.jsp?rt=jswl">JavaScript output</a>.

	</p> --%>

<!-- InstanceEndEditable -->

<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
  


</body>
<!-- InstanceEnd --></html>


