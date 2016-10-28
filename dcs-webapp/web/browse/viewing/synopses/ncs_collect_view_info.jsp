<%@ include file="/JSTLTagLibIncludes.jsp" %>
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
			<x:out select="$root/general/title"/>
		</c:set>						
		<c:set var="url">
			<x:out select="$root/general/url[starts-with(text(),'http')] | identifier[starts-with(text(),'ftp')]"/>
		</c:set>
		<c:set var="description">
			<x:out select="$root/description"/>
		</c:set>							

		<%-- display values --%>
		<%@ include file="title-and-url-display.jspf" %>

	</c:otherwise>
</c:choose>

