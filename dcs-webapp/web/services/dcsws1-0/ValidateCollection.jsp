<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><c:set var="contextURl"><%@ include file="../../ContextUrl.jsp" %></c:set><c:set var="reportUri" value="${contextURl}/manage/collections.do?command=validate&report=true&collection=${dcssf.collection}#report" />
<DCSWebService>
	<ValidateCollection>
		<result <c:if test="${not empty dcssf.resultCode}">code="${dcssf.resultCode}"</c:if>>Success</result>
		<collection><c:out value="${dcssf.collection}"/></collection>
		<statuses><c:forEach var="status" items="${dcssf.statuses}"><status><c:out value="${status}" /></status></c:forEach></statuses>
		<reportUri><c:out value="${reportUri}"/></reportUri>
	</ValidateCollection>
</DCSWebService>

