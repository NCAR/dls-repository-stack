/* ========= DISPLAY CONTROLLER ================================ 
This file contains the DisplayController class, which displays and updates
the display of standards using tabs two change between display modes.
*/

/* 
	DisplayControlTab - Base class for the tabs that control the display (All, Selected, Suggested)
*/

var DisplayControlTab = Class.create({
	initialize: function (name) {
		this.name = (name ? name : 'control tab');
		this.tab_bar = $('control-tabs');
		this.textElement = new Element ('div');
		this.textElement.addClassName ("nav-menu-text");
		this.cell = this.initCell();
		this.decorateCell();
	},
	getCell: function () {
		return this.cell;
	},
	getText: function () {
		return this.name;
	},
	isSelected: function () {
		return false;
	},
	hide: function () {
		if (this.getCell())
			this.getCell().hide();
	},
	show: function () {
		if (this.getCell())
			this.getCell().show();
	},
	initCell: function () {
		var cell = new Element ('td');
		cell.addClassName ('nav-menu-item');
		cell.insert (this.textElement);
		cell.observe ('click', this.clickHandler.bindAsEventListener (this));
		cell.observe ('mouseover', this.mouseOverHandler.bindAsEventListener (this));
		cell.observe ('mouseout', this.mouseOutHandler.bindAsEventListener (this));
		return cell;
	},
	clickHandler: function (event) {
		this.cell.setStyle ({backgroundColor:"pink"});
	},
	
	mouseOverHandler: function (event) {
		if (!this.isSelected()) {
			this.cell.addClassName ('over');
			this.textElement.addClassName ('over');
		}
	},
	
	mouseOutHandler: function (event) {
		if (!this.isSelected()) {
			this.cell.removeClassName ('over');
			this.textElement.removeClassName ('over');
		}
	},
	
	decorateCell: function () {
		var cell = this.cell;
		var text = this.textElement;
		text.update (this.getText());
		cell.removeClassName ('over');
		text.removeClassName ('over');
		if (this.isSelected()) {
			cell.addClassName ('selected');
		}
		else {
			cell.removeClassName ('selected');
		}
	}
});

var AllTab = Class.create (DisplayControlTab, {
	initialize: function ($super) {
		$super('All');
	},
	isSelected: function () {
		return (DisplayState.displayMode != 'list');
	},
	getText: function () {
		// res_qual hack:
		if (DisplayState.xmlFormat == 'res_qual')
			return "Choose Learning Goals";
		if (DisplayState.xmlFormat == 'lar')
			return "Browse &amp; choose standards";
		else
			return (this.isSelected() ? 'Browse Standards' : 'Browse Standards');
	},
	clickHandler: function (event) {
		event.stop();
		DisplayState.hash = null;
		if (!this.isSelected()) {
			// browseStandards (DisplayState.pathArg);
			DisplayState.displayController.updateDisplay ("standardsDisplay", "both", "tree");
		}
	}
});
	
var SelectedTab = Class.create (DisplayControlTab, {
	initialize: function ($super) {
		$super('Selected');
	},
	isSelected: function () {
		return (DisplayState.displayContent == 'selected');
	},
	getText: function () {
		// res_qual hack:
		if (DisplayState.xmlFormat == 'res_qual')
			return "Enter Resource Quality Info";
		if (DisplayState.xmlFormat == 'lar')
			return "Enter alignment degree info";
		if (DisplayState.xmlFormat == 'comm_anno')
			return "Enter alignment info";
		return (this.isSelected() ? 'Selected Standards' : 'Selected Standards');
	},
	clickHandler: function (event) {
		event.stop();
		DisplayState.hash = null;
		if (!this.isSelected()) {
			// showSelectedStandards (DisplayState.pathArg);
			DisplayState.displayController.updateDisplay ("standardsDisplay", "selected", "list");
		}
	}
});

