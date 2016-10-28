/* javascript functions in support of metadata editor */

var closed_img, opened_img;
var metadata_editor_support_debug = false;

var pages = $A();
var currentPage = null;
var muiExpander = null;

var Page = Class.create({
	initialize: function (name, mapping) {
		this.name = name;
		this.mapping = mapping;
	}
});

var MuiExpander = Class.create({
	_execute: function (parentid, exOrCo) {
		$$('table[id="'+parentid+'"] * a[href^="javascript:toggleDisplayState"]').each(
			function(a){
				var elem = a.href.match(/javascript:toggleDisplayState\('(.+)'\);/)[1];
				if( (exOrCo=='expand' && !$(elem).visible()) || (exOrCo=='collapse' && $(elem).visible()))
					toggleDisplayState( elem );
			});
	},
	
	expandAll: function(parentid) {
		this._execute(parentid, 'expand');
	},
	collapseAll: function(parentid) {
		this._execute(parentid, 'collapse');
	}
});

function notify (msg) {
	try {
		log (msg)
	} catch (error) {}
}

/* set up mouse handlers for page menu across top of editor pages */
function initializeNavMenu() {
	// log ("initializeNavMenu");
	var pagenav = $('page-nav');
	var pageCount = pages.size();
	pages.each (function (page) {
		var cell = new Element ('td');
		cell.addClassName ('nav-menu-item');
		if (pageCount < 5)
			cell.setStyle ({width:(100/pageCount)+"%"});
		var text = new Element ('div').update (page.name);
		text.addClassName ("nav-menu-text");
		cell.update (text);		
		
		if (page.mapping == currentPage) {
			cell.addClassName ('selected');
			// text.addClassName ('current');
		}
		else {
			cell.observe ('mouseover', function (event) { 
				cell.addClassName ('over');
				text.addClassName ('over');
			});
			cell.observe ('mouseout', function (event) { 
				cell.removeClassName ('over');;
				text.removeClassName ('over');
			});
			cell.observe ('click', function (event) { changeForm (page.mapping) });
		}
		pagenav.insert (cell);
	});
}


/* preload collapsible node images */
function preloadImages () {
	if (document.images)
		{
			closed_img = new Image(10,10);
			closed_img.src = "../images/closed.gif";
			
			opened_img = new Image(10,10);
			opened_img.src = "../images/opened.gif";
		}
	else {
		notify ("document.images not found!")
	}
}

var cachedBorder="none";
var highlightBorder="red 1px dashed";

function highlightBox( elementID ) {
	// alert ("highlightBox(" + elementID + ")");
	var objElement = $( elementID );
	if ( objElement != null ) {
			cachedBorder = objElement.style.border;
			objElement.style.border = highlightBorder;
	}
	else {
		notify ("toggleDisplayState() objElement not found: " + elementID);
	}
}

function unhighlightBox( elementID ) {
	// alert ("toggleDisplayState(" + elementID + ")");
	var objElement = $( elementID );
	if ( objElement != null ) {
			// alert ("about to apply cashed border: " + cachedBorder);
			objElement.style.border = cachedBorder;
			cachedBorder = "none";
	}
	else {
		notify ("toggleDisplayState() objElement not found: " + elementID);
	}
}

var effects_on = 1;
var show_effect = Effect.Appear; // show Grow Appear BlindDown
var hide_effect = Effect.Fade; // hide Shrink Fade BlindUp
var effect_args = {duration:.3}

