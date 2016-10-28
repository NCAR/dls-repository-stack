/**
All functionality in this module has access to following globals:

	- COLLECTIONS - initialized in
	- ASSET_MANAGER - initialized in pageInit()

@module upload_helper_script
*/

/**

@class globals
*/

/**
Global variable for AssetManager singleton

@property ASSET_MANAGER
@static
@final
*/
var ASSET_MANAGER = null;
var START = 'start';
var ANYWHERE = 'any';
var SELECTED = 'selected';
var ALL = 'all'

/**
parameters from this pages href, split into a hash

@property PARAMS
@static
@global
*/
var PARAMS = window.location.search.toQueryParams();

/**
Provides a select list API, including update, sort and filter.

Intended to be general, but currently tuned specifically for SelectTool file list

@class SelectList
*/
var SelectList = Class.create ({
	/**
	controller points to, e.g. (SelectTool)
	@method initialize
	*/
	initialize: function (container, searchResultsJson, controller) {
		this.container = $(container);
		this.controller = controller;
		this.searchResultsJson = searchResultsJson;
		this.selected = null;
		this.max_items = 1000;
		this.size = null;
		this.items = [];
		this.matchSpec;
		this.collections_count = -1;
		
		// log ("SelectList: selectTool initialItemKey: " + this.controller.initialItemKey);
		// log (" controller: " + this.controller.getName());
		this.setMatchSpec('start');
		
		$('select-filter').observe ('keyup', this.doFilter.bind(this));
		$('filter-clear').observe('click', this.doClearFilter.bind(this));
		
 		$$('.match-spec-button').each (function (buttonDom) {
			buttonDom.observe ('click', this.handleMatchSpecButtonClick.bind(this));
		}.bind(this));

		// log ("- searchResultsJson: " + JSON.stringify(searchResultsJson, null, 2));		
		if (searchResultsJson) {
			try {
				this.update (searchResultsJson);
				this.focus(this.selected);
			} catch (error) {
				log ("SelectList.update error: " + error)
			}
		}
		else {
			log ("searchResultsJson not found");
			// log ("- searchResultsJson: " + JSON.stringify(searchResultsJson, null, 2));
		}
	},
	
	/**
	Handles click on the match widget
	
	@method handleMatchSpecButtonClick
	*/
	handleMatchSpecButtonClick: function (event) {
		log ("handleMatchSpecButtonClick");
		var id = event.element().identify();
		var matchSpec;
		log (" - id: " + id);
		if (id == 'match-spec-start')
			matchSpec = START;
		else if (id == 'match-spec-any')
			matchSpec = ANYWHERE;
		else
			throw ("could not get matchSpec for " + id);
		
		this.setMatchSpec (matchSpec);
		
	},
	
	/**
	
	@method focus
	*/
	focus: function (itemId) {
		
		function positionItem (item) {
			var item_layout = item.dom.getLayout();
			var item_top = item_layout.get('top');
			var results = $('search_results');
			
			if (!results)
				throw ("results not found");
			
			var results_layout = results.getLayout();
			var results_top = results_layout.get('top');
			var results_height = results_layout.get('height');
			var results_bottom = results_top + results_height;
			
			var scroll_top =  results.scrollTop;
			
			// console.info (" - scrollTop: " + scroll_top);
			// console.info (" - item_top: " + item_top);
			// console.info (" - results_top: " + results_top + ", results_bottom: " + results_bottom);

			if (item_top < scroll_top) {
				results.scrollTop = item_top - 130;
			} else if (item_top > scroll_top + results_height) {
				results.scrollTop = item_top - results_height + 130;
			}
		}
		
		itemId = itemId || this.controller.initialItemKey;
		var item = this.items.find(function (_item) {
					if (_item.getId() == itemId)
						return true;
			});
		
		if (item) {
			this.selectItem (item);
			positionItem (item);
		}
		else {
			log ("WARN - cound not focus: item not found");
		}
	},
	
	/**
	
	@method setMatchSpec	
	*/
	setMatchSpec: function (arg) {
		// log ("setMatchSpec: " + arg)
		var startButton = $('match-spec-start');
		var anywhereButton = $('match-spec-any');
		
		if (arg == START) {
			startButton.addClassName('active');
			anywhereButton.removeClassName('active');
		}
		else if (arg == ANYWHERE) {
			anywhereButton.addClassName('active');
			startButton.removeClassName('active');	
		}
		this.matchSpec = arg;
		this.doFilter();
	},
		
	/**
	
	@method doClearFilter	
	*/
	doClearFilter: function (event) {
		$('select-filter').value = '';
		this.doFilter();
		$('select-filter').focus();
	},
	

	/**
	
	@method createItem
	*/
	createItem: function (itemJson, value) {
		
		return new SelectListItem(itemJson, value, this);
	},
	
	/**
	This sort arranges items that match at the start before those
	that match anywhere. But this requires that a sort is performed
	after each key stroke, which is pretty expensive ...
	@method itemSortFnNEW
	*/
	itemSortFnNEW: function (itemDom) {
		// return item.innerHTML.toLowerCase();
		// Elog (itemDom);
		var filter = $F('select-filter').toLowerCase();
		var term = itemDom.identify().toLowerCase();
		log (" - " + term);
		if (filter != null) {
			if (term.startsWith(filter))
				term = '0' + term;
			else
				term = '1' + term;
		}
		log ("   ... " + term);
		return term;
	},
	
	/**

	@method itemSortFn
	*/
	itemSortFn: function (item) {
		// return item.innerHTML.toLowerCase();
		return item.identify().toLowerCase();
	},
	
	/** 
	look in dom element marked as selected 
	
	@method selectItem
	*/
	selectItem: function (item) {
		// log ("selectItem: " + item.getId());
		this.container.select('li').each (function (li) {
			if (li.identify() == item.getId()) {
				li.addClassName('selected');
				this.selected = item.getId();
			}
			else
				li.removeClassName('selected');
		}.bind(this));
		ASSET_MANAGER.enableInsert()
	},

	/**
	
	@method doFilter
	*/
	doFilter: function (event) {
		// log ("SelectList doFilter()");
		function match(label, filterStr, matchSpec) {
			if (matchSpec == ANYWHERE)
				return label.indexOf(filterStr) != -1;
			if (matchSpec == START)
				return label.startsWith(filterStr);
			return false;
		}
		
		var filter_string = $F('select-filter');
		var emptyFilter = filter_string.strip().empty();
		var shown_count = 0;
		var show_all = emptyFilter;
		
		// dynamic_elements - UI labels and elements that need to be updated after each filter op
		var dynamic_elements = ['filter-status', 'filter-clear', 'match-widget'];
		
		dynamic_elements.each (function (id) {
			var element = $(id);
			if (emptyFilter) {
				if (id == 'filter-status')
					element.hide();
				else
					element.fade({duration: 0.5});
			}
			else {
				if (id == 'filter-status')
					element.show();
				else
					element.appear({duration: 0.5});
			}
		})
		
		// log (" - filtering on " + filter_string);
		this.items.each( function (item) {
			if (show_all || match (item.label.toLowerCase(), 
					   			   filter_string.toLowerCase(),
					   		   	   this.matchSpec)) 
			{
				item.show();
				shown_count++;
			}
			else {
				item.hide();
			}
		}.bind(this));
		
		try {
			$('shown_count').update (shown_count);
			/* log ('showing count total');
			$('shown_count_total').update (this.items.size()); */
		} catch (error) {
			log ("doFilter couldnt update shown_count: " + error)
		}
	},
	
	/** 
	construct SelectList dom from jsonArray 
	
	@method update
	*/
	update: function (searchResultsJson, filter) {
		log ("SelectList update()");
		this.searchResultsJson = searchResultsJson;
		this.collections_count = $H(this.searchResultsJson).size()
		this.items = []
		this.container.update();
		
		$H(searchResultsJson).each (function (pair) {
			var key = pair.key;
			var itemsJson = pair.value;
			itemsJson.each (function (itemJson, i) {
				if (!filter || filter(itemJson)) {
					var item = this.createItem (itemJson, key);
					this.items.push(item);
					this.container.insert (item.dom);
					if (i > this.max_items) 
						throw $break
				}
			}.bind(this));
		}.bind(this));			
		
		this.container.childElements().sortBy(this.itemSortFn).each (function (el) {
			this.container.appendChild(el);
        }.bind(this));

		this.doFilter();
		try {
			this.focus (this.selected);
		} catch (error) {
			log ("focus ERROR: " + error);
		}
		
		this.size = this.container.childElements().size();
	},

	selectedItem: function () {
		if (!this.selected)
			return null;
		return this.items.find (function (item) {
			return (item.getId() == this.selected);
		}.bind(this));
	},

	/**
	
	@method getSize
	*/
	getSize: function (collection) {
		// log ("getSize: " + collection);
		var collectionJson = this.searchResultsJson[collection]
		if (!collectionJson) {
			// log ("WARN: getSize did not find collection for " + collection)
			return 0;
		}
		return collectionJson.length;
	}
});


