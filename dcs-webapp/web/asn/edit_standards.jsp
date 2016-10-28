<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Edit Standards - ${asnForm.xmlFormat}</c:set>
<c:set var="showAsnStatus" value="${true}" />
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>

<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

<%@ include file="/manage/manageHTMLIncludes.jsp" %>
<link rel='stylesheet' type='text/css' href='${contextPath}/asn/standards.css'>
<script type="text/javascript" src="edit_standards.js"></script>
<script>

var standardsTable = null;

function sortTable (sortSpec) {
	if (!standardsTable) {
		throw "what doc table?"
	}
	standardsTable.sort (sortSpec);
}

function pageInit () {
		try {
			standardsTable = new StandardsTable ('doc-table');
		} catch (error) {
			log ("dcsTable error: " + error);
			return;
		}
		log ("standardsTable initialized");

		$A(['author-filter', 'created-filter', 'title-filter']).each (function (filter) {
				standardsTable.filters.push (new StandardsTableFilter (filter, standardsTable));
		});

		$$('input[name=selectedDocs][type=checkbox]').each (function (chkbox) {
			chkbox.observe ('click', function (event) {
				var row = event.element().up('tr');
				log ('I have been clicked and my value is: ' + $F(chkbox));
				var selected = $F(chkbox);
				if (selected)  {
					row.addClassName ('selected-doc');
				}
				else {
					row.removeClassName('selected-doc');
				}
			});
		});

		log ('about to count interesting');
		try {
			if (standardsTable.countInteresting() > 0) {
				log ("showing");
				show_summary();
			}
		} catch (error) {
			log ("could not countInteresting: " + error);
			}
		
		log ("about to filtersButtonInit");
			
		filtersButtonInit();
}

function filtersButtonInit() {
	var button = $('filters-button');
	var content = $(button.identify()+'_content');
	
	button.observe ('mouseover', function () {
		button.setStyle ({cursor:'pointer'});
	});
	button.observe ('click', function (event) {
		if (content.visible()) {
			button.removeClassName ('filters-button-open');
			content.hide();
		}
		else {
			log ("added");
			button.addClassName ('filters-button-open');
			content.show();
		}
	});
}

function saveChoices () {
	log ("save Choices()");
	var selected = standardsTable.getRows().inject ([], function (acc, row) {
		var chkbox = row.down('td.selected').down('input');
		var value = chkbox.getValue()
		if (value) acc.push (value);
		return acc;
	 });

	 log ("otherSubjectDocs");
	 // add selcted docs from other subjects so we don't loose them
	 $$('input[name="otherSubjectDocs"]').each (function (input) {
	 	selected.push (input.getValue());
		log ("pushed " + input.getValue());
	});

	log ("passing " + selected.length + " keys to action");

	 var params  = {
	 	'keys':selected,
		'xmlFormat': '${asnForm.xmlFormat}',
		'subject' : '${asnForm.subject}',
		'command' : 'update'
	};

	if (false) {
		log ("window location change has been commented out");
		return;
	}
	else {
		window.location = 'asn.do?' + $H(params).toQueryString();
	}
} /* save choices */

function form_reset () {
	if (confirm ('are you sure you want to reset the form?')) {
		$('standards-form').reset();
		stylize_table();
	}
	return false;
}

function stylize_table () {
		standardsTable.getRows().each (function (row) {
			var chkbox = row.down('td.selected').down('input');
			if (chkbox.checked)
				row.addClassName('selected-doc');
			else
				row.removeClassName('selected-doc');
	 });
}

function show_all () {
	log ('SHOW ALL');
	$('show-all-button').update (new Element ('b').update('all standards'));
	$('show-summary-button').update ( new Element ('a', {'href':'javascript:show_summary()'}).update ('show summary'));
	standardsTable.clearFilters();
	// standardsTable.showAll();
}

function show_summary () {
	log ('SHOW SUMMARY');
	$('show-all-button').update ( new Element ('a', {'href':'javascript:show_all();'}).update ('show all'));
	$('show-summary-button').update (new Element ('b').update('summary'));
	standardsTable.showSummary();
}

function select_all () {
	log ('select_all');
	$$('input[name=selectedDocs][type=checkbox]').each (function (checkbox) {
		checkbox.checked = true;
	});
	
	stylize_table();
}

function select_none () {
	log ('select_none');
	$$('input[name=selectedDocs][type=checkbox]').each (function (checkbox) {
		log ("yada");
		checkbox.checked = false;
	});
	stylize_table();
}

function toggle_selected () {
	log ('toggle_selected');
	$$('input[name=selectedDocs][type=checkbox]').each (function (checkbox) {
		checkbox.checked = !checkbox.checked;
	});
	stylize_table();
}


document.observe ("dom:loaded", pageInit);
</script>
<style type="text/css">

</style>

</head>

<body text="#000000" bgcolor="#ffffff">

<st:pageHeader currentTool="manage" toolLabel="${title}" />

<st:pageMessages />

<c:set var="manageBean" value="${asnForm.manageStandardsBean}" />
<c:set var="stdMgrBean" value="${asnForm.standardsManagerBean}" />

<c:set var="registeredDocIdsForSubject" value="${stdMgrBean.subjectIdsMap[asnForm.subject]}" />
<c:set var="allDocIdsForSubject" value="${stdMgrBean.subjectAllDocIdsMap[asnForm.subject]}" />

<c:set var="extraDocIds" value="${stdMgrBean.extraDocIds}" />

<c:set var="controls">
	<input type="button" value="exit" 
		onclick="window.location='asn.do?command=manage&xmlFormat=${asnForm.xmlFormat}&subject=${asnForm.subject}'" />
	<input type="reset" value="reset" onclick="form_reset();return false"/>
	<input type="button" value="save" onclick="saveChoices();" />
