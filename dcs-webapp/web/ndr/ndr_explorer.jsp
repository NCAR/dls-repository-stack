<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title" value="NDR Explorer" />
<html>
<head>
	<link rel="stylesheet" href="../styles.css" type="text/css" />
	<link rel="stylesheet" href="includes/json-styles.css" type="text/css" />
	<link rel="stylesheet" href="includes/ndr-browser-styles.css" type="text/css" />

<style type="text/css">

.search-input-row {
	padding:2px;
}

table.search-options {
	border-collapse: collapse;
	background-color:#e3e4f1;
	border:1px solid #333366;
}

table.search-options TR {
	vertical-align:middle;
}

table.search-options TD {
	padding:5px;
}

.input-label {
	font-weight:bold;
}

/* table tr {
	background-color:white;
	} */
</style>
	<%@ include file="/manage/manageHTMLIncludes.jsp" %>
	<script type="text/javascript">
		<%@ include file="includes/NDRGlobals.jsp" %>
	</script>
	<script type="text/javascript" src="includes/JsonViewer.js"></script>
	<script type="text/javascript" src="includes/NDRObject.js"></script>
	<script type="text/javascript" src="includes/NDRBrowser.js"></script>
	<script type="text/javascript" src="includes/NDRExplorer.js"></script>
	<script type="text/javascript">
	
/* var ndrApiBaseUrl; */
var debugging = false;
	

function pageInit () {
	
	if (!debugging)
		$('debug').hide();
	
	$('more-options').setStyle({display:"none"});
	$('ndrResults').hide();
		
	repositorySelectInit();
		
	Event.observe ('more-options-toggler', 'click', function (evnt) {
		toggleMoreOptions();
	});
	
	Event.observe ('listMembersButton', 'click', doListMembers);
	Event.observe ('findResourceButton', 'click', function (evnt) {
		href = NDRGlobals.ndrApiBaseUrl + "/findResource?url="+$F('resourceURL');
		window.location = href;
	});
	
	Event.observe ('getButton', 'click', doGet);
	// hitting "return" in the explorerHandle box will execute a "get"
	if ($('explorerHandle'))
		$('explorerHandle').observe ('keyup', function (evnt) {
			if (evnt.keyCode == 13) 
				doGet(evnt);
			});

	if ($('describeButton'))
		Event.observe ('describeButton', 'click', doDescribe);
	
	if ($('searchServiceButton')) {
		Event.observe ('searchServiceButton', 'click', function (evnt) {
			href = NDRGlobals.ndrApiBaseUrl + "/getMetadata/" + $F('explorerHandle') + "?format=nsdl_search&xsl=nsdl:122";
			// window.location = href;
			NDRGlobals.popup (href);
		});
	}
	
	if ($('searchServiceValidateButton')) {
		Event.observe ('searchServiceValidateButton', 'click', function (evnt) {
			href = NDRGlobals.ndrApiBaseUrl + "/getMetadata/" + $F('explorerHandle') + "?format=nsdl_search&xsl=nsdl:122";
			validate (href, "Search Service Record: ");
		});
	}
	
	if ($('searchServiceAboutButton')) {
		Event.observe ('searchServiceAboutButton', 'click', function (evnt) {
			href = NDRGlobals.ndrApiBaseUrl + "/getMetadataAbout/" + $F('explorerHandle') + "?format=nsdl_search&xsl=nsdl:122";
			// window.location = href+"#xml";
			NDRGlobals.popup (href);
		});
	}
	
	if ($('searchServiceAboutValidateButton')) {
		Event.observe ('searchServiceAboutValidateButton', 'click', function (evnt) {
			href = NDRGlobals.ndrApiBaseUrl + "/getMetadataAbout/" + $F('explorerHandle') + "?format=nsdl_search&xsl=nsdl:122";
			validate (href, "Search Service About Record: ");
		});
	}
	
	
/* 	Event.observe ('ndrResultsToggle', 'click', function (evnt) {
		doNdrResponseToggle ();
		Event.stop(evnt);
	}); */
	
	params = $H(this.location.search.toQueryParams());
	params.each (function (pair) {
		// alert (pair.key + ": " + pair.value)
		if (pair.key == "handle") {
			$('explorerHandle').value = pair.value;
			doNDRCall ("get", pair.value);
			hideMoreOptions();
		}
	});
}

/* 
		Initialize the NDR repository select drop down The NDR repository select
		widget controls the value of NCRGlobals.ndrApiBaseUrl, which is always
		initialized to the "configured NDR Host" (${ndrForm.ndrApiBaseUrl}" - see
		NDRGlobals.js), but which can be changed to look at other NDR Repositories
		for the duration of a single page load - reverting back to configured NDR
		HOST upon new page load.
*/
function repositorySelectInit () {
	
	// NDR Server options
	var ndrServers = [
		['NDR Test', 'http://ndr.nsdlib.org/api'],
		['NDR Production', 'http://ndr.nsdl.org/api']
	];
	
	// initialize the repository-select to have the current ndrServer selected by default
	var repositorySelect = $('repository-select');
	repositorySelect.options[0] = new Option ('Configured NDR Host', NDRGlobals.ndrApiBaseUrl, 1);
	
	// add other options
	$A(ndrServers).each (function (server) {
		var url = server[1];
		var name = server[0];
		if (url != NDRGlobals.ndrApiBaseUrl) {
			repositorySelect.options[repositorySelect.options.length] =
				new Option (name, url);
		}
	});
	
	repositorySelect.observe ('change', function (evnt) {
		setNdrApiBaseUrl();
	});
	
	// initialize
	setNdrApiBaseUrl();
}

