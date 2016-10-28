<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set value="DDS Download and Installation: Overview" var="title"/>
<html><head>	
		<title>${title}</title>

		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<META NAME="description" CONTENT="This page describes how to install and configure DDS.">		
				 
		<%@ include file="/nav/head.jsp" %>
		
	    </head>
	
<body>

<c:if test="${isDeploayedAtDL}">
	<div class="dlese_sectionTitle" id="dlese_sectionTitle">
	   DDS<br/>Installation
	</div>
	<div id="ddsLogo">
		<a href="${pageContext.request.contextPath}/dds_overview.jsp"><img border="0" alt="Digital Discovery System (DDS)" title="Digital Discovery System (DDS)" src="${pageContext.request.contextPath}/images/dds_logo_sm.gif"/></a>
	</div>
</c:if>
<%@ include file="/nav/top.jsp" %>

<div class="bodyContent">
	<h1>Overview</h1>
	<p>The documents below describe how to install and configure a DDS system:</p>
	
	<%@ include file="/docs/install_configure_summary_include.jsp" %>
    
</div>

<%@ include file="/nav/bottom.jsp" %>
</body>
</html>
