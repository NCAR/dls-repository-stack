<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Manage Collections</c:set>
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>

<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<%@ include file="/manage/manageHTMLIncludes.jsp" %>

<script>

// alert ("in manage collections");

var DcsTableConfig = {
	// Id of the DOM element in which the DcsTable will live
	parentId : 'collections-table-wrapper',
	imagePath : '${contextPath}/images',
	baseUrl : "${contextPath}/manage/collections.do",
	
	// use defaultPath when none is specified
	defaultPath : '${contextPath}/manage/collections.do',

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
			// href='${contextPath}/browse/query.do?s=0&q=&scs=0${set.setSpec}
			queryFunction : function (row) {
				return {
					s:"0", 
					q:"", 
					scs:"0"+ row.getProp("setSpec")
				};
			},
			path : '${contextPath}/browse/query.do'
		},
		
		FORMAT: {
			sortable: 1,
			text : "Format",
			valueFunction : function (row) {
				return row.getProp ("format");
			}
		},
		
		NUM_FILES: {
			sortable: 1,
			text : "Num<br/>Records",
			valueFunction : function (row) {
				return Number (row.getProp ("numFiles"));
			},
			queryFunction: function (row) {
				return {
					s:"0", 
					q:"", 
					scs:"0"+ row.getProp("setSpec")
				};
			},
			path : '${contextPath}/browse/query.do'
		},
		
		NUM_INVALID_AND_FINAL_STATUS: {
			sortable: 1,
			text : "Final Status<br>and Invalid",
			valueFunction : function (row) {
				return Number (row.getProp ("numFinalAndNotValid"));
			},
			queryFunction: function (row) {
				return {
					s:"0", 
					q:"", 
					scs:"0"+ row.getProp("setSpec"),
					vld: "notvalid",
					sss: row.getProp ("finalStatusLabel")
				};
			},
			path : '${contextPath}/browse/query.do'
		},
		
		DELETE: {
			sortable: 0,
			text : "Delete<br/>Collection",
			valueFunction : function (row) {
				return "Delete";
			},
			clickHandler: handleDelete
		},
	
		INDEX: {
			sortable: 0,
			text : "Index<br/>Records",
			valueFunction : function (row) {
				return "Index";
			},
			queryFunction : function (row) {
				return {
					command : 'reindexCollection',
					collection : row.getProp("setSpec")
				}
			}
			// no path was provided, so we use defaultPath
		},
		
		VALIDATE: {
			sortable: 0,
			text : "Validate<br/>Records",
			valueFunction : function (row) {
				return "Validate";
			},
			clickHandler : handleValidate
		},
		
		EXPORT: {
			sortable: 0,
			text : "Export<br/>Records",
			valueFunction : function (row) {
				return "Export";
			},
			queryFunction : function (row) {
				return {
					command : 'export',
					setup : 'true',
					collection : row.getProp('setSpec')
				}
			},
			path : "${contextPath}/manage/collections.do"
		},
		
		WEBSERVICE: {
			sortable: 0,
			text : "Services",
			valueFunction : function (row) {
			// create a check box element for this row. for some reason, IE6 doesn't understand
			// when i create an actual element, so here i just create a string representation ...
				var value = row.getProp ('enabled') == 'true' ? 'disable' : 'enable';
				var element = "<input type=\"checkbox\" value=\"" + value + "\"";
				if (row.getProp ('enabled') == 'true') {
					element += " CHECKED";
				}
				element += " />";
				return element;
			}, 
			clickHandler: handleWebservice
		}
	},   // end of column properties
	
	rowProperties : null,
	
	rowPropertiesInit : function () {
		var propertiesHash = {}
		var myRowKey;
		var myRowProperties;
		
		// loop through sets (representing collections)
		<c:forEach var="set" items="${csForm.sets}" varStatus="i">
		
			myRowKey = '${set.setSpec}';
			
			// load up dict with all the properties the item cells for this row will need.
			myRowProperties = {
				setSpec : '${set.setSpec}',
				name : '<c:out value="${set.name}" escapeXml="true" />',
				format: '${set.format}',
				numFiles: '${set.numFiles}',
				uniqueID : '${set.uniqueID}',
				enabled : '${set.enabled}',
				numFinalAndNotValid : '${set.numFinalAndNotValid}',
				finalStatusLabel : '${set.finalStatusFlag.label}',
				authority : '${set.authority}'
			}
			propertiesHash[myRowKey] = myRowProperties;
		
		</c:forEach>
		DcsTableConfig.rowProperties = propertiesHash;
	} // end of rowPropertiesInit
}  // end of DcsTableConfig


