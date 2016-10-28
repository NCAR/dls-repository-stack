<%--
	This JSP page constructs a display of the grade ranges, subjects, resource
	types, content standards and collections selected by the user for their 
	search. It is meant to be included inside an encompassing search JSP page.
	
	Example use:
	
	... 
	<%@ include file="user_selections.jsp" %>
	...	
--%>

<%-- Store the collections selections for display below --%>
<c:if test="${not empty queryForm.ky}">					
	<c:set var="selectionsDisplay">
		<c:if test="${not empty selectionsDisplay}">${selectionsDisplay} + </c:if>		
		<span class="selectionsTitle">Collection${fn:length(queryForm.ky) > 1 ? 's' : ''}:</span> 
		<span class="selectionsHighlight">
		<c:forEach var="paramValue" items="${queryForm.ky}" varStatus="status">
			<x:out select="$collectionsXmlDom_adminSearch/DDSWebService/ListCollections/collections/collection[searchKey=$paramValue]/renderingGuidelines/label"/><c:if test="${!status.last}">,</c:if>					
		</c:forEach>
		</span>
	</c:set>			
</c:if>

<c:if test="${not empty queryForm.sifmts && fn:length(queryForm.sifmts) > 0}">										
	<c:set var="selectionsDisplay">
		<c:if test="${not empty selectionsDisplay}">${selectionsDisplay} + </c:if>
		<span class="selectionsTitle">XML format${fn:length(queryForm.sifmts) > 1 ? 's' : ''}:</span>
		<span class="selectionsHighlight">
		<c:forEach var="paramValue" items="${queryForm.sifmts}" varStatus="status">
			${fn:substring(paramValue, 1, fn:length(paramValue))}<c:if test="${!status.last}">,</c:if>					
		</c:forEach>
		</span>
	</c:set>
</c:if> 


<c:if test="${not empty param.indexedAccessionStatuses}">												
	<c:set var="selectionsDisplay">
		<c:if test="${not empty selectionsDisplay}">${selectionsDisplay} + </c:if>
		<span class="selectionsTitle">Accession status${fn:length(paramValues.indexedAccessionStatuses) > 1 ? 'es' : ''}:</span> 
		<span class="selectionsHighlight">
		<c:forEach var="paramValue" items="${paramValues.indexedAccessionStatuses}" varStatus="status">
			${paramValue}<c:if test="${!status.last}">,</c:if>					
		</c:forEach>
		</span>
	</c:set>
</c:if> 

<c:if test="${not empty param.selectedIdMapperErrors}">												
	<c:set var="selectionsDisplay">
		<c:if test="${not empty selectionsDisplay}">${selectionsDisplay} + </c:if>		
		<span class="selectionsTitle">ID mapper error${fn:length(paramValues.selectedIdMapperErrors) > 1 ? 's' : ''}:</span>
		<span class="selectionsHighlight">
		<c:forEach var="paramValue" items="${paramValues.selectedIdMapperErrors}" varStatus="status">
			${paramValue == 'noerrors' ? 'Records showing no errors' : f:DPCErrorMsg(paramValue)}<c:if test="${!status.last}">,</c:if>					
		</c:forEach>
		</span>
	</c:set>
</c:if> 


<%-- Store the grade range selections for display below --%>
<c:if test="${not empty param.gr}">												
	<c:set var="selectionsDisplay">
		<c:if test="${not empty selectionsDisplay}">${selectionsDisplay} + </c:if>		
		<span class="selectionsTitle">Grades:</span> 
		<span class="selectionsHighlight">
		<c:forEach var="paramValue" items="${paramValues.gr}" varStatus="status">
			<x:out select="$gradeRangesXmlDom_adminSearch/DDSWebService/ListGradeRanges/gradeRanges/gradeRange[searchKey=$paramValue]/renderingGuidelines/label"/><c:if test="${!status.last}">,</c:if>					
		</c:forEach>
		</span>
	</c:set>
</c:if> 
<%-- Store the subjects selections for display below --%>
<c:if test="${not empty param.su}">					
	<c:set var="selectionsDisplay">
		<c:if test="${not empty selectionsDisplay}">${selectionsDisplay} + </c:if>
		<span class="selectionsTitle">Subject${fn:length(paramValues.su) > 1 ? 's' : ''}:</span> 
		<span class="selectionsHighlight">
		<c:forEach var="paramValue" items="${paramValues.su}" varStatus="status">
			<x:out select="$subjectsXmlDom_adminSearch/DDSWebService/ListSubjects/subjects/subject[searchKey=$paramValue]/renderingGuidelines/label"/><c:if test="${!status.last}">,</c:if>					
		</c:forEach>
		</span>
	</c:set>			
</c:if> 		
<%-- Store the resource type selections for display below --%>
<c:if test="${not empty param.re}">					
	<c:set var="selectionsDisplay">
		<c:if test="${not empty selectionsDisplay}">${selectionsDisplay} + </c:if>
		<span class="selectionsTitle">Resource type${fn:length(paramValues.re) > 1 ? 's' : ''}:</span> 
		<span class="selectionsHighlight">
		<c:forEach var="paramValue" items="${paramValues.re}" varStatus="status">
			<x:out select="$resourceTypesXmlDom_adminSearch/DDSWebService/ListResourceTypes/resourceTypes/resourceType[searchKey=$paramValue]/renderingGuidelines/label"/><c:if test="${!status.last}">,</c:if>					
		</c:forEach>
		</span>
	</c:set>			
</c:if> 
<%-- Store the content standards selections for display below --%>
<c:if test="${not empty param.cs}">					
	<c:set var="selectionsDisplay">
		<c:if test="${not empty selectionsDisplay}">${selectionsDisplay} + </c:if>
		<span class="selectionsTitle">Standard${fn:length(paramValues.cs) > 1 ? 's' : ''}:</span> 
		<span class="selectionsHighlight">
		<c:forEach var="paramValue" items="${paramValues.cs}" varStatus="status">
			<x:out select="$contentStandardsXmlDom_adminSearch/DDSWebService/ListContentStandards/contentStandards/contentStandard[searchKey=$paramValue]/renderingGuidelines/label"/><c:if test="${!status.last}">;</c:if>					
		</c:forEach>
		</span>
	</c:set>			
</c:if> 

 


<%-- Edit below to change the layout, look or feel as desired --%>
<c:if test="${not empty selectionsDisplay}"> 
	<nobr>Your selections:</nobr> ${selectionsDisplay}
</c:if>


