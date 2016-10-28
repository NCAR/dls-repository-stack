@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<DDSWebService xmlns="http://www.dlese.org/Metadata/ddsws" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.dlese.org/Metadata/ddsws http://www.dlese.org/Metadata/ddsws/1-1/ddsws.xsd">
	<GetRecord>
		<record>
<c:set var="headDocReader" value="${df11.results[0].docReader}"/><%@ include file="HeadElement.jsp" %>
<c:if test="${(param['response.mode'] == 'allOff' && f:contains(paramValues['response'],'metadata')) || param['response.mode'] != 'allOff'}">
			<metadata>
${df11.recordXml}		
			</metadata>
</c:if>
<%@ include file="RelationsElement.jsp" %><%-- Show relations data, if requested --%>
<%@ include file="RespOutputElement.jsp" %><%-- Show content requested in the response ouput --%>
<%@ include file="StoredContentElement.jsp" %><%-- Show content from stored Lucene fields, if requested --%>
		</record>
	</GetRecord>
</DDSWebService>


