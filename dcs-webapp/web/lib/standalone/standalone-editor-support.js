/* javascript functions in support of STANDALONE metadata editor */


	/* NOTE: doBestPractices call should be changed to openBestPracticesWindow
		see also:
			doBestPractice (topic)
			doBestPractices (xpath, format)
	*/
	
	/*
		WORKING bestPractices handler. Url opens best practices page in new window
	*/
/* 	function doBestPractices (url) {
		var features = "innerHeight=500,height=500,innerWidth=550,width=550,resizable=yes,scrollbars=yes";
		features += ",locationbar=no,menubar=no,location=no,toolbar=no";
		// alert ("url: " + url);
		// bpwin = window.open (url);
		var bpwin = window.open (url, "bpwin", features);
		bpwin.focus();
	} */

// SPECIFIC TO STANDALONE //
function doChangeId (id) {
	if (id == "") {
		alert ("please supply a record ID");
		return false;
	}
	document.sef.command.value = "changeID";
	document.sef.pathArg.value = id;
	return doSubmit();
}

// IMPELEMENTED SLIGHTLY DIFERENTLY IN METADATA-EDITOR-SUPPORT
function setFieldFocus (id) {
	// var id = ${sf:pathToId(xpath)};
	box = $(id+"_box");
	if (box == null) {
		alert ("box not found for " + id);
		return;
	}
	else {
		window.scrollTo (0, box.cumulativeOffset().top);
		box.setStyle ({border:"red 1px dashed"});
	}
	input = $(id+"_input");
	if (input == null) {
		// alert ("input not found for " + id);
		Event.observe(window, 'click', function (event) {
			// box.ClassNames.clear ();
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

