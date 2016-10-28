<%@ include file="includes/TagLibIncludes.jsp" %> 

<%-- 	
	Note: all of this stuff at the top acts as a controller and 
	can/should be put into the ActionServlet 
--%>

<c:catch var="pageError">


<%-- Define the parameters we don't want to propogate through the pager and other links (e.g. used in the protocol): --%>
<% pageContext.setAttribute("ignoreParams",new java.util.HashMap(25)); %>
<c:set target="${ignoreParams}" property="fullDescription" value=""/>
<c:set target="${ignoreParams}" property="collectionDescription" value=""/>
<c:set target="${ignoreParams}" property="s" value=""/>
<c:set target="${ignoreParams}" property="n" value=""/>
<c:set target="${ignoreParams}" property="rt" value=""/>
<c:set target="${ignoreParams}" property="sh" value=""/>
<c:set target="${ignoreParams}" property="hd" value=""/>
<c:set target="${ignoreParams}" property="cq" value=""/>
<c:set target="${ignoreParams}" property="bq" value=""/>
<c:set target="${ignoreParams}" property="se" value=""/>
<c:set target="${ignoreParams}" property="client" value=""/>
<c:set target="${ignoreParams}" property="mll" value=""/>
<c:set target="${ignoreParams}" property="mdl" value=""/>
<c:set target="${ignoreParams}" property="menu" value=""/>
<c:set target="${ignoreParams}" property="fmac" value=""/>
<c:set target="${ignoreParams}" property="sortBy" value=""/>
<c:if test="${not empty param.deq}"> 
	<c:set target="${ignoreParams}" property="d" value=""/>
	<c:set target="${ignoreParams}" property="qh" value=""/>
	<c:set target="${ignoreParams}" property="deq" value=""/>
</c:if>

<%-- Set up which elements to show --%>
<c:if test="${not empty paramValues.sh}">
	<% pageContext.setAttribute("show",new java.util.HashMap()); %>
	<c:forEach items="${paramValues.sh}" var="val">
		<c:set target="${show}" property="${val}" value=""/>
		<c:if test="${fn:contains(val,'Menu')}">
			<c:set var="hasMenu" value="t"/>
		</c:if>
	</c:forEach>
	<%-- Configure element groups: --%>
	<c:if test="${show.allMenus != null}">
		<c:set target="${show}" property="gradeLevelsMenu" value=""/>
		<c:set target="${show}" property="resourceTypesMenu" value=""/>
		<c:set target="${show}" property="collectionsMenu" value=""/>
		<c:set target="${show}" property="contentStandardsMenu" value=""/>
		<c:set target="${show}" property="subjectsMenu" value=""/>
	</c:if>
</c:if>
<c:if test="${not empty param.menu}"><c:set var="hasMenu" value="t"/></c:if>

<%-- Set up which elements to hide --%>
<c:if test="${not empty paramValues.hd}">
	<% pageContext.setAttribute("hide",new java.util.HashMap()); %>
	<c:forEach items="${paramValues.hd}" var="val">
		<c:set target="${hide}" property="${val}" value=""/>
	</c:forEach>
</c:if>

<c:set var="clientName" value="jshtml1-1"/>
<c:set var="HTMLClientName" value="jshtml1-1|${param.client}"/>

<%-- The number or search results to return per page --%>
<c:set var="resultsPerPage" value="${jsformv11.n}" />

<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>

<%-- The absolute URL to the directory in which this page resides. This is used to 
		create the URLs for the images and CSS. If you move this page to another 
		context, be sure to update the context portion of the path below! --%>
<c:set var="urlToMyDir" value="${contextUrl}/services/jshtml1-1"/>

<%-- The maximum character length of the URL that gets displayed in the UI. A smaller
		value here will prevent problems with HTML wrapping --%>
<c:set var="maxUrlLength" value="${empty param.mll ? '80' : param.mll}" />

<%-- The maximum character length of the description that gets displayed in the initial search results. 
		A smaller value here will make the search results page more compact. Leave blank to 
		display the full description text regardless off its length --%>
