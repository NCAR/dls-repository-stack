<%@ tag isELIgnored="false" 
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" 
%><%@ attribute name="title" required="true" type="java.lang.String"%>

<c:if test="${not empty applicationScope.instanceName}">${applicationScope.instanceName} - </c:if>${title}
