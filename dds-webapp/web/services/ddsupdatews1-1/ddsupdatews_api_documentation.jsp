<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<html><head>	
		<title>Update API</title>

		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<META NAME="description" CONTENT="This page describes the Digital Discovery System (DDS) repository update Web service API.">		
		
		<%-- Note: The variable 'isDeploayedAtDL' is defined in head.jsp, set to true if the current context is DLESE library --%>
		<%@ include file="/nav/head.jsp" %>
</head>
	
<body>
<c:if test="${isDeploayedAtDL}">
	<div class="dlese_sectionTitle" id="dlese_sectionTitle">
	    DDS Update<br/>Service
	</div>
</c:if>
<%@ include file="/nav/top.jsp" %>

<div class="bodyContent">
	<div style="margin-bottom:10px;">
		<h1>Update API</h1>
		<p>API version 1.1</p>

        <a name="overview"></a>
        <h2>Overview</h2>
        <p>The Update API is a REST-RPC hybrid API for creating, updating, and deleting records and collections in a DDS repository.
        </p>

		<h2>Table of Contents</h2>
		<ol>
			<li><a href="#overview">Overview</a></li>
			<li><a href="#concepts">Definitions and concepts</a></li>
			<li><a href="#requests">Requests and responses</a></li>
			<ul>
				<li><a href="#PutCollection">PutCollection</a></li>
				<li><a href="#DeleteCollection">DeleteCollection</a></li>
				<li><a href="#PutRecord">PutRecord</a></li>
				<li><a href="#DeleteRecord">DeleteRecord</a></li>
			</ul>
			<li><a href="#resultCodes">Result codes</a></li>
			<li><a href="#errorCodes">Error codes</a>		    </li>
			<li><a href="#auth">Authentication, authorization, and security</a></li>
			<li><a href="#search">Configuring features for search</a></li>
		</ol>
</div>

    <c:if test="${initParam.enableRepositoryUpdateWebService == 'false'}">
        <h2>Status</h2>
        <p>The API is <b>disabled</b> for this repository.</p>
    </c:if>

	<a name="concepts"></a>
	<h2>Definitions and concepts</h2>
  <p>The Update API uses a REST-RPC hybrid approach to accept requests expressed as HTTP argument/value pairs.
	Requests may be made using the HTTP GET or POST method, which behave identically and vary only in the length of the
	request allowed (GET has a limited request length whereas POST is unlimited). 
	Responses are returned in XML or <a href="${f:contextUrl(pageContext.request)}/services/ddsws1-1/service_specification.jsp#json">JSON</a> format (XML by default), which varies in structure and content 
	depending on the request as shown below in the examples section of this document.   
	</p>
	<ul>
  <li><i>Base URL</i> - the base URL used to access the Web service. This is the portion of the request that precedes the request arguments. For example ${f:contextUrl(pageContext.request)}/services/ddsupdatews1-1. </li>
  <li><i>Request arguments</i> - the argument=value pairs that make up the request and follow the base URL. </li>
  <li><i>Update API response envelope</i> - the XML container used to return data. This container returns different types of data depending on the request made. </li>
  </ul>
    <h3>HTTP request format</h3>
    <p>The format of the request consists of the base URL followed by the ? character followed by one or more argument=value pairs, which are separated by the &amp; character. Each request must contain one <i>verb=request</i> pair, where <i>verb</i> is the literal string 'verb' and <i>request</i> is one of the Update API request strings defined below. All arguments must be encoded using the <a href="http://www.ietf.org/rfc/rfc2396.txt">syntax rules for URIs</a>. This is the same <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#SpecialCharacters">encoding scheme that is described by the OAI-PMH</a>.</p>
    <p>The HTTP request format has the following structure:<br>
[base URL]?verb=<i>request</i>[&amp;additional arguments]. <br>
<br>
For example:</p>
    <pre>${f:contextUrl(pageContext.request)}/services/ddsupdatews1-1<br>		?verb=PutRecord&amp;id=SAMPLE-001&amp;xmlFormat=book&amp;collection=favorites<br>		&amp;recordXml=&lt;book&gt;&lt;title&gt;Book+sample&lt;/title&gt;&lt;/book&gt;</pre>
