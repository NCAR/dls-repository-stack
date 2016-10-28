<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>

<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>

<c:set value="DCS/NCS Software" var="title"/>	

<html>
<head>
	<title>${title}</title>
	
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<META NAME="keywords" CONTENT="DLS, digital learning sciences, digital collection system">
	<META NAME="description" CONTENT="Digital Collection System (DCS) Software Download">
	<META NAME="organization" CONTENT="Digital Learning Sciences">
	
	<%-- DLESE template header (CSS styles and JavaScript references) --%>
	<%@ include file="/nav/head.jsp" %> 
	
	 
	
</head>

<body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066">
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   DCS/NCS<br/>Software
</div>

<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>
		   

 <%-- Generate the download links automatically using the XML config: --%>  
 <c:import url="product_release_links.jsp?xmlFileLocation=http://www.dlese.org/downloads/dcs/product_release_config.xml" />
  
  
	<h3>Support</h3>
	<p>
	The <a href="http://sourceforge.net/projects/dlsciences/forums/forum/1311685">NCS User Forum</a> at SourceForge is a place where users can seek advice, provide tips, share experiences, report bugs, and interact with the developers and other users of the tool. 
	Inquiries may also be sent via e-mail to <a href="mailto:support@dlese.org">support@dlese.org</a>.
	</p>



<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
  
</body>
</html>


