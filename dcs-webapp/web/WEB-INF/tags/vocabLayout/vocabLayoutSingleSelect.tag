<%--  singleSelect
 
		
 - it requires a parameter named elementPath. e.g. here is a call for this tag:
 		<st:vocabLayoutSingleSelect elementPath="enumerationValuesOf(/itemRecord/educational/resourceTypes/resourceType)"/>
 	
 - this tag produces a static multi-level display. 
 - The very top level is not displayed, only the lower levels. 
   e.g., in the case of "resourceType" there is no header for "resourceType". The header is displayed by
   Renderer.

 - this tag will be produced by Renderer.getSelectInput when a vocabLayout is found for a 
   given xpath
   
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

<bean:define id="selectedValue" name="sef" property="${elementPath}"/>

<%-- Debugging utility to show vocabLayout file URI --%>
<c:if test="${false}">
	<c:set var="sid" value="${sf:pathToId (elementPath)}" />
	<div style="font-style:italic;font-size:8pt;margin-bottom:5px;">
		<a href="#" onclick="$('${sid}_source').toggle();return false;">-- Vocab Layout --</a>
		<div id="${sid}_source" style="display:block">
			<div>${sef.vocabLayout.source}</div>
			<div>path: ${sef.vocabLayout.path}</div>
			<div>elementPath: ${elementPath}</div>
			<div>id: ${sid}</div>
			<div>selected value: ${selectedValue}</div>
			<bean:define id="legalValues" name="sef" property="legalEnumerationValuesOf(${sef.vocabLayout.path})" />
			<div>there are ${fn:length(legalValues)} legal values</div>
			<c:if test="${not empty selectedValue and not sf:listContains (legalValues, selectedValue)}">Selected Value is not legal!</c:if>
		</div>
	</div>
</c:if>

<%-- Check for an illegal value (not in controlled vocab) --%>
<bean:define id="legalValues" name="sef" property="legalEnumerationValuesOf(${sef.vocabLayout.path})" />
<c:if test="${not empty selectedValue and not sf:listContains (legalValues, selectedValue)}">
	<div class="page-error-box">
		<%-- <span class="element-error-msg">warning: illegal value!</span> --%>
		<c:set var="radioId" value="${sf:pathToId(elementPath)}_${selectedValue}_id" />
		<input type="radio" id="${radioId}"  
			name="${elementPath}"
			value="<c:out value='${selectedValue}'/>" checked />
			<label class="${typeclass}"
				for="${radioId}"><c:out value="${selectedValue}"/></label> 
			<span class="error-msg">(NOTE: this value is not allowed for this field)</span>
	</div>
</c:if>

	<div>
		<c:set var="radioId" value="${sf:pathToId(elementPath)}_unspecified_id" />
		<input type="radio" id="${radioId}"  
			name="${elementPath}"
			value="" <c:if test="${empty selectedValue}">checked</c:if> />
			<label class="${typeclass}"
				for="${radioId}">Unspecified</label> 
	</div>

<%-- expand / collapse all --%>
<c:if test="${sef.vocabLayout.collapseExpand == 'true'}">
	<st:muiExpanderWidget elementPath="${elementPath}" />
</c:if>

<table class="vocab-layout-table" id="${sf:pathToId (elementPath)}"><td>
	<c:forEach var="layoutNode" items="${sef.vocabLayoutNodes}">
		<vl:vocabTreeNode elementPath="${elementPath}" vocabTerm="${layoutNode}" inputMode="single"/>
	</c:forEach>
</td></table>


