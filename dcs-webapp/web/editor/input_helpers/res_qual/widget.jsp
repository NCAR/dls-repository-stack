<c:if test="${not empty sef.suggestionServiceHelper}">

<c:set var="helperPath">${sf:idToPath (id)}/learningGoal/content</c:set>
<c:set var="serviceHelper" value="${sef.suggestionServiceHelper}"/>
<jsp:setProperty name="serviceHelper" property="xpath" value="${helperPath}"/>

<div id="suggestion-widget" style="margin-left:-10px;">

	<c:if test="${false}"> <%-- DEBUGGING INFO--%>
		<style type="text/css">
			.widget-info {font-size:10px;margin-top:3px}
		</style>
		<div class='widget-info'>Helper Xpath: ${helperPath}</div>
		<div class='widget-info'>DisplayMode: ${serviceHelper.displayMode} -----  
			DisplayContent: ${serviceHelper.displayContent}</div>
		<div class='widget-info'>idToPath: ${sf:idToPath (id)}</div>
	</c:if>
	
	<std:standards_MultiBox 
		elementPath="enumerationValuesOf(${serviceHelper.xpath})" 
		pathArg="${serviceHelper.xpath}"/>
</div>

<script type="text/javascript">
 	DisplayState.resQualTarget = '${id}';
</script>


</c:if>
