<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<DDSRepositoryUpdateService>
	<PutCollection>
		<responseDate>${ddsusf.responseDate}</responseDate>
		<result code="${ddsusf.resultCode}">${ddsusf.resultCode == 'success' ? 'Collection was successfully added or updated' : 'Not able to update or add collection'}</result>
		<recordUrl>${f:contextUrl(pageContext.request)}/services/ddsws1-1?verb=GetRecord&amp;id=<c:out value="${ddsusf.id}" escapeXml="true"/></recordUrl>
		<collectionInfo>
			<recordId><c:out value="${ddsusf.id}" escapeXml="true"/></recordId>
			<collectionKey>${ddsusf.collectionKey}</collectionKey>
			<xmlFormat>${ddsusf.xmlFormat}</xmlFormat>
		</collectionInfo>
	</PutCollection>
</DDSRepositoryUpdateService>
