
var StandardsTableFilter = Class.create ( {
	initialize: function (input, table) {
		this.input = $(input);
		this.table = table;
		myMatch = /(.*)-filter/.exec (this.input.identify());
		if (!myMatch) {
			log ("could not determine filterBy from " + this.input.identify())
		}
		this.filterBy = myMatch[1];
		
		this.matchBeginning = false; // match only beginning of lines
		this.input.observe ('focus', function (event) {
			this.input.observe ('keyup', this.handleKeyUp.bindAsEventListener (this));
		}.bind (this));
		
		this.input.observe ('blur', function (event) {
			this.input.stopObserving ('keyup');
		}.bind (this));
		
		if ($('filter-clear')) {
			$('filter-clear').observe ('click', function (event) {
				this.input.value = "";
				this.filter ();
				this.input.focus();
			}.bind (this));
		}
	},
	
	clear: function () {
		this.input.value = '';
	},
	
	handleKeyUp:  function (event) {
		this.filter();
	},
	
	filter: function () {
		try {
			// this.table.filter (this.filterBy, this.input.value);
			
			// multi-filter filtering
			this.table.filter ();
		} catch (error) {
			log ("filter error: " + error)
		}
	},		
	
	acceptRow: function (row) {
		var value = row.down('td.'+this.filterBy).innerHTML;
		return this.accept(value);
	},
	
	/* does the filter-input match provided value?
		the predicate for filtering  */
	accept: function (value) {
		var filter_input_value = this.input.value;
		//log ("filter: " + this.filterBy + ", " + filter_input_value);
		filter_input_value = filter_input_value.toLowerCase();
		// asterisks are wildcards
		filter_input_value = filter_input_value.sub (/\*/, ".*?", 10);
		var accept_regEx = null;
		if (this.matchBeginning) {
			// Regular Expression - match at beginning of line
			accept_regEx = new RegExp ("^" + filter_input_value);
		}
		else {
			// match anywhere
			accept_regEx = new RegExp (filter_input_value);
		}
		return accept_regEx.test(value.toLowerCase());
	}
});

var StandardsTable = Class.create ( {
	initialize: function (table) {
		this.table = $(table); // dom
		this.filters = $A([]);
		this.junction = this.table.down('tbody')
		if (!this.junction)
			throw ("junction not found")
		this.sortSpec = null;
	},
	
	getRows: function () {
		return this.table.select('tr').inject ([], function (acc, row, index) {
			if (index > 2)  // skip header, filter button and filters
				acc.push(row);
			return acc;
		});
	},
	
	sort: function (sortBy) {
		log ("sorting by " + sortBy);
		
		var sortedRows = null;
		if (sortBy == this.sortSpec) {
			log (" .. reversing");
			sortedRows = this.getRows();
			sortedRows.reverse()
		}
		else {
			sortedRows = this.getRows().sortBy (function (row) {
				 var sortCell = row.down('td.'+sortBy);
				 return sortCell ? sortCell.innerHTML : '';
			});
		
			if (sortBy == 'created') {
				sortedRows.reverse ();
			}
		}
		
		sortedRows.each (function (row) {
			this.junction.appendChild(row);
		}.bind(this));
		
		this.sortSpec = sortBy;
	},
	
	clearFilters: function () {
		log ('clear filters');
		this.filters.each (function (filter) { filter.clear() });
		this.filter();
	},
	
	/* filter by determines the cell we pull the value from */
	filter: function () {
		log ("NEW FILTER");
		this.getRows().each (function (row) {
/* 			if (this.acceptRow (row)) {
			// if (filter_re.test(val.toLowerCase())) { 
				row.show();
			}
			else {
				row.hide();
			} */
			
			this.accept(row) ? row.show() : row.hide();
				
		}.bind(this));
	},
	
	accept: function (row) {
		var failed = this.filters.detect (function (filter) {
			if (!filter.acceptRow(row))
				return true;
		});
		return failed ? false : true;
	},
		
	
	showAll: function () {
		this.getRows().each (function (row) {
			row = $(row);
			row.show();
		});
	},
	
	showSummary: function () {
		this.getRows().each (function (row) {
			row = $(row);
			var checkbox = row.down('td.selected').down('input');
			
			//  show only selected
			// if (checkbox.checked)
			
			// show extra docs, too
			if (checkbox.checked || row.hasClassName ("extra-doc") || row.hasClassName("registered-doc"))
				row.show();
			else
				row.hide();
		});
	},
	
	countInteresting: function () {
		log ("countInteresting");
		var count = 0;
		this.getRows().each (function (row) {
			var checkbox = row.down('td.selected').down('input');

			if (checkbox.checked || row.hasClassName ("extra-doc") || row.hasClassName("registered-doc"))
				count ++;
		});
		return count;
	}
	
	/* ,
	
	filterBySelected: function (input) {
		log ("filterBySelected(): " + $F(input));
		var show_all = $F(input);
		if (!show_all) {
			log ("showing all");
			this.showAll();
		}
		else {
			log ("showing only selected");
			this.showSummary();
		}
	} */

		
});


