<%-- $Id: manage_ndr_collections.jsp,v 1.17 2010/11/29 20:20:18 ostwald Exp $ --%>
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">NDR Admin</c:set>
<c:set var="baseNDRGetUrl" value="${ndrForm.ndrApiBaseUrl}/get/" />
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>

<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<%@ include file="/manage/manageHTMLIncludes.jsp" %>
	<link rel="stylesheet" href="includes/json-styles.css" type="text/css">
	<link rel="stylesheet" href="includes/ndr-browser-styles.css" type="text/css">
	
<style type="text/css">
.list-members-link {
	font-weight:bold;
}
.list-members-div {
	margin-top:5px;
}
</style>
	
	<script type="text/javascript">
		<%@ include file="includes/NDRGlobals.jsp" %>
	</script>
	<script type="text/javascript" src="includes/JsonViewer.js"></script>
	<script type="text/javascript" src="includes/NDRObject.js"></script>
	<script type="text/javascript" src="includes/NDRListMembersObject.js"></script>
	<script type="text/javascript" src="includes/NDRBrowser.js"></script>
	<script type="text/javascript" src="${contextPath}/lib/progress-indicator/progress-script.js" ></script>
	
<%@ include file="/lib/dcstable/dcsTableIncludes.jsp" %>

<script type="text/javascript">

var DcsTableConfig = {
	// Id of the DOM element in which the DcsTable will live
	parentId : 'collections-table-wrapper',
	imagePath : '${contextPath}/images',
	
	// use defaultPath when none is specified
	defaultPath : '${contextPath}/ndr/ndr.do',


/*
	params for a headerCell to support search, etc
		KEY, Text, param
	- Note: these values could also come from the Form bean (like we use setInfo)
*/
	columnProperties : {
		COLLECTION_NAME: {
			sortable: 1,
			text : "Collection Name",
			valueFunction : function (row) {
				return row.getProp ("name");
			},
			clickHandler : browseCollection,
			title: 'Access NDR Objects for this collection'
		},
		
		FORMAT: {
			sortable: 1,
			text : "Format",
			valueFunction : function (row) {
				return row.getProp ("format");
			}
		},
		
		COLLECTION_KEY: {
			sortable: 1,
			text : "Collection Key",
			valueFunction : function (row) {
				return row.getProp ("setSpec");
			}
		},
		
		INDEXED_RECORDS: {
			sortable: 1,
			text : "Indexed<br/>Records",
			valueFunction : function (row) {
				return Number (row.getProp ("numIndexed"));
			},
			queryFunction : function (row) {
				return {
					s:"0", 
					q:"", 
					scs:"0"+ row.getProp("setSpec")
				};
			},
			path : '${contextPath}/browse/query.do',
			title: 'Browse all records in this collection'
		},
/* 
		<%--
			async call doesn't fit with dcsTable paradigm, but more importantly,
			it is expensive and of little value to have ndr record count here ... 
		--%>
		NDR_RECORDS: {
			sortable: 1,
			text : "Records<br/>in NDR",
			valueFunction : function (row) {
				return Number (row.getProp ("numNDRRecs"));
			}
		}, */
		
		SYNC_ERRORS: {
			sortable: 1,
			text : "Sync<br/>Errors",
			valueFunction : function (row) {
				return Number (row.getProp ("numSyncErrors"));
			},
			queryFunction : function (row) {
				return {
					s:"0", 
					q:"", 
					scs:"0"+ row.getProp("setSpec"),
					syncErrors:'true'
				};
			},
			path : '${contextPath}/browse/query.do',
			title: 'Browse items with sync errors'
		},
	
		SYNC_COLLECTION: {
			text : "Sync to NDR",
			valueFunction : function (row) {
				return "Sync";
			},
			clickHandler : doSync,
			title : "Syncronize metadata in NDR with local metadata"
		},
		
		UNREGISTER_COLLECTION: {
			text : "Unregister<br/>Collection",
			valueFunction : function (row) {
				return "Unregister";
			},
			clickHandler : handleUnregister, 
			title : "Unregister collection from the NDR"
		}
	},  // end of column Props
	
	// row config goes here
	rowProperties : null,
	
	rowPropertiesInit : function () {
		var propertiesHash = {}
		var myRowKey;
		var myRowProperties;
		
		<c:forEach var="set" items="${ndrForm.ndrCollections}">
			// load up dict with everything the cells for this row will need.
			myRowKey = '${set.setSpec}';
			myRowProperties = {
				setSpec : '${set.setSpec}',
				name : '${set.name}',
				format: '${set.format}',
				numIndexed: '${set.numIndexed}',
				numNDRRecs: '??',
				numSyncErrors: '${set.numSyncErrors}'
			} 
			propertiesHash[myRowKey] = myRowProperties;
		
		</c:forEach>
		DcsTableConfig.rowProperties = propertiesHash;
	} // end of rowPropertiesInit
}  // end of DcsTableConfig			
			
			
function dcsTableInit () {
	// jlog ("dcsTableInit())");
	DcsTableConfig.rowPropertiesInit();
		dcsTable = new DcsTable (DcsTableConfig);
	return;
}


