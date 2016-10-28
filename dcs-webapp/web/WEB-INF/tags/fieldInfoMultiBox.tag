<%--  fieldInfoMultiBox
 
 - Render a muli-box with tool tips using info from fieldInfoReader
		
 - it requires a parameter named elementPath. e.g. here is a call for this tag:
 		<st:mdvMultiBox elementPath="enumerationValuesOf(/itemRecord/educational/resourceTypes/resourceType)"/>

 - this tag will be produced by Renderer.getMultiBoxInput for the cases when FieldInfoReader can be found for a 
   particular xpath
   
--%> 

<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>

<%@ attribute name="elementPath" required="true" %>
<%-- evaluate the currently selected values at elementPath and assign to selectedValues param --%>
<bean:define id="selectedValues" name="sef" property="enumerationValuesOf(${elementPath})"/>
<logic:notEmpty name="sef" property="fieldInfo(${elementPath})">
	<bean:define id="fieldInfo" name="sef" property="fieldInfo(${elementPath})" />
	<c:if test="${empty fieldInfo.vocabTerms}">
	<i>error: fieldInfo.vocabTerms is empty!</i>
	</c:if>
	<c:if test="${not empty fieldInfo.vocabTerms}">
		<c:forEach var="vocabTerm" items="${fieldInfo.vocabTerms}">
			<c:set var="checkBoxId" value="${sf:pathToId(elementPath)}_${vocabTerm.term}_id" />
			<input type="checkbox" id="${checkBoxId}" 
				name="enumerationValuesOf(${elementPath})"
				value="${vocabTerm.term}"
				<c:forEach var="elem" items="${selectedValues}">
					<c:if test="${ elem == vocabTerm.term }">checked</c:if> 
				</c:forEach>			
			/><label 
				<c:if test="${not empty vocabTerm.definition}">title="${vocabTerm.definition}"</c:if>
				for="${checkBoxId}">${vocabTerm.term}</label>
				<br/>
		</c:forEach>
	</c:if>
</logic:notEmpty>

<logic:empty name="sef" property="fieldInfo(${elementPath})">
	<i>error: fieldInfo not found for ${elementPath}</i>
</logic:empty>
