
var DirectorySearcher = Class.create ({
	/* these attributes must be defined (see 'verify') */
	initialize: function () {
		this.resultsTarget;  // table that search results are written to
		this.displayElement; // element encapsulating search results
		this.searchButton;   // 
		this.clearButton;
		this.searchTriggers;  //  ids of elements that listen for an "enter" and trigger search
	},
	
	/* make sure the required attributes are defined */
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
	
	search: function () {
		throw ("Search not implemented");
	},
	
	updateDisplay: function () {
		throw ("updateDisplay not implemented");
	},
	
	clear: function () {
		throw ("clear not implemented");
	},
	
	getResults: function () {
		throw ("getResults not implemented");
	},
	
	/* highlight all occurances of 'sub' in 'text' */
	highlight: function (text, sub) {
		if (!sub)	return text;
		return text.gsub (new RegExp(sub,'i'), function (match) {
			return '<span style="font-weight:bold;color:#000099">' + match[0] + '</span>';
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

var LdapSearcher = Class.create (DirectorySearcher, {
	initialize: function () {
		
		this.resultsTarget = $('ldap-search-results');
		this.displayElement = $('ldap-display');
		this.searchButton = $('ldapSearchButton');
		this.clearButton = $('ldapClearButton');
		this.searchTriggers = ['ldapSearchString', 'ldapSearchField']
		
		this.initListeners();
	},
	
	search: function (evnt) {
		evnt.stop(); // IS THIS NECESSARY?
		var button = evnt.element();
	
		var form = $(document.forms['userForm']);
		if (!form)
			throw ("userform not found");
		
		var searchString = $F('ldapSearchString');
		var ldapField = $F('ldapSearchField');
		log ("ldapField: " + ldapField);
		if (!ldapField) {
			alert ("please select an ldap field to search over");
			$('ldapSearchField').focus();
			return;
		}
		var params = {
			'command' : 'ldapUserInfo',
			'searchString' : '*' + searchString + '*',
			'ldapField' : ldapField
			}
	
		new Ajax.Request ('userInfo.do', {
			parameters: params,
			onSuccess: function (transport) {
					var response = transport.responseText;
					try {
						var json = response.evalJSON();
					} catch (error) { 
						log ("Error evaluating JSON: %s", error);
						return;
					}
					button.blur();
					try {
						this.updateDisplay (json, searchString, ldapField);
					} catch (error) {
						log ("display error: " + error);
					}
			}.bind(this),
			onFailure: function() { 
				log ('Something went wrong...') 
			}
		});		
	},
	
	updateDisplay: function (json, query, field) {
		log ("display with field = " + field + '  query = ' + query);
		var target = this.resultsTarget;
		target.update();
		var tbody = new Element ('tbody');
		target.appendChild (tbody);
			
		// columns :(value/label)
		var columns = $A([['uid','username'], ['name','fullname'], ['email','email']]);
		
		// make header row
		var headerrow = new Element ('tr');
		tbody.appendChild (headerrow);
		columns.each (function (col) { 
			headerrow.appendChild (new Element ('th').update (
				new Element('div').update(col[1])));
		});
		
		var results = this.getResults(json);
		
		if (!results) {
			log ("nothing found");
			var row = new Element ('tr');
			var cell = new Element ('td', {'colspan':columns.size()}).update ("nothing found");
			tbody.appendChild (row);
			row.appendChild (cell);
		}
			
		else {
			// display the results
			results.each (function (entry) {
				var row = new Element ('tr');
				columns.each ( function (col) {
					var col_id = col[0]
					var val = $H(entry).get (col_id);
					if ((col_id == 'name' && field == 'cn') || (col_id == 'uid' && field == 'uid')) {
						// highlight search term
						val = this.highlight (val, query);
					}
					row.appendChild (new Element ('td').update (val));
				tbody.appendChild(row);
				}.bind(this));
			}.bind(this));	
		}
		this.displayElement.show();
	},
	
	/* return an array of results */
	getResults: function (json) {
		
		var results = json.ldapInfo.entry;
		if (!results)
			return null;
		
		if (!results.length)
			return $A([results]);
		
		// sort results by <lastname, remainder>
		return $A(results).sortBy (function (entry) {
			var splits = entry.name.toLowerCase().split (/\s+/);
			var sortVal = splits.pop() + ',' + splits.splice (0, splits.length).join(" ");
			// log (sortVal)
			return sortVal;
		});
	}/* ,
	
	clear: function () {
		$('ldapSearchString').value="";
		$('ldapSearchField').value="";
		this.displayElement.hide();
	} */
	
});

var UcasSearcher = Class.create (DirectorySearcher, {
	initialize: function () {
		
		this.resultsTarget = $('ucas-search-results');
		this.displayElement = $('ucas-display'); 
		this.searchButton = $('ucasSearchButton');
		this.clearButton = $('ucasClearButton');
		this.searchTriggers = ['ucasFirstName', 'ucasLastName']
		
		this.initListeners();
	},
	
	search: function (evnt) {
		evnt.stop();
		var button = evnt.element();
		
		var params = {
			'command' : 'peopleSearch',
			'lastName' : $F('ucasLastName'),
			'firstName' : $F('ucasFirstName')
			}
		
	
		new Ajax.Request ('userInfo.do', {
			parameters: params,
			onSuccess: function (transport) {
					var response = transport.responseText;
					button.blur();
					try {
						var json = response.evalJSON();
					} catch (error) { 
						log ("Error evaluating JSON: %s", error);
						json = [];
					}
					
					try {
						this.updateDisplay (json, params['firstName'], params['lastName']);
					} catch (error) {
						log ("display error: " + error);
					}
			}.bind(this),
			onFailure: function() { 
				log ('Something went wrong...') 
			}
		});		
	},
	
	updateDisplay: function (json, fnq, lnq) {
		var target = $("ucas-search-results");
		target.update();
		var tbody = new Element ('tbody');
		target.appendChild (tbody);
		
			// make header row 
			
			// columns :(value/label)
			var columns = $A([
				['firstName', 'first name'], 
				/* ['middleName', 'm'], */ 
				['lastName', 'last name'],
				['email', 'email']
			]);
			
			var headerrow = new Element ('tr');
			tbody.appendChild (headerrow);
			columns.each (function (col) { 
				headerrow.appendChild (new Element ('th').update (
					new Element ('div').update(col[1])));
			});
		
		var results = this.getResults (json);
		
		if (!results || results.size() == 0) {
			log ("nothing found");
			var row = new Element ('tr');
			var cell = new Element ('td', {'colspan':columns.size()}).update ("nothing found");
			tbody.appendChild (row);
			row.appendChild (cell);
		}
			
		else {
			results.each (function (entry) {
				var row = new Element ('tr');
				columns.each ( function (col) {
					var col_id = col[0];
					var value = "foo"
					var value = $H(entry).get (col_id) || "&nbsp";
					if (col_id == 'firstName' && fnq)
						value = this.highlight (value, fnq);
					if (col_id == 'lastName') {
						value = this.highlight (value, lnq);
					}
					row.appendChild (new Element ('td').update (value));
				tbody.appendChild(row);
				}.bind(this));
			}.bind(this));
				
		}
		this.displayElement.show();	

	},
	
	getResults: function (json) {
		return $A(json).sortBy (function (entry) {
			return entry.lastName + entry.firstName;
		});
	}
});






