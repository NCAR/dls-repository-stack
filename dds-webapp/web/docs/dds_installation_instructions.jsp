<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html><head>	
		<title>DDS Software: Installation Instructions</title>

		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<META NAME="description" CONTENT="This page describes how to install a DDS system.">		
				 
		<%@ include file="/nav/head.jsp" %>
		
	    </head>
	
<body>
<c:if test="${isDeploayedAtDL}">
	<div class="dlese_sectionTitle" id="dlese_sectionTitle">
	   DDS Installation<br/>Instructions
	</div>
	<div id="ddsLogo">
		<a href="${pageContext.request.contextPath}/dds_overview.jsp"><img border="0" alt="Digital Discovery System (DDS)" title="Digital Discovery System (DDS)" src="${pageContext.request.contextPath}/images/dds_logo_sm.gif"/></a>
	</div>
</c:if>
<%@ include file="/nav/top.jsp" %>

<div class="bodyContent">
	<h1>Overview</h1>
	
	<p>These instructions describe how to install a DDS system in the Tomcat servlet container.</p>
	
	<h2>Installation Instructions</h2>
	
	<p>The following instructions describe how to install the pre-build DDS software from a WAR file. These instructions 
	are also available in the file INSTALL_INSTRUCTIONS.txt that come with the DDS distribution:</p>
	<c:import var="installTxt" url="INSTALL_INSTRUCTIONS.txt"/>

	<pre><c:out value="${installTxt}" escapeXml="true"/></pre>
	
	
	<h2>Build Instructions</h2>
	
	<p>DDS is available as a pre-built WAR file (see above). However, if you wish instead to build DDS from source, please follow these instructions (BUILD_INSTRUCTIONS.txt):</p>
	
	<c:import var="buildTxt" url="BUILD_INSTRUCTIONS.txt"/>
	<pre><c:out value="${buildTxt}" escapeXml="true"/></pre>	
	
</div>

<%@ include file="/nav/bottom.jsp" %>
</body>
</html>
