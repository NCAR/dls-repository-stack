<c:set var="contextPath"><%=request.getContextPath().trim()%></c:set>
<%-- <div style="margin-left:25px">
	<span style="width:200px;display:block;text-align:center;margin:25px 
							0px 0px 0px;padding:5px;border:purple 2px outset;background-color:#cbbecc;font-size:14pt;
							font-weight:bold;margin-top:5px;"> 
		<input type="button" value="find similar titles" id="${id}_trigger" />
	</span>
	<div id='affectedRecords' style='display:none'></div>
 	<div style="font-size:10px;margin-top:3px">id: ${id}</div>
	<div style="font-size:10px;margin-top:3px">xpath: ${sf:idToPath (id)}</div>
	<div style="font-size:10px;margin-top:3px">collection: ${sef.collection}</div> 
	<div style="font-size:10px;margin-top:3px">collection: ${sef.collection}</div>
	
</div> --%>

<table id="sim-title-helper_${id}">
	<tr valign="top">
		<td align="left">
			<input type="button" value="find similar titles" />
		</td>
		<td class="report-cell">
		</td>
	</tr>
</table>

<script type="text/javascript">

var SimilarTitlesHelper = Class.create ({
	initialize: function (id) {
		this.titleInputId = id;
		this.baseElement = $("sim-title-helper_"+this.titleInputId);

		this.recordId = '${sef.recId}';
		this.title = $F(this.titleInputId);
		this.titleKey = this.getTitleKey();
		
		this.trigger = this.baseElement.down('input[type="button"]');
		if (!this.trigger)
			throw "trigger not found";
		this.trigger.observe ('click', this.reportRecordsWithSimilarTitles.bind(this));
		
		$(this.titleInputId).observe ('blur', this.reportRecordsWithSimilarTitles.bind(this));
		
		log ('recordId: ' + this.recordId);
		log ('title: ' + $F(this.titleInputId));
		log ('titleKey: ' + this.titleKey);
		
		this.reportRecordsWithSimilarTitles()
	},
	
	getTitleKey: function () {
		return $F(this.titleInputId).toLowerCase().gsub(/[^abcdefghijklmnopqrstuvwxyz]/, '');
	},
	
	reportRecordsWithSimilarTitles: function (event) {
		this.findRecordsWithSimilarTitles( function (sims) {
			log (sims.length + ' sims found');
			
			var reportCell = this.baseElement.down('td.report-cell');
			
			// don't show message if there are no sims, UNLESS the user clicked the button
			var trigger = null;
			if (event)
				trigger = event.element().identify();
			if (sims.length == 0 && trigger != this.trigger.identify()) {
				reportCell.update();
				return;
			}
			
			try {
				var recsPlural = sims.length == 1 ? 'record' : 'records';
				var alertMsg = new Element ('div')
					.update(sims.length + ' ' + recsPlural + ' with similar titles found');
				if (sims.length > 0)
					alertMsg.addClassName ("error-msg");
				reportCell.update (alertMsg);
				sims.each (function (sim) {
					var params = {
						'fileid' : sim.id,
						'rt' : 'text'
					}
					var href = '../browse/display.do?' + $H(params).toQueryString();
					var idLink = new Element('a', {
						'title':'See record xml in new window',
						'target' : '_blank',
						'href' : href
					}).update(sim.id);
					var simReport = new Element ('div');
					simReport.update (new Element('span').update (sim.title + " ("));
					simReport.insert (idLink);
					simReport.insert (new Element('span').update(")"));
					reportCell.insert(simReport);
				});
			} catch (error) {
				alert (error);
			}
		}.bind(this));

	},
	
	/* returns a dict {title:title, id:recordId} of records having similar titles */
	findRecordsWithSimilarTitles: function(callback) {
		log ("findRecordsWithSimilarTitles");
		var query = 'dcsosmFlattenedTitle:'+this.getTitleKey();
		
		log ("QUERY: " + query);
		
 		return this.doProxy (query, function (json) {
			if (json == null) {
			log ("search response is null?!");	
			callback([])
			return;
			}
		
			if (json.DDSWebService.error) {
				var error_code = json.DDSWebService.error.code
				// this.listAffectedRecords (null);
				log ("error code: " + error_code);
				callback ([]);
				return;
			}
				
 			var records = json.DDSWebService.Search.results.record;
			if (!records) {
				log ("records NOT found");
				callback([])
				}
			else {
				// ensure that records is an array
				if (records.length == undefined)
					records = $A([records])
				// log (records.length + ' records found');
				
				// filter out THIS recordId ...
				var sims = records.inject ([], function (acc, rec) {
 					var recId = rec.metadata.record.general.recordID;
					var title = rec.metadata.record.general.title.content;
					if (recId != this.recordId) {
						acc.push({'title':title, 'id':recId});
					}
					return acc;
				}.bind(this));
				callback(sims);
			}
		}.bind(this));
	},
	
	/* builds and submits request to proxy, calls callback */
	doProxy: function(query, callback, numresults) {
	
		// by default we are querying the same instance we are using
		var baseUrl = '${f:contextUrl(pageContext.request)}/services/ddsws1-1'

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
					// log (response);
					try {
						var json = response.evalJSON();
					} catch (error) { 
						log ("Error evaluating JSON: " + error);
						return;
					}
					callback (json);
			},
			onFailure: function () {
				log ("something went wrong with proxy ...");
				callback (null);
			}
		});
	}

});

document.observe ('dom:loaded', function () {
	try {
		new SimilarTitlesHelper ('${id}')
	} catch (error) {
		log ('ERROR: ' + error);
	}
});
</script>
