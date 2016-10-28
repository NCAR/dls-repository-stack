<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Web Services</c:set>

<html:html>
<head>
	<title><st:pageTitle title="${title}" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	
	<%@ include file="/baseHTMLIncludes.jsp" %>

	<script language="JavaScript">
		function init () {
			if ("${param['currentIndex']}" != "")
				window.location.hash = "collection-status";
			if ("${param['trustedWsIps']}" != "")
				window.location.hash = "authorize";
		}
	</script>
</head>

<body text="#000000" bgcolor="#ffffff" onload="init()">

<st:pageHeader toolLabel="${title}" currentTool="services" />

<st:pageMessages okPath="admin.do?page=services" />
		
<%-- <p>Programmatic access to the system's repository, 
which stores the collections and records, is provided
through services:</p> --%>
	
<p>Web services provide programmatic access the collections and records stored in the system's repository.</p>

<h3>Search Web Service</h3>
<p>This service supports textual searching over metadata and content, searching by date and field, sorting by date 
and field, discovery of the controlled vocabularies that are part of the library including grade 
ranges, subjects, resources types and content standards, checking for the existence of a URL, 
and other functionality.</p>

<%-- http://nsdl.org/nsdl_dds/services/ddsws1-1/index.jsp
http://wiki.nsdl.org/index.php/Community:Search_API_FAQ  --%>

<ul>
	<li><a href="http://nsdl.org/nsdl_dds/services/ddsws1-1/index.jsp"
				 target="_blank">Search Web Service specification</a>.</li>
	
	<li>An example of a  <a href="../services/examples/dcsws/full_example1.jsp?q=ocean">web 
	service-enabled interface</a> that searches over contents of the collections</li>
	
	<li><a href="${contextPath}/admin/services/service_explorer.jsp">Search 
	Service Explorer</a></li>
</ul>


<h3>Repository Web Service</h3>

<p>This service enables records to be inserted into the system's repository by third-party applications.
Access to the Repository Web Service requires the 
client's IP to be authorized (see <a href="#authorize">Configure authorized client IPs</a> below).
</p>
<ul>
	<li><a href="../services/dcsws1-0/service_specification.jsp">Repository Web Service specification</a></li>
</ul>

<a name="authorize"></a>
<h3>Configure authorized client IPs</h3>
			
<p>Some web services require the client IP to be authorized:</p>

<ul>
		<li>
			By default only discoverable records are available through the Search Web Service except when authorized by the IPs configured below. Discoverable records include all adn, collection and annotation records 
			who's collection is enabled and all adn records that have status accessioned-discoverable.
			Clients that access the service from an authorized IP will have additional access to the
			publically non-discoverable records.
		</li>
		<li>Access to the Repository Web Service also requires the client's IP to be authorized</li>
</ul>

<p>
You may enter a comma-separated list of IP addresses below that are allowed access to 
non-discoverable records through the DDS web service interface. Each IP address must be 
separated by a comma and may contain the * wildcard character. For example 
" 128.117.126.132 " or " 128.117.126.*, 128.114.166.* " (without quotes).  

<html:form action="/admin/admin" method="get">
	<table>						
		<tr>
			<td>
				IP addresses that are allowed web service access to the Repository Web Service as well as to non-discoverable 
				records (separated by commas): 
			</td>
		</tr>
		<tr>
			<td>
				<html:text property="trustedWsIps" name="daf" size="80"/>
			</td>
		</tr>
				 
	</table>
	<input type="hidden" name="command" value="setTrustedIps" />
	<input type="hidden" name="config" value="${param.config}" />
	<br /><html:submit property="button" value="Set IP Addresses" />
</html:form>	
</p>

	
</body>
</html:html>

