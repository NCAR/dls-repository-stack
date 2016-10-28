<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<%@ page import="edu.ucar.dls.schemedit.display.*" %>
<%@ page import="java.util.*" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Application Configuration</c:set>
<html:html>

<head>
	<title><st:pageTitle title="${title}" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<%@ include file="/baseHTMLIncludes.jsp" %>
<style type="text/css">
.initParamValues {
	background-image:url("../images/file_folder2.gif");
	background-repeat:no-repeat;
	padding-left:18px; 
}
	
.paramName {
	font-weight:bold;
	color:#333366;
	font-style:italic;
}
	
#ccr-table {
	background-color:#666666;
	}

#ccr-table th {
	background-color:#CCCEE6;
	font-weight:bold;
	text-align:center;
}
#ccr-table td {
	background-color:#E3E4F1;
	text-align:center;
}
</style>
<script type="text/javascript">
function exportAll () {
	window.location="${contextPath}/admin/admin.do?command=exportAll";
}

function toggleReadOnly () {
	window.location="${contextPath}/admin/admin.do?command=toggleReadOnlyMode";
}

document.observe ("dom:loaded", function() {
	if ($("empty-registry-button")) {
		$("empty-registry-button").observe('click', function () {
			if (!confirm ('Do you REALLY want to unregister all standards documents?'))
				return;
				
			var params = {
					command : 'empty_registry'
				}
			window.location = "${contextPath}/admin/admin.do?" + $H(params).toQueryString();
			// alert( "${contextPath}/admin/admin.do?" + $H(params).toQueryString());			
		});
	}
	if ($("rebuild-ASN-cache-button")) {
		$("rebuild-ASN-cache-button").observe('click', function () {
			var msg = 'Rebuilding will take a few minutes but will not disrupt users as it completes.\n';
			msg += 'Click OK to proceed';
			if (!confirm (msg))
				return;
				
			var params = {
					command : 'rebuild_asn_cache'
				}
			window.location = "${contextPath}/admin/admin.do?" + $H(params).toQueryString();
			// alert( "${contextPath}/admin/admin.do?" + $H(params).toQueryString());			
		});
	}});

</script>

</head>

<body>

	<st:pageHeader toolLabel="${title}" currentTool="settings" />

	<st:pageMessages />

		<p>
		This is 
		<a href="${contextPath}/docs/CHANGES.txt" target="_blank"
			title="Read version release notes and change documentation">DCS version @VERSION@</a>.
		<c:if test="${not empty applicationScope.ddsStartUpDate}">
			The system was started on <fmt:formatDate value="${applicationScope.ddsStartUpDate}" type="both"/>.
		</c:if>
		</p>
	
	
<h3>Primary Configuration</h3>
<p>The main application configuration folder, containing configuration files for <i>collections</i>, 
<i>authorization</i>, and <i>metadata frameworks</i>, is located at:<br/>
	<span class="configPath">${initParam.dcsConfig}</span>.
To change, edit the context parameter <span class="paramName">dcsConfig</span> in server.xml or web.xml.
</p>

<h3>Metadata</h3>			

<p>The metadata files being indexed by this system are located at:<br/>
<%-- <img src="../images/file_folder2.gif" width="12" height="12"/> --%> 
	<span class="configPath">${initParam.collBaseDir}</span>.
To change, edit the context parameter <span class="paramName">collBaseDir</span> in server.xml or web.xml.
</p>	 

<P>
The base metadata export directory is located at:<br/>
	<span class="configPath">${initParam.exportBaseDir}</span>.
To change, edit the context parameter <span class="paramName">exportBaseDir</span> in server.xml or web.xml.
</P>

<c:choose>
	<c:when test="${initParam.autoExportStartTime == '0' || 
									initParam.autoExportStartTime == 'disabled'}">
		<p>Automatic metadata export is disabled. To enable it, 
		edit the context parameter <span class="paramName">autoExportStartTime</span> in server.xml or web.xml.</p>
	</c:when>
	<c:otherwise>
		<p>Automatic metadata export is scheduled to run each day at <b>${initParam.autoExportStartTime}</b></p>
	</c:otherwise>
