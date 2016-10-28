/**
@module upload_helper_script
@submodule aset_info_display
*/
/**


@class global functions
*/

var verbose = false;

function logJson (json, force) {
	// verbose defaults to true
	verbose = typeof(verbose) != "undefined" ? verbose : true;
	if (verbose || force)
		log (JSON.stringify (json, null, 2 ));
}


/**
Orphans are assets that are not cataloged in the metadata

@class Orphan
*/
var Orphan = Class.create({
		
	initialize: function (orphanInfo, controller) {
		// log ('----------------\nOrphan');
		// logJson(orphanInfo);
		
		this.orphanInfo = orphanInfo;
		this.controller = controller;
		var splits = orphanInfo.split('/');
		this.collection = splits[0];
		this.filename = splits[1];
		this.id = makeAssetKey(this.collection, this.filename);
		/* this.id = this.collection + '|' + this.filename; */
		// this.records = this.orphanInfo.records;
		
		var collectionInfo = getCollectionInfo(this.collection);
		if (!collectionInfo)
			throw ("collectionInfo NOT FOUND for asset directory: " + this.collection);
		this.collectionName = collectionInfo.name;
		this.filenameEl = null;
		this.trashIcon = null;
		this.dom = null; // populated by render()
		this.editButton = null;
		this.editDoneButton = null;
		this.editCancelButton = null;
		
	},
	
	toString: function () {
		return this.collection + '/' + this.filename;
	},
	
	isSelected: function () {
		return this.checkbox.checked;
	},
	
	/**
	
	@method hightlight
	*/
	highlight: function () {
		this.dom.addClassName('highlight');
		window.setTimeout( function () { 
			this.dom.removeClassName('highlight');
		}.bind(this), 4000);
	},

	makeStatic: function () {
		if (!this.filenameEl) {
			log ("element to make editable not found");
			return;
		}
		
		this.filenameEl
			.writeAttribute('contenteditable', null)
			.writeAttribute('title', '')
			.stopObserving();
			
		this.editButton.show();
		this.editDoneButton.hide();
		this.editCancelButton.hide();
			
		log (" filename made static");
	},
	
	makeEditable: function () {

		if (!this.filenameEl) {
			log ("element to make editable not found");
			return;
		}
		
		this.filenameEl
			.writeAttribute('contenteditable', 'true')
			.writeAttribute('title', 'click to rename asset file')
			.observe ('keyup', this.handleKeyUp.bind(this))
			.observe ('keydown', this.handleKeyDown.bind(this))
			.observe ('blur', this.handleBlur.bind(this))
			.observe ('focus', this.handleFocus.bind(this))
			
		this.filenameEl.focus();
		this.editButton.hide();
		this.editDoneButton.show();
		this.editCancelButton.show();
		window.filenameEl = this.filenameEl;
			
		log (" this.filenameEl made editable");
	},
	
	/**
	Creates HTML for this Orphan and returns LI container element
	
	@method render
	*/
	render: function () {

		var container = new Element ('li', {id : this.id})
				.addClassName ("orphan");
				
		this.filenameEl = new Element('div')
				.addClassName('filename')
/* 				.writeAttribute('contenteditable', 'true')
				.writeAttribute('title', 'click to rename asset file')
				.observe ('keyup', this.handleKeyUp.bind(this))
				.observe ('keydown', this.handleKeyDown.bind(this))
				.observe ('blur', this.handleBlur.bind(this))
				.observe ('focus', this.handleFocus.bind(this)) */
				.update(this.filename);
				
		var collectionEl = new Element('div')
				.addClassName('collection')
				.update(this.collectionName);
				
		// Simple Icons (not used)
		var trashIconSimple = new Element('li')
				.addClassName('fa fa-trash-o')
				.setStyle({fontSize:'1.3em', paddingLeft:'3px',verticalAlign:'-1px'});
				
		var editIconSimple = new Element('li')
				// .addClassName('fa fa-edit')
				.addClassName('fa fa-pencil')
				.setStyle({fontSize:'1.3em', padding:'0px 3px',verticalAlign:'-1px'});
				
		// Stacked Icons (used)
		this.editButton = createStackedIcon ("fa-pencil", function (event) {
				this.controller.hideMsg();
				this.makeEditable();
		}.bind(this), {title: "Rename this asset"});
		
		this.editDoneButton = createStackedIcon ("fa-check", function (event) {
				this.controller.hideMsg();
				// this.blur();
		}.bind(this), {title: "Done"}).hide();
		
		// call back can be NONE - work is done by blurhandler
		this.editCancelButton = createStackedIcon ("fa-times", function (event) {
				log ("cancelButton callback");
				this.controller.hideMsg();
				this.filenameEl.innerHTML = this.filename;
				// this.blur();
			}.bind(this), {title: "Cancel", eventname:'mousedown'}).hide();
			
		this.trashButton = createStackedIcon ("fa-trash-o", function (event) {
				this.controller.hideMsg();
				this.doDelete(event);
		}.bind(this), {title: "Trash this asset"});
				
		var downloadButton = createStackedIcon ("fa-download", function (event) {
				this.controller.hideMsg();
				var asset_url = contextPath + '/content/' + this.collection + '/' + this.filename;
				window.open(asset_url, '_blank');
		}.bind(this), {title: "Download this asset"});

		// allow position download icon		
		downloadButton.addClassName("asset-link");

		// this.checkbox not currently used
		if (false) {
			this.checkbox = new Element('input', {
				type:"checkbox",
				name:"selected_orphans",
				value:this.id
			})
			.observe ('click', this.handleCheckboxClick.bind(this));
		}

		container
			// .insert(this.checkbox)
			.insert(this.trashButton)
			.insert(this.filenameEl)
			.insert(this.editButton)
			.insert(this.editDoneButton)
			.insert(this.editCancelButton)
			/* .insert(downloadButton) */
			.insert(downloadButton)
			.insert(collectionEl);
			
		this.dom = container;
		return this.dom;
	},
	
	handleCheckboxClick: function (event) {
		log ("checkbox " + event.element().readAttribute("value"));
		this.controller.handleCheckboxClick(event);
	},
	
	/**
	on ESC we revert and return
	on RETURN we submit (via blur)
	
	@method handleKeyUp
	*/
	handleKeyUp: function (event) {
		// log ("handleKeyUp! " + event.keyCode);
		// escape revert and blur
		if (event.keyCode && event.keyCode == 27) {
			event.element().innerHTML = this.filename;
			event.element().blur();
			event.stop();
		}
		// RETURNS mean the user has submitted edits
		if (event.keyCode && event.keyCode == 13) {
			// log ("User has entered the new name: " + event.element().innerHTML);
			event.element().blur();
		}
	},
	
	/**
	block newlines in editable field!
	
	@method handleKeyDown
	*/
	handleKeyDown: function (event) {
		// log ("handleKeyDown! " + event.keyCode);
		if (event.keyCode && event.keyCode == 13) {
			event.preventDefault();
			event.stop();
		}
	},
	
	handleBlur: function (event) {
		log ("handleBlur()");
		
		event.element().removeClassName('focus');
		log (' - ' + $w(event.element().className));
		this.makeStatic();
		
		var content = event.element().innerHTML;
		
		log (" - content: " + content);
		log (" - filename: " + this.filename);
		
		if (content != this.filename) {
			if (true) {
				try {
					this.doRename (event, content);
				} catch (error) {
					log ("rename ERROR: " + error);
				}
			}
			else {
				if (confirm ("Do you want to rename this file as \'" + content + "\'?")) {
					log ('RENAMING ...');
					try {
						this.doRename (event, content);
					} catch (error) {
						log ("rename ERROR: " + error);
					}
				}
				else {
					log ('DONT rename');
					event.element().update (this.filename);
				}
			}
		}
		
		log (" - returning from handleBlur");
	},
	/**
	
	@method handleFocus
	*/
	handleFocus: function (event) {
		// log ("handleFocus! content: " + event.element().innerHTML);
		event.element().addClassName('focus');
	},
	
	/**
	
	@method doRename
	*/
	doRename: function (event, newFilename) {
		
		try {
			event.findElement(".orphan").down('.fa-pencil')
				.removeClassName('fa-pencil')
				.addClassName('fa-spinner fa-spin');
		} catch (error) {
			log ("WARN: could not make spinner: " + error);
		}
		
		var params = {
			action:'rename',
			newFilename:newFilename,
			oldFilename:this.filename,
			collection:this.collection
		}
		
		new Ajax.Request (contextPath + "/editor/upload.do", {
			parameters : params,
			onSuccess: function (response) {
				// log ("rename RESPONSE: " + response.responseText.trim().truncate(300));
				log ("RENAME onSuccess");
				// log ("RESPONSE: " + response.responseText.trim());
				var jsonResponse = null;
				try {
					jsonResponse = response.responseText.evalJSON();
					if (false) {
						log ("responseKeys");
						for (key in jsonResponse)
							log (" - " + key);
					}
					
					logJson (jsonResponse);
					
					if (jsonResponse.error) {
						alert ("error: " + jsonResponse.error);
						return;
					}
					if (jsonResponse.success) {
						log ("success: the new file is " + jsonResponse.success.filename);
						this.controller.setMsg ("asset renamed to " + jsonResponse.success.filename + ". ");
						try {
							var oldFilename = jsonResponse.request.oldFilename;
							var newFilename = jsonResponse.success.filename;
							var undoLink = new Element('a', {href:'javascript:void(0)'})
													.update("Undo")
													.observe('click', function (event) {
														this.undoRename(oldFilename, newFilename)
													}.bind(this))
							
							
							this.controller.getMsgEl().appendChild (undoLink);
						} catch (error) {
							log ("ERROR undoLink: " + error);
						}
						
						
						ASSET_MANAGER.deleteRename.update(makeAssetKey(this.collection, newFilename));
					}
					
					// assetEl.update(newFilename)
						
				} catch (error) {
					this.toolDom.update ("Could not process response as JSON: " + error);
					return;
				}
			}.bind(this)
		});			

	},
	/**
	
	@method undoRename
	*/
	undoRename: function (oldFilename, newFilename) {
		
		var params = {
			action : 'undo',
			command : 'rename',
			oldFilename : oldFilename,
			newFilename : newFilename,
			collection : this.collection
		}
		new Ajax.Request (contextPath + "/editor/upload.do", {
			parameters : params,
			onSuccess: function (response) {
				log ("undo RENAME onSuccess");
				var jsonResponse = null;
				try {
					jsonResponse = response.responseText.evalJSON();
					if (false) {
						log ("responseKeys");
						for (key in jsonResponse)
							log (" - " + key);
					}
					logJson (jsonResponse);
					if (jsonResponse.error) {
						alert ("error: " + jsonResponse.error);
						return;
					}
					if (jsonResponse.success) {
						
						// refresh view
						this.controller.setMsg ("asset name restored to " + jsonResponse.success.filename);
						
						ASSET_MANAGER.deleteRename.update(makeAssetKey(this.collection, jsonResponse.success.filename));
					}
					
					// this.message.update ("asset renamed");
					// assetEl.update(newFilename)
						
				} catch (error) {
					this.toolDom.update ("Could not process response as JSON: " + error);
					return;
				}
			}.bind(this)
		});			
		
	},
	
	/**
	
	@method handleDeleteButton
	*/
	doDelete: function (event) {
			
		var params = {
			action: 'delete',
			id: [this.id]
		}
		
		try {
			event.findElement(".fa-stack").down('.fa-trash-o')
				.removeClassName('fa-trash-o')
				.addClassName('fa-spinner fa-spin');
		} catch (error) {
			log ("WARN: could not make spinner: " + error);
		}
		
		new Ajax.Request (contextPath + "/editor/upload.do", {
			parameters : params,
			onSuccess: function (response) {
				log ("DELETE onSuccess");
				// log ("Delete RESPONSE: " + response.responseText.trim().truncate(300));
				// log ("RESPONSE: " + response.responseText.trim());
				var jsonResponse = null;
				try {
					jsonResponse = response.responseText.evalJSON();
					if (false) {
						log ("responseKeys");
						for (key in jsonResponse)
							log (" - " + key);
					}
					logJson (jsonResponse);
					if (jsonResponse.error) {
						alert ("error: " + jsonResponse.error);
						return;
					}
					if (jsonResponse.success) {
						log ("success!");
						
						var deletedKeys = jsonResponse.success.deleted;
						// refresh view
						var msg = deletedKeys.length + " asset" + (deletedKeys.length != 1 ? "s" : "") + " deleted. ";
						this.controller.setMsg (msg);
						this.controller.getMsgEl().appendChild (new Element('a', {href:'javascript:void(0)'})
														.update("Undo")
														.observe('click', function (event) {
																this.undoDelete(event, deletedKeys)
														}.bind(this)));
						
						ASSET_MANAGER.deleteRename.update();
					}
					
					// this.message.update ("asset renamed");
					// assetEl.update(newFilename)
						
				} catch (error) {
					this.toolDom.update ("Could not process response as JSON: " + error);
					return;
				}
			}.bind(this)
		});			
	},
	/**
	
	@method undoDelete
	*/
	undoDelete: function (event, keys) {
		log ("undoDelete()");
		keys.each(function (key) { log (" - " + key);});
		
		var params = {
			action : 'undo',
			command : 'delete',
			id : keys
		}
		new Ajax.Request (contextPath + "/editor/upload.do", {
			parameters : params,
			onSuccess: function (response) {
				log ("UNdelete onSuccess");
				var jsonResponse = null;
				try {
					jsonResponse = response.responseText.evalJSON();
					if (false) {
						log ("responseKeys");
						for (key in jsonResponse)
							log (" - " + key);
					}
					logJson (jsonResponse);
					if (jsonResponse.error) {
						alert ("error: " + jsonResponse.error);
						return;
					}
					if (jsonResponse.success) {
						log ("success!");
						
						var undeleted = jsonResponse.success.undeleted;
						// refresh view
						var msg = undeleted.length + " asset" + (undeleted.length != 1 ? "s" : "") + " restored. ";
						this.controller.setMsg (msg);
						
						ASSET_MANAGER.deleteRename.update(undeleted[0]);
					}
					
					// this.message.update ("asset renamed");
					// assetEl.update(newFilename)
						
				} catch (error) {
					this.toolDom.update ("Could not process response as JSON: " + error);
					return;
				}
			}.bind(this)
		});			
		
	}

	
	
});

