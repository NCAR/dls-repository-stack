<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>

<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>

<html>
<head>
	<title>Search API: Search Client</title>
	
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<META NAME="keywords" CONTENT="Digital Learning Sciences, DLS, DLESE, DDS">
	<META NAME="description" CONTENT="Digital Discovery System Web service code examples">
	<META NAME="creator" CONTENT="John Weatherley">
	<META NAME="organization" CONTENT="Digital Learning Sciences (DLS)">
	
	<%-- DLESE template header (CSS styles and JavaScript references) --%>
	<%@ include file="/nav/head.jsp" %> 
	
	<LINK REL="stylesheet" TYPE="text/css" HREF="${contextUrl}/services/jshtml1-1/documentation_styles.css">
 	 
</head>

<body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066">
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Search<br/>Service API
</div>

<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>

<h1>Search Client</h1>
  
    <p style="margin-top: 8px">
        The <b><a href="json_client/dds_search.html">Search client</a></b>
        provides the essential search functionality implemented with HTML, JavaScript, and
        <a href="https://en.wikipedia.org/wiki/JSONP">JSONP</a> and is free to copy and
        modify to support a wide range of search applications. The client can be configured to search an entire DDS repository or just one or more collections,
        implement smart links that map search queries to bookmarkable links in the page, provide keyword highlighting,
        word stemming, result boosting, and more.
    </p>

    <p style="margin-top: 8px">
        <b>To install</b>: Save the <a href="json_client/dds_search.html">HTML file</a>, the <a href="json_client/spinner.gif">spinner.gif file</a>, and either the jQuery implementation files
        <a href="json_client/dds_search_jquery_implementation.js">dds_search_jquery_implementation.js</a>
        and <a href="json_client/jquery-1.12.1.min.js">jquery-1.12.1.min.js</a>
        <i>or</i> the Prototype.js implentation file <a href="json_client/dds_search_prototypejs_implementation.js">dds_search_prototypejs_implementation.js</a>
        into a single directory. Then simply edit the code to customize as desired and deploy to any Web server.
        See the comments in the files for configuration options and details.<p>
        <ul>
            <li>To save the HTML file, open the page and choose 'Save As' from the 'File' menu in your browser.</li>
            <li>To save the spinner.gif file, open the URL to the gif in your browser, then choose 'Save' or 'Save As'.</li>
            <li>To save the JavaScript files, open the URL to the .js file in your browser, then choose 'Save' or 'Save As'.</li>
        </ul>
    </p>


   
<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
   
</body>
</html>


