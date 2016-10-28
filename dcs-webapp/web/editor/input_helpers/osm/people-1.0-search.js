/* API to communicate with the peopleDB */
var PeopleSearcher = Class.create ({
	/* these attributes must be defined (see 'verify') */
	initialize: function (action) {
		this.action = action;

		// mapping from xmlElementName to UI display name
		this.searchResultColumns = $A([
			['lastName', 'last name'],
			['firstName', 'first name'],
			['middleName', 'middle initial'],
			['email', 'email'],
			['upid', 'upid']
		]);
		this.searchResults = $A();

		// DOM Locations (seems a bit much??)
		this.resultsTarget = $('search-results'); // displaySearchResults target
		this.displayElement = $('ucas-display'); // the element used to hide or show display (why named ucas??)
		this.searchButton = $('ucasSearchButton');
		this.clearButton = $('ucasClearButton');
		this.searchTriggers = ['ucasFirstName', 'ucasLastName'];
		this.selectedResultDisplay = $("selected-result-display");
		this.selectedResult = $("selected-result"); // BAD NAME! - what should it be??
		this.positionDisplay = new PositionDisplay(this.selectedResult, this)
		this.initListeners();
		this.selectedPerson = null;


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
		if (!this.selectedResultDisplay)
			throw ("selectedResultDisplay not defined");
		if (!this.selectedResult)
			throw ("selectedResult not defined");
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
		evnt.stop();
		this.selectedResultDisplay.hide();
		var button = evnt.element();

		var params = {
			'command' : 'peopleSearch',
			'lastName' : $F('ucasLastName'),
			'firstName' : $F('ucasFirstName'),
			'upid' : $F('upid')
		}

		peopleDBProxy (this.action, params, function (json) {
			button.blur();
			try {
				this.searchResults = this.parseSearchResults(json);
				log ("got " + this.searchResults.size() + ' results');
				this.displaySearchResults (params['firstName'], params['lastName']);
			} catch (error) {
				log ("displaySearchResults error: " + error);
			}
		}.bind(this));
	},


	displaySearchResults: function (fnq, lnq) {
		log ('displaySearchResults')
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
			var row = new Element ('tr');
			var cell = new Element ('td', {'colspan':columns.size()}).update ("nothing found");
			tbody.appendChild (row);
			row.appendChild (cell);
		}

		else {
			this.searchResults.each (function (result) {
				var row = result.getDom(fnq, lnq, columns);
				tbody.appendChild(row);
			});
		}
		this.displayElement.show();

	},

	// parse json into SearchResult instances and sort by lastName + firstName
	parseSearchResults: function (json) {
		var results = $A(json).inject ([], function (acc, resultData) {
			acc.push (new SearchResult (resultData, this));
			return acc;
		}.bind(this));

		return $A(results).sortBy (function (entry) {
			return entry.lastName + entry.firstName;
		});
	},

	/* remove search input and results display */
	clear: function () {
		$A(this.searchTriggers).each (function (id) {
			$(id).value = "";
		});
		this.selectedResultDisplay.hide();
		this.displayElement.hide();
	}
});

