<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>


<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>

<html>
<head>
	<title>JavaScript Search Service: Documentation</title>
	
	<%-- DLESE template header (CSS styles and JavaScript references) --%>
	<%@ include file="/nav/head.jsp" %> 
	
	<LINK REL="stylesheet" TYPE="text/css" HREF="${contextUrl}/services/jshtml1-1/documentation_styles.css">		
     
</head>

<body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066">


<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   JavaScript<br/>Search Service
</div>


<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>

		   
<h1>JavaScript Search Service Documentation</h1>


<p>Service version JSHTML v1.1<br>
</p>

<p>The JavaScript Search Service (JSHTML) lets Web developers place repository search for <a href="http://www.dlese.org/Metadata/adn-item/">ADN resources</a> into Web pages with JavaScript, and is available from the <a href="/dds/services/dds_software.jsp">Digital Discovery System</a> (DDS). The service is designed to be used in real-time, high-availability Web sites to provide interactive search and discovery interfaces for repository resources. 
<%-- Note: This service is scheduled to be deprecated in favor of the more general Search API (see below). --%>
</p>
<p>The service generates interfaces for  DDS resources that reside in the <em>ADN metadata format only</em>.
For a general purpose search JavaScript client, use the <a href="../ddsws1-1/index.jsp">Search API</a> instead. A general-purpose JavaScript client that illustrates it's use is show in <a href="../examples/ddsws1-1/index.jsp">these examples</a>.
</p>
<p>With this JavaScript API, developers can place  search into their web page and customize a range of options such as the interactive search features that are made available to users, the way items are displayed in the page and the behavior and content made available from the the search environment itself. Customizable options include a search interface and standard menus, search results and full descriptions of the resources and the collections in which they reside, custom menus that can be mapped to any topic or area of interest,  SmartLinks that, when clicked, perform pre-defined searches and display them in the page,  and non-interactive displays of resources based on developer-defined searches.</p>
<h2>How to create a custom  search page </h2>
<p>A simple way to create your own custom search page is to start with one of the <a href="../examples/jshtml1-1/index.html" target="_blank">templates and examples</a> and use it as a boilerplate for your custom page. Another way is to use the documentation provided below, which contains a detailed description of the service API and example code that can be copied and pasted directly into your existing web page. </p>
<p>The service API can be used in static HTML pages (.html or .htm), as well as dynamic pages such as JSP, PHP, ASP, CGI and others. The service uses JavaScript in the web browser to render the  search - see the list of <a href="#browsers">supported browsers</a> below. </p>
<h2>How to deploy and install your custom page </h2>
<p> After you are done creating your page, simply deploy the page to your web site as you would any other HTML page. There are no special requirements needed to install or deploy pages that use the  JavaScript Search API. On most computers, you can  also view and interact with your page directly from your hard drive by simply double-clicking on the file    (for example &quot;index.html&quot;), and opening it in your web browser. </p>
<h2><a name="concepts"></a>Definitions and concepts </h2>
<p>The  JavaScript Search Service (JSHTML) is a web API that uses JavaScript to provide a customizable  search environment and deliver  search results to your web page. To use the service, you simply insert two &lt;SCRIPT&gt; elements into your web page at the point in which you want the search interface and dynamic content to be displayed. For example, the following code places a simple search box into a web page - simply copy and paste this code into your web page to see how it works:</p>

<c:set var="command">
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   ShowElement("searchBox");	
   RenderPage();	
 -->
</SCRIPT>
</c:set>
<c:url value="command_viewer.jsp" var="viewerUrl">
	<c:param name="command" value="${command}"/>
	<c:param name="anchor" value="concepts"/>
</c:url>
<c:url value="command_explorer.jsp" var="explorerUrl">
	<c:param name="command" value="${command}"/>
	<c:param name="anchor" value="concepts"/>
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a> </p>


<p>The first &lt;SCRIPT&gt; element points to the<em> serviceURL</em>, which is a JavaScript library  that provides access to the service provider and the available <em>commands</em>. The second is an inline &lt;SCRIPT&gt; element that is used to insert one or more  <em>commands</em> into your page. The <em>commands</em>  provide an API to control  the interactive features of the search environment and the dynamic content that is displayed in the page. Lastly, you may customize the fonts, colors and sizes of the dynamic HTML content displayed in your page using CSS, and an optional style sheet of <a href="view_suggested_styles.jsp">suggested CSS styles</a> is provided for inclusion in  your page. </p>
<ul>
  <li><em>Commands</em> - The <a href="#commands">commands</a> provide an API used to control which interactive features are shown in the search page such as a search box, menus 
  and SmartLinks, as well as the way information gets displayed in the search results.</li>
  <li><em>serviceURL</em>- The <a href="#serviceurl">serviceURL</a> provides the library of JavaScript commands and access to the given service provider. </li>
  <li><em>Suggested CSS</em> - The <a href="view_suggested_styles.jsp">suggested CSS styles</a> is a style sheet that is recommended for use in the web pages that use the service. </li>
</ul>
<h2>Example use in HTML</h2>
<p>The following HTML code creates a simple search page using the API. 
You may copy this code, save it as a separate file (for example search.html) and use it to start your own page:</p>

<c:import url="basic_example.html" var="basicExample"/>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${basicExample}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="basic_example.html" target="_blank">view this page</a></p>

<h1><a name="commands"></a>Commands</h1>
<p>This section describes the API for the service, knows as <em>commands</em>. The commands control the interactive features of the search environment and the dynamic content that is displayed in the page.</p>

<p>By default the service renders nothing in the page until one or more commands are inserted. In a typical development scenario, a developer will start with one of the two <a href="#required">required commands</a> and then add additional commands until each of the desired search and display features are included in the page. </p>
<h3>Using the commands </h3>
<p>With the exception of the <a href="#SmartLinkCommands">SmartLink commands</a>, all commands <em>must</em> reside within a single inline &lt;SCRIPT&gt; element. Prior to this inline script element you <em>must</em> source the JavaScript service library, or <a href="#serviceurl"><em>serviceURL</em></a>. Together, these two &lt;SCRIPT&gt; elements should be placed in your HTML page at the point in which you would like the dynamic content to be displayed and they <em>must not</em> be used more than once within a single  page. </p>
<p>For example: </p>
<c:set var="command">
<!-- The serviceURL -->
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>

<!-- An inline script element where the commands are used -->
<SCRIPT TYPE="text/javascript">
 <!--	
   ShowElement("searchBox");	
   RenderPage();	
 -->
