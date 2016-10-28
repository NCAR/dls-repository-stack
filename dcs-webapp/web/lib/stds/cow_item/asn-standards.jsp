<%@ include file="/lib/includes.jspf" %>
<%@ page import="edu.ucar.dls.schemedit.display.CollapseBean" %>

<%-- /comm_anno/ASNstandard --%>

<bean:define id="collapseBean" name="sef" property="collapseBean" type="CollapseBean" />
<%-- <c:set var="asnPath" value="/cowItem/educational/benchmarks/asnId" /> --%>
<c:set var="asnPath" value="${sef.suggestionServiceHelper.xpath}" />
<bean:define id="asnStandards" name="sef" property="membersOf(${asnPath})" /> 

	<c:choose>
		<c:when test="${empty asnStandards}">
		<table class="no-input-field-table">
			<tr class="form-row">
				<td title="${sf:decodePath('${asnPath}')}" class="label-cell">
					<div class="field-label">
						<div>standard</div>
					</div>
				</td>
					<div>
						<div style="font-style:italic">
							No ASN standards selected! Click on "Browse Standards" to select ASN standards.
						</div>
					</div>
				</td>
				<td class="action-box">
					<st:fieldPrompt pathArg="${asnPath}"/>
				</td>
			</tr>
		</table>
		
	</c:when>
	<c:otherwise> <%-- show standards with attributes--%>
	
		<html:form action="/editor/edit.do" styleId="selected-form" method="Post">
	
        <logic:iterate indexId="asnStdIndex" id="item" name="sef" property="repeatingMembersOf(${asnPath})">
          <c:set var="itemPath" scope="page" value="${asnPath}_${asnStdIndex+1}_"/>
          <c:set var="id" value="${sf:pathToId(itemPath)}"/>
          <jsp:setProperty name="collapseBean" property="id" value="${id}"/>
          <input type="hidden" name="${id}_displayState" id="${id}_displayState" value="${sef.collapseBean.displayState}" />
          <div id="${sf:pathToId(itemPath)}_box">
            <table class="no-input-field-table">
              <tr class="form-row">
                <td title="${sf:decodePath(asnPath)}" class="label-cell">
                  <div>
                    <div class="field-label">
                      <logic:equal name="sef" property="nodeIsExpandable(${asnPath}_${asnStdIndex+1}_)" value="true">
                        <a href="javascript:toggleDisplayState('${id}');">
                          <c:choose>
                            <c:when test="${sef.collapseBean.isOpen}">
                              <img id="${id}_img" border="0" hspace="3" height="12" width="12" src="../images/opened.gif" />
                            </c:when>
                            <c:otherwise>
                              <img id="${id}_img" border="0" hspace="3" height="12" width="12" src="../images/closed.gif" />
                            </c:otherwise>
                          </c:choose>ASNstandard ${asnStdIndex+1}
                        </a>
                      </logic:equal>
                      <logic:notEqual name="sef" property="nodeIsExpandable(${asnPath}_${asnStdIndex+1}_)" value="true">ASNstandard ${asnStdIndex+1}</logic:notEqual>
                    </div>
                  </div>
                </td>
                <td class="action-box">
                  <span class="action-button">
                    <a href="javascript:doDeleteElement('${asnPath}_${asnStdIndex+1}_')" title="${sf:decodePath('delete this ASNstandard')}">delete</a>
                  </span>
                </td>
                <td class="action-box">
                  <st:fieldPrompt pathArg="${asnPath}"/>
                </td>
              </tr>
            </table>
						
						<%-- ++++++++++++++++++++ --%>
						
            <div class="level-1" id="${id}" style="display:${sef.collapseBean.displayState};">
              <logic:equal name="sef" property="parentNodeExists(${asnPath}_${asnStdIndex+1}_)" value="true">
                <c:set var="elementPath" value="${asnPath}_${asnStdIndex+1}_"/>
                <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                <jsp:setProperty name="collapseBean" property="id" value="${id}"/>
                <input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
                <div id="${id}_box">
								
                  <table class="input-field-table">
                    <tr class="form-row">
											<td>
												<c:set var="asnId">
													<bean:write name="sef" property="valueOf(${asnPath}_${asnStdIndex+1}_)" />
												</c:set>
												
												<div align="right" style="font-size:9pt;margin:3px 10px 0px 0px">${asnId}</div>
											
												<%-- cache field value in hidden variable --%>
												<input type="hidden" 
															 name="valueOf(${asnPath}_${asnStdIndex+1}_)"
															 id="${id}}" 
															 value="${asnId}" />
														
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
													</div>
												</c:if>
												<c:if test="${empty stdNode}">
													<div style="font-style:italic">stdNode not found for ${asnId}</div>
												</c:if>
											</td>	
                    </tr>
                  </table>
                </div>
              </logic:equal>
							
							<%-- attribute #1 - alignmentCode --%>
							<c:set var="attrName" value="alignmentCode" />
							
              <logic:equal name="sef" property="parentNodeExists(${asnPath}_${asnStdIndex+1}_/@${attrName})" value="true">
                <c:set var="elementPath" value="${asnPath}_${asnStdIndex+1}_/@${attrName}"/>
                <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                <div class="level-2" id="${id}_box">
                  <table class="input-field-table">
                    <tr class="form-row">
                      <td class="label-cell" title="${sf:decodePath('${asnPath}_${asnStdIndex+1}_/@${attrName}')}">
                        <div class="field-label">
                          <div>${attrName}</div>
                        </div>
                      </td>
                      <td class="input-cell">
                        <st:elementMessages propertyId="valueOf(${asnPath}_${asnStdIndex+1}_/@${attrName})"/>
                        <div>
                          <div>
														<%-- code below handles case where "${attrName}" is a controlled vocab --%>
														
                            <st:fieldPrompt pathArg="${asnPath}_${asnStdIndex+1}_/@${attrName}"/>
                            <html:select name="sef" property="valueOf(${asnPath}_${asnStdIndex+1}_/@${attrName})" styleId="${id}">
                              <html:optionsCollection property="selectOptions(${asnPath}_${asnStdIndex+1}_/@${attrName})"/>
                            </html:select>
														
														<%-- plain old text input --%>
                            <%-- <st:fieldPrompt pathArg="${asnPath}_${asnStdIndex+1}_/@${attrName}"/>
                            <html:text property="valueOf(${asnPath}_${asnStdIndex+1}_/@${attrName})" size="50" styleId="${id}"/>
														--%>
                          </div>
                        </div>
                      </td>
                    </tr>
                  </table>
                </div>
              </logic:equal>
							
							<%-- attribute #2 - alignmentCode --%>
							<c:set var="attrName" value="lessonFocusCode" />
							
              <logic:equal name="sef" property="parentNodeExists(${asnPath}_${asnStdIndex+1}_/@${attrName})" value="true">
                <c:set var="elementPath" value="${asnPath}_${asnStdIndex+1}_/@${attrName}"/>
                <c:set var="id" value="${sf:pathToId(elementPath)}"/>
                <div class="level-2" id="${id}_box">
                  <table class="input-field-table">
                    <tr class="form-row">
                      <td class="label-cell" title="${sf:decodePath('${asnPath}_${asnStdIndex+1}_/@${attrName}')}">
                        <div class="field-label">
                          <div>${attrName}</div>
                        </div>
                      </td>
                      <td class="input-cell">
                        <st:elementMessages propertyId="valueOf(${asnPath}_${asnStdIndex+1}_/@${attrName})"/>
                        <div>
                          <div>
                            <st:fieldPrompt pathArg="${asnPath}_${asnStdIndex+1}_/@${attrName}"/>
														<html:select name="sef" property="valueOf(${asnPath}_${asnStdIndex+1}_/@${attrName})" styleId="${id}">
                              <html:optionsCollection property="selectOptions(${asnPath}_${asnStdIndex+1}_/@${attrName})"/>
                            </html:select>
                            <%-- <html:text property="valueOf(${asnPath}_${asnStdIndex+1}_/@${attrName})" size="50" styleId="${id}"/> --%>
                          </div>
                        </div>
                      </td>
                    </tr>
                  </table>
                </div>
              </logic:equal>
            </div>
						<%-- +++++++++++ --%>
          </div>
        </logic:iterate>


		</html:form>
	</c:otherwise>
	</c:choose>
	



