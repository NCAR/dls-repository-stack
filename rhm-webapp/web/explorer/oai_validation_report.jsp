<%@ include file="/TagLibIncludes.jsp" %><%-- 
	Schema validates an OAI data provider response - sucks in an OAI response and runs the the
	XMLValidationFilter ServletFilter --%>${oaiResponse}<c:remove var="oaiResponse" scope="session"/>