</SCRIPT>
</c:set>
<c:url value="command_viewer.jsp" var="viewerUrl">
	<c:param name="command" value="${command}"/>
	<c:param name="anchor" value="commands"/>
</c:url>
<c:url value="command_explorer.jsp" var="explorerUrl">
	<c:param name="command" value="${command}"/>
	<c:param name="anchor" value="commands"/>
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a> </p>


<h2>Summary of available commands</h2>
<p>Commands fall into one of four categories: Display commands,  SmartLink commands,  Logic commands and Required commands. All commands are optional except for the required commands, and many commands may be repeated.</p>
<p><strong>Display commands</strong> - These control what search options are available to your users and how the search results are displayed in your page.</p>
<p><a href="#ShowElement">ShowElement</a> - Instructs the service to display a particular HTML element in the page.</p>
<p><a href="#HideElement">HideElement</a> - Instructs the service to hide or disable a particular HTML element that would normally be displayed. </p>
<p><a href="#CustomMenuItem">CustomMenuItem</a> - Instructs the service to create a custom menu for your page, as you define.</p>
<p><a href="#MaxResultsPerPage">MaxResultsPerPage</a> - Instructs the service to display a given maximum number of results per page.</p>
<p><a href="#MaxLinkLength">MaxLinkLength</a> - Instructs the service to truncate the links displayed in the page to a given length.</p>
<p><a href="#MaxDescriptionLength">MaxDescriptionLength</a> - Instructs the service to truncate the description displayed in the search results to a given length. </p>
<p><strong>SmartLink commands</strong> - These control and define dynamic links and drop menus in your page.</p>
<p><a href="#SmartLink">SmartLink</a> - Instructs the service to perform a predefined search when a given link is clicked.</p>
<p><a href="#SmartLinkDropList">SmartLinkDropList</a> - Instructs the service to perform a predefined search when a given selection is made in a drop list.</p>
<p><strong>Logic commands</strong> - These control  global  behaviors for your page. </p>
<p><a href="#SearchConstraint">SearchConstraint</a> - Instructs the service to constrain all searches in the page to a given sub-set of resources.</p>
<p><a href="#SearchBoost">SearchBoost</a> - Instructs the service to boost the ranking of results in the page that match the criteria you define.</p>
<p><a href="#SortBy">SortBy</a> - Instructs the service to apply a given sort order to the results that are displayed.</p>
<p><a href="#RedirectSearchTo">RedirectSearchTo</a> - Instructs the service to redirect the user's search request to a second page where the result will be displayed. </p>
<p><a href="#SuppressErrors">SuppressErrors</a> - Instructs the service to suppress any developer errors that may be returned by the service. </p>
<p><strong>Required commands</strong> - One of these two commands must be included in order to render the dynamic content. </p>
<p><a href="#RenderPage">RenderPage</a> - Instructs the service to render the page. </p>
<p><a href="#RenderSearchResults">RenderSearchResults</a> - Instructs the service to perform a search based on a query you define and render the results automatically in the page.  </p>





<%-- ---- Display commands ---------------------------------------------------------- --%>

<p>&nbsp;</p>
<h2>Display commands</h2>
<p>These commands are used to control what search options are available to your users and how the search results are displayed in your page.</p>
<h3><a name="ShowElement"></a>ShowElement</h3>
<p><strong>Sample command </strong></p>
<pre>ShowElement("searchBox");</pre>


<p><strong>Summary and usage </strong></p>
<p>The ShowElement command instructs the service to display a particular HTML element in your page. You may repeat this command as many times as needed to insert each of the elements you would like displayed. The order in which you include this command in your page has no effect on the order in which items are displayed. </p>
<p><strong>Arguments</strong></p>
<p>This command takes one argument. The value of the argument must be one of the following:</p>
<ul>
  <li>searchBox - displays a search box in your page.
  </li>
</ul>
<ul>
  <li> gradeLevelsMenu - displays a menu for selecting grade levels</li>
</ul>
<ul>
  <li> resourceTypesMenu - displays a menu for selecting resource types </li>
</ul>
<ul>
  <li> subjectsMenu - displays a menu for selecting subjects </li>
</ul>
<ul>
  <li> contentStandardsMenu - displays a menu for selecting content standards </li>
</ul>
<ul>
  <li> collectionsMenu - displays a menu for selecting collections </li>
</ul>
<ul>
  <li> gradeLevelSR - displays the grade levels in the search results </li>
</ul>
<ul>
  <li> resourceTypeSR - displays the resource types in the search results </li>
</ul>
<ul>
  <li> subjectSR - displays the subjects in the search results</li>
</ul>
<ul>
  <li> collectionSR - displays the collections in the search results</li>
</ul>
<ul>
  <li> accessionDateSR - displays the date the resource was accessioned into the library in the search results </li>
</ul>
<ul>
  <li> annotations - displays the annotations in the search results and full description </li>
</ul>
<ul>
  <li> searchWithinSmartLinkResults - displays an option to search within SmartLink search results </li>
</ul>

<p><strong>Errors and exceptions</strong></p>
<p>This command returns no errors. If an incorrect argument is used, nothing is displayed. </p>

<p><strong>Examples</strong></p>

<p><strong><i>Example 1</i></strong></p>
<p>The following displays a search box:</p>
<c:set var="command">
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   ShowElement("searchBox");	
   RenderPage();	
 -->
</SCRIPT>
</c:set>
<c:url value="command_viewer.jsp" var="viewerUrl">
	<c:param name="command" value="${command}"/>
	<c:param name="anchor" value="ShowElement"/>
</c:url>
<c:url value="command_explorer.jsp" var="explorerUrl">
	<c:param name="command" value="${command}"/>
	<c:param name="anchor" value="ShowElement"/>
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a> </p>

<p><strong><i>Example 2</i></strong></p>
<p>The following displays a search box, the menu for grade levels, the menu for resource types, and the menu for subjects:</p>
<c:set var="command">
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   ShowElement("searchBox");
   ShowElement("gradeLevelsMenu");
   ShowElement("resourceTypesMenu");
   ShowElement("subjectsMenu");   	
   RenderPage();	
 -->
</SCRIPT>
</c:set>
<c:url value="command_viewer.jsp" var="viewerUrl">
	<c:param name="command" value="${command}"/>
	<c:param name="anchor" value="ShowElement"/>
</c:url>
<c:url value="command_explorer.jsp" var="explorerUrl">
	<c:param name="command" value="${command}"/>
	<c:param name="anchor" value="ShowElement"/>
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a> </p>

