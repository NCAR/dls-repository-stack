
var DcsTable = Class.create ({
	_className : "DcsTable",
	
	initialize: function (config) {
		// log ('init');
		this.images = new ImageManager (config.imagePath);
		this.parent = $(config.parentId);
		this.defaultActionPath = config.defaultPath;
		
		// configs
		this.colProps = $H(config.columnProperties);
		this.rowProps = $H(config.rowProperties);
				
		// row order init
		this.colKeys = this.colProps.keys()
		this.sortKey = this.colKeys[0];  // pluck first column as default
		this.sortOrder = 0;
	
		// Place holders for content
		this.headerRow = null;
		this.rows = $A();
		
		// Why is this here??
		this.cols = $A();
		
		// DOM structure
		this.tbody = null;
		this.element = this.render();
		
		this.tableFilter = null;
	},
	
	getColumnKeys : function () {
		return this.colProps.keys();
	},
	
	render: function () {
		// log ("DcsTable.render()");
		this.element = this.makeElement();
		this.parent.appendChild (this.element); // i think prototype "insert" has bad results?
		
		// this.headerRow = this.addHeaderRow (new DcsTableHeaderRow (this, 'header'));
		this.headerRow = this.addHeaderRow (this.createTableHeaderRow());
		
		this.rowProps.each (function (pair) {
			var myRowKey = pair.key;
			var myRowProps = pair.value;
			/* this.addRow (new DcsTableRow (this, myRowKey, myRowProps)); */
			this.addRow (this.createTableRow (myRowKey, myRowProps));
		}.bind (this));
		return this.element;
	},
	
	createTableHeaderRow: function () {
		return new DcsTableHeaderRow (this, 'header');
	},
	
	createTableRow: function (rowKey, rowProps) {
		return new DcsTableRow (this, rowKey, rowProps);
	},
	
	/* SIDE-EFFECT - this.tbody is initialized */
	makeElement: function () {
		var element = new Element ('table', {id:"dcs-table"});
		this.tbody = new Element ('tbody');
		element.appendChild (this.tbody);
		return element;
	},
	
	addRow: function (row) {
		this.rows.push (row);
		this.tbody.appendChild (row.element);
		return row;
	},
	
 	addHeaderRow: function (headerRow) {
		this.headerRow = headerRow;
		this.tbody.appendChild (headerRow.element);
		return headerRow;
	},
	
	getRow: function (key) {
		return this.rows.find ( function (row) {
			return row.key = key;
		});
	},

	getHeaderCell: function (key) {
		return this.headerRow.getCell(key);
	},
	
	sortRows: function (sortKey, sortOrder) {
		if (sortKey == this.sortKey) {
			if (sortOrder != null) {
				this.sortOrder = sortOrder % 2;
			}
			else {
				this.sortOrder = (this.sortOrder + 1) % 2;
			}
		}
		else {
			this.sortKey = sortKey;
		}
		
		// Build a sortable structure
 		var sortList = $A();
		this.rows.inject ( sortList, function (acc, row) {
			var value = row.getCell (sortKey).value;
			acc.push ( [value, row.element] );
			return acc;
		}); 

		if (typeof (sortList[0][0]) == "number") {
			sortList.sort (function (a, b) {
				// return (a[0] - b[0]); // this sorts in ascending order
				return (b[0] - a[0]); // descending order
			});
		}
		else {
			// sortList.sort();
			sortList = sortList.sortBy (function (s) { return s[0].toLowerCase() } );
		}
		
		if (this.sortOrder == 1) {
			sortList.reverse();
		}
		
		// REDRAW table
		// redraw header row to reflect current sort
		this.headerRow.redraw();
		
		// add elements in new order
		sortList.each ( function (item) {
			var rowElement = item[1];
			this.tbody.appendChild (rowElement); //  appendChild removes existing element before adding
		}.bind (this));
	},
		
	filter: function (filter_string, filter_column) {
		
		// log ("filter: " + filter_string + " (" + filter_column + ")");
		filter_string = filter_string.toLowerCase();
		// asterisks are wildcards
		filter_string = filter_string.sub (/\*/, ".*?", 10);
		var filter_re = null;
		var matchAnywhere = true;
		if (matchAnywhere) {
			// match anywhere
			filter_re = new RegExp (filter_string);
		}
		else {
			// Regular Expression - match at beginning of line
			filter_re = new RegExp ("^" + filter_string);
		}

		this.rows.each (function (row) {
			var val = row.getCell(filter_column).value;

			if (filter_re.test(val.toLowerCase())) {
				row.element.show();
			}
			else {
				row.element.hide();
			}
				
		}.bind(this));
	},
	
	report: function () {
		log ("dscTable report");
		this.rows.each ( function (row) {
			log ("name: " + row.getProp('name') + ", key: " + row.key);
		});
	},
	
	reportColumns: function () {
		log ("tableHeaderCells");
		this.headerRow.cells.each ( function (cell) {
			cell.report();
		});
	},
	
 	highlightRow: function (matchkeys) {

		if (typeof(matchkeys) == "string")
			matchkeys = [matchkeys]
		
		var isCurrent = function (row) {
			var ret = $A(matchkeys).find (function (key) {
				return (key == row.key);
			});

			return (ret != null)
		};
		
		this.rows.each (function (row) {
			if (isCurrent (row)) {
				row.element.addClassName ("highlight");
			}
			else {
				row.element.removeClassName ("over");
				row.element.removeClassName ("highlight");	
			}
		});
	},
	getUpArrow: function () {
		return this.images.getUpArrow();
	},
	getDownArrow: function () {
		return this.images.getDownArrow();
	},
	getBlankArrow: function () {
		return this.images.getBlankArrow();
	}
});



