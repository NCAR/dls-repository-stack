<%--
	Example JSP for displaying information using the DDSWebService (ddsws v1.1)
	See @DDS_SERVER_BASE_URL@/services/
	
	Provided by the Digital Library for Earth System Education (DLESE)
	free and with no warranty. This example may be copied and modified.
	e-mail: support@dlese.org

	These examples are available for download at 
	http://sourceforge.net/project/showfiles.php?group_id=23991&package_id=123037
	
	Requires Apache Tomcat 5 (http://jakarta.apache.org/tomcat/)
	or other JSP 2.0 compliant JSP container and the 
	JSTL v1.1 tag libraries, which are included in the examples download
	or can be obtained from http://java.sun.com (standard.jar and jstl.jar).	

	This example allows a user to perform a textual search for educational resources 
	in the library, select a grade range and subject to limit their search, view and 
	page through the results and display the full metadata for a single record. Shows 
	examples of using the UserSearch, GetRecord, ListGradeRanges, ListListResourceTypes 
	and ListSubjects  requests in a single page and implements caching of the grade range 
	and subject select lists for efficiency.	   
--%>

<%-- Reference the JSTL tag libraries that we may use --%>
<%@ page language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ page isELIgnored ="false" %>

<%-- Store the base URL to the service to use in the requests below --%>
<c:set var="ddswsBaseUrl" value="@DDS_SERVER_BASE_URL@/services/ddsws1-1"/>

<%-- Provide a way to re-load the grade ranges, resource types and subjects manually.
      This option is invisible to the user but an administrator can use it
	  to update the grade range and subject lists and resource type vocabularies
	  if necessary --%>
<c:if test="${param.reload == 'true'}">
	<%-- If indicated, delete the application scope variables
	     so they will be re-loaded below --%>
	<c:remove var="gradeRangeSelectListExamples3" scope="application"/>
	<c:remove var="subjectsSelectListExamples3" scope="application"/>
	<c:remove var="resourceTypesXmlDom" scope="application"/>	
</c:if>


<%-- Request and cache the grade ranges as an application scoped variable
        only if they have not been cached already --%>				
<c:if test="${empty gradeRangeSelectListExamples3}">
	<c:catch var="grError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListGradeRanges"/>
		</c:url>
		
		<%-- Perform the web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the grade ranges in an application scope XML DOM variable 'gradeRangesXmlDom' --%>				
		<x:parse var="gradeRangesXmlDom" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>	


		<%-- Create and store a select list of grade ranges from the ListGradeRanges XML response --%>
		<c:set var="gradeRangeSelectListExamples3" scope="application">
			<select name="gr">
			<option value=""> -- All grade ranges -- </option>
			<x:forEach select="$gradeRangesXmlDom/DDSWebService/ListGradeRanges/gradeRanges/gradeRange">
				<%-- Only display the grade range in the menu if it is not hidden --%>
				<x:if select="contains( renderingGuidelines/noDisplay, 'false' )">
					<%-- Set the selected grade range to the one chosen by the user --%>
					<c:set var="searchKey">
						<x:out select="searchKey"/> 
					</c:set>
					
					<%-- Some of the entries may be list headings with no search key --%>
					<c:if test="${not empty searchKey}">
						<option value='<x:out select="searchKey"/>'><x:out select="renderingGuidelines/label"/></option>
					</c:if>
				</x:if>
			</x:forEach>
			</select>
		</c:set>		
	</c:catch>
	<c:if test="${not empty grError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="gradeRangesXmlDom" scope="application"/>	
	</c:if>	
</c:if>


<%-- Request and cache the subjects as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty subjectsSelectListExamples3}">
	<c:catch var="suError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListSubjects"/>
		</c:url>
		
		<%-- Perform the web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the subjects in an application scope XML DOM variable 'subjectsXmlDom' --%>				
		<x:parse var="subjectsXmlDom" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>

		<%-- Create and store a select list of subjects from the ListSubjects XML response --%>
		<c:set var="subjectsSelectListExamples3" scope="application">
			<select name="su">		
			<option value=""> -- All subjects -- </option>
			<x:forEach select="$subjectsXmlDom/DDSWebService/ListSubjects/subjects/subject">
				<%-- Only display the grade range in the menu if it is not hidden --%>
				<x:if select="contains( renderingGuidelines/noDisplay, 'false' )">
					<%-- Set the selected subject to the one chosen by the user --%>
					<c:set var="searchKey">
						<x:out select="searchKey"/> 
					</c:set>
					
					<%-- Some of the entries may be list headings with no search key --%>
					<c:if test="${not empty searchKey}">
						<option value='<x:out select="searchKey"/>'><x:out select="renderingGuidelines/label"/></option>
					</c:if>
				</x:if>
			</x:forEach>
			</select>
		</c:set>
	</c:catch>
	<c:if test="${not empty suError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="subjectsXmlDom" scope="application"/>	
	</c:if>
