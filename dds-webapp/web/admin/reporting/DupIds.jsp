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

<title>Dup Ids Report</title>

<link rel='stylesheet' type='text/css' href='../dds_admin_styles.css'>



<style type="text/css">

	table{ 

		background-color:#f1f1f1;

	}

	BODY{

		padding-left:15px;

		padding-top:15px;

	}	

</style>



</head>

<body>

	

	<c:set var="dupIds" value="${drf.idsInMultipleRecords}"/>

	

	<%-- Generate the term-count map for the given fields --%>

	<c:set property="calculateCountsForFields" value="${param.fields}" target="${drf}" />	

	

	<%-- Display the data... --%>

	<c:set var="numFields" value='${fn:length( fn:split( param.fields , " " ) )}'/>



	Back to <a href="report.do">DDS reporting</a><br><br>

	

	<table border=1 cellpadding=4 cellspacing=0>		

		<tr>

			<td colspan=3>This report shows any ID's that exist in more than one record in the system, 

			which is an error.</td>

		</tr>



		<tr>

			<td colspan=3>Date generated: <%= new java.util.Date().toString() %></td>

		</tr>

	</table>





	

	<table border=1 cellpadding=4 cellspacing=0>		

		<tr>

			<td><b>Id (encoded)</b></td>

		</tr>

		<c:if test="${empty dupIds}">

			<tr>

				<td>There are no IDs that appear in more than one record</td>

			</tr>		

		</c:if>

		<c:forEach var="id" items="${dupIds}">						

			

			<c:url value="../query.do" var="recordsLink">

				<c:param name="s" value="0"/>

				<c:param name="q">id:${id}</c:param>

			</c:url>			

			

			<tr>

				<td><a href="${recordsLink}" target="_blank" title="Click to view records with this ID">${id}</a></td>

			</tr>

		</c:forEach>

	</table>



		

</body>

</html:html>



