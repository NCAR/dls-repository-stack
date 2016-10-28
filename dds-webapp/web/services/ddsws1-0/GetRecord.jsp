@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<DDSWebService>
	<GetRecord>
		<record>
<c:if test="${empty param.view || param.view == 'head'}">		
	<c:set var="docReader" value="${df.results[0].docReader}"/><%@ include file="HeadElement.jsp" %>
</c:if>
<c:if test="${empty param.view || param.view == 'metadata'}">
			<metadata>
${df.recordXml}		
			</metadata>
</c:if>
		</record>
	</GetRecord>
</DDSWebService>

