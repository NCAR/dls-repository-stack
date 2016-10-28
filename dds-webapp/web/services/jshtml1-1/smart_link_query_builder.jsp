<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="includes/TagLibIncludes.jsp" %>

<c:catch var="pageError">

<c:set var="clientName" value="jshtml1-1-query-builder"/>
<c:set var="HTMLClientName" value="jshtml1-1|smart_link_query_builder.jsp"/>

<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>

<%-- The absolute URL to the directory in which this page resides. This is used to 
		create the URLs for the images and CSS. If you move this page to another 
		context, be sure to update the context portion of the path below! --%>
<c:set var="urlToMyDir" value="${contextUrl}/services/jshtml1-1"/>

<%-- The URL to the redirect server that logs when users click on a resource link. 
		Delete or leave blank to use none. --%>
<c:set var="redirectBaseUrl" value="http://rd.dlese.org"/>

<%-- The base URL to the Web service. --%>
<c:set var="ddswsBaseUrl" value="${contextUrl}/services/ddsws1-0"/>

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

<%-- Create the SmartLink query and store it in variable 'query' --%>
<c:choose>
	<c:when test="${param.SLtype == 'hyperlink'}">
		<c:set var="quoteTxt">\"</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="quoteTxt">&quot;</c:set>
	</c:otherwise>
</c:choose>
<c:set var="textQuery">${fn:replace( fn:trim(param.q), "\"" , quoteTxt )}</c:set>
<c:set var="searchFields" value="${fn:split( 'gr su re cs ky', ' ')}"/>
<c:forEach items="${searchFields}" var="searchField">
	<c:if test="${not empty param[searchField]}">
	<c:set var="fieldsQuery">
	<c:if test="${not empty fieldsQuery}">${fieldsQuery} AND </c:if><c:forEach 
	items="${paramValues[searchField]}" var="pval" varStatus="i">${i.first ? '(' : ''}${searchField}:${pval}${i.last ? ')' : ' OR '}</c:forEach>
	</c:set>
	</c:if>
</c:forEach>
<c:choose>
	<c:when test="${not empty textQuery && not empty fieldsQuery}">
		<c:set var="query">(${textQuery}) AND (${fieldsQuery})</c:set>
	</c:when>
	<c:when test="${not empty textQuery}">
		<c:set var="query" value="${textQuery}"/>
	</c:when>
	<c:when test="${not empty fieldsQuery}">
		<c:set var="query" value="${fieldsQuery}"/>
	</c:when>
</c:choose>

<%-- See if there was an error in the input... --%>
<c:if test="${param.slname != null && (empty fn:trim(param.slname) || empty query)}">
	<c:set var="inputError" value="t"/>
</c:if>

<c:set var="title" value="JavaScript Search Service: SmartLink Builder Tool"/>

<html>
	<head>

		<c:if test="${not empty getRecordXmlDomError}">
			<!-- There was an XML parsing error: ${getRecordXmlDomError} -->	
		</c:if>
		
		<%-- Note: Title is set below using JavaScript --%>
		<title>${title}</title>

		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<META NAME="keywords" CONTENT="DLESE,digital library for earth system education">
		<META NAME="description" CONTENT="DLESE Discovery System JavaScript Service Portal">
		<META NAME="creator" CONTENT="John Weatherley">
		<META NAME="organization" CONTENT="DLESE Program Center">
		<META NAME="doctype" CONTENT="DLESE webpage">
		
	
		<%-- DLESE template header (CSS styles and JavaScript references) --%>
		<%@ include file="/nav/head.jsp" %>
		
		 

		<LINK REL="stylesheet" TYPE="text/css" HREF="${contextUrl}/services/jshtml1-1/documentation_styles.css">
		
		<link rel='stylesheet' type='text/css' href='${urlToMyDir}/suggested_styles.css'>
		<link rel='stylesheet' type='text/css' href='${urlToMyDir}/required_styles.css'>
		<style type="text/css">
			<!--
			/* Override the 'suggested' background color behind the resources's title */
			TR.resourceTitle {
				background-color:#E8ECF4;
			}
			.inputTitle {
				font-weight:bold;
				padding-top:4px;
				padding-bottom:2px;
			}	
			H3 {
				padding-left:2px;
			}				
			-->
		</style>		
	</head>
	
