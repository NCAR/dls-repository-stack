<%@ include file="/lib/includes.jspf" %>
<%@ page import="edu.ucar.dls.schemedit.display.CollapseBean" %>

<c:if test="${not empty sef.suggestionServiceHelper.selectedStandards}">

<bean:define id="collapseBean" name="sef" property="collapseBean" type="CollapseBean" />

<style type="text/css">
.debug {
 border:black solid 1px;
 padding:5px;
 background-color:#003399;
 color:white;
 text-align:center;
 font-size:12px;
 width:50%;
 margin-left:75px;
}

.note {
	background-color:#ffffcc;
	border:black solid 1px;
	padding:5px;
	margin:10px;
	}
</style>

<%@ include file="benchmarks-controls.jspf" %>

<html:form styleId="selected-form" action="/editor/edit.do" method="Post">

<div class="level-1" id="selected-benchmarks">

<%-- 	insert rendered jsp of /record/resourceQuality/representation/learningGoal here

	produced either:
	  - by the java RenderTester class -  RenderTester(res_qual, /record/resourceQuality/representation),  OR
	  - via shell command: % rt res_qual, /record/resourceQuality/representation
	
	TO UPDATE:
	in the rendered JSP, find the following element: 
		"<logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal)" value="true">"
		
	copy the entire JSP element ("logic:equal") to the system clipboard
	
	select the ENTIRE "logic:equal" element of this file (see below!)
	
	paste the clipboard over the selected element, and save the updated file.
	
	DON'T FORGET:
	to attach input-helper to "/record/resourceQuality/representation/learningGoal"
			(inputHelper path is res_qual/learningGoal-text.jsp)

	--%>
	
	
