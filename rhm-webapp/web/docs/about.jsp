<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../TagLibIncludes.jsp" %>

<html>

<head>
<title>About Harvest Repository Manager</title>
<c:import url="../head.jsp"/></head>

<body>
<c:import url="../top.jsp?sec=doc"/>

<h1>About Harvest Repository Manager</h1>
<p>The Harvest Repository Manager manages harvests for ingest into the UCAR Repository.</p>

<h3>Version</h3>
<p>This is Harvest Repository Manager software version @VERSION@.</p>
<p>
  Read the <a href="<c:url value='/docs/CHANGES.txt'/>" title="Read release notes and change information">release notes and changes</a>.</p>


<p>&nbsp;</p>
<p>&nbsp;</p>


<c:import url="../bottom.jsp?page=about"/>

</body>
</html>


