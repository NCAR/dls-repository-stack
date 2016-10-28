<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib tagdir="/WEB-INF/tags/std" prefix="std" %>
<%@ taglib tagdir="/WEB-INF/tags/res_qual" prefix="res_qual" %>
<%@ tag import="edu.ucar.dls.schemedit.display.CollapseBean" %>
<%@ tag import="edu.ucar.dls.schemedit.standards.SuggestionServiceHelper" %>

<%@ attribute name="elementPath" required="true" type="java.lang.String"%>

<%-- evaluate the currently selected values at elementPath and assign to selectedValues param --%>
<bean:define id="selectedValues" name="sef" property="${elementPath}"/>
<c:set var="myXpath" value="${sf:extractPredicateXPath(elementPath)}" />
<c:set var="myId" value="${sf:pathToId(myXpath)}" />
<bean:define id="collapseBean" name="sef" property="collapseBean" type="CollapseBean" />

<%-- <div style="border:blue solid 1px;padding:5px;font-size:8pt;">
<div>ElementPath: ${elementPath}</div>
<div>id: ${myId}</div>
<div>xpath: ${myXpath}</div>
</div> --%>

<div id="${myId}_async_view_element">
	<table class="control" cellspacing="0" cellpadding="3px">
		<tr style="background-color:#E8ECF4;align:center">
			<td>
				<div style="font-weight:bold">Current selections |</div>
			</td>
			<td>
				<div class="edit-button" 
						 style="background-color:#FFFFCC;padding:0px 3px 0px 3px;color:blue">Edit selections</div>
			</td>
		</tr>
	</table>
	<div id="${myId}_value_display"></div>
</div>

<div id="${myId}_async_edit_element" style="display:none">
	<table class="control" cellspacing="0" cellpadding="3px">
		<tr style="background-color:#E8ECF4;align:center">
			<td>
				<div style="font-weight:bold">Edit Selections |</div>
			</td>
			<td>
				<div class="view-button" 
				style="background-color:#FFFFCC;padding:0px 3px 0px 3px;color:blue">Current Selections</div>
			</td>
		</tr>
	</table>
	<div id="${myId}_vocab_layout"></div>
</div>

<%-- Use to cache selected values --%>
<div id="${myId}_hidden" style="display:none"></div>

<script type="text/javascript">

document.observe ("dom:loaded", function (event) {
	var selectedValues = [];
	<c:forEach var="instDiv" items="${selectedValues}">
		selectedValues.push ("${instDiv}");
	</c:forEach>
	new AsyncMultiBox ("${myId}", "${myXpath}", "${elementPath}", selectedValues);
});

</script>
