<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Collection Settings</c:set>
<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<%@ include file="/admin/adminHTMLIncludes.jsp" %>
<script type="text/javascript">

var DcsTableConfig = {

	// Id of the DOM element in which the DcsTable will live
	parentId : 'collections-setting-table-wrapper',
	imagePath : '${contextPath}/images',
	baseUrl : "${contextPath}/manage/collections.do",
	
	// use defaultPath when none is specified
	defaultPath : '${contextPath}/manage/collections.do',
	columnProperties : {
		COLLECTION_NAME: {
			sortable: 1,
			text : "Collection Name",
			valueFunction : function (row) {
				return row.getProp ("name");
			},
			queryFunction : function (row) {
				return {
					collection:row.getProp("setSpec")
				};
			},
			path : "${contextPath}/browse/view.do",
			title : "View collection configuration"
		},
		
		COLLECTION_KEY: {
			sortable: 1,
			text : "Key",
			valueFunction : function (row) {
				return row.getProp ("setSpec");
			}
		},
		
		FORMAT: {
			sortable: 1,
			text : "Format",
			valueFunction : function (row) {
				return row.getProp ("format");
			}
		},
		
		EDIT: {
			text : "Edit<br/>Settings",
			valueFunction : function (row) {
				return "Edit";
			},
			queryFunction : function (row) {
				return {
					src : 'local',
					collection:row.getProp("setSpec"),
					command: 'edit',
					recId : row.getProp ("setSpec")
				};
			},
			path : "${contextPath}/editor/collection_config.do",
			title : "edit configuration"
		},
				
		PREFIX: {
			sortable: 1,
			text : "Prefix",
			valueFunction : function (row) {
				return row.getProp ("idPrefix");
			}
		},
		
		COLLECTON_RECORD: {
			text : "Collection Record",
			valueFunction : function (row) {
				return "Edit";
			},
			queryFunction : function (row) {
				return "Role";
			},
			queryFunction : function (row) {
				return {
					recId : row.getProp ("id"),
					command: 'edit',
					xml_format : 'dlese_collect'
				};
			},
			path : "${contextPath}/editor/edit.do"
		}
	},
	// row config goes here
	rowProperties : null,
	
	rowPropertiesInit : function () {
		var propertiesHash = {};
		var myRowKey;
		var myRowProperties;
		
		<c:forEach var="set" items="${daf.sets}" varStatus="i">
			// load up dict with everything the cells for this row will need.
			myRowKey = '${set.setSpec}';
			myRowProperties = {
				setSpec : '${set.setSpec}',
				// name : '${set.name}',
				name : '<c:out value="${set.name}" escapeXml="true" />',
				format: '${set.format}',
				idPrefix: '${set.idPrefix}',
				id: '${set.id}',
				authority : '${set.authority}'
			} 
			propertiesHash[myRowKey] = myRowProperties;
		
		</c:forEach>
		DcsTableConfig.rowProperties = propertiesHash;
		
	} // end of rowPropertiesInit
	

}  // end of DcsTableConfig			

function dcsTableInit () {
	DcsTableConfig.rowPropertiesInit();
	dcsTable = new DcsCollectionsTable (DcsTableConfig);
	
	var columns = ['COLLECTION_NAME', 'FORMAT', 'PREFIX', 'COLLECTION_KEY'];
	var filterSpec = {
		"column":'${sessionBean.collectionFilter.label}', 
		'value':'${sessionBean.collectionFilter.value}'
	};
	filterWidget = new TableFilter ('table-filter', dcsTable, columns, filterSpec);
}

document.observe ("dom:loaded", dcsTableInit);
</script>

</head>

<body text="#000000" bgcolor="#ffffff">
<st:pageHeader currentTool="settings" toolLabel="${title}" />

<table width="100%" border="0" align="center">
  <tr>
    <td> 
		<st:pageMessages okPath="${contextPath}/admin/admin.do?page=collections" />
		 
		<st:collectionFilterTableWithContents>
			<p style="margin:3px 0px 3px 0px">The table below lists all collections. Use the provided 
					buttons to view or edit the settings for a particular collection.
			</p>
		</st:collectionFilterTableWithContents>
	
	<%--  SETTINGS TABLE  --%>
	<div id="collections-setting-table-wrapper"></div>	
	
	<br />
	
	</table><br />
 
	  <br />
    </td>
  </tr>
</table>


</body>
</html:html>

