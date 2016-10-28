<%--
	From JSP client version 2.5. Note that this page acts partially as the controller...	
--%>

<%-- Note: send the parameter reload=true to the encompassing page to re-generate
the XML Doms for each of the vocabularies --%>

<%-- Variable used for paging through results --%>
<c:set var="startingOffset">
	<c:if test="${empty param.s}">0</c:if>
	<c:if test="${!empty param.s}"><c:out value="${param.s}"/></c:if> 
</c:set>


<%-- Store the vocabulary request parameter names in variable 'vocabParameterNames' --%>
<%-- <c:set var="vocabParameterNames" value="gr su re ky cs"/> --%>

<%-- Determine wheter a search was requested as indicated by one of the search-related parameters,
		and set variable 'searchWasRequested' to true if this is the case --%>
<%-- <c:forTokens var="paramName" items="q qh ${vocabParameterNames} ${smartLinkMenuParameterNames}" delims=" ">
	<c:if test="${not empty param[paramName]}">
		<c:set var="searchWasRequested" value="true"/>
	</c:if>
</c:forTokens> --%>	

<%-- Determine wheter a search was requested --%>
<c:choose>
	<c:when test="${param.slc == null || param.slc == 'f'}">
		<c:if test="${ not empty param.q || jsformv11.hasMenuItemSelected || (not empty param.qh && param.slc != 'f')}">
			<c:set var="searchWasRequested" value="true"/>
		</c:if>
	</c:when>
	<c:otherwise>
		<c:if test="${ not empty param.q || jsformv11.hasMenuItemSelected }">
			<c:set var="searchWasRequested" value="true"/>
		</c:if>	
	</c:otherwise>
</c:choose>

<%-- If the user has submitted a search request, issue a Search request to the Web service
		and collect the search results. --%> 
<c:if test="${ searchWasRequested == 'true' && empty param.fullDescription && empty param.collectionDescription }">
	<%-- Indicate to display the search results in the UI --%>
	<c:set var="displaySearchResults" value="true"/>
</c:if>


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
		<%-- Parse the XML response and store it in an XML DOM variable named 'getRecordXmlDom' --%>
		<x:parse var="getRecordXmlDom">
			<c:out value="${jsformv11.docReader.xmlLocalized}" escapeXml="false"/>
		</x:parse>	
	</c:catch>	
</c:if>


<%-- 	------------- Begin grade range, subject, resource type and collections vocabs -------------- --%>
	
<%-- Request and cache the grade ranges as an application scoped variable
        only if they have not been cached already --%>				
<c:if test="${empty gradeRangesXmlDom || empty gradeRangeLabels || param.reload == 'true'}">
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

		<%-- Cache the vocab labels in a HashMap for efficient display --%>
		<% getServletContext().setAttribute("gradeRangeLabels",new java.util.HashMap()); %>
		<x:forEach select="$gradeRangesXmlDom/DDSWebService/ListGradeRanges/gradeRanges/gradeRange">
			<c:set var="vocabEntry"><x:out select="vocabEntry"/></c:set>
			<c:set var="label"><x:out select="renderingGuidelines/label"/></c:set>
			<c:set target="${applicationScope.gradeRangeLabels}" property="${vocabEntry}" value="${label}"/>		
		</x:forEach>			
	</c:catch>
	<c:if test="${not empty grError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="gradeRangesXmlDom" scope="application"/>	
	</c:if>	
</c:if>

<%-- Request and cache the subjects as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty subjectsXmlDom || empty subjectLabels || param.reload == 'true'}">
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

		<%-- Cache the vocab labels in a HashMap for efficient display --%>
		<% getServletContext().setAttribute("subjectLabels",new java.util.HashMap()); %>
		<x:forEach select="$subjectsXmlDom/DDSWebService/ListSubjects/subjects/subject">
			<c:set var="vocabEntry"><x:out select="vocabEntry"/></c:set>
			<c:set var="label"><x:out select="renderingGuidelines/label"/></c:set>
			<c:set target="${applicationScope.subjectLabels}" property="${vocabEntry}" value="${label}"/>		
		</x:forEach>		
	</c:catch>
	<c:if test="${not empty suError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="subjectsXmlDom" scope="application"/>	
	</c:if>
</c:if>


<%-- Request and cache the resource types as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty resourceTypesXmlDom || empty resourceTypeLabels || param.reload == 'true'}">
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

		<%-- Cache the vocab labels in a HashMap for efficient display --%>
		<% getServletContext().setAttribute("resourceTypeLabels",new java.util.HashMap()); %>
		<x:forEach select="$resourceTypesXmlDom/DDSWebService/ListResourceTypes/resourceTypes/resourceType">
			<c:set var="vocabEntry"><x:out select="vocabEntry"/></c:set>
			<c:set var="label"><x:out select="renderingGuidelines/label"/></c:set>
			<c:set target="${applicationScope.resourceTypeLabels}" property="${vocabEntry}" value="${label}"/>		
		</x:forEach>		
	</c:catch>
	<c:if test="${not empty reError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="resourceTypesXmlDom" scope="application"/>	
	</c:if>
</c:if>

<%-- Request and but do NOT cache (true ||) the collections as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${true || empty collectionsXmlDom || empty collectionLabels || param.reload == 'true'}">
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

		<%-- Cache the vocab labels in a HashMap for efficient display --%>
		<% getServletContext().setAttribute("collectionLabels",new java.util.HashMap()); %>
		<x:forEach select="$collectionsXmlDom/DDSWebService/ListCollections/collections/collection">
			<c:set var="vocabEntry"><x:out select="vocabEntry"/></c:set>
			<c:set var="label"><x:out select="renderingGuidelines/label"/></c:set>
			<c:set target="${applicationScope.collectionLabels}" property="${vocabEntry}" value="${label}"/>		
		</x:forEach>		
	</c:catch>
	<c:if test="${not empty kyError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="collectionsXmlDom" scope="application"/>	
	</c:if>
</c:if>

<%-- Request and cache the content standards as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty contentStandardsXmlDom || empty contentStandardLabels || param.reload == 'true'}">
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

		<%-- Cache the vocab labels in a HashMap for efficient display --%>
		<% getServletContext().setAttribute("contentStandardLabels",new java.util.HashMap()); %>
		<x:forEach select="$contentStandardsXmlDom/DDSWebService/ListContentStandards/contentStandards/contentStandard">
			<c:set var="vocabEntry"><x:out select="vocabEntry"/></c:set>
			<c:set var="label"><x:out select="renderingGuidelines/label"/></c:set>
			<c:set target="${applicationScope.contentStandardLabels}" property="${vocabEntry}" value="${label}"/>		
		</x:forEach>		
	</c:catch>
	<c:if test="${not empty csError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="contentStandardsXmlDom" scope="application"/>	
	</c:if>
</c:if>

