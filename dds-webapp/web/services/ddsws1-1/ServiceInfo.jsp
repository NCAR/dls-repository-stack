@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %><%@ include file="/ddswsBaseUrl.jsp" %><%-- Sets the DDSWS 1-1 baseURL into variable 'ddsws11BaseUrl' --%>

<DDSWebService xmlns="http://www.dlese.org/Metadata/ddsws" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.dlese.org/Metadata/ddsws http://www.dlese.org/Metadata/ddsws/1-1/ddsws.xsd">
	<ServiceInfo><%-- Outputs the serviceInfo. --%>
		<serviceName>${fn:trim(initParam.ddswsServiceName)}</serviceName>
		<description>${fn:trim(initParam.ddswsDescription)}</description>
		<adminEmail>${fn:trim(initParam.ddswsAdminEmail)}</adminEmail>		
		<baseURL>${ddsws11BaseUrl}</baseURL>
		<serviceVersion>1.1</serviceVersion>
		<indexVersion>${index.lastModifiedCount}</indexVersion>
		<indexCreatedOnDate>${f:dateToUtcDatestamp(index.creationDate)}</indexCreatedOnDate>
		<indexLastModifiedDate>${f:dateToUtcDatestamp(index.lastModifiedDate)}</indexLastModifiedDate>
		<compression>gzip</compression>
		<maxSearchResultsAllowed>${maxNumResultsDDSWS}</maxSearchResultsAllowed>
	</ServiceInfo>
</DDSWebService> 

