<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglibs-application.tld" prefix="app" %>
<%@ taglib uri='/WEB-INF/tlds/response.tld' prefix='res' %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored ="false" %>

<c:set var="indexLabel">
	<c:choose>
		<c:when test="${drf.indexToUse == 'webLogIndex'}">
			Web log
		</c:when>
		<c:otherwise>
			DDS
		</c:otherwise>	
	</c:choose>
</c:set>

<html:html>
<head>
<title>${indexLabel} index fields</title>
<link rel='stylesheet' type='text/css' href='../dds_admin_styles.css'>

<style type="text/css">
	.dlese_treeMenuTableCellText table td { 
		background-color:#f1f1f1;
	}
</style>
<%@ include file="/nav/head.jsp" %>

</head>



<body>
<!-- Sub-title just underneath DLESE logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Indexed<br/> Fields
</div>
<%@ include file="/nav/top.jsp" %>
	
	<h1>Indexed Fields</h1>
	<p>
		Click a field name to view the terms and counts for that field.
	</p>
	<table border=1 cellpadding=4 cellspacing=0>		
		<tr>
			<th>${indexLabel} index fields</th>
		</tr>		
		<c:forEach var="field" items="${drf.index.fields}">
			<c:url value="report.do" var="termsLink">
				<c:param name="verb" value="TermCount"/>
				<c:param name="fields" value="${field}"/>
				<c:if test="${not empty param.indexToUse}">
					<c:param name="indexToUse" value="${param.indexToUse}"/>
				</c:if>
			</c:url>			
			<tr>
				<td><a href="${termsLink}" title="Click to view terms and counts for this field">${field}</a></td>
			</tr>
		</c:forEach>
	</table>

	<br><br>
<%@ include file="/nav/bottom.jsp" %>
	
</body>
</html:html>