var SuggestedTab = Class.create (DisplayControlTab, {
	initialize: function ($super) {
		$super('Suggested');
	},
	isSelected: function () {
		return (DisplayState.displayContent == 'suggested');
	},
	getText: function () {
		if (DisplayState.suggestionsPresent)
			return 'Suggested Standards';
		else
			return (this.isSelected() ? 'Suggested Standards' : 'Suggest Standards');
	},
	clickHandler: function (event) {
		// event.stop();
		if (DisplayState.suggestionsPresent) {
			// don't refresh if there is no need
			if (!this.isSelected()) {
				// showSuggestedStandards(DisplayState.pathArg);
				DisplayState.displayController.updateDisplay ("standardsDisplay", "suggested", "list");
			}
		}
		else {
			// doSuggestStandards (event);
			event.stop();
			if (this.noResourceUrlPresent()) {
				var msg = "ResourceUrl is not present or does not begin with http://.";
				msg += "\nPlease edit the ResourceUrl field and try again.";
				alert (msg);
				return;
			}
			
			callback = function () {
				// log ("doSuggestStandards callback");
				DisplayState.suggestionsPresent = true;
			}
			DisplayState.displayController.updateDisplay ("suggestStandards", "suggested", "list", callback, event);			
		}
	},
	
	/* returns TRUE if the current url is not acceptable to the CAT service */
	noResourceUrlPresent: function () {
		var url = DisplayState.resourceUrl.strip();
 		if (!url || !url.startsWith ("http://"))
			return true; 
		return false;
	},
		
	
	mouseOverHandler: function (event) {
		if (!this.isSelected() || !DisplayState.suggestionsPresent) {
			this.cell.addClassName ('over');
			this.textElement.addClassName ('over');
		}
	},
	
	/* called when a sugestion criteria has changed, should indicate that
	   "suggestStandards will yeild new results"
	*/
	activate: function (event) {
		this.textElement.update('Suggest Standards');
	}
});

