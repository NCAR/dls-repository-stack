<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>

<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>

<html>
<head>
	<title>JavaScript Search Service: Overview</title>
	
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<META NAME="keywords" CONTENT="DLESE,digital library for earth system education">
	<META NAME="description" CONTENT="DLESE Discovery System JavaScript Service Portal">
	<META NAME="creator" CONTENT="John Weatherley">
	<META NAME="organization" CONTENT="DLESE Program Center">
	<META NAME="doctype" CONTENT="DLESE webpage">
	
	<%-- DLESE template header (CSS styles and JavaScript references) --%>
	<%@ include file="/nav/head.jsp" %> 
	
	<LINK REL="stylesheet" TYPE="text/css" HREF="${contextUrl}/services/jshtml1-1/documentation_styles.css">
	 
</head>

<body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066">
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   JavaScript<br/>Search Service
</div>


<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>

		   
<h1>JavaScript Search Service</h1>


  <p>The  JavaScript Search Service (JSHTML) lets web developers place a repository search for <a href="http://www.dlese.org/Metadata/adn-item/">ADN resources</a> into Web pages with JavaScript, and is available from the <a href="../../services/dds_software.jsp">Digital Discovery System</a> (DDS).
  <%-- Note: This service is scheduled to be deprecated in favor of the more general Search API (see below). --%>
  
  </p>
	<p>The service generates interfaces for  DDS resources that reside in the <em>ADN metadata format only</em>.
	For a general purpose search JavaScript client, use the Search API and it's <a href="../ddsws1-1/service_specification.jsp#json">JSON output</a> instead. A client that illustrates it's use is shown in <a href="../examples/ddsws1-1/index.jsp">these examples</a>.
	</p>
	  
  
  <p> The service is designed to be used in real-time, high-availability Web sites   to provide interactive search and discovery interfaces for DDS repository resources in the ADN format. The service provides a simple JavaScript API that allows developers to define a custom  search and place it within the context of their own web site. The API can be used in any HTML page and may be combined with Cascading Style Sheets (CSS) to control the look and feel of the search interface and search results. Using the API, web page developers can insert, customize and tailor the  search within their web page using a text editor or other development tool, and deploy their page to any web server.</p>
  <p>The service is illustrated and described formally here:</p>
  
  <ul>
    <li><a href="javascript_service_documentation.jsp">Documentation</a> - The documentation describes the service API in detail, provides sample code, and contains information about each of the customizable options that are available to you as a developer.</li>
  </ul>  
  <ul>
    <li><a href="smart_link_query_builder.jsp">SmartLink Builder Tool</a> - 
	Use the SmartLink Builder to generate custom SmartLinks for your page.</li>
  </ul>    
  <ul>
    <li><a href="../examples/jshtml1-1/index.html">Templates and Examples</a> - The examples illustrate the primary ways the  JavaScript Search API may be used to create a custom search environment, and can serve as templates for creating your own custom search pages. You may <a href="http://sourceforge.net/project/showfiles.php?group_id=23991&package_id=156558">download the examples</a> from our  download area on SourceForge. After downloading, unpack the ZIP file to access the examples inside.</li>
  </ul>

  <ul>
    <li><a href="javascript_service_tutorial.pdf">Tutorial</a> - 
	The tutorial provides a self-guided tour of the service and an overview of the API (requires <a href="http://www.adobe.com/products/acrobat/readstep2.html" target="_blank">Acrobat Reader</a>).</li>
  </ul>  
  

  
  <p>To access this service (JSHTML v1.1), use the following <em>serviceURL</em>:</p>
  <ul>
    <li><em>serviceURL</em> = ${contextUrl}/services/jshtml1-1/javascript_search_service.js</li>
  </ul>
  <h3>How to Create a Custom  Search Page </h3>
	
<p>If you are interested in placing a custom  search environment into a web 
  site, start by looking at the <a href="../examples/jshtml1-1/index.html">Template and Examples</a>, 
  which can be used to begin your own custom 
  pages. You should also see the <a href="javascript_service_documentation.jsp">Documentation</a>, 
  which contains detailed information about how to use the 
  service API including descriptions of each of the options that are available to control the 
  features and behavior of your search page. </p> 

  <%-- <h3>Contact us </h3>
	<p>If you have questions or comments regarding this service, please send e-mail to <a href="mailto:support@dlese.org">support@dlese.org</a>.
 --%>
	
	
	  <%-- <a name="sp"></a>
	<h1>Search page output explorer</h1>
	<p> 
		You can view the the JavaScript search page service
		in it's original <a href="${contextUrl}/services/jshtml1-1/tpl/js_search_server.jsp">HTML output</a> 
		or as <a href="${contextUrl}/services/jshtml1-1/tpl/js_search_server.jsp?rt=jswl">JavaScript output</a>.
	</p> --%>
	


<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
   
</body>
</html>


