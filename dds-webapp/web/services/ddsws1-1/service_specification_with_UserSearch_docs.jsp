<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ include file="/ddswsBaseUrl.jsp" %><%-- Sets the DDSWS 1-1 baseURL into variable 'ddsws11BaseUrl' --%>

<html>
<head>

<title>Search API: Documentation</title>

<META NAME="description" CONTENT="The Digital Discovery System Web Service (DDSWS) 
	is a search and retrieval service API for items that reside in a digital repository, 
	and is available from the Digital Discovery System (DDS) 
	and the Digital Collection System (DCS). The service may be used to search over and retrieve items
	from it's repository in real-time, and is designed to be used by high-availability applications.">

<%@ include file="/nav/head.jsp" %>
<c:set var="serverContextUrl" value="${domain}${pageContext.request.contextPath}"/>

<style type="text/css">
<!--
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
	.subSection {
		font-weight: bold;
		font-style: italic;
		color: #333333;
	}
	.thHeadRow {
		background-color:#ddd;
	}
-->
</style>

</head>

<body bgcolor="#ffffff" text="#000000" link="#0000cc" alink="#ff0000" vlink="#551a8b" leftmargin="8" topmargin="8">
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Search<br/>Service API
</div>

<%@ include file="/nav/top.jsp" %>

<a name="top"></a>

<h1>Search API Documentation</h1>
<div>
	<p>Service version: DDSWS v1.1</p>
	<h3>Table of Contents</h3>
	<ol>
		<li><a href="#overview">Overview</a></li>
		<li><a href="#concepts">Definitions and concepts</a></li>
		<li><a href="#requests">Service requests</a></li>
		<ul>
			<li><a href="#Search">Search</a></li>
			<li><a href="#UserSearch">UserSearch</a></li>
			<li><a href="#GetRecord">GetRecord</a></li>
			<li><a href="#ListFields">ListFields</a></li>
			<li><a href="#ListTerms">ListTerms</a></li>			
			<li><a href="#ListCollections">ListCollections</a></li>
			<li><a href="#ListGradeRanges">ListGradeRanges</a></li>
			<li><a href="#ListSubjects">ListSubjects</a></li>
			<li><a href="#ListResourceTypes">ListResourceTypes</a></li>
			<li><a href="#ListContentStandards">ListContentStandards</a></li>
			<li><a href="#ListXmlFormats">ListXmlFormats</a></li>
			<li><a href="#UrlCheck">UrlCheck</a></li>
			<li><a href="#ServiceInfo">ServiceInfo</a></li>
		</ul>
		<li><a href="#responses">Service responses</a></li>
		<ul>
			<li><a href="#errorCodes">Error and exception conditions</a></li>
			<li><a href="#json">Requesting JSON output</a></li>
			<li><a href="#localize">Removing namespaces</a></li>
		</ul>		
		<li><a href="#availableSearchFields">Search fields</a></li>
		<ul>
			<li><a href="#searchFieldsStandard">Standard search fields</a></li>
			<li><a href="#searchFieldsXPath">XPath search fields</a></li>
			<li><a href="#searchFieldsCustom">Custom search fields</a></li>
		</ul>				
		<li><a href="#Example_search_queries">Example search queries</a></li>
		<li><a href="#configureDDS">Configure search fields </a></li>
	</ol>
</div>
	
<a name="overview"></a>
<h2>Overview</h2>
    The Digital Discovery System Search Service (DDSWS) is a search and retrieval service API for items that reside in a digital repository, and is available from the <a href="/dds/services/dds_software.jsp">Digital Discovery System</a> (DDS) and the <a href="/dds/services/dcs_software.jsp">Digital Collection System</a> (DCS).
	Service requests are expressed as HTTP argument/value pairs and responses may be returned as XML or JSON.<br>
	<br>
	The primary service request is <a href="#Search">Search</a>, which  provides a wide  range of Information Retrieval features that are implemented using the <a href="http://lucene.apache.org/">Lucene</a> search engine and supports textual searching over repository metadata and content, searching within specific fields, date ranges, <a href="#geoSearch">geospatial bounding box search</a>, and other functionality.  Metadata are returned from the service for the objects that reside in the repository and may be disseminated in a number of XML formats as indicated by the <a href="#ListXmlFormats">ListXmlFormats</a> request.<br>
    <br>
   Web service requests and responses are described in detail below and examples are provided for reference by developers. 
   <%-- Developers may also find the Web service client <a href="${serverContextUrl}/services/examples/ddsws1-1/">JSP template and examples</a> helpful.<br> --%>
  <br><br>

<a name="concepts"></a>
<h2>Definitions and concepts</h2>
DDSWS is a Representational State Transfer (REST) style Web service API.  Service requests are expressed as HTTP argument/value pairs. These requests must be in either GET or POST format. Responses are returned in XML format by default, which varies in structure and content depending on the request as shown below in the examples section of this document. Responses can also be <a href="#json">returned as JSON</a> (JavaScript Object Notation) as an alternate output format to XML.<br>
<br>
<ul>
<li><i>Base URL</i> - the base URL used to access the Web service. This is the portion of the request that precedes the request arguments. For example ${serverContextUrl}/services/ddsws1-1.
<li><i>Request arguments</i> - the argument=value pairs that make up the request and follow the base URL. 
<li><i>DDSWS response envelope</i> - the XML container used to return data. This container returns different types of data depending on the request made.
</ul>
<br>
<h3>HTTP request format</h3>
The format of the request consists of the base URL followed by the ? character followed by one or more argument=value pairs, which are separated by the & character. Each request must contain one <i>verb=request</i> pair, where <i>verb</i> is the literal string 'verb' and <i>request</i> is one of the DDSWS request strings defined below. All arguments must be encoded using the <a href="http://www.ietf.org/rfc/rfc2396.txt">syntax rules for URIs</a>. This is the same <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#SpecialCharacters">encoding scheme that is described by the OAI-PMH</a>.<br>
<br>
<a name="requests"></a>
<h2>Service requests</h2>
This section defines the available requests, or <i>verbs</i>.<br>
<br>
The HTTP request format has the following structure:<br>
[base URL]?verb=<i>request</i>[&additional arguments]. <br>
<br>
For example:<br>
<pre>${ddsws11BaseUrl}?
        verb=GetRecord&amp;id=DLESE-000-000-000-001</pre><br>
<b>Summary of available requests:</b><br>
<br>
<a href="#Search">Search</a> - Allows a client to search across resources in the repository using standard Lucene queries, which support  term, field and phrase searches, term and term/field boosting, term stemming, wildcard and fuzzy searches, term proximity searches, and other functionality. The Search request has access to a wide range of <a href="#availableSearchFields">search fields</a>, and through the use of query clauses, can be used to apply custom search rank algorithms  (see <a href="#Example_search_queries">example search queries</a>). The request also supports searching by <a href="#ListXmlFormats">XML format</a>, date ranges, <a href="#geoSearch">geospatial bounding box search</a>, and other functionality.<br>
<br>
<a href="#UserSearch">UserSearch</a> - Is nearly identical to the Search request except that it operates over educational resources in the <a href="http://www.dlese.org/Metadata/adn-item/">ADN metadata format</a> only, and it applies a default searcher that automatically performs word stemming and  relevancy rank boosting for items that match higher relevancy search indicators such as when a matching  term appears in the title field as opposed to elsewhere. These search algorithms  are the same as those that are applied to user's searches in the DLESE library. This request is meant to to be used by clients working with ADN resources and that wish to  leverage the automatic word stemming and search rank algorithms that are applied.<br>
<br>
<a href="#ListFields">ListFields</a> - Accesses the fields in the index.<br>
<br>
<a href="#ListTerms">ListTerms</a> - Accesses the terms in a given field or fields.<br>
<br>
<a href="#GetRecord">GetRecord</a> - Accesses the metadata for a single record.<br>
<br>
<a href="#ListCollections">ListCollections</a> - Accesses the list of available metadata collections in the repository.<br>
<br>
<a href="#ListGradeRanges">ListGradeRanges</a> - Accesses the list of DLESE-specific controlled vocabularies and search keys for grade ranges.<br>
<br>
<a href="#ListSubjects">ListSubjects</a> - Accesses the list of DLESE-specific controlled vocabularies and search keys for subjects.<br>
<br>
<a href="#ListResourceTypes">ListResourceTypes</a> - Accesses the list of DLESE-specific controlled vocabularies and search keys for resource types.<br>
<br>
<a href="#ListContentStandards">ListContentStandards</a> - Accesses the list of DLESE-specific controlled vocabularies and search keys for content standards.<br>
<br>
<a href="#ListXmlFormats">ListXmlFormats</a> - Accesses the list of the available XML formats from this service.<br>
<br>
<a href="#UrlCheck">UrlCheck</a> - Allows a client to check whether a given URL is cataloged in the repository.<br>
<br>
<a href="#ServiceInfo">ServiceInfo</a> - Accesses information about this Web service.<br>

        <br>
        <br>
        <a name="Search"></a>
<h3>Search</h3>


	<b>Sample request</b><br>
        <br>
        The following request performs a search for the term "ocean" and returns 10 search results, starting at position 0:<br>
        <br>
        <a href="${ddsws11BaseUrl}?verb=Search&q=ocean&s=0&n=10&client=ddsws-documentation" target="_blank">${ddsws11BaseUrl}?verb=Search&q=ocean&s=0&n=10&client=ddsws-documentation</a><br>
        <br>
        <b>Summary and usage</b><br>
        <br>
        The Search request allows a client  to search across resources in the repository using standard Lucene queries, which support term, field and phrase searches, term and term/field boosting, term stemming, wildcard and fuzzy searches, term proximity searches, and other functionality. The Search request has access to a wide range of <a href="#availableSearchFields">search fields</a>, and through the use of query clauses, can be used to apply custom search rank algorithms (see <a href="#Example_search_queries">example search queries</a>). The request also supports searching by <a href="#ListXmlFormats">XML format</a>, date ranges, <a href="#geoSearch">geospatial bounding box search</a>, and other functionality.<br>
    
	    <br>
	    The Search and UserSearch response consists of an ordered set of metadata records, sorted by relevancy. 
	    The Search request searches over all XML formats that are available in the repository, 
	    unless otherwise specified in the 'xmlFormat' argument as described below. The UserSearch request searches over the records available in the ADN format only.
	    Flow control is managed by the client, which may 'page through' a 
	    set of results using the 's' and 'n' arguments as described below. <br>
	    
	    <br>
          The Search and UserSearch requests accept queries supplied in the standard <a href="http://lucene.apache.org/java/2_4_0/queryparsersyntax.html">Lucene Query Syntax</a> (LQS). 
	      LQS supports advanced Information Retrieval query clauses such as term and field boosting, wildcard and fuzzy searches, etc. Queries are supplied in the <i>q</i> 
	      argument of the request.<br>
          <br>
          <b>Arguments</b><br>
          <br>
          <span class="subSection">Textual and fielded searches:</span> The following argument is used to conduct textual and fielded searches and 
	      may be performed independently or in combination with other search criteria described below.<br>
	      <br>  
	    
	<ul>
	<li><span class="code">q</span> - (query) an <i>optional</i> argument that may contain plain text or field/term specifiers. 
	Boolean logic, field/term specifiers and boosting must be specified using the <a href="http://lucene.apache.org/java/2_4_0/queryparsersyntax.html">Lucene Query Syntax</a> (LQS). 
	Plain text terms (when no field is indicated) are used to search in the default field, which contains textual metadata extracted from the title, description, keywords, 
	grade ranges, resource types and other areas of the repository metadata. 
	See available <a href="#availableSearchFields">search fields</a> for detailed information about the fields that are available for searching.
	</ul>

<br>
<span class="subSection">Controlled vocabulary searches:</span> The following arguments perform a search by controlled vocabulary and
may be performed independently or in combination with other search criteria. The searchKey that must be used with these arguments must be discovered using 
the <a href="#VocabList">vocabulary list requests</a>. Example searchKey <span class="code">gr=07</span>. If supplied, the controlled vocabulary portion of the search 
criteria <em>must</em> match a given record in order for it to be included in the results. Note that searching by grade range (<code>gr</code>), resource type (<code>re</code>), subject (<code>su</code>) or content standard (<code>cs</code>) is useful only for clients that wish to search over <a href="http://www.dlese.org/Metadata/adn-item/">ADN records</a> using these DLESE-specific vocabularies.<br>
<br>
<ul>
<li><span class="code">ky</span> - (collection) an <i>optional repeatable</i> argument that limits the search to records that reside in the given metadata collection(s).
</ul>
<br>
<ul>
<li><span class="code">gr</span> - (grade range) an <i>optional repeatable</i> argument that limits the search to ADN records that contain the given grade range(s).  These grade ranges are a DLESE-specific controlled vocabulary that is part of the ADN framework.
</ul>
<br>
<ul>
<li><span class="code">re</span> - (resource type) an <i>optional repeatable</i> argument that limits the search to ADN records that contain the given resource type(s). These resource types are a DLESE-specific controlled vocabulary that is part of the ADN framework. 
</ul>
<br>
<ul>
<li><span class="code">su</span> - (subject) an <i>optional repeatable</i> argument that limits the search to ADN records that contain the given subject(s). These subjects are a DLESE-specific controlled vocabulary that is part of the ADN framework. 
</ul>
<br>
<ul>
<li><span class="code">cs</span> - (content standard) an <i>optional repeatable</i> argument that limits the search to ADN records that contain the given content standard(s).  These content standards are a DLESE-specific controlled vocabulary that is part of the ADN framework. 
</ul>

