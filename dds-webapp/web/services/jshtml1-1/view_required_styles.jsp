<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>

<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>

<html>
<head>
	<title>JavaScript Search Service: Required CSS Styles</title>
	
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<META NAME="keywords" CONTENT="DLESE,digital library for earth system education">
	<META NAME="description" CONTENT="DLESE Discovery System JavaScript Service Portal">
	<META NAME="creator" CONTENT="John Weatherley">
	<META NAME="organization" CONTENT="DLESE Program Center">
	<META NAME="doctype" CONTENT="DLESE webpage">
	
	<%-- DLESE template header (CSS styles and JavaScript references) --%>
	<%@ include file="/nav/head.jsp" %> 

	<LINK REL="stylesheet" TYPE="text/css" HREF="${contextUrl}/services/jshtml1-1/documentation_styles.css">
	 
	
	<style type="text/css">
		PRE {
			font-size:12px;
		}	
	</style>
	
</head>

<body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066">
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   JavaScript<br/>Search Service
</div>


<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>


<h1>Required CSS Styles</h1>

  <p>The  JavaScript Search Service includes a set of required Cascading Style Sheet (CSS) styles that are automatically inserted into your page. These styles correspond to CSS classes that are assigned to various HTML elements that are returned by the service and are necessary to render the elements  properly in your page. These styles are applied automatically, however you may override any one of these styles by redefining them in your page after the point at which you have included the API's &lt;script&gt; elements. The required styles are shown here for reference. </p>
  <p>See also: <a href="view_suggested_styles.jsp">Suggested CSS Styles</a></p>
  <h3>The required styles are as follows: </h3>
  <c:import url="required_styles.css" var="styles"/>
 <pre>
<c:out value="${styles}" escapeXml="true"/>
  </pre> 


	


<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
  
</body>
</html>



