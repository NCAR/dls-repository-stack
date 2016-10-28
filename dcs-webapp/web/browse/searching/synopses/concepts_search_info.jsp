<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:catch var="xmlParseError">
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${param.xml}" var="myRec"/>
	<x:set var="root" select="$myRec/concept"/>							
</c:catch>
<c:choose>
	<c:when test="${not empty xmlParseError}">
		<div style="color:red;">Error parsing record: ${xmlParseError}</div>
	</c:when>
	<c:otherwise>
	
	<%-- Compute Display Values --%>
	
	<c:set var="shortTitle">
		<x:out select="$root/shortTitle"/>
	</c:set>
	
	<c:set var="longTitle">
		<x:out select="$root/longTitle"/>
	</c:set>
	
	<c:set var="objectType">
		<x:out select="$root/contents/@object"/>
	</c:set>	
	
	<%-- Display --%>
		
	<c:choose>
		<c:when test="${not empty shortTitle}">
			<div class="browse-item-title">${shortTitle}</div>
		</c:when>	
		<c:otherwise>
			<div class="browse-item-title"><i>No shortTitle cataloged</i></div>
		</c:otherwise>
	</c:choose>
				
	<c:if test="${not empty longTitle}">
		<div class="searchResultValues"><em>Long Title:</em> &nbsp;
			<dds:keywordsHighlight>
				${longTitle}
			</dds:keywordsHighlight>
		</div>
	</c:if>

	<c:if test="${not empty objectType}">
		<div class="searchResultValues"><em>Object type:</em> &nbsp;
				${objectType}
		</div>
	</c:if>			
				
	<%-- All textual content nodes --%>
	<div class="searchResultValues"><em>Text Content:</em>
		<x:choose>
			<x:when select="$root//content/text">
				<x:forEach select="$root//content">
					<div style="margin:0px 0px 4px 10px">
						<dds:keywordsHighlight truncateString="true" maxStringLength="100" minStringLength="100">
							<x:out select="text" />
						</dds:keywordsHighlight>
					</div>
				</x:forEach>
			</x:when>
			<x:otherwise><i>no text content cataloged</i></x:otherwise>
		</x:choose>
	</div>
				
	<%-- Standards relations --%>
	<x:if select="$root//relations/relation[@object='Standard']">
		<div class="searchResultValues"><em>Standards</em>
			<div style="margin:2px 0px 0px 12px">
			<x:forEach select="$root//relations/relation[@object='Standard']">
				<li> <x:out select="id"/> (<x:out select="id/@type"/>)</li>
			</x:forEach>
			</div>
		</div>
	</x:if>

	</c:otherwise>
</c:choose>



