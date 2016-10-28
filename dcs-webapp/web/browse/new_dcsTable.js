
var DcsTable = Class.create( {
	initialize: function (element, sets, columns, contextPath) {
		log ("instantiating DcsTable");
		this.element = $(element);
		
		this.tableFilter = null;
		
		this.sets = sets;
		this.columns = columns;
		this.contextPath = contextPath;
		this.imageManager = new ImageManager (this.contextPath + '/images');
		
		this.sortColumn = 'name'; // default
		this.sortOrderProp = ASCENDING;
		
		this.headerCells = this.getHeaderCells();
		this.dataRows = this.getDataRowElements();
		
		this.decorate();
	},
	
	/* debugging */
	showColumns: function  () {
		log ("columns");
		this.columns.each (function (colProps, index) {
			log (index + ": " + colProps['headerText']);
		});
	},
		

/* 	getHeaderLabel: function (cellElement) {
		var id = $(cellElement).identify();
		return id.substring (0, (id.length-"_col".length));
	}, */

	getDataRowElements: function () {
		return this.element.select('tr').inject ([], function (acc, row, index) {
			if (index > 0)
				acc.push(row);
			return acc;
		});
	},

	/* add styling and event handlers to each cell in the table */
	decorate: function () {
		log ("decorateTable()");
		var tics = new Date().getTime();
		
		this.decorateHeader();
		
		// decorate all the content cells
		this.dataRows.each (function (row) {
			var setSpec = row.identify();
			row.observe ('mouseover', function (event) {
				row.addClassName ("over");
			});
			
			row.observe ('mouseout', function (event) {
				row.removeClassName ("over");
			});		
			
			row.childElements().each (function (cell, cellIndex) {
				this.decorateCell (setSpec, cell, cellIndex);
			}.bind(this));
		}.bind(this));
		var elapsed = new Date().getTime() - tics;
		log ("table decorated in " + (elapsed / 1000) + " secs");
	},

	/* decorate the table header */
	decorateHeader: function () {
		this.headerCells.each ( function (cell, index) {
			var id = cell.identify();
			var col = this.columns[index];
			
			// initialize the sortwidgets
			if (col['sortBy']) {
				cell.addClassName ('clickable');
				cell.observe ('click', this.sort.bind(this));
				if (this.sortColumn == col['sortBy']) {
					cell.insert (this.imageManager.getDownArrow());
				}
				else {
					cell.insert (this.imageManager.getBlankArrow());
				}
			}
		}.bind(this));
	},
	
	/* decorate a single cell (not a headerCell) in the table */
	decorateCell: function (setSpec, cell, cellIndex) {
		var col = this.columns[cellIndex];
		if (cellIndex == 0) {
			try {
				if (this.sets.get(setSpec)['authority'] == 'ndr') {
					cell.setStyle ({
						backgroundImage:"url('" + this.imageManager.getNdrDogEarSrc() + "')",
						backgroundRepeat:'no-repeat'
					});	
				}
			} catch (error) {
				log ("ERROR: " + error);
			}
		} else {
			cell.setAttribute('align', 'center');
		}
		if (col['onclick'] && cell.down('a').innerHTML != '0') {
			cell.addClassName ('clickable');
			cell.observe ('click', eval("this."+col['onclick']).bind(this));
			cell.observe ('mouseover', function (event) {
				cell.addClassName ('over');
			});
			cell.observe ('mouseout', function (event) {
				cell.removeClassName('over');
				cell.removeClassName('selected');
			});
			cell.observe ('mouseup', function (event) {
				cell.removeClassName ('selected');
			});
			cell.observe ('mousedown', function (event) {
				cell.addClassName ('selected');
			});
		}
	},
	
	getHeaderCells: function() {
		return this.element.down('tr.header-row').childElements();
	},

	// ----------- filter handler --------------------
	
	filter: function (filter_string, column) {
		
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

		this.dataRows.each (function (row) {
			var id = row.identify();
			var val = this.sets.get(id)[column];

			if (filter_re.test(val.toLowerCase())) {
				row.show();
			}
			else {
				row.hide();
			}
				
		}.bind(this));
	},
	
	// ---------- click handlers for table ------------------

	listAllRecords: function (event) {
		var setSpec = event.findElement('tr').identify();
		log ("setspec: " + setSpec);
		var path = this.contextPath + '/browse/query.do';
		var params = {
					scs:"0"+ setSpec,
					s:"0", 
					q:"" 
				};
		window.location = createUrl (path, params);
	},

	listNotValidRecords: function (event) {
		var setSpec = event.findElement('tr').identify();
		var path = this.contextPath + '/browse/query.do';
		var params = {
				s:"0", 
				q:"", 
				scs:"0"+ setSpec,
				vld: "notvalid"
			}
		window.location = createUrl (path, params);
	},

	createRecord: function (event) {
		var setSpec = event.findElement('tr').identify();
		var format = this.sets.get(setSpec)['format'];
		var path;
		var params;
		log ("createRecord: " + setSpec + " (" + format + ")");
		if (format == 'adn') {
			path = this.contextPath + '/record_op/adn.do'
			params = {
				command : 'new',
				collection : setSpec
			}
		}
		else if (format == 'mast') {
			path = this.contextPath + '/record_op/mast.do';
			params = {
				command: 'new',
				collection : setSpec
			}
		}
		else {
			path = this.contextPath + '/record_op/single.do';
			params = {
				command : 'newRecord',
				collection : setSpec,
				xmlFormat : format
			}
		}
	/* 	url = path;
		if (params)
			url += "?" + $H(params).toQueryString(); 
		window.location = url; */
		window.location = createUrl (path, params);
	},

	sort: function (event) {
		var headerCell = event.findElement('td');
		var index = this.headerCells.indexOf(headerCell);
		var col = this.columns[index];
		
		var sortBy = col['sortBy']
		var sortOrder = col['sortOrder']
		
		var rows = this.dataRows;
		
		if (sortBy == this.sortColumn) {
			this.sortOrderProp = this.sortOrderProp == ASCENDING ? 
											 DESCENDING : 
											 ASCENDING;
			rows = rows.reverse();
			headerCell.down('img').src = this.sortOrderProp == ASCENDING ?
														  this.imageManager.downArrow.src :
														  this.imageManager.upArrow.src;
		}
		else {
			this.sortColumn = sortBy;
			this.sortOrderProp = sortOrder;
			rows = rows.sortBy (function (row) {
				var id = row.identify();
				var sortVal = this.sets.get(id)[sortBy];
				sortVal = this.sortOrderProp == DESCENDING ? 
										   -parseInt(sortVal) : 
										   sortVal;
				return sortVal
			}.bind(this));
			
			// update the sort widgets in the header cells
			this.headerCells.each (function (hdrCell, index) {
				var colProps = this.columns[index];
				if (hdrCell == headerCell) {
					hdrCell.down('img').src = this.sortOrderProp == ASCENDING ?
															   this.imageManager.downArrow.src :
															   this.imageManager.upArrow.src;
				  }
				  else if (colProps['sortBy']) {
					 hdrCell.down('img').src = this.imageManager.blankArrow.src;
				 }
			}.bind(this));			
		}
		
		// redraw rows - appendChild detaches the node from parent before appending to childElements
		var tableBody = this.element.down('tbody');
		rows.each (function (row) {
			tableBody.appendChild(row);
		});
	}
});

