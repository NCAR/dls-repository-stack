<%@ include file="TagLibIncludes.jsp" %>
<%-- Create the OAI view --%>
<c:catch var="oaiResponseError">
	<c:if test="${empty oaiResponseOutput}">
		<%-- Construct the OAI Identify, ListSets or ListMetadataFormats request--%>
		<c:if test="${not empty param.baseUrl}">
			<c:set var="collBaseUrl" value="${param.baseUrl}"/>
		</c:if>
		<c:if test="${not empty param.verb}">
			<c:set var="requestVerb" value="${param.verb}"/>
		</c:if>		
		<c:url value="${collBaseUrl}" var="oaiRequest">
			<c:param name="verb" value="${requestVerb}"/>
		</c:url>
		
		<%-- Perform the OAI request --%>	
		<c:set var="oaiResponseOutput" value="${f:timedImportUsingEncoding(oaiRequest, 'UTF-8', 6000)}" />
	</c:if>
	
	<%-- Transform OAI response to HTML --%>
	<xtags:parse id="myDoc">${oaiResponseOutput}</xtags:parse>
	<c:set var="xformResult">
		<xtags:style xsl="oai2.xsl" document="${myDoc}" />
	</c:set>
</c:catch>
<div style="padding-bottom:30px">
	<c:choose>
		<c:when test="${not empty oaiResponseError}">
			There was no response to the ${requestVerb} request. This data provider may be off line at this time.
			<!-- Error: ${oaiResponseError} -->
		</c:when>
		<c:otherwise>${xformResult}</c:otherwise>
	</c:choose>
</div>