/**
has attributes:
- label - the string representation of the item
- value - a system-oriented representation of the item 
	(for now, can be used to store collection)
- list (the parent of this item)


@class SelectListItem
*/
var SelectListItem = Class.create ({
		
	/**
	
	@method initialize
	*/
	initialize: function (label, value, list) {
		this.label = label;
		this.value = value || this.label;
		this.list = list;
		if (!this.list)
			log ("this.list was a " + typeof (this.list));
		try {
			this.dom = this.render();
		} catch (error) {
			log ("SelectListItem ERROR: " + error);
		}
	},
	
	
	/**
	
	@method getId
	*/
	getId: function () {
		return makeItemKey (this.label, this.value);
	},
	
	/**
	
	@method render
	*/
	render: function () { 		
		var text = this.label;
		
		var itemDom = new Element ('li')
			.update(text)
			.addClassName ('select_item')
			
			.writeAttribute('id', this.getId())
			
			.observe ('click', function (event) {
				log ('item clicked - ' + this.label);
				log (' - value: ' + this.value);
				this.list.selectItem(this);
			}.bind(this))
			
			.observe ('mouseover', function (event) {
				event.element().addClassName ('over');
				
			})
			.observe ('mouseover', function (event) {
				event.element().removeClassName ('over');
			});
			
		if (this.list.collections_count > 1) {
			itemDom.insert({top:new Element('div')
							.update (this.value)
							.addClassName('collection')
/* 							.setStyle({
								float:'right',
								fontSize:'90%',
								color:'#666666'
							}) */
			});
		}
		
		var downloadIcon =  createStackedIcon ("fa-download", function (event) {
				var asset_url = contextPath + '/content/' + this.value + '/' + this.label;
				window.open(asset_url, '_blank');
		}.bind(this), {title: "Download this asset"});
		downloadIcon.addClassName("asset-link");
		itemDom.insert ({top:downloadIcon});
			
		return itemDom;
	},
	
	
	/**
	
	@method hide	
	*/
	hide: function () {
		this.dom.hide();
	},
	
	/**
	
	@method function
	*/
	show: function () {
		this.dom.show();
	},
	
	/**
	
	@method visible
	*/
	visible: function () {
		return this.dom.visible();
	}
});
								