/* Includes the Tabs as well as the Standard Display and 
functionality for switching between modes, requesting standards, etc
*/
var DisplayController = Class.create ({
	initialize: function () {
		var tab_bar = $('control-tabs');
		var navMenuTable = tab_bar.up('table');
		var navMenuBox = tab_bar.up ('div');
		if (!Prototype.Browser.IE)
			navMenuBox.setStyle({width:navMenuBox.getDimensions().width - 10});
		
		this.simpleSpinner = this._init_simple_spinner();
		this.tabs = $A();
		this.allTab = new AllTab();
		this.tabs.push (this.allTab);
		this.selectedTab = new SelectedTab();
		this.tabs.push (this.selectedTab);
		
		if (DisplayState.serviceIsActive) {
			this.suggestedTab = new SuggestedTab();
			this.tabs.push (this.suggestedTab);
		}
		
		this.tabs.each (function (tab) {
			tab_bar.insert (tab.getCell());
		});
		
		this.standards_display_element = $('standards-display');
		standardsBoxesInit();
	},
	
	_init_simple_spinner: function () {
		var img = $(new Image());
		img.src = "../images/fedora-spinner.gif";
		img.setStyle ({height:"25px",width:"25px"});
		return img;
	},
	
	updateTabs: function () {
		this.tabs.each ( function (tab) {
			tab.decorateCell();
		});
	},
	
	resetSuggestedTab: function (event) {
		// alert ("suggestionCriteriaChanged: " + event.memo);
		DisplayState.suggestionsPresent = false;
		this.suggestedTab.activate(event);
	},
	
	updateDisplay: function (command, displayContent, displayMode, callback, event) {
		if (true) {
			log ("DisplayController:updateStandardsDisplay");
			log ("  displayContent: " + displayContent);
			/* log ("  displayMode: " + displayMode); */
			log ("  command: " + command);
			this.showNavState();
		}
		
		var form = document.forms[0]; // this is the only thing chrome will accept
		if (form == undefined)
			throw "form is undefined";
		form.pathArg.value = DisplayState.pathArg;
		form.command.value = command;
		form.displayContent.value = displayContent;
		form.displayMode.value = displayMode;
		
		try { $('page-messages').hide() } catch (error) {}
		
		// NOTE form must be serialized before this.standards_display_element is
		// modified, or else the checkboxes are lost!!
		var params = $(form).serialize(true);
		params['async'] = "true";
		
		// cache selected values so they aren't lost in case of Ajax failure ...
		var selectedStandardsField = 'enumerationValuesOf(' + params['pathArg'] + ')';
		var selectedStds = params[selectedStandardsField];
		
		// show spinner for suggestions and tree display (since these can take a while)
		if (command == "suggestStandards" && event) {
			showSpinner(event);
		}
		else if (displayMode == "tree") {
			this.standards_display_element.update (this.simpleSpinner);
		}
		
		new Ajax.Request ('edit.do', {
			method:'post',
			parameters: params,
			onComplete: function(transport) {
				var hasError = false;
				try {
					if (transport.responseText.isJSON()) {
						var json = $H(transport.responseText.evalJSON());;
						if (json.get('error')) {
							var error = new Element('div').update ('Error: ' + json.get('error'));
							error.addClassName ('error-msg');
							var errorBox = new Element('div').update (error);
							errorBox.addClassName('page-error-box');
							this.standards_display_element.update (errorBox);
							hasError = true;
						}
					}
					else if (transport.status != 200) {
						hasError = true;
						throw ("ajax response had code: " + transport.status);
					}
				} catch (error) {
					log ("ajax error: " + error);
					hasError = true;
				}
				if (!hasError) {
					log ("DisplayController Ajax updating " + this.standards_display_element.identify());
					this.standards_display_element.update (transport.responseText);
 					DisplayState.displayContent = displayContent;
					DisplayState.displayMode = displayMode;
					this.updateTabs();
					if (callback) {
						callback();
					}
				}
				else { // we have an error but must preserve selected values!
					$A(selectedStds).each ( function (asnId) {
						var input = '<input type="hidden" name="'+selectedStandardsField;
						input += '" value="'+asnId+'" />';
						log (input);
						this.standards_display_element.insert (input);
					}.bind(this));
				
				}

				hideSpinner();
				standardsBoxesInit();
			}.bind(this),
			onFailure: function() { 
				alert('Unable to update standards display') 
			}
		});
	},
	
	showNavState: function () {
		log ("navState");
		log (".. pathArg: " + DisplayState.pathArg);
		log (".. hash: " + DisplayState.hash);
	}

});

