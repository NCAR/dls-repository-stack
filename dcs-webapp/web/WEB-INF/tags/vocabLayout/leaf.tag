<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib tagdir="/WEB-INF/tags/vocabLayout" prefix="vl" %>

<%@ attribute name="elementPath" required="true" %>
<%@ attribute name="vocabTerm" required="true" type="edu.ucar.dls.schemedit.vocab.layout.LayoutNode" %>
<%@ attribute name="inputMode" required="true"%>

<bean:define id="selectedValues" name="sef" property="${elementPath}"/>

<c:set var="typeclass" value="${(vocabTerm.type == 'group') ? 'typegroup' : 'typevocab'}" />

<div class="leaf-wrapper">

<c:choose>
	<c:when test="${inputMode == 'single'}">
	<%-- RADIO - selectedValues contains the selectedValue (not a list) --%>
		<c:set var="radioId" value="${sf:pathToId(elementPath)}_${vocabTerm.name}_id" />
		<input type="radio" id="${radioId}"
			name="${elementPath}"
			value="<c:out value="${vocabTerm.name}"/>"
				<c:if test="${ selectedValues == vocabTerm.name }">checked</c:if>
		/><label class="${typeclass}"
			<c:if test="${not empty vocabTerm.definition}">title="${vocabTerm.definition}"</c:if>
			for="${radioId}"><c:out value="${vocabTerm.label}"/>
			</label>

	</c:when>

	<c:when test="${inputMode == 'multiple'}">
	<%-- CHECK BOX --%>
		<c:set var="checkBoxId" value="${sf:pathToId(elementPath)}_${vocabTerm.name}_checkbox" />
		<input type="checkbox" id="${checkBoxId}"
			name="${elementPath}"
			value="<c:out value="${vocabTerm.name}"/>"
			<c:if test="${sf:arrayContains (selectedValues, vocabTerm.name)}">checked</c:if>
		/><label class="${typeclass}"
			<c:if test="${not empty vocabTerm.definition}">title="${vocabTerm.definition}"</c:if>
			for="${checkBoxId}"><c:out value="${vocabTerm.label}"/>
			</label>
	</c:when>
	<c:otherwise><div style="color:red">ERROR: unknown inputMode: (${inputMode})</div></c:otherwise>
</c:choose>
</div>
