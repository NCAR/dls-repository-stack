<%--
	This template provides a functional, customizable search page that searches for 
	educational resources in the Digital Library for Earth System Education (DLESE).		
	
	How to use this template:
	See the README.txt file included in the templates directory for detailed 
	instructions.
	
	These template files and installation instructions are available for download at:
	http://sourceforge.net/project/showfiles.php?group_id=23991&package_id=123037
	
	Additional documentation about the Web service this template uses (ddsws) is at:
	@DDS_SERVER_BASE_URL@/services/

	Provided by the Digital Library for Earth System Education (DLESE)
	free and with no warranty. This template may be copied and modified.
	e-mail: support@dlese.org
	
	This is JSP client version @DDSWS_CLIENT_VERSION@.	   
--%>

<%-- Reference the JSP tag libraries that are used in these template pages --%>
<%@ page language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="dt" uri="http://www.dlese.org/dpc/dds/tags" %>
<%@ taglib prefix="f" uri="http://dls.ucar.edu/tags/dlsELFunctions" %>
<%@ page isELIgnored ="false" %>


<%-- ------- EDIT THE FOLLOWING VARIABLES AS DESIRED ------- --%>

<%-- Please edit the following clientName variable to provide a 
		unique name for your client, for example 'mySiteClient'. This is 
		not displayed in the page but is used to create a unique
		name space for the settings assigned in your page, and for logging. --%>
<c:set var="clientName" value="@DDSWS_CLIENT_NAME@"/>

<%-- To display the grade ranges, resources types and subjects associated
		with each resource in the search results, set the variable
		'showVocabs' to 'true'. Note that this may reduce the 
		responsiveness of your search page --%>
<c:set var="showVocabs" value="true" />

<%-- To display reviews, comments, teaching tips and other annotations
		 in the search results, set the variable 'showReviews' to 'true' --%>
<c:set var="showReviews" value="true" />

<%-- The number or search results to return per page --%>
<c:set var="resultsPerPage" value="5" />

<%-- The number of smart links to render per column before starting 
		another column --%>
<c:set var="numSmartLinksPerColumn" value="3"/>

<%-- The absolute URL to the directory in which this page resides. This is used to 
		create the URLs for the images and CSS. If you move this page to another 
		context, be sure to update the context portion of the path below! --%>
<c:set var="urlToMyDir" value="${f:contextUrl(pageContext.request)}/@DDSWS_TEMPLATE_PATH@"/>

<%-- The maximum character length of the URL that gets displayed in the UI. A smaller
		value here will prevent problems with HTML wrapping --%>
<c:set var="maxUrlLength" value="80" />

<%-- The maximum character length of the description that gets displayed in the initial search results. 
		A smaller value here will make the search results page more compact. Leave blank to 
		display the full description text regardless off its length --%>
<c:set var="maxDescriptionLength" value="400" />

<%-- The color used to highlight the text that the user entered to perform the search. This may be a
		color name, or color value in HEX or RGB. For example "blue", "#0000FF", or "rgb(0,0,255)" --%>
<c:set var="keywordHighlightColor" value="#444444" />

<%-- The URL to the redirect server that logs when users click on a resource link. 
		Delete or leave blank to use none. --%>
<c:set var="redirectBaseUrl" value="http://rd.dlese.org"/>

<%-- The base URL to the Web service. --%>
<c:set var="ddswsBaseUrl" value="@DDS_SERVER_BASE_URL@/services/ddsws1-1"/>