<div class="level-1">

		<%--  The next element should look like this:
			<logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal)" value="true"> 
		--%>

          <logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal)" value="true">
            <div>
              <logic:greaterThan name="sef" property="memberCountOf(/record/resourceQuality/representation/learningGoal)" value="0">
                <logic:iterate indexId="learningGoalIndex" id="item" name="sef" property="repeatingMembersOf(/record/resourceQuality/representation/learningGoal)">
                  <c:set var="itemPath" scope="page" value="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_"/>
                  <c:set var="id" value="${sf:pathToId(itemPath)}"/>
                  <jsp:setProperty name="collapseBean" property="id" value="${id}"/>
                  <input type="hidden" name="${id}_displayState" id="${id}_displayState" value="${sef.collapseBean.displayState}" />
                  <div id="${sf:pathToId(itemPath)}_box">
                    <table class="no-input-field-table">
                      <tr class="form-row">
                        <td title="a specific idea that you want students to learn" class="label-cell">
                          <div>
                            <div class="field-label required ">
                              <logic:equal name="sef" property="nodeIsExpandable(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_)" value="true">
                                <a href="javascript:toggleDisplayState('${id}');">
                                  <c:choose>
                                    <c:when test="${sef.collapseBean.isOpen}">
                                      <img id="${id}_img" border="0" hspace="3" height="12" width="12" src="../images/opened.gif" />
                                    </c:when>
                                    <c:otherwise>
                                      <img id="${id}_img" border="0" hspace="3" height="12" width="12" src="../images/closed.gif" />
                                    </c:otherwise>
                                  </c:choose>learningGoal ${learningGoalIndex+1}
                                </a>
                              </logic:equal>
                              <logic:notEqual name="sef" property="nodeIsExpandable(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_)" value="true">learningGoal ${learningGoalIndex+1}</logic:notEqual>
                            </div>
                            <span class="action-button">
                              <st:bestPracticesLink pathArg="/record/resourceQuality/representation/learningGoal" fieldName="learningGoal"/>
                            </span>
                          </div>
                        </td>
                        <td class="action-box">
                          <span class="action-button">
                            <a href="javascript:doDeleteElement('/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_')" title="${sf:decodePath('delete this learningGoal')}">delete</a>
                          </span>
                        </td>
                        <td class="action-box">
                          <st:fieldPrompt pathArg="/record/resourceQuality/representation/learningGoal"/>
                          <div class="input-helper"><%@ include file="/editor/input_helpers/res_qual/learningGoal-text.jsp" %></div>
                        </td>
                      </tr>
                    </table>
                    <div id="${id}" style="display:${sef.collapseBean.displayState};">
                      <div class="level-2">
                        <logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/content)" value="true">
                          <c:set var="elementPath" value="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/content"/>
                          <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                          <jsp:setProperty name="collapseBean" property="id" value="${id}"/>
                          <input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
                          <div id="${id}_box">
                            <table class="input-field-table">
                              <tr class="form-row">
                                <td class="label-cell" title="learning goal information encoded as ASN URL identifiers">
                                  <div>
                                    <div class="field-label required">
                                      <div>content</div>
                                    </div>
                                    <span class="action-button">
                                      <st:bestPracticesLink pathArg="/record/resourceQuality/representation/learningGoal/content" fieldName="content"/>
                                    </span>
                                  </div>
                                </td>
                                <td class="input-cell">
                                  <st:elementMessages propertyId="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/content)"/>
                                  <div>
                                    <div>
                                      <st:fieldPrompt pathArg="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/content"/>
                                      <div styleId="${id}">
                                        <bean:write name="sef" property="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/content)"/>
                                        <html:hidden property="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/content)"/>
                                      </div>
                                    </div>
                                  </div>
                                </td>
                              </tr>
                            </table>
                          </div>
                        </logic:equal>
                        <div>
                          <logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/resAddrGoal)" value="true">
                            <c:set var="elementPath" value="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/resAddrGoal"/>
                            <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                            <jsp:setProperty name="collapseBean" property="id" value="${id}"/>
                            <input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
                            <div id="${id}_box">
                              <table class="input-field-table">
                                <tr class="form-row">
                                  <td class="label-cell" title="an indication of how well the resource is aligned to the learning goal">
                                    <div>
                                      <div class="field-label required">
                                        <div>resAddrGoal</div>
                                      </div>
                                      <span class="action-button">
                                        <st:bestPracticesLink pathArg="/record/resourceQuality/representation/learningGoal/resAddrGoal" fieldName="resAddrGoal"/>
                                      </span>
                                    </div>
                                  </td>
                                  <td class="input-cell">
                                    <st:elementMessages propertyId="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/resAddrGoal)"/>
                                    <div>
                                      <div>
                                        <st:fieldPrompt pathArg="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/resAddrGoal"/>
                                        <html:select name="sef" property="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/resAddrGoal)" styleId="${id}">
                                          <html:optionsCollection property="selectOptions(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/resAddrGoal)"/>
                                        </html:select>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </logic:equal>
                          <logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/resAddrGoal/@learnGoalPart)" value="true">
                            <c:set var="elementPath" value="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/resAddrGoal/@learnGoalPart"/>
                            <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                            <div class="level-4" id="${id}_box">
                              <table class="input-field-table">
                                <tr class="form-row">
                                  <td class="label-cell" title="the part(s) of a learning goal that a resource addresses">
                                    <div>
                                      <div class="field-label">
                                        <div>learnGoalPart</div>
                                      </div>
                                      <span class="action-button">
                                        <st:bestPracticesLink pathArg="/record/resourceQuality/representation/learningGoal/resAddrGoal/@learnGoalPart" fieldName="learnGoalPart"/>
                                      </span>
                                    </div>
                                  </td>
                                  <td class="input-cell">
                                    <st:elementMessages propertyId="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/resAddrGoal/@learnGoalPart)"/>
                                    <div>
                                      <div>
                                        <st:fieldPrompt pathArg="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/resAddrGoal/@learnGoalPart"/>
                                        <html:textarea property="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/resAddrGoal/@learnGoalPart)" style="width:98%" rows="2" styleId="${id}"/>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </logic:equal>
                        </div>
                        <div>
                          <logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/learnGoalEvidence)" value="true">
                            <c:set var="elementPath" value="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/learnGoalEvidence"/>
                            <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                            <jsp:setProperty name="collapseBean" property="id" value="${id}"/>
                            <input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
                            <div id="${id}_box">
                              <table class="input-field-table">
                                <tr class="form-row">
                                  <td class="label-cell" title="an indication of whether there is an explicit match between the resource and the entire or specified parts of a learning goal">
                                    <div>
                                      <div class="field-label required">
                                        <div>learnGoalEvidence</div>
                                      </div>
                                      <span class="action-button">
                                        <st:bestPracticesLink pathArg="/record/resourceQuality/representation/learningGoal/learnGoalEvidence" fieldName="learnGoalEvidence"/>
                                      </span>
                                    </div>
                                  </td>
                                  <td class="input-cell">
                                    <st:elementMessages propertyId="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/learnGoalEvidence)"/>
                                    <div>
                                      <div>
                                        <st:fieldPrompt pathArg="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/learnGoalEvidence"/>
                                        <html:select name="sef" property="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/learnGoalEvidence)" styleId="${id}">
                                          <html:optionsCollection property="selectOptions(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/learnGoalEvidence)"/>
                                        </html:select>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </logic:equal>
                          <logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/learnGoalEvidence/@comment)" value="true">
                            <c:set var="elementPath" value="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/learnGoalEvidence/@comment"/>
                            <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                            <div class="level-4" id="${id}_box">
                              <table class="input-field-table">
                                <tr class="form-row">
                                  <td class="label-cell" title="information about the connection between the resource and the learning goal">
                                    <div>
                                      <div class="field-label">
                                        <div>comment</div>
                                      </div>
                                      <span class="action-button">
                                        <st:bestPracticesLink pathArg="/record/resourceQuality/representation/learningGoal/learnGoalEvidence/@comment" fieldName="comment"/>
                                      </span>
                                    </div>
                                  </td>
                                  <td class="input-cell">
                                    <st:elementMessages propertyId="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/learnGoalEvidence/@comment)"/>
                                    <div>
                                      <div>
                                        <st:fieldPrompt pathArg="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/learnGoalEvidence/@comment"/>
                                        <html:textarea property="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/learnGoalEvidence/@comment)" style="width:98%" rows="2" styleId="${id}"/>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </logic:equal>
                        </div>
                        <div>
                          <logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/indicateRealorNot)" value="true">
                            <c:set var="elementPath" value="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/indicateRealorNot"/>
                            <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                            <jsp:setProperty name="collapseBean" property="id" value="${id}"/>
                            <input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
                            <div id="${id}_box">
                              <table class="input-field-table">
                                <tr class="form-row">
                                  <td class="label-cell" title="an indication as to which aspects of a learning goal are being represented">
                                    <div>
                                      <div class="field-label required">
                                        <div>indicateRealorNot</div>
                                      </div>
                                      <span class="action-button">
                                        <st:bestPracticesLink pathArg="/record/resourceQuality/representation/learningGoal/indicateRealorNot" fieldName="indicateRealorNot"/>
                                      </span>
                                    </div>
                                  </td>
                                  <td class="input-cell">
                                    <st:elementMessages propertyId="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/indicateRealorNot)"/>
                                    <div>
                                      <div>
                                        <st:fieldPrompt pathArg="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/indicateRealorNot"/>
                                        <html:select name="sef" property="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/indicateRealorNot)" styleId="${id}">
                                          <html:optionsCollection property="selectOptions(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/indicateRealorNot)"/>
                                        </html:select>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </logic:equal>
                          <logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/indicateRealorNot/@comment)" value="true">
                            <c:set var="elementPath" value="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/indicateRealorNot/@comment"/>
                            <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                            <div class="level-4" id="${id}_box">
                              <table class="input-field-table">
                                <tr class="form-row">
                                  <td class="label-cell" title="an explanation of which aspects of a learning goal are being addressed">
                                    <div>
                                      <div class="field-label">
                                        <div>comment</div>
                                      </div>
                                      <span class="action-button">
                                        <st:bestPracticesLink pathArg="/record/resourceQuality/representation/learningGoal/indicateRealorNot/@comment" fieldName="comment"/>
                                      </span>
                                    </div>
                                  </td>
                                  <td class="input-cell">
                                    <st:elementMessages propertyId="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/indicateRealorNot/@comment)"/>
                                    <div>
                                      <div>
                                        <st:fieldPrompt pathArg="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/indicateRealorNot/@comment"/>
                                        <html:textarea property="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/indicateRealorNot/@comment)" style="width:98%" rows="2" styleId="${id}"/>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </logic:equal>
                        </div>
                        <div>
                          <logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/repLearnGoal)" value="true">
                            <c:set var="elementPath" value="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/repLearnGoal"/>
                            <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                            <jsp:setProperty name="collapseBean" property="id" value="${id}"/>
                            <input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
                            <div id="${id}_box">
                              <table class="input-field-table">
                                <tr class="form-row">
                                  <td class="label-cell" title="an indication as to whether the resource accurately represents the aspects of the learning goal it claims to represent">
                                    <div>
                                      <div class="field-label required">
                                        <div>repLearnGoal</div>
                                      </div>
                                      <span class="action-button">
                                        <st:bestPracticesLink pathArg="/record/resourceQuality/representation/learningGoal/repLearnGoal" fieldName="repLearnGoal"/>
                                      </span>
                                    </div>
                                  </td>
                                  <td class="input-cell">
                                    <st:elementMessages propertyId="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/repLearnGoal)"/>
                                    <div>
                                      <div>
                                        <st:fieldPrompt pathArg="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/repLearnGoal"/>
                                        <html:select name="sef" property="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/repLearnGoal)" styleId="${id}">
                                          <html:optionsCollection property="selectOptions(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/repLearnGoal)"/>
                                        </html:select>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </logic:equal>
                          <logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/repLearnGoal/@comment)" value="true">
                            <c:set var="elementPath" value="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/repLearnGoal/@comment"/>
                            <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                            <div class="level-4" id="${id}_box">
                              <table class="input-field-table">
                                <tr class="form-row">
                                  <td class="label-cell" title="comments on the accuracy of the representation to depict the real thing">
                                    <div>
                                      <div class="field-label">
                                        <div>comment</div>
                                      </div>
                                      <span class="action-button">
                                        <st:bestPracticesLink pathArg="/record/resourceQuality/representation/learningGoal/repLearnGoal/@comment" fieldName="comment"/>
                                      </span>
                                    </div>
                                  </td>
                                  <td class="input-cell">
                                    <st:elementMessages propertyId="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/repLearnGoal/@comment)"/>
                                    <div>
                                      <div>
                                        <st:fieldPrompt pathArg="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/repLearnGoal/@comment"/>
                                        <html:textarea property="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/repLearnGoal/@comment)" style="width:98%" rows="2" styleId="${id}"/>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </logic:equal>
                        </div>
                        <div>
                          <logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/comprehensible)" value="true">
                            <c:set var="elementPath" value="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/comprehensible"/>
                            <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                            <jsp:setProperty name="collapseBean" property="id" value="${id}"/>
                            <input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
                            <div id="${id}_box">
                              <table class="input-field-table">
                                <tr class="form-row">
                                  <td class="label-cell" title="an indication as to whether students are likely to understand the representation AND the learning that is being represented">
                                    <div>
                                      <div class="field-label required">
                                        <div>comprehensible</div>
                                      </div>
                                      <span class="action-button">
                                        <st:bestPracticesLink pathArg="/record/resourceQuality/representation/learningGoal/comprehensible" fieldName="comprehensible"/>
                                      </span>
                                    </div>
                                  </td>
                                  <td class="input-cell">
                                    <st:elementMessages propertyId="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/comprehensible)"/>
                                    <div>
                                      <div>
                                        <st:fieldPrompt pathArg="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/comprehensible"/>
                                        <html:select name="sef" property="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/comprehensible)" styleId="${id}">
                                          <html:optionsCollection property="selectOptions(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/comprehensible)"/>
                                        </html:select>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </logic:equal>
                          <logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/comprehensible/@comment)" value="true">
                            <c:set var="elementPath" value="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/comprehensible/@comment"/>
                            <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                            <div class="level-4" id="${id}_box">
                              <table class="input-field-table">
                                <tr class="form-row">
                                  <td class="label-cell" title="comments on the comprehensibility of the representation">
                                    <div>
                                      <div class="field-label">
                                        <div>comment</div>
                                      </div>
                                      <span class="action-button">
                                        <st:bestPracticesLink pathArg="/record/resourceQuality/representation/learningGoal/comprehensible/@comment" fieldName="comment"/>
                                      </span>
                                    </div>
                                  </td>
                                  <td class="input-cell">
                                    <st:elementMessages propertyId="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/comprehensible/@comment)"/>
                                    <div>
                                      <div>
                                        <st:fieldPrompt pathArg="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/comprehensible/@comment"/>
                                        <html:textarea property="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/comprehensible/@comment)" style="width:98%" rows="2" styleId="${id}"/>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </logic:equal>
                        </div>
                        <div>
                          <logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/engaging)" value="true">
                            <c:set var="elementPath" value="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/engaging"/>
                            <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                            <jsp:setProperty name="collapseBean" property="id" value="${id}"/>
                            <input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
                            <div id="${id}_box">
                              <table class="input-field-table">
                                <tr class="form-row">
                                  <td class="label-cell" title="an indication as to whether the representation is sufficiently interesting to make students focus on the learning goal">
                                    <div>
                                      <div class="field-label required">
                                        <div>engaging</div>
                                      </div>
                                      <span class="action-button">
                                        <st:bestPracticesLink pathArg="/record/resourceQuality/representation/learningGoal/engaging" fieldName="engaging"/>
                                      </span>
                                    </div>
                                  </td>
                                  <td class="input-cell">
                                    <st:elementMessages propertyId="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/engaging)"/>
                                    <div>
                                      <div>
                                        <st:fieldPrompt pathArg="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/engaging"/>
                                        <html:select name="sef" property="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/engaging)" styleId="${id}">
                                          <html:optionsCollection property="selectOptions(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/engaging)"/>
                                        </html:select>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </logic:equal>
                          <logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/engaging/@comment)" value="true">
                            <c:set var="elementPath" value="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/engaging/@comment"/>
                            <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                            <div class="level-4" id="${id}_box">
                              <table class="input-field-table">
                                <tr class="form-row">
                                  <td class="label-cell" title="comments on how sufficiently interesting the resource is to make students focus on the learning goal">
                                    <div>
                                      <div class="field-label">
                                        <div>comment</div>
                                      </div>
                                      <span class="action-button">
                                        <st:bestPracticesLink pathArg="/record/resourceQuality/representation/learningGoal/engaging/@comment" fieldName="comment"/>
                                      </span>
                                    </div>
                                  </td>
                                  <td class="input-cell">
                                    <st:elementMessages propertyId="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/engaging/@comment)"/>
                                    <div>
                                      <div>
                                        <st:fieldPrompt pathArg="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/engaging/@comment"/>
                                        <html:textarea property="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/engaging/@comment)" style="width:98%" rows="2" styleId="${id}"/>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </logic:equal>
                        </div>
                        <div>
                          <logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/efficient)" value="true">
                            <c:set var="elementPath" value="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/efficient"/>
                            <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                            <jsp:setProperty name="collapseBean" property="id" value="${id}"/>
                            <input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
                            <div id="${id}_box">
                              <table class="input-field-table">
                                <tr class="form-row">
                                  <td class="label-cell" title="an indication as to whether the benefits of using the representation justify the costs in time and money">
                                    <div>
                                      <div class="field-label required">
                                        <div>efficient</div>
                                      </div>
                                      <span class="action-button">
                                        <st:bestPracticesLink pathArg="/record/resourceQuality/representation/learningGoal/efficient" fieldName="efficient"/>
                                      </span>
                                    </div>
                                  </td>
                                  <td class="input-cell">
                                    <st:elementMessages propertyId="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/efficient)"/>
                                    <div>
                                      <div>
                                        <st:fieldPrompt pathArg="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/efficient"/>
                                        <html:select name="sef" property="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/efficient)" styleId="${id}">
                                          <html:optionsCollection property="selectOptions(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/efficient)"/>
                                        </html:select>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </logic:equal>
                          <logic:equal name="sef" property="parentNodeExists(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/efficient/@comment)" value="true">
                            <c:set var="elementPath" value="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/efficient/@comment"/>
                            <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                            <div class="level-4" id="${id}_box">
                              <table class="input-field-table">
                                <tr class="form-row">
                                  <td class="label-cell" title="comments on whether the benefits of using the representation justify the costs in time and money">
                                    <div>
                                      <div class="field-label">
                                        <div>comment</div>
                                      </div>
                                      <span class="action-button">
                                        <st:bestPracticesLink pathArg="/record/resourceQuality/representation/learningGoal/efficient/@comment" fieldName="comment"/>
                                      </span>
                                    </div>
                                  </td>
                                  <td class="input-cell">
                                    <st:elementMessages propertyId="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/efficient/@comment)"/>
                                    <div>
                                      <div>
                                        <st:fieldPrompt pathArg="/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/efficient/@comment"/>
                                        <html:textarea property="valueOf(/record/resourceQuality/representation/learningGoal_${learningGoalIndex+1}_/efficient/@comment)" style="width:98%" rows="2" styleId="${id}"/>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                              </table>
                            </div>
                          </logic:equal>
                        </div>
                      </div>
                    </div>
                  </div>
                </logic:iterate>
              </logic:greaterThan>
            </div>
          </logic:equal>
      
</div>


<%-- End of rendered jsp (comment out the div just above here that produces
the "new" action button! --%>

</html:form>
</c:if>

<%@ include file="/lib/stds/res_qual/none-selected.jspf" %>
