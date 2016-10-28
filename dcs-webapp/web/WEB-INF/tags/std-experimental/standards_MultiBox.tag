<%--  casaaContentStandardMulitBox
 
 - collapsible, stateful multibox display supported by StandardsManager (casaa 4th level standards).
 	Hierarchies are collapsible and they retain their state.
		
 - it requires a parameter named elementPath. e.g. here is a call for this tag:
 		<std:dynaStandard_MulitBox elementPath="enumerationValuesOf(/itemRecord/educational/resourceTypes/resourceType)"/>
 	
 - this tag produces a dynamic muli-level display. 
 - The very top level is not displayed, only the lower levels. 
   e.g., in the case of "resourceType" there is no header for "resourceType". The header is displayed by
   Renderer.

 - this tag will be produced by Renderer.getMultiBoxInput for the cases when the xpath is 
   /itemRecord/educational/contentStandards/contentStandard
   
			SuggestedValues is a LIST
			SelectedValues is an ARRAY
--%> 

<script language="JavaScript" src="../lib/stds/stds-support.js"></script>
<script language="JavaScript" src="../lib/stds/tabs.js"></script>
<script language="JavaScript" src="../lib/stds/doc-selector.js"></script>
<link rel="stylesheet" href="../lib/stds/stds-styles.css" type="text/css">

<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib tagdir="/WEB-INF/tags/std" prefix="std" %>
<%@ tag import="edu.ucar.dls.schemedit.display.CollapseBean" %>
<%@ tag import="edu.ucar.dls.schemedit.standards.SuggestionServiceHelper" %>

<%@ attribute name="elementPath" required="true" type="java.lang.String"%>
<%@ attribute name="pathArg" required="true" type="java.lang.String"%>

<c:set var="serviceHelper" value="${sef.suggestionServiceHelper}"/>

<%-- evaluate javascript file with access to formBean --%>
<%@ include file="/lib/stds/display-state-init.jspf" %>

<%-- hidden layers that are shown while we wait for CAT to return suggestions --%>
<div id="blanket" style="display:none"></div>
<div id="spinner" style="display:none">
	<img src="../images/fedora-spinner.gif" height="25px" width="25px" />
	Accessing Suggestion Service ...
</div>

<div id="standards_MultiBox">

<input type="hidden" name="displayContent" value="${serviceHelper.displayContent}"/>
<input type="hidden" name="displayMode" value="${serviceHelper.displayMode}"/>
<input type="hidden" name="${elementPath}" value=""/>

<table><tr>
<c:choose>
	<c:when test="${serviceHelper.serviceIsActive}">
		<td width="500px">
			<std:controls elementPath="${elementPath}" pathArg="${pathArg}"/>
		</td>
	</c:when>
	
	<c:otherwise>
		<td width="400px">
				<%@ include file="/lib/stds/tabs.jspf" %>
		</td>
	</c:otherwise>
</c:choose>
</tr></table>

<div id="standards-display">
	<std:display serviceHelper="${serviceHelper}" elementPath="${elementPath}" />
</div>

</div>