<%-- 
	(optional) Define a contextualized domain (e.g. 'virtual collection') for this page:
	
	The variable 'searchBoostingAndConstraints' is used to designate term/field boosting and constraints 
	to be applied to all searches and SmartLinks in this page.
	
	The clauses defined below will be ANDed with the query entered by the user to produce the final
	query sent to the search engine. Values may contain boolean logic (using AND, OR), boosting (using
	a carrot followed by a number, e.g. ^3) and parentheses may be used for grouping. 
	Leave blank to apply no constraints or boosting. Refer to the following for details about the
	available search fields and query syntax:
	@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#availableSearchFields
	
	Examples:
		Restrict to a specific domain:
		<c:set var='searchBoostingAndConstraints' value='url:http*noaa.gov*' />		
	
		Restrict to ocean subjects:
		<c:set var='searchBoostingAndConstraints' value='su:02 OR su:04 OR su:0p' />
			or alternatively
		<c:set var='searchBoostingAndConstraints' value='subject:ocean*' />		
		
		Restrict to ocean subjects and boost results that contain florida in the title, placeNames 
		or elsewhere (uses allrecords:true to select the set of all records):
		<c:set var='searchBoostingAndConstraints' value='(su:02 OR su:04 OR su:0p) AND (allrecords:true OR title:florida*^20 OR florida*^180 OR placeNames:florida*^20)' />
		
		Boost all results with colorado in the title, placeNames or elsewhere (uses allrecords:true to select the set of all records):
		<c:set var='searchBoostingAndConstraints' value='(allrecords:true OR title:colorado*^40 OR placeNames:colorado^40 OR colorado*^40)' />				

		Restrict to subject atmospheric science and boost results that contain 'atmosphere' in the stemmed title field:
		<c:set var='searchBoostingAndConstraints' value='su:01 AND (allrecords:true OR titlestems:(atmosphere) )' />			
--%>
<c:set var='searchBoostingAndConstraints' value='' />		


<%-- ------- END VARIABLES EDIT ------- --%>


<%-- Include web_service_connection.jsp, which handles connecting with the Web service to 
		perform searches and get library vocabularies (grade levels, resource types etc.)
		and sets up a number of variables that are used throughout this page. --%>
<%@ include file="includes/web_service_connection.jsp" %> 


<%-- 	Include user_selections.jsp, which creates the HTML for the selections 
			the user has made (grade levels, subjects, smart links, etc.),
			and stores them in the variables 'selectedSmartLink', 
			'userSelections', and 'titleUserSelections' used below --%>
<%@ include file="includes/user_selections.jsp" %>	 

<html>
<head>
	<c:choose>
		<%-- Full description page title --%>
		<c:when test="${displayFullDescription == 'true'}">		
			<title>
				Description of: 
				<x:out select="$getRecordXmlDom/DDSWebService/GetRecord/record/metadata/itemRecord/general/title"/>
			</title>
		</c:when>		
		<%-- Collection description page title --%>
		<c:when test="${displayCollectionDescription == 'true'}">		
			<title>
				Description of collection:
				<c:set var="collectionVocabEntry">
					<x:out select="$getRecordXmlDom/DDSWebService/GetRecord/record/metadata/collectionRecord/access/key"/>
				</c:set>			
				<x:out select="$collectionsXmlDom/DDSWebService/ListCollections/collections/collection[vocabEntry=$collectionVocabEntry]/renderingGuidelines/label"/>
			</title>
		</c:when>
		<%-- Search results title --%>
		<c:when test="${not empty param.q || not empty userSelections}">		
			<title>Find a resource &gt; 
				<c:choose>
					<c:when test="${not empty param.q && empty userSelections}">
						&quot;${param.q}&quot;
					</c:when>
					<c:when test="${not empty param.q && not empty userSelections}">
						&quot;${param.q}&quot; &gt; ${userSelections}
					</c:when>
					<c:otherwise>
						${userSelections}
					</c:otherwise>
				</c:choose>
			</title>
		</c:when>			
		<%-- 'Smart link' title --%>
		<c:when test="${not empty param.d || not empty selectedSmartLink}">		
			<title>Resources &gt; ${selectedSmartLink} ${param.d}</title>
		</c:when>		
		<%-- Generic page title --%>
		<c:otherwise>	
			<title>Search the Digital Library for Earth System Education (DLESE)</title>
		</c:otherwise>
	</c:choose>
	
	<%-- The CSS styles that format the colors, fonts and look-and-feel of this page --%>
	<link rel='stylesheet' type='text/css' href='${urlToMyDir}/styles.css'>

