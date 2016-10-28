<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<DDSRepositoryUpdateService>
	<DeleteCollection>
		<responseDate>${ddsusf.responseDate}</responseDate>
		<collectionKey>${ddsusf.collectionKey}</collectionKey>
		<result code="${ddsusf.resultCode}">${ddsusf.resultCode == 'success' ? 'Collection was successfully deleted' : 'No such collection exists in the repository'}</result>
	</DeleteCollection>
</DDSRepositoryUpdateService>
