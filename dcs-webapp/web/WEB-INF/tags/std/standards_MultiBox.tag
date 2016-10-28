<%--  casaaContentStandardMulitBox
 
 - collapsible, stateful multibox display supported by StandardsManager (casaa 4th level standards).
 	Hierarchies are collapsible and they retain their state.
		
 - it requires a parameter named elementPath. e.g. here is a call for this tag:
 		<std:dynaStandard_MulitBox elementPath="enumerationValuesOf(/itemRecord/educational/resourceTypes/resourceType)"/>
 

 - this tag will be produced by Renderer.getMultiBoxInput for the cases when the xpath is 
   associated with a StandardsManager
   
			SuggestedValues is a LIST
			SelectedValues is an ARRAY
--%> 

<%-- Load JAVASCRIPT to instantiate classes for display-controller --%>
<script type="text/javascript" src="../lib/stds/stds-support.js"></script>
<script type="text/javascript" src="../lib/stds/display-controller.js"></script>
<script type="text/javascript" src="../lib/stds/doc-selector.js"></script>
<link rel="stylesheet" href="../lib/stds/stds-styles.css" type="text/css">

<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib tagdir="/WEB-INF/tags/std" prefix="std" %>
<%@ taglib tagdir="/WEB-INF/tags/res_qual" prefix="res_qual" %>
<%@ tag import="edu.ucar.dls.schemedit.display.CollapseBean" %>
<%@ tag import="edu.ucar.dls.schemedit.standards.SuggestionServiceHelper" %>

<%@ attribute name="elementPath" required="true" type="java.lang.String"%>
<%@ attribute name="pathArg" required="true" type="java.lang.String"%>

<c:set var="serviceHelper" value="${sef.suggestionServiceHelper}"/>

<%-- evaluate javascript file with access to formBean --%>
<%@ include file="/lib/stds/display-state-init.jspf" %>

<div id="standards_MultiBox">

<input type="hidden" name="displayContent" value="${serviceHelper.displayContent}"/>
<input type="hidden" name="displayMode" value="${serviceHelper.displayMode}"/>
<input type="hidden" name="${elementPath}" value=""/>

<st:fieldPrompt pathArg="${elementPath}" />

<c:choose>
	<c:when test="${empty serviceHelper.availableDocs}">
		<div class="page-error-box">
			<div class="error-msg"><b>ASN Standards Configuration ERROR:</b> there are no ASN Standards Documents
				available to choose from. Please notify your NCS administrator to update the ASN Standards Service
				configuration for the <b>${sef.xmlFormat}</b> framework.</div>
		</div>
	</c:when>
	
	<c:otherwise>

<table><tr>
		<td width="500px">
			<std:controls elementPath="${elementPath}" pathArg="${pathArg}"/>
		</td>
</tr></table>

<div id="standards-display">
	<c:choose>
		<c:when test="${serviceHelper.xmlFormat == 'res_qual' ||
				serviceHelper.xmlFormat == 'lar' ||
				serviceHelper.xmlFormat == 'comm_anno' ||
				serviceHelper.xmlFormat == 'cow_item'}">
			<res_qual:standards_display serviceHelper="${serviceHelper}" elementPath="${elementPath}" />
		</c:when>
		
		<c:otherwise>
			<std:display serviceHelper="${serviceHelper}" elementPath="${elementPath}" />
		</c:otherwise>
	</c:choose>
</div>

</c:otherwise>
</c:choose>

</div>
