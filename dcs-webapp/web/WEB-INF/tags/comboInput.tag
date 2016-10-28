<%--  comboInput tag
 
 - Renders an input element that allows user to make a choice from a supplied set of values, or alternatively
 type in a value.
   
 - Supporting javascript can be found in /lib/combo-input-support.js
--%> 

<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>

<%@ attribute name="elementPath" required="true" %>
<%@ attribute name="styleId" %>

<%-- id is a javascript variable used to identify the elements of this comboUnionInpug --%>
<c:set var="id" value="${sf:pathToId(elementPath)}"/>

<bean:define id="selectedValue" name="sef" property="valueOf(${elementPath})"/>
<bean:define id="options" name="sef" property="comboSelectOptions(${elementPath})" />

<%-- single-value select element --%>
<div id="${id}_select" style="display:block">
	<select id="${id}_select_menu" name="valueOf(${elementPath})" styleId="${styleId}"
		onChange="comboSelectOnChangeHandler('${id}', this.value)">
 	<c:forEach var="option" items="${options}">
		<option value="${option.value}" <c:if test="${option.value == selectedValue}">SELECTED</c:if> >${option.label}
	</c:forEach>
		<option value="${sef.comboOtherOption.value}" 
			title="after choosing this item you will be able to enter a value not on the list above">${sef.comboOtherOption.label}</option>
	</select>
</div>
<%-- text input element --%>
<div id="${id}_text" style="display:none">
	<input id="${id}_text_input" type="text" size="50" value="" onblur="updateComboSelect ('${id}', this.value)"/>
</div>