/**
Base class for the tools implmented by the Helper

	- SelectTool
	- UploadTool
	- WeblinkTool
	
@class HelperComponent
*/					
var HelperComponent = Class.create ({
	/**
	
	
	@constructor
	@method initialize
	@param id {String} unique id for this tool (e.g., 'file-select')
	@param controller {InputPanelHelper}
	*/
	initialize: function (id, controller) {
		this.id = id;
		this.controller = controller
		// this.row = $(this.id);
		// this.ctrl = this.row.down('.ctrl');
		// this.toolDom = this.row.down('.tool');
		
		this.ctrl = $(this.id + '-button');
		this.toolDom = $(this.id + '-tool');
		
		if (!this.ctrl)
			log ("WARN: no control found for " + this.id);
		if (!this.toolDom)
			log ("WARN: no toolDom found for " + this.id);
		this.ctrl.observe ('click', this.ctrlHandler.bind(this));
	},
		
	/**
	@method getName
	@return human-readible name for this tool (e.g., 'SelectTool')
	*/
	getName: function () {
		return 'HelperComponent';
	},
	
	/**
	Handles clicks on this tool's control (the 
	link in the UI that activates this tool)
	
	@method ctrlHandler
	*/
	ctrlHandler: function (event) {
		// log (" - click registered (" + this.id + ")");
		this.controller.setCurrent(this);
	},
	
	/**
	
	@method getValue
	@return {String} - the url value to be inserted in the metadata record
	*/
	getValue: function () {
		log ("ABSTRACT getValue()");
		// alert ("getValue is not implemented");
		throw ("getValue is not implemented");
	}
});