</c:if>


<%-- Request and cache the resource types as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty resourceTypesXmlDom}">
	<c:catch var="reError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListResourceTypes"/>
		</c:url>
		
		<%-- Perform the web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the resource types in an application scope XML DOM variable 'resourceTypesXmlDom' --%>				
		<x:parse var="resourceTypesXmlDom" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>
	</c:catch>
	<c:if test="${not empty reError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="resourceTypesXmlDom" scope="application"/>	
	</c:if>
</c:if>


<%-- If the user has submitted some input, issue the request to the web service --%> 
<c:if test="${ (not empty param.q || not empty param.gr || not empty param.su) && empty param.fullDescription }">

	<%-- Set up the variables used for paging through results--%>
	<c:set var="startingOffset">
		<c:if test="${empty param.s}">0</c:if>
		<c:if test="${!empty param.s}"><c:out value="${param.s}"/></c:if>
	</c:set>

	<%-- Issue the request to the web service. If a connection error occurs, store it in variable 'serviceError' --%>
	<c:catch var="serviceError">
		<%-- Construct the http request to send to the web service and store it in variable 'webServiceRequest' --%>
		<c:url value="${ddswsBaseUrl}" var="webServiceRequest">
			<%-- Define each of the http parameters used in the request --%>
			<c:param name="verb" value="UserSearch"/>
			<%-- Perform a textual search using the user's input --%>
			<c:param name="q" value="${param.q}"/>
			<%-- Restrict search by grade range --%>
			<c:param name="gr" value="${param.gr}"/>
			<%-- Restrict search by subject --%>
			<c:param name="su" value="${param.su}"/>			
			<%-- Begin the result at the current offset --%>
			<c:param name="s" value="${startingOffset}"/>
			<%-- Return 10 results --%>
			<c:param name="n" value="10"/>
			<%-- An option parameter used to log where requests come from --%>
			<c:param name="client" value="ddsws10examples"/>		
		</c:url>
		
		<%-- Issue the request to the web service server and store the response in variable 'xmlResponse' --%>	
		<c:import url="${webServiceRequest}" var="xmlResponse" charEncoding="UTF-8" />
		<%-- Parse the XML response and store it in an XML DOM variable named 'xmlDom' --%>
		<x:parse var="xmlDom">
			<c:out value="${xmlResponse}" escapeXml="false"/>
		</x:parse>	
	</c:catch>
</c:if>



