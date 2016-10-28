<%@ page language="java" %>
<%@ page import="org.apache.lucene.document.Document" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglibs-application.tld" prefix="app" %>
<%@ taglib uri='/WEB-INF/tlds/vocabulary.tld' prefix='vocab' %>
<%@ taglib uri='/WEB-INF/tlds/utils.tld' prefix='util' %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<%@ taglib uri='/WEB-INF/tlds/taglibs-io.tld' prefix="io" %>
<%@ include file="JSTLTagLibIncludes.jsp" %>

<jsp:useBean id="ddsQueryForm" class="edu.ucar.dls.dds.action.form.DDSQueryForm" scope="session"/>
<jsp:useBean id="ddsViewResourceForm" class="edu.ucar.dls.dds.action.form.DDSViewResourceForm" scope="session"/>

<html:html>
<head>
<title>DLESE Find a resource: Collections that contain:
	<logic:notPresent name="ddsViewResourceForm" property="error">
		<bean:write name="ddsViewResourceForm" property="resourceTitle"/>
	</logic:notPresent>
</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<!-- BEGIN HEADER: DO NOT EDIT! -->
	<!-- These are needed for site/side nav display: -->
	<link rel="stylesheet" type="text/css" href="/dlese_shared/dlese_styles.css"/>
	<script type="text/javascript" src="/dlese_shared/dlese_script_nav.js"></script>
	<script type="text/javascript" src="/dlese_shared/dlese_site_menus.js"></script>	
<!-- END HEADER: DO NOT EDIT! -->
<link rel='stylesheet' type='text/css' href='<%= request.getContextPath() %>/discovery_styles.css'>
<script type="text/javascript" src="<%= request.getContextPath() %>/dlese_script_vocab.js"></script>
<script type="text/javascript"><!--
	dlese_DISCOVERY_ROOT = '<%= request.getContextPath() %>/';
	//dlese_navSelected = 'dlese_edres';	// Which navigation button is selected?
//-->
</script>

</head>
<body bgcolor="#FFFFFF" text="#142929" link="#B75B00" vlink="#4E9A9A" alink="#FFBA04" margin="0"
onLoad="SetSelectedState(); dlese_searchOnLoad()">	

<%@ include file="search_navigation.jsp" %>

<div class="dlese_pageContentHighBanner"> 
<!-- Breadcrumb banner: -->
<div class="breadcrumbBanner">
	<div class="breadcrumbs">Educational resources &gt; Find a resource &gt; Resource collection descriptions</div>
</div>
<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0"> <!-- Grey line -->
<td bgcolor="#999999" height="1"></td></table>
<!-- Start page content -->
<style type="text/css"><!--
	.collDescHeader {
		font-family: arial, helvetica, sans-serif;
		font-weight: bold;
		margin-left: 6px;
		margin-bottom: 10px;
		margin-top: 10px;
	}
	P { margin-top: 4px; margin-bottom: 4px; }
	.description {
		margin-left: 6px; font-family: arial, helvetica, sans-serif;
	}
	.descriptionLink {
		margin-left: 14px; font-family: arial, helvetica, sans-serif;
		margin-bottom: 8px; margin-top: 5px;
	}
	.spacer { height: 10px; }
-->
</style>

<logic:present name="ddsViewResourceForm" property="error">
	<div class="pageTitleNoTable">
		<span style="color: red; font-weight: bold;">
			<bean:write name="ddsViewResourceForm" property="error" />
		</span>
	</div>
</logic:present>
<logic:notPresent name="ddsViewResourceForm" property="error">


