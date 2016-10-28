<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>

<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>

<html>
<head>
<title>Demos and Examples using DDS Services and APIs</title>
	
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<meta name="description" content="DDS custom search page preview site">
<meta name="creator" content="John Weatherley">

	
	<%-- DLESE template header (CSS styles and JavaScript references) --%>
	<%@ include file="/nav/head.jsp" %> 

	<LINK REL="stylesheet" TYPE="text/css" HREF="${contextUrl}/services/jshtml1-1/documentation_styles.css">
 	 
	<link rel="stylesheet" type="text/css" media="print" href="/dlese_shared/dlese_styles_tree_menus_print.css"/> 
</head>
<body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066">
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Search<br/>Service API
</div>
	

<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>

             <h1>Demos and Examples</h1>
             <p>
               This page links to examples that demonstrate how the DDS <a href="${pageContext.request.contextPath}/services/index.jsp">Services and APIs</a> have been used to implement a number of Web sites and applications. </p>
               
               <h3>Thematic search pages:</h3>
               <h4>Search pages that are in operation:</h4>
               <ul>
		<li><a href="http://www.cosee.net/" target="_blank">COSEE</a> - The national Center for Ocean Sciences Education Excellence Web site
		provides a search over the COSEE collections in DLESE, and also contains resources from each of the COSEE centers. See also
		the original <a href="http://search.dlese.org/cosee/index.jsp">demo</a> prepared for COSEE.		</li>
		<li><a href="http://mynasadata.larc.nasa.gov/DLESE_search.html" target="_blank">MY NASA DATA</a> - A lesson plan focus page on the MY NASA DATA site
			(<a href="http://mynasadata.larc.nasa.gov/lessons.html" target="_blank">linked from NASA</a>).		</li>
		<li><a href="http://asd-www.larc.nasa.gov/SCOOL/DLESE_search.html" target="_blank">NASA S'COOL</a> - A lesson plan focus page on the NASA S'COOL site
			(<a href="http://asd-www.larc.nasa.gov/SCOOL/lesson_plans/index.cfm" target="_blank">linked from  S'COOL</a>).	
		</li>
		<li><a href="http://www.globe.gov/tctg/templates/index.jsp" target="_blank">GLOBE</a> - The GLOBE search page for DLESE resources 
			(<a href="http://www.globe.gov/tctg/tgchapter.jsp?sectionId=1" target="_blank">linked from GLOBE</a>).		</li>
		<li>	
			<a href="http://search.dlese.org/fcosee/">Florida COSEE (demo)</a> -	Demo prepared for Florida COSEE	</li>
		<li><a href="http://www.dlisr.org/" target="_blank">DLISR</a> - The Digital Library of Indigenous Science Resources. 
			See also the <a href="http://search.dlese.org/dlisr/search.jsp">local DLISR page</a> proxy.		</li>
		<li><a href="http://njesta.org/pages/search.html" target="_blank">NJESTA</a> - The New Jersey Earth Science Teachers Associataion web site.				</li>
               </ul>
	           <h4>Search pages that are in beta testing or are demonstrators:</h4>
	           <ul>
		<li><a href="http://www.dlese.org/searchExamples/burnsville/index.html">Burnsville Integrated School District 191</a>
- Professional Development Site - A demonstration of using DLESE to
find professional development resources for a school district in
Minnesota. </li>
		<li><a href="http://www.dlese.org/searchExamples/coreocean/CORE-DLESE.htm">Consortion for Oceanographic Research and Education (CORE)</a> 
		- Using DLESE search to focus on Oceanographic resources for their marine education programs.		</li><li><a href="http://www.dlese.org/searchExamples/ipy/ipy.html">International Polar Year</a> - DLESE resources for polar research and education. Also, see the 
		<a href="http://search.dlese.org/ipy/">original IPY serach page</a> created at the DPC.
		</li>
		<li><a href="http://www.dlese.org/searchExamples/trec/TRECdemoLearningResources.html">Teachers and Researchers - Exploring and Collaborating (TREC)</a> 
		- A demonstration search page for TREC.		</li>
		<li><a href="http://search.dlese.org/www.teachersdomain.org/teaching_resources.html?qh=Renewable Energy&d=Renuable Energy">Teachers Domain</a> - A demo for a Teachers Domain (WGBH) lesson 
		plan that incorporate DLESE resources (see smart link at the bottom of the page).		</li>
		<li><a href="http://search.dlese.org/maps/">DLESE Google Maps</a> - A demonstration of DLESE's geospatial catalog information used to plot DLESE resources using Google maps.		</li>
		<li><a href="http://search.dlese.org/nhs/">Nederland High School</a> - Nederland, Colorado library concept map and DLESE search page.		</li>
		<li><a href="http://search.dlese.org/eval/">Evaluation Services</a> - A specialized search page that highlights Evaluation Services resources.		</li>
		<li><a href="http://search.dlese.org/eno.iris.edu/">IRIS ENO</a> - The IRIS seismology education and outreach site.		</li>
		<li><a href="http://search.dlese.org/education.noaa.gov/">NOAA Education</a> - the NOAA education Web site.		</li>
		<li><a href="http://search.dlese.org/woodshole/">Woods Hole</a> - The outreach Web site at Woods Hole.		</li>
		<li><a href="http://search.dlese.org/score/search.htm">SCORE</a> - Space science resources: The South Central Organization of Researchers and Educators site.		</li>
		<li><a href="http://search.dlese.org/meteorology">DLESE resources for meteorology</a> - DLESE resources about atmospheric science, focus site.		</li>
		<li><a href="http://search.dlese.org/climate/">DLESE resources on climate</a> - Climate and climate change.		</li>
	           </ul>
	           <h3><b>DLESE library components that are implemented and in operation using web services:</b></h3>
	           <ul>
		<li><a href="http://www.dlese.org">Resource discovery</a> - a system for searching educational resources  by type, grade level, education standard, and collection </li>
		<li><a href="http://www.dlese.org/jsp/cas/">Contributor Acknowledgement System</a> - a system 
		that automatically notifies resource creators that their resources have been cataloged in the library	</li></ul>
	<h3><b>Additional DLESE search pages:</b></h3>
	<ul>
		<li><a href="http://search.dlese.org/localization/" >Localization example</a> - a search page that demonstates the use of language localization.
		</li>
		<li><a href="http://search.dlese.org/query_builder/">Query builder</a> - DLESE search page query building tool.				
		</li>
		<li><a href="http://search.dlese.org/query_test_page.jsp">Query test page</a> - a simple search page for testing queries.		</li>
		<li><a href="http://search.dlese.org/content/">Content search comparison</a> - a search page for comparing search over metadata vs search over metadata and content combined.	
		</li><li><a href="http://search.dlese.org/js/">JavaScript search clients</a> - search page clients implemented using a JavaScript service proxy.	
		</li>
		<!-- <li><a href="ddsws/templates/index.jsp" target="_blank">Search client template</a> - a downloadable template for creating custom search pages and to test queries.
		</li> -->
		<li><a href="http://search.dlese.org/bridge/">Bridge search client</a> and <a href="http://search.dlese.org/bridge/bridge_xml_parser.jsp">XML parser tool</a> - The Bridge search page and XML parser tool.
		</li>
		<li><a href="http://search.dlese.org/preaccession/">Preaccessioned collections search client</a> - Search page for preaccessioned collections used for accessioning and quality assurance.	</li>
	</ul>

<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
  
</body></html>