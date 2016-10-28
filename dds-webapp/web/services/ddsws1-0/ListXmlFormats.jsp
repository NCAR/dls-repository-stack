@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<DDSWebService>
	<ListXmlFormats>
		<c:forEach items="${df.xmlFormats}" var="xmlFormat">	
			<xmlFormat>
				${xmlFormat}		
			</xmlFormat>
		</c:forEach>
	</ListXmlFormats>
</DDSWebService> 

