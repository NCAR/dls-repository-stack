var SET_DEFAULT_DOC_CONTEXT = null;

function pageInit () {
	
	$$('.subject-select').each (function (select) {
		makeSubjectsMenu(select);
		});
		
	$$('.open-subjects').each (function (button) {
		button.observe ('click', openSubjectsButtonHandler);
	});
	
	$$('.close-subjects').each (function (button) {
		button.observe ('click', closeSubjectsButtonHandler);
	});
	
	/* $('open-frameworks-button').observe ('click', openFrameworksButtonHandler); */
	$('open-frameworks-button').observe ('click', openAll);
	
	/* $('close-frameworks-button').observe ('click', closeFrameworksButtonHandler); */
	$('close-frameworks-button').observe ('click', closeAll);
	
	collapsibleRegionsInit();
	
	changeDefaultDocInit();
	
	if (CURRENT_FRAMEWORK || CURRENT_SUBJECT)
		highlight();
}

function highlight () {
	log ("framework: " + CURRENT_FRAMEWORK + "  subject: " + CURRENT_SUBJECT);
	
	var framework_element = subject_element = null;
	
	var framework_button = $('framework_' + CURRENT_FRAMEWORK);
	if (!framework_button) {
		log ('could not find framework_button');
		return;
	}
	openCollapsible (framework_button.identify());
	framework_element = framework_button.up('.framework');

	var subject_button = framework_element.down('#'+CURRENT_FRAMEWORK+'_'+CURRENT_SUBJECT);
	if (subject_button) {
		openCollapsible (subject_button.identify());
		subject_element = subject_button.up('.subject');
	}
	else {
		log ('subject_button not found at #'+CURRENT_FRAMEWORK+'_'+CURRENT_SUBJECT);
	}
	
	// highlight subject if possible, framework otherwise
	var highlight_element = subject_element || framework_element;
	highlight_element.scrollTo();
	if (highlight_element) {
		highlight_element.addClassName('highlighted');
		new PeriodicalExecuter ( function (pe) {
			highlight_element.removeClassName('highlighted');
			pe.stop();
		}, 1);
	}
		
}

function openAll(event) {
	$$('.collapsible-button').each (function (button) {
		openCollapsible (button.identify());
	});
}

function closeAll(event) {
	$$('.collapsible-button').each (function (button) {
		closeCollapsible (button.identify());
	});
}

function openFrameworksButtonHandler(event) {
	$$('.framework-header-table').each (function (framework) {
		var framework_button = framework.down('.collapsible-button')
		openCollapsible (framework_button.identify());
	});
}

function closeFrameworksButtonHandler(event) {
	$$('.framework-header-table').each (function (framework) {
		var framework_button = framework.down('.collapsible-button')
		closeCollapsible (framework_button.identify());
	});
}

/* the event is triggered by an 'open all subjects' button.
first we navigate up in the DOM to the nearest .framework element.
then we find (and open) all the '.subject-content' elements.
*/	
function openSubjectsButtonHandler (event) {
	// alert ("openSubjectButtonHandler");
	event.stop();
	var framework = event.findElement('.framework');
	if (!framework) {
		alert ('framework not found');
		return;
	}
	
	var framework_button = framework.down ('.framework-header-table').down ('.collapsible-button');
	openCollapsible (framework_button.identify());
	
	framework.select ('.subject').each (function (subject) {
		subject.select ('.collapsible-button').each (function (button) {
			openCollapsible (button.identify());
		});
	});
}

function closeSubjectsButtonHandler (event) {
	// alert ("closeSubjectButtonHandler");
	event.stop();
	var framework = event.findElement('.framework');
	if (!framework) {
		alert ('framework not found');
		return;
	}
	
	var framework_button = framework.down ('.framework-header-table').down ('.collapsible-button');
	openCollapsible (framework_button.identify());
	
	framework.select ('.subject').each (function (subject) {
		subject.select ('.collapsible-button').each (function (button) {
			closeCollapsible (button.identify());
		});
	});
}

function changeDefaultDocInit () {
	$$('.default-doc-button').each (function (button) {
		$(button).observe ('click', activateSetDefaultDoc);
	});
}

/* gets called on mouseover for each tr.registered-doc
for 'active' framework */
function registeredDocOver (event) {
	var tr = event.findElement('tr');
	tr.addClassName("default-doc-over");
}

function registeredDocOut (event) {
	var tr = event.findElement('tr');
	tr.removeClassName("default-doc-over");
}

/* use regular exp to extract a substring 
- Assumes pat defines match 'target': e.g. /foo_(.*)_bar */
function getMatch (s, pat) {
	log ("getMatch: " + s);
	try {
		return s.match(pat)[1];
	} catch (error) {}
	return null;
}

function defaultDocClickHandler (event) {
	var element = event.element();
	// log ("defaultDocClickHandler, type: " + event.type + ", obj: " + element.identify());
	var tr = event.findElement('tr.registered-doc');
	var framework_content = event.findElement('.framework_content');
	if (tr && framework_content) {
		var xmlFormat = getMatch(framework_content.identify(), /framework_(.*)_content/)
		log ('xmlFormat: ' + xmlFormat);
		log ('context: ' + SET_DEFAULT_DOC_CONTEXT['xmlFormat']);
		if (xmlFormat == SET_DEFAULT_DOC_CONTEXT['xmlFormat']) {
			var docKey = tr.identify();
			log ("tr clicked: " + docKey);
			SET_DEFAULT_DOC_CONTEXT['key']=docKey;
			$('default-doc_'+xmlFormat).update (docKey);
			var params = {
				'command' : 'setDefaultDoc',
				'xmlFormat' : xmlFormat,
				'key' : docKey
			}
			var url = 'asn.do?' + $H(params).toQueryString();
				
			new Ajax.Request (url, {
				onSuccess: function (transport) {
					// log ('onSuccess');
					// log (transport.responseText);
				}
			});
		}
		else {
			log ("wrong format");
		}
	}
	if (!SET_DEFAULT_DOC_CONTEXT['key'])
		SET_DEFAULT_DOC_CONTEXT['key'] = SET_DEFAULT_DOC_CONTEXT['prior'];
	deactivateSetDefaultDoc();
}