<c:set var="maxDescriptionLength" value="${empty param.mdl ? '400' : param.mdl}" />

<%-- The CSS class used to highlight the text that the user entered to perform the search. --%>
<c:set var="keywordHighlightClass" value="textHighlight" />

<%-- The URL to the redirect server that logs when users click on a resource link. 
		Delete or leave blank to use none. --%>
<c:set var="redirectBaseUrl" value="http://rd.dlese.org"/>

<%-- The base URL to the Web service. --%>
<c:set var="ddswsBaseUrl" value="${contextUrl}/services/ddsws1-0"/>

<%-- The URL to the annotations submission form --%>
<c:set var="annoSumbissionUrl" value="${applicationScope.ddsConfigProperties['annotation.submission.url']}"/>	


<%-- ------- END VARIABLES EDIT ------- --%>


<%-- Include web_service_connection.jsp, which handles connecting with the Web service to 
		perform searches and get library vocabularies (grade levels, resource types etc.)
		and sets up a number of variables that are used throughout this page. --%>
<%@ include file="includes/web_service_connection.jsp" %> 

<%-- 	Include user_selections.jsp, which creates the HTML for the selections 
			the user has made (grade levels, subjects, smart links, etc.),
			and stores them in the variables
			'userSelections', and 'titleUserSelections' used below --%>
<%@ include file="includes/user_selections.jsp" %>	 

<%-- Define a thin line element for use throughout the pages --%>		
<c:set var="thinLine">
	<table class="thinLine">
		<tr class="thinLine"><td class="thinLine"></td></tr>
	</table>
</c:set>

<%-- Only display the HTML, HEAD and BODY tags if this is not being output as JavaScript... --%>
<c:choose>
<c:when test="${param.rt != null}">
	<html>
		<head>

			<c:if test="${not empty getRecordXmlDomError}">
				<!-- There was an XML parsing error: ${getRecordXmlDomError} -->	
			</c:if>
			
			<%-- Note: Title is set below using JavaScript --%>
			<title>Search DLESE</title>
			
			<%-- The CSS styles that format the colors, fonts and look-and-feel of this page --%>
			<%-- <link rel='stylesheet' type='text/css' href='${urlToMyDir}/suggested_styles.css'> --%>
			<link rel='stylesheet' type='text/css' href='${urlToMyDir}/required_styles.css'>
			
		</head>
		
		<%-- 	When the page loads, call the JavaScript function checkSelectedItems(),
				which selects the items in the checkbox menus that the user previously
				selected  --%>
		<body>
</c:when>
<c:otherwise>
	<link rel='stylesheet' type='text/css' href='${urlToMyDir}/required_styles.css'>
</c:otherwise>
</c:choose>

