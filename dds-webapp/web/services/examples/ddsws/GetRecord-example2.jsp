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
	<title>GetRecord example 2</title> 
</head>
<body>

<%-- If the user has submitted some input, issue the request to the web service --%> 
<c:if test="${not empty param.id}">
	<%-- Issue the request to the web service. If a connection error occurs, store it in variable 'serviceError' --%>
	<c:catch var="serviceError">
		<%-- Construct the http request to send to the web service and store it in variable 'webServiceRequest' --%>
		<c:url value="@DDS_SERVER_BASE_URL@/services/ddsws1-1" var="webServiceRequest">
			<%-- Define each of the http parameters used in the request --%>
			<c:param name="verb" value="GetRecord"/>
			<c:param name="id" value="${param.id}"/>
		</c:url>
		
		<%-- Issue the request to the web service server and store the response in variable 'xmlResponse' --%>	
		<c:import url="${webServiceRequest}" var="xmlResponse" charEncoding="UTF-8" />
		<%-- Parse the XML response and store it in an XML DOM variable named 'xmlDom' --%>
		<x:parse var="xmlDom">
			<c:out value="${xmlResponse}" escapeXml="false"/>
		</x:parse>	
	</c:catch>
</c:if>


<p>
<h2>GetRecord example 2</h2>

<a href="index.jsp">Back to examples index</a><br>

<h3>Information about this example</h3>
<table border="1" cellpadding="2" cellspacing="0" style="background-color:#eeeeee" width="95%"> 
	<tr>
		<td colspan="2">
				Displays the title, description and URL from a single ADN record
				and allows the user to enter the ID of the record to display
				[<a href="GetRecord-example2.txt">view JSP code</a>]
		</td>
	</tr>
	<tr>
		<td>Web service request<br>that is used in this example:</td>
		<td><a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#GetRecord">GetRecord</a></td>
	</tr>
	<c:if test="${not empty webServiceRequest}">
		<tr>
			<td>The http request<br> that was sent to the web service:</td>
			<td>${webServiceRequest}</td>
		</tr>
		<tr>
			<td>The XML response<br> that was returned by the web service:</td>
			<td>[<a href="${webServiceRequest}">view XML response</a>]</td>
		</tr>
	</c:if>
</table>
</p> 

<h3>Get input from the user</h3>

Enter the ID for an ADN record to view (for example, try DLESE-000-000-000-001):
<form action="">
	<input size="30" type="text" name="id" value="${param.id}">
	<input type="submit" value="view record">
</form>

<%-- If the user has submitted an ID to view, display the response (or the error) --%>
<c:if test="${not empty param.id}">

	<%-- Display the record metadata to the user (if no connection error occured) --%> 
	<c:if test="${empty serviceError}">
	
	<h3>The web service response rendered as HTML</h3>
	
	<%-- If a connection error has occured, send a message to the user --%>
	<c:if test="${not empty serviceError}">
		<b>ERROR - unable to connect with the server</b>. Error message: ${serviceError}
	</c:if>

	<x:choose>		
		<%-- Display the record metadata if it has been returned successfully by the web service --%>
		<x:when select="$xmlDom/DDSWebService/GetRecord/record/metadata/itemRecord/general/title">		
			<p><b>The metadata from a single ADN record:</b></p>
			<table cellpadding="6" cellspacing="0" border="1" width="95%">
				<tr>
					<td>
						Title:
					</td>
					<td>
						<b>
						<%-- Use XPath to retreive the content in the XML response --%>
						<x:out select="$xmlDom/DDSWebService/GetRecord/record/metadata/itemRecord/general/title"/>
						</b>
					</td>
				</tr>
				<tr>
					<td valign="top">
						Description:
					</td>
					<td>
						<x:out select="$xmlDom/DDSWebService/GetRecord/record/metadata/itemRecord/general/description"/>
					</td>
				</tr>	
				<tr>
					<td>
						URL:
					</td>
					<td>
						<c:set var="primaryURL">
							<x:out select="$xmlDom/DDSWebService/GetRecord/record/metadata/itemRecord/technical/online/primaryURL"/>
						</c:set>
						<a href="${primaryURL}">${primaryURL}</a>
					</td>
				</tr>
				<tr>
					<td> 
						DLESE catalog ID:
					</td>
					<td>
						<x:out select="$xmlDom/DDSWebService/GetRecord/record/head/id"/>
					</td>
				</tr>	
			</table>	
		</x:when>
	
		<%-- Display the web service error message if one was provided --%>
		<x:when select="$xmlDom/DDSWebService/error">
			Sorry, we were unable to access the ID you requested '${param.id}'. 
			The reason provided was: <i><x:out select="$xmlDom/DDSWebService/error"/></i>	
		</x:when>
		
		<%-- If we couldn't find the title and there wasn't an error, this must not be an ADN record --%>
		<x:otherwise>
			The record you requested, '${param.id}' is not available in the ADN format.
			Please try again.
		</x:otherwise>	
	</x:choose>


	
	</c:if>

</c:if>

<br><br><br><br>

</body>
</html>


