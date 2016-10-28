<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ include file="/ddswsBaseUrl.jsp" %><%-- Sets the DDSWS 1-1 baseURL into variable 'ddsws11BaseUrl' --%>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>

<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>

<html>
<head>
    <title>Search API: Overview</title>

    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <META NAME="keywords" CONTENT="Digital Learning Sciences, DLS">
    <META NAME="description" CONTENT="Digital Discovery System Search API documentation">
    <META NAME="creator" CONTENT="John Weatherley">
    <META NAME="organization" CONTENT="Digital Learning Sciences (DLS)">

    <%-- DLESE template header (CSS styles and JavaScript references) --%>
    <%@ include file="/nav/head.jsp" %>

</head>

<body bgcolor="#FFFFFF" text="#000000"
      link="#220066" vlink="#006600" alink="#220066">
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
    Search<br/>Service API
</div>

<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>

<h1>Search API</h1>


<p>
    The Search API is a REST-RPC hybrid API for searching over items in a DDS repository.
</p>

<br/>

<p>
    <a href="service_specification.jsp">Documentation</a> - Read the Search API documentation for a description of each
    API request and overall features.
</p>

<p>
    <a href="${pageContext.request.contextPath}/services/examples/ddsws1-1/index.jsp">Search Client</a> -
    View and download client code that can be used to implement a variety of search applications.
</p>

<p>
    <a href="service_explorer.jsp">Explorer</a> - Issue and explore API requests and responses in your Web browser.
</p>

<br/>

<h3>
    Search API baseURL
</h3>
<p>baseURL for this repository: <span class="code">${ddsws11BaseUrl}</code></p>

<br/>

<h3>
    API Features
</h3>
<p>Some features of the API include:</p>
<br/>

<ul>
    <li><a href="service_specification.jsp#Search">Textual search</a> over XML records and related content</li>
    <li><a href="service_specification.jsp#facetedSearch">Faceted search</a></li>
    <li><a href="service_specification.jsp#availableSearchFields">Search fields</a> for every element and attribute in
        the
        XML records
    </li>
    <li><a href="service_specification.jsp#sorting">Results sorted</a> by relevance or any combination of lexical values
        found in the fields
    </li>
    <li><a href="service_specification.jsp#json">XML, JSON, or JSONP response output</a></li>
    <li><a href="service_specification.jsp#collectionSearch">Search by collection</a></li>
    <li><a href="service_specification.jsp#dateRange">Search by date ranges</a> and values</li>
    <li><a href="service_specification.jsp#geoSearch">Search by geospatial bounding box</a></li>
    <li><a href="service_specification.jsp#searchFieldsRelation">Search using relationships between records in the
        repository</a></li>
    <li><a href="queryparsersyntax.pdf">Lucene query syntax</a></li>
</ul>

<br/>
<h3>Previous versions</h3>
<p>Previous versions of the service are available for reference: <a
        href="${pageContext.request.contextPath}/services/ddsws1-0/index.jsp">Search API v1.0</a> and <a
        href="${pageContext.request.contextPath}/services/oai2-0/index.jsp">ODL Search Service</a>.</p>


<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>

</body>
</html>