// handleMap associates a setSpec with the corresponding mdpHandle
var handleMap = {
	<c:forEach var="pair" items="${ndrForm.mdpHandleMap}" varStatus="i">
			"${pair.key}" : "${pair.value}" <c:if test="${not i.last}">,</c:if>
	</c:forEach>}
	
function pageInit () {

	if ('${ndrServiceActive}' != 'true') return;

	if ($('import-button')) {
		$('import-button').observe ('click', doImport);
	}
		
	if ($("register-select"))
		$("register-select").observe ('change', doRegister);
		
		if ("${ndrForm.isSyncing}" == "true") {
				var finalize = function () {
					if (confirm ("Sync is complete. Would you like to see report?"))
						window.location = "ndr.do?command=syncreport"
					else
						window.location = "ndr.do?command=manage"
				}
	
			var progressUrl = "ndr.do?command=syncprogress";
			
			new PeriodicalExecuter(function(pe) {
				updateProgress (pe, progressUrl, finalize);
			}, 3); 
		}
		
}

/* retrieve the count of records for specified collection and update collections table */
function doUpdateNdrCount (counter) {
	var e = $(counter);
	var collection = e.id.substr ("ndrcount-".length);
	
	new Ajax.Request ("ndr.do", {
		parameters: {
			command:"ndrRecordCount", 
			collection:collection
		},
		onSuccess: function (transport) {
		  var countStr = transport.responseText;
			if (isNaN (parseInt (countStr))) {
				/* hide the row for collections that are not available (probably 
				   because they they are not stored in the ndr we're looking at */
				// alert (collection + " is not available");
				var row = e.up ('tr').hide();
				var collName = row.down ('td').down('a').innerHTML;
				var msg = $(document.createElement ("li"));
				var msgbody = collName + " (" + collection + ") is not available and has been hidden";
				msg.update (msgbody);
				$('collection-info').appendChild (msg);
			}
			else {
				e.update (countStr);
			}
		}
	}); 
}

/*
	handles click on a collectionLink (linktext is collection name) in the "NDR Collections" table.
*/
function browseCollection (evnt) {
	
	var collName = this.row.getProp ("name");
	var setSpec = this.row.getProp ("setSpec");
	this.row.table.highlightRow (setSpec);
	
	var target = $("collection-info");
	var handle = handleMap[setSpec];
	target.update (new Element ("h3").update(collName));
	
	/* ndrLink ("metadataProvider") 
		handler displays metadataProvider object
	*/
	var mdpDiv = new Element ("div");
	mdpDiv.addClassName ("list-members-div");
	
	var mdpLink = new Element ("a", {
		title : "click to access the MetadataProvider Object",
		href : NDRGlobals.ndrApiBaseUrl + "/get/" + handle
	}).update ("Metadata Provider")
		.addClassName ("list-members-link")
		.addClassName ("ndr-link")
		.observe ('click', ndrclick);
		
	mdpDiv.appendChild (mdpLink);
	target.appendChild (mdpDiv);
	
	/* membersLink ("metadata records") 
		handler displays links to metdata records for this collection
	*/
	var membersDiv = new Element ("div").addClassName ("list-members-div");
	
 	var membersLink = new Element ("a", {
			title : "click to access the Metadata Objects for this collection",
			href : NDRGlobals.ndrApiBaseUrl + "/listMembers/" + handle
	}).update ("Metadata Records")
	  .addClassName ("list-members-link")
	  .observe ('click', listMembersLink);
		
	membersDiv.appendChild (membersLink);
	target.appendChild (membersDiv);
	
	target.scrollTo();
	
}


// handles the "register" command
function doRegister (evnt) {
	var collection = $F("register-select");
	if (collection == "")
			alert ("Please select a collection");
	else {
			var confirmStr = "Are you sure you want to register this collection with the NDR?\n\n" +
											 "This will write all item- and collection-level information about the\n" +
											 "selected collection to the NDR";
			if (confirm (confirmStr)) {
				var params = {
	
					command:"sync",
					pid:collection
				}
				url = "${contextPath}/ndr/ndr.do?" + $H(params).toQueryString();
				window.location = url;
				// alert (url);
		}
		else {
			$("register-select").options[0].selected = true;
		}
	}
}


function doSync (evnt) {
	var setSpec = this.row.getProp ("setSpec");
	doModalAction (evnt, this.row, function () {
		if (confirm('Really sync this collection?')){
			var baseUrl = "${contextPath}/ndr/ndr.do"
			var params = {
				command:"sync",
				pid:setSpec
			}
			var url = baseUrl + "?" + $H(params).toQueryString();
			// alert (url);
			window.location=url;
		}
	});
}

