<%-- deleteRecordLink.tag
	- requires that a propertyId param is set in calling page 
	- diplays error messages relevant to a particular field (identified by propertyId)
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>

<%@ attribute name="id" required="true" %>
<%@ attribute name="label" %>
<%@ attribute name="disabled"%>

<c:set var="authorized" value="${sf:isAuthorized ('deleteRecord', sessionBean)}" />

<input type="button" 
	value="${not empty label ? label : 'delete'}"
	title='delete this record (not undoable!)'
	<c:if test="${disabled}">disabled="true"</c:if>
	<c:choose>
		<c:when test="${authorized}">
			class="record-action-button"
			onclick="if (confirm('Really delete ${id}?')) window.location='../record_op/single.do?command=deleteRecord&recId=${id}';"
		</c:when>
		<c:otherwise>
			class="record-action-button-disabled"
			onclick="alert ('You do not have permission to delete records');"
		</c:otherwise>
	</c:choose>  />

