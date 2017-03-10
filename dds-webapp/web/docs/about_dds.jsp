<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
    <title>About DDS</title>

    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <META NAME="description" CONTENT="Information about the Digital Discovery System (DDS) software.">

    <%@ include file="/nav/head.jsp" %>

</head>

<body>
<c:if test="${isDeploayedAtDL}">
    <div class="dlese_sectionTitle" id="dlese_sectionTitle">
        About<br/>DDS
    </div>
    <div id="ddsLogo">
        <a href="${pageContext.request.contextPath}/dds_overview.jsp"><img border="0"
                                                                           alt="Digital Discovery System (DDS)"
                                                                           title="Digital Discovery System (DDS)"
                                                                           src="${pageContext.request.contextPath}/images/dds_logo_sm.gif"/></a>
    </div>
</c:if>
<%@ include file="/nav/top.jsp" %>

<div class="bodyContent">
    <h1>About DDS</h1>

    <p>The Digital Discovery System (DDS) software is developed by Digital Learning Sciences (DLS) at the
        <a href="http://www.ucar.edu/">University Corporation for Atmospheric Research</a> (UCAR).</p>

    <p>
        Support for this software has been provided by the <a href="http://www.nsf.gov/">National Science
        Foundation</a> (NSF) under grants for the National Science Digital Library
        (NSDL), the Digital Library for Earth System Education (DLESE), the
        Institute of Cognitive Science, University of Colorado at Boulder, and other projects.
    </p>


    <a name="changes"></a>
    <h2>Version</h2>
    <p>This is DDS version @VERSION@. Read the latest <a href="CHANGES.txt">release notes and changes</a>.


    <a name="license"></a>
    <h2>License</h2>
    <p>DDS is released under the <a href="license/LICENSE.txt">Apache License v2</a>.</p>

    <p>This product includes software developed by the Apache Software Foundation (http://www.apache.org/).
        This product includes software developed by the Alexandria Digital Library, University of California at Santa
        Barbara, and its contributors.
        Additional third party licenses and notices may be found in the <i>license</i> folder in the DDS software
        distribution.</p>


</div>

<%@ include file="/nav/bottom.jsp" %>
</body>
</html>
