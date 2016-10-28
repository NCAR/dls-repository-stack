
var ndrTypes = [ "metadata", "metadataProvider", "resource", "aggregator", "agent", "metadataProvider" ];

var Finder = Class.create()
Finder.prototype = {
	initialize: function (ndrType) {
		this.ndrType = ndrType;
		this.properties = $A();
		this.relationships = $A();
	},
	
	getInputXML: function () {
		var inputXML = document.createElement ("InputXML");
		alert (inputXML.nodeName) //  = "InputXML";
		inputXML.setAttribute ("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		
		var typeElement = document.createElement (this.ndrType);
		inputXML.appendChild (typeElement);
		return inputXML;
	}
}

var RuleList = Class.create();
RuleList.prototype = {
	initialize: function (element, ruleType) {
		this.ruleType = ruleType;
		this.rules = $A();
		this.counter = 0;
		this.element = $(element);
		debug ("RuleList initialized: " + this.element.id);
	},
	
	add: function () {
		var rule = new FindRule (this.counter++, this.ruleType);
		this.rules.push(rule);
		this.render();
	},
	
	remove: function (propNum) {
		try {
			var index = this.rules.indexOf (this.rules.find ( function (prop) {
				if (propNum == prop.num) return true;
			}));
			if (index > -1)
				this.rules.splice (index, 1);
		} catch (error) { debug (error) }
		this.render();
	},
		
	render: function () {
		var myElement = this.element;
		myElement.update();
		this.rules.each (function (prop) {
			myElement.appendChild (prop.asElement());
		});
	}
}
		
/* FindRule - a component of a find request ("Property" or "Relationship") */
var FindRule = Class.create()
FindRule.prototype = {
	initialize: function (num, ruleType) {
		this.ruleType = ruleType; // prop or rel
		this.num = num;
		this.name = null;
		this.value = null;
		this.element = null;
	},
	asElement: function () {
		if (this.element == null) {
			var e = $(document.createElement ("div"));
			e.setAttribute ("id", this.ruleType+this.num);
			e.setAttribute ("class", "rule");
			e.update (this.ruleType+this.num);
			
			nameInput = $(document.createElement ("input"));
			nameInput.setAttribute ("type", "text");
			nameInput.setAttribute ("value", this.name);
			nameInput.setAttribute ("name", this.ruleType+"Name"+this.num);
			nameInput.setAttribute ("id", this.ruleType+"Name"+this.num);
			e.appendChild (nameInput);
			
			valueInput = $(document.createElement ("input"));
			valueInput.setAttribute ("type", "text");
			valueInput.setAttribute ("value", this.value);
			valueInput.setAttribute ("name", this.ruleType+"Value"+this.num);
			valueInput.setAttribute ("id", this.ruleType+"Value"+this.num);
			e.appendChild (valueInput);
			
			deleteButton = $(document.createElement ("input"));
			deleteButton.setAttribute ("type", "button");
			deleteButton.setAttribute ("value", "-");
			// deleteButton.setAttribute ("onclick", "removeProperty(\'" + this.num + "\')");
			e.appendChild (deleteButton);
			
			deleteButton.observe ("click", removeRule);
			
			this.element = e;
		}
			
		return this.element;
	}
}

function escapeXML (xml) {
	var s = $(xml).inspect();
	return s.escapeHTML().gsub (/\n[\s]*\n/, '\n');
	
/* 	$(xml).descendants().each ( function (e) {
		e.innerHTML = e.innerHTML.escapeHTML();
	});
	return xml; */
}

function showXML (xml) {
	var target = $('ndrResponse');
	target.update()
	var pre = $(document.createElement("pre"));
	pre.setStyle ({fontSize:"10pt;"});
	// escape and delete blank lines
	// pre.innerHTML = xml.escapeHTML().gsub (/\n[\s]*\n/, '\n');
	pre.appendChild (xml);
	debug (pre.innerHTML.escapeHTML().gsub (/\n[\s]*\n/, '\n'));
	pre.update (pre.innerHTML.escapeHTML().gsub (/\n[\s]*\n/, '\n'));

	target.appendChild (pre);
}

function doFind (evnt) {
	debug ("doFind()", true);
	var verb = "listMembers";
	var form = $('finder-form');
	var ndrType = $F('ndr-type-select');
	if (ndrType == "") {
		alert ("please choose an NDR Object Type");
		return;
	}
	params = {
		ndrType: ndrType
	}

	$('finder-form').getElements().each ( function (el) {
		// debug (el.id + ": " + el.getValue());
		var id = el.id
		if (id.startsWith ("relationship") || id.startsWith ("property"))
			params[id] = $F(id);
	});
	
	$H(params).each (function (pair) {
		debug (pair.key + ": " + pair.value);
	});
	
}

function doFind1 (evnt) {
	debug ("doFind", true);
	var verb = "listMembers";
	var form = $('finder-form');
	var ndrType = $F('ndr-type-select');
	if (ndrType == "") {
		alert ("please choose an NDR Object Type");
		return;
	}
	params = {
		ndrType: ndrType
	}
	properties.rules.each ( function (prop) {
		var propName = prop.ruleType+prop.num;
		debug (propName);
		var nameField = prop.ruleType+"Name"+prop.num
		params[nameField] = $F(nameField);
		
		var valueField = prop.ruleType+"Value"+prop.num
		params[valueField] = $F(valueField);
	});
	
	relationships.rules.each ( function (prop) {
		var propName = prop.ruleType+prop.num;
		debug (propName);
		var nameField = prop.ruleType+"Name"+prop.num
		params[nameField] = $F(nameField);
		
		var valueField = prop.ruleType+"Value"+prop.num
		params[valueField] = $F(valueField);
	});
	
	$H(params).each (function (pair) {
		debug (pair.key + ": " + pair.value);
	});
}

function doFindOLD (evnt) {
	debug ("doFind");
	var verb = "listMembers";
	var form = $('finder-form');
	var ndrType = $F('ndr-type-select');
	if (ndrType == "") {
		alert ("please choose an NDR Object Type");
		return;
	}
	var finder = new Finder(ndrType);
	
	//  defined in NDRExplorer.js
	//  (finder.getInputXML().toString(), $('ndrResponse'));
	
	showXML (finder.getInputXML());
	focusResponse();
	
	Event.stop(evnt);
}

function doNdrApiProxy (evnt) {
	// alert ("viewNDRObject");
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
				 (transport.responseText, target);
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

// handles get and list calls (that don't requre ndrProxy)
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


