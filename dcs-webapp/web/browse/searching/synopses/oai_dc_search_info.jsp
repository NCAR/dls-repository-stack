<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:catch var="xmlParseError">
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${param.xml}" var="dcRecord"/>
	<x:set var="root" select="$dcRecord/dc"/>							
</c:catch>
<c:choose>
	<c:when test="${not empty xmlParseError}">
		<div style="color:red;">Error parsing available OAI DC: ${xmlParseError}</div>
	</c:when>
	<c:otherwise>
		<c:set var="title">
			<x:out select="$root/title"/>
		</c:set>						
		<c:set var="url">
			<x:out select="$root/identifier[starts-with(text(),'http')] | identifier[starts-with(text(),'ftp')]"/>
		</c:set>
		<c:set var="description">
			<x:out select="$root/description"/>
		</c:set>							
		
		<x:set var="resourceType" select="$root/type" />  <%-- multi-value --%>
		
		<c:if test="${not empty title}">
			<div class="browse-item-title">${title}</div>
		</c:if>					
		
		<%-- Display Values --%>
		
		<c:if test="${not empty url}">
			<div class="searchResultValues">
				<a href='${url}' target="_blank"
					title='view resource in a new window'>${sf:truncate(url, param.urlTruncateLen)}</a>
			</div>
		</c:if>
			
		<c:if test="${not empty description}">
			<div class="searchResultValues"><em>Description:</em> &nbsp;
				<dds:keywordsHighlight>${description}</dds:keywordsHighlight>
			</div>
		</c:if>
		
		<c:if test="${fn:length(resourceType) > 0}">
				<div class="searchResultValues">
				<em>Resource type:</em> &nbsp;
				<x:forEach select="$resourceType" varStatus="i">
					<dds:keywordsHighlight><x:out select="."/>${i.last ? '' : ','}</dds:keywordsHighlight>
				</x:forEach>
				</div>
		</c:if>
		
	</c:otherwise>
</c:choose>



