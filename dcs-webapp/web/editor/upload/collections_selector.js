/** 
collections_selector

CollectionSelector - provides a select mechanism to specify which collections are searched for asset selector

we have global acess to 
- COLLECTIONS
- ASSET_MANAGER

attributes

api
 - open
 - close
 - populate
 - toggle
*/

var CollectionsSelector = Class.create ({
	initialize: function (selectTool) {
		log ("CollectionsSelector");
		this.controller = selectTool;
		this.populate();
		
		$('collections-toggle').observe ('click', this.toggle.bind(this));
		
		if ($('file-select-collections')) {
			$('file-select-collections')
				.down('.closer')
				.observe('click', this.close.bind(this));
		}
		this.escapeHandler = document.on('keyup', this.handleKeyUp.bind(this));
	},

	/**
	
	@method open
	*/
	open: function (event) {
		log ("open");	
		var button = event.findElement(".ui-button");
		// log ("- button: " + button.innerHTML);
		var layout = button.getLayout();
		var positioned_offset = button.positionedOffset();
		var botton_top = positioned_offset.top;
		var button_bottom = layout.get('top') + layout.get('margin-box-height');
		// log ("button_top: " + layout.get('top') + ',  height: ' + layout.get('height'));
		
		// log ("button_top: " + botton_top + ',  height: ' + layout.get('margin-height'));
		log (" - button_bottom: " + button_bottom);
		
		$('file-select-collections').setStyle({
				top:37 + 'px' // hard-coded to bottom of button - why can't i calculate this?
		});
		
		$('collections-toggle').addClassName("active");
		$('coll-toggle-icon')
			.removeClassName('fa-caret-right')
			.addClassName('fa-caret-down');
		ASSET_MANAGER.getModalBackground().show();
		ASSET_MANAGER.escapeHandler.stop();
		this.escapeHandler.start();
		$('file-select-collections').appear({duration:0.3});
	},

	/**
	
	@method close
	*/
	close: function (event) {
		log ("close");
		this.escapeHandler.stop();
		ASSET_MANAGER.escapeHandler.start();
		$('collections-toggle').removeClassName("active");
		$('coll-toggle-icon')
			.removeClassName('fa-caret-down')
			.addClassName('fa-caret-right');
		ASSET_MANAGER.getModalBackground().fade({duration:0.3});
		$('file-select-collections').hide();
	},

	toggle: function (event) {
		if ($('file-select-collections').visible()) {
			this.close(event);
		}
		else {
			this.open(event);
		}
	},
	
	handleKeyUp: function (event) {
		// log ("Collections_selector: KEYUP");
		if (event.keyCode && event.keyCode == 27) {
			this.close();
			event.stop();
		}
	},
	
	getSelected: function () {
		return this.controller.selected_collections;
	},
	
	/**
	
	@method populateCollectionsSelect
	*/
	populate: function () {
		log ("populateCollectionsSelect()");
		var target = $('collections_list');
		
		target.update();
		log (' - there are ' + COLLECTIONS.length + ' collections');
		
		if (false) {
			log ("selected_collections: " + this.getSelected() +
					" (" + typeof(this.getSelected()) + ")");
			if (Object.isArray(this.getSelected())) {
				this.getSelected().each (function (col) {
						log (" - " + col);
				});
			}
		}
		
		var sortedCollections = COLLECTIONS.sortBy(function (item) {
				return item.name.toLowerCase();
			});
		
		
		// we insert an ALL checkbox at the top of the list
		sortedCollections.unshift ({
				key: '__all__',
				name: 'All',
				xmlFormat: 'all'
			});
		
		
		log ("Processing sorted Collections ...");
		
		sortedCollections.each(function (info) {

			try {
				var isSelected = (this.getSelected() == info.key || this.getSelected().indexOf(info.key) != -1);
			} catch (error) {
				log ("isSelected ERROR: " + error);
			}
			// log (' - selected: ' + this.getSelected());
			// log (' - info.key' + info.key + " (isSelected ? " + isSelected + ")");

			
			var checkbox = new Element('input', {
						type:'checkbox', 
						name:'selected-collections',
						id:info.key,
						value:info.key,
						checked:isSelected
			});
			
			// var tooltip = isSelected ? "Unselect " + info.key : "Select" + info.key
			var tooltip = info.key; 
			checkbox.writeAttribute ("title", tooltip);
			
 			checkbox.observe ('change', function (event) {
				var selector = "input[type='checkbox']";
				var checkId = event.element().identify()
				var selected = [];
				log ('CLICKED: ' + checkId);
				if (checkId == '__all__') {
					selected = 
						$('collections_list').select(selector).inject ([], function (acc, box) {
							if (box.identify() == '__all__') {
								// box.writeAttribute('checked', 'checked');
								box.checked = true;
								
							}
							else {
								box.checked = false
								acc.push(box.value);
							}
							return acc;
						});
				}
				else {
					selected = 
						$('collections_list').select(selector).inject ([], function (acc, box) {
							if (box.identify() == '__all__') {
								box.checked = false;
							}
							else if (box.checked)
								acc.push(box.value);
							return acc;
						});
				}
				
				this.controller.selected_collections = selected;
				log (" - " + this.controller.selected_collections.length + " this.controller.selected_collections")
				this.controller.update();
			}.bind(this));

			var label = new Element('label', {for:info.key})
				// .setStyle({display:'block'})
				.update (info.name)
				.writeAttribute ("title", tooltip);
				
			var item = new Element('li').insert(checkbox).insert(label)
			if (info.key == '__all__')
				item.addClassName ('__all__');
			
/* 			if (info.key == 'pbis-tips') {
				log (' - this one: \"' + info.key + "\" (" + this.getSelected().indexOf(info.key) + ")");
				if (Object.isArray(this.getSelected())) {
					log ( 'selected is an ARRAY: ' + this.getSelected().toString());
					this.getSelected().each (function (col) {
						log (" - \"" + col + "\"");
					});
				}
			} */
			// log (" - selected: " + this.getSelected());
			
			if (this.getSelected() == info.key || this.getSelected().indexOf(info.key) != -1)
				item.setStyle({fontWeight:'bold'});
			target.insert(item);
		}.bind(this));
	}
});
