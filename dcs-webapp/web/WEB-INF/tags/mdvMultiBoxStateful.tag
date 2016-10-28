<%--  mdvMultiBoxStateful
 
 - collapsible, stateful multibox display supported by MetadataVocab. Hierarchies are collapsible and they
 	retain their state.
		
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
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="edu.ucar.dls.schemedit.display.CollapseBean" %>

<%@ attribute name="elementPath" required="true" type="java.lang.String"%>


<%-- Indicate the UI system.interface.language trio (XML groups file) to be used for generating vocab display: --%>
<jsp:setProperty name="sef" property="vocabInterface" value="dds.descr.en-us"/>
<jsp:setProperty name="sef" property="vocabField" value="${elementPath}" />

<%-- evaluate the currently selected values at elementPath and assign to selectedValues param --%>
<bean:define id="selectedValues" name="sef" property="${elementPath}"/>


<bean:define id="collapseBean" name="sef" property="collapseBean" type="CollapseBean" />

<%-- last level - to keep track of the level of the last item displayed --%>
<c:set var="lastLevel" value="1"/>

<div>
<table style="width:100%;"><td valign=top nowrap>
	<c:forEach var="vocabTerm" items="${sef.vocabList}">
		<c:if test="${ !vocabTerm.noDisplay }">
			<c:choose>
				<c:when test="${ !vocabTerm.hasSubList }">
					<input type="checkbox" id="<c:out value="${vocabTerm.name}"/>_id" 
						name="${elementPath}"
						value="<c:out value="${vocabTerm.name}"/>"
						<c:forEach var="elem" items="${selectedValues}">
							<c:if test="${ elem == vocabTerm.name }">checked</c:if> 
						</c:forEach>			
					/><label 
						<c:if test="${not empty vocabTerm.definition}">title="${vocabTerm.definition}"</c:if>
						for="<c:out value="${vocabTerm.name}"/>_id"><c:out value="${vocabTerm.label}"/>
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
				   <c:forEach begin="${vocabTerm.groupLevel + 1}" end="${lastLevel}">
						</td></table></div>
					</c:forEach>
					
					<%-- define variable to hold the key/id for this element --%>
					<c:set var="id" value="${sf:pairToId(elementPath,vocabTerm.label)}" scope="page" />
					
					<%-- use jsp:setProperty to set the id prop of collapseBean prior to accessing state --%>
					<jsp:setProperty name="collapseBean" property="id" value="${id}" />
					
					<%-- set hidden var to currentState --%>
					<input type="hidden" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
					
					<%-- here is the label --%>
					<div style="margin:0px; padding:0px;"
						<c:if test="${not empty vocabTerm.definition}">title="${vocabTerm.definition}"</c:if>
						<c:if test="${ empty vocabTerm.definition}">title="Click to open/close"</c:if>
					><a href="javascript:toggleDisplayState('${id}');"
						class="vocabHeading"><img 
						src='../images/btnExpand.gif' alt="Click to show/hide" border="0" hspace="5" 
						width="11" hight="11"><b>${vocabTerm.label}</b></a>
					</div>
					<div style="margin-left:15px;display:${sef.collapseBean.displayState}" id="${id}">
					<table style="width:100%;"><td>
					<c:set var="lastLevel" value="${vocabTerm.groupLevel}"/>
				</c:when>
			</c:choose>
		</c:if>
	</c:forEach>
<%-- close up any open table elements --%>
 <c:forEach begin="1" end="${lastLevel}" >
	</td></table></div>
</c:forEach>