<html>
<head>	
	<title>Search the Digital Library for Earth System Education (DLESE)</title>
	
	<%-- Use CSS to format the look-and-feel of this page --%>
	<style type="text/css">
		<!--
		BODY {
			margin-left: 15px;
			margin-right: 15px;
			margin-top: 15px;
			margin-bottom: 1px;
			padding: 0px;
			font-family: Arial, Helvetica, sans-serif; 
			font-size: 10pt; 			
		} 
		
		H1 { 
			Font-Family : Arial, Helvetica, sans-serif ; 
			Font-Size : 14pt ;
			Color : #333366; 
		} 
		
		H2 { 
			Font-Family : Arial, Helvetica, sans-serif ; 
			Font-Size : 12pt ;
			Color : #333366; 
		} 

		H3 { 
			Font-Family : Arial, Helvetica, sans-serif ; 
			Font-Size : 10pt ;
			Color : #333366;
			margin-bottom: 0px;
		} 
			  
		TD { 
			font-family: Arial, Helvetica, sans-serif; 
			font-size: 10pt; 
		} 
				  
		A { text-decoration: underline; } 
		
		A:link { 
			Font-Family: Arial, Helvetica, sans-serif;
			Color: #333366
		} 
		
		A:active {
			Color: #333366;
			text-decoration: none;
		} 
		
		/* A:visited { Color: #333366 } */
		
		/* A pseudo class 'blackul' for the A tag */
		A.blackul:link {
			color: #333333; 
			text-decoration: none; 
			font-weight: bold;
			font-size: 11pt; 	
		}
		
		A.blackul:visited {
			color: #333333; 
			text-decoration: none; 
			font-weight: bold;
			font-size: 11pt; 	
		}
		
		.description { 
			padding-left: 10px; 	
			padding-right: 14px; 		
			padding-bottom: 8px; 
		}
		
		.highlight {
			color: #676cab;
		} 
		.edTop { 
			font-weight: bold;
			color: #333333; 
			padding-left: 4px; 	
			padding-top: 10px; 		
		} 
		.edEn { 
			padding-left: 12px; 	
		}		
		-->
    </style>

	<%-- Use javascript to put the curser in the search box and 
	     select the grade range and subject in the select lists --%>
	<script language="JavaScript">
		<!--
		function setSelected(selectedGrade,selectedSubject) {				
			/* Place the curser in the search box */
			document.searchForm.q.focus();

			/* Select the grade range the user had chosen */
			for(i = 0; i < document.searchForm.gr.options.length; i++){
				if(selectedGrade == document.searchForm.gr.options[i].value){
					document.searchForm.gr.options[i].selected = true;
					break;
				}
			}
			
			/* Select the subject the user had chosen */
			for(i = 0; i < document.searchForm.su.options.length; i++){
				if(selectedSubject == document.searchForm.su.options[i].value){
					document.searchForm.su.options[i].selected = true;
					break;
				}
			}			
		}
		-->
	</script>	
</head>

<%-- When the page loads, call the JavaScript function defined above, 
     passing in subject and grade range --%>
<body onload="JavaScript:setSelected('${param.gr}','${param.su}');">

<p>
<h2>Search the Digital Library for Earth System Education (DLESE)</h2>

<a href="index.jsp">Back to examples index</a><br>
<a href="full_example3.txt">View JSP code</a>

<p>
<form action="" name="searchForm">
	<table>
		<tr>
			<td>
				Enter a search:
			</td>
			<td>
				<input size="40" type="text" name="q" value="<c:out value='${param.q}' escapeXml='true'/>">
				<input type="submit" value="Search">
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>			
				<table cellspacing=0>
					<tr>
						<td>Grade range:</td>
						<td>&nbsp; Subject:</td>
					</tr>
					<tr>
						<td>${gradeRangeSelectListExamples3} </td>
						<td>&nbsp; ${subjectsSelectListExamples3}</td>
					</tr>					
				</table>			
			</td>
		</tr>
	</table>
</form>
</p>

