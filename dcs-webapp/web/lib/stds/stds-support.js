/*
	support for standards display actions
*/

var DisplayState = {
	xmlFormat : null,
	displayMode : null,
	displayContent : null,
	pathArg : null,
	currentDocKey : null,
	serviceIsActive : null,
	suggestionsPresent : null,
	
	// gradeRange info
	gradeRangeOptions : null,
	startGrade : null,
	endGrade : null,
	
	resourceUrl: null,

	// docSelector info
	availableDocs : null,
	
	noCatDocs : null,
	
	optionalFields : null,
	hasSubjects : null,
	
	// controllers	
	docSelector : null,
	gradeRangeController : null,
	keywordsController : null,
	displayController : null,
	resQualTarget : null
}

/* Holds the standards documents (docInfo instances) in two hashes:
- keyHash - map from key to docInfo for all available docs
- TopicHash - map from topic to list of keys for each unique topic in available docs

Used to create topic selector and to populate doc selector 
*/
var StdDocList = Class.create ({
	initialize: function (name) {
		this.name = name
		this.topicHash = $H();
		this.keyHash = $H();
	},
	
	keys: function () {
		return this.keyHash.keys().sort();
	},
	
	size: function () {
		return this.keyHash.size();
	},
	
	getDocInfo: function (key) {
		return this.keyHash.get(key);
	},
	
	/* get keys of Docuements having specified topic (aka subject) */
	getTopicDocs: function (topic) {
		// return this.topicHash.get(topic).inject ([], function (array, key) {
		return this.topicHash.get(topic).sort().collect (function (key) {
			return this.getDocInfo (key);
		}.bind(this));
	},
		
	/* get a sorted list of topics available */
	getTopics: function () {
		return this.topicHash.keys().sort();
	},
	
	/* add provided docInfo to avaliableDocs */
	add: function (docInfo) {
		// docInfo is a hash of props for a standardsDoc. see jsInit.jspf
		// log ("adding " + docInfo['key']);
		var topic = docInfo.topic;
		var key = docInfo.key
		if (!this.topicHash.get(topic))
			this.topicHash.set(topic, $A());
		this.topicHash.get(topic).push (key);
		this.keyHash.set(docInfo.key, docInfo);
	},
	
	/* utility to print out a representation of the AvailableStdDocs instance */
	report: function () {
		log (this.name);
		log ("\t my size is " + this.size());
		
		this.topicHash.keys().each ( function (topic) {
			log (topic);
			this.getTopicDocs (topic).each (function (docInfo) {
				log ("\t" + docInfo.title);
			});
		}.bind(this))
	}
});

var KeywordsController = Class.create ({
	initialize: function () {
		
		this.element = $('cat-keywords');
		
		/* keywords controller is always shown so user can type in values,
		   but if there is no metadata field we suppress the reset button
		*/
		if (!DisplayState.hasOptionalField ('keywords')) {
			$('keyword-reset-button').hide();
		}
		else {
			$('keyword-reset-button').observe ('click', this.reset.bindAsEventListener (this));
		}
		
		
/* 		
		$('keyword-reset-button').observe ('click', this.reset.bindAsEventListener (this));
		if (DisplayState.xmlFormat == 'mast' || DisplayState.xmlFormat == 'mast_demo')
			$('keyword-reset-button').hide(); */
		
		// keywords.value is initialized in place (see controls.tag)
		var value = $F('keywords')
		if (!value) {
			$('keywords-enabled-box').checked = false;
			// this.disable();
		}
		else {
			// this.enable();
			// log ("value, so checked = TRUE");
		}
	},
	
	disable: function () {
		$('keyword-reset-button').disable();
		$('keywords').disable();
	},
	
	enable: function () {
		$('keyword-reset-button').enable();
		$('keywords').enable();
	},
	
	reset: function (event) {

		log ("reset");
		var button = event.element();
		var form = $(document.sef);
		var params = form.serialize (true);
		params['command'] = 'async';
		params['updatefield'] = 'keywords';
		new Ajax.Request ('edit.do', {
			method:'post',
			parameters: params,
			onSuccess: function (transport) {
				var response = transport.responseText;
				// log ("async info: " + response);
				try {
					var json = response.evalJSON();
				} catch (error) { 
					alert ("Error evaluating JSON: " + error);
					return;
				}
					
				// make sure we have an array of keywords (not a single string)
				var recordKeywords = json.keywords.keyword;
				recordKeywords = Object.isString (recordKeywords) ?
					$A([recordKeywords]) : $A(recordKeywords);
				
				// now update the control (which is a textarea)
				document.sef.keywords.value = recordKeywords.join (", ");
				
				if (DisplayState.hasOptionalField ('keywords'))
					alert ("keywords have been reset to the metadata values for this record");
				button.blur();
			}.bind(this),
			onFailure: function() { 
				alert('Something went wrong trying to obtain keywords ...') 
			}
		});
	}, 
	isEmpty: function () {
		return 	$F('keywords').strip() == "";
	},
	
	handleUseKeywords : function (event) {
		log ("handleUseKeywords!");
 		var button = event.element();
		if (button.checked) {
			log ("  button is CHECKED");
			// $('keywords').enable();
			// this.enable();
		}
		else {
			log ("  button is NOT checked");
			// this.disable();
		}
		if (button.checked && this.isEmpty(event))
			this.reset(event); 
	}
});
	
