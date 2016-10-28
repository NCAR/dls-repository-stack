<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>


<%-- Set up some variables used in this page --%>
<c:set var="contextUrl"><%@ include file="../../ContextUrl.jsp" %></c:set>
<c:set var="odlBaseUrl">${contextUrl}/services/oai2-0</c:set>
<c:set var="ddswsBaseUrl">${contextUrl}/services/ddsws1-0</c:set>
<c:set var="rssBaseUrl">${contextUrl}/services/rss2-0</c:set>



<c:set var="dateSelectOptions">
	<option value='none'> -- No value -- </option>
	<c:forEach var="datePair" items="${helperBean.utcDates}">
		<option value='${datePair.date}'>${datePair.label}</option>						
	</c:forEach>
</c:set>

<%-- Grab available XML Formats and store in a select list variable--%>				
<c:if test="${empty xmlFormatsSelectListDDSExp || param.reload == 'true'}">
	<c:catch var="serviceError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListXmlFormats"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		<%-- Parse the XML results --%>
		<x:parse var="xmlFormats">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>
		
		<c:set var="xmlFormatsSelectListDDSExp" scope="application">
			<x:forEach select="$xmlFormats/DDSWebService/ListXmlFormats/xmlFormat">
				<option value='<x:out select="."/>'><x:out select="."/></option>
			</x:forEach>
		</c:set>
	
		<c:set var="xmlFormatsRadioButtonsList_DDSExp" scope="application">
			<div>
				<a 	href="javascript:toggleVisibility('Format');" 
					title="Click to show/hide" 
					class="vocabHeading"><img src='images/btnExpand.gif' 
					alt="Click to show/hide" border="0" hspace="5" width="11" hight="11">XML FORMAT</a>				
			</div> 		
			<div id="Format" style="display:none; width:100%;">
				<div style="margin-left:18px">
				<input type="radio" id="no_xml_format" name="xmlFormat" value="none" checked/>
				<label for="no_xml_format" class="vocabListLabels">Record's native format (none indicated)</label><br/>
				<x:forEach select="$xmlFormats/DDSWebService/ListXmlFormats/xmlFormat">
					<input type="radio" id="<x:out select="."/>_xmlFormat_id" name="xmlFormat" value="<x:out select="."/>" />
					<label for="<x:out select="."/>_xmlFormat_id" class="vocabListLabels"><x:out select="."/></label><br/>
				</x:forEach>
				</div>
			</div>
		</c:set>				
	</c:catch>
	<c:if test="${not empty serviceError}">
		<c:set var="xmlFormatsSelectListDDSExp" scope="application">
			<option value='--- none available ---'>--- none available ---</option>
		</c:set>
	</c:if>
</c:if>


<%-- Grab available Collections and store in a select list variable--%>				
<c:if test="${empty collectionsSelectListOAIDDSExp || param.reload == 'true'}">
	<c:catch var="collectionsError">			
		<%-- Construct the request (URL) --%>
		<c:url value="${ddswsBaseUrl}" var="request">
			<c:param name="verb" value="ListCollections"/>
		</c:url>
		
		<%-- Perform the Web service request --%>	
		<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
		<%-- Parse the XML results --%>
		<x:parse var="collections">
			<c:out value="${xmlOutput}" escapeXml="false"/>
		</x:parse>
		
		<c:set var="collectionsSelectListOAIDDSExp" scope="application">
			<x:forEach select="$collections/DDSWebService/ListCollections/collections/collection">
				<option value='<x:out select="vocabEntry"/>'><x:out select="renderingGuidelines/label"/></option>
			</x:forEach>
		</c:set>					
	</c:catch>
	<c:if test="${not empty collectionsError}">
		<c:set var="collectionsSelectListOAIDDSExp" scope="application">
			<option value='--- none available ---'>--- none available ---</option>
		</c:set>
	</c:if>	
</c:if>


<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>


<html>
<head>
<title>ODL Search Service Explorer</title>
	
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<script language="JavaScript" src="../dds_services_script.js"></script>
<script language="JavaScript">
	var OAI_BASE_URL = "${odlBaseUrl}";	
</script>


	<%-- DLESE template header (CSS styles and JavaScript references) --%>
	<%@ include file="/nav/head.jsp" %> 

</head><body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066">

<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   ODL Search <br/>Service
</div>


