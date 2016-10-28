/*
	NOTE: 
		- closedImg, -openedImg must be set by calling page
*/

function toggleVisibility( item ) {
	unHighlight();
	if ($(item+"_id"))
		$(item+"_id").visible() ? closeItem (item) : openItem (item);
}

function closeItem ( item ) {
	$(item+"_id").hide();
	img = $(item+"_widget").down ('img');
	img.src = closedImg;
}
	
function openItem ( item ) {
	$(item+"_id").show();
	img = $(item+"_widget").down ('img');
	img.src = openedImg;
}

function rowOver (evnt) {
	var row = getRow (Event.element(evnt));
	if (row) row.addClassName ("over");
}

function rowOut (evnt) {
	var row = getRow (Event.element(evnt));
	if (row) row.removeClassName ("over");
}

function getRow (obj) {
	if (obj)
		return obj.up ('tr');
	return null;
}

function highlight (obj) {
	var row = getRow (obj);
	unHighlight();
	if (row) row.addClassName ('highlight');
}

function unHighlight () {
	$$ ('.item-table-row').each (function (row) {
		row.removeClassName ("highlight");
	});
}

// activate the item-table's widgets and buttons
function itemTableInit () {
		// activate "open-user-widgets"
	$$(".open-close-widget").each (function (wgt) {
		wgt.observe ('click', function (evnt) {
			var item = wgt.id.substring ( 0, wgt.id.length - "_widget".length);
			toggleVisibility (item)
			Event.stop (evnt);
		});
	});
		
 	$$('.item-table-row').each (function (row) {
		row.observe ('mouseover', rowOver);
		row.observe ('mouseout', rowOut);
		row.removeClassName ("highlight");
	});
	
	$$('inner-table').each (function (it) {
		it.getElementsBySelector ('tr').each ( function (row) {
			row.observe ('mouseover', rowOver);
			row.observe ('mouseout', rowOut);
		});
	});
	
	if ($("open-items-button")) {
		// activate "open-items-button"
		Event.observe ("open-items-button", 'click', function (evnt) {
			$$(".open-close-widget").each (function (wgt) {
				var item = wgt.id.substring ( 0, wgt.id.length - "_widget".length);
				openItem (item);
				});
			Event.stop (evnt);
			}, false);
	}
	
	if ($("close-items-button")) {
		// activate "close-items-button"
		Event.observe ("close-items-button", 'click', function (evnt) {
			$$(".open-close-widget").each (function (wgt) {
				var item = wgt.id.substring ( 0, wgt.id.length - "_widget".length);
				closeItem (item)
				});
			Event.stop (evnt);
		});
	}
}