<body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066" onload="JavaScript:document.searchForm.slname.focus()">

<div style="position: absolute; top: 5px; right: 5px; text-align: center;">
	<a href="${pageContext.request.contextPath}/services/jshtml1-1/index.jsp">JavaScript Service<br/>Home</a>
</div>

<div style="padding:10px">


<h1>${title}</h1>

<%-- The form used to get the text and selections entered by the user (must be named 'searchForm') --%>
<form class="dleseTmpl" action="" name="searchForm" style="margin-top:0px; margin-bottom:0px;">
	<table class="stdTable">
			<tr class="stdTable">
				<td class="stdTable" style="padding-bottom:1px">
					<c:if test="${empty userSelections}">
						<div style="padding-bottom:4px">
					</c:if>
					
					<br>
					
					This tool can be used to create a custom <a href="javascript_service_documentation.jsp#SmartLinkCommands">SmartLink</a> for your
					web page. A SmartLink is a hyperlink or drop menu that returns a set of  resources when clicked or selected.
					See this <a href="${contextUrl}/services/examples/jshtml1-1/full_example.html" target="_blank">example page</a>, which illustrates a SmartLink drop menu and
					SmartLink hyperlinks at the top of the page.
					
					<c:if test="${not empty inputError}">
						<div class="errorTxt" style="font-weight:bold;padding-top:8px;">
							Note: There was an error in your input - please see messages below.
						</div>
					</c:if>
					
					<h3>Step 1 - Choose the type of SmartLink you would like:</h3>
					
					
					<input type="radio" id="hyperlinkSL" name="SLtype" value="hyperlink" ${(empty param.SLtype || param.SLtype == 'hyperlink') ? 'checked' : ''}/>
					<label for="hyperlinkSL">SmartLink - A hyperlink that displays a set of search results when clicked</label> (see <a href="javascript_service_documentation.jsp#SmartLink">SmartLink description</a>)<br>
					<input type="radio" id="selectSL" name="SLtype" value="select" ${param.SLtype == 'select' ? 'checked' : ''}/>
					<label for="selectSL">SmartLinkDropList - A drop list that contains menu selections that display a set of search results when selected</label>
					(see <a href="javascript_service_documentation.jsp#SmartLinkDropList">SmartLinkDropList description</a>)					
					
					<h3>Step 2 - Choose a label for your SmartLink:</h3>
					<c:if test="${param.slname != null && fn:length(fn:trim(param.slname)) == 0}">
						<div class="errorTxt" style="padding-bottom:2px">
							* Please input the label you would like for your SmartLink:
						</div>
					</c:if>					
					<input size="40" type="text" name="slname" value="<c:out value='${param.slname}' escapeXml='true'/>">

					<h3>Step 3 - Choose the search terms and/or classifiers to map to your SmartLink:</h3>
					<c:if test="${param.q != null && fn:length(fn:trim(query)) == 0}">
						<div class="errorTxt">
							* Please input terms, classifiers or a combination of
							both terms and classifiers to map to your SmartLink:
						</div>
					</c:if>						
					<div class="inputTitle">Search terms mapped to your SmartLink (optional):</div>
					<input size="40" type="text" name="q" value="<c:out value='${param.q}' escapeXml='true'/>">				
						
					<c:if test="${empty userSelections}">
						</div>
					</c:if>

				</td>
			</tr>

		<tr class="stdTable">
			<td class="stdTable" id="yourSelections" style="${empty userSelections ? 'padding-top:0;padding-bottom:0;' : 'padding-top:2;padding-bottom:2;'}">					
				<div class="inputTitle">Classifiers mapped to your SmartLink (optional):</div>
				<span style="${empty userSelections ? 'display:none;' : ''}">
				<nobr>Your selections:</nobr> ${userSelections}
				</span>
			</td>
		</tr>		

		<tr class="stdTable">
			<td class="stdTable" style="padding-top:1px">			
				<%-- 	Include checkbox_menus.jsp, which creates the HTML for the checkbox menus. 
						Note that web_service_connection.jsp, which is included above, does the work of 
						contacting the Web service to get the values and labels used to generate these menus. --%>
				<%@ include file="includes/checkbox_menus.jsp" %>
				${applicationScope.checkBoxMenus[gradeRanges]}
				${applicationScope.checkBoxMenus[subjects]}
				${applicationScope.checkBoxMenus[resourceTypes]}
				${applicationScope.checkBoxMenus[contentStandards]}
				${applicationScope.checkBoxMenus[collections]}		
				
				<%--	The JavaScript function clearAllSelections() (below) is used to clear all selected 
						checkboxes in the form named 'searchForm' --%>
				<table><tr><td>
					<input type="button" value="Clear selections" class="clearbutton" onClick="JavaScript:clearAllSelections();"/>
				</td></tr></table>
				
				<h3>Step 4 - Create your SmartLink:</h3>
				<p>
				Click the botton below to create or regenerate your SmartLink. Your SmartLink code will be
				displayed below in this page:
				</p>
				
				<c:if test="${not empty inputError}">
					<p class="errorTxt">
						Note: There was an error in your input - please fix the errors above and try again.
					</p>
				</c:if>	
				
				<p>
					<input type="submit" value="Create SmartLink">
				</p>	
				
			</td>
		</tr>
	</table>	
