<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>

<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>

<c:set value="DDS Software: Download" var="title"/>

<html>
<head>
    <title>${title}</title>

    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <META NAME="keywords" CONTENT="DLS, digital learning sciences, digital discovery system">
    <META NAME="description" CONTENT="Digital Discovery System (DDS) Software Download">
    <META NAME="organization" CONTENT="Digital Learning Sciences">

    <%-- Template header (CSS styles and JavaScript references) --%>
    <%@ include file="/nav/head.jsp" %>


</head>

<body bgcolor="#FFFFFF" text="#000000"
      link="#220066" vlink="#006600" alink="#220066">
<c:if test="${isDeploayedAtDL}">
    <div class="dlese_sectionTitle" id="dlese_sectionTitle">
        DDS<br/>Software
    </div>
    <div id="ddsLogo">
        <a href="${pageContext.request.contextPath}/dds_overview.jsp"><img border="0"
                                                                           alt="Digital Discovery System (DDS)"
                                                                           title="Digital Discovery System (DDS)"
                                                                           src="${pageContext.request.contextPath}/images/dds_logo_sm.gif"/></a>
    </div>
</c:if>
<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>

<%-- Generate the download links automatically using the XML config: --%>
<c:import
        url="product_release_links.jsp?xmlFileLocation=http://www.dlese.org/downloads/dds/product_release_config.xml"/>


<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>

</body>
</html>


