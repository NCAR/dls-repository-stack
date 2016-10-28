<%--
	This template provides a functional, customizable search page that searches for 
	educational resources in the Digital Library for Earth System Education (DLESE).

	This page may be inserted into a static HTML as JavaScript.		
	
	Derived from JSP client version 2.2.	   
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

<%-- The number or search results to return per page --%>
<c:set var="resultsPerPage" value="10" />
<c:catch>
	<c:if test="${not empty param.numresults && param.numresults > 0 && param.numresults <= 50}">
		<c:set var="resultsPerPage" value="${param.numresults}" />
	</c:if>
</c:catch>


<%-- The name for this client, for example 'mySiteClient', used for logging user queries 
		attibuted with this client. Please change this from the default value 
		(ddswsTemplateAtDPC) or remove or leave blank to specify none. --%>
<c:set var="clientName" value="jsSearchTpl"/>

<%-- The color used to highlight the text that the user entered to perform the search. This may be a
		color name, or color value in HEX or RGB. For example "blue", "#0000FF", or "rgb(0,0,255)" --%>
<c:set var="keywordHighlightColor" value="#000066" />

<%-- The maximum character length of the description that gets displayed in the initial search results. 
		A smaller value here will make the search results page more compact. Leave blank to 
		display the full description text regardles off its length --%>
<c:set var="maxDescriptionLength" value="400" />

<%-- The maximum character length of the URL that gets displayed in the UI. A smaller
		value here will prevent problems with HTML wrapping --%>
<c:set var="maxUrlLength" value="80" />

<%-- The URL to the redirect server that logs when users click on a resource link. 
		Delete or leave blank to use none. --%>
<c:set var="redirectBaseUrl" value="http://rd.dlese.org"/>

<%-- The URL to the directory where this js proxy resides for linking images, etc. --%>
<c:set var="thisDirUrl" value="${f:contextUrl(pageContext.request)}/services/jshtml1-0/tpl"/>

<%-- 
	(optional) Define a contextualized domain (e.g. 'virtual collection') for this page:
	
	The variable 'searchBoostingAndConstraints' is used to designate term/field boosting and constraints 
	to be applied to all searches and SmartLinks in this page.
	
	The clauses defined below will be ANDed with the query entered by the user to produce the final
	query sent to the search engine. Values may contain boolean logic (using AND, OR), boosting (using
	a carrot followed by a number, e.g. ^3) and parentheses may be used for grouping. 
	Leave blank to apply no constraints or boosting. Refer to the following for details about the
	available search fields and query syntax:
	http://www.dlese.org/dds/services/ddsws1-0/service_specification.html#availableSearchFields
	
	Examples:
		Restrict to a specific domain:
		<c:set var='searchBoostingAndConstraints' value='url:http*noaa.gov*' />		
	
		Restrict to ocean subjects:
		<c:set var='searchBoostingAndConstraints' value='su:02 OR su:04 OR su:0p' />
			or alternatively
		<c:set var='searchBoostingAndConstraints' value='subject:ocean*' />		
		
		Restrict to ocean subjects and boost results that contain florida in the title, placeNames 
		or elsewhere (uses ky:0* to select the set of all records):
		<c:set var='searchBoostingAndConstraints' value='(su:02 OR su:04 OR su:0p) AND (ky:0* OR title:florida*^20 OR florida*^180 OR placeNames:florida*^20)' />
		
		Boost all results with colorado in the title, placeNames or elsewhere (uses ky:0* to select the set of all records):
		<c:set var='searchBoostingAndConstraints' value='(ky:0* OR title:colorado*^40 OR placeNames:colorado^40 OR colorado*^40)' />				

		Restrict to subject atmospheric science and boost results that contain atmosphere terms in the stemmed title field:
		<c:set var='searchBoostingAndConstraints' value='su:01 AND (ky:0* OR titlestems:${f:stem("atmosphere")}^2)' />			
--%>
<c:set var='searchBoostingAndConstraints' value='${param.sbc}' />		


<%-- ------- END VARIABLES EDIT ------- --%>


<%-- Include web_service_connection.jsp, which handles connecting with the Web service to 
		perform searches and get library vocabularies (grade levels, resource types etc.)
		and sets up a number of variables that are used throughout this page. --%>
<%@ include file="web_service_connection.jsp" %> 

