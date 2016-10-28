<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<DCSWebService>
	<UrlCheck>
		<requestInfo>
			<url>${dcssf.url}</url>
			<c:if test="${not empty dcssf.collections}">
				<collections>
				<c:forEach var="c" 		
					items="${dcssf.collections}"><collection>${c}</collection></c:forEach>
				</collections>
			</c:if>
		</requestInfo>
		<resultInfo>
			<totalNumResults>${dcssf.numResults}</totalNumResults>		
		</resultInfo>
		<c:if test="${not empty dcssf.results}">
		<results>
		<c:forEach items="${dcssf.results}" var="result" varStatus="item"><c:set var="docReader" value="${result.docReader}"/>			
				<recordId><c:out value="${docReader.id}" escapeXml="true"/></recordId>		
		</c:forEach>
		</results>
		</c:if>
	</UrlCheck>
</DCSWebService>  

