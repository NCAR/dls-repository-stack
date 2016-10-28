<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ tag import="edu.ucar.dls.schemedit.display.CollapseBean" %>
<%@ taglib tagdir="/WEB-INF/tags/vocabLayout" prefix="vl" %>

<%@ attribute name="elementPath" required="true" %>
<%@ attribute name="vocabTerm" required="true" type="edu.ucar.dls.schemedit.vocab.layout.LayoutNode"%>
<%@ attribute name="inputMode" required="true"%>

<bean:define id="collapseBean" name="sef" property="collapseBean" type="CollapseBean" />
<bean:define id="selectedValues" name="sef" property="${elementPath}"/>

<c:set var="vocabId" value="${sf:pairToId(elementPath,vocabTerm.label)}" scope="page" />

<%-- renderedHeader is the visual representation of the header. possibly a check box --%>
<c:set var="renderedHeader">
	<c:choose>
		<c:when test="${not empty vocabTerm.name and inputMode == 'multiple'}">
			<c:set var="checkBoxId" value="${sf:pathToId(elementPath)}_${vocabTerm.name}_checkbox" />
			<div class="selectable-header-label ${vocabTerm.collapsible ? 'collapsible' : ''}">
				<input type="checkbox" id="${checkBoxId}"
					name="${elementPath}"
					value="<c:out value="${vocabTerm.name}"/>"
					<c:if test="${sf:arrayContains (selectedValues, vocabTerm.name)}">checked</c:if>		
				/><label class="typegroup"
					<c:if test="${not empty vocabTerm.definition}">title="${vocabTerm.definition}"</c:if>
					for="${checkBoxId}">${vocabTerm.label}</label>
			</div>
		</c:when>
		<c:when test="${not empty vocabTerm.name and inputMode == 'single'}">
			<%-- for single mode, selectedValues contains the selectedValue (not a list) --%>
			<c:set var="radioId" value="${sf:pathToId(elementPath)}_${selectedValues}_id" />
			<div class="selectable-header-label ${vocabTerm.collapsible ? 'collapsible' : ''}">
				<input type="radio" id="${radioId}"
					name="${elementPath}"
					value="<c:out value="${vocabTerm.name}"/>"
					<c:if test="${ selectedValues == vocabTerm.name }">checked</c:if> 	
				/><label class="typegroup"
					<c:if test="${not empty vocabTerm.definition}">title="${vocabTerm.definition}"</c:if>
					for="${radioId}">${vocabTerm.label}</label>
			</div>
		</c:when>
		<c:otherwise>
			<span class="typegroup"
				  <c:if test="${not empty vocabTerm.definition}">title="${vocabTerm.definition}"</c:if>
			>${vocabTerm.label}</span>
		</c:otherwise>
	</c:choose>
</c:set>
	
<%-- add collapse/expand icon if this header is collapsible --%>
<c:choose>
	<c:when test="${vocabTerm.collapsible}" >
		<jsp:setProperty name="collapseBean" property="id" value="${vocabId}" />
		<c:set var="myDisplayState" value="${sef.collapseBean.displayState}" />
		<input type="hidden" name="${vocabId}_displayState" id="${vocabId}_displayState" value="${sef.collapseBean.displayState}" />
		<div class="selectable-collapsible-wrapper">
			<a href="javascript:toggleDisplayState('${vocabId}');"
				title="Click to open/close"><img id="${vocabId}_img" 
				class="collapse-widget"
				src="${sef.collapseBean.isOpen ? '../images/opened.gif' : '../images/closed.gif'}" 
				alt="Click to show/hide"></a>${renderedHeader}
		</div>
	</c:when>
	<c:otherwise>
		<c:set var="myDisplayState" value="block" />
		<div class="plain-header">${renderedHeader}</div>
	</c:otherwise>
</c:choose>

<div class= "sublist-wrapper" id="${vocabId}"style="display:${myDisplayState};">
	<%-- Use 97% width to prevent deep hierarchical displays from clipping in IE --%>
	<table width="97%" cellspacing="0" cellpadding="0"><td valign="top">
	<c:forEach var="subListItem" items="${vocabTerm.subList}">
		<vl:vocabTreeNode elementPath="${elementPath}" vocabTerm="${subListItem}" inputMode="${inputMode}" />
	</c:forEach>
	</td></table>
</div>
		
