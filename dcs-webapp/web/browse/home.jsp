<%-- $Id: home.jsp,v 1.20 2010/08/31 22:59:35 ostwald Exp $ --%>
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>

<html:html>

<head>
<title><st:pageTitle title="Home" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<%@ include file="/baseHTMLIncludes.jsp" %>
<%@ include file="/lib/dcstable/dcsTableIncludes.jsp" %>

<script type="text/javascript">

var dcsTable = null;

var DcsTableConfig = {
	// Id of the DOM element in which the DcsTable will live
	parentId : 'collections-table-wrapper',
	imagePath : '${contextPath}/images',
	baseUrl : "${contextPath}/browse/home.do",
	
	// use defaultPath when none is specified
	defaultPath : '${contextPath}/browse/home.do',


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
			path : '${contextPath}/browse/query.do',
			title: 'view items'
		},
		
		CREATE_RECORD: {
			text : "Create<br/>Record",
			valueFunction : function (row) {
				return "Create";
			},
			clickHandler : createRecordHandler,
			title: 'create a new record'
		},
		
		FORMAT: {
			sortable: 1,
			text : "Metadata Format",
			valueFunction : function (row) {
				return row.getProp ("format");
			}
		},
		
		NUM_RECORDS: {
			sortable: 1,
			text : "Num<br/>Records",
			valueFunction : function (row) {
				return Number (row.getProp ("numFiles"));
			},
			clickHandler : numRecordsHandler,
			title : 'view all records in this collection'
		},
		
		NUM_VALID: {
			sortable: 1,
			text : "Num<br/>Valid",
			valueFunction : function (row) {
				return Number (row.getProp ("numValid"));
			},
			queryFunction: function (row) {
				return {
					s:"0", 
					q:"", 
					scs:"0"+ row.getProp("setSpec"),
					vld: "valid"
				};
			},
			path : '${contextPath}/browse/query.do',
			title : 'view valid records in this collection'
		},
		
		NUM_NOT_VALID: {
			sortable: 1,
			text : "Num Not<br/>Valid",
			valueFunction : function (row) {
				return Number (row.getProp ("numNotValid"));
			},
			queryFunction: function (row) {
				return {
					s:"0", 
					q:"", 
					scs:"0"+ row.getProp("setSpec"),
					vld: "notvalid"
				};
			},
			path : '${contextPath}/browse/query.do',
			title : 'view non-valid records in this collection'
		},
	
		ROLE: {
			text : "Role",
			valueFunction : function (row) {
				return row.getProp ("userRole");
			}
		}
	},
	// row config goes here
	rowProperties : null,
	
	rowPropertiesInit : function () {
		var propertiesHash = {}
		var myRowKey;
		var myRowProperties;
		
		<c:forEach var="set" items="${browseForm.sets}" varStatus="i">
			// load up dict with everything the cells for this row will need.
			myRowKey = '${set.setSpec}';
			myRowProperties = {
				setSpec : '${set.setSpec}',
				name : '<c:out value="${set.name}" escapeXml="true" />',
				format: '${set.format}',
				numFiles: '${set.numFiles}',
				numNotValid: '${set.numNotValid}',
				numValid: '${set.numValid}',
				userRole : '${browseForm.userRoles[set.setSpec]}',
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
	
	var columns = ['COLLECTION_NAME', 'FORMAT'];
	var filterSpec = {
		"column":'${sessionBean.collectionFilter.label}', 
		'value':'${sessionBean.collectionFilter.value}'
	};
	// filterWidget is defined in TableFilter
	filterWidget = new TableFilter ('table-filter', dcsTable, columns, filterSpec);
	
	try {
		$('filter-input').focus();
	} catch (error) {}
		
	
	return;
}

function numRecordsHandler (event) {
	log ("numRecordsHandler");
	var path = '${contextPath}/browse/query.do';
	var params = {
				s:"0", 
				q:"", 
				scs:"0"+ this.row.getProp("setSpec")
			};
	window.location = createUrl (path, params);
}

/* an event handler for the "CREATE" item cells. Handled by the cell, referencing
properties held in the cellHdr record;
*/
function createRecordHandler (event) {
	var setSpec = this.row.getProp ("setSpec");
	var format = this.row.getProp ("format");
	var path;
	var params;
	log ("doCreateRecord: " + setSpec + " (" + format + ")");
	if (format == 'adn') {
		path = '${contextPath}/record_op/adn.do'
		params = {
			command : 'new',
			collection : setSpec
		}
	}
	else if (format == 'mast') {
		path = "${contextPath}/record_op/mast.do";
		params = {
			command: 'new',
			collection : setSpec
		}
	}
	else {
		path = "${contextPath}/record_op/single.do";
		params = {
			command : 'newRecord',
			collection : setSpec,
			xmlFormat : format
		}
	}
/* 	url = path;
	if (params)
		url += "?" + $H(params).toQueryString(); 
	window.location = url; */
	window.location = createUrl (path, params);
}
	
document.observe ("dom:loaded", dcsTableInit);

</script>

</head>

<body>
<%@ include file="/locked_record_status.jspf" %>

<st:pageHeader currentTool="home" toolLabel="Home" />

<table width="100%" border="0" align="center">
  <tr> 
    <td> 
  
	<c:set var="okay_link" value="${contextPath}/browse/home.do" />
	<logic:messagesPresent>
	<%-- ####### Display messages, if present ####### --%>

	<table width="100%" bgcolor="#000000" cellspacing="1" cellpadding="8">
	  <tr bgcolor="ffffff"> 
		<td>
			<b>Messages:</b>
			<ul>
				<html:messages id="msg" property="message"> 
					<li><bean:write name="msg"/></li>									
				</html:messages>
				<html:messages id="msg" property="error"> 
					<li><font color=red>Error: <bean:write name="msg"/></font></li>									
				</html:messages>
				<logic:messagesPresent property="indexErrors">
					<c:set var="okay_link" value="${contextPath}/manage/collections.do" />
					<p><font color=red><b>ALERT: The following collections must be reindexed before they can be accessed:</b></font></p>
					<ul>
						<html:messages id="msg" property="indexErrors">
							<li><font color=red><bean:write name="msg"/></font></li>									
						</html:messages>
					</ul>
				<p><font color=red>To reindex, go to the "Manage -> Manage Collections" page by clicking the "OK" link below. If
				you do not have sufficent privileges to access the page, please alert the system administrator.</font></p>
				</logic:messagesPresent>
				
			</ul>
			<blockquote>[ <a href="${okay_link}">OK</a> ]</blockquote>
		</td>
	  </tr>
	</table><br><br>
	</logic:messagesPresent>
	
	<st:framework-registry-warnings 
		baseUrl="${contextPath}/browse/home.do" />
	
	<st:collectionFilterTableWithContents>
		<p style="margin:3px 0px 3px 0px">Access a collection or create records. 
					To access a collection, click on the collection name. 
					To create a new record, click on <i>create</i> for the appropriate collection.
		</p>
	</st:collectionFilterTableWithContents>
	
	<%--  COLLECTIONS TABLE  --%>
	<div id="collections-table-wrapper"></div>	

    </td>
  </tr>
</table>

</body>
</html:html>