</form>


<c:if test="${param.slname != null && empty inputError}">

<c:choose>
	<c:when test="${param.SLtype == 'hyperlink'}">
<c:set var="smartLinkCode">
<a href='JavaScript:SmartLink("${query}","${fn:trim(param.slname)}")'>${fn:trim(param.slname)}</a>
</c:set>
<c:set var="smartLinkDisplay">
<pre class="preBold">
<c:out value="${smartLinkCode}" escapeXml="true"/>
</pre>
</c:set>
	</c:when>
	<c:otherwise>
<c:set var="smartLinkCode">
<select id="smartLinkDropList" onchange="JavaScript:SmartLinkDropList()">
  <option value="">-- Select a category --</option>		   
  <option value="${query}">${fn:trim(param.slname)}</option>
</select>
</c:set>
<c:set var="smartLinkDisplay">
<pre class="preLt">
&lt;select id=&quot;smartLinkDropList&quot; onchange=&quot;JavaScript:SmartLinkDropList()&quot;&gt;
  &lt;option value=&quot;&quot;&gt;-- Select a category --&lt;/option&gt;
</pre>		   
<pre class="preBold">
  &lt;option value=&quot;${query}&quot;&gt;${fn:trim(param.slname)}&lt;/option&gt;
</pre>
<pre class="preLt">
&lt;/select&gt;
</pre>
</c:set>
	</c:otherwise>
</c:choose>
<c:set var="examplePageCode">
<div style="margin:10px;margin-left:4px;">
Try your SmartLink:<br><br>
${smartLinkCode}
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

