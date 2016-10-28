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
--%>

<c:set var="contextUrl"><%@ include file="../../ContextUrl.jsp" %></c:set>
<c:set var="ddswsBaseUrl">${contextUrl}/services/ddsws1-0</c:set>

<%-- Store the base URL to the service to use in the requests below --%>
<c:set var="ddswsBaseUrl" value="${ddswsBaseUrl}"/>

<%-- Provide a way to re-load the grade ranges, resource types and subjects manually.
      This option is invisible to the user but an administrator can use it
	  to update the grade range and subject lists and resource type vocabularies
	  if necessary --%>
<c:if test="${param.reload == 'true'}">
	<%-- If indicated, delete the application scope variables
	     so they will be re-loaded below --%>
	<c:remove var="gradeRangesXmlDom_DDSExp" scope="application"/>
	<c:remove var="subjectsXmlDom_DDSExp" scope="application"/>
	<c:remove var="resourceTypesXmlDom_DDSExp" scope="application"/>
	<c:remove var="collectionsXmlDom_DDSExp" scope="application"/>	
	<c:remove var="contentStandardsXmlDom_DDSExp" scope="application"/>
</c:if>

<%-- Variable used for paging through results --%>
<c:set var="startingOffset">
	<c:if test="${empty param.s}">0</c:if>
	<c:if test="${!empty param.s}"><c:out value="${param.s}"/></c:if> 
</c:set>

<%-- If the user has submitted a search request, issue the request to the Web service --%> 
<c:if test="${ (not empty param.q || not empty param.qh || not empty param.gr || not empty param.su || not empty param.re || not empty param.ky || not empty param.cs) && empty param.fullDescription && empty param.collectionDescription }">
	
	<%-- Indicate to display the search results in the UI --%>
	<c:set var="displaySearchResults" value="true"/>
	
	<%-- Issue the request to the Web service. If a connection error occurs, store it in variable 'serviceError' --%>
	<c:catch var="searchXmlDomError">
		<%-- Construct the http request to send to the Web service and store it in variable 'webServiceRequest' --%>
		<c:url value="${ddswsBaseUrl}" var="webServiceRequest">
			
			<%-- Set up the query string sent to the Web service search engine --%>
			<c:choose>
				<%-- If the user has entered a Lucene clause-based search, output it unchanged --%>
				<c:when test='${f:matches( param.q, ".*[:^()].*" )}'>
					<c:set var="stems" value="${f:stem(param.q)}"/>
					<c:set var="searchQuery" value="${param.q}"/>
				</c:when>				
				<%-- If this is a normal textual search, perform some query enhancements --%>
				<c:when test="${not empty fn:trim( param.q )}">
					<c:set var="stems" value="${f:stem(param.q)}"/>
					<c:set var="qORClause">
						<c:forTokens var="token" items="${f:replaceAll(param.q,'OR|AND','')}" 
						delims=" " varStatus="i">${token}${i.last ? '' : ' OR '}</c:forTokens>
					</c:set>
					<c:set var="stemsORClause">
						<c:forTokens var="token" items="${f:replaceAll(stems,'OR|AND','')}" 
						delims=" " varStatus="i">${token}${i.last ? '' : ' OR '}</c:forTokens>
					</c:set>					
					<c:set var="stemClause">
						<c:if test="${!fn:contains(param.q,'&quot;')}">
							OR stems:(${f:stemClause(param.q)})
						</c:if>
					</c:set>
					<c:set var="searchQuery">
						( ( (${param.q}) ${stemClause} ) AND (ky:0* OR titlestems:(${stemsORClause})^2 OR stems:(${stemsORClause})^2 OR title:(${qORClause})^2 OR description:(${qORClause})^2) )
					</c:set>
				</c:when>
				<%-- If this is a 'Smart link' search, output it unchanged --%>
				<c:when test="${not empty fn:trim( param.qh )}">
					<c:set var="searchQuery" value="(${param.qh})"/>
				</c:when>
			</c:choose>
			<%-- Add boosting and constraints if they have been defined --%>
			<c:choose>
				<c:when test="${not empty fn:trim( searchQuery ) && not empty searchBoostingAndConstraints }">
					<c:set var="searchQuery" value="(${searchQuery}) AND (${searchBoostingAndConstraints})"/>
				</c:when>
				<c:when test="${ not empty searchBoostingAndConstraints }">
					<c:set var="searchQuery" value="(${searchBoostingAndConstraints})"/>
				</c:when>
			</c:choose>					
			
			<%-- Define each of the http parameters used in the Web service request --%>
			
			<%-- Use the 'Search' request so we can control our own query logic --%>	
			<c:param name="verb" value="Search"/>
			<%-- Perform a textual search using the user's input and defined constraints --%>
			<c:param name="q" value="${searchQuery}"/>
			<%-- Restrict search by grade range --%>
			<c:forEach var="paramValue" items="${paramValues.gr}">
				<c:param name="gr" value="${paramValue}"/>
			</c:forEach>		
			<%-- Restrict search by subject --%>
			<c:forEach var="paramValue" items="${paramValues.su}">
				<c:param name="su" value="${paramValue}"/>
			</c:forEach>
			<%-- Restrict search by resource type --%>
			<c:forEach var="paramValue" items="${paramValues.re}">
				<c:param name="re" value="${paramValue}"/>
			</c:forEach>
			<%-- Restrict search by collection --%>
			<c:forEach var="paramValue" items="${paramValues.ky}">
				<c:param name="ky" value="${paramValue}"/>
			</c:forEach>	
			<%-- Restrict search by content standard --%>
			<c:forEach var="paramValue" items="${paramValues.cs}">
				<c:param name="cs" value="${paramValue}"/>
			</c:forEach>

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
<c:if test="${empty gradeRangesXmlDom_DDSExp}">
	<c:catch var="grError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListGradeRanges"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the grade ranges in an application scope XML DOM variable 'gradeRangesXmlDom_DDSExp' --%>				
		<x:parse var="gradeRangesXmlDom_DDSExp" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>			
	</c:catch>
	<c:if test="${not empty grError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="gradeRangesXmlDom_DDSExp" scope="application"/>	
	</c:if>	