<%-- The form used to get the text and selections entered by the user (must be named 'searchForm') --%>
<c:if test="${ (show.searchBox != null || hasMenu != null) }">
	<form class="dleseTmpl" action="${param.fmac}" name="searchForm" style="margin-top:0px; margin-bottom:0px;">
		<table class="stdTable">
			<c:if test="${show.searchBox != null || hasMenu != null}">
				<tr class="stdTable">
					<td class="stdTable" style="padding-bottom:1px" nowrap>
						<c:if test="${empty userSelections}">
							<div style="padding-bottom:4px">
						</c:if>
						<c:choose>
							<c:when test="${show.searchBox != null}">
								<input size="40" type="text" name="q" value="<c:out value='${param.q}' escapeXml='true'/>">
							</c:when>
							<c:otherwise>
								<input type="hidden" name="menuOnly" value="t">
							</c:otherwise>
						</c:choose>
						<input type="submit" value="Search">
						
						<%-- Param used to select the option in a smart link select list --%>
						<c:if test="${not empty param.mySelection}">
							<input type="hidden" name="mySelection" value="${param.mySelection}">	
						</c:if>
						
						<%-- Search within SmartLink results --%>
						<c:if test="${show.searchWithinSmartLinkResults != null && not empty param.d && param.slc != 'f'}">
							<br/>
							<input type="radio" id="do_sl_recs" name="slc" value="t" checked/>
							<label for="do_sl_recs">Search in <i>${param.d}</i></label><br>
							<input type="radio" id="do_all_recs" name="slc" value="f"/>
							<label for="do_all_recs">Search all resources</label>
							<c:if test="${not empty param.d}">
								<input type="hidden" name="d" value="${param.d}">
							</c:if>
							<c:if test="${not empty param.qh}">
								<input type="hidden" name="qh" value="<c:out value='${param.qh}' escapeXml='true'/>">
							</c:if>					
						</c:if>
							
						<c:if test="${empty userSelections}">
							</div>
						</c:if>
					</td>
				</tr>
			</c:if>
			<tr class="stdTable">
				<td class="stdTable" id="yourSelections" style="${empty userSelections ? 'display:none;padding-top:0;padding-bottom:0;' : 'padding-top:2;padding-bottom:2;'}">					
					<nobr>Your selections:</nobr> ${userSelections}
				</td>
			</tr>		
	
			<c:set var="test" value="gradeRanges"/>
			<tr class="stdTable">
				<td class="stdTable" style="padding-top:1px">			
					<%-- 	Include checkbox_menus.jsp, which creates the HTML for the checkbox menus. 
							Note that web_service_connection.jsp, which is included above, does the work of 
							contacting the Web service to get the values and labels used to generate these menus. --%>
					<%@ include file="includes/checkbox_menus.jsp" %>
	
					<c:if test="${show.gradeLevelsMenu != null}">${applicationScope.checkBoxMenus[gradeRanges]}</c:if>
					<c:if test="${show.subjectsMenu != null}">${applicationScope.checkBoxMenus[subjects]}</c:if>
					<c:if test="${show.resourceTypesMenu != null}">${applicationScope.checkBoxMenus[resourceTypes]}</c:if>
					<c:if test="${show.contentStandardsMenu != null}">${applicationScope.checkBoxMenus[contentStandards]}</c:if>
					<c:if test="${show.collectionsMenu != null}">${applicationScope.checkBoxMenus[collections]}</c:if>
					${customMenus}
					
					<%-- The 'smartMenus' is customizable using the file smart_link_definitions.xml --%>
					<%-- ${applicationScope.checkBoxMenus[smartMenus]} --%>				
					
					<%--	The JavaScript function clearAllSelections() (below) is used to clear all selected 
							checkboxes in the form named 'searchForm' --%>
					<c:if test="${hasMenu != null}">
						<table><tr><td>
							<input type="button" value="Clear selections" class="clearbutton" onClick="JavaScript:clearAllSelections();"/>
						</td></tr></table>
					</c:if>
					
				</td>
			</tr>
		</table>	
	</form>
</c:if>


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
		<c:when test="${ param.q != null && empty param.q && (empty param.qh || not empty param.slc) }"> 
			<%-- Display a thin line --%>
			${thinLine}			
			
			<table class="stdTable" border="0" cellspacing="0" cellpadding="0" width="100%" style="padding-top:20px">
				<tr class="stdTable"><td class="stdTable">
					<div style="padding-top:5px; padding-bottom:15">
						You did not define a search.
						<c:choose>
							<c:when test="${show.searchBox != null && not empty hasMenu}">
								Please enter some text into the search box
								or select one or more items from the menus above.
							</c:when>
							<c:when test="${show.searchBox != null}">
								Please enter some text into the search box above.
							</c:when>
							<c:when test="${not empty hasMenu}">
								Please select one or more items above.
							</c:when>						
						</c:choose>
					</div>
				</td></tr>
			</table>
		</c:when>	
		<%-- If the user has hit submit and only menus are being displayed --%>
		<c:when test="${ param.menuOnly != null && !jsformv11.hasMenuItemSelected }"> 
			<%-- Display a thin line --%>
			${thinLine}			
			
			<table class="stdTable" border="0" cellspacing="0" cellpadding="0" width="100%" style="padding-top:20px">
				<tr class="stdTable"><td class="stdTable">
					<div style="padding-top:5px; padding-bottom:15">
						You did not define a search. Please select one or more items from the menu(s) above.
					</div>
				</td></tr>
			</table>
		</c:when>		
	</c:choose>