function handleUnregister (evnt) {
	// var collection = Event.element(evnt).id.substr ("unregister_".length);
	var collection = this.row.getProp ("setSpec");
	doModalAction (evnt, this.row, function () {
			doUnregisterCollection (collection);
	});
}

function doUnregisterCollection (collection) {

			var confirmStr = "Are you sure you want to register this collection with the NDR?\n\n" +
											 "This will remove all item- and collection-level information about the\n" +
											 "selected collection from the NDR";
			if (confirm (confirmStr)) {
			var params = {
				collection : collection,
				command  : "unregister"
				}
			url = "${contextPath}/ndr/ndr.do?" + $H(params).toQueryString();
			window.location = url;
			// alert (url);
		}
}

/* currently NOT CALLED */
function doImport (evnt) {
	Event.stop(evnt);
	var handle = $F('import-mdp');
	if (handle == "-- MetadaProvider Handle --") {
		alert ("please enter a mdpHandle");
		return;
	}
	if (confirm('Really import this collection?')){
		var baseUrl = "${contextPath}/ndr/ndr.do"
		var params = {
			command:"import",
			mdpHandle:handle
		}
		var url = baseUrl + "?" + $H(params).toQueryString();
		window.location=url;
	}
}

document.observe ("dom:loaded", dcsTableInit);
Event.observe (window, 'load', pageInit, false);

</script>
</head>

<body text="#000000" bgcolor="#ffffff">

<st:pageHeader currentTool="ndr" toolLabel="${title}" />

<table width="100%" cellpadding="0" cellspacing="0">
	<tr valign="top">
		<td align="left"><p style="margin-top:5px;">Administer collections currently Registered to write to the NDR</p></td>
		<td align="right"><%@ include file="ndr_context.jspf" %></td>
		<%-- <c:if test="${ndrForm.ndrServer != 'ndr.nsdl.org'}"> (<b>${ndrForm.ndrServer}</b>)</c:if>. --%>
	</tr> 
</table>

<st:pageMessages okPath="${contextPath}/ndr/ndr.do" />

<c:choose>
	<c:when test="${not ndrServiceActive}">
		<h3>NDR Service is not active!</h3>
		<p>Interaction with the NDR is stopped until NDR Service is activated.</p>
		<div><input type="button" value="Activate NDR Service"
								onclick="window.location='${contextPath}/ndr/ndr.do?activate=true'"/></div>
	</c:when>
<c:otherwise>
<div align="right"><input type="button" value="Deactivate NDR Service"
						onclick="window.location='${contextPath}/ndr/ndr.do?deactivate=true'"/></div>
						
						
<c:choose>
	<c:when test="${ndrForm.isSyncing}">
				<h3>Sync in progress</h3>
				
				<%@ include file="/lib/progress-indicator/progress-widget.jsp" %>
				
	</c:when>
	<c:otherwise>
	
		<h3>Register a collection with the NDR</h3>
		
		<table cellpadding="5px">
			<tr>
				<td>
					<select name="register-select" id="register-select">
						<option value="">-- Select a collection --</option>
						<c:forEach var="set" items="${ndrForm.dcsCollections}">
							<option value="${set.setSpec}">${set.name}</option>	
						</c:forEach>
					</select>
				</td>
				<td>
					<p>The selected collection will be written to the NDR and subsequent actions, such as
					metadata creation and edits, as well as collection configuration changes, will be reflected
					in the NDR.
					Collections that are registered with the NDR are displayed in the <span class="doc-em">NDR Collections</span>
					table below.
					</p>
				</td>
			</tr>
		</table>
	
<%-- 
		<!-- IMPORT COLLECTION disabled 12/05/07 -->
		<h3>Import a collection from the NDR</h3>
		
		<table cellpadding="5px">
			<tr>
				<td>
					<input type="text" name="import-mdp" id="import-mdp" size="30" value="-- MetadaProvider Handle --"/>
					<input type="button" id="import-button" value="import" />
				</td>
				<td>
					<p>Imports collection for the given mdpHandle
					</p>
				</td>
			</tr>
		</table> 
 --%>
 
	</c:otherwise>
</c:choose>


<%--  NDR COLLECTIONS TABLE  --%>
<div style="margin-top:10px;">
<h3>NDR Collections</h3>
<p>Click on a <span class="doc-em">Collection Name</span> to access it's NDR Objects.<br />
	Click on <span class="doc-em">Sync to NDR</span> to synchronize the records in 
	the NDR with the records stored locally.<br />
	To remove a collection from the NDR, click <span class="doc-em">Unregister Collection</span>.
</p>

<%--  NDR COLLECTIONS TABLE  --%>
<div id="collections-table-wrapper"></div>
	
</div>

<hr />

<div id="collection-info" style="margin-top:10px;"></div>

</c:otherwise>
</c:choose>

</body>
</html:html>

