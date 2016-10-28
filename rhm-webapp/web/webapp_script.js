var collTabs = null;
var oaiTabs = null;
var harvestTabs = null;
var parms = window.location.search.parseQuery();

function onPageLoad() {
	if($('chars'))
		$('chars').focus();
	doFind();
	
	if(typeof(setStateUrl) == 'undefined' || setStateUrl == null || setStateUrl == '')
		setStateUrl = 'async_content/set_session_state.jsp';
	
	if($('collTabs') != null) {
		if(typeof(collTab) == 'undefined' || collTab == null || collTab == '')
			collTab = 'overview';
		collTabs = new Control.Tabs($('collTabs'), { 
			defaultTab: collTab,
			beforeChange: function(old_container) { },
			afterChange: function(new_container) {
				new_container.blur();
				/* new Ajax.Request(setStateUrl, {   
					parameters: { collTabs: new_container.id }
				}); */
			} 
		});
	}
	
	if($('oaiTabs') != null) {
		if(typeof(oaiTab) == 'undefined' || oaiTab == null || oaiTab == '')
			oaiTab = 'Identify';
		oaiTabs = new Control.Tabs($('oaiTabs'), { 
			defaultTab: oaiTab,
			beforeChange: function(old_container) { },
			afterChange: function(new_container) {
				new_container.blur();
				/* new Ajax.Request(setStateUrl, {   
					parameters: { oaiTabs: new_container.id }
				}); */
				doOaiResponse(new_container);
			} 
		});
	}
	
	if($('harvestTabs') != null) {
		if(typeof(harvestTab) == 'undefined' || harvestTab == null || harvestTab == '')
			harvestTab = 'details';
		harvestTabs = new Control.Tabs($('harvestTabs'), { 
			defaultTab: harvestTab,
			beforeChange: function(old_container) { },
			afterChange: function(new_container) {
				new_container.blur();
				/* new Ajax.Request(setStateUrl, {   
					parameters: { harvestTabs: new_container.id }
				}); */
				if(new_container.id == 'logs')
					doLogDisplay();
				else if(new_container.id == 'records')
					doRecordsDisplay();				
			} 
		});
	}	
	if($('okButton'))
		$('okButton').focus();
}

function doOaiResponse(tab) {
	//alert('doOaiResponse: ' + baseUrl);
	if(tab.id == 'Explore')
	{
		window.location.href=contextPath+'/explorer?baseUrl='+baseUrl;
		return;
	}
	if(tab != null) {
		var url = contextPath+'/oai_response_display.jsp';
		var contentDiv = $(tab.id+'Content');
		if(contentDiv != null && contentDiv.empty()) {
			contentDiv.update('<div class="loadingMsg">Loading...</div>');
			new Ajax.Updater(contentDiv, url, { parameters: { verb:tab.id, baseUrl:baseUrl } }); 
		}
	}
}

function doLogDisplay(s) {	
	if(uuid != null) {
		var url = contextPath+'/harvest_logs_view.jsp';
		if(typeof(s) != 'undefined')
			url = url + '?s=' + s;
		var elm = $(uuid+'-logs');
		if(elm != null && (elm.empty() || typeof(s) != 'undefined')) {
			elm.update('<div class="loadingMsg">Loading...</div>');
			new Ajax.Updater(elm, url, { parameters: { uuid:uuid },
										 onComplete:doneUpdatingLogs }); 
		}
	}
}

function doneUpdatingLogs()
{
	$$("td.log_message").each(function(elmt){elmt.innerHTML = urlify(elmt.innerHTML) })
}

