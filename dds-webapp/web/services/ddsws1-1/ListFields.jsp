@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %> 
<DDSWebService xmlns="http://www.dlese.org/Metadata/ddsws" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.dlese.org/Metadata/ddsws http://www.dlese.org/Metadata/ddsws/1-1/ddsws.xsd">
	<!--  Response may contain fields that are currently empty (no terms in them)  -->
	<ListFields>
		<head>
			<indexVersion>${index.lastModifiedCount}</indexVersion>
			<totalNumFields>${fn:length(index.fields)}</totalNumFields>
		</head>
		<c:if test="${fn:length(index.fields) > 0}">
			<fields>
				<c:forEach var="field" items="${index.fields}">
					<field>${field}</field></c:forEach>	
			</fields>
		</c:if>
	</ListFields>
</DDSWebService>

