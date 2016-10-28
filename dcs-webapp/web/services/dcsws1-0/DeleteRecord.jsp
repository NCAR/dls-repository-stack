<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<DCSWebService>
	<DeleteRecord>
		<responseDate>${dcssf.responseDate}</responseDate>
		<recordId>${dcssf.id}</recordId>
		<result code="${dcssf.resultCode}">${dcssf.resultCode == 'success' ? 'Record was successfully deleted' : 'No such record exists in the repository'}</result>
	</DeleteRecord>
</DCSWebService>