/*
change the display state of an expandable element, also updating corresponding
widget if one exists.
state is preserved via a hidden variable (elementID_displayState)
that is defined in the html
*/
function toggleDisplayState( elementID ) {
	var objElement = $( elementID );
	var objImg = $( elementID + "_img" );
	var objState = $( elementID + "_displayState" );
	var inputHelper = $( elementID + "_inputHelper" );
	// log ("toggleDisplayState(): " + elementID);
	
	if ( objElement ) {
		if ( objElement.visible() ) {
			if (effects_on) {
				new hide_effect (elementID, effect_args);
				if (inputHelper)
					new hide_effect (inputHelper, effect_args);
			}
			else {
				objElement.hide ();
				if (inputHelper)
					inputHelper.hide ();
			}
			if (objImg != null)
				objImg.src = closed_img.src;
			if (objState != null)
				objState.value = 'none';
		}
		else {
			
			if (effects_on) {
				new show_effect (elementID, effect_args);
				if (inputHelper)
					new show_effect (inputHelper, effect_args);
			}
			else {
				objElement.show ()
				if (inputHelper)
					inputHelper.show();				
			}
			
			if (objImg != null)
				objImg.src = opened_img.src;
			if (objState != null)
				objState.value = 'block';
		}
	}
	else {
		notify ("toggleDisplayState() objElement not found: " + elementID);
	}
}

// support for collapsible mui nodes
function toggleMuiVisibility( elementID ) {
	// alert ("toggleVisiblity(" + elementID + ")");
	var objElement = $( elementID );
	var objImg = $( elementID + "_img" );
	if ( objElement != null ) {
		if ( objElement.style.display == "none" ) {
			objElement.show ();
			if (objImg != null) {
				objImg.src = "../images/btnExpand.gif";
			}
		}
		else {
			objElement.hide ();
			if (objImg != null) {
				objImg.src = "../images/btnExpandClsd.gif";
			}
		}
	}
	else {
		notify ("objElement was null for " + elementID);
	}
}

// stateless version of toggleDisplayState
function toggleVisibility( elementID ) {
	// log ("toggleVisiblity(" + elementID + ")");
	var objElement = $( elementID );
	if ( objElement ) {
		if ( objElement.visible() ) {
			objElement.hide ();
		}
		else {
			objElement.show ();
		}
	}
	else {
		notify ("objElement was null for " + elementID);
	}
}

/*
open a new window and load href
*/
function doView (href) {
	openBestPracticesWindow (href);
	return false;
}

/*
Check to see if record is dirty, and if so, get confirmation from user before
exiting.
*/
function guardedExit (href) {
/* 	log ("guardedExit()");
	log ("  href: " + href);
	log ("action: " + document.forms[0].action); */
	
	// make ajax call to determine if the record is dirty
	var params = $(document.forms[0]).serialize(true);
	params['command'] = "dirtyDocCheck";
	
	new Ajax.Request (document.forms[0].action, {
		method:'post',
		parameters: params,
		onComplete: function(transport) {
			var resp = transport.responseText;
			try {
				if (!resp.isJSON()) {
					log ("response ain't json!");
				}
				var json = $H(resp.evalJSON());;
				if (json.get('error')) {
					this.standards_display_element.update ("Error: " + json.get('error'));
					hasError = true;
				}
				var isDirty = json.get("docIsDirty") == 'true';
				if (isDirty == null)
					throw "illegal json response";
				
				if (isDirty) {
					if (!confirm ("You have unsaved changes. Do you REALLY want to exit the editor?")) {
						return;
					}
				}
			} catch (error) {
				log ("guardedExit error .. abandoning this page");
			}
			return doExit(href);
		}
	});
}

/* exit the editor, optionally passing forwarding Href to controller */
function doExit (forwardHref) {
	// log ("doExit() forwardHref:" + forwardHref);
	if (forwardHref) {
		if (forwardHref != "forwardToCaller" ) {
			// the path we get here is domain-relative, and it must be context-relative for
			// consumption by controller - chop off first segment of provided path
			var contextRelativePath = "/" + forwardHref.split("/").findAll ( function (seg, i) {
				return (i > 1); /*  */
			}).join ("/");
			forwardHref = contextRelativePath;
		}
		document.forms[0].pathArg.value = forwardHref;
	}
	document.forms[0].command.value = "exit";
	return doSubmit();
}

/* 
used for hand-crafted tool help. Takes a topic name and constructs a url to that topic within
the tool-help directory, where static files exist for each tool-help topic
*/
function doToolHelp (topic) {
	var url = "../tool-help/" + topic + ".html";
	openBestPracticesWindow (url);
	return false;
}

