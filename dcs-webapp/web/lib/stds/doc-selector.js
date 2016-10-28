/* ========== Multi-standard controls ==================
supports the selection of a standards document by topic and author.
when current standardsDoc is changed, then the display must be updated ...
*/
var StandardDocSelector = Class.create ({
	initialize: function () {
		this.EXPERIMENTAL = false;
		
		this.currentDocKey = DisplayState.currentDocKey;
		this.mode = DisplayState.displayMode;
		this.content = DisplayState.dispayContent;
		
		this.allDocSelector = $("all-doc-selector");
		
		if (this.EXPERIMENTAL) {
			this.docButton = $("current-doc-button"); // opens docSelector
			this.docButton.observe ('click', this.onDocButtonClick.bind(this));
			this.docLabel = $("current-doc-label"); // shows current doc
			this.updateDocLabel ();
			$('doc-select-done').observe ('click', this.onDoneClick.bind(this));
			$('doc-select-cancel').observe ('click', this.onCancelClick.bind(this));
		}
		
		this.docSelector = $("current-doc-selector"); // contains author and topic selectors
		
		this.authorSelector = $("author-selector");
		this.authorSelector.options[0] = new Option (" -- select author -- ", "");
		
		if (!this.EXPERIMENTAL)
			this.authorSelector.observe ('change', this.onAuthorChange.bindAsEventListener (this));
		
		this.topicSelector = $('topic-selector');
		this.topicSelector.options[0] = new Option (" -- select topic -- ", "");
		this.topicSelector.observe ('change', this.onTopicChange.bindAsEventListener (this));
		
		// availableDocs initialized in jsInit.js 
		DisplayState.availableDocs.getTopics().each ( function (topic) {
			var option = new Option (topic, topic);
			if (this.currentDocKey.indexOf (topic) != -1) option.selected = 1;
			this.topicSelector.options[this.topicSelector.options.length] = option;
		}.bind (this));
		
		this.updateAuthorSelector ();

		// allDocSelector is not currently used
		if (this.allDocSelector) {
			DisplayState.availableDocs.keys().each ( function (key) {
				var docInfo = DisplayState.availableDocs.getDocInfo (key)
				var option = new Option (docInfo.authorName, docInfo.key, this.currentDocKey == docInfo.key);
				this.allDocSelector.options[this.allDocSelector.options.length] = option;
				$(option).setAttribute ("title", docInfo.key);
			}.bind (this));
			
			this.allDocSelector.observe ('change', this.onChange.bindAsEventListener (this));
		}
		
		// log ("calling changeCurrentStdsDoc");
		this.changeCurrentStdsDoc (null, this.currentDocKey);
		
		// inputs popup through blanket in IE
		if ((Prototype.Browser.IE)) {
			document.observe ('blanket:activated', function (event) {
					this.authorSelector.hide();
					this.topicSelector.hide();
			}.bind (this));
			
			document.observe ('blanket:deactivated', function (event) {
					this.authorSelector.show();
					this.topicSelector.show();
			}.bind (this));
		}
		
	},
	
/* 	setCurrentDoc: function (event, docKey) {
		this.changeCurrentStdsDoc (event, docKey);
	}, */
		
	
	updateDocLabel: function () {
		var docInfo = DisplayState.availableDocs.getDocInfo (this.currentDocKey);
		var labelText = [docInfo.author, docInfo.topic, docInfo.created].join (' - ');
		var mainLabel = new Element ('div').update (labelText);
		mainLabel.addClassName ('std-doc-label');
		this.docLabel.update (mainLabel);
		var subLabel = new Element ('div').update (docInfo.title);
		subLabel.addClassName ('std-doc-title');
		this.docLabel.insert (subLabel);
	},
	
	onDocButtonClick: function (event) {
		this.docSelector.show();
		this.docButton.hide();
		this.docLabel.hide();
	},
	
	onCancelClick: function (event) {
		this.docSelector.hide();
		this.docButton.show();
		this.docLabel.show();
	},
	
	onDoneClick: function (event) {
		var newDocKey = $F(this.authorSelector);
		this.changeCurrentStdsDoc (event, newDocKey);
		this.onCancelClick(event);
	},
	
	onTopicChange: function (event) {
		var topic = $F(this.topicSelector);
		this.updateAuthorSelector (topic);
	},
	
	/* Populate the author selector with options determined by the
		value of the topicSelector
	*/
	updateAuthorSelector: function () {
		// log ('StandardDocSelector:updateAuthorSelector');
		var topic = $F(this.topicSelector);
		if (!topic) return;
		var selector = this.authorSelector;
		
		//clear all but first option
		for (var i=selector.options.length-1;i>0;i--) selector.options[i] = null;
		
		DisplayState.availableDocs.getTopicDocs(topic).each ( function (docInfo) {
			var option = new Option (docInfo.authorName, docInfo.key);
			if (this.currentDocKey == docInfo.key) option.selected = 1
			selector.options[selector.options.length] = option;
			
			// make tooltip from title and year if necessary
			var tooltip = docInfo.title
			var year = docInfo.created
			if (tooltip.indexOf (year) == -1)
				tooltip += " (" + year + ")";
			$(option).setAttribute ("title", tooltip);
		}.bind (this));
	},
	
	// not used when in Expermental mode
 	onAuthorChange: function (event) {
		// log ('StandardDocSelector:onAuthorChange');
		var newDocKey = $F(this.authorSelector);
		this.changeCurrentStdsDoc (event, newDocKey);
	},
	
	onChange: function (event) {
		var newDocKey = $F(this.allDocSelector);
		this.changeCurrentStdsDoc (event, newDocKey);
	},
	
	// if CAT is not configured to suggest from the current document, then disable the suggest tab!
	changeCurrentStdsDoc: function (event, newDocKey) {
 		/*
		log ("changeCurrentStdsDoc: newDocKey: " + newDocKey +
			"   currentDoc: " + this.currentDocKey);
		log ("DisplayState.displayContent: " + DisplayState.displayContent);
		*/
			
		// don't wory about disableSuggestedTab and noCatDocs, since the cat service is not active
		if (DisplayState.serviceIsActive) {
		
			var noCatDocs = DisplayState.noCatDocs;
			var disableSuggestedTab = false;
			if (noCatDocs) {
				// noCatDocs.report()
				if (noCatDocs.getDocInfo (newDocKey)) {
					alert ("WARNING: " + newDocKey + " is not currently loaded in CAT");
					disableSuggestedTab = true;
				}
			}
	
			/* suggestedTab is not defined with the service is not active */
			if (DisplayState.displayController.suggestedTab) {
				if (disableSuggestedTab) {
					if (DisplayState.displayController.suggestedTab.isSelected()) {
						DisplayState.displayContent = 'selected'; // show selected, cause we can't show suggested ...
					}
					DisplayState.displayController.suggestedTab.hide();
					
				}
				else {
					DisplayState.displayController.suggestedTab.show();
				}
			}
		}
			
			
		if (!newDocKey || newDocKey == this.currentDocKey) {
			// log ("no need to update display - bailing");
			return;
		}
		
		// blur focus from authorSelect so that subsequent scrolls don't trigger search
		// window.focus(); no longer needed
		document.sef.currentStdDocKey.value = newDocKey;
		this.currentDocKey = newDocKey;
		
		if (this.EXPERIMENTAL)
			this.updateDocLabel ();
		DisplayState.suggestionsPresent = false;
		
		log ("** about to update display: DisplayState.displayContent: " + DisplayState.displayContent);
		
		if (DisplayState.displayContent == 'suggested') {
			DisplayState.displayController.updateDisplay ("suggestStandards", "suggested", "list", callback, event);
		}
		else if (DisplayState.displayContent == 'selected') {
			DisplayState.displayController.updateDisplay ("standardsDisplay", "selected", "list");
		}
		else {
			DisplayState.displayController.updateDisplay ("standardsDisplay", "both", "tree");
		}
		return;
	}
}); 