/**

@class WebLinkTool
@extends HelperComponent
*/
var WebLinkTool = Class.create (HelperComponent, {
	
	initialize: function ($super, id, controller) {
		this.input = $('weblink-input');
		this.messages = $('weblink-messages');
		
		$super(id, controller);
		this.input.value = this.controller.existingAssetPath || ''
		this.input.setStyle({width:'90%'});
		this.input.on ('keyup', this.validateInput.bind(this));
	},
	
	validateInput: function (event) {
		var url = this.getValue();
		var msg = null;
		// log ("validate: " + url);
		
		if (isProtectedUrl (url)) {
			this.showMessage ('protected-url-msg');
			ASSET_MANAGER.disableInsert();
		}
		else {
			this.messages.hide();
			ASSET_MANAGER.enableInsert();
		}
	},
	
	showMessage: function (messageId) {
		this.messages.childElements().each (function (msg) {
			if (msg.identify() == messageId)
				msg.show();
			else
				msg.hide();
		});
		this.messages.show();
	},
	
	getName: function () {
		return 'WebLinkTool';
	},
	
	ctrlHandler: function ($super, event) {
		$super(event);
		this.input.focus();
		this.validateInput();
	},
	
	getValue: function () {
		/* return $F('weblink-input'); */
		return $F(this.input);
	}
	
	

});

