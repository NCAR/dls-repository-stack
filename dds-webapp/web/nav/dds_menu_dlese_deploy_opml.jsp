<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="isAdminPage" value="${fn:contains(param.requestURI,'/admin/')}"/>
<!-- 
	This version of the menus is for display when DDS is deployed with the dlese.org library site
	Original OPML generated by DLESE Headliner 2.2b, modified later and placed in JSP so variables can be used.
	
	Note, pass in to the viewing page param treeCache=off/on to turn on/off caching of the menu view
-->
<opml version="2.0" xmlns:trees="http://www.dlese.org/Metadata/ui/trees"> 
	<head>
		<title>DDS Web Services</title>
		<!-- The context attribute below is written by the DDSServlet class with the value of the installed context path, e.g. "/dds/" -->
		<trees:menu images="${pageContext.request.contextPath}/images/opml_menu/triangles_bullets/" context="${pageContext.request.contextPath}/"/>
		<dateModified>Tue, 23 May 2006 13:26:32 MDT</dateModified>
	</head>
	<body>
		<outline text="Overview" type="link" description="Overview of DLESE Web services and APIs" url="services/index.jsp"/>
		
		<c:if test="${isAdminPage}">
			<%-- Note that this admin area is only used when *not* deployed at DLESE (through SSH) --%>
			<outline text="&lt;hr&gt;" type="separator"/>
			<outline text="Manage the Repository" type="link" description="Manage and view collections and items in the repository" url="admin/index.jsp">
				<outline text="Collection Manager" type="link" description="View and manage collections in the repository" url="admin/admin.do"/>
				<outline text="Maintenance" type="link" url="admin/maintenance.do" description="Maintain the Repository"/>
				<outline text="Advanced Search" type="link" description="Search, view and manage items in the repository" url="admin/query.do" />
				<c:if test="${fn:endsWith(param.requestURI,'admin/editing.do')}">
					<outline text="Edit Record" type="link" url="admin/editing.do"/>
				</c:if>					
				<c:if test="${fn:endsWith(param.requestURI,'admin/display.do')}">
					<outline text="Full Record View" type="link" url="admin/display.do"/>
				</c:if>
				<outline text="Indexed Fields &amp; Terms" description="View the search fields and terms in the repository" type="link" url="admin/reporting/index_page.jsp"/>
				<c:if test="${fn:endsWith(param.requestURI,'admin/reporting/report.do')}">
					<outline text="Fields/Terms Viewer" type="link" url="admin/reporting/report.do"/>
				</c:if>
				<outline text="Reports" description="View reports for the repository" type="link" url="admin/reporting/reports.jsp"/>
				<c:if test="${fn:endsWith(param.requestURI,'admin/report.do')}">
					<outline text="Report Viewer" type="link" url="admin/report.do"/>
				</c:if>					
				<outline text="Metadata-UI Manager" type="link" url="admin/vocab.do"/>
				
			</outline>		
			<outline text="&lt;hr&gt;" type="separator"/>
		</c:if>
		
		<outline text="Search API" type="link" description="Information about the Search API" url="services/ddsws1-1/index.jsp">
			<outline text="Documentation" type="link" url="services/ddsws1-1/service_specification.jsp"/>
			<outline text="Search Client" type="link" url="services/examples/ddsws1-1/index.jsp"/>
			<outline text="Explorer" type="link" url="services/ddsws1-1/service_explorer.jsp"/>
			<!-- <outline text="Templates and Examples" type="link" url="services/examples/ddsws/" /> -->
			<%-- <outline text="Previous Versions" url="services/ddsws/archive.jsp" type="text">
				<outline text="DDSWS v1.0" url="services/ddsws1-0/index.jsp" type="link">
					<outline text="Documentation" type="link" url="services/ddsws1-0/service_specification.jsp"/>
					<outline text="Explorer" type="link" url="services/ddsws1-0/service_explorer.jsp"/>			
				</outline>
				<outline text="ODL Search" url="services/oai2-0/index.jsp" type="link">
					<outline text="Documentation" type="link" url="services/oai2-0/odl_service_documentation.jsp"/>
					<outline text="Explorer" type="link" url="services/oai2-0/service_explorer.jsp"/>			
				</outline>					
			</outline> --%>
		</outline>
		<outline text="Update API" type="link" description="Create, update and delete collections and items" url="services/ddsupdatews1-1/ddsupdatews_api_documentation.jsp"/>
		<outline text="JavaScript Search Service" type="link" description="Information about the JavaScript Search Service" url="services/jshtml1-1/index.jsp">
			<outline text="Documentation" url="services/jshtml1-1/javascript_service_documentation.jsp" type="link">
				<outline text="Suggested CSS Styles" url="services/jshtml1-1/view_suggested_styles.jsp" type="link"/>
				<outline text="Required CSS Styles" url="services/jshtml1-1/view_required_styles.jsp" type="link"/>
			</outline>
			<outline text="SmartLink Builder Tool" url="services/jshtml1-1/smart_link_query_builder.jsp" type="link" />
			<outline text="Templates and Examples" url="services/examples/jshtml1-1/index.html" type="link" />
			<outline text="Tutorial" url="services/jshtml1-1/javascript_service_tutorial.pdf" type="link" />
		</outline>
		<outline text="&lt;hr&gt;" type="separator"/>
		<outline text="OAI Data Provider" type="group" description="Information about the OAI Data Provider" url="services/oaiDataProvider/index.jsp">
			<outline text="Explorer" url="services/oaiDataProvider/oai_explorer.jsp" type="link"/>
		</outline>
		<outline text="&lt;hr&gt;" type="separator"/>
		<outline text="DDS Software" url="dds_overview.jsp" type="link">
			<outline text="Download and Configure" type="link" description="Information about downloading, installing, and configuring DDS" url="docs/index.jsp">
				<outline text="Download DDS" url="services/dds_software.jsp" type="link"/>
				<outline text="Install DDS" description="Installation Instructions for DDS" url="docs/dds_installation_instructions.jsp" type="link" />
				<outline text="Configure Data Sources" description="Configure data sources for DDS" url="docs/configure_dds_data_sources.jsp" type="link" />
				<outline text="Configure Search" description="Configure the fields, facets, and relationships that are used for search" url="docs/configure_dds_search_fields.jsp" type="link" />
			</outline>
			<outline text="About DDS" type="link" description="Information about DDS" url="docs/about_dds.jsp"/>		
		</outline>
		<outline text="NCS Software" url="services/dcs_software.jsp" type="link"/>
		<outline text="jOAI Software" url="services/joai_software.jsp" type="link"/>
		<outline text="Demos/Examples" url="services/examples/index.jsp" type="link"/>
	</body>
</opml>