<br>
<span class="subSection">Date range searches:</span> The following arguments instruct the service to search in a given index date field and may be performed 
independently or in combination with other search criteria. The values provided in the fromDate or toDate arguments must be a union date type string of the 
form yyyy-MM-dd or an ISO8601 UTC datastamp of the form yyyy-MM-ddTHH:mm:ssZ. Example dates include 2004-07-08 or 2004-07-26T21:58:25Z. 
The <a href="#availableSearchFields">fields that are available for searching by date</a> are listed below. If supplied, the date range portion 
of the search criteria <em>must</em> match a given record in order for it to be included in the results. These arguments are <i>Not supported in the UserSearch request.</i> <br>
<br>
<ul>
<li><span class="code">dateField</span> - an <i>optional</i> argument that indicates which index date field to search in. If supplied, one or both of either the <span class="code">fromDate</span> or <span class="code">toDate</span> arguments <i>must</i> be supplied.
</ul>
<br>
<ul>
<li><span class="code">fromDate</span> - an <i>optional</i> argument that indicates a date range to search from. If supplied, the <span class="code">dateField</span> argument <i>must</i> also be supplied.
</ul>
<br>
<ul>
<li><span class="code">toDate</span> - an <i>optional</i> argument that indicates a date range to search to. If supplied, the <span class="code">dateField</span> argument <i>must</i> also be supplied.
</ul>
<a name="geoSearch"></a>
<br>
<span class="subSection">Geospatial searches: </span>Geospatial searches operate over each record that  has associated with it a <EM>geographic footprint</EM> (a geographic region representing the records's area of relevance) in the form of a <EM>box</EM> (defined below). A <EM>geospatial query</EM> takes a query region (also in the form of a box) and a spatial predicate (one of &quot;within,&quot; &quot;contains,&quot; &quot;overlaps,&quot;) and returns all documents that 1) have a geographic footprint that 2) has the predicate relationship to the query region.
<br><br>	
Formally, a <EM>box</EM> is a geographic region defined by north and south bounding coordinates (latitudes expressed in degrees north of the equator and in the range [-90,90]) and east and west bounding coordinates (longitudes expressed in degrees east of the Greenwich meridian and in the range [-180,180]). The north bounding coordinate must be greater than or equal to the south. The west bounding coordinate may be less than, equal to, or greater than the east; in the latter case, a box that crosses the &plusmn;180&deg; meridian is described. As a special case, the set of all longitudes is described by a west bounding coordinate of -180 and an east bounding coordinate of 180.
<br><br>
The following arguments instruct the service to conduct a geospatial query over the subset of records that contain a geospatial  footprint. Geospatial queries may be performed independently or in combination with other search criteria. To perform a geospatial query, <em>all five of the required geospatial arguments must be included</em>, otherwise none may be included, and thus are <em>conditionally required</em>. If an error in the request arguments is encountered, the service will return an appropriate error response and message. The optional geospatial argument <em>may</em> be included if desired.
<br>
<br>
<ul>
  <li><span class="code">geoPredicate</span> - a <i>conditionally required</i> argument that indicates the   relationship to the query region. Values must be one of <nobr>[ <CODE>within</CODE> | <CODE>overlaps</CODE> | <CODE>contains</CODE> ].</nobr>
</ul>
<br>
<ul>
  <li><span class="code">geoBBNorth</span> - a <i>conditionally required</i> argument that indicates the northern most latitude of search. Values must be a floating point number in the range <nobr>[-90,90].</nobr>
</ul>
<br>
<ul> 
  <li><span class="code">geoBBSouth</span> - a <i>conditionally required</i> argument that indicates the southern most latitude of search. Values must be a floating point number in the range <nobr>[-90,90].</nobr>
</ul>
<br>
<ul>  
  <li><span class="code">geoBBWest</span> - a <i>conditionally required</i> argument that indicates the western most longitude of search. Values must be a floating point number in the range <nobr>[-180,180].</nobr>
</ul>
<br>
<ul>  
  <li><span class="code">geoBBEast</span> - a <i>conditionally required</i> argument that indicates the eastern most longitude of search. Values must be a floating point number in the range <nobr>[-180,180].</nobr>
</ul>
<br>
<ul>
  <li><span class="code">geoClause</span> - an <i>optional</i> argument that indicates the boolean clause applied to the geospatial portion of the search. Values must be one of <nobr>[ <CODE>must</CODE> | <CODE>should</CODE> ],</nobr> where <CODE>must</CODE> indicates the geospatial portion of the search criteria <em>must</em> match a given record in order for it to be included in the results; <CODE>should</CODE> indicates it <em>should</em> match but is not required in order to appear in the search results. Default value is <CODE>must</CODE>. 
</ul>
<br>
<span class="subSection">Flow control:</span> A search client can control the flow of paging through a set of search results and the size of the result set using the the <span class="code">s</span> (starting offset) and <span class="code">n</span> (number returned) arguments.  As an example, when a search is initially performed, the client might construct a request that supplies the arguments <span class="code">s=0</span> and <span class="code">n=10</span> to return up to the first 10 matching results. The client would then page through the set of results by issuing subsequent requests indicating <span class="code">s=10</span> and <span class="code">n=10</span> for the next ten results, <span class="code">s=20</span> and <span class="code">n=10</span> for results 20 through 30 and so forth up to <code>totalNumResults</code>. To retrieve each successive segment of search results the client must supply identical search criteria in all search related arguments (<span class="code">q, xmlFormat, gr, su, cs, re, xmlFormat, so,</span> etc.),  sorting and date-restrictive arguments. DDS search  is deterministic and the set and order of search results are guaranteed to be identical for any two identical searches (assuming the repository has not changed in the interim). Thus the <span class="code">s</span> and <span class="code">n</span> arguments can be thought of as indicating the 'window' into the set of ordered search results into which the client wants to see.<br>
<br>
<ul>
<li><span class="code">s</span> - (starting offset) - a <i>required</i> argument that specifies the starting offset into the results set upon which metadata records should be returned. May be any integer grater than or equal to 0.
</ul>
<br>
<ul>
<li><span class="code">n</span> - (number returned) - a <i>required</i> argument that specifies the number of metadata records to return, beginning at the offset specified by <span class="code">s</span>. Must be a integer from 1 to <span class="code">maxSearchResultsAllowed</span>, as indicated in the response to the <a href="#ServiceInfo">ServiceInfo</a> request. The maximum allowed by this server is ${maxNumResultsDDSWS}.
</ul>
<br>


<span class="subSection">Additional arguments:</span> The following arguments may also be supplied in the request.<br>
<br>
<ul>
<li><span class="code">xmlFormat</span> - an <i>optional</i> argument that indicates the format the records must be returned in. If specified, searches are limited to only those records that can be disseminated in the given format. If not specified, the records will be returned in their native format using a localized version of 
XML (e.g. stripped of their namespace and schema declarations). The available formats may be discovered using the <a href="#ListXmlFormats">ListXmlFormats</a> request. 
<i>Not supported in the UserSearch request.</i>
</ul>
 <br>
<ul>
<li><span class="code">client</span> - an <i>optional</i> argument that may be supplied by the client to indicate where the request originated from. Example values might be <i>ddsExamplesSearchClient</i> or <i>myLibrarySearchClient</i>. When supplied, this information is used by the services administrators to  help understand how people are using the service on a client-by-client basis.
</ul>
<br>
<ul>
<li><span class="code">so</span> - (search over) an <i>optional</i> argument that must contain the value allRecords or discoverableRecords. Clients that request to search over allRecords must be authorized by IP, otherwise an error is returned. Defaults to discoverableRecords. <i>Not supported in the UserSearch request.</i>
</ul>
<br>

<span class="subSection">Sorting the response:</span> The following two arguments instruct the service to sort the response by a given index field. The service sorts the entire result set lexically prior to returning the requested portion of the results. Only one of these two arguments may be supplied in the request. Values must a <a href="#availableSearchFields">sortable field in the index</a>, as listed below. These arguments are <i>Not supported in the UserSearch request.</i><br>
 <br>
<ul>
  <li><span class="code">sortAscendingBy</span> - an <i>optional</i> argument that instructs the service to sort the search results in ascending lexical order by a given index field. 
</ul>
<br>
<ul>
  <li><span class="code">sortDescendingBy</span> - an <i>optional</i> argument that instructs the service to sort the search results in descending lexical order by a given index field. 
</ul>
<p>&nbsp;  </p>
<br>
<b>Errors and exceptions</b><br>
<br>
See <a href="#errorCodes">error and exception conditions</a>.<br>
<br>
<b>Examples</b><br>
<br>
<i><b>Request</b></i><br>
<br>
Search for the word ocean.<br>
<br>
<pre>${ddsws11BaseUrl}?
           verb=Search&amp;q=ocean&amp;s=0&amp;n=10</pre><br>
<i><b>Response</b></i><br>
<br>
<table border=0 cellspacing=0 cellpadding=0>
<tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 
&lt;DDSWebService&gt;
  &lt;Search&gt;
    &lt;resultInfo&gt;
      &lt;totalNumResults&gt;520&lt;/totalNumResults&gt;
      &lt;numReturned&gt;10&lt;/numReturned&gt;
      &lt;offset&gt;0&lt;/offset&gt;
    &lt;/resultInfo&gt;
    &lt;results&gt;
      &lt;record&gt;
        &lt;head&gt;
          &lt;id&gt;DLESE-COLLECTION-000-000-000-018&lt;/id&gt;
          &lt;collection recordId="DLESE-COLLECTION-000-000-000-012"&gt;
              Science Ed Resource Center (SERC)&lt;/collection&gt;
          &lt;xmlFormat&gt;dlese_collect&lt;/xmlFormat&gt;
          &lt;fileLastModified&gt;2004-03-29T20:44:41Z&lt;/fileLastModified&gt;
          &lt;whatsNewDate type="collection"&gt;2004-03-29&lt;/whatsNewDate&gt;
          &lt;additionalMetadata realm="dlese_collect"&gt;
            &lt;formatOfRecords&gt;adn&lt;/formatOfRecords&gt;
            &lt;isEnabled&gt;true&lt;/isEnabled&gt;
            &lt;numRecords&gt;325&lt;/numRecords&gt;
            &lt;numRecordsIndexed&gt;324&lt;/numRecordsIndexed&gt;
            &lt;partOfDrc&gt;false&lt;/partOfDrc&gt;
          &lt;/additionalMetadata&gt;
        &lt;/head&gt;
        &lt;metadata&gt;
          &lt;collectionRecord&gt;
            &lt;general&gt;
              &lt;fullTitle&gt;Carleton College Science Education 
               Resource Center (SERC) - Starting Point Entry 
               Level Geoscience Collection
              &lt;/fullTitle&gt;
              ...

&lt;/DDSWebService&gt;
</pre> </td></tr>
</table>
<br>
<i><b>Request</b></i><br>
<br>
Search for the word ocean and limit the search to grade range High (9-12).<br>
<br>
<pre>${ddsws11BaseUrl}?
           verb=Search&amp;q=ocean&amp;gr=02&amp;s=0&amp;n=10</pre><br>
<i><b>Response</b></i><br>
<br>
<table border=0 cellspacing=0 cellpadding=0>
<tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 
&lt;DDSWebService&gt;
  &lt;Search&gt;
    &lt;resultInfo&gt;
      &lt;totalNumResults&gt;208&lt;/totalNumResults&gt;
      &lt;numReturned&gt;10&lt;/numReturned&gt;
      &lt;offset&gt;0&lt;/offset&gt;
    &lt;/resultInfo&gt;
    &lt;results&gt;
      &lt;record&gt;
	&lt;head&gt;
          &lt;id&gt;NASA-Edmall-2315&lt;/id&gt;
          &lt;collection recordId="DLESE-COLLECTION-000-000-000-014"&gt;
             NASA ED Mall Collection&lt;/collection&gt;
          &lt;xmlFormat&gt;adn&lt;/xmlFormat&gt;
          &lt;fileLastModified&gt;2004-06-17T18:24:10Z&lt;/fileLastModified&gt;
          &lt;whatsNewDate type="itemnew"&gt;2003-07-29&lt;/whatsNewDate&gt;
          &lt;additionalMetadata realm="adn"&gt;
            &lt;accessionStatus&gt;
               accessioneddiscoverable
            &lt;/accessionStatus&gt;
            &lt;partOfDrc&gt;false&lt;/partOfDrc&gt;
          &lt;/additionalMetadata&gt;
        &lt;/head&gt;
        &lt;metadata&gt;
          &lt;itemRecord&gt;
            &lt;general&gt;
              &lt;title&gt;Coriolis Force&lt;/title&gt;
              ...

&lt;/DDSWebService&gt;
</pre> </td></tr>
</table>
<br>
<i><b>Request</b></i><br>
<br>
Search for all ADN records new to the repository since July 7th, 2004 and sort descending by the wndate field.<br>
<br>
<pre>${ddsws11BaseUrl}?
verb=Search&amp;s=0&amp;n=10&amp;fromDate=2004-07-08&amp;dateField=wndate
&amp;sortDescendingBy=wndate&amp;xmlFormat=adn-localized</pre><br>
<i><b>Response</b></i><br>
<br>
<table border=0 cellspacing=0 cellpadding=0>
<tr><td>Same format as above.</td></tr>
</table>
<br><br>
<br>



<a name="UserSearch"></a>
<h3>UserSearch</h3>
<p><b>Sample request</b><br>
      <br>
      The following request performs a search for the term "ocean" and returns 10 search results, starting at position 0:<br>
      <br>
      <a href="${ddsws11BaseUrl}?verb=UserSearch&q=ocean&s=0&n=10&client=ddsws-documentation" target="_blank">${ddsws11BaseUrl}?verb=UserSearch&q=ocean&s=0&n=10&client=ddsws-documentation</a><br>
      <br>
      <b>Summary and usage</b><br>
      <br>
      The UserSearch request is nearly identical to the Search request except that it operates over educational resources in the <a href="http://www.dlese.org/Metadata/adn-item/">ADN metadata format</a> only, and it applies a default searcher that automatically performs word stemming and relevancy rank boosting for items that match higher relevancy search indicators such as when a matching term appears in the title field as opposed to elsewhere. These search algorithms are the same as those that are applied to user's searches in the DLESE library. This request is meant to to be used by clients working with ADN resources and that wish to leverage the automatic word stemming and search rank algorithms that are applied.<br>
	    <br>
	    The UserSearch response is identical to the Search response and consists of an ordered set of ADN records, sorted by relevancy. The default searcher that is used incorporates several Information Retrieval techniques designed to augment the search rank and total number of results. These augmentations include word stemming, boosting of records that contain search terms in their title or description and a slight boosting of records that are cataloged by two or more collections or are part of the <a href="${serverContextUrl}/collection.do?key=drc">DLESE Reviewed Collection</a>.  The default searcher's algorithms are applied automatically to all terms and phrases supplied by the client in the default field portion of the query sent in the request, and all other fields in the query are treated normally (see available <a href="#availableSearchFields">search fields</a> for examples and details). Clients may use this request as a starting point and apply additional boosting to what is provided by the default searcher by supplying additional ranking clauses in the  query sent in the request. Clients wishing to implement their own search rank algorithms fully from scratch, or to search over records in formats other than ADN, should use the <a href="#Search">Search</a> request.<br>
          <br>
          <b>Arguments</b><br>
          <br>
          UserSearch accepts the same arguments as the <a href="#Search">Search</a> request, with the exception of <em>xmlFormat</em>, <em>sortAscendingBy</em>, <em>sortDescendingBy</em>, <em>dateField</em>, <em>fromDate</em>,  <em>toDate</em>, and <em>so</em>. <br>
          <br>
          <b>Errors and exceptions</b><br>
          <br>
          See <a href="#errorCodes">error and exception conditions</a>.<br>
          <br>
          <b>Examples</b> <br>
          <br>
          <i><b>Request</b></i> 
          <br>
          <br>
          Same as the <a href="#Search">Search</a> request, however the verb argument must be indicated as 'UserSearch'  and the arguments listed above are not accepted. 
          <br>
          <br>
          <i><b>Response</b></i>
          <br>
          <br>
          Identical to that of the <a href="#Search">Search</a> request. UserSearch only returns ADN records. <br>
            <br>
            <br>
            <a name="GetRecord"></a></p>
<h3>GetRecord</h3>
<b>Sample request</b><br>
<br>
The following request displays the metadata for record ID DLESE-000-000-000-001 displayed in it's native XML format:<br>
<br>
<a href="${ddsws11BaseUrl}?verb=GetRecord&id=DLESE-000-000-000-001" target="_blank">${ddsws11BaseUrl}?verb=GetRecord&id=DLESE-000-000-000-001</a><br>
<br>
<b>Summary and usage</b><br>
<br>
The GetRecord request is used to pull up the metadata for a single item in the repository. Clients should use this request to display the metadata from a single record, for example if the user has requested "more information" about a resource. The data is returned in <a href="http://www.dlese.org/Metadata/adn-item/index.htm">ADN format</a> and other formats including <span class="code">dlese_collect, dlese_anno, oai_dc, nsdl_dc</span> and <span class="code">briefmeta</span>. <a href="http://www.dlese.org/Metadata/adn-item/0.6.50/samples/index.htm">Sample ADN records are available here</a>.<br>
<br>
<b>Arguments</b><br>
<br>
<ul>
<li><span class="code">id</span> - a <i>required</i> argument that specifies the  identifier for the record.
</ul>
<br>
<ul>
<li><span class="code">xmlFormat</span> - an <i>optional</i> argument that indicates the format the record must be returned in. If specified, responses are limited to only those records that are available in the given format. If not specified, the record will be returned in it's native format using a localized version of XML (e.g. stripped of it's namespace and schema declaration). The available formats may be discovered using the <a href="#ListXmlFormats">ListXmlFormats</a> request.
</ul>
<br>
<ul>
<li><span class="code">so</span> - (search over) an <i>optional</i> argument that must contain the value allRecords or discoverableRecords. Users who request to search over allRecords must be authorized by IP, otherwise an error is returned. Defaults to discoverableRecords.
</ul>
<br>
<b>Errors and exceptions</b><br>
<br>
See <a href="#errorCodes">error and exception conditions</a>.<br>
<br>
<b>Examples</b><br>
<br>
<i><b>Request</b></i><br>
<br>
Request the  record id DLESE-000-000-000-337 and get the response in ADN format. Shown without the required <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#SpecialCharacters">encoding</a>, for clarity.<br>
<br>
<pre>${ddsws11BaseUrl}?
        verb=GetRecord&amp;id=DLESE-000-000-000-337</pre><br>
