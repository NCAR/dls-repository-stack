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
		<c:set var="url">
			<x:out select="$root/URL[starts-with(text(),'http')] | identifier[starts-with(text(),'ftp')]"/>
		</c:set>
		<c:set var="description">
			<x:out select="$root/description"/>
		</c:set>	
		<c:set var="issue">
			<x:out select="$root/issue"/>
		</c:set>	
		<c:set var="date">
			<x:out select="$root/date"/>
		</c:set>
		<c:set var="rights_status">
			<x:out select="$root/rights/@status"/>
		</c:set>
		
		<%-- display values --%>
		<c:if test="${not empty title}">
			<div class="browse-item-title">${title}</div>
		</c:if>					
		
		<div class="searchResultValues">
			<c:if test="${not empty url}">
					<a href='${url}' target="_blank"
						title='view resource in a new window'>${sf:truncate(url, param.urlTruncateLen)}</a>
			</c:if>
		
			<c:if test="${rights_status eq 'Private'}">
				<i>(Private)</i>
			</c:if>
		</div>
				
		<c:if test="${not empty date}">
			<div class="searchResultValues"><em>Date:</em> &nbsp;
				<dds:keywordsHighlight>${date}</dds:keywordsHighlight>
			</div>
		</c:if>
		
		<c:if test="${not empty issue}">
			<div class="searchResultValues"><em>Issue:</em> &nbsp;
				<dds:keywordsHighlight>${issue}</dds:keywordsHighlight>
			</div>
		</c:if>
		
		<c:if test="${not empty description}">
			<div class="searchResultValues"><em>Description:</em> &nbsp;
				<dds:keywordsHighlight>${description}</dds:keywordsHighlight>
			</div>
		</c:if>
			
	<%-- relations --%>
	<x:if select="$root/relation">
	<div class="searchResultValues"><em>Relations:</em>
				<x:forEach select="$root/relation">
					<c:set var="type"><x:out select="@type" /></c:set>
					<c:set var="label"><x:out select="@title" /></c:set>
					<c:set var="href"><x:out select="@url" /></c:set>
					<div style="margin:0px 0px 4px 10px">
						<li><span class="doHighlight">${type}</span> - 
						<c:choose>
							<c:when test="${not empty label and not empty href}">
								<a href="${href}"><span class="doHighlight">${label}</span></a>
							</c:when>
							<c:when test="${not empty label}">
								<span class="doHighlight">${label}</span>
							</c:when>
							<c:when test="${not empty href}">
								<a href="${href}"><span class="doHighlight">${href}</span></a>
							</c:when>
						</c:choose>
						</li>
					</div>
				</x:forEach>
	</div>
	</x:if>


	</c:otherwise>
</c:choose>



