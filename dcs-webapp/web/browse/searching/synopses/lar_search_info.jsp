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
			<x:out select="$root/title"/>
		</c:set>						
		<c:set var="identifier">
			<x:out select="$root/identifier[starts-with(text(),'http')] | identifier[starts-with(text(),'ftp')]"/>
		</c:set>
		<c:set var="description">
			<x:out select="$root/description"/>
		</c:set>							
		
		<%-- display values --%>
		
		<c:if test="${not empty title}">
			<div class="browse-item-title">${title}</div>
		</c:if>					
		
		<c:if test="${not empty identifier}">
			<div class="searchResultValues">
				<a href='${identifier}' target="_blank"
					title='view resource in a new window'>${sf:truncate(identifier, param.urlTruncateLen)}</a>
			</div>
		</c:if>
			
		<c:if test="${not empty description}">
			<div class="searchResultValues"><em>Description:</em> &nbsp;
				<dds:keywordsHighlight>${description}</dds:keywordsHighlight>
			</div>
		</c:if>
			

	</c:otherwise>
</c:choose>



