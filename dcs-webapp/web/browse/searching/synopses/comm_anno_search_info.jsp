<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:catch var="xmlParseError">
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${param.xml}" var="myRec"/>
	<x:set var="rootElement" select="$myRec/comm_anno"/>							
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
 		<c:set var="annotatedID">
			<x:out select="$rootElement/annotatedID"/>
		</c:set>
		<c:set var="rating">
			<x:out select="$rootElement/rating"/>
		</c:set>
		
		<%-- Display Values --%>
		
		<c:if test="${not empty title}">
			<div class="browse-item-title">${title}</div>
		</c:if>		
		
		<c:if test="${not empty rating}">
			<div class="searchResultValues"><em>Rating:</em>
				<%-- ${result.docReader.rating} --%>
				<c:forEach begin="1" end="${rating}">
					<img src="${contextPath}/images/star.gif"/>
				</c:forEach>
			</div>
		</c:if>
		
	<x:if select="$rootElement/text">
	<div class="searchResultValues"><em>Text:</em></div>
		<ul style="margin:3px 0px 5px 0px;list-style-type:disc">
			<x:forEach select="$rootElement/text">
				<li>
					<em><x:out select="@type"/>:</em> <x:out select="."/>
				</li>
			</x:forEach>
		</ul>
	</x:if>
	
	</c:otherwise>
</c:choose>



