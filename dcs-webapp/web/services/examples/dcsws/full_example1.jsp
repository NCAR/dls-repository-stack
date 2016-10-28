<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%-- Reference the JSTL tag libraries that we may use --%>
<%@ page language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ page isELIgnored ="false" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st"%>
<%-- Store the base URL to the service to use in the request below --%>
<c:set var="contextUrl"><%@ include file="/ContextUrl.jsp" %></c:set>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="ddswsBaseUrl" value="${contextUrl}/services/ddsws1-1"/>
<c:set var="title" value="Example Search Interface"/>

<%-- If the user has submitted some input, issue the request to the web service --%> 
<c:if test="${not empty param.q}">

	<%-- Set up the variables used for paging through results--%>
	<c:set var="startingOffset">
		<c:if test="${empty param.s}">0</c:if>
		<c:if test="${!empty param.s}"><c:out value="${param.s}"/></c:if>
	</c:set>

	<%-- Issue the request to the web service. If a connection error occurs, store it in variable 'serviceError' --%>
	<c:catch var="serviceError">
		<%-- Construct the http request to send to the web service and store it in variable 'webServiceRequest' --%>
		<c:url value="${ddswsBaseUrl}" var="webServiceRequest">
			<%-- Define each of the http parameters used in the request --%>
			<c:param name="verb" value="UserSearch"/>
			<%-- Perform a textual search using the user's input --%>
			<c:param name="q" value="${param.q}"/>
			<%-- Begin the result at the current offset --%>
			<c:param name="s" value="${startingOffset}"/>
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



<html>
<head>	
	<title>${title}</title>
	
	<%@ include file="/baseHTMLIncludes.jsp" %>
	<%-- Use CSS to format this page --%>
	<style type="text/css">
      <!--
		BODY {
			margin: 7px;
			padding: 0px;
			font-family: Arial, Helvetica, sans-serif; 
			font-size: 10pt; 			
		} 
		
		H1 { 
			Font-Family : Arial, Helvetica, sans-serif ; 
			Font-Size : 14pt ;
			Color : #333366; 
		} 

		H2 { 
			Font-Family : Arial, Helvetica, sans-serif ; 
			Font-Size : 12pt ;
			Color : #333366; 
		} 
			  
		TD { 
			font-family: Arial, Helvetica, sans-serif; 
			font-size: 10pt; 
		} 
			  	  
		A { text-decoration: underline; } 
		
		A:link { 
			Font-Family: Arial, Helvetica, sans-serif;
			Color: #333366
		} 
		
		A:active {
			Color: #333366;
			text-decoration: none;
		} 
		
		/* A:visited { Color: #333366 } */
		
		/* A pseudo class 'blackul' for the A tag */
		A.blackul:link {
			color: #333333; 
			text-decoration: none; 
			font-weight: bold;
			font-size: 11pt; 	
		}
		
		A.blackul:visited {
			color: #333333; 
			text-decoration: none; 
			font-weight: bold;
			font-size: 11pt; 	
		}

		.description { 
			padding-left: 10px; 	
			padding-right: 14px; 		
			padding-bottom: 8px; 
		} 		
      -->
    </style>	
</head>

<%-- Use javascript to put the curser in the search box --%>
<body onload="document.searchForm.q.focus();">

	<st:pageHeader toolLabel="${title}" currentTool="services" />
	
	<st:breadcrumbs>
		Services                                            
		<st:breadcrumbArrow />
		<a  href="${contextUrl}/admin/admin.do?page=services">Web Services</a>
		<st:breadcrumbArrow />
		<span class="current">${title}</span>
	</st:breadcrumbs>

<p>This example is accessing the following Service Base URL: ${ddswsBaseUrl}<p>
<h3>Example Service-Powered Search Interface</h3>

<p>
<form action="full_example1.jsp" name="searchForm">
	Enter a search:
	<input size="30" type="text" name="q" value="<c:out value='${param.q}' escapeXml='true'/>">
	<input type="submit" value="Search">
</form>
</p>

<%-- If the user did not type any text, send a message --%>
<c:if test="${ param.q !=null && empty param.q }"> 
	You did not define a search. Please enter some text into the box above.
