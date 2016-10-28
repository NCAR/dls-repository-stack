<%-- deleteRecordButton.tag
	- requires that a propertyId param is set in calling page 
	- diplays error messages relevant to a particular field (identified by propertyId)
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="id" required="true" %>
<%@ attribute name="label" %>
<%@ attribute name="disabled"%>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>

<input type="button" value="${not empty label ? label : 'view'}" 
	class="record-action-button"
	onclick="location='${contextPath}/browse/view.do?id=${id}'"
	title='full view of this record'
	<c:if test="${disabled}">disabled="true"</c:if>
/>
