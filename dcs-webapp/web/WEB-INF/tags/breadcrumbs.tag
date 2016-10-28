<%-- breadcrumbs.tag
	wraps content in a table formating breadcrumbs
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ tag body-content="scriptless" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<table cellpadding=0 cellspacing=0><tr><td><div class="breadcrumbs"><jsp:doBody /></div></td></tr></table>

