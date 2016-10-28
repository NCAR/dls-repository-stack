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


<bean:define id="collapseBean" name="sef" property="collapseBean" type="CollapseBean" />

<bean:define id="standardsElements" name="sef" property="membersOf(/record/standard)" /> 
<c:set var="asnStandards" value="${sf:getAsnLarStandards (standardsElements)}" />
<c:set var="otherStandards" value="${sf:getNonAsnLarStandards (standardsElements)}" />


<c:set var="id" value="other-standards"/>
<jsp:setProperty name="collapseBean" property="id" value="${id}"/>

<%-- open asn_standards element if hash refers to an asnStandard --%>
<jsp:setProperty name="collapseBean" property="displayState" 
	value="${sf:isNonAsnLarStandardDescendent(sef.hash,standardsElements)} "/>

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
					</c:choose>Other Standards
					</a>
			</div>
		</td>
	</tr>
</table>

</div>

<%-- The box surrounding the ASN Standards --%>
<%-- class="level-x" to indent --%>
<div id="${id}" class="level-2" style="display:${sef.collapseBean.displayState};">
	<c:choose>
		<c:when test="${empty otherStandards}">
		<table class="no-input-field-table">
			<tr class="form-row">
				<td title="${sf:decodePath('/record/standard')}" class="label-cell">
					<div class="field-label">
						<div>standard</div>
						<div>
							<span class="action-button">
									<st:bestPracticesLink pathArg="/record/standard" 
									fieldName="Standard"/>
							</span>
						</div>						
					</div>
				</td>
				<td class="action-box">
					<div>
						<div class="action-button">
						<%-- if there are ASN-standards, the path for the new standard must be set to their count --%>
							<c:set var="new_std_path"><c:choose>
								<c:when test="${not empty asnStandards}">/record/standard_${fn:length(asnStandards)}_</c:when>
								<c:otherwise>/record/standard</c:otherwise>
							</c:choose></c:set>
							<a href="javascript:doNewElement('${new_std_path}')" title="${sf:decodePath('Create a new standard')}">add standard</a>
						</div>
					</div>
				</td>
				<td class="action-box">
					<st:fieldPrompt pathArg="/record/standard"/>
				</td>
			</tr>
		</table>
	</c:when>
	
	<%-- NOW Start case where there ARE standards to show --%>
	
	<c:otherwise>
		<c:forEach var="item" items="${otherStandards}" varStatus="status" >
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
													<st:bestPracticesLink pathArg="/record/standard" 
													fieldName="Standard"/>
											</span>
										</div>
									</div>
								</div>
							</td>
							<td class="action-box">
								<span class="action-button">
									<a href="javascript:doDeleteElement('/record/standard_${std_number}_')" title="${sf:decodePath('delete this standards')}">delete</a>
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
							<%-- STANDARDS BOX --%>
								<logic:equal name="sef" property="parentNodeExists(/record/standard_${std_number}_/alignment)" value="true">
									<table class="no-input-field-table">
										<tr class="form-row">
											<td title="${sf:decodePath('/record/standard_${std_number}_/alignment')}" class="label-cell">
												<div>
													<div class="field-label">
														<logic:equal name="sef" property="nodeIsExpandable(/record/standard_${std_number}_/alignment)" value="true">
															<a href="javascript:toggleDisplayState('${id}');">
																<c:choose>
																	<c:when test="${sef.collapseBean.isOpen}">
																		<img id="${id}_img" border="0" hspace="3" height="12" width="12" src="../images/opened.gif" />
																	</c:when>
																	<c:otherwise>
																		<img id="${id}_img" border="0" hspace="3" height="12" width="12" src="../images/closed.gif" />
																	</c:otherwise>
																</c:choose>alignment
															</a>
														</logic:equal>
														<logic:notEqual name="sef" property="nodeIsExpandable(/record/standard_${std_number}_/alignment)" value="true">standard</logic:notEqual>
														<div>
															<span class="action-button">
																	<st:bestPracticesLink pathArg="/record/standard/alignment" 
																	fieldName="Standard Alignment"/>
															</span>
														</div>
														</div>
												</div>
											</td>
											<td class="action-box">
												<div>
													<div>
														<logic:notEqual name="sef" property="nodeExists(/record/standard_${std_number}_/alignment)" value="true">
															<span class="action-button">
																<a href="javascript:doNewElement('/record/standard_${std_number}_/alignment')" title="${sf:decodePath('Specify the contents of this optional element')}">choose</a>
															</span>
														</logic:notEqual>
														<logic:equal name="sef" property="nodeExists(/record/standard_${std_number}_/alignment)" value="true">
															<span class="action-button">
																<a href="javascript:doDeleteElement('/record/standard_${std_number}_/alignment')" title="${sf:decodePath('remove this optional element')}">remove</a>
															</span>
														</logic:equal>
													</div>
												</div>
											</td>
											<td class="action-box">
												<st:fieldPrompt pathArg="/record/standard_${std_number}_/alignment"/>
												<div class="input-helper"><%@ include file="/editor/input_helpers/lar/alignment-widget.jsp" %></div>
											</td>
										</tr>
									</table>
									<div id="${id}" style="display:${sef.collapseBean.displayState};">
										<div class="level-3">
											<logic:equal name="sef" property="nodeExists(/record/standard_${std_number}_/alignment/id)" value="true">
												<div>
													<logic:equal name="sef" property="parentNodeExists(/record/standard_${std_number}_/alignment/id)" value="true">
														<c:set var="elementPath" value="/record/standard_${std_number}_/alignment/id"/>
														<c:set var="id" value="${sf:pathToId(elementPath)}"/>
														<jsp:setProperty name="collapseBean" property="id" value="${id}"/>
														<input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
														<div id="${id}_box">
															<table class="input-field-table">
																<tr class="form-row">
																	<td class="label-cell" title="${sf:decodePath('/record/standard_${std_number}_/alignment/id')}">
																		<div class="field-label">
																			<div>id</div>
																			<div>
																				<logic:notEqual name="sef" property="nodeExists(/record/standard_${std_number}_/alignment/id)" value="true">
																					<span class="action-button">
																						<a href="javascript:doNewElement('/record/standard_${std_number}_/alignment/id')" title="${sf:decodePath('Specify the contents of this optional element')}">choose</a>
																					</span>
																				</logic:notEqual>
																				<logic:equal name="sef" property="nodeExists(/record/standard_${std_number}_/alignment/id)" value="true">
																					<span class="action-button">
																						<a href="javascript:doDeleteElement('/record/standard_${std_number}_/alignment/id')" title="${sf:decodePath('remove this optional element')}">remove</a>
																					</span>
																				</logic:equal>
																			</div>
																			<div>
																				<span class="action-button">
																						<st:bestPracticesLink pathArg="/record/standard/alignment/id" 
																						fieldName="Alignment ID"/>
																				</span>
																			</div>																			
																		</div>
																	</td>
																	<td class="input-cell">
																		<st:elementMessages propertyId="valueOf(/record/standard_${std_number}_/alignment/id)"/>
																		<div>
																			<div>
																				<st:fieldPrompt pathArg="/record/standard_${std_number}_/alignment/id"/>
																				<html:text property="valueOf(/record/standard_${std_number}_/alignment/id)" size="50" styleId="${id}"/>
																			</div>
																		</div>
																	</td>
																</tr>
															</table>
														</div>
													</logic:equal>
													<logic:equal name="sef" property="parentNodeExists(/record/standard_${std_number}_/alignment/id/@type)" value="true">
														<c:set var="elementPath" value="/record/standard_${std_number}_/alignment/id/@type"/>
														<c:set var="id" value="${sf:pathToId(elementPath)}"/>
														<div class="level-5" id="${id}_box">
															<table class="input-field-table">
																<tr class="form-row">
																	<td class="label-cell" title="${sf:decodePath('/record/standard_${std_number}_/alignment/id/@type')}">
																		<div class="field-label required">
																			<div>type</div>
																			<div>
																				<span class="action-button">
																						<st:bestPracticesLink pathArg="/record/standard/alignment/id/@type" 
																						fieldName="Standard Alignment Id Type"/>
																				</span>
																			</div>																			
																		</div>
																	</td>
																	<td class="input-cell">
																		<st:elementMessages propertyId="valueOf(/record/standard_${std_number}_/alignment/id/@type)"/>
																		<div>
																			<div>
																				<st:fieldPrompt pathArg="/record/standard_${std_number}_/alignment/id/@type"/>
																				<div class="input-helper"><%@ include file="/editor/input_helpers/lar/idType-widget.jsp" %></div>
																				<html:select name="sef" property="valueOf(/record/standard_${std_number}_/alignment/id/@type)" styleId="${id}">
																					<html:optionsCollection property="selectOptions(/record/standard_${std_number}_/alignment/id/@type)"/>
																				</html:select>
																			</div>
																		</div>
																	</td>
																</tr>
															</table>
														</div>
													</logic:equal>
												</div>
											</logic:equal>
											<logic:equal name="sef" property="nodeExists(/record/standard_${std_number}_/alignment/text)" value="true">
												<logic:equal name="sef" property="parentNodeExists(/record/standard_${std_number}_/alignment/text)" value="true">
													<c:set var="elementPath" value="/record/standard_${std_number}_/alignment/text"/>
													<c:set var="id" value="${sf:pathToId(elementPath)}"/>
													<jsp:setProperty name="collapseBean" property="id" value="${id}"/>
													<input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
													<div id="${id}_box">
														<table class="input-field-table">
															<tr class="form-row">
																<td class="label-cell" title="${sf:decodePath('/record/standard_${std_number}_/alignment/text')}">
																	<div class="field-label required">
																		<div>text</div>
																		<div>
																			<logic:notEqual name="sef" property="nodeExists(/record/standard_${std_number}_/alignment/text)" value="true">
																				<span class="action-button">
																					<a href="javascript:doNewElement('/record/standard_${std_number}_/alignment/text')" title="${sf:decodePath('Specify the contents of this optional element')}">choose</a>
																				</span>
																			</logic:notEqual>
																			<logic:equal name="sef" property="nodeExists(/record/standard_${std_number}_/alignment/text)" value="true">
																				<span class="action-button">
																					<a href="javascript:doDeleteElement('/record/standard_${std_number}_/alignment/text')" title="${sf:decodePath('remove this optional element')}">remove</a>
																				</span>
																			</logic:equal>
																		</div>
																		<div>
																			<span class="action-button">
																					<st:bestPracticesLink pathArg="/record/standard/alignment/text" 
																					fieldName="Standard Alignment Text"/>
																			</span>
																		</div>																		
																	</div>
																</td>
																<td class="input-cell">
																	<st:elementMessages propertyId="valueOf(/record/standard_${std_number}_/alignment/text)"/>
																	<div>
																		<div>
																			<st:fieldPrompt pathArg="/record/standard_${std_number}_/alignment/text"/>
																			<html:text property="valueOf(/record/standard_${std_number}_/alignment/text)" size="50" styleId="${id}"/>
																		</div>
																	</div>
																</td>
															</tr>
														</table>
													</div>
												</logic:equal>
											</logic:equal>
											<logic:equal name="sef" property="nodeExists(/record/standard_${std_number}_/alignment)" value="true">
												<logic:empty name="sef" property="membersOf(/record/standard_${std_number}_/alignment/*)">
													<div>
														<input type="hidden" name="valueOf(/record/standard_${std_number}_/alignment)" value="" />
														<table class="input-field-table">
															<tr class="form-row">
																<td class="label-cell" title="${sf:decodePath('/record/standard_${std_number}_/alignment')}">
																	<div class="field-label">
																		<div class="required-choice">required choice</div>
																	</div>
																</td>
																<td class="input-cell">
																	<st:elementMessages propertyId="valueOf(/record/standard_${std_number}_/alignment)"/>
																	<div>
																		<jsp:setProperty name="sef" property="tmpArg" value=""/>
																		<html:select name="sef" property="tmpArg" onchange="return (doChoice(this.value))" title="${sf:decodePath('Choose from drop down list')}">
																			<html:optionsCollection property="choiceOptions(/record/standard_${std_number}_/alignment)"/>
																		</html:select>
																	</div>
																</td>
															</tr>
														</table>
													</div>
												</logic:empty>
											</logic:equal>
										</div>
									</div>
								</logic:equal>
							</div>
							<c:set var="elementPath" value="/record/standard_${std_number}_/alignmentDegree"/>
							<c:set var="id" value="${sf:pathToId(elementPath)}"/>
							<jsp:setProperty name="collapseBean" property="id" value="${id}"/>
							<input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />

						<%-- Alignment Degree goes here ... --%>
						<lar:alignment-degree std_number="${std_number}" />							

						</div>
					</div>
				</div>
			</c:forEach>
	</c:otherwise>
	</c:choose>
	<div>
		<logic:greaterThan name="sef" property="memberCountOf(/record/standard_${std_number}_)" value="0">
			<logic:equal name="sef" property="acceptsNewSibling(/record/standard_${std_number}_)" value="true">
				<div style="width:100px;text-align:center">
					<div class="action-button">
						<a href="javascript:doNewElement('/record/standard_${std_number}_')" title="${sf:decodePath('Create a new standard')}">add standard</a>
					</div>
				</div>
			</logic:equal>
		</logic:greaterThan>
	</div>
</div>
