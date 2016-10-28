@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %>
<OpenSkySubmissionService>
	<c:choose>
		<c:when test="${not empty recForm.errorList}"><error>
			The request had the following errors:<c:forEach var="err" items="${recForm.errorList}" varStatus="i" >
				<err>${i.count} - ${err}</err></c:forEach>
				</error>
		</c:when>
	<c:when test="${not empty recForm.errorMsg}">
		<error>${recForm.errorMsg}</error>
	</c:when>
	<c:otherwise>
		<error>The request is not valid</error>
	</c:otherwise></c:choose>
</OpenSkySubmissionService>

