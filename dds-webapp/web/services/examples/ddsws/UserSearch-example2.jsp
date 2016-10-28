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
	<title>UserSearch example 2</title> 
</head>
<body>

<%-- If the user has submitted some input, issue the request to the web service --%> 
<c:if test="${not empty param.q}">
	<%-- Issue the request to the web service. If a connection error occurs, store it in variable 'serviceError' --%>
	<c:catch var="serviceError">
		<%-- Construct the http request to send to the web service and store it in variable 'webServiceRequest' --%>
		<c:url value="@DDS_SERVER_BASE_URL@/services/ddsws1-1" var="webServiceRequest">
			<%-- Define each of the http parameters used in the request --%>
			<c:param name="verb" value="UserSearch"/>
			<%-- Perform a textual search using the user's input --%>
			<c:param name="q" value="${param.q}"/>
			<%-- Begin the result at offset 0 --%>
			<c:param name="s" value="0"/>
			<%-- Return 10 results --%>
			<c:param name="n" value="10"/>
			<%-- An option parameter used to log where requests come from --%>
			<c:param name="client" value="ddsws10examples"/>		
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
<h2>UserSearch example 2</h2>

<a href="index.jsp">Back to examples index</a><br>

<h3>Information about this example</h3> 
<table border="1" cellpadding="2" cellspacing="0" style="background-color:#eeeeee" width="95%"> 
	<tr>
		<td colspan="2">
			This example performs a textual search using the text entered by the user and
			displays the first ten results
			[<a href="UserSearch-example2.txt">view JSP code</a>]
		</td>
	</tr>
	<tr>
		<td>Web service request<br>that is used in this example:</td>
		<td><a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#UserSearch">UserSearch</a></td>
	</tr>
	<c:if test="${not empty param.q}">
	<tr>
		<td>The http request<br> that is sent to the web service:</td>
		<td>${webServiceRequest}</td>
	</tr>
	<tr>
		<td>The XML response<br> that is returned by the web service:</td>
		<td>[<a href="${webServiceRequest}">view XML response</a>]</td>
	</tr>
	</c:if>
</table>
</p> 


<h3>Get input from the user</h3>

Enter text to search for:
<form action="">
	<input size="30" type="text" name="q" value="${param.q}">
	<input type="submit" value="Search">
</form>

<%-- If the user did not type any text, send a message --%>
<c:if test="${ param.q !=null && empty param.q }"> 
	You did not define a search. Please enter some text into the box above.
</c:if>

<%-- If the user typed a search, display the results --%>
<c:if test="${not empty param.q}">
	<h3>The web service response rendered as HTML</h3>
	
	<%-- If a connection error has occured, send a message to the user --%>
	<c:if test="${not empty serviceError}">
		<b>ERROR - unable to connect with the server</b>. Error message: ${serviceError}
	</c:if>
	
	<%-- Display the record metadata to the user (if no connection error occured) --%> 
	<c:if test="${empty serviceError}">

	<x:choose>
		<%-- If the total number of results is greater than zero, display them --%>
		<x:when select="$xmlDom/DDSWebService/Search/resultInfo/totalNumResults > 0">
			<p><b>Records that contain the text <i>${param.q}</i></b></p>
			
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
							<x:out select="metadata/itemRecord/general/title"/>
							</b>
						</td>
					</tr>
					<tr>
						<td valign="top" width="90">
							Description:
						</td>
						<td>
							<x:out select="metadata/itemRecord/general/description"/>
						</td>
					</tr>	
					<tr>
						<td width="90">
							URL:
						</td>
						<td>
							<c:set var="primaryURL">
								<x:out select="metadata/itemRecord/technical/online/primaryURL"/>
							</c:set>
							<a href="${primaryURL}">${primaryURL}</a>
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
	
		<%-- If there were no matches, report to user --%>
		<x:otherwise>
			Your search for <i>${param.q}</i> had no matches. 	
		</x:otherwise>		
	</x:choose>
			
	</c:if>

</c:if>

<br><br><br><br>

</body>
</html>


