<c:if test="${not empty param.response}"><%-- Note: This element also exists in ListCollections.jsp - change there too!
Output specific content requested by the consumer. (expandable to more as needed)... 
--%><c:forEach items="${paramValues.response}" var="respType">
		<c:choose><%-- 
		
		Return the full collection metadata for this record:
		
		--%><c:when test="${respType == 'collectionMetadata' || param['response.mode'] == 'allOn'}">
<collectionMetadata>
${headDocReader.myCollectionDoc.xmlStripped}
</collectionMetadata>
			</c:when><%-- 
			
		Return the full collection metadata for all collections that catalog this resoruce:
		
		--%><c:when test="${!isRelatedRecord && respType == 'allCollectionsMetadata'}">
<allCollectionsMetadata>
				<c:forEach items="${headDocReader.myCollectionDocs}" var="collectionDoc">
<collectionMetadata>
${collectionDoc.xmlStripped}
</collectionMetadata>
				</c:forEach>
</allCollectionsMetadata>
			</c:when>
			<c:when test="${respType == 'head' || respType == 'metadata'}"><%-- Valid response handled elsewhere... --%></c:when><%-- 
			
		Handle non-valid request params:
		
		--%><c:otherwise>
<%-- 
Note: The 'response' argument indicated, '${respType}', is not valid.
Must be one of ['collectionMetadata','allCollectionsMetadata']"/> Response output request ignored.
--%>
			</c:otherwise>
		</c:choose>
	</c:forEach>
</c:if>