<div class="pageTitleNoTable">
	Collections that contain: <a href='<bean:write name="ddsViewResourceForm" 
		property="resourceResultLinkRedirectURL" filter="false" 
		/>T=viewCollections/ID=<bean:write name="ddsViewResourceForm" property="primaryResultDocId" filter="false"
		/>/*<bean:write name="ddsViewResourceForm" property="resourceUrl"/>' 
		target='_blank'><bean:write name="ddsViewResourceForm" property="resourceTitle"/></a>
</div>

<bean:define id="primaryCollectionKey" name="ddsViewResourceForm" property="primaryRequestedCollectionKey" />
<bean:define id="collectionKey" name="ddsViewResourceForm" property="primaryResultDoc.docReader.collectionKey"/>


<%-- Display collection metadata from this record...(if my collection was selected by the user) --%>
<logic:equal name="collectionKey" value="<%= (String)primaryCollectionKey %>">
	<div class="blueBand">
		<vocab:uiLabel system="dds" interface="descr" language="en-us"
			field="ky" value="<%= (String)collectionKey %>"/>
		&nbsp;
	</div>
	<div class="description">
		<jsp:setProperty name="ddsViewResourceForm" property="collectionKey" value="<%= (String)collectionKey %>" />
		<jsp:getProperty name="ddsViewResourceForm" property="collectionDescription" />
	</div>
	<div class="descriptionLink">		
		<a href='view_resource.do?description=${ddsViewResourceForm.primaryResultDoc.docReader.id}'>Full description</a> of this resource provided by <vocab:uiLabel system="dds" interface="descr" language="en-us"
			field="ky" value="<%= (String)collectionKey %>"/>
	</div>	
	<div class="spacer">&nbsp;</div>	
</logic:equal>

<%-- Display collection metadata for items in another collections... (if that other collection was the one selected by the user) --%>
<logic:present name="ddsViewResourceForm" property="primaryResultDoc.docReader.associatedCollectionKeys">
	<logic:iterate id="associatedCollectionKey" name="ddsViewResourceForm"
		property="primaryResultDoc.docReader.associatedCollectionKeys">
		<logic:equal name="associatedCollectionKey" value="<%= (String)primaryCollectionKey %>">
			<div class="blueBand">				
				<vocab:uiLabel system="dds" interface="descr" language="en-us"
					field="ky" value="<%= (String)associatedCollectionKey %>"/>
				&nbsp;
			</div>
			<div class="description">
				<jsp:setProperty name="ddsViewResourceForm" property="collectionKey" value="<%= (String)associatedCollectionKey %>" />
				<jsp:getProperty name="ddsViewResourceForm" property="collectionDescription" />
			</div>		
			<logic:present name="ddsViewResourceForm" property="primaryResultDoc.docReader.displayableAssociatedItemResultDocs">
				<logic:iterate id="result" name="ddsViewResourceForm"
					property="primaryResultDoc.docReader.displayableAssociatedItemResultDocs">
					<logic:equal name="result" property="docReader.collectionKey" value="<%= (String)associatedCollectionKey %>">
						<div class="descriptionLink">					
							<a href='view_resource.do?description=<bean:write name="result" property="docReader.id" 
								/>'>Full description</a> of this resource provided by <vocab:uiLabel system="dds" interface="descr" language="en-us"
								field="ky" value="<%= (String)associatedCollectionKey %>"/>
						</div>
					</logic:equal>
				</logic:iterate>
			</logic:present>
			<div class="spacer">&nbsp;</div>
		</logic:equal>	
	</logic:iterate>
</logic:present>

<%-- Display collection metadata for this item... (if not the one initially selected by the user) --%>
<logic:notEqual name="collectionKey" value="<%= (String)primaryCollectionKey %>">
	<div class="blueBand">		
		<vocab:uiLabel system="dds" interface="descr" language="en-us"
			field="ky" value="<%= (String)collectionKey %>"/>
		&nbsp;
	</div>
	<div class="description">
		<jsp:setProperty name="ddsViewResourceForm" property="collectionKey" value="<%= (String)collectionKey %>" />
		<jsp:getProperty name="ddsViewResourceForm" property="collectionDescription" />
	</div>
	<div class="descriptionLink">		
		<a href='view_resource.do?description=${ddsViewResourceForm.primaryResultDoc.docReader.id}'>Full description</a> of this resource provided by <vocab:uiLabel system="dds" interface="descr" language="en-us"
			field="ky" value="<%= (String)collectionKey %>"/>
	</div>		
	<div class="spacer">&nbsp;</div>	
</logic:notEqual>

<%-- Display collection metadata for items in other collections... --%>
<logic:present name="ddsViewResourceForm" property="primaryResultDoc.docReader.associatedCollectionKeys">
	<logic:iterate id="associatedCollectionKey" name="ddsViewResourceForm"
		property="primaryResultDoc.docReader.associatedCollectionKeys">
		<logic:notEqual name="associatedCollectionKey" value="<%= (String)primaryCollectionKey %>">
			<div class="blueBand">	
				<vocab:uiLabel system="dds" interface="descr" language="en-us"
					field="ky" value="<%= (String)associatedCollectionKey %>"/>
				&nbsp;
			</div>
			<div class="description">
				<jsp:setProperty name="ddsViewResourceForm" property="collectionKey" value="<%= (String)associatedCollectionKey %>" />
				<jsp:getProperty name="ddsViewResourceForm" property="collectionDescription" />
			</div>	
			<logic:present name="ddsViewResourceForm" property="primaryResultDoc.docReader.displayableAssociatedItemResultDocs">
				<logic:iterate id="result" name="ddsViewResourceForm"
					property="primaryResultDoc.docReader.displayableAssociatedItemResultDocs">
					<logic:equal name="result" property="docReader.collectionKey" value="<%= (String)associatedCollectionKey %>">
						<div class="descriptionLink">
							<a href='view_resource.do?description=<bean:write name="result" property="docReader.id" 
								/>'>Full description</a> of this resource provided by <vocab:uiLabel system="dds" interface="descr" language="en-us"
								field="ky" value="<%= (String)associatedCollectionKey %>"/>
						</div>
					</logic:equal>
				</logic:iterate>
			</logic:present>
			<div class="spacer">&nbsp;</div>			
		</logic:notEqual>	
	</logic:iterate>
</logic:present>


</logic:notPresent>

<br />

<%@ include file="footer.jsp" %>

<!-- End page content -->
</div>


</body>
</html:html> 