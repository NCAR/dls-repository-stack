<%@ tag isELIgnored ="false" %><%@ 
tag language="java" %><%@ 
taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
taglib prefix="html" uri="/WEB-INF/tlds/struts-html.tld" %>

<%@ attribute name="user" required="true" type="edu.ucar.dls.schemedit.security.user.User" %>
<c:if test='${applicationScope["authenticationEnabled"]}'>
	<c:if test="${not empty user}">
		User: ${user.username} <%-- (${user.role}) --%>
		&nbsp;|&nbsp;
		<html:link forward="logoff">logout</html:link>
	
	</c:if>
	<c:if test="${empty user}">
		<html:link forward="logon">login</html:link>
	</c:if>
</c:if>

