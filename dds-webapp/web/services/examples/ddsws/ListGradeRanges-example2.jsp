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
	<title>ListGradeRanges example 2</title> 
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
<h2>ListGradeRanges example 2</h2>

<a href="index.jsp">Back to examples index</a><br>

<h3>Information about this example</h3> 
<table border="1" cellpadding="2" cellspacing="0" style="background-color:#eeeeee" width="95%"> 
	<tr>
		<td colspan="2">
			This example renders a drop-down menu to select a grade range and displays 
			the first ten records in the library that are cataloged by
			the grade range after it has been selected by the user  
			[<a href="ListGradeRanges-example2.txt">view JSP code</a>]
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

<%-- Get input from the user --%>
<form action="">
	Choose a grade range:<br>
		
	<%-- Create a select list of grade ranges from the ListGradeRanges XML response --%>
	<select name="searchKey">
	<option value=""> -- Choose a grade range -- </option>
	<x:forEach select="$xmlDom/DDSWebService/ListGradeRanges/gradeRanges/gradeRange">
		<%-- Only display the grade range in the menu if it is not hidden --%>
		<x:if select="contains( renderingGuidelines/noDisplay, 'false' )">
			<%-- Set the selected grade range to the one chosen by the user --%>
			<c:set var="searchKey">
				<x:out select="searchKey"/> 
			</c:set>
			<c:choose>
				<c:when test="${searchKey == param.searchKey}">
					<c:set var="isSelected" value="selected"/>
				</c:when>
				<c:otherwise>
					<c:set var="isSelected" value=""/>
				</c:otherwise>
			</c:choose>
			<option ${isSelected} value='<x:out select="searchKey"/>'><x:out select="renderingGuidelines/label"/></option>
		</x:if>
	</x:forEach>
	</select>
	<input type="submit" value="Show records">
</form>

<%-- If the user has selected a grade range, display it's records --%>
<c:if test="${not empty param.searchKey}">

	<%-- Issue a second request to the web servce to search for the records with the selected grade range --%>
	<c:catch var="serviceError">
	
		<%-- Get the search field specifier used to search over grade ranges --%>
		<c:set var="searchField">
			<x:out select="$xmlDom/DDSWebService/ListGradeRanges/searchField"/>	
		</c:set>
		
		<%-- Construct the http request to send to the web service and store it in variable 'webServiceRequest' --%>
		<c:url value="@DDS_SERVER_BASE_URL@/services/ddsws1-1" var="webServiceRequest">
			<%-- Define each of the http parameters used in the request --%>
			<c:param name="verb" value="UserSearch"/>
			<%-- Search using the grade range search field and key --%>
			<c:param name="${searchField}" value="${param.searchKey}"/>
			
			<%-- Begin the result at offset 0 --%>
			<c:param name="s" value="0"/>
			<%-- Return 10 results --%>
			<c:param name="n" value="10"/>
			<%-- An option parameter used to log where requests come from --%>
			<c:param name="client" value="ddsws10examples"/>		
		</c:url>
		
		<%-- Issue the request to the web service server and store the response in variable 'xmlResponse' --%>	
		<c:import url="${webServiceRequest}" var="xmlResponse" charEncoding="UTF-8" />
		<%-- Parse the XML response and store it in an XML DOM variable named 'xmlDom2' --%>
		<x:parse var="xmlDom2">
			<c:out value="${xmlResponse}" escapeXml="false"/>
		</x:parse>	
	</c:catch>
	
	<%-- If the total number of results is greater than zero, display them --%>
	<x:if select="$xmlDom2/DDSWebService/Search/resultInfo/totalNumResults > 0">
		
		<%-- Store the searchKey of the selected grade range for use in the XPath expression below --%>	
		<c:set var="gradeRangeSearchKey" value="${param.searchKey}"/>
			
		<p><b>Records by grade range	
		  <i>
		    <%-- Output the grade range label in the ListGradeRanges XML for the grade range that is selected --%>
		    <x:out select="$xmlDom/DDSWebService/ListGradeRanges/gradeRanges/gradeRange[searchKey=$gradeRangeSearchKey]/renderingGuidelines/label"/>		        		
		  </i>
		</b></p>
		
		Displaying the first <x:out select="$xmlDom2/DDSWebService/Search/resultInfo/numReturned"/>
		records out of <x:out select="$xmlDom2/DDSWebService/Search/resultInfo/totalNumResults"/>:<br>
		[<a href="${webServiceRequest}">view XML response</a>]<br><br> 
		
		
		<%-- Store the gradeRanges XML element for use in the x:forEach loop below --%>
		<x:set var="gradeRangesElement" select="$xmlDom/DDSWebService/ListGradeRanges/gradeRanges"/>
		
		<%-- Loop through each of the record nodes --%>			
		<x:forEach select="$xmlDom2/DDSWebService/Search/results/record" var="record">			
			<table cellpadding="6" cellspacing="0" border="1" width="95%">
				<tr>
					<td width="90">
						Title:
					</td>
					<td>
						<b>
						<%-- We are already at XPath $xmlDom/DDSWebService/Search/results/record
						so we can continue our path from there --%>
						<x:out select="$record/metadata/itemRecord/general/title"/>
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
						Grade ranges:
					</td>				
					<td>
					<%-- For each of the grade ranges listed in this record, display their label --%>
					<x:forEach select="metadata/itemRecord/educational/audiences/audience">
						<%-- Store the grade range vocab entry for use in the XPath expression below --%>
						<c:set var="gradeRangeVocabEntry">
							<x:out select="gradeRange"/>
						</c:set>
						
						<%-- Display the grade range label extracted from the gradeRange element XML --%>
						<x:out select="$gradeRangesElement/gradeRange[vocabEntry=$gradeRangeVocabEntry]/renderingGuidelines/label"/>		
						<br>					
					</x:forEach>					
					
					</td>
				</tr>					
				<tr>
					<td width="90"> 
						From collection:
					</td>
					<td>
						<x:out select="head/collection"/>
					</td>
				</tr>		
			</table>
			<br><br>
		</x:forEach>
	</x:if>	
					
</c:if>
	
<br><br>

</c:if>

<br><br><br><br>

</body>
</html>


