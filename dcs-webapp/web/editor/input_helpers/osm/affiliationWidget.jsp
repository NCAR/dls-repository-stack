<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<%-- <div style="margin-left:25px">
	<span style="width:200px;display:block;text-align:center;margin:25px 0px 0px 0px;padding:5px;border:purple 2px outset;background-color:#cbbecc;font-size:14pt;font-weight:bold;margin-top:5px;">
		test_widget! 
		<input type="button" value="click me" id="${id}_button" />
	</span>
	<div style="font-size:10px;margin-top:3px">xpath: ${sf:idToPath (id)}</div>
	<div style="font-size:10px;margin-top:3px">id: ${id}</div>
	<div style="font-size:10px;margin-top:3px">contextPath: ${contextPath}</div>
	
</div> --%>

<!-- <div?<span style="border:thin blue solid;background-color:yellow;padding:3px;">TEST WIDGET</span></div> -->

<input type="button" value="Affiliation Helper" id="${id}_button" />
<div id="${id}_msg"></div>
<script type="text/javascript"  src="${contextPath}/editor/input_helpers/osm/affiliationHelper.js"></script>
<script type="text/javascript">

var awError = null;
<c:set var="renderer" value="${sef.framework.renderer}"/>

/* log ("AFFILIATION_WIDGET (${id})");
log ("  xmlFormat: ${sef.xmlFormat}");
log ("  renderer: ${renderer}"); */

if ("${renderer}" != "OsmEditorRenderer") {
	awError = '<div class=\"error-msg\"><span class=\"element-error-msg\">Configuration Error!</span><br />\n';
	awError += 'OsmEditorRender is necessary for the affiliation widget.<br/>';
	awError += '"${renderer}\" is currently configured (see framework configuration).</div>';
}

document.observe ("dom:loaded", function () {
	if (awError) {
		$('${id}_msg').update (awError);
		$('${id}_msg').addClassName ("error-msg");
		$('${id}_button').disable();
	}
	else {
		var helper = new AffiliationHelper ("${id}", "${sf:pathToId ('/')}");
		$('${id}_button').observe ('click', helper.openAffiliationTool.bind(helper));
	}
});

</script>
