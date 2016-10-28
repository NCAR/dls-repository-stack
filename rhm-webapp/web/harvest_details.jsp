<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ include file="TagLibIncludes.jsp" %>

<c:set var="collId" value="${param.id}"/>
<c:set var="uuid" value="${param.uuid}"/>
<c:if test="${not empty uuid}">
	<c:set var="collId" value="${hmfuncs:idFromUuid(uuid)}"/>
</c:if>

<c:catch var="collDocError">
	<bean:define property="collectionRecordNode(${collId})" name="harvestManager" id="myCollectionRecord" />

	<bean:define property="collectionDAO(${collId})" name="harvestManager" id="collectionDAO" />
</c:catch>

<c:catch var="historyDocError">
	<bean:define property="harvestTriggerDocument(${collId})" name="harvestManager" id="harvestTriggerDocument" />
	<c:set var="harvestTriggerDocument" value="${f:localizeDom4j(harvestTriggerDocument)}"/>
	<xtags:variable id="triggerEvent" select="$harvestTriggerDocument/TriggerHistory/triggerEvents/triggerEvent[@uuid='${uuid}']" type="node"/>

	<c:if test="${not empty triggerEvent}">	
		<xtags:variable id="collectionNA" select="$triggerEvent/triggerFile/harvestRequest/collectionNA"/>
		<xtags:variable id="timeStamp" select="$triggerEvent/harvestStatus/timeStamp"/>
		<xtags:variable id="statusRecievedAtTime" select="$triggerEvent/harvestStatus/statusRecievedAtTime"/>
		<%-- Use the harvest ingest timestamp status if recieved at time is not available (in transition for display) --%>
		<c:if test="${empty statusRecievedAtTime}">
			<xtags:variable id="statusRecievedAtTime" select="$triggerEvent/harvestStatus/timeStamp"/>
		</c:if>	
	</c:if>
</c:catch>


<html>
<head>
	<title>Harvest Details &gt; ${collectionDAO.title}</title>
	<c:import url="head.jsp"/>
	<script>
		var baseUrl='${collectionDAO.baseHarvestURL}';
		var uuid='${uuid}';
		var collectionNA='${collectionNA}';
		var harvestTimeStamp='${timeStamp}';
		var recordsContext='${initParam.harvestRecordsContext}';
		var contextPath='${pageContext.request.contextPath}';
	</script>	