<h1>&nbsp;</h1>
<h1><a name="requests"></a> Requests and responses</h1>
<p>This section describes the available requests, or <i>verbs</i>, and the XML responses returned by the service. Responses can also be output in JSON form. </p>
  <a name="PutCollection"></a>
  <h2>PutCollection</h2>
    <p><b>Summary and usage</b><br>
      <br>
      A collection is a container for storing  XML records in the repository. One or more collection must be created before records can be inserted. The PutCollection request may be used to create an empty collection  in the repository or update information about an existing collection such as it's title and description.  Upon calling PutCollection, if the  collection does not exist, a new one with the given <code>name</code>, <code>description</code>, <code>collectionKey</code>, <code>xmlFormat</code>, and <code>additionalMetadata</code> will be created. If the collection already exists with the given <code>collectionKey</code> and <code>xmlFormat</code>, it's <code>name</code>, <code>description</code>, and <code>additionalMetadata</code> will be updated with the new values indicated. An error is returned if an existing collection  exists with a different XML format. To change the XML format, delete the collection  and created it again with the new format. Once a collection  has been created, records may be inserted into it using the PutRecord request. </p>
    <p><b>Sample request</b><br>
      <br>
The following  request adds or updates the collection <em>favorites</em>, xmlFormat <em>book</em>, assigning it the name <em>My favorite books</em>:<br>
<br>
<code>${f:contextUrl(pageContext.request)}/services/ddsupdatews1-1?verb=PutCollection&amp;collectionKey=favorites&amp;xmlFormat=book&amp;name=My+favorite+books </code><br>
      <br>
      <b>Arguments</b></p>
    <ul>
      <li><code>collectionKey</code> - a <em>required</em> argument that specifies the key for the collection, for example <em>collection-123</em>. This key must be unique and must contain letters, numbers and the characters period, dash and underscore only <nobr>(<code>[a-zA-Z0-9.-_]</code>).</nobr> This value is  assigned to the key and record ID for the collection. </li>
      <li><span class="code"><code>xmlFormat</code></span> - a <i>required</i> argument that indicates the format for the records that reside this collection, for example <em>oai_dc</em>. The xml format must contain letters, numbers and the characters period, dash and underscore only <nobr>(<code>[a-zA-Z0-9.-_]</code>).</nobr> </li>
  <li><span class="code"><code>name</code></span> - a <em>required</em> argument that contains a descriptive name for the collection. This is written to the <code>fullTitle</code> element of the collection record. </li>
  <li><code>description</code> - an <em>option</em>al argument that contains a description for the collection. If omitted, name is used as the description.</li>
  <li><code>additionalMetadata</code> - an <em>option</em>al argument that may contain  text or XML. If supplied, the contents are written to the <code>additionalMetadata</code> element of the collection record and are searchable using individual XPath search fields when searching over both the collection records as well as the records in the collections.</li>
  </ul>
<p><b>Examples</b></p>
<p>See example above. </p>
<p><b>Response</b></p>
<p>The service responds with data about the result of the request operation. In addition, see <a href="#resultCodes">result codes</a>.</p>
<p><b>Errors and exceptions</b></p>
<p>If an error occurs, an error response is returned. See <a href="#errorCodes">error and exception conditions</a>.</p>

<a name="DeleteCollection"></a>
<h2>DeleteCollection</h2>
<p><b>Summary and usage</b><br>
  <br>
  The DeleteCollection request may be used to delete a collection from the repository. Upon completion, the collection  and all records within it will no longer reside in the repository.</p>
<p><b>Sample request</b><br>
  <br>
The following request deletes the collection <em>favorites</em> from the repository:<br>
<br>
<code>${f:contextUrl(pageContext.request)}/services/ddsupdatews1-1?verb=DeleteCollection&amp;collectionKey=favorites</code><br>
  <br>
  <b>Arguments</b></p>
<ul>
  <li><code>collectionKey</code> - a <em>required</em> argument that specifies the key for the collection to delete, for example <em>collection-123</em>.</li>
  </ul>
<p><br>
    <b>Examples</b></p>
<p>See example above. </p>
<p><b>Response</b></p>
<p>The service responds with data about the result of the request operation. In addition, see <a href="#resultCodes">result codes</a>.</p>
<p><b>Errors and exceptions</b></p>
<p>If an error occurs, an error response is returned. See <a href="#errorCodes">error and exception conditions</a>.</p>

<a name="PutRecord"></a>
<h2>PutRecord</h2>
<p><b>Summary and usage</b><br>
  <br>
  The PutRecord request may be used to add or update a single record in the repository. Records must be placed in an existing collection  and should conform to the <code>xmlFormat</code> indicated for that collection. If the record does not exist, a new record with the given content will be created.  If the record already exists, it will be replaced with the new content. An error is returned if an existing record with the same ID resides in a different collection or if the XML  input is not well formed. The caller must validate the record against it's schema and/or ensure integrity of all input prior to insertion if strict data conformance is desired.</p>
<p><b>Sample request</b><br>
  <br>