</c:choose>

<p><input type="button" value="exportAll" onclick="exportAll()" /> Export all collections as configured (collections that are
not configured to export will not be affected).</p>


<c:if test="${initParam.ndrServiceEnabled}">
	<h3>NDR Service</h3>
	<p>ndrServiceEnabled is Enabled</p>
	<p>The configured NDR-API baseUrl is:<br/>
	<span class="configPath">${initParam.ndrApiBaseUrl}</span>.
	</p>
	
	<p>The NCS Agent handle is:<br/>
	<span class="configPath">${initParam.ncsAgentHandle}</span>.

	</p>
	<c:if test="${not empty initParam.ndrMasterCollection}" >
		<p>NDR Master Collection:<br/>
			<span class="configPath">${initParam.ndrMasterCollection}</span>.
		</p>
	</c:if>
	
	<c:if test="${not empty initParam.ndrMasterAgent}" >
		<p>NDR Master Agent: <br/>
			<span class="configPath">${initParam.ndrMasterAgent}</span>.
		</p>
	</c:if>
	
</c:if>

<h3>Standards Service</h3>
<c:choose>
	<c:when test="${not empty initParam.standardsServiceConfig}">
		<p>Standards Service Configuration File is located at:</br>
		<span class="configPath">${initParam.standardsServiceConfig}</span>.</p>
		
		<p>ASN Standards Library is located at:</br>
		<span class="configPath">${initParam.asnStandardsLibrary}</span>.</p>
			
		<p>To change these params, edit the context parameter
		<span class="paramName">standardsServiceConfig</span> or 
		<span class="paramName">asnStandardsLibrary</span> or 
		in server.xml or web.xml.
		</p>
		
		<c:if test="${not empty standardsRegistry}">
			<table>
				<tr valign="top">
					<td>
						<input style="margin:5px 15px 0px 0px;" type="button" value="Empty Standards Registry" 
						id="empty-registry-button" />
					</td>
					<td>
						<p>The Standards Registry should be periodically emptied, so that any changes 
						in the source ASN documents are loaded. Note: After emptying the registry, there will be a slight delay as each requested Standard
						must be retrieved from the ASN WebService</p>	
					</td>
				</tr>
				<tr valign="top">
					<td>
						<input style="margin:5px 15px 0px 0px;" type="button" value="Rebuild ASN Cache" 
						id="rebuild-ASN-cache-button" />
					</td>
					<td>
						<p>The NCS relies on a cache of ASN Authors and Standards Documents. This cache must be rebuilt to
						capture new Authors and Standards when they are made available by ASN. NOTE: Rebuilding the ASN Cache
						take a few minutes to complete.</p>	
					</td>				
				
				</tr>
			</table>
		</c:if>
		<c:if test="${empty standardsRegistry}"> SR NOT found </c:if>
		
	</c:when>
	<c:otherwise><p>Standards Service not configured</p></c:otherwise>
</c:choose>

<c:if test="${not empty repositoryService.collectionOfCollectionRecords}">
	<h3>Collections Of Collection Records</h3>
	
	<div style="margin:10px 0px 10px 10px">
	<table width="40%" border="0" cellpadding="6" cellspacing="1" id="ccr-table">
		<tr>
			<th>CollectionKey</th>
			<th>handleServiceBaseUrl</th>
		</tr>
		<c:forEach var="ccrEntry" items="${repositoryService.collectionOfCollectionRecords}">
		<tr>
			<td>${ccrEntry.key}</td>
			<td><a href="${ccrEntry.value}" target="_blank">${ccrEntry.value}</a></td>
		</tr>
		</c:forEach>
  </table>
	</div>

</c:if>


<c:if test="${not empty daf.virtualPageConfig}" >
<h3>Virtual Page Configuration</h3>

