<%--  standards_display.tag
	just like std/display.tag only customized for res_qual use
	
	THIS jsp updates the page state
	- suggestedStandards
	- selectedStandards
	- creates hidden var for selected standards (since these are not de-selectable in the tree view)
	
	but only displays the tree view. if the display calls for "selected", then this is taken care of
	in a subsequent AJAX call.
	
--%> 

<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib tagdir="/WEB-INF/tags/std" prefix="std" %>
<%@ taglib tagdir="/WEB-INF/tags/res_qual" prefix="res_qual" %>
<%@ tag import="edu.ucar.dls.schemedit.standards.SuggestionServiceHelper" %>

<%@ attribute name="elementPath" required="true" type="java.lang.String"%>
<%@ attribute name="serviceHelper" required="true" type="edu.ucar.dls.schemedit.standards.SuggestionServiceHelper"%>

<c:set var="suggestedStandards" value="${serviceHelper.suggestedStandards}"/>
<c:set var="selectedStandards" value="${serviceHelper.selectedStandards}"/>
		
<%-- DEBUGGING --%>
<%-- <div style="border:black solid 1px;padding:5px;background-color:#006600;color:white;text-align:center;font-size:12px;width:50%;margin-left:75px">
	I am res_qual standards display
</div> --%>

<c:if test="${serviceHelper.displayMode == 'tree'}">
	<c:if test="${serviceHelper.standardsFormat == 'asn'}">
	 <h4><std:docLabel standardsDocument="${serviceHelper.standardsDocument}" /></h4>
	</c:if>
</c:if>

<%-- 		
	REQUIRED!! - we don't allow selected standards to be de-selected for ANY of the displays. this is accomplished by
	disabling the checkboxes, which means we need hidden inputs to preserve the selected standards 
--%>
<c:forEach var="std" items="${selectedStandards}">
	<input type="hidden" name="${elementPath}" value="${std}" />
</c:forEach>

<%-- DEBUGING ONLY - show selected standards --%>
<%-- <div style="border:#006600 thin solid;margin:5px;padding:5px;">
<c:forEach var="std" items="${selectedStandards}">
	<div style="font-size:10px">${std}</div>
</c:forEach>
</div> --%>

<%-- TREE View --%>
<c:if test="${serviceHelper.displayMode == 'tree'}">
	<div class="list-explanation">The Entire Content Standards Hierarchy</div>
	<table><td valign="top">
	
		<!-- children of root -->
		<c:forEach var="stdNode" items="${serviceHelper.rootStandardNode.subList}">
			<res_qual:standards_treeNode stdNode="${stdNode}" elementPath="${elementPath}" />
		</c:forEach> 
	</td></table>
	
</c:if>


<c:if test="${serviceHelper.displayMode == 'list'}">

	<%-- Selected Standards NOW HANDLED ELSEWHERE
		this is populated by (ResQual | Lan)DisplayController.update
	
	--%>

	
	<%-- Suggested Standards --%>
	<%-- NOTE - This probably does not work for res_qual or lar --%>
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
							<res_qual:standards_checkbox 
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
		
	</c:if>
	
</c:if>


