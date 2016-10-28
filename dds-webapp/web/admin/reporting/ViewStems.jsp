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







<html:html>

<head>

<title>Word stems</title>

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

<body onLoad="JavaScript: document.stemForm.words.focus()"> 



	<c:set value="" target="${drf}" property="stemWords"/>	

	<c:if test="${not empty param.words}">

		<c:set value="${param.words}" target="${drf}" property="stemWords"/>

	</c:if>

	Back to <a href="report.do">DDS reporting</a>.<br>



	<h2>Show stems</h2>

	<p>This form allows you to see the stems used in DDS for given words.</p>	

	Enter one or more words separated by space:

	<form method="GET" action="report.do" name="stemForm">

		<input type="text" name="words" size="50" value="${param.words}">

		<input type="hidden" name="verb" value="ViewStems"> 

		<input type="submit" value="Show stems">

	</form>	

	

	<table border=1 cellpadding=4 cellspacing=0>		

		<tr>

			<td><b>Original word</b></td>

			<td><b>Stem</b></td>

		</tr>

		<c:forEach varStatus="status" items="${drf.stems}">

			<tr>

				<td>${drf.originalWords[status.index]}</td>

				<td>${drf.stems[status.index]}</td>				

			</tr>

		</c:forEach>

	</table>



</body>

</html:html>



