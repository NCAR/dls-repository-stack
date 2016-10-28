@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<DDSWebService>
	<UrlCheck>
		<resultInfo>
			<totalNumResults>${df.numResults}</totalNumResults>		
		</resultInfo>
		<c:if test="${not empty df.results}">
		<results>
		<c:forEach items="${df.results}" var="result" varStatus="item"><c:set var="docReader" value="${result.docReader}"/>			
			<matchingRecord>
				<url><c:out value="${docReader.url}" escapeXml="true"/></url>
				<%@ include file="HeadElement.jsp" %><%-- The header --%>			
			</matchingRecord>			
		</c:forEach>
		</results>
		</c:if>
	</UrlCheck>
</DDSWebService>  