/**
Controlled by DeleteRenameTool - i.e., this is what initializes it 
and then calls the update() method


@class OrphanInfoDisplay
*/
var OrphanInfoDisplay = Class.create({
		
		initialize: function (jsonOrphanInfo) {
			// log ("OrphanInfoDisplay");
			
			this.container = $('orphanInfo').update();
			this.messageEl = $('delete-rename-tool').down('.message');
			
			// these are initialized in update()
			this.json = null;
			this.items = null;
			
			log ("OrphanInfoDisplay initialized");
			
		},
		
		showSpinner: function () {
			this.container.update (new Element ('i').addClassName('fa fa-spinner fa-2x fa-spin'));
		},
		
		update: function (jsonOrphanInfo, focusId) {
			// log ("orphanInfoDisplay update .... focusId: " + focusId);
			logJson(jsonOrphanInfo)
			
			this.json = jsonOrphanInfo;
				
			this.items = this.json.inject ([], function (acc, orphanInfo) {
					try {
						acc.push(new Orphan (orphanInfo, this));
					} catch (error) {
						log ("Asset WARNING: " + error);
					}
					return acc;
			}.bind(this));
			// log (this.items.length + " orphans");
			this.render(focusId);
		},
		
		
		render: function (focusId) {
			// log ("OrphanInfoDisplay RENDER focusId: " + focusId);
			// log (" - ASSET_MANAGER.upload.collection " + ASSET_MANAGER.upload.collection);
			this.container.update();
			var orphan_items_el = $('orphanList');
			if (!orphan_items_el)
				orphan_items_el = this.container.appendChild(
									new Element('ul', {id:'orphanList'}));
			orphan_items_el.update();
			
			if (false) {
				log ("ORPHANS:")
				this.items.each (function (orphan) {
						log (" - " + orphan.toString());
				});
			}
		
			if (this.items.length > 0) {
			
				// log ("sorting orphans by collection");
				if (true) {
					// sort collections alpha, but put current collection up top (why?);
					var sorted_orphans = this.items.sortBy (function (orphan) {
						return orphan.toString().toLowerCase();
					});
				}
				else
					sorted_orphans = this.items;
				
				
				log ("rendering sorted orphans (" + sorted_orphans.size() + ")" );
				sorted_orphans.each (function (orphan) {
					// log (" - " + orphan.toString());
					try {
						var orphanDom = orphan.render()
						if (focusId && (focusId == orphan.id)) {
							orphan.highlight();
						}
						orphan_items_el.insert(orphanDom);
					} catch (error) {
						log ('Asset render ERROR: ' + error);
					}
			
				}.bind(this));
			}
			else {
				log ("no orphans found");
				orphan_items_el.insert ("None found - all available assets are cataloged");
			}
				
			if (this.getMsgEl.innerHTML != '')
				this.showMsg();
			
		},

		getMsgEl: function () {
			return this.messageEl;
		},
		
		setMsg: function (content) {
			this.messageEl.update(content);
		},
		
		showMsg: function (content) {
			this.messageEl.show();
		},
		
		hideMsg: function () {
			this.messageEl.update();
			this.messageEl.hide();
		}
		
});
							



/* function handleOrphanSelector(event) {
	log ("handleOrphanSelector() - " + event.element().value);
} */

	