function urlify(text) {
    var urlRegex = /url: ((http|ftp|https):\/\/[\w-]+(\.[\w-]+)+([\w.,@?^=%&amp;:\/~+#-]*[\w@?^=%&amp;\/~+#-]))?/;
    return text.replace(urlRegex, 'url: <a href="$1">$1</a>')
}

function doRecordsDisplay(dir) {
	if(uuid != null && collectionNA != null && harvestTimeStamp != null) {
		var url = contextPath+'/harvested_records_view.jsp';
		if(typeof(dir) != 'undefined') {
			if(!dir.endsWith('/'))
				return window.open(dir.sub(contextPath,'/'+recordsContext));
			dir = dir.sub(contextPath, '')
			dir = dir.sub('/'+uuid, '')
			dir = dir.sub('/'+collectionNA, '')
			url = url + '?dir=' + dir;
		}
		var elm = $(uuid+'-records');
		if(elm != null && (elm.empty() || typeof(dir) != 'undefined')) {
			elm.update('<div class="loadingMsg">Loading...</div>');
			new Ajax.Updater(elm, url, { parameters: { uuid:uuid, collectionNA:collectionNA, timeStamp:harvestTimeStamp } }); 
		}
	}
}

function doFind() {
	var findForm = $('findForm');
	if(findForm) {
		var count = 0;
		var found = false;
		var chars = findForm.serialize(true).chars.strip().toLowerCase();
		$$('.collrow').each( function (e) {
			var titleElm = e.select('.collTitleLnk')[0];
			if(chars.length == 0 || titleElm.innerHTML.toLowerCase().indexOf(chars) >= 0){
				e.show();
				count++;
				found = true;
			}
			else
				e.hide();
		});
		if(!found)
			$('noCollMsgRow').show();
		else
			$('noCollMsgRow').hide();
		$('collsFound').update(count);
	}
}

function doUpdateBaseUrl() {
	var b = getBaseUrl();
	if (b && b.length > 0)
		window.location='?baseUrl='+escape(b);
}

function doAjaxUpdate(element,url) {
	if(element && url){
		var e = $('validationResult');
		new Ajax.Updater(e, url); 
	}
}

function toggleView(id) {
	if($(id))
		$(id).toggle();
	if($(id+'-open'))
		$(id+'-open').toggle();
}

function popWin(theURL,w,h) {
	return window.open(theURL,"mapWin",("width="+w+",height="+h+",resizable,scrollbars")).focus();
}
function swin(theURL,w,h) {
	return window.open(theURL,"newWin", ("width="+w+",height="+h+",resizable,location")).focus();
}

function fullWin(theURL) {
	return window.open(theURL,"_blank").focus();
	//return window.open(theURL,"fullWin",("resizable,location,scrollbars,menubar")).focus();
}


// -------- OAI-PMH and ODL helper functions for the OAI 'Explorer' pages ---------

function getBaseUrl() {
	if(document.baseUrl && document.baseUrl.baseUrl) 
		return trim(document.baseUrl.baseUrl.value);
	else if (parms.baseUrl)
		return trim(parms.baseUrl);
	return '';
}

function mkResume(rt,verb,token,inSameWin) {	
	var fm = document.resumptionForm;
	if(fm) {
		var verb = fm.verb.options[fm.verb.selectedIndex].value;
		var token = fm.resumptionToken.value;
	}
	var url = '';
	if(rt && rt == 'validate')
		url = contextPath + '/explorer/oai_validator.jsp?baseUrl=' + escape( getBaseUrl() ) + '&';	
	else 
		url = getBaseUrl() + '?';
	var goToUrl = url + 
				"verb=" + verb + 
				"&resumptionToken=" + token + getNameEmailParams(rt);
	if(inSameWin)
		window.location = goToUrl;
	else
		fullWin(goToUrl);
}

function mkStaticRepoRequest(rt) {
	var url = '';
	if(rt && rt == 'validate')
		url = contextPath + '/explorer/oai_validator.jsp?baseUrl=' + escape( getBaseUrl() );
	else
		url = getBaseUrl();
	fullWin( url );
}


function mkSimpleVerbRequest(verb,rt) {	
	var fm = document.getRecordForm;
	var format = fm.formats.options[fm.formats.selectedIndex].value;
	var identifier = fm.identifier.value;
	
	var url = '';
	if(rt && rt == 'validate')
		url = contextPath + '/explorer/oai_validator.jsp?baseUrl=' + escape( getBaseUrl() ) + '&';	
	else 
		url = getBaseUrl() + '?';
	fullWin(	url + 
				"verb=" + verb + getNameEmailParams(rt)); 
}

function mkGetRecord(rt) {	
	var fm = document.getRecordForm;
	var format = fm.formats.options[fm.formats.selectedIndex].value;
	var identifier = fm.identifier.value;
	var verb = "GetRecord";
	
	var url = '';
	if(rt && rt == 'validate')
		url = contextPath + '/explorer/oai_validator.jsp?baseUrl=' + escape( getBaseUrl() ) + '&';	
	else 
		url = getBaseUrl() + '?';
	fullWin(	url + 
				"verb=" + verb + 
				"&metadataPrefix=" + format +
				"&identifier=" + identifier + getNameEmailParams(rt)); 
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
	
	var url = '';
	if(rt && rt == 'validate')
		url = contextPath + '/explorer/oai_validator.jsp?baseUrl=' + escape( getBaseUrl() ) + '&';	
	else 
		url = getBaseUrl() + '?';
	fullWin(	url + "verb=" + verb + 
				"&metadataPrefix=" + format +
				set +from + until + getNameEmailParams(rt));
}

function getNameEmailParams(rt) {	
	var p = '';
	if(rt && rt == 'validate') {
		if($('repositoryName'))
			p += '&repositoryName=' + escape($('repositoryName').innerHTML);
		else if(parms.repositoryName)
			p += '&repositoryName=' + escape(parms.repositoryName);
		if($('adminEmail'))
			p += '&adminEmail=' + escape($('adminEmail').innerHTML);
		else if(parms.adminEmail)
			p += '&adminEmail=' + escape(parms.adminEmail);		
	}
	return p;
}

	
function trim(s) {
	return s.replace(/\s+/,'').replace(/\s+/,'');	
}

// ------------ The control_tabs code [start] --------------

/**
 * @author Ryan Johnson <ryan@livepipe.net>
 * @copyright 2007 LivePipe LLC
 * @package Control.Tabs
 * @license MIT
 * @url http://livepipe.net/projects/control_tabs/
 * @version 2.1.1
 */

if(typeof(Control) == 'undefined')
	var Control = {};
Control.Tabs = Class.create();
Object.extend(Control.Tabs,{
	instances: [],
	findByTabId: function(id){
		return Control.Tabs.instances.find(function(tab){
			return tab.links.find(function(link){
				return link.key == id;
			});
		});
	}
});
Object.extend(Control.Tabs.prototype,{
	initialize: function(tab_list_container,options){
		this.activeContainer = false;
		this.activeLink = false;
		this.containers = $H({});
		this.links = [];
		Control.Tabs.instances.push(this);
		this.options = {
			beforeChange: Prototype.emptyFunction,
			afterChange: Prototype.emptyFunction,
			hover: false,
			linkSelector: 'li a',
			setClassOnContainer: false,
			activeClassName: 'active',
			defaultTab: 'first',
			autoLinkExternal: true,
			targetRegExp: /#(.+)$/,
			showFunction: Element.show,
			hideFunction: Element.hide
		};
		Object.extend(this.options,options || {});
		(typeof(this.options.linkSelector == 'string')
			? $(tab_list_container).getElementsBySelector(this.options.linkSelector)
			: this.options.linkSelector($(tab_list_container))
		).findAll(function(link){
			return (/^#/).exec(link.href.replace(window.location.href.split('#')[0],''));
		}).each(function(link){
			this.addTab(link);
		}.bind(this));
		this.containers.values().each(this.options.hideFunction);
		if(this.options.defaultTab == 'first')
			this.setActiveTab(this.links.first());
		else if(this.options.defaultTab == 'last')
			this.setActiveTab(this.links.last());
		else
			this.setActiveTab(this.options.defaultTab);
		var targets = this.options.targetRegExp.exec(window.location);
		if(targets && targets[1]){
			targets[1].split(',').each(function(target){
				this.links.each(function(target,link){
					if(link.key == target){
						this.setActiveTab(link);
						throw $break;
					}
				}.bind(this,target));
			}.bind(this));
		}
		if(this.options.autoLinkExternal){
			$A(document.getElementsByTagName('a')).each(function(a){
				if(!this.links.include(a)){
					var clean_href = a.href.replace(window.location.href.split('#')[0],'');
					if(clean_href.substring(0,1) == '#'){
						if(this.containers.keys().include(clean_href.substring(1))){
							$(a).observe('click',function(event,clean_href){
								this.setActiveTab(clean_href.substring(1));
							}.bindAsEventListener(this,clean_href));
						}
					}
				}
			}.bind(this));
		}
	},
	addTab: function(link){
		this.links.push(link);
		link.key = link.getAttribute('href').replace(window.location.href.split('#')[0],'').split('/').last().replace(/#/,'');
		this.containers[link.key] = $(link.key);
		link[this.options.hover ? 'onmouseover' : 'onclick'] = function(link){
			if(window.event)
				Event.stop(window.event);
			this.setActiveTab(link);
			return false;
		}.bind(this,link);
	},
	setActiveTab: function(link){
		if(!link)
			return;
		if(typeof(link) == 'string'){
			this.links.each(function(_link){
				if(_link.key == link){
					this.setActiveTab(_link);
					throw $break;
				}
			}.bind(this));
		}else{
			this.notify('beforeChange',this.activeContainer);
			if(this.activeContainer)
				this.options.hideFunction(this.activeContainer);
			this.links.each(function(item){
				(this.options.setClassOnContainer ? $(item.parentNode) : item).removeClassName(this.options.activeClassName);
			}.bind(this));
			(this.options.setClassOnContainer ? $(link.parentNode) : link).addClassName(this.options.activeClassName);
			this.activeContainer = this.containers[link.key];
			this.activeLink = link;
			this.options.showFunction(this.containers[link.key]);
			this.notify('afterChange',this.containers[link.key]);
		}
	},
	next: function(){
		this.links.each(function(link,i){
			if(this.activeLink == link && this.links[i + 1]){
				this.setActiveTab(this.links[i + 1]);
				throw $break;
			}
		}.bind(this));
		return false;
	},
	previous: function(){
		this.links.each(function(link,i){
			if(this.activeLink == link && this.links[i - 1]){
				this.setActiveTab(this.links[i - 1]);
				throw $break;
			}
		}.bind(this));
		return false;
	},
	first: function(){
		this.setActiveTab(this.links.first());
		return false;
	},
	last: function(){
		this.setActiveTab(this.links.last());
		return false;
	},
	notify: function(event_name){
		try{
			if(this.options[event_name])
				return [this.options[event_name].apply(this.options[event_name],$A(arguments).slice(1))];
		}catch(e){
			if(e != $break)
				throw e;
			else
				return false;
		}
	}
});
if(typeof(Object.Event) != 'undefined')
	Object.Event.extend(Control.Tabs);

// ------------ The control_tabs code [end] --------------



// ------------ Sortable [start] --------------
/*
    Make a table sortable by clicking on its header cells. Expects a table of the form:

    <table class="sortable">
		<thead>
			<tr>
				<td class="sortNumeric">ID</td>
				<td class="sortText">Summary</td>
				<td class="sortDate">Occurred on</td>
			</tr>
		</thead>
		<tbody>
			<tr> some cells...</tr>
		</tbody>
    </table>

    Where the classes of the table cell headers determine the way that the columns are sorted. It is unlikely
    to work correctly if the table contains rowspans or colspans.
	
	Assigns the CSS class(s) sortedNone, sortedAsc, sortedDesc to the table head cells.

    Inspired by Stuart Langridge's code at http://www.kryogenix.org/code/browser/sorttable/.

    Requires Prototype 1.5+ (http://prototype.conio.net/)

    This is released under the Creative Commons Attribution-ShareAlike 2.5 license.
    (http://creativecommons.org/licenses/by-sa/2.5/). 

    Copyright Inigo Surguy, 24th March 2006
	
	This version modified by John Weatherley, UCAR Digital Learning Sciences
*/

function initSortable() {
    log("Looking for tables to make sortable");
    $$("table.sortable thead td").each( function(cell) { 
		log( "Making column header '"+getText(cell)+"' sortable");
		cell.addClassName('sortedNone');
		cell.title = 'Click to sort column';
		Event.observe(cell, 'click', doSort, false);
    });
}

function doSort(e) {
	var colEle = getAncestor(e.target, "td")
    log("Sorting table column '"+getText(colEle)+"'");
    var table = getAncestor(colEle, "table");
    var rows = $A(table.rows);
    var bodyRows = rows.without(rows.first());
    var position = getCellIndex(getAncestor(colEle, "td"));
    log("There are "+bodyRows.length+" rows to be sorted - sorting the column in position "+position);
    var getCellText = function(row) { return getText($A(row.getElementsByTagName("td"))[position]); }
    var columnValues = bodyRows.map(getCellText);
    //log("Current column values order is "+columnValues);

    var simpleCompare = function(a,b) { return a < b ? -1 : a == b ? 0 : 1; };
    var compareComposer = function(normalizeFn) { return function(a,b) { return simpleCompare(normalizeFn(a), normalizeFn(b) ) }; }
    var compareFunctions = {
		"sortCaseSensitive" : simpleCompare ,
		"sortText" : compareComposer(function(a) { return a.toLowerCase(); }) ,
		// Extracts the first numeric part of a string
		"sortNumeric" : compareComposer(function(a) { 
			var num = parseInt(a.replace(/^.*?(\d+).*$/,"$1"));
			return (num = num ? num : 0);
		}) ,
		// Expects an ISO date format "13 MAR 2006 10:17:02 GMT"
		"sortDate" : compareComposer(Date.parse) ,
		// Converts a date "13 MAR 10:17" to "13 MAR 2000 10:17", which is an ISO date format usable by Date.parse
		"sortShortDate" : compareComposer(function(a) { return Date.parse(a.replace(/^(\d+)\s*(\w+)\s*(\d+:\d+)$/,"$1 $2 2000 $3")); })
    }
	
	// Determine the compare function from the CSS class (default sortText):
	var compareFunction = "sortText";
	for (var i in compareFunctions) {
		if(colEle.hasClassName(i)) {
			compareFunction = i;
			break;
		}
	}	
	
    log("Cell's compareFunction is "+compareFunction);
    var sortfn = compareFunctions[compareFunction];

    var order = (colEle.hasClassName("sortedDesc") ? -1 : 1);
    colEle.siblings().each( function(cell) {
		cell.addClassName("sortedNone").removeClassName("sortedAsc").removeClassName("sortedDesc");
    });	
    colEle.addClassName((order==-1) ? "sortedAsc" : "sortedDesc").removeClassName((order==1) ? "sortedAsc" : "sortedDesc").removeClassName("sortedNone");
    bodyRows.sort(function(rowA, rowB) { return order * sortfn( getCellText(rowA), getCellText(rowB) ); });
    bodyRows.each(function(row) { table.tBodies[0].appendChild(row); })
    log("Table sorted");
}

function getText(element) {
	if(element)
		return (element.textContent) ? element.textContent : element.innerText; 
	return ''; 
}

// The td.cellIndex member doesn't work in Safari, so use a function to do the same task
function getCellIndex(td) { return $A(td.parentNode.cells).indexOf(td); }

function getAncestor(element, type) { 
    var ancestor = element; 
    while(ancestor && ancestor.tagName.toUpperCase()!=type.toUpperCase()) { ancestor=ancestor.parentNode; } 
    return ancestor; 
}
function log(m) {
	if(window.console)
		window.console.log(m);
}
Event.observe(window, 'load', initSortable, false);

// ------------ Sortable [end] --------------


Event.observe(window, 'load', onPageLoad, false);

function displayRecord(setSpec, metadatahandle, recordType, responseType){
	url = contextPath+'/metadata_db_record.jsp?setSpec='+setSpec+"&metadatahandle="+metadatahandle+"&recordType="+recordType+"&responseType="+responseType;
	var newWindow = window.open(url, '_blank');
	newWindow.focus();

    }