<i><b>Response</b></i><br>
<br>
<table border=0 cellspacing=0 cellpadding=0>
<tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 
&lt;DDSWebService&gt;
  &lt;GetRecord&gt;
    &lt;record&gt;
      &lt;head&gt;
        &lt;id&gt;DLESE-000-000-000-337&lt;/id&gt; 
        &lt;collection recordId="DLESE-COLLECTION-000-000-000-015"&gt;
          DLESE Community Collection (DCC)&lt;/collection&gt; 
        &lt;xmlFormat&gt;adn&lt;/xmlFormat&gt; 
        &lt;fileLastModified&gt;2004-06-24T19:06:08Z&lt;/fileLastModified&gt; 
        &lt;whatsNewDate type="itemnew"&gt;2003-07-10&lt;/whatsNewDate&gt; 
        &lt;additionalMetadata realm="adn"&gt;
          &lt;accessionStatus&gt;accessioneddiscoverable&lt;/accessionStatus&gt; 
          &lt;partOfDrc&gt;true&lt;/partOfDrc&gt; 
          &lt;alsoCatalogedBy collectionLabel="NASA ESE Reviewed 
             Collection" 
             collectionRecordId="DLESE-COLLECTION-000-000-000-023"&gt;
                  NASA-ESERevProd333&lt;/alsoCatalogedBy&gt; 
        &lt;/additionalMetadata&gt;
      &lt;/head&gt;
      &lt;metadata&gt;
        &lt;itemRecord&gt;
          &lt;general&gt;
            &lt;title&gt;Earth Science Picture of the Day&lt;/title&gt; 
            ...

&lt;/DDSWebService&gt;
</pre> </td></tr>
</table>
<br>
<br>
<a name="ListFields"></a>
<h3>ListFields</h3>
<b>Sample request</b><br>
<br>
The following request lists all fields in the index:<br>
<br>
<a href="${ddsws11BaseUrl}?verb=ListFields" target="_blank">${ddsws11BaseUrl}?verb=ListFields</a><br>
<br>
<b>Summary and usage</b><br>
<br>
The ListFields request is used to get all search fields that reside in the index. It is not necessary for the Lucene fields to be stored.<br>
<br>
<b>Arguments</b><br> 
<br>
None
<p><br>
  <b>Errors and exceptions</b><br>
  <br>
See <a href="#errorCodes">error and exception conditions</a>.<br>
<br>
<b>Examples</b><br>
<br>
See link above

<br>
<br>
<a name="ListTerms"></a>
<h3>ListTerms</h3>
<b>Sample request</b><br>
<br>
The following request lists all terms in the index for field 'title':<br>
<br>
<a href="${ddsws11BaseUrl}?field=title&verb=ListTerms" target="_blank">${ddsws11BaseUrl}?field=title&verb=ListTerms</a><br>
<br>
<b>Summary and usage</b><br>
<br>
The ListTerms request is used to get all search terms that exist in the index for a given field or fields. It is not necessary for the Lucene fields to be stored. For each term the response indicates the number of times it appears in the index (termCount) as well as the number of documents (records) it appears in (docCount). <br>
<br>
<b>Arguments</b><br>
<br>
<ul>
<li><span class="code">field</span> - a <i>required repeatable</i> argument that contains the name of a field. The field argument may be repeated as many times as desired within a single request. Note that response times will increase dramatically when more than one field is requested. 
</ul>
<p><br>
  <b>Errors and exceptions</b><br>
  <br>
