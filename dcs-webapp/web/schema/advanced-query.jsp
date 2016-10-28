<%@ include file="/JSTLTagLibIncludes.jsp" %>
<jsp:useBean id="svf" class="edu.ucar.dls.xml.schema.action.form.SchemaViewerForm"  scope="session"/>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglibs-application.tld" prefix="app" %>
<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> --%>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title" value="Advanced Query" />

<html>
<head>
	<title>${title}</title>
	<link rel="stylesheet" href="query-styles.css" type="text/css">
	<script type="text/javascript" src="${contextPath}/lib/javascript/prototype.js"></script>
	<script type="text/javascript" src="${contextPath}/lib/javascript/effects.js"></script>
	<script type="text/javascript" src="${contextPath}/lib/javascript/ncsGlobals.js"></script>
<script type="text/javaScript" >
	
function pageInit () {
	$("go-button").observe ('click', function () {
		var params = $('query-form').serialize(true);
		// params.update ({'command','search'});
		params['command'] = 'search';
		var href = '${contextPath}/schema/query.do?' + $H(params).toQueryString();
		log ('href: ' + href);
		window.location = href;
	})

	// if there is a query string, parse it and load form
	var queryString = window.location.search;
	log ('queryString: ' + queryString);
	try {
		intializeQueryFields();
	} catch (error) {
		log ("error: " + error);
		}
}	

function intializeQueryFields () {
log ('intializeQueryFields()');
	var queryString = window.location.search;
	if (!queryString) {
		stuffQueryFields() ; // DEBUGGING
		return;
		}
	var params = $H(queryString.toQueryParams())
	params.each ( function (pair) {
		var field = pair.key;
		log (" - field: " + field)
		if (field.startsWith ("field_")) {
			var num = field.substr("field_".length);
			log ("num: " + num);
			$('value_'+num).value = params.get('value_'+num);
			setRadio ('match_'+num, params.get('match_'+num) || 'exact');
		}
	});
}

/* for debugging */
function stuffQueryFields () {
	$('field_1').value = "/record/general/keyword";
	$('value_1').value = "Astrophysics - Solar and Stellar Astrophysics"
	setRadio ('match_1', 'exact')

	$('field_2').value = "/record/general/abstract";
	$('value_2').value = "evolution"
	setRadio ('match_2', 'partial')
}

function setRadio (name, value) {
		$('query-form').getInputs('radio', name).each (function (button) {
			log ("radio: " + button.identify());
			button.checked = (button.value == value);
	});
}

function log (s) {
	if (window.console)
		console.log (s);
}
	
document.observe ('dom:loaded', pageInit);
	
</script>
	
	
</head>

<h1>${title}</h1>

<c:if test="${false}">
	<div style="border:thin blue solid;padding:5px;font-size:10pt;">
		<div><b>Params</b></div>
		<c:forEach var="pair" items="${param}">
			<div><b>${pair.key}:</b> ${pair.value}</div>
		</c:forEach>
	</div>
</c:if>

<form id="query-form">




<div class="query-clause">
	<div>
		<span class="input-label">field</span>
		<input type="text" id="field_1" name="field_1" value="/record/general/keyword" size="50"/>
		</div>
		
		<div><span class="input-label">value</span>
		<input type="text" id="value_1" name="value_1" value="Astrophysics - Solar and Stellar Astrophysics" size="50"/>
	</div>

	<input type="radio" id="match_1_exact" name="match_1" value="exact" checked="1" />
	<label for="match_1_exact" class="input-label">exact</label>
	
	<input type="radio" id="match_1_partial" name="match_1" value="partial" />
	<label for="match_1_partial" class="input-label">partial</label>
</div>

<div class="query-clause">
	<div>
		<span class="input-label">field</span>
		<input type="text" id="field_2" name="field_2" value="/record/general/abstract" size="50"/>
		</div>
		
		<div><span class="input-label">value</span>
		<input type="text" id="value_2" name="value_2" value="evolution" size="50"/>
	</div>
	<input type="radio" id="match_2_exact" name="match_2" value="exact" />
	<label for="match_2_exact" class="input-label">exact</label>
	
	<input type="radio" id="match_2_partial" name="match_2" value="partial" checked="1" />
	<label for="match_2_partial" class="input-label">partial</label>
</div>

<input type="button" id="go-button" value="go" />
</form>

<div>${aqf.numResults} Results</div>
<div id="results"></div>

</body>
</html>
