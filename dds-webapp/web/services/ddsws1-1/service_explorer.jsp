<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ include file="/ddswsBaseUrl.jsp" %><%-- Sets the DDSWS 1-1 baseURL into variable 'ddsws11BaseUrl' --%>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>

<%-- Set up some variables used in this page --%>
<c:set var="contextUrl"><%@ include file="../../ContextUrl.jsp" %></c:set>
<c:set var="odlBaseUrl">${contextUrl}/services/oai2-0</c:set>
<c:set var="ddswsBaseUrl">${ddsws11BaseUrl}</c:set>
<c:set var="rssBaseUrl">${contextUrl}/services/rss2-0</c:set>

<c:set var="dateSelectOptions">
	<option value='none'> -- No value -- </option>
	<c:forEach var="datePair" items="${helperBean.utcDates}">
		<option value='${datePair.date}'>${datePair.label}</option>						
	</c:forEach>
</c:set>

<%-- Grab available XML Formats and store in a select list variable--%>				
<c:catch var="serviceError">			
	<%-- Construct the request (URL) --%>
	<c:url value="${ddswsBaseUrl}" var="request">
		<c:param name="verb" value="ListXmlFormats"/>
	</c:url>
	
	<%-- Perform the Web service request --%>	
	<c:import url="${request}" var="xmlOutput" charEncoding="UTF-8" />
	<%-- Parse the XML results --%>
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${xmlOutput}" var="xmlFormats"/>
	
	<c:set var="xmlFormatsSelectListDDSExp">
		<x:forEach select="$xmlFormats/DDSWebService/ListXmlFormats/xmlFormat">
			<option value='<x:out select="."/>'><x:out select="."/></option>
		</x:forEach>
	</c:set>	
	
	<%-- Create the list of XML formats --%>
	<c:set var="xmlFormatsRadioButtonsList_DDSExpv11">
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
	<c:set var="xmlFormatsSelectListDDSExp">
		<option value='--- none available ---'>--- none available ---</option>
	</c:set>
</c:if>

<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>

<html>
<head>
<title>Search API: Explorer</title>
	
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<META NAME="keywords" CONTENT="Digital Learning Sciences, DLS">
<META NAME="description" CONTENT="Digital Discovery System Web service explorer">
<META NAME="creator" CONTENT="John Weatherley">
<META NAME="organization" CONTENT="Digital Learning Sciences (DLS)">

<script language="JavaScript" src="../dds_services_script.js"></script>
<script language="JavaScript">
	var BASE_URL = "${ddswsBaseUrl}";
	var OAI_BASE_URL = "${odlBaseUrl}";	
</script>

<style type="text/css">
	.reqLnk { 
		padding: auto;
		font-family: Arial, Helvetica, sans-serif; 
		font-size: 10pt; 
		padding-bottom: 10px; 
		padding-left: 10px; 
	} 
	
	/* ------ Styles used in the checkbox menus ------ */
	
	A.vocabHeading:link {
		color: #000000; 
		text-decoration: none; 
		font-weight: bold;
		font-size: 9pt; 	
	}
	A.vocabHeading:visited {
		color: #000000; 
		text-decoration: none; 
		font-weight: bold;
		font-size: 9pt; 	
	}	
	A.vocabHeading:active {
		color: #000000; 
		text-decoration: none; 
		font-weight: bold;
		font-size: 9pt; 	
	}	
	.vocabSubHeading { 
		color: #000000; 
		text-decoration: none; 
		font-weight: bold;
		font-size: 9pt; 			
	}
	.vocabListLabels { 
		font-size: 9pt; 
	}
	.clearbutton {
		font-size: 9px;
		font-family: Arial, Helvetica, sans-serif;
		cursor: pointer;
		color: #333366; 
		height: 16px; 
		background-color: #eeeeee;
	}		
</style>

<%-- DLESE template header (CSS styles and JavaScript references) --%>
<%@ include file="/nav/head.jsp" %> 

</head><body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066"
onload="document.searchForm.q.focus();">

<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Search<br/>Service API
</div>

