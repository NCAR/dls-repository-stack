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

<%-- Determine how indexing info should be displayed depending on wheter it's coming from
the file indexer versus the IndexingManager (for external data sources like the NDR)  --%>
<c:set var="isIndexedByIndexingManager">${applicationScope.recordDataSource != 'fileSystem'}</c:set>

<%-- Background indexing in use?  --%>

<c:set var="isBackgroundIndex" value="${applicationScope.isBackgroundIndex}"/>
<c:set var="isForegroundIndex" value="${applicationScope.isForegroundIndex}"/>
<c:set var="isImportIndex" value="${applicationScope.isImportIndex}"/>

<c:set var="isIndexingContinuously" value="${initParam.performContinuousIndexing == 'true'}"/>

<%-- Determine if the IDMapper is being used --%>
<c:set var="idMapperUrl" value='${fn:trim(initParam["dbURL"])}'/>
<c:set var="isIDMapperUsed">${idMapperUrl != 'id_mapper_not_used'}</c:set>

<c:set var="showIndexingMessages">${param.command == 'showIndexingMessages'}</c:set>

<%-- This needs to be saved here since var 'index' is conflicted below --%>
<c:set var="indexLocation" value="${index.indexLocation}"/>

<c:set var="dataSourceString">
<c:choose>
	<c:when test="${applicationScope.recordDataSource == 'fileSystem'}">
		file system
	</c:when>
	<c:when test="${applicationScope.recordDataSource == 'edu.ucar.dls.dds.ndr.NDRIndexer'}">
		NDR
	</c:when>
</c:choose>
</c:set>

<html:html>

<head>
	<title>Collection Manager</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

	<%-- <link rel='stylesheet' type='text/css' href='/dlese_shared/dlese_styles.css'> --%>

	<style type="text/css">
		/* Override the 'suggested' background color behind the resources's title */
		LI.actions {
			padding-bottom:8px;
			list-style-type:circle;
		}
		.footnote {
			font-size: 8pt;
		}
		#mainBody TABLE {
			margin: 0px;
			padding: 0px;
		}
	</style>
	<link rel='stylesheet' type='text/css' href='dds_admin_styles.css'>
	<%@ include file="/nav/head.jsp" %>

</head>

<body text="#000000" bgcolor="#ffffff">
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Collection<br/> Manager
</div>


<%@ include file="/nav/top.jsp" %>

<div id="mainBody">

<c:if test="${!isDeploayedAtDL}">
	<a href="${pageContext.request.contextPath}/index.jsp"><img id="ddsLogo" border="0" alt="Digital Discovery System (DDS)" title="Digital Discovery System (DDS)" src="${pageContext.request.contextPath}/admin/images/dds_logo_sm.gif"/></a>
	<div id="ddsCustomBanner">${initParam.globalInstallationBanner}</div>
