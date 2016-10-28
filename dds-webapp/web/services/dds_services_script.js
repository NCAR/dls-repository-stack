
	// A simple window with no browser buttons.
	function swin(theURL,w,h) {
          window.open(theURL,"newWin", ("width="+w+",height="+h+",status,resizable,scrollbars")).focus()
    }
	function swinNS(theURL) {
          window.open(theURL,"newWin", ("status,resizable,scrollbars")).focus()
    }

	
	function openFullWindow(theURL,w,h) {
          window.open(theURL,"newWin", ("width="+w+",height="+h+",status,resizable,menubar,scrollbars,toolbar,location,directories,titlebar")).focus()
    }
	
	// Confirm before going to a given URL
	function confirmHref( URL, msg ) {	
		if ( confirm( msg ) )
			document.location.href = URL;
	}	

	   

	
	// -------- DDSWebService explorer helper functions ---------
	
	function getDdswsBaseUrl() {
		if(document.baseUrlForm)
			return trim(document.baseUrlForm.baseUrl.value);
		else
			return BASE_URL;
	}
	
	function getJsonArgs() {
		var fm = document.jsonForm;
		if(fm.jsonCheck.checked){
			var response = '&output=json';
			var callback = trim(fm.jsonCallback.value);
			if(callback.length > 0)
				response = response + '&callback=' + callback;
			return response;
		}
		return '';
	}
	
	function getTransformArg() {
		var fm = document.transformForm;
		if(fm.transformCheck.checked){
			return '&transform=localize';
		}
		return '';
	}	
	
	function resetXmlFormatRadio() {
		var objElement = document.getElementById( 'no_xml_format' );
		objElement.checked = true;
	}
	
	function mkServiceInfo() {
		window.location = 	getDdswsBaseUrl() + 
							"?verb=ServiceInfo" + getJsonArgs() + getTransformArg(); 	
	}
	
	function mkListFields() {
		window.location = 	getDdswsBaseUrl() + 
							"?verb=ListFields" + getJsonArgs() + getTransformArg(); 	
	}
	
	function mkListTerms(rt) {	
		var fm = document.listTermsForm;
		var field = trim(fm.field.value);
		var verb = "ListTerms";
		window.location = 	getDdswsBaseUrl() + 
							"?field=" + field +
							"&verb=" + verb + getJsonArgs() + getTransformArg();
							 
	}	

	function mkListRequest() {
		var fm = document.listRequest;
		var request = fm.verb.options[fm.verb.selectedIndex].value;
		
		window.location = 	getDdswsBaseUrl() + 
							"?verb=" + request + getJsonArgs() + getTransformArg(); 	
	}
	
	function mkGetRecordDDSWS(rt) {	
		var fm = document.getRecordFormDDSWS;
		var format = fm.formats.options[fm.formats.selectedIndex].value;
		if(format == "-any-")
			format = "";
		else
			format = "&xmlFormat=" + format;		
		var identifier = trim(fm.identifier.value);
		var verb = "GetRecord";
		window.location = 	getDdswsBaseUrl() + 
							"?verb=" + verb + 
							"&id=" + identifier + format + getJsonArgs() + getTransformArg();
	}

	function mkUrlCheck(rt) {	
		var fm = document.urlCheckForm;
		var url = trim(fm.url.value);
		var verb = "UrlCheck";
		window.location = 	getDdswsBaseUrl() + 
							"?url=" + url +
							"&verb=" + verb + getJsonArgs() + getTransformArg();
							 
	}	
	
	function mkSearch(rt) {	
		var fm = document.searchForm;
		var from = "";
		var until = "";
		var geoParams = "";
		
		var vocabs = "";
		for(var i=0; i< fm.elements.length; i++){
			if(fm.elements[i].checked && fm.elements[i].name != 'doGeo'){
				var name = fm.elements[i].name;
				var value = fm.elements[i].value;
				if( !(name == 'xmlFormat' && value == 'none') ){
					vocabs = vocabs + "&" + name + "=" + value;
				}
			}	
		}
			
		var query = trim(fm.q.value).replace(/\+/g,"%2B"); // Escape/preserve the + sign in the query
		var searchRequest = fm.searchRequest.options[fm.searchRequest.selectedIndex].value;
		var temp = new Array();
		temp = query.split(' ');
		if(temp.length > 0){
			query = temp[0];
			for(var i = 1; i < temp.length; i++){
				query += "+" + temp[i];
			}
		}
		
		if(fm.doGeo.checked) {
			geoParams =	"&geoPredicate=" + fm.geoPredicate.options[fm.geoPredicate.selectedIndex].value +
						"&geoClause=" + fm.geoClause.options[fm.geoClause.selectedIndex].value +
						"&geoBBNorth=" + trim(fm.geoBBNorth.value) +
						"&geoBBSouth=" + trim(fm.geoBBSouth.value) +
						"&geoBBWest=" + trim(fm.geoBBWest.value) +
						"&geoBBEast=" + trim(fm.geoBBEast.value);
		}
		
		window.location = 	getDdswsBaseUrl() + 
							"?verb=" + searchRequest +
							"&q=" + query + vocabs + 
							"&s=0" +
							"&n=10&client=ddsws-explorer" + geoParams + getJsonArgs() + getTransformArg();
	}
	
	function insertLatLons() {	
		var fm = document.searchForm;
		if(fm) {
			fm.geoBBNorth.value = "48.00";
			fm.geoBBSouth.value = "24.00";
			fm.geoBBWest.value = "-125.00";
			fm.geoBBEast.value = "-67.50";
			doCkGeo();
		}		
	}
	
	function doCkGeo() {
		var fm = document.searchForm;
		if(fm)
			fm.doGeo.checked = true;
	}


	// -------- ODLP and OAI helper functions ---------
	
	function mkResume(rt) {	
		var fm = document.resumptionForm;
		var verb = fm.verb.options[fm.verb.selectedIndex].value;
		var token = fm.resumptionToken.value;
		window.location = 	OAI_BASE_URL + 
							"?verb=" + verb + 
							"&resumptionToken=" + trim(token);
	}

	function mkGetRecord(rt) {	
		if(rt != null)
			rt = "&rt=" + rt;
		else
			rt = "";
		var fm = document.getRecordForm;
		var format = fm.formats.options[fm.formats.selectedIndex].value;
		var identifier = fm.identifier.value;
		var verb = "GetRecord";
		window.location = 	OAI_BASE_URL + 
							"?verb=" + verb + 
							"&metadataPrefix=" + format +
							"&identifier=" + trim(identifier) + rt; 
	}

	function mkListRecordsIdentifers(rt) {	
		var fm = document.listRecordsIdentifers;
		var format = fm.formats.options[fm.formats.selectedIndex].value;
		var set = fm.sets.options[fm.sets.selectedIndex].value;
		if(set == " -- All -- ")
			set = "";
		else
			set = "&set=" + set;
		var from = fm.from.options[fm.from.selectedIndex].value;
		if(from == "none")
			from = "";
		else
			from = "&from=" + from;
			
		var until = fm.until.options[fm.until.selectedIndex].value;
		if(until == "none")
			until = "";
		else
			until = "&until=" + until;
		var verb = fm.verb.options[fm.verb.selectedIndex].value;
		window.location = 	OAI_BASE_URL + 
							"?verb=" + verb + 
							"&metadataPrefix=" + format +
							set +from + until; 
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
				
		var advanced = "on";
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
		window.location = 	OAI_BASE_URL + 
							"?verb=" + verb + 
							"&metadataPrefix=" + format +
							from + until +
							"&set=dleseodlsearch/" + query + 
							"/" + set +
							"/0/10&client=dds-odl-explorer"; 
	}		
	
	// ---- END ODLP/OAI functions

	
	
	/* Trims white space from a string */
	function trim(str)
	{
	   return rTrim(lTrim(str));
	}
	
	var whitespace = new String(" \t\n\r");	
	
	/* Trims white space from the left side of a string */
	function lTrim(str)
	{
	
	   var s = new String(str);
	
	   if (whitespace.indexOf(s.charAt(0)) != -1) {
		  // We have a string with leading blank(s)...
	
		  var j=0, i = s.length;
	
		  // Iterate from the far left of string until we
		  // don't have any more whitespace...
		  while (j < i && whitespace.indexOf(s.charAt(j)) != -1)
			 j++;
	
		  // Get the substring from the first non-whitespace
		  // character to the end of the string...
		  s = s.substring(j, i);
	   }
	   return s;
	}
	
	/* Trims white space from the right side of a string */
	function rTrim(str)
	{
	   var s = new String(str);
	
	   if (whitespace.indexOf(s.charAt(s.length-1)) != -1) {
		  // We have a string with trailing blank(s)...
	
		  var i = s.length - 1;       // Get length of string
	
		  // Iterate from the far right of string until we
		  // don't have any more whitespace...
		  while (i >= 0 && whitespace.indexOf(s.charAt(i)) != -1)
			 i--;
	
	
		  // Get the substring from the front of the string to
		  // where the last non-whitespace character is...
		  s = s.substring(0, i+1);
	   }
	
	   return s;
	}
	

