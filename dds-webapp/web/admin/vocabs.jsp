<%@ page language="java" %>

<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-nested.tld" prefix="nested" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/response.tld" prefix="resp" %>
<%@ taglib uri="/WEB-INF/tlds/request.tld" prefix="req" %>
<%@ taglib uri="/WEB-INF/tlds/datetime.tld" prefix="dt" %>
<%@ taglib uri='/WEB-INF/tlds/utils.tld' prefix='util' %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

<jsp:useBean id="vocabAdminForm" class="edu.ucar.dls.vocab.action.form.VocabAdminForm" scope="session"/>

<html:html>

<head>
<title>Metadata-UI Manager</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<link rel='stylesheet' type='text/css' href='dds_admin_styles.css'>

<%@ include file="/nav/head.jsp" %>
</head>

<body text="#000000" bgcolor="#ffffff">
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Metadata-UI<br/> Manager
</div>

<%@ include file="/nav/top.jsp" %>


<br>

<table width="93%" border="0" align="center">
  <tr> 
    <td> 
		  
	  <h1>Metadata-UI manager</h1>

  
	<%-- ######## Display messages, if present ######## --%>
	<c:if test="${ not empty vocabAdminForm.loaderFeedback }">
		<p>${ vocabAdminForm.loaderFeedback }</p>
	</c:if>
    
	<p>The Metadata-UI Manager is used to control the labels and
	formatting associated with the vocabularies in the ADN and dlese_collect metadata frameworks.
	The labels and formatting are reflected in the DDS Web and JavaScript services.
	</p>
	
  	<%-- ######## Load vocabs ######## --%>
	<p>Clicking 'Reload vocabulary' below will cause the UI, labels and formatting directives in the 
	DDS to reflect the changes made in the XML
	Metadata-UI configuration files.  Although the indexer is given a new instance,
	changes won't be reflected within the search results of already indexed records until re-indexed in the
	<a href="admin.do">Collection manager</a>.&nbsp;&nbsp;
	</p>
	
	<p><html:form action="/admin/vocab.do" method="get"> 
		<html:submit property="command" value="Reload vocabulary" /> 
     </html:form></p>
	 
	<h3>Vocabulary settings</h3> 
	 
			  <P>
			  The library vocabularies are being loaded from:<br>
			  <img src="images/file_folder2.gif" width="12" height="12"> <span class="initParamValues">${initParam['metadataGroupsLoaderFile']}</span>.
			  To change, edit the context parameter <code>metadataGroupsLoaderFile</code> in server.xml or web.xml.
			  </P>			  

			  <P>
			  The library vocabularies audience is currently set to:<br>
			  <img src="images/file_folder2.gif" width="12" height="12"> <span class="initParamValues">${initParam['metadataVocabAudience']}</span>.
			  To change, edit the context parameter <code>metadataVocabAudience</code> in server.xml or web.xml.
			  </P>			  

			  <P>
			  The library vocabularies language is currently set to:<br>
			  <img src="images/file_folder2.gif" width="12" height="12"> <span class="initParamValues">${initParam['metadataVocabLanguage']}</span>.
			  To change, edit the context parameter <code>metadataVocabLanguage</code> in server.xml or web.xml.
			  </P>	 
	 
    </td>
  </tr>
</table>


<%@ include file="/nav/bottom.jsp" %> 

</body>
</html:html>


