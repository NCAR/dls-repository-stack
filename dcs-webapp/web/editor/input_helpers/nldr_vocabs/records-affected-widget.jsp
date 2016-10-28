<c:set var="contextPath"><%=request.getContextPath().trim()%></c:set>
<%-- <div style="margin-left:25px">
	<span style="width:200px;display:block;text-align:center;margin:25px 0px 0px 0px;padding:5px;border:purple 2px outset;background-color:#cbbecc;font-size:14pt;font-weight:bold;margin-top:5px;"> 
		<input type="button" value="show affected records" id="${id}_trigger" />
	</span>
	<div id='affectedRecords' style='display:none'></div>
 	<div style="font-size:10px;margin-top:3px">id: ${id}</div>
	<div style="font-size:10px;margin-top:3px">xpath: ${sf:idToPath (id)}</div>
	<div style="font-size:10px;margin-top:3px">collection: ${sef.collection}</div> 
	<div style="font-size:10px;margin-top:3px">collection: ${sef.collection}</div>
	
</div> --%>

<script type="text/javascript">


var RecordsAffectedHelper = Class.create ( {
	initialize: function (id) {
		this.id = id
		this.recordsAffectedInput = $(id);
		
	
		this.controls = this.initializeControls();
		
		this.affectedRecordsListing = this.initializeAffectedRecordsListing();
		
		this.controls.observe ('click', function () {
			if (this.affectedRecordsListing.visible())
				this.affectedRecordsListing.hide();
			else
				this.showAffectedRecords();
		}.bind(this));
		
		this.termElement = this.getTermElement(); // doesn't change for life of page
		if (this.termElement) {
			this.termElement.observe ('change', this.updateRecordsAffected.bind(this));
			this.updateRecordsAffected();
		}
		else {
			log ("no term element found");
			this.setAffectedValueField(0);
		}
	},
	
	initializeAffectedRecordsListing: function () {
	log ("initializeAffectedRecordsListing");
		var listingElement = new Element ('div');
		listingElement.setStyle ({
			margin:'3px 3px 3px 70px',
			border:'thin solid #CCCEE6',
			padding: '3px',
			width:'250px'
		});
		listingElement.hide();
		this.controls.insert ({after:listingElement});
		return listingElement;
	},
	
	initializeControls: function() {
		var trigger = new Element ('span').update ('show/hide affected records');
		trigger.addClassName ("action-button");
		trigger.observe ('mouseover', function () {
			trigger.setStyle ({cursor:'pointer', textDecoration:'underline'});
		});
		trigger.observe ('mouseout', function () {
			trigger.setStyle ({cursor:'default', textDecoration:'none'});
		});
		trigger.setStyle ({padding:'0px 0px 0px 15px', display:'none'});
		this.recordsAffectedInput.insert ({after:trigger});
		return trigger;
	},
	
	/* we don't know where the term is stored. it may be at
		/term/action/keep, add, or remove
	*/	
	getTermElement: function() {

		return $('_^_vocabTerm_^_fullName');
	},

	/* term is the value of this.termElement. it CAN change within lifespan of page */
	getTerm: function() {
		return $F(this.termElement);
	},

	/* the query (i.e., the searchField(s)) we use is dependent on the collection. 
	   unfortunately, this means that the collection names are hard-coded into the 
		 code ... */
	getQuery: function() {
		log ("getQuery()");
		var collection = "${sef.collection}";
		var term = this.getTerm();
		log ("term: " + term);
		if (collection == "pubname") {
			return '/key//record/general/pubName:"'+term+'"';
		}
		if (collection == "event") {
			return '/key//record/general/eventName:"'+term+'"';
		}
		if (collection == "inst") {
			var q = '/key//record/contributors/organization/affiliation/instName:"'+term+'"';
			q += ' OR ';
			q += '/key//record/contributors/person/affiliation/instName:"'+term+'"';
			return q;
		}
				
		throw "WARNING: unrecognized termType: " + termType;
	},

	/* obtain the number of records affected via proxy, and update the recordsAffected metadata field */
	updateRecordsAffected: function() {
		log ("updating");
		var query = null;
		try {
			query = this.getQuery();
		} catch (error) {
			log ("unable to populate recordsAffected field: " + error);
			return;
		}
		
		log ("query: " + query)
		
		this.doProxy (query, function (json) {
			if (json == null) {
				this.setAffectedValueField(null);
				return;
			}
		
			if (json.DDSWebService.error) {
				var code = json.DDSWebService.error.code
				if (code == 'noRecordsMatch')
					this.setAffectedValueField(0);
				else
					log ('recordsAffected error: ' + code)
				return;
			}
				
			var numResults = json.DDSWebService.Search.resultInfo.totalNumResults;
			if (numResults != NaN) {
				this.setAffectedValueField (numResults);
			}
		}.bind(this), 1);
	},

	setAffectedValueField: function (numRecords) {
		log ("setting recordsAffected to " + numRecords);
		this.affectedRecordsListing.hide();
		if (numRecords == null) {
			this.controls.hide();
			return;
		}
		this.recordsAffectedInput.value = numRecords;
		if (numRecords > 0) {
			log ("showing");
			this.controls.show()
		}
		else
			this.controls.hide();
	},
			
	
	/* display the records affected by the term (the results of searching for this
		term in the approapriate metadata fields */
	showAffectedRecords: function() {
		var query = null;
		try {
			query = this.getQuery();
		} catch (error) {
			log ("unable to show affected records: " + error);
			return;
		}
		
		log ("QUERY: " + query);
		
		this.doProxy (query, function (json) {
			if (json == null) {
				this.listAffectedRecords (null);
				return;
			}
		
			if (json.DDSWebService.error) {
				var code = json.DDSWebService.error.code
				this.listAffectedRecords (null);
				return;
			}
				
			var records = json.DDSWebService.Search.results.record;
			if (!records)
				log ("records not found");
			else {
				// ensure that records is an array
				if (records.length == undefined)
					records = $A([records])
				log (records.length + ' records');
				this.listAffectedRecords (records)
			}

		}.bind(this));
	},

	/* show affected records in UI */
	listAffectedRecords: function(records) {
		var display = this.affectedRecordsListing;
		display.update()
		
		if (!records) {
			display.insert (new Element ('div').update ('no records to show'));
			return;
		}
		
		// for now, simply show the IDs
		$A(records).each (function (record) {
			var id = record.head.id;
			display.insert (new Element ('div').update (id));
		});

		display.show();
	},
	
	/* builds and submits request to proxy, calls callback */
	doProxy: function(query, callback, numresults) {
		// var baseUrl = 'http://nldr.library.ucar.edu/schemedit/services/ddsws1-1'
		var baseUrl = 'http://tambora.ucar.edu:10160/schemedit/services/ddsws1-1'
		// var baseUrl = 'http://localhost/schemedit/services/ddsws1-1'

		
		var searchParams = {
				'verb' : 'Search',
				'q' : query,
				'xmlFormat' : 'osm',
				's' : 0,
				'output':'json',
				'n' : numresults || 500
			}
			
		log ("proxy: ${contextPath}/jsonProxy.do");
		log ("uri: " + baseUrl + '?' + $H(searchParams).toQueryString());
			
		new Ajax.Request ("${contextPath}/jsonProxy.do", {
			parameters: {
				'uri': baseUrl + '?' + $H(searchParams).toQueryString()
			},
			onSuccess: function (transport) {
					var response = transport.responseText.strip();
					log (response);
					try {
						var json = response.evalJSON();
					} catch (error) { 
						log ("Error evaluating JSON: " + error);
						return;
					}
					callback (json)
			},
			onFailure: function () {
				log ("something went wrong with proxy ...");
				callback (null);
			}
		});
	}
});

document.observe ('dom:loaded', function () {
	new RecordsAffectedHelper ('${id}')
});

</script>
