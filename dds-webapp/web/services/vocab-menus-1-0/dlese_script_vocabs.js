// ------------------------------------ DLESE controlled vocabulary dHTML functions ------------------------------------

dlese_pageHasVocabs = true;

var dlese_CHECKBOX_IMG = new Array();

dlese_CHECKBOX_IMG[ 0 ] = new Image();

dlese_CHECKBOX_IMG[ 0 ].src = "/dlese_shared/images/vocab/boxWhite.gif";

dlese_CHECKBOX_IMG[ 1 ] = new Image();

dlese_CHECKBOX_IMG[ 1 ].src = "/dlese_shared/images/vocab/boxBlack.gif";

dlese_CHECKBOX_IMG[ 2 ] = new Image();

dlese_CHECKBOX_IMG[ 2 ].src = "/dlese_shared/images/vocab/boxGray.gif";

var dlese_ARROW_OPEN = new Image(); dlese_ARROW_OPEN.src = "/dlese_shared/images/vocab/ArrowUpTransp.gif";

var dlese_ARROW_CLOSED = new Image(); dlese_ARROW_CLOSED.src = "/dlese_shared/images/vocab/ArrowDownTransp.gif";

var dlese_HIDE_STATUS_MOUSE_OVER =  " onMouseOver=\"self.status = ''; return true;\" onMouseUp=\"self.status = ''; return true;\" onClick=\"self.status = ''; return true;\"";

var dlese_LABEL_WRAP_WIDTH = 50;	

var dlese_CLEAR_ALL_BUTTON = "/dlese_shared/images/vocab/clear_all.gif";

var dlese_CLEAR_ALL_BUTTON_GREYED_OUT = "/dlese_shared/images/vocab/clear_all_gray.gif";



var dlese_allLists = new Array();

var dlese_allListsSimpleName = new Array();

var dlese_loadedQuery = ''; 

function dlese_vocabList( name, groupType, groupLabel, groupLabelAbbrev ) {

	this.name = name;

	this.groupLabel = groupLabel;

	this.groupLabelAbbrev = groupLabelAbbrev;

	this.div = name + 'div';

	this.parent = 0;

	this.parentIndex = 0;

	this.level = 1;	

	this.closeCall = 0;

	this.groupType = groupType;	// 0 = collapsible, 1 = fixed open

	this.label = new Array();

	this.labelAbbrev = new Array();

	this.sublist = new Array();

	this.checkedState = new Array();

	this.inputField = new Array();

	this.inputValue = new Array();

	this.wrap = new Array();

	this.description = new Array();

	this.href = new Array(); // Special case, list element is just a link, not a checkbox or list sub-header

}

function dlese_AV( list, label, labelAbbrev, field, value, isChecked, wrap, sublist, href, description ) {

	var ind = list.label.length;

	list.label[ ind ] = dlese_wrapVocabLabel( label );

	list.labelAbbrev[ ind ] = labelAbbrev;

	list.inputField[ ind ] = field;

	list.inputValue[ ind ] = value;

	if ( dlese_loadedQuery.indexOf( "&" + field + "=" + value + "&" ) > -1 )

		list.checkedState[ ind ] = 1;

	else

		list.checkedState[ ind ] = 0;

	list.wrap[ ind ] = wrap;

	if ( sublist ) {

		list.sublist[ ind ] = sublist;

		sublist.parent = list;

		sublist.parentIndex = ind;

		sublist.level = list.level + 1;

	}

	if ( description )

		list.description[ ind ] = description;

	if ( href )

		list.href[ ind ] = href;

}

