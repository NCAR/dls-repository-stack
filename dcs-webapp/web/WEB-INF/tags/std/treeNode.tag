<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib tagdir="/WEB-INF/tags/std" prefix="std" %>
<%@ tag import="edu.ucar.dls.schemedit.display.CollapseBean" %>
<%@ tag import="edu.ucar.dls.schemedit.standards.SuggestionServiceHelper" %>

<%@ attribute name="stdNode" required="true" type="edu.ucar.dls.schemedit.standards.StandardsNode"%>
<%@ attribute name="elementPath" required="true" type="java.lang.String"%>

<%-- evaluate the currently selected values at elementPath and assign to selectedValues param --%>
<bean:define id="selectedValues" name="sef" property="${elementPath}"/>

<c:set var="serviceHelper" value="${sef.suggestionServiceHelper}"/>
<c:set var="suggestedValues" value="${serviceHelper.suggestedStandards}"/>

<bean:define id="collapseBean" name="sef" property="collapseBean" type="CollapseBean" />

<c:set var="stdIsSuggested" value="${sf:listContains (suggestedValues, stdNode.id)}"/>
<c:set var="stdIsSelected" value="${sf:arrayContains(selectedValues, stdNode.id)}" />
<c:if test="${ !stdNode.noDisplay }">
	<c:choose>
		<c:when test="${ stdNode.isLeafNode }">  <%-- leaf node - make a checkbox --%>
			<std:checkbox 
				stdNode="${stdNode}" 
				stdIsSuggested="${stdIsSuggested}"
				stdIsSelected="${stdIsSelected}"
				elementPath="${elementPath}" />
		</c:when>
		<c:when test="${ stdNode.hasSubList }">

			<%-- define variable to hold the key/id for this element --%>
			<c:set var="id" value="${sf:pairToId(elementPath,stdNode.id)}" scope="page" />
			
			<%-- use jsp:setProperty to set the id prop of collapseBean prior to accessing state --%>
			<jsp:setProperty name="collapseBean" property="id" value="${id}" />
			
			<%-- set hidden var to currentState --%>
			<%-- OR here we could choose to set the display state depending on whether we want to hide
			or show this subList ... --%>
			<input type="hidden" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
			
			<%-- here is the Collapsible heading --%>
			<div style="margin:0px; padding:0px;"
				<c:if test="${not empty stdNode.definition}">title="${stdNode.definition}"</c:if>
				<c:if test="${ empty stdNode.definition}">title="Click to open/close"</c:if>
			><a href="javascript:toggleDisplayState('${id}');"
				class="vocabHeading"><c:choose>
					<c:when test="${sef.collapseBean.isOpen}"><img id="${id}_img"
						src='../images/opened.gif' alt="Click to show/hide" border="0" hspace="5" 
						width="11" hight="11"></c:when>
					<c:otherwise><img id="${id}_img"
						src='../images/closed.gif' alt="Click to show/hide" border="0" hspace="5" 
						width="11" hight="11"></c:otherwise></c:choose>
				 <b>${stdNode.label}</b></a>
			</div>
			
			<%-- Children --%>
			<div style="margin-left:15px;display:${sef.collapseBean.displayState}" id="${id}">
				<c:forEach var="childNode" items="${stdNode.subList}" >
					<std:treeNode stdNode="${childNode}" elementPath="${elementPath}" />
				</c:forEach>
			</div>

		</c:when>
	</c:choose>
</c:if>