See <a href="#errorCodes">error and exception conditions</a>.<br>
<br>
<b>Examples</b><br>
<br>
See link above
<br>
<br>
<a name="ListCollections"></a>
<h3>ListCollections</h3>
<b>Sample request</b><br>
<br>
The following request lists the metadata collections that are available in the repository:<br>
<br>
<a href="${ddsws11BaseUrl}?verb=ListCollections" target="_blank">${ddsws11BaseUrl}?verb=ListCollections</a><br>
<br>
<b>Summary and usage</b><br>
<br>
The ListCollections request is used to discover the available metadata collections in the repository and to retrieve the search field/key values used to perform searches across collections. Clients should use this request to generate user interface widgets for selecting collections to search from, or to display collection information such as the number of records in a collection. This request belongs to the <a href="#VocabList">vocabulary list class of requests</a>.<br>
<br>
The response from ListCollections conforms to the vocabulary list response format but includes two additional elements: &lt;recordId&gt; and &lt;<a href="#additionalMetadata">additionalMetadata</a>&gt;<br>
<br>
<b>Examples</b><br>
<br>
Refer to the documentation for the <a href="#VocabList">vocabulary list class of requests</a>.<br>
<br>
<br>
<a name="ListGradeRanges"></a>
<h3>ListGradeRanges</h3>
<b>Sample request</b><br>
<br>
The following request lists the DLESE-specific grade range vocabularies and corresponding search keys:<br>
<br>
<a href="${ddsws11BaseUrl}?verb=ListGradeRanges" target="_blank">${ddsws11BaseUrl}?verb=ListGradeRanges</a><br>
<br>
<b>Summary and usage</b><br>
<br>
The ListGradeRanges request is used to discover the DLESE controlled vocabularies and search field/keys for grade ranges used in the <code>adn</code> and <code>dlese_collect</code> metadata frameworks. Clients that work with these DLESE frameworks may use this request to generate user interface widgets for selecting grade ranges to search from. This request belongs to the <a href="#VocabList">vocabulary list class of requests</a>.<br>
<br>
<b>Examples</b><br>
<br>
Refer to the documentation for the <a href="#VocabList">vocabulary list class of requests</a>.<br>
<br>
<br>
<a name="ListSubjects"></a>
<h3>ListSubjects</h3>
<b>Sample request</b><br>
<br>
The following request lists the DLESE-specific subject vocabularies and corresponding search keys:<br>
<br>
<a href="${ddsws11BaseUrl}?verb=ListSubjects" target="_blank">${ddsws11BaseUrl}?verb=ListSubjects</a><br>
<br>
<b>Summary and usage</b><br>
<br>
The ListSubjects request is used to discover the DLESE controlled vocabularies and search field/keys for subjects used in the <code>adn</code> and <code>dlese_collect</code> metadata frameworks. Clients that work with these DLESE frameworks may use this request to generate user interface widgets for selecting the subjects to search from. This request belongs to the <a href="#VocabList">vocabulary list class of requests</a>.<br>
<br>
<b>Examples</b><br>
<br>
Refer to the documentation for the <a href="#VocabList">vocabulary list class of requests</a>.<br>
<br>
<br>
<a name="ListResourceTypes"></a>
<h3>ListResourceTypes</h3>
<b>Sample request</b><br>
<br>
The following request lists the DLESE-specific resource type vocabularies and corresponding search keys:<br>
<br>
<a href="${ddsws11BaseUrl}?verb=ListResourceTypes" target="_blank">${ddsws11BaseUrl}?verb=ListResourceTypes</a><br>
<br>
<b>Summary and usage</b><br>
<br>
The ListResourceTypes request is used to discover the DLESE controlled vocabularies and search field/keys for resource types used in the <code>adn</code> and <code>dlese_collect</code> metadata frameworks. Clients that work with these DLESE frameworks may use this request to generate user interface widgets for selecting the resource types to search from. This request belongs to the <a href="#VocabList">vocabulary list class of requests</a>.<br>
<br>
<b>Examples</b><br>
<br>
Refer to the documentation for the <a href="#VocabList">vocabulary list class of requests</a>.<br>
<br>
<br>
<a name="ListContentStandards"></a>
<h3>ListContentStandards</h3>
<b>Sample request</b><br>
<br>
The following request lists the DLESE-specific content standard vocabularies and corresponding search keys:<br>
<br>
<a href="${ddsws11BaseUrl}?verb=ListContentStandards" target="_blank">${ddsws11BaseUrl}?verb=ListContentStandards</a><br>
<br>
<b>Summary and usage</b><br>
<br>
The ListContentStandards request is used to discover the DLESE controlled vocabularies and search field/keys for content standards used in the <code>adn</code> and <code>dlese_collect</code> metadata frameworks. Clients that work with these DLESE frameworks may use this request to generate user interface widgets for selecting the content standards to search from. This request belongs to the <a href="#VocabList">vocabulary list class of requests</a>.<br>
<br>
<b>Examples</b><br>
<br>
Refer to the documentation for the <a href="#VocabList">vocabulary list class of requests</a>.<br>
<br>
<br>
<a name="VocabList"></a>
<h3>Vocabulary list requests</h3>
<b>Summary and usage</b><br>
<br>
Vocabulary list requests include <a href="#ListGradeRanges">ListGradeRanges</a>, <a href="#ListSubjects">ListSubjects</a>, <a href="#ListResourceTypes">ListResourceTypes</a>, <a href="#ListContentStandards">ListContentStandards</a>, and <a href="#ListCollections">ListCollections</a>*. Each of the vocabulary list requests use the same request and response format.<br>
<br>
Vocabulary list requests are used to determine the search values supplied in the gr, su, re, cs and ky arguments of the <a href="#Search">Search</a> and <a href="#UserSearch">UserSearch</a> requests and should be used to construct user interface menus for selecting the grade ranges, subjects, etc. for users to limit their search by.<br>
<br>
More specifically, vocabulary list requests represent the class of requests that expose controlled vocabularies in the repository (grade ranges, subjects, resource types, content standards and collections). Vocabulary list requests may be used to discover the vocabulary entries ('Primary elementary'. etc.), the search field/key pair used to perform and limit searches across the given vocabulary in the <a href="#Search">Search</a> and <a href="#UserSearch">UserSearch</a> requests ('gr=07', etc.), and a set of rendering guidelines used to determine things such as whether to display the vocabulary listing to the user and the label that should displayed, for example 'Primary (K-2)'.<br>
<br>
Implementation tip: Library vocabularies change very infrequently (on the order of years or months). Clients should retrieve the vocabulary values once and cache them, for example at application start up, rather than retrieving them each time a user accesses the client.<br>
<br>
*Note: ListCollections conforms to the vocabulary list response but includes two additional elements: &lt;recordId&gt; and &lt;<a href="#additionalMetadata">additionalMetadata</a>&gt;<br>
<br>
<b>Arguments</b><br>
<br>
None.<br>
<br>
<b>Errors and exceptions</b><br>
<br>
 See <a href="#errorCodes">error and exception conditions</a>.<br>
<br>
<b>Examples</b><br>
<br>
<i><b>Request</b></i><br>
<br>
Request the grade ranges that are available. Note the verb argument may contain any of the vocabulary list requests (<a href="#ListGradeRanges">ListGradeRanges</a>, <a href="#ListSubjects">ListSubjects</a>, <a href="#ListResourceTypes">ListResourceTypes</a>, <a href="#ListContentStandards">ListContentStandards</a>, or <a href="#ListCollections">ListCollections</a>) corresponding to the vocabulary you are interested in. <br>
<br>
<pre>${ddsws11BaseUrl}?verb=ListGradeRanges</pre><br>
<i><b>Response</b></i><br>
<br>
<table border=0 cellspacing=0 cellpadding=0>
<tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 
&lt;DDSWebService&gt;
  &lt;ListGradeRanges&gt;
    &lt;searchField&gt;gr&lt;/searchField&gt;
    &lt;gradeRanges&gt;
      &lt;gradeRange&gt;
        &lt;vocabEntry&gt;DLESE:Primary elementary&lt;/vocabEntry&gt;
        &lt;searchKey&gt;07&lt;/searchKey&gt;
        &lt;renderingGuidelines&gt;
          &lt;label&gt;Primary (K-2)&lt;/label&gt;
          &lt;noDisplay&gt;false&lt;/noDisplay&gt;
          &lt;wrap&gt;false&lt;/wrap&gt;
          &lt;divider&gt;false&lt;/divider&gt;
          &lt;hasSubList&gt;false&lt;/hasSubList&gt;
          &lt;isLastInSubList&gt;false&lt;/isLastInSubList&gt;
          &lt;groupLevel&gt;0&lt;/groupLevel&gt;
        &lt;/renderingGuidelines&gt;
      &lt;/gradeRange&gt;
      &lt;gradeRange&gt;
        &lt;vocabEntry&gt;DLESE:Intermediate elementary&lt;/vocabEntry&gt;
        ...

&lt;/DDSWebService
</pre> </td></tr>
</table>
<br>
*Note: ListCollections conforms to the vocabulary list response format shown above but includes two additional elements: &lt;recordId&gt; and &lt;<a href="#additionalMetadata">additionalMetadata</a>&gt;<br>
<br>
<br>
<a name="ListXmlFormats"></a>
<h3>ListXmlFormats</h3>
<b>Sample request</b><br>
<br>
The following request lists the XML formats that may be disseminated from this service and their corresponding search keys:<br>
<br>
<a href="${ddsws11BaseUrl}?verb=ListXmlFormats" target="_blank">${ddsws11BaseUrl}?verb=ListXmlFormats</a><br>
<br>
<b>Summary and usage</b><br>
<br>
The ListXmlFormats request is used to discover the XML formats available from the repository as a whole or for a single record in the repository. Clients should use this request to discover the available XML formats and the keys that may be supplied in the 'xmlFormat' argument of the <a href="#Search">Search</a> or <a href="#GetRecord">GetRecord</a> requests.<br>
<br>
DDSWS is able to disseminate a number of XML formats including <a href="http://www.dlese.org/Metadata/adn-item/index.htm">ADN</a> (<span class="code">adn</span>), <a href="http://www.dlese.org/Metadata/news-opps/index.htm">News&amp;Opps</a> (<span class="code">news_opps</span>), <a href="http://www.dlese.org/Metadata/annotation/index.htm">DLESE annotation</a> (<span class="code">dlese_anno</span>), <a href="http://www.dlese.org/Metadata/collection/index.htm">DLESE collection</a> (<span class="code">dlese_collect</span>), <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#dublincore">OAI Dublin Core</a> (<span class="code">oai_dc</span>), NSDL Dublin Core (<span class="code">nsdl_dc</span>), and others.<br>
<br>
Certain records are available in multiple formats. For example, records that were originally cataloged in the ADN format may be returned in the adn, adn-localized, briefmeta, oai_dc, nsdl_dc, format. When a record is requested in a non-native format, it's XML is transformed to the requested format using XSLT or other transformation prior to being returned by the service.<br>
<br>
Many XML formats are available in namespace-specific form or a localized form that contains no namespace or schema declaration. Localized XML is indicated by adding -localized to the end of the XML format specifier, for example adn-localized. When localized XML is returned, the XML is generally easier to read and XPath notation is greatly simplified. By default, all requests in the service return localized versions of the metadata unless a non-localized specifier is indicated.<br>
<br>
<b>Arguments</b><br>
<br>
<ul>
<li><span class="code">id</span> - an <i>optional</i> argument that specifies an ID in the repository. If supplied the request will show only those XML formats that are available for that ID. If omitted, the response will indicate all XML formats that are available in the repository.
</ul>
<br>
<b>Errors and exceptions</b><br>
<br>
See <a href="#errorCodes">error and exception conditions</a>.<br>
<br>
<b>Examples</b><br>
<br>
<i><b>Request</b></i><br>
<br>
Show all XML formats available for ID DLESE-000-000-000-001.<br>
<br>
<pre>${ddsws11BaseUrl}?
             verb=ListXmlFormats&amp;id=DLESE-000-000-000-001</pre><br>
<i><b>Response</b></i><br>
<br>
<table border=0 cellspacing=0 cellpadding=0>
<tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 
&lt;DDSWebService&gt;
  &lt;ListXmlFormats&gt;
    &lt;xmlFormat&gt;adn&lt;/xmlFormat&gt;
    &lt;xmlFormat&gt;adn-localized&lt;/xmlFormat&gt;
    &lt;xmlFormat&gt;briefmeta&lt;/xmlFormat&gt;
    &lt;xmlFormat&gt;nsdl_dc&lt;/xmlFormat&gt;
    &lt;xmlFormat&gt;oai_dc&lt;/xmlFormat&gt;
  &lt;/ListXmlFormats&gt;
&lt;/DDSWebService&gt;
</pre> </td></tr>
</table>
<br>
<br>
<a name="UrlCheck"></a>
<h3>UrlCheck</h3>
<b>Sample request</b><br>
<br>
The following request searches for all records in the repository that have a URL ending in '.pdf':<br>
<br>
<a href="${ddsws11BaseUrl}?url=http://*.pdf&verb=UrlCheck" target="_blank">${ddsws11BaseUrl}?url=http://.pdf&verb=UrlCheck</a><br>
<br>
<b>Summary and usage</b><br>
<br>
The UrlCheck request is used to check whether a given URL is in the DDS repository. This request supports the use of the * wildcard construct. The * character, or wildcard construct, indicates that <i>any</i> character combination is a valid match. For example, a search for http://www.dlese.org/myResource* will match the two URLs http://www.dlese.org/myResource1.html and http://www.dlese.org/myResource2.html. The wildcard construct may appear at any position in the URL argument <i>except</i> the first position. <br>
<br>
<b>Arguments</b><br>
<br>
<ul>
<li><span class="code">url</span> - a <i>required repeatable</i> argument that contains a URL. The url argument may be repeated as many times as desired within a single request.
</ul>
<br>
<b>Errors and exceptions</b><br>
<br>
See <a href="#errorCodes">error and exception conditions</a>.<br>
<br>
<b>Examples</b><br>
<br>
<i><b>Request</b></i><br>
<br>
Determine whether the URL 'http://epod.usra.edu/' is in the repository. Shown without the required <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#SpecialCharacters">encoding</a>, for clarity.<br>
<br>
<pre>${ddsws11BaseUrl}?
    verb=UrlCheck&amp;url=http://epod.usra.edu/</pre><br>
<i><b>Response</b></i><br>
<br>
<table border=0 cellspacing=0 cellpadding=0>
<tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 
&lt;DDSWebService&gt;
  &lt;UrlCheck&gt;
    &lt;resultInfo&gt;
      &lt;totalNumResults&gt;1&lt;/totalNumResults&gt; 
    &lt;/resultInfo&gt;
    &lt;results&gt;
      &lt;matchingRecord&gt;
        &lt;url&gt;http://epod.usra.edu/&lt;/url&gt; 
        &lt;head&gt;
          &lt;id&gt;DLESE-000-000-000-337&lt;/id&gt; 
          &lt;collection recordId="DLESE-COLLECTION-000-000-000-015"&gt;
            DLESE Community Collection (DCC)&lt;/collection&gt; 
          &lt;xmlFormat&gt;adn&lt;/xmlFormat&gt; 
          &lt;fileLastModified&gt;2004-06-24T19:06:08Z&lt;/fileLastModified&gt; 
          &lt;whatsNewDate type="itemnew"&gt;2003-07-10&lt;/whatsNewDate&gt; 
          &lt;additionalMetadata realm="adn"&gt;
            &lt;accessionStatus&gt;accessioneddiscoverable&lt;/accessionStatus&gt; 
            &lt;partOfDrc&gt;true&lt;/partOfDrc&gt; 
            &lt;alsoCatalogedBy collectionLabel="NASA ESE 
                 Reviewed Collection" 
              collectionRecordId="DLESE-COLLECTION-000-000-000-023"&gt;
                 NASA-ESERevProd333&lt;/alsoCatalogedBy&gt; 
          &lt;/additionalMetadata&gt;
        &lt;/head&gt;
      &lt;/matchingRecord&gt;
    &lt;/results&gt;
  &lt;/UrlCheck&gt;
