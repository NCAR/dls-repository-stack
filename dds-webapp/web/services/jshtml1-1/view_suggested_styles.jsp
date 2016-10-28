<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>

<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>

<html>
<head>
	<title>JavaScript Search Service: Suggested CSS Styles</title>
	
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


<h1>Suggested CSS Styles</h1>

  
<p>The  JavaScript Search Service includes a set of suggested Cascading Style 
  Sheet (CSS) styles, which are recommended for use in your web pages. These styles correspond to CSS classes that have been assigned to various HTML elements that are returned by the service. </p>
<p>To use the suggested styles, insert the following code into the &lt;head&gt; 
  portion of your HTML page:</p>

<c:set var="cssLnk">
<LINK REL="stylesheet" TYPE="text/css" 
	HREF="${contextUrl}/services/jshtml1-1/suggested_styles.css">
</c:set>

<pre><c:out value="${cssLnk}" escapeXml="true"/></pre>
  
  <p style="margin-top:16px">After inserting the suggested styles, you may override any of the style elements by re-defining them later in your page. For example, the following code inherits the suggested styles and then overrides the background color of the resource title, setting it to blue: </p>
  <c:set var="cssLnk">
<LINK REL="stylesheet" TYPE="text/css" 
	HREF="${contextUrl}/services/jshtml1-1/suggested_styles.css">
	
<style type="text/css">
	TR.resourceTitle {
		background-color:#E8ECF4;
	}			
</style>	 
</c:set> 
<pre><c:out value="${cssLnk}" escapeXml="true"/></pre>

 <p>See also: <a href="view_required_styles.jsp">Required CSS Styles</a></p>
    
  
  <h3>The suggested styles are as follows: </h3>
  <c:import url="suggested_styles.css" var="styles"/>
 <pre>
<c:out value="${styles}" escapeXml="true"/>
  </pre> 



<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
  
</body>
</html>



