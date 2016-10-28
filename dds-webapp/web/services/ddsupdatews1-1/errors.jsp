<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<DDSRepositoryUpdateService>
	<c:if test="${not empty ddsusf.errorCode}"><c:set var="errorCodeAttrib">code="${ddsusf.errorCode}"</c:set></c:if>
	<c:set var="requestVerb">requestVerb="${param.verb}"</c:set>
	<c:if test="${not empty ddsusf.errorMsg}">
		<error ${requestVerb} ${errorCodeAttrib}><c:out value="${ddsusf.errorMsg}" escapeXml="true"/></error>
	</c:if>
	<c:if test="${empty ddsusf.errorMsg}">
		<error ${requestVerb} ${errorCodeAttrib}>The request is not valid</error>
	</c:if>
</DDSRepositoryUpdateService>

