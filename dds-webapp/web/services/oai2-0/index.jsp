<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>


<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>

<html>
<head>
	<title>Services and APIs: DDS ODL Search Service</title>
	
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	
	<%-- template header (CSS styles and JavaScript references) --%>
	<%@ include file="/nav/head.jsp" %> 
	
	<LINK REL="stylesheet" TYPE="text/css" HREF="${contextUrl}/services/jshtml1-1/documentation_styles.css">

</head>

<body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066">
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   ODL Search <br/>Service
</div>


<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>


<h1>DDS ODL Search Service</h1>

  <p><b>A new version of this service is available</b>. 
		The documentation here is for a previous version. 
		Please refer to <a href="../ddsws/">Current Search Web Service</a> for new application development.	</p>  
  
  <p>The DDS <a href="http://oai.dlib.vt.edu/odl/" target="_blank">Open Digital Library</a> (ODL) Search Service is an API that extends the <a href="http://openarchives.org/" target="_blank">Open Archives Initiative Protocol for Metadata Harvesting</a> (OAI-PMH)  to allow for textual searching of the DDS repository content. </p>
  <p>The DDS ODL service provides a rich search API and gives full access to the metadata in DDS's collections. Metadata is available for educational resources, annotations, news &amp; opportunities and collections. The service supports textual searching over metadata and content, searching by date and field, sorting by date and field, discovery of the controlled vocabularies that are part of the library including grade ranges, subjects, resources types and content standards, checking for the existence of a URL, and other functionality. </p>
  <p>The service is illustrated and described formally here: </p>
  
  <ul>
    <li><a href="odl_service_documentation.jsp">DDS ODL Search Service documentation</a> - The documentation describes the service in detail, with service examples and information about each of the  requests and search APIs.</li>
  </ul>  
  
  <ul>
    <li><a href="service_explorer.jsp">ODL Search  Service Explorer </a>- The ODL Search Service explorer allows you to issue each of the available requests to the service and view the XML response using your web browser.</li>
</ul>
  <p>To access the primary DDS repository using this service (DDS ODL), use the following <em>Base URL</em>:</p>
  <ul>
    <li><em>Base URL</em> = ${contextUrl}/services/oai2-0</li>
  </ul>
  
  <c:if test="${isDeploayedAtDL}">
		  <p>Note that you can not use the above <em>Base URL</em> to harvest DLESE metadata using the standard OAI-PMH (a badArgument error will be returned). To harvest from DLESE, use the <A 
		href="http://www.dlese.org/libdev/interop/oai/data_prov.html">DLESE OAI data provider</A> instead.</p>
		  <h3>Contact us </h3>
			<p>If you have questions or comments regarding this service, please send e-mail to <a href="mailto:support@dlese.org">support@dlese.org</a>.
 </c:if>


<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
  
  
</body>
</html>


