<%-- Renders the OPML output - set vars vocabField and vocabMetaFormat before using 
--%><vocabs:setResponseGroup 
	metaFormat="${vocabMetaFormat}" 
	audience="${initParam.metadataVocabAudience}"
	language="${initParam.metadataVocabLanguage}" 
	field="${vocabField}" /><c:forEach items="${vocabItems}" var="resp"><vocabs:setResponseValue value="${resp}" /></c:forEach>
<vocabs:getResponseOPML/>

