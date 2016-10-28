/*
	This file should be first loaded for all page javascript.

	It is included by "/baseHTMLIncludes.jsp"
*/

var ASCENDING = 0;
var DESCENDING = 1;

/*
	write to the fireBug console if possible - make some attempt to support
	formatting strings (e.g., "my name is %s", myname)
*/

Prototype.Browser.IE6 = Prototype.Browser.IE && parseInt(navigator.userAgent.substring(navigator.userAgent.indexOf("MSIE")+5)) == 6;
Prototype.Browser.IE7 = Prototype.Browser.IE && parseInt(navigator.userAgent.substring(navigator.userAgent.indexOf("MSIE")+5)) == 7;
Prototype.Browser.IE8 = Prototype.Browser.IE && !Prototype.Browser.IE6 && !Prototype.Browser.IE7;

function isBrowserOldIE () {
	
	var browser=navigator.appName;
	var b_version=navigator.appVersion;
	var version=parseFloat(b_version);

	// alert ("IE?: " + Prototype.Browser.IE + "\nversion: " + version);

	// Explorer 9 is version 5 and canNOT render dcstable filter
	if (Prototype.Browser.IE && version<=5)
		return true;
	else
		return false;
}

function log (val) {
	if(window.console)
		window.console.log(val);
}

/* call to escape regex special chars */
RegExp.quote = function(str) {
    return (str+'').replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1");
};


