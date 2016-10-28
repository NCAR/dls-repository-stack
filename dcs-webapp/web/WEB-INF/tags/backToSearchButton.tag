
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="sessionBean" required="true" type="edu.ucar.dls.schemedit.SessionBean" %>
<%@ attribute name="label" %>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>

<c:choose>
	<c:when test="${not empty sessionBean.searchParams}">
	 <input class="record-action-button" type="button" 
			onclick="window.location='${contextPath}/browse/query.do?s=${sessionBean.paigingParam}${sessionBean.searchParams}'"
			title="return to current search"
			value="${not empty label ? label : 'back to search'}"/>
	</c:when>
	<c:otherwise>
		 <input class="record-action-button" type="button"
			onclick="window.location='${contextPath}/browse/query.do'"
			title="return to current search"
			value="${not empty label ? label : 'back to search'}"/>
	</c:otherwise>
</c:choose>