<p><strong><i>Example 3</i></strong></p>
<p>The following displays a search box, the menu for grade levels, the menu for resource types, and the menu for subjects. Then,
in the search results it displays the grade levels, resource types and subjects:</p>
<c:set var="command">
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   ShowElement("searchBox");
   ShowElement("gradeLevelsMenu");
   ShowElement("resourceTypesMenu");
   ShowElement("subjectsMenu");
   ShowElement("gradeLevelSR");
   ShowElement("resourceTypeSR");
   ShowElement("subjectSR");    	
   RenderPage();	
 -->
</SCRIPT>
</c:set>
<c:url value="command_viewer.jsp" var="viewerUrl">
	<c:param name="command" value="${command}"/>
	<c:param name="anchor" value="ShowElement"/>
	<c:param name="q" value="ocean"/>
</c:url>
<c:url value="command_explorer.jsp" var="explorerUrl">
	<c:param name="command" value="${command}"/>
	<c:param name="anchor" value="ShowElement"/>
	<c:param name="q" value="ocean"/>
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a> - Note: 
when using these links, a search for 'ocean' is automatically performed for you to show how the search results
are displayed. </p>

<p><strong><i>Example 4</i></strong></p>
<p>The following displays a search box, the menu for grade levels, the menu for resource types, and the menu for subjects. Then,
in the search results it displays the grade levels, resource types and subjects:</p>
<c:set var="command">
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   ShowElement("searchBox");
   ShowElement("gradeLevelsMenu");
   ShowElement("resourceTypesMenu");
   ShowElement("subjectsMenu");
   ShowElement("gradeLevelSR");
   ShowElement("resourceTypeSR");
   ShowElement("subjectSR");    	
   RenderPage();	
 -->
</SCRIPT>
</c:set>
<c:url value="command_viewer.jsp" var="viewerUrl">
	<c:param name="command" value="${command}"/>
	<c:param name="anchor" value="ShowElement"/>
	<c:param name="q" value="ocean"/>
</c:url>
<c:url value="command_explorer.jsp" var="explorerUrl">
	<c:param name="command" value="${command}"/>
	<c:param name="anchor" value="ShowElement"/>
	<c:param name="q" value="ocean"/>
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a> - Note: 
when using these links, a search for 'ocean' is automatically performed for you to show how the search results
are displayed. </p>


<h3><a name="HideElement"></a>HideElement</h3>
<p><strong>Sample command </strong></p>
<pre>HideElement("pager");</pre>
<p><strong>Summary and usage </strong></p>
<p>The HideElement command instructs the service to hide or disable an HTML element that would normally be displayed by the service. You may repeat this command as many times as needed to disable each of the elements you would like hidden. The order in which you include this command in your page has no effect on it's behavior. </p>
<p><strong>Arguments</strong></p>
<p>This command takes one argument. The value of the argument must be one of the following:</p>
<ul>
  <li>pager - disables the display of the links to page through the search results. This may be useful for pages that wish to display a fixed number of resources only, for example. </li>
</ul>
<ul>
  <li> titleRewrite - normally the service re-writes the title displayed in the page to one that reflects the search action performed by the user. Using this argument, you can disable  title re-writing to preserve the title you have assigned to the page. </li>
</ul>
<ul>
  <li> logo - disables the display of the DLESE logo at the bottom of the page. This may be useful for sites in which you want to display a simple search box in one page (with no logo) and the search results in another page, using the RedirectSearchTo( ) command. </li>
</ul>
<p><strong>Errors and exceptions</strong></p>
<p>This command returns no errors. If an incorrect argument is used, nothing is changed. </p>
<p><strong>Examples</strong></p>

<p><strong><i>Example 1</i></strong></p>
<p>The following disables the display of the links to page through the results. 
It then displays a list of the ten most relevant classroom activities about 'earthquakes':</p>
<c:set var="command">
<b style="margin-left:4px">Classroom activities about earthquakes:</b>
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   HideElement("pager");	
   RenderSearchResults("earthquakes AND re:0c");
 -->
</SCRIPT>
</c:set> 
<c:url value="command_viewer.jsp" var="viewerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="HideElement"/> 
</c:url> 
<c:url value="command_explorer.jsp" var="explorerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="HideElement"/> 
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a></p>

<p><strong><i>Example 2</i></strong></p>
<p>The following displays a simple search page with the title re-write function of the service turned off:</p>
<c:set var="command">
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   HideElement("titleRewrite");	
   ShowElement("searchBox");
   RenderPage();
 -->
</SCRIPT>
</c:set> 
<c:url value="command_viewer.jsp" var="viewerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="HideElement"/>
	<c:param name="q" value="ocean"/>
</c:url> 
<c:url value="command_explorer.jsp" var="explorerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="HideElement"/>
	<c:param name="q" value="ocean"/>
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a>  - Note: 
when using these links, a search for 'ocean' is automatically performed for you to show how the search results
are displayed.</p>



<h3><a name="CustomMenuItem"></a>CustomMenuItem</h3>
<p><strong>Sample command </strong></p>
<pre>CustomMenuItem("Topics","Biology","su:03");</pre>
<p><strong>Summary and usage </strong></p>
<p>The CustomMenuItem command allows you to create custom menus for your page. 
You may repeat this command as many times as needed to define each of the menus and menu items you want for your page. 
The order in which you include this command in your page determines the order in which the custom menu is rendered,
relative to other custom menus. Custom menus are always placed after the standard menus. Note that you
may not add or remove items from the standard menus. </p>
<p>
See related discussion about <a href="#search">embedded search queries</a> and <a href="#escape">escaping reserved characters</a>.
</p>

<p><strong>Arguments</strong></p>
<p>This command takes three arguments. The arguments are used as follows:</p>
<ul>
	<li>
		First argument: menu name - Indicates the name of the custom menu. 
		If no custom menu exists by this name, one will first be created and then the
		item will be added to it. This name is set to upper case automatically.
	</li>
</ul>
<ul>
	<li>
		Second argument: item name - Indicates the name of the item to add to the given menu.
	</li>
</ul>
<ul>
	<li>
		Third argument: query mapping - Defines the search query that gets mapped to this menu item. 
		When the menu item is selected by the user, their search is limited to the set of
		resources that match this query.
	</li>
</ul>
<p><strong>Errors and exceptions</strong></p>
<p>This command returns an error if other than three arguments are supplied. </p>
<p><strong>Examples</strong></p>

<p><strong><i>Example 1</i></strong></p>
<p>The following creates custom menu for ocean sciences, using the three ocean science
subject classifiers:</p>
<c:set var="command">
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   CustomMenuItem("Ocean science field","Biological oceanography","su:02");
   CustomMenuItem("Ocean science field","Chemical oceanography","su:04");
   CustomMenuItem("Ocean science field","Physical oceanography","su:0p");	
   ShowElement("searchBox");
   RenderPage();
 -->
