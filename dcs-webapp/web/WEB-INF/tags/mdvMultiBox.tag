<%--  mdvMultiBox
 
 - this is a modified version of resourceTag1.
		
 - it requires a parameter named elementPath. e.g. here is a call for this tag:
 		<st:mdvMultiBox elementPath="enumerationValuesOf(/itemRecord/educational/resourceTypes/resourceType)"/>
 	
 - this tag produces a static muli-level display. 
 - The very top level is not displayed, only the lower levels. 
   e.g., in the case of "resourceType" there is no header for "resourceType". The header is displayed by
   Renderer.

 - this tag will be produced by Renderer.getMultiBoxInput for the cases when a MVS mapping can be found for a 
   particular xpath
   
--%> 

<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ attribute name="elementPath" required="true" %>

<%-- Indicate the UI system.interface.language trio (XML groups file) to be used for generating vocab display: --%>
<jsp:setProperty name="sef" property="vocabInterface" value="dds.descr.en-us"/>
<jsp:setProperty name="sef" property="vocabField" value="${elementPath}" />

<%-- evaluate the currently selected values at elementPath and assign to selectedValues param --%>
<bean:define id="selectedValues" name="sef" property="${elementPath}"/>
<input type="hidden" name="${elementPath}" value="" />

<%-- last level - to keep track of the level of the last item displayed --%>
<c:set var="lastLevel" value="1"/>

<div>
<table style="width:100%;"><td valign=top nowrap>
	<c:forEach var="vocabTerm" items="${sef.vocabList}">
		<c:if test="${ !vocabTerm.noDisplay }">
			<c:choose>
				<c:when test="${ !vocabTerm.hasSubList }">
					<c:set var="checkBoxId" value="${sf:pathToId(elementPath)}_${vocabTerm.name}_id" />
					<input type="checkbox" id="${checkBoxId}" 
						name="${elementPath}"
						value="<c:out value="${vocabTerm.name}"/>"
						<c:forEach var="elem" items="${selectedValues}">
							<c:if test="${ elem == vocabTerm.name }">checked</c:if> 
						</c:forEach>			
					/><label 
						<c:if test="${not empty vocabTerm.definition}">title="${vocabTerm.definition}"</c:if>
						for="${checkBoxId}"><c:out value="${vocabTerm.label}"/>
						</label>
					<br/>
					<c:if test="${ vocabTerm.wrap }">
						</td><td valign=top nowrap>
					</c:if>
					<c:if test="${ vocabTerm.isLastInSubList }">
						</td></table></div>
					</c:if>
				</c:when>
				<c:when test="${ vocabTerm.hasSubList }">
					<%-- close open table elements until the groupLevel is reached --%>
				   <c:forEach var="item" begin="${vocabTerm.groupLevel + 1}" end="${lastLevel}">
						</td></table></div>
					</c:forEach>
					<div  style="margin:0px; padding:0px;"
						<c:if test="${not empty vocabTerm.definition}">title="${vocabTerm.definition}"</c:if>
					>
					<b>${vocabTerm.label}</b>
					</div>
					<div style="margin-left:15px;">
					<table style="width:100%;"><td>
					<c:set var="lastLevel" value="${vocabTerm.groupLevel}"/>
				</c:when>
			</c:choose>			
		</c:if>
	</c:forEach>
<%-- close up any open table elements --%>
 <c:forEach var="item" items="${iterationArray}" begin="1" end="${lastLevel}" >
	</td></table></div>
</c:forEach>


