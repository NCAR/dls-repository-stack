@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %><c:set var="servicesUrl"><%@ include file="ServiceUrl.jsp" %></c:set>
<DDSWebService xmlns="http://www.dlese.org/Metadata/ddsws" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.dlese.org/Metadata/ddsws http://www.dlese.org/Metadata/ddsws/1-1/ddsws.xsd">
	<ServiceInfo><%-- Outputs the serviceInfo. --%>
		<serviceName>Digital Discovery System Web Service (DDSWS)</serviceName>
		<baseURL>${fn:trim(servicesUrl)}</baseURL>
		<serviceVersion>1.1</serviceVersion>
		<adminEmail>${adminEmailDDSWS}</adminEmail>
		<compression>gzip</compression>
		<description>
			The Digital Discovery System Web Service (DDSWS) is a Lucene based search and retrieval service API for items that reside in a digital repository. 
			The service may be used to search over and retrieve items from it's repository in real-time, and is designed to be used by high-availability applications.		
		</description>
	</ServiceInfo>
</DDSWebService> 

