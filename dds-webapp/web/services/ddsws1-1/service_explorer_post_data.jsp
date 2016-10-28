<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ include file="/ddswsBaseUrl.jsp" %><%-- Sets the DDSWS 1-1 baseURL into variable 'ddsws11BaseUrl' --%>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>

<%-- Set up some variables used in this page --%>
<c:set var="contextUrl"><%@ include file="../../ContextUrl.jsp" %></c:set>
<c:set var="odlBaseUrl">${contextUrl}/services/oai2-0</c:set>
<c:set var="ddswsBaseUrl">${ddsws11BaseUrl}</c:set>
<c:set var="rssBaseUrl">${contextUrl}/services/rss2-0</c:set>

<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>

<html>
<head>
<title>Search API: Explorer (POST method)</title>
	
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
	<h1>Search API Explorer (POST method)</h1>
	<p>This DDS API explorer allows you to issue the Search request using POST method, which is useful when issuing very large queries. See the the general-purpose <a href="service_explorer.jsp">DDS API explorer</a> for additional
	request options. </p>
	
	<form name="baseUrlForm" action="javascript:void(0)" style="padding:0px; margin:0px">
	<p>Service <em>Base URL</em>: &nbsp; <input type="text" name="baseUrl" size="70" value="${ddswsBaseUrl}"></p>
	</form>
	
	<hr width="100%">
	
	<p>
	
	<table cellpadding=3 cellspacing=0>
	
	<%-- Search and UserSearch--%>		
	<tr>
		<td colspan=3>
			<a href="service_specification.jsp#Search" class="blackul" title="See description of the Search request">Search</a>
		</td>
	</tr>
		<form name="searchForm" method="POST" action="${ddswsBaseUrl}">
		<tr>
			<td width="10%" nowrap>
				Enter a search query:
			</td>
			<td nowrap colspan=2 align="left">
				<textarea rows="10" cols="90" name="q"></textarea>
				<input type="hidden" name="s" value="0"/>
				<input type="hidden" name="n" value="10"/>
				<input type="hidden" name="verb" value="Search"/>
				<input type="hidden" name="client" value="ddsws-explorer-post"/>
				<br/>
				<input title="View the Search response" type="submit" value="Search"/>
			</td>
		</tr>
				
		</form>			
	
	<tr>
		<td colspan=3>
			<div style="padding-top:20px">
				Note: 
				The Search request is sent with the following argument values: 
					<code>verb=Search</code>,
					<code>s=0</code>,
					<code>n=10</code>,
					<code>client=ddsws-explorer-post</code>.
			</div>
			<div>
				All query criteria must be placed in the query box. To search by Collection or XML format, add these to your query.
			</div>
		</td>
	</tr>	
		
	</table>
	
	
	</p>

  	


<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
  

</body>
</html>


