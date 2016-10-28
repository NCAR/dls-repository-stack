<%-- 
sample_framework_search_info.jsp - demonstrates VIEW display for a custom framework.
- metadata values are pulled from the XML record using xpaths and stored using the "c:set" tag
- then values are displayed using the JSTL notation ${variable}

NOTE: this file must be named {framework}_view_info.jsp! 
--%>

<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:catch var="xmlParseError">
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${param.xml}" var="myRec"/>
	<x:set var="rootElement" select="$myRec/record"/>							
</c:catch>
<c:choose>
	<c:when test="${not empty xmlParseError}">
		<div style="color:red;">Error parsing metadata: ${xmlParseError}</div>
	</c:when>
	<c:otherwise>
	
		<%-- Compute Values using xpath and storing to variable names--%>
		<c:set var="title">
			<x:out select="$rootElement/title"/>
		</c:set>						
		<c:set var="url">
			<x:out select="$rootElement/url"/>
		</c:set>
		<c:set var="description">
			<x:out select="$rootElement/description"/>
		</c:set>							
		
		<x:set var="resourceType" select="$rootElement/type" />  <%-- multi-value --%>
		
<%-- Display Values using JSTL ${variable_name} --%>
				
			<%-- make a border around the values displayed by this jsp file --%>
		<div style="padding:3px;border:#333366 2px dotted">
		<div align="center">
			<%-- show the file name just for fun --%>
			<span style="background-color:#333366;color:white;font-size:.8em">
				custom_framework_view_info.jsp
			</span>
		</div>
		
		<c:if test="${not empty title}">
			<div class="browse-item-title">${title}</a></div>
		</c:if>
		
		<c:if test="${not empty url}">
			<div class="searchResultValues">
				<a href='${url}' target="_blank"
					title="view resource in new window">${sf:truncate(url, param.urlTruncateLen)}</a>
			</div>
		</c:if>
		
		</div>

		
	</c:otherwise>
</c:choose>



