<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<html><head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<!-- 	

	This page implements a search client for a DDS or NCS repository using HTML and JavaScript.



	Uses the JSON output from the DDSWS service. See DDSWS service documentation:

		http://www.dlese.org/dds/services/ddsws1-1/service_specification.jsp

	

	All XML formats can be searched and viewed with this client, with some additional 

	display features provided for the following formats: oai_dc, nsdl_dc, msp2, ncs_item, adn, dlese_anno, 

	dlese_collect, news_opps, concepts.



	The page also displays the collections, XML formats, search fields, search terms and service inforation

	from the repository.	



	Requires:

		dds_search_implementation.js - The JavaScript portion of the implementaion

		prototype-1.6.x.js - Prototype JavaScript framework. See API docs at: http://www.prototypejs.org/

	

	To install: 

		- Save this HTML file, dds_search_implementation.js, and prototype-1.6.x.js

		into a single directory.	

			-- To save this HTML file, choose 'Save As' from the 'File' menu in your browser.

			-- To save the JavaScript files, open a URL to each file in your browser, then choose 'Save' 

			or 'Save As'.

		- Edit the configuration options in the <script> section below to modify certain output and behaviors, as desired.

		- Edit the CSS styles below to modify the way the page looks (colors, fonts, sizes, etc.), as desired.

	

	Digital Learning Sciences (DLS)

	University Corporation for Atmospheric Research (UCAR)

	P.O. Box 3000

	Boulder, Colorado 80307 USA

	http://dlsciences.org/

		

	e-mail questions to: support@dlese.org

		

	This client may be copied and modified, free and with no warranty.

-->

	<title>DDS Search</title>
	
	<!-- The Prototype JavaScript framework (http://www.prototypejs.org/): -->
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/prototype/1.6.1.0/prototype.js"></script>
	
	<!-- This script implements the search and display features (requires prototype.js) : -->
	<script type="text/javascript" src="dds_search_implementation.js"></script>
	
	
	<!-- Configuration options: -->
	<script type="text/javascript">
		// --------- Begin configuration options ---------
		
		// Set the URL to the desired DDSWS service end point:
		var baseUrl = '<c:url value="/services/ddsws1-1"/>';
		
		// Expand the query to support word stemming and boost if terms appear in the title?
		var expandAndBoostSearch = true;
		
		// Highlight keywords in search results?
		var highlightKeywords = true;		
		
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
		/* DDSSearch.defineSmartLink('1', 'All resources', 'allrecords:true'); // A query that matches all records
		DDSSearch.defineSmartLink('2', 'Physics subject', '/text//nsdl_dc/subject:(physics)', ['physics']);
		DDSSearch.defineSmartLink('3', 'Math subject', '/text//nsdl_dc/subject:(mathematics OR math)', ['math','mathematics']);
		DDSSearch.defineSmartLink('4', 'Engineering subject', '/text//nsdl_dc/subject:(engineering)', ['engineering']);
		DDSSearch.defineSmartLink('5', 'High school audience', '/text//nsdl_dc/audience:("high school" OR "9-12")', ['high school']);
		DDSSearch.defineSmartLink('6', 'Middle school audience', '/text//nsdl_dc/audience:("middle school" OR "6-8")', ['middle school']); */
		
		
		// ----- More options to aid developers (optional) -----
		
		// Display links for the full record metadata to open as a tree?
		var displayFullRecordMetadata = true;		
		
		// Show developer information links on the page?
		//var showDeveloperInfo = false;
		
		// --------- End configuration options ---------		
	</script>
	
	<style type="text/css">
	
		/* Styles for the HTML output of this client */
		body, html, p, h1, h2, h3, h4, a, td, li, ul {
			font-family: Geneva, Arial, Helvetica, sans-serif;
		}
		a {
			outline: none;
		}
		form {
			margin: 0px;
			padding: 0px;
		}
		.smartLinkLabel {
			font-weight: bold;
			font-size: 110%;
			color: #666;
		}		
		.record {
			padding-bottom: 20px;
		}
		.recordTitle {
			color: #0000FF;
		}
		.recordTitle A {
			/* text-decoration: none; 
			font-weight: bold;
			color: black; */
		}
		.recordUrl { 
			font-size: 90%;
			color: #008000;
		}	
		.recordDesc {
			padding-top: 4px;
			font-size: 90%;
		}
		.recordSource {
			color: #888;
			font-size: 90%;
			margin-top:2px;
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
			font-size: 80%;
			font-weight: bold;
			text-align: right;
		}	
		.pager {
			white-space: nowrap;
			padding-top:10px;
			padding-bottom:15px;
			font-size:90%;
			font-weight: normal;
			text-align: left;			
		}
		.pager A {
			color: #0000FF;
		}		
		#loadMsg {
			padding: 20px;
			color: #777;
			font-size:12px;
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
			font-size: 12px;
			cursor: pointer;
			margin-top:4px;
		}
		.openerTxt {
			color:green;
			padding: 0px 2px 2px 2px;
			text-decoration: none;
		}
		.openerTxt:hover {
			background-color: #eee;
		}
		.openerLnk {
			font-family: monospace;
			font-size: 9px;
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
			color:#444;
		}
		.json-object, .json-value {
			padding-left: 20px;
			font-size: 95%;
		}
		.json-object .key {
			font-weight: bold;
			font-size: 100%;
		}
		.json-value .key {
			font-weight: bold;
		}
		#serviceResponse {
			margin-top:15px;
		}
	</style>
