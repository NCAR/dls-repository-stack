<%--
	This JSP page connects with the DLESE Web service (ddsws) to peform
	the user's search and retrieve the resource full description and collection
	description. It also contacts the Web service to get the grade ranges, 
	subjects, resource types and collections for display in menus. 
	It is meant to be included in encompassing JSP pages that need to use the 
	Web service.
	
	Provided by the Digital Library for Earth System Education (DLESE)
	free and with no warranty. This example may be copied and modified.
	e-mail: support@dlese.org
	Information: http://www.dlese.org/dds/services/

	These templates are available for download at 
	http://sourceforge.net/project/showfiles.php?group_id=23991&package_id=123037
	
	Requires Apache Tomcat 5 (http://jakarta.apache.org/tomcat/)
	or other JSP 2.0 compliant JSP container and the 
	JSTL v1.1 tag libraries, which are included in the examples download
	or can be obtained from http://java.sun.com (standard.jar and jstl.jar).

	This is JSP client version 2.2.	
--%>

<%-- Note: send the parameter reload=true to the encompassing page to re-generate
the XML Doms for each of the vocabularies --%>


<%-- Store the base URL to the Web service to use in the requests below --%>
<%-- <c:set var="ddswsBaseUrl" value="http://tremor:9187/dds/services/ddsws1-0"/> --%>
<c:set var="ddswsBaseUrl" value="${f:contextUrl(pageContext.request)}/services/ddsws1-0"/>

<%-- Variable used for paging through results --%>
<c:set var="startingOffset">
	<c:if test="${empty param.s}">0</c:if>
	<c:if test="${!empty param.s}"><c:out value="${param.s}"/></c:if> 
</c:set>

<%-- Store the XML DOM for the custom smart link menu in variable 'smartLinkMenuDom' --%>
<%-- <c:import url="smart_link_definitions.xml" var="xmlResponse" charEncoding="UTF-8" />
<x:parse var="smartLinkMenuDom">
	<c:out value="${xmlResponse}" escapeXml="false"/>
</x:parse> --%>


<%-- Store the smart link menu parameter names we're using in variable 'smartLinkMenuParameterNames'
		and store any menu parameter names that are smart links only in 'smartLinkOnlyParameterNames' --%>
<%-- <x:forEach select="$smartLinkMenuDom/root/menu" var="menu">
	<c:set var="isInMenu"><x:out select="boolean($menu/@menuLabel)"/></c:set>
	<c:set var="isInSmartLinks"><x:out select="boolean($menu/@smartLinkLabel)"/></c:set>
	<c:set var="smartLinkMenuParameterNames">${smartLinkMenuParameterNames} slm<x:out select="$menu/@id"/></c:set>
	<c:if test="${isInSmartLinks && !isInMenu}">
		<c:set var="smartLinkOnlyParameterNames">${smartLinkOnlyParameterNames} slm<x:out select="$menu/@id"/></c:set>
	</c:if>	
</x:forEach> --%>

<%-- Store the vocabulary request parameter names in variable 'vocabParameterNames' --%>
<c:set var="vocabParameterNames" value="gr su re ky cs"/>

<%-- Determine wheter a search was requested as indicated by one of the search-related parameters,
		and set variable 'searchWasRequested' to true if this is the case --%>
<c:forTokens var="paramName" items="q qh ${vocabParameterNames} ${smartLinkMenuParameterNames}" delims=" ">
	<c:if test="${not empty param[paramName]}">
		<c:set var="searchWasRequested" value="true"/>
	</c:if>
</c:forTokens>	

<%-- If the user has submitted a search request, issue a Search request to the Web service
		and collect the search results. --%> 
<c:if test="${ searchWasRequested == 'true' && empty param.fullDescription && empty param.collectionDescription }">
	
	<%-- Indicate to display the search results in the UI --%>
	<c:set var="displaySearchResults" value="true"/>
	
	<%-- Issue the request to the Web service. If a connection error occurs, store it in variable 'serviceError' --%>
	<c:catch var="searchXmlDomError">
		<%-- Construct the http request to send to the Web service and store it in variable 'webServiceRequest' --%>
		<c:url value="${ddswsBaseUrl}" var="webServiceRequest">
			<c:set var="usersQuery" value="${fn:trim(param.q)}"/>
			<c:if test="${not empty usersQuery}">
				<c:set var="searchQuery">${usersQuery}</c:set>					
			</c:if>	
			<c:if test="${not empty fn:trim( param.qh ) && param.allrecs != 'n'}">
				<c:choose>
					<c:when test="${empty searchQuery}">
						<c:set var="searchQuery" value="(${param.qh})"/>
					</c:when>
					<c:otherwise>
						<c:set var="searchQuery" value="(${param.qh}) AND (${searchQuery})"/>					
					</c:otherwise>
				</c:choose>
			</c:if>			
			<%-- Add any global boosting and constraints that have been defined for this page --%>
			<c:choose>
				<c:when test="${not empty fn:trim( searchQuery ) && not empty searchBoostingAndConstraints }">
					<c:set var="searchQuery" value="(${searchQuery}) AND (${searchBoostingAndConstraints})"/>
				</c:when>
				<c:when test="${ not empty searchBoostingAndConstraints }">
					<c:set var="searchQuery" value="(${searchBoostingAndConstraints})"/>
				</c:when>
			</c:choose>					
						
			<%-- Construct the smart link menu query string and store it in variable 'slmQuery'  --%>
			<%-- <c:set var="slmQuery">
				<c:forTokens var="paramName" items="${smartLinkMenuParameterNames}" delims=" " varStatus="j">
					<c:if test="${not empty param[paramName]}">
						${empty menuId ? '' : ' AND '}
						<c:set var="menuId" value="${fn:substringAfter(paramName,'slm')}"/>
						( <c:forEach var="paramValue" items="${paramValues[paramName]}" 
							varStatus="i">( <x:out select="$smartLinkMenuDom/root/menu[@id=$menuId]/menuItem[@id=$paramValue]/queryMapping" escapeXml="false"/> )${i.last ? ' ' : ' OR '}</c:forEach> )
					</c:if>
				</c:forTokens>
			</c:set> --%>

			<%-- Add the smart link virtual query to the user's query --%>
			<c:if test="${not empty fn:trim(slmQuery)}">
				<c:choose>
					<c:when test="${not empty fn:trim(searchQuery)}">
						<c:set var="searchQuery" value="( ${searchQuery} ) AND ( ${slmQuery} )"/>
					</c:when>
					<c:otherwise>
						<c:set var="searchQuery" value="( ${slmQuery} )"/>				
					</c:otherwise>
				</c:choose>
			</c:if>			

			<%-- Define each of the http parameters used in the Web service request --%>
						
			<%-- Define search parameters for each requested vocab parameters (grade range,
					resource type, etc.) --%>
			<c:forTokens var="paramName" items="${vocabParameterNames}" delims=" ">
				<c:forEach var="paramValue" items="${paramValues[paramName]}">
					<c:param name="${paramName}" value="${paramValue}"/>
				</c:forEach>
			</c:forTokens>					
			
			<%-- Use the 'Search' request so we can control our own query logic --%>	
			<c:param name="verb" value="UserSearch"/>
			<%-- Supply the query we have constructed via the 'q' parameter --%>
			<c:param name="q" value="${searchQuery}"/>
			
			<%-- Note: The below variables (restrictToGradeRanges, restrictToSubjects, etc.)
			may be used to restrict by gr, su, etc. however if the user
			selects additional gr, su, etc. they will be added to the result set rather
			than searching within those fields. To define the boolean logic separately, pass these
			in as part of a Search query --%>
			<%-- Restrict search by pre-defined grade range --%>
			<c:forTokens var="paramValue" items="${restrictToGradeRanges}" delims=", ">
				<c:param name="gr" value="${paramValue}"/>
			</c:forTokens>		
			<%-- Restrict search by pre-defined subject --%>
			<c:forTokens var="paramValue" items="${restrictToSubjects}" delims=", ">
				<c:param name="su" value="${paramValue}"/>
			</c:forTokens>
			<%-- Restrict search by pre-defined resource type --%>
			<c:forTokens var="paramValue" items="${restrictToResourceTypes}" delims=", ">
				<c:param name="re" value="${paramValue}"/>
			</c:forTokens>
			<%-- Restrict search by pre-defined collection --%>
			<c:forTokens var="paramValue" items="${restrictToCollections}" delims=", ">
				<c:param name="ky" value="${paramValue}"/>
			</c:forTokens>	
			<%-- Restrict search by pre-defined content standard --%>
			<c:forTokens var="paramValue" items="${restrictToContentStandards}" delims=", ">
				<c:param name="cs" value="${paramValue}"/>
			</c:forTokens>
						
			<%-- Begin the result at the current offset --%>
			<c:param name="s" value="${startingOffset}"/>
			<%-- Return resultsPerPage results --%>
			<c:param name="n" value="${resultsPerPage}"/>
			<%-- Ensure all results are in ADN format --%>
			<c:param name="xmlFormat" value="adn-localized"/>
			<%-- An option parameter used to log where requests come from --%>
			<c:param name="client" value="${clientName}"/>		
		</c:url>
		
		<%-- Issue the request to the Web service server and store the response in variable 'xmlResponse' --%>	
		<c:import url="${webServiceRequest}" var="xmlResponse" charEncoding="UTF-8" />
		<%-- Parse the XML response and store it in an XML DOM variable named 'searchXmlDom' --%>
		<x:parse var="searchXmlDom">
			<c:out value="${xmlResponse}" escapeXml="false"/> 
		</x:parse>	
	</c:catch>
</c:if>

<%-- Un-comment the following to inspect the search query sent to the Web service --%>
 <%-- <b>SearchQuery sent to the Web service: '${searchQuery}'</b><br> --%> 

<%-- If the user has requested to see a full description or collection description, issue the
		getRecord Web service request and store the result in variable 'getRecordXmlDom' --%> 
<c:if test="${not empty param.fullDescription || not empty param.collectionDescription}">

	<%-- Indicate to display the the full description or collection description in the UI --%>
	<c:choose>
		<c:when test="${not empty param.fullDescription}">
			<c:set var="displayFullDescription" value="true"/>
		</c:when>
		<c:when test="${not empty param.collectionDescription}">
			<c:set var="displayCollectionDescription" value="true"/>
		</c:when>		
	</c:choose>
	
	
	<%-- Issue a GetRecord request to the Web service to pull up the record's metadata --%>
	<c:catch var="getRecordXmlDomError">
		<%-- Construct the http request to send to the Web service and store it in variable 'webServiceRequest' --%>
		<c:url value="${ddswsBaseUrl}" var="webServiceRequest">
			<%-- Define each of the http parameters used in the request --%>
			<c:param name="verb" value="GetRecord"/>
			<c:choose>
				<c:when test="${not empty param.fullDescription}">
					<c:param name="id" value="${param.fullDescription}"/>
				</c:when>
				<c:when test="${not empty param.collectionDescription}">
					<c:param name="id" value="${param.collectionDescription}"/>
				</c:when>				
			</c:choose>
		</c:url>
		
		<%-- Issue the request to the Web service server and store the response in variable 'xmlResponse' --%>	
		<c:import url="${webServiceRequest}" var="xmlResponse" charEncoding="UTF-8" />
		<%-- Parse the XML response and store it in an XML DOM variable named 'getRecordXmlDom' --%>
		<x:parse var="getRecordXmlDom">
			<c:out value="${xmlResponse}" escapeXml="false"/>
		</x:parse>	
	</c:catch>	
</c:if>


<%-- 	------------- Begin grade range, subject, resource type and collections vocabs -------------- --%>
	
<%-- Request and cache the grade ranges as an application scoped variable
        only if they have not been cached already --%>				
<c:if test="${empty gradeRangesXmlDom || param.reload == 'true'}">
	<c:catch var="grError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListGradeRanges"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the grade ranges in an application scope XML DOM variable 'gradeRangesXmlDom' --%>				
		<x:parse var="gradeRangesXmlDom" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>			
	</c:catch>
	<c:if test="${not empty grError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="gradeRangesXmlDom" scope="application"/>	
	</c:if>	
</c:if>

<%-- Request and cache the subjects as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty subjectsXmlDom || param.reload == 'true'}">
	<c:catch var="suError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListSubjects"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the subjects in an application scope XML DOM variable 'subjectsXmlDom' --%>				
		<x:parse var="subjectsXmlDom" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>
	</c:catch>
	<c:if test="${not empty suError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="subjectsXmlDom" scope="application"/>	
	</c:if>
</c:if>


<%-- Request and cache the resource types as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty resourceTypesXmlDom || param.reload == 'true'}">
	<c:catch var="reError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListResourceTypes"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
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

<%-- Request and cache the collections as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty collectionsXmlDom || param.reload == 'true'}">
	<c:catch var="kyError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListCollections"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the resource types in an application scope XML DOM variable 'collectionsXmlDom' --%>				
		<x:parse var="collectionsXmlDom" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>
	</c:catch>
	<c:if test="${not empty kyError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="collectionsXmlDom" scope="application"/>	
	</c:if>
</c:if>

<%-- Request and cache the content standards as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty contentStandardsXmlDom || param.reload == 'true'}">
	<c:catch var="csError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListContentStandards"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the resource types in an application scope XML DOM variable 'contentStandardsXmlDom' --%>				
		<x:parse var="contentStandardsXmlDom" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>
	</c:catch>
	<c:if test="${not empty csError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="contentStandardsXmlDom" scope="application"/>	
	</c:if>
</c:if>

