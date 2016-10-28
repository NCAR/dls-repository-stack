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

<c:set var="elementPath" value="/record/general/subjects"/>
<c:set var="id" value="${sf:pathToId(elementPath)}"/>
<jsp:setProperty name="collapseBean" property="id" value="${id}"/>

<input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
<div id="${id}_box">
	<logic:equal name="sef" property="parentNodeExists(/record/general/subjects)" value="true">
		<table class="no-input-field-table">
			<tr class="form-row">
				<td title="${sf:decodePath('/record/general/subjects')}" class="label-cell">
						<div class="field-label required ">
							<a href="javascript:toggleDisplayState ('${id}');">
								<c:choose>
									<c:when test="${sef.collapseBean.isOpen}">
										<img id="${id}_img" border="0" hspace="3" height="12" width="12" src="../images/opened.gif" />
									</c:when>
									<c:otherwise>
										<img id="${id}_img" border="0" hspace="3" height="12" width="12" src="../images/closed.gif" />
									</c:otherwise>
								</c:choose>subjects
							</a>
						</div>
						<st:bestPracticesLink pathArg="${elementPath}" fieldName="${elementName}"/>
				</td>
				<td class="action-box">
					<div>
						<st:fieldPrompt pathArg="${elementPath}" />
						<st:elementMessages propertyId="valueOf(/record/general/subjects)"/>
					</div>
				</td>
			</tr>
		</table>

		<div id="${id}" style="display:${sef.collapseBean.displayState};">
			<msp2:subjectsChild elementName="scienceSubject" />
			<msp2:subjectsChild elementName="mathSubject" />
			<msp2:subjectsChild elementName="educationalSubject" />
		</div>
 	</logic:equal>
</div>


			  