The following example request adds or updates the metadata record ID <em>SAMPLE-001</em>, xmlFormat <em>book</em> in collection <em>favorites</em>:<br>
<br>
<code>${f:contextUrl(pageContext.request)}/services/ddsupdatews1-1?verb=PutRecord&amp;id=SAMPLE-001&amp;xmlFormat=book&amp;collectionKey=favorites&amp;recordXml=&lt;book&gt;&lt;title&gt;Book+sample&lt;/title&gt;&lt;/book&gt;</code><br>
  <br>
  <b>Arguments</b></p>
<ul>
  <li><span class="code"><code>id</code></span> - a <i>required</i> argument that specifies the identifier for the record. Note that for certain XML formats the identifier is derived from the record XML, in which case this value is ignored. The service returns the identifier it assigns for the record in the response to this request. </li>
  <li><code>collectionKey</code> - a <em>required</em> argument that specifies the key for the collection  in which the record will be inserted, for example <em>collection-123</em>. This key corresponds to the one indicated in the PutCollection request. </li>
  <li><span class="code"><code>xmlFormat</code></span> - a <i>required</i> argument that indicates the format of the record, which must match the format associated with the collection in which the record is being put, for example <em>oai_dc</em>. This xmlFormat corresponds to the one indicated in the PutCollection request.</li>
  <li><span class="code"><code>recordXml</code></span> - a <em>required</em> argument that contains the XML for the record being inserted. The XML should conform to the format specified in the <code>xmlFormat</code> argument. The caller must validate all input prior to insertion if strict data conformance is desired. </li>
</ul>
<p><br>
    <b>Examples</b></p>
<p>See example above. </p>
<p><b>Response</b></p>
<p>The service responds with data about the result of the request operation. In addition, see <a href="#resultCodes">result codes</a>.</p>
<p><b>Errors and exceptions</b></p>
<p>If an error occurs, an error response is returned. See <a href="#errorCodes">error and exception conditions</a>.</p>

<a name="DeleteRecord"></a>
<h2>DeleteRecord</h2>
<p><b>Summary and usage</b><br>
  <br>
  The DeleteRecord request may be used to delete a record from the repository. Upon completion, the record will no longer reside in the repository. </p>
<p><b>Sample request</b><br>
  <br>
The following request deletes the record <em>SAMPLE-001</em> from the repository:<br>
<br>
<code>${f:contextUrl(pageContext.request)}/services/ddsupdatews1-1?verb=DeleteRecord&amp;id=SAMPLE-001</code><br>
  <br>
  <b>Arguments</b></p>
<ul>
  <li><code>id</code> - a <em>required</em> argument that specifies the id for the record to delete<em>.</em></li>
</ul>
<p><br>
    <b>Examples</b></p>
<p>See example above. </p>
<p><b>Response</b></p>
<p>The service responds with data about the result of the request operation. In addition, see <a href="#resultCodes">result codes</a>.</p>
<p><b>Errors and exceptions</b></p>
<p>If an error occurs, an error response is returned. See <a href="#errorCodes">error and exception conditions</a>.</p>
<p>&nbsp;</p>

<h3><a name="resultCodes"></a> Result Codes </h3>
<p>When  the service responds to a request it includes a <code>&lt;result&gt;</code> element and <code>resultCode</code> attribute that indicates the result of the request.  Clients are advised to test the value of these codes and respond appropriately. A human readable message is also supplied. <br>
    <br>
</p>
<table width="600px" border="0" cellpadding="6" cellspacing="1" class="bgTable">
  <TBODY>
    <TR>
      <TH align=middle>Result Codes</TH>
      <TH align=middle>Description</TH>
      <TH align=middle>Applicable Verbs</TH>
    </TR>
    <TR>
      <TD>success</TD>
      <TD>The result of the request was successful. </TD>
      <TD align=middle><I>all verbs</I></TD>
    </TR>
    <TR>
      <TD>collectionDoesNotExist</TD>
      <TD>The collection indicated does not exists in the repository. </TD>
      <TD align=middle><code>DeleteCollection</code></TD>
    </TR>
    <TR>
      <TD>recordDoesNotExist</TD>
      <TD>The record indicated does not exists in the repository. </TD>
      <TD 
align=middle><code>DeleteRecord</code></TD>
    </TR>
  </TBODY>
</TABLE>
<p>&nbsp;</p>

<h3><a name="errorCodes"></a> Error and Exception Conditions</h3>
<p>If an error or exception occurs, the service returns an <code>&lt;error&gt;</code> element with the type of error indicated by a <code>code</code> attribute. Clients are advised to test the value of these codes and respond appropriately. A human readable error message is also supplied. <br>
    <br>
