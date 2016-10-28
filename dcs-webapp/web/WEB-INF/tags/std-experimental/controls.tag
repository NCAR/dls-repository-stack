<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="html" uri="/WEB-INF/tlds/struts-html.tld" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/std" prefix="std" %>
<%@ tag import="edu.ucar.dls.schemedit.standards.SuggestionServiceHelper" %>

<%@ attribute name="elementPath" required="true" type="java.lang.String"%>
<%@ attribute name="pathArg" required="true" type="java.lang.String"%>

<bean:define id="selectedValues" name="sef" property="${elementPath}"/>

<c:set var="serviceHelper" value="${sef.suggestionServiceHelper}"/>

<script type="text/javascript">

Event.observe (window, 'load', function (event) {
	
	if ($('update-suggestions-button'))
		$('update-suggestions-button').observe ('click', doSuggestStandards);
/* 	else
		alert ("update-suggestions-button is not longer used"); */

});

</script>

<%--
	<!-- Input for updating suggestions (now handled via tab) -->
	<input 
		id="update-suggestions-button"
		type="button" 
		class="record-action-button" 
		value="Update Suggestions"/>
							
		<!-- Input for selecting new standards doc (superceded by
		two-level select ...) -->
		<select id="all-doc-selector"></select>
	
 --%>

	<div id="serviceAttributes" style="margin:5px 0px 5px 0px">
	
	<c:if test="${serviceHelper.serviceIsActive}">
	
	<div id="criteria-toggle" class="stds-ui-label">
		Suggestion Criteria <img src="../images/btnExpandClsd.gif" />
	</div>
	<table id="criteria-table" style="display:none" bgcolor="black" cellspacing="1" cellpadding="2" width="100%">
		<tr bgcolor="white">
			<td class="attr-header">Criteria</td>
			<td class="attr-header">Enabled</td>
			<td class="attr-header">Value</td>
		</tr>
		
		<%-- Grade Ranges --%>
		<tr bgcolor="white"  align="center">
			<td class="attr">grade ranges
				<div>
				<input type="button" class="reset-button" value="reset" 
					onclick="updateControl(this, 'gradeRanges');"
					title="Reset the grade range selections to match the values selected for this record"/>
				</div>
			</td>
			<td class="attr">
				<div>
				<input type="checkbox"
						name="useGradeRanges" id="useGradeRanges"
						title="When enabled, limit suggested standards to specified grade range"
						value="true" <c:if test="${serviceHelper.useGradeRanges}">checked</c:if> />
				</div>
			</td>
			<td class="attr" align="center">
				<table cellspacing="0" cellpadding="0" border="0">
					<td width="45%" align="center">
						<div class="attr">start grade</div>
						<select name="gradeRanges" id="startGrade-select"></select>
					</td>
					<td width="10%" align="center">&nbsp;</td>
					<td width="45%" align="center">
						<div class="attr">end grade</div>
						<select name="gradeRanges" id="endGrade-select"></select>
					</td>
				</table>
			</td>
		</tr>
		
		<%-- keywords --%>
		<tr bgcolor="white" align="center">
			<td class="attr">keywords
				<div>
					<input type="button" class="reset-button" value="reset"
						onclick="updateControl(this, 'keywords');"
						title="Reset the keywords field to contain the keywords specified for this record"/>
				</div>
			</td>
			<td class="attr">
				<input type="checkbox"
					name="useKeywords"
					title="When enabled, includes keywords in query to Suggestion service"
					value="true" <c:if test="${serviceHelper.useKeywords}">checked</c:if> />
			</td>
			<td class="attr" align="left">
				<textarea name="keywords" rows="2" 
					cols="40" style="width:100%">${serviceHelper.selectedKeywords}</textarea>
			</td>
		</tr>
		
		<%-- Description --%>
		<tr bgcolor="white"  align="center">
			<td class="attr">description</td>
			<td class="attr" align="center">
				<input type="checkbox"
					title="When enabled, includes content of metadata description field in Suggestion service query"
					name="useDescription"
					value="true" <c:if test="${serviceHelper.useDescription}">checked</c:if> />
			</td>
			<td>&nbsp;</td>
		</tr> 
		
		<%-- URL --%>
 		<tr bgcolor="white" align="center">
			<td class="attr">recordUrl</td>
			<td>&nbsp;</td>
			<td class="attr" align="left"><a href="${serviceHelper.recordUrl}" target="_blank">${serviceHelper.recordUrl}</a></td>
		</tr>
	</table>
	</div>
</c:if>

<%-- Author and topic selectors --%>
<c:if test="${fn:length(serviceHelper.availableDocs) > 1}">
	<%-- hidden var to communicate state with controller --%>
	<html:hidden property="currentStdDocKey" />
	<div class="stds-ui-label">Current Standards Document
		<input class="stds-ui-button" id="current-doc-button" type="button" value="change" />
	</div>
	<div style="padding:0px 0px 5px 0px">
	<div id="current-doc-label">
		<%-- <std:docLabel standardsManager="${serviceHelper.standardsManager}" /> --%>
	</div>
	<table id="current-doc-selector" style="display:none">
		<tr valign="bottom">
			<%-- TOPIC --%>
			<td>
				<div class="attr-header">topic</div>
				<select name="topic-selector" id="topic-selector"></select>
			</td>
			<%-- AUTHOR --%>
			<td>
				<div class="attr-header">author</div>
				<select id="author-selector"></select>
			</td>
			<td>
				<input class="stds-ui-button" id="doc-select-done" type="button" value="done" />
			</td>
			<td>
				<input class="stds-ui-button" id="doc-select-cancel" type="button" value="cancel" />
			</td>
		</tr>
	</table>
	</div>
</c:if>



<%@ include file="/lib/stds/tabs.jspf" %>

<script type="text/javascript">




function getCriteriaTriggers () {
	var criteria = $("criteria-table");
	var triggers = criteria.select ("input").inject ( [], function (array, input) {
		// log ("\t" + input.type);
		if (input.type == "checkbox" || input.type == "radio" || input.type == "button")
			array.push (input);
		return array;
	});
	
	triggers = criteria.select ("select").inject (triggers, function (array, input) {
		array.push (input);
		return array;
	});
	
	return criteria.select ("textarea").inject (triggers, function (array, input) {
			array.push (input);
			return array;
		});
}

function criteriaInit () {
	var toggle = $('criteria-toggle');
	var table = $('criteria-table');

	// toggle click handler
	toggle.observe ('click', function (event) {
		table.toggle();
	});

	toggle.observe ('mouseover', function (event) {
		toggle.setStyle({cursor:'pointer'});
	});

	var triggers = getCriteriaTriggers ();
	
	// fire "ctrl:criteriaChanged" event when any criteria is changed
	triggers.each (function (input) {
		// log ($(input).inspect());
		input.observe ('change', function (event) {
			input.fire ("ctrl:criteriaChanged", event.element().identify());
		});
	});
	
	// inputs popup through blanket in IE, so hide them while blanket is active
	if ((Prototype.Browser.IE)) {
		document.observe ('blanket:activated', function (event) {
			triggers.each (function (input) {
				$(input).hide();
			});
		});
		
		document.observe ('blanket:deactivated', function (event) {
			triggers.each (function (input) {
				$(input).show();
			});
		});
	}
	
}

criteriaInit()


</script>