</c:if>

<%-- Request and cache the subjects as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty subjectsXmlDom_DDSExp}">
	<c:catch var="suError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListSubjects"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the subjects in an application scope XML DOM variable 'subjectsXmlDom_DDSExp' --%>				
		<x:parse var="subjectsXmlDom_DDSExp" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>
	</c:catch>
	<c:if test="${not empty suError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="subjectsXmlDom_DDSExp" scope="application"/>	
	</c:if>
</c:if>


<%-- Request and cache the resource types as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty resourceTypesXmlDom_DDSExp}">
	<c:catch var="reError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListResourceTypes"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the resource types in an application scope XML DOM variable 'resourceTypesXmlDom_DDSExp' --%>				
		<x:parse var="resourceTypesXmlDom_DDSExp" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>
	</c:catch>
	<c:if test="${not empty reError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="resourceTypesXmlDom_DDSExp" scope="application"/>	
	</c:if>
</c:if>

<%-- Request and cache the collections as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty collectionsXmlDom_DDSExp}">
	<c:catch var="kyError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListCollections"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the resource types in an application scope XML DOM variable 'collectionsXmlDom_DDSExp' --%>				
		<x:parse var="collectionsXmlDom_DDSExp" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>
	</c:catch>
	<c:if test="${not empty kyError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="collectionsXmlDom_DDSExp" scope="application"/>	
	</c:if>
</c:if>

<%-- Request and cache the content standards as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty contentStandardsXmlDom_DDSExp}">
	<c:catch var="csError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListContentStandards"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the resource types in an application scope XML DOM variable 'contentStandardsXmlDom_DDSExp' --%>				
		<x:parse var="contentStandardsXmlDom_DDSExp" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>
	</c:catch>
	<c:if test="${not empty csError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="contentStandardsXmlDom_DDSExp" scope="application"/>	
	</c:if>
</c:if>

