<%@ page language="java" %>
<jsp:useBean id="reportForm" class="edu.ucar.dls.dds.action.form.DDSAdminQueryForm" scope="request"/>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglibs-application.tld" prefix="app" %>
<%@ taglib uri="/WEB-INF/tlds/request.tld" prefix="req" %>
<%@ include file="../JSTLTagLibIncludes.jsp" %>

<html:html>
<head>
<title><bean:write name="reportForm" property="reportTitle"/></title>

<link rel='stylesheet' type='text/css' href='dds_admin_styles.css'>

<script>
	// Override the method below to set the focus to the appropriate field.
	function sf(){}	
</script>
<%@ include file="/nav/head.jsp" %>

</head>
<body onLoad=sf()>
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Reports
</div>
<%@ include file="/nav/top.jsp" %>
<br>

	<%-- ######## Display messages, if present ######## --%>
	<logic:messagesPresent> 		
	<table width="90%" bgcolor="#000000" cellspacing="1" cellpadding="8">
	  <tr bgcolor="ffffff"> 
		<td>
			<div class="searchResultValues"><em>Messages:</em>
			<ul>
				<html:messages id="msg" property="message"> 
					<li><bean:write name="msg"/></li>									
				</html:messages>
				<html:messages id="msg" property="error"> 
					<li><font color=red>Error: <bean:write name="msg"/></font></li>									
				</html:messages>
			</ul>
			<blockquote>[ <a href="report.do?report=noreport">OK</a> ]</blockquote>
		</td>
	  </tr>
	</table>		
	<br><br>
	</logic:messagesPresent>
	
<table width="100%" cellpadding="6" cellspacing="0">

  <tr>
	<td colspan=2>
		<h1><bean:write name="reportForm" property="reportTitle"/></h1>	
	</td>
  </tr>

  <logic:equal name="reportForm" property="numResults" value="0">
  <logic:present parameter="q">
	  <tr>
		<td colspan=2>
		<logic:notPresent name="reportForm" property="rq">
		There are no matching records for your request.
		</logic:notPresent>
		<logic:present name="reportForm" property="rq">
		You searched for <font color="blue"><bean:write name="reportForm" property="rq" filter="false"/></font>.
		There are no records in this report that match that query. 
		</logic:present>
		
		</td>
	  </tr>
  </logic:present>
  </logic:equal>
 

 <%-- Search within results --%>
