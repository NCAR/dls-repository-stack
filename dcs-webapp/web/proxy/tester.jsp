<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.schemedit.security.access.ActionPath" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<html>
<head>
<%@ include file="/baseHTMLIncludes.jsp" %>
<script type="text/javascript" src="directory-search.js"></script>

<style type="text/css">

div.search-control table {
	border-collapse:collapse;
}
div.search-control table td {
	padding:0px 4px 0px 0px;
}

</style>
</head>
<body>


<div id="ucas-directory-search" class="toggling-header directory-search-header"></div>

<div id="ucas-search">

		<div class="search-control">
		
		<table>
			<tr valign="bottom">
				<td>
					<div class="search-control-label">first name</div>
					<input type="text" id="ucasFirstName" value="" />
				</td>
				<td>
					<div class="search-control-label">last name</div>
					<input type="text" id="ucasLastName" value="" />
				</td>
				<td>
					<input type="button" id="ucasSearchButton" value=" search " />
				</td>
				<td>
					<input type="button" id="ucasClearButton" value=" clear " />
				</td>
			</tr>
		</table>
		<div class="search-control-label">selects records that contain values provided in both fields
					(blank values are ignored)</div>
		</div>

	<div id="ucas-display" style="display:none;margin-top:10px">
		<div class="search-control-label">search results</div>
		<table id="ucas-search-results" class="user-listing-table">
		</table>
	</div>
	
</div>

<script type="text/javascript">
// requires "directory-search.js" - see edit-user.jsp
	try {
		new UcasSearcher();
		log ("UcasSearcher instantiated");
	} catch (error) {
		alert ("could not instantiate Ucas directory searcher: " + error)
	}
</script>
</body>
</html>
