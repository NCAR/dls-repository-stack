<%@ include file="/lib/includes.jspf" %>



<html>

	<head>

		<script type="text/javascript">

			<!--

			var submitAction="${sef.submitAction}";

			// -->

		</script>

		<script language="JavaScript" src="../lib/metadata-editor-support.js"></script>

		<link rel="stylesheet" href="../styles.css" type="text/css">

		<link rel="stylesheet" href="../lib/autoform-styles.css" type="text/css">

		<title>${sef.frameworkName} Metadata Editor</title>

	</head>

<body bgcolor=white onload="init('${sef.hash}', '${sef.guardedExitPath}')">

<noscript>You must enable JavaScript to use this application!</noscript>

<a name="top"/>

<st:pageHeader />

<%@ include file="/lib/topNavMenu.jspf" %>





<st:editorPageMessages/>



<html:form action="${sef.formActionPath}" method="Post">

	<input type="hidden" name="currentPage" value="${sef.currentPage}"/>

	<input type="hidden" name="nextPage" value=""/>

	<input type="hidden" name="command" value="validate"/>

	<input type="hidden" name="pathArg" value=""/>

	<input type="hidden" name="recId" value="${sef.recId}"/>

	<input type="hidden" name="confirmedExit" value=""/>

	<input type="hidden" name="displayAction" value=""/>

	

<jsp:include page="${sef.currentPageEncoded}.jsp"/>



</html:form>



<%@ include file="/lib/navMenu.jspf" %>



</body>

</html>

