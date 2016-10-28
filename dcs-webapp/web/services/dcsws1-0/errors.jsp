<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<DCSWebService>
	<c:set var="requestVerb">requestVerb="${param.verb}"</c:set>
	<result <c:if test="${not empty dcssf.resultCode}">code="${dcssf.resultCode}"</c:if>>error</result>
	<c:choose>
		<c:when test="${not empty dcssf.errorList}">
			<error ${requestVerb}>
			  The request had the following errors:<c:forEach var="err" items="${dcssf.errorList}" varStatus="i" >
				<err>${i.count} - ${err}</err></c:forEach>
			</error>
		</c:when>
	<c:when test="${not empty dcssf.errorMsg}">
		<error ${requestVerb}>${dcssf.errorMsg}</error>
	</c:when>
	<c:otherwise>
		<error ${requestVerb}>The request is not valid</error>
	</c:otherwise></c:choose>
</DCSWebService>

