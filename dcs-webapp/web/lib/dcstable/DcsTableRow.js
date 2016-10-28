
// rows must have properties for at least all the sortKeys!
var DcsTableRow = Class.create ({
	_className : "DcsTableRow",
			
	initialize: function (table, key, props) {

		this.table = table;
		this.key = key;
		this.props = $H(props);
		this.cells = $A();
		this.render();
	},
		
	getProp: function (prop) {
		return this.props.get (prop);
	},
	
	showProps: function () {
		log ("props for row " + this.key);
		this.props.each ( function (pair) {
			log (pair.key + ": " + pair.value);
		})
	},
	
	makeElement: function () {
		var element = new Element ('tr');
		element.addClassName ('dcs-table-row');
		return element;
	},
	
 	mouseOverHandler: function (event) {
		this.element.addClassName ("over");
	},

	mouseOutHandler: function (event) {
		this.element.removeClassName ("over");
 	},
	
	decorate: function () {
		this.boundMouseOverHandler = this.mouseOverHandler.bindAsEventListener(this);
		this.boundMouseOutHandler = this.mouseOutHandler.bindAsEventListener(this);
	
		this.element.observe ('mouseover', this.boundMouseOverHandler);
		this.element.observe ('mouseout', this.boundMouseOutHandler);
	},
	
	addCell: function (cell) {
		this.cells.push (cell);
		this.element.appendChild (cell.element);
		return cell;
	},
	
	getCell: function (key) {
		return this.cells.find ( function (cell) {
			return cell.key == key;
		});
	},
	report: function () {
		log ("row report");
		this.cells.each ( function (cell) {
			log (cell.key);
		});
	},
	
 	getHeaderCell: function (key) {
		return this.table.headerRow.getCell (key);
	},
	
	render: function () {
		this.element = this.makeElement();
		this.decorate();
		this.table.colProps.each (function (pair, i) {
			this.addCell (new DcsTableCell (this, pair.key, i));
		}.bind (this));
	},
	
	redraw: function () {
		this.element.update();
		this.cells.each ( function (cell) {
			cell.render();
			this.element.appendChild (cell.element);
		}.bind(this));
	}
});

var DcsTableHeaderRow = Class.create (DcsTableRow, {
	_className : "DcsTableRow",
	
	makeElement: function () {
		var element = new Element ('tr');  // not in IE!
		element.addClassName ('header-row');
		return element;
	},
		
	render: function () {
		this.element = this.makeElement();
		this.decorate();
		this.table.colProps.each (function (pair, i) {
			this.addCell (new DcsTableHeaderCell (this, pair.key, i));
		}.bind (this));
	}
});

