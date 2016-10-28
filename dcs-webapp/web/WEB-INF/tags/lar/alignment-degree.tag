<%-- AlignmentDegree

(/record/standard/alignmentDegree)

Used by both ASN standards (asn-standards.tag)
And other standards (other-standards.tag)

REQUIRED attribute: std_number : the index (1-based) of the current standards element in the instance doc.
NOTE: the UI does NOT necessarily show the standards elements in order, since it groups ASN's by themselves.
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

<%@ attribute name="std_number" required="true" type="java.lang.Integer" %>			<%-- xpath to field --%>

<bean:define id="collapseBean" name="sef" property="collapseBean" type="CollapseBean" />

<c:set var="elementPath" value="/record/standard_${std_number}_/alignmentDegree"/>
<c:set var="id" value="${sf:pathToId(elementPath)}"/>

<c:if test="${false}">
	<h5>alignment-degree.tag</h5>
	<div>elementPath: ${elementPath}</div>
	<div>id: ${id}</div>
	<div>std_number: ${std_number}</div>
</c:if>

<jsp:setProperty name="collapseBean" property="id" value="${id}"/>
<input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />

	<div id="${id}_box">
		<table class="input-field-table">
			<tr class="form-row">
				<td class="label-cell" title="${sf:decodePath('/record/standard_${std_number}_/alignmentDegree')}">
					<div class="field-label">
						<div>alignmentDegree</div>
					</div>
					<div>
						<span class="action-button">
								<st:bestPracticesLink pathArg="/record/standard/alignmentDegree" 
								fieldName="Alignment Degree"/>
						</span>
					</div>
				</td>
				<td class="input-cell">
					<st:elementMessages propertyId="valueOf(/record/standard_${std_number}_/alignmentDegree)"/>
					<div>
						<div>
							<st:fieldPrompt pathArg="/record/standard_${std_number}_/alignmentDegree"/>
							<html:select name="sef" property="valueOf(/record/standard_${std_number}_/alignmentDegree)" styleId="${id}">
								<html:optionsCollection property="selectOptions(/record/standard_${std_number}_/alignmentDegree)"/>
							</html:select>
						</div>
					</div>
				</td>
			</tr>
		</table>
	</div>

<%-- END Alignment degree --%>

