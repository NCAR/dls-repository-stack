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
		<c:set var="title">
			<x:out select="$root/general/title"/>
		</c:set>						
		<c:set var="url">
			<x:out select="$root/general/url[starts-with(text(),'http')] | identifier[starts-with(text(),'ftp')]"/>
		</c:set>
		<c:set var="description">
			<x:out select="$root/general/description"/>
		</c:set>							
		
		<x:set var="pathways" select="$root/collection/pathways/name" />  <%-- multi-value --%>
		
		<c:set var="visibility" >
			<x:out select="$root/collection/OAIvisibility" />
		</c:set>
		
		<x:set var="focus" select="$root/collection/collectionPurposes/collectionPurpose" />  <%-- multi-value --%>
		
		<%-- ----- display values ------ --%>
		
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
				<dds:keywordsHighlight>${description}</dds:keywordsHighlight>
			</div>
		</c:if>

		<c:if test="${fn:length(pathways) > 0}">
				<div class="searchResultValues">
				<em>${fn:length(pathways)< 2 ? 'Pathway' : 'Pathways'}:</em> &nbsp;
				<x:forEach select="$pathways" varStatus="i">
					<dds:keywordsHighlight><x:out select="."/>${i.last ? '' : ','}</dds:keywordsHighlight>
				</x:forEach>
				</div>
		</c:if>

		<c:if test="${not empty visibility}">
			<div class="searchResultValues"><em>OAI visibility:</em> &nbsp;
				<dds:keywordsHighlight>${visibility}</dds:keywordsHighlight>
			</div>
		</c:if>
		
		<c:if test="${fn:length(focus) > 0}">
				<div class="searchResultValues">
				<em>Focus:</em> &nbsp;
				<x:forEach select="$focus" varStatus="i">
					<dds:keywordsHighlight><x:out select="."/>${i.last ? '' : ','}</dds:keywordsHighlight>
				</x:forEach>
				</div>
		</c:if>
		
	</c:otherwise>
</c:choose>