<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>
 
  				
	<a name="ddsws"></a>
	<h1>Search API Explorer</h1>
	<p>The service explorer allows you to issue many of the available requests to the service and view the XML or JSON response using your web browser.</p>
	<p>This is service version DDSWS v1.1. </p>
	
	<form name="baseUrlForm" action="javascript:void(0)" style="padding:0px; margin:0px">
	<p>Service <em>Base URL</em>: &nbsp; <input type="text" name="baseUrl" size="70" value="${ddswsBaseUrl}"></p>
	</form>
	
	<hr width="100%">
	
	<p>
	
	<table cellpadding=3 cellspacing=0>
	
	<%-- Search and UserSearch--%>		
	<tr>
		<td colspan=3>
			<a href="service_specification.jsp#Search" class="blackul" title="See description of the Search request">Search</a>*
		</td>
	</tr>
		<form name="searchForm" action='javascript:mkSearch()'>
		<tr>
			<td nowrap>
				Enter search terms:
			</td>
			<td nowrap colspan=2>
				<input type="text" name="q" size="40">
				<select name="searchRequest">
					<option value='Search'>Search</option>									
				</select>	
				<input title="View the Search response" type="button" value="Search" onClick='mkSearch()'>
			</td>
		</tr>
					
		<tr>
			<td valign="top" nowrap>
				<label for="doGeo">Geospatial search</label> <input id="doGeo" type="checkbox" name="doGeo" />:
			</td>			
			<td nowrap colspan=2>
				<select name="geoPredicate" onchange="doCkGeo()">
					<option value='within'>Within box</option>									
					<option value='contains'>Contains box</option>
					<option value='overlaps'>Overlaps box</option>
				</select>
				<select name="geoClause" onchange="doCkGeo()">
					<option value='must'>Must match (AND)</option>									
					<option value='should'>Should match (OR)</option>
				</select>				
				&nbsp;&nbsp; Bounding box:
				N <input type="text" name="geoBBNorth" size="5" onchange="doCkGeo()">
				S <input type="text" name="geoBBSouth" size="5" onchange="doCkGeo()">
				W <input type="text" name="geoBBWest" size="5" onchange="doCkGeo()">
				E <input type="text" name="geoBBEast" size="5" onchange="doCkGeo()">
				<span style="font-size:9px;">&#187; <a href="javascript:void(0)" onclick="insertLatLons()">insert example lat/lons</a></span>
			</td>
		</tr>
		
		<tr>
			<td valign="top">Additional options:</td>
			<td colspan=2>			
				<%-- 	Include the checkbox menu JSP. Note that web_service_connection.jsp, which is included above,
						does the work or contacting the Web service to get the values and lables used to generate
						the menus. --%>
				<%@ include file="web_service_connection.jsp" %>
				<%@ include file="checkbox_menus.jsp" %>
				
				<%-- Then choose which menus you want to include (comment-out or rearrange as desired) --%>
				<%-- ${gradeRangesCheckBoxMenu_DDSExpv11}
				${subjectsCheckBoxMenu_DDSExpv11}
				${resourceTypesCheckBoxMenu_DDSExpv11} 
				${contentStandardsCheckBoxMenuDDSExp} --%>
				${collectionsCheckBoxMenu_DDSExpv11}
				${xmlFormatsRadioButtonsList_DDSExpv11}
				
				<%--	The JavaScript function clearAllSelections() (below) is used to clear all selected 
						checkboxes in the form named 'searchForm' --%>
				<div style="margin:3px 0px 0px 6px">
					&#187; <a href="javascript:void(0)" onclick="clearAllSelections();resetXmlFormatRadio();">Clear all selections</a>
				</div>
			</td>
		</tr>		
		</form>			
	
		
		<tr>
			<td colspan=3 height=20>&nbsp;
				
			</td>
		</tr>

			
			
	<%-- GetRecord --%>		
	<form name="getRecordFormDDSWS" action='javascript:mkGetRecordDDSWS()'>
	<tr>
		<td colspan=3 nowrap>
			<a href="service_specification.jsp#GetRecord" title="See description of GetRecord" class="blackul">GetRecord</a>
		</td>
	</tr>
	<tr>
		<td nowrap>
			Enter ID:
		</td>
		<td nowrap colspan=2>
			<input type="text" name="identifier" size="50">
			<input title="View the GetRecord response" type="button" value="GetRecord" onClick='mkGetRecordDDSWS()'>
		</td>
	</tr>
	<tr>
	  <tr>
			<td>&nbsp;</td>
			<td nowrap colspan=2>			
				Return in format:
				<select name="formats">
					<option value='-any-'> -- Record's native format -- </option>
					${xmlFormatsSelectListDDSExp}			
				</select>
			</td>
	  </tr>
	</tr>	
	</form>			

	<tr>
		<td colspan=3 height=20>&nbsp;</td>
	</tr>	

	
	<%-- ListFields --%>		
	<form name="listFieldsForm" action='javascript:mkListFields()'>
		<tr>
			<td colspan=3 nowrap>
				<a href="service_specification.jsp#ListFields" title="See description of ListFields" class="blackul">ListFields</a>
			</td>
		</tr>
		<tr>
			<td nowrap>
				Issue request:
			</td>		
			<td colspan=2 nowrap>
				<input title="View the ListFields response" type="button" value="ListFields" onClick='mkListFields()'>
			</td>
		</tr>
	</form>		

		<tr>
			<td colspan=3 height=20>&nbsp;</td>
		</tr>
		
	<%-- ListTerms --%>		
	<form name="listTermsForm" action='javascript:mkListTerms()'>
		<tr>
			<td colspan=3 nowrap>
				<a href="service_specification.jsp#ListTerms" title="See description of ListTerms" class="blackul">ListTerms</a>
			</td>
		</tr>
		<tr>
			<td nowrap>
				Enter a field:
			</td>
			<td nowrap colspan=2>
				<input type="text" name="field" size="50">
				<input title="View the ListTerms response" type="button" value="ListTerms" onClick='mkListTerms()'>
			</td>
		</tr>
	</form>
	
		<tr>
			<td colspan=3 height=20>&nbsp;</td>
		</tr>
		
		
	<%-- UrlCheck --%>		
	<form name="urlCheckForm" action='javascript:mkUrlCheck()'>
	<tr>
		<td colspan=3 nowrap>
			<a href="service_specification.jsp#UrlCheck" title="See description of UrlCheck" class="blackul">UrlCheck</a>
		</td>
	</tr>
	<tr>
		<td nowrap>
			Enter a URL:
		</td>
		<td nowrap colspan=2>
			<input type="text" name="url" size="50">
			<input title="View the UrlCheck response" type="button" value="UrlCheck" onClick='mkUrlCheck()'>
		</td>
	</tr>
	</form>	

		<tr>
			<td colspan=3 height=20>&nbsp;</td>
		</tr>
		
	<%-- ServiceInfo --%>		
	<form name="serviceInfoForm" action='javascript:mkServiceInfo()'>
	<tr>
		<td colspan=3 nowrap>
			<a href="service_specification.jsp#ServiceInfo" title="See description of ServiceInfo" class="blackul">ServiceInfo</a>
		</td>
	</tr>
	<tr>
		<td nowrap>
			Issue request:
		</td>	
		<td colspan=2>
			<input type="hidden" name="verb" value="ServiceInfo">
			<input title="View the ServiceInfo response" type="submit" value="ServiceInfo">
		</td>
	</tr>
	</form>	
	
		<tr>
			<td colspan=3 height=20>&nbsp;</td>
		</tr>		
		
	</table>
	
	
	<table cellpadding=3 cellspacing=0 width="100%">
		
	<%-- List requests... --%>		
	<form name="listRequest" action='javascript:mkListRequest()'>
	<tr>
		<td colspan=2 nowrap>
			<a href="service_specification.jsp#ListCollections" title="See description of ListCollections" class="blackul">ListCollections</a>,		
			<a href="service_specification.jsp#ListXmlFormats" title="See description of ListXmlFormats" class="blackul">ListXmlFormats</a>
		</td>
	</tr>
	<tr>
		<td colspan=2 nowrap>
			Choose a request:
			<select name="verb">
				<option value='ListCollections'>ListCollections</option>
				<option value='ListXmlFormats'>ListXmlFormats</option>								
			</select>	
			<input title="View the response" type="submit" value="issue request">
		</td>
	</tr>

	</form>	
			
		<tr>
			<td colspan=2 height=20>&nbsp;</td>
		</tr>		
		

	
		<tr>
			<td colspan=2 height=20><hr style="padding:0; margin:0;"/></td>
		</tr>
		
		<tr>
			<td colspan=2 height=20>&nbsp;</td>
		</tr>		

		<%-- JSON output --%>		
		<form name="jsonForm" action="javascript:void(0);">
			<tr>
				<td colspan=2 nowrap>
					<a href="service_specification.jsp#json" title="See description of JSON output" class="blackul">JSON Output</a>
				</td>
			</tr>
			<tr>
				<td colspan=2>
					<input type="checkbox" name="jsonCheck"> Output responses as JSON instead of XML.
					<div style="margin-top:3px">
					Callback function (optional) <input type="text" name="jsonCallback" size="20">
					</div>
				</td>
			</tr>
		</form>	
				
		<tr>
			<td colspan=2 height=20>&nbsp;</td>
		</tr>		
		
		<%-- Transform (localize) output --%>		
		<form name="transformForm" action="javascript:void(0);">
			<tr>
				<td colspan=2 nowrap>
					<a href="service_specification.jsp#localize" title="See description of localize output" class="blackul">Remove Namespaces from Output</a>
				</td>
			</tr>
			<tr>
				<td colspan=2>
					<input type="checkbox" name="transformCheck"> Remove namespaces from XML and JSON output.
				</td>
			</tr>
		</form>

		<tr>
			<td colspan=2 height=20><hr style="padding:0; margin:0;"/></td>
		</tr>
		
		<tr>
			<td colspan=2 height=20>&nbsp;</td>
		</tr>	
		
			<tr>
				<td colspan=2>		
					To issue large queries using the Search request use the  <a href="service_explorer_post_data.jsp">DDS API explorer POST data</a>.
				</td>
			</tr>
		
				
		<tr>
			<td colspan=2 height=20>&nbsp;</td>
		</tr>	
		<tr>
			<td colspan=2 height=20><hr style="padding:0; margin:0;"/></td>
		</tr>		
	
	</table>
	
	*Note: not all request arguments are illustrated above.
	Refer to the <a href="service_specification.jsp">search web service documentation</a> for 
	a complete description of each request. 
	
	</p>
  	


<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
  

</body>
</html>


