<%--  booleanSelect
 
 - this is a modified version of resourceTag1.
		
 - it requires a parameter named elementPath. e.g. here is a call for this tag:
 		<st:mdvMultiBox elementPath="enumerationValuesOf(/itemRecord/educational/resourceTypes/resourceType)"/>
 	
 - this tag produces a static muli-level display. 
 - The very top level is not displayed, only the lower levels. 
   e.g., in the case of "resourceType" there is no header for "resourceType". The header is displayed by
   Renderer.

 - this tag will be produced by Renderer.getMultiBoxInput for the cases when a MVS mapping can be found for a 
   particular xpath
   
--%> 

<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ attribute name="elementPath" required="true" %>
<%@ attribute name="styleId" %>

<%-- evaluate the currently selected values at elementPath and assign to selectedValues param --%>
<bean:define id="selectedValue" name="sef" property="${elementPath}"/>

<c:set var="labels" value="${fn:split('-- unspecified --+true+false', '+')}"/>
<c:set var="values" value="${fn:split(' +true+false', '+')}"/>
<select name="${elementPath}" id="${styleId}">
 <c:forEach begin="0" end="2" varStatus="status">
 	<option value="${values[status.index]}" <c:if test="${selectedValue == values[status.index]}">SELECTED</c:if> >${labels[status.index]}
</c:forEach>
</select>

