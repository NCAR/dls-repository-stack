<%--  mdvMultiBoxStatic
 
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
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ tag import="edu.ucar.dls.schemedit.display.CollapseBean" %>

<%@ attribute name="elementPath" required="true" %>


<%-- Indicate the UI system.interface.language trio (XML groups file) to be used for generating vocab display: --%>
<jsp:setProperty name="sef" property="vocabInterface" value="dds.descr.en-us"/>
<jsp:setProperty name="sef" property="vocabLanguage" value="en-us"/>
<jsp:setProperty name="sef" property="vocabAudience" value="cataloger"/>
<jsp:setProperty name="sef" property="vocabField" value="${elementPath}" />

<%-- evaluate the currently selected values at elementPath and assign to selectedValues param --%>
<bean:define id="selectedValues" name="sef" property="${elementPath}"/>
<bean:define id="collapseBean" name="sef" property="collapseBean" type="CollapseBean" />
<%-- last level - to keep track of the level of the last item displayed --%>
<c:set var="lastLevel" value="1"/>

<div>
<h4>muiDEBUGMultiBox</h4>
<table style="width:100%;"><td valign=top nowrap>
	<c:forEach var="vocabTerm" items="${sef.vocabList}">
		<c:if test="${ !vocabTerm.noDisplay }">
			<c:choose>
			
				<%-- leaf node --%>
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
				
				<%-- node with children --%>
				<c:when test="${ vocabTerm.hasSubList }">  
					<%-- close open table elements until the groupLevel is reached --%>
				   <c:forEach begin="${vocabTerm.groupLevel + 1}" end="${lastLevel}">
						</td></table></div>
					</c:forEach>
					<%-- here is the label --%>
					<c:choose>
						<c:when test="${vocabTerm.collapsible}" >
							<c:set var="vocabId" value="${sf:pathToId(vocabTerm.label)}" />
							<jsp:setProperty name="collapseBean" property="id" value="${vocabId}" />
							<input type="hidden" name="${vocabId}_displayState" value="${sef.collapseBean.displayState}" />
							<div style="margin:0px; padding:0px;"
								<c:if test="${not empty vocabTerm.definition}">title="${vocabTerm.definition}"</c:if>
								<c:if test="${ empty vocabTerm.definition}">title="Click to open/close"</c:if>
							><a href="javascript:toggleMuiVisibility('${vocabId}');"
								class="vocabHeading"><img id="${vocabId}_img"
								src="${sef.collapseBean.isOpen ? '../images/btnExpand.gif' : '../images/btnExpandClsd.gif'}" 
								alt="Click to show/hide" border="0" hspace="5" 
								width="11" hight="11"><b>${vocabTerm.label}</b></a>
							</div>
							<div style="margin-left:15px;display:${sef.collapseBean.displayState}"  id="${vocabId}">					
						</c:when>
					  <c:otherwise>
							<div style="margin:0px; padding:0px;"
								<c:if test="${not empty vocabTerm.definition}">title="${vocabTerm.definition}"</c:if>
							><b>${vocabTerm.label}</b><c:if test="${vocabTerm.collapsible}">&nbsp;<b>*</b></c:if>
							</div>
							<div style="margin-left:15px">
						</c:otherwise>
					</c:choose>
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


