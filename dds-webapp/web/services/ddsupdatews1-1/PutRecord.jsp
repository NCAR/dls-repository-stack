<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<DDSRepositoryUpdateService>
	<PutRecord>
		<responseDate>${ddsusf.responseDate}</responseDate>
		<result code="${ddsusf.resultCode}">${ddsusf.resultCode == 'success' ? 'Record was successfully added or updated' : 'Not able to update or add record'}</result>
		<recordUrl>${f:contextUrl(pageContext.request)}/services/ddsws1-1?verb=GetRecord&amp;id=<c:out value="${ddsusf.id}" escapeXml="true"/></recordUrl>
		<recordInfo>
			<recordId><c:out value="${ddsusf.id}" escapeXml="true"/></recordId>
			<collectionKey>${ddsusf.collectionKey}</collectionKey>
			<xmlFormat>${ddsusf.xmlFormat}</xmlFormat>
		</recordInfo>
	</PutRecord>
</DDSRepositoryUpdateService>
