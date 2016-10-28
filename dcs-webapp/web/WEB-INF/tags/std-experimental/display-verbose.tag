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
   
			SuggestedStandards is a LIST
			SelectedStandards is an ARRAY
--%> 

<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib tagdir="/WEB-INF/tags/std" prefix="std" %>
<%@ tag import="edu.ucar.dls.schemedit.standards.SuggestionServiceHelper" %>

<%@ attribute name="elementPath" required="true" type="java.lang.String"%>
<%@ attribute name="serviceHelper" required="true" type="edu.ucar.dls.schemedit.standards.SuggestionServiceHelper"%>

<c:set var="suggestedStandards" value="${serviceHelper.suggestedStandards}"/>
<c:set var="selectedStandards" value="${serviceHelper.selectedStandards}"/>

<c:if test="${serviceHelper.standardsFormat == 'asn'}">
 <h4><std:docLabel standardsManager="${serviceHelper.standardsManager}" /></h4>
</c:if>

<div style="padding:10px;border:blue thin solid">
	<div>DisplayMode: ${serviceHelper.displayMode}</div>
	<div>DisplayContent: ${serviceHelper.displayContent}</div>
</div>

<%-- TREE View --%>
<c:if test="${serviceHelper.displayMode == 'tree'}">
	<div class="list-explanation">The Entire Content Standards Hierarchy</div>
	<table><td valign="top">
	
		<!-- children of root -->
		<c:forEach var="stdNode" items="${serviceHelper.rootStandardNode.subList}">
			<std:treeNode stdNode="${stdNode}" elementPath="${elementPath}" />
		</c:forEach> 
	</td></table>
	
	<%-- Other selected standards as hidden inputs! --%>
	<h4>other selected</h4>
	<c:forEach var="doc" items="${serviceHelper.otherSelectedStandards}">
		<div>${doc.key}</div>
		<c:forEach var="std" items="${doc.value}">
			<div style="margin-left:15px">${std.id}</div>
			<input type="hidden" name="${elementPath}" value="${std.id}" />
		</c:forEach>
	</c:forEach>
</c:if>


<c:if test="${serviceHelper.displayMode == 'list'}">

	<%-- Selected Standards --%>
	<c:if test="${serviceHelper.displayContent == 'selected'}">
		<c:choose>
			<c:when test="${not empty selectedStandards}">
				<div class="list-explanation">List Ordered according to Standards Hierarchy</div>
				<c:forEach var="stdNode" items="${serviceHelper.standardsNodes}">
					<c:set var="stdIsSuggested" value="${sf:listContains (suggestedStandards, stdNode.id)}"/>
					<c:if test="${sf:listContains(selectedStandards, stdNode.id)}">
						<std:checkbox 
							stdNode="${stdNode}" 
							stdIsSuggested="${stdIsSuggested}"
							stdIsSelected="${true}"
							elementPath="${elementPath}" 
							dleseId="${serviceHelper.standardsFormat == 'dlese'}"/>
					</c:if>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<div><i>No standards are selected</i></div>
			</c:otherwise>
		</c:choose>
		
		<%-- SelectedStandards from other docs --%>
		<c:if test="${not empty serviceHelper.otherSelectedStandards}">
			<c:set var="selectedStdsBean" value="${serviceHelper.selectedStandardsBean}" />
			<div id="other-toggle" 
				 style="background-color:#CCCEE6;padding:2px;font-weight:bold;" >
				<img id="other-toggle_img" border="0" hspace="3" height="12" width="12" src="../images/closed.gif" />
				Selected standards from other standards documents</div>
			<div id="other-selected-standards" style="display:none;margin:0px 0px 0px 15px">
				<c:forEach var="doc" items="${serviceHelper.otherSelectedStandards}">
					<jsp:setProperty name="selectedStdsBean" property="docKey" value="${doc.key}" />
					<h4><std:docInfoLabel docInfo="${selectedStdsBean.docInfo}" /></h4>
					<c:forEach var="stdNode" items="${doc.value}">
						<std:checkbox 
							stdNode="${stdNode}" 
							stdIsSuggested="${false}"
							stdIsSelected="${true}"
							elementPath="${elementPath}"/>
					</c:forEach>
				</c:forEach>
			</div>
		</c:if>
	</c:if>
	
	<%-- Suggested Standards --%>
	<c:if test="${serviceHelper.displayContent == 'suggested'}">
		<c:choose>
			<c:when test="${not empty suggestedStandards}">
				<%-- display suggestions in order! --%>
				<div class="list-explanation">List Ordered with Most Relevant Suggestions at Top</div>
				
				<%-- we have to account for ALL the SELECTED nodes (see below) --%>
				
				<%-- First the SUGGESTED nodes --%>
				<c:forEach var="stdNode" items="${serviceHelper.standardsNodes}">
					<c:if test="${sf:listContains (suggestedStandards, stdNode.id)}">
						<c:set var="stdIsSelected" value="${sf:listContains(selectedStandards, stdNode.id)}" />
							<std:checkbox 
								stdNode="${stdNode}" 
								stdIsSuggested="${true}"
								stdIsSelected="${stdIsSelected}"
								elementPath="${elementPath}" 
								dleseId="${serviceHelper.standardsFormat == 'dlese'}"/>
					</c:if>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<div><i>No standards were suggested</i></div>
			</c:otherwise>
		</c:choose>
		
		<%-- hidden inputs for standards from current doc that are selected but not shown as suggested
				 so these values are not lost --%>
				 
		<h4>Selected std that are NOT shown as suggested</h4>
		<c:forEach var="std" items="${selectedStandards}">
			<c:if test="${not sf:listContains (suggestedStandards, std)}">
				<div>${std}</div>
				<input type="hidden" name="${elementPath}" value="${std}" />
			</c:if>
		</c:forEach>
	</c:if>
	
</c:if>

<%-- <script type="text/javascript">//<!--

	// activate other standards link
	var otherStdsToggle = $('other-toggle');
	if (otherStdsToggle) {
		otherStdsToggle.observe ('click', toggleOtherSelectedStandards);
		otherStdsToggle.observe ('mouseover', function (event) {
			otherStdsToggle.setStyle ({cursor:'pointer'});
		});
	}

	if (!Prototype.Browser.IE) {
		// initializeSuggestionBoxes
		try {
			var boxes = $('standards-display').select('div.suggestion-box');
			boxes.each ( function (box) {
				new StandardsBox (box);
			});
		} catch (error) { alert ("display init error: " + error); }
	}
//-->
</script> --%>

