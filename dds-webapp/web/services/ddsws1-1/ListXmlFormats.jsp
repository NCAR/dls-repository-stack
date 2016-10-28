@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<DDSWebService xmlns="http://www.dlese.org/Metadata/ddsws" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.dlese.org/Metadata/ddsws http://www.dlese.org/Metadata/ddsws/1-1/ddsws.xsd">
	<ListXmlFormats>
		<c:forEach items="${df11.xmlFormats}" var="xmlFormat">	
			<xmlFormat>
				${xmlFormat}		
			</xmlFormat>
		</c:forEach>
	</ListXmlFormats>
</DDSWebService> 

