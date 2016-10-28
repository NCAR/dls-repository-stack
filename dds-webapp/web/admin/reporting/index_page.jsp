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

<c:set var="indexToUse" value="${empty drf.indexToUse ? 'ddsIndex' : drf.indexToUse}"/>

<c:set var="indexLabel">
	<c:choose>
		<c:when test="${indexToUse == 'webLogIndex'}">
			Web log
		</c:when>
		<c:otherwise>
			DDS
		</c:otherwise>	
	</c:choose>
</c:set>

<html>

<!-- $Id: index_page.jsp,v 1.22 2010/12/04 03:33:33 jweather Exp $ -->

<head>
	<title>Indexed Fields &amp; Terms</title>
	<link rel='stylesheet' type='text/css' href='../dds_admin_styles.css'>
	
	<script language="javascript">
	<!--
		function useIndex(newIndex){
			indexToUse = newIndex.options[newIndex.selectedIndex].value;
			
			if(indexToUse != ""){
				window.location.href = "report.do?indexToUse=" + indexToUse;
			}
		}
	
	-->
	</script>
	
	<%@ include file="/nav/head.jsp" %>
</head>
<body onLoad="JavaScript: document.fieldForm.fields.focus()">
<!-- Sub-title just underneath DLESE logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Indexed<br/> Fields &amp; Terms
</div>
<%@ include file="/nav/top.jsp" %>

<c:if test="${!isDeploayedAtDL}">
	<a href="${pageContext.request.contextPath}/index.jsp"><img id="ddsLogo" border="0" alt="Digital Discovery System (DDS)" title="Digital Discovery System (DDS)" src="${pageContext.request.contextPath}/admin/images/dds_logo_sm.gif"/></a>
	<div id="ddsCustomBanner">${initParam.globalInstallationBanner}</div>
</c:if>

<div style="padding-left:15px">
	

	<h1>Indexed Fields &amp; Terms</h1>
	

	
<%-- 
	<p>
	<div style="padding-bottom:3px">
	Show reports for the following index:
	</div>
	<html:form action="admin/reporting/report">
		<html:select property="indexToUse" value="${indexToUse}" onchange="JavaScript:useIndex(this.form.indexToUse)">
			<html:option value="ddsIndex">DDS index</html:option>
			<html:option value="webLogIndex">Web log index</html:option>
		</html:select>	
	</html:form>
	</p> 
--%>
	

	<h3>Indexed Fields</h3>
	<p>
		<a href="report.do?verb=ListFields&indexToUse=${indexToUse}" title="Click to view the list of fields">View all fields in the ${indexLabel} index</a>.
		From there, you can view the term count reports for each of the fields.
	</p>	

	
	<h3>Indexed Terms</h3>
	<p>These reports show the terms that exist in the ${indexLabel} index within a given field or
	fields. A total term count, total number of documents in which the term appears and 
	list of fields in which the term appears
	is provided.</p>
	
	<%-- Enter a field or list of fields separated by space below *:
	
	<form method="GET" action="report.do" name="fieldForm">
		<input type="text" name="fields" size="30" value="">
		<input type="hidden" name="verb" value="TermCount">
		<input type="hidden" name="indexToUse" value="${indexToUse}">
		<input type="submit" value="Show report">
		<br>* Note that if more than one field is requested the report will take considerably longer to generate.				
	</form>	 --%>	
	
	<c:if test="${indexToUse == 'ddsIndex'}">
		<p>
		Use one of these pre-defined links:
		<div style="padding-top:4px">
			<%-- <a href="report.do?verb=TermCount&fields=title+keyword+description&indexToUse=ddsIndex">Term count report: title, description and keyword fields</a><br> --%>
			<a href="report.do?verb=TermCount&fields=title&indexToUse=ddsIndex">Term count report: title field</a><br>
			<a href="report.do?verb=TermCount&fields=default&indexToUse=ddsIndex">Term count report: default field</a><br>
			<a href="report.do?verb=TermCount&fields=stems&indexToUse=ddsIndex">Term count report: stems field</a><br>
		</div>
		</p>
	</c:if>
	
	
<%-- 	<div style="padding-top:10px">
	Term count report details:
	</div>	
	
	<c:import var="reportTxt" url="REPORT_INFORMATION.txt"/>
	<pre><c:out value="${reportTxt}" escapeXml="true"/></pre> --%>	
	
	<br>

</div>

<%@ include file="/nav/bottom.jsp" %> 
</body>
</html>




