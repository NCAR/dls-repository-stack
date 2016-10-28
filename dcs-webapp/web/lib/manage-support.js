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

function doParentDir() {
	document.sef.command.value = "parentDir";
	return doSubmit();
}
	
function doEditSample () {
	document.sef.command.value = "edit";
	document.sef.src.value = "sample";
	return doSubmit();
	}

function doFetchById (id) {
	if (id == "") {
		alert ("please enter an id to fetch");
		return false;
		}
	document.sef.src.value = "remote";
	document.sef.recId.value = id;
	document.sef.command.value = "edit";
	return doSubmit();
	}
	
function doNewRecord () {
	document.sef.command.value = "newRecord";
	return doSubmit();
	}
	
	/* submit the current form for validation */
function doSubmit() {
	document.sef.action = submitAction;
	document.sef.method = "post";
	document.sef.submit();
	return true;
}

