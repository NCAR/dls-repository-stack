<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title" value="NDR Explorer" />
<html>
<head>
	<link rel="stylesheet" href="../styles.css" type="text/css">
	<link rel="stylesheet" href="includes/json-styles.css" type="text/css">
	<link rel="stylesheet" href="includes/ndr-browser-styles.css" type="text/css">

<style type="text/css">
.small-button {
	font-size:8pt;
	}
table tr {
	background-color:white;
	}
</style>
	
	<script type="text/javascript">
		<%@ include file="includes/NDRGlobals.jsp" %>
	</script>
	<script type="text/javascript" src="../lib/javascript/prototype.js"></script>
	<script type="text/javascript" src="includes/JsonViewer.js"></script>
	<script type="text/javascript" src="includes/NDRObject.js"></script>
	<script type="text/javascript" src="includes/NDRBrowser.js"></script>
	<script type="text/javascript" src="includes/NDRExplorer.js"></script>
	<script type="text/javascript">
	
var ndrApiBaseUrl;
var debugging = true;
	
function pageInit () {

	if (!debugging)
		$('debug').hide();
	
	NDRGlobals.ndrApiBaseUrl = $F('repository-select');
		
	Event.observe ('listMembersButton', 'click', doListMembers);
	Event.observe ('getButton', 'click', doGet);
	Event.observe ('describeButton', 'click', doDescribe);
	
	if ($('ndrProxySubmit'))
		Event.observe ('ndrProxySubmit', 'click', doNdrApiProxy);
	
	Event.observe ('repository-select', 'change', function (evnt) {
		// alert ($F('repository-select'));
		NDRGlobals.ndrApiBaseUrl = $F('repository-select');
	});
	
	
	// activates the buttons that set the handle in the GET form
	Event.observe ('setMDPgettter', 'click', function (envt) {
		$('getterHandle').value = $F('defaultMDPhandle');
		});
	Event.observe ('setMDgettter', 'click', function (envt) {
		$('getterHandle').value = $F('defaultMDhandle');
		});
		
	// activate button to set default MDP for lister
	Event.observe ('setMDPlister', 'click', function (envt) {
		$('listerMDPhandle').value = $F('defaultMDPhandle');
		});
		
	// activates the buttons that set the handle in the NDR Proxy form
	
	if ($('setMDPproxy')) {
		Event.observe ('setMDPproxy', 'click', function (envt) {
			$('proxyHandle').value = $F('defaultMDPhandle');
			var select = $('ndrType');
			$A(select.options).each ( function (o) {
					if (o.value == "MetadataProvider") o.selected = true;
			});
		});
	
		Event.observe ('setMDproxy', 'click', function (envt) {
			$('proxyHandle').value = $F('defaultMDhandle');
			var select = $('ndrType');
			$A(select.options).each ( function (o) {
				if (o.value == "Metadata") o.selected = true;
				});
		});
	}
		
	Event.observe ('ndrResultsToggle', 'click', function (evnt) {
			doNdrResponseToggle ();
			Event.stop(evnt);
		});
		
	if ($('inputXML')) {
		var inputXML = '<inputXML xmlns="http://ns.nsdl.org/ndr/request_v1.00/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://ns.nsdl.org/ndr/request_v1.00/ http://ns.nsdl.org/schemas/ndr/request_v1.00.xsd" schemaVersion="1.00.000">';
		inputXML += '\n\n</inputXML>';
		
		debug (inputXML);
		$('inputXML').value = inputXML;
	}
	
}

Event.observe (window, "load", pageInit);
</script>
	<title><st:pageTitle title="${title}" /></title>
</head>
<body bgcolor=white>

<a name="top"/>
<h1>${title}</h1>
<div id="debug">
	<h3>Debug</h3>
</div>

<form action="ndr.do">
	<input type="hidden" name="command" value="explore" />
	<input type="hidden" name="update" value="1" />
	
