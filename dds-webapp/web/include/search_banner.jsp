<div class="dlese_sectionTitle" id="dlese_sectionTitle">
	Find a<br />Resource
</div>	
<noscript>
	<a href="<%= request.getContextPath() %>/noscript.do?showVocab=gr"><img alt="Select grade level(s)"
		border="0" src="/dlese_shared/images/vocab/grade_level_noscript.gif" class="dlese_vocabGrNoscript"></a>
	<a href="<%= request.getContextPath() %>/noscript.do?showVocab=re"><img alt="Select resource type(s)"
		border="0" src="/dlese_shared/images/vocab/resource_type_noscript.gif" class="dlese_vocabReNoscript"></a>
	<a href="<%= request.getContextPath() %>/noscript.do?showVocab=ky"><img alt="Select collection(s)" 
		border="0" src="/dlese_shared/images/vocab/collections_noscript.gif" class="dlese_vocabKyNoscript"></a>
	<a href="<%= request.getContextPath() %>/noscript.do?showVocab=cs"><img alt="Select standard(s)" 
		border="0" src="/dlese_shared/images/vocab/standards_noscript.gif" class="dlese_vocabCsNoscript"></a> 
</noscript>
<c:set var="addFormInput">
	<%-- NOSCRIPT vocab state carrythrough: --%>
	<vocabs:hidden metaFormat="dlese_collect" audience="${ initParam.metadataVocabAudience }" language="${ initParam.metadataVocabLanguage }" field="ky" />
	<vocabs:hidden metaFormat="adn" audience="${ initParam.metadataVocabAudience }" language="${ initParam.metadataVocabLanguage }" field="cs" />
	<vocabs:hidden metaFormat="adn" audience="${ initParam.metadataVocabAudience }" language="${ initParam.metadataVocabLanguage }" field="su" />	
	<vocabs:hidden metaFormat="adn" audience="${ initParam.metadataVocabAudience }" language="${ initParam.metadataVocabLanguage }" field="re" />	
	<vocabs:hidden metaFormat="adn" audience="${ initParam.metadataVocabAudience }" language="${ initParam.metadataVocabLanguage }" field="gr" />										
</c:set>
<c:set var="templatesXml">
	<dummy></dummy>
</c:set>
<c:set var="templatesXsl">
	<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"> 
		<xsl:include href="${ domain }/dlese_shared/dlese_site_templates.xsl" />
		<xsl:template match="/">
			<xsl:call-template name="dlese_page_top_content">
				<xsl:with-param name="searchOverSelectedIndex">1</xsl:with-param>
				<xsl:with-param name="searchKeywords">${ ddsQueryForm.q }</xsl:with-param>
				<c:if test="${ not empty addFormInput }">
					<xsl:with-param name="addFormInput">${ addFormInput }</xsl:with-param>
				</c:if>				
			</xsl:call-template>
		</xsl:template>
	</xsl:stylesheet>
</c:set>		
<x:transform xml="${ templatesXml }" xslt="${ templatesXsl }" />
<div class="dlese_yourSelectionsCriteria" id="dlese_selectedCriteria"><noscript>
	<span class="dlese_yourSelectionsNoscriptLabel">Your selections:</span>
	${ ddsQueryForm.vocabInputState.stateFeedback }
</noscript></div>	

<script type="text/javascript"><!--	
	dlese_loadedQuery = '${ ddsQueryForm.vocabInputState.state }';
	document.writeln( '<a href="javascript:dlese_clearAllVocabs()" class="cursorPointer">'
		+ '<img alt="Clear all selections" src="<%= request.getContextPath() %>/images/vocab/clear_all.gif" '
		+ 'id="clearAll" border="0" class="dlese_clearAllButton"></a>' );
	dlese_renderVocabButtonsDiv();
// -->
</script> 

<script type="text/javascript" 
	src="${ jsVocabsUrl }/vocabs_flyout.jsp?metaFormat=adn&audience=${ initParam.metadataVocabAudience }&language=${ initParam.metadataVocabLanguage }&field=gr"></script>
<script type="text/javascript" 
	src="${ jsVocabsUrl }/vocabs_flyout.jsp?metaFormat=adn&audience=${ initParam.metadataVocabAudience }&language=${ initParam.metadataVocabLanguage }&field=re"></script>
<script type="text/javascript" 
	src="${ jsVocabsUrl }/vocabs_flyout.jsp?metaFormat=adn&audience=${ initParam.metadataVocabAudience }&language=${ initParam.metadataVocabLanguage }&field=cs"></script>
<script type="text/javascript" 
	src="${ jsVocabsUrl }/vocabs_flyout.jsp?metaFormat=dlese_collect&audience=${ initParam.metadataVocabAudience }&language=${ initParam.metadataVocabLanguage }&field=ky"></script>
<script type="text/javascript" 
	src="${ jsVocabsUrl }/vocabs_flyout.jsp?metaFormat=adn&audience=${ initParam.metadataVocabAudience }&language=${ initParam.metadataVocabLanguage }&field=su"></script>

<script type="text/javascript"><!--	
	dlese_defineVocabButton( 'su' );
	dlese_renderVocabButton( 'gr', 'images/vocab/grade_level_closed.gif', 
		'images/vocab/grade_level_open.gif', 'Select Grade Level(s)', 
			126, 91,	// location of button 
			126, 93 );	// location of flyout
	dlese_renderVocabButton( 're', 'images/vocab/resource_type_closed.gif', 
		'images/vocab/resource_type_open.gif', 'Select Resource Type(s)', 
			223, 91, 
			223, 93 );	
	dlese_renderVocabButton( 'ky', 'images/vocab/collections_closed.gif', 
		'images/vocab/collections_open.gif', 'Select Collection(s)', 
			334, 91, 
			334, 93 );
	dlese_renderVocabButton( 'cs', 'images/vocab/standards_closed.gif', 
		'images/vocab/standards_open.gif', 'Select Standard(s)', 
			428, 91, 
			428, 93 );	
	var clearAll = document.getElementById( "clearAll" );
	clearAll.style.visibility = 'visible';
	dlese_renderSelectedVocabsFeedback();
// -->
</script> 


