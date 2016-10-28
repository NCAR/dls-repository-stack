var filterWidget = null;

/* TableFilter can be disabled by initParam, so we short circuit initialize if the
	filter is disabled
*/
var TableFilter = Class.create ({
	initialize: function (id, filteredTable, filterColumns, filterSpec) {
		this.element = $(id);
		if (!this.element) {
			// alert ("collections filter widget not found");
			return;
		}
		this.filteredTable = filteredTable;
		/*
			the table that we are filtering has to be able to access this object
			so that click handlers can use the filter spec.
		*/
		
		this.filteredTable.tableFilter = this;
		this.filterColumns = $A(filterColumns);
		this.filterColumn = this.filterColumns.first()
		this.trigger = this.element.down("img#filter-trigger");
		this.input = this.element.down("input#filter-input")
		this.menu = new FilterMenu (this);
		
		// initialize from filterSpec
		if (filterSpec) {
			this.input.value = filterSpec.value;
			if (filterSpec.column) // if the column is not specified, use the default
				this.filterColumn = filterSpec.column;
		}
		
		// log ("filterColumn: " + this.filterColumn + "  value: " + this.input.value);
		
		this.updatePrompt();
		
		this.input.observe ('focus', function (event) {
			this.input.observe ('keyup', this.handleKeyUp.bindAsEventListener (this));
		}.bind (this));
		
		this.input.observe ('blur', function (event) {
			this.input.stopObserving ('keyup');
		}.bind (this));
		
		if ($('filter-clear')) {
			$('filter-clear').observe ('click', function (event) {
				this.input.value = "";
				this.filteredTable.filter ("", this.filterColumn);
			}.bind (this));
		}
		if (filterSpec)
			this.filter();
		// log ('initialized');
	},

	getColumnLabel: function (key) {
		return this.filteredTable.getHeaderCell(key).text;
	},
	
/*  	updateWidgetMenu: function () {
		this.filterColumn
	}, */
	
	filter: function () {
		// log ("filter: column: " + this.filterColumn + "  value: " + this.input.value);
		try {
			this.filteredTable.filter (this.input.value, this.filterColumn);
		} catch (error) {
			log ("filter error: " + error)
		}
	},
	
	handleKeyUp:  function (event) {
		this.filter();
	},
	
	updatePrompt: function () {
		var prompt = "Filter"
		if (this.filterColumn) {
			prompt += " " + this.getColumnLabel (this.filterColumn);
		}
		$('filter-prompt').update (prompt);
		if (this.menu)
			this.menu.markSelected();
		else
			log ("this.menu not found!");
	},
	
	getFilterSpec: function () {
		return 	{
			fwcol:this.filterColumn,
			fwval:this.input.value
		};
	}

});

function boundHideFilterMenu () {
	filterWidget.menu.hide();
}

/* the drop down presented by the filterWidget */
var FilterMenu = Class.create( {
	initialize: function (widget) {
		this.widget = widget;
		this.element = this.widget.element.down("div#filter-menu");
		this.choices = $A();
		this.widget.filterColumns.each (function (choice) {
			this.addChoice (choice);
		}.bind(this));
		this.hide();
	},
	
	hide: function (event) {
		if (event) {
			event.stop();
		}
		this.element.hide();
		document.stopObserving ('click', boundHideFilterMenu);
		this.widget.trigger.observe ('click', this.show.bindAsEventListener(this));
	},
	
	show: function (event) {
		if (event) event.stop();
		this.element.show();
		document.observe ('click', boundHideFilterMenu);
		this.widget.trigger.stopObserving ('click');
	},
	
	addChoice: function (key) {
		var label = this.widget.getColumnLabel(key)
		
		var ch = new Element ("li").update(label)
		ch.addClassName ("menu-choice");
		
		ch.writeAttribute ("id", key);
							 
		ch.observe ('mouseover', function (e) {
			ch.addClassName ("over");
		});
		ch.observe ('mouseout', function (e) {
			ch.removeClassName ("over");
		});
		
		ch.observe ('click', function (e) {
			this.widget.filterColumn = key;
			this.widget.updatePrompt();
			this.widget.filter();
			this.widget.input.focus();
		}.bind(this));
		
		this.choices.push (ch);
		this.element.insert (ch);
	},
	markSelected: function () {
		this.choices.each ( function (choice) {
			if (choice.id == this.widget.filterColumn) {
				log (choice.id + " disc");
				// choice.setStyle ({'listStyleType':'disc', 'padding':'0px 0px 0px 0px'});
				choice.addClassName ("selected");
			}
			else {
				log (choice.id + " none");
				// choice.setStyle ({'listStyleType':'none','padding-left':'0px 0px 0px 25px'});
				choice.removeClassName ("selected");
			}
		}.bind(this));
	}
});

