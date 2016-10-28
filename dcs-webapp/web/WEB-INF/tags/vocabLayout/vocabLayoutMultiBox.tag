<%--  mdvMultiBoxStatic

 - this is a modified version of resourceTag1.

 - it requires a parameter named elementPath. e.g. here is a call for this tag:
 		<st:mdvMultiBox elementPath="enumerationValuesOf(/itemRecord/educational/resourceTypes/resourceType)"/>

 - this tag produces a static muli-level display.
 - The very top level is not displayed, only the lower levels.
   e.g., in the case of "resourceType" there is no header for "resourceType". The header is displayed by
   Renderer.

 - this tag will be produced by Renderer.getMultiBoxInput for the cases when a MVS mapping can be found for a
   particular xpath

--%>

<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ tag import="edu.ucar.dls.schemedit.display.CollapseBean" %>
<%@ taglib tagdir="/WEB-INF/tags/vocabLayout" prefix="vl" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>

<%@ attribute name="elementPath" required="true" %>

<jsp:setProperty name="sef" property="vocabField" value="${elementPath}" />

<%-- Debugging utility to show vocabLayout file URI --%>
<c:if test="${false}">
	<c:set var="sid" value="${sf:pathToId (elementPath)}" />
	<div style="font-style:italic;font-size:8pt;margin-bottom:5px;">
		<a href="#" onclick="$('${sid}_source').toggle();return false;">-- Vocab Layout --</a>
		<div id="${sid}_source" style="display:none">${sef.vocabLayout.source}</div>
	</div>
</c:if>

<%-- Check for an illegal values (those not in controlled vocab) --%>
<bean:define id="selectedValues" name="sef" property="${elementPath}"/>
<bean:define id="legalValues" name="sef" property="legalEnumerationValuesOf(${sef.vocabLayout.path})" />
<c:set var="bogusValues">
	<c:forEach var="selectedValue" items="${selectedValues}">
		<c:if test="${not empty selectedValue and not sf:listContains (legalValues, selectedValue)}">
			<%-- <span class="element-error-msg">warning: illegal value!</span> --%>
			<c:set var="checkBoxId" value="${sf:pathToId(elementPath)}_${selectedValue}_checkbox" />
			<div>
				<input type="checkbox" id="${checkBoxId}"
					name="${elementPath}"
					value="<c:out value="${selectedValue}"/>" checked />
					<label class="${typeclass}" for="${checkBoxId}"><c:out value="${selectedValue}"/></label>
			</div>
		</c:if>
	</c:forEach>
</c:set>
<c:if test="${not empty bogusValues}">
	<div class="page-error-box">
		<div class="error-msg">ALERT: These values are not allowed for this field</div>
			${bogusValues}
		</div>
</c:if>

<%-- expand / collapse all --%>
<c:if test="${sef.vocabLayout.collapseExpand == 'true'}">
	<st:muiExpanderWidget elementPath="${elementPath}" />
</c:if>

<table class="vocab-layout-table"><td>
	<c:forEach var="layoutNode" items="${sef.vocabLayoutNodes}">
		<vl:vocabTreeNode elementPath="${elementPath}" vocabTerm="${layoutNode}" inputMode="multiple" />
	</c:forEach>
</td></table>

<c:if test="${(sef.vocabLayout.smartCheckBox == 'true')}">
	<c:set var="layoutJson" value="${sef.vocabLayout.json}" />
	<c:if test="${not empty layoutJson}">
		<script type="text/javascript">
				try {
					var layoutJson = ${layoutJson};
					var treeContainer = $("${sf:pathToId (elementPath)}");
					var vocabTree = new SmartCheckBoxTree (layoutJson, treeContainer);
				  // vocabTree.report();
				} catch (error) {
					log ("layoutJson error: " + error);
				}
		</script>
	</c:if>

</c:if>
