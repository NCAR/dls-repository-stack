<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:catch var="xmlParseError">
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${param.xml}" var="myRec"/>
	<x:set var="rootElement" select="$myRec/teach"/>							
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

								
 		<c:set var="annotated_CCS_ID">
			<x:out select="$rootElement/annotatedID[@type='CCS']"/>
		</c:set>
		<c:set var="classPeriods">
			<x:out select="$rootElement/classPeriods"/>
		</c:set>		
		
		<%-- Display Values --%>
		
		<c:if test="${not empty title}">
			<div class="browse-item-title">${title}</div>
		</c:if>					
		
		<c:if test="${not empty classPeriods}">
			<div class="searchResultValues"><em>Class periods:</em>&nbsp;
			${classPeriods}</div>
		</c:if>	
	
	<%-- assessment identifiers (type='CSS') --%>
	<x:if select="$rootElement/assessments/identifier[@type='CCS']">
	<div class="searchResultValues"><em>Assessment Identifiers:</em></div>
		<ul style="margin:3px 0px 5px 0px;list-style-type:disc">
			<x:forEach select="$rootElement/assessments/identifier[@type='CCS']">
				<c:set var="ccs_id"><x:out select="."/></c:set>
				<li><a href="${contextPath}/browse/view.do?id=${ccs_id}"
					title="see CCS assessment record">${ccs_id}</a>
				</li>
			</x:forEach>
		</ul>
	</x:if>
	
	</c:otherwise>
</c:choose>



