<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title" value="SAT Explorer" />
<html>
<head>
	<link rel="stylesheet" href="../styles.css" type="text/css">
	<link rel="stylesheet" href="includes/json-styles.css" type="text/css">
	<link rel="stylesheet" href="includes/ndr-browser-styles.css" type="text/css">

<style type="text/css">
.small-button {
	font-size:8pt;
	}
#input-table tr {
	background-color:white;
	}
	
#responseHeader {
	margin-top:10px;
	background-color:#dcdcfc;
	border-bottom:black solid 1px;
}

</style>
	
	<script type="text/javascript" src="../lib/javascript/prototype.js"></script>
	<script type="text/javascript" src="includes/JsonViewer.js"></script>
	<script type="text/javascript">
	
var debugging = false;
	
var satApiUrl = "http://lehnert.syr.edu:8080/casaa-rest/service.do?"
var basePurlUrl = "http://purl.org/ASN/resources/"

function normalizePurl (id) {
	if (id.indexOf("/") == -1)
		return basePurlUrl + id
	return id
}

function doGetStandard () {
		
		var params = {
 			method: "getStandard",
			username: "test",
			password: "p",
			identifier: normalizePurl ($F('purlID'))
			};
		
		try {
			viewSATObject (params);
		} catch (error) {
			alert (error.toString());
		}
}


// create the url for the SAT service and then view results via viewSatObject
function doSuggestStandards () {
		
		var state = $F('state');
		var author = typeof (state) == "object" ? state.join(",") : state;
		
		var params = {
 			method: "suggestStandards",
			username: "test",
			password: "p",
			query: normalizePurl ($F('purlID')),
			author: author,
			topic: $F('topic'),
			startGrade: $F('startGrade'),
			endGrade: $F('endGrade'),
			maxResults: "5"
			};
		
		try {
			viewSATObject (params);
		} catch (error) {
			alert (error.toString());
		}
}

/* if ALL is selected, ensure none of other choices is selected */
function setState (evnt) {
		var state = $('state')
		if (state.options[0].selected) {
			$A(state.options).each ( function (option, index) {
				if (index > 0) option.selected = false;
				})
		}
}

function viewSATObject (satParams) {
	// alert ("viewSATObject");
	
	var format = $F('viewMethod') == "XML" ? "xml" : "json";
	var params = {format:format, command:"proxy"}
	var ajaxUrl = "ndr.do?" + $H(params).toQueryString();
	debug ("ajaxUrl: " + ajaxUrl, true)
/* 		if ($F('viewMethod') == "XML") {
		window.location = href;
	} */
	
	satParams = $H(satParams)
	
	var satUrl; // the webservice query
	
	// the following avoides the double-encoding of the purlId, but this isn't
	// necessary, since it is decoded properly
/* 	if (satParams.keys().indexOf ('query') != -1) {
		var purlID = satParams.remove ('query');
		satUrl = satApiUrl + satParams.toQueryString() + "&query=" + purlID;
	}
	else {
		satUrl = satApiUrl + satParams.toQueryString();
	} */
	
	satUrl = satApiUrl + satParams.toQueryString();
	
/*   window.location = satUrl;
	return; */
	
	debug ("satUrl: <a href=\"" + satUrl + "\">" + satUrl + "</a>")
	new Ajax.Request (ajaxUrl + "&uri=" + encodeURIComponent (satUrl), {
		method:'get',
		onSuccess: function (transport) {
			// debug (transport.responseText);
			var target = $('satResponse')
			if (format == "xml") {
				viewResponseAsXml (transport.responseText, target);
			}
			else {
			
				try {
					var json = transport.responseText.evalJSON();
					viewResponseAsJson (json, target);
				} catch (error) {
					alert (error)
				}
			}
			// $("responseHeader").scrollTo();
			focusResponse ();
			showSatQuery (satUrl);
		},
		onFailure: function(){ alert('Something went wrong...') }
		});
	return false;
}
		
function focusResponse () {
		if (!$('satResults').visible()) {
			doSatResponseToggle ();
		}
	$("responseHeader").scrollTo();
}

function doSatResponseToggle () {
		var satResults = $('satResults');
		$('satResultsToggle').innerHTML = satResults.visible() ? "show" : "hide";
		satResults.toggle();
	}

//-------------- viewers -----------------

function viewResponseAsXml (xml, target) {
	target.update()
	var pre = $(document.createElement("pre"));
	pre.setStyle ({fontSize:"10pt;"});
	pre.innerHTML = xml.escapeHTML().gsub (/\n[\s]*\n/, '\n');;
	target.appendChild (pre);

}

