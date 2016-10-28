<%@ include file="/lib/includes.jspf" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<html>
<head>
<%@ include file="/editor/editorHTMLIncludes.jsp" %>
<script>

function doUpload (event) {
	log ("doUpload()");
	if (!$F('myFile')) {
		alert ("Please select a file");
		return;
		}
	var form = $('myform');
	form.command.value = "upload";
	form.forwardPath.value = "${uploadForm.forwardPath}"; // context-relative!
	form.submit();
}

function pageInit () {
	$('upload_button').observe ('click', doUpload); 
}

Event.observe (window, 'load', pageInit);

</script>
<style type="text/css">
em {
	font-weight:bold;
	font-style:italic;
	}
</style>
</head>
<body>
<h1>Upload File</h1>

<st:pageMessages okPath="upload.do" />

<p>Select <em>Browse..</em> to select the file, and then <em>Upload</em> to perform the upload to the NDR.</p>

<form name="myform" id="myform" method="post" action="upload.do" enctype="multipart/form-data">
	<input type="hidden" name="command" value=""/>
	<input type="hidden" name="forwardPath" value=""/>
<div align="center">
	<div>
				<input type="file" size="50" name="myFile" id="myFile" class='record-action-button'  size="30" />
				<input type="button" id="upload_button" class='record-action-button' value="Upload"/>
	</div>
	<div style="margin:20px 0px 0px 0px">
				<input type="button"  class='record-action-button' 
							 value="Cancel" onclick='window.close()' />
	</div>
</div>
</form>
</body>
</html>