&lt;/DDSWebService&gt;
</pre> </td></tr>
</table>
Note: responses to this request contain the common <a href="#head">head</a> element.<br>
<br>
<i><b>Request</b></i><br>
<br>
Determine whether the URL 'http://epod.usra.edu/' or 'http://www.marsquestonline.org/index.html' is in the repository. <br>
<br>
<pre>${ddsws11BaseUrl}?
   verb=UrlCheck&amp;url=http://epod.usra.edu/&amp;
   url=http://www.marsquestonline.org/index.html</pre><br>
<i><b>Response</b></i><br>
<br>
<table border=0 cellspacing=0 cellpadding=0>
<tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 
&lt;DDSWebService&gt;
  &lt;UrlCheck&gt;
    &lt;resultInfo&gt;
      &lt;totalNumResults&gt;2&lt;/totalNumResults&gt; 
    &lt;/resultInfo&gt;
    &lt;results&gt;
      &lt;matchingRecord&gt;
        &lt;url&gt;http://www.marsquestonline.org/index.html&lt;/url&gt; 
        ....
      &lt;/matchingRecord&gt;
      &lt;matchingRecord&gt;
        &lt;url&gt;http://epod.usra.edu/&lt;/url&gt; 
        ...
      &lt;/matchingRecord&gt;
    &lt;/results&gt;
  &lt;/UrlCheck&gt;
&lt;/DDSWebService&gt;
</pre> </td></tr>
</table>
<br>
<i><b>Request</b></i><br>
<br>
Determine whether a URL that <i>begins with</i> 'http://www.dlese.org' is in the repository. The * character acts as a wildcard, which may appear at any position in the URL argument <i>except</i> the first position. <br>
<br>
<pre>${ddsws11BaseUrl}?
         verb=UrlCheck&amp;url=http://www.dlese.org* </pre><br>
<i><b>Response</b></i><br>
<br>
<table border=0 cellspacing=0 cellpadding=0>
<tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 
&lt;DDSWebService&gt;
  &lt;UrlCheck&gt;
    &lt;resultInfo&gt;
      &lt;totalNumResults&gt;2&lt;/totalNumResults&gt; 
    &lt;/resultInfo&gt;
    &lt;results&gt;
      &lt;matchingRecord&gt;
        &lt;url&gt;http://www.dlese.org/vgee/index.htm&lt;/url&gt; 
        ...
      &lt;/matchingRecord&gt;
      &lt;matchingRecord&gt;
        &lt;url&gt;
   http://www.dlese.org/documents/policy/CollectionsScope_final.html
        &lt;/url&gt; 
        ...
      &lt;/matchingRecord&gt;
    &lt;/results&gt;
  &lt;/UrlCheck&gt;
 &lt;/DDSWebService&gt;
</pre> </td></tr>
</table>
<br>
<i><b>Request</b></i><br>
<br>
Determine whether the URL 'http://epod.usra.edu/zzzz' is in the repository. In this case no matching records are found.<br>
<br>
<pre>${ddsws11BaseUrl}?
        verb=UrlCheck&amp;url=http://epod.usra.edu/zzzz </pre><br>
<i><b>Response</b></i><br>
<br>
<table border=0 cellspacing=0 cellpadding=0>
<tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 
&lt;DDSWebService&gt;
  &lt;UrlCheck&gt;
    &lt;resultInfo&gt;
      &lt;totalNumResults&gt;0&lt;/totalNumResults&gt; 
    &lt;/resultInfo&gt;
  &lt;/UrlCheck&gt;
&lt;/DDSWebService&gt;
</pre> </td></tr>
</table>
<br>
<br>
<a name="ServiceInfo"></a>
<h3>ServiceInfo</h3>
<b>Sample request</b><br>
<br>
The following request displays information about this Web service:<br>
<br>
<a href="${ddsws11BaseUrl}?verb=ServiceInfo" target="_blank">${ddsws11BaseUrl}?verb=ServiceInfo</a><br>
<br>
<b>Summary and usage</b><br>
<br>
The ServiceInfo request is used to retrieve general information about this Web service including name, description, the URL used to access the service (base URL), service version, the maximum 
number of search results allows by the Search and UserSearch requests, and an administrator e-mail. <br>
<br>
<b>Arguments</b><br>
<br>
None<br>
<br>
<b>Errors and exceptions</b><br>
<br>
 See <a href="#errorCodes">error and exception conditions</a>.<br>
<br>
<b>Examples</b><br>
<br>
<i><b>Request</b></i><br>
<br>
Display information about the Web service <br>
<br>
<pre>${ddsws11BaseUrl}?verb=ServiceInfo</pre><br>
<i><b>Response</b></i><br>
<br>
<table border=0 cellspacing=0 cellpadding=0>
<tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 
&lt;DDSWebService&gt;
  &lt;ServiceInfo&gt;
    &lt;serviceName&gt;
      Digital Library for Earth System Education (DLESE) 
      discovery Web service
    &lt;/serviceName&gt;
    &lt;baseURL&gt;${serverContextUrl}/services/ddsws1-1&lt;/baseURL&gt;
    &lt;serviceVersion&gt;1.1&lt;/serviceVersion&gt;
    &lt;adminEmail&gt;${adminEmailDDSWS}&lt;/adminEmail&gt;
    &lt;compression&gt;gzip&lt;/compression&gt;
    &lt;maxSearchResultsAllowed&gt;${maxNumResultsDDSWS}&lt;/maxSearchResultsAllowed&gt;
    &lt;description&gt; ... description here ... &lt;/description&gt;
  &lt;/ServiceInfo&gt;
&lt;/DDSWebService&gt;
</pre> </td></tr>
</table>
<br>
<br>

<a name="responses"></a>
<h2>Service responses</h2>
Service responses are returned in XML or JSON format and vary in structure and content depending on the request made.
This section describes common response structures that are returned by the service.
The content and structure of each of the request responses are described above, not here.
<br>
<br>
<a name="CommonElements"></a>
<h3>Common response elements</h3>
Several requests in the protocol share common XML elements in their responses. These include the &lt;head&gt; and &lt;additionalMetadata&gt; elements, which are described below.<br>
<br>
<a name="head"></a>
<i><b>The head element</b></i><br>
<br>
The head element appears in the <a href="#UserSearch">UserSearch</a>, <a href="#Search">Search</a>, <a href="#GetRecord">GetRecord</a>, <a href="#UrlCheck">UrlCheck</a> responses. The head element is used to return information about a single record. This includes the ID of the record, the collection in which the record is a member of, the XML format of the record, the date the record was last modified, the <a href="#whatsNewDate">whatsNewDate</a> and an <a href="#additionalMetadata">additionalMetadata</a> element. <br>
<br>
Head element example:<br>
<table border=0 cellspacing=0 cellpadding=0>
<tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 
...
&lt;head&gt;
   &lt;id&gt;CEIS-000-000-001&lt;/id&gt;
   &lt;collection recordId="DLESE-COLLECTION-000-000-000-003"&gt;
          Discover Our Earth&lt;/collection&gt;
   &lt;xmlFormat&gt;adn&lt;/xmlFormat&gt;
   &lt;fileLastModified&gt;2004-07-02T17:32:29Z&lt;/fileLastModified&gt;
   &lt;whatsNewDate type="itemnew"&gt;2003-07-19&lt;/whatsNewDate&gt;
	&lt;additionalMetadata realm="adn"&gt;
         ...
        &lt;/additionalMetadata&gt;
&lt;/head&gt;
...
</pre> </td></tr>
</table>
<br>
<br>
<a name="additionalMetadata"></a>
<i><b>The additionalMetadata element</b></i><br>
<br>
The additionalMetadata element appears in <a href="#UserSearch">UserSearch</a>, <a href="#Search">Search</a>, <a href="#GetRecord">GetRecord</a>, <a href="#UrlCheck">UrlCheck</a> and the <a href="#VocabList">vocabulary list</a> class of responses. The additionalMetadata element is used to return additional information related to the record's format type, referred to as <i>realms</i>. The information realms include adn and dlese_collect, and each contains slightly different information related to underlying format type.<br>
<br>
additionalMetadata element example:<br>
<table border=0 cellspacing=0 cellpadding=0>
<tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 
...
&lt;additionalMetadata realm="adn"&gt;
   &lt;accessionStatus&gt;accessioneddiscoverable&lt;/accessionStatus&gt;
   &lt;partOfDrc&gt;false&lt;/partOfDrc&gt;
   &lt;alsoCatalogedBy collectionLabel="DLESE Community Collection (DCC)"
        collectionRecordId="DLESE-COLLECTION-000-000-000-015"&gt;
           DLESE-000-000-000-840&lt;/alsoCatalogedBy&gt;
   &lt;alsoCatalogedBy collectionLabel="Cutting Edge" 
        collectionRecordId="DLESE-COLLECTION-000-000-000-010"&gt;
           SERC-NAGT-000-000-000-322&lt;/alsoCatalogedBy&gt;
&lt;/additionalMetadata&gt;
...
</pre> </td></tr>
</table>

<br>
  <a name="errorCodes"></a>
  
<h3>Error and exception conditions</h3>
If an error or exception occurs, the service returns an &lt;error&gt; element with the type of error indicated by a code attribute.
Clients are advised to test the value of these codes and respond with an appropriate message to users. For example, if a user conducts a search that has no matches, the code <CODE>noRecordsMatch</CODE> will be returned from the server and a message indicating that the search had no results can be displayed. The error codes are similar to those defined by OAI-PMH. <br>
<br>
<table border=1 cellspacing=0 cellpadding=0>
  <TBODY>
    <TR class="thHeadRow">
      <TH align=middle>Error Codes</TH>
      <TH align=middle>Description</TH>
      <TH align=middle>Applicable Verbs</TH>
    </TR>
    <TR>
      <TD><CODE>noRecordsMatch</CODE></TD>
      <TD>The combination of  values supplied in the <CODE>Search</CODE> or <CODE>UserSearch</CODE> request  resulted in a query that had no matching records. </TD>
      <TD 
align=middle><CODE>Search</CODE><BR>
          <CODE>UserSearch</CODE></TD>
    </TR>
    <TR>
      <TD><CODE>badQuery</CODE></TD>
      <TD>The value supplied in the <CODE>q</CODE> argument of the <CODE>Search</CODE> or <CODE>UserSearch</CODE> request was  malformed or syntactically incorrect. </TD>
      <TD align=middle><CODE>Search</CODE><BR>
        <CODE>UserSearch</CODE></TD>
    </TR>	
    <TR>
      <TD><CODE>badArgument</CODE></TD>
      <TD>The request includes illegal arguments, is missing required arguments, includes a repeated argument, or values for arguments have an illegal syntax.</TD>
      <TD align=middle><I>all verbs</I></TD>
    </TR>
    <TR>
      <TD><CODE>badVerb</CODE></TD>
      <TD>Value of the <CODE>verb</CODE> argument is not a legal or the verb argument is missing. </TD>
      <TD align=middle><I>N/A</I></TD>
    </TR>
    <TR>
      <TD><CODE>cannotDisseminateFormat</CODE></TD>
      <TD>The metadata format identified by the value given for the <CODE>xmlFormat</CODE> argument is not supported by the item or by the repository.</TD>
      <TD 
align=middle><CODE>GetRecord</CODE></TD>
    </TR>
    <TR>
      <TD><CODE>idDoesNotExist</CODE></TD>
      <TD>The value of the <CODE>id</CODE> argument is unknown or illegal in the repository.</TD>
      <TD 
align=middle><CODE>GetRecord</CODE></TD>
    </TR>
    <TR>
      <TD><CODE>notAuthorized</CODE></TD>
      <TD>The client that made the request is not authorized to access the requested data from the service.</TD>
      <TD 
align=middle><I>all verbs</I></TD>
    </TR>
    <TR>
      <TD><CODE>internalServerError</CODE></TD>
      <TD>The server for the service encountered a problem and was not able to respond to the request.</TD>
      <TD 
