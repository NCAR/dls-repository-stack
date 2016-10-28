<%@ include file="/lib/includes.jspf" %>

<bean:define id="xpath" name="sef" property="suggestionServiceHelper.xpath" />

<c:choose>
	<c:when test="${sef.suggestionServiceHelper.xmlFormat == 'res_qual'}">
		<res_qual:standards_display elementPath='<%= "enumerationValuesOf(" + xpath + ")" %>'
			 serviceHelper="${sef.suggestionServiceHelper}" />
	</c:when>
	<c:when test="${sef.suggestionServiceHelper.xmlFormat == 'lar'}">
		<res_qual:standards_display elementPath='<%= "enumerationValuesOf(" + xpath + ")" %>'
			 serviceHelper="${sef.suggestionServiceHelper}" />
	</c:when>
	<c:otherwise>
		<std:display elementPath='<%= "enumerationValuesOf(" + xpath + ")" %>'
			 serviceHelper="${sef.suggestionServiceHelper}" />
	</c:otherwise>
</c:choose>



