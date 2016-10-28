<%@ taglib prefix="c" uri="/WEB-INF/tlds/c.tld" %>
<html>
<head>
	<title>DLESE Developer Examples</title>
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
	
	<h1 class="hasSubhead">DLESE Developer Examples</h1>
	<h2>A starting point for building new DLESE Web pages and systems</h2>
	
	<p>This site serves as a reference base for example DLESE code and templates.  As we 
	develop new tools and services, we can demonstrate their use here in a manner
	that will make it easy for developers to understand and re-use.</p>
	<ul>
		<li>Templates:</li>
		<ul>
			<li><a href="dlese_templates_example.jsp">Plain page</a> -- Example of a basic DLESE
			page without a side menu.</li>
			<li><a href="dlese_templates_example_side_menus.jsp">Static side menus</a> -- Example of a
			DLESE page with a static side menu (used by our Wiki sites)</li>
			<li><a href="dlese_templates_example_side_menus_dynamic.jsp">Dynamic side menus</a> -- 
			Example of a DLESE page with a dynamic (collapsable hierarchy) side menu (used by
			sites like Metadata)</li>
		</ul>
		<li>Services:</li>
		<ul>
			<li><a href="/fcosee/">FCOSEE</a> -- Demonstration of the DDS search service as used by
			partner site FCOSEE</li>
			<li><a href="ss_generic.jsp">Generic</a> -- Demonstration of a "generic" use of the search 
			service with minimal style formatting (<em>Coming soon</em>)</li>
			<li><a href="vocabs.jsp">Controlled vocabularies</a> -- Demonstrations of using the
			controlled voabularies UI services (<em>Coming soon</em>)</li>
		</ul>
		<li>Controlled vocabularies:</li>
		<ul>
			<li><a href="/dds/admin/examples/vocabs.do?field=resourceType">Display vocabulary
			properties</a> -- Example translation and iteration over the "Resource Type" vocabularies
			in a table that indicates all of the vocabulary item properties</li>
			<li><a href="/dds/admin/examples/vocabs.do?render=fcosee">Render example 1</a> -- Example 
			rendering of DLESE ADN vocabulary inputs in the style used by the FCOSEE site</li>			
		</ul>
	</ul>
	
	<c:import url="${thisServer}/dlese_shared/templates/bottom_content.html" />
</body>
</html>