<%--  <% if(reportForm.getRq() != null || !reportForm.getNumResults().equals("0")) { %> 
  <tr>
	<td colspan=2 nowrap>	 
  	<html:form action="/admin/report" method="GET">
		<script>function sf(){document.reportForm.rq.focus();}</script>
		
		Search within this report: <html:text property="rq" size="45"/>
		<logic:iterate name="reportForm" property="nrqParams" id="param">
			<input type='hidden' name='<bean:write name="param" property="name" filter="false"/>' value='<bean:write name="param" property="val" filter="false"/>'>
		</logic:iterate>
		<input type="hidden" name="s" value="0">	

		<html:submit value="Search"/>
		

	</html:form>
	</td>
	</tr>
	<% } %> --%>
  
  <%-- <tr>
	<td colspan=2>
			
				Back to <a href="admin.do">Collection manager</a>.
	</td>
  </tr> --%>
	
  <logic:notEqual name="reportForm" property="numResults" value="0">
   
   <tr>
	<td colspan=2 nowrap>		
		<%-- Pager --%>
		<logic:present name="reportForm" property="prevResultsUrl">
			<a href='report.do?<bean:write name="reportForm" property="prevResultsUrl"/>'><img src='../images/arrow_left.gif' width='16' height='13' border='0' alt='Previous results page'/></a>
		</logic:present>
		&nbsp;Errors: <bean:write name="reportForm" property="start"/> - <bean:write name="reportForm" property="end"/> out of <bean:write name="reportForm" property="numResults"/>&nbsp;
		<logic:present name="reportForm" property="nextResultsUrl">
			<a href='report.do?<bean:write name="reportForm" property="nextResultsUrl"/>'><img src='../images/arrow_right.gif' width='16' height='13' border='0' alt='Next results page'/></a>
		</logic:present>	
	</td>
  </tr>   

	  <tr>
		<td colspan="2">		  
			<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0">
				<td bgcolor="#999999" height="1"></td>
			</table>
		</td>
	</tr>

	<%-- XSL that removes all namespaces from XML, making xPath easier to work with --%>
	<c:set var="removeNamespacesXsl">
		<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" >
			<xsl:template match="@*" >
				<xsl:attribute name="{local-name()}" >
					<xsl:value-of select="." />
				</xsl:attribute>
				<xsl:apply-templates/>
			</xsl:template>
			<xsl:template match ="*" >
				<xsl:element name="{local-name()}" >
					<xsl:apply-templates select="@* | node()" />
				</xsl:element>
			</xsl:template>
		</xsl:stylesheet>	
	</c:set>	
	
  <logic:iterate id="result" name="reportForm" property="results" indexId="index" offset="<%= reportForm.getOffset() %>" length="<%= reportForm.getLength() %>">  
 
	  <tr>
		<td colspan="2">		
			
			<%-- Display errors --%>
			<logic:match name="result" property="docReader.readerType" value="ErrorDocReader"> 
			
			<%-- Parse the record XML and pull out the title and URL --%>
			<c:catch var="parseError">
				<%-- Remove namespaces from the XML and create the DOM --%>
				<x:transform xslt="${removeNamespacesXsl}" xml="${result.docReader.fullContent}" var="fileXMLDom"/>

				<c:set var="recordTitle">
					<x:out select="$fileXMLDom/itemRecord/general/title" />
				</c:set>		
				<c:set var="url">
					<x:out select="$fileXMLDom/itemRecord/technical/online/primaryURL" />
				</c:set>
				<c:set var="recordId">
					<x:out select="$fileXMLDom/itemRecord/metaMetadata/catalogEntries/catalog/@entry" />
				</c:set>		
				
				<%-- Determine the collection key using the file path (crude method) --%>
				<c:set var="dirPath" value="${fn:split(result.docReader.docDir,'/\\\\')}"/>
				<c:set var="collection" value="${dirPath[fn:length(dirPath)-1]}" />							
			</c:catch>
			
			<%-- Output any errors as a comment --%>
			<c:if test="${not empty parseError}">
				<!-- Error parsing record XML: ${parseError} -->
			</c:if>
			
			<tr class='admin_blue1'> 
				<td colspan="2">					
					<c:if test="${empty recordTitle}">
						<c:set var="recordTitle" value="${result.docReader.fileName}"/>
					</c:if>				
					<font size=+1><b>${recordTitle}</b></font><br>
					<c:if test="${not empty url}">
						<a href="${url}" target="_blank">${url}</a>
					</c:if>
				</td>
				<td align="right" > 
					<font color="red"><b>Error</b></font> 
				</td>					
			</tr>
			<tr> 
				<td colspan="2">				
					This record had errors during the indexing process and is not available for discovery.
				</td>
			</tr>			
			<tr> 
				<td colspan="2">
					<c:choose>
						<c:when test="${result.docReader.errorDocType == 'dupIdError'}">	
							Message: The ID '${result.docReader.duplicateId}' that was found in this file was already specified in
							<a href="display.do?fullview=${result.docReader.duplicateId}">another file</a> located at
							${result.docReader.duplicateIdSourceFilePath}							
						</c:when>							
						<c:otherwise>
							Message: <bean:write name="result" property="docReader.errorMsg" filter="true" />
							<logic:empty name="result" property="docReader.errorMsg">
								<bean:write name="result" property="docReader.exceptionName" filter="true" />
							</logic:empty>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>			
			<tr colspan="2"> 
				<td>
					<c:if test="${not empty recordId}">
						<div class="searchResultValues"><em>ID:</em> &nbsp;
							${recordId}
						</div>
					</c:if>
					<c:if test="${not empty collection}">
						<div class="searchResultValues"><em>Collection:</em> &nbsp;
							${collection}
						</div>
					</c:if>						
					<div class="searchResultValues"><em>File location:</em> &nbsp;
						<bean:write name="result" property="docReader.docsource" filter="false" />
					</div>
					<div class="searchResultValues"><em>File last modified:</em> &nbsp;
						<bean:write name="result" property="docReader.lastModifiedString" filter="false" />
					</div>
					<div class="searchResultValues"><em>File indexed on:</em> &nbsp;
						${result.docReader.dateFileWasIndexedString}
					</div>					
					<div class="searchResultValues">
					[ <a href='display.do?file=<bean:write name="result" property="docReader.docsource" filter="false" />'>view XML</a> ]
					[ <a href='display.do?file=<bean:write name="result" property="docReader.docsource" filter="false" />&rt=validate'>validate XML</a> ]
					<c:if test="${not empty recordId}">
						[ <a href='reporting/report.do?verb=ViewIDMapper&id=${recordId}&collection=${collection}'>IDMapper Data</a> ]
					</c:if>						
					</div>
			</logic:match>			

			<%-- Display docs with validation errors --%>
			<logic:present name="result" property="docReader.validationReport">
				<logic:match name="result" property="docReader.readerType" value="XMLDocReader">
					<font size=+1><div class="searchResultValues"><em>File: <bean:write name="result" property="docReader.fileName" filter="false" /></em></font><br>				
					<div class="searchResultValues"><em>This file was indexed but contains validation errors.</em><br>				
				</logic:match>
				<logic:match name="result" property="docReader.readerType" value="ItemDocReader">
					<font size=+1><div class="searchResultValues"><em><bean:write name="result" property="docReader.title" filter="false" /></em></font><br>
					<a href='<bean:write name="result" property="docReader.url" filter="false" />' target=blank><bean:write name="result" property="docReader.url" filter="false" /></a><br>
					<div class="searchResultValues"><em>This record was indexed but contains validation errors.</em><br>
					<div class="searchResultValues"><em>Description:</em> &nbsp;<bean:write name="result" property="docReader.description" filter="false" /><br>
				</logic:match>				
				<div class="searchResultValues"><em>ID:</em> &nbsp;<bean:write name="result" property="docReader.id" filter="false" /><br>							
				<div class="searchResultValues"><em>Set:</em> &nbsp;
					<logic:iterate id="set" name="result" property="docReader.sets">
					<bean:write name="set" filter="false" />
					</logic:iterate><br>				
				<div class="searchResultValues"><em>File format:</em> <bean:write name="result" property="docReader.nativeFormat" filter="false" /><br>
				<div class="searchResultValues"><em>File location:</em> <bean:write name="result" property="docReader.docsource" filter="false" /><br>								
				<div class="searchResultValues"><em>File last modified:</em> <bean:write name="result" property="docReader.lastModifiedString" filter="false" /><br>
				<div class="searchResultValues"><em>Validation message:</em> <font color=red><bean:write name="result" property="docReader.validationReport" filter="false" /></font><br>	
				<logic:iterate id="format" name="result" property="docReader.availableFormats">
				<div class="searchResultValues"><em><bean:write name="format" filter="false" />:</em> 
				[ <a href='display.do?id=<bean:write name="result" property="docReader.id" filter="false" />&metadataFormat=<bean:write name="format" filter="false" />'>view XML</a> ] 
				[ <a href='display.do?id=<bean:write name="result" property="docReader.id" filter="false" />&metadataFormat=<bean:write name="format" filter="false" />&rt=validate'>validate XML</a> ] &nbsp;&nbsp;			
				</logic:iterate>
			</logic:present>
			
			<%-- Display web log entries --%>
			<logic:match name="result" property="docReader.readerType" value="WebLogReader">
				<logic:notEmpty name="result" property="docReader.notes">
					<div class="searchResultValues"><em>Log note:</em> <bean:write name="result" property="docReader.notes" filter="false" /><br>
				</logic:notEmpty>				
				<div class="searchResultValues"><em>Date of request:</em> <bean:write name="result" property="docReader.requestDate" filter="false" /><br>
				<div class="searchResultValues"><em>URL requested:</em> <bean:write name="result" property="docReader.requestUrl" filter="false" /><br>				
				<div class="searchResultValues"><em>Requesting host:</em> <bean:write name="result" property="docReader.remoteHost" filter="false" />
			</logic:match>
			
			
		</td>
	  </tr>   
  </logic:iterate>
  
	  <tr>
		<td colspan="2">		  
			<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0">
				<td bgcolor="#999999" height="1"></td>
			</table>
		</td>
	</tr>
   <tr>
	<td colspan=2 nowrap>		
		<%-- Pager --%>
		<logic:present name="reportForm" property="prevResultsUrl">
			<a href='report.do?<bean:write name="reportForm" property="prevResultsUrl"/>'><img src='../images/arrow_left.gif' width='16' height='13' border='0' alt='Previous results page'/></a>
		</logic:present>
		&nbsp;Errors: <bean:write name="reportForm" property="start"/> - <bean:write name="reportForm" property="end"/> out of <bean:write name="reportForm" property="numResults"/>&nbsp;
		<logic:present name="reportForm" property="nextResultsUrl">
			<a href='report.do?<bean:write name="reportForm" property="nextResultsUrl"/>'><img src='../images/arrow_right.gif' width='16' height='13' border='0' alt='Next results page'/></a>
		</logic:present>	
	</td>
  </tr>  
  </logic:notEqual> 
</table>

<br><br>
<%@ include file="/nav/bottom.jsp" %>
</body>
</html:html>

