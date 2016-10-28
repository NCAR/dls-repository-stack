@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %> 
<DDSWebService xmlns="http://www.dlese.org/Metadata/ddsws" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.dlese.org/Metadata/ddsws http://www.dlese.org/Metadata/ddsws/1-1/ddsws.xsd">
	<ListTerms>
		<head>
			<fields>
				<c:forEach items="${paramValues.field}" var="field">
					<c:if test="${not empty fn:trim(field)}">
						<field>${field}</field>
					</c:if>
				</c:forEach>
			</fields>
			<indexVersion>${index.lastModifiedCount}</indexVersion>
			<totalNumTerms>${numTerms}</totalNumTerms>
		</head>
		<c:if test="${numTerms > 0}">
			<terms>
				<c:forEach var="term" items="${termAndDocCountsMap}">
				<term termCount="${term.value.termCount}" docCount="${term.value.docCount}"><c:out value="${term.key}" escapeXml="true"/></term></c:forEach>
			</terms>
		</c:if>
	</ListTerms>
</DDSWebService>
