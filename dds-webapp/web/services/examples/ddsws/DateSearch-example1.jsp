<%--
	Example JSP for displaying information using the DDSWebService (ddsws v1.1)
	See @DDS_SERVER_BASE_URL@/services/
	
	Provided by the Digital Library for Earth System Education (DLESE)
	free and with no warranty. This example may be copied and modified.
	e-mail: support@dlese.org

	These examples are available for download at 
	http://sourceforge.net/project/showfiles.php?group_id=23991&package_id=123037
	
	Requires Apache Tomcat 5 (http://jakarta.apache.org/tomcat/)
	or other JSP 2.0 compliant JSP container and the 
	JSTL v1.1 tag libraries, which are included in the examples download
	or can be obtained from http://java.sun.com (standard.jar and jstl.jar).	
--%>

<%-- Reference the JSTL tag libraries that we may use --%>
<%@ page language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ page isELIgnored ="false" %>

<html>
<head>	
	<title>Date search example 1</title> 
</head>
<body>


<%-- Calculate the date 28 days ago --%>
<c:set var="now" value="<%= new java.util.Date() %>"/>
<c:set var="dayInYear"><fmt:formatDate pattern="D" value="${now}"/></c:set>
<c:set var="year"><fmt:formatDate pattern="yyyy" value="${now}"/></c:set>
<%-- Subtract 28 days from today's date and compensate for overlapping the year --%>
<c:set var="dayInYear" value="${dayInYear - 28}"/>
<c:if test="${dayInYear < 1}">
	<c:set var="dayInYear" value="${dayInYear + 365}"/>
	<c:set var="year" value="${year - 1}"/>
</c:if>
<fmt:parseDate var="searchFromDate" value="${dayInYear}-${year}" pattern="D-yyyy"/>
<%-- Create the date argument of the required form 'yyyy-MM-dd' to be 
		sent in the Web service request --%>
<c:set var="searchFromArgument">
	<fmt:formatDate pattern="yyyy-MM-dd" value="${searchFromDate}"/> 
</c:set>

<%-- Issue the request to the web service. If a connection error occurs, store it in variable 'serviceError' --%>
<c:catch var="serviceError">
	<%-- Construct the http request to send to the web service and store it in variable 'webServiceRequest' --%>
	<c:url value="@DDS_SERVER_BASE_URL@/services/ddsws1-1" var="webServiceRequest">
		<%-- Define each of the http parameters used in the request --%>
		<c:param name="verb" value="Search"/>
		<%-- Begin the result at offset 0 --%>
		<c:param name="s" value="0"/>
		<%-- Return 10 results --%>
		<c:param name="n" value="50"/>
		<%-- Indicate the date to search from --%>
		<c:param name="fromDate" value="${searchFromArgument}"/>
		<%-- Indicate the date field to search in (required) --%>
		<c:param name="dateField" value="wndate"/>
		<%-- Sort the results by date --%>
		<c:param name="sortDescendingBy" value="wndate"/>			
		<%-- Limit our search to only ADN records --%>
		<c:param name="xmlFormat" value="adn-localized"/>			
		<%-- An optional parameter used to log where requests come from --%> 
		<c:param name="client" value="ddsws10examples"/>		
	</c:url>
	
	<%-- Issue the request to the web service server and store the response in variable 'xmlResponse' --%>	
	<c:import url="${webServiceRequest}" var="xmlResponse" charEncoding="UTF-8" />
	<%-- Parse the XML response and store it in an XML DOM variable named 'xmlDom' --%>
	<x:parse var="xmlDom">
		<c:out value="${xmlResponse}" escapeXml="false"/>
	</x:parse>	
</c:catch>


<p>
<h2>Date search example 1</h2>

<a href="index.jsp">Back to examples index</a><br>

<h3>Information about this example</h3> 
<table border="1" cellpadding="2" cellspacing="0" style="background-color:#eeeeee" width="95%"> 
	<tr>
		<td colspan="2">
			This example performs a search for records that are new to the library within the 
			last 28 days and sorts the results by their date.
			[<a href="DateSearch-example1.txt">view JSP code</a>]
		</td>
	</tr>
	<tr>
		<td>Web service request<br>that is used in this example:</td>
		<td><a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#Search">Search</a></td>
	</tr>
	<tr>
		<td>The http request<br> that is sent to the web service:</td>
		<td>${webServiceRequest}</td>
	</tr>
	<tr>
		<td>The XML response<br> that is returned by the web service:</td>
		<td>[<a href="${webServiceRequest}">view XML response</a>]</td>
	</tr>
</table>
</p> 


<h3>The web service response rendered as HTML</h3>

<%-- If a connection error has occured, send a message to the user --%>
<c:if test="${not empty serviceError}">
	<b>ERROR - unable to connect with the server</b>. Error message: ${serviceError}
</c:if>

<%-- Display the record metadata to the user (if no connection error occured) --%> 
<c:if test="${empty serviceError}">

<p><b>Records that are new to the library since <fmt:formatDate dateStyle="full"value="${searchFromDate}" /> (28 days ago),
sorted by date</b></p>

<x:choose>
	<x:when select="$xmlDom/DDSWebService/Search/resultInfo/numReturned">
	
		Displaying the first <x:out select="$xmlDom/DDSWebService/Search/resultInfo/numReturned"/>
		results out of <x:out select="$xmlDom/DDSWebService/Search/resultInfo/totalNumResults"/> 
		matches:<br><br>
		
		<%-- Loop through each of the record nodes --%>
		<x:forEach select="$xmlDom/DDSWebService/Search/results/record">
			<table cellpadding="6" cellspacing="0" border="1" width="95%">
				<tr>
					<td width="90">
						Title:
					</td>
					<td>
						<b>
						<%-- We are already at XPath $xmlDom/DDSWebService/Search/results/record
						so we can continue our path from there --%>
						<x:out select="metadata/itemRecord/general/title"/><br>
						<c:set var="primaryURL">
							<x:out select="metadata/itemRecord/technical/online/primaryURL"/>
						</c:set>
						<a href="${primaryURL}"  target="_blank">${primaryURL}</a>
						</b>
					</td>
				</tr>		
				<tr>
					<td valign="top" width="90">
						New to the library on:
					</td>
					<td>
						<%-- Parse the whats new date from the record --%>
						<fmt:parseDate var="whatsNewDate" pattern="yyyy-MM-dd">
							<x:out select="head/whatsNewDate"/>
						</fmt:parseDate>
						<%-- Output the date in a nice format --%>
						<fmt:formatDate dateStyle="full"value="${whatsNewDate}" />
					</td>
				</tr>		
				<tr>
					<td width="90"> 
						DLESE catalog ID:
					</td>
					<td>
						<x:out select="head/id"/>
					</td>
				</tr>		
			</table>
			<br><br>
		</x:forEach>	
	</x:when>
	<x:otherwise>
		There are no new records since <fmt:formatDate dateStyle="full"value="${searchFromDate}" />
	</x:otherwise>
</x:choose>

</c:if>

<br><br><br><br>

</body>
</html>


