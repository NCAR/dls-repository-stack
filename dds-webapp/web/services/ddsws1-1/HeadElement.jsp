<c:if test="${(param['response.mode'] == 'allOff' && f:contains(paramValues['response'],'head')) || param['response.mode'] != 'allOff'}">
	<head><%-- Outputs the head element. Place the record's doc reader in var 'headDocReader' before including --%>
		<id>${headDocReader.id}</id> 
		<c:choose>
			<c:when test="${not empty df11.recordFormat}"><%-- Set in GetRecord --%>
				<c:set var="xmlFormat" value="${df11.recordFormat}"/>
			</c:when>
			<c:otherwise><%-- Set in Search --%>
				<c:set var="xmlFormat" value="${headDocReader.requestedXmlFormatType}"/>
			</c:otherwise>
		</c:choose>
		<xmlFormat nativeFormat="${headDocReader.nativeFormat}">${xmlFormat}</xmlFormat> <c:set value="${headDocReader.collection}" target="${df11}" property="value" /><c:set target="${df11}" value="dlese_collect" property="metaFormat" /> <c:set value="key" target="${df11}" property="field" />			
		<collection recordId="${headDocReader.myCollectionsRecordId}" ky="${headDocReader.collectionKey}" key="${headDocReader.collection}"><c:choose><c:when test="${df11.isVocabTermAvailable}">${df11.vocabTerm.label}</c:when><c:otherwise><c:out value="${headDocReader.myCollectionDoc.shortTitle}"/></c:otherwise></c:choose></collection>
		<fileLastModified>${headDocReader.lastModifiedAsUTC}</fileLastModified> 
		<c:set var="wnDate" value="${headDocReader.whatsNewDateDate}"/>
		<c:if test="${not empty wnDate}">
		<whatsNewDate type="${headDocReader.whatsNewType}"><fmt:formatDate value="${wnDate}" pattern="yyyy-MM-dd"/></whatsNewDate>
		</c:if>
		<c:choose>
			<c:when test="${headDocReader.nativeFormat == 'adn'}">
				<%@ include file="AdnAdditionalMetadata.jsp" %>
			</c:when>
			<c:when test="${headDocReader.nativeFormat == 'dlese_collect'}"> 
				<%@ include file="DleseCollectAdditionalMetadata.jsp" %>
			</c:when>			
			<c:when test="${headDocReader.nativeFormat == 'ncs_collect'}">
				<%@ include file="NcsCollectAdditionalMetadata.jsp" %>
			</c:when>
			<c:when test="${headDocReader.nativeFormat == 'ncs_item'}">
				<%@ include file="NcsItemAdditionalMetadata.jsp" %>
			</c:when>	
		</c:choose>
	</head>
</c:if>


