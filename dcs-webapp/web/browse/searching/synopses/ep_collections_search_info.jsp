<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:catch var="xmlParseError">
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${param.xml}" var="myRec"/>
	<x:set var="rootElement" select="$myRec/ep_collection"/>							
</c:catch>
<c:choose>
	<c:when test="${not empty xmlParseError}">
		<div style="color:red;">Error parsing record: ${xmlParseError}</div>
	</c:when>
	<c:otherwise>
	
		<%-- Compute Values --%>
		
		<c:set var="name">
			<x:out select="$rootElement/name"/>
		</c:set>						
		<c:set var="description">
			<x:out select="$rootElement/description"/>
		</c:set>							
		
		<%-- Display Values --%>
		
		<c:if test="${not empty name}">
			<div class="browse-item-title">${name}</div>
		</c:if>		
		
		<c:if test="${not empty description}">
			<div class="searchResultValues"><em>Description:</em> &nbsp;
				<dds:keywordsHighlight>${description}</dds:keywordsHighlight><br/>
			</div>
		</c:if>	

	</c:otherwise>
</c:choose>



