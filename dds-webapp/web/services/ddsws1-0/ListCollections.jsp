@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<c:if test="${empty applicationScope.ListCollectionsResponse10}">
<c:set var="ListCollectionsResponse10" scope="application">
<DDSWebService>
	<ListCollections>
		<searchField>ky</searchField>
		<collections> <c:set value="key" target="${df}" property="field" />
		<c:set value="dlese_collect" target="${df}" property="metaFormat" />
		<c:forEach items="${df.results}" var="result">
			<c:set var="headDocReader" value="${result.docReader}"/>				
			<c:set value="${headDocReader.key}" target="${df}" property="value" />
			<c:set var="vocabTerm" value="${df.vocabTerm}"/>
			<collection>
				<vocabEntry>${vocabTerm.name}</vocabEntry>
				<searchKey>${vocabTerm.id}</searchKey>
				<recordId>${headDocReader.id}</recordId>
				<%@ include file="DleseCollectAdditionalMetadata.jsp" %>
				<renderingGuidelines><%-- Only supply rendering guidelines if they are available --%>
				<c:choose>
					<c:when test="${df.isVocabTermAvailable}">
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
</c:set>
</c:if>
${applicationScope.ListCollectionsResponse10} <%-- Output the response... --%>
<c:if test="${not empty param.vocabInterface && param.vocabInterface != 'dds.descr.en-us' }">
	<c:remove var="ListCollectionsResponse10" scope="application"/> <%-- Remove the cache if we're not storing the default response --%>
</c:if>

