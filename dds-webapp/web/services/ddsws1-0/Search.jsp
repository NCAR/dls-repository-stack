@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %> 
<DDSWebService>
	<Search>
		<resultInfo>
			<totalNumResults>${df.numResults}</totalNumResults>
			<numReturned>${ df.numResults - df.s < df.n ? ( df.numResults - df.s < 0 ? 0 : df.numResults - df.s ) : df.n}</numReturned>
			<offset>${df.s}</offset>			
		</resultInfo>
		<results>
	<c:forEach items="${df.results}" var="result" begin="${df.s}" end="${df.s + df.n - 1}" varStatus="item">	
			<c:set var="docReader" value="${result.docReader}"/>
			<record>
<c:if test="${empty param.view || param.view == 'head'}">
<%@ include file="HeadElement.jsp" %><%-- The header --%>
</c:if>
				<c:if test="${empty param.view || param.view == 'metadata'}">
				<metadata>
<c:set value="${param.xmlFormat}" target="${docReader}" property="requestedXmlFormat"/>
${docReader.requestedXmlFormat}		
				</metadata>
				</c:if>
			</record>
	</c:forEach>
		</results>
	</Search>
</DDSWebService>
