/* NDRBrowser.js - support for async browsing of NDR structures */

/* remove any existing highlight classes and add a highlight class to
	given element.
*/
function highlight (e) {
	$$ (".highlight").each ( function (node) {
		node.removeClassName ("highlight");
		});
	e.addClassName ("highlight");
	// e.scrollTo();
}

/* Obtains the title of this ndrObj's serviceDescription and 
   updates the target element with it.
*/
function updateLabelUsingServiceDesc (ndrObj, target) {
	var sdHref = ndrObj.getServiceDescriptionUrl();
	
	new Ajax.Request ("ndr.do?command=proxy&uri=" + encodeURIComponent(sdHref), {
	method:'get',
	onSuccess: function (transport) {
		try {
			var json = transport.responseText.evalJSON(true);
			var title = json.serviceDescription['dc:title'];
			if (title)
				target.update (title);
		} catch (error) {
			// alert ("unable to process service description: " + error.toString());
		}
	},
	onFailure: function(){ alert('Something went wrong...') }
	});
}

/* get the first ndrObject within given element */
function findNdrObject (e) {
 	var ndrObjects = $(e).select ('.ndr-object')
	if (ndrObjects == null)
		throw ("ndrObject is null");
	if (ndrObjects.size() > 0) {
		return $(ndrObjects.first());
	}
	return null;
}
	
/* handle double-click (shift-click) on an ndr object by 
	making the selected object top-level in NDR Explorer 
	
	obj.href looks like: http://acorn.dls.ucar.edu:7228/repository/api/get/ndr:1
	ndrApiBaseUrl looks like: http://acorn.dls.ucar.edu:7228/repository/api
*/
function ndrdoubleclick (obj) {
	
	var pat = NDRGlobals.ndrApiBaseUrl + "/get/";
	var handle = obj.href.substr (pat.length);

	window.location = NDRGlobals.explorerLink (handle);
	return false;
}
	
/*	handler for clicking on an "ndr-link". Displays the NDR object
	associated with the handle if they aren't already displayed.
*/
function ndrclick (evnt) {
	Event.stop (evnt);
	var ndrLink = $(Event.element(evnt));
	var responseNode = $(ndrLink.parentNode)
	
	// if we've already retrieved an NDR object, simply toggle it's visibility
	if (evnt.shiftKey) {
		return ndrdoubleclick (ndrLink);
	}
	// if we've already retrieved an NDR object, simply toggle it's visibility
	var ndrObject = findNdrObject (responseNode)
	if (ndrObject){
		ndrObject.toggle();
		highlight(ndrObject);
		return false;
	}
	
/* 	// show spinner
	var spinnerDiv = $(document.createElement ("div"));
	spinnerDiv.update (NDRGlobals.spinner);
	responseNode.appendChild (spinnerDiv); 
*/
	new Ajax.Request ("ndr.do?command=proxy&uri=" + encodeURIComponent(ndrLink.href), {
		method:'get',
		onSuccess: function (transport) {
			try {
				var json = transport.responseText.evalJSON(true);
				// var ndrObj = new NDRObject (json);
				var ndrObj = getNDRObject (json);
				ndrObj.toHtml(responseNode);
				
				var decoration = ndrObj.getHandleDecoration();
				if (decoration) 
					responseNode.insertBefore (decoration, ndrLink.next("div.ndr-object"));
				
				// EXPERIMENTAL --------------
				if (ndrObj instanceof Aggregator &&
					ndrObj.getServiceDescriptionUrl()) {
					updateLabelUsingServiceDesc (ndrObj, decoration);
				}
				// --------------------------------------
				
				/*
					Register event listeners for all newly created "ndr-link" elements
				*/
				responseNode.select (".ndr-link").each (function (obj) {
					// make sure the listener is only assigned once!
					obj.stopObserving ("click", ndrclick);
					obj.observe ("click", ndrclick);
				});
				
				highlight(findNdrObject (responseNode));
			} catch (error) {
				alert ("error handling ndrclick: " + error.toString());
			}
		},
		onFailure: function(){ alert('Something went wrong...') }
		});
	return false;
}

/* looks for json elements as children. used to detect if listMembersLink
 has already been called so we don't keep appending the same content.
 */
function getJsonObject (e) {
	var jsonObjects = e.select ('.json-object')
	if (jsonObjects.size() > 0) {
		return $(jsonObjects.first());
	}
		return null;
}

/*	handler for clicking on an "list members -link". Fires off a listMembers
	query and displays results as ndr-links.
*/
function listMembersLink (evnt) {
	// alert ("ndrclick");
	Event.stop (evnt);
	var obj = $(Event.element(evnt));
	var responseNode = $(obj.parentNode)
	
	// if we've already retrieved an json object, simply toggle it's visibility
	var ndrObject = findNdrObject (responseNode)
 	if (ndrObject){
		ndrObject.toggle();
		return false;
	}
	
	new Ajax.Request ("ndr.do?command=proxy&uri=" + encodeURIComponent(obj.href), {
		method:'get',
		onSuccess: function (transport) {
			try {
				var json = transport.responseText.evalJSON(true);
				
				new NDRListMembersObject (json).toHtml(responseNode);
				
				responseNode.select (".ndr-link").each (function (obj) {
					// make sure the listener is only assigned once!
					obj.stopObserving ("click", ndrclick);
					obj.observe ("click", ndrclick);
				});
				
				// highlight(findNdrObject (responseNode));
			} catch (error) {
				//alert ("error: " + error.toString());
				alert ("couldn't display NDRListMembersObject as html");
			}
	},
	onFailure: function(){ alert('Something went wrong...') }
	});
	return false;
}

/* NO LONGER USED - display the inactive members of a mdp. necessary now that we are using the inactive flag,
which means that inactive members are not found by regular listMember call, and must be
retrieved separately.
*/
function listInactiveMembersLink (evnt) {
	// alert ("listInactiveMembersLink");
	Event.stop (evnt);
	var obj = $(Event.element(evnt));
	var responseNode = $(obj.parentNode)
	
	// if we've already retrieved an json object, simply toggle it's visibility
	var ndrObject = findNdrObject (responseNode)
 	if (ndrObject){
		ndrObject.toggle();
		return false;
	}
	
	var collection = obj.id.substr ("inactiveMembers-".length);
	
	new Ajax.Request ("ndr.do", {
		parameters: {
			command:"inactiveMembers", 
			collection:collection},
		onSuccess: function (transport) {
			try {
				var json = transport.responseText.evalJSON(true);
				
				new NDRListMembersObject (json).toHtml(responseNode);
				
				responseNode.select (".ndr-link").each (function (obj) {
					// make sure the listener is only assigned once!
					obj.stopObserving ("click", ndrclick);
					obj.observe ("click", ndrclick);
				});
				
				// highlight(findNdrObject (responseNode));
			} catch (error) {
				//alert ("error: " + error.toString());
				alert ("couldn't display NDRListMembersObject as html");
			}
		}
	}); 
}

function validate (href, label) {
	new Ajax.Request ("ndr.do?command=validate&uri=" + encodeURIComponent(href), {
		method: 'get',
		onSuccess: function (transport) {
			var isValid = (transport.responseText.strip() == 'true');
			if (isValid)
				alert (label + " is VALID");
			else
				alert (label + "is NOT valid");
		}
	}); 
}