function dlese_wrapVocabLabel( label ) {

	var ind = label.indexOf( " " );

	var ret = "";

	var tet = ""

	while ( ind > -1 ) {

		tet += label.substring( 0, ind + 1 );

		label = label.substring( ind + 1, label.length );

		ind = label.indexOf( " " );

		if ( ( ind > -1 ) && ( tet.length > dlese_LABEL_WRAP_WIDTH ) ) {

			ret += tet + "<br>&nbsp;";

			tet = "";

		}

	}

	ret += tet + label;

	var regex = /\'/g;

	return "<nobr>&nbsp;" + ret.replace( regex, "&#39;" ) + "&nbsp;</nobr>";

}

function dlese_searchOnLoad() {

	dlese_showSelectClearLinksIfGroupTwo();	

}

function dlese_setList( name ) {

	var list = eval( "tm_" + name + "0" );

	dlese_setParentStates( list );

	dlese_allLists[ dlese_allLists.length ] = list.name;

	dlese_allListsSimpleName[ dlese_allListsSimpleName.length ] = name;

}

function dlese_renderSelectedVocabsFeedback() {

	var obj = document.getElementById( "clearAll" );

	if ( obj ) 

		obj.style.visibility = 'visible';

	dlese_setVocabQueryString();

}



// Top level vocab toggle buttons:

function dlese_vocabButton( name, closedImg, openImg, left, top ) {

	this.name = name;

	this.closedImg = new Image();

	this.closedImg.src = closedImg;

	this.openImg = new Image();

	this.openImg.src = openImg;

	this.isOpen = false;

	this.isRendered = false;

	this.left = left;

	this.top = top;	

	this.focusLink = 0;

}

function dlese_toggleVocabButton( name ) {

	var button = eval( "document.vButton" + name );

	button.isOpen = !button.isOpen;

	var imgObj = document.getElementById( button.name + "button" );

	if ( button.isOpen ) {

		imgObj.src = button.openImg.src;

	}

	else {		

		mouseClickedOnOpenVocab = false;

		imgObj.src = button.closedImg.src;

	}

	if ( imgObj.style.cursor )

		imgObj.style.cursor = 'pointer'; 

}

function dlese_openVocabButton( name ) {

	var button = eval( "document.vButton" + name );

	var imgObj = document.getElementById( button.name + "button" );

	imgObj.src = button.openImg.src;

	if ( imgObj.style.cursor )

		imgObj.style.cursor = 'wait';

}

function dlese_closeVocabButton( name ) {

	var button = eval( "document.vButton" + name );

	button.isOpen = false;

	mouseClickedOnOpenVocab = false;

	var imgObj = document.getElementById( button.name + "button" );

	imgObj.src = button.closedImg.src;

	if ( imgObj.style.cursor )

		imgObj.style.cursor = 'pointer';

}

function dlese_toggleVocab( listName ) {

	dlese_toggleVocabButton( listName ); 

	setTimeout( "dlese_doToggleVocab( '" + listName + "' )", 1 );

}

function dlese_doToggleVocab( listName ) {

	dlese_hideAllVocab( "tm_" + listName + "0" );

	toggleObjDisplay( "tm_" + listName + "0div" );	

}

function dlese_checkHideFrontPage( name ) {

	if ( dlese_isFrontPage ) {

		var button = eval( "document.vButton" + name );

		if ( button ) {

			if ( button.isOpen ) {

				dlese_hideSelectListsIE( true );

				dlese_vocabsShowing = true;

			}

			else {

				dlese_hideSelectListsIE( false );

				dlese_vocabsShowing = false;

			}

		}

		else

			dlese_vocabsShowing = false;

	}

}

function dlese_renderVocabButton( name, closedImg, openImg, altText, left, top, listLeft, listTop ) {

	eval( "document.vButton" + name + " = new dlese_vocabButton( '" + name + "', '" + closedImg 

		+ "', '" + openImg + "', " + listLeft + ", " + listTop + " );" );

	document.writeln( "<div class='dlese_vocabButtons' id='" + name + "buttonDiv' style='position: absolute; z-index: 30000; top: " 

		+ top + "px; left: " + left + "px;'><a href='javascript:dlese_vocabButtonClicked( \"" + name + "\" );'"

		+ "class='cursorPointer' " + dlese_HIDE_STATUS_MOUSE_OVER + " onMouseDown='mouseClickedOnOpenVocab = true;'><img alt='" 

		+ altText + "' border='0' id='" + name + "button' " + "src='" + closedImg 

		+ "' class='cursorPointer'></a></div>" );

}

