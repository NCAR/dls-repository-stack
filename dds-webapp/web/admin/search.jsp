<%@ include file="AdminTagLibIncludes.jsp" %>
<%@ page contentType="text/html; charset=utf-8" %>
<jsp:useBean id="queryForm" class="edu.ucar.dls.dds.action.form.DDSAdminQueryForm" scope="session"/>

<%-- Grab some services we're using... --%>
<%@ include file="web_service_connection.jsp" %>

<%-- Determine how indexing info should be displayed depending on wheter it's coming from
the file indexer versus the IndexingManager (for external data sources like the NDR)  --%>
<c:set var="isIndexedByIndexingManager">${applicationScope.recordDataSource != 'fileSystem'}</c:set>

<res:setHeader name="Cache-Control">cache</res:setHeader>

<html:html>

<!-- $Id: search.jsp,v 1.128 2013/04/24 22:04:57 jweather Exp $ -->

<head>
<title>Advanced Search</title>

<%-- <link rel='stylesheet' type='text/css' href='/dlese_shared/dlese_styles.css'> --%>
<link rel='stylesheet' type='text/css' href='dds_admin_styles.css'>

<script>
	// Override the method below to set the focus to the appropriate field.
	function sf(){}	
</script>

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
<body onLoad="sf(); checkSelectedItems( )">

<!-- Sub-title just underneath DLESE logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Advanced<br/> Search
</div>

<%@ include file="/nav/top.jsp" %>

<c:if test="${!isDeploayedAtDL}">
	<a href="${pageContext.request.contextPath}/index.jsp"><img id="ddsLogo" border="0" alt="Digital Discovery System (DDS)" title="Digital Discovery System (DDS)" src="${pageContext.request.contextPath}/admin/images/dds_logo_sm.gif"/></a>
	<div id="ddsCustomBanner">${initParam.globalInstallationBanner}</div>
</c:if>
  
<h1 style="margin-left:10px">Advanced Search</h1>

<table width="99%" border="0" align="center">
  <tr> 
    <td>   	
	
	 	

	 

	<%-- ######## Display messages, if present ######## --%>
	<logic:messagesPresent> 		
	<table width="90%" bgcolor="#000000" cellspacing="1" cellpadding="8">
	  <tr bgcolor="ffffff"> 
		<td>
			<b>Messages:</b>
			<ul>
				<html:messages id="msg" property="message"> 
					<li><bean:write name="msg"/></li>									
				</html:messages>
				<html:messages id="msg" property="error"> 
					<li><font color=red>Error: <bean:write name="msg"/></font></li>									
				</html:messages>
			</ul>
			<blockquote>[ <a href="query.do">OK</a> ]</blockquote>
		</td>
	  </tr>
	</table>		
	<br><br>
	</logic:messagesPresent>	
	

	
	<%-- Search box --%>
	<table align=left cellspacing="6" cellspacing=0>
	  <html:form action="/admin/query" method="GET" style="padding-bottom:0px">
	  <script>
	  	function sf(){document.queryForm.q.focus();}	
	  </script>
	  
	  <tr>
		<td colspan=6>
			<input type="hidden" name="s" value="0"/>
			<nobr>
			<html:text property="q" size="75"/>&nbsp;
			<html:submit value="Search"/>
			</nobr>
		</td>
	  </tr>

	  <tr valign=top>			
		<td align="left" valign="top" colspan=6>
			
			<table cellspacing=0 cellpadding=0>
				<tr>
					<td align=left valign=top>
						<c:set var="idMapperUrl" value='${fn:trim(initParam["dbURL"])}'/>					
					
						<%@ include file="checkbox_menus.jsp" %>
						${collectionsCheckBoxMenu_adminSearch}
						${xmlFormatsCheckBoxMenu_adminSearch}
						<c:if test="${idMapperUrl != 'id_mapper_not_used'}">
							${idMapperErrorsCheckBoxMenu_adminSearch}
						</c:if>
						<input type="button" value="Clear selections" class="clearbutton" onClick="JavaScript:clearAllSelections();"/>
						<%-- ${indexedAccessionStatusesCheckBoxMenu_adminSearch} --%>
					</td>
					<%-- Un-comment below to include the ADN vocab menus, and un-comment in checkbox_menus.jsp --%>
					<%-- <td width=15>&nbsp;</td>
					<td align=left valign=top>
						${gradeRangesCheckBoxMenu_adminSearch}
						${subjectsCheckBoxMenu_adminSearch}
						${resourceTypesCheckBoxMenu_adminSearch} 
						${contentStandardsCheckBoxMenu_adminSearch}	
					</td> --%>
				</tr>
			</table>
		</td>
	 </tr>	 
	  </html:form>
	  </table>
	 </td>
	</tr>
