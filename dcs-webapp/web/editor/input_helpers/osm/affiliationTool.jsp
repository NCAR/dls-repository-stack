<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Affiliation Helper</c:set>

<html:html locale="true">
<head>
<title><st:pageTitle title="${title}" /></title>
<%@ include file="/baseHTMLIncludes.jsp" %>
<link rel='stylesheet' type='text/css' href='${contextPath}/editor/input_helpers/osm/affiliation-helper-styles.css'>

<script type="text/javascript" src="${contextPath}/editor/input_helpers/osm/resultTableRows.js"></script>
<script type="text/javascript" src="${contextPath}/editor/input_helpers/osm/people-search.js"></script>
<script type="text/javascript" src="${contextPath}/editor/input_helpers/osm/affiliationTool.js"></script>
<script type="text/javascript">

/* helper is defined in affiliationWidget.jsp */
var helper = window.top.affiliationHelper;
var searcher;

if (!helper) {
	alert ("helper not found");
}

/* get data from the cataloged record */
/* function showRecordData(data) {
		$('debug').update();
		$H(record_data).each(function (pair) {
			$('debug').insert ('<div>'+pair.key+': '+pair.value+'</div>');
		});
} */

/* clear the data read by affiliationHelper.js */
/* function clearData() {
	log ('clear data');
	helper.toolData = null;
} */


function pageInit () {

	var searcher = new PeopleSearcher("${contextPath}/proxy/ucas.do");
	var affliationTool = new AffiliationTool (helper, searcher);

}

Event.observe (window, 'load', pageInit, false);

</script>
</head>
<body>
<table id="search-scope">
	<tr>
		<td width="80%">
			<h1>${title}</h1>
		</td>
		<td style="white-space:nowrap">
			<div><b>Status:</b>
				<ul class="hor-list">
					<li>
						<input type="checkbox" name="status" id="active_status" checked="true" value="true"/>
						<label for="active_status">Active</label>
					</li>
					<li>
						<input type="checkbox" name="status" id="inactive_status" value="true"/>
						<label for="inactive_status">Inactive</label>
					</li>
				</ul>
			</div>
			<div>
				<b>Scope:</b>
				<ul class="hor-list">
					<li>
						<input type="radio" name="scope" id="all_scope" value="all" checked="true"/>
						<label for="all_scope">All</label>
					</li>
					<li>
						<input type="radio" name="scope" id="staff_scope" value="staff"/>
						<label for="staff_scope">Staff</label>
					</li>
					<li>
						<input type="radio" name="scope" id="visitor_scope" value="visitor"/>
						<label for="visitor_scope">Visitor</label>
					</li>	
					<li>
						<input type="radio" name="scope" id="collaborator_scope" value="collaborator"/>
						<label for="collaborator_scope">Collaborator</label>
					</li>				
				</ul>
			</div>
		</td>
	</tr>
</table>
<%-- <h4 id="affiliation-id"></h4> --%>

<div id='debug'></div>

<%-- <input type="button" value="GetData" id="get-data-button" />
<input type="button" value="SetData" id="set-data-button" /> --%>
<hr/>

<div id="ucas-search">

		<div class="search-control">

		<table>
			<tr valign="bottom">
				<td>
					<div class="search-control-label">last name</div>
					<input type="text" id="ucasLastName" value="" />
				</td>
				<td>
					<div class="search-control-label">first name</div>
					<input type="text" id="ucasFirstName" value="" />
				</td>
<%-- 				<td>
					<div class="search-control-label">middle</div>
					<input type="text" id="ucasMiddleName" value="" size="1" />
				</td> --%>
				<td>
					<div class="search-control-label">upid</div>
					<input type="text" id="upid" value="" size="6"/>
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

	<div id="ucas-display" style="display:none;margin-top:10px;">
		<div class="sub-header">Search results <span id="numResults"></span></div>
		<div class="search-control-label">Select a search result by clicking anywhere in its row</div>
		<div class="search-results-wrapper">
			<table id="search-results"></table>
		</div>
	</div>

</div>

<hr/>

<div id="affiliation-tool-info"></div>

<input type="button" value="Done" id="done-button" />
<input type="button" value="Cancel" id="cancel-button" />

<div id="report"></div>
</body>
</html:html>
