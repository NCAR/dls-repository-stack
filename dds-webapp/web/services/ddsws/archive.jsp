<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>
<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>


<html><!-- InstanceBegin template="/Templates/services.dwt" codeOutsideHTMLIsLocked="true" -->
<head>

<!-- InstanceBeginEditable name="head" -->

<!-- InstanceEndEditable -->
<script language="JavaScript" src="../../../Templates/dds_services_script.js"></script>

	<%-- DLESE template header (CSS styles and JavaScript references) --%>
	<%@ include file="/nav/head.jsp" %> 
	
	

</head>   
<body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066">
<!-- InstanceBeginEditable name="sectionTitle" -->
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Search<br/>Web Service
</div>
<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>

<h1>Previous versions of the Search Web Service</h1>

  <p>Previous versions of the Search Web Service are listed below for reference.
  New applications should be developed using the <a href="../ddsws1-1/index.jsp">current version</a>.
  </p>
  
  <p>Previous service versions:
	  <ul>
		<li><a href="../ddsws1-0/index.jsp">DDSWS v1.0</a></li>
	  </ul>
	  <ul>
		<li><a href="../oai2-0/index.jsp">DLESE ODL Search</a></li>
	  </ul>	  
  </p>
  <p>&nbsp;</p>  
  	<h3>Contact us </h3>

	<p>If you have questions or comments regarding this service, please send e-mail to <a href="mailto:support@dlese.org">support@dlese.org</a>.



	

	

	  <%-- <a name="sp"></a>

	<h1>Search page output explorer</h1>

	<p> 

		You can view the the JavaScript search page service

		in it's original <a href="${contextUrl}/services/jshtml1-1/tpl/js_search_server.jsp">HTML output</a> 

		or as <a href="${contextUrl}/services/jshtml1-1/tpl/js_search_server.jsp?rt=jswl">JavaScript output</a>.

	</p> --%>

<!-- InstanceEndEditable -->

<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
    


</body>
<!-- InstanceEnd --></html>


