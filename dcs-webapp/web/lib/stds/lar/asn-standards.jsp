<%@ include file="/lib/includes.jspf" %>
<%@ page import="edu.ucar.dls.schemedit.display.CollapseBean" %>


<bean:define id="collapseBean" name="sef" property="collapseBean" type="CollapseBean" />
<c:set var="asnPath">/record/standard[standard/id/@type='ASN']</c:set>
<bean:define id="standardsElements" name="sef" property="membersOf(/record/standard)" /> 
<c:set var="asnStandards" value="${sf:getAsnLarStandards (standardsElements)}" />

	<c:choose>
		<c:when test="${empty asnStandards}">
		<table class="no-input-field-table">
			<tr class="form-row">
				<td title="${sf:decodePath('/record/standard')}" class="label-cell">
					<div class="field-label">
						<div>standard</div>
					</div>
				</td>
				<td class="action-box">
					<div>
						<div style="font-style:italic">
							No ASN standards selected. Click on "Browse Standards" to select ASN standards.
						</div>
					</div>
				</td>
				<td class="action-box">
					<st:fieldPrompt pathArg="/record/standard"/>
				</td>
			</tr>
		</table>
		
	</c:when>
	<c:otherwise> <%-- normal case - show standards --%>
	<html:form action="/editor/edit.do" styleId="selected-form" method="Post">
		<c:forEach var="item" items="${asnStandards}" varStatus="status">

			<c:set var="std_number" value="${sf:indexOf (standardsElements, item) + 1}" />
			<c:set var="itemPath" scope="page" value="/record/standard_${std_number}_"/>
			<c:set var="id" value="${sf:pathToId(itemPath)}"/>
			<jsp:setProperty name="collapseBean" property="id" value="${id}"/>
			<input type="hidden" name="${id}_displayState" id="${id}_displayState" value="${sef.collapseBean.displayState}" />
			<div id="${sf:pathToId(itemPath)}_box">
				<table class="no-input-field-table">
					<tr class="form-row">
						<td title="${sf:decodePath('/record/standard')}" class="label-cell">
							<div>
								<div class="field-label">
									<logic:equal name="sef" property="nodeIsExpandable(/record/standard_${std_number}_)" value="true">
										<a href="javascript:toggleDisplayState('${id}');">
											<c:choose>
												<c:when test="${sef.collapseBean.isOpen}">
													<img id="${id}_img" border="0" hspace="3" height="12" width="12" src="../images/opened.gif" />
												</c:when>
												<c:otherwise>
													<img id="${id}_img" border="0" hspace="3" height="12" width="12" src="../images/closed.gif" />
												</c:otherwise>
											</c:choose>standard ${std_number}
										</a>
									</logic:equal>
									<logic:notEqual name="sef" property="nodeIsExpandable(/record/standard_${std_number}_)" value="true">standard ${std_number}</logic:notEqual>
									<div>
										<span class="action-button">
											<st:bestPracticesLink pathArg="/record/educational/ASN_standard_field" fieldName="ASN Standard"/>
										</span>
									</div>
									
								</div>
							</div>
						</td>
						<td class="action-box">
							<span class="action-button">
								<%-- DELETE ASN STANDARD --%>
								<a href="javascript:doDeleteElement('/record/standard_${std_number}_')" title="${sf:decodePath('delete this standards')}">delete</a>
								<%-- <a href="javascript:doDeleteAsnStandard('${std_number}')" title="${sf:decodePath('delete this standards')}">delete</a> --%>
							</span>
						</td>
						<td class="action-box">
							<st:fieldPrompt pathArg="/record/standard"/>
							<div class="input-helper"><%@ include file="/editor/input_helpers/lar/alignment-widget.jsp" %></div>
						</td>
					</tr>
				</table>
				<div id="${id}" style="display:${sef.collapseBean.displayState};">
					<div class="level-2">
						<c:set var="elementPath" value="/record/standard_${std_number}_/alignment"/>
						<c:set var="id" value="${sf:pathToId(elementPath)}"/>
						<jsp:setProperty name="collapseBean" property="id" value="${id}"/>
						<input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
						<div id="${id}_box" style="display:${sef.collapseBean.displayState};">
						
						<c:set var="asnId"><bean:write name="sef" property="valueOf(/record/standard_${std_number}_/alignment/id)" /></c:set>
						<input type="hidden" 
									 name="valueOf(/record/standard_${std_number}_/alignment/id)"
									 id="${id}${sf:pathToId('/id')}" 
									 value="${asnId}" />
						<input type="hidden" 
									 name="valueOf(/record/standard_${std_number}_/alignment/id/@type)"
									 id="${id}${sf:pathToId('/id/@type')}"
									 value="ASN" />
									 
						<c:set var="stdNode" value="${sf:getAsnStandard (asnId)}" />
						
						<c:if test="${not empty stdNode}">
						<c:set var="stdDoc" value="${sf:getAsnDocument (stdNode.docId)}" />
							<div style="padding:5px;border:#CCCEE6 1px solid;margin:0px 0px 2px 5px">
								<c:if test="${not empty stdDoc}">
									<div style="font-size:12px;font-weight:bold;">${stdDoc.topic} - ${stdDoc.authorName} 
										${stdDoc.created} - <i>${stdDoc.title}</i></div>
								</c:if>
								<c:if test="${empty stdDoc}">
									<div>doc not found for ${stdNode.docId}</div>
								</c:if>
								<c:forEach var="entry" items="${stdNode.lineage}" varStatus="i">
									<div style="margin:2px 2px 2px ${i.index * 15}px;">${entry}</div>
								</c:forEach>
								<%-- <div align="right" style="font-size:9pt;margin:3px 10px 0px 0px">${asnId}</div> --%>
							</div>
						</c:if>
						<c:if test="${empty stdNode}">
							<div style="font-style:italic">stdNode not found for ${asnId}</div>
						</c:if>
						
						</div> <%-- end standard_box --%>

						<%-- Alignment Degree goes here ... --%>
						<lar:alignment-degree std_number="${std_number}" />
						
					</div>
				</div>
			</div>
		</c:forEach>
		</html:form>
	</c:otherwise>
	</c:choose>