function dlese_defineVocabButton( name ) { // For buttons that aren't displayed (i.e., subject):

	eval( "document.vButton" + name + " = new dlese_vocabButton( '" + name + "', '', '', 0, 0 );" );

}

function dlese_activateVocabButton( name ) {

	var list = eval( "tm_" + name + "0" );

	list.closeCall = "dlese_closeVocabButton( '" + name + "' )";

}

function dlese_focusFirstListLink( name ) {

	var fLink = document.getElementById( "vButton" + name + "FocusLink" );

	var fList = eval( name );

	var fLayer = document.getElementById( fList.div );

	if ( fLink != null && fLayer.style.display != 'none' && fLayer.style.visibility != 'hidden' )

		fLink.focus();

}

function dlese_vocabButtonClicked( name ) {

	var button = eval( "document.vButton" + name );

	if ( button.isRendered ) {

		dlese_toggleVocab( name );

		setTimeout( "dlese_focusFirstListLink( 'tm_" + name + "0' )", 200 );

	}

	else {

		dlese_openVocabButton( name );

		setTimeout( "dlese_startRenderVocab( '" + name + "' )", 10 );

	}

	setTimeout( "dlese_checkHideFrontPage('" + name + "')", 100 );

	window.status = '';

}

function dlese_startRenderVocab( name ) {

	eval( "document.vButton" + name + ".focusLink = " + ( document.links.length + 3 ) );

	dlese_renderVocab( name );

	dlese_toggleVocab( name );

	var imgObj = document.getElementById( name + "button" );

	if ( imgObj.style.cursor )

		imgObj.style.cursor = 'pointer';

	dlese_showSelectClearLinksIfGroupTwo();

}

function dlese_renderVocab( name, simple ) {

	var button = eval( "document.vButton" + name );

	var list = eval( "tm_" + name + "0" );

	var top = button.top;

	var left = button.left;

	dlese_displayVocab( list, left, top, true );

	dlese_setToVisible( list );

	dlese_activateVocabButton( name );

	button.isRendered = true;	

}

var innerTextAppend = '';

