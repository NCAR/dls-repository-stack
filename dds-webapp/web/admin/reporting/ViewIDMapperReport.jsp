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
<%@ taglib prefix="f" uri="http://dls.ucar.edu/tags/dlsELFunctions" %>
<%@ page isELIgnored ="false" %>



<html:html>
<head>
<title>IDMapper report</title>
<link rel='stylesheet' type='text/css' href='../dds_admin_styles.css'>

<style type="text/css">
	table{ 
		background-color:#f1f1f1;
	}
	BODY{
		padding-left:15px;
		padding-top:15px;
	}
	DIV.rec {
		padding: 2px;
		padding-top: 3px;
	}
	DIV.noRec {
		padding: 2px;
		background-color:#f1f1f1;
		width:250px;
	}	
</style>

</head>


<body onLoad="JavaScript: if(document.myForm) document.myForm.id.focus()"> 

	<c:set value="" target="${drf}" property="stemWords"/>	
	<c:if test="${not empty param.words}">
		<c:set value="${param.words}" target="${drf}" property="stemWords"/>
	</c:if>
	Back to <a href="report.do">DDS reporting</a><br>

	<h2>Show IDMapper Report</h2>
	
	<c:choose>
		<c:when test="${fn:trim(initParam['dbURL']) == 'id_mapper_not_used'}">
			<p><b>The IDMapper service is currently not configured in this system</b></p>
		</c:when>
		<c:otherwise>
	
			<p>This form allows you to see the IDMapper service data for a given record.</p>	
			<form method="GET" action="report.do" name="myForm">
				Enter a record ID (example: DLESE-000-000-000-001):<br>
				<input type="text" name="id" size="50" value="${drf.id}"><br><br>
				Enter the collection key in which the record(s) resides (example: dcc):<br>
				<input type="text" name="collection" size="10" value="${drf.collection}"><br><br>		
				<input type="hidden" name="verb" value="ViewIDMapper"> 
				<input type="submit" value="Show IDMapper data">
			</form>	
			
			<c:if test="${not empty drf.id}">
				<br><br>
				<b style="font-size:110%">Report for ID ${drf.id}, collection ${drf.collection}:</b><br>
				[ <a href="../display.do?fullview=${drf.id}">Full view</a> ]
				<br><br>
				
				<c:set var="mmdRec" value="${drf.myMmdRec}"/>
				<b>Id mapper data for this record:</b><br>
				<c:choose>
					<c:when test="${empty mmdRec}">
						<div class="noRec">
							No IDMapper available for this record
						</div>
					</c:when>
					<c:otherwise>
						<div class="rec">
							<%@ include file="DisplayMmdRec.jsp" %>
						</div>
					</c:otherwise>
				</c:choose>
				
				<br>
				
				<b>Dups in other collections:</b><br>
				<c:set var="mmdRecs" value="${drf.mmdRecsDupsOther}"/>
				<c:choose>
					<c:when test="${empty mmdRecs}">
						<div class="noRec">
							There are no dups in other collections
						</div>
					</c:when>
					<c:otherwise>
						<c:forEach varStatus="status" items="${mmdRecs}" var="mmdRec">
							<div class="rec">
								<%@ include file="DisplayMmdRec.jsp" %>
							</div>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				
				<br>
				
				<b>Dups in the same collection:</b><br>
				<c:set var="mmdRecs" value="${drf.mmdRecsDupsSame}"/>
				<c:choose>
					<c:when test="${empty mmdRecs}">
						<div class="noRec">
							There are no dups in the same collection
						</div>
					</c:when>
					<c:otherwise>
						<c:forEach varStatus="status" items="${mmdRecs}" var="mmdRec">
							<div class="rec">
								<%@ include file="DisplayMmdRec.jsp" %>
							</div>
						</c:forEach>
					</c:otherwise>
				</c:choose>			
			</c:if>

		</c:otherwise>
	</c:choose>
	<br><br><br>
</body>
</html:html>