align=middle><I>all verbs</I></TD>
    </TR>	
  </TBODY>
</table>

<br>
<b>Example error response</b><br>
<br>

<br>
<i><b>Request</b></i><br>
<br>
Request a  record id that does not exist in the repository using GetRecord.<br>
<br>
<pre>${ddsws11BaseUrl}?
          verb=GetRecord&amp;id=BAD-ID-123</pre>
<br>
<i><b>Response</b></i><br>
<br>
<table border=0 cellspacing=0 cellpadding=0>
<tr><td><pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 
&lt;DDSWebService 
    xmlns=&quot;http://www.dlese.org/Metadata/ddsws&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; 
    xsi:schemaLocation=&quot;http://www.dlese.org/Metadata/ddsws 
	http://www.dlese.org/Metadata/ddsws/1-1/ddsws.xsd&quot;&gt;
	&lt;error code=&quot;idDoesNotExist&quot;&gt;ID &quot;BAD-ID-123&quot; does not exist in the repository&lt;/errror&gt;<br>&lt;/DDSWebService&gt;</pre> </td>
</tr>
</table>
<br>      <br>
      <a name="json"></a>
<h3>Requesting JSON output</h3>
Each of the service responses can  be returned as JSON (JavaScript Object Notation) as an alternate output format to XML. JSON is a simple data format based on the object notation of the JavaScript language and is commonly used in Ajax-style programming to bring content into Web pages asynchronously. For more information about  JSON and how it is used, see Douglas Crockford's site <a href="http://www.json.org/">www.json.org</a> and the <a href="http://developer.yahoo.com/common/json.html">Yahoo! JSON developers page</a>. A DDS client that illustrates it's use is shown in <a href="../examples/ddsws1-1/index.jsp">these examples</a>.<br/>
<br/>
By default, all responses are output in XML format. To get JSON output, include the argument <span class="code">output=json</span>   in the request. Additionally, a callback argument <span class="code">callback=<em>function</em></span> may be included to wrap the JSON output in parentheses and a function name of your choosing. 
The JSON output by the service is a direct translation of the XML structure into JSON.
<br/>
<br/>
<b>Arguments</b>
<br/><br/>

<ul>
  <li><span class="code">output=json</span> - An <i>optional</i> argument that, when used, instructs the service to return JSON output instead of XML. 
</ul>
<br>
<ul>
  <li><span class="code">callback=<em>function</em></span> - An <em>optional</em> argument that, when used in conjunction with the output=json argument, instructs the service to return the JSON output wrapped in parentheses and a function name of your choosing, as indicated by the argument value. 
</ul>

<br>

<br>
<br>
<a name="localize"></a>
<h3>Removing namespaces from output</h3>
Namespaces can be removed from the XML and JSON output from the service, which can simplify working with and processing the output. 
<br/><br/>
By default, all responses are returned with the namespaces that appear in the requested format disseminated from the repository. To remove namespaces, include the argument transform=localize in the request.
<br/><br/>
<b>Arguments</b>
<br/><br/>

<ul>
  <li><span class="code">transform=localize</span> - An <i>optional</i> argument that, when used, instructs the service to return the XML or JSON output without namespaces. 
</ul>

<p> <br>
<br>
<br>
<a name="availableSearchFields"></a>
<h2> Search fields</h2>
<p>This section describes the search fields that are available in the DDSWS <a href="#Search">Search</a> and <a href="#UserSearch">UserSearch</a> requests. 
    The repository  contains  fields that are extracted from each of the XML records within, and a given repository may contain records in many different native XML formats. Searches within a given field operate over the set of records that contain that field. For example, a search in the <code>default</code> field operates over all records in the repository, since all records are guaranteed to contain the <code>default</code> field, whereas a search in the <code>title</code> field operates over a potentially smaller sub-set of  records that contain the <code>title</code> field.   Boolean searches may be performed across and within each of the fields using the <a href="http://lucene.apache.org/java/2_4_0/queryparsersyntax.html">Lucene Query Syntax</a> (LQS) supplied in the <code>q</code> argument of the <a href="#Search">Search</a> or <a href="#UserSearch">UserSearch</a> request. <a href="#Example_search_queries">Example search queries</a> are provided below. </p>
<p>Fields may contain plain text, controlled vocabularies or encoded field values.<br>
    <br>
    Certain fields may be used to sort the search results when used in the <code>sortAscendingBy</code> or <code>sortDescendingBy</code> arguments of the <a href="#Search">Search</a> request. Sortable fields are indicated below. </p>
<p><strong>How search fields are generated </strong></p>
<p>At index creation time,  each record is inserted in the repository in it's native XML format. The indexer extracts standard, XPath and custom search fields from the content of the native XML and additional  fields associated with the item may also be extracted from other sources, such as text derived from a crawl of the resource described by the metadata record. The indexer then generates a single entry containing each of the fields and inserts it into the repository. All records are guaranteed to contain certain fields such as the <code>default</code> and <code>stems</code> fields, as well as XPath fields for their native XML format.  Details about the standard, XPath and custom fields are provided below. </p>
<p><strong>Searching across and within specific XML formats</strong></p>
<p>The Search request operates over and disseminates records in any available XML format. By default, searches operate over the available fields for all records in the repository regardless of format, and results may contain records of mixed XML formats. For example, a search for <em>default:ocean</em> searches the for the term ocean in the default field across all records in the repository and may return records in <code>oai_dc</code>, <code>adn, </code><code>dlese_anno</code> and other formats in a single result set depending on what matches are found. </p>
<p><em>Requesting search results in a specific XML format:</em> Certain XML formats can be disseminated from the service in multiple formats, for example records that reside natively as <code>adn</code>  can also be disseminated in the <code>oai_dc</code> format. The Search request  accepts an optional <code>xmlFormat</code> argument, which instructs the service to search over and return only those records that can be disseminated in the given format. In this case, the search still operates over the fields associated with the record's <em>native</em> XML format, however the results will be returned in the requested XML format only, and records that reside in a different native format will be transformed and returned in the requested XML format. </p>
<p><em>Limiting search to  specific XML formats:</em> Each record contains the special field <code>xmlFormat</code>, which contains the format key associated with the native format for the record. To search over and return records that reside in specific native XML formats, include this field in the  query for the Search request. For example, the query <em>xmlFormat:oai_dc ocean</em> will search for and return all records in the repository that reside in the native <code>oai_dc</code> format and that contain the term <em>ocean</em> in the <code>default</code> field.</p>
<p>The xml format keys that may be used in the <code>xmlFormat</code> argument or <code>xmlFormat</code> search field in the Search request may be discovered using the <a href="#ListXmlFormats">ListXmlFormats</a> request. </p>
<p><strong>Text versus stemmed text </strong></p>
<p>When searching in a text field, exact terms are matched. For example a search for <i>ocean</i> will return all records that contain the exact term <i>ocean</i> in the given field. Where indicated, certain textual fields have stemming applied to them using the <a href="http://www.tartarus.org/~martin/PorterStemmer/">Porter stemmer algorithm</a> (snowball variation). When searching in a field that has been stemmed, all records containing morphologically similar terms in the given field are matched. For example a search for <i>stems:ocean</i> will return all records that contain the terms <i>ocean, oceans</i> or <i>oceanic</i> in the <i>stems</i> field. Note that when searching in a stemmed field, the client should <em>not</em> apply stemming to the terms it supplies for search. Stemming will be applied automatically by the search engine for these fields and no pre-processing is necessary by the client.</p>
<p><br>
</p>
<a name="searchFieldsStandard"></a>
<h3>Standard Search Fields </h3>
<p>  Standard search fields are available  across all XML formats that support them, which includes oai_dc, nsdl_dc, ncs_collect, adn, dlese_collect, dlese_anno, news_opps, concepts and all other formats that have  them <a href="#configureDDS">configured in a given DDS repository</a>. </p>
<ul>
<li><code>default</code> - The default field represents the text that is most appropriate for searching by humans using natural language, textual queries. For the  <a href="#Search">Search</a> request, this field is searched when no field specifier is indicated in the query (e.g. the query <em>default:ocean</em> and <em>ocean</em> are equivalent). For the  <a href="#UserSearch">UserSearch</a> request, the default field must be explicitly indicated in the query and if no field specifier is indicated, UserSearch performs an expanded search as described above in the <a href="#UserSearch">UserSearch</a> description. <br>
  <br>
  The default field contains text extracted from different locations in the metadata depending on the XML format. May not be sorted. Available for all formats. Contains the following content:
  <ul>
<li>adn: Includes title, description, keyword, resource type, subjects, event names, place names, temporal coverage names, terms found in the primary URL, and creators last name. May be sorted.
<li>dlese_collect: Includes full title, short title, description, subjects, keywords, and terms extracted from the primary URL, scope URL and review process URL.
<li>dlese_anno: Includes title, description and terms extracted from the URL.
<li>news_opps: Includes title, description, keywords, announcements, topics, audience, diversities, location, and sponsors institution.
<li>All other formats: Contains the full content from all Elements and Attributes within the XML. 
</ul>
</ul>
<br>
<ul>
<li><code>admindefault</code> - This field is meant to support users who are responsible for maintaining the repository. It contains the full content from all Elements as well as Attributes within the XML. May not be sorted. Available for all formats. 
</ul>
<br>
<ul>
<li><code>stems</code> - Contains the same content as the default field, however all terms are stemmed. May not be sorted. Available for all formats.
</ul>
<br>
<ul>
<li><code>title</code> - Contains the titles of resources or items, as text. May be sorted. Available for formats: adn, news_opps, ncs_collect and all formats that specify this field for indexing. 
</ul>
<br>
<ul>
<li><code>titlestems</code> - Contains the same content as the title field, however all terms are stemmed. May not be sorted. Available for formats:<code> adn, news_opps, ncs_collect</code> and all formats that specify the <code>title</code> field for indexing.
</ul>
<br>
<ul>
<li><code>url</code> - Contains the URL for the resource encoded as text. Useful search query examples include http*nasa.gov* or http*.edu*. May be sorted. Available for formats: adn, ncs_collect and all formats that specify this field for indexing.
</ul>
<br>
<ul>
<li><code>description</code> - Contains the descriptions of resources or items, as text. May be sorted. Available for formats: <code>adn, dlese_collect, news_opps, ncs_collect</code> and all formats that specify this field for indexing.
</ul>
<br>
<ul>
<li><code>descriptionstems</code> - Contains the same content as the description field, however all terms are stemmed. May not be sorted. Available for formats: <code>adn, dlese_collect, news_opps, ncs_collect</code> and all formats that specify the <code>description</code> field for indexing.
</ul>
<br>
<ul>  
<li><code>xmlFormat</code> - Contains the native XML format key for the record, for example oai_dc or adn, which may be discovered via the <a href="#ListXmlFormats">ListXmlFormats</a> service request. Available for formats: all formats.
</ul>
<br>
<ul>  
<li><code>idvalue</code> - Contains the internal unique identifier for the record, for example MY-ID-001, indexed untokenized as a keyword. Available for formats: all formats.
</ul>
<br>
<ul>  
<li><code>allrecords</code> - Special field that matches all records in the repository by applying <em>allrecords:true</em> to the query. This is useful for constructing certain types of queries, for example <em>allrecords:true NOT ocean</em> returns all records in the repository that do not contain the term ocean in the default field. Single valid value is <code>true</code>. This has the same effect as the Lucene query <code>*:*</code>. Available for formats: all formats.
</ul>
<br>
<ul>  
<li><code>hasBoundingBox</code> - Boolean value that indicates whether the record has a geospatial bounding box footprint available for search. Valid values are either <code>true</code> or <code>false</code>. Available for formats: all formats.
</ul>
<br>

<h3>&nbsp;</h3>
<a name="searchFieldsXPath"></a>
<h3>XPath Search Fields</h3>
<p> XPath search fields provide separate searchable fields for the contents of every element and attribute found in the native XML  of the records.  For each element and attribute there are three forms of search fields: <em>text</em>, <em>stemmed text</em> and untokenized <em>keywords</em>. These provide a powerful, flexible way to search  for specific text or data within and across the records in the repository. </p>
<p>The XPath fields consist of a prefix followed by an XPath that addresses a specific XML element or attribute in the XML record. Prefixes are one of <code>/text/</code>, <code>/stems/</code>, or <code>/key/</code>, which specify to search over <em>text</em>, <em>stemmed text</em> or untokenized <em>keyword</em> forms of the data, respectively. This is followed by a namespace-free, position-free XPath addressing a specific  element or attribute in the XML.</p>
<p>The three types of search fields are processed in the following manner:</p>
<p><code>text</code> - Text is processed using the Lucene <a href="http://lucene.apache.org/java/1_4_3/api/org/apache/lucene/analysis/standard/StandardAnalyzer.html">StandardAnalyzer</a>.<br>
  <code>stems</code> - Text is processed using the Lucene <a href="http://lucene.apache.org/java/2_2_0/api/org/apache/lucene/analysis/snowball/SnowballAnalyzer.html">SnowballAnalyzer</a> for the english language.<br>
  <code>key</code> - Text is processed using the Lucene KeywordAnalyzer, which is case-sensitive and includes the entire element or attribute as a single token. </p>
