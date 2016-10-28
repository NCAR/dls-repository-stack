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
	<title>Repository Details</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		
	<%-- <link rel='stylesheet' type='text/css' href='/dlese_shared/dlese_styles.css'> --%>
	<style type="text/css">
		HTML BODY H3{
			margin-bottom:5px;
		}
		label{
			font-style:italic;
			width:100px;
			display: inline-block;
			padding-bottom:2px;
		}
	
		table.results, table.collection_details
		{
			border-spacing: 0px;
			display:inline-block;
			vertical-align:top;
			background: none repeat scroll 0 0 #E3E4F1;
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
		
		
		div.section
		{
			margin-bottom:30px;
		}
		.value
		{
			text-align:right
		}
		div.facet_detail
		{
			margin-bottom:20px;
		}
	</style>		
	<link rel='stylesheet' type='text/css' href='dds_admin_styles.css'>
	<%@ include file="/nav/head.jsp" %>	
	
</head>

<body text="#000000" bgcolor="#ffffff">
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Repository<br/> Details
</div>

<%@ include file="/nav/top.jsp" %>

<c:set var="indexSummary" value="${rdf.indexSummary}"/>
<c:set var="indexInfo" value="${indexSummary.index_detail}"/>
<c:set var="priorIndexInfo" value="${indexSummary.prior_index_detail}"/>
<c:set var="oneColumnThreshold" value="12"/>

<html:errors />

<div id="general_info" class="section">
	<h3>General Info</h3>
	<label>Repository Path:</label>${indexInfo.path}<br/>
	<label>Record #: </label>${indexInfo.records}<br/>
	<label>Collection #:</label> ${indexInfo.collections}<br/>
	<label>Started Date:</label> ${indexInfo.started}<br/>
	<label>Finished Date:</label> ${indexInfo.finished}
</div>

<div id="prior_info" class="section">
	<h3>Compared To Prior Index</h3>
	<label>Repository Path:</label>${priorIndexInfo.path}<br/>
	<label>Record #: </label>${priorIndexInfo.records}<br/>
	<label>Collection #:</label>${priorIndexInfo.collections}<br/>
	<label>Started Date:</label>${priorIndexInfo.started}<br/>
	<label>Finished Date:</label>${priorIndexInfo.finished}
</div>

 <div id="collection_details" class="section">
	<h3>Collection Details</h3>
	<c:set var="showAllCollections" value="true"/>
	<%@ include file="display_repository_collections_details.jsp" %>
</div>

 <div id="facet_details" class="section">
 	<h3>Facet Details</h3>
 	<util:sortMap var="sorted_facets" map="${indexSummary.facets_details}" sortKey="key"/>
 	<c:set var="facetLength" value="${fn:length(sorted_facets)}"/>
 	<logic:iterate id="facet_container" name="sorted_facets" indexId="facetIndex">
 		<c:set var="facet" value="${facet_container.value}"/>
 		<a href="#${facet.label }">${facet.label }</a><c:if test="${facetIndex<facetLength-1 }">,&nbsp;</c:if> 
 	</logic:iterate>
 	<br/>
 	<br/>
 	<logic:iterate id="facet_container" name="sorted_facets" indexId="facetIndex">
 		<c:set var="facet" value="${facet_container.value}"/>
 		<div class="facet_detail" id="${facet.label}">
			<h4>${facet.label }</h4>
				<table style="margin-left:0px;" class="results">
					<tr>
						<th align='left' >Label</th>
						<th align="right">Record #</td>
						<th align="right">Changed #</td>
						<th  align="right">Changed %</th>
					</tr>
					<util:sortMap var="sorted_results" map="${facet.results}" sortKey="key"/>
					
					
					<logic:iterate id="result_container" name="sorted_results" indexId="resultIndex">
						<c:set var="result" value="${result_container.value}"/>
						<tr>
							<td>
								<c:choose>
									<c:when test="${not empty result.name}">
										${result.name}
									</c:when>
									<c:otherwise>
										${result.label}
									</c:otherwise>
								</c:choose>
							</td>
							<td class="value" valign="top" align="right">${result.records}</td>
							
							<c:set var="count_changed" value="${result.count_changed}"/>
							<c:set var="percent_changed" value="${result.percent_changed}"/>
							<td align="right" valign="top"><c:if test="${count_changed>0}">+</c:if>${count_changed}</td>
							<td align="right" valign="top"><c:if test="${percent_changed>0.0}">+</c:if><fmt:formatNumber type="number" value="${percent_changed*100}"/>%</td>
						</tr>
		
				
					</logic:iterate>
				</table>
		</div>
	</logic:iterate>
 </div>

<%@ include file="/nav/bottom.jsp" %>
</body>
</html:html>