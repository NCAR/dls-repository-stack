<%-- breadcrumbs.tag
	wraps content in a table formating breadcrumbs
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="contents" required="true"%>
<table><tr><td><div class="breadcrumbs">${contents}</div></td></tr></table>