</table>

<table width="100%" cellpadding="4" cellspacing="0" id="searchResultsTable">
	<c:set var="userSelections">
		<%@ include file="user_selections.jsp" %>
	</c:set>
	<c:if test="${not empty userSelections}">
		<tr id="yourSelections">
			<td colspan=2>
				<div style="margin-bottom:6px">
					<%-- Display the selections the user entered --%> 
					${userSelections}
				</div>
			</td>
		</tr>
	</c:if>
	
  <c:choose>
  	<c:when test="${queryForm.numResults == '0'}">
		  <logic:present parameter="q">
			  <tr>
				<td colspan=2>
					<div style="margin-top:16px">
						Your search 
						<logic:notEmpty name="queryForm" property="q">
							for <span class="selectionsHighlight"><bean:write name="queryForm" property="q" filter="false"/></span>
						</logic:notEmpty>
						had no matches.
					</div>
				</td>
			  </tr>
		  </logic:present>
	 </c:when>
	 <c:otherwise>
		   <tr>
			<td colspan=2 nowrap>		
				<%-- Pager --%>
				<c:set var="myPager">
					<logic:present name="queryForm" property="prevResultsUrl">
						<a href='query.do?<bean:write name="queryForm" property="prevResultsUrl"/>'><img src='../images/arrow_left.gif' width='16' height='13' border='0' alt='Previous results page'/></a>
					</logic:present>
					Results: <bean:write name="queryForm" property="start"/> - <bean:write name="queryForm" property="end"/> out of <bean:write name="queryForm" property="numResults"/>&nbsp;
					<logic:present name="queryForm" property="nextResultsUrl">
						<a href='query.do?<bean:write name="queryForm" property="nextResultsUrl"/>'><img src='../images/arrow_right.gif' width='16' height='13' border='0' alt='Next results page'/></a>
					</logic:present>
					<logic:notEmpty name="queryForm" property="q">
						for <span class="selectionsHighlight">${queryForm.q}</span>
					</logic:notEmpty>
				</c:set>
				${myPager}
			</td>
		  </tr>  
		  </tr> 
		  
		   <tr>
			<td colspan=2>		
				<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0">
					<td bgcolor="#999999" height="1"></td>
				</table>
			</td>
		  </tr>     
		  
		  <c:forEach var="result" items="${queryForm.results}" begin="${queryForm.offset}" end="${queryForm.offset + queryForm.length - 1}"	varStatus="status">		
		
			<%-- Place the search result in the page --%>
			<%@ include file="search_result_display.jsp" %>
					
		  </c:forEach>
		  
		   <tr>
			<td colspan="2">
				<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0">
					<td bgcolor="#999999" height="1"></td>
				</table>
			  <br>
			</td>
			</tr>
		   <tr>
			<td colspan=2 nowrap>		
				<%-- Pager --%>
				${myPager}
			</td>
		  </tr>


	  </c:otherwise>
  </c:choose>
</table>
	
  <br><br>

<%@ include file="/nav/bottom.jsp" %>  
</body>
</html:html>