function dlese_displayVocab( list, x, y, topCall ) {	

	var innerText = '';

	var innerTextAppend = '';

	var sc;

	if ( topCall ) {

		sc = getObject( 'dlese_vocabButtons' );

		innerText = sc.innerHTML;

		innerTextAppend = '';

	}

	if ( topCall )

			innerText += "<table id='" + list.div + "' style='z-index: 10; position: relative; left: " + x + "px; top: " 

					+ y + "px; display: none; visibility: visible;' class='dlese_vocabFlyout' onMouseDown='mouseClickedOnOpenVocab = true;'>"

					+ "<tr><td><table border='0' cellpadding='0' cellspacing='0'><tr><td class='dlese_vocabFlyoutIndent'></td><td>";		

	else 

		innerText += "<table id='" + list.div + "' style='display: block; visibility: visible;' class='dlese_vocabDropdown'><tr><td>";

	innerText += "<table cellpadding='0' cellspacing='0' border='0'><tr><td>";

	if ( list.level == 1 )

		innerText += "<table width='100%' border='0' cellpadding='0' cellspacing='0'><tr>"

			+ "<td align='left' valign='top'><a href='javascript:dlese_selectClearAll( " + list.name + " )' "

			+ "class='dlese_vocabSelectClearLinksTopLevel' onMouseOver=\"document.status = ''; return true;\"><nobr>Select / Clear all</nobr></a></td>"

			+ "<td align='right' valign='top'><a href='javascript:dlese_hideList( " + list.name + " )' "

			+ "class='dlese_vocabSelectClearLinksTopLevel' onMouseOver=\"document.status = ''; return true;\" "

			+ "title='Close'>X</a></td></tr></table></td></tr>"

			+ "<tr><td height='1' class='dlese_vocabsDividerLines'></td></tr><tr><td>";

	innerText += "<table cellpadding='0' cellspacing='0' border='0'><tr>"

		+ "<td align='left' valign='top'>";

	innerText += "<table cellpadding='0' cellspacing='0' border='0' style='margin: 0px; padding: 0px;'>";

	for ( var i = 0; i < list.label.length; i++ ) {

		if ( i == 0 && ( list.name == 'tm_ky0' ) )

			innerText += "<tr><td valign='top' colspan='2' style='padding-left: 12px;'>"

				+ "<a href='http://www.dlese.org" + dlese_DISCOVERY_ROOT + "browse.htm' title='Browse resources in each collection'>Browse resources &amp; collections</a></td></tr>"; 

		var rightLink = '';

		innerText += "<tr><td valign='top' width='5'>";

		if ( list.href[ i ] ) {

			innerText += "<a href='" + list.href[ i ] + "'>" + list.label[ i ] + "</a>";

		}

		else if ( !list.sublist[ i ] ) {

			innerText += "<a href='javascript:dlese_toggleInput( " + list.name + ", " + i + " );' class='dlese_listToggleLink' " + dlese_HIDE_STATUS_MOUSE_OVER + ">";

			innerText += "<img id='" + list.name + i + "check' border='0' src='" + dlese_CHECKBOX_IMG[ list.checkedState[ i ] ].src 

				+ "' style='margin-top: 4px; margin-bottom: 0px;' alt='Click to select " + list.label[ i ] + "'></a> ";	

		}

		else if ( list.sublist[ i ].groupType < 1 ) {

			innerText += "<a href='javascript:dlese_toggleInput( " + list.name + ", " + i + " );' class='dlese_listToggleLink' " + dlese_HIDE_STATUS_MOUSE_OVER + ">";

			innerText += "<img id='" + list.name + i + "arrow' border='0' src='"  

					+ dlese_ARROW_CLOSED.src + "' style='margin-top: 4px; margin-bottom: 0px;'></a>";

		}

		var focusId = '';

		if ( list.level == 1 && i == 0 ) {

			focusId = ' id="vButton' + list.name + 'FocusLink"';

		}

		var moText = dlese_HIDE_STATUS_MOUSE_OVER;

		if ( list.description[ i ] ) {

			moText = " onMouseOver=\"dlese_showDescription( " + list.name + ", " + i + ", event ); self.status = ''; return true;\""

				+ "onMouseOut=\"dlese_hideDescription(); self.status = ''; return true;\"";

		}		

		if ( list.sublist[ i ] ) {

			if ( list.sublist[ i ].groupType == 1 ) {

				if ( list.sublist[ i ].sublist[ 0 ] ) {

					rightLink = "<a href='#' class='dlese_listToggleLink' style='position: relative; left: -4px;'" 

						+ focusId + moText + ">";

				}

				else

					rightLink = "<a href='#' class='dlese_listToggleLink' style='position: relative; left: -12px;'" 

						+ focusId + moText + ">";

			}

			else {

				rightLink = "<a href='javascript:dlese_toggleInput( " + list.name + ", " + i 

					+ " );' class='dlese_listToggleLink'" + focusId + moText + ">";

			}

		}

		else {

			rightLink = "<a href=\"javascript:dlese_toggleInput( " + list.name + ", " + i 

				+ " );\" class='dlese_listToggleLink'" + focusId + moText + ">";

		}	

		innerText += "</td><td valign='top' nowrap='true' width='100%'>" + rightLink + list.label[ i ] + "</a>";

		if ( list.sublist[ i ] ) {

			innerText += " <a href='javascript:dlese_selectClearAll( " + list.sublist[ i ].name 

				+ " )' id='vocabSelectClear" + list.sublist[ i ].name 

				+ "' class='dlese_vocabSelectClearLinks' onMouseOver=\"document.status = ''; return true;\">Select / Clear all</a>";

			if ( list.sublist[ i ].groupType > 0 )

				innerText += "</td></tr><tr><td colspan='2'>";

			innerText += dlese_displayVocab( list.sublist[ i ] );

		}

		innerText += "</td></tr>";

		if ( list.wrap[ i ] ) {

			innerText += "</table></td><td width='1' class='dlese_vocabsDividerLines'><div style='font-size: 1px;'>&nbsp;</div></td>"

				+ "<td>&nbsp;</td><td align='left' valign='top'>"

				+ "<table border='0' cellpadding='0' cellspacing='0'>";

		}

	}

	innerText += "</table></td></tr></table></td></tr></table></td></tr></table>";	

	if ( topCall ) {

		innerText += "</td></tr></table>";

		sc.innerHTML = innerText;

		setTimeout( "dlese_focusFirstListLink( '" + list.name + "' )", 200 );

	}

	else

		return innerText;

}

