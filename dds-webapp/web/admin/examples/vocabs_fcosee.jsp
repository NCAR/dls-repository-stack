<%--
	This page is used to auto-generate controlled vocabulary checklists for the FCOSEE
	site, using the Vocabulary Manager API.  This is ALL it does--this page can be loaded
	into a browser, and then "View source" can be used to copy and paste the resulting HTML
	code into the FCOSEE JSP page, which does not have direct access to the CVMS.
 
	Use the following regexes against the resulting code to apply JSTL logic for maintaining state: 
	MATCH: (\<input type="checkbox"[^\>]+name=")([^\"]+)([^\>]+value=")([^\"]+)"(\s+/>)
	REPLACE: $1$2$3$4"\n<c:set var="testVocab">$4</c:set>\n<c:forEach var="elem" items="\${paramValues.$2}">\n\t<c:if test="${ elem == testVocab }">checked</c:if>\n</c:forEach>$5	
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="/WEB-INF/tlds/c.tld" %>

<jsp:useBean id="exampleVocabsForm" class="edu.ucar.dls.dds.action.form.VocabForm" scope="request"/>
<%-- Indicate the UI system.interface.language trio (XML groups file) to be used for generating vocab display: --%>
<jsp:setProperty name="exampleVocabsForm" property="vocabInterface" value="dds.descr.en-us"/>
<style type="text/css"><!--
	.vocabHeader { font-weight: bold; text-decoration: none; }
-->
</style>
<script type="text/javascript">
<!--
function ToggleVisibility( strElementID ) {
	var objElement = document.getElementById( strElementID );
	if ( objElement != null ) {
		if ( objElement.style.display == '' )
			objElement.style.display = 'none';
		else
			objElement.style.display = '';
	}
}
// -->
</script>
<div class="vocabHeader">
	<a href="javascript:ToggleVisibility('GradeRanges');" class="vocabHeader">GRADE LEVELS</a>
</div>
<div id="GradeRanges" class="ClassificationBody" style="display:none; width:100%;">
<table style="width:100%;"><td>
	<jsp:setProperty name="exampleVocabsForm" property="field" value="gradeRange" />
	<c:forEach var="vocabTerm" items="${exampleVocabsForm.vocabList}">
		<c:if test="${ !vocabTerm.noDisplay }">
			<input type="checkbox" id="<c:out value="${vocabTerm.name}"/>_id" 
				name="gradeRange"
				value="<c:out value="${vocabTerm.name}"/>"
				<c:forEach var="elem" items="${paramValues.gradeRange}">
					<c:if test="${ elem == vocabTerm.name }">checked</c:if> 
				</c:forEach>		
			/>
			<label for="<c:out value="${vocabTerm.name}"/>_id"><c:out value="${vocabTerm.label}"/></label>
			<br/>
			<c:if test="${ vocabTerm.wrap }">
				</td><td>
			</c:if>
		</c:if>
	</c:forEach>
</td></table>
</div>

<div class="vocabHeader">
	<a href="javascript:ToggleVisibility('ResourceTypes');" class="vocabHeader">RESOURCE TYPES</a>
</div>
<div id="ResourceTypes" class="ClassificationBody" style="display:none; width:100%;">
<table style="width:100%;"><td valign=top nowrap>
	<jsp:setProperty name="exampleVocabsForm" property="field" value="resourceType" />
	<c:forEach var="vocabTerm" items="${exampleVocabsForm.vocabList}">
		<c:if test="${ !vocabTerm.noDisplay }">
			<c:choose>
				<c:when test="${ !vocabTerm.hasSubList }">
					<input type="checkbox" id="<c:out value="${vocabTerm.name}"/>_id" 
						name="resourceType"
						value="<c:out value="${vocabTerm.name}"/>"
						<c:forEach var="elem" items="${paramValues.resourceType}">
							<c:if test="${ elem == vocabTerm.name }">checked</c:if> 
						</c:forEach>			
					/><label for="<c:out value="${vocabTerm.name}"/>_id"><c:out value="${vocabTerm.label}"/></label>
					<br/>				
					<c:if test="${ vocabTerm.wrap }">
						</td><td valign=top nowrap>
					</c:if>
					<c:if test="${ vocabTerm.isLastInSubList }">
						</td></table>
						</div>
					</c:if>
				</c:when>
				<c:when test="${ vocabTerm.hasSubList }">
					<div class="ClassificationHeader" style="margin:0px; padding:0px;">
						<a href="javascript:ToggleVisibility('<c:out value="${vocabTerm.name}"/>_list');"><b><c:out 
							value="${vocabTerm.label}"/></b></a>
					</div>
					<div id="<c:out value="${vocabTerm.name}"/>_list" class="ClassificationBody" style="display:none; width:100%;">
					<table style="width:100%;"><td>
				</c:when>				
			</c:choose>			
		</c:if>
	</c:forEach>
</td></table>
</div>

<div class="vocabHeader">
	<a href="javascript:ToggleVisibility('Subjects');" class="vocabHeader">RESOURCE SUBJECTS</a>
</div>
<div id="Subjects" class="ClassificationBody" style="display:none; width:100%;">
<table style="width:100%;"><td valign=top nowrap>
	<jsp:setProperty name="exampleVocabsForm" property="field" value="subject" />
	<c:forEach var="vocabTerm" items="${exampleVocabsForm.vocabList}">
		<c:if test="${ !vocabTerm.noDisplay }">
			<c:choose>
				<c:when test="${ !vocabTerm.hasSubList }">
					<input type="checkbox" id="<c:out value="${vocabTerm.name}"/>_id" 
						name="subject"
						value="<c:out value="${vocabTerm.name}"/>"
						<c:forEach var="elem" items="${paramValues.subject}">
							<c:if test="${ elem == vocabTerm.name }">checked</c:if> 
						</c:forEach>			
					/><label for="<c:out value="${vocabTerm.name}"/>_id"><c:out value="${vocabTerm.label}"/></label>
					<br/>				
					<c:if test="${ vocabTerm.wrap }">
						</td><td valign=top nowrap>
					</c:if>
					<c:if test="${ vocabTerm.isLastInSubList }">
						</td></table>
						</div>
					</c:if>
				</c:when>
				<c:when test="${ vocabTerm.hasSubList }">
					<div class="ClassificationHeader" style="margin:0px; padding:0px;">
						<a href="javascript:ToggleVisibility('<c:out value="${vocabTerm.name}"/>_list');"><b><c:out 
							value="${vocabTerm.label}"/></b></a>
					</div>
					<div id="<c:out value="${vocabTerm.name}"/>_list" class="ClassificationBody" style="display:none; width:100%;">
					<table style="width:100%;"><td>
				</c:when>				
			</c:choose>			
		</c:if>
	</c:forEach>
</td></table>
</div>
