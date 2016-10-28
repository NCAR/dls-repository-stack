<%--
	This JSP page constructs a display of the grade ranges, subjects, resource
	types, content standards, collections smart links and menus selected by 
	the user for their search. It is meant to be included inside an 
	encompassing search JSP page.
	
	Example use:
	
	... 
	
	<%@ include file="user_selections.jsp" %>
	<c:if test="${not empty titleUserSelections}">
		<title> ${titleUserSelections} </title>
	</c:if>		
	
	<c:if test="${not empty selectedSmartLink}">
		Resources about: ${selectedSmartLink}
	</c:if>	
	
	<c:if test="${not empty userSelections}">
		Your selections: ${userSelections}
	</c:if>	

	...	
	
	This is JSP client version @DDSWS_CLIENT_VERSION@.		
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
<c:if test="${mySmartMenuDom != null}">
	<c:forTokens var="paramName" items="${smartLinkMenuParameterNames}" delims=" ">
		<c:if test="${not empty param[paramName]}">
			<c:set var="menuId" value="${fn:substringAfter(paramName,'slm')}"/>
			<c:set var="mySmartMenuDom" value="${applicationScope.smartLinkResources[mySmartMenuDomKey]}"/> 
			<c:choose>
				<%-- If this is a smart link... --%>
				<c:when test="${smartLinkOnlyParameterNames[paramName] != null}">
					<c:set var="paramValue" value="${param[paramName]}"/>
					<c:set var="selectedSmartLink">
						<c:set var="selectionsLabel">
							<x:out select="$mySmartMenuDom/root/menu[@id=$menuId]/@selectionsLabel" escapeXml="false"/>
						</c:set> 
						<c:if test="${not empty selectionsLabel}">${selectionsLabel} &gt;</c:if>
						<x:out select="$mySmartMenuDom/root/menu[@id=$menuId]/menuItem[@id=$paramValue]/label" escapeXml="false"/>
					</c:set>
				</c:when>
				<%-- If this is a menu item ... --%>
				<c:otherwise>
					<c:set var="selectionsLabel">
						<x:out select="$mySmartMenuDom/root/menu[@id=$menuId]/@selectionsLabel" escapeXml="false"/>
					</c:set> 				
					<c:set var="userSelections">
						<c:if test="${not empty userSelections}">${userSelections} +</c:if> 
						<c:if test="${not empty selectionsLabel}">${selectionsLabel}:</c:if>
						<c:forEach var="paramValue" items="${paramValues[paramName]}" varStatus="i">
							<x:out select="$mySmartMenuDom/root/menu[@id=$menuId]/menuItem[@id=$paramValue]/label" escapeXml="false"/><c:if test="${!i.last}">,</c:if>
						</c:forEach>
					</c:set>				
				</c:otherwise>
			</c:choose>
		</c:if>
	</c:forTokens>
</c:if>