</SCRIPT>
</c:set> 
<c:url value="command_viewer.jsp" var="viewerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="CustomMenuItem"/> 
</c:url> 
<c:url value="command_explorer.jsp" var="explorerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="CustomMenuItem"/> 
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a></p>

<p><strong><i>Example 2</i></strong></p>
<p>The following creates two custom menus and also uses the standard menu for grade levels:</p>
<c:set var="command">
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   // Use the placeNames field to designate locations:
   CustomMenuItem("Location","California","placeNames:california");
   CustomMenuItem("Location","Oregon","placeNames:oregon");

   // Select resource types for activities:
   CustomMenuItem("Type of resource","Activities", "re:0c OR re:0d OR re:0g OR re:0j");
   // Select all resource types in Tools:
   CustomMenuItem("Type of resource","Tools", "re:00h OR re:00i OR re:00j");
   // Select all Data set resource types:
   CustomMenuItem("Type of resource","Datasets", "re:07 OR re:08 OR re:09");   
   
   ShowElement("searchBox");
   ShowElement("gradeLevelsMenu");
   ShowElement("resourceTypeSR");
   RenderPage();
 -->
</SCRIPT>
</c:set> 
<c:url value="command_viewer.jsp" var="viewerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="CustomMenuItem"/> 
</c:url> 
<c:url value="command_explorer.jsp" var="explorerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="CustomMenuItem"/> 
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a></p>



<h3><a name="MaxResultsPerPage"></a>MaxResultsPerPage</h3>

<p><strong>Sample command </strong></p>
<pre>MaxResultsPerPage("5");</pre>

<p><strong>Summary and usage </strong></p>
<p>The MaxResultsPerPage command instructs the service to display a given maximum number of results per page.
If this command is not used, the default value is set to 10.</p>

<p><strong>Arguments</strong></p>
<p>This command takes one argument.</p>
<ul>
	<li>
		Argument sets the maximum number of results to display. The value of the argument must be an integer from 1 to 100.
	</li>
</ul>

<p><strong>Errors and exceptions</strong></p>
<p>This command returns an error if the value provided is not an integer from 1 to 100. </p>

<p><strong>Examples</strong></p>

<p><strong><i>Example 1</i></strong></p>
<p>The following sets the number of results to display to 5, and displays 
a search box:</p>
<c:set var="command">
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   MaxResultsPerPage("5");
   ShowElement("searchBox");
   RenderPage();
 -->
</SCRIPT>
</c:set> 
<c:url value="command_viewer.jsp" var="viewerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="MaxResultsPerPage"/>
	<c:param name="q" value="seismometers"/> 	
</c:url> 
<c:url value="command_explorer.jsp" var="explorerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="MaxResultsPerPage"/> 
	<c:param name="q" value="seismometers"/> 		
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a> - Note: 
when using these links, a search for 'seismometers' is automatically performed for you to show how the search results
are displayed. </p>

<p><strong><i>Example 2</i></strong></p>
<p>This example demonstrates an error condition. The MaxResultsPerPage command 
is used improperly to set an invalid value of 200. In this case an error message
is displayed immediately when the page is displayed because of the use of the
RenderSearchResults command. Note that many errors are not displayed until
the user performs a search in the page.</p>
<c:set var="command">
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   MaxResultsPerPage("200");
   RenderSearchResults("global climate change");
 -->
</SCRIPT>
</c:set> 
<c:url value="command_viewer.jsp" var="viewerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="MaxResultsPerPage"/>
</c:url> 
<c:url value="command_explorer.jsp" var="explorerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="MaxResultsPerPage"/> 
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a></p>



<h3><a name="MaxLinkLength"></a>MaxLinkLength</h3>
<p><strong>Sample command </strong></p>
<pre>MaxLinkLength("50");</pre>

<p><strong>Summary and usage </strong></p>
<p>The MaxLinkLength command instructs the service to truncate the links displayed in the page to a given length.
Setting a smaller value helps avoid problems with pages not wrapping properly in
narrow pages and/or elements.
If this command is not used, the default value is set to 80.</p>

<p><strong>Arguments</strong></p>
<p>This command takes one argument.</p>
<ul>
	<li>
		Argument sets the maximum length of the links that are displayed in the page. The value of the argument must be an integer greater than 0.
	</li>
</ul>

<p><strong>Errors and exceptions</strong></p>
<p>This command returns an error if the value provided is not an integer greater than 0. </p>

<p><strong>Examples</strong></p>
<p>See the <a href="../examples/jshtml1-1/index.html" target="_blank">templates and examples</a>.</p>




<h3><a name="MaxDescriptionLength"></a>MaxDescriptionLength</h3>
<p><strong>Sample command </strong></p>
<pre>MaxDescriptionLength("200");</pre>

<p><strong>Summary and usage </strong></p>
<p>The MaxDescriptionLength command instructs the service to truncate the description displayed in the search results to a given length.
This may be useful when you wish to provide a compact display of resources in your page.
When truncated, the description is displayed with three trailing periods ( ... ).
If this command is not used, the default value is set to 400.</p>

<p><strong>Arguments</strong></p>
<p>This command takes one argument.</p>
<ul>
	<li>
		Argument sets the maximum length of the description that is displayed in the page. The value of the argument must be an integer greater than 0.
	</li>
</ul>

<p><strong>Errors and exceptions</strong></p>
<p>This command returns an error if the value provided is not an integer greater than 0. </p>

<p><strong>Examples</strong></p>
<p>See the <a href="../examples/jshtml1-1/index.html" target="_blank">templates and examples</a>.</p>



<%-- ---- SmartLink commands ---------------------------------------------------------- --%>

<p>&nbsp;</p>
<h2><a name="SmartLinkCommands"></a>SmartLink commands </h2>
<p>The two SmartLink commands (SmartLink and SmartLinkDropList) provide a way to display a set of search results 
 when a user clicks a hyperlink or selects an item from a drop list.
</p>
 
 <p>SmartLink commands are different from the other commands because they 
are used within standard HTML elements (the &lt;a&gt; tag and &lt;select&gt; lists) in your page and not within the the inline &lt;SCRIPT&gt; element.</p>

<p>Tip: Use the <a href="smart_link_query_builder.jsp">SmartLink Builder</a> tool to create SmartLinks for your 
pages.</p>

