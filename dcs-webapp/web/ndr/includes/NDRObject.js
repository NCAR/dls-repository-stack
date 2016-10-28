//------------- NDRObject ---------------

function getNDRObject (jsonObj) {
	var ndrObjType;
	var ndrObj = null;		
	var ndrObject = jsonObj.NSDLDataRepository.NDRObject;
	
	try {
		ndrObjType = $H(ndrObject.properties).get("nsdl:objectType");
	} catch (error) { 
		log ("could not determine ndrObjectType: " + error);
		ndrObjType = null;
	}
	switch (ndrObjType) {
		case "MetadataProvider":
			ndrObj = new MetadataProvider (jsonObj);
			break;
		case "Agent":
			ndrObj = new Agent (jsonObj);
			break;
		case "Metadata":
			ndrObj = new Metadata (jsonObj);
			break;
		case "Resource":
			ndrObj = new Resource (jsonObj);
			break;
		case "Aggregator":
			ndrObj = new Aggregator (jsonObj);
			break;
		default:
			log ("unrecognized type: " + ndrObjType);
	}
	return ndrObj;
}


var NDRObject = Class.create({

	initialize: function (jsonObj) {
		this.json = jsonObj;
		var ndrObject = jsonObj.NSDLDataRepository.NDRObject;
		this.ndrProperties = $H(ndrObject.properties);
		this.ndrDatastreams = $H(ndrObject.data);
		this.ndrRelationships = $H(ndrObject.relationships);
	},
	
	getServiceDescriptionUrl: function () {
		if (this instanceof MetadataProvider ||
			this instanceof Aggregator) {
			return this.ndrDatastreams.get('serviceDescription').content
		}
		else {
			return null;
		}
	},
	
	getProperty: function (prop) {
		return this.ndrProperties.get(prop);
	},
	
	getRelationship: function (relnName) {
 		reln = this.ndrRelationships.find (function (pair) {
			// alert ("pair.key: " + pair.key + "  type: " + typeof (pair.value) + " relnName: " + relnName);
			return (pair.key == relnName);
		});
		if (reln) {
			// if there are more than one relationship of name relnName, take first
			val = reln.value
			return (typeof (val) == "string" ? val : $A(val).first());
		}
		else
			return null;
	},
	
	isDeleted: function () {
		return this.ndrProperties.get("fedora-model:state") == "Deleted";
	},
	
	getObjectType: function () {
		return this.ndrProperties.get("nsdl:objectType");
	},
	
	getHandle: function () {
		 return (this.ndrProperties.get("nsdl:hasHandle") || this.ndrProperties.get("NSDL:hasHandle"));
	},
	
	getResourceUrl: function () {
			return null;  // only Resource object have a resourceURL
	},
	
	getHandleDecoration: function () {
		
		var label = "decoration not implemented for " + this.getObjectType();
		return new Element("span")
			.update (label)
			.setStyle ({backgroundColor:'pink'});
	},
	
	toHtml: function (target) {
		if (!target) {
			target = document.createElement ('div');
		}
 		target = $J(target).addDiv();
		target.addClassName ("ndr-object");
		target.addJheader (this.getObjectType() + " &mdash; " + this.getHandle());
		
		if (this.isDeleted())
			target.addClassName ("deleted");
		
		if (this.getResourceUrl())
			target.addJvalue ("resourceURL", this.getResourceUrl());
		
		if (this.ndrProperties) {
			this.renderProperties (target);
		}
		// datastreams is a hash of hashes, the value of each which (e.g.., "format") may be (list).
		if (this.ndrDatastreams) {
			this.renderDataStreams (target);
		}
		// relationships is a flat hash.
		if (this.ndrRelationships) {
			this.renderRelationships (target);
		}
		return target;
	},
	
	renderProperties: function (target) {
		var properties = target.addJobj ("properties");
		this.ndrProperties.each ( function (pair) {
			
/*			// restricted properties
			if (pair.key.startsWith("ncs:") ||
				pair.key.startsWith("fedora")) {
				properties.addJvalue (pair.key, pair.value);
			} 
*/			
			// show all properties except the handle
			if (pair.key.toLowerCase() != "nsdl:hashandle") {
				properties.addJvalue (pair.key, pair.value);
			}
		});
	},
	
	renderDataStreams: function (target) {
		var datastreams = target.addJobj ("data streams");
		
		/* 	for datastreams, we traverse *values* because we use
			the value.ID for the label, NOT the element *name* */
		this.ndrDatastreams.values().each ( function (ds) {
			if (Object.isArray(ds)) {
				$A(ds).each (function (strm) {
					// filter out the _info streams
					if (!strm.ID.endsWith ("_info")) {
						var dsLink = makeDataStreamLink (strm.content);
						datastreams.addJvalue (strm.ID, dsLink);
					}
				} );
			}
			else {
				var dsLink = makeDataStreamLink (ds.content);
				datastreams.addJvalue (ds.ID, dsLink);
			}
		}.bind (this));
	},
	
	renderRelationships: function (target) {
		var relationships = target.addJobj ("relationships");
		this.ndrRelationships.each ( function (reln) {
			if (Object.isArray(reln.value)){
				$A(reln.value).each (function (handle) {
					var ndrLink = makeNdrObjectLink (handle);
					relationships.addJvalue (reln.key, ndrLink); 
				});
			}
			else {
				var handle = reln.value;
				var ndrLink = makeNdrObjectLink (handle);
				relationships.addJvalue (reln.key, ndrLink);
			}
		});		
	}
});

