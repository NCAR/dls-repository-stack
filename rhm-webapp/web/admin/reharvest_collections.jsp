<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ include file="../../TagLibIncludes.jsp" %>
<html>
<head>
	<title>Re-Process Collections</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	
	<%-- Include style/menu templates --%>
	<%@ include file="../../head.jsp" %>

</head>
<body>
<%-- Include style/menu templates --%>
<c:import url="../top.jsp" />

<div id="breadcrumbs">
	<a href='<c:url value="/admin"/>' class="collTitleLnk">Admin</a>
	&gt;
	Re-Process Collections
</div>
	
<h1>Re-Process Collections Detail</h1>

<p>
Re-process all collections from a snapshot of their previously harvested records.
This will re-normalize and re-process each record according to the
latest harvest configurations. 
</p>

<p>
	The system is
	<b>
	<c:choose>
		<c:when test="${initParam.reprocessCollectionsMonthly == 'true'}">
            configured to automatically re-process
        </c:when>
		<c:otherwise>
            configured not to automatically re-process
		</c:otherwise>
	</c:choose>
	</b>
    all collections monthly (on the last day of each month).
	Use the context parameter <code>reprocessCollectionsMonthly</code> to control this behavior.
</p>

<br/>
<p> 	     	
	<c:if test="${reharvesterSummary.reharvestStatus == 'running' || reharvesterSummary.reharvestStatus == 'on_hold' || reharvesterSummary.reharvestStatus == 'finished'}">
		<p>
			Re-Process Started ${reharvesterSummary.lastHarvestStart}
		</p>
    </c:if>
    <c:if test="${reharvesterSummary.reharvestStatus == 'finished'}">
		<p>
			Re-Process Completed/Stopped ${reharvesterSummary.lastHarvestEnd}
		</p>
    </c:if>


        Action:
		<c:choose>
			<c:when test="${reharvesterSummary.reharvestStatus == 'running' || reharvesterSummary.reharvestStatus == 'on_hold'}">
				<form name="reharvestCollections" action="" method="POST" onsubmit="return confirm('Are you sure you want to stop the current re-process all collections thread. This will also terminate all harvests that are currently being ran.')">
					<input type="hidden" name="action" value="stop_reharvest"/>
					<input type="submit" value="Stop Re-Harvest"/>
				</form>
			</c:when>
			<c:otherwise>
				<form name="reharvestCollections" action="" method="POST" onsubmit="return confirm('Are you sure you want to re-process all collections?')">
					<input type="hidden" name="action" value="reharvest"/>
					<input type="submit" value="Re-Harvest All Collections Now"/>
				</form>
			</c:otherwise>
		</c:choose>
</p>

<c:if test="${not empty err}"> 
	${err}
</c:if>

<c:if test="${empty err && reharvesterSummary.reharvestStatus != 'not_ran'}">
<fieldset style="width:67%">
	<legend>Status</legend>
	<table cellpadding=3>
		<tr>
			<td nowrap>Not Started</td>
			<td>Collection re-process has not been started yet.</td>
		</tr>
		<tr>
			<td>Started</td>
			<td>Collection re-process is in currently running.</td>
		</tr>
        <tr>
            <td valign=top>Completed</td>
            <td>Collection re-process has completed.
            </td>
        </tr>
		<tr>
			<td valign=top>On Hold</td>
			<td >A collection harvest was triggered some other way prior to
				the re-harvester getting to it. Therefore the entire 
				re-process thread will stop until its completed
			</td>
		</tr>
	</table>
</fieldset>
<p>
	<table border="0" cellpadding="6" cellspacing="1" width="70%" style="border: 1px solid gray;">
			<thead>
				<tr class="headrow" style="text-align:left; white-space:nowrap">
					<td class="sortText" >Collection Name</td>
					<td class="sortText" nowrap>Status</td>
				</tr>
			</thead>
			<tbody>
			
			<c:set var="collectionSummaryList" value="${reharvesterSummary.collectionSummaryList}"/>

			<c:forEach var="collection_summary" items="${collectionSummaryList}">
				<tr class="bodyrow collrow">
					<td>
						<c:url var="collUrl" value="/collection_details.jsp">
							<c:param name="id">${collection_summary.collectionId}</c:param>
						</c:url>	
						<a href="${collUrl}">${collection_summary.collectionName}</a>
					</td>
					<td nowrap>
						<c:choose>
							<c:when test="${collection_summary.status == 'started'}">Started</c:when>
							<c:when test="${collection_summary.status == 'on_hold'}">On Hold</c:when>
							<c:when test="${collection_summary.status == 'completed'}">Completed</c:when>
							<c:otherwise>
								Not Started
							</c:otherwise>
						</c:choose>					
					</td>
				</tr>
			</c:forEach>
			</tbody>
	</table>
	
</p>
</c:if>

<%-- Include style/menu templates --%>
<%@ include file="../../bottom.jsp" %>
</body>
</html>