</head>

<%-- 	When the page loads, call the JavaScript function checkSelectedItems(),
		which selects the items in the checkbox menus that the user previously
		selected  --%>
<body class="dleseTmpl" onload="JavaScript:document.searchForm.q.focus(); checkSelectedItems();">

<%-- Display a heading for the search results --%>
<table class="banner" height="40" cellpadding="0" cellspacing="1" border="0">
	<tr class="banner" valign="center">
		<td>
			<table width="100%" cellpadding="0" cellspacing="0">
				<tr>
					<td class="banner">
						<div class="bannerTxt">Search the Digital Library for Earth System Education (DLESE)</div>
					</td>
					<td align="right">
						<div style="margin-right:8px; margin-left:10px">
							<a href="http://www.dlese.org/" title="Digital Library for Earth System Education" class="dleseTmpl" target="_blank"><img alt="Digital Library for Earth System Education" src="${urlToMyDir}/images/dleselogo.jpg" align="middle" border="0"></a>
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>  

<%-- Welcome and links to instructions --%>
<table class="dleseTmpl" border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr class="dleseTmpl"><td class="dleseTmpl">
			<div style="padding-left:5px; padding-top:5px; padding-bottom:10px">
				About this template:
				<ul class="dleseTmpl">
					<li class="dleseTmpl">You can download, install and modify this template 
					to <a href="INSTALLATION.html" class="dleseTmpl">create your own customized search page</a>.</li>		
					<li class="dleseTmpl">A <a href="static.html" class="dleseTmpl">JavaScript version</a> of this page may also be used to
					deploy your search page to a static HTML server.</li> 	
				</ul>
			</div>
	
			<%-- Display a gray line --%>
			<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0">
				<tr><td bgcolor="#999999" height="1"></td></tr>
			</table>
			
			<div style="padding-top:8px; padding-left:5px">
				Example smart links:	
			</div>
	</td></tr>
</table>

<%-- Include smart_links.jsp, which creates the HTML for the smart links that are defined
		in smart_link_definitions.xml. --%>
<%@ include file="includes/smart_links.jsp" %>
<%-- Then display the smart links --%>
${applicationScope.smartLinkResources[smartLinks]}

<%-- Display a thin blue line, half the length of the page --%>
<table class="banner" style="margin-top:10px; width:350px; height:4px" cellpadding="0" cellspacing="1" border="0">
	<tr class="banner" valign="center"><td class="banner"></td></tr>
</table> 

<%-- The form used to get the text and selections entered by the user (must be named 'searchForm') --%>
<form class="dleseTmpl" action="" name="searchForm" style="margin-top:14px; margin-bottom:4px;">
	<table class="dleseTmpl">
		<tr class="dleseTmpl">
			<td class="dleseTmpl" nowrap>
				<b>Enter your own keyword or topic:</b><br> 
				<input size="40" type="text" name="q" value="<c:out value='${param.q}' escapeXml='true'/>">
				<input type="submit" value="Search">
			</td>
		</tr>
		<tr class="dleseTmpl">
			<td class="dleseTmpl" colspan="2">					
				<c:if test="${not empty userSelections}">
					<table class="dleseTmpl" id="yourSelections">
						<tr class="dleseTmpl">
							<td class="dleseTmpl" colspan="2">
								<%-- Then display the selections, which are stored in variable 'userSelections' --%>
								<nobr>Your selections:</nobr> ${userSelections}
							</td>			
						</tr>
					</table>
				</c:if>				
			</td>
		</tr>		
		<c:set var="test" value="gradeRanges"/>
		<tr class="dleseTmpl">
			<td class="dleseTmpl">			
				<%-- 	Include checkbox_menus.jsp, which creates the HTML for the checkbox menus. 
						Note that web_service_connection.jsp, which is included above, does the work of 
						contacting the Web service to get the values and labels used to generate these menus. --%>
				<%@ include file="includes/checkbox_menus.jsp" %>
				
				<%-- Comment-out or rearrange the order of the following menus as desired --%>
				<%-- The following menus are defined in the file checkbox_menus.jsp --%>
				${applicationScope.checkBoxMenus[gradeRanges]}
				${applicationScope.checkBoxMenus[subjects]}
				${applicationScope.checkBoxMenus[resourceTypes]}
				
				<%-- The following two variables may be un-commented to display menus for
				content standards and library collections --%>
				<%-- ${applicationScope.checkBoxMenus[contentStandards]} --%>
				<%-- ${applicationScope.checkBoxMenus[collections]} --%>
				
				<%-- The 'smartMenus' is customizable using the file smart_link_definitions.xml --%>
				${applicationScope.checkBoxMenus[smartMenus]}				
				
				<%--	The JavaScript function clearAllSelections() (below) is used to clear all selected 
						checkboxes in the form named 'searchForm' --%>
				<table><tr><td>
					<input type="button" value="Clear selections" class="clearbutton" onClick="JavaScript:clearAllSelections();"/>
				</td></tr></table>
				
			</td>
		</tr>
	</table>	
