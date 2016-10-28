<c:if test="${not empty param.relation}"><%-- Outputs the relations element. Place the record's doc reader in var 'headDocReader' before including --%>
<relations>
	<c:forEach items="${paramValues.relation}" var="relationType">
		<c:choose>
			<%-- Handle alsoCatalogedBy relation --%>
			<c:when test="${relationType == 'alsoCatalogedBy'}">
				<c:set var="alsoCatalogedByCount" value="0"/>			
				<c:set var="alsoCatalogedByRecords">
					<c:forEach items="${headDocReader.deDupedResultDocs}" var="deDupResult" varStatus="dupsStatus"> 												
						<c:choose>
							<c:when test="${headDocReader.id != deDupResult.docReader.id && deDupResult.docReader.isMyCollectionEnabled}">
								<record>
									<c:set var="headDocReaderAM" value="${deDupResult.docReader}"/><%@ include file="HeadElementAM.jsp" %>
<c:if test="${(param['response.mode'] == 'allOff' && f:contains(paramValues['response'],'metadata')) || param['response.mode'] != 'allOff'}">									
<metadata>
${deDupResult.docReader.xmlStripped}
</metadata>
</c:if>
<c:set var="isRelatedRecord" value="${true}"/>
<c:set var="headDocReaderSAVE" value="${headDocReader}"/>
<c:set var="headDocReader" value="${deDupResult.docReader}"/>
<%@ include file="RespOutputElement.jsp" %><%-- Show content requested in the response ouput --%>
<%@ include file="StoredContentElement.jsp" %><%-- Show content from stored Lucene fields, if requested --%>
<c:set var="headDocReader" value="${headDocReaderSAVE}"/>
<c:remove var="isRelatedRecord"/>
								</record>
								<c:set var="alsoCatalogedByCount" value="${alsoCatalogedByCount+1}"/>
							</c:when>
						</c:choose>	
					</c:forEach>
				</c:set>
				<c:choose>
					<c:when test="${alsoCatalogedByCount+0 == 0}">
						<relation type="alsoCatalogedBy" num="0"/>
					</c:when>
					<c:otherwise>
						<relation type="alsoCatalogedBy" num="${alsoCatalogedByCount}">
							${alsoCatalogedByRecords}
						</relation>
					</c:otherwise>
				</c:choose>
			</c:when>
			<%-- Handle all other relations --%>
			<c:otherwise>
				<c:set var="numRelations" value="${fn:length(headDocReader.relatedRecordsMap[relationType])}"/>
				<c:choose>
					<c:when test="${numRelations == 0}">
						<relation type="${relationType}" num="${numRelations}"/>
					</c:when>
					<c:otherwise>
						<relation type="${relationType}" num="${numRelations}">
							<c:forEach items="${headDocReader.relatedRecordsMap[relationType]}" var="relationRecord">
								<c:choose>
									<c:when test="${relationRecord.docReader.isMyCollectionEnabled}">
										<record>
											<c:set var="headDocReaderAM" value="${relationRecord.docReader}"/><%@ include file="HeadElementAM.jsp" %>
<c:if test="${(param['response.mode'] == 'allOff' && f:contains(paramValues['response'],'metadata')) || param['response.mode'] != 'allOff'}">											
<metadata>
${relationRecord.docReader.xmlStripped}
</metadata>
</c:if>
										</record>
									</c:when>
									<c:otherwise>
										<record status="disabled"/>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</relation>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</c:forEach>
</relations>
</c:if>