<% 
	Map virtualPageMap = new HashMap();
	VirtualPageConfig vpc = VirtualPageConfig.getInstance();
	if (vpc != null) {
		for (Iterator i=vpc.getKeys().iterator();i.hasNext();) {
			String xmlFormat = (String)i.next();
			VirtualPageList virtualPageList = vpc.getConfig(xmlFormat);
			virtualPageMap.put (xmlFormat, virtualPageList);
			}
	}
	pageContext.setAttribute ("vpc", vpc);
	pageContext.setAttribute ("vpcSource", vpc.getSourceURI());
	pageContext.setAttribute ("virtualPageMap", virtualPageMap);
%>

	<p>Virtual Page Configuration File is located at:</br>
		<span class="configPath">${vpcSource}</span>.</p>

<c:if test="${not empty initParam.primaryContentPathOnServer}">
<h3>Asset Manager Config</h3>
<p>The base directory for protected content is:<br />
	<span class="configPath">${initParam.primaryContentPathOnServer}</span><br/>
	To change, edit the context parameter <span class="paramName">primaryContentPathOnServer</span> 
	in server.xml or web.xml.
</p>
</c:if>
		
<%-- <h4>Configured Frameworks</h4>
		
<c:forEach var="vplEntry" items="${virtualPageMap}" >
	<c:set var="xmlFormat" value="${vplEntry.key}" />
	<c:set var="virtualPageList" value="${vplEntry.value}" />
	<div style="font-weight:bold">${xmlFormat}</div>
	<c:choose>
		<c:when test="${not empty virtualPageList.missingFields || not empty virtualPageList.extraFields}">
			<c:if test="${not empty virtualPageList.missingFields}">
				<div><h5 style="color:red;display:inline">Missing Fields</h5> - fields not accounted for in config.</div>
				<ul style="margin-top:0px;">
					<c:forEach var="field" items="${virtualPageList.missingFields}" >
						<li>${field}</li>
					</c:forEach>
				</ul>
			</c:if>
			<c:if test="${not empty virtualPageList.extraFields}">
			<div><h5 style="color:red;display:inline">Extra Fields</h5> - present in config but not at top-level of framework.</div>
				<ul style="margin-top:0px;">
					<c:forEach var="field" items="${virtualPageList.extraFields}" >
						<li>${field}</li>
					</c:forEach>
				</ul>
			</c:if>	
		</c:when>
		<c:otherwise>
			<p>Correctly Configured</p>
		</c:otherwise>
	</c:choose>
</c:forEach> --%>
</c:if>

<h3>Other configurations</h3>
<P>
The index and other repository data is located at:<br/>
	<span class="configPath">${initParam.repositoryData}</span>.
To change, edit the context parameter <span class="paramName">repositoryData</span> in server.xml or web.xml.
</P>	

<p><b>OAI Provider</b> is <b>${initParam.oaiPmhEnabled == 'true' ? 'enabled' : 'disabled'}</b>.
To change this setting, 
edit the context parameter <span class="paramName">oaiPmhEnabled</span> in server.xml or web.xml.</p>

<p>The MetadataUI loader file, which controls the display of controlled vocabularies in the Metadata Editor,
is located at:<br />
	<span class="configPath">${initParam.metadataGroupsLoaderFile}.</span><br/>
	To change, edit the context parameter <span class="paramName">metadataGroupsLoaderFile</span> 
	in server.xml or web.xml.
</p>	
		
<h3>Read Only Mode</h3>
<p>
<c:choose>
	<c:when test="${readOnlyMode}"><input type="button" value="Enable Writes" onclick="toggleReadOnly()"/> 
		This application is currently in Read Only mode.</c:when>
	<c:otherwise><input type="button" value="Enable Read Only" onclick="toggleReadOnly()" />
		Disable write operations for this application.</c:otherwise>
</c:choose>
</p>

</body>
</html:html>