var ResQualDisplayController = Class.create (DisplayController, {
	initialize: function ($super) {
		log ("ResQualDisplayController");
		$super();
		log (" ... raDC after super");
		try {
			this.updateReqQualDisplay();
		} catch (error) {
			alert ("ResQualDisplayController.updateReqQualDisplay error: " + error);
		}
	},
	
	getUpdateCommand: function() {
		return "resqualBenchmarks";
	},
	
	getDisplayTarget: function () {
		return $(DisplayState.resQualTarget);
	},
	
	setSuggestCriteriaVisibility: function (displayContent) {
		// log ("setSuggestCriteriaVisibility: displayContent: " + displayContent);
		
		var suggestionCriteria = $('criteria-table')
		
		if (suggestionCriteria) {
			if (displayContent == 'suggested')
				suggestionCriteria.show()
			else
				suggestionCriteria.hide()
		}
	},
	
	/* update display is only called after first page load */
	updateDisplay: function ($super, command, displayContent, displayMode, callback, event) {
		log ("\nRES_QUAL:updateDisplay()");
		log (" .. displayContent: " + displayContent);
		var mycallback = function () {
			// log ("MY call back");
			// this.showNavState();
			if (!DisplayState.hash)
				DisplayState.hash = "asn-standards";
			if (callback) {
				callback;
			}
			this.finalizeDisplay();
		}.bind(this);
		
		callback = function () {
			this.updateReqQualDisplay(mycallback);
		}.bind(this);
			
		try {
			$super (command, displayContent, displayMode, callback, event);
		} catch (error) {
			log ("DisplayController.updateDisplay error: " + error);
		}
		
	}, 
	
	/* gets state from form (serviceHelper) */
	updateReqQualDisplay: function (callback) {
		log ("updateReqQualDisplay");
		// this.showNavState();
		var form = document.forms[0]; // this is the only thing chrome will accept?
		if (form == undefined)
			throw ("updateReqQualDisplay:form is undefined");
		
		var displayContent = form.displayContent.value;
		
		var target = this.getDisplayTarget();
		if (!target)
			throw ("Target not found in DOM");
		
		this.setSuggestCriteriaVisibility(displayContent);
		
		// KLUDGE - res_qual needs this, lar doesn't
		if (DisplayState.xmlFormat == 'res_qual') target.update();
		
		if (displayContent != 'selected') {
			// log ("displayContent is not selected - bailing");
			if ($('doc-selector')) $('doc-selector').show();
			if (callback)
				callback();
			return;
		}

		if ($('doc-selector'))  $('doc-selector').hide();
		
		form.command.value = this.getUpdateCommand();
		var params = $(form).serialize(true);
		params['async'] = "true";
		if (DisplayState.hash)
			params['hash'] = DisplayState.hash
		
		// LOG FORM VALUES ....
		if (false) {
			log ("FORM VALUES before sending edit request to controller");
			$H(params).each (function (pair) {
				log (pair.key + ": " + pair.value);
			});
		}
		
		new Ajax.Request ('edit.do', {
			method:'post',
			parameters: params,
			onComplete: function(transport) {
				// log ('ajax response returned');
				var hasError = false;
				try {
					if (transport.responseText.isJSON()) {
						var json = $H(transport.responseText.evalJSON());;
						if (json.get('error')) {
							target.update ("Display Error: " + json.get('error'));
							hasError = true;
							target.show();
						}
					}
					else if (transport.status != 200) {
						hasError = true;
						throw ("ajax response had code: " + transport.status);
					}
				} catch (error) {
					log ("ajax error: " + error);
				}
				if (!hasError) {
					target.update (this.sanitize (transport.responseText));
					if (callback) {
						log ("calling callback")
						callback();
					}
					else {
						log ("calling finalizeDisplay");
						this.finalizeDisplay();
					}
				}
			}.bind(this),
			onFailure: function() { 
				alert ('Unable to update display benchmarks') 
			}
		});
	},
	
	/* Remove the form brackets from the response (after removing all blank lines).
		This is done by simply ommitting the lines that begin with either '<form'
		or '</form'. This ASSUMES that there are no other tags on the same line
		as the forms!
	
		note: this is necessary for IE, but doesn't hurt for firefox. 
			(it is removing a nested form, after all)
	*/
	sanitize: function (responseText) {
		
		// remove all empty lines and 'form lines'
		var sanitized = responseText.split('\n').select (function (line, index) {
			var stripped = line.strip();
			return (stripped && !stripped.startsWith('<form') && !stripped.startsWith('</form'))
		}).join('\n');
		// log (sanitized);
		return sanitized;
	},
	
	/* take care of final display details */
	finalizeDisplay: function () {
		log ("res_qual - finalizeDisplay");
		var target = this.getDisplayTarget();
		if (!target.visible()) {
			toggleDisplayState (target.identify());
		}
		target.show();
		if (DisplayState.hash)
			setFieldFocus(DisplayState.hash);
	}
	
});

