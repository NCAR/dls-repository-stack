<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<%@ page import="java.util.Date" %>

<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-nested.tld" prefix="nested" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/response.tld" prefix="resp" %>
<%@ taglib uri="/WEB-INF/tlds/request.tld" prefix="req" %>
<%@ taglib uri="/WEB-INF/tlds/datetime.tld" prefix="dt" %>
<%@ taglib uri='/WEB-INF/tlds/utils.tld' prefix='util' %>
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<html:html>

<head>
	<title>Repository Maintenance</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		
	<%-- <link rel='stylesheet' type='text/css' href='/dlese_shared/dlese_styles.css'> --%>
	<style type="text/css">
		div.index_header {
		    background: none repeat scroll 0 0 #CCCEE6;
			color:#181053;
		    padding: 5px;
		    height:32px;
		}
		div.current_index{
		    background-color:  #17ED69;
		}
		div.error_index{
		    background-color:  #FC6967;
		}
		div#id_action_required {
			color:red;
			font-weight:bold;
			width:600px
		}
		span.index_name {
		    font-size: 1.1em;
		    font-weight: bold;
		}
		label{
			font-style:italic;
		}
		div.index_dates{
			
			margin-left: 50%;
		}
		div.index_counts{
			float: left;
		}
		div.index{
			width:700px
		}
		div.index_dates label
		{
			width:90px;
			display: inline-block
		}
		div.index_counts label
		{
			width:140px;
			display: inline-block
		}
		div.index_details
		{
			 background: none repeat scroll 0 0 #E3E4F1;
			 padding: 5px;
		}
		div.index_header div.index_info{
			float: left;
		}
		div.index_header div.action{
			margin-left: 60%;
			margin-top:6px;
			font-size: 1.1em;
			font-weight: bold;
			text-align:right;
		}
		div.error_details{
			margin-top:20px;
			color:red;
			font-size: 1.0em;
			font-weight: bold;
		}
		table.results tr th, table.collection_details tr th{
			white-space:nowrap;
			background: none repeat scroll 0 0 #CCCEE6;
			padding:4px;
		}
		
		table.results tr td, table.collection_details tr td{
			background: none repeat scroll 0 0 #E3E4F1;
			padding:2px
		}
		table.collection_details tr.error td{
			background-color: #FC6967;
		}
		
		table.collection_details
		{
			margin-top:5px;
			border-spacing: 0px;
   		    border-collapse: separate;

		}
		a.more_details_link
		{
			float: right;
		}
		input.action_button
		{
			font-size:11px;
		}
		
	</style>	
	<link rel='stylesheet' type='text/css' href='dds_admin_styles.css'>
	<%@ include file="/nav/head.jsp" %>	
	<script src="//ajax.googleapis.com/ajax/libs/scriptaculous/1.9.0/scriptaculous.js"></script>
	<script>
	function toggle_change_details(divId, anchorId)
	{
		aDiv = $(divId);
		if(aDiv.visible())
		{
			Effect.BlindUp(divId, { duration: .25 });
			$(anchorId).update("View Changes")
		}
		else
		{
			Effect.BlindDown(divId, { duration: .25 });
			$(anchorId).update("Hide Changes")
		}
	}
	
	</script>
</head>

<body text="#000000" bgcolor="#ffffff">
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Repository<br/> Maintenance
</div>

<%@ include file="/nav/top.jsp" %>

<div id="indexes">
<h3>Indexes on System</h3>

