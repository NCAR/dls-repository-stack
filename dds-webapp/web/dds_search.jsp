<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ include file="/ddswsBaseUrl.jsp" %><%-- Sets the DDSWS 1-1 baseURL into variable 'ddsws11BaseUrl' --%>
<%@ page contentType="text/html; charset=utf-8" %>
<!--
This page implements a search client for a DDS or NCS repository using HTML and JavaScript.

It is based on the example client available here:
${pageContext.request.contextPath}/services/examples/ddsws1-1/

Uses the JSON output from the DDSWS service. See DDSWS service documentation:
http://www.dlese.org/dds/services/ddsws1-1/service_specification.jsp

This client may be copied and modified, free and with no warranty.
-->

<html>
<head>
    <title>Search the Repository</title>

    <%@ include file="/nav/head.jsp" %>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <style type="text/css">

        /* Styles for the HTML output of this client */
        body, html, p, h1, h2, h3, h4, a, td, li, ul {
            /* font-family: Geneva, Arial, Helvetica, sans-serif; */
        }

        a {
            outline: none;
        }

        form {
            margin: 0px;
            padding: 0px;
        }

        #searchResults, #searchResults p {
            font-size: 120%;
        }

        #searchResults {
            margin-top: 10px;
        }

        .smartLinkLabel {
            font-weight: bold;
            font-size: 110%;
            color: #666;
        }

        .record {
            padding-bottom: 20px;
        }

        .recordTitle, .recordTitle em {
            color: #006B3B;
            font-size: 16px;
        }

        .recordTitle A, .recordTitle A em {
            font-size: 16px;
            color: inherit;
            /* text-decoration: none;
            font-weight: bold;
            color: black; */
        }

        .recordUrl {
            color: #008000;
        }

        .recordDesc {
            padding-top: 4px;
        }

        .recordDesc em {
            color: #333;
        }

        .recordSource {
            color: #888;
            margin-top: 4px;
            margin-bottom:5px;
        }

        .queryText {
            color: #888;
            font-weight: bold;
        }

        .keyword {
            font-weight: bold;
        }

        .pager, .resultsDisp {
            border-top: 1px solid #888;
            margin: 8px 4px 4px 0px;
            padding-top: 3px;
            font-weight: bold;
            text-align: right;
        }

        .pager {
            white-space: nowrap;
            padding-top: 5px;
            padding-bottom: 15px;
            font-weight: normal;
            text-align: left;
        }

        .pager A {
            padding: 2px 3px 2px 3px;
            border: 1px solid #FFFDFA;
        }

        .pager A:hover {
            background-color: #eee;
            border: 1px solid #ddd;
        }

        #loadMsg {
            padding: 20px;
            color: #444;
            font-size: 12px;
        }

        /* Used for keyword hightlight */
        EM {
            font-weight: bold;
            font-style: normal;
        }

        /* -- Styles for developer-related display elements (optional) -- */
        .jsonContainer {
            margin: 5px 40px 10px 18px;
            padding: 5px 5px 5px 0px;
            border: 1px solid #ccc;
            background-color: #eee;
        }

        .jsonOpener {
            cursor: pointer;
            margin-top: 4px;
        }

        .openerTxt {
            color: green;
            padding: 0px 2px 2px 2px;
            text-decoration: none;
        }

        .openerTxt:hover {
            background-color: #eee;
        }

        .openerLnk {
            font-family: monospace;
            text-decoration: none;
            color: #777;
            padding: 0px 3px 0px 3px;
            border: 1px solid white;
            background-color: #eee;
            border: 1px solid #ccc;
            position: relative;
            top: -2px;
        }

        .openerLnk:hover {
            color: #444;
        }

        .json-object, .json-value {
            padding-left: 20px;
        }

        .json-object .key {
            font-weight: bold;
        }

        .json-value .key {
            font-weight: bold;
        }

        #serviceResponse {
            margin-top: 15px;
        }
    </style>

    <!-- This script implements the search and display features (requires prototype.js) : -->
    <script type='text/javascript'
            src='${pageContext.request.contextPath}/services/examples/ddsws1-1/json_client/dds_search_prototypejs_implementation.js'></script>

    <!-- Configuration options: -->
    <script type='text/javascript'>
        // --------- Begin configuration options ---------

        // Set the URL to the desired DDSWS service end point:
        var baseUrl = '${ddsws11BaseUrl}';

        // Expand the query to support word stemming and boost if terms appear in the title?
        var expandAndBoostSearch = true;

        // Highlight keywords in search results?
        var highlightKeywords = true;

        // Display record ID in search results?
        var displayId = true;


        // Search over these collections only (for values, see 'searchKey' shown in the ListCollections response) (remove to search all):
        //var collections = ['1206789974136','1235694767688'];

        // Search over these XML formats only (for values, see 'xmlFormat' shown in the ListXmlFormats response) (remove to search all):
        //var xmlFormats = ['ncs_item','msp2'];

        /* 	Define Smart Links, which map a search query to a link or selector in the UI.

         To define a smart link:

         1. Call the function DDSSearch.defineSmartLink passing in the following arguments:
         DDSSearch.defineSmartLink(
         httpParam, //The http parameter sent to this page
         label, //The label to display
         searchQuery, // The search query to execute
         boostTerms // An optional array of one or more terms used to boost the search results that contain them
         );

         2. Include HTML in your page to send the given value for the 'sl' http parameter to this page, and http param action=Search.
         For example, to execute the first smart link here, construct a url like this: .../dds_search.html?action=Search&sl=1
         See <div id="smartLinks"> HTML below for HTML that does this.

         */
        DDSSearch.defineSmartLink('1', 'All resources', '*:*'); // A query that matches all records
        DDSSearch.defineSmartLink('2', 'Ocean resources', 'ocean', ['ocean', 'sea']); // A query for 'ocean' in the default field, boosting results that contain 'ocean' or 'sea'
        DDSSearch.defineSmartLink('3', 'Water resources', 'title:water', ['water']); // A query to for 'water' in the title field, boosting results that contain 'water'


        // ----- More options to aid developers (optional) -----

        // Display links for the full record metadata to open as a tree?
        var displayFullRecordMetadata = true;

        // Show developer information links on the page?
        var showDeveloperInfo = false;

        // --------- End configuration options ---------
    </script>
