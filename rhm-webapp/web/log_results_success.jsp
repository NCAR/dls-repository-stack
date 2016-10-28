<%@ include file="TagLibIncludes.jsp" %>
<jsp:useBean id="now" scope="page" class="java.util.Date" />
<c:if test="${not empty hmf.statusNotificationMsg}">
The following status message was received and recorded by the Harvest Repository Manager application at <fmt:formatDate value="${now}" type="both" dateStyle="long" timeStyle="long" />:<br/>
${hmf.statusNotificationMsg}
</c:if>