<table cellpadding="5px" bgcolor="black"  width="100%">

	<tr valign="bottom">
		<td><div class="prompt">View Results As</div>
			<select id="viewMethod">
				<option SELECTED>NDRObject</option>
				<option>JSONObject</option>
				<option>XML</option>
			</select>
		</td>
		<td><div colspan="2" class="prompt">Choose NDR Repository</div>
			<select id="repository-select" name="repository-select">
				<option value="http://ndrtest.nsdl.org/api" SELECTED>NDR Test</option>
				<option value="http://ndr.nsdl.org/api">NDR Production</option>
			</select>
		</td>

			<td colspan="2" align="center">
				<div> Metadata Handle - 2200/test.20070403150219940T</div>
				<div> MetadataProvider Handle - 2200/test.20070411183250394T</div>
			</td>
	</tr>
 
	<tr>
		<td><h4>Default Handles</h4></td>
		<td align="center">
			<table>
				<tr>
					<td>
						<div class="prompt">Default Metadata Handle</div>
						<input type="text" name="defaultMDhandle" id="defaultMDhandle" size="30" value="${ndrForm.defaultMDhandle}" />
					</td>
					<td width="30px"></td>
					<td>
						<div class="prompt">Default MetadataProvider Handle</div>
						<input type="text" name="defaultMDPhandle" id="defaultMDPhandle" size="30" value="${ndrForm.defaultMDPhandle}" />
					</td>
				</tr>
			</table>
		</td>
		<td>
			<input type="submit" id="defaultsUpdateButton" value="update" />
		</td>
	</tr>

 
	<tr valign="middle">
		<td align="bottom"><h4>List Members</h4>(for MetadataProvider)</td>
		<td>
			<table>
				<tr>
					<td>		
						<div class="prompt">Metadata Provider Handle</div>
						<input type="text" id="listerMDPhandle" size="30" value=""/>
					</td>
					<td width="30px"></td>
					<td align="center">
						<input type="button" class="small-button" id="setMDPlister" value="default MDP" /><br/>
					</td>
				</tr>
			</table>
						
		</td>
		<td>
			<input type="button" id="listMembersButton" value=" go " />
		</td>
	</tr>
	
	<tr valign="middle">
		<td align="bottom"><h4>Get / Describe</h4></td>
		<td>
			<table>
				<tr>
					<td>
						<div class="prompt">Handle</div>
						<input type="text" id="getterHandle" size="30" value=""/>
					</td>
					<td width="30px"></td>
					<td align="center">
						<input type="button" class="small-button" id="setMDPgettter" value="default MDP" /><br/>
						<input type="button" class="small-button" id="setMDgettter" value="default MD" />
					</td>
				</tr>
			</table>
		</td>
		<td>
			<input type="button" id="getButton" value=" get " />
			<br /><br/>
			<input type="button" id="describeButton" value=" describe " />
		</td>
	</tr>
	
 

	<!-- NDR Proxy Commented out for now -->
	<tr valign="top">
		<form id="ndrApiProxyForm">
		<td align="bottom"><h4>NDR Proxy</h4></td>
		<td>
			<table>
				<tr>
					<td>
						<div class="prompt">Object Handle</div>
						<input type="text" id="proxyHandle" name="handle" size="30" value="" />
					</td>
					<td width="30px"></td>
					<td align="center">
						<input type="button" class="small-button" id="setMDPproxy" value="default MDP" /><br/>
						<input type="button" class="small-button" id="setMDproxy" value="default MD" />
					</td>
				</tr>
			</table>
					
			<div class="prompt">inputXML</div>
			<textArea id="inputXML" name="inputXML" rows="12" cols="60"></textArea>
		</td>
		<td>
			<div class="prompt">Object Type</div>
			<select id="ndrType">
<!-- 				<option value="AGGREGATOR">aggregator</option>
				<option value="RESOURCE">resource</option> -->
				<option value="Metadata">metadata</option>
				<option value="MetadataProvider">metadata provider</option>
			</select>
			<br/><br/>
			<div class="prompt">command</div>
			<select id="ndrCmd">
				<option value="add">add</option>
				<option value="modify">modify</option>
<!-- 				<option value="DELETE">delete</option> -->
				<option value="find">find</option>
			</select>
			<br/><br/>
				<input type="button" id="ndrProxySubmit" value=" go " />
		</td>
		</form>
	</tr>

</table>
</form>
<table id="responseHeader" width="100%">
	<tr>
				<td align="left"><h3>NDR Response  
					<span style="font-size:10pt;font-weight:normal">
						[ <a href="" id="ndrResultsToggle">hide</a> ]
					</span></h3></td>
		
		<td align="right"><a href="#top">to top</a></td>
	</tr>
</table>
<div id="ndrResults">
<div id="queryUrl" style="border:grey thin solid;padding:5px;margin-bottom:5px;"></div>
<div id="ndrResponse"></div>
</div>

</body>
</html>
