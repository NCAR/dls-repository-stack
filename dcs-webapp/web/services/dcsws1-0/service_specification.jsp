<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Repository WebService</c:set>
<html>
<head>
<%@ include file="/baseHTMLIncludes.jsp" %>

<title>${title}</title>

<style type="text/css"><!--
	/* Tweak the dlese_styles css to work with HTML that was imported form the old Swiki */	
 	TD { font-size:100%; }
	H2 { 
		font-size: 140%;
		font-style: normal;	
		margin-left: 0px; 
		padding-left: 0px; 
		margin-bottom: 10px; 
		margin-top: 5px;	
	}	
	H3 { margin-left: 0px; padding-left: 0px; margin-bottom: 10px; margin-top: 5px; }
	H4 { margin-left: 0px; padding-left: 0px; margin-bottom: 10px; margin-top: 5px; }
	UL { 
		padding-left: 7px;
		padding-top: 2px;
		padding-bottom: 0px;
		margin-left: 16px;
		margin-bottom: 0px;
		margin-top: 0px;
	}
	LI { 
		padding-top: 0px;
		padding-bottom: 0px;
		margin-bottom: 0px;
		margin-top: 0px;
	} 
	PRE {
		font-size: 110%;	
	}
	.info-box {
		border:2px #333366 solid;
		padding:5px;
		margin:5px 15px 5px 15px;
	}
	p {
		/* background-color:#ebe7e7; */
		}

-->
</style>
</head>

<body>
<st:pageHeader toolLabel="${title}" currentTool="services" />

<st:breadcrumbs>
	<a href="${contextPath}/admin/admin.do">Services</a>                                             
	<st:breadcrumbArrow />
	<a  href="${contextPath}/admin/admin.do?page=services">Web Services</a>
	<st:breadcrumbArrow />
	<span class="current">${title}</span>
</st:breadcrumbs>

<div style="padding-bottom:10px">
	Service version 1.0<br/>
	Document version $Revision: 1.3 $<br/>
	Last revised $Date: 2011/07/05 23:06:12 $
</div>


<p><a href="mailto:mailto:ostwald@ucar.edu">Jonathan Ostwald</a> &lt;<a
href="mailto:ostwald@ucar.edu">ostwald@ucar.edu</a>&gt; - University Corporation
for Atmospheric Research, DLESE Program Center.</p>

<p>The DLESE Collection System (DCS) Repository Web Service (DCSWebService)
provides programmatic access to the DCS Repository.</p>

<p>The DCS Repository Web Service enables information to be inserted into the DCS
repository, and thereby compliments the DDS Discovery System (DDS) Web Service,
which enables <i>searching</i> over the contents of the repository. Both
services are supported by the DCS.</p>

<div class="info-box">
<p>Please see the 
<a href="http://www.dlese.org/dds/services/ddsws1-0/service_specification.jsp">DDS Web Service
specification</a> for basic information about Web Services, as well as for more detailed
information about the DDS Web Service. In particular, the following concepts are explained 
in the DDS Web Service spec, which provide essential background for understanding the web services
documented on this page:</p>

<ul>
<li>Web Service Definitions and concepts, and 
<li>General HTTP request format.
</ul>

<p>To experiment with DDS Web Services, visit the <a
href="http://www.dlese.org/dds/services">DLESE Web services portal</a>.</p>
</div>


<a name="requests"></a>
<h2>DCS Repository Web Service requests and responses</h2>
This section defines the available requests or <i>verbs</i>.

<p>The HTTP request format has the following format:
[base URL]?verb=<i>request</i>[&additional arguments]. </p>

<p>For example:<br/>
<pre>http://www.dlese.org/dds/services/ddsws1-0?
        verb=GetRecord&amp;id=DLESE-000-000-000-001</pre></p>
				
<b>Summary of available requests:</b>

<p><a href="#GetId">GetId</a> - Allows an authorized client to obtain a record ID
for a specified collection. The returned ID is unique for that collection.</p>

<p><a href="#PutRecord">PutRecord</a> - Allows an authorized client to add a
metadata record to the repository in a specified collection. The metadata record
is supplied by the client, and must contain a valid and unique ID. If a record already
exists for specified collection and ID, then the existing record is replaced.</p>

<p><a href="#DeleteRecord">DeleteRecord</a> - Deletes a specified record.</p>

<a name="GetId"></a>
<h3>GetId</h3>
<b>Sample request</b>
<br/>
The following request obtains an ID for the "dcc" collection:<br/>
<br/>
<a href="http://dcs.dlese.org/schemedit/services/dcsws1-0?verb=GetId&collection=crs" 
	target="_blank">http://dcs.dlese.org/schemedit/services/dcsws1-0?verb=GetId&collection=crs</a><br/>
