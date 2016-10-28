<%--
	From JSP client version 2.5.		
--%>

<%-- Add the grade range selections... --%>
<c:if test="${not empty param.gr}">												
	<c:set var="userSelections">
		<c:if test="${not empty userSelections}">${userSelections} +</c:if> 
		Grade${fn:length(paramValues.gr) > 1 ? 's' : ''}: 
		<c:forEach var="paramValue" items="${paramValues.gr}" varStatus="status">
			<x:out select="$gradeRangesXmlDom/DDSWebService/ListGradeRanges/gradeRanges/gradeRange[searchKey=$paramValue]/renderingGuidelines/label"/><c:if test="${!status.last}">,</c:if>					
		</c:forEach>
	</c:set>
</c:if> 
<%-- Add the subjects selections...--%>
<c:if test="${not empty param.su}">					
	<c:set var="userSelections">
		<c:if test="${not empty userSelections}">${userSelections} +</c:if> 
		Subject${fn:length(paramValues.su) > 1 ? 's' : ''}: 
		<c:forEach var="paramValue" items="${paramValues.su}" varStatus="status">
			<x:out select="$subjectsXmlDom/DDSWebService/ListSubjects/subjects/subject[searchKey=$paramValue]/renderingGuidelines/label"/><c:if test="${!status.last}">,</c:if>					
		</c:forEach>
	</c:set>			
</c:if> 		
<%-- Add the resource type selections... --%>
<c:if test="${not empty param.re}">					
	<c:set var="userSelections">
		<c:if test="${not empty userSelections}">${userSelections} +</c:if> 
		Resource type${fn:length(paramValues.re) > 1 ? 's' : ''}: 
		<c:forEach var="paramValue" items="${paramValues.re}" varStatus="status">
			<x:out select="$resourceTypesXmlDom/DDSWebService/ListResourceTypes/resourceTypes/resourceType[searchKey=$paramValue]/renderingGuidelines/label"/><c:if test="${!status.last}">,</c:if>					
		</c:forEach>
	</c:set>			
</c:if> 
<%-- Add the content standard selections... --%>
<c:if test="${not empty param.cs}">					
	<c:set var="userSelections">
		<c:if test="${not empty userSelections}">${userSelections} +</c:if> 
		Standard${fn:length(paramValues.su) > 1 ? 's' : ''}: 
		<c:forEach var="paramValue" items="${paramValues.cs}" varStatus="status">
			<x:out select="$contentStandardsXmlDom/DDSWebService/ListContentStandards/contentStandards/contentStandard[searchKey=$paramValue]/renderingGuidelines/label"/><c:if test="${!status.last}">;</c:if>					
		</c:forEach>
	</c:set>			
</c:if> 
<%-- Add the collections selections... --%>
<c:if test="${not empty param.ky}">					
	<c:set var="userSelections">
		<c:if test="${not empty userSelections}">${userSelections} +</c:if> 
		Collection${fn:length(paramValues.ky) > 1 ? 's' : ''}: 
		<c:forEach var="paramValue" items="${paramValues.ky}" varStatus="status">
			<x:out select="$collectionsXmlDom/DDSWebService/ListCollections/collections/collection[searchKey=$paramValue]/renderingGuidelines/label"/><c:if test="${!status.last}">,</c:if>					
		</c:forEach>
	</c:set>			
</c:if>

<%-- Construct the smart link menu selections made by the user --%>
<c:forEach var="paramName" items="${jsformv11.smartLinkParameterNames}">
	<c:if test="${not empty param[paramName]}">
		<c:set var="userSelections">
			<c:if test="${not empty userSelections}">${userSelections} +</c:if> 
			${jsformv11.menuLabelsMap[paramName]}:
			<c:forEach var="paramValue" items="${paramValues[paramName]}" varStatus="i">
				<c:set var="key" value="${paramName}-${paramValue}"/>
				${jsformv11.menuItemsMap[key]}<c:if test="${!i.last}">,</c:if>
			</c:forEach>
		</c:set>
	</c:if>
</c:forEach>