var MetadataProvider = Class.create ( NDRObject, {
	getHandleDecoration: function () {
		
		var label = (this.getProperty ("ncs:collectionName") ||
					 this.getProperty ("nsdl:setName") ||
					 this.getProperty ("dlese:collectionName") || "unknown");
		
		return new Element ("span")
			.update(label)
			.addClassName ("metadataprovider-label");
	}
});

var Agent = Class.create ( NDRObject, {
	getHandleDecoration: function () {
		var resource = this.ndrProperties.find ( function (pair) {
			return pair.key.startsWith ("nsdl:hasResource")
		});
		var label = resource != null ? resource.value : "not found";
		return new Element("span")
			.update (label)
			.addClassName ("agent-label");
	}
});

var Metadata = Class.create ( NDRObject, {
	getHandleDecoration: function () {
		var label = this.getProperty ("nsdl:uniqueID");
		return new Element("span")
			.update (label)
			.addClassName ("metadata-label");
	}
});

var Resource = Class.create ( NDRObject, {
	getResourceUrl: function () {
		return this.ndrProperties.get("nsdl:hasResourceURL");
	},
	getHandleDecoration: function () {
		
		// derive the label from the first property starting with "nsdl:hasResource"
		var resource = this.ndrProperties.find ( function (pair) {
			return pair.key.startsWith ("nsdl:hasResource")
		});
				
		var label = resource != null ? resource.value : "not found";
		return new Element("span")
			.update (label)
			.addClassName ("resource-label");
	}
});

var Aggregator = Class.create ( NDRObject, {
	getHandleDecoration: function () {
		var label = this.getObjectType();
		return new Element("span")
			.update (label)
			.addClassName ("aggregator-label");
	}
});

// ------------- utility -------------------------
	
function makeDataStreamLink (href) {
	if (!href) return null;

	if (href.startsWith ("http")) {
		var className = null;
		var target = null;
		var label = href;
		if (href.endsWith ("format_pc") && NDRGlobals.contentProxyBaseUrl) {
			className = "pc-link";
			target = "_blank";
			var handle = NDRGlobals.getHandle (href);
			if (handle)
				href = NDRGlobals.contentProxyBaseUrl + "/getContent?handle=" + handle;
		}
		var link = new Element("a" , {href:href}).update (label);
		if (className)
			link.addClassName (className);
		if (target)
			link.setAttribute ("target", target);
		return link;
	}
	return href;
}

function makeNdrObjectLink (handle) {
	var ndrGetBaseUrl = NDRGlobals.ndrApiBaseUrl + "/get/";
	var href = ndrGetBaseUrl + handle;
	return new Element ("a", {href:href})
		.update(handle)
		.addClassName ('ndr-link');
}
