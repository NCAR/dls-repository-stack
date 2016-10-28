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

<c:set var="myXpath" value="${sf:extractPredicateXPath(elementPath)}" />
<c:set var="myId" value="${sf:pathToId(myXpath)}" />
<bean:define id="selectedValue" name="sef" property="${elementPath}"/>
<bean:define id="legalValues" name="sef" property="legalEnumerationValuesOf(${sef.vocabLayout.path})" />
<bean:define id="collapseBean" name="sef" property="collapseBean" type="CollapseBean" />

<%-- CURRENT SELECTION --%>
<div id="${myId}_async_view_element">

	<table class="control" cellspacing="0" cellpadding="3px">
		<tr style="background-color:#E8ECF4;align:center">
			<td>
				<div style="font-weight:bold">Current selection |</div>
			</td>
			<td>
				<div class="edit-button" style="background-color:#FFFFCC;padding:0px 3px 0px 3px;color:blue">Edit selection</div>
			</td>
		</tr>
	</table>
	
	<div id="${myId}_value_display"></div>
</div>

<div id="${myId}_async_edit_element" style="display:none">

	<table class="control" cellspacing="0" cellpadding="3px">
		<tr style="background-color:#E8ECF4;align:center">
			<td>
				<div style="font-weight:bold">Edit Selection |</div>
			</td>
			<td>
				<div class="view-button" style="background-color:#FFFFCC;padding:0px 3px 0px 3px;color:blue">Current Selection</div>
			</td>
		</tr>
	</table>

	<div id="${myId}_vocab_layout"></div>
</div>

<%-- Use to cache selected values --%>
<div id="${myId}_hidden" style="display:none"></div>

<script type="text/javascript">

document.observe ("dom:loaded", function (event) {
	new AsyncSingleSelect ("${myId}", "${myXpath}", "${elementPath}", "${selectedValue}");
});

function getValue () {
		var form = document.forms[0]; // this is the only thing chrome will accept
		var key = "${elementPath}";
		if (form == undefined)
			throw "form is undefined";
		var params = form.serialize(true);
		var value = params[key];
		log (value);
		return value;
}

</script>
