@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %>
<DDSWebService xmlns="http://www.dlese.org/Metadata/ddsws" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.dlese.org/Metadata/ddsws http://www.dlese.org/Metadata/ddsws/1-1/ddsws.xsd">
	<c:if test="${not empty df11.errorCode}"><c:set var="errorCodeAttrib">code="${df11.errorCode}"</c:set></c:if>
	<c:if test="${not empty df11.errorMsg}">
		<error ${errorCodeAttrib}><c:out value="${df11.errorMsg}" escapeXml="true"/></error>
	</c:if>
	<c:if test="${empty df11.errorMsg}">
		<error ${errorCodeAttrib}>The request is not valid</error>
	</c:if>
</DDSWebService>