<br/>
<b>Summary and usage</b><br/>
<br/>
The GetId service returns a record identifier that is guaranteed to be unique
across the specified collection. This service is used in conjunction with the <a
href="#PutRecord">PutRecord</a> service to programmatically insert a record into
a particular collection. Both the GetId and PutRecord services require that the
client is authenticated to perform the requests, since they both modify the
information managed by the DCS.<br/><br/>

<b>Arguments</b><br/>
<br/>
<ul>
<li>collection - a <i>required</i> argument that specifies the collection for which the ID is obtained.
</ul>
<br/>
<b>Errors and exceptions</b><br/>
<br/>
Error is indicted if the request does not the required arguments or if an argument is included that is not supported.<br/>
<br/>
<b>Example</b><br/>
<br/>
<i><b>Request</b></i><br/>
<br/>
Request an ID for the "dcc" collection.<br/>
<br/>
<pre>http://dcs.dlese.org/schemedit/services/dcsws1-0?
           verb=GetId&amp;collection=dcc</pre><br/>
<i><b>Response</b></i><br/>
<br/>

<table border=1 cellspacing=0 cellpadding=5><tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt;
&lt;DCSWebService&gt;
  &lt;GetId&gt;
    &lt;id&gt;DCC-000-000-000-006&lt;/id&gt;
  &lt;/GetId&gt;
&lt;/DCSWebService&gt;</pre></td></tr></table>
<br/>

<i><b>Request</b></i><br/>
<br/>
Perform a request that contains a non-existent collection and thus is in error.<br/>
<br/>
<pre>http://dcs.dlese.org/schemedit/services/dcsws1-0?
		verb=GetId&amp;collection=mycollection</pre><br/>
<i><b>Response</b></i><br/>
<br/>

<table border=1 cellspacing=0 cellpadding=5><tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt;
&lt;DCSWebService&gt;
    &lt;error&gt;collection 'mycollection' not recognized&lt;/error&gt;
&lt;/DCSWebService&gt;
</pre></td></tr></table>
<br/>

<a name="PutRecord"></a>
<h3>PutRecord</h3>

<b>Summary and usage</b>

<p>The PutRecord request inserts an XML record into the specified collection in the
repository of the DCS. If a record already exists, then it is replaced with the
record passed in the PutRecord request.</p>

<p>It is commonly used by external applications that collect
information from a user, package the information as an XML record, and finally
insert the record into the DCS. The PutRecord service might also be used to
programmatically move records from some external location into the DCS.</p>

<b>Arguments</b><br/>
<br/>
<ul>

<li>xmlFormat - a <i>required</i> argument that specifies the metadata framework
of the record.</li><br/>

<li>collection - a <i>required</i> argument that specifies the collection into
which the record is to be inserted.</li><br/>

<!-- 
	notes on id param:
	- the id inside the record is the one that takes precedence. (why should it even be required?)
	- records having that id will be overwritten!
	-->
	
<li>id - a <i>required</i> argument that specifies the ID of the record (see <a
href="#GetId">GetId</a>)</li><br/>

<li>recordXml - a <i>required</i> argument supplying the XML record (as an
encoded string) to be inserted into the DCS repository. For more information
about encoding the recordXml, see the <a
href="http://www.ietf.org/rfc/rfc2396.txt">syntax rules for URIs</a>. </li><br/>
 
<li>dcsStatus - an optional argument that updates the status of this record within the DCS (the status is not
part of the metadata)</li><br/>
 
<li>dcsStatusNote - an optional argument that annotates the status of this record within the DCS (the statusNote is not
part of the metadata)</li>
</ul><br/>

<b>Examples</b><br/>
<br/>
<i><b>Request</b></i><br/>
<br/>
Invoke the PutRecord service to insert a record of <i>adn</i> format and having an 
id of <i>DCC-000-000-000-020</i> into the <i>dcc</i> collection.
 The (encoded) recordXml parameter is abbreviated for clarity.<br/>
<br/>
<pre>http://dcs.dlese.org/schemedit/services/dcsws1-0?
		verb=PutRecord&amp;xmlFormat=adn&amp;collection=dcc&amp;id=DCC-000-000-000-020&amp;recordXml=%3C...%3E</pre><br/>