</head>
<body>
<%@ include file="/nav/top.jsp" %>
<noscript>
    <p style="color:red">JavaScript is required to use this search page. Please enable JavaScript in your browser.</p>
</noscript>

<h1>Search the Repository</h1>

<form action="" id="searchForm" style="margin-top:14px">
    <input size="45" type="text" name="q"/>
    <input type="submit" value="Search"/>
</form>

<!-- Smart links that map a query to a link or selector in the UI. See the required JavaScript above -->
<div id="smartLinks" style="margin-top:5px;">
    <a href="?action=Search&sl=1">View all resources</a>
</div>

<!-- A loading message to provide feedback to the user. -->
<div id="loadMsg" style="display:none">Loading ...</div>

<!-- The search results are inserted in this div by the JavaScript -->
<div id="searchResults"></div>


<!-- ----- Optional: Show useful information for developers (set var showDeveloperInfo=true): ----- -->

<!-- The developer service responses are inserted in this div by the JavaScript -->
<div id="serviceResponse" class="jsonContainer" style="display:none"></div>

<div id="showDeveloperInfo" style="display:none; position:absolute; top: 10px; right: 10px;">
    <div>
        <a href="" id="devRequestUrl" target="_blank" style="display:none">View web service request and response</a>
    </div>
    <div style="margin-top:2px">
        <a href="${pageContext.request.contextPath}/services/ddsws1-1/service_specification.jsp" target="_blank">View
            web service documentation</a>
    </div>
    <div id="quickLinks" style="margin-top:5px;">
        View web service data:
        <div style="padding-left:20px">
            <a href="?action=ListCollections">List collections</a><br/>
            <a href="?action=ListXmlFormats">List XML formats</a><br/>
            <a href="?action=ListFields">List search fields</a><br/>
            <a href="?action=ServiceInfo">Service info</a><br/>
        </div>
    </div>
</div>

<div style="margin-top:140px"></div>
<%@ include file="/nav/bottom.jsp" %>
</body>
</html>