<p>The XPaths used for the search fields are the most simple form of XPath expression, containing no namespaces or position specifiers. For more information about XPath see <a href="http://www.w3.org/TR/xpath">XPath Language 1.0</a>. The <a href="http://www.zvon.org/xxl/XPathTutorial/General/examples.html">ZVON XPath Tutorial</a> is also useful. Note that this is not an implementation of XQuery but rather a mapping of simple XPaths to searchable Lucene fields.</p>
<p>For example, consider this simple XML instance document:</p>
<pre>&lt;book&gt;
  &lt;author birthDate=&quot;1955-01-25&quot;&gt;
    &lt;firstName&gt;John&lt;/firstName&gt;
    &lt;lastName&gt;Doe&lt;/lastName&gt;
  &lt;/author&gt;
  &lt;identifier&gt;http://books.org/catalog_123&lt;/identifier&gt;
&lt;/book&gt;</pre>

<p>The index will contain the following search fields for this record:</p>
<p><code>/text//book/author/firstName<br>
/stems//book/author/firstName<br>
/key//book/author/firstName</code></p>

<p><code>/text//book/author/lastName<br>
/stems//book/author/lastName<br>
/key//book/author/lastName</code></p>

<p><code>/text//book/author/@birthDate<br>
/stems//book/author/@birthDate<br>
/key//book/author/@birthDate</code></p>

<p><code>/text//book/identifier<br>
/stems//book/identifier<br>
/key//book/identifier</code></p>

<p>As another example, consider the following Dublin Core <code>oai_dc</code> record:</p>
<pre>
&lt;oai_dc:dc xmlns:oai_dc=&quot;http://www.openarchives.org/OAI/2.0/oai_dc/&quot;
    xsi:schemaLocation=&quot;http://www.openarchives.org/OAI/2.0/oai_dc/ 
	http://www.openarchives.org/OAI/2.0/oai_dc.xsd&quot; 
    xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot; 
    xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;&gt;
  &lt;dc:title xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot;&gt;Ocean Science Leadership Awards&lt;/dc:title&gt;
  &lt;dc:description xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot;&gt;This is a description of the 
  Ocean Science Leadership Awards... &lt;/dc:description&gt;
  &lt;dc:subject xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot;&gt;Earth system science&lt;/dc:subject&gt;
  &lt;dc:subject xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot;&gt;Education&lt;/dc:subject&gt;
  &lt;dc:format xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot;&gt;text/html&lt;/dc:format&gt;
  &lt;dc:type xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot;&gt;Text&lt;/dc:type&gt;
  &lt;dc:identifier xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot;&gt;
     http://www.usc.edu/org/cosee-west/quikscience/OceanLeadershipAwards.html
  &lt;/dc:identifier&gt;
&lt;/oai_dc:dc&gt;</pre>  
   
  <p>The following Lucene queries are examples that match specific text and data in this record. As with all fielded Lucene queries, these queries consist of a field name followed by a colon &quot;:&quot; and then followed by the term(s) to search for. Note that XPaths do not contain namespaces or position specifiers:</p>
  <p><em>/stems//dc/title:oceans</em> - Matches the stemmed form of the term <em>ocean</em> found in the title element of the  XML record. </p>
  <p><em>/text//dc/subject:education</em> - Matches the term <em>education</em> found in one of the subject elements of the  XML record. </p>
  <p><em>/key//dc/format:&quot;text/html&quot;</em> - Matches the untokenized keyword term  <em>text/html</em> found in the format element of the  XML record.</p>
  <p>&nbsp;</p>
  <p><strong>Determining which XPaths have been indexed </strong></p>
  <p>In addition to the XPaths fields, a special field named <code>indexedXpaths</code> contains each XPath that has been indexed for a given record, as a keyword. Using this field it is possible to search for all records that have <em>any</em> value assigned for a given XPath. For example, the following query:</p>
  <p><em>indexedXpaths:&quot;/dc/subject&quot;</em> - Matches all records that have <em>any</em> value in the <em>/dc/subject</em> field.</p>
  <p>Conversely, the following query:</p>
  <p><em>allrecords:true !indexedXpaths:&quot;/dc/subject&quot;</em> - Matches all records that have <em>no</em> value in the <em>/dc/subject</em> field.</p>
  <p>&nbsp;</p>
  <a name="searchFieldsCustom"></a>
  <h3>Custom Search Fields</h3>
<p>Custom search fields are available for specific XML formats as indicated below. Additional custom search fields that are not described here may also be available for a given <a href="#configureDDS">DDS repository configuration</a>. </p>
<p><br>
      <b>Text fields</b> - These fields contain plain text or, where indicated, text that has been stemmed using the <a href="http://www.tartarus.org/~martin/PorterStemmer/">Porter stemmer algorithm</a> (snowball variation). <br>
</p>
<ul>
<li><code>keyword</code> - Contains keywords associated with the resource or item, as text. May be sorted. Available for formats: adn, dlese_collect, news_opps.
</ul>
<br>
<ul>
<li><code>creator</code> - Contains the first, middle and last name of each contributor for the resource. May not be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><code>organizationInstName</code> - Contains the name of the contributing institution. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">organizationInstDepartment</span> - Contains the name of the contributing institution's department. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">personInstName</span> - Contains the name of the contributing person's institution. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">personInstDepartment</span> - Contains the name of the contributing person's institutional department. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">emailPrimary</span> - The primary contributor's e-mail. May be sorted. Available for formats: adn. 
</ul>
<br>
<ul>
<li><span class="code">emailOrganization</span> - The contributing organization's e-mail. May be sorted. Available for formats: adn. 
</ul>
<br>
<ul>
<li><span class="code">emailAlt</span>- The alternate contributor's e-mail. May be sorted. Available for formats: adn. 
</ul>
<br>
<ul>
<li><span class="code">placeNames</span> - Place names, for example "colorado," "AZ," "Brazil," as text. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">eventNames</span> - Event names, for example "windstorm," "Destruction of Pompeii," as text. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">temporalCoverageNames</span> - Temporal coverage names, for example "cambrian," "Triassic Period," as text. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">itemAudienceTypicalAgeRange</span> - The typical age range for this resource. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">itemAudienceInstructionalGoal</span> - The instructional goals for this resource. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">newsOppstitle</span> - News & Opportunities title. May be sorted. Available for formats: news_opps.
</ul>
<br>
<ul>
<li><span class="code">newsOppsdescription</span> - News & Opportunities description. May be sorted. Available for formats: news_opps.
</ul>
<br>
<ul>
<li><span class="code">newsOppskeyword</span> - News & Opportunities keywords. May be sorted. Available for formats: news_opps.
</ul>
<br>
<ul>
<li><span class="code">ncsCollectOaiBaseUrl</span> - Contains the NSDL Collection OAI baseURL. Useful search clause examples include http*nasa.gov* or http*.edu*. May be sorted. Available for formats:  ncs_collect.
  <br>
  From xpath: /record/collection/ingest/oai/@baseURL
</ul>
<br>
<b>Textual content</b> - These fields contain the text of the content of the resources themselves, extracted by crawling the first page of the resource. These are available for all ADN resources in the reository whose primary content is in HTML or PDF. <br>
<br>
<ul>
<li><span class="code">itemContent</span> - The full textual content of the resource. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">itemContentTitle</span> - The HTML title element text. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">itemContentHeaders</span> - The HTML header element (H1, H2, etc.) text. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">itemContentType</span> - The HTTP content type header terms that were returned by the Web server that holds the resource, for example "text html", "application pdf". May be sorted. Available for formats: adn.
</ul>
<br>
<b>Textual vocabulary fields</b> - These fields contain DLESE controlled vocabularies that have been indexed as plain text. <br>
<br>
<ul>
<li><span class="code">gradeRange</span> - The DLESE grade range vocabularies verbatim as text, for example "DLESE:Primary elementary." These values may be discovered using the <a href="#ListGradeRange">ListGradeRange</a> request within the vocabEntry element. May be sorted. Available for formats: adn, dlese_collect.
</ul>
<br>
<ul>
<li><span class="code">resourceType</span> - The DLESE resource type vocabularies verbatim as text, for example "DLESE:Learning materials:Classroom activity." These values may be discovered using the <a href="#ListResourceTypes">ListResourceTypes</a> request within the vocabEntry element. May be sorted. Available for formats: adn, dlese_collect.
</ul>
<br>
<ul>
<li><span class="code">subject</span> - The DLESE subject vocabularies verbatim as text, for example "DLESE:Atmospheric science." These values may be discovered using the <a href="#ListSubjects">ListSubjects</a> request within the vocabEntry element. May be sorted. Available for formats: adn, dlese_collect.
</ul>
<br>
<ul>
<li><span class="code">contentStandard</span> - The DLESE content standard vocabularies verbatim as text, for example "NSES:K-4:Unifying Concepts and Processes Standards:Change, constancy, and measurement." These values may be discovered using the <a href="#ListContentStandards">ListContentStandards</a> request within the vocabEntry element. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">itemAudienceBeneficiary</span> - The intended audience beneficiary, for example "GEM:Bilingual students," as text. See "Beneficiaries" under <a href="http://www.dlese.org/Metadata/adn-item/0.6.50/docs/xml-info.htm#vocabs">http://www.dlese.org/Metadata/adn-item/0.6.50/docs/xml-info.htm#vocabs</a>. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">itemAudienceToolFor</span> - The intended audience for this tool, for example "GEM:Middle school teachers," as text. See "Tool for educators" under <a href="http://www.dlese.org/Metadata/adn-item/0.6.50/docs/xml-info.htm#vocabs">http://www.dlese.org/Metadata/adn-item/0.6.50/docs/xml-info.htm#vocabs</a>. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">itemAudienceTeachingMethod</span> - The intended audience teaching methods, for example "DLESE:Small group learning," as text. See "Teaching methods " under <a href="http://www.dlese.org/Metadata/adn-item/0.6.50/docs/xml-info.htm#vocabs">http://www.dlese.org/Metadata/adn-item/0.6.50/docs/xml-info.htm#vocabs</a>. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">itemannotypes</span> - Indicates the type of annotation that this item has, for example "Teaching tip," "Information on challenging teaching and learning situations," as text. These values are shown in the <a href="http://www.dlese.org/Metadata/annotation/0.1.01/vocabs/type.xsd">types schema</a>. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">itemannostatus</span> - Indicates the status of an annotation that this item has, for example "Text annotation completed," as text. These values are shown in the <a href="http://www.dlese.org/Metadata/annotation/0.1.01/vocabs/status.xsd">status schema</a>. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">itemannoformats</span> - Indicates the format of an annotation that this item has. Values include 'text', 'audio', 'video' and 'graphical'.  May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">itemannopathways</span> - Indicates the pathway of an annotation that this item has, for example "CRS (Community Review System)," as text. These values are shown in the <a href="http://www.dlese.org/Metadata/annotation/0.1.01/vocabs/pathway.xsd">pathway schema</a>. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">newsOppsannouncement</span> - News & Opportunities announcements. May be sorted. Available for formats: news_opps.
</ul>
<br>
<ul>
<li><span class="code">newsOppsaudience</span> - News & Opportunities audience. May be sorted. Available for formats: news_opps.
</ul>
<br>
<ul>
<li><span class="code">newsOppsdiversity</span> - News & Opportunities diversity. May be sorted. Available for formats: news_opps.
</ul>
<br>
<ul>
<li><span class="code">newsOppslocation</span> - News & Opportunities locations. May be sorted. Available for formats: news_opps.
</ul>
<br>
<ul>
<li><span class="code">newsOppstopic</span> - News & Opportunities topics. May be sorted. Available for formats: news_opps.
</ul>
<br>

<ul>
<li><span class="code">ncsCollectEdLevel</span> - NSDL Collection education level field. May be sorted. Available formats: ncs_collect.<br>
  From xpath: /record/educational/educationLevels/nsdlEdLevel, /record/educational/educationLevels/otherEdLevel  
</ul>
<br>
  
<ul>
<li><span class="code">ncsCollectCollectionPurpose</span> - NSDL Collection collection purpose field. May be sorted. Available formats: ncs_collect.<br>
  From xpath: /record/collection/collectionPurposes/collectionPurpose
</ul>
<br>  
  