</form>


<%-- 	Display the search results, full description or collection description. Note that
		the page web_service_connection.jsp, which is included above, does the work of contacting
		the web service and returning the appropriate response, which is used below. --%> 
<div style="padding-top:10px">
	<c:choose>
		<%-- If the user entered some search input, display the search results --%>
		<c:when test="${displaySearchResults == 'true'}">
			<%-- Insert the search results --%>
			<%@ include file="includes/search_results.jsp" %>		
		</c:when>
		
		<%-- Display the record full description if requested --%>
		<c:when test="${displayFullDescription == 'true'}">		
			<%-- Insert the full description --%>
			<%@ include file="includes/full_description.jsp" %>	
		</c:when>	
		
		<%-- Display the collection description if requested --%>
		<c:when test="${displayCollectionDescription == 'true'}">		
			<%-- Insert the collection description --%>
			<%@ include file="includes/collection_description.jsp" %>	
		</c:when>
		
		<%-- If the user has hit submit but did not define a search, send a message --%>
		<c:when test="${ param.q != null && empty param.q && empty param.qh }"> 
			<%-- Display a gray line --%>
			<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0">
				<tr><td bgcolor="#999999" height="1"></td></tr>
			</table>			
			
			<table class="dleseTmpl" border="0" cellspacing="0" cellpadding="0" width="100%" style="padding-top:20px">
				<tr class="dleseTmpl"><td class="dleseTmpl">
					You did not define a search. Please enter some text into the box
					or select one or more items from the menus above.
				</td></tr>
			</table>
		</c:when>	
	</c:choose>
</div>

<%-- Display a thin blue line --%>
<table height="6px" style="margin-top:20px" class="banner" cellpadding="0" cellspacing="1" border="0">
	<tr class="banner" valign="center"><td class="banner"></td></tr>
</table> 

<%-- The DLESE logo... --%>
<table align="center" border="0" cellpadding="3" cellspacing="0" style="margin-top:15px">
	<tr valign="middle" align="center">
		<td valign="middle">
			<a href="http://www.dlese.org/" title="Digital Library for Earth System Education" class="dleseTmpl" target="_blank"><img alt="Digital Library for Earth System Education" src="${urlToMyDir}/images/dleselogo.jpg" align="middle" border="0"></a>
			<div style="font-size: 8pt"> 
				<nobr>					
					<a href="http://www.dlese.org/" style="text-decoration:none; font-size:8pt; color:#000000" 
						target="_blank">Resource discovery provided by the<br>Digital Library for Earth System Education</a>
				</nobr>
			</div>
		</td>
	</tr>
</table>

<br><br><br><br>
</body>
</html>