</head><body>
	<noscript>
		<p style="color:red">JavaScript is required to use this search page. Please enable JavaScript in your browser.</p>
	</noscript>

	<div style="position: absolute; top: 5px; right: 5px; font-size: 12px; margin-top: 5px;">
		<a href="../nsdl_dc_vocab_counts/">Back to Vocabs Report</a>
	</div>	
	
	<h3>DDS Search</h3>
		
	<form action="" id="searchForm">
		<textarea rows="5" cols="80" name="q" id="q"></textarea>
		<input value="Search" type="submit">
		<input value="Search" name="action" type="hidden">
	</form>
	
	<!-- Smart links that map a query to a link or selector in the UI. See the required JavaScript above -->
	<%-- <div id="smartLinks" style="font-size: 12px; margin-top: 5px;">
		Smart links:
		<a 
href="http://www.dls.ucar.edu/people/jweatherley/dds_nsdl/?action=Search&amp;sl=1">All
 resources</a>
		| <a 
href="http://www.dls.ucar.edu/people/jweatherley/dds_nsdl/?action=Search&amp;sl=2">Physics
 subject</a>
		| <a 
href="http://www.dls.ucar.edu/people/jweatherley/dds_nsdl/?action=Search&amp;sl=3">Math
 subject</a>
		| <a 
href="http://www.dls.ucar.edu/people/jweatherley/dds_nsdl/?action=Search&amp;sl=4">Engineering
 subject</a>
		| <a 
href="http://www.dls.ucar.edu/people/jweatherley/dds_nsdl/?action=Search&amp;sl=5">High
 school audience</a>
		| <a 
href="http://www.dls.ucar.edu/people/jweatherley/dds_nsdl/?action=Search&amp;sl=6">Middle
 school audience</a>
	</div>
	<div id="termsFields" style="font-size: 12px; margin-top: 10px;">
		Terms/fields:
		<a target="_blank" 
href="http://acorn.dls.ucar.edu:9187/dds/admin/reporting/report.do?verb=ListFields&amp;indexToUse=ddsIndex">List
 of fields</a>
		| <a target="_blank" 
href="http://acorn.dls.ucar.edu:9187/dds/admin/reporting/report.do?verb=TermCount&amp;fields=%2fkey%2f%2fnsdl_dc%2fsubject&amp;indexToUse=ddsIndex">Subject
 (as keys)</a>
		| <a target="_blank" 
href="http://acorn.dls.ucar.edu:9187/dds/admin/reporting/report.do?verb=TermCount&amp;fields=%2ftext%2f%2fnsdl_dc%2fsubject&amp;indexToUse=ddsIndex">Subject
 (individual terms)</a>
		| <a target="_blank" 
href="http://acorn.dls.ucar.edu:9187/dds/admin/reporting/report.do?verb=TermCount&amp;fields=%2fkey%2f%2fnsdl_dc%2faudience&amp;indexToUse=ddsIndex">Audience
 (as keys)</a>
		| <a target="_blank" 
href="http://acorn.dls.ucar.edu:9187/dds/admin/reporting/report.do?verb=TermCount&amp;fields=%2fkey%2f%2fnsdl_dc%2fhasPart&amp;indexToUse=ddsIndex">Has
 part (as keys)</a>
	</div> --%>
	

	<!-- A loading message to provide feedback to the user. -->
	<div id="loadMsg" style="display: none;">Loading ...</div>
	
	<!-- The search results are inserted in this div by the JavaScript -->
	<div id="searchResults"></div>
	
	
	
	<!-- ----- Optional: Show useful information for developers (set var showDeveloperInfo=true): ----- -->
	
	<!-- The developer service responses are inserted in this div by the JavaScript -->
	<div id="serviceResponse" class="jsonContainer" style="display: none;"></div>
	
	
	<div id="showDeveloperInfo" style="display: none; position: absolute; 
top: 10px; right: 10px; font-size: 12px;">
		<div>
			<a href="" id="devRequestUrl" target="_blank" style="display: none;">View
 web service request and response</a>
		</div>	
		<div style="margin-top: 2px;">
			<a 
href="http://www.dlese.org/dds/services/ddsws1-1/service_specification.jsp"
 target="_blank">View web service documentation</a>
		</div>
		<div id="quickLinks" style="font-size: 12px; margin-top: 5px;">
		 	View web service data:
			<div style="padding-left: 20px;">
				<a 
href="http://www.dls.ucar.edu/people/jweatherley/dds_nsdl/?action=ListCollections">List
 collections</a><br>
				<a 
href="http://www.dls.ucar.edu/people/jweatherley/dds_nsdl/?action=ListXmlFormats">List
 XML formats</a><br>
				<a 
href="http://www.dls.ucar.edu/people/jweatherley/dds_nsdl/?action=ListFields">List
 search fields</a><br>
				<a 
href="http://www.dls.ucar.edu/people/jweatherley/dds_nsdl/?action=ServiceInfo">Service
 info</a><br>
			</div>
		</div>		
	</div>

</body></html>