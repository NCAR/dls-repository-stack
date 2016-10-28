@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>

<DDSWebService xmlns="http://www.dlese.org/Metadata/ddsws" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.dlese.org/Metadata/ddsws http://www.dlese.org/Metadata/ddsws/1-1/ddsws.xsd">

	<UrlCheck>

		<resultInfo>

			<totalNumResults>${df11.numResults}</totalNumResults>		

		</resultInfo>

		<c:if test="${not empty df11.results}">

		<results>

		<c:forEach items="${df11.results}" var="result" varStatus="item"><c:set var="docReader" value="${result.docReader}"/>			

			<matchingRecord>

				<url><c:out value="${docReader.url}" escapeXml="true"/></url>

				<c:set var="headDocReader" value="${docReader}"/><%@ include file="HeadElement.jsp" %><%-- The header --%>			

			</matchingRecord>			

		</c:forEach>

		</results>

		</c:if>

	</UrlCheck>

</DDSWebService>  



