<%@ include file="TagLibIncludes.jsp" %>
<c:url value="/async_content/set_session_state.jsp" var="setStateUrl"/>
<script language="JavaScript">
	<%-- Set the url to the state saver, so it has session info if cookies are off: --%>
	var setStateUrl = '${setStateUrl}';

	<%-- Set the tabs that should be selected onLoad: --%>
	var collTab = '${empty param.collTab ? selectedCollTab : param.collTab}';
	var oaiTab = '${empty param.oaiTab ? selectedOaiTab : param.oaiTab}';
	<c:if test="${not empty param.uuid}">
		collTab = 'history';
	</c:if>
	var harvestTab = '${empty param.harvestTab ? selectedHarvestTab : param.harvestTab}';

	var contextPath = '${pageContext.request.contextPath}';
</script>

<script type='text/javascript' src='<c:url value="/prototype-1.6.0.2.js"/>'></script>
<script type='text/javascript' src='<c:url value="/scriptaculous-js-1.8.1/effects.js"/>'></script>
<%-- <script type='text/javascript' src='<c:url value="/scriptaculous-js-1.8.1/scriptaculous.js?load=effects"/>'></script> --%>
<%-- <script type='text/javascript' src='<c:url value="/browser.js"/>'></script>
<script type='text/javascript' src='<c:url value="/menu.js"/>'></script> --%>
<script type='text/javascript' src='<c:url value="/webapp_script.js"/>'></script>
<%-- <script type='text/javascript' src='<c:url value="/log4javascript.js"/>'></script> --%>

<link rel='stylesheet' type='text/css' href='<c:url value="/webapp_styles.css"/>'>
<link rel="SHORTCUT ICON" href="/favicon.ico">

