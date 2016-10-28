/* NDRExplorer - support for NDR Explorer page */ 

/*
	Display a NDR object in a specified format - handler for the "Get" button in
	the NDR Explorer UI:
	url - getUrl (e.g., http://ndrtest.nsdl.org/api/get/2200/test.20080710155619680T)
	viewMethod - determined by "viewMethod" UI control, defaults to "json"
*/
function viewNDRObject (url, viewMethod) {
	
	// format is either xml or jason (could be NDRObject or NSONObject)
	var format = $F('viewMethod') == "XML" ? "xml" : "json";
	var params = {format:format, command:"proxy"}
	var ajaxUrl = "ndr.do?" + $H(params).toQueryString();
	
	
	// to short-circut AJAX...
/* 		if ($F('viewMethod') == "XML") {
		window.location = href;
	} */
	
	/* make async call to proxy with the "get" url for particular NDR object.
		the proxy will return either an XML or JSON representation of object,
		depending on specified view format.
		in both cases, the response is rendered, attached to the hard-coded
		"target" element ('ndrResponse').
	*/
	new Ajax.Request (ajaxUrl + "&uri=" + encodeURIComponent(url), {
		method:'get',
		onSuccess: function (transport) {
			var target = $('ndrResponse')
			if (format == "xml") {
				viewResponseAsXml (transport.responseText, target);
			}
			else {
			
				try {
					var json = transport.responseText.evalJSON();
					viewResponseAsJson (json, target, viewMethod);
				} catch (error) {
					alert ("NDR Object could not be found - showing as JSON")
					viewResponseAsJson (json, target, 'JSONObject');
				}
			}
			// $("responseHeader").scrollTo();
			focusResponse ();
			showNdrQuery (url);
		},
		onFailure: function(){ alert('Something went wrong...') }
		});
	return false;
}
		
function focusResponse () {
	var ndrResults = $('ndrResults');
	if (ndrResults) {
		if (!ndrResults.visible()) {
			doNdrResponseToggle ();
		}
		window.scrollTo (0, ndrResults.cumulativeOffset().top);
	}
}

function doNdrResponseToggle () {
		var ndrResults = $('ndrResults');
		// $('ndrResultsToggle').innerHTML = ndrResults.visible() ? "show" : "hide";
		ndrResults.toggle();
	}
	
//-------------- viewers -----------------

function viewResponseAsXml (xml, target) {
	target.update()
	var pre = $(document.createElement("pre"));
	pre.setStyle ({fontSize:"10pt;"});
	// escape and delete blank lines
	pre.innerHTML = xml.escapeHTML().gsub (/\n[\s]*\n/, '\n');
	target.appendChild (pre);
}

function viewResponseAsJson (JSONObject, target, viewMethod) {
	target = $(target)
	
	if (!viewMethod)
		viewMethod = $F('viewMethod')

	if (!JSONObject) {
		alert ("JSONObject not loaded?");
		return;
		}
		
	// clear the target element
	target.update();
		
	switch (viewMethod) {
		case "NDRObject":
			// new NDRObject (JSONObject).toHtml(target);
			getNDRObject (JSONObject).toHtml(target);
			break;
		case "JSONObject":
			$J(target).render (JSONObject);
			break;
		default:
			alert ("render() - unrecognized viewMethod: " + viewMethod);
	}
	target.select (".ndr-link").each (function (obj) {
		// make sure the listener is only assigned once!
		obj.stopObserving ("click", ndrclick);
		obj.observe ("click", ndrclick);
	});
}			
	
function viewMetadataRecords (JSONObject, target) {
	target = $(target)

	if (!JSONObject) {
		alert ("JSONObject not loaded?");
		return;
		}
		
	// clear the target element
	target.update("<div style='font-size:150%;font-weight:bold'>viewMetadataRecords</div>");
		
	$J(target).render (JSONObject);

	target.select (".ndr-link").each (function (obj) {
		// make sure the listener is only assigned once!
		obj.stopObserving ("click", ndrclick);
		obj.observe ("click", ndrclick);
	});
}


function showNdrQuery (href) {
	$('queryUrl').update ("ndrQuery: " + href);
}

/* 	NOT CURRENTLY USED - Submit XMLInput-based calls (e.g., 
	"find", "modify", ..) to NDR 
*/
function doNdrApiProxy (evnt) {
	Event.stop (evnt);
	
	// extract values from form
	var form = $('ndrApiProxyForm');
	var ndrType = $F('ndrType')  // Metadata, metadataProvider, ...
	var ndrCmd  = $F('ndrCmd');  // add, modify, ...
	
	// add ndrType to command if command is NOT find
	var verb    = $F('ndrCmd') == 'find' ? ndrCmd : ndrCmd + ndrType;
	
	// sanity - must have value
	var inputXML = $F('inputXML');
	if (!inputXML) {
		alert ("inputXML is required");
		return;
		}

	// set up params for Ajax.Request
	params = $H({
			command : "ndrProxy",
			verb : verb, 
			inputXML : $F('inputXML'),
			handle : ndrCmd == "modify" ? $F('proxyHandle') : ""
		});
	
	// specify how we will view the response
	var responseDisplayFormat = "xml";
		
	// new Ajax.Request ("ndr.do?command=proxy&uri=" + encodeURIComponent(url), {
	new Ajax.Request ("ndr.do?", {
		method:'post',
		parameters: params,
		onSuccess: function (transport) {
			var target = $('ndrResponse')
			if (responseDisplayFormat == "xml") {
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
			focusResponse();
		},
		onFailure: function(){ alert('Something went wrong...') }
		});
	return false;
}

/* Handles GET and LIST NDR calls (that don't requre ndrProxy) */
function doNDRCall (verb, handle, viewMethod) {
	// alert ("doNDRCall");
	if (handle == "") {
		alert ("handle is required");
		return;
	}

	var href = $A([NDRGlobals.ndrApiBaseUrl, verb, handle]).join ("/");

	try {
		viewNDRObject (href, viewMethod);
	} catch (error) {
		alert (error.toString());
	}

// window.location = $A([NDRGlobals.ndrApiBaseUrl, verb, handle]).join ("/");
}

/* get the value of the explorerHandle element, strip it, and then
   reassign it to form before returning stripped value
*/
function getExplorerHandle () {
	var handle = $F('explorerHandle').strip();
	$('explorerHandle').value = handle;
	return handle;
}

/* "List member" handler */
function doListMembers (evnt) {
	var verb = "listMembers";
	doNDRCall (verb, getExplorerHandle(), "JSONObject")
	Event.stop(evnt);
}

/* "Get" handler */
function doGet (evnt) {
	var verb = "get";
	doNDRCall (verb, getExplorerHandle())
	Event.stop(evnt);
}

/* "Describe" handler */
function doDescribe (evnt) {
	var verb = "describe";
	doNDRCall (verb, getExplorerHandle(), "JSONObject")
	Event.stop(evnt);
}

