<%-- editorCopyRecordButton.tag
	- requires recId argument
	- optional label argument defaults to "Copy"
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="recId" required="true" %>
<%@ attribute name="label" %>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>

<input type="button" 
	class="editor-action-button"
	value="${not empty label ? label : 'Copy'}" 
	onClick="guardedExit('${contextPath}/record_op/single.do?command=copyRecord&id=${recId}');"
	title="Copy this record and edit the copy"/>
	