function dlese_showDescription( list, index, event ) {

	var obj = document.getElementById( "dlese_vocabDescriptions" );

	if ( obj ) {

		var scrollOffset = 0;

		if ( navigator.appName == "Microsoft Internet Explorer" ) {

			scrollOffset = document.body.scrollTop;

		} 

		else { 

			scrollOffset = window.pageYOffset;

		}		

		obj.innerHTML = list.description[ index ];

		var goTop = event.clientY + 20 + scrollOffset;

		if ( event.clientY + 40 > document.body.clientHeight )	// make sure it doesn't go below visible area

			goTop = event.clientY - 40 + scrollOffset;

		obj.style.top = goTop;

		var goLeft = event.clientX + 10;

		if ( goLeft + 320 > document.body.clientWidth )	// make sure it doesn't go off the right edge of visible area

			goLeft = document.body.clientWidth - 320;

		obj.style.left = goLeft;

		obj.style.display = 'block';

	}

}

function dlese_hideDescription( divId ) {

	var obj = document.getElementById( "dlese_vocabDescriptions" );

	if ( obj ) {

		obj.style.display = 'none';

	}

}

function dlese_toggleInput( list, index ) {

	dlese_vocabsShowing = true;

	var imgObj;

	if ( list.sublist[ index ] && ( list.sublist[ index ].groupType < 1 ) ) { // just toggling display, NOT state

		var timeoutDelay = 0;

		for ( var i = 0; i < list.label.length; i++ ) {

			if ( list.sublist[ i ] && ( i != index ) && ( list.sublist[ i ].groupType < 1 ) ) {

				if ( eval( "document.getElementById( '" + list.sublist[ i ].div + "' ).style.display" ) != 'none' ) {

					hideObj( list.sublist[ i ].div );

					imgObj = document.getElementById( list.sublist[ i ].name + "arrow" );

					if ( imgObj ) {

						if ( list.sublist[ i ].groupType == 0 )

							imgObj.src = dlese_ARROW_CLOSED.src;

						else

							imgObj.src = dlese_ARROW_CLOSED_DROPDOWN.src;

					}

					var selectClearLink = document.getElementById( "vocabSelectClear" + list.sublist[ i ].name );

					if ( selectClearLink && selectClearLink.style )

						selectClearLink.style.visibility = 'hidden';

					timeoutDelay = 250;

				}

			}

		}

		setTimeout( "dlese_toggleInputFinish( " + list.name + ", " + index + " )", timeoutDelay );

	}

	else {

		imgObj = document.getElementById( list.name + index + "check" );		

		if ( list.checkedState[ index ] == 0 ) {

			if ( imgObj )

				imgObj.src = dlese_CHECKBOX_IMG[ 1 ].src;

			list.checkedState[ index ] = 1;

		}

		else if ( list.checkedState[ index ] == 1 ) {

			if ( imgObj )

				imgObj.src = dlese_CHECKBOX_IMG[ 0 ].src;

			list.checkedState[ index ] = 0;

		}

		dlese_checkParentState( list );

		dlese_setVocabQueryString();

	}

	var except = list;

	while ( except.parent.parent ) {

		except = except.parent;

	}

	var topList = except.parent;

	if ( topList ) {

		for ( var i = 0; i < topList.label.length; i++ )

			if ( topList.sublist[ i ] != except )

				dlese_hideList( topList.sublist[ i ] );

	}	

}

