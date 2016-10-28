<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:catch var="xmlParseError">
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${param.xml}" var="myRec"/>
	<x:set var="rootElement" select="$myRec/record"/>							
</c:catch>
<c:choose>
	<c:when test="${not empty xmlParseError}">
		<div style="color:red;">Error parsing record: ${xmlParseError}</div>
	</c:when>
	<c:otherwise>
	
		<c:set var="title">
			<x:out select="$rootElement/title"/>
		</c:set>						
		<c:set var="url">
			<x:out select="$rootElement/URL[starts-with(text(),'http')] | identifier[starts-with(text(),'ftp')]"/>
		</c:set>
		<c:set var="issue">
			<x:out select="$rootElement/issue"/>
		</c:set>	
		<c:set var="date">
			<x:out select="$rootElement/date"/>
		</c:set>
		<c:set var="description">
			<x:out select="$rootElement/description"/>
		</c:set>		
		
		<%@ include file="title-and-url-display.jspf" %>

		<c:if test="${not empty date}">
			<div class="searchResultValues"><em>Date:</em> &nbsp;
				<dds:keywordsHighlight>${date}</dds:keywordsHighlight>
			</div>
		</c:if>
		
		<c:if test="${not empty issue}">
			<div class="searchResultValues"><em>Issue:</em> &nbsp;
				<dds:keywordsHighlight>${issue}</dds:keywordsHighlight>
			</div>
		</c:if>
		
		
	</c:otherwise>
</c:choose>