<%-- ----- Show/hide header ----- --%>
<c:if test="${param.showhdr != 'false'}">

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
		<%-- 'Smart link' title --%>
		<c:when test="${not empty param.d && param.allrecs != 'n'}">		
			<title>${param.d}</title>
		</c:when>		
		<%-- Generic page title --%>
		<c:otherwise>	
			<title>Search the Digital Library for Earth System Education (DLESE)</title>
		</c:otherwise>
	</c:choose>
	
	<%-- The CSS styles that format the colors, fonts and look-and-feel of this page --%>
	<link rel='stylesheet' type='text/css' href='${thisDirUrl}/styles.css'>

</head>

<%-- 	When the page loads, call the JavaScript function checkSelectedItems(),
		which selects the items in the checkbox menus that the user previously
		selected  --%>
<body>

<p>
<%-- Display a heading for the search results --%>
<table width="100%" bgcolor="#777777" height="40" cellpadding="0" cellspacing="1" border="0" class="resultSectionHeading">
	<tr bgcolor='#aaaadd' valign="center">
		<td>
			<table width="100%" cellpadding=0 cellspacing=0>
				<tr>
					<td>
						<div class="resultSectionHeading">Search the Digital Library for Earth System Education (DLESE)</div>
					</td>
					<c:if test="${param.showlogo != 'false'}">
						<td align="right">
							<div style="margin-right:8px; margin-left:10px">
								<a href="http://www.dlese.org/" title="Digital Library for Earth System Education" target="_blank"><img alt="Digital Library for Earth System Education" src="${thisDirUrl}/images/dleselogo.jpg" align="middle" border="0"></a>
							</div>
						</td>
					</c:if>
				</tr>
			</table>
		</td>
	</tr>
</table>  
</p>

<%-- ----- End show/hide header ----- --%>
</c:if>


<%-- Include smart_links.jsp, which creates the HTML for the smart links that are defined
		in smart_link_definitions.xml. --%>
<%-- <%@ include file="smart_links.jsp" %> --%>
<%-- Then display the smart links --%>
<%-- ${smartLinks} --%>

<%-- The form used to get the text and selections entered by the user (must be named 'searchForm') --%>
<c:if test="${param.showform != 'false'}">

	<c:if test="${param.showformtags != 'false'}">
	<form action="" name="searchForm" style="margin-top:8px; margin-bottom:4px;">
	</c:if>
	<table>
		<c:if test="${param.showtb != 'false'}">
		<tr>
			<td nowrap>
				<input size="40" type="text" name="q" value="<c:out value='${param.q}' escapeXml='true'/>">
				<input type="submit" value="Search">
				<br>
				<c:if test="${not empty param.qh && param.allrecs != 'n'}">
					<input type="hidden" name="qh" value="${param.qh}">
				</c:if>				
				<c:if test="${not empty param.d && param.allrecs != 'n'}">
					<input type="radio" id="do_all_recs" name="allrecs" value="y" checked/>
					<label for="do_all_recs">Search in <i>${param.d}</i></label><br>
					<input type="radio" id="do_all_recs" name="allrecs" value="n"/>
					<label for="do_all_recs">Search all resources</label>
					<c:if test="${not empty param.d}">
						<input type="hidden" name="d" value="${param.d}">
					</c:if>					
				</c:if>						
			</td>
		</tr>		
		</c:if>
		<tr>
			<td colspan="2">		
				<%-- 	Include user_selections.jsp, which creates the HTML for the selections 
							the user has made (grade levels, subjects etc.) --%>
				<%@ include file="user_selections.jsp" %>				
				<c:if test="${not empty userSelections}">
					<table id="yourSelections">
						<tr>
							<td colspan="2">
								<%-- Then display the selections, which are stored in variable 'userSelections' --%>
								<nobr>Your selections:</nobr> ${userSelections}
							</td>			
						</tr>
					</table>
				</c:if>				
			</td>
		</tr>		
		
		<tr>
			<td>			
				<%-- 	Include checkbox_menus.jsp, which creates the HTML for the checkbox menus. 
						Note that web_service_connection.jsp, which is included above, does the work of 
						contacting the Web service to get the values and labels used to generate the menus. --%>
				<%@ include file="checkbox_menus.jsp" %>
				<%-- Then choose which menus you want to display, which are stored in the following
						variables (comment-out or rearrange as desired) --%>
				<%-- ${smartLinkMenu} --%> <%-- 'smartLinkMenu' is customizable in the file smart_link_definitions.xml --%>
				<c:if test="${param.showgr != 'false'}">${gradeRangesCheckBoxMenuJsTpl}</c:if>
				<c:if test="${param.showsu != 'false'}">${subjectsCheckBoxMenuJsTpl}</c:if>
				<c:if test="${param.showre != 'false'}">${resourceTypesCheckBoxMenuJsTpl}</c:if>
				<c:if test="${param.showcs != 'false'}">${contentStandardsCheckBoxMenuJsTpl}</c:if>
				<c:if test="${param.showky != 'false'}">${collectionsCheckBoxMenuJsTpl}</c:if>
				
				<%-- If the user has selected a smart link that does not appear in a menu,
						pass on it's value using hidden input --%>
				<c:forTokens var="paramName" items="${smartLinkOnlyParameterNames}" delims=" ">
					<c:if test="${not empty param[paramName]}">
						<input id="smartLinkSelection" type="hidden" name="${paramName}" value="${param[paramName]}">
					</c:if>
				</c:forTokens>
				
				<%--	The JavaScript function clearAllSelections() (below) is used to clear all selected 
						checkboxes in the form named 'searchForm' --%>
				<table><tr><td>
					<input type="button" value="Clear selections" class="clearbutton" onClick="JavaScript:clearAllSelections();"/>
				</td></tr></table>
				
			</td>
		</tr>

	</table>	
	<c:if test="${param.showformtags != 'false'}">
	</form>
	</c:if>
