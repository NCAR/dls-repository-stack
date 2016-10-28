<%@ page language="java" %>
<%@ page import="org.apache.lucene.document.Document" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglibs-application.tld" prefix="app" %>
<%@ taglib uri="/WEB-INF/tlds/taglibs-io.tld" prefix="io" %>
<%@ taglib uri='/WEB-INF/tlds/vocabulary.tld' prefix='vocab' %>
<%@ taglib uri='/WEB-INF/tlds/utils.tld' prefix='util' %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<jsp:useBean id="ddsQueryForm" class="edu.ucar.dls.dds.action.form.DDSQueryForm" scope="session"/>
<jsp:useBean id="collectionForm" class="edu.ucar.dls.dds.action.form.DDSViewCollectionForm" scope="request"/>

<logic:equal name="collectionForm" property="headless" value="false">
	
	<html>
	<head>
	<title>DLESE Collection Description: <bean:write name="collectionForm" property="docReader.fullTitle" />
	</title>
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
	
	<%@ include file="search_navigation.jsp" %>
	
	<div class="dlese_pageContentHighBanner"> 
	<!-- Breadcrumb banner: -->
	<div class="breadcrumbBanner">
		<div class="breadcrumbs">Educational resources &gt; Find a resource &gt; Full description of collection
		</div> 
	</div>
	<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0"> <!-- Grey line -->
	<td bgcolor="#999999" height="1"></td></table>
	<!-- Start page content -->

</logic:equal>


<%-- Indicate which UI system.interface.language trio will be used for generating vocab labels: --%>
<jsp:setProperty name="collectionForm" property="system" value="dds.descr.en-us"/>
<logic:equal name="collectionForm" property="headless" value="false">
	<logic:notEmpty name="collectionForm" property="docReader.collectionUrl">
		<h3 class="hasSubhead"><a href='<bean:write name="collectionForm" property="docReader.collectionUrl" />' 
			target='_blank' style='text-decoration: none; color: #000099;'><bean:write name="collectionForm" property="docReader.fullTitle" /></a></h3>
		<a href='<bean:write name="collectionForm" property="docReader.collectionUrl" />' 
			target='_blank' style='margin-left: 7px; margin-top: 3px; margin-bottom: 3px;'><bean:write name="collectionForm" property="docReader.collectionUrl" /></a>
	</logic:notEmpty>
	<logic:empty name="collectionForm" property="docReader.collectionUrl">
		<h3><bean:write name="collectionForm" property="docReader.fullTitle" /></h3>
	</logic:empty>
</logic:equal>

<logic:notEmpty name="collectionForm" property="docReader.description">
	<p><bean:write name="collectionForm" property="docReader.description" /></p>
</logic:notEmpty>

<logic:notEmpty name="collectionForm" property="docReader.gradeRanges">
	<p><em>Collection is intended for:</em>
	<logic:iterate id="gr" name="collectionForm" property="docReader.gradeRanges"> 
		<jsp:setProperty name="collectionForm" property="vocabCacheValue" value="<%= gr %>"/>
	</logic:iterate>
	<logic:iterate id="vocabValue" name="collectionForm" property="cachedVocabValuesInOrder"
		indexId="grIndex">		
		<bean:write name="vocabValue" property="label" /><logic:notEqual
			name="collectionForm" property="cachedVocabValuesLastIndex" 
			value="<%= grIndex.toString() %>">,
		</logic:notEqual>						
	</logic:iterate> 
	</p>
</logic:notEmpty>

<p><em>Try searching on these terms (type in keyword box):</em>
	<logic:iterate id="su" name="collectionForm" property="docReader.subjects"
		indexId="suIndex"><vocab:uiLabel system="dds" interface="descr" language="en-us" field="su"
			value="<%= su.toString() %>" />, </logic:iterate>
	<bean:write name="collectionForm" property="docReader.keywordsDisplay" />			
</p>
<p>
	<em>Cost:</em>
	<logic:equal name="collectionForm" property="docReader.cost" value="DLESE:Yes">
		Yes
	</logic:equal>
	<logic:equal name="collectionForm" property="docReader.cost" value="DLESE:No">
		No
	</logic:equal>
	<logic:equal name="collectionForm" property="docReader.cost" value="DLESE:Unknown">
		Unknown
	</logic:equal>
	<logic:notEmpty name="collectionForm" property="docReader.scopeUrl">
		<br /><a href='<bean:write name="collectionForm" 
			property="docReader.scopeUrl" />' target='_blank'>Collection Scope and 
			Policy Statement</a>
	</logic:notEmpty>
</p>		


<logic:equal name="collectionForm" property="headless" value="false">

	<%@ include file="footer.jsp" %>	
	<!-- End page content -->
	</div> 

	</body>
	</html> 

</logic:equal>	
