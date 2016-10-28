function doSetFramework () {
	var form = document.forms[0];
	if (form == null) {
		alert ("form not found");
		return;
	}
	// alert ("id: " + id);
	var params = {
		'command' : 'setFramework',
		'id' : form.frameworkSelect.value
	}
	var loc = "schema.do?" + $H(params).toQueryString();
	window.location = loc;
}

function doFieldsFiles () {
	// alert ("id: " + id);
	var params = {
		'xmlFormat' : $F('frameworkSelect')
	}
	var loc = "fields.do?" + $H(params).toQueryString();
	window.location = loc;	
}

function toggleVisibility( elementID ) {
	var objElement = document.getElementById( elementID );
	if ( objElement != null ) {
		if ( objElement.style.display == '' )
			objElement.style.display = 'none';
		else
			objElement.style.display = '';
	}
}

function clearFrameworkSelections( ) {
	var formObj = document.forms[0];
	if(formObj.selectedFrameworks != null){
		if (formObj.selectedFrameworks.length) {
			for(i = 0; i < formObj.selectedFrameworks.length; i++){
				formObj.selectedFrameworks[i].checked = false;
			}
		}
		else
			formObj.selectedFrameworks.checked = false;
	}
}

function SelectAllFrameworks( ) {
	var formObj = document.forms[0];
	if(formObj.selectedFrameworks != null){
		if (formObj.selectedFrameworks.length) {
			for(i = 0; i < formObj.selectedFrameworks.length; i++){
				formObj.selectedFrameworks[i].checked = 1;
			}
		}
	}
}

