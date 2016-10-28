<%--
	This JSP page connects with the DDS Web service (ddsws) to peform
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

<c:set var="contextUrl"><%@ include file="../ContextUrl.jsp" %></c:set>
<c:set var="ddswsBaseUrl">${contextUrl}/services/ddsws1-0</c:set>

<%-- Store the base URL to the service to use in the requests below, and replace if https --%>
<c:set var="ddswsBaseUrl" value="${fn:replace( fn:replace(ddswsBaseUrl, ':443', ''), 'https://', 'http://')}"/>


<%-- 	------------- Begin grade range, subject, resource type and collections vocabs -------------- --%>
	
<%-- Request and cache the grade ranges as an application scoped variable
        only if they have not been cached already --%>				
<c:if test="${empty gradeRangesXmlDom_adminSearch || param.reload == 'true'}">
	<c:catch var="grError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListGradeRanges"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the grade ranges in an application scope XML DOM variable 'gradeRangesXmlDom_adminSearch' --%>				
		<x:parse var="gradeRangesXmlDom_adminSearch" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>			
	</c:catch>
	<c:if test="${not empty grError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="gradeRangesXmlDom_adminSearch" scope="application"/>	
	</c:if>	
</c:if>

<%-- Request and cache the subjects as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty subjectsXmlDom_adminSearch || param.reload == 'true'}">
	<c:catch var="suError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListSubjects"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the subjects in an application scope XML DOM variable 'subjectsXmlDom_adminSearch' --%>				
		<x:parse var="subjectsXmlDom_adminSearch" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>
	</c:catch>
	<c:if test="${not empty suError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="subjectsXmlDom_adminSearch" scope="application"/>	
	</c:if>
</c:if>


<%-- Request and cache the resource types as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty resourceTypesXmlDom_adminSearch || param.reload == 'true'}">
	<c:catch var="reError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListResourceTypes"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the resource types in an application scope XML DOM variable 'resourceTypesXmlDom_adminSearch' --%>				
		<x:parse var="resourceTypesXmlDom_adminSearch" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>
	</c:catch>
	<c:if test="${not empty reError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="resourceTypesXmlDom_adminSearch" scope="application"/>	
	</c:if>
</c:if>

<%-- Request and cache the collections as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty collectionsXmlDom_adminSearch || reload_admin_collection_menus == 'true' || param.reload == 'true'}">
	
	<c:catch var="kyError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListCollections"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the resource types in an application scope XML DOM variable 'collectionsXmlDom_adminSearch' --%>				
		<x:parse var="collectionsXmlDom_adminSearch" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>
	</c:catch>
	<c:if test="${not empty kyError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="collectionsXmlDom_adminSearch" scope="application"/>	
	</c:if>
</c:if>

<%-- Request and cache the content standards as an application scoped variable
        only if they have not been cached already --%>								
<c:if test="${empty contentStandardsXmlDom_adminSearch || param.reload == 'true'}">
	<c:catch var="csError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListContentStandards"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		
		<%-- Store and cache the resource types in an application scope XML DOM variable 'contentStandardsXmlDom_adminSearch' --%>				
		<x:parse var="contentStandardsXmlDom_adminSearch" scope="application">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>
	</c:catch>
	<c:if test="${not empty csError}">
		<%-- If an error has occured, make sure the xmlDom is deleted 
		so it will re-generate the next time the page is accessed --%>
		<c:remove var="contentStandardsXmlDom_adminSearch" scope="application"/>	
	</c:if>
</c:if>

