<c:if test="${(param['response.mode'] == 'allOff' && f:contains(paramValues['response'],'head')) || param['response.mode'] != 'allOff'}">
	<head><%-- Outputs the head element for records appearing in relation and Additional Metadata element. Place the record's doc reader in var 'headDocReaderAM' before including --%>
		<id>${headDocReaderAM.id}</id> 
		<xmlFormat nativeFormat="${headDocReaderAM.nativeFormat}">${headDocReaderAM.nativeFormat}</xmlFormat><%-- For relations, native format is always returned --%>
		<c:set value="${headDocReaderAM.collection}" target="${df11}" property="value" /><c:set target="${df11}" value="dlese_collect" property="metaFormat" /> <c:set value="key" target="${df11}" property="field" />
		<collection recordId="${headDocReaderAM.myCollectionsRecordId}" ky="${headDocReaderAM.collectionKey}" key="${headDocReaderAM.collection}"><c:choose><c:when test="${df11.isVocabTermAvailable}">${df11.vocabTerm.label}</c:when><c:otherwise><c:out value="${headDocReaderAM.myCollectionDoc.shortTitle}"/></c:otherwise></c:choose></collection>		
		<fileLastModified>${headDocReaderAM.lastModifiedAsUTC}</fileLastModified> 
		<c:set var="wnDate" value="${headDocReaderAM.whatsNewDateDate}"/>
		<c:if test="${not empty wnDate}">
		<whatsNewDate type="${headDocReaderAM.whatsNewType}"><fmt:formatDate value="${wnDate}" pattern="yyyy-MM-dd"/></whatsNewDate>
		</c:if>                                                                                                                                                                                               
	</head>
</c:if>


