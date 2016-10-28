<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ tag import="edu.ucar.dls.schemedit.display.CollapseBean" %>
<%@ taglib tagdir="/WEB-INF/tags/vocabLayout" prefix="vl" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib tagdir="/WEB-INF/tags/msp2" prefix="msp2" %>

<bean:define id="collapseBean" name="sef" property="collapseBean" type="CollapseBean" />

<c:set var="elementPath" value="/record/general/language"/>
<%-- 
<c:set var="id" value="${sf:pathToId(elementPath)}"/>
<jsp:setProperty name="collapseBean" property="id" value="${id}"/>
<input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
 --%>

<logic:equal name="sef" property="parentNodeExists(/record/general/language)" value="true">
	<table class="input-field-table" id="${sf:pathToId(elementPath)}_box" />
		<tr class="form-row">
			<td title="the primary human language or languages of the content in the learning object" class="label-cell">
				<div>
					<div class="field-label required">
						<div>language</div>
					</div>
					<span class="action-button">
						<st:bestPracticesLink pathArg="/record/general/language" fieldName="language"/>
					</span>
				</div>
			</td>
			<td class="input-cell">
			<st:elementMessages propertyId="valueOf(/record/general/language_${languageIndex+1}_)"/>
				<div>
					<input type="hidden" name="enumerationValuesOf(/record/general/language)" 
						value=""  id="${sf:pathToId(elementPath)}"/>
					<vl:vocabLayoutMultiBox elementPath="enumerationValuesOf(/record/general/language)"/>
				</div>
			</td>
		</tr>
	</table>
</logic:equal>


			  
