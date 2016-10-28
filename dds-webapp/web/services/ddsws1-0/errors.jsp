@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %>
<DDSWebService>
	<c:if test="${not empty df.errorMsg}">
		<error><c:out value="${df.errorMsg}" escapeXml="true"/></error>
	</c:if>
	<c:if test="${empty df.errorMsg}">
		<error>The request is not valid</error>
	</c:if>
</DDSWebService>