var SubjectsController = Class.create ({
	initialize: function () {
		
		this.element = $('cat-subjects');
		
		/* subjects controller is only shown when specified by helper
		*/
		if (!DisplayState.hasOptionalField ('subjects')) {
			$('subjects-enabled-box').checked = false;
			return;
		}
		
		this.element.show();
/* 		
		$('keyword-reset-button').observe ('click', this.reset.bindAsEventListener (this));
		if (DisplayState.xmlFormat == 'mast' || DisplayState.xmlFormat == 'mast_demo')
			$('keyword-reset-button').hide(); */
		
		// keywords.value is initialized in place (see controls.tag)
		// var value = $F('subjects') // we are not using input for subjects!
		if (!DisplayState.hasSubjects) {
			// log ("no subjects");
			$('subjects-enabled-box').checked = false;
			// $('subjects-enabled-box').disable();
		}
		else {
			// log ("displayState.hasSubjects");
		}
	},
	
	isEmpty: function () {
		return 	$F('keywords').strip() == "";
	},
	
	/* triggered when enable-subject box is clicked. since subjects doesn't
	accept any input, the reset function (and hence this function) has no
	real purpose...
	*/
	handleUseSubjects : function (event) {
		log ("handleUseSubjects!");
 		var button = event.element();
		if (button.checked) {
			log ("  button is CHECKED");
		}
		else {
			log ("  button is NOT checked!");
		}
/* 		if (button.checked && this.isEmpty(event))
			this.reset(event);  */
	}
});


/*
	controls the gradeRanges selector in the controller table
*/
var GradeRangeController = Class.create ({
	initialize: function () {
		this.gradeRangeOptions = DisplayState.gradeRangeOptions;
		if (!this.gradeRangeOptions || this.gradeRangeOptions.size() == 0) {
			log ("no gradeRange options provided");
			$("cat-grade-ranges").hide();
			return;
		}
		else {
			$("cat-grade-ranges").show();
		}
		this.initControl();
		this.update (DisplayState.startGrade, DisplayState.endGrade);
		if (this.isEmpty()) {
			$('gr-enabled-box').checked = false;
			// this.disable();
		}
		$('gr-reset-button').observe ('click', this.reset.bindAsEventListener (this));
	},
	
	/* initialize the gradeSelect dropdowns using the gradeRangeOptions attribute,
		and setting current selection for both to the "default option" */
	initControl: function () {
		var startSelect = $('startGrade-select');
		var endSelect = $('endGrade-select');
		
		// add a default option to the front of the gradeRangeOptions
		var defaultOption = {label:"-- choose --", value:"-1"};
		var selectOptions = $A([defaultOption].concat (this.gradeRangeOptions));
		selectOptions.each (function ( option, index ) {
			var defaultSelected = (index == 0); // default is selected
			// IE selects apparently don't prototype's insert methods
			startSelect.options[startSelect.options.length] = 
				new Option (option.label, option.value, defaultSelected);
			endSelect.options[endSelect.options.length] = 
				new Option (option.label, option.value, defaultSelected);
		});
	},
	
	update: function (startGrade, endGrade, msg) {
		// log ("GradeRangeController:update: " + startGrade + ", " + endGrade);
		var startSelect = $('startGrade-select');
		$A(startSelect.options).each ( function (option ) {
				option.selected = (option.value == startGrade ? true : false);
			});
		
		var endSelect = $('endGrade-select');
		$A(endSelect.options).each ( function (option ) {
			option.selected = (option.value == endGrade);
		});
		if (msg) {
			// alert (msg);
		}
	},
	
	isEmpty: function (event) {
		// log ("isEmpty()");
		var start = $F('startGrade-select');
		var end = $F('endGrade-select');
		// log ("start: '%s', end: '%s'", start, end);
		return start == '-1' && end == '-1';
	},
	
	disable: function () {
		$('startGrade-select').disable();
		$('endGrade-select').disable();
		$('gr-reset-button').disable();
	},
	
	enable: function () {
		$('startGrade-select').enable();
		$('endGrade-select').enable();
		$('gr-reset-button').enable();
	},
	
	reset: function (event) {
		var button = event.element();
		var form = $(document.sef);
		var params = form.serialize (true);
		params['command'] = 'async';
		params['updatefield'] = 'gradeRanges';
		new Ajax.Request ('edit.do', {
			method:'post',
			parameters: params,
			onSuccess: function (transport) {
				var response = transport.responseText;
				// log ("async info: " + response.trim());
				try {
					var json = response.evalJSON();
				} catch (error) { 
					alert ("Error evaluating JSON: " + error);
					return;
				}
					
				var startGrade = json.gradeRanges.startGrade;
				var endGrade = json.gradeRanges.endGrade;
				
				// log ("selectedStartOptionValue: " + startGrade);
				// log ("selectedEndOptionValue: " + endGrade);
	
				var msg = "grade range values have been reset to the metadata values for this record";
				this.update (startGrade, endGrade, msg);
				button.blur();
			}.bind(this),
			onFailure: function() { 
				alert('Something went wrong...') 
			}
		});
	}, 
	handleUseGradeRange : function (event) {
		log ("handleUseGradeRange!");
		var button = event.element();
		if (button.checked) {
			log ("  button is CHECKED");
			// this.enable();
		}
		else {
			log ("  button is NOT checked");
			// this.disable();
		}
		if (button.checked && this.isEmpty(event))
			this.reset(event);
	}
});
		
