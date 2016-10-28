/* admin-support.js - javascript to support schemedit admin page */

// alert ("hello from admin-support.js");

function doToolHelp (name) {
	var features = "innerHeight=500,height=500,innerWidth=650,width=650,resizable=yes,scrollbars=yes";
	features += ",locationbar=no,menubar=no,location=no,toolbar=no";
	var url = "../tool-help/" + name + ".html";
	// alert ("url: " + url);
	// bpwin = window.open (url);
	var thwin = window.open (url, "bpwin", features);
	thwin.focus();
	return false;
	}
	
function doExit () {
	document.adminForm.command.value = "exit";
	return doSubmit();
	}
	
function doReload () {
	document.adminForm.command.value = "reloadSchema";
	return doSubmit();
}

function doSaveProps () {
	document.adminForm.command.value = "saveProps";
	return doSubmit();
}

function doUploadSample () {
	document.adminForm.command.value = "uploadSample";
	return doSubmit();
}

function doUpdate () {
	document.adminForm.command.value = "update";
	return doSubmit();
}

	/* submit the current form for validation */
function doSubmit() {
	document.adminForm.method = "post";
	document.adminForm.action="admin.do";
/* 	alert ("summitting: command=" + document.adminForm.command.value);
	alert ("schemaFile: " + document.adminForm.schemaFile.value);
	alert ("sampleFilePath" + document.adminForm.sampleFilePath.value); */
	document.adminForm.submit();
	return true;
}

