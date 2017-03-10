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
        display histograms for browsing the facet categories,
        implement smart links that map search queries to bookmarkable links in the page, provide keyword highlighting,
        word stemming, result boosting, and more.
    </p>

    <div style="margin-top: 8px; padding: 4px; background-color: rgba(204, 206, 230, 0.42)">
        <p>
            <b>To install</b>, save these files into a single directory:
            <ul>
                <li><a href="json_client/dds_search.html">dds_search.html</a></li>
                <li><a href="json_client/dds_styles.css">dds_styles.css</a></li>
                <li>Image files <a href="json_client/spinner.gif">spinner.gif</a>,
                    <a href="json_client/transparent.gif">transparent.gif</a>,
                    <a href="json_client/asc.gif">asc.gif</a>,
                    <a href="json_client/desc.gif">desc.gif</a>,
                    <a href="json_client/bg.gif">bg.gif</a>,
                    <a href="json_client/hist_ruler.gif">hist_ruler.gif</a>
                </li>

                <li>Plus either the jQuery <i>or</i> Prototype.js implementation files:
                    <ul>
                        <li>jQuery implementation files:
                            <ul>
                                <li><a href="json_client/dds_search_jquery_implementation.js">dds_search_jquery_implementation.js</a>,
                                    <a href="json_client/facetConfigurationSettings.js">facetConfigurationSettings.js</a>,
                                    <a href="json_client/jquery-1.12.1.min.js">jquery-1.12.1.min.js</a>,
                                    <a href="json_client/jquery.tablesorter.js">jquery.tablesorter.js</a>,
                                    <a href="json_client/defiant.min.js">defiant.min.js</a></li>
                            </ul>
                        </li>
                        <li>Prototype.js implementation file:
                            <ul>
                                <li><a href="json_client/dds_search_prototypejs_implementation.js">dds_search_prototypejs_implementation.js</a></li>
                            </ul>
                        </li>
                    </ul>
                </li>
            </ul>
        </p>
    </div>


   
<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
   
</body>
</html>