/* NOT USED */
function getWindowSize (event) {
	var width, height;
	w = window;
/* 	var dims = $(document.body).getDimensions();
	alert ("width: " + dims.width + ", height: " + dims.height);
	return { width: dims.width, height: dims.height }; */
	
	if (Prototype.Browser.IE) {
		// alert ("IE Sucks: " + w.innerHeight + ", " + w.document.documentElement.clientHeight + 
		//	", " + w.document.body.clientHeight);
		// width = w.innerWidth || (w.document.documentElement.clientWidth || w.document.body.clientWidth);
		// height = w.innerHeight || (w.document.documentElement.clientHeight || w.document.body.clientHeight);
		width = document.body.offsetWidth;
		// height = event.pointerY() + document.body.offsetHeight;
		height = event.pointerY() + 1000;
		$(document.body).setStyle({ overflow:'hidden'});
		return { width: width, height: height };
	}
	else
		return document.viewport.getDimensions();
}


/* toggles display of other selected standards (in the "selected" standards display)
	clickHandler for the 'other-toggle' element (see display.tag) */
function toggleOtherSelectedStandards () {
	var id = 'other-selected-standards';
	var element = $( id );
	var img = $('other-toggle_img');
	if ( element ) {
		if ( element.visible() ) {
			element.hide ();
			img.src = '../images/closed.gif';
		}
		else {
			element.show ();
			img.src = '../images/opened.gif';
		}
	}
	else {
		log ("element was null for " + id);
	}
}

