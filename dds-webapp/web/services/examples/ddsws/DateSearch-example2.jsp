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
	<title>Date search example 2</title> 
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

<%-- Set which field to sort by --%>
<c:choose>
	<c:when test="${not empty param.sortBy}">
		<c:set var="sortBy" value="${param.sortBy}"/>
	</c:when>
	<c:otherwise>
		<c:set var="sortBy" value="title"/>
	</c:otherwise>
</c:choose>

<%-- Set the sort order --%>
<c:choose>
	<%-- For date fields, sort descending --%>
	<c:when test="${sortBy == 'wndate'}">
		<c:set var="sortOrder" value="sortDescendingBy"/>
	</c:when>
	<%-- For text fields, sort ascending --%>
	<c:otherwise>
		<c:set var="sortOrder" value="sortAscendingBy"/>
	</c:otherwise>
</c:choose>

<%-- Set up the variables used for paging through results--%>
<c:set var="startingOffset">
	<c:if test="${empty param.s}">0</c:if>
	<c:if test="${!empty param.s}"><c:out value="${param.s}"/></c:if>
</c:set>

<%-- Set how many results we want to display per page --%>
<c:set var="resultsPerPage" value="10"/>

<%-- Issue the request to the web service. If a connection error occurs, store it in variable 'serviceError' --%>
<c:catch var="serviceError">
	<%-- Construct the http request to send to the web service and store it in variable 'webServiceRequest' --%>
	<c:url value="@DDS_SERVER_BASE_URL@/services/ddsws1-1" var="webServiceRequest">
		<%-- Define each of the http parameters used in the request --%>
		<c:param name="verb" value="Search"/>
		<%-- Begin the result at offset 0 --%>
		<c:param name="s" value="${startingOffset}"/>
		<%-- Return resultsPerPage results --%>
		<c:param name="n" value="${resultsPerPage}"/>
		<%-- Indicate the date to search from --%>
		<c:param name="fromDate" value="${searchFromArgument}"/>
		<%-- Indicate the date field to search in (required) --%>
		<c:param name="dateField" value="wndate"/>
		<%-- Sort the results by date --%>
		<c:param name="${sortOrder}" value="${sortBy}"/>			
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
<h2>Date search example 2</h2>

<a href="index.jsp">Back to examples index</a><br>

