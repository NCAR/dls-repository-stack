<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:catch var="xmlParseError">
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${param.xml}" var="myRec"/>
	<x:set var="rootElement" select="$myRec/orgConfig"/>							
</c:catch>
<c:choose>
	<c:when test="${not empty xmlParseError}">
		<div style="color:red;">Error parsing record: ${xmlParseError}</div>
	</c:when>
	<c:otherwise>
	
		<%-- Compute Values --%>
		
		<c:set var="title">
			<x:out select="$rootElement/title"/>
		</c:set>						
		<c:set var="url">
			<x:out select="$rootElement/url[starts-with(text(),'http')] | identifier[starts-with(text(),'ftp')]"/>
		</c:set>
						
		
		<%-- Display Values --%>
		
		<c:if test="${not empty title}">
			<div class="browse-item-title">${title}</div>
		</c:if>					
		
		<c:if test="${not empty url}">
			<div class="searchResultValues">
				<a href='${url}' target="_blank"
					title='view resource in a new window'>${sf:truncate(url, param.urlTruncateLen)}</a>
			</div>
		</c:if>
		


	</c:otherwise>
</c:choose>



