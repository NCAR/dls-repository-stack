@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><c:set var="contextURl"><%@ include file="../../ContextUrl.jsp" %></c:set><c:set var="reportUri" value="${contextURl}/admin/collection.do?command=export&report=true&collection=${dcssf.collection}#report" />
<DCSWebService>
	<ExportCollection>
		<result>Success</result>
		<collection><c:out value="${dcssf.collection}"/></collection>
		<statuses><c:forEach var="status" items="${dcssf.statuses}"><status><c:out value="${status}" /></status></c:forEach></statuses>
		<exportDir><c:out value="${dcssf.exportDir}"/></exportDir>
		<reportUri><c:out value="${reportUri}"/></reportUri>
	</ExportCollection>
</DCSWebService>