function dcsTableInit () {

	DcsTableConfig.rowPropertiesInit();
	
	// test rowprops
	if (0) {
		log ("row config")
		log ("DcsTableConfig.rowProperties: " + typeof DcsTableConfig.rowProperties);
		$H(DcsTableConfig.rowProperties).each ( function (pair) {
			log ("\t" + pair.key);
		});
		return;	
	}
	
	// test colProps
	if (0) {
		log ("col props");
		$H(DcsTableConfig.columnProperties).each (function (pair) {
			log ("\t" + pair.key);
		});
		log (" --> " + $H(DcsTableConfig.columnProperties).keys()[0]);
	}
	
	dcsTable = new DcsCollectionsTable (DcsTableConfig);
	var columns = ['COLLECTION_NAME', 'FORMAT'];
	var filterSpec = {
		"column":'${sessionBean.collectionFilter.label}', 
		'value':'${sessionBean.collectionFilter.value}'
	};
	filterWidget = new TableFilter ('table-filter', dcsTable, columns, filterSpec);
	return;
}

/*
	clickHandler definitions for handlers named in column configuration
*/

function handleDelete (evnt) {
	// var collection = evnt.element().id.sub ("delete_", "");
	var collection = this.row.key;
	evnt.stop();
	doModalAction (evnt, this.row, function () {
		if (confirm('Really delete this collection?')){
			var params = {
				command:"deleteCollection",
				collection:collection
			}
			var url = DcsTableConfig.baseUrl + "?" + $H(params).toQueryString();
			// alert ('would have deleted ' + url);
			window.location=url;
		}
	});
}

function handleWebservice (evnt) {

	var modal = false;

	var checkbox = this.element.down ("input")
	var setUid = this.row.getProp ("uniqueID")
	
	// change the checkbox so the user sees an effect
	checkbox.checked = (!checkbox.checked);
	
	var action = checkbox.value; // "disable" : "enable";
	var confirmMsg = "Are you sure you want to " + action + " webservice access to this collection?";
	var params = $H({setUid : setUid})
	params.set(action+"Set", "t");

	var url = DcsTableConfig.baseUrl + "?" + params.toQueryString();
	
	if (modal)
		doModalAction (evnt, this.row, function () {
			if (confirm (confirmMsg)) {
				window.location = url;
			}
			else {
				checkbox.checked = (!checkbox.checked);
			}
			Event.stop(evnt);
		});
	else {
		window.location = url;
	}
}

function handleValidate (evnt) {
/*
	if we implment a choice where the user can validate all (w/o using cached validation,
	then we would warn/confirm before calling validate. But for now we don't bother.
*/
	var modal = false;
	/* var setSpec = Event.element(evnt).id.sub ("validate_", ""); */
	var setSpec = this.row.getProp ("setSpec");
	var params = {
		command:"validate",
		setup:"true",
		collection:setSpec
	}
	var url = DcsTableConfig.baseUrl + "?" + $H(params).toQueryString();
	
	if (modal) {
		doModalAction (evnt, this.row, function () {
			if (confirm('"Validation may take several minutes for large collections. Continue?"')){
					window.location=url;
			}
			Event.stop (evnt);
		});
	}
	else {
		window.location = url;
	}
}

document.observe ("dom:loaded", dcsTableInit);

</script>
</head>

<body text="#000000" bgcolor="#ffffff">

<st:pageHeader currentTool="manage" toolLabel="${title}" />

<%@ include file="indexing_status.jsp" %>

<%-- MANAGE COLLECTION BUTTONS --%>
<st:collectionFilterTableWithContents>	
	<p>Manage the collections for which you have 
		<span class="doc-em">Manager</span> privilege.
	</p>
	<table width="100%">
		<tr>
			<td>
				<input type="button" value="Create new collection" 
					onclick="window.location='${contextPath}/manage/collections.do?command=new'"/>
			</td>
			
			<%-- INDEXING STATUS --%>
			<td align="right">
				<input type="button"
					title="get information about the progress of the indexing process"
					value="Check most recent indexing status"
					onclick="window.location='${contextPath}/manage/collections.do?command=showIndexingMessages'" />
			
			<logic:greaterThan name="csForm" property="numIndexingErrors" value="0">
				<br/>
					<b>Warning:</b> Some records had errors during indexing. <br/>See
				<a href="${contextPath}/admin/report.do?q=error:true&s=0&report=Files+that+could+not+be+indexed+due+to+errors">Files that 
				could not be indexed due to errors</a>.
			</logic:greaterThan> 
			
<%-- 						Warning: some files	could not be indexed due to errors</a>.
				<input type="button" class="smallButton warning"
					title="Files that could not be indexed due to errors"
					value="See Indexing Errors"
					onclick="window.location='${contextPath}/admin/report.do?q=error:true&s=0&report=Files+that+could+not+be+indexed+due+to+errors';" /> --%>
			</td>
		</tr>
	</table>
</st:collectionFilterTableWithContents>	

<%--  COLLECTIONS TABLE  --%>
<div id="collections-table-wrapper"></div>

	

</body>
</html:html>

