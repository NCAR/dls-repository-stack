<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Metadata Frameworks</c:set>
<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<%@ include file="/admin/adminHTMLIncludes.jsp" %> 
<script type="text/javascript">

var DcsTableConfig = {
	// Id of the DOM element in which the DcsTable will live
	parentId : 'frameworks-setting-table-wrapper',
	imagePath : '${contextPath}/images',
	baseUrl : "${contextPath}/manage/frameworks.do",
	
	// use defaultPath when none is specified
	defaultPath : '${contextPath}/manage/frameworks.do',
	columnProperties : {
		FRAMEWORK_NAME: {
			sortable: 1,
			text : "Framework",
			valueFunction : function (row) {
				return row.getProp ("xmlFormat");
			},
			queryFunction : function (row) {
				return {
					format : row.getProp("xmlFormat")
				};
			},
			path : "${contextPath}/browse/view.do",
			title : "View framework configuration"
		},
		
		VERSION: {
			text : "Version",
			valueFunction : function (row) {
				return row.getProp ("version");
			}
		},
				
		EDIT: {
			text : "Edit Settings",
			valueFunction : function (row) {
				return "Edit";
			},
			path : "${contextPath}/editor/framework_config.do",
			queryFunction : function (row) {
				return {
					recId : row.getProp ('xmlFormat'),
					command : 'edit',
					src : 'local'
				}
			},
			title : "edit framework configuration"
		},
		
		RELOAD: {
			text : "Reload",
			valueFunction : function (row) {
				return "Reload";
			},
			queryFunction : function (row) {
				return {
					xmlFormat : row.getProp ("xmlFormat"),
					command : 'loadFramework'
				};
			},
			path : "${contextPath}/admin/frameworks.do",
			title : "reload this framework"
		}/* ,
		// Unload is not functional - disabling 11/30/09
		UNLOAD: {
			text : "Unload",
			valueFunction : function (row) {
				return "Unload";
			},
			clickHandler : function () {
				alert ("sorry, UNLOAD is not yet implemented");
			}
		} */
	},
	// row config goes here
	rowProperties : null,
	
	rowPropertiesInit : function () {
		var propertiesHash = {}
		var myRowKey;
		var myRowProperties;
		
		<c:forEach var="entry" items="${faForm.frameworks}" varStatus="i">
			<c:set var="framework" value="${entry.value}" />
			// load up dict with everything the cells for this row will need.
			myRowKey = '${framework.xmlFormat}';
			myRowProperties = {
				xmlFormat : '${framework.xmlFormat}',
				name : '${framework.name}',
				version: '${framework.version}'
			} 
			propertiesHash[myRowKey] = myRowProperties;
			
		</c:forEach>
		DcsTableConfig.rowProperties = propertiesHash;
		
		// DEBUGGING
/* 		log ("ROW PROPERTIES");
		$H(propertiesHash).each (function (pair) {
			var xmlFormat = pair.key;
			var props = pair.value;
			log (xmlFormat);
			$H(props).each (function (pair) {
				log ("\t%s: %s", pair.key, pair.value);
			});
		}); */
		
		
	} // end of rowPropertiesInit
}  // end of DcsTableConfig			

function dcsTableInit () {
	DcsTableConfig.rowPropertiesInit();
	dcsTable = new DcsTable (DcsTableConfig);
	dcsTable.sortRows (dcsTable.sortKey, 0);
	
	var unitializedFrameworks = $A();
	<c:if test="${not empty faForm.uninitializedFrameworks}">
		<c:forEach var="framework" items="${faForm.uninitializedFrameworks}">
			unitializedFrameworks.push ("${framework.xmlFormat}")
		</c:forEach>
	</c:if>
	
	dcsTable.highlightRow (unitializedFrameworks);
	return;
}

document.observe ("dom:loaded", dcsTableInit);
</script>

</head>                                    

<body text="#000000" bgcolor="#ffffff">

<st:pageHeader toolLabel="${title}" currentTool="settings" />

<%-- <st:pageMessages okPath="frameworks.do" /> --%>
<st:pageMessages />  <%-- try to reduce unneeded clicks --%>

<h3>Loaded Frameworks</h3>
<p>
	The following metadata frameworks and indicated versions are currently loaded.
	<br />
	To view the configuration for a particular 
	metadata framework, click on the framework name.
	<br />
	To change configurations associated with a metadata framework, click the appropriate edit link.
</p>
			
<c:if test="${not empty faForm.uninitializedFrameworks}">
<div class="page-error-box error-msg" style="margin:0px 0px 10px 0px;">
	<b>ALERT</b>: The following frameworks must be reloaded before they can be used:
	<ul><c:forEach var="framework" items="${faForm.uninitializedFrameworks}">
		<li class="error-msg">${framework.name} (${framework.xmlFormat})</li>
	</c:forEach>
	</ul>
</div>
</c:if>

<st:framework-registry-warnings 
	baseUrl="${contextPath}/admin/frameworks.do" />
		
<div id="frameworks-setting-table-wrapper"></div>

<c:if test="${not empty faForm.unloadedFrameworks}">
<br/>
	<h3>Avaliable Frameworks</h3>
	<p>These frameworks are not currently loaded</p>
	<div style="margin:15px 0px 10px 50px">
<%-- 			<select name="format-to-load" id="format-to-load">
				<option value="">-- Select a format --</option>
				<c:forEach var="xmlFormat" items="${faForm.unloadedFrameworks}">
					<option value="${xmlFormat}">${xmlFormat}</option>	
				</c:forEach>
			</select> --%>
		<table bgcolor="#666666" cellpadding="6" cellspacing="1" border="0">
			<tr bgcolor='#CCCEE6'>
				<td align="center" nowrap><b>Format</b></td>
				<td align="center" nowrap><b>Load</b></td>
			</tr>
			<c:forEach var="xmlFormat" items="${faForm.unloadedFrameworks}">
				<tr class='dcs-table-row'> 
					<td align="center">${xmlFormat}</td>
				<td align="center">
					<a href="../admin/frameworks.do?xmlFormat=${xmlFormat}&command=loadFramework">Load</a>
				</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</c:if>

<br />

<h3>Framework Configuration Directory</h3>
	<p>The Frameworks configuration directory is located at:<br />
	<span class="configPath">${frameworkConfigDir}</span>
		This contains the configuration records of loaded and available frameworks.</p>
</body>
</html:html>

