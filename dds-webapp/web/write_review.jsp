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
<title>DLESE Write a review</title>
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
onLoad="dlese_searchOnLoad()">	
<a name="top"></a>
<div class="dlese_pageContentHighBanner"> 
<!-- Breadcrumb banner: -->
<div class="breadcrumbBanner">
	<div class="breadcrumbs">
		Educational resources &gt; Find a resource &gt; Write a review
	</div>
</div>
<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0"> <!-- Grey line -->
<td bgcolor="#999999" height="1"></td></table>
<!-- Start page content -->
<style type="text/css"><!--
	.reviewBlock {
		margin-left: 15px; 
		margin-top: 10px; 
		margin-bottom: 10px;
		font-family: arial, helvetica, sans-serif;
		font-size: 0.8em;
	}
-->
</style>

<div class="pageTitleNoTable">
	Write a review for <a href='<bean:write name="ddsViewResourceForm" 
		property="resourceUrl"/>' target='_blank'><bean:write name="ddsViewResourceForm" property="resourceTitle"/></a>
</div>

<logic:equal name="ddsViewResourceForm" property="isDrcReviewInProgress" value="true">
	<div class="blueBand">Community Review System (CRS)</div>
	<div class="reviewBlock">
		<a href='<bean:write name="ddsViewResourceForm" property="pathwayUrl"/>' target='_blank'>Write a review 
		using the CRS</a>. 
		The CRS is a formal process designed to identify the strengths 
		(and weaknesses) of a resource.  It enables users to compare resources against a common set of criteria, 
		making more informed decisions about their use. Note that you must be an educator who has used the resource 
		or decided against using it in order to write a CRS review. Resources that successfully pass through the CRS 
		process become part of the valued DLESE Reviewed Collection. 
		<a href='<%= request.getContextPath() %>/description_collection.jsp?file=/collections/crs_fc.html'>Full 
		collection description</a>
	</div>
</logic:equal>

<div class="blueBand">Nominate this resource for formal review</div>	

<form style="margin-top: 0px; margin-bottom: 0px">
<div class="reviewBlock">
		Would you like to nominate this resource to be evaluated for acceptance into the 
		DLESE Reviewed Collection (DRC)? <input type="checkbox"> Yes
</div>

<div class="blueBand">Quick comments</div>
<div class="reviewBlock">
		If you only have a minute and want to share your experience using this
		resource with others, please answer the questions below.
	<div style="margin-bottom: 5px; margin-top: 14px;">
		1. Are you an educator: <input type="checkbox"> Yes  <input type="checkbox"> No
	</div>
	<div style="margin-bottom: 5px;">
		2. If so, have you used the resource in your teaching environment: 
			<input type="checkbox"> Yes  <input type="checkbox"> No
	</div>
	<div style="margin-bottom: 5px;">	
		3. Please enter comments below, noting the grade level(s) and course
		title(s) for which you used the resource and type of students (inner
		city/rural/special needs, etc.) that used it. Also indicate whether you
		would recommend the resource to others.
		 <br /><br />
		<table cellpadding="0" cellspacing="0">
			<td valign="top">
				<textarea rows="6" cols="50"></textarea>
			</td>
			<td width="10">&nbsp;</td>
			<td valign="top">
				Your email address (optional; will be held in confidence)<br />
				<input type="text" size="30"><br /><br />
				<input type="button" value="Submit">
			</td>
		</table>
	</div>		
</p>
</div>
</form>

<%@ include file="footer.jsp" %>
<!-- End page content -->
</div> 
<!-- Navigation code placed at bottom of page, for the benefit of screen readers: -->
<%@ include file="search_navigation.jsp" %>
</body>
</html:html> 