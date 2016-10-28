<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<html>
<head>
	
	<%@ include file="/baseHTMLIncludes.jsp" %>
	
	<title><st:pageTitle title="Create Collection Confirmation" /></title>
<script type="text/javascript">
function doRegisterWithNdr () {
		if (confirm ("Are you sure you want to register this collection in the ndr?")) {
			var params = {
				collection : "${csForm.collectionKey}",
				command  : "writeCollectionInfo"
				}
			url = "${contextPath}/ndr/ndr.do?" + $H(params).toQueryString();
			window.location = url;
			// alert (url);
		}
}

function handleCreateRecord (event) {
	//log ("createRecordHandler");
	var xmlFormat = "${csForm.collectionKey}";
	var format = "${csForm.formatOfRecords}";
	var path;
	var params;
	if (format == 'adn') {
		path = '${contextPath}/record_op/adn.do'
		params = {
			command : 'new',
			collection : xmlFormat
		}
	}
	else if (format == 'mast') {
		path = "${contextPath}/record_op/mast.do";
		params = {
			command: 'new',
			collection : xmlFormat
		}
	}
	else {
		path = "${contextPath}/record_op/single.do";
		params = {
			command : 'newRecord',
			collection : xmlFormat,
			xmlFormat : format
		}
	}
	url = path;
	if (params)
		url += "?" + $H(params).toQueryString();
	// log ("going to " + url)
	window.location = url;
}
	

</script>
	
</head>
<body bgcolor=white>
<st:pageHeader currentTool="manage" toolLabel="Create Collection" />

<st:pageMessages okPath="" />

<c:if test="${ndrServiceEnabled}">

	<h3>Register Collection in the NDR?</h3>
	<p>
		<input type="button" value="Register" onclick="doRegisterWithNdr()" />
		Click this button to store the metadata for this collection in the NDR.
	</p>
</c:if>

<h3>Collection Attributes</h3>
<div style="margin-left:50px">
	<table cellpadding="4">
		<tr valign="top">
			<td align="right" width="150px"><em>Title:</em></td>
			<td>${csForm.shortTitle}</td>
		</tr>
		<tr valign="top">
			<td align="right"><em>Item Record Format:</em></td>
			<td>${csForm.formatOfRecords}</td>
		</tr>
		<tr valign="top">
			<td align="right"><em>Id Prefix:</em></td>
			<td>${csForm.idPrefix}</td>
		</tr>
		
		<c:if test="${csForm.userProvidedKey}">
			<tr valign="top">
				<td align="right"><em>Collection Key:</em></td>
				<td>${csForm.collectionKey}</td>
			</tr>		
		</c:if>
		
		<%-- <tr><td></td></tr> --%>
		<tr valign="bottom">
			<td height="30" colspan="2" align="center">
		
				<%-- COLLECTION ACCESS --%>
			<c:if test="${authenticationEnabled}">
			<input type="button" value="Assign access to new collection" 
					onclick="window.location='${contextPath}/manage/collectionAccessManager.do?command=edit&collection=${csForm.collectionKey}'"/>
			</c:if>
					
			<input type="button" id="create_record_button" value="Create an itemRecord" 
				onclick="handleCreateRecord();"/>
			
			<input type="button" value="Create another collection" 
				onclick="window.location='${contextPath}/manage/collections.do?command=new'"/>
			
			</td></tr>
	</table>
	
</body>
</html>
