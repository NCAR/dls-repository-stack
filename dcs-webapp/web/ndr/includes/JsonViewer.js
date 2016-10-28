/* ----------- JsonViewerElement --------------------
	infrastructure for displaying json structures.
	extends prototypes "Element" class.
	
	NOTE: the global ndrApiBaseUrl must be defined 
	in a caller for ndr_links to work
*/

// Element is not a Class, so we can't subclass it!
var JsonViewerElement = Class.create();
Object.extend (JsonViewerElement, Element);

JsonViewerElement.addMethods ( {
	addElement: function (element, tag) {
		element = $J(element);
		return $J(element.appendChild (new Element (tag)));
	},
	
	addDiv: function (element) {
		return $J(element).addElement("div");
	},
	// key is the json object's name
	setKey: function (element, key) {
		element = $J(element)
		kSpan = element.addElement ("span");
		kSpan.update (key);
		kSpan.addClassName ("key");
		return element;
	},

	/*  value is assocated with key
		NOTE: This method is NDRObject specific!! It should not be defined this
		way HERE!
	*/
	setValue: function (element, value) {
		var val = element.addElement ("span");
		
		// if value is already a link, simply insert it
		if (Object.isElement (value)) {
			val.insert (value);
		}
		
		// kludge supporting display of NDR handles (e.g., in "listHandles")
		else if (NDRGlobals.isHandle (value)) {
			var ndrGetBaseUrl = NDRGlobals.ndrApiBaseUrl + "/get/"
			var href = ndrGetBaseUrl + value;
			var link = new Element ("a", {href:href}).update(value);
			link.addClassName ("ndr-link");
			val.insert (link);
		}
		else {
			val.insert (value);
		}
		return val;
	},
	
	addJobj: function (element, key) {
		var obj = $J(element).addDiv();
		obj.addClassName ("json-object");
		obj.setKey (key);
		return obj;
	},
	
	addJheader: function (element, key) {
		var obj = $J(element).addDiv();
		obj.addClassName ("json-header");
		obj.setKey (key);
		return obj;
	},
	
	// renders a key = value json element
	addJvalue: function (element, key, value) {
		// handle multi-values
		
		if (typeof (value) == "string" || Object.isElement(value)) {
			var obj = $J(element).addDiv();
			obj.addClassName ("json-value");
			obj.setKey (key);
			obj.insert ("&nbsp;=&nbsp;");
			obj.setValue (value);
			// obj.insert(new Element ("span").update (value));
			return obj;
		}
		
		if (typeof (value) == "object") {
			var obj = element.addJobj (key);
			return obj.render (value);	
		}
	},
	
	/* renders "obj" (a jasonViewer element) as child of "element" */
	render: function (element, obj) {
		element = $J(element);
		$H(obj).each (function (pair) {
			var valType = typeof (pair.value);
			switch ( valType ) {
				case "undefined":
				case "function":
				case "unknown":
					return;
				case "boolean":
					element.addJvalue (pair.key, pair.value.toString());
					return;
				case "string":
					element.addJvalue (pair.key, pair.value);
					break;
				case "object":
					// if the value is an array, show each value with the same key
					if (Object.isArray(pair.value)) {
 						pair.value.each (function (val) {
							element.addJvalue (pair.key, val); 
						});
					}
					else {
						var obj = element.addJobj (pair.key);
						obj.render (pair.value);
					}
					return;

				default:
					// log ("unknown type: " + objType + " for key: " + pair.key);
					return;
			}
		});
	}
});
	
// -------------- utility ------------------

/* alias for JsonViewerElement.extend */ 
function $J(element) {
  if (arguments.length > 1) {
    for (var i = 0, elements = [], length = arguments.length; i < length; i++)
      elements.push($J(arguments[i]));
    return elements;
  }
  if (typeof element == 'string')
    element = $(element);
  return JsonViewerElement.extend(element);
}

