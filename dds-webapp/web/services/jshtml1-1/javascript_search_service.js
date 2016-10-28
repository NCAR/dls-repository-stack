if(baseUrl)
	showError("You must not source the serviceURL JavaScript library more than once in a single HTML page.");
var baseUrl = "@DDS_SERVER_BASE_URL@/services/jshtml1-1";
var show = new Array();
var hide = new Array();
var menus = new Array();
var cq = null;
var bq = null;
var sq = null;
var sort = null;
var redirect = null;
var n = null;
var mll = null;
var mdl = null;
var se = null;
var nr = 0;
initPage();

function CustomMenuItem(menu,item,query){
	if(arguments.length != 3)
		showError("The CustomMenuItem() command must have three arguments: name of menu, name of item in menu, and query mapping for menu item.");
	else
		menus.push(menu + "|" + item + "|" + query);
}

function ShowElement(elm){
	if(elm != null)
		show.push(elm);
}

function HideElement(elm){
	if(elm != null)
		hide.push(elm);
}

function SearchConstraint(val){
	cq = val;
}

function SearchBoost(val){
	bq = val;
}

function SortBy(val){
	if(arguments.length != 1)
		showError("'The SortBy() command must contain one argument. Possible values are 'title', 'mostrecent' or 'mostrelevent'");
	
	if(val == 'title' || val == 'mostrecent')
		sort = val;	
	else if (val != 'mostrelevent')
		showError("'The SortBy() command may only contain the value 'title', 'mostrecent' or 'mostrelevent'");
}

function RedirectSearchTo(val){
	redirect = val;	
}

function MaxResultsPerPage(val){
	n = val;	
}

function MaxLinkLength(val){
	mll = val;	
}

function MaxDescriptionLength(val){
	mdl = val;	
}

function SuppressErrors(){
	se = 't';	
}


function SmartLink(query,label){
	if(label == null || query == null){
		showError('The SmartLink command requires two parameters: the first parameter is the search query, the second is the label that is displayed in the search results. Example: SmartLink("subject:ocean*", "Ocean science");');		
		return;
	}
	if(redirect == null)
		var base = "";
	else
		var base = redirect;
	window.location = base + "?qh=" + escape(query) + "&d=" + escape(label);
}

function SmartLinkDropList() {
	var optionSelected = document.getElementById( 'smartLinkDropList' );
	
	if(optionSelected == null){
		showError("The SmartLinkDropList command requires that the select element it resides in contains the following attribute and value: id='smartLinkDropList'");		
		return;
	}
	
	option = optionSelected.options[optionSelected.selectedIndex];
	
	if(option == null)
		return;
	
	q = option.value;
	d = getText(option);
	
	if(q != null && q != "" && q != '--none--'){
		if(redirect == null)
			var base = "";
		else
			var base = redirect;		
		
		window.location.href = base + "?qh=" + escape(q) + "&d=" + escape(d) + "&mySelection=" + optionSelected.selectedIndex;
	}
}

function RenderSearchResults(query,label) {
	if(query == null){
		showError('The RenderSearchResults command requires one or two parameter: the first parameter is the search query, the second is optional and is used for the label that is displayed in the search results. Example: RenderSearchResults("subject:ocean*", "Ocean science");');		
		return;
	}	
	if(label == null)
		label = '';
	sq = "&deq=t&qh=" + escape(query) + "&d=" + escape(label);
	RenderPage();
}

function RenderPage(){
	nr++;
	if(nr > 1){
		showError('You may not use the RenderPage or RenderSearchResults command more than once in a single HTML page.');
		return;
	}	
	
	var queryString = window.location.search.substring(1);
	if(queryString != null && queryString.length > 0)
		var qs = queryString + "&rt=jswl";
	else
		var qs = "rt=jswl"; 
	
	if(sq != null)
		qs += sq;

	qs += "&client=" + escape(window.location.href.split("?")[0]);
	
	for(i = 0; i < show.length; i++)
		qs += "&sh=" + show[i];

	for(i = 0; i < hide.length; i++)
		qs += "&hd=" + hide[i];
	
	if(cq != null)
		qs += "&cq=" + escape(cq);

	if(bq != null)
		qs += "&bq=" + escape(bq);

	if(redirect != null)
		qs += "&fmac=" + escape(redirect);
	
	if(sort != null)
		qs += "&sortby=" + sort;
	
	if(n != null)
		qs += "&n=" + n;
	
	if(se != null)
		qs += "&se=" + se;
	
	if(mll != null)
		qs += "&mll=" + mll;
	
	if(mdl != null)
		qs += "&mdl=" + mdl;
	
	for(i = 0; i < menus.length; i++)
		qs += "&menu=" + escape(menus[i]);
		
	var jsUrl = baseUrl + "?" + qs;	
	document.write( "\<SCRIPT LANGUAGE='JAVASCRIPT' SRC='" + jsUrl + "' \>\</SCRIPT\>" );
}

function showError(msg){
	if(se == null)
		alert('Web developers: There was an error in your DLESE search page. ' + msg);
}

function initPage(){
	var objElement = document.getElementById( 'smartLinkDropList' );
	if ( objElement != null ) {
		var queryString = window.location.search.substring(1);
		if(queryString != null && queryString.length > 0){
			var parms = queryString.split('&');
			for (var i=0; i<parms.length; i++) {
			   var pos = parms[i].indexOf('=');
			   if (pos > 0) {
				  var paramName = parms[i].substring(0,pos);
				  var paramVal = parms[i].substring(pos+1);
				  if(paramName == 'mySelection'){
					objElement.selectedIndex = paramVal;
					break;
				  }
			   }
			}
		}		
	}	
}

function getText( elm ) {
	if (elm)
	{
		if (elm.childNodes[0])
			return elm.childNodes[0].nodeValue;
		else if (elm.innerHTML)
			return elm.innerHTML;
		else
			return "";
	}			
}
