<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:catch var="xmlParseError">
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${param.xml}" var="myRec"/>
	<x:set var="rootElement" select="$myRec/commParadata "/>							
</c:catch>
<c:choose>
	<c:when test="${not empty xmlParseError}">
		<div style="color:red;">Error parsing record: ${xmlParseError}</div>
	</c:when>
	<c:otherwise>
	
		<%-- Compute Values --%>
		
		<c:set var="usageDataProvidedForName">
			<x:out select="$rootElement/usageDataProvidedForName"/>
		</c:set>						
		<c:set var="usageDataResourceURL">
			<x:out select="$rootElement/usageDataResourceURL"/>
		</c:set>
		<c:set var="paradataDescription">
			<x:out select="$rootElement/paradataDescription"/>
		</c:set>							
		
		<%-- Display Values --%>
		
	<c:if test="${not empty usageDataProvidedForName}">
		<div class="searchResultValues"><em>UsageData Provided For Name:</em> &nbsp;
			<dds:keywordsHighlight>${usageDataProvidedForName}</dds:keywordsHighlight><br/>
		</div>
	</c:if>
	
	<c:if test="${not empty usageDataResourceURL}">
		<div class="searchResultValues"><em>UsageData ResourceURL:</em> &nbsp;
			<dds:keywordsHighlight>${usageDataResourceURL}</dds:keywordsHighlight><br/>
		</div>
	</c:if>
		
	<c:if test="${not empty paradataDescription}">
		<div class="searchResultValues"><em>Paradata Description:</em> &nbsp;
			<dds:keywordsHighlight>${paradataDescription}</dds:keywordsHighlight><br/>
		</div>
	</c:if>


	</c:otherwise>
</c:choose>



