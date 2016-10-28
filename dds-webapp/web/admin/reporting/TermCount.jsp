<%@ page contentType="text/html; charset=UTF-8" %>

<%-- 

	JW - Note 2010-07-27: For some reason if more than one field is requested DDS will freeze up
	(Symptoms began after Lucene 3 upgrade. Disabled links for now... 

--%>


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
<title>${indexLabel} index term count report</title>
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
   Term<br/> Counts
</div>
<%@ include file="/nav/top.jsp" %>

	<%-- Generate the term-count map for the given fields --%>
	<c:set property="calculateCountsForFields" value="${param.fields}" target="${drf}" />	
	
	<h1>Term Counts</h1>
	<p style="margin-bottom: 12px">For field(s): <b>${drf.termCountFields}</b></p>
	
	<%-- Display the data... --%>
	<c:set var="numFields" value='${fn:length( fn:split( param.fields , " " ) )}'/>
	
	<table border=1 cellpadding=4 cellspacing=0 style="margin-bottom: 6px">		
		<c:if test="${numFields <= 1}">
			<tr>
				<td colspan=3>Term count report for field: <b>${drf.termCountFields}</b></td>
			</tr>
			<tr>
				<td colspan=3>Total number of terms in this field: <fmt:formatNumber value="${drf.numTerms}" type="number"/></td>
			</tr>
		</c:if>
		<c:if test="${numFields > 1}">
			<tr>
				<td colspan=3>Term count report for fields: <b>${drf.termCountFields}</b></td>
			</tr>
			<tr>
				<td colspan=3>Total number of terms in these fields: <fmt:formatNumber value="${drf.numTerms}" type="number"/></td>
			</tr>		
		</c:if>
		<tr>
			<td colspan=3>Date generated: <%= new java.util.Date().toString() %></td>
		</tr>
		<tr>
			<td colspan=3><a href="REPORT_INFORMATION.txt">Information about this report</a></td>
		</tr>		
	</table>

	
	<table border=1 cellpadding=4 cellspacing=0>		
		<tr>
			<td><b>Term</b></td>
			<td><b>Term count</b></td>
			<td><b>Record count</b></td>
			<td><b>Ave count per record</b></td>
			<c:if test="${numFields > 1}">
				<td><b>Appears in these fields</b></td>
			</c:if>
		</tr>
		<c:forEach var="term" items="${drf.termCountMap}">						
			<c:url value="../query.do" var="recordsLink">
				<c:param name="s" value="0"/>
				<c:param name="q">
					${term.value.fields[0]}:"${term.key}"<c:forEach var="field" 
						items="${term.value.fields}" begin="1"> OR ${field}:"${term.key}"</c:forEach>					
				</c:param>
				<c:if test="${not empty param.indexToUse}">
					<c:param name="indexToUse" value="${param.indexToUse}"/>
				</c:if>
			</c:url>
			<tr>
			<c:choose>
				<c:when test="${drf.indexToUse == 'ddsIndex'}">
					<td><a href="${recordsLink}" title="Click to view records that contain this term">${term.key}</a></td>
				</c:when>
				<c:otherwise>
					<td>${term.key}</td>	
				</c:otherwise>
			</c:choose>
				<td>${term.value.termCount}</td>				
				<td>${term.value.docCount}</td>
				<td><fmt:formatNumber value="${term.value.termCount / term.value.docCount}" pattern="##.###"/></td>
				<c:if test="${numFields > 1}">
					<td>
						${term.value.fields[0]}<c:forEach var="field" 
							items="${term.value.fields}" begin="1">, ${field}</c:forEach>
					</td>
				</c:if>
			</tr>
		</c:forEach>
	</table>

<%@ include file="/nav/bottom.jsp" %> 		
</body>
</html:html>