/**

@class SelectTool
@extends HelperComponent
*/
var SelectTool = Class.create (HelperComponent, {
	
	/**
	
	@method initialize
	*/
	initialize: function ($super, id, controller) {
		this.updated = false;
		this.selectList = null;
		/* this.selected_collections = [controller.collection]; */
		var assetCollection = getAssetDir (controller.existingAssetPath) || PARAMS.collection;
		this.selected_collections = [assetCollection];
		this.collections_selector = null;
		
		var initialFileName = controller.existingAssetPath.split('/').last();
		this.initialItemKey = makeItemKey(initialFileName, assetCollection);
		
		$super(id, controller);
		
/*  		$$('.collections-button').each (function (buttonDom) {
			buttonDom.observe ('click', this.handleCollectionsButtonClick.bind(this));
		}.bind(this)); */
		
	},
	
	/**
	
	@method handleCollectionsButtonClick
	*/
 	handleCollectionsButtonClickOFF: function (event) {
		log ("handleCollectionsButtonClick");
/* 		var id = event.element().identify();
		log (" - id: " + id);
		var listSpec;
		if (id == 'all-collections-button')
			listSpec = ALL
		else if (id == 'selected-collections-button')
			listSpec = SELECTED
		this.setCollectionListSpec (listSpec) */
	},	
	
	getName: function () {
		return 'SelectTool'
	},
	
	getSelectedLabel: function () {
		var selectedItem = this.selectList.selectedItem();
		if (!selectedItem)
			return null;
		return selectedItem.label;
	},
	
	getSelectedItem: function () {
		try {
			// log("getSelectedItem() " +  this.selectList.selectedItem());
			return this.selectList.selectedItem();
		} catch (error) {
			// log ("getSelectedItem WARN: " + error);
		}
		return null;
	},
	
	getSize: function () {
		if (!this.selectList)
			return 0;
		return this.selectList.size;
	},
	
	getValue: function () {
		return this.getSelectedLabel();
	},
	
	getCollection: function () {
		if (!this.selectList.selected)
			return null;
		return this.selectList.selectedItem().value;
	},
	
	ctrlHandler: function ($super, event) {
		$super(event);
		$('select-filter').focus();
		if (this.collections_selector == null)
			this.collections_selector = new CollectionsSelector(this);
		if (!this.updated) {
			this.update();
		}
		if (this.getSelectedItem())
			this.controller.enableInsert();
		else
			this.controller.disableInsert();
	},
	
	/**
	refresh this selectList with data obtained via Ajax Call (to UploadAction)
	*/
	update: function () {
		log ('Select-tool UPDATE');
		
		var params = {
			action: 'list',
			collection: this.selected_collections
		}
		
		new Ajax.Request (contextPath + "/editor/upload.do", {
			parameters: params,
			onSuccess: function (response) {
				// log ("RESPONSE: " + response.responseText.trim().truncate(300));
				var jsonResponse = null;
				try {
					jsonResponse = response.responseText.evalJSON();
					if (false) {
						log ("responseKeys");
						for (key in jsonResponse)
							log (" - " + key);
					}
					// log (JSON.stringify (jsonResponse, null, 2 ));
				} catch (error) {
					this.toolDom.update ("Could not eval response as JSON: " + error);
					return;
				}
				/* var resultsDom = this.toolDom.appendChild (new Element('div', {id:'search_results'}));
				if (!resultsDom)
					log ("ERROR: resultsDom not found"); */
				// resultsDom.update (new Element('pre').update (JSON.stringify (jsonResponse, null, 2 )));
				// this.renderResults(jsonResponse.filenames);
				try {
					if (!this.selectList){
						log ('selectList does yet exist creating ...');
						// var resultsDom = this.toolDom.appendChild (new Element('div', {id:'search_results'}));
						var resultsDom = this.toolDom.down('#search_results');
						if (!resultsDom) {
							log ("SEARCH_RESULTS not found");
							try {
								if (!this.toolDom) 
									log ("TOOL DOM not found either");
								else {
									log ("toolDom id: " + this.tooDom.identity());
								}
							} catch (error) {
								log ("update ERROR: " + error);
							}
						}
						this.selectList = new SelectList(resultsDom, jsonResponse.results, this);
					}
					else {
						log ('selectList exists');
						this.selectList.update(jsonResponse.results);
					}
				} catch (error) {
					log ("error: " + error);
				}
				this.updateStatus();
				this.populateCollectionsDisplay();
				this.updated = true;
				log ("- " + this.getName() + " updated with " + this.getSize() + " items");
			}.bind(this)
		});
	},
	
	/**
	Update dynamic variables after update - in particular the message for
	the select list. If there is a single collection being searched, show it's 
	name. Otherwise, show "n collections".
	
	@method updateStatus
	*/
	updateStatus: function () {
		
		// log ("updateStatus");
		try {
			if (this.selected_collections.length == 1) {
				// log (" single collection")
				var info = getCollectionInfo (this.selected_collections[0]);
				if (info) {
					// log (" - " + info.name);
					$('single-collection-name').update (info.name + " (" + info.key + ")");
					$('single-collection-name').show();
					$('collection-display-trigger').hide();
				}
				else
					log (" !! info not found for " + this.selected_collections[0]);
			} else {
				var count = this.selected_collections.length == 0 ? COLLECTIONS.length : this.selected_collections.length;
				// log ("count: " + count);
				
				$('collection-count').update (count);
				$('single-collection-name').hide();
				$('collection-display-trigger').show();
			}
		}
		catch (error) {
			log ("ERROR: " + error);
		}

		$('file-count').update (this.getSize());
		// $('collection-count').update (this.selected_collections.length);
		log (" status updated ...");
	}
});
	
