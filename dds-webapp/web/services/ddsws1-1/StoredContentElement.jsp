<c:if test="${not empty param.storedContent}"><%-- Show content from stored Lucene fields, if requested. Place the record's doc reader in var 'headDocReader' before including 
--%><c:set var="isMultiRecordMode" value="${param['storedContent.mode'] == 'multiRecord'}"/>
<c:if test="${!(isMultiRecordMode && isRelatedRecord)}">
<c:set var="myDocMap" value="${isMultiRecordMode ? headDocReader.lazyDocumentMapValuesArrayMultiRecord : headDocReader.lazyDocMapValuesArray}"/>
<storedContent mode="${isMultiRecordMode ? 'multiRecord' : 'singleRecord'}"><c:forEach 
	items="${paramValues.storedContent}" 
	var="field"><c:forEach items='${myDocMap[field]}' var="theValue">
	<content fieldName="<c:out value='${field}' escapeXml='true'/>"><c:out value='${theValue}' escapeXml='true'/></content></c:forEach></c:forEach>
</storedContent></c:if></c:if>
