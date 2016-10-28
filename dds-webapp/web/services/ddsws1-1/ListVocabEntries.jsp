@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<c:catch var="error">
<DDSWebService xmlns="http://www.dlese.org/Metadata/ddsws" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.dlese.org/Metadata/ddsws http://www.dlese.org/Metadata/ddsws/1-1/ddsws.xsd">
	<${param.verb}>
		<searchField>${df11.fieldId}</searchField>
		<${df11.field}s>
		<%-- c:set value="${df11.field}" target="${df11}" property="field"/ --%>
		<c:forEach var="vocabTerm" items="${df11.vocabList}">
			<${df11.field}>
				<vocabEntry>${vocabTerm.name}</vocabEntry>
				<c:if test="${not empty vocabTerm.id}"><searchKey>${vocabTerm.id}</searchKey></c:if>
				<c:if test="${not empty vocabTerm.definition}"><definition>${vocabTerm.definition}</definition></c:if>
				<c:if test="${not empty vocabTerm}"><%-- Only supply rendering guidelines if they are available --%>
					<renderingGuidelines>
						<label>${vocabTerm.label}</label>
						<c:if test="${not empty vocabTerm.labelAbbrev}"><labelAbbrev>${vocabTerm.labelAbbrev}</labelAbbrev></c:if>					
						<noDisplay>${vocabTerm.noDisplay}</noDisplay>
						<wrap>${vocabTerm.wrap}</wrap>
						<divider>${vocabTerm.divider}</divider>
						<hasSubList>${vocabTerm.hasSubList}</hasSubList>
						<isLastInSubList>${vocabTerm.isLastInSubList}</isLastInSubList>
						<groupLevel>${vocabTerm.groupLevel}</groupLevel>
					</renderingGuidelines>
				</c:if>
			</${df11.field}>
		</c:forEach>		
		</${df11.field}s>
	</${param.verb}>
</DDSWebService>
</c:catch>
<c:if test="${not empty error}">
 <c:set target="${df11}" property="errorMsg">
 	Sorry, the server has encountered an internal error.
	Please try your request again later.
 </c:set>
 <jsp:forward page="errors.jsp"/>
</c:if>		

