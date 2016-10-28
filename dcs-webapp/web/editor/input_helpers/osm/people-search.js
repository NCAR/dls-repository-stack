/* API to communicate with the peopleDB */

var PEOPLE_DB_BASE_URL = 'https://people.api.ucar.edu';

var PeopleSearcher = Class.create ({
	/* these attributes must be defined (see 'verify') */
	initialize: function (action) {
		this.action = action;

		// mapping from xmlElementName to UI display name
		this.searchResultColumns = $A([
			['lastName', 'last name'],
			['firstName', 'first name'],
			['middleName', 'middle name'],
			['email', 'email'],
			['upid', 'upid'],
			['internalOrg', 'org']
		]);
		this.searchResults = $A();

		// DOM Locations
		this.doneButton = $('done-button');
		this.resultsTarget = $('search-results'); // displaySearchResults target
		this.displayElement = $('ucas-display'); // the element used to hide or show display (why named ucas??)
		this.searchButton = $('ucasSearchButton');
		this.clearButton = $('ucasClearButton');
		// this.searchTriggers = ['ucasFirstName', 'ucasLastName', 'ucasMiddleName', 'upid'];
		
		// search triggers are fields that listen for a "return" and then do search
		this.searchTriggers = ['ucasFirstName', 'ucasLastName', 'upid'];
		this.selectedPerson = null;
		
		try {
			this.initListeners();
		} catch (error) {
			alert ("init error: " + error);
			return;
		}

		if (true) {
			// search automatically (but need to delay or the params arent' there??)
			// log ("waiting to perform automatic search");
			new PeriodicalExecuter(function(pe) {
				this.search();
				pe.stop();
			}.bind(this), 0.5);
		}
	},

	/* ensure required attributes are defined */
	verify: function () {
		if (!this.resultsTarget)
			throw ("resultsTarget is not defined");
		if (!this.displayElement)
			throw ("displayElement is not defined");
		if (!this.searchButton)
			throw ("searchButton is not defined");
		if (!this.clearButton)
			throw ("clearButton is not defined");
		if (!this.searchTriggers)
			throw ("searchTriggers not defined");
		if (!this.doneButton)
			throw ("doneButton not defined");
	},

	/* assign listeners for buttons and other triggers */
	initListeners: function () {
		try {
			this.verify();
		} catch (error) {
			throw ("missing required attribute: " + error);
		}
		this.searchButton.observe ("click", this.search.bindAsEventListener(this));
		this.clearButton.observe ("click", this.clear.bindAsEventListener(this));

		// if user hits "return" in the text box, do search
		$A(this.searchTriggers).each (function (id) {
			$(id).observe ('keyup', function (evnt) {
				if (evnt.keyCode == 13)
					this.search (evnt);
			}.bind(this));
		}.bind(this));
	},

	/*  uses the values of UI fields to create search URL
		gets response via peopleDBProxy (/proxy/ucas.do)
	*/
	search: function (evnt) {
		
		this.doneButton.disable();
		
		if (evnt) {
			evnt.stop();
			var button = evnt.element();
			button.blur();
		}

		var params = {
			'lastName' : $F('ucasLastName'),
			// 'middleName' : $F('ucasMiddleName'), // no longer use middle for search
			'firstName' : $F('ucasFirstName'),
			'upid' : $F('upid'),
			
			'searchType' : 'advancedSearch', // required for all options
			'includeActive' : $F('active_status'),
			'includeInactive' : $F('inactive_status'),
			
			'searchScope' : ($F('all_scope') || 
							 $F('staff_scope') ||
							 $F('visitor_scope') ||
							 $F('collaborator_scope'))
		}

		var peopleDBUrl = PEOPLE_DB_BASE_URL + "/persons?" + $H(params).toQueryString();

		log ("PeopleSearcher: searching ...")
		// this.showSearchParams (peopleDBUrl);
		
		peopleDBProxy (this.action, peopleDBUrl, function (json) {

			try {
				this.searchResults = this.parseSearchResults(json);
				log ("PeopleSearcher: got " + this.searchResults.size() + ' search results');
			} catch (error) {
				log ("ERROR: PeopleSearcher parseSearchResults: " + error);
			}

			try {
				this.displaySearchResults (params['firstName'], params['lastName']);
			} catch (error) {
				log ("ERROR PeopleSearcher displaySearchResults error: " + error);
			}
		}.bind(this));
	},

	showSearchParams: function (url) {
		var report = $("report");
		if (!report) return;
		report.update();
		var params = url.toQueryParams()
		for (key in params) {
			if (params[key])
				report.insert (new Element ("div").update (key + ": " + params[key]))
		}
	},
	
	/* search results are initialized by search() via parseSearchResults */
	displaySearchResults: function (fnq, lnq) {
		// log ('displaySearchResults')
		var target = $("search-results");
		target.update();
		var tbody = new Element ('tbody');
		target.appendChild (tbody);

		// columns :(value/label)
		var columns = this.searchResultColumns;

		// HEADER
		var headerrow = new Element ('tr');
		tbody.appendChild (headerrow);
		columns.each (function (col) {
			headerrow.appendChild (new Element ('th').update (
				new Element ('div').update(col[1])));
		});


		if (!this.searchResults || this.searchResults.size() == 0) {
			log ("nothing found");
			$('numResults').update('(0)');
			var row = new Element ('tr');
			var cell = new Element ('td', {'colspan':columns.size()}).update ("nothing found");
			tbody.appendChild (row);
			row.appendChild (cell);
		}

		else {
			// log (' - processing ' + this.searchResults.size() + ' searchResults');
			$('numResults').update('(' + this.searchResults.size() + ')');
			this.searchResults.each (function (result) {
				var row = result.getDom(columns, {'fnq':fnq, 'lnq':lnq});
				tbody.appendChild(row);
			});
		}
		
		// if there is only one result, go ahead and select it.
		if (this.searchResults.size() == 1) {
			this.searchResults.first().clickHandler(null);
		}
		
		this.displayElement.show();

	},

	// parse json into SearchResult instances and sort by lastName + firstName
	parseSearchResults: function (json) {
		var results = $A(json).inject ([], function (acc, resultData) {
			acc.push (new SearchResult (resultData, this));
			return acc;
		}.bind(this));

		// return sorted
		return $A(results).sortBy (function (entry) {
			return entry.lastName + entry.firstName;
		});
	},

	/* remove search input and results display */
	clear: function () {
		$A(this.searchTriggers).each (function (id) {
			$(id).value = "";
		});
		this.displayElement.hide();
	}
});

function peopleDBProxy (action, peopleDB2Url, callback) {
	// log ('peopleDBProxy: - ' + peopleDB2Url);
	new Ajax.Request (action, {
		parameters: {
			'command' : 'peopleDB2Search',
			'url' : peopleDB2Url
		},
		onSuccess: function (transport) {
			var response = transport.responseText;
			// log (response.trim().truncate(500)); // causes IE to barf!
			try {
				var json = response.evalJSON();
			} catch (error) {
				log ("Error evaluating JSON: " + error);
				json = [];
			}
			callback(json);

		}.bind(this),
		onFailure: function() {
			log ('peopleDBProxy: Something went wrong...')
		},
		onComplete: function (transport) {
			// log (" proxy complete");
		}
	});
}
