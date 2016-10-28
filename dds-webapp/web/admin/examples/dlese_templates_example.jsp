<%@ taglib prefix="c" uri="/WEB-INF/tlds/c.tld" %>
<html>
<head>
	<title>Example use of DLESE templates and styles</title>
	<c:set var="thisServer">
		http://<%= request.getServerName() + ":" + request.getServerPort() %>
	</c:set>
	<c:import url="${thisServer}/dlese_shared/templates/header.html" />
	<script type="text/javascript"><!--
		dlese_docIsSideNav = false;
	//-->
	</script>	
</head>
<body onLoad="dlese_pageOnLoad();">
	<div class="dlese_sectionTitle" id="dlese_sectionTitle">
		Developer<br />Examples
	</div>	
	<c:import url="${thisServer}/dlese_shared/templates/top_content.html" />
	
	<h1>Example use of DLESE templates and styles</h1>
	<p>This is an example JSP that dynamically includes the DLESE site banner and CSS templates.</p>
	
	<c:import url="${thisServer}/dlese_shared/templates/bottom_content.html" />
</body>
</html>


