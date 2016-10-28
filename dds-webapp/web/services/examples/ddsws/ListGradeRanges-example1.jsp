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
	<title>ListGradeRanges example 1</title> 
</head>
<body>

<%-- Issue the request to the web service. If a connection error occurs, store it in variable 'serviceError' --%>
<c:catch var="serviceError">
	<%-- Construct the http request to send to the web service and store it in variable 'webServiceRequest' --%>
	<c:url value="@DDS_SERVER_BASE_URL@/services/ddsws1-1" var="webServiceRequest">
		<%-- Define each of the http parameters used in the request --%>
		<c:param name="verb" value="ListGradeRanges"/>	
	</c:url>
	
	<%-- Issue the request to the web service server and store the response in variable 'xmlResponse' --%>	
	<c:import url="${webServiceRequest}" var="xmlResponse" charEncoding="UTF-8" />
	<%-- Parse the XML response and store it in an XML DOM variable named 'xmlDom' --%>
	<x:parse var="xmlDom">
		<c:out value="${xmlResponse}" escapeXml="false"/>
	</x:parse>	
</c:catch>


<p>
<h2>ListGradeRanges example 1</h2>

<a href="index.jsp">Back to examples index</a><br>

<h3>Information about this example</h3> 
<table border="1" cellpadding="2" cellspacing="0" style="background-color:#eeeeee" width="95%"> 
	<tr>
		<td colspan="2">
			This example displays a list of grade ranges that are available in the library 
			[<a href="ListGradeRanges-example1.txt">view JSP code</a>]
		</td>
	</tr>
	<tr>
		<td>Web service request<br>that is used in this example:</td>
		<td><a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListGradeRanges">ListGradeRanges</a></td>
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

<%-- Display the response to the user (if no connection error occured) --%> 
<c:if test="${empty serviceError}">

<p><b>Information about the collections in the library</b></p>

<table cellpadding="6" cellspacing="0" border="1" width="95%">
	<tr>
		<th>
			<nobr>Grade range label<br>
			displayed to users</nobr>
		</th>
		<th>
			<nobr>Grade ranges's<br>vocab entry</nobr>
		</th>
		<th>
			<nobr>Search<br>key</nobr>
		</th>
		<th>
			<nobr>Display<br>to users?</nobr>
		</th>					
	</tr>

	<%-- Loop through each of the collection nodes --%>
	<x:forEach select="$xmlDom/DDSWebService/ListGradeRanges/gradeRanges/gradeRange">
		<tr>
			<td>
				<%-- We are already at XPath $xmlDom/DDSWebService/ListGradeRanges/gradeRanges/gradeRange
				so we can continue our path from there --%>				
				<x:out select="renderingGuidelines/label"/>
			</td>
			<td>
				<x:out select="vocabEntry"/>
			</td>
			<td>
				<x:out select="searchKey"/>
			</td>
			<td>
				<%-- Choose whether this should be displayed to users in menus --%>
				<x:choose>
					<x:when select="renderingGuidelines/noDisplay = 'true'">no</x:when>
					<x:otherwise>yes</x:otherwise>
				</x:choose>
			</td>					
		</tr>	
	</x:forEach>

</table>
<br><br>

</c:if>

<br><br><br><br>

</body>
</html>