<table class="stdTable">
		<tr class="stdTable">
			<td class="stdTable" style="padding-bottom:1px">
			
			<h3></a>Step 5 - Verify your SmartLink:</h3>

			<c:url value="command_viewer.jsp" var="viewerUrl">
				<c:param name="command" value='${fn:replace(examplePageCode,"\'","%27")}'/>
				<c:param name="anchor" value="simplePage"/>
			</c:url>
			
			<p><a href="${viewerUrl}" target="_blank">Click here to view a sample page that contains your SmartLink</a>.
			Once the sample page opens, ${param.SLtype == 'hyperlink' ? 'click' : 'select'} your SmartLink
			to see the resources it will return. Tip: you can also scroll down in this page
			to see the resources it will return.</p>
			
			<h3><a name="cp"></a>Step 6 - Copy and paste the SmartLink code into your web page:</h3>


	<table border="0" cellspacing="0" cellpadding="4" style="margin-left:5px; margin-bottom:4px">
	   <tr>
		<td>
			<c:choose>
				<c:when test="${param.SLtype == 'hyperlink'}">
					Simply copy and paste this hyperlink &lt;a&gt; tag into your page
					at the place you would like it to go.
				</c:when>
				<c:otherwise>
					Do one of the following:<br><br>
					
					1. To create a new SmartLinkDropList in your page, copy and
					paste the full code below into your page at the place you would like
					it to go. The drop menu will have
					a single SmartLink in it, which you can add to later by creating additional
					SmartLinkDropList items using this tool. Note that you may only have ONE SmartLinkDropList 
					in a given page.<br><br>
					
					2. If you already have a SmartLinkDropList in your page, copy and paste only the bold section 
					of code into your existing drop list to add additional items to it.<br><br>
					
				</c:otherwise>
			</c:choose>
		</td>
	  </tr>	

	  <tr>
		<td>
	
			<table class="codeTB">
				<tr class="codeTB">
				<td class="codeTB">
${smartLinkDisplay}
				</td>
				</tr>
			</table>
			

			
		</td>
		</tr>
	   <tr>
		<td>
			Note that the following minimum serivce code must also appear in your page, 
			if it does not already:
		</td>
	  </tr>	
	  <tr>
	  	<td>
		
			<table class="codeTB">
				<tr class="codeTB">
				<td class="codeTB">
<pre class="preLt">
&lt;SCRIPT TYPE=&quot;text/javascript&quot;
   SRC=&quot;${contextUrl}/services/jshtml1-1/javascript_search_service.js&quot;&gt;&lt;/SCRIPT&gt;
&lt;SCRIPT TYPE=&quot;text/javascript&quot;&gt;
 &lt;!--	
   // Comment out the following command to remove the search box from your page
   ShowElement("searchBox");
   
   // Comment out the following command to remove the 'search within the SmartLink results' option
   ShowElement("searchWithinSmartLinkResults");   
   
   // The following command is required
   RenderPage();
 --&gt;
&lt;/SCRIPT&gt;
</pre>
				</td>
				</tr>			
			</table>
			
		</td>		
	  </tr> 
	</table>


		</td>
	</tr>
</table>

</c:if>

<%-- 	Display the search results, full description or collection description. Note that
		the page web_service_connection.jsp, which is included above, does the work of contacting
		the web service and returning the appropriate response, which is used below. --%> 

<c:if test="${param.q != null}">		
<table class="stdTable">
		<tr class="stdTable">
			<td class="stdTable" style="padding-bottom:1px">		
			
			<h3>Your SmartLink will return the following resources when ${param.SLtype == 'hyperlink' ? 'clicked' : 'selected'}:</h3>

		</td>
	</tr>
</table>	
</c:if>		
			
<SCRIPT TYPE="text/javascript" SRC="@DDS_SERVER_BASE_URL@/services/jshtml1-1/javascript_search_service.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
	<!--	
	
	ShowElement("gradeLevelSR");
	ShowElement("resourceTypeSR");
	ShowElement("subjectSR");	
	ShowElement("collectionSR");
	ShowElement("accessionDateSR");	

	HideElement("titleRewrite");
		
	RenderPage();	
	
	-->
</SCRIPT>

<!-- Provide a message if JavaScript is disabled in the user's browser -->
<NOSCRIPT>
	This page requires that JavaScript be turned on. Please activate JavaScript in your browser.
</NOSCRIPT> 

<br><br>
<%-- Display a thin line --%>
${thinLine}	
	

</c:catch>
<c:if test="${not empty pageError}">
	
<div style="padding:10px;">
Sorry, our system encountered an internal error.<br><br>
Please send questions or comments to <a href="mailto:support@dlese.org">support@dlese.org</a>
</div>

<!-- 
Error: ${pageError}
-->
</c:if>



<%-- Include bottom html --%>
<%-- <%@ include file="/nav/bottom.jsp" %>    --%> 
  
</div>   
</body>
</html>
