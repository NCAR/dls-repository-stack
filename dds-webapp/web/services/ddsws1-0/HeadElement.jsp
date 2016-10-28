	<head><%-- Outputs the head element. Set the index i variable before including --%>
		<id>${docReader.id}</id> <c:set value="${docReader.collection}" target="${df}" property="value" /><c:set target="${df}" value="dlese_collect" property="metaFormat" /> <c:set value="key" target="${df}" property="field" />			
		<collection recordId="${docReader.myCollectionsRecordId}">${df.isVocabTermAvailable ? df.vocabTerm.label : docReader.myCollectionDoc.shortTitle}</collection>
		<c:choose>
			<c:when test="${not empty df.recordFormat}"> 
				<c:set var="xmlFormat" value="${df.recordFormat}"/>
			</c:when>
			<c:otherwise>
				<c:set var="xmlFormat" value="${docReader.nativeFormat}"/>
			</c:otherwise>
		</c:choose>
		<xmlFormat>${xmlFormat}</xmlFormat>
		<fileLastModified>${docReader.lastModifiedAsUTC}</fileLastModified> 
		<c:set var="wnDate" value="${docReader.whatsNewDateDate}"/>
		<c:if test="${not empty wnDate}">
		<whatsNewDate type="${docReader.whatsNewType}"><fmt:formatDate value="${wnDate}" pattern="yyyy-MM-dd"/></whatsNewDate>
		</c:if>
		<c:choose>
			<c:when test="${docReader.nativeFormat == 'adn'}"> 
				<%@ include file="AdnAdditionalMetadata.jsp" %>
			</c:when>
			<c:when test="${docReader.nativeFormat == 'dlese_collect'}"> 
				<%@ include file="DleseCollectAdditionalMetadata.jsp" %>
			</c:when>			
		</c:choose>
	</head>