function viewResponseAsJson (JSONObject, target) {
	target = $(target)
	var viewMethod = $F('viewMethod')

	if (!JSONObject) {
		alert ("JSONObject not loaded?");
		return;
		}
	target.update();
		
	$J(target).viewJson (JSONObject);

	target.select (".ndr-link").each (function (obj) {
		// make sure the listener is only assigned once!
		obj.stopObserving ("click", ndrclick);
		obj.observe ("click", ndrclick);
	});
}			
		

function showSatQuery (href) {
	$('queryUrl').update ("satQuery: " + href);
}


function pageInit () {

	if (!debugging) {
		$('debug').hide();
		$('queryUrl').hide();
	}
		
	Event.observe ('suggestStandardsButton', 'click', doSuggestStandards, false);
	
	Event.observe ('getStandardButton', 'click', doGetStandard, false);
		
	Event.observe ('satResultsToggle', 'click', function (evnt) {
			doSatResponseToggle ();
			Event.stop(evnt);
		}, false);
		
	Event.observe ('state', 'change', setState, false);
}



Event.observe (window, "load", pageInit, false);
</script>
	<title><st:pageTitle title="${title}" /></title>
</head>
<body bgcolor=white>
<a name="top"/>
<h1>${title}</h1>
<div id="debug">
	<h3>Debug</h3>
</div>


<table id="input-table" cellpadding="5px" bgcolor="black">
	<tr valign="bottom">
		<td><div class="prompt">View Results As</div>
			<select id="viewMethod">
				<option >JSONObject</option>
				<option SELECTED>XML</option>
			</select>
		</td>
		<td colspan="2"></td>
	</tr>
	<tr valign="middle">
		<td align="bottom"><h4>Suggest Standards</td>
		<td>		
			<table cellpadding="8px">
				<tr>
					<td colspan="4">
						<div class="prompt">purlID</div>
						<input type="text" id="purlID" size="50" value="http://purl.org/ASN/resources/S1021014"/>
					</td>
				</tr>
				<tr valign="top">
					<td>
						<div class="prompt">state</div>
						<select id="state" multiple="true">
							<option value="" SELECTED>ALL</option>
							<option>Colorado</option>
							<option>Minnesota</option>
							<option>New York</option>
							<option>Massachusetts</option>
							<option>Ohio</option>
							<option value="National Science Education Standards (NSES)">NSES</option>
						</select>
					</td>
					<td>
						<div class="prompt">Start Grade</div>
						<select id="startGrade">
							<%-- <option value="" SELECTED>ALL</option> --%>
							<option value="" SELECTED>ANY</option>
							<option value="0">K</option>
							<option>1</option>
							<option>2</option>
							<option>3</option>
							<option>4</option>
							<option>5</option>
							<option>6</option>
							<option>7</option>
							<option>8</option>
							<option>9</option>
							<option>10</option>
							<option>11</option>
							<option>12</option>
						</select>
					</td>
					<td>
						<div class="prompt">End Grade</div>
						<select id="endGrade">
							<%-- <option value="" SELECTED>ALL</option> --%>
							<option value="" SELECTED>ANY</option>
							<option value="0">K</option>
							<option>1</option>
							<option>2</option>
							<option>3</option>
							<option>4</option>
							<option>5</option>
							<option>6</option>
							<option>7</option>
							<option>8</option>
							<option>9</option>
							<option>10</option>
							<option>11</option>
							<option>12</option>
						</select>
					</td>
					<td>
						<div class="prompt">Topic</div>
						<select id="topic">
							<%-- <option value="" SELECTED>ALL</option> --%>
							<option value="" SELECTED>ALL</option>
							<option>Math</option>
							<option>Science</option> 
						</select>
					</td>
				</tr>
			</table>
		</td>
		<td>
			<input type="button" id="getStandardButton" value=" Get Standard " />
			<br/><br/>
			<input type="button" id="suggestStandardsButton" value=" Suggest Standards " />
		</td>
	</tr>
</table>

<table id="responseHeader" width="100%">
	<tr>
				<td align="left"><h3>SAT Response  
					<span style="font-size:10pt;font-weight:normal">
						[ <a href="" id="satResultsToggle">hide</a> ]
					</span></h3></td>
		
		<td align="right"><a href="#top">to top</a></td>
	</tr>
</table>
<div id="satResults">
<div id="queryUrl" style="font-size:8pt;border:grey thin solid;padding:5px;margin-bottom:5px;"></div>
<div id="satResponse"></div>
</div>

</body>
</html>
