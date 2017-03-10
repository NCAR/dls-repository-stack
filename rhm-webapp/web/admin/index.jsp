<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ include file="../../TagLibIncludes.jsp" %>
<html>
<head>
	<title>Harvest Repository Manager Settings and Configuration</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	
	<%-- Include style/menu templates --%>
	<%@ include file="../../head.jsp" %>

</head>
<body>
<%-- Include style/menu templates --%>
<c:import url="../top.jsp" />

<h1>Admin</h1>

<p> 	     	
	<a href="config.jsp">Server Configuration</a>  <br/>
      View the current software settings and configuration. 
	  To change the settings, edit the context parameters defined in the
	  Tomcat server.xml file, which overrides the default values in the
	  context's WEB-INF/web.xml file. Restart Tomcat to apply new settings.
</p>

<p> 	     	
	<a href="reharvest_collections.do">Re-Process Collections</a>  <br/>
      Re-process all collections from a snap shot of their previously harvested records. 
      This will re-normalize and re-process each record according to the
      latest harvest configurations. 
</p>

<p> 	     	
	<a href="reports.jsp">Reports</a>  <br/>
     Run a report to gather information and statistics for the Harvest Repository Manager. These reports do
     not affect the database so they are safe to run any time.
</p>

<p> 	     	
	<a href="script_runner.do">Script Runner</a>  <br/>
     Run a script using the current server configuration. This is for developers only.
</p>

<p>
	<a href='<c:url value="/docs/CHANGES.txt"/>'>See Changes.txt</a> <br/>
	View changes made in the software for each version.
    This is software version @VERSION@. Application was started on ${startUpDate}.
</p>



<%-- Include style/menu templates --%>
<%@ include file="../../bottom.jsp" %>
</body>
</html>

