<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Repository term count report</c:set>

<html:html>
<head>
<%@ include file="/baseHTMLIncludes.jsp" %>
<title><st:pageTitle title="${title}" /></title>

<style type="text/css">
	table#report-table{ 
		background-color:#f1f1f1;
		margin:15px 0px 15px 0px;
	}

</style>

</head>
<body>

<st:pageHeader toolLabel="${title}" currentTool="settings" />

	<div><a href="ListFields.jsp?verb=ListFields">Back to All indexed Fields</a></div>

	<%-- Generate the term-count map for the given fields --%>
	<c:set property="calculateCountsForFields" value="${param.fields}" target="${drf}" />
	
	
	<%-- Display the data... --%>
	<c:set var="numFields" value='${fn:length( fn:split( param.fields , " " ) )}'/>
	
	<table id="report-table" border=1 cellpadding=4 cellspacing=0>		
		<c:if test="${numFields <= 1}">
			<tr>
				<%-- <td colspan=3>Term count report for field: <b>${drf.termCountFields}</b></td> --%>
				<td colspan=3>Term count report for field: <b>${param.fields}</b></td>
			</tr>
			<tr>
				<td colspan=3>Total number of terms in this field: <fmt:formatNumber value="${drf.numTerms}" type="number"/></td>
			</tr>
		</c:if>
		<c:if test="${numFields > 1}">
			<tr>
				<td colspan=3>Term count report for fields: <b>${drf.termCountFields}</b></td>
			</tr>
			<tr>
				<td colspan=3>Total number of terms in these fields: <fmt:formatNumber value="${drf.numTerms}" type="number"/></td>
			</tr>		
		</c:if>
		<tr>
			<td colspan=3>Date generated: <%= new java.util.Date().toString() %></td>
		</tr>
	</table>

	
	<table border=1 cellpadding=4 cellspacing=0>		
		<tr>
			<td><b>Term</b></td>
			<td><b>Term count</b></td>
			<td><b>Record count</b></td>
			<td><b>Ave count per record</b></td>
			<c:if test="${numFields > 1}">
				<td><b>Appears in these fields</b></td>
			</c:if>
		</tr>
		<c:forEach var="term" items="${drf.termCountMap}">						
			<c:url value="/browse/query.do" var="recordsLink">
				<c:param name="s" value="0"/>
				<c:param name="q">
					${term.value.fields[0]}:"${term.key}"<c:forEach var="field" 
						items="${term.value.fields}" begin="1"> OR ${field}:"${term.key}"</c:forEach>					
				</c:param>
			</c:url>
			<tr>
				<td><a href="${recordsLink}" target="_blank" title="Click to view records that contain this term">${term.key}</a></td>
				<td>${term.value.termCount}</td>				
				<td>${term.value.docCount}</td>
				<td><fmt:formatNumber value="${term.value.termCount / term.value.docCount}" pattern="##.###"/></td>
				<c:if test="${numFields > 1}">
					<td>
						${term.value.fields[0]}<c:forEach var="field" 
							items="${term.value.fields}" begin="1">, ${field}</c:forEach>
					</td>
				</c:if>
			</tr>
		</c:forEach>
	</table>

		
</body>
</html:html>

