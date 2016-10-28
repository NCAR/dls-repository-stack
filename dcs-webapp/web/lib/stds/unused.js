function showSelectedStandardsStatic () {
	log ("showSelectedStandards");
	document.sef.pathArg.value = DisplayState.pathArg;
	document.sef.command.value = "standardsDisplay";
	document.sef.displayContent.value = "selected";
	document.sef.displayMode.value = "list";
	return doSubmit();
}


function doSuggestStandardsStatic (event) {
	log ("doSuggestStandards()");
	showSpinner (event);
	event.stop();

	document.sef.pathArg.value = DisplayState.pathArg;
	document.sef.command.value = "suggestStandards";
	document.sef.displayContent.value = "suggested";
	document.sef.displayMode.value = "list";
	return doSubmit();
}

/* following two are OBSOLETE but reusable with update */

/* shows info about the casaa demo - */
function doAbout () {
	var features = "innerHeight=500,height=500,innerWidth=550,width=550,resizable=yes,scrollbars=yes";
	features += ",locationbar=no,menubar=no,location=no,toolbar=no";
	var url = "http://www.dpc.ucar.edu/people/ostwald/casaa/demo/casaa_demo.html";
	var aboutwin = window.open (url, "about", features);
	aboutwin.focus();
}

/* casaa function to query service for assigned standards for current record */
function doGetAssignments (recId) {
	var features = "innerHeight=500,height=500,innerWidth=550,width=550,resizable=yes,scrollbars=yes";
	features += ",locationbar=no,menubar=no,location=no,toolbar=no";
	var url = "../adn/adn.do?command=getAssignedStandards&pathArg=" + DisplayState.pathArg + "&recId=" + recId;
	var casaawin = window.open (url, "casaa", features);
	casaawin.focus();
}
