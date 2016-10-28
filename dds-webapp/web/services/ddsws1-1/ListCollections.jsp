@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<DDSWebService xmlns="http://www.dlese.org/Metadata/ddsws" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.dlese.org/Metadata/ddsws http://www.dlese.org/Metadata/ddsws/1-1/ddsws.xsd">
	<ListCollections>
		<searchField>ky</searchField>
		<collections> <c:set value="key" target="${df11}" property="field" />
		<c:set value="dlese_collect" target="${df11}" property="metaFormat" />
		<c:forEach items="${df11.results}" var="result">
			<c:set var="headDocReader" value="${result.docReader}"/>				
			<c:set value="${headDocReader.key}" target="${df11}" property="value" />
			<c:set var="vocabTerm" value="${df11.vocabTerm}"/>			
			<collection>
				<vocabEntry>${vocabTerm.name}</vocabEntry>
				<searchKey>${vocabTerm.id}</searchKey>
				<recordId>${headDocReader.id}</recordId>
<c:if test="${not empty param.response}"><%-- Note: This element also exists in RespOutputElement.jsp - change there too!
Output specific content requested by the consumer. (expandable to more as needed)... 
--%><c:forEach items="${paramValues.response}" var="respType">
		<c:choose>	
			<c:when test="${respType == 'collectionMetadata'}">
<collectionMetadata>
<!-- The full collection record -->
${headDocReader.xmlStripped}
</collectionMetadata>
			</c:when>
			<c:otherwise>
<!-- 
Note: The 'response' argument indicated, '${respType}', is not valid.
Must be one of ['collectionMetadata']"/> Response output request ignored.
-->
			</c:otherwise>
		</c:choose>
	</c:forEach></c:if>
				<%@ include file="DleseCollectAdditionalMetadata.jsp" %>
				<renderingGuidelines><%-- Only supply rendering guidelines if they are available --%>
				<c:choose>
					<c:when test="${df11.isVocabTermAvailable}">
						<label>${vocabTerm.label}</label>
						<c:if test="${not empty vocabTerm.labelAbbrev}"><labelAbbrev>${vocabTerm.labelAbbrev}</labelAbbrev></c:if>					
						<noDisplay>${vocabTerm.noDisplay || !headDocReader.isEnabled}</noDisplay>
						<wrap>${vocabTerm.wrap}</wrap>
						<divider>${vocabTerm.divider}</divider>
						<hasSubList>${vocabTerm.hasSubList}</hasSubList>
						<isLastInSubList>${vocabTerm.isLastInSubList}</isLastInSubList>
						<groupLevel>${vocabTerm.groupLevel}</groupLevel> 
					</c:when>
					<c:otherwise>
						<label>${result.docReader.shortTitle}</label>
						<noDisplay>${!headDocReader.isEnabled}</noDisplay>
					</c:otherwise>
				</c:choose>
				</renderingGuidelines>
			</collection>
		</c:forEach>
		</collections>
	</ListCollections>
</DDSWebService>
