<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<%@ page contentType="text/html; charset=UTF-8" %>


<%-- 
title
URL
description.... 2 lines is good
pubName .... 1 line is good --%>


<c:catch var="xmlParseError">
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${param.xml}" var="myRec"/>
	<x:set var="rootElement" select="$myRec/collectionRecord"/>							
</c:catch>
<c:choose>
	<c:when test="${not empty xmlParseError}">
		<div style="color:red;">Error parsing record: ${xmlParseError}</div>
	</c:when>
	<c:otherwise> 
	
		<%-- Compute Values --%>
		
		<c:set var="title">
			<x:out select="$rootElement/shortTitle"/>
		</c:set>						
		<c:set var="url">
			<x:out select="$rootElement/URL[starts-with(text(),'http')] | identifier[starts-with(text(),'ftp')]"/>
		</c:set>
		<c:set var="description">
			<x:out select="$rootElement/description"/>
		</c:set>	
		<c:set var="itemFormat">
			<x:out select="$rootElement/itemFormat"/>
		</c:set>
		<c:set var="key">
			<x:out select="$rootElement/key"/>
		</c:set>
		
		<%-- Display Values --%>
		
		<c:if test="${not empty title}">
			<div class="browse-item-title">${title}</div>
		</c:if>					
		
		<c:if test="${not empty url}">
			<div class="searchResultValues">
				<a href='${url}' target="_blank"
					title='view resource in a new window'>${sf:truncate(url, param.urlTruncateLen)}</a>
			</div>
		</c:if>
		
		<c:if test="${not empty itemFormat}">
			<div class="searchResultValues"><em>Item format:</em> &nbsp; 
				<span style="font-weight:bold">${itemFormat}</span>
			</div>
		</c:if>	
		
		<c:if test="${not empty key}">
			<div class="searchResultValues"><em>Key:</em> &nbsp; 
				<span style="font-weight:bold">${key}</span>
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
		</c:choose>
	
	</c:otherwise>
</c:choose>