<h3>Information about this example</h3> 
<table border="1" cellpadding="2" cellspacing="0" style="background-color:#eeeeee" width="95%"> 
	<tr>
		<td colspan="2">
			This example performs a search for records that are new to the library within the 
			last 28 days and allows the user to sort by date, title, URL, ID or collection.
			[<a href="DateSearch-example2.txt">view JSP code</a>]
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
	sorted by field '${sortBy}'</b></p>
	
	<x:choose>
		<%-- When there are some results to display... --%>
		<x:when select="$xmlDom/DDSWebService/Search/resultInfo/numReturned">
						
			<%-- Save the number of results in variable 'numResults' --%>
			<c:set var="numResults">
				<x:out select="$xmlDom/DDSWebService/Search/resultInfo/totalNumResults"/>
			</c:set>
			
			<%-- Create and store the HTML for the pager in variable 'pager' --%>
			<c:set var="pager">
				<nobr>
				<c:if test="${(startingOffset - resultsPerPage) >= 0}">	
					<%-- Construct a URL to this page that gets the previous page of results --%>
					<c:url value="" var="prevResultsUrl">
						<c:param name="s" value="${startingOffset - resultsPerPage}"/>
						<c:param name="sortBy" value="${sortBy}"/>												
					</c:url>						
					<a href="${prevResultsUrl}" title="Previous page of resources">&lt;&lt;</a> &nbsp;
				</c:if>				
				<c:forEach	begin="${ (startingOffset/resultsPerPage) - 4 >= 0 ? (startingOffset/resultsPerPage) - 4 : 0}" 
							end="${ ( (numResults-1)/resultsPerPage) - 4 >= (startingOffset/resultsPerPage) ? (startingOffset/resultsPerPage) + 4 : ( (numResults-1)/resultsPerPage)}" 
							varStatus="status">
					<c:choose>
						<c:when test="${ status.index == (startingOffset/resultsPerPage) }">
							<b>${ status.index + 1 }</b>
						</c:when>
						<c:otherwise>
							<%-- Construct a URL to this page that gets the nth page of results --%>
							<c:url value="" var="pageUrl">
								<c:param name="sortBy" value="${sortBy}"/>
								<c:param name="s" value="${resultsPerPage*status.index}"/>
							</c:url>							
							<a href="${pageUrl}">${ status.index + 1 }</a>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				<c:if test="${(startingOffset + resultsPerPage) < numResults}">					
					<%-- Construct a URL to this page that gets the next page of results --%>
					<c:url value="" var="nextResultsUrl">
						<c:param name="s" value="${startingOffset + resultsPerPage}"/>
						<c:param name="sortBy" value="${sortBy}"/>							
					</c:url>							
					&nbsp; <a href="${nextResultsUrl}" title="Next page of resources">&gt;&gt;</a>
				</c:if>
				</nobr>
			</c:set>		
			<%-- ------ end pager ------ --%>		
			
			
			<table width="100%">
				<tr>
					<td>
						<%-- Display the number of results --%>
						<nobr>
						Results
						<c:out value="${startingOffset +1}"/> 
						-
						<c:if test="${startingOffset + resultsPerPage > numResults}">
							<c:out value="${numResults}"/>
						</c:if>
						<c:if test="${startingOffset + resultsPerPage <= numResults}">
							<c:out value="${startingOffset + resultsPerPage}"/>
						</c:if>	
						</nobr>					
						<nobr>out of <c:out value="${numResults}"/>
						<%-- The pager and 'sorted by' label --%>
						sorted by 		
						<c:choose>
							<c:when test="${sortBy == 'title'}">
								Title
							</c:when>
							<c:when test="${sortBy == 'url'}">
								URL
							</c:when>
							<c:when test="${sortBy == 'wndate'}">
								Date in Library
							</c:when>			
							<c:when test="${sortBy == 'idvalue'}">
								ID
							</c:when>
							<c:when test="${sortBy == 'collection'}">
								Collection
							</c:when>	
						</c:choose>					
						</nobr>
					</td>
					<td align="right">
						${pager}
					</td>				
				</tr>
			</table>
			<br>
			
			<%-- Display a table of results --%>
			<table width="100%" bgcolor="#666666" cellpadding="6" cellspacing="1" border="0">
				<tr bgcolor='#CCCEE6'> 
					<td align="center" nowrap>
						<c:choose>
							<c:when test="${sortBy == 'title'}">
								<b>Title</b>
							</c:when>
							<c:otherwise>
								<c:url value="" var="sortByRequest">
									<c:param name="sortBy" value="title"/>
								</c:url>
								<a href="${sortByRequest}" title="Sort by title"><b>Title</b></a>
							</c:otherwise>
						</c:choose>				
					</td>
					<td align="center" nowrap>
						<c:choose>
							<c:when test="${sortBy == 'url'}">
								<b>URL</b>
							</c:when>
							<c:otherwise>
								<c:url value="" var="sortByRequest">
									<c:param name="sortBy" value="url"/>
								</c:url>
								<a href="${sortByRequest}" title="Sort by URL"><b>URL</b></a>
							</c:otherwise>
						</c:choose>					
					</td>
					<td align="center" nowrap>
						<c:choose>
							<c:when test="${sortBy == 'wndate'}">
								<b>Date in<br>Library</b>
							</c:when>
							<c:otherwise>
								<c:url value="" var="sortByRequest">
									<c:param name="sortBy" value="wndate"/>
								</c:url>
								<a href="${sortByRequest}" title="Sort by date"><b>Date in<br>Library</b></a>
							</c:otherwise>
						</c:choose>					
					</td>
					<td align="center" nowrap>
						<c:choose>
							<c:when test="${sortBy == 'idvalue'}">
								<b>ID</b>
							</c:when>
							<c:otherwise>
								<c:url value="" var="sortByRequest">
									<c:param name="sortBy" value="idvalue"/>
								</c:url>
								<a href="${sortByRequest}" title="Sort by id"><b>ID</b></a>
							</c:otherwise>
						</c:choose>				
					</td>
					<td align="center" nowrap>
						<c:choose>
							<c:when test="${sortBy == 'collection'}">
								<b>Collection</b>
							</c:when>
							<c:otherwise>
								<c:url value="" var="sortByRequest">
									<c:param name="sortBy" value="collection"/>
								</c:url>
								<a href="${sortByRequest}" title="Sort by collection"><b>Collection</b></a>	
							</c:otherwise>
						</c:choose>					
					</td>	    	  
				</tr>
		
				<%-- Loop through each of the record nodes --%>
				<x:forEach select="$xmlDom/DDSWebService/Search/results/record">			
					<tr bgcolor='#e3e4f1'> 
						<td align="left">
							<x:out select="metadata/itemRecord/general/title"/>				
						</td>
						<td align="left">
							<c:set var="primaryURL">
								<x:out select="metadata/itemRecord/technical/online/primaryURL"/>
							</c:set>
							<c:choose>
								<c:when test="${fn:length(primaryURL) > 50}">
									<a href="${primaryURL}" target="_blank">${fn:substring(primaryURL,0,50)} ...</a>
								</c:when>
								<c:otherwise>
									<a href="${primaryURL}" target="_blank">${primaryURL}</a>
								</c:otherwise>
							</c:choose>								
						</td>
						<td align="left" nowrap>
							<%-- Parse the whats new date from the record --%>
							<fmt:parseDate var="whatsNewDate" pattern="yyyy-MM-dd">
								<x:out select="head/whatsNewDate"/>
							</fmt:parseDate>
							<%-- Output the date in a nice format --%>
							<fmt:formatDate dateStyle="full"value="${whatsNewDate}" />
						</td>
						<td align="left" nowrap>
							<x:out select="head/id"/>	  
						</td>
						<td align="left" nowrap>  		
							<x:out select="head/collection"/>	  		  
						</td>	    	  
					</tr>			
				</x:forEach>	
			</table>
		</x:when>
		<%-- When there are no results to display... --%>
		<x:otherwise>
			There are no new records since <fmt:formatDate dateStyle="full"value="${searchFromDate}" />
		</x:otherwise>
	</x:choose>
	
</c:if>

<br><br><br><br>

</body>
</html>