<h3><a name="SmartLink"></a>SmartLink</h3>
<p><strong>Sample command</strong></p>
<pre>&lt;a href='JavaScript:SmartLink(&quot;oceans&quot;,&quot;Oceans&quot;)'&gt;Oceans&lt;/a&gt;</pre>
<p><strong>Summary and usage </strong></p>
<p>The SmartLink command allows you to create one or more custom hyperlinks for your page that 
is mapped to one or more search queries. When the user clicks on a hyperlink SmartLink, the given query is executed and
the search results are displayed in your page. This allows you to make a group of resources available 
to your site visitors with a single click, and embed your knowledge of the repository's information
domain into your page. </p>
<p>This command must be placed within an HTML &lt;a&gt; tag, as shown above.</p>

<p>
See related discussion about <a href="#search">embedded search queries</a> and <a href="#escape">escaping reserved characters</a>.
</p>

<p>Tip: Use the <a href="smart_link_query_builder.jsp">SmartLink Builder</a> tool to create SmartLinks for your 
pages. You can also create your SmartLinks by writing and editing code directly, as detailed below.</p>

<p><strong>Arguments</strong></p>
<p>This command takes two arguments. The arguments are used as follows:</p>

<ul>
	<li>
		First argument: query mapping - Defines the search query that gets mapped to the SmartLink. 
		When the link is clicked by the user, this search query is executed and the
		results are displayed in your page.
	</li>
</ul>
<ul>
	<li>
		Second argument: link name - Indicates the name of the SmartLink,
		which is displayed in the search results. Note that you also must
		use standard HTML to create your link and link name.
	</li>
</ul>

<p><strong>Errors and exceptions</strong></p>
<p>This command returns an error if anything other than two arguments are supplied. </p>
<p><strong>Examples</strong></p>

<p><strong><i>Example 1</i></strong></p>
<p>The following creates two SmartLinks: one for tsunamis and one for earthquakes:</p>
<c:set var="command">
<div style="margin-left:5px">
  <b>Click on a topic SmartLink:</b><br>
  <a href='JavaScript:SmartLink("tsunamis","Tsunamis")'>Tsunamis</a><br>
  <a href='JavaScript:SmartLink("earthquakes","Earthquakes")'>Earthquakes</a><br>
</div>
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   RenderPage();
 -->
</SCRIPT>
</c:set> 
<c:url value="command_viewer.jsp" var="viewerUrl"> 
	<c:param name="command" value='${fn:replace(command,"\'","%27")}'/> 
	<c:param name="anchor" value="SmartLink"/> 
</c:url> 
<c:url value="command_explorer.jsp" var="explorerUrl"> 
	<c:param name="command" value='${fn:replace(command,"\'","%27")}'/>
	<c:param name="anchor" value="SmartLink"/> 
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a></p>


<p><strong><i>Example 2</i></strong></p>
<p>The following creates two SmartLinks: one for Florida and one for California.
It also places a search box in the page and allows the user to search within
the SmartLink results:</p>
<c:set var="command">
<div style="margin-left:5px">
  <b>Click on a locale SmartLink:</b><br>
  <a href='JavaScript:SmartLink("florida","Florida")'>Florida</a><br>
  <a href='JavaScript:SmartLink("california","California")'>California</a><br>
</div>
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   ShowElement("searchBox");
   ShowElement("searchWithinSmartLinkResults");
   RenderPage();
 -->
</SCRIPT>
</c:set> 
<c:url value="command_viewer.jsp" var="viewerUrl"> 
	<c:param name="command" value='${fn:replace(command,"\'","%27")}'/> 
	<c:param name="anchor" value="SmartLink"/> 
</c:url> 
<c:url value="command_explorer.jsp" var="explorerUrl"> 
	<c:param name="command" value='${fn:replace(command,"\'","%27")}'/> 
	<c:param name="anchor" value="SmartLink"/> 
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a></p>



<h3><a name="SmartLinkDropList"></a>SmartLinkDropList</h3>
<p><strong>Sample command</strong></p>
<pre>
&lt;select id=&quot;smartLinkDropList&quot; onchange=&quot;JavaScript:SmartLinkDropList()&quot;&gt;
  &lt;option value=&quot;&quot;&gt;-- Select a category --&lt;/option&gt;		   
  &lt;option value=&quot;tsunamis&quot;&gt;Tsunamis&lt;/option&gt;
  &lt;option value=&quot;earthquakes&quot;&gt;Earthquakes&lt;/option&gt;				   
&lt;/select&gt;
</pre>
<p><strong>Summary and usage </strong></p>
<p>The SmartLinkDropList command allows you to create a drop list containing one or more custom selections for your page that 
is mapped to one or more search queries. When the user selects an item in the SmartLinkDropList, the given query is executed and
the search results are displayed in your page. This allows you to make groups of resources available 
to your site visitors from a single drop menu, and embed your knowledge of the repository's information
domain into your page. </p>
<p>This command must be used within an HTML &lt;select&gt; list in conjunction with the
HTML &lt;option&gt; tag. Specifically, this command must be placed within the 'onchange' attribute
of the &lt;select&gt; element and/or within the 'action' element of an encompassing &lt;form&gt; 
for the &lt;select&gt; list. In addition, the 'id' attribute of the &lt;select&gt; list must be set
to 'smartLinkDropList'. See the examples below.</p>

<p>
See related discussion about <a href="#search">embedded search queries</a> and <a href="#escape">escaping reserved characters</a>.
</p>

<p>Tip: Use the <a href="smart_link_query_builder.jsp">SmartLink Builder</a> tool to create SmartLinks for your 
pages. You can also create your SmartLinks by writing and editing code directly, as detailed below.</p>

<p><strong>Arguments</strong></p>
<p>This command takes no arguments within the command JavaScript, however the &lt;option&gt;
tags used within the &lt;select&gt; list must contain the following:</p>

<ul>
	<li>
		The 'value' attribute of the &lt;option&gt; tag: query mapping - Defines the search query that gets mapped to the menu item. 
		When the menu item is selected by the user, this search query is executed and the
		results are displayed in your page. If this is left empty, no action is taken.
	</li>
</ul>
<ul>
	<li>
		Content of the &lt;option&gt; tag: menu name - Indicates the name of the SmartLink,
		which is displayed in the menu and in the search results.
	</li>
</ul>

<p><strong>Errors and exceptions</strong></p>
<p>This command returns an error if the id attribute of the select list is not set properly. </p>
<p><strong>Examples</strong></p>

