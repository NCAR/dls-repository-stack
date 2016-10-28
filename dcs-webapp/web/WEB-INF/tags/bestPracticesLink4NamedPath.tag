<%-- bestPracticesLink4NamedPath.tag
	- from provided namedSchemaPath, displays link that will access tool help page
	- calls doBestPractices on the page on which it appears (e.g., create_collection.jsp)
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>

<%@ attribute name="pathName" required="true" %>
<%@ attribute name="xmlFormat" required="true"%>
<%@ attribute name="linkText" %>


<c:set var="bpLink" value="${sf:getNamedXPath(pathName, xmlFormat, frameworkRegistry)}" />
<c:if test="${not empty bpLink}">
	<a href="javascript:doBestPractices('${bpLink}', '${xmlFormat}')">${not empty linkText ? linkText : "best practices"}</a>
</c:if>
