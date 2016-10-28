<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:catch var="xmlParseError">
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${param.xml}" var="myRec"/>
	<x:set var="root" select="$myRec/record"/>							
</c:catch>
<c:choose>
	<c:when test="${not empty xmlParseError}">
		<div style="color:red;">Error parsing record: ${xmlParseError}</div>
	</c:when>
	<c:otherwise>
		<%-- compute values --%>
		<c:set var="title">
			<x:out select="$root/general/title"/>
		</c:set>						
		<c:set var="url">
			<x:out select="$root/general/url[starts-with(text(),'http')] | identifier[starts-with(text(),'ftp')]"/>
		</c:set>
		<c:set var="description">
			<x:out select="$root/general/description"/>
		</c:set>							
		
		<%-- display values --%>
		
		<c:if test="${not empty title}">
			<div class="browse-item-title">${title}</div>
		</c:if>					
		
		<c:if test="${not empty url}">
			<div class="searchResultValues">
				<a href='${url}' target="_blank"
					title='view resource in a new window'>${sf:truncate(url, param.urlTruncateLen)}</a>
			</div>
		</c:if>
			
		<c:if test="${not empty description}">
			<div class="searchResultValues"><em>Description:</em> &nbsp;
			<x:forEach select="$root//general/description">
				<dds:keywordsHighlight><x:out select="."/></dds:keywordsHighlight>
			</x:forEach>
			</div>
		</c:if>
			

	</c:otherwise>
</c:choose>



