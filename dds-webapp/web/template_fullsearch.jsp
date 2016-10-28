<%@ page language="java" %>
<%@ page import="org.apache.lucene.document.Document" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglibs-application.tld" prefix="app" %>
<%@ taglib uri='/WEB-INF/tlds/vocabulary.tld' prefix='vocab' %>
<%@ taglib uri='/WEB-INF/tlds/utils.tld' prefix='util' %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<jsp:useBean id="ddsQueryForm" class="edu.ucar.dls.dds.action.form.DDSQueryForm" scope="session"/>
<html:html>
<head>
<title>DLESE</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel='stylesheet' type='text/css' href='/dlese_shared/dlese_styles.css'>
<link rel='stylesheet' type='text/css' href='<%= request.getContextPath() %>/discovery_styles.css'>
<script type="text/javascript" src="/dlese_shared/dlese_script_nav.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/dlese_script_vocab.js"></script>
<script type="text/javascript"><!--
dlese_DISCOVERY_ROOT = '<%= request.getContextPath() %>/';
//dlese_navSelected = 'dlese_edres';	// Which navigation button is selected?
//-->
</script>
<script type="text/javascript" src="/dlese_shared/dlese_site_menus.js"></script>
</head>
<body bgcolor="#FFFFFF" text="#142929" link="#B75B00" vlink="#4E9A9A" alink="#FFBA04" margin="0"
onLoad="SetSelectedState()">	
<a name="top"></a>
<div class="dlese_pageContentHighBanner"> 
<!-- Breadcrumb banner: -->
<div class="breadcrumbBanner">
	<div class="breadcrumbs"> &gt; </div>
</div>
<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0"> <!-- Grey line -->
<td bgcolor="#999999" height="1"></td></table>
<!-- Start page content -->


<!-- End page content -->
</div> 
<!-- Navigation code placed at bottom of page, for the benefit of screen readers: -->
<%@ include file="search_navigation.jsp" %>
</body>
</html:html> 