<%-- 
Experimental user-popup link. would replace existing username displays and
trigger a pop-up window displaying user info 
see web/user/popup_user_info.jsp
--%>
<%@ tag isELIgnored ="false" %><%@ 
tag language="java" %><%@ 
taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
taglib prefix="html" uri="/WEB-INF/tlds/struts-html.tld" %>

<%@ attribute name="username" required="true" %>

<%-- Where is the trigger to expose popup_user_info.jsp?? --%>
${username}

<%-- <c:if test='${applicationScope["authenticationEnabled"]}'>

</c:if> --%>

