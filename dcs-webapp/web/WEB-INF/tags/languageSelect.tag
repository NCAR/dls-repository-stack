<%--  languageSelect
 
   
--%> 

<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>

<%@ attribute name="elementPath" required="true" %>
<%@ attribute name="styleId" %>

<logic:notEmpty name="sef" property="fieldInfo(${elementPath})">
	<bean:define id="fieldInfo" name="sef" property="fieldInfo(${elementPath})" />
	<c:if test="${not empty fieldInfo.vocabTerms}">
 		<html:select name="sef" property="valueOf(${elementPath})" styleId="${styleId}">
			<html:optionsCollection name="fieldInfo" property="termSelectOptions"/>
		</html:select>
	</c:if>
	<c:if test="${empty fieldInfo.vocabTerms}">
		<i>error: fieldInfo.vocabTerms is empty</i>
		</c:if>
</logic:notEmpty>

<logic:empty name="sef" property="fieldInfo(${elementPath})">
	<html:text property="valueOf(${elementPath})" styleId="${styleId}" />
	<%-- fieldInfo not found for ${elementPath} --%>
</logic:empty>
