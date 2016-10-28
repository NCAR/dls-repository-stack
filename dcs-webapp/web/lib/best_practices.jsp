<%-- Admin.jsp
	admin for any framework
	- use SchemEditAdminForm
	- bean has been populated by SchemEditAdminAction
	-  
--%>
<%@ page isELIgnored ="false" %>
<%@ page language="java" %>
<jsp:useBean id="sef" class="edu.ucar.dls.schemedit.action.form.SchemEditForm"  scope="session"/>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglibs-application.tld" prefix="app" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="fieldInfo" value="${sef.fieldInfoReader}"/>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link rel="stylesheet" href="../styles.css" type="text/css">
		<title>Best Practices - ${fieldInfo.name}</title>
	</head>
<body bgcolor=white>
<h3>Best Practices - "<em>${fieldInfo.name}</em>"</h3>
<p style="margin-bottom:0px;font-size:0.9em;">xpath: ${fieldInfo.path}</p>
<hr>

<%-- <h4>Name: ${fieldInfo.name}</h4>
<h4>Definition: ${fieldInfo.definition}</h4> --%>

<c:if test="${not empty fieldInfo.definition}">
	<h4>Definition</h4>
	<p><em><b>${fieldInfo.name}</b></em> - ${fieldInfo.definition}</p>
</c:if>

<c:if test="${not empty fieldInfo.dos}">
	<h4>Things to do</h4>
	<ul>
		<c:forEach var="do" items="${fieldInfo.dos}" varStatus="status">
			<li>${do}</li>
		</c:forEach>
	</ul>
</c:if>

<c:if test="${not empty fieldInfo.donts}">
	<h4>Things to avoid</h4>
	<ul>
		<c:forEach var="dont" items="${fieldInfo.donts}" varStatus="status">
			<li>${dont}</li>
		</c:forEach>
	</ul>
</c:if>


<c:if test="${not empty fieldInfo.examples}">
	<h4>Examples</h4>
	<ul>
	<c:forEach var="example" items="${fieldInfo.examples}" varStatus="status">
		<li>${example}</li>
	</c:forEach>
	</ul>
</c:if>

<c:if test="${not empty fieldInfo.otherPractices}">

	<c:forEach var="otherPractice" items="${fieldInfo.otherPractices}" varStatus="status">
		<h4>${otherPractice.header}</h4>
		<ul>
		<c:forEach var="practice" items="${otherPractice.practices}">
			<li>${practice}</li>
		</c:forEach>
		</ul>
	</c:forEach>

</c:if>

<c:if test="${not empty fieldInfo.vocabTerms}">
	<h4>Terms</h4>
	<ul>
		<c:forEach var="termAndDeftn" items="${fieldInfo.vocabTerms}" varStatus="status">
			<li><b>${termAndDeftn.term}</b> - ${termAndDeftn.definition}<c:if 
			test="${not empty termAndDeftn.attribution}">&nbsp;[${termAndDeftn.attribution}]</c:if></li>
		</c:forEach>
	</ul>
</c:if>
</body>
</html>
