		<script type="text/javascript"><!--
			document.dlese_surveyIsPresent = ${ initParam.showSurvey };
		//-->
		</script>
		<%-- DLESE standard (shared) styles and script --%>
		<c:import url="${domain}/dlese_shared/templates/header_refs.html"/>
		<link rel="stylesheet" type="text/css" href="/dlese_shared/dlese_styles_discovery.css"/>
		<link rel="stylesheet" type="text/css" href="${ jsVocabsUrl }/dlese_styles_vocabs.css"/>
		<script type="text/javascript"><!--
			dlese_DISCOVERY_ROOT = '<%= request.getContextPath() %>/';
			dlese_pageHasVocabs = true;
			dlese_pageShowsVocabSelected = true;
		//-->
		</script> 		
		<%-- DDS-specific styles and script --%>
		<link rel='stylesheet' type='text/css' href='<%= request.getContextPath() %>/discovery_styles_screen.css'>
		<link rel='stylesheet' type='text/css' href='<%= request.getContextPath() %>/discovery_styles_print.css' media='print'>
		<script type="text/javascript" src="${ jsVocabsUrl }/dlese_script_vocabs.js"></script>
