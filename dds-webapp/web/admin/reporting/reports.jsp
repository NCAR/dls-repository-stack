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



<html>


<head>
	<title>Reports</title>
	<link rel='stylesheet' type='text/css' href='../dds_admin_styles.css'>

	
	<%@ include file="/nav/head.jsp" %>
</head>
<body>
<!-- Sub-title just underneath DLESE logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Reports<br/>
</div>
<%@ include file="/nav/top.jsp" %>

<c:if test="${!isDeploayedAtDL}">
	<a href="${pageContext.request.contextPath}/index.jsp"><img id="ddsLogo" border="0" alt="Digital Discovery System (DDS)" title="Digital Discovery System (DDS)" src="${pageContext.request.contextPath}/admin/images/dds_logo_sm.gif"/></a>
	<div id="ddsCustomBanner">${initParam.globalInstallationBanner}</div>
</c:if>

<div style="padding-left:15px">
	

	<h1>Reports</h1>
	
	<p>Reports available from DDS are listed below</p>
	
	<ul>
		<c:if test="${isDeploayedAtDL}">
			<li>
				<a href="report.do?verb=ViewDupIds" title="Click to view the list of dup IDs">Dup IDs report</a> - Shows ID's that exist in more than one record in the system,
				which is an error. These are *not* dup records!
			</li>
			<li><a href="report.do?verb=ViewIDMapper" title="Click to view the IDMapper data">IDMapper data</a> - Shows IDMapper data for a given record</li>	
			<li><a href="ucar-member-institutions-report.jsp">UCAR Member Institutions</a> - Shows a count of the number of records that
			are associated with UCAR member institutions</li>
		</c:if>
		<li><a href="nsdl_dc_vocab_counts/">NSDL_DC Vocab Counts</a> - Shows a count of the number of nsdl_dc records that catalog
		certain vocabs including audience, education level, and type.</li>
	</ul>
	<br>
	
	<h3>Word stems</h3>
	<p>This form allows you to see the stems used in DDS for given words.</p>	
	Enter one or more words separated by space:
	<form method="GET" action="report.do" name="stemForm">
		<input type="text" name="words" size="50" value="${param.words}">
		<input type="hidden" name="verb" value="ViewStems"> 
		<input type="submit" value="Show stems">
	</form>	
	
	<h3>DDS Harvester Tool</h3>
	<p>Use the <a href="${pageContext.request.contextPath}/admin/harvesting/ddsws_records_harvester.jsp">DDS harvester tool</a> to harvest records from another DDS repository.</p>			
	<br><br>	
	
</div>

<%@ include file="/nav/bottom.jsp" %> 
</body>
</html>




