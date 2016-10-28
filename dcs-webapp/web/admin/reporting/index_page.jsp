<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Index Reporting</c:set>

<html:html>
<head>
<%@ include file="/baseHTMLIncludes.jsp" %>
<title><st:pageTitle title="${title}" /></title>
</head>
<body onLoad="JavaScript: document.fieldForm.fields.focus()">

<st:pageHeader toolLabel="${title}" currentTool="settings" />

<div>
	<h1>DCS Term and Field Reporting</h1>
	
	<p><a href="../admin.do?page=index">Back to the Indexing page</a></p>
	
	<h2>Fields in the DCS index</h2>
	<p>This report shows a <a href="ListFields.jsp?verb=ListFields" title="Click to view the list of fields">list of all fields in the DCS index</a>.</p>	
	
	<h2>Term count reports:</h2>
	<p>These reports show the terms that exist in the index within a given field or
	fields. A total term count, total number of documents in which the term appears and 
	list of fields in which the term appears
	is provided.</p>
	
	Enter a field or list of fields separated by space below *:
	
	<form method="GET" action="report.do" name="fieldForm">
		<input type="text" name="fields" size="30" value="">
		<input type="hidden" name="verb" value="TermCount"> 
		<input type="submit" value="Show report">
		<br>* Note that if more than one field is requested the report will take considerably longer to generate.				
	</form>		
	
	Or use one of these pre-defined links:
	<p>
	<a href="report.do?verb=TermCount&fields=title+keyword+description">Term count report: title, description and keyword fields</a><br>
	<a href="report.do?verb=TermCount&fields=stems">Term count report: stems field</a><br>
	<a href="report.do?verb=TermCount&fields=default">Term count report: default field</a><br>
	</p>
	
	

	<p>For details about the term count reports, see <a href="REPORT_INFORMATION.txt">REPORT_INFORMATION.txt</a></p>	
	

	<h2>Show stems</h2>
	<p>This form allows you to see the stems used in DCS for given words.</p>	
	Enter one or more words separated by space:
	<form method="GET" action="report.do" name="stemForm">
		<input type="text" name="words" size="50" value="${param.words}">
		<input type="hidden" name="verb" value="ViewStems"> 
		<input type="submit" value="Show stems">
	</form>	
	
</div>
</body>
</html:html>




