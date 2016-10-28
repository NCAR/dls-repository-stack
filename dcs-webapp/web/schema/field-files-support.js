var request_params = window.location.href.parseQuery();

function doReload () {
	if (!confirm ("WARNING: Reloading a framework will cause uses currently editing records"
				  + " in this framework to loose there work!!. Are you sure you want to reload"
	              + " the framework?"))
		 return;
	
	var params = {
		'command' : 'reload'
	}
	var loc = "fields.do?" + $H(params).toQueryString();
	window.location = loc;
}

function doSortListingBy (sortListingBy) {
	doCommand('list', sortListingBy);
}

function doSchemaViewer () {

	// alert ("id: " + id);
	var params = {
		'command' : 'setFramework',
		'id' : $F('frameworkSelect')
	}
	var loc = "schema.do?" + $H(params).toQueryString();
	window.location = loc;
}

function doSetFramework () {
	
	var xmlFormat = $F('frameworkSelect');
	if (!xmlFormat) {
		alert ("please select a framework");
		return;
	}

	var command = request_params['command'];
	log ("command: " + command);
	var params = {
		'xmlFormat' : xmlFormat,
		'command' : command
	}
	var loc = "fields.do?" + $H(params).toQueryString();
	window.location = loc;
}

function doCommand (command, sortListingBy) {
	log ("do command");
	
	var xmlFormat = $F('frameworkSelect');
	if (!xmlFormat) {
		alert ("please select a framework");
		return;
	}
	
	log ("command: " + command);
	var params = {
		'xmlFormat' : xmlFormat,
		'command' : command
	}
	if (sortListingBy)
		params['sortListingBy'] = sortListingBy;
	
	var loc = "fields.do?" + $H(params).toQueryString();
	window.location = loc;
	// alert ("would have gone to " + loc);
}

function initializeFileListingSortBy (sortedBy) {
	log ("initializeFileListingSortBy: " + sortedBy);
	if (!$('sort-listing-menu'))
		return;
	if (sortedBy == 'path') {
		var nameLink = new Element ('a', {'href':'#'}).update('name').addClassName ('field-link');
		nameLink.observe ('click', function (event) {
			doSortListingBy('name');
		});
		$('sortListingByName').update (nameLink);
		$('sortListingByPath').update ("path").setStyle({fontWeight:'bold'});
	}
	if (sortedBy == 'name') {
		var pathLink = new Element ('a', {'href':'#'}).update('path').addClassName ('field-link');
		pathLink.observe ('click', function (event) {
			doSortListingBy('path');
		});
		$('sortListingByPath').update (pathLink);
		$('sortListingByName').update ("name").setStyle({fontWeight:'bold'});
	}
		
}

function initializeFrameworkPicker (currentFormat, allFrameworks, fieldsFrameworks) {
	log ("initializeFrameworkPicker");
	var select = $('frameworkSelect');
	allFrameworks.each (function (xmlFormat) {
		var option = new Option (xmlFormat);
		if (fieldsFrameworks.indexOf(xmlFormat) == -1)
			option.disabled = true;
		else if (xmlFormat == currentFormat)
			option.selected = true;
		select.options[select.options.length] = option;
	});
	select.observe ('change', doSetFramework)
}
	


