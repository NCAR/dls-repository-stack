<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="html" uri="/WEB-INF/tlds/struts-html.tld" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="edu.ucar.dls.schemedit.standards.SuggestionServiceHelper" %>

<%@ attribute name="elementPath" required="true" type="java.lang.String"%>
<%@ attribute name="pathArg" required="true" type="java.lang.String"%>

<bean:define id="selectedValues" name="sef" property="${elementPath}"/>

<c:set var="serviceHelper" value="${sef.suggestionServiceHelper}"/>

	<div id="" style="margin:5px 0px 5px 0px">
	
		<%-- Author and topic selectors --%>
		<c:if test="${fn:length(serviceHelper.availableDocs) > 1}">
			<%-- hidden var to communicate state with controller --%>
			<html:hidden property="currentStdDocKey" />
			
			<table id="doc-selector" bgcolor="black" border="0" cellspacing="1" cellpadding="2" width="100%">
					<tr bgcolor="white">
						<td class="attr-header" colspan="2"><b>Select Standards Document</b></td>
					</tr>
				<tr bgcolor="white">
					<%-- TOPIC --%>
					<td class="attr" width="50%"><b>topic</b>
						<select name="topic-selector" id="topic-selector"></select>
					</td>
					<%-- AUTHOR --%>
					<td class="attr"><b>author</b>
						<select id="author-selector"></select>
					</td>
				</tr>
			</table>
			<br/>
		</c:if>
	
<c:if test="${serviceHelper.serviceIsActive}">
		
	<table id="criteria-table" bgcolor="black" cellspacing="1" cellpadding="2" width="100%">
		<tr bgcolor="white">
			<td class="attr-header" colspan="3"><b>Suggestion Criteria</b></td>
		</tr>
		<tr bgcolor="white">
			<td class="attr-header">Criteria</td>
			<td class="attr-header">Enabled</td>
			<td class="attr-header">Value</td>
		</tr>
		
		<%-- GRADE RANGES --%>
		<tr id="cat-grade-ranges" style="display:none" bgcolor="white"  align="center">
			<td class="attr">grade ranges
				<div>
				<input type="button" id="gr-reset-button" class="reset-button" value="reset" 
					title="Reset the grade range selections to match the metadata values for this record"/>
				</div>
			</td>
			<td class="attr">
				<div>
				<input type="checkbox"
						name="useGradeRanges" id="gr-enabled-box"
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
		
		<%-- KEYWORDS --%>
		<tr  id="cat-keywords" bgcolor="white" align="center">
			<td class="attr">keywords
				<div>
					<input type="button" id="keyword-reset-button" class="reset-button" value="reset"
						title="Reset the keywords field to contain the keywords for this metadata record"/>
				</div>
			</td>
			<td class="attr">
				<input type="checkbox"
					name="useKeywords" id="keywords-enabled-box"
					title="When enabled, includes keywords in query to Suggestion service"
					value="true" <c:if test="${serviceHelper.useKeywords}">checked</c:if> />
			</td>
			<td class="attr" align="left">
				<%-- value set in KeywordsController --%>
				<textarea name="keywords" id="keywords" rows="2" 
					cols="40" style="width:100%">${serviceHelper.selectedKeywords}</textarea> 
			</td>
		</tr>
		
		<%-- SUBJECTS --%>
		<tr  id="cat-subjects" style="display:none" bgcolor="white" align="center">
			<td class="attr">subjects</td>
			<td class="attr">
				<input type="checkbox"
					name="useSubjects" id="subjects-enabled-box"
					title="When enabled, includes subjects in query to Suggestion service"
					value="true" <c:if test="${serviceHelper.useSubjects}">checked</c:if> />
			</td>
			<td class="attr" align="left">
				<%-- show value as comma-delimited list --%>
<%-- 			<textarea name="subjects" id="subjects" rows="2" 
					cols="40" style="width:100%">${fn:join (serviceHelper.recordSubjects, ', ')}</textarea>  --%>
					
					<%-- show value as individual rows --%>
<%-- 			<c:forEach var="subject" items="${serviceHelper.recordSubjects}" varStatus="i">
						<div>${subject}</div>
					</c:forEach> --%>
					
				<c:choose>
					<c:when test="${not empty serviceHelper.recordSubjects}">
						Using subjects currently specified in record  
					</c:when>
					<c:otherwise>
						<i>There are no subjects currently specified in this record</i>
					</c:otherwise>
				</c:choose>
			</td>					
			</td>
		</tr>
		
		<%-- Description --%>
		<tr id="cat-description" bgcolor="white"  align="center">
			<td class="attr">description</td>
			<td class="attr" align="center">
				<input type="checkbox"
					title="When enabled, includes content of metadata description field in Suggestion service query"
					name="useDescription"
					value="true" <c:if test="${serviceHelper.useDescription}">checked</c:if> />
			</td>
			<td class="attr" align="left">
				<c:choose>
					<c:when test="${not empty fn:trim(serviceHelper.recordDescription)}">
						Using current value of description field
					</c:when>
					<c:otherwise>
						<i>There is no current value for description field</i>
					</c:otherwise>
				</c:choose>
			</td>
		</tr> 
		
		<%-- URL --%>
 		<tr id="cat-url" bgcolor="white" align="center">
			<td class="attr">resource Url</td>
			<td>&nbsp;</td>
			<td class="attr" align="left"><a href="${serviceHelper.recordUrl}" target="_blank">${serviceHelper.recordUrl}</a></td>
		</tr>
	</table>
	</div>
</c:if>

<%@ include file="/lib/stds/tabs.jspf" %>

<script type="text/javascript">
function getCriteriaTriggers () {
	var criteria = $("criteria-table");
	var triggers = criteria.select ("input").inject ( [], function (array, input) {
		// log ("\t" + input.type + ": " + input.identify());
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
	if (toggle) {
		toggle.observe ('click', function (event) {
			table.toggle();
		});
	
		toggle.observe ('mouseover', function (event) {
			toggle.setStyle({cursor:'pointer'});
		});
	}

	var triggers = getCriteriaTriggers ();
	
	// fire "ctrl:criteriaChanged" event when any criteria is changed
	triggers.each (function (input) {
		var event_name = input.type == 'button' ? 'click' : 'change';
		input.observe (event_name, function (event) {
			log ("sending 'ctrl:criteriaChanged' for " + event.element().identify());
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
	
	
	/* initial state of the keywords is assigned at load-time. here we
	assign listeners?
	*/
	var keywords_box = $('keywords-enabled-box');
	keywords_box.observe ('change', function (event) {
		keywords_box.fire ("ctrl:useKeywordsChanged");
		log ("ctrl:useKeywordsChanged FIRED");
	});
	
	var subjects_box = $('subjects-enabled-box');
	subjects_box.observe ('change', function (event) {
		subjects_box.fire ("ctrl:useSubjectsChanged");
		log ("ctrl:useSubjectsChanged FIRED");
	});
	
	var gr_box = $('gr-enabled-box');
	gr_box.observe ('change', function (event) {
		gr_box.fire ("ctrl:useGradeRangesChanged");
		log ("ctrl:useGradeRangesChanged FIRED");
	});
	
}


if ('${serviceHelper.serviceIsActive}' == 'true') {
	criteriaInit();
}


</script>