var LarDisplayController = Class.create (ResQualDisplayController, {
	initialize: function ($super) {
		$super();
	},
	
	getUpdateCommand: function() {
		return "larAsnStandards";
	},
	
	getDisplayTarget: function () {
		return $('standards-display');
	},
	
	/* Overloaded from res_qual - it does last bit to set the display of standards the way
	*  we want, like focusing on hash
	*/
	finalizeDisplay: function () {
		log ("LAR: finalize display");
		// this.showNavState();
		var target = this.getDisplayTarget();

		if (DisplayState.hash) {
			log ("hash: " + DisplayState.hash);
			if ($(DisplayState.hash)) {
				log ("  setting focus...");
				setFieldFocus(DisplayState.hash);
				
			}
			else {
				log ("  hash element not defined");
				if (DisplayState.hash.startsWith("_^_records_^_standard")) {
					log ("hash not defined - zooming to asn-standards_");
					setFieldFocus("asn-standards");
				}
			}
		}		
	}	
});

var CommAnnoDisplayController = Class.create (ResQualDisplayController, {
	initialize: function ($super) {
		$super();
		this.asnStdsCache = null;
		this.baseIdPath = '_^_comm_anno_^_ASNstandard';
		this.asnStandardPatt = /valueOf\(\/comm_anno\/ASNstandard_([0-9])+_\)/
		
	},
	
	getSelectedStandards: function () {
		var form = document.forms[0]; // this is the only thing chrome will accept
		if (form == undefined)
			throw "form is undefined";
		
		var params = $(form).serialize(true);

		return $H(params).inject ( [], function (acc, pair) {
			var key = pair.key;
			var val = pair.value;
			
			var match = this.asnStandardPatt.exec(key);
			
			if (match && match[0].length == key.length) {
				// log ("MATCH key: " + val + " (" + typeof(val) + ")");
				if (val.strip())
					acc.push(val);
			}
			return acc;
		}.bind(this));
	},
	
	getUpdateCommand: function() {
		return "commAnnoAsnStandards";
	},
	
	getDisplayTarget: function () {
		return $('standards-display');
	},
	
	getNewlySelectedStandards: function (selectedStandards) {

		if (this.asnStdCache) {
			// log ("the cache has " + this.asnStdCache.size());
			return selectedStandards.inject ([], function (acc, selected) {
				var isCached = this.asnStdCache.find(function (cached) {
					return (cached == selected);
				});
				if (!isCached)
					acc.push (selected)
				return acc;
			}.bind(this));
		}
		
		// log ("no cache");
		return new Array();
	},
		
	
	/* Overloaded from res_qual - it does last bit to set the display of standards the way
	*  we want, like focusing on hash
	*/
	finalizeDisplay: function () {
		// log ("COMM_ANNO: finalize display");
		this.showNavState();
		var target = this.getDisplayTarget();

		//log ("displayMode: " + DisplayState.displayMode);
		// log ("displayContent: " + DisplayState.displayContent);
		
		if (DisplayState.displayContent == 'selected') {
			var selectedStandards = this.getSelectedStandards();
			var newlySelectedStandards = this.getNewlySelectedStandards (selectedStandards);
			if (newlySelectedStandards == null || newlySelectedStandards.size() == 0) {
				// log ('NO NEWLY SELECTEDS');
			}
			else {
			
				var form = document.forms[0]; // this is the only thing chrome will accept
				if (form == undefined)
					throw "form is undefined";
				var params = $(form).serialize(true);
				
				$H(params).each (function (pair) {
					var key = pair.key;
					// log ("KEY: " + key);
					var val = pair.value;
					var match = this.asnStandardPatt.exec(key);
					if (match && match[0].length == key.length) {
						// log ('looking for val: ' + val);
						var isNew = newlySelectedStandards.find(function (std) {
							// log ("  - " + std);
							return (val == std);
						});
						
						if (isNew) {
							
							var id = self.baseIdPath+'_'+match[1]+'_';
							// log ("ensuring " + id + ' is visible');
							var obj = $(id);
							if (!obj) {
								// log ("object not found!");
							}
							else {
								if (!obj.visible()) {
									// log ('.. opening');
									toggleDisplayState(id);
								}
							}
						}
					}
				}.bind(this));
			}
			
			log ("about to set cache");
			this.asnStdCache = selectedStandards;
		}
		
		if (!DisplayState.hash)
			log ('no DisplayState.hash');
		
		if (DisplayState.hash) {
			log ("hash: " + DisplayState.hash);
			if ($(DisplayState.hash)) {
				log ("  setting focus...");
				try {
					setFieldFocus(DisplayState.hash);
				} catch (error) {
					prtln ('could not set focus');
					// this.getDisplayTarget.scrollTo();
				}
				
			}
			else {
				log ("  hash element not defined");
				if (DisplayState.hash.startsWith("_^_comm_anno_^_ASNstandard")) {
					log ("hash not defined - zooming to _ASNstandard_");
					setFieldFocus("standards-display");
				}
			}
		}		
	}	
});

    
var CowItemDisplayController = Class.create (CommAnnoDisplayController, {
	initialize: function ($super) {
		// log ("CowItemDisplayController");
		$super();
		this.baseIdPath = '_^_cowItem_^_educational_^_benchmarks_^_asnId';
		this.asnStandardPatt = /valueOf\(\/cowItem\/educational\/benchmarks\/asnId_([0-9])+_\)/
	},
	
	getUpdateCommand: function() {
		return "cowItemAsnStandards";
	}
});