</p>
<table width="600px" border="0" cellpadding="6" cellspacing="1" class="bgTable">
  <TBODY>
    <TR>
      <TH align=middle>Error Codes</TH>
      <TH align=middle>Description</TH>
      <TH align=middle>Applicable Verbs</TH>
    </TR>
    <TR>
      <TD><CODE>badVerb</CODE></TD>
      <TD>Value of the <CODE>verb</CODE> argument is not  legal or the verb argument is missing. </TD>
      <TD align=middle><I>N/A</I></TD>
    </TR>	
    <TR>
      <TD><CODE>badArgument</CODE></TD>
      <TD>The request includes illegal arguments, is missing required arguments, includes a repeated argument, or values for arguments have an illegal syntax.</TD>
      <TD align=middle><I>all verbs</I></TD>
    </TR>
    <TR>
      <TD><CODE>serviceDisabled</CODE></TD>
      <TD>The service is disabled and not available for the repository.</TD>
      <TD 
align=middle><I>all verbs</I></TD>
    </TR>
    <TR>
      <TD><CODE>illegalOperation</CODE></TD>
      <TD>The operation is not legal for the given request.</TD>
      <TD 
align=middle><I>all verbs</I></TD>
    </TR>
    <TR>
      <TD><CODE>notAuthorized</CODE></TD>
      <TD>The client that made the request is not authorized to use the service.</TD>
      <TD 
align=middle><I>all verbs</I></TD>
    </TR>
    <TR>
      <TD><CODE>internalServerError</CODE></TD>
      <TD>The server encountered a problem and was not able to respond to the request.</TD>
      <TD 
align=middle><I>all verbs</I></TD>
    </TR>
  </TBODY>
</TABLE>

<p>&nbsp;</p>
<h1><a name="auth"></a></h1>
<h1>Authentication, authorization,   and security</h1>
<p>The Update API does not include a native authentication/authorization model, however it is possible to ensure reasonable security by configuring a DDS repository using the techniques outlined below. Appropriate steps should be taken to secure the repository before enabling the API. This section is meant for administrators who are establishing a repository and assumes prior knowledge of network security,   Servlets, Tomcat, the Apache web server, and other  components in a typical Tomcat webapp installation.</p>
<h3>Configuring for security </h3>
<ol>
  <li>Restrict access to the API  to only localhost  and/or hosts on a trusted Local Area Network. This may be done by placing  Tomcat's http server ports behind a firewalled host that allows access only from localhost and/or  other  trusted hosts on a closed network. With this kind of host configuration it is possible to use the Apache web server and <code>modproxy</code> or <code>modjk</code> to allow public access to desired portions of the DDS webapp, such as the Search API, while restricting access to the Update API and/or other portions of the webapp.</li>
  <li>If the API must be accessed over a Wide Area Network, consider the following options:
    <ol>
      <li>Configure the API to allow access from authorized IP addresses only. To do so, enter the IP in the configuration section found in the DDS  <a href="${pageContext.request.contextPath}/admin/admin.do">Collection Manager</a>.</li>
      <li>Configure the  API to run over SSL to ensure all data is encrypted as it is sent over the Internet. The Apache web server may be used to configure SSL, with  <code>modproxy</code> or <code>modjk</code> used to provide access  to the API and/or other portions of the webapp. Clients must support SSL in order to communicate with the API configured in this way.</li>
      <li>Configure the API to require BASIC authorization for access. The Apache web server may be used to configure BASIC Auth, with  <code>modproxy</code> or <code>modjk</code> used to provide access  to the API and/or other portions of the webapp. Clients must support BASIC auth in order to communicate with the API configured in this way.</li>
      </ol>
  </li>
</ol>
<h3>Enabling the Update API</h3>
<p>By default the Update API is disabled when DDS is installed. To enable the service, use the context parameter <code>enableRepositoryUpdateWebService</code>, found in the DDS <code>web.xml</code> deployment descriptor (see comments therein for details).</p>
<p>&nbsp;</p>

<a name="search"></a>
<h1>Configuring features for search</h1>
<p>The DDS repository system provides a  flexible architecture for configuring the search fields, facets, and relationships that are created for the XML records inserted into the repository. See  <a href="${pageContext.request.contextPath}/docs/configure_dds_search_fields.jsp">Configuring Search Fields, Facets, and Relationships for XML Frameworks</a> for information. </p>
<p>&nbsp; </p>
<p>Last revised: $Date: 2016/04/27 19:20:13 $</p>

<%@ include file="/nav/bottom.jsp" %>
</div>
</body>
</html>