var SearchResult = Class.create ({
	initialize: function (json, searcher) {
		this.json = json;
		this.searcher = searcher
		this.firstName = json.firstName;
		this.lastName = json.lastName;
		this.upid = json.upid;
		this.middleName = json.middleName;
		this.email = json.email;
		this.nickname = json.nickname
		this.positions = null;
		this.row = null;
	},

	/* creates the dom for this search result and as side effect sets this.row */
	getDom: function (fnq, lnq, columns) {
		this.row = $(new Element ('tr'));
		// this.row.addClassName ('result-row');
		this.row.observe ('mouseover', function (event) {
			this.row.addClassName ('result-row-over');
		}.bind(this));
 		this.row.observe ('mouseout', function (event) {
			this.row.removeClassName ('result-row-over');
		}.bind(this));
		this.row.observe ('click', this.handleSearchResultClick.bind(this));
		columns.each ( function (col) {
			var col_id = col[0];
			var value = $H(this.json).get (col_id) || "&nbsp";
			if (col_id == 'firstName' && fnq)
				value = this.highlight (value, fnq);
			if (col_id == 'lastName') {
				value = this.highlight (value, lnq);
			}
			this.row.appendChild (new Element ('td').update (value));
		}.bind(this));
		return this.row;
	},

	/* show the positions for this search result */
	displayPositions: function () {
		log ("displayPositions!");
		if (this.positions == null) {
			var params = {
			'command' : 'getPerson',
			'upid' : this.upid
			}

			peopleDBProxy (this.searcher.action, params, function (json) {
				try {

					this.positions = $A(json.positions).inject ([], function (acc, positionData) {
						acc.push (positionData);
						return acc;
					});

					this.positions = this.positions.sortBy (function (position) {
						return position.startDate;
					});
					// this.updatePositionDisplay();
					this.searcher.positionDisplay.displayPositions(this.positions);

				} catch (error) {
					log ("displayPositions error: " + error);
				}
			}.bind(this));
		}
		else {
			// this.updatePositionDisplay();
			this.searcher.positionDisplay.displayPositions(this.positions);
		}

	},


	/* highlight all occurances of 'sub' in 'text' */
	highlight: function (text, sub) {
		if (!sub)	return text;
		return text.gsub (new RegExp(sub,'i'), function (match) {
			return '<span style="font-weight:bold;color:#000099">' + match[0] + '</span>';
		});
	},

	handleSearchResultClick: function (event) {
		log ("row click");
		this.searcher.selectedPerson = this;
		this.searcher.searchResults.each (function (result) {
			if (result == this)
				result.row.addClassName ('result-row-selected');
			else
				result.row.removeClassName ('result-row-selected');
		}.bind(this));
		this.displayPositions();
		log ('  selected: ' + this.searcher.selectedPerson.lastName);
	}
});

var PositionDisplay = Class.create ({
	initialize: function (positionDomRoot, searcher) {
		this.positionDomRoot = positionDomRoot;
		this.searcher = searcher;
		this.columns = $A([
			['organization', 'organization'],
			['startDate', 'start'],
			['endDate', 'end']
		]);
	},

	displayPositions: function (positions) {
		log ("displayPositions()");
		this.searcher.selectedResultDisplay.show();
		this.positionDomRoot.update();
		log (positions.size() + " positions");
		var target = new Element ('tbody');
		this.positionDomRoot.appendChild(target);
		target.appendChild (this.getHeaderRow());

		positions.each (function (pos) {
			log ("inserting position");
			target.insert (this.getPositionRow(pos));
		}.bind(this));
	},

	handlePositionClick: function () {
		log ("gotPositionClick");
	},

	getPositionRow: function (position) {

		// columns :(value/label)

		var row = $(new Element ('tr'));
		row.addClassName ('result-row');
		row.observe ('mouseover', function (event) {
			row.addClassName ('result-row-over');
		});
		row.observe ('mouseout', function (event) {
			row.removeClassName ('result-row-over');
		});
		row.observe ('click', this.handlePositionClick.bind(this));
		this.columns.each ( function (col) {
			var col_id = col[0];
			var value = $H(position).get (col_id) || "&nbsp";
			row.appendChild (new Element ('td').update (value));
		}.bind(this));
		return row;
	},

	getHeaderRow: function (position) {

		// columns :(value/label)

		var headerrow = new Element ('tr');
		this.columns.each (function (col) {
			headerrow.appendChild (new Element ('th').update (
				new Element ('div').update(col[1])));
		});
		return headerrow;
	}
});

function peopleDBProxy (action, params, callback) {
	new Ajax.Request (action, {
		parameters: params,
		onSuccess: function (transport) {
			var response = transport.responseText;
			// log (response);
			try {
				var json = response.evalJSON();
			} catch (error) {
				log ("Error evaluating JSON: %s", error);
				json = [];
			}
			callback(json);

		}.bind(this),
		onFailure: function() {
			log ('peopleDBProxy: Something went wrong...')
		}
	});
}
