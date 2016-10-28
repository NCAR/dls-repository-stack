<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="../../JSTLTagLibIncludes.jsp" %>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>

<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>

<html>
<head>
	<title>DLESE JavaScript Search Page Service, v1.0</title>
	
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<META NAME="keywords" CONTENT="DLESE,digital library for earth system education">
	<META NAME="description" CONTENT="DLESE Discovery System JavaScript Service Portal">
	<META NAME="creator" CONTENT="John Weatherley">
	<META NAME="organization" CONTENT="DLESE Program Center">
	<META NAME="doctype" CONTENT="DLESE webpage">

	<style type="text/css">
		.reqLnk { 
			padding: auto;
			font-family: Arial, Helvetica, sans-serif; 
			font-size: 10pt; 
			padding-bottom: 10px; 
			padding-left: 10px; 
		} 
		
		/* ------ Styles used in this page ------ */
		
		.examplesDivList {
			padding-bottom: 2px;
		}		
	</style>
	
	<%-- DLESE template header (CSS styles and JavaScript references) --%>
	<c:import url="${domain}/dlese_shared/templates/header_refs.html" /> 

</head>

<body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066">
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   JavaScript<br/>Search Service
</div>

<%-- DLESE template top (banner menu) --%>
<c:import url="${domain}/dlese_shared/templates/content_top.html"/>

<h1>DLESE JavaScript Search Page Service</h1>

<div style="padding-left:15px"> 
  <p>The DLESE JavaScript search page service provides a way to place a fully-functional
  DLESE search any Web page using JavaScript. Because it uses JavaScript, Web site designers
  can use the service in static HTML pages without the need to install an application server.
  </p> 
   
   <p>
   Note that this version of the service (v1.0) is in beta phase, meaning it is still undergoing
   testing and development. We consider the service to be stable, however
   we are gathering information about how this service can best support it's 
   intended users. We encourage you to use this service
   in your HTML pages and help us make it better. Please 
   send comments to <a href="mailto:support@dlese.org">support@dlese.org</a>.
  </p>  
  				
	<a name="examples"></a>
	<h1>Examples</h1>
	<p> 
	These example HTML pages use the JavaScript search page service to
	render a DLESE search in different ways. 

		<ul>
			<li>
			<a href="/dds/services/examples/jshtml1-0/example1.html">Example 1</a> - 
			Uses the DLESE JavaScript search page with it's default rendering settings.	 
			</li>
			
			<li>
			<a href="/dds/services/examples/jshtml1-0/example2.html">Example 2</a> - 
			In this example the HTML designer has made some simple customizations 
			to the search page.
			</li>
			
			<li>
			<a href="/dds/services/examples/jshtml1-0/example3.html">Example 3</a> - 
			In this example the HTML designer has places some smart links at the
			top of the page and changed the default rendering of the search page.
			</li>			
		</ul>
	</p>
	
	<p>
	To insert a DLESE search into your HTML page, open one of the above
	examples in your web browser and choose &quot;view source.&quot; 
	Then simply copy and paste the code between the &lt;SCRIPT&gt; into your page.
	Read the comments shown in the code to see how you can customize 
	the look-and-feel of your search page.
	</p>
	
	
	<a name="sp"></a>
	<h1>Search page output explorer</h1>
	<p> 
		You can view the the JavaScript search page service
		in it's original <a href="/dds/services/jshtml1-0/tpl/js_search_server.jsp">HTML output</a> 
		or as <a href="/dds/services/jshtml1-0/tpl/js_search_server.jsp?rt=jswl">JavaScript output</a>.
	</p>
	
</div>

<%-- DLESE template bottom (footer links and logo) --%>
<c:import url="${domain}/dlese_shared/templates/content_bottom.html"/>
</body>
</html>