function dlese_toggleInputFinish( list, index ) {

	dlese_vocabsShowing = true;

	toggleObjDisplay( list.sublist[ index ].div );

	var obj = document.getElementById( list.sublist[ index ].div );

	imgObj = document.getElementById( list.sublist[ index ].name + "arrow" );		

	if ( obj.style.visibility == 'visible' && imgObj ) {

		if ( ( list.sublist[ index ].groupType > 0 ) )

			imgObj.src = dlese_ARROW_OPEN_DROPDOWN.src;

		else

			imgObj.src = dlese_ARROW_OPEN.src;

		var selectClearLink = document.getElementById( "vocabSelectClear" + list.sublist[ index ].name );

		if ( selectClearLink && selectClearLink.style )

			selectClearLink.style.visibility = 'visible';

	}

	else {

		if ( ( list.sublist[ index ].groupType == 0 ) && imgObj )

			imgObj.src = dlese_ARROW_CLOSED.src;

		else if ( imgObj )

			imgObj.src = dlese_ARROW_CLOSED_DROPDOWN.src;

		if ( list.sublist[ index ].groupType != 1 ) {

			var selectClearLink = document.getElementById( "vocabSelectClear" + list.sublist[ index ].name );

			if ( selectClearLink && selectClearLink.style )

				selectClearLink.style.visibility = 'hidden';

		}		

	}

}

function dlese_checkParentState( list, dontRender ) {

	if ( list.parent ) {

		var noneChecked = true;

		var allChecked = true;

		for ( var i = 0; i < list.label.length; i++ )

			if ( list.checkedState[ i ] == 0 )

				allChecked = false;

			else if ( list.checkedState[ i ] == 1 )

				noneChecked = false;

			else if ( list.checkedState[ i ] == 2 )

				allChecked = noneChecked = false;

		if ( noneChecked )

			list.parent.checkedState[ list.parentIndex ] = 0;

		else if ( allChecked )

			list.parent.checkedState[ list.parentIndex ] = 1;

		else

			list.parent.checkedState[ list.parentIndex ] = 2;		

		var imgObj = document.getElementById( list.parent.name + list.parentIndex + "check" );

		if ( !dontRender && imgObj )

			imgObj.src = dlese_CHECKBOX_IMG[ list.parent.checkedState[ list.parentIndex ] ].src;		

		dlese_checkParentState( list.parent, dontRender );

	}

}

function dlese_setParentStates( list ) {

	dlese_checkParentState( list, 1 );

	for ( var i = 0; i < list.label.length; i++ ) {

		if ( list.sublist[ i ] )

			dlese_setParentStates( list.sublist[ i ] );

	}

}

function dlese_setVocabQueryString() {

	dlese_vocabQueryString = '';

	for ( var i = 0; i < dlese_allLists.length; i++ ) {

		var list = eval( dlese_allLists[ i ] );

		dlese_queryString( list );

	}

	dlese_renderVocabSelectedState();

}

