<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:catch var="xmlParseError">
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${param.xml}" var="myRec"/>
	<x:set var="root" select="$myRec/vocabTerm"/>							
</c:catch>
<c:choose>
	<c:when test="${not empty xmlParseError}">
		<div style="color:red;">Error parsing record: ${xmlParseError}</div>
	</c:when>
	<c:otherwise>
	
		<%-- compute values --%>
		<c:set var="fullName">
			<x:out select="$root/fullName"/>
		</c:set>	
		<c:set var="recordsAffected">
			<x:out select="$root/recordsAffected"/>
		</c:set>	
		<%-- we want sourceInfo/authority - sourceInfo repeates --%>
		<c:set var="sourceInfo">
			<x:out select="$root/sourceInfo"/>
		</c:set>						
		<%-- altName repeats --%>
		<c:set var="altName">
			<x:out select="$root/altName"/>
		</c:set>	

		
		<c:if test="${not empty fullName}">
			<dds:keywordsHighlight>
				<div class="browse-item-title">${fullName}</div>
			</dds:keywordsHighlight>
		</c:if>
		
		<c:if test="${not empty recordsAffected}">
			<div class="searchResultValues">
			 <em>Record<c:if test="${recordsAffected > 1}">s</c:if> affected:</em> &nbsp;${recordsAffected}
			</div>
		</c:if>
		
		<c:if test="${not empty altName}">
			<div class="searchResultValues"><em>Alt name:</em> &nbsp;
			<x:forEach select="$root/altName" varStatus="i">
				<dds:keywordsHighlight><x:out select="."/><c:if test="${not i.last}">,</c:if></dds:keywordsHighlight>
			</x:forEach>
			</div>
		</c:if>
	
		<c:if test="${not empty sourceInfo}">
			<div class="searchResultValues"><em>Source info:</em> &nbsp;
			<x:forEach select="$root//sourceInfo/authority" varStatus="i">
				<dds:keywordsHighlight><x:out select="."/><c:if test="${not i.last}">,</c:if></dds:keywordsHighlight>
			</x:forEach>
			</div>
		</c:if>

	</c:otherwise>
</c:choose>



