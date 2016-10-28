<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html><head>	
		<title>DDS Software: Configure Data Sources</title>

		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<META NAME="description" CONTENT="This page describes how to configure the data source for the DDS system.">		
				 
		<%@ include file="/nav/head.jsp" %>
		
	    </head>
	
<body>
<c:if test="${isDeploayedAtDL}">
	<div class="dlese_sectionTitle" id="dlese_sectionTitle">
	   DDS Data<br/>Sources
	</div>
	<div id="ddsLogo">
		<a href="${pageContext.request.contextPath}/dds_overview.jsp"><img border="0" alt="Digital Discovery System (DDS)" title="Digital Discovery System (DDS)" src="${pageContext.request.contextPath}/images/dds_logo_sm.gif"/></a>
	</div>
</c:if>

<%@ include file="/nav/top.jsp" %>

<div class="bodyContent">
	<h1>Overview</h1>
	
	<p>These instructions describe how to configure the data source for the DDS system.</p>
	
	<h2>Data Source Configuration Instructions</h2>
	
	<p>The following instructions are also available in the file CONFIGURE_DATA_SOURCES.txt that come with the DDS distribution:</p>
	<c:import var="installTxt" url="CONFIGURE_DATA_SOURCES.txt"/>

	<pre><c:out value="${installTxt}" escapeXml="true"/></pre>
	
</div>

<%@ include file="/nav/bottom.jsp" %>
</body>
</html>