</c:if>


	  <h1>Collection manager</h1>

	<%-- ####### Display messages, if present ####### --%>
	<logic:messagesPresent>



	<table width="99%" bgcolor="#000000" cellspacing="1" cellpadding="0">
	  <tr bgcolor="ffffff">
		<td style="padding:8px">

				<%-- <html:messages id="msg" property="showIndexMessagingLink">
					Indexing messages are displayed below<br><br>
				</html:messages>	 --%>

				<c:if test="${showIndexingMessages}">
					<b>Actions:</b>
					<ul>
						<html:messages id="msg" property="showIndexMessagingLink">
							<form action="admin.do">
								<li class="actions">
									<bean:write name="msg"/>
									<button class="smallButton" type="submit">Refresh indexing status</button> - Refresh the indexing messages.
									<input name="command" value="showIndexingMessages" type="hidden"/>
								</li>
							</form>
						</html:messages>
						<form>
							<li class="actions"><button class="smallButton" type="submit">Close messages</button></li>
						</form>
					</ul>
				</c:if>

				<b>${showIndexingMessages ? 'Indexing status messages:' : 'Messages'} ${isBackgroundIndex  ? '<span style="color:orange">(showing status for background index)</span>' : ''}</b>
				<c:if test="${showIndexingMessages}">
					<div class="msgList">
				</c:if>
					<html:messages id="msg" property="message">
						<div><ul><li><bean:write name="msg"/></li></ul></div>
					</html:messages>
					<html:messages id="msg" property="error">
						<div><ul><li style="color:red;">Error: <bean:write name="msg"/></li></ul></div>
					</html:messages>
					<%-- <html:messages id="msg" property="showIndexMessagingLink">
						<logic:greaterThan name="raf" property="numIndexingErrors" value="0">
							<li>Some records had errors during indexing.
							<a href="report.do?q=error:true&s=0&report=Files+that+could+not+be+indexed+due+to+errors">See
							list of errors</a></li>
						</logic:greaterThan>
					</html:messages>  --%>
				<c:if test="${showIndexingMessages}">
					</div>
				</c:if>

				<c:if test="${!showIndexingMessages}">
					<b>Actions:</b>

					<ul>
						<c:if test="${param.command == 'reindexCollection'}">
							<form action="admin.do">
								<li class="actions">
									<button class="smallButton" type="submit">Check indexing status</button> - View indexing status messages.
									<input name="command" value="showIndexingMessages" type="hidden"/>
								</li>
							</form>
						</c:if>
						<form>
							<li class="actions"><button class="smallButton" type="submit">Close messages</button></li>
						</form>
					</ul>
				</c:if>
		</td>
	  </tr>
	</table>

	<br><br>
	</logic:messagesPresent>


		<p>
			Use the Collection Manager to view and administer collections in the repository.
		</p>

		<p>
			<form action="admin.do">
				<button class="smallButton" type="submit">Check indexing status</button> - View information about the progress of the indexing process.
				<input name="command" value="showIndexingMessages" type="hidden"/>
			</form>

		  <logic:greaterThan name="raf" property="numIndexingErrors" value="0">
			<br>
				Note: Some records had errors during indexing. See:
				<a href="report.do?q=error:true&s=0&report=Files+that+could+not+be+indexed+due+to+errors">Files that
				could not be indexed due to errors</a>.
			</logic:greaterThan>
		</p>

	<%-- ======== Start collectionsTable ======== --%>
	<c:if test="${cachedTableVersion != index.lastModifiedCount || not empty param.reload || not empty param.disableSet || not empty param.enableSet}">
		<c:set var="cachedTableVersion" value="${index.lastModifiedCount}" scope="application"/>
		<c:set var="collectionsTable" scope="application">

		<c:set var="collCount" value="${fn:length(raf.sets)}"/>


		<div style="margin-top:15px"><h3>Collections</h3></div>

		<div style="margin-top:5px">Find collections:</div>
		<table border="0" cellpadding="0" cellspacing="0" width="100%"><tr>
			<td align="left" nowrap>
				<form id="findForm" action="javascript: void(0)" name="findForm" style="margin:2px 0px 4px 0px;">
					<input onkeyup="doFind()" id="chars" size="40" type="text" name="chars" value="" />
					<input id="showAllButton" type="button" value="Clear" onclick="$('chars').clear().focus(); doFind()" />
				</form>
			</td>
			<td align="left" width="100%" nowrap><a href="<c:url value=''/>?reload=t" style="margin-left:15px">Reload table</a></td>
			<c:choose>
				<c:when test="${collCount > 0}">
					<td align="right" nowrap><span id="collsFound">${collCount}</span> of ${collCount} collections</td>
				</c:when>
			</c:choose>
		</tr></table>

		  <c:set var="totalFiles" value="0"/>
		  <c:set var="totalIndexed" value="0"/>
		  <c:set var="totalErrors" value="0"/>
		  <c:set var="totalIndexEntries" value="${index.numDocs}"/>

		  <table width="100%" bgcolor="#666666" cellpadding="6" cellspacing="1" border="0" style="margin-top:5px" class="sortable">
			<%-- ######## Collections UI ######## --%>
			<html:form action="/admin/admin" method="GET">
			<thead>
			<tr>
			  <td align="center" class="admin_blue_dark" nowrap >
			  		<a href='javascript:void(0)' title="Sort by collection name" class="headLabel"><b>Collection Name</b></a>
			  </td>
			  <td align="center" class="admin_blue_dark" nowrap>
				<a href='javascript:void(0)' title="Sort by key" class="headLabel"><b>Key</b></a>
			  </td>
			  <td align="center" class="admin_blue_dark" nowrap>
				<a href='javascript:void(0)' title="Sort by format" class="headLabel"><b>Format</b></a>
			  </td>
			  <c:if test="${!isIndexedByIndexingManager}">
				  <td align="center" class="admin_blue_dark" nowrap>
					<a href='javascript:void(0)' title="Sort by number of files" class="headLabel"><b>Num<br/>Files</b></a>
				  </td>
			  </c:if>
			  <td align="center" class="admin_blue_dark" nowrap>
				<a href='javascript:void(0)' title="Sort by number of items indexed" class="headLabel"><b>Num<br>Indexed</b></a>
			  </td>
			  <c:if test="${!isIndexedByIndexingManager}">
				  <td align="center" class="admin_blue_dark" nowrap>
					<a href='javascript:void(0)' title="Sort by number of items with indexing errors" class="headLabel"><b>Record<br>Errors</b></a>
				  </td>
			  </c:if>
			  <td align="center" class="admin_blue_dark" nowrap>
				<a href='javascript:void(0)' title="Sort by status" class="headLabel"><b>Discovery<br>Status</b></a>
			  </td>
			  <c:if test="${isForegroundIndex}">
				  <td align="center" class="admin_blue_dark" nowrap>
					<a href='javascript:void(0)' title="Sort by action" class="headLabel"><b>Action</b></a>
				  </td>
			   </c:if>
			</tr>
			</thead>
			</html:form>

			<tbody>
			<logic:empty name="raf" property="sets">
			<tr>
			  <td colspan="8" class='admin_blue1' style="color:gray" align="center">
				<b>No collections specified</b>
			  </td>
			 </tr>
			</logic:empty>


			<logic:notEmpty name="raf" property="sets">
			<logic:iterate id="set" name="raf" property="sets" indexId="ii">

			  <c:set var="totalFiles" value="${totalFiles + set.numFiles}"/>
			  <c:set var="totalIndexed" value="${totalIndexed + set.numIndexed}"/>
			  <c:set var="totalErrors" value="${totalErrors + set.numIndexingErrors}"/>

			<tr class="collrow">
			  <td class='admin_blue1'>
				<a href='display.do?fullview=<bean:write name="set" property="id"/>' title='Show collection information for ${set.name}' class="collTitleLnk"><bean:write name="set" property="name"/></a>
			  </td>
			  <td class='admin_blue1'>
				${set.setSpec}
				<c:if test="${set.setSpec == 'collect'}">
					<a href="#2"><sup class="footnote">2</sup></a>
				</c:if>
			  </td>
			  <td class='admin_blue1'>${set.format}</td>
			  <%-- Num files --%>
			  <c:if test="${!isIndexedByIndexingManager}">
				<td align="center" class='admin_blue1'>
					<span style="display:none">asdf<fmt:formatNumber pattern="#########" minIntegerDigits="9" value="${set.numFiles}"/></span><%-- for sorting function... --%>
					<fmt:formatNumber type="number" value="${set.numFiles}"/>
				</td>
			  </c:if>
			  <%-- Num indexed --%>
			  <td align="center" class='admin_blue1'>
			  	<span style="display:none"><fmt:formatNumber pattern="#########" minIntegerDigits="9" value="${set.numIndexed}"/></span><%-- for sorting function... --%>
				<logic:greaterThan name="set" property="numIndexed" value="0">
					<c:set value="dlese_collect" target="${raf}" property="metaFormat" />
					<c:set value="key" target="${raf}" property="field" />
					<c:set value="${set.setSpec}" target="${raf}" property="value" />
					<c:set value="${fn:trim(raf.vocabTerm.id)}" var="vocabKyId"/>
					<a href='query.do?q=&s=0&ky=${vocabKyId}' title='Browse records for "${set.name}"'><fmt:formatNumber type="number" value="${set.numIndexed}"/></a>
					<c:if test="${!raf.isVocabTermAvailable}">
						<%-- Un-comment below to show footnote for collections not in MUI --%>
						<%-- <a href="#4"><sup class="footnote">4</sup></a> --%>
						<c:choose>
							<c:when test="${empty missingCollectionKeys}">
								<c:set var="missingCollectionKeys" value="${set.setSpec}"/>
							</c:when>
							<c:otherwise>
								<c:set var="missingCollectionKeys" value="${set.setSpec}, ${missingCollectionKeys}"/>
							</c:otherwise>
						</c:choose>
					</c:if>
				</logic:greaterThan>
				<logic:equal name="set" property="numIndexed" value="0">
					0
				</logic:equal>
			  </td>
			  <%-- Num indexing errors --%>
			  <c:if test="${!isIndexedByIndexingManager}">
				  <td align="center" class='admin_blue1'>
				  	<span style="display:none"><fmt:formatNumber pattern="#########" minIntegerDigits="9" value="${set.numIndexingErrors}"/></span><%-- for sorting function... --%>
					<logic:greaterThan name="set" property="numIndexingErrors" value="0">
						<a href='report.do?q=error:true+AND+docdir:&quot;${f:escape(set.directory)}&quot;&s=0&report=Indexing+errors+for+collection+<bean:write name="set" property="nameEncoded"/>'
							title='Browse indexing errors for "${set.name}"'><fmt:formatNumber type="number" value="${set.numIndexingErrors}"/></a>
					</logic:greaterThan>
					<logic:equal name="set" property="numIndexingErrors" value="0">
						0
					</logic:equal>
				  </td>
			  </c:if>
			  <td valign="middle" align="center" class='admin_blue1' nowrap>
				<%-- Enable / Disable Collection --%>
				<c:if test="${set.enabled == 'true'}">
					<form action="admin.do" onsubmit="return confirm( 'Are you sure you want to disable access to ${f:jsEncode(set.name)}?' )">
						<span style="color:#333333; font-weight:bold;margin-right:3px;">Enabled</span>
						<button class="smallButton" type="submit">Disable</button>
						<input name="disableSet" value="t" type="hidden"/>
						<input name="currentIndex" value="${ii}" type="hidden"/>
						<input name="currentSetName" value="${set.name}" type="hidden"/>
						<input name="currentSetSpec" value="${set.setSpec}" type="hidden"/>
						<input name="setUid" value="${set.uniqueID}" type="hidden"/>
					</form>
				</c:if>
				<c:if test="${set.enabled != 'true'}">
					<form action="admin.do" onsubmit="return confirm( 'Are you sure you want to enable access to ${f:jsEncode(set.name)}?' )">
						<span style="color:gray;margin-right:3px;">Disabled</span>
						<button class="smallButton" type="submit">Enable</button>
						<input name="enableSet" value="t" type="hidden"/>
						<input name="currentIndex" value="${ii}" type="hidden"/>
						<input name="currentSetName" value="${set.name}" type="hidden"/>
						<input name="currentSetSpec" value="${set.setSpec}" type="hidden"/>
						<input name="setUid" value="${set.uniqueID}" type="hidden"/>
					</form>
				</c:if>
				<c:if test="${set.accessionStatus != 'accessioned'}">
					<div style="color:gray; font-size:80%">Collection not accessioned <a href="#1"><sup class="footnote">1</sup></a></div>
				</c:if>
			  </td>
			  <c:if test="${isForegroundIndex}">
				  <td align="center" valign="middle" class='admin_blue1' nowrap>
						<form action="admin.do" onsubmit='return confirm( "${set.numIndexed > 0 ? 'Reindex' : 'Index'} the records for ${f:jsEncode(set.name)}?" )'>
							<button class="smallButton" type="submit">${set.numIndexed > 0 ? 'Reindex' : 'Index'}</button>
							<input name="command" value="reindexCollection" type="hidden"/>
							<input name="key" value="${set.setSpec}" type="hidden"/>
						</form>


					<%-- [<a href="admin.do?command=reindexCollection&key=${set.setSpec}" title="${set.numIndexed > 0 ? 'Reindex' : 'Index'} the records for ${set.name}"
						  onclick='return confirm( "${set.numIndexed > 0 ? 'Reindex' : 'Index'} the records for ${f:jsEncode(set.name)}?" )'>${set.numIndexed > 0 ? 'reindex' : 'index'}</a>] --%>
				  </td>
			  </c:if>
			 </tr>
			</logic:iterate>
			<tr id="noCollMsgRow" style="display:none">
				<td class="admin_blue1" colspan="${isBackgroundIndex ? '5' : '8'}">No matching collections found</td>
			</tr>

			</logic:notEmpty>
			</tbody>
		  </table>

		  <%-- ============= Summary of repository ============= --%>
		  <logic:notEmpty name="raf" property="sets">
		   <div style="margin-top:15px"><b>Summary</b></div>
		   <table width="100%" bgcolor="#666666" cellpadding="6" cellspacing="1" border="0" style="margin-top:5px">
		   <thead>
			<tr>
			  <td align="center" colspan="1" class='admin_blue_dark' nowrap>
				Collections
			  </td>

			  <c:if test="${!isIndexedByIndexingManager}">
				  <td align="center" colspan="1" class='admin_blue_dark' nowrap>
					Files
				  </td>
			  </c:if>

			  <td align="center" colspan="1" class='admin_blue_dark' nowrap>
				Records
			  </td>

			  <c:if test="${!isIndexedByIndexingManager}">
				  <td align="center" colspan="1" class='admin_blue_dark' nowrap>
					Indexing errors
				  </td>
			  </c:if>

			  <td align="center" colspan="1" class='admin_blue_dark' nowrap>
				Index entries
			  </td>
			</tr>
		   </thead>

		   <tbody>
			<tr>
			  <td align="center" class='admin_blue1' nowrap>
				${collCount}
			  </td>

			  <c:if test="${!isIndexedByIndexingManager}">
				  <td align="center" class='admin_blue1' nowrap>
					<fmt:formatNumber type="number" value="${totalFiles}"/>
				  </td>
			   </c:if>
			  <td align="center" class='admin_blue1' nowrap>
				<a href='query.do?q=*:*&s=0'
					title='Browse all records'><fmt:formatNumber type="number" value="${totalIndexed}"/></a>
			  </td>
			  <c:if test="${!isIndexedByIndexingManager}">
				  <td align="center" class='admin_blue1' nowrap>
						<a href='report.do?q=error:true&s=0&report=All+indexing+errors'
							title='Browse all indexing errors'><fmt:formatNumber type="number" value="${totalErrors}"/></a>
				  </td>
			  </c:if>
			  <td align="center" class='admin_blue1'>
				<fmt:formatNumber type="number" value="${totalIndexEntries}"/>
				<c:if test="${isIDMapperUsed}"><a href="#3"><sup class="footnote">3</sup></a></c:if>
			  </td>
			</tr>
			<%-- <tr>
				<td colspan="5" class='admin_blue1'>
							  <div stylez="margin:4px 0px 4px 0px">Repository index was created <fmt:formatDate value="${index.creationDate}" type="both" timeStyle="short"/> and last modified <fmt:formatDate value="${index.lastModifiedDate}" type="both" timeStyle="long"/></div>

				</td>
			</tr> --%>
			</tbody>
			</table>

			<table width="10%" bgcolor="#666666" cellpadding="6" cellspacing="1" border="0" style="margin-top:10px">
				<tbody>
					<tr>
						<td class='admin_blue_dark' width="1%" nowrap>Index created</td>
						<td colspan="4" class='admin_blue1' nowrap>
							<div style="padding: 0px 4px 0px 4px">
								<fmt:formatDate value="${index.creationDate}" type="both" timeStyle="long"/>
							</div>
						</td>
					</tr>
					<tr>
						<td class='admin_blue_dark'  width="1%" nowrap>Index last modified</td>
						<td colspan="4" class='admin_blue1' nowrap>
							<div style="padding: 0px 4px 0px 4px">
								<fmt:formatDate value="${index.lastModifiedDate}" type="both" timeStyle="long"/>
							</div>
						</td>
					</tr>
				</tbody>
			</table>


		  </logic:notEmpty>

		</c:set>
		</c:if>
		${collectionsTable}

		<%-- ======== End collectionsTable ======== --%>


		<table width="100%" bgcolor="#ffffff" cellpadding="4" cellspacing="1" border="0">
			<tr>
				<td bgcolor='#ffffff' style="font-size:8pt">
					<a name="1"></a>
					<sup class="footnote">1</sup>
					The Disabled/Enabled settings control which collections are searchable/discoverable
					in the search Web services (DDSWS and JSHTML). If a collection is not accessioned
					according to it's collection record, this is indicated above,
					however the searchable/discoverable behavior of these
					collections are still controlled by the Disabled/Enabled settings.
				</td>
			</tr>
			<tr>
				<td bgcolor='#ffffff' style="font-size:8pt">
					<a name="2"></a>
					<sup class="footnote">2</sup>
					The records in the 'collect' collection define which collections appear
					in this repository.
				</td>
			</tr>
			<c:if test="${isIDMapperUsed}">
				<tr>
					<td bgcolor='#ffffff' style="font-size:8pt">
						<a name="3"></a>
						<sup class="footnote">3</sup>
						The number of entries in the index may be less
						than the number of records indexed. This occurs because a single library resource
						is sometimes cataloged multiple times across ADN collections, in which case all
						records are placed within a single index entry for the given resource,
						'de-duping' them so that only one result will appear for the resource in searches.
					</td>
				</tr>
			</c:if>
			<%-- Un-comment below to show footnote for collections not in MUI --%>
			<%-- <c:if test="${not empty missingCollectionKeys}">
				<tr>
					<td bgcolor='#ffffff' style="font-size:8pt">
						<a name="4"></a>
						<sup class="footnote">4</sup> Indicates the vocab ID is missing in the MUI
						files file for this collection
						(MUI IDs are missing for the following collection keys:<code style="color:red"> ${missingCollectionKeys}</code>.
					</td>
				</tr>
			</c:if> --%>
		</table>

		<table>

		<%-- Links to reports --%>
		<tr>
			<td colspan=2>
			  <br><br>
			  <h3>View the fields and terms in the index</h3>
			</td>
		</tr>
		<tr>
			<td colspan=2>
				<p>View the full list of <a href="reporting/">fields and terms in the index</a> and
				<a href="reporting/#otherReports">other reports</a> from the contents of the index.</p>
			</td>
		</tr>

		<%-- Links to index admin --%>
		<tr>
			<td colspan=2>
			  <br/><br/>
			  <h3>Index administration</h3>
			</td>
		</tr>

		<tr>
			<td colspan=2>
				<p>
					<c:choose>
						<c:when test="${isImportIndex}">
							The local repository is read-only and indexing is being handled externally.
							The index will be reloaded once an index is imported externally
						</c:when>
						<c:when test="${isBackgroundIndex}">
							<c:choose>
								<c:when test="${isIndexingContinuously}">
									The indexer is configured to build new indexes in the background automatically and continuously.
									When the system starts, the indexer begins working in the background.
									Changes are seen in the repository only when a new index is fully complete and committed.
									The indexer then begins building another index again in the background.
								</c:when>
								<c:otherwise>
									The indexer is configured to build new indexes in the background when started manually using the controls below
									and/or automatically as described below.
									Changes are seen in the repository only when a new index is fully complete and committed.
								</c:otherwise>
							</c:choose>
							<div style="padding-top:3px">
								The background indexer config file is located at <code>${initParam.itemIndexerConfigDir}</code> using indexer class
								<nobr><code>${initParam.recordDataSource} (recordDataSource)</code></nobr>.
							</div>
						</c:when>
						<c:otherwise>
							The indexer is configured to for incremental updates. Changes will be seen immediately
							each time a record, group of records, or collection is updated.
						</c:otherwise>
					</c:choose>
				</p>

				<c:if test="${!isImportIndex}">
					<p>
						<c:choose>
							<c:when test="${empty raf.indexingStartDate}">
								  The indexer has been configured to allow manual updating only
								  (automatic updating has been disabled). Click 'Reindex all files'
								  to update the index manually.
								  To enable automatic updates at regular intervals, edit the context parameters
								 <code>indexingStartTime</code> and  <code>indexingDaysOfWeek</code> in server.xml or web.xml.
							</c:when>
							<c:otherwise>
								  The indexer is scheduled to run automatically on ${raf.indexingDaysOfWeek}
								  at ${raf.indexingTimeOfDay} (next run will occur on or after ${raf.indexingTimeOfDay} ${raf.indexingStartDate}).
								  To change these values, edit the context parameters
								   <code>indexingStartTime</code> and  <code>indexingDaysOfWeek</code> in server.xml or web.xml.
							 </c:otherwise>
						</c:choose>
					</p>
				</c:if>

				<c:if test="${!isImportIndex}">
					<p>
						The following actions are available for managing the DDS Lucene index.
						You may wish to <a href="admin.do?command=showIndexingMessages">check the most recent indexing status</a>
						before performing one or more of these actions.
					</p>
				</c:if>
				<table width="100%" bgcolor="#ffffff" cellpadding="0" cellspacing="8" border="0">
					<c:if test="${isIndexedByIndexingManager}">
						<c:if test="${!isImportIndex}">
							<tr valign="top">
								<td>
									<html:form action="/admin/admin"
										onsubmit="return confirm( 'This action will update the index using the external indexer. This will take a while before completing. Continue?' )"
										method="post" style="margin-top:4px;">
										<input type="hidden" name="command" value="indexingManagerAction"/>
										<input type="hidden" name="imCommand" value="indexAllCollections"/>
										<html:submit property="button" value="Index data sources" />
									</html:form>
								</td>
								<td>
									This action updates the index ${isBackgroundIndex ? 'in the background':''} from the ${dataSourceString} data source.
								</td>
							</tr>
							<tr valign="top">
								<td>
									<html:form action="/admin/admin"
										onsubmit="return confirm( 'This action will stop the indexing process. This may take a while before completing. Continue?' )"
										method="post" style="margin-top:4px;">
										<input type="hidden" name="command" value="indexingManagerAction"/>
										<input type="hidden" name="imCommand" value="abortIndexing"/>
										<html:submit property="button" value="Stop indexing" />
									</html:form>
								</td>
								<td>
									This action stops the ${isBackgroundIndex ? 'background':''} indexer at it's current point of progress.
								</td>
							</tr>
						</c:if>
					</c:if>

					<c:if test="${!isIndexedByIndexingManager}">
						<tr valign="top">
							<td>
								<html:form action="/admin/admin"
									onsubmit="return confirm( 'This action will update the index for all records configured above. The process may take several minutes to complete. Continue?' )"
									method="post" style="margin-top:4px;">
									<input type="hidden" name="command" value="Reindex all files"/>
									<html:submit property="button" value="Reindex all records" />
								</html:form>
							</td>
							<td>
								This action adds, deletes or updates
								each entry in the index, synchronizing the index with
								the files configured in the collections above. This process occurs
								in the background without disrupting normal discovery.
							</td>
						</tr>
						<tr valign="top">
							<td>
								<html:form action="/admin/admin"
									onsubmit="return confirm( 'This action will instruct the indexer to stop at its current stage of progress. Continue?' )"
									method="post" style="margin-top:4px;">
									<input type="hidden" name="command" value="stopIndexing">
									<html:submit property="button" value="Stop the indexer" />
								</html:form>
							</td>
							<td>
								This action stops the indexer at
								it's current stage of operation, if the indexer is currently running.
								Indexing will begin again automatically at the next scheduled update or may be started
								manually by clicking 'Reindex all files' above.
							</td>
						</tr>
					</c:if>
					<c:if test="${isForegroundIndex}">
						<tr valign="top">
							<td>
								<html:form action="/admin/admin"
									onsubmit="return confirm( 'Warning! This action will DELETE and rebuild the index. All items will become temporarily undiscoverable until they are reindexed. Continue?' )"
									method="post" style="margin-top:4px;">
									<input type="hidden" name="command" value="rebuildIndex">
									<html:submit property="button" value="Reset the index" />
								</html:form>
							</td>
							<td>
								<c:choose>
									<c:when test="${!isIndexedByIndexingManager}">
										This action deletes and rebuilds the
										index from scratch. All items become undiscoverable until they are reindexed. Perform this action only if
										you believe the index is corrupt or can not be updated properly using the 'Reindex all files' action.
									</c:when>
									<c:otherwise>
										This action deletes the index.
										All items become undiscoverable until they are reindexed. Perform this action only if
										you believe the index is corrupt.
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:if>
				</table>
			</td>
		</tr>

		<%-- Service access permissions --%>
		<tr>
			<td colspan=2>
			  <br><br>
			  <h3>Access permissions for Web services</h3>
			</td>
		</tr>
		<tr>
			<td colspan=2>
				<p>Permit clients full read/write access to the <a href="../services/ddsupdatews1-1/ddsupdatews_api_documentation.jsp">Update API</a> (DDSUpdateWS) and <i>non-discoverable</i> records in
				the <a href="../services/ddsws/">Search API</a> (DDSWS) by entering their IP address below.
				</p>

				<p>
					Access to the Update API is restricted to authorized IPs only.
					Records in the Search API are considered <i>discoverable</i> and avialable without restriction
					if their collection is <i>Enabled</i> in the Colleciton
					Manager above and, for ADN records, the record's status is <i>accessioned-discoverable</i>.
					All other records are considered <i>non-discoverable</i> and are restricted to authorized IPs only.
				</p>

				<p>
				<html:form action="/admin/admin" method="get">
					<table width="100%" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td colspan="2" style="padding-bottom:2px">
								Authorize full read/write access to these IPs (comma-separated list):
							</td>
						</tr>
						<tr valign="top">
							<td width="20%">
								<input type="hidden" name="command" value="setTrustedIps"/>
								<html:text property="trustedWsIps" name="raf" size="80"/>
								<div style="font-size:8pt">
									Examples: <i>127.0.0.1</i> (localhost), <i>128.117.126.132</i> or using wildcards <i>128.117.126.*, 128.114.166.*</i>
								</div>
							</td>
							<td align="left" style="padding-left:4px">
								<html:submit property="button" value="Save IP Addresses" />
							</td>
						</tr>
					</table>

				</html:form>
				</p>
			</td>
		</tr>

		<%-- Repository configuration --%>
		<tr>
			<td colspan=2>
			  <br><br>
			  <h3>DDS configuration</h3>
			</td>
		</tr>
		<tr>
			<td colspan=2>

			<p>
				<c:choose>
					<c:when test="${isIndexedByIndexingManager}">
						The collections being indexed by this system are being managed by the indexing class <code>${initParam.recordDataSource}</code>.
						The indexing configuration file is located at: <br>
						<img src="images/file_folder2.gif" width="12" height="12"> <span class="initParamValues">${initParam.itemIndexerConfigDir}</span>.
						Edit the config file to make changes in the indexing behavior.
						To define where the config file is located, edit the context parameter <code>itemIndexerConfigDir</code> in server.xml or web.xml.
					</c:when>
					<c:otherwise>
						The collections and metadata files being indexed by this system are located at:<br>
						<img src="images/file_folder2.gif" width="12" height="12"> <span class="initParamValues">${raf.metadataRecordsLocation}</span>.
						To change where these are located, edit the context parameter <code>collBaseDir</code> in server.xml or web.xml.
					</c:otherwise>
				</c:choose>
			</p>

			  <P>
				  The index is located at:<br>
				  <img src="images/file_folder2.gif" width="12" height="12"> <span class="initParamValues">${indexLocation}</span>.
				  The index is generated and managed internally by the DDS system.
				  To change where it is stored, edit the context parameter <code>indexLocation</code> in server.xml or web.xml.
			  </P>

			  <p>
				Persistent data for DDS application settings are stored at:<br>
				<img src="images/file_folder2.gif" width="12" height="12"> <span class="initParamValues">${raf.repositoryDataDir}</span>.
				These persistent settings are saved and retrieved internally by the DDS system.
				To change where these are stored, edit the context parameter <code>repositoryData</code> in server.xml or web.xml.
			  </p>

			  <p>
				DDS and Repository Manager configuration properties files are located at:<br>
				<img src="images/file_folder2.gif" width="12" height="12"> <span class="initParamValues">${raf.configDirLocation}</span>. These
				files are used to configure the default search fields (title, description, URL, ID, etc.) for given XML frameworks, fields used for boosting, and other functions.
				These are loaded on startup and may be edited manually and then reloaded using the button below.
				<html:form action="/admin/admin"
					method="get" style="margin-top:4px;">
					<input type="hidden" name="command" value="reloadRepositoryConfig">
					<input type="submit" name="button" value="Reload configuration files">
				</html:form>
			  </p>

			  <c:if test="${isIDMapperUsed}">
				  <p>
					  	<img src="images/db.gif" width="13" height="11">
						The ID Mapper service that is being used is located at:<br/>
						<span class="initParamValues">${fn:substringBefore(idMapperUrl,"?")}</span>.
						To change, edit the context parameter <code>dbURL</code> in server.xml, or
						remove the parameter <code>dbURL</code> to disable the ID Mapper.
				  </p>
			  </c:if>

			  <c:if test="${isIDMapperUsed}">
				  <p>
				  <img src="images/file_folder2.gif" width="12" height="12">
				  <c:set var="idMapperExclusionFile" value='${fn:trim(initParam["idMapperExclusionFile"])}'/>
				  <c:choose>
					<c:when test="${idMapperExclusionFile == null || idMapperExclusionFile == 'none'}">
						The ID Mapper service is not currently using an ID duplicates exclusion file. To configure
						one, add a context parameter <code>idMapperExclusionFile</code> in server.xml
						that contains the URI to the XML file that lists IDs to be excluded from the
						IDMapper duplicates.
					</c:when>
					<c:otherwise>
						The ID Mapper service is using the ID duplicates exclusion file locate at:<br>
						<span class="initParamValues">
						<c:choose>
							<c:when test="${fn:startsWith(idMapperExclusionFile,'http')}">
								<a href="${idMapperExclusionFile}" target="_blank">${idMapperExclusionFile}</a>.
							</c:when>
							<c:otherwise>
								${idMapperExclusionFile}.
							</c:otherwise>
						</c:choose>
						</span>
						To change, edit the context parameter <code>idMapperExclusionFile</code> in server.xml, or
						remove the parameter <code>idMapperExclusionFile</code> to use none.
					</c:otherwise>
				  </c:choose>
				  </p>
			  </c:if>


				<p>
				This is
				<a href="docs/CHANGES.txt" title="Read version release notes and change documentation">DDS version @VERSION@</a>.
				<c:if test="${not empty applicationScope.ddsStartUpDate}">
					The system was started on <fmt:formatDate value="${applicationScope.ddsStartUpDate}" type="both"/>.
				</c:if>
				</p>

			</td>
		</tr>


		<%-- Configure the search boosting values --%>
<%-- 		<tr>
			<td colspan=2>
			  <br><br>
			  <h3>Search result boosting</h3>
			</td>
		</tr>
		<tr>
			<td colspan=2>

				  <p>Boosting factors are used to modify the order of search results by adjusting the relative rank of
				  the items that appear in the result set. A boosting factor of 0 equates
				  to minimum or no adjustment in relative rank, a factor of 0.5 equates to a small boost in relative ranking,
				  a factor 2.5 equates to a large boost in relative ranking and a factor of approximately 6.0 or greater equates to
				  an absolute boost in rank. Values must be a number equal to or grater than 0 and may include decimals.
				  Examples: 0.1 (small boost in rank), 1.3 (medium boost in rank), 2.5 (large boost in rank)
				  or 6 (absolute boosting of rank). Changes take effect immediately and do not require re-indexing.</p>
				<p>
				<html:form action="/admin/admin" method="post">
					<table>
						<tr>
							<td>
								DRC boosting factor:
							</td>
							<td>
								<html:text property="drcBoostFactor" name="raf" size="6"/>
							</td>
						</tr>
						<tr>
							<td>
								Resources with multiple records boosting factor:
							</td>
							<td>
								<html:text property="multiDocBoostFactor" name="raf" size="6"/>
							</td>
						</tr>
						 </table>
					<input type="hidden" name="command" value="ubf">
					<br><html:submit property="button" value="Set boosting values" />
					or <a href="admin.do?command=ubf&resetDefaultBoosting=t"><b>Restore default boosting values</b></a>
				</html:form>

				</p>
			</td>
		</tr>
 --%>

	</table><br>

	  <br>

<table height="400">
  <tr>
    <td>&nbsp;</td>
  </tr>
</table>

</div>

<%@ include file="/nav/bottom.jsp" %>
</body>
</html:html>


