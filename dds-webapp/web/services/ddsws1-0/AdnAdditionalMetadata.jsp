
			<additionalMetadata realm="adn">
				<accessionStatus>${docReader.accessionStatus}</accessionStatus>
				<partOfDrc>${docReader.partOfDRC}</partOfDrc>
				<c:forEach items="${docReader.errorTypes}" var="errorType" varStatus="status">
					<error type="${errorType}">${docReader.errorStrings[status.index]}</error>
				</c:forEach>
				<c:choose><%-- Set up which associatedItems to display --%>
					<c:when test="${df.authorizedFor == 'trustedApps'}"> 
						<c:set var="associatedItems" value="${docReader.associatedItemResultDocs}"/>
					</c:when>
					<c:otherwise>				
						<c:set var="associatedItems" value="${docReader.displayableAssociatedItemResultDocs}"/>
					</c:otherwise>
				</c:choose>	
				<c:set target="${df}" value="dlese_collect" property="metaFormat" />
				<c:forEach items="${associatedItems}" var="associatedItem">
					<c:if test="${associatedItem.docReader.isMyCollectionEnabled}">
							<c:set value="${associatedItem.docReader.collection}" target="${df}" property="value" />
							<c:set value="key" target="${df}" property="field" />
							<alsoCatalogedBy collectionLabel="${df.vocabTerm.label}" 
						collectionRecordId="${associatedItem.docReader.myCollectionsRecordId}">${associatedItem.docReader.id}</alsoCatalogedBy> 			
					</c:if>
				</c:forEach> <c:set value="key" target="${df}" property="field" />
				<c:forEach items="${docReader.annotationResultDocs}" var="annotation">
					<c:if test="${annotation.docReader.isMyCollectionEnabled}">
						<c:set value="${annotation.docReader.collection}" target="${df}" property="value" />
						<c:set var="statusAttrib"><%-- Adapt the new 1.0 anno status string to the old 0.1 way --%>
							<c:choose>
								<c:when test="${annotation.docReader.format == 'text'}">
									Text annotation ${annotation.docReader.status}
								</c:when>
								<c:when test="${annotation.docReader.format == 'audio'}">
									Audio annotation ${annotation.docReader.status}
								</c:when>
								<c:when test="${annotation.docReader.format == 'graphical'}">
									Graphical annotation ${annotation.docReader.status}
								</c:when>
								<c:when test="${annotation.docReader.format == 'video'}">
									Video annotation ${annotation.docReader.status}
								</c:when>
								<c:otherwise>
									${annotation.docReader.format} annotation ${annotation.docReader.status}
								</c:otherwise>
							</c:choose>
						</c:set>
						<annotatedBy collectionLabel="${df.vocabTerm.label}" 
					collectionRecordId="${annotation.docReader.myCollectionsRecordId}" type="${annotation.docReader.type}" status="${statusAttrib}">
							<id>${annotation.docReader.id}</id>
							<c:if test="${not empty annotation.docReader.title}"><title><c:out value="${annotation.docReader.title}" escapeXml="true"/></title></c:if>
							<c:if test="${not empty annotation.docReader.url}"><url><c:out value="${annotation.docReader.url}" escapeXml="true"/></url></c:if>
							<c:if test="${not empty annotation.docReader.description}"><description><c:out value="${annotation.docReader.description}" escapeXml="true"/></description></c:if>
						</annotatedBy>
					</c:if>
				</c:forEach>
			</additionalMetadata>