<c:choose>
	<%-- If the user entered some search input, display the results --%>
	<c:when test="${ (not empty param.q || not empty param.su || not empty param.gr) && empty param.fullDescription }">
		
		<%-- If a connection error has occured, send a message to the user --%>
		<c:if test="${not empty serviceError}">
			Sorry, our system has encountered a problem and we were unable to perform your 
			search at this time. Please try again later.		
		</c:if>
		
		<%-- Display the record metadata to the user (if no connection error occured) --%> 
		<c:if test="${empty serviceError}">

		<%-- Set up some variables that we will use below to display to the user --%>
		<c:if test="${not empty param.q}">										
			<%-- Store the text the user entered for display below --%>	
			<c:set var="usersText">
				<span class="highlight">${fn:trim( param.q )}</span>
			</c:set>
		</c:if> 				
		<c:if test="${not empty param.gr}">					
			<%-- Store the selected grade range searchKey for use in the XPath expression below --%>	
			<c:set var="gradeRangeSearchKey" value="${param.gr}"/>					
			
			<%-- Store the grade range label for display below --%>	
			<c:set var="gradeRangeLabel">
				<c:if test="${not empty param.q}">,</c:if>
				<nobr>grades <span class="highlight"><x:out select="$gradeRangesXmlDom/DDSWebService/ListGradeRanges/gradeRanges/gradeRange[searchKey=$gradeRangeSearchKey]/renderingGuidelines/label"/></span></nobr>
			</c:set>
		</c:if> 
		<c:if test="${not empty param.su}">					
			<%-- Store the selected subject searchKey for use in the XPath expression below --%>	
			<c:set var="subjectSearchKey" value="${param.su}"/>					
			
			<%-- Store the subject label for display below --%>	
			<c:set var="subjectLabel">
				<c:if test="${not empty param.q || not empty param.gr}">,</c:if>	
				<nobr>subject <span class="highlight"><x:out select="$subjectsXmlDom/DDSWebService/ListSubjects/subjects/subject[searchKey=$subjectSearchKey]/renderingGuidelines/label"/></span></nobr>
			</c:set>
		</c:if> 			
		
		<x:choose>
			<%-- If the total number of results is greater than zero, display them --%>
			<x:when select="$xmlDom/DDSWebService/Search/resultInfo/totalNumResults > 0">
			
				<%-- ------ Begin paging logic ------ --%>
				
				<%-- Save the number of results in variable 'numResults' --%>
				<c:set var="numResults">
					<x:out select="$xmlDom/DDSWebService/Search/resultInfo/totalNumResults"/>
				</c:set>
				
				<%-- Construct a URL to this page that gets the next page of results --%>
				<c:url value="" var="nextResultsUrl">
					<c:param name="q" value="${param.q}"/>
					<c:param name="gr" value="${param.gr}"/>
					<c:param name="su" value="${param.su}"/>
					<c:param name="s" value="${startingOffset + 10}"/>
				</c:url>
				<%-- Construct a URL to this page that gets the previous page of results --%>
				<c:url value="" var="prevResultsUrl">
					<c:param name="q" value="${param.q}"/>
					<c:param name="gr" value="${param.gr}"/>
					<c:param name="su" value="${param.su}"/>				
					<c:param name="s" value="${startingOffset - 10}"/>
				</c:url>
				
				<%-- Create and store the HTML for the pager in variable 'pager' --%>
				<c:set var="pager">
					<c:if test="${(startingOffset - 10) >= 0}">
						<nobr><a href='<c:out value="${prevResultsUrl}"/>'>&lt;&lt; Prev</a> &nbsp;</nobr>
					</c:if>
					<nobr>
					${numResults > 1 ? 'results':'result'}
					<c:out value="${startingOffset +1}"/> 
					-
					<c:if test="${startingOffset + 10 > numResults}">
						<c:out value="${numResults}"/>
					</c:if>
					<c:if test="${startingOffset + 10 <= numResults}">
						<c:out value="${startingOffset + 10}"/>
					</c:if>	
					</nobr>					
					<nobr>out of <c:out value="${numResults}"/></nobr>				
					<c:if test="${(startingOffset + 10) < numResults}">
						<nobr>&nbsp; <a href='<c:out value="${nextResultsUrl}"/>'>Next &gt;&gt;</a></nobr>
					</c:if>
				</c:set>		
				<%-- ------ end paging logic ------ --%>			
	
				<table  width="100%" cellpadding="4" cellspacing="0">
					<tr>
						<td>
							<%-- Display the search options the user entered --%>
							<nobr>Your search for</nobr> ${usersText}${gradeRangeLabel}${subjectLabel} <nobr>had ${numResults} ${numResults > 1 ? 'results':'result'}</nobr>
						</td>
						<td align="right">
							<p>${pager}</p>
						</td>				
					</tr>
				
					<%-- Render a gray line --%>
					<tr>
						<td colspan="2">
							<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0">
								<td bgcolor="#999999" height="1"></td>
							</table>
						</td>
					</tr>
					
					<%-- Loop through each of the record nodes --%>
					<x:forEach select="$xmlDom/DDSWebService/Search/results/record">
					<tr>
						<td colspan="2">
							<%-- We are already at XPath $xmlDom/DDSWebService/Search/results/record
							so we can continue our path from there --%>
							<a href='<x:out select="metadata/itemRecord/technical/online/primaryURL"/>' 
								class="blackul"><x:out select="metadata/itemRecord/general/title"/></a><br>
							<a href='<x:out select="metadata/itemRecord/technical/online/primaryURL"/>'><x:out select="metadata/itemRecord/technical/online/primaryURL"/></a>					
						</td>			
					</tr>
					<tr>
						<td colspan="2">
							<div class="description">
								<x:out select="metadata/itemRecord/general/description"/>
								
								<%-- Construct a link to this page to pull up the full description --%>
								<a href='?fullDescription=<x:out select="head/id"/>&q=${param.q}&gr=${param.gr}&su=${param.su}'>Full description</a>
							</div>
						</td>			
					</tr>		
					</x:forEach>
	
					<tr>
						<td>
							&nbsp;
						</td>
						<td align="right">
							<p>${pager}</p>
						</td>				
					</tr>				
				</table>
							
			</x:when>
		
			<%-- If there were no matches, report to user --%>
			<x:otherwise>
				Your search for ${usersText}${gradeRangeLabel}${subjectLabel} had no matches. 	
			</x:otherwise>		
		</x:choose>
		</c:if>
		
	</c:when>
	
	<%-- Display the record full description if requested --%>
	<c:when test="${not empty param.fullDescription}">
	
		<%-- Issue a GetRecord request to the web service to pull up the record's metadata --%>
		<c:catch var="getRecordError">
			<%-- Construct the http request to send to the web service and store it in variable 'webServiceRequest' --%>
			<c:url value="${ddswsBaseUrl}" var="webServiceRequest">
				<%-- Define each of the http parameters used in the request --%>
				<c:param name="verb" value="GetRecord"/>
				<c:param name="id" value="${param.fullDescription}"/>
			</c:url>
			
			<%-- Issue the request to the web service server and store the response in variable 'xmlResponse' --%>	
			<c:import url="${webServiceRequest}" var="xmlResponse" charEncoding="UTF-8" />
			<%-- Parse the XML response and store it in an XML DOM variable named 'xmlDom' --%>
			<x:parse var="getRecordXmlDom">
				<c:out value="${xmlResponse}" escapeXml="false"/>
			</x:parse>	
		</c:catch>	
		
		<%-- If a connection error has occured, send a message to the user --%>
		<c:if test="${not empty getRecordError}">
			Sorry, our system has encountered a problem and we were unable to display
			the information at this time. Please try again later.		
		</c:if>
		
		<x:choose>		
			<%-- Display the record metadata if it has been returned successfully by the web service --%>
			<x:when select="$getRecordXmlDom/DDSWebService/GetRecord/record/metadata/itemRecord/general/title">		
				
				
				<table  width="100%" cellpadding="4" cellspacing="0">
					<tr>
						<td>
							<h3>Resource description</h3>
						</td>			
					</tr>
				
					<%-- Render a gray line --%>
					<tr>
						<td>
							<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0">
								<td bgcolor="#999999" height="1"></td>
							</table>
						</td>
					</tr>
					
					<%-- Store the record element in variable 'record' for use below --%>
					<x:set select="$getRecordXmlDom/DDSWebService/GetRecord/record" var="record"/>
					<%-- Store the gradeRanges XML element for use in the XPath expressions below --%>
					<x:set var="gradeRangesElement" select="$gradeRangesXmlDom/DDSWebService/ListGradeRanges/gradeRanges"/>
					<%-- Store the subjects XML element for use in the XPath expressions below --%>
					<x:set var="subjectsElement" select="$subjectsXmlDom/DDSWebService/ListSubjects/subjects"/>						
					<%-- Store the resource types XML element for use in the XPath expressions below --%>
					<x:set var="resourceTypesElement" select="$resourceTypesXmlDom/DDSWebService/ListResourceTypes/resourceTypes"/>						
					<tr>
						<td>
							<%-- We are already at XPath $getRecordXmlDom/DDSWebService/Search/results/record
							so we can continue our path from there --%>
							<a href='<x:out select="$record/metadata/itemRecord/technical/online/primaryURL"/>' 
								class="blackul"><x:out select="$record/metadata/itemRecord/general/title"/></a><br>
							<a href='<x:out select="$record/metadata/itemRecord/technical/online/primaryURL"/>'><x:out select="$record/metadata/itemRecord/technical/online/primaryURL"/></a>					
						</td>			
					</tr>		
					<tr>
						<td>					
							<div class="description">
								<x:out select="$record/metadata/itemRecord/general/description"/>								
								
								<div class="edTop"><i>Grade range:</i></div>
								<x:forEach select="$record/metadata/itemRecord/educational/audiences/audience/gradeRange">
									<%-- Store the grade range vocab entry for use in the XPath expression below --%>
									<c:set var="gradeRangeVocabEntry">
										<x:out select="."/>
									</c:set>									
									<div class="edEn">
										<%-- Display the vocab label to the user --%>
										<x:out select="$gradeRangesElement/gradeRange[vocabEntry=$gradeRangeVocabEntry]/renderingGuidelines/label"/>		
									</div>
								</x:forEach>
								
								<div class="edTop"><i>Type of resource:</i></div>
								<x:forEach select="$record/metadata/itemRecord/educational/resourceTypes/resourceType">
									<%-- Store the resource type vocab entry for use in the XPath expression below --%>
									<c:set var="resourceTypeVocabEntry">
										<x:out select="."/>
									</c:set>							
									<div class="edEn">
										<%-- Display the vocab label to the user --%>
										<x:out select="$resourceTypesElement/resourceType[vocabEntry=$resourceTypeVocabEntry]/renderingGuidelines/label"/>		
									</div>										
								</x:forEach>
								
								<div class="edTop"><i>Subject:</i></div>
																
								<x:forEach select="$record/metadata/itemRecord/general/subjects/subject">								
									<%-- Store the grade range vocab entry for use in the XPath expression below --%>
									<c:set var="subjectVocabEntry">
										<x:out select="."/>
									</c:set>									
									<div class="edEn">
										<%-- Display the vocab label to the user --%>
										<x:out select="$subjectsElement/subject[vocabEntry=$subjectVocabEntry]/renderingGuidelines/label"/>		
									</div>								
								</x:forEach>
								
								<x:if select="$record/metadata/itemRecord/general/keywords/*">
									<div class="edTop"><i>Keywords:</i></div>
									<x:forEach select="$record/metadata/itemRecord/general/keywords/keyword">
										<div class="edEn"><x:out select="." escapeXml="false" /></div>
									</x:forEach>	
								</x:if>
								
								<div class="edTop"><i>Technical requirements:</i></div>
								<x:forEach select="$record/metadata/itemRecord/technical/online/requirements/requirement/reqType">
									<x:choose>
										<x:when select=". = 'DLESE:General:No specific technical requirements'">
											<div class="edEn">No specific technical requirements, just a browser required</div>
										</x:when>									
										<x:when select=". = 'DLESE:Other:More specific technical requirements'">
											<x:forEach select="../../../otherRequirements/otherRequirement/otherType">
												<div class="edEn"><x:out select="."/></div>
											</x:forEach>
										</x:when>
										<x:otherwise>
											<div class="edEn"><x:out select="substring-after(.,'DLESE:')" escapeXml="false" /></div>
										</x:otherwise>
									</x:choose>							
								</x:forEach>							
	
								<div class="edTop"><i>Cost / Copyright:</i></div>
								<div class="edEn">
									<x:set var="cost" select="$record/metadata/itemRecord/rights/cost" />
									<x:choose>
										<x:when select="starts-with($record/metadata/itemRecord/rights/cost,'DLESE:No')">
											No cost
										</x:when>
										<x:otherwise>
											<x:out select="substring-after($record/metadata/itemRecord/rights/cost,'DLESE:')" escapeXml="false"/>
										</x:otherwise>
									</x:choose>
									<br>
									<x:out select="$record/metadata/itemRecord/rights/description" escapeXml="false"/>
								</div>		 					
								
								<div class="edTop"><i>DLESE catalog ID:</i></div>
								<div class="edEn">
									<x:out select="$record/metadata/itemRecord/metaMetadata/catalogEntries/catalog/@entry" />
								</div>					
									
							</div>						
						</td>
					</tr>			
				</table>					
			</x:when>
		
			<%-- Display the web service error message if one was provided --%>
			<x:when select="$getRecordXmlDom/DDSWebService/error">
				Sorry, we were unable to display the full record you requested. 
				The reason: <i><x:out select="$getRecordXmlDom/DDSWebService/error"/></i>	
			</x:when>
			
			<%-- If we couldn't find the title and there wasn't an error, this must not be an ADN record --%>
			<x:otherwise>
				Sorry, the record you requested, '${param.fullDescription}' cannot be displayed.
			</x:otherwise>	
		</x:choose>		
		
		
		
	</c:when>	
	
	<%-- If the user has hit submit but did not define a search, send a message --%>
	<c:when test="${ param.q != null && empty param.q }"> 
		You did not define a search. Please enter some text into the box above
		or select a grade range or subject.
	</c:when>	

</c:choose>



<br><br><br><br>

</body>
</html>


