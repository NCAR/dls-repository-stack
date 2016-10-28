<%@ tag isELIgnored ="false" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib tagdir="/WEB-INF/tags/std" prefix="std" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="html" uri="/WEB-INF/tlds/struts-html.tld" %>

<%@ tag import="edu.ucar.dls.schemedit.standards.StandardsNode" %>

<%@ attribute name="stdNode" required="true" type="edu.ucar.dls.schemedit.standards.StandardsNode"%>
<%@ attribute name="stdIsSuggested" required="true" type="java.lang.Boolean"%>
<%@ attribute name="stdIsSelected" required="true" type="java.lang.Boolean"%>
<%@ attribute name="elementPath" required="true" type="java.lang.String"%>
<%@ attribute name="dleseId" required="false" type="java.lang.String"%>

<c:set var="showStdId" value="${false}"/>
<c:set var="showStatementNotation" value="${false}"/>

<div class="suggestion-box ${stdIsSuggested ? 'suggested' : ''}	${stdIsSelected ? 'checked' : ''}" >
	<input type="checkbox" id="<c:out value="${stdNode.id}"/>_id" 
		name="${elementPath}"
		value="<c:out value='${stdNode.id}'/>"
		<c:if test="${stdIsSelected}">checked="1"</c:if>
	/>
	<label 
		<c:if test="${not empty stdNode.definition}">title="${stdNode.definition}"</c:if>
		for="<c:out value="${stdNode.id}"/>_id"><c:out 
			value="${dleseId == 'true' ? stdNode.id : stdNode.label}" escapeXml="false" />
	</label>
	<c:if test="${showStdId}">
 		<span style="font-size:10px;color:white;padding:2px;background-color:gray;">
				${fn:substringAfter (stdNode.id, 'http://purl.org/ASN/resources/')}</span>
	</c:if>
	<c:if test="${showStatementNotation && not empty stdNode.statementNotation}">
			<span style="font-weight:bold;padding:2px;background-color:pink;">${stdNode.statementNotation}</span>
	</c:if>
</div>

<c:if test="${sef.xmlFormat == 'nsdl_anno' && sef.suggestionServiceHelper.displayContent == 'selected'}">
<c:set var="att_path">${sf:extractPredicateXPath(elementPath)}/@alignment</c:set>
<%-- <div style="border:3px purple inset;padding:5px">
	attr path: ${att_path}
</div> --%>
	<div class="level-2">
		<table class="input-field-table">
			<tr class="form-row">
				<td class="label-cell" title="${sf:decodePath(att_path)}">
					<div class="field-label">
						<div>alignment</div>
					</div>
				</td>
				<td class="input-cell">
					<st:elementMessages propertyId="valueOf(${att_path})"/>
					<bean:define id="myOptions" name="sef" property="selectOptions(/nsdl_anno/ASNstandard/@alignment)" />
					<div>
						<div>
							<st:fieldPrompt pathArg="${att_path}"/>
 							<html:select name="sef" property="valueOf(${att_path})">
									<html:optionsCollection name="myOptions"/>
							</html:select>
						</div>
					</div>
				</td>
			</tr>
		</table>
	</div>
	

</c:if>
