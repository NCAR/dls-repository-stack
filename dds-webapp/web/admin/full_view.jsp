<%@ include file="AdminTagLibIncludes.jsp" %>
<%@ page contentType="text/html; charset=utf-8" %>
<jsp:useBean id="result" class="edu.ucar.dls.dds.action.form.DDSAdminQueryForm" scope="session"/>

<%-- Note: queryForm is used as the var name for an instance of VocabForm in included page --%>
<c:set var="queryForm" value="${result}"/>

<%-- Determine how indexing info should be displayed depending on wheter it's coming from
the file indexer versus the IndexingManager (for external data sources like the NDR)  --%>
<c:set var="isIndexedByIndexingManager">${applicationScope.recordDataSource != 'fileSystem'}</c:set>

<html:html>
<head>

<!-- $Id: full_view.jsp,v 1.56 2013/04/24 22:04:56 jweather Exp $ -->

<%-- <link rel='stylesheet' type='text/css' href='/dlese_shared/dlese_styles.css'> --%>
<link rel='stylesheet' type='text/css' href='dds_admin_styles.css'>

<title>Full Record View</title>

<c:if test="${not empty titleError}">
	<title>Full View Unavailable for ID ${param.fullview}</title>		
	<!-- Java error in title message:
		${titleError}
		<c:forEach items="${titleError.stackTrace}" var="trace">
		${trace}</c:forEach>
	-->
</c:if>
<%@ include file="/nav/head.jsp" %>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<style>
	.dlese_treeMenuTableCellText TABLE, .dlese_treeMenuTableCellText {
		padding-left: 0px;
		padding-right: 0px;
		margin: 0px;
		border: 0px;
		width: 100%;
	}	
</style>

</head>
<body>
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Full Record<br/> View
</div>

<%@ include file="/nav/top.jsp" %>

<c:if test="${!isDeploayedAtDL}">
	<a href="${pageContext.request.contextPath}/index.jsp"><img id="ddsLogo" border="0" alt="Digital Discovery System (DDS)" title="Digital Discovery System (DDS)" src="${pageContext.request.contextPath}/admin/images/dds_logo_sm.gif"/></a>
	<div id="ddsCustomBanner">${initParam.globalInstallationBanner}</div>
</c:if>

<c:catch var="bodyError">

<c:choose> 
<c:when test="${result == null || result.docReader == null}">
	<p><br><br><center><h2>No record is avialable for ID ${param.fullview}</h2></center></p>
</c:when>
<c:otherwise>

<logic:messagesNotPresent>
<logic:notEmpty name="result" property="docReader">
<table width="100%" cellpadding="10" cellspacing="0" id="searchResultsTable">	

 <tr>
 <td colspan="2">

	<%-- ######## Display messages, if present ######## --%>
	<logic:messagesPresent> 		
	<table width="90%" bgcolor="#000000" cellspacing="1" cellpadding="8">
	  <tr bgcolor="ffffff"> 
		<td>
			<h3>Unable to display full view</h3>
			<ul>
				<html:messages id="msg" property="message"> 
					<li><bean:write name="msg"/></li>									
				</html:messages>
				<html:messages id="msg" property="error"> 
					<li><font color=red>Error: <bean:write name="msg"/></font></li>									
				</html:messages>
			</ul>
		</td>
	  </tr>
	</table>		
	<br><br>
	</logic:messagesPresent>
			
	<%-- <p>Back to <a href="query.do">search results</a></p> --%>
	</td></tr>

	<%-- Define the display type for search results --%>
	<c:set var="displayType" value="full"/>
	
	<%-- Place the search result in the page --%>
	<%@ include file="search_result_display.jsp" %>
			
	<tr>
		<td colspan="2">		  
			<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0">
				<td bgcolor="#999999" height="1"></td>
			</table>
		</td>
	</tr>
			
		 
</table>
</logic:notEmpty>

</logic:messagesNotPresent>
	
</c:otherwise>
</c:choose>

</c:catch>

<c:if test="${not empty bodyError || not empty titleError}">
	<p>
		<H1>An internal error occured and we were unable to access record ID: ${param.fullview}</H1> 
	</p>
		<!-- Java error body message:
		${titleError}
		${bodyError}
		
		<c:forEach items="${bodyError.stackTrace}" var="trace">
			${trace}
		</c:forEach>
		-->
</c:if>


<%@ include file="/nav/bottom.jsp" %> 
</body>
</html:html>