/**
The collection attribute in the Upload Tool refers to the collection where the current
asset is stored, and if there isn't an asset it defaults to Helper.collection

@class UploadTool
@extends HelperComponent
*/
var UploadTool = Class.create (HelperComponent, {
	
	initialize: function ($super, id, controller) {
		this.fileName = null;
		this.selectedFile = null; // selected file to upload
		$super(id, controller);
		this.collection = getAssetDir(this.controller.existingAssetPath) || controller.collection;
		log ("UPLOAD COLLECTION is " + this.collection);
	},
	
	getName: function () {
		return 'UploadTool';
	},
	
	getValue: function () {
		return this.fileName;
	},
	
	getCollection: function () {
/* 		log ("getCollection");
		log (" selected: " + getSelectedCollection());
		log ("this.collection: " + this.collection); */
		return getSelectedCollection() || this.collection;
	},
	
	ctrlHandler: function ($super, event) {
		$super(event);
		if (this.selectedFile)
			this.controller.enableInsert();
		else
			this.controller.disableInsert();
	},
	fooberry: function (event) {
		alert ('fooberry');
	},
	/**
	This code based on http://www.matlus.com/html5-file-upload-with-progress/
	*/
	fileSelected: function  () {
		log ("fileSelected()");
		log (" ASSET_MANAGER.collection: " + ASSET_MANAGER.collection);
		var file = $('fileInputButton').files[0];
		if (file) {
			this.selectedFile = file;
			this.fileName = file.name;
			// ASSET_MANAGER.upload.collection = collection;
			ASSET_MANAGER.enableInsert();
			
			var fileSize = formatFileSize(file.size);
			
			var lastMod = moment(file.lastModifiedDate);
			// log ('lastMod: ' + lastMod.format('M/D/YY H:mm'));
			
			$$('.selected-fileName').each (function (el) {el.update (file.name)});
			// document.getElementById('selected-fileName').innerHTML = 'Name: ' + file.name;
			document.getElementById('selected-fileSize').innerHTML = 'Size: ' + fileSize;
			document.getElementById('selected-lastMod').innerHTML = 'Last modified: ' + lastMod.format(modDateFormat);

			
			$('file-selected-info').show();
			$('visible-upload-button').innerHTML = "Choose a different file";
			$('uploadInputs').setStyle({margin:"1px 0px 0px 20px"});
			
			
			// get assetInfo
			new Ajax.Request (contextPath + "/editor/upload.do", {
				parameters : {action: 'assetInfo', filename: file.name},
				onSuccess: function (response) {
					// log ("RESPONSE: " + response.responseText.trim().truncate(300));
					// log ("RESPONSE: " + response.responseText.trim());
					var jsonResponse = null;
					try {
						jsonResponse = response.responseText.evalJSON();
						if (false) {
							log ("responseKeys");
							for (key in jsonResponse)
								log (" - " + key);
						}
						// log (JSON.stringify (jsonResponse, null, 2 ));

						new AssetInfoDisplay(jsonResponse);
						// updateAssetInfoDisplay (jsonResponse);
							
					} catch (error) {
						this.toolDom.update ("Could not process response as JSON: " + error);
						return;
					}
				}
			});			
		}
		else {
			// file was not present
			ASSET_MANAGER.disableInsert();
			ASSET_MANAGER.upload.selectedFile = null;
		}
	}
	
});

/**

@class DeleteRenameTool
@extends HelperComponent
*/
var DeleteRenameTool = Class.create (HelperComponent, {
	
	initialize: function ($super, id, controller) {
		
		$super(id, controller);
	
		this.orphanDisplay = new OrphanInfoDisplay();
		
		if (false) {
			// drRefresh is an icon that refreshes the display
		
			var drRefresh = $('dr-refresh');
			var icon = drRefresh.down('i.fa-refresh');
			
			if (!drRefresh)
				alert ("no drRefresh");
			if (!icon)
				alert ("no icon!");
			
			drRefresh.observe ('mouseover', function (event) {
				icon.addClassName('over');
			});
			
			drRefresh.observe ('mouseout', function (event) {
				icon.removeClassName('over');
			});
			
			drRefresh.observe ('click', function (event) {
				// var icon = event.findElement('#dr-refresh').down('i');
				icon.addClassName('fa-spin');
				this.update(function (event) {
					icon.removeClassName('fa-spin');
					log ("removed? " + $w(icon.className));
				});
			}.bind(this));
		}
	},

	getName: function () {
		return 'DeleteRenameTool';
	},
	
	ctrlHandler: function ($super, event) {
		log ("DeleteRenameTool");
		$super(event);
		this.controller.disableInsert();
		
		// Check for need to update?
		this.update();
	},
	
	getValue: function () {this.container = $('assetInfo');
		// There is no value for this tool
		return null
	},
	
	/**
	
	@method update
	*/
	update: function (focusId) {
		
		var collections = COLLECTIONS.pluck('key');
		
		if (this.orphanDisplay.items == null)
			this.orphanDisplay.showSpinner();
		
		// log (collections.size() + ' collections plucked');
		
		new Ajax.Request (contextPath + "/editor/upload.do", {
			parameters : {action: 'orphanInfo', collection: collections},
			onSuccess: function (response) {
				// log ("RESPONSE: " + response.responseText.trim().truncate(300));
				// log ("RESPONSE: " + response.responseText.trim());
				var jsonResponse = null;
				try {
					jsonResponse = response.responseText.evalJSON();
					if (false) {
						log ("responseKeys");
						for (key in jsonResponse)
							log (" - " + key);
					}
					if (!jsonResponse.orphans)
						throw ("orphan data not found in response");
					logJson(jsonResponse);
					try {
						this.orphanDisplay.update (jsonResponse.orphans, focusId);
					} catch (error) {
						log ("ERROR update: " + error);
					}
					
/* 					if (callback) {
						log ("calling callback");
						callback(jsonResponse);
					} */
						
				} catch (error) {
					this.toolDom.update ("Could not process response as JSON: " + error);
					return;
				}
			}.bind(this)
		});			
	}
		
	
});


