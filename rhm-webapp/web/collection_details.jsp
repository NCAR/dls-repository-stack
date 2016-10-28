<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ include file="TagLibIncludes.jsp" %>

<%-- Redirect to prevent double submit --%>
<c:if test="${not empty triggerBean}">
	<c:set var="triggerBeanSession" value="${triggerBean}" scope="session"/>
	<c:redirect url="/collection_details.jsp">
		<c:param name="id" value="${param.id}"/>
	</c:redirect>
</c:if>
<c:if test="${not empty refreshNcsBean}">
	<c:set var="refreshNcsBeanSession" value="${refreshNcsBean}" scope="session"/>
	<c:redirect url="/collection_details.jsp">
		<c:param name="id" value="${param.id}"/>
	</c:redirect>
</c:if>

<c:set var="collId" value="${param.id}"/>
<c:if test="${not empty param.uuid}">
	<c:set var="collId" value="${hmfuncs:idFromUuid(param.uuid)}"/>
</c:if>

<c:catch var="collDocError">
	<bean:define property="collectionRecordNode(${collId})" name="harvestManager" id="myCollectionRecord" />
</c:catch>

<c:catch var="historyDocError">
	<bean:define property="harvestTriggerDocument(${collId})" name="harvestManager" id="harvestTriggerDocument" />
	<c:set var="harvestTriggerDocument" value="${f:localizeDom4j(harvestTriggerDocument)}"/>
</c:catch>

<c:catch var="collDocError">
	<bean:define property="collectionDAO(${collId})" name="harvestManager" id="collectionDAO" />
</c:catch>

<c:catch var="lastHarvest">
	<c:remove var="lastTriggeredDAO" />
	<bean:define property="lastTriggeredDAO(${collId})" name="harvestManager" id="lastTriggeredDAO" />
</c:catch>
<c:catch var="lastSuccessfulHarvest">
	<c:remove var="lastSuccessfulHarvestEvent" />
	<bean:define property="lastSuccessfulHarvestTriggerEvent(${collId})" name="harvestManager" id="lastSuccessfulHarvestEvent" />
</c:catch>

<html>
<head>
	<title>Collection Harvest Overview &gt; ${collectionDAO.title}</title>
	<c:import url="head.jsp"/>
	<script>
		var baseUrl='${collectionDAO.baseHarvestURL}';
		var collectionId='${collectionDAO.collectionId}';
	</script>