</c:if>

<%-- If the user typed a search, display the results --%>
<c:if test="${not empty param.q}">
	
	<%-- If a connection error has occured, send a message to the user --%>
	<c:if test="${not empty serviceError}">
		Sorry, our system has encountered a problem and we were unable to perform your 
		search at this time. Please try again later.
	</c:if>
	
	<%-- Display the record metadata to the user (if no connection error occured) --%> 
	<c:if test="${empty serviceError}">

	<x:choose>
		<%-- If the total number of results is greater than zero, display them --%>
		<x:when select="$xmlDom/DDSWebService/Search/resultInfo/totalNumResults > 0">
		
			<%-- ------ Begin paging logic ------ --%>
			
			<%-- Save the number of results in variable 'numResults' --%>
			<c:set var="numResults">
				<x:out select="$xmlDom/DDSWebService/Search/resultInfo/totalNumResults"/>
			</c:set>
			
			<%-- Construct a URL to this page that gets the next page of results --%>
			<c:url value="" var="nextResultsUrl">
				<c:param name="q" value="${param.q}"/>
				<c:param name="s" value="${startingOffset + 10}"/>
			</c:url>
			<%-- Construct a URL to this page that gets the previous page of results --%>
			<c:url value="" var="prevResultsUrl">
				<c:param name="q" value="${param.q}"/>	
				<c:param name="s" value="${startingOffset - 10}"/>
			</c:url>
			
			<%-- Create and store the HTML for the pager in variable 'pager' --%>
			<c:set var="pager">
				<c:if test="${(startingOffset - 10) >= 0}">
					<nobr><a href='<c:out value="${prevResultsUrl}"/>'>&lt;&lt; Prev</a> &nbsp;</nobr>
				</c:if>
				<nobr>
				${numResults > 1 ? 'results':'result'}
				<c:out value="${startingOffset +1}"/> 
				-
				<c:if test="${startingOffset + 10 > numResults}">
					<c:out value="${numResults}"/>
				</c:if>
				<c:if test="${startingOffset + 10 <= numResults}">
					<c:out value="${startingOffset + 10}"/>
				</c:if>	
				</nobr>					
				<nobr>out of <c:out value="${numResults}"/></nobr>				
				<c:if test="${(startingOffset + 10) < numResults}">
					<nobr>&nbsp; <a href='<c:out value="${nextResultsUrl}"/>'>Next &gt;&gt;</a></nobr>
				</c:if>
			</c:set>		
			<%-- ------ end paging logic ------ --%>
		

			<table  width="100%" cellpadding="4" cellspacing="0">
				<tr>
					<td>
						<nobr>Your search for</nobr> <i>${param.q}</i> <nobr>had ${numResults} ${numResults > 1 ? 'results':'result'}</nobr>
					</td>
					<td align="right">
						<p>${pager}</p>
					</td>				
				</tr>
			
				<%-- Render a gray line --%>
				<tr>
					<td colspan="2">
						<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0">
							<td bgcolor="#999999" height="1"></td>
						</table>	
					</td>
				</tr>
				
				<%-- Loop through each of the record nodes --%>
				<x:forEach select="$xmlDom/DDSWebService/Search/results/record">
				<tr>
					<td colspan="2">
						<%-- We are already at XPath $xmlDom/DDSWebService/Search/results/record
						so we can continue our path from there --%>
						<a href='<x:out select="metadata/itemRecord/technical/online/primaryURL"/>' 
							class="blackul"><x:out select="metadata/itemRecord/general/title"/></a><br>
						<a href='<x:out select="metadata/itemRecord/technical/online/primaryURL"/>'><x:out select="metadata/itemRecord/technical/online/primaryURL"/></a>					
					</td>			
				</tr>
				<tr>
					<td colspan="2">
						<div class="description">
							<x:out select="metadata/itemRecord/general/description"/>
						</div>
					</td>			
				</tr>		
				</x:forEach>

				<tr>
					<td>
						&nbsp;
					</td>
					<td align="right">
						<p>${pager}</p>
					</td>				
				</tr>				
			</table>
						
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


