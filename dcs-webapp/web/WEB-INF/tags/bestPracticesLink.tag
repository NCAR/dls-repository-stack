<%-- bestPracticesLink.tag
	- displays link that will access best practices information
		required parameters
	- pathArg - xpath to field
	- fieldName - used in tooltip
	
	Customization
	- a "bestPracticesLabel" can be configured (framework-config) (defaults to "best practices")
	- to customize behavior, create your own action url and call "openBestPracticesWindow"
	- to customize further, replace the jsp code below with your own.
	
	accessible beans include
	-- sef - SchemEditForm
	-- fieldInfo - sef
	-- user - current session User, attributes include firstName, lastName, username, email, institution, etc
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://dls.ucar.edu/tags/dlsELFunctions" %>


<%@ attribute name="pathArg" required="true" %>			<%-- xpath to field --%>
<%@ attribute name="fieldName" required="true" %>		<%-- fieldName - used in tooltip --%>

<%-- default label is "best practices" --%>
<c:set var="label" value="${not empty sef.framework.bestPracticesLabel ?
					  sef.framework.bestPracticesLabel : 'best practices'}"/>
				
<c:set var="xmlFormat"><c:choose>
				<c:when test="${not empty sef.framework.xmlFormat}">${sef.framework.xmlFormat}</c:when>
				<%-- <c:when test="${not empty viewForm}">${viewForm.docReader.nativeFormat}</c:when> --%>
				<c:when test="${not empty viewForm.framework.xmlFormat}">${viewForm.framework.xmlFormat}</c:when>
				<c:otherwise>??</c:otherwise>
				</c:choose></c:set>
					  
<span class="action-button">
	<c:choose>
		<c:when test="${true}">

			<%-- approach 1 - customizable only via bestPracticesLabel --%>
				<a href="javascript:doBestPractices('${f:URLEncoder(pathArg)}', '${xmlFormat}');"
				   title="see best practices for the ${fieldName} field">${label}</a>
		</c:when>
		
		<c:when test="${false}">
			<%-- approach 2 - create custom url and call "openBestPracticesWindow" --%>
			<c:set var="bpUrl">../editor/edit.do?command=bestpractices&xmlFormat=${xmlFormat}&pathArg=${f:URLEncoder(pathArg)}</c:set>
				<a href="javascript:openBestPracticesWindow('${bpUrl}');"
				   title="see best practices for the ${fieldName} field">${label}</a>
		</c:when>
		<c:when test="${false}">
			<%-- approach 3 - simulate SMILE link --%>
			<c:set var="fieldInfo" value="${sef.fieldInfoReader}"/>
			<c:set var="smile_wiki_url"
			value="http://howtosmile.org/feedback/smile_redirect.php?field=${f:URLEncoder(pathArg)}&recID=${sef.recId}&user=${user.username}"/>

			<a href="javascript:openBestPracticesWindow('${smile_wiki_url}');"
			   title="see best practices for the ${fieldName} field">${label}</a>
		</c:when>
		
	</c:choose>
</span>