function standardsBoxesInit () {

	if (!Prototype.Browser.IE) {
		// initializeSuggestionBoxes
		try {
			var boxes = $('standards-display').select('div.suggestion-box');
			boxes.each ( function (box) {
				try {
					new StandardsBox (box);
				} catch (error) {}
			});
		} catch (error) { 
			// alert ("display init error: " + error); 
			log ("display init error: " + error); 
		}
	}
	
	// activate other standards link (in the selected standards display only)
	var otherStdsToggle = $('other-toggle');
	if (otherStdsToggle) {
		otherStdsToggle.observe ('click', toggleOtherSelectedStandards);
		otherStdsToggle.observe ('mouseover', function (event) {
			otherStdsToggle.setStyle ({cursor:'pointer'});
		});
	}
	
	$('standards-display').select ('.use-doc-button').each ( function (button) {
		button = $(button)
		button.observe ('click', function (event) {
			DisplayState.docSelector.changeCurrentStdsDoc (event, button.id);
		});
	});
}

function showSpinner () {
	
	var win = new Window({
		className: 'dcs_alphacube', // bluelighting alphacube mac_os_x 
		// title: "Update Status", 
		width:300, 
		height:100, 
		destroyOnClose: true, 
		recenterAuto:false,
		minimizable:false,
		maximizable:false,
		closable:false,
		showEffectOptions: {duration:0.5},
		hideEffectOptions: {duration:0.3}
	});
	
	// CONTENT!
	// var spinner = DisplayState.displayController.simpleSpinner
	var content = win.getContent();
	var contentHTML = "<div align=\'center'><table height=\'80px\' cellspacing=\'10px\'><tr valign=\'middle'><td>" +
						"<img src=\'../images/fedora-spinner.gif\' height=\'25px\' width=\'25px'>" +
					   "</td><td><h4>Accessing Suggestion Service ...</h4></td></tr></table></div>";
	
	content.update (contentHTML);
	
	// win.setStatusBar("Status bar info"); 
 	win.showCenter(true);
	
}

function hideSpinner () {
	var win = this.parent.Windows.focusedWindow;
	if (win) {
		win.close();
	}
}


/* not currently used */
function doMoreLikeThis (event) {
	showSpinner (event);
	event.stop();
	document.sef.pathArg.value = DisplayState.pathArg;
	document.sef.command.value = "moreLikeThis";
	document.sef.displayContent.value = "suggested";
	document.sef.displayMode.value = "list";
	return doSubmit();
}

