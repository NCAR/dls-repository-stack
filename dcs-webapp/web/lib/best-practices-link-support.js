/*
	bestPractices handler for fields in the editor. Url opens best practices page in new window
	- xpath refers to metadata field
	- format identifies metadata framework
	currently simply builds a url and submits to openBestPracticesWindow, but could be customized
	to invoke other behavior.
*/
function doBestPractices (xpath, format) {
	var url = "../editor/edit.do?command=bestpractices&xmlFormat=" + format + "&pathArg=" + xpath;
	openBestPracticesWindow (url);
}

/* 
	used for hand-crafted bestPractices. Takes a topic name and constructs a url to that topic within
	the best-practices directory, where static files exist for each topic
	DEPRECIATED in favor of the best practices using the metadataVocab Fields files (see "doBestPractices" above)
*/
function doBestPractice (topic) {
	var url = "best-practices/" + topic + ".html";
	openBestPracticesWindow (url);
	return false;
}
	
/* utility - opens provided URL in a named window */
function openBestPracticesWindow (url, window_name) {
	window_name = window_name || "bpwin"
	var features = "innerHeight=500,height=500,innerWidth=650,width=650,resizable=yes,scrollbars=yes";
	features += ",locationbar=no,menubar=no,location=no,toolbar=no";
	var win = window.open (url, window_name, features);
	win.focus();
}


