<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.SetInfo" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Create Record</c:set>

<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>

<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<%@ include file="/baseHTMLIncludes.jsp" %>
<link rel="stylesheet" href="${contextPath}/lib/autoform-styles.css" type="text/css">
<script type="text/javascript">

var SetMap = $H();

var SetInfo = Class.create ({
	initialize: function (name, spec) {
		this.name = name;
		this.spec = spec;
		}
});

function initializeSetMap () {
	var val;
	var key;
	<c:forEach var="entry" items="${sif.setMap}">
		<c:if test="${not empty entry.value}">
				key = '${entry.key}';
				val = $A()
		
			<c:forEach var="set" items="${entry.value}">
				// log ("${entry.key}" + ": " +  "${set.setSpec}");
				val.push (new SetInfo ('${set.name}', '${set.setSpec}'))
			</c:forEach>
			SetMap.set (key, val);
		</c:if>
	</c:forEach>
}

function updateSetSelect (event) {
	var xmlFormat = (event ? event.element().value : null);
	var setSelect = $('set-select');
	var currentSet = $F(setSelect);
	// log ("current: " + currentSet);
	setSelect.update();
	setSelect.options[setSelect.options.length] = new Option ("-- select collection --" , "", 1);
	
	// when no xmlFormat is selected, show all collections
	if (!xmlFormat) {
		// alert ("no xmlFormat provided");
		SetMap.each (function (pair) {
			pair.value.each ( function (setInfo) {
				var selected = (currentSet == setInfo.spec);
				setSelect.options[setSelect.options.length] = new Option (setInfo.name, setInfo.spec, selected);
			});
		});
	}
	else {
		var setInfos = SetMap.get (xmlFormat);
		if (!setInfos) {
			alert ("no setInfos found for " + xmlFormat);
			return;
		}
		setInfos.each ( function (setInfo) {
			var selected = (currentSet == setInfo.spec);
			setSelect.options[setSelect.options.length] = new Option (setInfo.name, setInfo.spec, selected);
		});
	}

}

function pageInit () {
	/* $('insert-button').observe ('click', insertRef); */
	$$("a.refId").each ( function (refId) {
		refId.observe ('click', insertRef);
	});
	
	$('sif-form').observe ('submit', doSubmit);

	$('type-select').observe ('change', updateSetSelect)
	
	initializeSetMap ();
	
	// debugging
/* 	SetMap.each ( function (pair) {
		log (pair.key + ": " + pair.value.inject(  [], function (array, set) {
			array.push (set.name);
			return array;
		}).join (', '));
	}); */
	
	updateSetSelect();
}

function doSubmit (event) {

	// event.stop();
	
	if ($F('set-select') == "") {
		alert ("Please select a collection in which to create a new record")
		$('set-select').focus();
		event.stop();
		return;
	}
	
/* 	$('sif-form').getElements().each ( function (e) {
		log (e.name + ": " + e.value);
		}); */
		
	$('sif-form').submit;
	
}

Event.observe (window, 'load', pageInit);
</script>
</head>
<body>
<%--  --%>
<%@ include file="pageHeader.jspf" %>
<h3>Create SIF Object</h3>

<div>
	<html:form styleId="sif-form" action="editor/sif.do">
	
<table border="1">
	<tr valign="top">
		<td>
			Object Type
		</td>
		<td>
			<html:select styleId="type-select" property="selectedType">
				<html:optionsCollection property="typeOptions" />
			</html:select>
		</td>
	</tr>
	<tr valign="top">
		<td>
			Collection
		</td>
		<td>
			<html:select styleId="set-select" property="collection">
			</html:select>
		</td>
	</tr>
	<tr>
		<td>
			New Object Title
		</td>
		<td>
			<html:text property="title" size="50" />
		</td>
	</tr>
<%-- 	<tr>
		<td>
			Description
		</td>
		<td>
			<html:textarea property="description" rows="5" />
		</td>
	</tr> --%>

</table>
		<html:hidden property="elementId" />
		<c:forEach var="sifType" items="${sif.sifTypes}">
			<input type="hidden" name="sifTypes" value="${sifType}" />
		</c:forEach>
		<html:submit property="command" value="Create Object"/>
	</html:form>
</div>




</body>

</html:html>