function dlese_queryString( list ) {

	for ( var i = 0; i < list.label.length; i++ ) {

		if ( list.sublist[ i ] )

			dlese_queryString( list.sublist[ i ] );

		else if ( list.checkedState[ i ] == 1 ) {

			dlese_vocabQueryString += "&" + list.inputField[ i ] + "=" + list.inputValue[ i ];

		}

	}	

}

function dlese_setToVisible( list, notTopList ) {

	if ( list.groupType < 1 ) {

		var obj = getObject( list.div );

		obj.style.display = "none";

		obj.style.visibility = 'visible';

	}

	for ( var i = 0; i < list.label.length; i++ )

		if ( list.sublist[ i ] )

			dlese_setToVisible( list.sublist[ i ], true );

}

function dlese_hideList( list ) {

	var imgObj;

	if ( list.closeCall )

		eval( list.closeCall );

	if ( list.groupType < 1 )

		hideObj( list.div );

	if ( list.parent && ( list.groupType < 1 ) ) {

		imgObj = getObject( list.parent.name + list.parentIndex + "arrow" );

		if ( imgObj ) {

			if ( list.groupType == 0 )

				imgObj.src = dlese_ARROW_CLOSED.src;

			else

				imgObj.src = dlese_ARROW_CLOSED_DROPDOWN.src;

		}

	}

	for ( var i = 0; i < list.label.length; i++ ) {

		if ( list.sublist[ i ] ) {

			if ( list.sublist[ i ].groupType < 1 ) {

				imgObj = getObject( list.name + i + "arrow" );

				if ( imgObj ) {

					if ( list.sublist[ i ].groupType == 0 )

						imgObj.src = dlese_ARROW_CLOSED.src;

					else

						imgObj.src = dlese_ARROW_CLOSED_DROPDOWN.src;

				}

			}

			dlese_hideList( list.sublist[ i ] );

		}

	}

	if ( list.groupType != 1 ) {

		var selectClearLink = document.getElementById( "vocabSelectClear" + list.name );

		if ( selectClearLink && selectClearLink.style )

			selectClearLink.style.visibility = 'hidden';

	}	

}

function dlese_hideAllVocab( except ) {

	for ( var i = 0; i < dlese_allLists.length; i++ ) {

		if ( except != dlese_allLists[ i ] ) {

			var list = eval( dlese_allLists[ i ] );

			var button = eval( "document.vButton" + dlese_allListsSimpleName[ i ] );

			if ( button.isRendered ) {

				dlese_hideList( list );

			}

		}

	}

}

function dlese_selectClearAll( list ) {

	dlese_vocabsShowing = true;

	if ( dlese_allSelected( list ) )

		dlese_checkAll( list, 0 );

	else

		dlese_checkAll( list, 1 );

}

function dlese_allSelected( list ) {

	var ret = true;

	for ( var i = 0; i < list.label.length; i++ ) {

		if ( list.checkedState[ i ] == 0 )

			return false;

		if ( list.sublist[ i ] )

			ret = dlese_allSelected( list.sublist[ i ] );

	}

	return ret;

}

function dlese_checkAll( list, state ) {

	window.status = '';

	if ( state < 0 ) { // state of -1 means check parent to determine new state

		if ( list.parent.checkedState[ list.parentIndex ] == 1 ) {

			state = 0;

		}

		else {

			state = 1;

		}

	}

	for ( var i = 0; i < list.label.length; i++ ) {

		list.checkedState[ i ] = state;

		var cb = getObject( list.name + i + "check" );

		if ( cb )

			eval( "cb.src = dlese_CHECKBOX_IMG[ " + state + " ].src" );

		if ( list.sublist[ i ] )

			dlese_checkAll( list.sublist[ i ], state );

	}

	dlese_checkParentState( list );

	dlese_setVocabQueryString();

}



// "Your selections" (State feedback of what user currently has selected):

