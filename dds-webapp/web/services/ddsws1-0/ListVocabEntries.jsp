@BLANK@<?xml version="1.0" encoding="UTF-8" ?><%@ include file="TagLibIncludes.jsp" %><%@ page contentType="text/xml; charset=UTF-8" %>
<c:catch var="error">
<DDSWebService>
	<${param.verb}>
		<searchField>${df.fieldId}</searchField>
		<${df.field}s>
		<%-- c:set value="${df.field}" target="${df}" property="field"/ --%>	
		<c:forEach var="vocabTerm" items="${df.vocabList}">
			<${df.field}>
				<vocabEntry>${vocabTerm.name}</vocabEntry>
				<c:if test="${not empty vocabTerm.id}"><searchKey>${vocabTerm.id}</searchKey></c:if>
				<c:if test="${not empty vocabTerm.definition}"><definition>${vocabTerm.definition}</definition></c:if>
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
			</${df.field}>
		</c:forEach>	 
		</${df.field}s>
	</${param.verb}>
</DDSWebService>
</c:catch>
<c:if test="${not empty error}">
 <c:set target="${df}" property="errorMsg">
 	The vocab interface you requested, '${param.vocabInterface},' is not available! 
	The vocabInterface argument may be omitted to use the default interface, which is 'dds.descr.en-us'
 </c:set>
 <jsp:forward page="errors.jsp"/>
</c:if>		