<i><b>Response</b></i><br/>
<br/>
<table border=1 cellspacing=0 cellpadding=5>
<tr><td><pre>
&lt;DCSWebService&gt;
  &lt;PutRecord&gt;
    &lt;result&gt;Success&lt;/result&gt;
    &lt;xmlFormat&gt;adn&lt;/xmlFormat&gt;
    &lt;collection&gt;dcc&lt;/collection&gt;
    &lt;id&gt;DCC-000-000-000-020&lt;/id&gt;
  &lt;/PutRecord&gt;
&lt;/DCSWebService&gt;
</pre> </td></tr>
</table>
<br/>
<br/>
<i><b>Request</b></i><br/>
<br/>
Invoke the PutRecord service without the required "xmlFormat" and "collection" arguments, which results in an error. (Again, the 
(encoded) recordXml parameter is abbreviated for clarity).<br/>
<br/>
<pre>http://dcs.dlese.org/schemedit/services/dcsws1-0?
		verb=PutRecord&amp;id=DCC-000-000-000-020&amp;recordXml=%3C...%3E</pre><br/>
<i><b>Response</b></i><br/>
<br/>
<table border=1 cellspacing=0 cellpadding=5>
<tr><td><pre>
&lt;DCSWebService&gt;
  &lt;error&gt;The request had the following errors:
    &lt;err&gt;1 - The 'xmlFormat' argument is required but is missing or empty&lt;/err&gt;
    &lt;err&gt;2 - The 'collection' argument is required but is missing or empty&lt;/err&gt;
  &lt;/error&gt;
&lt;/DCSWebService&gt;
</pre> </td></tr>
</table>

<br/>
<a name="DeleteRecord"></a>
<h3>DeleteRecord</h3>
<br/>
<b>Summary and usage</b><br/>
<br/>
The DeleteRecord service removes a record specified by its ID from the DCS. The DeleteRecord call requires that the
client is authenticated to perform the request, since it modifies the
information managed by the DCS.<br/><br/>
A record cannot be deleted if it is being edited by another user in the DCS. In this case an error message is returned and 
the record is not deleted (see below).
<br/><br/>
<b>Arguments</b><br/>
<br/>
<ul>
<li>id - a <i>required</i> argument that specifies the record to be deleted.
</ul>
<br/>
<b>Errors and exceptions</b><br/>
<br/>
Error is indicted if the request does not the required arguments or if an id parameter does not correspond to a record
in the DCS.<br/>
<br/>
<b>Example</b><br/>
<br/>
<i><b>Request</b></i><br/>
<br/>
Delete an existing record.<br/>
<br/>
<pre>http://dcs.dlese.org/schemedit/services/dcsws1-0?
           verb=DeleteRecord&amp;id=DLESE-000-000-000-032</pre><br/>
<i><b>Response</b></i><br/>
<br/>
<table border=1 cellspacing=0 cellpadding=5>
<tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt;
&lt;DCSWebService&gt;
  &lt;DeleteRecord&gt;
    &lt;recordId&gt;DLESE-000-000-000-032&lt;/id&gt;
    &lt;responseDate&gt;2010-10-18T22:35:42Z&lt;/id&gt;
    &lt;result code="success"&gt;Record was successfully deleted&lt;/id&gt;
  &lt;/DeleteRecord&gt;
&lt;/DCSWebService&gt;
</pre> </td></tr>
</table>
<br/>
<br/>
<i><b>Request</b></i><br/>
<br/>
Perform a request that contains a non-existent id and thus is in error.<br/>
<br/>
<pre>http://dcs.dlese.org/schemedit/services/dcsws1-0?
		verb=DeleteRecord&amp;id=DLESE-000-000-000-XXX</pre><br/>
<i><b>Response</b></i><br/>
<br/>
<table border=1 cellspacing=0 cellpadding=5>
<tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt;
&lt;DCSWebService&gt;
    &lt;error&gt;record not found for id: DLESE-000-000-000-XXX&lt;/error&gt;
&lt;/DCSWebService&gt;
</pre> </td></tr>
</table>
<br/>
<br/>
<i><b>Request</b></i><br/>
<br/>
Perform a request upon a record that is being edited in the DCS.<br/>
<br/>
<pre>http://dcs.dlese.org/schemedit/services/dcsws1-0?
		verb=DeleteRecord&amp;id=DLESE-000-000-000-004</pre><br/>
<i><b>Response</b></i><br/>
<br/>
<table border=1 cellspacing=0 cellpadding=5>
<tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt;
&lt;DCSWebService&gt;
    &lt;error&gt;Record DLESE-000-000-000-004 is busy and cannot be deleted at this time&lt;/error&gt;
&lt;/DCSWebService&gt;
</pre> </td></tr>
</table>
<br/>

</body></html>
