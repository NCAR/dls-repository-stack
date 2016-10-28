<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:catch var="xmlParseError">
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${param.xml}" var="at_eadRecord"/>
	<x:set var="root" select="$at_eadRecord/record"/>							
</c:catch>
<c:choose>
	<c:when test="${not empty xmlParseError}">
		<div style="color:red;">Error parsing available XML: ${xmlParseError}</div>
	</c:when>
	<c:otherwise>
		<c:set var="title">
			<x:out select="$root/title"/>
		</c:set>						
		<c:set var="href">
			<x:out select="$root/href"/>
		</c:set>
		<c:set var="extent">
			<x:out select="$root/extent"/>
		</c:set>							
		
		<c:set var="box">
			<x:out select="$root/box"/>
		</c:set>	
		
		<c:set var="folder">
			<x:out select="$root/folder"/>
		</c:set>	
		
		
		<c:if test="${not empty title}">
			<div class="browse-item-title">${title}</div>
		</c:if>					
		
		<%-- Display Values --%>
		
		<c:if test="${not empty href}">
			<div class="searchResultValues">
				<a href='${url}' target="_blank"
					title='view digitalObject in a new window'>${sf:truncate(url, param.urlTruncateLen)}</a>
			</div>
		</c:if>
			
		<c:if test="${not empty extent}">
			<div class="searchResultValues"><em>Extent:</em> &nbsp;
				<dds:keywordsHighlight>${extent}</dds:keywordsHighlight>
			</div>
		</c:if>
		
		<table cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td>
					<div class="searchResultValues"><em>Box:</em> &nbsp;
						<dds:keywordsHighlight>${box}</dds:keywordsHighlight>
					</div>
				</td>
				<td>
					<div class="searchResultValues"><em>Folder:</em> &nbsp;
						<dds:keywordsHighlight>${folder}</dds:keywordsHighlight>
					</div>
				</td>
			</tr>
		</table>
		
	</c:otherwise>
</c:choose>



