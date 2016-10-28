<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ include file="../../TagLibIncludes.jsp" %>
<html>
<head>
	<title>Harvest Repository Manager Settings and Configuration</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	
	<%-- Include style/menu templates --%>
	<%@ include file="../../head.jsp" %>

</head>
<body>
<%-- Include style/menu templates --%>
<c:import url="../top.jsp" />

<div id="breadcrumbs">
	<a href='<c:url value="/admin"/>' class="collTitleLnk">Admin</a>
	&gt;
	Script Runner
</div>
	
<h1>Script Runner</h1>
<p>
Run a script using the current server configuration. This is for developers only.
</p>
<br/>
<p> 	     	
	
	<form name="scriptRunner" action="" method="POST" onsubmit="return confirm('Are you sure you want to run this script? Running a script can corrupt the database.')">
		<table>
			<tr>
				<td>Script Package</td>
				<td><input type="text" name="script_package" value="${script_package}" size="35"/></td>
			</tr>
			<tr>
				<td>Script Name</td>
				<td><input type="text" name="script_name" value="${script_name}" size="35"/></td>
			</tr>
			<tr>
				<td align="right" colspan=2>
					<input type="submit" value="Execute Script"/>
				</td>
			</tr>
		</table>
	</form>
</p>

<c:if test="${not empty msg}">
	Result: ${msg}
</c:if>

<%-- Include style/menu templates --%>
<%@ include file="../../bottom.jsp" %>
</body>
</html>