<p><strong><i>Example 1</i></strong></p>
<p>The following creates a drop list with two options: one for tsunamis and one for earthquakes.
The resources are displayed automatically when the user
selects an item in the list:</p>
<c:set var="command">
<div style="margin-left:5px">
  <b>View resources about the following:</b><br><br>
  <select id="smartLinkDropList" onChange="JavaScript:SmartLinkDropList()">
      <option value="">-- Select a category --</option>		   
      <option value="tsunamis">Tsunamis</option>
      <option value="earthquakes">Earthquakes</option>				   
  </select>
</div>
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   RenderPage();
 -->
</SCRIPT>
</c:set> 
<c:url value="command_viewer.jsp" var="viewerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="SmartLinkDropList"/> 
</c:url> 
<c:url value="command_explorer.jsp" var="explorerUrl"> 
	<c:param name="command" value="${command}"/>
	<c:param name="anchor" value="SmartLinkDropList"/> 
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a></p>


<p><strong><i>Example 2</i></strong></p>
<p>The following creates a drop list with two options: one for &quot;severe storms&quot; and one 
for tornadoes.
The resources are displayed after the user selects an item in the list 
and clicks 'Go':</p>
<c:set var="command">
<div style="margin-left:5px">
  <b>Select a topic and click Go:</b><br><br>
  <form method="get" action="JavaScript:SmartLinkDropList()">  
    <select id="smartLinkDropList">
        <option value="">-- Select a category --</option>		   
        <option value="&quot;severe storms&quot;">Severe storms</option>
        <option value="tornadoes">Tornadoes</option>				   
    </select>
    <input type="submit" value="Go">
  </form>
</div>
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   RenderPage();
 -->
</SCRIPT>
</c:set> 
<c:url value="command_viewer.jsp" var="viewerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="SmartLinkDropList"/> 
</c:url> 
<c:url value="command_explorer.jsp" var="explorerUrl"> 
	<c:param name="command" value="${command}"/>
	<c:param name="anchor" value="SmartLinkDropList"/> 
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a></p>



<p><strong><i>Example 3</i></strong></p>
<p>The following shows a combination of using a SmartLinkDropList along with 
a regular search box with the option to search within the SmartLink results:</p>
<c:set var="command">
<div style="margin-left:5px">
  <b>Choose a quick link:</b><br>
  <select id="smartLinkDropList" onChange="JavaScript:SmartLinkDropList()">
      <option value="">-- Select a category --</option>		   
      <option value="tsunamis">Tsunamis</option>
      <option value="earthquakes">Earthquakes</option>				   
  </select>
  <br><br><b>Or search for resources by keyword:</b><br>
</div>
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   ShowElement("searchBox");
   ShowElement("searchWithinSmartLinkResults");
   RenderPage();
 -->
</SCRIPT>
</c:set> 
<c:url value="command_viewer.jsp" var="viewerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="SmartLinkDropList"/> 
</c:url> 
<c:url value="command_explorer.jsp" var="explorerUrl"> 
	<c:param name="command" value="${command}"/>
	<c:param name="anchor" value="SmartLinkDropList"/> 
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a></p>




  <%-- ---- Logic commands ---------------------------------------------------------- --%>
  <p>&nbsp;</p>
  <h2>Logic commands </h2>
<p>Logic commands are responsible for controlling certain logic in your page such as global search constraints and boosting, how search results are sorted, and whether errors should be displayed to aid developers. </p>
<h3><a name="SearchConstraint"></a>SearchConstraint</h3>
<p><strong>Sample command </strong></p>
<pre>SearchConstraint("su:08");</pre>

<p><strong>Summary and usage </strong></p>
<p>The SearchConstraint command instructs the service to constrain all searches in the page to a given sub-set of resources that you 
define using a search query.
When this command is used, all searches and SmartLinks conducted in the page are constrained 
to the sub-set of resources that match the query provided in this command. 
</p>

<p>
See related discussion about <a href="#search">embedded search queries</a> and <a href="#escape">escaping reserved characters</a>.
</p>

<p><strong>Arguments</strong></p>
<p>This command takes one argument.</p>
<ul>
	<li>
		Argument defines the search query used to constrain all searches and SmartLinks in the page.
	</li>
</ul>

<p><strong>Errors and exceptions</strong></p>
<p>This command returns an error if a syntax error is found in the search query. </p>

<p><strong>Examples</strong></p>
<p>See the <a href="../examples/jshtml1-1/index.html" target="_blank">templates and examples</a>.</p>





<h3><a name="SearchBoost"></a>SearchBoost</h3>
<p><strong>Sample command </strong></p>
<pre>SearchBoost("florida");</pre>

<p><strong>Summary and usage </strong></p>
<p>The SearchBoost command instructs the service to boost the ranking of results in the page that match the criteria you define using a search query.
When this command is used, resources that match the search query are boosted
in all searches and SmartLinks conducted in the page. Boosting provides extra ranking to the resources, which
places them higher in results than they would normally be by default. This allows you to highlight those
resources that are most relevant to your audience.</p>

<p>
See related discussion about <a href="#search">embedded search queries</a> and <a href="#escape">escaping reserved characters</a>.
</p>

<p><strong>Arguments</strong></p>
<p>This command takes one argument.</p>
<ul>
	<li>
		Argument defines the search query used to boost resources in all searches and SmartLinks in the page.
	</li>
</ul>

<p><strong>Errors and exceptions</strong></p>
<p>This command returns an error if a syntax error is found in the search query. </p>

<p><strong>Examples</strong></p>
<p>See the <a href="../examples/jshtml1-1/index.html" target="_blank">templates and examples</a>.</p>






<h3><a name="SortBy"></a>SortBy</h3>
<p><strong>Sample command </strong></p>
<pre>SortBy("title");</pre>

<p><strong>Summary and usage </strong></p>
<p>The SortBy command instructs the service to apply a given sort order to the results that are displayed.
This command lets you sort the results alphabetically by their title or by the date 
in which they were accessioned into the library. If not used, the service
orders the results by relevancy based on the search criteria provided by the user.</p>


<p><strong>Arguments</strong></p>
<p>This command takes one argument. The argument must be one of the following three values</p>
<ul>
	<li>
		title - Sorts the results alphabetically by their title.
	</li>
</ul>
<ul>
	<li>
		mostrecent - Sorts the results in order by their accession date in the library, with the most recent shown first.
	</li>
</ul>
<ul>
	<li>
		mostrelevent - Sorts the results by the most relevant to the user's search query (this is the default setting).
	</li>
</ul>

<p><strong>Errors and exceptions</strong></p>
<p>This command returns an error if an incorrect value is supplied. </p>

