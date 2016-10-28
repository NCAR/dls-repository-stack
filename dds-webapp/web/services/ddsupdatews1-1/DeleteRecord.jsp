<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<DDSRepositoryUpdateService>
	<DeleteRecord>
		<responseDate>${ddsusf.responseDate}</responseDate>
		<recordId>${ddsusf.id}</recordId>
		<result code="${ddsusf.resultCode}">${ddsusf.resultCode == 'success' ? 'Record was successfully deleted' : 'No such record exists in the repository'}</result>
	</DeleteRecord>
</DDSRepositoryUpdateService>