/* StandardsBox supporting truncation of text, with 'more' and 'less' links */
var StandardsBox = Class.create({
	initialize: function (box) {
		this.box = box;
		this.labelElement = $(this.box.down ('label'));
		this.itemText = this.labelElement.innerHTML;
		this.maxLen = 250;
		this.moreLink = null;
		this.lessLink = null;
		this.linkPad = "&nbsp;&nbsp;&nbsp;";

		if (this.itemText.length > this.maxLen) {
			/* KLUDGE - math standards contain img tags for math symbols, which,
			   when split, cause trouble (resulting in editor request with no params,
			   which hoses the editor's state, resulting in a "MissingLockError").
			   So for now, DO NOT TRUNCATE standards containing IMG tags, but are we sure
			   that truncating ANY tag will not result in a problem??
			*/

			if (this.itemText.indexOf ('<img') == -1) {
				this.truncate ();
			}
		}
		this.initializeSelectHandler();
	},
	initializeSelectHandler: function () {
		this.labelElement.observe ('click', function ( event ) {
			
			var label = event.element();
			var input = label.previous('input');
			
			if (DisplayState.xmlFormat == "res_qual" && input.checked) {
				return;
			}
			
			var box = label.up ('div.suggestion-box');
			// state of input is when the event occurred (BEFORE input has changed)
			if (!input.checked)
				box.addClassName ("checked");
			else
				box.removeClassName ("checked");
		});
		
		this.box.down('input[type="checkbox"]').observe ('click', function ( event ) {
			var input = event.element();
			
			// res_qual - selected standards do not react to mouse input
			// NOT WORKING???
			if (DisplayState.xmlFormat == "res_qual" && (!input || input.checked)) {
				return;
			}
			
			var box = input.up ('div.suggestion-box');
			// state of input is AFTER the input has changed
			if (input.checked)
				box.addClassName ("checked");
			else
				box.removeClassName ("checked");
		});
		
	}, 
	getMoreLink: function () {
		if (!this.moreLink) {
			this.moreLink = new Element ("a", {href:"#"}).update ('more');
			this.moreLink.observe ('click', this.expand.bindAsEventListener (this));
		}
		return this.moreLink;
	},
	getLessLink: function () {
		if (!this.lessLink) {
			this.lessLink = new Element ("a", {href:"#"}).update ('less');
			this.lessLink.observe ('click', this.truncate.bindAsEventListener (this));
		}
		return this.lessLink;
	},
	truncate: function (event) {
		if (event)
			event.stop();
		this.labelElement.update (this.itemText.truncate(this.maxLen, ' ...'));
		this.labelElement.insert (this.linkPad, {position:'bottom'}); 
		this.labelElement.insert (this.getMoreLink(), {position:'bottom'});
	},
	
	expand: function (event) {
		event.stop();
		this.labelElement.update (this.itemText);
		this.labelElement.insert (this.linkPad, {position:'bottom'});
		this.labelElement.insert (this.getLessLink(), {position:'bottom'});
	}
});

/* Called on dom:loaded, Initialize the ASN Standards Display Controller and place it in the DisplayState */
function stdSupportInit (event) {
	
	try {
		if (DisplayState.xmlFormat == 'res_qual') {
			DisplayState.displayController = new ResQualDisplayController();
		}
		else if (DisplayState.xmlFormat == 'lar') {
			DisplayState.displayController = new LarDisplayController();
		}
		else if (DisplayState.xmlFormat == 'comm_anno') {
			DisplayState.displayController = new CommAnnoDisplayController();
		}
		else if (DisplayState.xmlFormat == 'cow_item') {
			log ("instantiating CowItemDisplayController");
			DisplayState.displayController = new CowItemDisplayController();
			log (" ... instantiated CowItemDisplayController");
		}
		else
			DisplayState.displayController = new DisplayController();
	} catch (error) {
		log ("initializeDisplayController error: " + error);
	}
	
	if (DisplayState.serviceIsActive) {		
		DisplayState.gradeRangeController = new GradeRangeController();
		DisplayState.keywordsController = new KeywordsController();
		try {
			DisplayState.subjectsController = new SubjectsController();
		} catch (error) {
			log ("subjectsController error: " + error);
		}
		
		// catch event signaling that the suggestion search criteria has changed
		document.observe ("ctrl:criteriaChanged", function (event) {
			DisplayState.displayController.resetSuggestedTab(event);
		});
		
		// handlers for events triggered by checkboxes that en/disable field controls
 		document.observe ("ctrl:useGradeRangesChanged", function (event) {
			DisplayState.gradeRangeController.handleUseGradeRange (event);
		});
		
		document.observe ("ctrl:useKeywordsChanged", function (event) {
			DisplayState.keywordsController.handleUseKeywords (event);
		});
		
		document.observe ("ctrl:useSubjectsChanged", function (event) {
			DisplayState.subjectsController.handleUseSubjects (event);
		});
		
	}
	
	// if there are no availableDocs then there is no need for a DocSelector
	if (DisplayState.availableDocs) {
		DisplayState.docSelector = new StandardDocSelector ();
	}
	
	if (Prototype.Browser.IE) {
		var navMenu = $$ (".nav-menu-box")[0];
		// setElementWidth is defined in metadata-editor-support.js
		setElementWidth('standards_MultiBox', navMenu);
	}
	
	// standardsBoxesInit(); // NOW DONE IN DIPSLAY CONTROLLER
	// $('keyword-reset-button').observe ('click', resetKeywords);
	
	// log ("... stdSupportInit finished");
}

document.observe ("dom:loaded", stdSupportInit);