/* load another editor page. pathArg, when present will cause the element it specifies to
   be exposed in editor
*/
function changeForm (nextPage, pathArg) {
	if (pathArg != null) {
		// alert ("pathArg: " + pathArg);
		document.forms[0].pathArg.value = pathArg;
	}
	document.forms[0].nextPage.value = nextPage;
	document.forms[0].command.value = "changeForm";
	return doSubmit();
}


/* Save the metadata and continue editing */
function doSave (id) {
	if (document.forms[0].recId.value == "") {
		alert ("this record has no ID! please use the \"Record ID\" field and \"Change ID\" button to assign a record ID");
		return false;
	}
	document.forms[0].command.value = "save";
	return doSubmit();
}

/* Save the metadata and continue editing */
function doSaveSimilar (id) {
	if (document.forms[0].recId.value == "") {
		alert ("this record has no ID! please use the \"Record ID\" field and \"Change ID\" button to assign a record ID");
		return false;
	}
	document.forms[0].command.value = "saveSimilar";
	return doSubmit();
}

function doValidate () {
	document.forms[0].command.value = "validate";
	return doSubmit();
}

/* create a new element at the specified path */
function doNewElement (xpath) {
	document.forms[0].command.value = "newElement";
	document.forms[0].pathArg.value = xpath;
	return doSubmit();
}

function doNewChoice (xpath) {
	if (xpath == null || xpath == "") {
		alert ("please make a choice");
		return false;
	}
	else {
		// alert ("!doNewChoice with '" + arg + "'");
		return doNewElement (xpath);
	}
}

/* convey the user's choice to the controller */
function doChoice (xpath) {
	// alert ("new choice: " + xpath);
	if (xpath == "") {
		alert ("please make a choice");
		return false;
	}
	document.forms[0].command.value = "choice";
	document.forms[0].pathArg.value = xpath;
	return doSubmit();
}

// NOTE: only in STANDALONE (if even then)
function doChangeId (id) {
	if (id == "") {
		alert ("please supply a record ID");
		return false;
	}
	document.forms[0].command.value = "changeID";
	document.forms[0].pathArg.value = id;
	return doSubmit();
}

/* delete the element at the specified path */
function doDeleteElement (xpath) {
	if (confirm ("Are you sure you want to remove this field and any subfields it contains?")) {
		document.forms[0].command.value = "deleteElement";
		document.forms[0].pathArg.value = xpath;
		return doSubmit();
	}
}

/* delete an element of xsd:any type. This is done by clearing the content
   of the element and then calling changeForm to refresh the display (which has the side
   effect of removing any anyTypeElements).
*/
function doDeleteAnyElement (xpath) {
	
	if (confirm ("Are you sure you want to remove this field?")) {
		var name = "anyTypeValueOf(" + xpath + ")";
		var selector = "textarea[name='" + name + "']";
		var inputs = $$(selector);
		if (inputs) {
			inputs.first().value = "";
			changeForm (currentPage, xpath);
		}
		else 
			alert ("Sorry! The system is unable to delete this element!");
	}
}

/* delete the choice element at the specified path */
function doDeleteChoiceElement (xpath, id) {

	if (confirm ("Delete CHOICE: Are you sure you want to remove this field and any subfields it contains?")) {
		document.forms[0].command.value = "deleteElement";
		document.forms[0].pathArg.value = xpath;
		return doSubmit();
	}

}

/* submit the current form for validation */
function doSubmit() {
	
	/*  before submitting, we have to grab the form elements that have been inserted into the DOM after it
		was loaded. Firefox seems to be the only browser that munges an ajax form in with the existing form.
		All others keep the dynamically added elements separate, and therefore if we are not careful, these
		elements are not passed when the main form ("metadata-form") is submitted.
	*/
	
	mungeFormFieldElements ();

	document.forms[0].method = "post";
	document.forms[0].submit();
	return true;
}