function dlese_renderVocabSelectedState() {

	var say = "";

	for ( var i = 0; i < dlese_allLists.length; i++ ) {

		var list = eval( dlese_allLists[ i ] );

		var gvss = dlese_getVocabSelectedState( list );			

		if ( gvss ) {

			say += "<span>" + list.groupLabelAbbrev + ":</span> " + gvss + " + ";

		}

	}

	if ( say ) {

		var ind = say.lastIndexOf( ' + ' );

		if ( ind > -1 )

			say = say.substring( 0, ind );

	}	

	if ( document.images.clearAll ) {

		if ( !say ) {

			document.images.clearAll.src = dlese_CLEAR_ALL_BUTTON_GREYED_OUT;

		}

		else {

			document.images.clearAll.src = dlese_CLEAR_ALL_BUTTON;

		}

	}

	var obj = document.getElementById( "dlese_selectedCriteria" );

	if ( obj ) {

		obj.style.color = '#000042';		

		obj.innerHTML = say; 

	}

}

function dlese_getVocabSelectedState( list ) {

	var ret = '';

	var allSelected = true;

	for ( var i = 0; i < list.label.length; i++ ) {

		if ( list.checkedState[ i ] == 1 ) {

			if ( !list.sublist[ i ] )

				ret += list.labelAbbrev[ i ] + ", ";

			else

				ret += list.labelAbbrev[ i ] + " (all), ";

		}

		else if ( list.checkedState[ i ] == 2 ) {

			ret += list.labelAbbrev[ i ] + " (some), ";

			allSelected = false;

		}

		else

			allSelected = false;

	}

	if ( allSelected )

		return 'All';

	if ( ret ) {

		var ind = ret.lastIndexOf( ', ' );

		if ( ind > -1 )

			ret = ret.substring( 0, ind );

	}

	return ret;

}

function dlese_clearAllVocabs() {

	document.images.clearAll.src = dlese_CLEAR_ALL_BUTTON_GREYED_OUT;

	var obj = document.getElementById( "dlese_selectedCriteria" );

	if ( obj ) 

		obj.innerHTML = ''; 

	setTimeout( "dlese_doClearAll()", 10 );

}

function dlese_doClearAll() {

	for ( var i = 0; i < dlese_allLists.length; i++ ) {

		var button = eval( "document.vButton" + dlese_allListsSimpleName[ i ] );

		dlese_clearList( eval( dlese_allLists[ i ] ), button.isRendered );

	}

	dlese_setVocabQueryString();

}

function dlese_clearList( list, isRendered ) {

	for ( var i = 0; i < list.label.length; i++ ) {

		list.checkedState[ i ] = 0;

		if ( isRendered ) {

			var imgObj = document.getElementById( list.name + i + "check" );

			if ( imgObj )

				imgObj.src = dlese_CHECKBOX_IMG[ 0 ].src;

		}

		if ( list.sublist[ i ] )

			dlese_clearList( list.sublist[ i ], isRendered );

	}

}

function dlese_showSelectClearLinksIfGroupTwo() {

	for ( var i = 0; i < dlese_allLists.length; i++ ) {

		var list = eval( dlese_allLists[ i ] );

		dlese_showSelectClearForList( list );

	}

}

function dlese_showSelectClearForList( list ) {

	if ( list.groupType == 1 ) {

		var selectClearLink = document.getElementById( "vocabSelectClear" + list.name );

		if ( selectClearLink && selectClearLink.style ) {

			selectClearLink.style.visibility = 'visible';

		}

	}

	for ( var i = 0; i < list.label.length; i++ ) {

		if ( list.sublist[ i ] )

			dlese_showSelectClearForList( list.sublist[ i ] )

	}

}

function dlese_renderVocabButtonsDiv() {

	document.writeln( "<div id='dlese_vocabButtons' class='dlese_vocabButtonsDiv' onClick='dlese_vocabsShowing = true;'>&nbsp;</div>" );

	document.writeln( "<div id='dlese_vocabDescriptions' class='dlese_vocabDescription'>&nbsp;</div>" );

}





