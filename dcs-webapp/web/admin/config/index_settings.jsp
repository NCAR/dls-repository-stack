<%-- $Id: index_settings.jsp,v 1.9 2013/11/08 19:25:36 ostwald Exp $ --%>

<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Indexing</c:set>
<html:html>

<head>
	<title><st:pageTitle title="${title}" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<%@ include file="/baseHTMLIncludes.jsp" %>
</head>

<body>

	<st:pageHeader toolLabel="${title}" currentTool="settings" />

	<%@ include file="indexing_status.jsp" %>
	
	<h3>Indexing Controls</h3>
	<logic:greaterThan name="daf" property="numIndexingErrors" value="0">
		<p>
			<b>Warning:</b> Some records had errors during indexing. <br/>See
			<a href="${contextPath}/admin/report.do?q=error:true&s=0&report=Files+that+could+not+be+indexed+due+to+errors">Files that 
			could not be indexed due to errors</a>.
		</p>
	</logic:greaterThan> 
	<table width="100%" bgcolor="#ffffff" cellpadding="0" cellspacing="8" border="0">
	<tr valign="top">
			<td>
				<button class="smallButton"
								onclick="window.location='${contextPath}/admin/admin.do?command=showIndexingMessages'; return false;">Check indexing status</button>
			</td>
			<td width="90%">For information about the progress of the indexing process.</td>
		</tr>
		</table>
	
	<p>The metadata files being indexed by this system are located at:<br />
	<span class="configPath">${daf.metadataRecordsLocation}</span>.
	To change, edit the context parameter &quot;collBaseDir&quot; in server.xml or web.xml.	
	</p>
	
	<table width="100%" bgcolor="#ffffff" cellpadding="0" cellspacing="8" border="0">
		<%-- IN-PLACE --%>
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
				each entry in the index, occurring
				in the background without disrupting normal operations. 						
			</td>						
		</tr>

		<tr valign="top">
			<td>
				<html:form action="/admin/admin" 
					onsubmit="return confirm( 'This action will stop the indexing process. This may take a while before completing. Continue?' )"
					method="post" style="margin-top:4px;">
					<input type="hidden" name="command" value="stopIndexing"/>
					<html:submit property="button" value="Stop indexing" />
				</html:form>
			</td>
			<td>
				This action stops the ${isBackgroundIndexing ? 'background':''} indexer
				at it's current point of progress, if the indexer is currently running.
				Indexing will begin again automatically at the next scheduled update or
				may be started manually by clicking 'Reindex all records' above.
			</td>						
		</tr>
		
		<%-- CLEAN-SLATE --%>
		<tr valign="top">
			<td>
				<html:form action="/admin/admin" 
					onsubmit="return confirm( 'Warning! This action will DELETE and rebuild the index. All items will become temporarily undiscoverable until they are reindexed. Continue?' )"
					method="post" style="margin-top:4px;">
					<input type="hidden" name="command" value="rebuildIndex" />
					<html:submit property="button" value="Reset the index" />
				</html:form>
			</td>
			<td>
				This action deletes and rebuilds the 
				index from scratch. All items become undiscoverable until they are reindexed. Perform this action only if 
				you believe the index is corrupt or can not be updated properly using the 'Reindex all files' action.
				<i>Please ensure that no one is editing a metadata record or system configuration before performing this action!</i>
			</td>
		</tr>
	</table>

	
<h3>Vocabulary Controls</h3>
	
	<p>The MetadataUI loader file is located at:<br />
		<span class="configPath">${daf.metadataGroupsLoaderFile}</span>. 
		This controls the display of controlled vocabularies in the Metadata Editor.</p>
		
	<table width="100%" bgcolor="#ffffff" cellpadding="0" cellspacing="8" border="0">
		<tr valign="top">
				<td>
					<html:form action="/admin/admin" 
						method="post">
						<html:hidden property="config" value="${indexTag}" />
						<html:submit property="command" value="Reload vocabulary" />
					</html:form> 
				</td>
				<td width="90%">Reloads all vocabularies.  Be sure no record editing is occurring as this operation can
					affect metadata editing forms and may cause an editor's session to become disrupted.</td>
		</tr>
	</table>
	
	<a name="reports"></a>
	<h3>Term Field Reports</h3>

	<p>The <a href="${contextPath}/admin/reporting/">term field reporting area</a> allows you to
	generate reports that detail the terms that appear in various fields as
	well as a list of fields. This area also has a tool for converting words
	to their stem form to see how it is done in the index.</p>

 
 
	<%-- Configure Stemming --%>
<%-- 
	<a name="stemming"></a>
	<h3>Stemming</h3>
	
	<p>Stemming is used to determine the morphological root of a given 
	inflected word form. For example, the stemming algorithm here identifies the words 'ocean,' 'oceans'
	and 'oceanic' as being from the single root 'ocean.' With stemming enabled, users who search for 'oceanic'
	will match records that contain 'oceanic,' 'ocean' or 'oceans,' and records that contain the
	exact match 'oceanic' are returned first among results. When stemming is disabled, only those records 
	that contain the word 'oceanic' are returned.
	</p>
	
	<p>
	<logic:equal property="stemmingEnabled" name="daf" value="true">
			Stemming is <font color="green"><b>enabled</b></font>. 
			Click to <a href="admin.do?setStemmingEnabled=false"><b>disable stemming</b></a>
			(takes effect immediately - re-indexing not required). 
	 </logic:equal>	
	<logic:notEqual property="stemmingEnabled" name="daf" value="true">
			Stemming is <font color="red"><b>disabled</b></font>. 
			Click to <a href="admin.do?setStemmingEnabled=true"><b>enable stemming</b></a>
			(takes effect immediately - re-indexing not required).
	 </logic:notEqual>	
	 </p>

 --%>
	 
</body>
</html:html>

