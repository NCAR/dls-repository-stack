/**
this code is evaluated after the dom has been loaded for the upload_helper.jsp file.

It makes use of jsp tags to get values, and thus it is injected into the jsp file 

THIS CODE ASSUMES THE SCRIPT TAGS ARE PRESENT IN THE TARGET JSP
 */

log ("HELLO FROM VIEW SCRIPTS");

$('collection-display-trigger').observe ('click', function (event) {
		var display = $('collections-display');
		var trigger = event.element();
		var layout = trigger.getLayout();
		
		if (!display.visible()) {
			var top = layout.get('top')+layout.get('border-box-height') + 3;
			log ("TOP: " + top);
			display.setStyle({
					top:top+'px'
				});
		}
		display.toggle();
}); 

$('fileInputButton').observe ('mouseover', function (event) {
	$('visible-upload-button').addClassName('over');
});
$('fileInputButton').observe ('mouseout', function (event) {
	$('visible-upload-button').removeClassName('over');
});
$('fileInputButton').observe ('keydown', function(event) {
		/* $('visible-upload-button').removeClassName('over'); */
});