/**
Controls UploadHelper UI (see upload_helper.jsp)

we have access to request params passed to iframe

@class UploadHelper
*/
var AssetManager = Class.create ({
		
	/**
	decorate the dom with functionality, etc
	there are three main tools
	
	@method initialize 		
	*/
	/* initialize: function (existingAssetPath, collection) { */
	initialize: function () {
		
		this.existingAssetPath = PARAMS.existingAssetPath;
		log ("ASSET_MANAGER -- this.existingAssetPath: " + this.existingAssetPath);
		this.collection = PARAMS.collection;
		this.recordId = PARAMS.recordId;
		this.widgetId = PARAMS.widgetId;
		this.select = new SelectTool ('file-select', this);
		this.upload = new UploadTool ('upload', this);
		this.weblink = new WebLinkTool ('weblink', this);
		this.deleteRename = new DeleteRenameTool('delete-rename', this);
		this.tools = [this.select, this.upload, this.weblink, this.deleteRename]
		this.current = null;
		this.insertButton = $('insert-button');
		this.insertButtonObserver = 
			this.insertButton.on ('click', this.insertButtonHandler.bind(this));
		this.cancelButtonObserver = 
			$('cancel-button').on('click', this.cancelButtonHandler.bind(this));
		$('helper-banner').down('.closer').observe('click', this.close.bind(this));
		
		this.escapeHandler = document.on('keyup', this.handleKeyUp.bind(this));
	},
	
	handleKeyUp: function (event) {
		// log ("Helper KEYUP");
		if (event.keyCode && event.keyCode == 27) {
/* 			if (parent.modal_close)
				parent.modal_close(); */
			var widget = this.getWidget();
			if (widget)
				widget.modal_close()
			return;
		}
	},
	
	getWidget: function() {
		log ("get widget: " + this.widgetId);
		try {
			return parent.getWidget(this.widgetId);
		} catch (error) {
			log ("getWidget ERROR (" + this.widgetId + "): " + error);
		}
		return null;
	},
	
	close: function () {
		// parent.modal_close();
		this.getWidget().modal_close();
	},
	
	cancelButtonHandler: function (event) {
		// parent.modal_close();
		this.getWidget().modal_close();
	},
	
	disableInsert: function () {
		this.insertButton.disable();
	},
	
	enableInsert: function () {
		this.insertButton.enable();
	},
	
	/**
	Asks for a value from the current tool, and then pass this value to
	parent.doUploadHelperComplete
	
	@method insertButtonHandler		
	*/
	insertButtonHandler: function (event) {
		var collection = this.collection;
		if (this.current){
			log ("insertButtonHandler " + this.current.getName());
		}
		
		var filename = null;
		try {

			filename = this.current.getValue();
				
			log (" filename: " + filename);
			// doUploadHelperComplete will not change value if filename is null
		} catch (error) {
			log ("insert-button could not get value to insert: " + error);
			
		}
		if (this.current == this.upload) {
			log (' - insert button - upload is active tool');
			// collection = this.upload.collection;
			collection = this.upload.getCollection();
			try {
				log (' - about to UPLOAD - filename: ' + filename + ', collection: ' + collection);
				uploadFile(function () {
					this.getWidget().doUploadHelperComplete(filename, collection);	
				}.bind(this));
			} catch (error) {
				log ("WARN: could not uploadFile: " + error);
			}
			return;
				
		}
		else if (this.current == this.select) {
			collection = this.select.getCollection();
		}
		
		try {
			// parent.doUploadHelperComplete(filename, collection);
			this.getWidget().doUploadHelperComplete(filename, collection);
		} catch (error) {
			log ("parent.doUploadHelperComplete ERROR: " + error);
		}
	},
	
	/**
	
	@method setCurrent		
	*/		
	setCurrent: function (tool) {
		// log ("setCurrent: " + tool.getName());
		this.current = tool;
		this.tools.each (function (ulh_tool) {
			if (ulh_tool == tool) {
				ulh_tool.ctrl.addClassName('current');
				ulh_tool.toolDom.show();
			}
			else {
				ulh_tool.ctrl.removeClassName('current');
				ulh_tool.toolDom.hide();
			}
		});
		// log (' - current set to ' + this.current.id);

	},
	
	getModalBackground: function () {
		log ("getModalBackground()");
		var modal_background = $('modal-background');
		if (!modal_background) {
			modal_background = new Element('div', {id:"modal-background"})
				.setStyle({
					display:'none'
				});
			$('helper-tools').insert (modal_background);
		}
		return modal_background;
	}
				
});