<p><strong>Examples</strong></p>
<p>See the <a href="../examples/jshtml1-1/index.html" target="_blank">templates and examples</a>.</p>





<h3><a name="RedirectSearchTo"></a>RedirectSearchTo</h3>
<p><strong>Sample command </strong></p>
<pre>RedirectSearchTo("search_page2.html");</pre>

<p><strong>Summary and usage </strong></p>
<p>The RedirectSearchTo command instructs the service to redirect the user's search request to a second page where the result will be displayed. 
This is useful if you would like to have a small search box on one or more pages that opens to 
a separate search results page within your site.</p>


<p><strong>Arguments</strong></p>
<p>This command takes one argument. The argument must be a valid URL that is either absolute or relative to the current page.</p>

<p><strong>Errors and exceptions</strong></p>
<p>This command returns no errors if used incorrectly. </p>

<p><strong>Examples</strong></p>
<p>See the <a href="../examples/jshtml1-1/example5/index.html">templates and examples</a>. </p>






<h3><a name="SuppressErrors"></a>SuppressErrors</h3>
<p><strong>Sample command </strong></p>
<pre>SuppressErrors();</pre>

<p><strong>Summary and usage </strong></p>
<p>The SuppressErrors command instructs the service to suppress any developer errors that may be returned by the service.
Normally, the service will provide errors if the service is used incorrectly 
in your page, which can be useful during development for debugging.
If you want to ensure errors are not displayed to your users, use this command. When used, this command must 
appear before all other commands declared in
the inline &lt;script&gt; portion of your page.</p>


<p><strong>Arguments</strong></p>
<p>This command takes no arguments.</p>

<p><strong>Errors and exceptions</strong></p>
<p>This command returns no errors if used incorrectly. </p>

<p><strong>Examples</strong></p>
<p>See the <a href="../examples/jshtml1-1/index.html" target="_blank">templates and examples</a>.</p>





  <%-- ---- Required commands ---------------------------------------------------------- --%>
<p>&nbsp;</p>
<h2><a name="required"></a>Required commands </h2>
<p>In order to use the service, <em>exactly one</em> of the two required commands <em>must</em> be placed in the page. Additional commands may also be included in the page in addition to the required command. </p>
<h3><a name="RenderPage"></a>RenderPage</h3>
<p><strong>Sample command </strong></p>
<pre>RenderPage();</pre>

<p><strong>Summary and usage </strong></p>
<p>The RenderPage command instructs the service to render the dynamic portion of the page. This command must appear after all other commands declared in
the inline &lt;script&gt; portion of your page.</p>


<p><strong>Arguments</strong></p>
<p>This command takes no arguments.</p>

<p><strong>Errors and exceptions</strong></p>
<p>This command returns no errors if used incorrectly. </p>

<p><strong>Examples</strong></p>
<p>See the <a href="../examples/jshtml1-1/index.html" target="_blank">templates and examples</a>.</p>





<h3><a name="RenderSearchResults"></a>RenderSearchResults</h3>
<p><strong>Sample command </strong></p>
<pre>RenderSearchResults("colorado");</pre>

<p><strong>Summary and usage </strong></p>
<p>The RenderSearchResults command instructs the service to perform a search based on a query you define and render 
the results automatically in the page. This is useful for listing resources
non-interactively based on a page theme, for example. It can also be used in environments in which the search query
is generated outside of the service framework such as within another application
that generates the queries to display repository resources. 
This command must appear after all other commands declared in
the inline &lt;script&gt; portion of your page.</p>

<p>
See related discussion about <a href="#search">embedded search queries</a> and <a href="#escape">escaping reserved characters</a>.
</p>

<p><strong>Arguments</strong></p>
<p>This command takes one argument.</p>
<ul>
	<li>
		Argument defines the search query used to display the resources in the page.
	</li>
</ul>

<p><strong>Errors and exceptions</strong></p>
<p>This command returns an error if used in conjunction with the RenderPage command or if a query syntax error is present. </p>

<p><strong>Examples</strong></p>

<p><strong><i>Example 1</i></strong></p>
<p>The following displays a list of the relevant classroom activities about 'earthquakes':</p>
<c:set var="command">
<b style="margin-left:4px">Classroom activities about earthquakes:</b>
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   RenderSearchResults("earthquakes AND re:0c");
 -->
</SCRIPT>
</c:set> 
<c:url value="command_viewer.jsp" var="viewerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="RenderSearchResults"/> 
</c:url> 
<c:url value="command_explorer.jsp" var="explorerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="RenderSearchResults"/> 
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a></p>

<p><strong><i>Example 2</i></strong></p>
<p>The following displays a list of ocean science resources, with those resources containing 'florida'
boosted in the search results:</p>
<c:set var="command">
<b style="margin-left:4px">Ocean science resources (with boosting for Florida):</b>
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
 <!--	
   SearchBoost("florida");
   RenderSearchResults("su:02 OR su:04 OR su:0p");
 -->
</SCRIPT>
</c:set> 
<c:url value="command_viewer.jsp" var="viewerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="RenderSearchResults"/> 
</c:url> 
<c:url value="command_explorer.jsp" var="explorerUrl"> 
	<c:param name="command" value="${command}"/> 
	<c:param name="anchor" value="RenderSearchResults"/> 
</c:url>
<table class="codeTB">
  <tr class="codeTB">
    <td class="codeTB"><pre><c:out value="${command}" escapeXml="true"/></pre></td>
  </tr>
</table>
<p>Try it: <a href="${viewerUrl}" target="_blank">view this example</a> or <a href="${explorerUrl}" target="_blank">explore this example</a></p>
<p>&nbsp;</p>



<%-- ---- Additional documentation ---------------------------------------------------------- --%>

<h1><a name="serviceurl" id="serviceurl"></a>serviceURL</h1>
<p> The serviceURL is used to gain access to the service  and available <a href="#commands">commands</a>. To use the service, simply source the JavaScript serviceURL  in your HTML page. </p>

<p>For example: </p>
<c:set var="command">
<SCRIPT TYPE="text/javascript"
   SRC="${contextUrl}/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
</c:set>

<pre><c:out value="${command}" escapeXml="true"/></pre>
<p>Once you have sourced the serviceURL in your page, you can begin to use the commands in inline script and in SmartLink HTML. </p>
<p>To change from using one service provider to another, simply edit the serviceURL to point to that of the desired service provider. For consistency, you should also change the URL you are using to reference the <a href="view_suggested_styles.jsp">suggested CSS styles</a>, if this applies. </p>