</c:set>

<c:choose>
	<c:when test="${not empty stdMgrBean}">
		<form id="standards-form">
		
		<table width="100%">
			<tr>
				<td>
					<h1>${asnForm.xmlFormat} -> ${asnForm.subject}</h1>
					<p>Configure the <b>${asnForm.subject}</b> standards to be available to catalogers in
					the <b>${asnForm.xmlFormat}</b> framework.</p>

					<%-- Show all stdsDocs for this subject --%>
					<c:set var="subjectItems" value="${asnForm.allDocsSubjectMap[asnForm.subject]}" />
					<div>There are ${fn:length(subjectItems)} items for ${asnForm.subject}
						and ${fn:length(registeredDocIdsForSubject)} registered Docs
			<%-- 			<span style="padding:5px;margin:5px">
							<span class="command-button"><a href="#" onclick="show_all()">show all</a></span>
							<span class="command-button"><a href="#" onclick="show_summary()">summary</a></span>
						</span> --%>
					</div>
				</td>
				<td align="right">
					<table id="key">
						<tr>
							<th align="left">key</th>
						</tr>
						<tr class="registered-doc">
							<td>registered standard doc</td>
						</tr>
						<tr class="extra-doc">
							<td>not registered but used</td>
						</tr>
						<tr class="std-doc-item">
							<td>not registered</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		
		<div style="float:right">${controls}</div>
		<table id="doc-table" width="100%">
		
		<%-- Filters --%>
			<tr id="filter-toggle-row">
				<th colspan="2" align="left"><ul id="filters-button"><li>Filters</li></ul></td>
				<th>
					<span class="command-button" id="show-all-button"><a href="javascript:show_all()">show all</a></span>
					<span class="command-button" id="show-summary-button"><a href="javascript:show_summary()">summary</a></span>
				</th>
				<th></th>
				<c:if test="${showAsnStatus}">
					<th>&nbsp;</th>
				</c:if>
			</tr>
			<tr id="filters-button_content" style="display:none">
				<th><input class="filter" id="author-filter" value=""/></th>
				<th><input class="filter" id="created-filter" value=""/></th>
				<th><input class="filter" id="title-filter" value=""/></th>
				<c:if test="${showAsnStatus}">
					<th>&nbsp;</th>
				</c:if>
				<th bgcolor="#ffffcc" nowrap>
<%-- 						<span class="command-button"><a href="#" onclick="select_all()">all</a></span>
						<span class="command-button"><a href="#" onclick="select_none()">none</a></span>
						<span class="command-button"><a href="#" onclick="toggle_selected()">toggle</a></span> --%>
			</tr>

		
			<%-- header --%>
			<tr>
				<th width="120px" nowrap><a href="javascript:sortTable('author');">author</a></th>
				<th width="75px"  nowrap><a href="javascript:sortTable('created');">created</a></th>
				<th width="90%"><a href="javascript:sortTable('title');">title</a></th>
				<c:if test="${showAsnStatus}">
					<th width="75" nowrap><a style="white-space:nowrap" href="javascript:sortTable('status');">ASN status</a></th>
				</c:if>
				<th>registered</th>
			</tr>
			
			
			
			<c:forEach var="docInfo" items="${subjectItems}">
						<%-- we want to know if this standard is "available" from the standardsManagerBean --%>
 						<%-- <c:set var="selected" value="${sf:listContains(registeredDocIdsForSubject, docInfo.id)}" /> --%>

						<%--
						we want to know if this doc is:
						1 - currently loaded - this gets a check
						2-  configured in this format's doc set.
						--%>
						<c:set var="extra_doc" value="${sf:listContains(extraDocIds, docInfo.id)}" />
						<c:set var="selected_doc" value="${sf:listContains(registeredDocIdsForSubject, docInfo.id)}" />
						<c:set var="registered_doc" value="${sf:listContains(registeredDocIdsForSubject, docInfo.id)}" />

						<c:set var="classNames">std-doc-item ${extra_doc ? 'extra-doc':''} ${registered_doc ? 'registered-doc' : ''}
							${selected_doc ? 'selected-doc' : ''}</c:set>
				<tr id="${docInfo.key}" class="${classNames}" >
					<td class="author">${docInfo.authorName}</td>
					<td class="created">${docInfo.created}</td>
					<td class="title">${docInfo.title}</td>
					<c:if test="${showAsnStatus}">
						<td class="status">${docInfo.status}</td>
					</c:if>
					<td  class="selected" width="100px" align="center">&nbsp;
						<input type="checkbox" value="${docInfo.key}" name="selectedDocs" ${registered_doc ? 'checked' : ''} />
					</td>
				</tr>
			</c:forEach>
		</table>
		<div style="float:right">${controls}</div>
		<%-- Make hidden vars for the registered standards docs from OTHER subjects --%>
		<c:forEach var="subjectItemsMap" items="${stdMgrBean.subjectItemsMap}" >

			<c:if test="${asnForm.subject != subjectItemsMap.key}">
				<%-- <div>${subjectItemsMap.key}</div> --%>
				<c:forEach var="docInfo" items="${subjectItemsMap.value}" >
					<%-- <div>${docId}</div> --%>
					<input type="hidden" value="${docInfo.key}" name="otherSubjectDocs"/>
				</c:forEach>
			</c:if>
		</c:forEach>

		</form>
	</c:when>
	<c:otherwise><h1>stdMgrBean not found for ${asnForm.xmlFormat}</h1></c:otherwise>
</c:choose>

</body>
</html:html>
