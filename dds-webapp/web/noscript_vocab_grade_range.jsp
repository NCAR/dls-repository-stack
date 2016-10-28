<%@ include file="/include/setup.jsp" %>
<html>
	<head>
		<title>DLESE Select Grade Range</title>
		<%@ include file="/include/header.jsp" %>
	</head>
	<body>	
		<%@ include file="/include/search_banner.jsp"%>
		<div class="breadcrumbBanner">
			<span class="breadcrumbs">Educational resources &gt; Select Grade Range</span>
		</div>
		<h1>Select Grade Range</h1>
		
			<form action="/dds/query.do" method="GET" style="width: 90%; margin-left: 7px;">		
					
				<vocabs:checkboxes field="gr" 
					metaFormat="adn" audience="${ initParam.metadataVocabAudience }" language="${ initParam.metadataVocabLanguage }"
					wrap="10" tdWidth="220" /><br />		
								
				<input type="text" name="q" size="40" value="${ ddsQueryForm.q }" />
				<input type="hidden" name="s" value="0">
				<input type="image" alt="Start the search" 
					src="/dlese_shared/images/navigation/search_on_gold.gif" border="0">	
					
				<%-- NOSCRIPT vocab state carrythrough: --%>
				<vocabs:hidden metaFormat="dlese_collect" audience="${ initParam.metadataVocabAudience }" language="${ initParam.metadataVocabLanguage }" field="ky" />
				<vocabs:hidden metaFormat="adn" audience="${ initParam.metadataVocabAudience }" language="${ initParam.metadataVocabLanguage }" field="cs" />
				<vocabs:hidden metaFormat="adn" audience="${ initParam.metadataVocabAudience }" language="${ initParam.metadataVocabLanguage }" field="su" />	
				<vocabs:hidden metaFormat="adn" audience="${ initParam.metadataVocabAudience }" language="${ initParam.metadataVocabLanguage }" field="re" />	
				
			</form>	
					
		<br clear='all'>
		<p class="dlese_topLink"><a href="#top"><img src='/dlese_shared/images/arrowup.gif' border='0' 
		alt='Return to the top of the page'></a></p>


		<%-- DLESE template bottom (footer links and logo) --%>
		<c:import url="${domain}/dlese_shared/templates/content_bottom.html"/>
	</body>
</html>