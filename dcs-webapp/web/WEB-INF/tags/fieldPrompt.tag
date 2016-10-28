<%-- 
	fieldPrompt.tag -- inserts prompts for the field at "pathArg" if they are defined in the
fields file for this path and framework
--%>

<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>


<%@ attribute name="pathArg" required="true" %>			<%-- xpath to field --%>

<%-- <div style="color:purple;font-size:85%">Prompt for ${pathArg}</div> --%>
<c:set var="prompts" value="${sf:getPrompts (pathArg, sef.framework)}" />
<c:if test="${not empty prompts}">
	<c:set var="tagName" value="${fn:length(prompts) > 1 ? 'li' : 'div'}"/>
	<c:forEach var="prompt" items="${prompts}">
		<${tagName} class="field-prompt">${prompt}</${tagName}>
	</c:forEach>
</c:if>
