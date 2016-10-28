<%@ include file="/JSTLTagLibIncludes.jsp" %>
<jsp:useBean id="reportForm" class="edu.ucar.dls.dds.action.form.DDSAdminQueryForm" scope="request"/>

<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Report</c:set>

<html:html>
<head>
<title><st:pageTitle title="${title}" /></title>

<%@ include file="/baseHTMLIncludes.jsp" %>

<script>
	// Override the method below to set the focus to the appropriate field.
	function sf(){}	
</script>
</head>
<body>

<%@ include file="/locked_record_status.jspf" %>
<st:pageHeader currentTool="Settings" toolLabel="${title}" />

<br>

	<st:pageMessages okPath="report.do?report=noreport" />
	
	<h3><bean:write name="reportForm" property="reportTitle"/></h3>	

<table width="100%" cellpadding="6" cellspacing="0">
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
			<a href='report.do?${reportForm.prevResultsUrl}'><img src='../images/arrow_left.gif' width='16' height='13' border='0' alt='Previous results page'/></a>
		</logic:present>
		&nbsp;Errors: ${reportForm.start} - ${reportForm.end} out of ${reportForm.numResults}&nbsp;
		<logic:present name="reportForm" property="nextResultsUrl">
			<a href='report.do?${reportForm.nextResultsUrl}'><img 
				src='../images/arrow_right.gif' width='16' height='13' border='0' alt='Next results page'/></a>
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
  
  <logic:iterate id="result" name="reportForm" 
			property="results" indexId="index" 
			offset="<%= reportForm.getOffset() %>" 
			length="<%= reportForm.getLength() %>">  
	  
	  <tr>
		<td colspan="2">		
			
				
			<%-- Display errors --%>
			<logic:match name="result" property="docReader.readerType" value="ErrorDocReader">
			<tr class='admin_blue1'> 
				<td>				
					<font size=+1><b>File: ${result.docReader.fileName}</b></font><br>
					This record had errors during the indexing process and is not available for discovery
				</td>
					<td align="right" > 
						<font color="red"><b>Error</b></font> 
					</td>					
			</tr>
			<tr> 
				<td colspan="2">				
					Message: <bean:write name="result" property="docReader.errorMsg" filter="false" />
					<logic:empty name="result" property="docReader.errorMsg">
						<bean:write name="result" property="docReader.exceptionName" filter="false" />
					</logic:empty>
				</td>
			</tr>			
			<tr colspan="2"> 
				<td>											
					<div class="searchResultValues"><em>File location:</em> &nbsp;
						<bean:write name="result" property="docReader.docsource" filter="false" />
					</div>
					<div class="searchResultValues"><em>File last modified:</em> &nbsp;
						<bean:write name="result" property="docReader.lastModifiedString" filter="false" />
					</div>			
					<div class="searchResultValues">
					[ <a href='${contextPath}/browse/display.do?file=<bean:write name="result" property="docReader.docsource" filter="false" />&rt=text'>view XML</a> ]
					[ <a href='${contextPath}/browse/display.do?file=<bean:write name="result" property="docReader.docsource" filter="false" />&rt=validate'>validate XML</a> ]
					
					</div>
					<%-- <div align="right">docReaderType: <bean:write name="result" property="docReader.readerType" filter="false" /></div> --%>
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
				[ <a href='${contextPath}/browse/display.do?id=<bean:write name="result" property="docReader.id" filter="false" />&metadataFormat=<bean:write name="format" filter="false" />&rt=text'>view XML</a> ] 
				[ <a href='${contextPath}/browse/display.do?id=<bean:write name="result" property="docReader.id" filter="false" />&metadataFormat=<bean:write name="format" filter="false" />&rt=validate'>validate XML</a> ] 

				<%-- hook to ADN editor --%>
				[ <a href='/schemedit/adn/adn.do?src=remote&command=edit&recId=<bean:write name="result" property="docReader.id" filter="false" />' target="_blank"><b>Edit XML</b></a> ]

				&nbsp;&nbsp;			
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

<%-- <logic:notEqual name="reportForm" property="numResults" value="0">	
<center>
<font face="Arial, Helvetica, sans-serif" size="-1">
<a href="../index.jsp">Discovery</a> |
<a href="admin.do">Collection manager</a> |
<a href="query.do">Discovery for administrators</a> | 
<a href="vocab.do">Vocabulary manager</a> |	
Reports
</font>
</center>
</logic:notEqual> --%>

<br><br>
</body>
</html:html>