</head>
<body>
	<c:import url="top.jsp"/>

	<div id="breadcrumbs">
		<a href='<c:url value="/collections.jsp"/>' class="collTitleLnk">Collections</a>
		&gt;
		<a href='<c:url value="/collection_details.jsp"/>?id=${collId}' class="collTitleLnk">Collection Harvest Overview</a>
		&gt;
		Harvest Details
	</div>
		
	<c:if test="${not empty collId}">
		<h1>Harvest Details</h1>
				
		<table id="titleTable" border="0" cellpadding="0" cellspacing="0" width="100%">
			<tr>
				<td><h3>Harvest performed for: ${collectionDAO.title}</h3></td>
				<td align="right">
					<div style='height:${collectionDAO.imageHeight+6}px'>
						<a href="${collectionDAO.url}" target="_blank">View collection information</a>
					</div>
				</td>
			</tr>
		</table>		
	
		<ul id="harvestTabs" class="tabs_cs">
			<li class="tabs_cs"><a href="#details"><table cellpadding="0" cellspacing="0" border="0"><tr><td valign="middle">Details</td></tr></table></a></li>
			<li class="tabs_cs"><a href="#logs"><table cellpadding="0" cellspacing="0" border="0"><tr><td valign="middle">Harvest<br/>Logs</td></tr></table></a></li>
			<li class="tabs_cs"><a href="#records"><table cellpadding="0" cellspacing="0" border="0"><tr><td valign="middle">Harvested<br/>Records</td></tr></table></a></li>						
		</ul>
	</c:if>
	
	<div id="contentSection">
			
		<div id="details" class="tabDiv" style="display:none">
			<div class="contentDiv">
				<h3>Harvest Details</h3>

				<c:choose>
					<c:when test="${not empty triggerEvent}">	
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
					<xtags:variable id="harvestProtocol" select="$triggerEvent/triggerFile/harvestRequest/protocol"/>
					<div style="padding-bottom:8px">
						<table border="0" cellpadding="6" cellspacing="1" width="100%">
								<tbody>
									<xtags:variable id="statusCode" select="$triggerEvent/harvestStatus/statusCode"/>
									<tr class="bodyrow">
										<td>Status</td>
										<td>
											<c:choose>
												<c:when test="${not empty statusCode}">
													${statusCode == '2' ? 'Harvest completed successfully' : ''}
													${statusCode == '3' ? 'Harvest completed successfully, but with warnings' : ''}
													${statusCode == '6' ? 'Harvest completed successfully, but with record errors' : ''}
													${statusCode == '4' ? 'Harvest failed' : ''}
													${statusCode == '5' ? 'Harvest failed - Ingest process did not complete after 3 or more days.' : ''}
												</c:when>
												<c:otherwise>
													${hmfuncs:isHarvestStale(myDate) ? 'Harvest failed (timeout) - Ingest process not complete after 3 or more days' : 'Awaiting status from ingest process.'}
												</c:otherwise>
											</c:choose>
										</td>
									</tr>
									<xtags:variable id="statusCodeDescription" select="$triggerEvent/harvestStatus/statusCodeDescription"/>
									<tr class="bodyrow">
										<td>Notes</td>
										<td>${statusCodeDescription}</td>
									</tr>						
									<tr class="bodyrow">
										<td>Triggered On</td>
										<td><fmt:formatDate value="${myDate}" type="both" dateStyle="long" timeStyle="long"/></td>
									</tr>
									<tr class="bodyrow">
										<td>Completed On</td>
										<td>
											<c:choose>
												<c:when test="${empty statusRecievedAtTime}">-</c:when>
												<c:otherwise>
													<c:catch>
														<c:set var="timeStampDate" value="${f:utcDatestampToDate(statusRecievedAtTime)}"/>
													</c:catch>
													<c:choose>
														<c:when test="${empty timeStampDate}">${statusRecievedAtTime}</c:when>
														<c:otherwise>
															<fmt:formatDate value="${timeStampDate}" type="both" dateStyle="long" timeStyle="long" />
														</c:otherwise>
													</c:choose>
												</c:otherwise>
											</c:choose>
										</td>
									</tr>										
									<tr class="bodyrow">
										<td>Harvest Type</td>
										<xtags:variable id="runType" select="$triggerEvent/triggerFile/harvestRequest/runType"/>
										<xtags:variable id="harvestType" select="$triggerEvent/@harvestType"/>
										<td>
											<c:choose>
												<c:when test="${runType == 'test'}">Test mode (no changes were made to the database),</c:when>
												<c:otherwise>Production mode (changes were made to the database),</c:otherwise>
											</c:choose>
											${harvestType == 'manual' ? 'triggered manually' : 'triggered automatically'}
										</td>
									</tr>
									<c:choose>
										<c:when test="${harvestProtocol=='oai'}">						
											<tr class="bodyrow">
												<td>Harvested Base URL</td>
												<td>${baseUrl}</td>
											</tr>
											
											<tr class="bodyrow">
												<td>Set(s)</td>
												<td>${mySets}</td>
											</tr>
											<tr class="bodyrow">
												<td>Format</td>
												<td><xtags:valueOf select="$triggerEvent/triggerFile/harvestRequest/formats"/></td>
											</tr>
										</c:when>
										<c:when test="${harvestProtocol=='harvestedRecordsDB'}">						
											<tr class="bodyrow">
												<td>Source</td>
												<td>Harvested Records Database ( Last successful ${collectionDAO.ingestProtocol} harvest )</td>
											</tr>
										</c:when>
										<c:when test="${harvestProtocol=='lrSlice'}">
											<tr class="bodyrow">
												<td>Node URL</td>
												<td nowrap>
													<code>${baseUrl}</code>
												</td>
											</tr>
											<tr class="bodyrow">
												<td>API Path</td>
												<td nowrap>
													<code>slice</code>
												</td>
											</tr>
	
											<xtags:variable id="sliceRequests" select="$triggerEvent/triggerFile/harvestRequest/sliceRequests/sliceRequest" type="list"/>
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
											
											<xtags:variable id="publicKeys" select="$triggerEvent/triggerFile/harvestRequest/publicKeyFilters/publicKey" type="list"/>
						 
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
										</c:when>
									</c:choose>
									<tr class="bodyrow">
												<td>Format</td>
												<td><code>${collectionDAO.libraryFormat}</code></td>
											</tr>
									<tr class="bodyrow">
										<td>UUID</td>
										<td>
											${uuid}
											
											<c:if test="${initParam.runMode == 'test'}">
												<nobr>[Test notify:
												<jsp:useBean id="nowDate" class="java.util.Date" scope="page"/>
												<a href="<c:url value='/'/>log_results.do?status=2&amp;uuid=${uuid}&amp;ts=${f:replaceAll(f:dateToUtcDatestamp(nowDate),':','-')}" title="log success">s</a>
												<a href="<c:url value='/'/>log_results.do?status=3&amp;uuid=${uuid}&amp;ts=${f:replaceAll(f:dateToUtcDatestamp(nowDate),':','-')}" title="log success with warnings">sw</a>
												<a href="<c:url value='/'/>log_results.do?status=4&amp;uuid=${uuid}&amp;ts=${f:replaceAll(f:dateToUtcDatestamp(nowDate),':','-')}" title="log failure">f</a>
												<a href="<c:url value='/'/>log_results.do?status=5&amp;uuid=${uuid}&amp;ts=${f:replaceAll(f:dateToUtcDatestamp(nowDate),':','-')}" title="log failure - timeout">to</a>
												<a href="<c:url value='/'/>log_results.do?status=2&amp;uuid=BOGUS-${uuid}&amp;ts=${f:replaceAll(f:dateToUtcDatestamp(nowDate),':','-')}" title="test improper log">b</a>]</nobr>
											</c:if>											
										</td>
									</tr>											
								</tbody>
							</table>
						</div>
					</c:when>
					<c:otherwise>
						There is no information available for this harvest.
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		
		<div id="logs" class="tabDiv" style="display:none">
			<div class="contentDiv">

				<h3>Harvest Logs</h3>
				<div style="padding-bottom:6px">Harvest date: <fmt:formatDate value="${myDate}" type="both" dateStyle="long" timeStyle="long"/></div>
				<div style="padding-bottom:10px"><a href="javascript:doLogDisplay(0)">Refresh logs</a></div>
				<div id="${uuid}-logs"></div>	
				
			</div>
		</div>
		
		<div id="records" class="tabDiv" style="display:none">
			<div class="contentDiv">

				<h3>Harvested Records</h3>
				<div style="padding-bottom:6px">Harvest date: <fmt:formatDate value="${myDate}" type="both" dateStyle="long" timeStyle="long"/></div>
				<div style="padding-bottom:10px"><a href="javascript:void(0)" onclick="doRecordsDisplay('/')">Refresh listing</a></div>
				<div id="${uuid}-records"></div>	
								
			</div>
		</div>		
		
	</div>
	
	<c:import url="bottom.jsp"/>
</body>
</html>