/* Hide or show the more options window under "NDR Object Handle" */
function toggleMoreOptions () {
	if ( $('more-options').visible() )
		hideMoreOptions();
	else
		showMoreOptions();
}

function hideMoreOptions () {
	$('more-options-toggler').update ("more options");
	$('more-options').hide();
}

function showMoreOptions () {
	$('more-options-toggler').update ("less options");
	$('more-options').show();
}

function setNdrApiBaseUrl () {
	NDRGlobals.ndrApiBaseUrl = $F('repository-select');
	$('repository-url').update (NDRGlobals.ndrApiBaseUrl);
}

Event.observe (window, "load", pageInit);
</script>
	<title><st:pageTitle title="${title}" /></title>
</head>
<body bgcolor=white>

<st:pageHeader currentTool="ndr" toolLabel="${title}" />

 
<%@ include file="ndr_context.jspf" %> 
<div style="font-size:85%;margin-top:2px" align="right">Default NDR Host may be overridden in "more options" below</div>

<st:pageMessages okPath="${contextPath}/ndr/ndr.do" />

<a name="top"/>
<div id="debug">
	<h3>Debug</h3>
</div>

<%-- <%@ include file="cannedExplorerLinks.jsp" %> --%>


<form action="ndr.do">
	<input type="hidden" name="command" value="explore" />
	
<div align="center" class="search-input">
	<div class="search-input-row">
		<h3>NDR Object Handle</h3>
	</div>
	<div class="search-input-row">
		<input type="text" id="explorerHandle" size="30" value=""/>
	</div>
	<div class="search-input-row">	
		<input type="button" id="getButton" value=" Get " />
		<input type="button" id="listMembersButton" value=" List Members " />
	</div>
	<div class="search-input-row">			
		<div style="font-size:80%">
			<a href="#"  id="more-options-toggler">More Options</a>
		</div>
	</div>
</div>

<div id="more-options" align="center">
	<table class="search-options">
		<tr>
<%-- 
		<!-- un-used views commented out 4/1/2009 -->
		<td colspan=2 align="center">
				<table width="90%" cellspacing="0" cellpadding="0">
					<tr valign="bottom" align="center">
						<td valign="bottom">
							<input type="button" id="describeButton" value=" Describe " />
						</td>
						<td>
							<div class="input-label" style="text-align:center">Search Service Record</div>
							<input type="button" id="searchServiceButton" value="View" />
							<input type="button" id="searchServiceValidateButton" value="Validate" />
						</td>
						<td>
							<div class="input-label" style="text-align:center">Search Service About Record</div>							
							<input type="button" id="searchServiceAboutButton" value="View" />
							<input type="button" id="searchServiceAboutValidateButton" value="Validate" />
						</td>
					</tr>
				</table>
			</td>
		</tr> 
--%>
	
		<tr>
			<td><div class="input-label">Display Format</div></td>
			<td>
				<select id="viewMethod">
					<option SELECTED>NDRObject</option>
					<option>JSONObject</option>
					<option>XML</option>
				</select>
			</td>
		</tr>
		<tr>
			<td>
					<div class="input-label">Select NDR Repository</div>
			</td>
			<td>
					<select id="repository-select" name="repository-select"></select>
				<span id="repository-url" style="margin-left:20px;font-style:italic;font-size:10pt"></span>
			</td>
		</tr>
		<tr>
		
		<tr valign="middle">
			<td align="bottom"><div class="input-label">Find a Resource</div></td>
			<td>
				<span>
					<div class="prompt">Resource URL</div>
					<input type="text" id="resourceURL" size="50" value=""/>
				</span>
				<input type="button" id="findResourceButton" value=" Find Resource " />
			</td>
		</tr>
	</table>
</div>
</form>


<%-- <table id="responseHeader" width="100%">
	<tr>
				<td align="left"><h3>NDR Response  
					<span style="font-size:10pt;font-weight:normal">
						[ <a href="" id="ndrResultsToggle">hide</a> ]
					</span></h3></td>
		
		<td align="right"><a href="#top">to top</a></td>
	</tr>
</table> --%>
<div id="ndrResults">
	<hr style="margin:10px 0px 10px 0px" />
	<div id="queryUrl" style="border:grey thin solid;padding:5px;margin-bottom:5px;"></div>
	<div id="ndrResponse"></div>
</div>

</body>
</html>
