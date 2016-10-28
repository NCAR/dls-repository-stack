@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %><%@ page import="edu.ucar.dls.services.dds.action.form.*" %><%@ page import="edu.ucar.dls.index.*" %>
<DDSWebService xmlns="http://www.dlese.org/Metadata/ddsws" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.dlese.org/Metadata/ddsws http://www.dlese.org/Metadata/ddsws/1-1/ddsws.xsd">
	<Search>
		<resultInfo>
			<totalNumResults>${df11.numResults}</totalNumResults>
			<totalNumRecordsInLibrary>${param.verb == 'UserSearch' ? rm.numDiscoverableADNResources : rm.numDiscoverableResources}</totalNumRecordsInLibrary>						
			<numReturned>${ df11.numResults - df11.s < df11.n ? ( df11.numResults - df11.s < 0 ? 0 : df11.numResults - df11.s ) : df11.n}</numReturned>
			<offset>${df11.s}</offset>
		</resultInfo>
		<%@ include file="FacetInfoElement.jsp" %>
		<c:if test="${df11.n > 0}">
			<results>
			<%
				// Use scriplet to loop for effiencey (<c:forEach> iterates over all items in a List/Collection upto begin unnecessarily)
				DDSServicesForm df11 = (DDSServicesForm)pageContext.getRequest().getAttribute("df11");
				ResultDocList resultDocList = df11.getResults();
				for(int i = df11.getS(); i < (df11.getS() + df11.getN()) && i < resultDocList.size(); i ++) {
					pageContext.setAttribute("docReader", ((ResultDoc)resultDocList.get(i)).getDocReader() );
			%> 
				<record><c:set value="${param.xmlFormat}" target="${docReader}" property="requestedXmlFormat"/>
				<c:if test="${f:contains(paramValues['response'],'score')}"><score>${docReader.score}</score></c:if><%-- The Lucene hit score --%>
	<c:set var="headDocReader" value="${docReader}"/><%@ include file="HeadElement.jsp" %><%-- The header --%>
				<c:if test="${(param['response.mode'] == 'allOff' && f:contains(paramValues['response'],'metadata')) || param['response.mode'] != 'allOff'}">
					<metadata>
	${docReader.requestedXml}		
					</metadata>
				</c:if>
	<%@ include file="RelationsElement.jsp" %>	
	<%@ include file="RespOutputElement.jsp" %><%-- Show content requested in the response ouput --%>
	<%@ include file="StoredContentElement.jsp" %><%-- Show content from stored Lucene fields, if requested --%>				
				</record>	
			<% // End for loop...
				}  
			%>
			</results>
		</c:if>
	</Search>
</DDSWebService>