<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>



	<h1>ODL Search Service Explorer</h1>
	
	
  <p><b>A new version of this service is available</b>. 
		The documentation here is for a previous version. 
		Please refer to <a href="../ddsws/">Current Search Web Service</a> for new application development.	</p>  
	<p>The ODL Search Service explorer allows you to issue each of the available requests to the service and view the XML response using your web browser. Because XML is returned, we recommend using Internet Explorer 5 or later, Firefox or Netscape 7.1 or later.</p>
	<p>Service <em>Base URL</em>: &nbsp; ${odlBaseUrl}</p>
	
	<c:if test="${isDeploayedAtDL}">
			<p>Note that you can not use the above <em>Base URL</em> to harvest DLESE metadata using the standard OAI-PMH (a badArgument error will be returned). To harvest from DLESE, use the <A 
		href="../oaiDataProvider/index.jsp">DLESE OAI data provider</A> instead.</p>
	</c:if>
	<hr width="100%">

	<%-- ODL search UI --%>
	<p>
	<table cellpadding=3 cellspacing=0>
		<form name="odlSearchForm" action="javascript:mkOdlSearch()">
		<tr>
			<td colspan=3 nowrap>
				<a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords" title="See description of ListRecords" target="_blank" class="blackul">ListRecords</a>
				and
				<a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers" title="See description of ListIdentifiers" target="_blank" class="blackul">ListIdentifiers</a>
			</td>
		</tr>
		<tr>
			<td nowrap>
				Enter search terms:
			</td>
			<td colspan=2 nowrap>
				<input type="text" name="query" size="45">
				<select name="verb">
					<option value="ListIdentifiers">ListIdentifiers</option>
					<option value="ListRecords" selected>ListRecords</option>
				</select>
				<input type="button" value="search" onClick="mkOdlSearch()">
			</td>
		</tr>
		
		<tr>
			<td>&nbsp;</td>
			<td colspan=2 nowrap>
				Set:
				<select name="sets">
					<option value=' -- All -- '> -- All -- </option>			
					${collectionsSelectListOAIDDSExp}
				</select>				
				Format:
				<select name="formats">
					${xmlFormatsSelectListDDSExp}	
				</select>
			</td>
		</tr>
				
		<tr>
			<td>&nbsp;</td>
			<td align=right>
				Records modified since:
			</td>
			<td>
				<select name="from">
					${dateSelectOptions}
				</select>
				&nbsp;[ <a href="index.jsp">update time</a> ]
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td align=right>
				Records modified before:
			</td>
			<td>
				<select name="until">
					${dateSelectOptions}
				</select>
				&nbsp;[ <a href="index.jsp">update time</a> ]
			</td>
		</tr>
		</form>
	</table>
	

	
	<%-- GetRecord --%>
	<table>
	<form name="getRecordForm" action='javascript:mkGetRecord()'>
	<tr>
		<td colspan=3 nowrap>
			<a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#GetRecord" title="See description of GetRecord" target="_blank" class="blackul">GetRecord</a>
		</td>
	</tr>
	<tr>
		<td nowrap>
			Enter ID:
		</td>
		<td nowrap colspan=2>
			<input type="text" name="identifier" size="30">
			<input title="View the GetRecord response" type="button" value="view" onClick='mkGetRecord()'>
			<input title="Validate the GetRecord response" type="button" value="validate" onClick='mkGetRecord("validate")'>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td nowrap colspan=2>			
			Return in format:
			<select name="formats">
				${xmlFormatsSelectListDDSExp}			
			</select>
		</td>
	</tr>
	</form>	
	</table>	
	
	<table>
		<%-- ListSets --%>		
		<tr>
			<td colspan=3>
				<a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListSets" title="See description of ListSets" target="_blank" class="blackul">ListSets</a>
			</td>
		</tr>
		<tr>
			<form name="ListSets" action="">
			<td colspan=3>
				<input title="View the ListSets response" type="button" value="view" onClick='window.location = OAI_BASE_URL + "?verb=ListSets"'>
				<input title="Validate the ListSets response" type="button" value="validate" onClick='window.location = OAI_BASE_URL + "?verb=ListSets&rt=validate"'>
			</td>
			</form>	
		</tr>		

		<tr>
			<td colspan=3 height=20>&nbsp;
				
			</td>
		</tr>
		
		<%-- ListMetadataFormats --%>		
		<tr>
			<td colspan=3>
				<a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListMetadataFormats" title="See description of ListMetadataFormats" target="_parent" class="blackul">ListMetadataFormats</a>
			</td>
		</tr>
		<tr>
			<form name="ListMetadataFormats" action="">
			<td colspan=3>
				<input title="View the ListMetadataFormats response" type="button" value="view" onClick='window.location = OAI_BASE_URL + "?verb=ListMetadataFormats"'>
				<input title="Validate the ListMetadataFormats response" type="button" value="validate" onClick='window.location = OAI_BASE_URL + "?verb=ListMetadataFormats&rt=validate"'>
			</td>
			</form>	
		</tr>				
	</table>
	
	
	</p>		
	


<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
  

</body>
</html>