</c:if>

<%-- 	Display the search results, full description or collection description. Note that
		the page web_service_connection.jsp, which is included above, does the work of contacting
		the web service and returning the appropriate response, which is used below. --%> 
<c:choose>
	<%-- If the user entered some search input, display the search results --%>
	<c:when test="${displaySearchResults == 'true'}">
		<%-- Insert the search results --%>
		<%@ include file="search_results.jsp" %>		
	</c:when>
	
	<%-- Display the record full description if requested --%>
	<c:when test="${displayFullDescription == 'true'}">		
		<%-- Insert the full description --%>
		<%@ include file="full_description.jsp" %>	
	</c:when>	
	
	<%-- Display the collection description if requested --%>
	<c:when test="${displayCollectionDescription == 'true'}">		
		<%-- Insert the collection description --%>
		<%@ include file="collection_description.jsp" %>	
	</c:when>
	
	<%-- If the user has hit submit but did not define a search, send a message --%>
	<c:when test="${ param.q != null && empty param.q && empty param.qh }"> 
		<div style="padding-top:15px">
			You did not define a search. Please enter some text into the box
			or select one or more items from the menus above.
		</div>
	</c:when>	
</c:choose>

<%-- Display a thin blue line --%>
<table width="100%" height="6px" style="margin-top:20px" bgcolor="#777777" cellpadding="0" cellspacing="1" border="0"  class="resultSectionHeading">
	<tr bgcolor='#aaaadd' valign="center" >
		<td width="8000px">
		</td>
	</tr>
</table> 

<%-- The DLESE logo... --%>
<c:if test="${param.showlogo != 'false'}">
	<table align="center" border="0" cellpadding="3" cellspacing="0" style="margin-top:15px">
		<tr valign="middle" align="center">
			<td valign="middle">
				<a href="http://www.dlese.org/" title="Digital Library for Earth System Education" target="_blank"><img alt="Digital Library for Earth System Education" src="${thisDirUrl}/images/dleselogo.jpg" align="middle" border="0"></a>
				<div style="font-size: 8pt"> 
					<nobr>					
						<a href="http://www.dlese.org/" style="text-decoration: none; font-size: 8pt; color:000000" 
							target="_blank">Resource discovery provided by the<br>Digital Library for Earth System Education</a>
					</nobr>
				</div>
			</td>
		</tr>
	</table>
</c:if>


<script>
	<!--
	try{
		document.searchForm.q.focus(); 
		checkSelectedItems();
	} catch (e) {}	
	-->
</script>

<%-- ----- Show/hide header ----- --%>
<c:if test="${param.showhdr != 'false'}">

<br><br><br><br>

</body>
</html>

</c:if>
