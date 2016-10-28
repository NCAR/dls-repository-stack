
var DcsTableCell = Class.create ({
	_className : "DcsTableCell",
	initialize: function (row, key, colNum) {
		this.row = row;
		this.key = key;
		this.colNum = colNum;
		this.cellSpec = this.row.getProp('name') + " - " + this.key;
		this.myColProps = this.row.table.colProps.get(key);
		
		this.hdrCell = this.row.getHeaderCell (this.key);
		
		if (!this.hdrCell) {
			var msg =  "header cell not found for " + this.cellSpec;
			throw msg;
		}

		this.value = this.getValue();
		this.render();
		
	},
	
	getColProp: function (prop) {
		return this.myColProps[prop];
	},
	
	getName: function () {
		return  this.row.key + " - " + this.key;
	},
	getValue: function () {
		if (!this.hdrCell) {
			throw "header cell not found for " + this.cellSpec;
		}
		var fn = this.hdrCell.valueFunction
		if (!fn) {
			log ("valueFunction not found for " + this.cellSpec);
			return "ERROR"
		}
		return fn (this.row);
	},
		
	render: function () {
		this.element = this.makeElement();
		this.decorate();
	},
	
	redraw: function () {
		this.render();
	},
	makeElement: function () {
		
		// this.element = $(this.row.element.insertCell(-1));
		this.element = new Element ('td');
		
		// for some reason we can use setStyles on the element here..
		if (this.colNum > 0)
			this.element.setAttribute ("align", "center");
		
		this.setContent();
		return this.element;
	},
	
	setContent: function () {
		this.element.update (this.value);
		return this.element;
	}, 
	
	decorate: function () {
		// this.element.setStyle ({backgroundColor:'red'});
		
		var className = this.getColProp ('className');
		var idFunction = this.getColProp ('idFunction');
		var title = this.getColProp ('title');
		if (className) 
			this.element.addClassName (className);
		if (idFunction)
			this.element.setStyle ({id:idFunction (this.row)});
		if (title)
			this.element.setAttribute ('title', title);
		
		this.initHandlers ();

		return this.element;
	},
	
	initHandlers: function () {
		var hasClickHandler = false;
		var clickHandler = this.getColProp ("clickHandler");
		var queryStringFunction = this.getColProp ('queryFunction');
		
		if (clickHandler) {
			// this wrap is no longer used for anything, but left as an example of how to do it
			this.element.observe ('click', clickHandler.wrap (
				function (proceed, event) {
					// this.element.addClassName('selected');
					proceed(event);
				}).bindAsEventListener (this));
			hasClickHandler = true;
		}
		else if (queryStringFunction) {
			// log ("queryStringFunction for " + this.key + ": " + queryStringFunction);
			if (this.value && this.value != "0") {
				
				this.element.observe ('click', this.clickHandler.bindAsEventListener(this));
				hasClickHandler = true;
				
				// show the query for shift-click (even though we trap it with 'dblclick')??
				this.element.observe ('dblclick', function (event) {
					var params = queryStringFunction (this.row)
					log ($H(params).toQueryString());
				}.bindAsEventListener(this));
			}
		}
			
		if (hasClickHandler && this.value) {
			this.element.observe ("mouseout", this.mouseOutHandler.bindAsEventListener(this));
			this.element.observe ("mouseover", this.mouseOverHandler.bindAsEventListener(this));
			this.element.observe ("mousedown", function (event) {
				this.element.addClassName ("selected");
			}.bindAsEventListener (this));
			this.element.observe ("mouseup", function (event) {
				this.element.removeClassName ("selected");
			}.bindAsEventListener (this));
			
			
			this.element.addClassName ("clickable");
		}
		else {
			// log ("NO clickHandler");
		}
	},
		
	mouseOutHandler: function (event) {
		this.element.removeClassName ("over");
		this.element.removeClassName ("selected");
	},
	
	mouseOverHandler: function (event) {
		this.element.addClassName ("over");
	},
	
	clickHandler: function (event) {
		event.stop();
		var queryStringFunction = this.getColProp ('queryFunction');
		if (queryStringFunction) {
			var path = this.getColProp ('path') || this.row.table.defaultActionPath;
/* 			var url = path + "?" + $H(queryStringFunction (this.row)).toQueryString();
			// log ("url: " + url);
			window.location = url; */
			var params = queryStringFunction (this.row);
			var tableFilterSpec = (this.row.table.tableFilter != null ? 
								  this.row.table.tableFilter.getFilterSpec() :
								  null);
			window.location = createUrl (path, params);
		}
		else {
			log ("no queryStringFunction defined for " + this.key);
		}
	},
	
	report: function () {
		log (this.value + "(" + this.jsClassName + ")")
	}
});

var DcsTableHeaderCell = Class.create (DcsTableCell, {
	_className : "DcsTableCell",
	
	// key maps this header cell to it's content cell
	// it also serves as a sortSpec
	initialize: function (row, key, colNum) {
		this.row = row;
		this.myColProps = this.row.table.colProps.get(key);
		this.text = this.myColProps['text'];
		this.valueFunction = this.myColProps ['valueFunction'];
		this.sortable = this.myColProps ['sortable'];
		this.colNum = colNum;
		this.key = key;
		this.render();
	},

	render: function () {
		// log ("DcsTableHeaderCell.render()");
		this.element = this.makeElement();
		this.decorate();
	},
	
	makeElement: function () {

		this.element = new Element ('td');
		this.setContent ();	
		return this.element;
	},
	
	setContent: function () {
		this.element.update(new Element ('span').update (this.text));

	},
	
	decorate: function () {
		if (this.sortable) {
			
			this.element.addClassName ("clickable");
			
			// CLICK_HANDLER
			this.element.observe ('click', this.sortClickHandler.bindAsEventListener (this));

			//  MOUSE_OVER
			this.element.observe ('mouseover', function (event) {
				this.element.addClassName ("over");
			}.bindAsEventListener (this));
			
			//  MOUSE_OUT
			this.element.observe ('mouseout', function (event) {
				this.element.removeClassName ("over");
			}.bindAsEventListener (this));
			
			// SORT WIDGET
			this.setSortWidget();
			
		}
		else {
			// there is no link handler for this cell, we can use it as
			// we wish to experiment with
		}
	},
	
	setSortWidget: function () {
		var img = this.element.down ("img");
		if (img)
			this.element.removeChild (img);
		
		// log (this.key + " (" + this.row.table.sortKey + ")");
		if (this.key == this.row.table.sortKey) {
			
			this.element.setAttribute ("title","reverse row order");
			if (this.row.table.sortOrder == 0) {
				this.element.insert ( this.row.table.getDownArrow(), top);
			}
			else {
				this.element.insert ( this.row.table.getUpArrow(), top );
				
			}
		}
		else {
			this.element.insert (this.row.table.getBlankArrow(), top);
			this.element.setAttribute ("title","sort table by this column");
		}
	},
	
	sortClickHandler: function (event) {
		// log ("SORT :", + this.key);
		this.element.addClassName ("selected");;
		this.row.table.sortRows (this.key);
	},
	
	report: function (prefix)  {
		var s = "";
		if (prefix)
			s += prefix + ": ";
		s += this.jsClassName + ": key: " + this.key;
		s += "  fn: " + this.valueFunction;
		
		log (s);
	}

});