<ul>
<li><span class="code">ncsCollectAudience</span> - NSDL Collection audience field. May be sorted. Available formats: ncs_collect.<br>
  From xpath: /record/educational/audiences/nsdlAudience, /record/educational/audiences/otherAudience
</ul>
<br>

<ul>
<li><span class="code">ncsCollectSubject</span> - NSDL Collection subject field. May be sorted. Available formats: ncs_collect.<br>
  From xpath: /record/general/subject
</ul>
<br>

<ul>
<li><span class="code">ncsCollectCollectionSubject</span> - NSDL Collection collection subject field. May be sorted. Available formats: ncs_collect.<br>
  From xpath: /record/collection/collectionSubjects/collectionSubject
</ul>
<br>

<ul>
  <li><span class="code">ncsCollectPathwayName</span> - NSDL Collection pathway name. May be sorted. Available formats: ncs_collect.<br>
    From xpath: /record/collection/pathways/name
</ul>
<br>


<b>Encoded vocabulary fields</b> - These fields contain  DLESE-specific controlled vocabularies used in the <code>adn</code> and <code>dlese_collect</code> metadata frameworks that have encoded into keys. Corresponding textual vocabulary fields are listed above, e.g. the same information is indexed both as text and as keys for these fields: <span class="code">gr</span> - gradeRanges; <span class="code">re</span> - resourceTypes; <span class="code">su</span> - subjects; <span class="code">cs</span> - contentStandards.<br>
<br>
<ul>
<li><span class="code">gr</span> - The DLESE grade range vocabularies encoded as a two or three character key, for example "05." These values may be discovered using the <a href="#ListGradeRanges">ListGradeRanges</a> request within the searchKey element. May be sorted. Available for formats: adn. 
</ul>
<br>
<ul>
<li><span class="code">re</span> - The DLESE resource type vocabularies encoded as a two or three character key, for example "05." These values may be discovered using the <a href="#ListResourceTypes">ListResourceTypes</a> request within the searchKey element. May be sorted. Available for formats: adn. 
</ul>
<br>
<ul>
<li><span class="code">su</span> - The DLESE subject vocabularies encoded as a two or three character key, for example "05." These values may be discovered using the <a href="#ListSubjects">ListSubjects</a> request within the searchKey element. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">cs</span> - The DLESE content standard vocabularies encoded as a two or three character key, for example "05." These values may be discovered using the <a href="#ListContentStandards">ListContentStandards</a> request within the searchKey element. May be sorted. Available for formats: adn.
</ul>
<br>
<b>Defined key fields</b> - These fields contain finite sets of key values  that may be used to limit searches to a sub-set of records.<br>
<br>
<ul>
<li><span class="code">ky</span> - Contains the search key for the collection in which the record resides, which may be used to limit search to within one or more collections of records. These values may be discovered using the <a href="#ListCollections">ListCollections</a> request within the searchKey element. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">collection</span> - Similary to <code>ky</code>, contains the record's collection vocabulary entry appended with a 0, for example "0dcc," "0comet.". These values may be discovered using the <a href="#ListCollections">ListCollections</a> request within the vocabEntry element. May be sorted. Available for all formats.
</ul>
<br>
<ul>
<li><span class="code">itemhasanno</span> - Indicates whether an item has an annotation. Values are either "true" or "false." May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">partofdrc</span> - Indicates whether the item or collection is part of the DLESE Reviewed Collection (DRC). Values are either "true" or "false." May be sorted. Available for formats: adn, dlese_collect.
</ul>
<br>
<ul>
<li><span class="code">multirecord</span> - Indicates whether the resource that the record catalogs is also cataloged by other records in other collections. Values are either "true" or "false." May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">wntype</span> - Indicates the reason the item is new to the repository, corresponding to the 'wndate' field. Possible values are: itemnew, itemannocomplete, itemannoinprogress, annocomplete, drcannocomplete, drcannoinprogress, collection. May be sorted. Available for all formats.
</ul><br>
<ul>
<li><span class="code">ncsCollectHasOai</span> - Boolean value that indicates whether the NSDL Collection metadata contains OAI information (an OAI baseURL). Possible values are: true, false. May be sorted. Available formats: ncs_collect.<br>
  From xpath: /record/collection/ingest/oai/@baseURL
</ul>
<br>
<ul>
<li><span class="code">ncsCollectOaiVisibility</span> - The NSDL Collection OAI visibility field falue. Possible values are: public, protected, private. May be sorted. Available formats: ncs_collect.
  <br>
  From xpath: /record/collection/OAIvisibility
</ul>
<br>
<ul>
<li><span class="code">ncsCollectIsPathway</span> - Boolean value that indicates the NSDL Collection pathway value. Possible values are: true, false. May be sorted. Available formats: ncs_collect.
  <br>
  From xpath: /record/collection/pathway
</ul>

<br>
    <b>Fields available for searching by value or range of value </b> - These fields may be searched by exact value or by range of value:<br>
<br>
<ul>
<li><span class="code">itemannoaveragerating</span> - Contains the average of all star ratings assigned to a given resource. Values range from 1.000 to 5.000. 
Example search syntax <em>itemannoaveragerating:[3.500 TO 5.000]</em> - returns all resources with an average star rating of 3.5 to 5.0. May be sorted. Available for formats:adn.
</ul>
<br>
<ul>
<li><span class="code">itemannoratingvalues</span> - Contains all star ratings assigned to a given resource. Values range from 1 to 5. 
Example search syntax <em>itemannoratingvalues:[3 TO 5]</em> - returns all resources that have one or more ratings of 3, 4, or 5 stars assigned to them. May be sorted. Available for formats:adn.
</ul>
<br>
<ul>
<li><span class="code">itemannonumratings</span> - Contains the number of star ratings that have been assigned to a given resource. Values are encoded to 5 digits, for example <em>00000</em> or <em>00014</em>. 
Example search syntax <em>itemannonumratings:[00004 TO 99999]</em> - returns all resources  that have from 4 to 99999 star ratings assigned to them. May be sorted. Available for formats:adn.
</ul>
<br>
<ul>
<li><span class="code">annorating</span> - Contains the star rating of a given annotation record. Values are integers from 1 to 5. 
Example search syntax <em>annorating:[3 TO 5]</em> - returns all annotations that have a start rating of 3 to 5. May be sorted. Available for formats: dlese_anno.
</ul><br>
<ul>
<li><span class="code">ncsCollectOaiFrequency</span> - Integer and float values that indicate the NSDL Collection OAI harvest frequency in months. Range queries are not supported. (search by value only). Possible values are: 1, 2, ... n; 0.5. May be sorted. Available formats: ncs_collect.<br>
  From xpath: /record/collection/ingest/oai/@frequency
</ul>
<br>
<b>Fields available for searching by date</b> - These fields may be supplied in the 'dateField' argument of the <a href="#Search">Search</a> request:<br>
<br>
<ul>
<li><span class="code">wndate</span> - A date field that indicate the date the item was new to the repository, corresponding to the 'wntype' field. May be sorted. Available for all formats.
</ul>
<br>
<ul>
<li><span class="code">accessiondate</span> - The ADN accession date for the record. May be sorted. Available for formats: adn.
</ul>
<br>
<ul>
<li><span class="code">collaccessiondate</span> - The dlese_collect accession date for the collection. May be sorted. Available for formats: dlese_collect.
</ul>
<br>
<ul>
<li><span class="code">modtime</span> - A date field that corresponds to the time the items file was last modified or touched. This does not necessarily indicate that the content in the record changed. May be sorted. Available for all formats.
</ul>
<br>
<ul>
<li><span class="code">newsOppsapplyBydate</span> - News & Opportunities applyBy date. May be sorted. Available for formats: news_opps.
</ul>
<br>
<ul>
<li><span class="code">newsOppsarchivedate</span> - News & Opportunities archive date. May be sorted. Available for formats: news_opps.
</ul>
<br>
<ul>
<li><span class="code">newsOppsduedate</span> - News & Opportunities due date. May be sorted. Available for formats: news_opps.
</ul>
<br>
<ul>
<li><span class="code">newsOppseventStartdate</span> - News & Opportunities eventStart date. May be sorted. Available for formats: news_opps.
</ul>
<br>
<ul>
<li><span class="code">newsOppseventStopdate</span> - News & Opportunities eventStop date. May be sorted. Available for formats: news_opps.
</ul>
<br>
<ul>
<li><span class="code">newsOppspostdate</span> - News & Opportunities post date. May be sorted. Available for formats: news_opps.
</ul>
<br>
<ul>
<li><span class="code">newsOppsrecordCreationdate</span> - News & Opportunities recordCreation date. May be sorted. Available for formats: news_opps.
</ul>
<br>
<ul>
<li><span class="code">newsOppsrecordModifieddate</span> - News & Opportunities recordModified date. May be sorted. Available for formats: news_opps.
</ul>
<br>
<br>
<a name="Example_search_queries" id="Example_search_queries"></a>
<h3>Example search queries</h3>
<br>
This section shows some examples of performing searches using the <a href="#Search">Search</a> or <a href="#UserSearch">UserSearch</a> request. To perform these searches, the values shown below should be supplied in the 'q' argument, using the <a href="http://lucene.apache.org/java/2_4_0/queryparsersyntax.html">Lucene Query Syntax</a> (LQS). Additional arguments may be supplied to the Search or UserSearch request to further limit the search, such as xmlFormat, dateField and the vocabulary fields gr, su, re and cs.<br>
<br>
Search for the term 'ocean' in the default field:<br>
  <pre>ocean</pre><br>
Search for the term 'ocean' in the stems field. This will return documents containing morphologically similar terms including <i>ocean, oceans</i> and <i>oceanic</i>:<br>
  <pre>stems:ocean</pre><br>
Search for the terms 'currents in the oceans' in the stems field. Notice that the client should supply the plain english version of the terms without pre-stemming them. In this example  the resulting search matches documents that contain both <em>currents, current </em>or<em> currently</em> AND <em>oceans, ocean, </em>or<em> oceanic</em> (the terms 'in' and 'the' are stop words that are dropped for the purpose of search):<br>
  <pre>stems:(currents in the oceans)</pre>
  <br>  
Search for resources that that have an average star rating of 3.5 to 5.0:<br>
  <pre>itemannoaveragerating:[3.500 TO 5.000]</pre><br>  
Search for resources that contain  'noaa.gov' in their URL:<br>
  <pre>url:http*noaa.gov*</pre><br>
Search for the term ocean within resources from 'noaa.gov':<br>
  <pre>url:http*noaa.gov* AND ocean</pre><br>
Search for term 'estuary' in the stems field, and limit the search to subject biological oceanography (subject key 02):<br>
  <pre>stems:estuary AND su:02</pre>  <br>
Search for the term 'ocean' in the default field, and boost the ranking of results that contain 'ocean' in their title (stemmed) (uses the special clause allrecords:true to select the set of all records). Note that this clause returns the same number of results as if the search were performed  only over the word 'ocean' in the default field, but it applies additional boosting for records that contain the term 'ocean' in their title (stemmed), which augments the search rank of the results that are returned. This example illustrates the kind of search rank augmentation that is applied automatically in the UserSearch request.<br>
  <pre>ocean AND (allrecords:true OR titlestems:ocean^2)</pre>
  <br>
Show all records with subject biological oceanography, and boost results that contain florida in the title (stemmed), description or placeNames fields (uses the clause allrecords:true to select the set of all records):<br>
  <pre>su:02 AND (allrecords:true OR titlestems:florida*^20 
           OR description:florida*^20 OR placeNames:florida^20) </pre>
  <br>
	<br>
<h2>Glossary</h2>
<p><a name="whatsNewDate"></a>
    <b>whatsNewDate</b> - A date that describes when an item was new to the repository. Generally this corresponds to the item's accession date or the date in which the item first became accessible in the repository.<br>
</p>

<h2>&nbsp;</h2>
<h2>Configure search fields </h2>
<p><a name="configureDDS"></a> The following document provides information for system administrators who are installing and managing a DDS repository system, which includes the <a href="/dds/services/dds_software.jsp">Digital Discovery System</a> (DDS) and the <a href="/dds/services/dcs_software.jsp">NSDL Collection System</a> (NCS). </p>
<ul>
  <li>
        <a href="${pageContext.request.contextPath}/docs/configure_dds_search_fields.jsp">Configure Search Fields</a> - This document describes how to configure the search fields for specific XML frameworks in the repository.</li>
</ul><p><br>
</p>

<div style="padding-top:40px; color:#555555;">
	  John Weatherley
	  &lt;<script type='text/javascript'>
	  <!-- 
		dlese_rea13( 'wjrngure', 'hpne.rqh' ); 
	// -->
	  </script>&gt;<br>
  Last revised: $Date: 2016/04/08 22:18:08 $
</div>

<%@ include file="/nav/bottom.jsp" %>


</body></html>