</div>

<%-- The DLESE logo... --%>
<c:if test="${hide.logo == null}">

	<%-- Display a thin line --%>
	${thinLine}	
	
	<table align="center" border="0" cellpadding="3" cellspacing="0" style="margin-top:15px">
		<tr valign="middle" align="center">
			<td valign="middle">
				<a href="http://www.dlese.org/" title="Digital Library for Earth System Education" class="dleseTmpl" target="_blank"><img alt="Digital Library for Earth System Education" src="${urlToMyDir}/images/dleselogo.jpg" align="middle" border="0"></a>
				<div class="logo"> 
					<nobr>					
						<a href="http://www.dlese.org/" class="logo" 
							target="_blank">Resource discovery provided by the<br/>Digital Library for Earth System Education</a>
					</nobr>
				</div>
			</td>
		</tr>
	</table>
</c:if>

<SCRIPT TYPE="text/javascript">
	<!--
	if( document.searchForm != null ){	
		if(document.searchForm.q != null )
			document.searchForm.q.focus(); 
		checkSelectedItems();
	}

	function setTitle() {
		<c:if test="${hide.titleRewrite == null}">
			<c:set var="pageTitle">
				<c:choose>
					<%-- Full description page title --%>
					<c:when test="${displayFullDescription == 'true'}">		
						Description of: 
						${empty jsformv11.docReader.title ? param.fullDescription : jsformv11.docReader.title}
					</c:when>		
					<%-- Collection description page title --%>
					<c:when test="${displayCollectionDescription == 'true'}">									
							Description of collection: 
							${empty jsformv11.docReader.title ? param.collectionDescription : jsformv11.docReader.title}
					</c:when>
					<%-- Search results title --%>
					<c:when test="${not empty param.q || not empty userSelections}">		
							Find a resource >
							<c:choose>
								<c:when test="${not empty param.q && empty userSelections}">
									"${param.q}"
									<c:if test="${not empty param.d && param.slc != 'f'}">
										> ${param.d}
									</c:if>			
								</c:when>
								<c:when test="${not empty param.q && not empty userSelections}">
									"${param.q}" 
									<c:if test="${not empty param.d && param.slc != 'f'}">
										> ${param.d}
									</c:if>									
									> ${userSelections}
								</c:when>
								<c:otherwise>
									<c:if test="${not empty param.d && param.slc != 'f'}">
										${param.d} ${empty userSelections ? '' : '>'}
									</c:if>								
									${userSelections}
								</c:otherwise>
							</c:choose>
					</c:when>			
					<%-- 'Smart link' title --%>
					<c:when test="${not empty param.d}">		
						Resources > ${param.d}
					</c:when>		
					<%-- Generic page title --%>
					<c:otherwise>	
						<c:set var="doNoTitle" value=""/>
					</c:otherwise>
				</c:choose>
			</c:set>
			
			<c:if test="${doNoTitle == null}"> 
				document.title = "${ f:jsEncode( f:replaceAll( pageTitle, '\\s+', ' ') )}";
			</c:if>
		</c:if>
	}
	setTitle();
	
	-->
</SCRIPT>

<c:if test="${param.rt != 'jswl'}">
</body>
</html>
</c:if>

</c:catch>
<c:if test="${not empty pageError || not empty jsformv11.errorMsg}">
	<c:if test="${param.se != 't'}">
		<c:set var="errorText">To fix the problem, please check the commands you've placed in your page. Here is more information about the error:</c:set>
		<%-- <p style="color:red;padding:15;">${errorText} <i>${pageError} ${jsformv11.errorMsg}</i></p> --%>
		<SCRIPT TYPE="text/javascript">
			<!-- 
				showError('${f:jsEncode(errorText)} ${f:jsEncode(pageError)} ${f:jsEncode(jsformv11.errorMsg)}');
			-->
		</SCRIPT>
	</c:if>
</c:if>