<logic:iterate id="indexSummaryReport" name="rmf" property="indexSummaryReports" indexId="ii">
	<c:set var="indexInfo" value="${indexSummaryReport.index_detail}"/>
	<c:choose>
	 	<c:when test="${rmf.currentRepositoryPath eq indexInfo.path}">
	 		<c:set var='isCurrent' value="true"/>
	 	</c:when>
	 	<c:otherwise>
	 		<c:set var='isCurrent' value="false"/>
	 	</c:otherwise>
	 </c:choose>
	<c:choose>
	 	<c:when test="${indexInfo.status eq 'error'}">
	 		<c:set var='errorIndex' value="true"/>
	 		<c:if  test="${rmf.actionRequired}">
				<div id='id_action_required'>Action Required!<br/><br/>Background Indexer is currently disabled due to an error during indexing of ${indexInfo.name}. 
				Please select whether to make it current or ignore the errored out index. Once this
				is complete the background indexer will be turned back on.
				</div>
				<br/>
			</c:if>
	 	</c:when>
	 	<c:otherwise>
	 		<c:set var='errorIndex' value="false"/>
	 	</c:otherwise>
	 </c:choose>
	<c:choose>
	 	<c:when test="${indexInfo.status eq 'ignore'}">
	 		<c:set var='ignoreIndex' value="true"/>
	 	</c:when>
	 	<c:otherwise>
	 		<c:set var='ignoreIndex' value="false"/>
	 	</c:otherwise>
	 </c:choose>
	 
	<div class="index">
		<div class="index_header <c:if test="${isCurrent}">current_index</c:if> <c:if test="${errorIndex}">error_index</c:if>">
			<div class="index_info">
				<span class="index_name">Index - ${indexInfo.finished}</span>
				<br/>
				${indexInfo.path}
			</div>
			<div class="action">

				 <c:choose>
				 	<c:when test="${isCurrent}">
				 		Current	
				 	</c:when>
				 	<c:when test="${ignoreIndex}">
				 		Ignored
				 	</c:when>
				 	<c:otherwise>
				 		<html:form action="/admin/maintenance" 
							onsubmit="return confirm( 'This action will switch to using the selected index. Continue?' )"
							method="post" style="margin-top:4px;">
							<input type="hidden" name="command" value="setToCurrentIndex"/>
							<input type="hidden" name="commandRepositoryPath" value="${indexInfo.path}"/>
							<html:submit property="button" value="Make Current" styleClass="action_button" />
						</html:form>
						<c:if test="${errorIndex}">
				 			<html:form action="/admin/maintenance" 
							onsubmit="return confirm( 'This action will ignore this index and try to index again. Continue?' )"
							method="post" style="margin-top:4px;">
								<input type="hidden" name="command" value="setToIgnored"/>
								<input type="hidden" name="commandRepositoryPath" value="${indexInfo.path}"/>
								<html:submit property="button" value="Permanently Ignore" title="Permanently ignore this index and try to index again." styleClass="action_button"/>
							</html:form>
				 		</c:if>
				 	</c:otherwise>
				 </c:choose>
			</div>
		</div>
		<div class="index_details" >
			<div class='index_counts'>
				<label>Number of Records: </label>${indexInfo.records}<br/>
				<label>Number of Collections:</label> ${indexInfo.collections}
			</div>
			<div class='index_dates'>
				
				<label>Started Date:</label> ${indexInfo.started}<br/>
				<label>Finished Date:</label> ${indexInfo.finished}
			</div>

			<c:set var="collections_details" value="${indexSummaryReport.collections_details}"/>
			
			<c:if test="${errorIndex}">
				<div class="error_details">
					<c:if test="${collections_details.passed_max_threshold_for_collection_count_changes eq 'false'}">
						Collections added/removed (${collections_details.collections_added}/${collections_details.collections_removed}) exceeded the max threshold of ${collections_details.max_threshold_for_collection_count_changes}<br/>
					</c:if>
					<c:if test="${collections_details.passed_all_max_threshold_for_record_removals eq 'false'}">
						Collection records removed exceeded the max threshold of ${collections_details.max_threshold_for_record_removals*100}%<br/>
					</c:if>
				</div>
			</c:if> 
			<br/>
			<a id="details_expanded_anchor_${ii}" href="javascript:toggle_change_details('change_details_${ii}', 'details_expanded_anchor_${ii}')">
				<c:choose>
					<c:when test="${not errorIndex}">
						View Changes
					</c:when>
					<c:otherwise>
						Hide Changes
					</c:otherwise>
			 	</c:choose>
			 </a>
			
			<a href="repository_details.do?repository_name=${indexInfo.name}" class='more_details_link'>Full Report</a>
			 
			<div class="change_details" id='change_details_${ii}' <c:if test="${not errorIndex}">style="display:none;"</c:if>>
				<h4 style="display:inline-block">Compared To Prior index:</h4> ${indexSummaryReport.prior_index_detail.path}
				<c:set var="indexSummary" value="${indexSummaryReport}"/>
				<c:set var="showAllCollections" value="false}"/>
				<%@ include file="display_repository_collections_details.jsp" %>
				
			</div>
		</div>
	</div>
	<br/>
</logic:iterate>
</div>

<c:if test="${not empty initParam.importRepositoryDestinations||not empty initParam.exportRepositoryDestinations}">
	<div id="options">
		<h3>Options</h3>
		<c:if test="${not empty initParam.exportRepositoryDestinations}">
			Commited backgorund indexes are automatically being exported to path(s): 
			<br/>
			${initParam.exportRepositoryDestinations}
			<br/></br>
		</c:if>
		<c:if test="${not empty initParam.importRepositoryDestinations}">
			Repositories are automatically being imported from path(s): 
			<br/>
			${initParam.importRepositoryDestinations}
			<br/><br/>
			Automatically make new imported repositories current:
			<html:form action="/admin/maintenance" 
				method="post" style="margin-top:4px;">
				<input type="hidden" name="command" value="changeMakeImportedRepositoriesCurrentRepository"/>
				<html:radio property="makeImportedRepositoriesCurrentRepository" value="true" onclick="submit()"/>Yes
				<html:radio property="makeImportedRepositoriesCurrentRepository" value="false" onclick="submit()"/>No
			</html:form>
		</c:if>
	</div>
</c:if>
<%@ include file="/nav/bottom.jsp" %>
</body>
</html:html>