/* collect elements from forms other than the main one (i.e., document.forms[0]) and
add their values to the main form as hidden elements. */
function mungeFormFieldElements() {
	var mainForm = document.forms[0];
	$$("form").each ( function (otherForm) {
		if (otherForm != mainForm) {
			// alert ("munging otherForm: " + otherForm.name + " (id: " + otherForm.identify() + ")");
			$A(otherForm.elements).each (function (el) {
				try {
					/* don't munge null-valued radio buttons 
						- WHAT ABOUT CHECKBOXES?? - they should be okay, since we want to know
						about all their values.
					*/
					if (el.readAttribute ("type") == "radio" && !el.getValue()) {
					}
					else if (mainForm.elements[el.name] != null) {
						// don't overwrite existing values
					}
					else {
						var input = new Element ('input');
						input.writeAttribute ("type", "hidden");
						input.writeAttribute ("name", el.name);
						input.value = el.value
						mainForm.insert (input);
					}
				} catch (error) {
					log ("munge error: " + error);	
				}
			});
		}
	});
}
	

/* zoom the window to the specified anchor */
function doHash (hash) {
	if (hash != "") {
		setFieldFocus (hash);
	}
}

function flashFieldLabel (id) {
	/* to get from the id to the label we go to the previous sibling element (table)
	and then down to a cell with class="label_cell"
	*/
	try {
		var home = $(id);
		var labelCell = null;
		var candidates = null;
		if (!home) throw ("element not found for id: %s", id);
		// log ("element name: " + home.tagName);
		if (home.tagName == "DIV") {
			candidates = home.previous().select("td.label-cell");
			if (candidates.size() == 0) {
				// log ("looking for main-element-label");
				var prev = home.previous()
				// log ("prev: %s", prev.tagName);
				candidates = prev.select ("div.main-element-label");
				if (!candidates) {
					// log ("couldn't find div.main-element-label")
				}
			}
			else {
				// log ("%d candidates found", candidates.size());
			}
			
			if (candidates.size() == 0)
				throw ("label-cell not found");
			labelCell = candidates[0]
		}
 		else if (home.tagName == "INPUT" || home.tagName == "TEXTAREA" || home.tagName == "SELECT") {
			candidates = home.up ("tr.form-row").select("td.label-cell");
			if (!candidates) {
				candidates = home.up ("table.no-input-field-table").select ("div.main-element-label");
			}
			if (candidates.size() == 0)
				throw ("label-cell not found");
			labelCell = candidates[0]
			home.focus()
		}
		else {
			throw ("unable to handle tag for '%s' (%s)", home.tagName, id);
		}
		
		if (labelCell) {
			labelCell.addClassName ("flash");
			var cnt = 0;
			new PeriodicalExecuter(function(pe) {
 				if (cnt++ > 0) {
					labelCell.removeClassName ("flash");
					pe.stop();
				}
			}, 1.0);
		}

	} catch (error) {
		notify ("flashFieldLabel error: " + error);
	}
}


function setFieldFocus (id) {
	log ("setFieldFocus(): " + id);
	var highlight_box_enabled = true;
	
	box = $(id+"_box");
	if (box == null) {
		notify ("box not found for " + id);
		return;
	}
	else {
		window.scrollTo (0, box.cumulativeOffset().top);
		if (highlight_box_enabled) {
			flashFieldLabel (id);
		}
	}
	
	input = $(id+"_input");
	if (input == null) {
		notify ("input not found for " + id);
		Event.observe(window, 'click', function (event) {
			box.setStyle ({border:"none"});
		});
	}
	else {
		input.focus();
		// focusObj = id;
		Event.observe(input, 'blur', function (event) {
			// box.ClassNames.clear ();
			box.setStyle ({border:"none"});
		});
	}
}

function debug_init () {
	// Event.observe(window, 'scroll', function (evnt) {handle_scroll(evnt)} , false);

	if ($('debug-info') && metadata_editor_support_debug) {
		Event.observe(window, 'scroll', 
			function (evnt) {$('debug-info').setStyle({top:window.pageYOffset});},
			false);
		Event.observe('debug-info', 'click', 
			function (evnt) {$('debug-info').setStyle({display:"none"});},
			false);
	}
}

