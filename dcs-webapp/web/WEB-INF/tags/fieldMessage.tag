<%-- fieldMessage.tag
	- used to display messages associated with generic input fields (the metadata editor uses "elementMessages"
	- requires that a propertyId param is set in calling page 
	- diplays error messages relevant to a particular field (identified by propertyId)
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>

<%@ attribute name="propertyId" required="true" %>

<logic:messagesPresent property="${propertyId}">
	<html:messages id="msg" property="${propertyId}">
		<div class="error-msg">${msg}</div>
	</html:messages>
</logic:messagesPresent>
