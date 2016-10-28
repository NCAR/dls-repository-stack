//------------- NDRListMembersObject ---------------
         
var NDRListMembersObject = Class.create({

	initialize: function (jsonObj) {
		this.json = jsonObj;
		var response = $H(jsonObj.NSDLDataRepository)
		if (!response) alert ("no response");
		this.responseTime = response.get('responseTime');
		this.requestURL = response.get('requestURL');
		this.resultData = response.get('resultData');
		this.error = response.get('error');
	},
	
	hasError: function () {
		return this.error != null;
	},
	
	getError: function () {
		return this.error;
	},
	
	// return a array, but be careful if there is only one item - we must package it as an array
 	getHandles: function () {
		try {
			var handles = this.resultData.handleList.handle;
			if (typeof (handles) == "string")
				return $A([handles]);
			else
				return $A(handles);
		} catch (error) {
			return $A();
		}
	},
	
	makeNdrLink: function (handle, label) {
		var ndrGetBaseUrl = NDRGlobals.ndrApiBaseUrl + "/get/"
		var href = ndrGetBaseUrl + handle;
		return new Element ("a", {href:href})
			.update (label == null ? handle : label)
			.addClassName ("ndr-link");
	},
	
	displayMember: function (handle, target) {
		var e = target.addElement ("div");
		e.addClassName ("json-value");
		e.appendChild (this.makeNdrLink (handle));
		try {
			var idSpan = $(document.createElement ("span"));
			idSpan.setAttribute ("id", handle);
			idSpan.setStyle ({marginLeft:"15px"});
		} catch (error) {
			alert ("couldnt make idSpan");
		}
		
		e.appendChild (idSpan);
		this.setId (handle);
	},
	
	// make an ajax call to obtain the record id
	// NOTE: now just displaying the collectionName ... because ID is getting shown through "NDRObject decoration"
	setId: function (handle) {
		var ndrGetBaseUrl = NDRGlobals.ndrApiBaseUrl + "/get/"
		var href = ndrGetBaseUrl + handle;
		var member = this;
		new Ajax.Request ("ndr.do?command=proxy&uri=" + encodeURIComponent(href), {
			method:'get',
			onSuccess: function (transport) {
				try {
					var json = transport.responseText.evalJSON(true);
					
					// var md = new NDRObject (json);
					var md = getNDRObject (json);
					var id = md.getProperty ("nsdl:uniqueID");
					// $(handle).update ("id: " + id); // show the id of the MD record
					
					// is this MD for a managed Collection? if so, 
					var relnName = "ncs:collectionMetadataFor";
					var collectionMDFor = md.getRelationship (relnName);
					if (collectionMDFor && !collectionMDFor.strip().empty()) {
						// display a link to the collection MDP
/* 						var link = member.makeNdrLink (collectionMDFor, "managed MDP");
						link.setStyle ({marginLeft:"15px"});
						link.observe ("click", ndrclick);
						$(handle).appendChild (link); */
						
						var nameSpan = $(document.createElement ("span"));
						var nameId = "collection-name-"+collectionMDFor;
						nameSpan.setAttribute ("id", nameId);
						nameSpan.addClassName ("metadataprovider-label");
						nameSpan.setStyle ({marginLeft:"15px"});
						$(handle).appendChild (nameSpan);
						member.setCollectionName (collectionMDFor, nameId);
					}

				} catch (error) {
					alert ("couldn't obtain NDRObject for " + handle);
				}
			}
		});
	},
	
	setCollectionName: function (mdpHandle, target) {
		var ndrGetBaseUrl = NDRGlobals.ndrApiBaseUrl + "/get/"
		var href = ndrGetBaseUrl + mdpHandle;
		var member = this;
		new Ajax.Request ("ndr.do?command=proxy&uri=" + encodeURIComponent(href), {
			method:'get',
			onSuccess: function (transport) {
				try {
					var json = transport.responseText.evalJSON(true);
					// var md = new NDRObject (json);
					var md = getNDRObject (json);
					var collectionName = md.getProperty ("ncs:collectionName");
					
					if (collectionName)
						$(target).update ("collectionMetadataFor <b>" + collectionName + "</b>");
					else
						$(target).update ("<i>collection name not found</i>");

				} catch (error) {
					alert ("couldn't obtain collectionName for " + mdpHandle);
				}
			}
		});
	},
	
	toHtml: function (target) {
		if (!target) {
			target = document.createElement ('div');
		}
 		target = $J(target).addDiv();
		target.addClassName ("ndr-object");
		target.addJheader ("Members List");
		
		var handles = this.getHandles();
		if (handles && handles.size() > 0) {
			
			handles.each (function (handle) {
				this.displayMember (handle, target);
			}.bind(this));
		}
		else {
			var e = target.addElement ("div");
			e.addClassName ("json-value");
			e.update ("<i>no members found</i>");
		}
		return target;
	}
});
