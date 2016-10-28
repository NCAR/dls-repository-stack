<%-- editorNewRecordButton.tag
	- requires xmlFormat argument
	- requires collection argument
	- optional label argument defaults to "Copy"
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="collection" required="true" %>
<%@ attribute name="xmlFormat" required="true" %>
<%@ attribute name="label" %>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>

<c:choose>
	<c:when test="${xmlFormat == 'adn'}">
		<c:set var="newRecordPath" 
			value="${contextPath}/record_op/adn.do?collection=${collection}&command=new" />
	</c:when>
	<c:otherwise>
	<%-- value="/${xmlFormat}/${xmlFormat}.do?command=newRecord&collection=${collection}"/> --%>
		<c:set var="newRecordPath" 
			value="${contextPath}/record_op/single.do?command=newRecord&collection=${collection}&xmlFormat=${xmlFormat}"/>
	</c:otherwise>
</c:choose>
<input type="button"
	class="editor-action-button"
	value="${not empty label ? label : 'New'}"
	title="Edit a new record"
	onclick="guardedExit('${newRecordPath}')" />

