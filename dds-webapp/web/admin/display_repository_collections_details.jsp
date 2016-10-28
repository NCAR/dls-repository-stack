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

<c:set var="collections_details" value="${indexSummary.collections_details}"/>
<table class="collection_details" style="margin-left:0px;">
	<tr>
		<th align="left">Collection Name</th>
		<th align="right">Record #</td>
		<th align="right">Changed #</td>
		<th align="right">Changed %</th>
	</tr>
	<tr>
		<td>All</td>
		<c:set var="total_records" value="${indexSummary.index_detail.records}"/>
		<c:set var="count_changed" value="${indexSummary.record_detail.count_changed}"/>

		<td align="right">${total_records}</td>
		<td align="right"><c:if test="${count_changed>0}">+</c:if>${count_changed}</td>
		<td align="right"><c:if test="${percent_changed>0.0}">+</c:if><fmt:formatNumber type="number" value="${indexSummary.record_detail.percent_changed*100}"/>%</td>
	</tr>


 	<util:sortMap var="sorted_collections" map="${collections_details.collections}" sortKey="key"/>

	<logic:iterate id="collection" name="sorted_collections" >
		<c:set var="collectionDetail" value="${collection.value }"/>
		<c:if test="${showAllCollections||(collectionDetail.added eq 'true'||collectionDetail.removed eq 'true'||collectionDetail.passed_test eq 'false'||collectionDetail.count_changed!=0)}">
			<tr>
				<td>${collectionDetail.name }
				<c:choose>
					<c:when test="${collectionDetail.added eq 'true'}">
						<b>(Added)</b>
					</c:when>
					<c:when test="${collectionDetail.removed eq 'true'}">
						<b>(Removed)</b>
					</c:when>
					<c:when test="${collectionDetail.passed_test eq 'false'}">
						<b>(Exceeded Threshold)</b>
					</c:when>
				</c:choose>
				</td>
				<c:set var="count_changed" value="${collectionDetail.count_changed}"/>
				<c:set var="percent_changed" value="${collectionDetail.percent_changed}"/>
				
				<td align="right" valign="top">${collectionDetail.records}</td>
				<td align="right" valign="top"><c:if test="${count_changed>0}">+</c:if>${count_changed}</td>
				<td align="right" valign="top"><c:if test="${percent_changed>0.0}">+</c:if><fmt:formatNumber type="number" value="${percent_changed*100}"/>%</td>
			</tr> 
		</c:if>
	</logic:iterate>
	
</table>
				
		