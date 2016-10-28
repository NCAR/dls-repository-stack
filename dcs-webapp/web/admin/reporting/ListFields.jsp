<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Indexed fields</c:set>

<html:html>
<head>
<%@ include file="/baseHTMLIncludes.jsp" %>
<title><st:pageTitle title="${title}" /></title>

<style type="text/css">
	table#fields-table{ 
		background-color:#f1f1f1;
		margin: 15px 0px 0px 0px;
	}

	table#fields-table td{ 
		background-color:#ffffff;
	}
	
	
</style>

</head>
<body>

<st:showPage page="ListFields.jsp"/>
	
<st:pageHeader toolLabel="${title}" currentTool="settings" />


	<table id="fields-table" border=1 cellpadding=4 cellspacing=0>		
		<tr>
			<th>DCS index fields</th>
		</tr>		
		<c:forEach var="field" items="${applicationScope.index.fields}">
			<tr>
				<td><a href="report.do?verb=TermCount&fields=${field}" title="Click to view terms and counts for this field">${field}</a></td>
			</tr>
		</c:forEach>
	</table>

	<br><br>

	
</body>
</html:html>

