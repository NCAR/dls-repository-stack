<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title" value="NDR Finder" />
<html>
<head>
	<link rel="stylesheet" href="../styles.css" type="text/css" />
	<link rel="stylesheet" href="includes/json-styles.css" type="text/css" />
	<link rel="stylesheet" href="includes/ndr-browser-styles.css" type="text/css" />

<style type="text/css">

.search-input-row {
	padding:2px;
}

table.find-options {
	border-collapse: collapse;
	background-color:#e3e4f1;
	border:1px solid #333366;
}

table.find-options TR {
	vertical-align:middle;
}

table.find-options TD {
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
	<script type="text/javascript" src="includes/NDRFinder.js"></script>
	<script type="text/javascript">
	
/* var ndrApiBaseUrl; */
var debugging = true;
var properties = null;
	
function pageInit () {
	
	properties = new RuleList('properties', "property");
	relationships = new RuleList('relationships', "relationship");

	if (!debugging)
		$('debug').hide();
	
	debug ("Prototype version: " + Prototype.Version);
		
	$('more-options').setStyle({display:"none"});
	$('ndrResults').hide();
		
	// alert ("looking for " + "${ndrForm.ndrApiBaseUrl}")
	// initialize the repository-select to have the current ndrServer selected by default
	$('repository-select').childElements().each (function (option) {
		// alert (option.value + " (" + option.selected + ")");
		if (option.value.startsWith ("${ndrForm.ndrApiBaseUrl}"))
			option.selected = true;
		else
			option.selected = false;
	});
	
	// initialize types select
	$A(ndrTypes).each ( function (ndrType) {
		var option = document.createElement ("option");
		option.innerHTML = ndrType;
		$('ndr-type-select').appendChild (option);
	});
	
	setNdrApiBaseUrl();
		
	Event.observe ('more-options-toggler', 'click', function (evnt) {
		toggleMoreOptions();
	});

	Event.observe ('findButton', 'click', doFind);
	Event.observe ('repository-select', 'change', function (evnt) {
		setNdrApiBaseUrl();
	});	
}

function addProperty (evnt) {
	// debug ("addProperty()");
	properties.add();
}

function addRelationship (evnt) {
	// debug ("addRelationship()");
	relationships.add();
}

function removeRule (evnt) {
	// var list = evnt.element().up ("[class='rule-list']");
	var rule = Event.element(evnt).up (".rule");
	var list = Event.element(evnt).up (".rule-list");
	if (rule && list) {
		// debug ("rule-list: " + list.id);
		rulenum = $A(rule.id).findAll ( function (ch) { 
								return !isNaN(parseInt(ch));
							}).join ("");
		if (rulenum) {
			var funcall = list.id + ".remove("+rulenum+")";
			eval (funcall);
		}
		else
			debug ("couldn't resolve rulenum");
	}
	else {
		debug ("rule-list pr rule not found");
	}
	// properties.remove (propNum);
}

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

<st:pageMessages okPath="${contextPath}/ndr/ndr.do" />

<a name="top"/>
<div id="debug">
	<h3>Debug</h3>
</div>

<form id="finder-form" action="ndr.do">
	<input type="hidden" name="command" value="find" />
	
<div align="center" class="search-input">
	<div class="search-input-row">
		<h3>Find Query</h3>
	</div>
	<div class="search-input-row">NDR Object Type
		<select id="ndr-type-select">
			<option value=""> -- NDR Object Type --</option>
		</select>
	</div>
	
	<%-- PROPERTIES --%>
	<div align="left"><b>Properties</b>&nbsp;&nbsp;  
		<input type="button" value="+" onclick="addProperty()" />
	</div>
	<div id="properties" class="rule-list"></div>
	
	<%-- RELATIONSHIPS --%>
	<div align="left"><b>Relationships</b>&nbsp;&nbsp;  
		<input type="button" value="+" onclick="addRelationship()" />
	</div>
	<div id="relationships" class="rule-list"></div>	
	
	
	
	
	<div class="search-input-row">	
		<input type="button" id="findButton" value=" Find " />
	</div>
	<div class="search-input-row">			
		<div style="font-size:80%">
			<a href="#" id="more-options-toggler">More Options</a>
		</div>
	</div>
</div>

<div id="more-options" align="center">
	<table class="find-options">
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
					<select id="repository-select" name="repository-select">
						<option value="http://ndrtest.nsdl.org/api">NDR Test</option>
						<option value="http://server7.nsdl.org/api">NDR Near Production</option>
						<option value="http://ndr.nsdl.org/api">NDR Production</option>
					</select>
				<span id="repository-url" style="margin-left:20px;font-style:italic;font-size:10pt"></span>
			</td>
		</tr>
		<tr>
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