function debug (s) {
	if (metadata_editor_support_debug) {
		$('debug-info').setStyle({display:"block", top:window.pageYOffset, fontSize:"8pt"});
		$('debug-info').innerHTML = s;
	}
}

/* set the right edge of provided "element" to that of the
  examplar element (or viewport) (minus some padding);
 */
function setElementWidth (element, exemplar) {
	element = $(element);
	if (!element) {
		return;
	}
	
	var rightEdge;
	if (!exemplar)
		rightEdge = document.body.clientWidth;
	else {
		exemplar = $(exemplar);
		var exWidth = exemplar.getDimensions().width;
		var exLeft = exemplar.viewportOffset().left;
		rightEdge = exWidth + exLeft;
	}
		
	// alert ("right edge: " + rightEdge);
	var dims = element.getDimensions();
	var padding = 7;
	var elementRight = element.viewportOffset().left + dims.width;
	var slop = elementRight - (rightEdge);
	var newWidth = dims.width - slop - padding;
	element.setStyle ({width:newWidth+"px"});
}

/* an ATTEMPT to keep stuff from flowing off the edge of Window screens. 
It seems that we can't set the width of all the "element-and-input" elements without
screwing up the display, but we may be able to just set the width of the field
that houses the standards ...
*/
function initializeFieldWidths () {

	var fields = $$ (".element-and-input");
	// alert (fields.size() + " element-and-input fields found");
	var viewportWidth = document.body.clientWidth;
	fields.each ( function (box) {
		setElementWidth (box, navMenu);
	});

}

	
// no longer called (we just use single icon in metadata editor
/* function sifObjCreator (id, sifTypes) {
	sifCommand (id, sifTypes, 'create');
} */

/* 
	id - recordId
	sifTypes = acceptable sif xmlFormats for this RefId field
*/
function sifRefHandler (id, sifTypes, command) {
	$(id).scrollTo();
	var types = sifTypes.split (',');
	// log ("types: " + types.join (','));
	// log ("id: " + id);
	params = {
		command: 'find',
		elementId: id,
		sifTypes: types,
		recId: document.forms[0].recId.value
	}
	var url = 'sif.do?' + $H(params).toQueryString();
	// log (url);
	// window.location = url;
	
	var featureMap = {
		innerHeight : 600, height : 600,
		innerWidth : 650, width : 650,
		resizable : 'yes', scrollbars : 'yes',
		locationbar : 'no', menubar : 'no', location : 'no', toolbar : 'no'
	}
	var features = $A()
	$H(featureMap).each ( function (pair) {
		features.push (pair.key + "=" + pair.value);
	});
	var bpwin = window.open (url, "sifTool", features.join (','));
	bpwin.focus();
	return false;
}

/*
called on page load. 
- doHash scrolls to anchor refered to by hashArg. 
- preloadImages loads the expandable node widgets.
*/
function init(hashArg) {
	initializeNavMenu();
	doHash (hashArg);
	muiExpander = new MuiExpander();
	preloadImages();

	Event.observe(window, 'scroll', 
		function (evnt) {
			var scrollOffsets = document.viewport.getScrollOffsets();
			var y = scrollOffsets.top;
			var w = document.viewport.getWidth();
			var y_threshold = 144;
			var icon = $('to-top-icon');
			var padding=4;
			if (icon) {
				if (y > y_threshold) {
					icon.setStyle ({top:y+padding, left:w-(icon.getWidth()+padding), display:"block"});
				}
				else {
					icon.setStyle ({display:"none"});
				}
			}
		},
		false);
		
	// attempt to keep stuff from flowing over the right edge of the viewport.
	// DISABLED because it causes trouble for field layout ...
/* 	if (Prototype.Browser.IE)
		initializeFieldWidths (); */
		
	// debug_init();
	
	new VocabTypeAhead ();
	
}



