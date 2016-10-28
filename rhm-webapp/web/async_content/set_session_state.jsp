<%@ include file="../TagLibIncludes.jsp" %>
<%-- This page sets session states for the given params --%>

<%-- Grab the answer record --%>
<c:if test="${not empty param.collTabs}">
	<c:set var="selectedCollTab" value="${param.collTabs}" scope="session"/>	
</c:if>
<c:if test="${not empty param.oaiTabs}">
	<c:set var="selectedOaiTab" value="${param.oaiTabs}" scope="session"/>	
</c:if>
<c:if test="${not empty param.harvestTabs}">
	<c:set var="selectedHarvestTab" value="${param.harvestTabs}" scope="session"/>	
</c:if>


