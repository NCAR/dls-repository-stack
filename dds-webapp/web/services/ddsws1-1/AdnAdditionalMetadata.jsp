
			<additionalMetadata>
				<adn>
					<accessionStatus>${headDocReader.accessionStatus}</accessionStatus>
					<partOfDrc>${headDocReader.partOfDRC}</partOfDrc>
					<c:forEach items="${headDocReader.errorTypes}" var="errorType" varStatus="status">
						<error type="${errorType}">${headDocReader.errorStrings[status.index]}</error>
					</c:forEach>
					
					<c:choose><%-- Set up which relatedResources to display --%>
						<c:when test="${df11.authorizedFor == 'trustedApps'}"> 
							<c:set var="relatedResources" value="${headDocReader.multiRelatedResources}"/>
						</c:when>
						<c:otherwise>				
							<c:set var="relatedResources" value="${headDocReader.multiDisplayableRelatedResources}"/>
						</c:otherwise>
					</c:choose>					
					<c:if test="${not empty relatedResources}">
						<relatedResources>
						<c:forEach items="${relatedResources}" var="relatedResource">
							<relatedResource specifiedByRecordId="${relatedResource.specifiedById}">
								<c:if test="${not empty relatedResource.kind}">
									<kind><c:out value="${relatedResource.kind}" escapeXml="true"/></kind>
								</c:if>
								<c:if test="${not empty relatedResource.url}">
									<url><c:out value="${relatedResource.url}" escapeXml="true"/></url>
								</c:if>							
								<c:if test="${not empty relatedResource.title}">
									<title><c:out value="${relatedResource.title}" escapeXml="true"/></title>
								</c:if>
								<c:if test="${not empty relatedResource.id}">
									<id><c:out value="${relatedResource.id}" escapeXml="true"/></id>
								</c:if>							
							</relatedResource>
						</c:forEach>
						</relatedResources>
					</c:if>
					<c:choose><%-- Set up which associatedItems to display --%>
						<c:when test="${df11.authorizedFor == 'trustedApps'}"> 
							<c:set var="associatedItems" value="${headDocReader.associatedItemResultDocs}"/>
						</c:when>
						<c:otherwise>				
							<c:set var="associatedItems" value="${headDocReader.displayableAssociatedItemResultDocs}"/>
						</c:otherwise>
					</c:choose>
					<c:forEach items="${associatedItems}" var="associatedItem">
						<c:if test="${associatedItem.docReader.isMyCollectionEnabled}">
							<alsoCatalogedBy>
								<record>
									<c:set var="headDocReaderAM" value="${associatedItem.docReader}"/><%@ include file="HeadElementAM.jsp" %>
									<metadata>
										${associatedItem.docReader.xmlStripped}
									</metadata>
								</record>						
							</alsoCatalogedBy>
						</c:if>
					</c:forEach> <c:set value="key" target="${df11}" property="field" />
					<c:if test="${!f:contains(paramValues.relation,'isAnnotatedBy')}"><%-- Do not show annotations here if they are being output in the relations element --%>
						<c:forEach items="${headDocReader.annotationResultDocs}" var="annotation">
							<c:if test="${annotation.docReader.isMyCollectionEnabled}">
							<annotatedBy>
								<record>
									<c:set var="headDocReaderAM" value="${annotation.docReader}"/><%@ include file="HeadElementAM.jsp" %>
									<metadata>
										${annotation.docReader.xmlStripped}
									</metadata>
								</record>
							</annotatedBy>
							</c:if>
						</c:forEach>
					</c:if>
					<renderingGuidelines><c:set var="vocabMetaFormat" value="adn"/>
						<gradeRangeOPML><c:set var="vocabField" value="gr"/><c:set var="vocabItems" value="${headDocReader.multiGradeRanges}"/>
							<%@ include file="OPMLOutput.jsp" %>
						</gradeRangeOPML>
						<resourceTypeOPML><c:set var="vocabField" value="re"/><c:set var="vocabItems" value="${headDocReader.multiResourceTypes}"/>
							<%@ include file="OPMLOutput.jsp" %>
						</resourceTypeOPML>
						<subjectOPML><c:set var="vocabField" value="su"/><c:set var="vocabItems" value="${headDocReader.multiSubjects}"/>
							<%@ include file="OPMLOutput.jsp" %>
						</subjectOPML>	
						<contentStandardOPML><c:set var="vocabField" value="cs"/><c:set var="vocabItems" value="${headDocReader.multiContentStandards}"/>
							<%@ include file="OPMLOutput.jsp" %>
						</contentStandardOPML>					
					</renderingGuidelines>					
				</adn>
			</additionalMetadata>


