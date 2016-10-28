Event.observe(window, 'load', onPageLoad);
function onPageLoad() {
	updateTitleBar();
	if($('chars'))
		$('chars').focus();	
	doFind();
}

// Make the title bar match the page title:
function updateTitleBar() {
	if($('titleBar') && document.title && document.title.length > 0)
		$('titleBar').update(document.title);
}

// In place of the on load if installed at dlese
function dlese_pageOnLoad() {}


function popupHelp(path, w){
	
	if (w == 'split')
	{
		newwindow=window.open(path+"/popup.jsp?help="+w, "help", "scrollbars,resizable,width=500,height=500");
		if(newwindow == null)
			alert("Please enable popups for this site. The help window could not be displayed.");
		else
			newwindow.focus();
	}	
	else
	{
		newwindow=window.open(path+"/popup.jsp?help="+w, "help", "scrollbars,resizable,width=500,height=500");
		if(newwindow == null)
			alert("Please enable popups for this site. The help window could not be displayed.");
		else			
			newwindow.focus();
	}
}

// ---------------------------------- mailto: spambot avoiding ----------------------------------
function renderEmailAddress( prefix, postfix ) {
	var address = prefix + "@" + postfix;
	document.write( "<a href='mailto:" + address + "'>" + address + "</a>" );
}

function dlese_rea13( prefix, postfix ) {
	if ( typeof rot13 != "undefined" )
		renderEmailAddress( rot13( prefix ), rot13( postfix ) );	
}

// ------------------------------- Rot13 for mailto: obfuscation -------------------------------
// This work is hereby released into the Public Domain. To view a copy of the 
// public domain dedication, visit http://creativecommons.org/licenses/publicdomain/ 
// or send a letter to Creative Commons, 559 Nathan Abbott Way, Stanford, California 94305, USA.
// origin: 2000-01-08 nospam@geht.net http://tools.geht.net/rot13.html
// Use at own risk.
// The problem is that JavaScript 1.0 does not provide a Char to Numeric value conversion.
// Thus we define a map.
// Because there are 64K UniCode characters, this map does not cover all characters.
//
var rot13map; 
function rot13init() { 
	var map = new Array(); 
	var s = "abcdefghijklmnopqrstuvwxyz"; 
	for (i=0; i<s.length; i++) 
		map[s.charAt(i)] = s.charAt((i+13)%26); 
	for (i=0; i<s.length; i++) 
		map[s.charAt(i).toUpperCase()] = s.charAt((i+13)%26).toUpperCase(); 
	return map; 
} 
function rot13(a) { 
	if (!rot13map) 
		rot13map = rot13init(); 
	s = ""; 
	for (i=0; i<a.length; i++) { 
		var b = a.charAt(i); 
		s += (b>='A' && b<='Z' || b>='a' && b<='z' ? rot13map[b] : b); 
	} 
	return s; 
}


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
    to work correctly if the table con`tains rowspans or colspans.
	
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


// ------------ Find collections  --------------

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

// ------------ Find [end]  --------------

