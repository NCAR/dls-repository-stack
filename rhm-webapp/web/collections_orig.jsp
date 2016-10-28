<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="TagLibIncludes.jsp" %>
<c:if test="${not empty refreshNcsBean}">
	<c:set var="refreshNcsBeanSession" value="${refreshNcsBean}" scope="session"/>
	<c:redirect url="/collections.jsp"></c:redirect>
</c:if>


<html>
<head>
	<title>Harvest Repository Manager Collections</title>
	<%@ include file="/head.jsp" %>
</head>
<body>
	<%@ include file="/top.jsp" %>

	<%-- <div id="breadcrumbs">
		Collections
	</div> --%>

	<h1>Collections</h1>

	<c:if test="${not empty refreshNcsBeanSession}">
		<c:set var="actionMessage">
			<c:choose>
				<c:when test="${empty refreshNcsBeanSession.errorMsg}">
					Collections have been updated from the DCS.
				</c:when>
				<c:otherwise>
					<span class="errMsg">Unable to update the collections list:</span> ${refreshNcsBeanSession.errorMsg}
				</c:otherwise>
			</c:choose>
		</c:set>
		<c:set var="actionButton">
			<form action='<c:url value="/collections.jsp"/>' style="padding:0px; margin:0px; border:0px;">
				<input type="submit" value="&nbsp; OK &nbsp;" id="okButton"/>
			</form>
		</c:set>
		<c:remove var="refreshNcsBeanSession"/>
	</c:if>
	<%-- Display the action message --%>
	<c:if test="${not empty actionMessage}">
		<div>
			<table id="actionMessage" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="center">${actionMessage}</td>
					<td valign="center">${actionButton}</td>
				</tr>
			</table>
		</div>
	</c:if>

	<c:set var="collectionsTable">
		<div id="collectionsTable">
		<table class="sortable" border="0" cellpadding="6" cellspacing="1" width="100%">
			<thead>
				<tr class="headrow" style="text-align:left; white-space:nowrap">
					<td class="sortText" width="100%" nowrap>Collection Name</td>
					<td class="sortText" width="100%" nowrap>View Contexts</td>
					<td class="sortText" nowrap>Harvest<br/>Format</td>
					<td class="sortNumeric" nowrap>Months<br/>Between<br/>Harvests</td>
					<td class="sortText" nowrap>Next Harvest<br/><span style="font-size:11px; white-space:nowrap">(production mode)</span></td>
					<td class="sortText" nowrap>Last Harvest<br/><span style="font-size:11px; white-space:nowrap">(production mode)</span></td>
					<td class="sortText" nowrap>Status</td>
					<%-- <td>Harvest<br/>History</td> --%>
				</tr>
			</thead>
			<tbody>
			<c:set var="collections" value="${harvestManager.collectionsDocument}"/>
			<c:set var="collCount" value="0"/>

			<c:forEach var="collection" items="${harvestManager.ingestableCollections}">
				<c:set var="collCount" value="${collCount+1}"/>
				<c:url var="collUrl" value="collection_details.jsp">
					<c:param name="id">${collection.collectionId}</c:param>
				</c:url>
				<tr class="bodyrow collrow">
					<td width="100%" onclick="window.location='${collUrl}'" onmouseover="Element.addClassName(this, 'overLnk')" onmouseout="Element.removeClassName(this, 'overLnk')" style="cursor:pointer">
						<a href='${collUrl}' class="collTitleLnk">${collection.title}</a>
					</td>

                    <td align="center">
                        <c:choose>
                            <c:when test="${empty collection.viewContexts}">
                                (none)
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="viewContext" items="${collection.viewContexts}" varStatus="i">
                                    ${viewContext}${i.last ? '' : ','}
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>

                    </td>

					<td align="center">
						${collection.libraryFormat}
					</td>
					<td align="center">
						${collection.frequency == '0' ? '-' : collection.frequency}
					</td>
					<td align="center" nowrap>
						<c:catch var="nextHarvest">
							<c:remove var="nextTriggerDate" />
							<bean:define property="nextTriggerDate(${collection.collectionId})" name="harvestManager" id="nextTriggerDate" />
						</c:catch>
						<c:choose>
							<c:when test="${collection.frequency == '0'}">manual</c:when>
							<c:when test="${empty nextTriggerDate}">Within 30 days</c:when>
							<c:otherwise>
								<span style="display:none"><fmt:formatDate value="${nextTriggerDate}" pattern="yyyy-MM-dd"/></span><%-- For js sorting... --%>
								<fmt:formatDate value="${nextTriggerDate}" dateStyle="medium"/>
							</c:otherwise>
						</c:choose>
					</td>
					<c:catch var="lastHarvest">
						<c:remove var="lastTriggeredDAO" />
						<bean:define property="lastTriggeredDAO(${collection.collectionId})" name="harvestManager" id="lastTriggeredDAO" />
					</c:catch>
					<td align="center" nowrap>
						<c:choose>
							<c:when test="${empty lastTriggeredDAO}">Not yet harvested</c:when>
							<c:otherwise>
                                <div></div>
								<span style="display:none"><fmt:formatDate value="${lastTriggeredDAO.lastTriggeredDate}" pattern="yyyy-MM-dd"/></span><%-- For js sorting... --%>
								<fmt:formatDate value="${lastTriggeredDAO.lastTriggeredDate}" dateStyle="medium"/>
								<br/>
								<c:if test="${not empty lastTriggeredDAO.lastReprocessDate}">
									<span class="reprocess_date">Re-Proc: <fmt:formatDate value="${lastTriggeredDAO.lastReprocessDate}" dateStyle="medium"/></span>
								</c:if>
                                <div>
                                    <a href="MetadataSearch.do?clear=true&setSpec=${collection.setSpec}" class="collTitleLnk">View records</a>
                                </div>
							</c:otherwise>
						</c:choose>
					</td>
					<c:remove var="uuidUrl"/>
					<c:remove var="uuidTdAttributes"/>
					<c:if test="${not empty lastTriggeredDAO}">
						<c:url var="uuidUrl" value="harvest_details.jsp">
							<c:param name="uuid" value="${lastTriggeredDAO.uuid}"/>
						</c:url>
						<c:set var="uuidTdAttributes">
							onclick="window.location='${empty uuidUrl ? collUrl : uuidUrl}'" onmouseover="Element.addClassName(this, 'overLnk')" onmouseout="Element.removeClassName(this, 'overLnk')" style="cursor:pointer"
						</c:set>
					</c:if>
					<td align="center" ${uuidTdAttributes} nowrap>

						<c:if test="${not empty lastTriggeredDAO}">
							<a href="${uuidUrl}">
						</c:if>

						<c:if test="${lastTriggeredDAO.runType == 'test'}">Test</c:if>
						<c:choose>
							<c:when test="${empty lastTriggeredDAO}">-</c:when>
							<c:when test="${empty lastTriggeredDAO.lastTriggeredStatus}">Awaiting<br/>status</c:when>
							<c:when test="${lastTriggeredDAO.lastTriggeredStatus == '2'}">Success</c:when>
							<c:when test="${lastTriggeredDAO.lastTriggeredStatus == '3'}">Success<br/>(warnings)</c:when>
							<c:when test="${lastTriggeredDAO.lastTriggeredStatus == '6'}">Success<br/>(record errors)</c:when>
							<c:when test="${lastTriggeredDAO.lastTriggeredStatus == '4'}">Failure</c:when>
							<c:when test="${lastTriggeredDAO.lastTriggeredStatus == '5'}">Failure<br/>(timeout)</c:when>
						</c:choose>
						<c:if test="${not empty lastTriggeredDAO}">
							</a>
						</c:if>

					</td>
				</tr>
			</c:forEach>
			<tr id="noCollMsgRow" class="bodyrow" style="${empty collId ? '' : 'display:none'}">
				<td colspan="5">${empty collId ? 'No collections are configured' : 'No matching collections found'}</td>
			</tr>
			</tbody>
		</table>

		</div>
	</c:set>


	<div>Find:</div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%"><tr>
		<td align="left">
			<form id="findForm" action="javascript: void(0)" name="findForm" style="margin-top:2px; margin-bottom:4px;">
				<input onkeyup="doFind()" id="chars" size="40" type="text" name="chars" value="" />
				<input id="showAllButton" type="button" value="Clear" onclick="$('chars').clear().focus(); doFind()" />
			</form>
		</td>
		<c:choose>
			<c:when test="${collCount > 0}">
				<td align="right"><span id="collsFound">${collCount}</span> of ${collCount} OAI collections</td>
			</c:when>
		</c:choose>
	</tr></table>

	<c:if test="${initParam.harvestCheckInterval != '86400'}">
		<div style="padding-bottom:6px; padding-top:6px; color:#555">Note: Automatic harvests are currently disabled. Harvests may be performed manually only.</div>
	</c:if>

	${collectionsTable}

	<div style="padding-top:15px">
		* Collections in the Harvest Repository Manager are configured via the <a href="http://uc.dls.ucar.edu/dcs/">Collection System</a> (DCS)
		and includes all UCAR collections that have a DCS status of <i>Done</i> or <i>In Progress</i>.
        See the <a href="http://uc.dls.ucar.edu/dcs/">UCAR Collection Records</a> to manage these collections in the DCS.
	</div>
	<div style="padding-top:15px">
		<form name="refreshFromNcs" action="<c:url value='/admin/refresh_from_ncs.do'/>" method="POST">
			<input title="Update collections" type="submit" value="Update collections" />
			<input type="hidden" name="action" value="refreshFromNcs"/>
			<input type="hidden" name="srcPage" value="collOverview"/>
			Update the collections list from the DCS to view recent changes.
		</form>
	</div>
	<div style="padding-top:15px">
		View <a href="<c:url value='/admin/'/>">Harvest Repository Manager Settings and Configuration</a> details (requires authorization).
	</div>

	<%@ include file="/bottom.jsp" %>
</body>
</html>


