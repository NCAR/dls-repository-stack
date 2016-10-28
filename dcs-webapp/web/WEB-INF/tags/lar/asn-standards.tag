
<%-- 
ToDo:

- In view mode: Disable what is now the "standard" element. Display the ID and Standard text (see res_qual)
- In browse mode: Hide entire box and instead display ASN Standards Chooser. 

--%>

<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ tag import="edu.ucar.dls.schemedit.display.CollapseBean" %>
<%@ taglib tagdir="/WEB-INF/tags/vocabLayout" prefix="vl" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib tagdir="/WEB-INF/tags/lar" prefix="lar" %>
<%@ taglib tagdir="/WEB-INF/tags/std" prefix="std" %>

<bean:define id="collapseBean" name="sef" property="collapseBean" type="CollapseBean" />

<c:set var="asnPath">/record/standard[alignment/id/@type='ASN']</c:set>

<bean:define id="standardsElements" name="sef" property="membersOf(/record/standard)" /> 
<c:set var="asnStandards" value="${sf:getAsnLarStandards (standardsElements)}" />

<c:set var="id" value="asn-standards"/>
<jsp:setProperty name="collapseBean" property="id" value="${id}"/>

<%-- open asn_standards element if hash refers to an asnStandard --%>
<jsp:setProperty name="collapseBean" property="displayState" 
	value="${sf:isAsnLarStandardDescendent(sef.hash,standardsElements)} "/>

<input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
<div id="${id}_box">

<table class="no-input-field-table">
	<tr class="form-row">
		<td class="label-cell">
			<div class="field-label">
					<a href="javascript:toggleDisplayState('${id}');">
					<c:choose>
						<c:when test="${sef.collapseBean.isOpen}">
							<img id="${id}_img" border="0" hspace="3" height="12" width="12" src="../images/opened.gif" />
						</c:when>
						<c:otherwise>
							<img id="${id}_img" border="0" hspace="3" height="12" width="12" src="../images/closed.gif" />
						</c:otherwise>
					</c:choose>ASN Standards
					</a>
					<div>
						<span class="action-button">
								<st:bestPracticesLink pathArg="/record/educational/ASN_standards_selector" 
								fieldName="ASN Standards Service"/>
						</span>
					</div>
			</div>
		</td>
		<td class="input-cell">
			<st:fieldPrompt pathArg="/record/educational/ASN_standards_selector"/>
		</td>
	</tr>
</table>

</div>

<%-- The box surrounding the ASN Standards --%>

<div id="${id}" class="level-2" style="display:${sef.collapseBean.displayState};">

	<%-- START Standards Selection UI --%>
			<c:if test="${not empty sef.suggestionServiceHelper}">
			
				<c:set var="serviceHelper" value="${sef.suggestionServiceHelper}"/>
				
				<%-- ASN SELECT WIDGET --%>
				<div id="asn-select-widget">
				
				<c:if test="${false}" > <%-- DEBUGGING info --%>
					<style type="text/css">
						.widget-info {font-size:10px;margin-top:3px}
					</style>
					<%-- <div class='widget-info'>Helper Xpath: ${serviceHelper.xpath}</div> --%>
					<div class='widget-info'>DisplayMode: ${serviceHelper.displayMode}</div>
					<div class='widget-info'>DisplayContent: ${serviceHelper.displayContent}</div>
					<div class='widget-info'>Hash: ${sef.hash}</div>
					<%-- <div class='widget-info'>idToPath: ${sf:idToPath (id)}</div> --%>
				</c:if>
					
					<std:standards_MultiBox 
						elementPath="enumerationValuesOf(${serviceHelper.xpath})" 
						pathArg="${serviceHelper.xpath}"/>
				</div>
				
			</c:if>
	<%-- END Standards Selection UI --%>



</div>
