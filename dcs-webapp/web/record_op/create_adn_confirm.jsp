<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<html>
<head>

	<%@ include file="/baseHTMLIncludes.jsp" %>
	
	<title><st:pageTitle title="New Record Confirmation" /></title>
</head>
<body bgcolor=white>

<%-- page header --%>
<st:pageHeader currentTool="home" toolLabel="Create ADN Record" />

<st:breadcrumbs>
	Collection: ${carForm.collectionName}
	<st:breadcrumbArrow />
	<span class="current">${carForm.recId}</span>
</st:breadcrumbs>

<st:pageMessages okPath="" />

<div style="margin-left:50px">
	<table cellpadding="4">
		<tr valign="top">
			<td align="right" width="35%" nowrap="1"><em>Primary URL:</em></td>
			<td>${carForm.primaryUrl}</td>
		</tr>	
		<tr valign="top">
			<td align="right"><em>Title:</em></td>
			<td>${carForm.title}</td>
		</tr>
		<tr valign="top">
			<td align="right"><em>Record Id:</em></td>
			<td>${carForm.recId}</td>
		</tr>
		<%-- <tr><td></td></tr> --%>
		<tr valign="bottom"><td height="30" colspan="2" align="center">
			<input type="button" value="Edit entire record" onclick="location='${carForm.editRecordLink}'" />
			&nbsp;
			<input type="button" value="Search collection" onclick="location='../browse/query.do?q&scs=0${carForm.collection}'" />
			&nbsp;
			<input type="button" value="To collections home" onclick="location='../browse/home.do'" />
			&nbsp;
			<input type="button" value="Create another record" 
				onclick="window.location='adn.do?collection=${carForm.collection}&command=new'"/>
		</td></tr>
	</table>





</body>
</html>
