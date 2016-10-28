<%-- page-messages.tag 
	- requires that a propertyId param is set in calling page 
	- displays messages that pertain to a page (rather than an element)
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="okPath" %>
<%@ attribute name="okText" %>

<%-- ####### Display messages, if present ####### --%>
<logic:messagesPresent> 		
<div class="page-message-block">
		<b>Messages:</b>
		<ul>
			<html:messages id="msg" property="message"> 
				<li class="confirm-msg"><bean:write name="msg"/></li>									
			</html:messages>
			<html:messages id="msg" property="error"> 
				<li class="error-msg">Error: <bean:write name="msg"/></li>									
			</html:messages>
		</ul>
		<c:if test="${not empty okPath}">
			<blockquote>[ <a href="${okPath}">${empty okText ? 'OK' : okText}</a> ]</blockquote>
		</c:if>
</div>
</logic:messagesPresent>
