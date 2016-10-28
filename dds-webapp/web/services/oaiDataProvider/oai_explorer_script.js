// -------- OAI-PMH and ODL helper functions for the OAI 'Explorer' pages ---------

function getBaseUrl() {
	return trim(document.baseUrl.baseUrl.value);	
}

function mkResume(rt) {	
	var fm = document.resumptionForm;
	var verb = fm.verb.options[fm.verb.selectedIndex].value;
	var token = fm.resumptionToken.value;
	if(rt)
		rt = "&" + rt;
	else
		rt = '';	
	window.location = 	getBaseUrl() + 
						"?verb=" + verb + 
						"&resumptionToken=" + token + rt;
}

function mkGetRecord(rt) {	
	var fm = document.getRecordForm;
	var format = fm.formats.options[fm.formats.selectedIndex].value;
	var identifier = fm.identifier.value;
	var verb = "GetRecord";
	if(rt)
		rt = "&" + rt;
	else
		rt = '';
	window.location = 	getBaseUrl() + 
						"?verb=" + verb + 
						"&metadataPrefix=" + format +
						"&identifier=" + identifier + rt; 
}

function mkListRecordsIdentifers(rt) {	
	var fm = document.listRecordsIdentifers;
	var format = fm.formats.options[fm.formats.selectedIndex].value;
	var set = "";
	if(fm.sets)
		set = fm.sets.options[fm.sets.selectedIndex].value;
	if(set == " -- All -- " || set == "")
		set = "";
	else
		set = "&set=" + set;
	
	var from = "";
	if(fm.from)
		from = fm.from.options[fm.from.selectedIndex].value;
	if(from == "" || from == "none")
		from = "";
	else
		from = "&from=" + from;
	
	var until = "";
	if(fm.until)
		until = fm.until.options[fm.until.selectedIndex].value;
	if(until == "" || until == "none")
		until = "";
	else
		until = "&until=" + until;
	var verb = fm.verb.options[fm.verb.selectedIndex].value;
	if(rt)
		rt = "&" + rt;
	else
		rt = '';		
	window.location = 	getBaseUrl() + 
						"?verb=" + verb + 
						"&metadataPrefix=" + format +
						set +from + until + rt; 
}

function mkOdlSearch(rt) {	
	// ODL search format is: dleseodlsearch/[query string]/[set]/[offset]/[length]
	// Use set = null to indicate no set spec.
	var fm = document.odlSearchForm;
	var from = "";
	var until = "";
	var format = fm.formats.options[fm.formats.selectedIndex].value;
	set = fm.sets.options[fm.sets.selectedIndex].value;
	if(set == " -- All -- ")
		set = "null";
			
	// If the advanced search is visible, get setting from the form:
	if(advanced == "on"){
		from = fm.from.options[fm.from.selectedIndex].value;
		if(from == "none")
			from = "";
		else
			from = "&from=" + from;
			
		until = fm.until.options[fm.until.selectedIndex].value;
		if(until == "none")
			until = "";
		else
			until = "&until=" + until;
	}
	var verb = fm.verb.options[fm.verb.selectedIndex].value;
	var query = fm.query.value;
	var temp = new Array();
	temp = query.split(' ');
	if(temp.length > 0){
		query = temp[0];
		for(var i = 1; i < temp.length; i++){
			query += "+" + temp[i];
		}
	}
	if(rt)
		rt = "&" + rt;
	else
		rt = '';
	window.location = 	getBaseUrl() + 
						"?verb=" + verb + 
						"&metadataPrefix=" + format +
						from + until +
						"&set=dleseodlsearch/" + query + 
						"/" + set +
						"/0/10" + rt; 
}	

function trim(s) {
	return s.replace(/\s+/,'').replace(/\s+/,'');	
}
