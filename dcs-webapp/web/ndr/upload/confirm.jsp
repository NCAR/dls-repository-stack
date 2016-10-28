<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<html:html>
<head>
<title>Upload Confirmation</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</meta>
<%@ include file="/baseHTMLIncludes.jsp" %>
<script type="text/javascript">

function pageInit() {
	 $('done_button').observe ('click', handleDone);
}

function handleDone () {
	var url = "${uploadForm.contentURL}"
	window.opener.update_upload_widget (url);
	window.close();
}

Event.observe (window, "load", pageInit);
</script>
<style type="text/css">
table {
	margin:10px 0px 10px 10px;
	border-collapse:collapse;
}

td {
	padding:5px;
	border:1px #666666 solid;
}

td.prop {
	background-color:#CCCEE6;
	font-weight:bold;
}

td.val {	
	background-color:#E3E4F1;
}

em {
	font-weight:bold;
	font-style:italic;
}
</style>
</head>
<body>

<h1>Upload Confirmation</h1>
<div style="border:thin green solid;font-style:italic;padding:5px;margin:10px">
	This is a default page. If an actionForward param is passed into the Upload
	Action, you can direct the confirmation to the page of your liking.
</div>
<st:pageMessages />
You have uploaded:

<table cellpadding="5" cellspacing="1" bgcolor="purple">
	<tr bgcolor="white">
		<td class="prop" align="right">File Name:</td>
		<td class="val" align="left">${uploadForm.myFile.fileName}</td>
	</tr>
	<tr bgcolor="white">
		<td class="prop" align="right">Content Type:</td>
		<td class="val" align="left">${uploadForm.myFile.contentType}</td>
	</tr>
	<tr bgcolor="white">
		<td class="prop" align="right">File Size:</td>
		<td class="val" align="left">${uploadForm.myFile.fileSize}</td>
	</tr>	
</table>

<%-- <div>contentURL: ${uploadForm.contentURL}</div> --%>

<div style="margin:0px 0px 10px 0px">
	<b>NOTE:</b> You must hit the <em>Done</em> button to update the metadata record!
</div>

<div style="margin:0px 0px 10px 40px">
<input type="button" value="Done" id="done_button" />
<input type="button" value="Cancel" onclick='window.close()' />
</div>

</body>
</html:html>