<h1><a name="search"></a>Embedded search queries</h1>
<p>
A powerful feature of this service is the ability to embed pre-defined
search queries when customizing the search options and the information that you highlight and 
make available from your page. Search queries are used in the following commands:
CustomMenuItem,
SearchConstraint, SearchBoost, RenderSearchResults, SmartLink, and SmartLinkDropList.
</p>
<p>
Simple queries 
are easy to define, however more complicated queries require familiarity
with the query syntax and knowledge of the search fields that are available from the
service. Many powerful queries are demonstrated in this 
documentation and may be used directly in your own pages. For a full discussion of the
query syntax and search fields, see 
the section linked here entitled 
<a href="http://www.dlese.org/dds/services/ddsws1-1/service_specification.jsp#availableSearchFields">Available search fields</a>.  
</p>



<h1><a name="escape"></a>Escaping reserved characters</h1>
<p>
When defining search queries, the quotation character ( &quot; ) needs
to be escaped both in the JavaScript commands and HTML that you use in your page. 
This applies to the JavaScript used in the following commands: CustomMenuItem,
SearchConstraint, SearchBoost, RenderSearchResults, SmartLink. This also applies to the
the HTML used in conjunction with the SmartLinkDropList command.
</p>
<p>
To escape the quotation character in the JavaScript commands, precede the character with a slash, for example:
</p>
<c:set var="command">
   SearchConstraint("\"Atmospheric science\"");
</c:set>
<pre><c:out value="${command}" escapeXml="true"/></pre>

<p>
To escape the quotation character used in conjunction with the SmartLinkDropList command, replace the quotation with
it's entity reference (&quot;quot;), for example:
</p>
<c:set var="command">		
<select id="smartLinkDropList" onChange="JavaScript:SmartLinkDropList()">	
  <option value="">
    -- Select a subject --</option>		   
  <option value="&quot;Atmospheric science&quot;">
    Atmospheric science</option>
  <option value="biology">
    Biology</option>				   
</select>
</c:set>
<pre><c:out value="${command}" escapeXml="true"/></pre>



<h1><a name="browsers"></a>What browsers are supported?</h1>
<p> This service uses JavaScript in the web browser to  render the  search. </p>
<p>The following browsers are believed to be compatible with the service:  Internet Explorer 5.5 and above (Windows),   Internet Explorer 5.2 (Mac OS X), Firefox (all versions), Netscape 6 and above, Mozilla,  Opera 7 and above, Safari 1.0+ and Konqueror versions 3.01 and above.</p>
<p>The following browsers are believed NOT to be compatible with the service: Internet Explorer 5.0 and earlier, Konqueror 2.x and earlier, and Opera 6.x and earlier.</p>
<h1><a name="tips"></a>Tips for developers</h1>
	<p>As you edit and develop your custom search pages, the following tips may be helpful: </p>
	<ul>
	  <li>You can see how the the dynamic content is displayed in your page by opening and viewing your HTML file in your web browser. As you make changes, you should perform some searches in your page to see how the search results and other elements are displayed. </li>
	  <li>As you edit  the commands in your page, you will need to 'force' the browser to refresh itself in order to see the changes. This can be done by clicking the 'reload' button while holding the &lt;shift&gt; key or pressing the &lt;F5&gt; key on Windows. In some cases you may need to close all windows of your browser and then re-open the page to view your changes.</li>
	  <li>Issues with Internet Explorer: On some systems, you may receive a warning from Internet Explorer that 'active content' has been restricted. If this happens you will need to 'Click for more options' and choose 'Allow Blocked Content.' In addition, the browser's back and forward buttons do not work properly when viewing a local file from your computer. Note that these issues <em>do not</em> occur when the page is deployed and accessed from the Internet - the problems only occur when viewing a local file from your computer.</li>
	  <li>Be sure to use the JavaScript debugging features of your browser. If you see a JavaScript error, check to make sure you have spelled the command properly. Read your browser's JavaScript errors for help with debugging. </li>
	  <li>All of the text returned by the service resides in a table. To control the way fonts are displayed, assign CSS to the &lt;td&gt; element, for example:
	    <pre>TD { font-family: Arial, sans-serif; font-size: 10pt; }</pre>
	  </li>
	  <li>Each of the table HTML elements (&lt;table&gt;, &lt;tr&gt;, &lt;td&gt;) returned by the service are assigned the CSS class 'stdTable' . To control the way fonts and other elements are are displayed in the service response independently from the way your own HTML is displayed, use the 'stdTable' class specifier in your CSS, for example:
	    <pre>TD.stdTable { font-family: Arial, sans-serif; font-size: 10pt; }</pre>
	  </li>	  
	  <li>As you develop your page, you may find it useful to view the <a href="view_suggested_styles.jsp">suggested CSS styles</a> and the <a href="view_required_styles.jsp">required CSS styles</a> that are used by the service. </li>
      <li>The JavaScript and CSS that is required to use this service are relatively simple to use, even if you are not familiar with these two web standards. In many cases, the <a href="../examples/jshtml1-1/index.html" target="_blank">templates and examples</a> will provide all you need to be able to create and customize your own web page. If you would like to learn more about these standards, however, we recommend the W3C <a href="http://www.w3schools.com/js/" target="_blank">JavaScript Tutorial</a> or <a href="http://www.w3schools.com/css/" target="_blank">CSS Tutorial</a>. There are also many other resources available on the Internet or in print.</li>
  </ul>

  
	<h1>How the service works </h1>
	<p>The dynamic content is delivered to the web page through JavaScript using an architecture that is similar to a Representation State Transfer (REST) style web service. In REST style web service architectures, requests are typically encoded as a URL and responses are returned as XML. In the JSHTML architecture, requests are encoded as a URL and the response is returned to the web page as HTML wrapped inside a JavaScript write( ) statement. Developers use the suite of JavaScript commands to control the dynamic features of the page; the commands do the work of constructing the necessary http requests and inserting the JavaScript response into the  page. CSS may  then be applied to the page to customize the fonts, colors and sizes of the HTML elements that are returned by the service.</p>

	<%-- <h3>Contact us </h3>
	<p>If you have questions or comments regarding this service, please send e-mail to <a href="mailto:support@dlese.org">support@dlese.org</a>.</p>
	--%>
	<p>
		<i>
			Author: John Weatherley
			  &lt;<script type='text/javascript'>
			  <!-- 
			  	dlese_rea13( 'wjrngure', 'hpne.rqh' ); 
			// -->
			  </script>&gt;<br>
			Last revised $Date: 2012/09/26 22:21:18 $
		</i>
	</p>
	


<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
  
</body>
</html>


