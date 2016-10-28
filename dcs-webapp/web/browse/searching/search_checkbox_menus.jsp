<%--
	This JSP page constructs checkbox menus for grade ranges, subjects, 
	resource types and collections, etc, for the search page. 
	
	It is meant to be included inside a
	HTML <form> element named 'searchForm' of an encompassing JSP.
	
--%>

<%-- Define JavaScript used to show/hide and make selections in the menus --%>
<script type="text/javascript">
<!--	

	var arrowUp = "";
	var arrowDown = "";
	if(document.images) {
		arrowUp = new Image;
		arrowDown = new Image;
		arrowUp.src = "../images/btnExpandClsd.gif";
		arrowDown.src = "../images/btnExpand.gif";
	} 
	var duration = 0.2;
	var showEffect = Effect.Appear; // BlindDown
	var hideEffect = Effect.Fade; // BlindUp

	function doSetNumPagingRecords(resultsPerPage) {
		var nonPaigingParams = unescape("${queryForm.nonPaigingParams}");
		var params = $H(nonPaigingParams.toQueryParams());
		
		params.set('command', 'setNumPagingRecords');
		params.set('resultsPerPage', resultsPerPage);
		// scrub '+' from query string
		params.set('q', params.get('q').sub(/\+/, ' '));
		
		var loc = "query.do?" + params.toQueryString();
		window.location = loc;
	}
	
	function clearSearchString () {
		$("queryForm").q.value = "";
		sf();
	}

	function getId4StatusNoteWidget (widget) {
		try {
					return widget.up ('.item-info').previous('.item-header').id;
		} catch (error) {
			log ("getId4StatusNoteWidget error: " + error);
		}
		return null;
	}
	
	/* controls the open/close of search critieria selections */
	function toggleVisibility( elementID ) {
		var objElement = $(elementID);
		var arrowObj = $(elementID+'_arrow')
		if (objElement) {
			if (true) { // use effects?
				if (objElement.visible()) {
					hideEffect (objElement, {duration:duration});
					arrowObj.src = arrowUp.src;
				}
				else {
					showEffect (objElement, {duration:duration});
					arrowObj.src = arrowDown.src;
					}
			}
			else {
				objElement.toggle();
				if (arrowObj)
						arrowObj.src = (objElement.visible() ? arrowDown.src : arrowUp.src);
			}
		}
	}
	
	// debugging: show some attributes of an event
	function eventProps (event) {
		log ("event props");
		var attrs = ['type', 'ctrlKey', 'altKey', 'metaKey', 'shiftKey'];
		$A(attrs).each ( function (attr) {
			log ("\t" + attr + ": " + event[attr]);
		});
	}
		
	function handleStatusVisibilityWidget (event) {
		// log ("handleStatusVisibilityWidget");
		// eventProps (event);
		var widget = event.element();
		event.stop();
		var header = widget.up ('.item-info').previous('.item-header');
		var id = header.id;
		var statusNote = $("statusNote_" + id);
		var newState = statusNote.visible() ? 'hide' : 'show';
		if (event.shiftKey) {
			try {
				$$('.item-header').each (function (header) {
					setStatusVisibility (header.id, newState);
				});
			} catch (error) {
				log (error);
			}
			// alert ("shitfKey");
		}
		else {
			try {
				setStatusVisibility (id, newState);
			} catch (error) {
				log (error);
			}
		}
	}
	
 	function setStatusVisibility( id, state ) {
		var statusNote = $("statusNote_" + id);
		if (!statusNote)
			throw ("statusNote not found for " + id);
		if (state == 'hide') {
			hideEffect (statusNote, {duration:duration});
		}
		else if (state == 'show') {
			showEffect (statusNote, {duration:duration});
		}
		else
			throw ("recieved illegal state: " + state);
	}
	
	function clearBoxes ( inputId ) {
		try {
			var items = $('queryForm')[inputId];
			if (!items.length)
				items = [items]
			$A(items).each ( function (box) {box.checked = false;});
		} catch (error) {}
	}
	
	function setBoxes ( inputId ) {
		try {
			var items = $('queryForm')[inputId];
			if (!items.length)
				items = [items]
			$A(items).each ( function (box) {box.checked = true;});
		} catch (error) {}
	}
	
	function clearEditorSelections( ) {
		clearBoxes ('ses')
	}
	
	function clearStatusSelections( ) {
		clearBoxes ('sss');
	}
	
	function clearValiditySelections () {
		clearBoxes ('vld');
	}
	
	function clearFormatSelections( ) {
		clearBoxes ('sfmts');

	}
	
	function clearCollectionSelections( ) {
		clearBoxes ('scs');
	}
	
	function clearCreatorSelections( ) {
		clearBoxes ('srcs');
	}
	
	function clearAllSelections( ) {
		clearSearchString();
		clearEditorSelections();
		clearStatusSelections();
		clearFormatSelections();
		clearValiditySelections ()
		clearCollectionSelections ();
		clearCreatorSelections ();
		if ($( 'yourSelections' ))
			$( 'yourSelections' ).hide();
	}	
	
	/* functions controlling batch operations */		
	
	function authBatchMove () {
		<c:if test="${sf:isAuthorized('batchMove', sessionBean)}">
			return true;
		</c:if>
		return false;
		}
		
	function authBatchCopyMove () {
		<c:if test="${sf:isAuthorized('batchCopyMove', sessionBean)}">
			return true;
		</c:if>
		return false;
		}
		
	function authBatchDelete () {
		<c:if test="${sf:isAuthorized('batchDelete', sessionBean)}">
			return true;
		</c:if>
		return false;
		}
		
	function authBatchStatus () {
		<c:if test="${sf:isAuthorized('batchStatus', sessionBean)}">
			return true;
		</c:if>
		return false;
		}
		
	/* Execute batch operation if user is authorized */
	function doBatchOp (operation) {
		var srcCollection = "${param.scs}";
		if (srcCollection != null && srcCollection.length > 1)
			srcCollection = srcCollection.substring(1)
	
		if (operation == null || operation == "") {
			alert ("please select an operation");
			return;
			}
		var authorized = null;
		if (operation == "batchDelete") authorized = authBatchDelete ();
		if (operation == "batchMove") authorized = authBatchMove();
		if (operation == "batchCopyMove") authorized = authBatchCopyMove();
		if (operation == "batchStatus") authorized = authBatchStatus();
		
		if (authorized == null) {
			 alert ("unsupported operation: " + operation);
		}
		
		else if (authorized == false) {
			alert ("you are not authorized to perform a " + operation);
			}
		else {
			window.location = "${contextPath}/record_op/batch.do?op=" + operation + "&scs=" + srcCollection;
			}
		return false;
	}

Event.observe (window, 'load', function (event) {
	log ('initializing checkboxes');
	$$('.checkboxHeading').each (function (element) {
		element.observe ('click', toggleCheckboxControls);
	});
});
	
function toggleCheckboxControls (event) {
		var element = event.findElement('div');
		var controls = element.down('.checkbox-control');
		if (controls)
			controls.toggle();

}

	
// -->
</script>



