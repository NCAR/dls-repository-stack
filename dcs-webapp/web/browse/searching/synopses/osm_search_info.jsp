<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<%@ page contentType="text/html; charset=UTF-8" %>
<c:set var="contextPath"><%=request.getContextPath().trim()%></c:set>

<%-- 
title
URL
description.... 2 lines is good
pubName .... 1 line is good --%>


<c:catch var="xmlParseError">
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${param.xml}" var="myRec"/>
	<x:set var="rootElement" select="$myRec/record"/>							
</c:catch>
<c:choose>
	<c:when test="${not empty xmlParseError}">
		<div style="color:red;">Error parsing record: ${xmlParseError}</div>
	</c:when>
	<c:otherwise> 
	
		<%-- Compute Values --%>
		
		<c:set var="title">
			<x:out select="$rootElement/general/title"/>
		</c:set>						
		<c:set var="url">
			<x:out select="$rootElement/general/urlOfRecord[starts-with(text(),'http')] | identifier[starts-with(text(),'ftp')]"/>
		</c:set>
		<c:set var="description">
			<x:out select="$rootElement/general/description"/>
		</c:set>		
		<c:set var="abstract">
			<x:out select="$rootElement/general/abstract"/>
		</c:set>
		<c:set var="pubName">
			<x:out select="$rootElement/general/pubName"/>
		</c:set>	

		<c:set var="doi">
		  <x:out select="$rootElement/classify/idNumber[@type='DOI']" />
		</c:set>
		
		<%-- Display Values --%>
		
		<c:if test="${not empty title}">
			<div class="browse-item-title">${title}</div>
		</c:if>					
		
		<c:if test="${not empty url}">
			<div class="searchResultValues">
				<a href='${url}' target="_blank"
					title='view resource in a new window'>${sf:truncate(url, 100)}</a>
			</div>
		</c:if>
		
		<c:choose>
			<c:when test="${not empty description}">
				<div class="searchResultValues"><em>Description:</em> &nbsp;
					<dds:keywordsHighlight 
							truncateString="true" 
							maxStringLength="300">${description}</dds:keywordsHighlight>
				</div>
			</c:when>
			<c:when test="${not empty abstract}">
				<div class="searchResultValues"><em>Abstract:</em> &nbsp;
					<dds:keywordsHighlight 
							truncateString="true" 
							maxStringLength="300">${abstract}</dds:keywordsHighlight>
				</div>
			</c:when>
		</c:choose>

		<c:if test="${not empty pubName}">
		<div class="searchResultValues"><em>Pub Name:</em> &nbsp;
			<dds:keywordsHighlight>${pubName}</dds:keywordsHighlight><br/>
		</div>
	</c:if>

	
	<%-- primary assets --%>
	<x:if select="$rootElement/resources/primaryAsset">
	<div class="searchResultValues"><em>Primary Assets:</em>
				<x:forEach select="$rootElement/resources/primaryAsset">
					<c:set var="type"><x:out select="@type" /></c:set>
					<c:set var="order"><x:out select="@order" /></c:set>
					<c:set var="label"><x:out select="title" /></c:set>
					<c:set var="href"><x:out select="@url" /></c:set>
					<div style="margin:0px 0px 4px 10px">
						<li>
							<c:if test="${not empty type}"><span class="doHighlight">${type}</span> - </c:if>
							<c:choose>
								<c:when test="${not empty label and not empty href}">
									<span class="doHighlight">${label}</span>
								</c:when>
								<c:when test="${not empty label}">
									<span class="doHighlight">${label}</span>
								</c:when>
								<c:when test="${not empty href}">
									<span class="doHighlight">${href}</span>
								</c:when>
							</c:choose>
							
							<c:if test="${not empty href}">
								<c:set var="nldrHref" value="${sf:getNldrAssetUrl(href)}" />
								<c:choose>
									<c:when test="${not empty nldrHref}">
										<a href="${nldrHref}"><img height="15px" width="15px"
											border="0" src="${contextPath}/images/pdf-icon.png" /></a>
									</c:when>
									<c:otherwise></c:otherwise>
								</c:choose>
							</c:if>	
							
						</li>
					</div>
				</x:forEach>
	</div>
	</x:if>
	

	<%-- other assets --%>
	<x:if select="$rootElement/resources/otherAsset">
	<div class="searchResultValues"><em>Other Assets:</em>
				<x:forEach select="$rootElement/resources/otherAsset">
					<c:set var="type"><x:out select="@type" /></c:set>
					<c:set var="order"><x:out select="@order" /></c:set>
					<c:set var="label"><x:out select="title" /></c:set>
					<c:set var="href"><x:out select="@url" /></c:set>
					<div style="margin:0px 0px 4px 10px">
						<li>
							<c:if test="${not empty type}"><span class="doHighlight">${type}</span> - </c:if>
							<c:choose>
								<c:when test="${not empty label and not empty href}">
									<span class="doHighlight">${label}</span>
								</c:when>
								<c:when test="${not empty label}">
									<span class="doHighlight">${label}</span>
								</c:when>
								<c:when test="${not empty href}">
									<span class="doHighlight">${href}</span>
								</c:when>
							</c:choose>
							
							<c:if test="${not empty href}">
								<c:set var="nldrHref" value="${sf:getNldrAssetUrl(href)}" />
								<c:choose>
									<c:when test="${not empty nldrHref}">
										<a href="${nldrHref}"><img height="15px" width="15px"
											border="0" src="${contextPath}/images/pdf-icon.png" /></a>
									</c:when>
									<c:otherwise></c:otherwise>
								</c:choose>
							</c:if>							
							
						</li>
						
					</div>
				</x:forEach>
	</div>
	</x:if>

	
	<%-- relations --%>
	<x:if select="$rootElement/resources/relation">
	<div class="searchResultValues"><em>Relations:</em>
				<x:forEach select="$rootElement/resources/relation">
					<c:set var="type"><x:out select="@type" /></c:set>
					<c:set var="label"><x:out select="@title" /></c:set>
					<c:set var="href"><x:out select="@url" /></c:set>
					<div style="margin:0px 0px 4px 10px">
						<li>
							<c:if test="${not empty type}"><span class="doHighlight">${type}</span> - </c:if>
							<c:choose>
								<c:when test="${not empty label and not empty href}">
									<a href="${href}"><span class="doHighlight">${label}</span></a>
								</c:when>
								<c:when test="${not empty label}">
									<span class="doHighlight">${label}</span>
								</c:when>
								<c:when test="${not empty href}">
									<a href="${href}"><span class="doHighlight">${sf:truncate(href, 120)}</span></a>
								</c:when>
							</c:choose>
						</li>
					</div>
				</x:forEach>
	</div>
	</x:if>


		<c:if test="${not empty doi}">
		    <div class="searchResultValues"><em>DOI:</em> ${doi}</div>
		</c:if>


	
	</c:otherwise>
</c:choose>



