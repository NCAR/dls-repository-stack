<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:catch var="xmlParseError">
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${param.xml}" var="myRec"/>
	<x:set var="rootElement" select="$myRec/assessment"/>							
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
		
		<%-- Display Values --%>
		
		<c:if test="${not empty title}">
			<div class="browse-item-title">${title}</div>
		</c:if>					
		
	<%-- questions 2 - levels deep --%>
	<x:if select="$rootElement/question">
	<div class="searchResultValues"><em>Question:</em></div>
		<ul style="margin:3px 0px 5px 0px;list-style-type:disc">
		
		<x:forEach select="$rootElement/question/outline">
		
			<c:set var="order"><x:out select="@order" /></c:set>
			<c:set var="text"><x:out select="@text" /></c:set>
			<c:set var="url"><x:out select="@url" /></c:set>
			<c:set var="id"><x:out select="@id" /></c:set>
			
			<li>
				<c:if test="${not empty text}"><div class="doHighlight">${text}</div></c:if>
				<c:if test="${not empty url}">
					<div>
						<em>Url:</em>
						<a href="<c:out value='${url}' />" target="_blank"
							title='view resource in a new window'><span class="doHighlight">${sf:truncate(url, param.urlTruncateLen)}</span></a>
					</span>
					</div>
				</c:if>
				<c:if test="${not empty id}">
					<div>
						<em>Id:</em>
						<span class="doHighlight">${id}</span>
					</div>
				</c:if>
				
				<x:if select="outline">
					<ul style="margin:3px 0px 3px 0px;padding-left:15px">
					<x:forEach select="outline">
						<c:set var="order"><x:out select="@order" /></c:set>
						<c:set var="text"><x:out select="@text" /></c:set>
						<c:set var="url"><x:out select="@url" /></c:set>
						<c:set var="id"><x:out select="@id" /></c:set>
						
						<li>
							<c:if test="${not empty text}"><div class="doHighlight">${text}</div></c:if>
							<c:if test="${not empty url}">
								<div>
									<em>Url:</em>
									<span class="doHighlight"><a href='${url}' target="_blank"
										title='view resource in a new window'>${sf:truncate(url, param.urlTruncateLen)}</a>
									</span>
								</div>
							</c:if>
							<c:if test="${not empty id}">
								<div>
									<em>Id:</em>
									<span class="doHighlight">${id}</span>
								</div>
							</c:if>
						</li>
					</x:forEach>
					</ul>
				</x:if>			
			
			</li>
		</x:forEach>
		</ul>
	</x:if>
	
	
	</c:otherwise>
</c:choose>



