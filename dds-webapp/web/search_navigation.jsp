<div class="dlese_sectionTitle" id="dlese_sectionTitle">
	Find a<br />Resource
</div>	
<%@ include file="include/search_banner.jsp" %>

<div class="yourSelectionsCriteria" id="selectedCriteria"><noscript>
	<span class="yourSelectionsNoscriptLabel">Your selections:</span>
	${ ddsQueryForm.vocabInputState.stateFeedback }
</noscript></div>	

	
<script type="text/javascript"><!--	
	if ( !dlese_noVocabs )
		document.writeln( '<a href="javascript:ClearAllVocabs()" class="cursorPointer">'
			+ '<img alt="Clear all selections" src="<%= request.getContextPath() %>/images/vocab/clear_all.gif" '
			+ 'id="clearAll" border="0" class="clearAllButton"></a>' );
	if ( dlese_isNetscape )
		NS_VOCABS_LEFT_OFFSET = 25;
	loadedQuery = '<bean:write name="ddsQueryForm" property="vocabInputState.state" filter="false" />';
	renderVocabButtonsDiv();
	var L_OFFSET = 1;
	document.vButtongr = new VocabButton( 'gr', 'images/vocab/grade_level_closed.gif', 
		'images/vocab/grade_level_open.gif', 0 + L_OFFSET, 118 );
	document.vButtonre = new VocabButton( 're', 'images/vocab/resource_type_closed.gif', 
		'images/vocab/resource_type_open.gif', 97 + L_OFFSET, 118 );
	document.vButtonky = new VocabButton( 'ky', 'images/vocab/collections_closed.gif', 
		'images/vocab/collections_open.gif', 208 + L_OFFSET, 118 );
	document.vButtoncs = new VocabButton( 'cs', 'images/vocab/standards_closed.gif', 
		'images/vocab/standards_open.gif', 302 + L_OFFSET, 118 );
	DefineVocabButton( 'su' );
		
	RenderVocabButton( 'gr', 'images/vocab/grade_level_closed.gif', 
		'images/vocab/grade_level_open.gif', 'Select Grade Level(s)', 125 + L_OFFSET, 91, 
			125 + L_OFFSET, 91 ); // location of flyout
	RenderVocabButton( 're', 'images/vocab/resource_type_closed.gif', 
		'images/vocab/resource_type_open.gif', 'Select Resource Type(s)', 222 + L_OFFSET, 91, 
			222 + L_OFFSET, 91 );	
	RenderVocabButton( 'ky', 'images/vocab/collections_closed.gif', 
		'images/vocab/collections_open.gif', 'Select Collection(s)', 333 + L_OFFSET, 91, 
			333 + L_OFFSET, 91 );
	RenderVocabButton( 'cs', 'images/vocab/standards_closed.gif', 
		'images/vocab/standards_open.gif', 'Select Standard(s)', 427 + L_OFFSET, 91, 
			427 + L_OFFSET, 91 );	
		
	if ( !dlese_noscript ) {
		var clearAll = document.getElementById( "clearAll" );
		clearAll.style.visibility = 'visible';
	}
	RenderVocabSelectedState();		
	dlese_renderSiteNavButtons();
	setTimeout( "dlese_changeDevDomains()", 500 ); 
// -->
</script> 

<vocabs:treeMenu id="gr" 
	metaFormat="adn" audience="${ initParam.metadataVocabAudience }" language="en-us"
	x="115" y="10" nsWidth="190" />
	
<vocabs:treeMenu id="re" 
	metaFormat="adn" audience="${ initParam.metadataVocabAudience }" language="en-us"
	x="212" y="109" nsWidth="480" />
	
<vocabs:treeMenu id="cs" 
	metaFormat="adn" audience="${ initParam.metadataVocabAudience }" language="en-us"
	x="324" y="109" nsWidth="370" />
	
<vocabs:treeMenu id="ky" 
	metaFormat="dlese_collect" audience="${ initParam.metadataVocabAudience }" language="en-us"
	x="415" y="109" nsWidth="270" />

<%-- Hidden carrythrough of subject which isn't a flyout selection: --%>
<vocabs:hidden id="su" 
	metaFormat="adn" audience="${ initParam.metadataVocabAudience }" language="en-us" />

<%-- vocabDescriptions is for pop-up text associated with a vocab (i.e. collections described) --%> 
<div id="vocabDescriptions" class="vocabDescription">
</div>