</head>
<body>
	<c:import url="top.jsp"/>

	<div id="breadcrumbs">
		<a href='<c:url value="/collections.jsp"/>' class="collTitleLnk">Collections</a>
		&gt;
		Collection Harvest Overview
	</div>

	<c:if test="${not empty collId}">
		<h1>Collection Harvest Overview</h1>

		<%-- Create an action confirm message, if needed --%>
		<c:if test="${not empty triggerBeanSession}">
			<c:set var="actionMessage">
				<c:choose>
					<c:when test="${empty triggerBeanSession.errorMsg}">
						Harvest was initiated...
					</c:when>
					<c:otherwise>
						<span class="errMsg">The harvest trigger failed:</span> ${triggerBeanSession.errorMsg}
					</c:otherwise>
				</c:choose>
			</c:set>
			<c:set var="actionButton">
				<form action='<c:url value="/collection_details.jsp"/>' style="padding:0px; margin:0px; border:0px;">
					<input type="submit" value="&nbsp; OK &nbsp;" id="okButton"/>
					<input type="hidden" name="id" value="${collId}"/>
				</form>
			</c:set>
			<c:remove var="triggerBeanSession"/>
		</c:if>

		<c:if test="${not empty refreshNcsBeanSession}">
			<c:set var="actionMessage">
				<c:choose>
					<c:when test="${empty refreshNcsBeanSession.errorMsg}">
						Harvest and collection settings have been refreshed.
					</c:when>
					<c:otherwise>
						<span class="errMsg">Unable to refresh the harvest and collection settings:</span> ${refreshNcsBeanSession.errorMsg}
					</c:otherwise>
				</c:choose>
			</c:set>
			<c:set var="actionButton">
				<form action='<c:url value="/collection_details.jsp"/>' style="padding:0px; margin:0px; border:0px;">
					<input type="submit" value="&nbsp; OK &nbsp;" id="okButton"/>
					<input type="hidden" name="id" value="${collectionDAO.collectionId}"/>
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


		<table id="titleTable" border="0" cellpadding="0" cellspacing="0" width="100%">
			<tr>
				<td><h3>${collectionDAO.title}</h3></td>
				<td align="right">
					<div style='height:${collectionDAO.imageHeight+6}px'>
						<a href="${collectionDAO.url}" target="_blank">View collection information</a>
					</div>
				</td>
			</tr>
		</table>

		<ul id="collTabs" class="tabs_cs">
			<li class="tabs_cs"><a href="#overview"><table cellpadding="0" cellspacing="0" border="0"><tr><td valign="middle">Collection<br/>Information</td></tr></table></a></li>
			<li class="tabs_cs"><a href="#history"><table cellpadding="0" cellspacing="0" border="0"><tr><td valign="middle">Harvest<br/>History</td></tr></table></a></li>
		</ul>
	</c:if>

	<div id="contentSection">
		<c:catch var="nextHarvest">
			<c:remove var="nextTriggerDate" />
			<bean:define property="nextTriggerDate(${collId})" name="harvestManager" id="nextTriggerDate" />
		</c:catch>
		<c:set var="nextHarvest">
			<c:choose>
				<c:when test="${collectionDAO.frequency == '0'}">Manual</c:when>
				<c:when test="${empty nextTriggerDate}">Within 30 days</c:when>
				<c:otherwise><fmt:formatDate value="${nextTriggerDate}" dateStyle="medium"/></c:otherwise>
			</c:choose>
		</c:set>

		<div id="overview" class="tabDiv" style="display:none">
			<div class="contentDiv">
                <c:url value="/collection_details.jsp" var="triggerCollUrl">
                    <c:param name="id" value="${param.id}"/>
                </c:url>

				<table border="0" cellpadding="6" cellspacing="1" width="100%">
						<thead>
							<tr class="headrow">
								<td colspan="2">
									<div>
										${collectionDAO.title}
									</div>
									<c:if test="${not empty collectionDAO.url}">
										<div style="font-weight:normal">
											<a href="${collectionDAO.url}" target="_blank">${collectionDAO.url}</a>
										</div>
									</c:if>
								</td>
							</tr>
						</thead>
						<tbody>
							<tr class="bodyrow">
								<td colspan="2">
									${collectionDAO.description}
								</td>
							</tr>


							<tr class="bodyrow">
								<td><b>Actions:</b></td>
								<td>
								    <%--
									<ul style="margin:.1em; padding-left:1.5em;">
                                        <li><a href="MetadataSearch.do?clear=true&setSpec=${collectionDAO.setSpec}">View Harvested Records from this Repository</a></li>
                                        <li><a href="javascript:window.location.href='explorer?baseUrl='+baseUrl+'&collectionId='+collectionId">Explore this OAI Repository</a></li>
                                        <li ><a href="javascript:$('triggerView').toggle();">Trigger a Harvest</a></li>
                                    </ul>
                                    --%>

                                    <div>
                                        <form name="viewHarvestedRecords" title="View Harvested Records from this Repository.">
                                            <input class="actionBtn" type="button" value="View harvested records" onclick="window.location.href='MetadataSearch.do?clear=true&setSpec=${collectionDAO.setSpec}'"/>
                                        </form>
                                    </div>

                                    <div>
                                        <form name="exploreOAI" title="Explore this OAI Data Provider.">
                                            <input class="actionBtn" type="button" value="Explore this OAI data provider" onclick="window.location.href='explorer?baseUrl='+baseUrl+'&collectionId='+collectionId"/>
                                        </form>
                                    </div>


									<div>
										<form name="editNcs" title="Edit the harvest and collection settings using the Collection System (DCS) (requires authorization).">
											<input class="actionBtn" type="button" value="Edit collection harvest settings in DCS" onclick='window.open("${initParam.ncsViewEditRecordUrl}${collectionDAO.collectionId}")'/>
										</form>
									</div>

									<div>
										<form name="refreshFromNcs" action="<c:url value='/admin/refresh_from_ncs.do'/>" method="POST" title="Refresh the collection harvest settings from the DCS to see changes after editing.">
											<input class="actionBtn" type="submit" value="Refresh collection harvest settings from DCS" />
											<input type="hidden" name="action" value="refreshFromNcs"/>
											<input type="hidden" name="id" value="${collectionDAO.collectionId}"/>
										</form>
									</div>

                                    <div>
                                        <form name="triggerHarvest" title="Trigger a harvest manually">
                                            <input class="actionBtn" type="button" value="Trigger a harvest manually..." onclick="$('triggerView').toggle();"/>
                                        </form>
                                    </div>


                                    <div id="triggerView" style="display:none">
                                                <div class="adminWidgets" style="margin:0em 2em 0em 2em;">


                                                    <p>Next scheduled harvest: ${nextHarvest}</p>

                                                    <p>Use the control below to trigger a harvest. These actions may be performed
                                                    by <em>authorized users only</em>.</p>

                                                    <div>
                                                        <form name="requestHarvest" action="<c:url value='/admin/trigger_harvest.do'/>" method="POST">
                                                            <input type="hidden" name="id" value="${collId}"/>
                                                            <input type="hidden" name="collTab" value="history"/>

                                                            <div style="margin-top:6px">
                                                                <table border="0" cellpadding="2" cellspacing="0"><tr>
                                                                    <td valign="top"><input type="radio" name="runType" value="full_reharvest" id="full" checked/></td>
                                                                    <td valign="top"><label for="full">Production mode full
                                                                    - Harvest and ingest all records.</label></td>
                                                                </tr></table>
                                                            </div>
                                                            <div style="margin-top:6px">
                                                                <table border="0" cellpadding="2" cellspacing="0"><tr>
                                                                    <td valign="top"><input type="radio" name="runType" value="test" id="check"/></td>
                                                                    <td valign="top"><label for="check">Test mode
                                                                    - Test harvest the OAI provider and perform integrity checks without ingesting records.</label>

                                                                    </td>
                                                                </tr></table>
                                                            </div>
                                                            <br/>
                                                            <div>
                                                                <fieldset>
                                                                    <legend>Source</legend>
                                                                    <table border="0" cellpadding="2" cellspacing="0">
                                                                    <tr>
                                                                        <td><input type="radio" name="protocol" value="" checked/></td>
                                                                        <td>${collectionDAO.ingestProtocol} (default)</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td><input type="radio" name="protocol" value="harvestedRecordsDB"/></td>
                                                                        <td>Harvested Records DB (reprocess the last successful harvest)</td>
                                                                    </tr>
                                                                    </table>
                                                                </fieldset>
                                                            </div>
                                                            <br/>
                                                            <div>
                                                                <input title="Harvest now" type="submit" value="Trigger harvest now" onClick="return confirm('Trigger harvest for \'${f:jsEncode(collectionDAO.title)}\' now?')"/>
                                                            </div>
                                                            <br/>

                                                        </form>
                                                    </div>

                                                </div>
                                            </div>

								</td>
							</tr>

							<c:url value='/harvest_details.jsp' var="lastUuidUrl">
								<c:param name="uuid" value="${lastTriggeredDAO.uuid}"/>
							</c:url>
							<tr class="bodyrow">
								<td>
									Status of Last Harvest<br/>
									<c:if test="${not empty lastTriggeredDAO}">
										Harvest Date: <fmt:formatDate value="${lastTriggeredDAO.lastTriggeredDate}" type="both" dateStyle="medium" timeStyle="medium"/><br/>
									</c:if>
									<c:choose>
										<c:when  test="${lastTriggeredDAO.runType == 'test'}">
											(test mode)
										</c:when>
										<c:otherwise>
											(production mode)
										</c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:choose>
										<c:when test="${empty lastTriggeredDAO}">Not yet harvested</c:when>
										<c:when test="${empty lastTriggeredDAO.lastTriggeredStatus}">
											<c:set var="statusMsg" value="${hmfuncs:isHarvestStale(lastTriggeredDAO.lastTriggeredDate) ? 'Harvest failed (timeout) - Ingest process not complete after 3 or more days' : 'Awaiting status from ingest process'}"/>
											<a href="${lastUuidUrl}">${statusMsg}</a>
										</c:when>
										<c:when test="${lastTriggeredDAO.lastTriggeredStatus == '2'}"><a href="${lastUuidUrl}">Harvest completed successfully</a></c:when>
										<c:when test="${lastTriggeredDAO.lastTriggeredStatus == '3'}"><a href="${lastUuidUrl}">Harvest completed successfully, but with warnings</a></c:when>
										<c:when test="${lastTriggeredDAO.lastTriggeredStatus == '6'}"><a href="${lastUuidUrl}">Harvest completed successfully, but with record errors</a></c:when>
										<c:when test="${lastTriggeredDAO.lastTriggeredStatus == '4'}"><a href="${lastUuidUrl}">Harvest failed</a></c:when>
										<c:when test="${lastTriggeredDAO.lastTriggeredStatus == '5'}"><a href="${lastUuidUrl}">Harvest failed - Ingest process did not complete after 3 or more days</a></c:when>
									</c:choose>
								</td>
							</tr>

							<%-- <tr class="bodyrow">
								<td>Last Harvest Date<br/>(production mode)</td>
								<td nowrap>
									<c:choose>
										<c:when test="${empty lastTriggeredDAO}">Not yet harvested</c:when>
										<c:otherwise><fmt:formatDate value="${lastTriggeredDAO.lastTriggeredDate}" type="both" dateStyle="medium" timeStyle="medium"/></c:otherwise>
									</c:choose>
								</td>
							</tr> --%>

							<%-- <c:if test="${empty lastTriggeredDAO.lastTriggeredStatus || lastTriggeredDAO.lastTriggeredStatus == '4'}"> --%>
								<tr class="bodyrow">
									<td>Last Successful Harvest<br/>
									<c:choose>
										<c:when  test="${lastTriggeredDAO.runType == 'test'}">
											(test mode)
										</c:when>
										<c:otherwise>
											(production mode)
										</c:otherwise>
									</c:choose>

									</td>
									<td nowrap>
										<xtags:variable id="lastSuccessfulHarvestDate" select="$lastSuccessfulHarvestEvent/@date"/>
										<c:choose>
											<c:when test="${empty lastSuccessfulHarvestDate}">None</c:when>
											<c:otherwise>
												<c:catch var="successDateErr">
													<c:set var="successDate" value="${f:utcDatestampToDate(lastSuccessfulHarvestDate)}"/>
												</c:catch>
												<xtags:variable id="lastSuccessfulHarvestUuid" select="$lastSuccessfulHarvestEvent/@uuid"/>
												<c:url value='/harvest_details.jsp' var="lastSuccessfulUuidUrl">
													<c:param name="uuid" value="${lastSuccessfulHarvestUuid}"/>
												</c:url>
												<a href="${lastSuccessfulUuidUrl}">
												<c:choose>
													<c:when test="${empty successDate}"><nobr>${lastSuccessfulHarvestDate}</nobr></c:when>
													<c:otherwise><fmt:formatDate value="${successDate}" type="both" dateStyle="long" timeStyle="long"/></c:otherwise>
												</c:choose></a>
											</c:otherwise>
										</c:choose>
										<c:if test="${not empty lastTriggeredDAO and lastTriggeredDAO.lastReprocessDate!=null}">
											<br/>
											<span class="reprocess_date">Re-Proc: <fmt:formatDate value="${lastTriggeredDAO.lastReprocessDate}" type="both" dateStyle="long" timeStyle="long"/></span>
										</c:if>
									</td>
								</tr>
							<%-- </c:if> --%>

							<tr class="bodyrow">
								<td>Next Harvest<br/>
									<c:choose>
										<c:when  test="${lastTriggeredDAO.runType == 'test'}">
											(test mode)
										</c:when>
										<c:otherwise>
											(production mode)
										</c:otherwise>
									</c:choose>

								</td>
								<td nowrap>${nextHarvest}</td>
							</tr>

                            <tr class="bodyrow">
                                <xtags:variable id="myViewContexts" select="$myCollectionRecord/metadata/record/collection/viewContexts/viewContext" type="list"/>
                                <td>View Context${fn:length(myViewContexts) > 1 ? 's':''}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${empty myViewContexts}"><div>No view contexts are configured</div></c:when>
                                        <c:otherwise>
                                            <c:forEach items="${myViewContexts}" var="viewContext" varStatus="i">
                                                <div><code><xtags:valueOf select="$viewContext" /> </code></div>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="boxNote">
                                        Note: Use view context <code>UCARConnect</code> to include in the UCARConnect search index.
                                    </div>
                                </td>
                            </tr>
                            <tr class="bodyrow">
                                <xtags:variable id="collectionStatus" select="$myCollectionRecord/head/additionalMetadata/collection_info/ncsStatus" type="String"/>
                                <td>Collection Status</td>
                                <td>
                                    <div>
                                        <code>
                                        <c:choose>
                                            <c:when test="${empty collectionStatus}">No status indicated</c:when>
                                            <c:when test="${collectionStatus == 'NCSFinalStatus'}">Done</c:when>
                                            <c:otherwise>${collectionStatus}</c:otherwise>
                                        </c:choose>
                                        </code>
                                    </div>
                                    <div class="boxNote">
                                        Note: Use DCS status <code>Done</code> to include this collection in search indexes,
                                        and <code>In Progress</code> to harvest without including in search indexes.
                                    </div>
                                </td>
                            </tr>
							<tr class="bodyrow">
								<xtags:variable id="myCollContacts" select="$myCollectionRecord/metadata/record/collection/contacts/contact" type="list"/>
								<td>Contact${fn:length(myCollContacts) > 1 ? 's':''}</td>
								<td>
									<c:choose>
										<c:when test="${empty myCollContacts}"><em>None provided</em></c:when>
										<c:otherwise>
											<c:forEach items="${myCollContacts}" var="set" varStatus="i">
												<div><nobr><xtags:valueOf select="$set/@name" /> <a href="mailto:<xtags:valueOf select='$set/@email' />"><xtags:valueOf select='$set/@email' /></a></nobr></div>
											</c:forEach>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>

							<c:choose>
								<c:when test="${collectionDAO.ingestProtocol=='oai'}">
									<tr class="bodyrow">
										<td>OAI Base URL</td>
										<td nowrap>
											<code>${collectionDAO.baseHarvestURL}</code>
                                            <div class="boxNote">
                                                May be a standard OAI-PMH repository BaseURL or a Static OAI Repository URL.
                                            </div>
										</td>
									</tr>
									<tr class="bodyrow">
										<xtags:variable id="myCollSets" select="$myCollectionRecord/metadata/record/collection/ingest/oai/set" type="list"/>
										<td>Set${fn:length(myCollSets) > 1 ? 's':''}</td>
										<td>
											<c:choose>
												<c:when test="${empty myCollSets}">(No set specified - request all records)</c:when>
												<c:otherwise>
													<c:forEach items="${myCollSets}" var="set" varStatus="i">
														<c:if test="${i.count == 5}">
															<c:set var="setsUiId" value="${collId}-sui"/>
															<a id="${setsUiId}-open" href="#toggle" onclick="javascript:toggleView('${setsUiId}')">more...</a><span id="${setsUiId}" style="display:none">
														</c:if>
														<code><xtags:valueOf select="$set/@setSpec" /></code>${i.last ? '':', '}
														<c:if test="${i.last && not empty setsUiId}">
															<nobr>(<a id="${setsUiId}-close" href="#toggle" onclick="javascript:toggleView('${setsUiId}')">show less</a>)<nobr></span>
														</c:if>
													</c:forEach>
                                                    <div class="boxNote">
                                                        Note: Sets are ignored if this is a Static OAI Repository.
                                                    </div>
												</c:otherwise>
											</c:choose>
										</td>
									</tr>
									<tr class="bodyrow">
										<td>Format</td>
										<td>
											<div>Format: <code>${collectionDAO.libraryFormat}</code></div>
											<div>OAI metadataPrefix used to harvest: <code>${collectionDAO.metadataPrefix}</code></div>
										</td>
									</tr>
								</c:when>
								<c:when test="${collectionDAO.ingestProtocol=='lrSlice'}">
									<tr class="bodyrow">
										<td>Node URL</td>
										<td nowrap>
											<code>${collectionDAO.baseHarvestURL}</code>
										</td>
									</tr>
									<tr class="bodyrow">
										<td>API Path</td>
										<td nowrap>
											<code>slice</code>
										</td>
									</tr>


									<xtags:variable id="sliceRequests" select="$myCollectionRecord/metadata/record/collection/ingest/lrSlice/sliceRequests/sliceRequest" type="list"/>
									<tr class="bodyrow">
										<td>Slice Request Param ${fn:length(sliceRequests) > 1 ? 's':''}</td>
										<td>
											<c:choose>
												<c:when test="${empty sliceRequests}">(No slice params specified - request all records)</c:when>
												<c:otherwise>
													<c:forEach items="${sliceRequests}" var="sliceRequest">
														<code>
															<xtags:valueOf select="sliceRequestParams" context="${sliceRequest}"/>
															<xtags:variable id="filters" select="payloadSchemaFilter" context="${sliceRequest}" type="list"/>
															<c:if test="${not empty filters}">
															( Schema Filter${fn:length(filters) > 1 ? 's':''}:
															<c:forEach items="${filters}" var="filter">
																${filter.text}
															</c:forEach>
															)
															</c:if>
														</code>

													</c:forEach>
												</c:otherwise>
											</c:choose>
										</td>
									</tr>

									<xtags:variable id="publicKeys" select="$myCollectionRecord/metadata/record/collection/ingest/lrSlice/publicKeyFilters/publicKey" type="list"/>

									<tr class="bodyrow">
										<td>Public Key Filter${fn:length(publicKeys) > 1 ? 's':''}</td>
										<td>
											<c:choose>
												<c:when test="${empty publicKeys}">(No public keys specified - request all records)</c:when>
												<c:otherwise>
													<c:forEach items="${publicKeys}" var="publicKey">
														<code>
															${publicKey.text}
															<xtags:variable id="checkHash" select="@checkHash" context="${publicKey}"/>
															<c:choose>
																<c:when test="${checkHash=='true'}">
																	(check hash)
																</c:when>
																<c:otherwise>
																	(do not check hash)
																</c:otherwise>
															</c:choose>
														</code>

													</c:forEach>
												</c:otherwise>
											</c:choose>
										</td>
									</tr>

									<tr class="bodyrow">
										<td>Format</td>
										<td><code>${collectionDAO.libraryFormat}</code></td>
									</tr>
								</c:when>
							</c:choose>

							<tr class="bodyrow">
								<td>Months Between Harvests</td>
								<td nowrap>
									${collectionDAO.frequency == '0' ? '0 (manual only)' : collectionDAO.frequency}
								</td>
							</tr>
							<tr class="bodyrow">
								<td nowrap>Collection Handle</td>
								<td nowrap>
									${collectionDAO.collectionHandle}
								</td>
							</tr>
							<tr class="bodyrow">
								<td>Collection ID</td>
								<td nowrap>
									 ${collectionDAO.collectionId}
									 <nobr>[ <a href="${initParam.ncsWebServiceURL}?verb=GetRecord&amp;id=${collectionDAO.collectionId} " target="_blank">view collection record</a> ]</nobr>
								</td>
							</tr>

						</tbody>
					</table>
			</div>
		</div>

		<div id="history" class="tabDiv" style="display:none">
			<div class="contentDiv">
				<h3>Harvest History</h3>
				<p>Next scheduled harvest: ${nextHarvest}</p>
				<c:choose>
					<c:when test="${not empty harvestTriggerDocument}">
						<xtags:variable id="totalCount" select="count($harvestTriggerDocument/TriggerHistory/triggerEvents/triggerEvent)"/>

						<%-- ------ Begin paging logic ------ --%>

						<c:set var="startingOffset" value="${empty param.s ? '0' : param.s}"/>
						<c:url value="" var="nextResultsUrl">
							<c:param name="id" value="${collId}"/>
							<c:param name="s" value="${startingOffset + 50}"/>
							<c:param name="collTab">history</c:param>
						</c:url>
						<c:url value="" var="prevResultsUrl">
							<c:param name="id" value="${collId}"/>
							<c:param name="s" value="${startingOffset - 50}"/>
							<c:param name="collTab">history</c:param>
						</c:url>

						<c:set var="pager">
							<div style="padding-bottom: 5px">
								<c:if test="${(startingOffset - 50) >= 0}">
									<nobr><a href='<c:out value="${prevResultsUrl}"/>'>&lt;&lt; Prev</a> &nbsp;</nobr>
								</c:if>
								harvest
								<c:out value="${startingOffset +1}"/>
								-
								<c:if test="${startingOffset + 50 > totalCount}">
									<c:out value="${totalCount}"/>
								</c:if>
								<c:if test="${startingOffset + 50 <= totalCount}">
									<c:out value="${startingOffset + 50}"/>
								</c:if>
								out of <c:out value="${totalCount}"/>
								<c:if test="${(startingOffset + 50) < totalCount}">
									<nobr>&nbsp; <a href='<c:out value="${nextResultsUrl}"/>'>Next &gt;&gt;</a></nobr>
								</c:if>
							</div>
						</c:set>

						<%-- ------ end paging logic ------ --%>

						${pager}

						<c:set var="curCount" value="1"/>

						<div style="padding-bottom:8px">
						<table border="0" cellpadding="6" cellspacing="1" width="100%">
						<thead>
							<tr class="headrow">
								<td align="center" nowrap>Triggered On</td>
								<td align="center" nowrap>Completed On</td>
								<td align="center" nowrap>Harvest Type</td>
								<td align="center">Status</td>
								<td align="center">&nbsp;</td>
							</tr>
						</thead>

						<xtags:forEach id="triggerEvent"
							select="$harvestTriggerDocument/TriggerHistory/triggerEvents/triggerEvent"
							sort="@date" ascending="false">

							<c:if test="${curCount >= (startingOffset+1)}">
								<xtags:variable id="utcDatestampString" select="$triggerEvent/@date"/>
								<c:set var="myDate" value="${f:utcDatestampToDate( utcDatestampString )}"/>
								<xtags:variable id="mySets" select="$triggerEvent/triggerFile/harvestRequest/sets"/>
								<c:set var="mySets">
									<c:choose>
										<c:when test="${empty mySets}">None specified</c:when>
										<c:otherwise>${f:replaceAll(fn:trim(mySets),'\\s+',', ')}</c:otherwise>
									</c:choose>
								</c:set>
								<xtags:variable id="baseUrl" select="$triggerEvent/triggerFile/harvestRequest/baseURL"/>
								<xtags:variable id="uuid" select="$triggerEvent/triggerFile/harvestRequest/uuid"/>
								<c:url value='/harvest_details.jsp' var="uuidUrl">
									<c:param name="uuid" value="${uuid}"/>
								</c:url>
								<tbody>
									<tr class="bodyrow" style="cursor:pointer" onclick="window.location='${uuidUrl}'" onmouseover="Element.addClassName(this, 'overLnk')" onmouseout="Element.removeClassName(this, 'overLnk')">
										<td align="center" nowrap><fmt:formatDate value="${myDate}" type="both" dateStyle="long" timeStyle="long"/></td>
										<td align="center" nowrap>
											<xtags:variable id="statusRecievedAtTime" select="$triggerEvent/harvestStatus/statusRecievedAtTime"/>
											<%-- Use the harvest ingest timestamp status if recieved at time is not available (in transition for display) --%>
											<c:if test="${empty statusRecievedAtTime}">
												<xtags:variable id="statusRecievedAtTime" select="$triggerEvent/harvestStatus/timeStamp"/>
											</c:if>
											<c:remove var="timeStampDate"/>
											<c:choose>
												<c:when test="${empty statusRecievedAtTime}">-</c:when>
												<c:otherwise>
													<c:catch>
														<c:set var="timeStampDate" value="${f:utcDatestampToDate(statusRecievedAtTime)}"/>
													</c:catch>
													<c:choose>
														<c:when test="${empty timeStampDate}">${statusRecievedAtTime}</c:when>
														<c:otherwise>
															<fmt:formatDate value="${timeStampDate}" type="both" dateStyle="long" timeStyle="long"/>
														</c:otherwise>
													</c:choose>
												</c:otherwise>
											</c:choose>
										</td>
										<xtags:variable id="runType" select="$triggerEvent/triggerFile/harvestRequest/runType"/>
										<xtags:variable id="harvestType" select="$triggerEvent/@harvestType"/>
										<xtags:variable id="harvestProtocol" select="$triggerEvent/triggerFile/harvestRequest/protocol"/>

										<td>
											<c:choose>
												<c:when test="${runType == 'test'}">Test mode (no changes were made to the database),</c:when>
												<c:otherwise>Production mode (changes were made to the database),</c:otherwise>
											</c:choose>
											${harvestType == 'manual' ? 'triggered manually' : 'triggered automatically'}.
											<br/>via
											<c:choose>
												<c:when test="${harvestProtocol=='harvestedRecordsDB'}">
														Harvested Records Database ( Last successful ${collectionDAO.ingestProtocol} harvest )
												</c:when>
												<c:otherwise>
													${harvestProtocol}
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<xtags:variable id="statusCode" select="$triggerEvent/harvestStatus/statusCode"/>
											<c:choose>
												<c:when test="${not empty statusCode}">
													${statusCode == '2' ? 'Harvest completed successfully.' : ''}
													${statusCode == '3' ? 'Harvest completed successfully, but with warnings.' : ''}
													${statusCode == '4' ? 'Harvest failed.' : ''}
													${statusCode == '5' ? 'Harvest failed - Ingest process did not complete after 3 or more days.' : ''}
													${statusCode == '6' ? 'Harvest completed successfully, but with record errors.' : ''}
												</c:when>
												<c:otherwise>
													${hmfuncs:isHarvestStale(myDate) ? 'Harvest failed (timeout) - Ingest process not complete after 3 or more days.' : 'Awaiting status from ingest process.'}
												</c:otherwise>
											</c:choose>
										</td>
										<td align="center">
											<a href="${uuidUrl}">View<br/>Details</a>
										</td>
									</tr>
								</tbody>
							</c:if>
							<c:set var="curCount" value="${curCount+1}"/>
							<c:if test="${curCount > (startingOffset+50)}"><xtags:break /></c:if>
						</xtags:forEach>
						</table>
						</div>
						${pager}
					</c:when>
					<c:otherwise>
						No harvests have been triggered for this repository.
					</c:otherwise>
				</c:choose>
			</div>
		</div>

	</div>

	<c:import url="bottom.jsp"/>
</body>
</html>