function activateSetDefaultDoc (event) {
	log ("activateSetDefaultDoc");
	var button = event.element();
	var xmlFormat = getMatch(button.identify(), /default_(.*)/);
	log ('xmlFormat: ' + xmlFormat);
	SET_DEFAULT_DOC_CONTEXT = {
		xmlFormat: xmlFormat,
		prior: $('default-doc_'+xmlFormat).innerHTML
	}
	openSubjectsButtonHandler(event);
	var framework_content = $('framework_'+ xmlFormat + '_content');
	framework_content.down('.set-default-prompt').show();
	document.observe ('click', defaultDocClickHandler);
	framework_content.select('tr.registered-doc').each (function (tr) {
		tr.observe ('mouseover', registeredDocOver);
		tr.observe ('mouseout', registeredDocOut);
	});
}


function deactivateSetDefaultDoc () {
	var xmlFormat = SET_DEFAULT_DOC_CONTEXT['xmlFormat'];
	log ("deactivating setDefaultDoc for " + xmlFormat);
	var framework_content = $('framework_'+ xmlFormat + '_content');
	framework_content.down('.set-default-prompt').hide();
	document.stopObserving ('click', defaultDocClickHandler);
	framework_content.select('tr.registered-doc').each (function (tr) {
		tr.stopObserving ('mouseover', registeredDocOver);
		tr.stopObserving ('mouseout', registeredDocOut);
		tr.removeClassName("default-doc-over");
		if (tr.identify() == SET_DEFAULT_DOC_CONTEXT['key'])
			tr.addClassName("default-doc");
		else
			tr.removeClassName("default-doc");
	});
	SET_DEFAULT_DOC_CONTEXT = null;
}


/* initialize all regions marked as "collapsible" */
function collapsibleRegionsInit() {
	// log ('collapsibleRegionsInit()');

	$$('.collapsible-button').each (function (button) {
		button = $(button);
		log (" - " + button.identify());
		var content = $(button.identify()+'_content');
		if (!content)
			log ("where is " + button.identify()+'_content??');
		button.observe ('mouseover', function () {
			button.setStyle ({cursor:'pointer'});
		});
		button.observe ('click', function (event) {
			if (content.visible()) {
				closeCollapsible (button.identify());
			}
			else {
				openCollapsible (button.identify());
			}
		});
	});
}

function closeCollapsible (id) {
	var button = $(id);
	button.removeClassName ('collapsible-button-open');
	var content = $(id+'_content')
	content.hide();
}

function openCollapsible (id) {
	var button = $(id);
	log ("button-id: " + id);
	button.addClassName ('collapsible-button-open');
	var content = $(id+'_content')
	content.show();
}

function handleSubjectSelectChange (event) {
// obtain the subject from the select's selection
	var select = event.element();
	var xmlFormat = getMatch(select.identify(), /subject-select_(.*)/);
	log ("handleSubjectSelectChange: " + select.identify());
	
	log ("stab in the dark .. subject: " + $F(select));
	var subject = $F(select);
	if (!subject) {
		alert ("Please select a subject");
		return;
	}
	editSubjectItems (subject, xmlFormat) 
}

function makeSubjectsMenu (select) {
	// derive xmlFormat from select.id
	// e.g., subject-select_${xmlFormat}
	var xmlFormat = getMatch(select.identify(), /subject-select_(.*)/);
	
	var active_subjects = FORMAT_SUBJECT_MAP[xmlFormat]
	var subject_options = $(select).options;
	
	$A(ALL_SUBJECTS).each (function (subject) {
		if (active_subjects.indexOf(subject) == -1) {
				subject_options[subject_options.length] =  new Option (subject);
		}
	});
	select.observe ("change", handleSubjectSelectChange);
}

/* just for debugging */
function showFormatSubjectMap () {
	log ('FORMAT_SUBJECT_MAP');
	var map = $H(FORMAT_SUBJECT_MAP);
	map.each (function (pair) {
		log (pair.key + " (" + pair.value + ")");
		$A(pair.value).each (function (val) {
			log ("  " + val);
		});
	});
}

function reloadStandardsManager(xmlFormat) {
	log ("reloadStandardsManager for " + xmlFormat);
	var params = {
		'command' : 'update',
		'xmlFormat' : xmlFormat
	}
	href = CONTEXT_PATH + '/asn/asn.do?' + $H(params).toQueryString();
	log ('href: ' + href);
	window.location = href;
}

function toggleSubjectHeader (subjHdr) {
	var items = subjHdr.next();
	if (items.visible()) {
		subjHdr.removeClassName ("subject-listing-open");
		items.hide();
	}
	else {
		subjHdr.addClassName ("subject-listing-open");
		items.show();
	}
}	

// function editSubjectItems (subject, xmlFormat) {

function editSubjectItems (subject, xmlFormat) {

	log ("editSubjectItems for " + subject);
	var params = {
		'command' : 'edit',
		'xmlFormat' : xmlFormat,
		'subject' : subject
	}
	href = CONTEXT_PATH + '/asn/asn.do?' + $H(params).toQueryString();
	log ('href: ' + href);
	window.location = href;
}


