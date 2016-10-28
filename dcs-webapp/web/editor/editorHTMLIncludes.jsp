<link rel='stylesheet' type='text/css' href='${contextPath}/styles.css'>
<link rel="stylesheet" href="${contextPath}/lib/autoform-styles.css" type="text/css">

<link href="//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" rel="stylesheet">

<script type="text/javascript" src="${contextPath}/lib/javascript/prototype.js"></script>
<script type="text/javascript" src="${contextPath}/lib/javascript/ncsGlobals.js"></script>
<script type="text/javascript" src="${contextPath}/lib/javascript/scriptaculous.js?load=effects"></script>

<%-- pwcindows includes --%>
<link rel="stylesheet" href="${contextPath}/lib/javascript/pwc/themes/default.css" type="text/css">
<link rel="stylesheet" href="${contextPath}/lib/javascript/pwc/themes/dcs_alphacube.css" type="text/css">
<script type="text/javascript" src="${contextPath}/lib/javascript/pwc/window.js"></script>
<script type="text/javascript" src="${contextPath}/lib/javascript/pwc/window_ext.js"></script>
<script type="text/javascript" src="${contextPath}/lib/edit-status.js"></script>

<script type="text/javascript">
<%@ include file="/lib/pageMenuData.jsp" %>
</script>
<script type="text/javascript" src="${contextPath}/lib/pageMenu.js"></script>
<script type="text/javascript" src="${contextPath}/lib/best-practices-link-support.js"></script>
<script type="text/javascript" src="${contextPath}/lib/vocabTree.js"></script>
<script type="text/javascript" src="${contextPath}/lib/async-vocab-layout/async-vocab-layout-support.js"></script>
<%-- <script type="text/javascript" src="${contextPath}/lib/metadata-editor-debugging.js"></script> --%>
<script type="text/javascript" src="${contextPath}/lib/vocab-type-ahead.js"></script>
<script type="text/javascript" src="${contextPath}/lib/metadata-editor-support.js"></script>
<c:if test="${sef.xmlFormat == 'osm'}">
	<script type="text/javascript" src="${contextPath}/lib/osm-editor-support.js"></script>
</c:if>
<c:if test="${sef.xmlFormat == 'lar'}">
	<script type="text/javascript" src="${contextPath}/lib/lar-editor-support.js"></script>
</c:if>
<%-- <script type="text/javascript" src="${contextPath}/lib/unique-values-support.js"></script> --%>
<%@ include file="/lib/combo-input-support.jspf" %>