/**

@method pageInit 
*/
function pageInit () {
	// var selectCtrl = new SelectTool ('select-ctrl');
	//var selectCtrl = $('select-ctrl');
	
	uploader();
	
	/* we have PARAMS passed to IFrame available and can pass them to any classes
	   we create here, or make them global
	    - action: helper
		- frameId: uFrm1397411833174
		- recordId: COMMENT-BSCS-000-000-000-036
		- NO MORE - existingFileName: SS4_prerequisite.pdf
		- existingAssetPath; the value of the metadataField
		X sizeDisplay:
		- collection: comment_bscs
	// var collection = PARAMS.collection;
	*/
	
	/* ASSET_MANAGER = new AssetManager(PARAMS.existingAssetPath, PARAMS.collection); */
	ASSET_MANAGER = new AssetManager();
	
}

/**

exposes 
- fileSelected
- uploadFile
*/
var uploader = function () {

	var the_callback;
	
	function uploadFile(callback) {
		the_callback = callback;
		log ("uploadFile()");
		log ("  - collection: " + ASSET_MANAGER.upload.collection);
		ASSET_MANAGER.upload.selectedFile = null;
	  var xhr = new XMLHttpRequest();
	  // var fd = $('fileInputForm').getFormData();
	  
	  var fd = new FormData();
	  fd.append("theFile", $('fileInputButton').files[0]);
	  fd.append("action", 'upload');
	  fd.append("recordId", ASSET_MANAGER.recordId);
	  fd.append("collection", ASSET_MANAGER.upload.collection);
	  fd.append("existingFileName", ASSET_MANAGER.upload.fileName);
	  
	  /* event listners */
	  xhr.upload.addEventListener("progress", uploadProgress, false);
	  xhr.addEventListener("load", uploadComplete, false);
	  xhr.addEventListener("error", uploadFailed, false);
	  xhr.addEventListener("abort", uploadCanceled, false);
	  /* Be sure to change the url below to the url of your upload server side script */
	  xhr.open("POST", contextPath + "/editor/upload.do");
	  xhr.send(fd);
	}
	
	function uploadProgress(event) {
		log ("uploadProgress()");
		if (event.lengthComputable) {
		  var percentComplete = Math.round(event.loaded * 100 / event.total);
		  document.getElementById('progressNumber').innerHTML = percentComplete.toString() + '%';
		}
		else {
		  document.getElementById('progressNumber').innerHTML = 'unable to compute';
		}
	}
	
	/* This event is raised when the server send back a response */
	function uploadComplete(event) {
		// alert ("uploadComplete()");
		if (!event.target.responseText.isJSON()) {
			log ("WARN: uploadComplete received a non-JSON response\n" + 
				event.target.responseText.strip().truncate(50));
			// throw ("Unknown Upload error - response not JSON");
			$('progressNumber').update();
			// Notify user of error!
			return;
		}
		
		var jsonResponse = event.target.responseText.evalJSON();
		log ("jsonResponse: " + JSON.stringify(jsonResponse, null, 2));
		
		// SET instance var to responseJson
		ASSET_MANAGER.upload.fileName = jsonResponse.success.fileName;
		log ("SET fileName to " + ASSET_MANAGER.upload.fileName);
		if (the_callback)
			the_callback()
	}
	
	function uploadFailed(event) {
		alert("There was an error attempting to upload the file.");
	}
	
	function uploadCanceled(event) {
		alert("The upload has been canceled by the user or the browser dropped the connection.");
	}
	
	window.uploadFile = uploadFile;
	// window.fileSelected = fileSelected;
	// window.getAssetDir = getAssetDir;
	
 };

document.observe ('dom:loaded', pageInit);




