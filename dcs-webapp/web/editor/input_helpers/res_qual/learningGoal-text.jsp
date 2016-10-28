<%-- <%@ page import="edu.ucar.dls.schemedit.standards.asn.AsnStandardsNode" %> --%>

<c:set var="asnIdPath">${sf:idToPath(id)}/content</c:set>
<c:set var="benchmarkId"><bean:write name="sef" property="valueOf(${asnIdPath})"/></c:set>
<c:set var="stdNode" value="${sf:getAsnStandard (benchmarkId)}" />
<c:set var="suggestedStandards" value="${sef.suggestionServiceHelper.suggestedStandards}"/>

<c:if test="${not empty stdNode}">
	<c:set var="stdIsSuggested" value="${sf:listContains (suggestedStandards, stdNode.id)}"/>
	<div class="suggestion-box checked ${stdIsSuggested ? 'suggested' : ''}" style="padding:5px;">
		<c:forEach var="entry" items="${stdNode.lineage}" varStatus="i">
			<div style="margin:2px 2px 2px ${i.index * 15}px;">${entry}</div>
		</c:forEach>
	</div>
</c:if>
