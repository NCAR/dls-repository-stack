<%--  renderSortWidget
 - Renders a link that causes a result list to be sorted.
--%> 

<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>

<%@ attribute name="widget" required="true" type="edu.ucar.dls.schemedit.display.SortWidget" %>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>

<c:if test="${empty widget}">widget is empty</c:if>
<%-- must use escaped version of query string (queryForm.qe)!! --%>
<c:set var="basehref" value="${contextPath}/browse/query.do?q=${queryForm.qe}&sortField=${widget.fieldName}${queryForm.sortWidgetParams}" />
<c:choose>
	<c:when test="${widget == queryForm.currentSortWidget}">
		<a href="${basehref}&sortOrder=${widget.otherOrder}"
			title="Reverse order"><b>${widget.label}</b></a>
			<a href="${basehref}&sortOrder=${widget.otherOrder}"
			title="Reverse order"><img src='../images/${widget.image}' 
					alt="Click reverse order" border="0" hspace="5" width="11" height="11"></a>
	</c:when>
	<c:otherwise>
		<a href="${basehref}&sortOrder=${widget.order}"	
			title="Sort listing by ${widget.label}"><b>${widget.label}</b></a>
	</c:otherwise>
</c:choose>
