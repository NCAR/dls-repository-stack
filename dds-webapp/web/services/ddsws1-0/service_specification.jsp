<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>
<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>

<html><!-- InstanceBegin template="/Templates/services.dwt" codeOutsideHTMLIsLocked="true" -->
<head>

<!-- InstanceBeginEditable name="head" -->
	<META NAME="keywords" CONTENT="DLESE,digital library for earth system education">

	<META NAME="description" CONTENT="DLESE Discovery System JavaScript Service Portal">

	<META NAME="creator" CONTENT="John Weatherley">

	<META NAME="organization" CONTENT="DLESE Program Center">

	<META NAME="doctype" CONTENT="DLESE webpage">

<!-- InstanceEndEditable -->
<script language="JavaScript" src="../../../Templates/dds_services_script.js"></script>

	<%-- DLESE template header (CSS styles and JavaScript references) --%>
	<%@ include file="/nav/head.jsp" %> 
	
	

</head>   
<body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066">
<!-- InstanceBeginEditable name="sectionTitle" -->
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Services and<br/>  APIs
</div>
<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>

<table border="0" cellspacing="0" cellpadding="0">

  <tbody><tr>

<td valign="top">



  <h1>Search Web Service Documentation</h1>
  <div style="padding-bottom:20px">

	Service version DDSWS v1.0<br>

	Document version $Revision: 1.6 $<br>

	Last revised $Date: 2009/12/03 18:47:28 $</div>





  <p><b>A new version of this service is available</b>. 
    The documentation here is for a previous version. 
    Please refer to the <a href="../ddsws/">Current Search Web Service</a> for new application development. 
    
    
    
  <br>
  <br>
    
    
        
  <br>
    John Weatherley 
    
  &lt;
  <script type='text/javascript'><!--

	dlese_rea13( 'wjrngure', 'hpne.rqh' );

// -->

</script>
  &gt;
    
    - University Corporation for Atmospheric Research, DLESE Program Center.<br>
    
  <br>
    The DLESE Discovery System Web Service (DDSWS) provides programmatic access to DLESE's search functionality and offers full access to the metadata in DLESE's collections. The service is designed to be used in real-time, high-availability Web sites and applications  to provide interactive search and discovery interfaces for library resources. The service is also intended to support research that involves the metadata and content in the library. Metadata is available for educational resources, annotations, news & opportunities and collections. The service supports textual searching over metadata and content, searching by date and field, sorting by date and field, discovery of the controlled vocabularies that are part of the library including grade ranges, subjects, resources types and content standards, checking for the existence of a URL, and other functionality. A full range of Information Retrieval features are exposed through the service as outlined in the description for the <a href="#Search">Search</a> and <a href="#UserSearch">UserSearch</a> requests. The service is able to disseminate a number of XML formats as indicated by the <a href="#ListXmlFormats">ListXmlFormats</a> request.<br>
    
  <br>
    
    The Web service requests and responses are described in detail below and examples are provided for reference by developers. Developers may also find the Web service client <a href="http://www.dlese.org/dds/services/examples/ddsws/">JSP template and examples</a> helpful.<br>
    
  <br>
    
    Thanks to Mike Wright, Tammy Sumner, Sebastian de la Chica, Faisal Ahmad, Srikaran Reddy and other DLESE and NSDL collaborators who contributed directly or indirectly to the design and testing of this service. The design of this service was greatly informed by the work of the <a href="http://www.openarchives.org/">Open Archives Initiative</a> and <a href="http://oai.dlib.vt.edu/odl/">Open Digital Libraries</a>.  </p>
  <h3>Definitions and concepts</h3>
  <p>DDSWS is a Representational State Transfer (REST) style Web service. In REST style Web service architectures, requests are typically encoded as a URL and responses are returned as XML.<br>
    
      <br>
    
    More formally: DDSWS requests are expressed as HTTP argument/value pairs. These requests must be in either GET or POST format. Responses are returned in XML format, which varies in structure and content depending on the request as shown below in the examples section of this document.<br>
    
  <br>
  </p>
  <ul>

<li><i>Base URL</i> - the base URL used to access the Web service. This is the portion of the request that precedes the request arguments. For example http://www.dlese.org/dds/services/ddsws1-0.
<li><i>Request arguments</i> - the argument=value pairs that make up the request and follow the base URL. 
<li><i>DDSWS response envelope</i> - the XML container used to return data. This container returns different types of data depending on the request made.
</ul>

<br>

<h3>HTTP request format</h3>

<p>The format of the request consists of the base URL followed by the ? character followed by one or more argument=value pairs, which are separated by the & character. Each request must contain one <i>verb=request</i> pair, where <i>verb</i> is the literal string 'verb' and <i>request</i> is one of the DDSWS request strings defined below. All arguments must be encoded using the <a href="http://www.ietf.org/rfc/rfc2396.txt">syntax rules for URIs</a>. This is the same <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#SpecialCharacters">encoding scheme that is described by the OAI-PMH</a>.<br>
  
    <br>
  
    <a name="requests"></a></p>
<h3>DDSWS requests and responses</h3>
<p>This section defines the available requests or <i>verbs</i>. </p>
<p>  The HTTP request format has the following structure:<br>
  [base URL]?verb=<i>request</i>[&additional arguments]. </p>
<p>  For example:</p>
<blockquote> http://www.dlese.org/dds/services/ddsws1-0?<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  verb=GetRecord&amp;id=DLESE-000-000-000-001   </blockquote>
<h4>Summary of available requests:</h4>
  
    <p><a href="#Search">Search</a> - Allows a client to search the full metadata repository and the content of the resources using a rich textual and fielded Boolean search query language that supports advanced Information Retrieval query features such as term, field and phrase searches, term and term/field boosting, term stemming, wildcard and fuzzy searches, term proximity searches, and other functionality. The Search request has access to a wide range of <a href="#availableSearchFields">available search fields</a>, and through the use of query clauses, can be used to apply custom page rank algorighms to searches (see <a href="#Example_search_queries">example search queries</a>). The request also supports searching by <a href="#ListXmlFormats">XML format</a>, searching by date and sorting results by index field, and restricting searches by library <a href="#VocabList">controlled vocabularies</a>, such as grade ranges, subjects, etc. </p>
    <p><a href="#UserSearch">UserSearch</a> - Is nearly identical to the Search request except that it operates over educational resources (ADN metadata format) only, and it applies a default searcher that automatically performs word stemming and provides page rank boosting for resources that match higher relevancy search indicators, such as when a resource contains a matching search term in the title field of it's metadata as opposed to elsewhere in it's text. These search algorithms  are the same as those that are applied to user's searches in the DLESE library web site. This request is meant to to be used by clients that wish to provide a search experience similar to that in the DLESE library web site or wish to leverage the automatic word stemming and page rank algorithms that are applied to searches. <br>
      
        <br>
      
        <a href="#GetRecord">GetRecord</a> - Accesses the metadata for a single record.<br>
      
        <br>
      
        <a href="#ListCollections">ListCollections</a> - Accesses the list of available metadata collections in the repository.<br>
      
        <br>
      
        <a href="#ListGradeRanges">ListGradeRanges</a> - Accesses the list of controlled vocabularies and search keys for grade ranges.<br>
      
        <br>
      
        <a href="#ListSubjects">ListSubjects</a> - Accesses the list of controlled vocabularies and search keys for subjects.<br>
      
        <br>
      
        <a href="#ListResourceTypes">ListResourceTypes</a> - Accesses the list of controlled vocabularies and search keys for resource types.<br>
      
        <br>
      
        <a href="#ListContentStandards">ListContentStandards</a> - Accesses the list of controlled vocabularies and search keys for content standards.<br>
      
        <br>
      
        <a href="#ListXmlFormats">ListXmlFormats</a> - Accesses the list of the available XML formats from this service.<br>
      
        <br>
      
        <a href="#UrlCheck">UrlCheck</a> - Allows a client to check whether a given URL is cataloged in the repository.<br>
      
        <br>
      
        <a href="#ServiceInfo">ServiceInfo</a> - Accesses information about this Web service.<br>
      
        <br>
      
        <br>
      
        <a name="Search"></a>
      
          </h4>
        </p>
    <h4>Search</h4>
<p><b>Sample request</b><br>

    <br>

    The following request performs a search for the term "ocean" and returns 10 search results, starting at position 0:<br>

    <br>

    <a href="http://www.dlese.org/dds/services/ddsws1-0?verb=Search&q=ocean&s=0&n=10&client=ddsws-documentation" target="_blank">http://www.dlese.org/dds/services/ddsws1-0?verb=Search&q=ocean&s=0&n=10&client=ddsws-documentation</a><br>

    <br>

    <b>Summary and usage</b><br>

    <br>

    The Search request allows a client to search the full metadata repository and the content of the resources using a rich textual and fielded Boolean search query language that supports advanced Information Retrieval query features such as term, field and phrase searches, term and term/field boosting, term stemming, wildcard and fuzzy searches, term proximity searches, and other functionality. The Search request has access to a wide range of <a href="#availableSearchFields">available search fields</a>, and through the use of query clauses, can be used to apply custom page rank algorighms to searches (see <a href="#Example_search_queries">example search queries</a>). The request also supports searching by <a href="#ListXmlFormats">XML format</a>, searching by date and sorting results by index field, and restricting searches by library <a href="#VocabList">controlled vocabularies</a>, such as grade ranges, subjects, etc.</p>

<p>The Search and UserSearch response consists of an ordered set of metadata records, sorted by relevancy. Flow control is managed by the client, which may 'page through' a set of results using the 's' and 'n' arguments as described below. The metadata returned by the Search request may be of any type that is supported by the library, including ADN, collection, annotation, news and opportunities, briefmeta or other supported formats. The metadata returned by the UserSearch request is always in ADN format.  <br>

      <br>

      The Search and UserSearch request accepts queries using the <a href="http://jakarta.apache.org/lucene/docs/queryparsersyntax.html">Lucene Query Syntax</a> (LQS). LQS supports advanced Information Retrieval query clauses such as term and field boosting, wildcard and fuzzy searches, etc. Queries are supplied in the <i>q</i> argument of the request.<br>

      <br>

      <b>Arguments</b></p>

<ul>

<li>q - (query) an <i>optional</i> argument that may contain plain text or field/term specifiers. Boolean logic, field/term specifiers and boosting must be specified using the <a href="http://jakarta.apache.org/lucene/docs/queryparsersyntax.html">Lucene Query Syntax</a> (LQS). Plain text terms (when no field is indicated) are used to search in the default field, which contains textual metadata extracted from the title, description, keywords, grade ranges, resource types and other areas of the library metadata. See <a href="#availableSearchFields">available search fields</a> for examples and detailed information about the fields that are available for searching.
</ul>

<br>

<ul>

<li>xmlFormat - an <i>optional</i> argument that indicates the format the records must be returned in. If specified, searches are limited to only those records that are available in the given format. If not specified, the records will be returned in their native format using a localized version of XML (e.g. stripped of their namespace and schema declarations). The available formats may be discovered using the <a href="#ListXmlFormats">ListXmlFormats</a> request. <i>Not supported in the UserSearch request.</i>
</ul>

<br>

<ul>

<li>client - an <i>optional</i> argument that may be supplied by the client to indicate where the request originated from. Example values might be <i>ddsExamplesSearchClient</i> or <i>myLibrarySearchClient</i>. When supplied, this information is used by DLESE to  help understand how people are using the service and a client-by-client basis.
</ul>

<br>

<ul>

<li>so - (search over) an <i>optional</i> argument that must contain the value allRecords or discoverableRecords. Users who request to search over allRecords must be authorized by IP, otherwise an error is returned. Defaults to discoverableRecords. <i>Not supported in the UserSearch request.</i>
</ul>

<p>
  Flow control is managed by the client through the use of the 's' and 'n' arguments, which specify the desired state of paging through a set of search results. For example, when a search is initially performed, the client would typically construct a request that supplies the arguments s=0 and n=10 to return up to the first 10 matching results. The client could then page through the set of results by issuing subsequent requests indicating s=10 and n=10 for the next ten results, s=20 and n=10 for results 20 through 30 and so forth up to totalNumResults. For each requested page of results the client must supply identical search criteria in the q, gr, su, cs, re, xmlFormat, so, sorting and date-restrictive arguments. The search algorithm used in DDSWS is deterministic, which means the set and order of search results are guaranteed to be identical for any two searches when identical search criteria are applied. Thus the 's' and 'n' arguments can be thought of as indicating the 'window' into the set of ordered search results into which the client wants to see.</p>
<ul>

<li>s - (starting offset) - a <i>required</i> argument that specifies the starting offset into the results set upon which metadata records should be returned. May be any integer grater than or equal to 0.
</ul>

<br>

<ul>

<li>n - (number returned) - a <i>required</i> argument that specifies the number of metadata records to return, beginning at the offset specified by 's'. Must be a integer from 1 to 500.
</ul>

<p><br>
  
  The following arguments serve to limit searches by controlled vocabulary. The searchKey that must be used with these arguments must be discovered using the <a href="#VocabList">vocabulary list requests</a>. Example searchKey gr=07</p>
<ul>

<li>gr - (grade range) an <i>optional repeatable</i> argument that limits the search to the given grade range(s). If none specified defaults to searching over all grade ranges. If more than one is specified then boolean OR logic is applied across each. 
</ul>

<br>

<ul>

<li>re - (resource type) an <i>optional repeatable</i> argument that limits the search to the given resource type(s). If none specified defaults to searching over all resource types. If more than one is specified then Boolean OR logic is applied across each. 
</ul>

<br>

<ul>

<li>su - (subject) an <i>optional repeatable</i> argument that limits the search to the given subject(s). If none specified defaults to searching over all subjects. If more than one is specified then Boolean OR logic is applied across each. 
</ul>

<br>

<ul>

<li>cs - (content standard) an <i>optional repeatable</i> argument that limits the search to the given content standard(s). If none specified defaults to searching over all content standards. If more than one is specified then Boolean OR logic is applied across each. 
</ul>

<br>

<ul>

<li>ky - (collection key) an <i>optional repeatable</i> argument that limits the search to the given collection(s). If none specified defaults to searching over all collections. If more than one is specified then Boolean OR logic is applied across each. 
</ul>

<p>  The following two arguments instruct the service to sort the response by a given index field. The service sorts the entire result set lexically prior to returning the requested portion of the results. Only one of these two arguments may be supplied in the request. Values must a <a href="#availableSearchFields">sortable field in the index</a>, as listed below. These arguments are <i>Not supported in the UserSearch request.</i></p>
<ul>

<li>sortAscendingBy - an <i>optional</i> argument that instructs the service to sort the search results in ascending lexical order by a given index field. 
  <br>
</ul>

<ul>

<li>sortDescendingBy - an <i>optional</i> argument that instructs the service to sort the search results in descending lexical order by a given index field. 
  </ul>

<p>The following arguments instruct the service to search in a given index date field. Date searches may be performed independently or in combination with text-based or vocabulary-based searches. The values provided in the fromDate or toDate arguments must be a union date type string of the form yyyy-MM-dd or an ISO8601 UTC datastamp of the form yyyy-MM-ddTHH:mm:ssZ. Example dates include 2004-07-08 or 2004-07-26T21:58:25Z. The <a href="#availableSearchFields">fields that are avialable for searching by date</a> are listed below. These arguments are <i>Not supported in the UserSearch request.</i></p>
<ul>

<li>dateField - an <i>optional</i> argument that indicates which index date field to search in. If supplied, one or both of either the fromDate or toDate arguments <i>must</i> be supplied.
  <br>
</ul>

<ul>

<li>fromDate - an <i>optional</i> argument that indicates a date range to search from. If supplied, the dateField argument <i>must</i> also be supplied.
  <br>
</ul>

<ul>

<li>toDate - an <i>optional</i> argument that indicates a date range to search to. If supplied, the dateField argument <i>must</i> also be supplied.
</ul>

<p><b>Errors and exceptions</b><br>
  
      <br>
  Error is indicted if the request does not the required arguments or if an argument is included that is not supported.<br>
  
      <br>
  
      <b>Examples</b><br>
  
      <br>
  
      <i><b>Request</b></i><br>
  
      <br>
  Search for the word ocean.</p>
<blockquote> 
  <p>http://www.dlese.org/dds/services/ddsws1-0?
    
    verb=Search&amp;q=ocean&amp;s=0&amp;n=10 </p>
  </blockquote>
<p><i><b>Response</b></i></p>
<table border=1 cellspacing=0 cellpadding=5>

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

<pre>http://www.dlese.org/dds/services/ddsws1-0?

           verb=Search&amp;q=ocean&amp;gr=02&amp;s=0&amp;n=10</pre><br>

<i><b>Response</b></i><br>

<br>

<table border=1 cellspacing=0 cellpadding=5>

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

Search for all ADN records new to the library since July 7th, 2004 and sort descending by the wndate field.<br>

<br>

<pre>http://www.dlese.org/dds/services/ddsws1-0?

verb=Search&amp;s=0&amp;n=10&amp;fromDate=2004-07-08&amp;dateField=wndate

&amp;sortDescendingBy=wndate&amp;xmlFormat=adn-localized</pre><br>

<i><b>Response</b></i><br>

<br>

<table border=1 cellspacing=0 cellpadding=5>

<tr><td>Same format as above.</td></tr>
</table>

<br>

<br>

<i><b>Request</b></i><br>

<br>

Perform a request that does not contain all required arguments and thus is in error.<br>

<br>

<pre>http://www.dlese.org/dds/services/ddsws1-0?

         verb=Search&amp;q=ocean&amp;gr=02&amp;s=0</pre><br>

<i><b>Response</b></i><br>

<br>

<table border=1 cellspacing=0 cellpadding=5>

<tr><td><pre>

&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 

&lt;DDSWebService&gt;

  &lt;error&gt;

    The argument 'n' is required. n specifies the 

    maximum number of results to return.

  &lt;/error&gt;

&lt;/DDSWebService&gt;

</pre> </td></tr>
</table>

<br>

<br>

<a name="UserSearch"></a>

<h4>UserSearch</h4>
<p><b>Sample request</b><br>

    <br>

    The following request performs a search for the term "ocean" and returns 10 search results, starting at position 0:<br>

    <br>

    <a href="http://www.dlese.org/dds/services/ddsws1-0?verb=UserSearch&q=ocean&s=0&n=10&client=ddsws-documentation" target="_blank">http://www.dlese.org/dds/services/ddsws1-0?verb=UserSearch&q=ocean&s=0&n=10&client=ddsws-documentation</a><br>

    <br>

    <b>Summary and usage</b><br>

    <br>

    The UserSearch request is nearly identical to the Search request except that it operates over educational resources (<a href="http://www.dlese.org/Metadata/adn-item/index.htm">ADN metadata format</a>) only, and it applies a default searcher that automatically performs word stemming and provides page rank boosting for resources that match higher relevancy search indicators, such as when a resource contains a matching search term in the title field of it's metadata as opposed to elsewhere in it's text. These search algorithms are the same as those that are applied to user's searches in the DLESE library web site. This request is meant to to be used by clients that wish to provide a search experience similar to that in the DLESE library web site or wish to leverage the automatic word stemming and page rank algorithms that are applied to searches.    </p>

<p>The UserSearch response is identical to the Search response and consists of an ordered set of ADN records, sorted by relevancy. The default searcher that is used incorporates several Information Retrieval techniques designed to augment the page rank and total number of results. These augmentations include word stemming, boosting of records that contain search terms in their title or description and a slight boosting of records that are cataloged by two or more collections or are part of the <a href="http://www.dlese.org/dds/collection.do?key=drc">DLESE Reviewed Collection</a>.  The default searcher's algorithms are applied automatically to all terms and phrases supplied by the client in the default field portion of the query sent in the request, and all other fields in the query are treated normally (see <a href="#availableSearchFields">available search fields</a> for examples and details). Clients may use this request as a starting point and apply additional boosting to what is provided by the default searcher by supplying additional ranking clauses in the  query sent in the request. Clients wishing to implement their own page rank algorithms fully from scratch, or to search over records in formats other than ADN, should use the <a href="#Search">Search</a> request.<br>

      <br>

      <b>Arguments</b><br>

      <br>

      UserSearch accepts the same arguments as the <a href="#Search">Search</a> request, with the exception of <em>xmlFormat</em>, <em>sortAscendingBy</em>, <em>sortDescendingBy</em>, <em>dateField</em>, <em>fromDate</em>,  <em>toDate</em>, and <em>so</em>. <br>

      <br>

      <b>Errors and exceptions</b><br>

      <br>

      Same as the <a href="#Search">Search</a> request.<br>

      <br>

      <b>Examples</b> <br>

      <br>

      <i><b>Request</b></i> </p>

<p>Same as the <a href="#Search">Search</a> request, however the verb argument must be indicated as 'UserSearch'  and the arguments listed above are not accepted. </p>

<p><i><b>Response</b></i></p>

<p>Identical to that of the <a href="#Search">Search</a> request. UserSearch only returns ADN records. <br>

        <br>

        <br>

        <a name="GetRecord"></a></p>

<h4>GetRecord</h4>
<b>Sample request</b><br>

<br>

The following request displays the metadata for record ID DLESE-000-000-000-001 displayed in it's native XML format:<br>

<br>

<a href="http://www.dlese.org/dds/services/ddsws1-0?verb=GetRecord&id=DLESE-000-000-000-001" target="_blank">http://www.dlese.org/dds/services/ddsws1-0?verb=GetRecord&id=DLESE-000-000-000-001</a><br>

<br>

<b>Summary and usage</b><br>

<br>

The GetRecord request is used to pull up the metadata for a single item in the repository. Clients should use this request to display the metadata from a single record, for example if the user has requested "more information" about a resource. The data is returned in <a href="http://www.dlese.org/Metadata/adn-item/index.htm">ADN format</a> and other formats including dlese_collect, dlese_anno, oai_dc, nsdl_dc and briefmeta. <a href="http://www.dlese.org/Metadata/adn-item/0.6.50/samples/index.htm">Sample ADN records are available here</a>.<br>

<br>

<b>Arguments</b><br>

<br>

<ul>

<li>id - a <i>required</i> argument that specifies the DLESE identifier for the record.
</ul>

<br>

<ul>

<li>xmlFormat - an <i>optional</i> argument that indicates the format the record must be returned in. If specified, responses are limited to only those records that are available in the given format. If not specified, the record will be returned in it's native format using a localized version of XML (e.g. stripped of it's namespace and schema declaration). The available formats may be discovered using the <a href="#ListXmlFormats">ListXmlFormats</a> request.
</ul>

<br>

<ul>

<li>so - (search over) an <i>optional</i> argument that must contain the value allRecords or discoverableRecords. Users who request to search over allRecords must be authorized by IP, otherwise an error is returned. Defaults to discoverableRecords.
</ul>

<br>

<b>Errors and exceptions</b><br>

<br>

Error is indicted if the request does not contain the id argument or if the record is not available.<br>

<br>

<b>Examples</b><br>

<br>

<i><b>Request</b></i><br>

<br>

Request the DLESE record id DLESE-000-000-000-337 and get the response in ADN format. Shown without the required <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#SpecialCharacters">encoding</a>, for clarity.<br>

<br>

<pre>http://www.dlese.org/dds/services/ddsws1-0?

        verb=GetRecord&amp;id=DLESE-000-000-000-337</pre><br>

<i><b>Response</b></i><br>

<br>

<table border=1 cellspacing=0 cellpadding=5>

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

<i><b>Request</b></i><br>

<br>

Request a DLESE record id that does not exist in the repository. An error is returned.<br>

<br>

<pre>http://www.dlese.org/dds/services/ddsws1-0?

          verb=GetRecord&amp;id=DLESE-000-000-0z00-001</pre><br>

<i><b>Response</b></i><br>

<br>

<table border=1 cellspacing=0 cellpadding=5>

<tr><td><pre>

&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 

&lt;DDSWebService&gt;

  &lt;error&gt;ID DLESE-000-000-0z00-001 does not 

     exists in the repository.&lt;/error&gt; 

&lt;/DDSWebService&gt;

</pre> </td></tr>
</table>

<br>

<i><b>Request</b></i><br>

<br>

Issue an invalid response (the id argument is missing)<br>

<br>

<pre>http://www.dlese.org/dds/services/ddsws1-0?verb=GetRecord</pre><br>

<i><b>Response</b></i><br>

<br>

<table border=1 cellspacing=0 cellpadding=5>

<tr><td><pre>

&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 

&lt;DDSWebService&gt;

  &lt;error&gt;The id argument is required but is 

      missing or empty&lt;/error&gt; 

&lt;/DDSWebService&gt;

</pre> </td></tr>
</table>

<br>

<br>

<a name="ListCollections"></a>

<h4>ListCollections</h4>
<b>Sample request</b><br>

<br>

The following request lists the metadata collections that are available in the library:<br>

<br>

<a href="http://www.dlese.org/dds/services/ddsws1-0?verb=ListCollections" target="_blank">http://www.dlese.org/dds/services/ddsws1-0?verb=ListCollections</a><br>

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

<h4>ListGradeRanges</h4>
<b>Sample request</b><br>

<br>

The following request lists the library grade range vocabularies and corresponding search keys:<br>

<br>

<a href="http://www.dlese.org/dds/services/ddsws1-0?verb=ListGradeRanges" target="_blank">http://www.dlese.org/dds/services/ddsws1-0?verb=ListGradeRanges</a><br>

<br>

<b>Summary and usage</b><br>

<br>

The ListGradeRangesrequest is used to discover the controlled vocabularies and search field/keys for grade ranges. Clients should use this request to generate user interface widgets for selecting grade ranges to search from. This request belongs to the <a href="#VocabList">vocabulary list class of requests</a>.<br>

<br>

<b>Examples</b><br>

<br>

Refer to the documentation for the <a href="#VocabList">vocabulary list class of requests</a>.<br>

<br>

<br>

<a name="ListSubjects"></a>

<h4>ListSubjects</h4>
<b>Sample request</b><br>

<br>

The following request lists the library subject vocabularies and corresponding search keys:<br>

<br>

<a href="http://www.dlese.org/dds/services/ddsws1-0?verb=ListSubjects" target="_blank">http://www.dlese.org/dds/services/ddsws1-0?verb=ListSubjects</a><br>

<br>

<b>Summary and usage</b><br>

<br>

The ListSubjects request is used to discover the controlled vocabularies and search field/keys for subjects. Clients should use this request to generate user interface widgets for selecting the subjects to search from. This request belongs to the <a href="#VocabList">vocabulary list class of requests</a>.<br>

<br>

<b>Examples</b><br>

<br>

Refer to the documentation for the <a href="#VocabList">vocabulary list class of requests</a>.<br>

<br>

<br>

<a name="ListResourceTypes"></a>

<h4>ListResourceTypes</h4>
<b>Sample request</b><br>

<br>

The following request lists the library resource type vocabularies and corresponding search keys:<br>

<br>

<a href="http://www.dlese.org/dds/services/ddsws1-0?verb=ListResourceTypes" target="_blank">http://www.dlese.org/dds/services/ddsws1-0?verb=ListResourceTypes</a><br>

<br>

<b>Summary and usage</b><br>

<br>

The ListResourceTypes request is used to discover the controlled vocabularies and search field/keys for resource types. Clients should use this request to generate user interface widgets for selecting the resource types to search from. This request belongs to the <a href="#VocabList">vocabulary list class of requests</a>.<br>

<br>

<b>Examples</b><br>

<br>

Refer to the documentation for the <a href="#VocabList">vocabulary list class of requests</a>.<br>

<br>

<br>

<a name="ListContentStandards"></a>

<h4>ListContentStandards</h4>
<b>Sample request</b><br>

<br>

The following request lists the library content standard vocabularies and corresponding search keys:<br>

<br>

<a href="http://www.dlese.org/dds/services/ddsws1-0?verb=ListContentStandards" target="_blank">http://www.dlese.org/dds/services/ddsws1-0?verb=ListContentStandards</a><br>

<br>

<b>Summary and usage</b><br>

<br>

The ListContentStandards request is used to discover the controlled vocabularies and search field/keys for content standards. Clients should use this request to generate user interface widgets for selecting the content standards to search from. This request belongs to the <a href="#VocabList">vocabulary list class of requests</a>.<br>

<br>

<b>Examples</b><br>

<br>

Refer to the documentation for the <a href="#VocabList">vocabulary list class of requests</a>.<br>

<br>

<br>

<a name="VocabList"></a>

<h4>Vocabulary list requests</h4>
<b>Summary and usage</b><br>

<br>

Vocabulary list requests include <a href="#ListGradeRanges">ListGradeRanges</a>, <a href="#ListSubjects">ListSubjects</a>, <a href="#ListResourceTypes">ListResourceTypes</a>, <a href="#ListContentStandards">ListContentStandards</a>, and <a href="#ListCollections">ListCollections</a>*. Each of the vocabulary list requests use the same request and response format.<br>

<br>

Vocabulary list requests are used to determine the search values supplied in the gr, su, re, cs and ky arguments of the <a href="#Search">Search</a> and <a href="#UserSearch">UserSearch</a> requests and should be used to construct user interface menus for selecting the grade ranges, subjects, etc. for users to limit their search by.<br>

<br>

More specifically, vocabulary list requests represent the class of requests that expose controlled vocabularies in the library (grade ranges, subjects, resource types, content standards and collections). Vocabulary list requests may be used to discover the vocabulary entries ('Primary elementary'. etc.), the search field/key pair used to perform and limit searches across the given vocabulary in the <a href="#Search">Search</a> and <a href="#UserSearch">UserSearch</a> requests ('gr=07', etc.), and a set of rendering guidelines used to determine things such as whether to display the vocabulary listing to the user and the label that should displayed, for example 'Primary (K-2)'.<br>

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

None.<br>

<br>

<b>Examples</b><br>

<br>

<i><b>Request</b></i><br>

<br>

Request the grade ranges that are available. Note the verb argument may contain any of the vocabulary list requests (<a href="#ListGradeRanges">ListGradeRanges</a>, <a href="#ListSubjects">ListSubjects</a>, <a href="#ListResourceTypes">ListResourceTypes</a>, <a href="#ListContentStandards">ListContentStandards</a>, or <a href="#ListCollections">ListCollections</a>) corresponding to the vocabulary you are interested in. <br>

<br>

<pre>http://www.dlese.org/dds/services/ddsws1-0?verb=ListGradeRanges</pre><br>

<i><b>Response</b></i><br>

<br>

<table border=1 cellspacing=0 cellpadding=5>

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

<h4>ListXmlFormats</h4>
<b>Sample request</b><br>

<br>

The following request lists the XML formats that may be disseminated from this service and their corresponding search keys:<br>

<br>

<a href="http://www.dlese.org/dds/services/ddsws1-0?verb=ListXmlFormats" target="_blank">http://www.dlese.org/dds/services/ddsws1-0?verb=ListXmlFormats</a><br>

<br>

<b>Summary and usage</b><br>

<br>

The ListXmlFormats request is used to discover the XML formats available from the repository as a whole or for a single record in the repository. Clients should use this request to discover the available XML formats and the keys that may be supplied in the 'xmlFormat' argument of the <a href="#Search">Search</a> or <a href="#GetRecord">GetRecord</a> requests.<br>

<br>

DDSWS is able to disseminate a number of XML formats including <a href="http://www.dlese.org/Metadata/adn-item/index.htm">ADN</a> (adn), <a href="http://www.dlese.org/Metadata/news-opps/index.htm">News&amp;Opps</a> (news_opps), <a href="http://www.dlese.org/Metadata/annotation/index.htm">DLESE annotation</a> (dlese_anno), <a href="http://www.dlese.org/Metadata/collection/index.htm">DLESE collection</a> (dlese_collect), <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#dublincore">OAI Dublin Core</a> (oai_dc), NSDL Dublin Core (nsdl_dc), and others.<br>

<br>

Certain records are available in multiple formats. For example, records that were originally cataloged in the ADN format may be returned in the adn, adn-localized, briefmeta, oai_dc, nsdl_dc, format. When a record is requested in a non-native format, it's XML is transformed to the requested format using XSLT or other transformation prior to being returned by the service.<br>

<br>

Many XML formats are available in namespace-specific form or a localized form that contains no namespace or schema declaration. Localized XML is indicated by adding -localized to the end of the XML format specifier, for example adn-localized. When localized XML is returned, the XML is generally easier to read and XPath notation is greatly simplified. By default, all requests in the service return localized versions of the metadata unless a non-localized specifier is indicated.<br>

<br>

<b>Arguments</b><br>

<br>

<ul>

<li>id - an <i>optional</i> argument that specifies an ID in the repository. If supplied the request will show only those XML formats that are available for that ID. If ommitted, the response will indicate all XML formats that are available in the repository.
</ul>

<br>

<b>Errors and exceptions</b><br>

<br>

Error is indicted if the id argument is supplied but the given ID is not in the repository.<br>

<br>

<b>Examples</b><br>

<br>

<i><b>Request</b></i><br>

<br>

Show all XML formats available for ID DLESE-000-000-000-001.<br>

<br>

<pre>http://www.dlese.org/dds/services/ddsws1-0?

             verb=ListXmlFormats&amp;id=DLESE-000-000-000-001</pre><br>

<i><b>Response</b></i><br>

<br>

<table border=1 cellspacing=0 cellpadding=5>

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

<h4>UrlCheck</h4>
<b>Sample request</b><br>

<br>

The following request searches for all records in the library that have a URL ending in '.pdf':<br>

<br>

<a href="http://www.dlese.org/dds/services/ddsws1-0?url=http://*.pdf&verb=UrlCheck" target="_blank">http://www.dlese.org/dds/services/ddsws1-0?url=http://.pdf&verb=UrlCheck</a><br>

<br>

<b>Summary and usage</b><br>

<br>

The UrlCheck request is used to check whether a given URL is in the DDS repository. This request supports the use of the * wildcard construct. The * character, or wildcard construct, indicates that <i>any</i> character combination is a valid match. For example, a search for http://www.dlese.org/myResource* will match the two URLs http://www.dlese.org/myResource1.html and http://www.dlese.org/myResource2.html. The wildcard construct may appear at any position in the URL argument <i>except</i> the first position. <br>

<br>

<b>Arguments</b><br>

<br>

<ul>

<li>url - a <i>required repeatable</i> argument that contains a URL. The url argument may be repeated as many times as desired within a single request.
</ul>

<br>

<b>Errors and exceptions</b><br>

<br>

Error is indicted if the request does not contain the URL argument or if the URL or URLs are not present in the repository.<br>

<br>

<b>Examples</b><br>

<br>

<i><b>Request</b></i><br>

<br>

Determine whether the URL 'http://epod.usra.edu/' is in the repository. Shown without the required <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#SpecialCharacters">encoding</a>, for clarity.<br>

<br>

<pre>http://www.dlese.org/dds/services/ddsws1-0?

    verb=UrlCheck&amp;url=http://epod.usra.edu/</pre><br>

<i><b>Response</b></i><br>

<br>

<table border=1 cellspacing=0 cellpadding=5>

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

<pre>http://www.dlese.org/dds/services/ddsws1-0?

   verb=UrlCheck&amp;url=http://epod.usra.edu/&amp;

   url=http://www.marsquestonline.org/index.html</pre><br>

<i><b>Response</b></i><br>

<br>

<table border=1 cellspacing=0 cellpadding=5>

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

<pre>http://www.dlese.org/dds/services/ddsws1-0?

         verb=UrlCheck&amp;url=http://www.dlese.org* </pre><br>

<i><b>Response</b></i><br>

<br>

<table border=1 cellspacing=0 cellpadding=5>

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

<pre>http://www.dlese.org/dds/services/ddsws1-0?

        verb=UrlCheck&amp;url=http://epod.usra.edu/zzzz </pre><br>

<i><b>Response</b></i><br>

<br>

<table border=1 cellspacing=0 cellpadding=5>

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

<h4>ServiceInfo</h4>
<b>Sample request</b><br>

<br>

The following request displays information about this Web service:<br>

<br>

<a href="http://www.dlese.org/dds/services/ddsws1-0?verb=ServiceInfo" target="_blank">http://www.dlese.org/dds/services/ddsws1-0?verb=ServiceInfo</a><br>

<br>

<b>Summary and usage</b><br>

<br>

The ServiceInfo requests is used to retrieve general information about this Web service including name, description, the URL used to access the service (base URL), service version and an administrator e-mail. <br>

<br>

<b>Arguments</b><br>

<br>

None<br>

<br>

<b>Errors and exceptions</b><br>

<br>

None<br>

<br>

<b>Examples</b><br>

<br>

<i><b>Request</b></i><br>

<br>

Display information about the Web service <br>

<br>

<pre>http://www.dlese.org/dds/services/ddsws1-0?verb=ServiceInfo</pre><br>

<i><b>Response</b></i><br>

<br>

<table border=1 cellspacing=0 cellpadding=5>

<tr><td><pre>

&lt;?xml version="1.0" encoding="UTF-8" ?&gt; 

&lt;DDSWebService&gt;

  &lt;ServiceInfo&gt;

    &lt;serviceName&gt;

      Digital Library for Earth System Education (DLESE) 

      discovery Web service

    &lt;/serviceName&gt;

    &lt;baseURL&gt;http://www.dlese.org/dds/services/ddsws1-0&lt;/baseURL&gt;

    &lt;serviceVersion&gt;1.0&lt;/serviceVersion&gt;

    &lt;adminEmail&gt;support@dlese.org&lt;/adminEmail&gt;

    &lt;compression&gt;gzip&lt;/compression&gt;

    &lt;description&gt; ... description here ... &lt;/description&gt;

  &lt;/ServiceInfo&gt;

&lt;/DDSWebService&gt;

</pre> </td></tr>
</table>

<br>

<br>

<a name="CommonElements"></a>

<h4>Common response elements</h4>
Several requests in the protocol share common XML elements in their responses. These include the &lt;head&gt; and &lt;additionalMetadata&gt; elements, which are described below.<br>

<br>

<a name="head"></a>

<i><b>The head element</b></i><br>

<br>

The head element appears in the <a href="#UserSearch">UserSearch</a>, <a href="#Search">Search</a>, <a href="#GetRecord">GetRecord</a>, <a href="#UrlCheck">UrlCheck</a> responses. The head element is used to return information about a single record. This includes the ID of the record, the collection in which the record is a member of, the XML format of the record, the date the record was last modified, the <a href="#whatsNewDate">whatsNewDate</a> and an <a href="#additionalMetadata">additionalMetadata</a> element. <br>

<br>

Head element example:<br>

<table border=1 cellspacing=0 cellpadding=5>

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

<table border=1 cellspacing=0 cellpadding=5>

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

<a name="availableSearchFields"></a>

<h3>Available search fields</h3>
This section describes the fields that are available for searching using the <a href="#Search">Search</a> and <a href="#UserSearch">UserSearch</a> requests,

as well as many of the commands. 

Fields may contain plain text, controlled vocabularies or encoded field values. The index contains fields derived from multiple XML metadata formats. A given field may contain metadata from a single format or from multiple formats as indicated below. Arbitrary Boolean searches may be performed across and within each of these fields using the <a href="http://jakarta.apache.org/lucene/docs/queryparsersyntax.html">Lucene Query Syntax</a> (LQS) supplied in the 'q' argument of the <a href="#Search">Search</a> request. <a href="#Example_search_queries">Example search queries</a> are provided below. <br>

<br>

Fields that may be sorted, as indicated, may be supplied in the 'sortAscendingBy' or 'sortDescendingBy' arguments of the <a href="#Search">Search</a> request.<br>

<br>

<b>Text fields</b> - These fields contain plain text or, where indicated, text that has been stemmed using the <a href="http://www.tartarus.org/~martin/PorterStemmer/">Porter stemmer algorithm</a>.  <br>

<br>

When searching in a text field, exact terms are matched. For example a search for <i>ocean</i> will return all documents that contain the exact term <i>ocean</i> in the given field. When searching in a field that has been stemmed, however, all documents containing morphologically similar terms in the given field will be returned. For example a search for <i>ocean</i> will return all documents that contain the terms <i>ocean, oceans</i> or <i>oceanic</i>. <br>

<br>

Note that when searching in a field that has been stemmed, the client must not apply the stemming algorithm to the terms. Stemming will be applied automatically by the search engine for the fields that support it - no pre-processing is necessary by the client.<br>

<br>

<ul>

<li>default - The default field represents the text that is most appropriate for searching by humans using natural language queries. The default field contains text extracted from different locations in the metadata depending on the XML format. The default field is the only field that does not require the use of a field specifier in the LQS query clause (e.g. the clause "default:ocean" and "ocean" are equivalent). May not be sorted. Available for formats:

<ul>

<li>adn: Includes title, description, keyword, resource type, subjects, event names, place names, temporal coverage names, terms found in the primary URL, and creators last name. May be sorted.
<li>dlese_collect: Includes full title, short title, description, subjects, keywords, and terms extracted from the primary URL, scope URL and review process URL.
<li>dlese_anno: Includes title, description and terms extracted from the URL.
<li>news_opps: Includes title, description, keywords, announcements, topics, audience, diversities, location, and sponsors institution.
</ul>
</ul>

<br>

<ul>

<li>stems - Identical to the default field, however all terms are in stemmed form. May not be sorted. 
</ul>

<br>

<ul>

<li>title - Contains the titles of resources or items, as text. May be sorted. Available for formats: adn, news_opps.
</ul>

<br>

<ul>

<li>titlestems - Contains the titles of resources or items, as stemmed text. May not be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>url - Contains the URL for the resource tokenized as text. Useful search clause examples include http*nasa.gov* or http*.edu*. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>description - Contains the descriptions of resources or items, as text. May be sorted. Available for formats: adn, dlese_collect, news_opps.
</ul>

<br>

<ul>

<li>keyword - Contains keywords associated with the resource or item, as text. May be sorted. Available for formats: adn, dlese_collect, news_opps.
</ul>

<br>

<ul>

<li>creator - Contains the first, middle and last name of each contributor for the resource. May not be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>organizationInstName - Contains the name of the contributing institution. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>organizationInstDepartment - Contains the name of the contributing institution's department. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>personInstName - Contains the name of the contributing person's institution. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>personInstDepartment - Contains the name of the contributing person's institutional department. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>emailPrimary - The primary contributor's e-mail. May be sorted. Available for formats: adn. 
</ul>

<br>

<ul>

<li>emailOrganization - The contributing organization's e-mail. May be sorted. Available for formats: adn. 
</ul>

<br>

<ul>

<li>emailAlt- The alternate contributor's e-mail. May be sorted. Available for formats: adn. 
</ul>

<br>

<ul>

<li>placeNames - Place names, for example "colorado," "AZ," "Brazil," as text. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>eventNames - Event names, for example "windstorm," "Destruction of Pompeii," as text. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>temporalCoverageNames - Temporal coverage names, for example "cambrian," "Triassic Period," as text. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>itemAudienceTypicalAgeRange - The typical age range for this resource. Available for formats: adn.
</ul>

<br>

<ul>

<li>itemAudienceInstructionalGoal - The instructional goals for this resource. Available for formats: adn.
</ul>

<br>

<ul>

<li>newsOppstitle - News & Opportunities title. May be sorted. Available for formats: news_opps.
</ul>

<br>

<ul>

<li>newsOppsdescription - News & Opportunities description. May be sorted. Available for formats: news_opps.
</ul>

<br>

<ul>

<li>newsOppskeyword - News & Opportunities keywords. May be sorted. Available for formats: news_opps.
</ul>

<br>

<b>Textual content</b> - These fields contain the text of the content of the resources themselves. These are available for all resources in the library whose primary content is in HTML or PDF. <br>

<br>

<ul>

<li>itemContent - The full textual content of the resource. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>itemContentTitle - The HTML title element text. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>itemContentHeaders - The HTML header element (H1, H2, etc.) text. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>itemContentType - The HTTP content type header terms that were returned by the Web server that holds the resource, for example "text html", "application pdf". May be sorted. Available for formats: adn.
</ul>

<br>

<b>Textual vocabulary fields</b> - These fields contain DLESE controlled vocabularies that have been indexed as plain text. <br>

<br>

<ul>

<li>gradeRange - The DLESE grade range vocabularies verbatim as text, for example "DLESE:Primary elementary." These values may be discovered using the <a href="#ListGradeRange">ListGradeRange</a> request within the vocabEntry element. May be sorted. Available for formats: adn, dlese_collect.
</ul>

<br>

<ul>

<li>resourceType - The DLESE resource type vocabularies verbatim as text, for example "DLESE:Learning materials:Classroom activity." These values may be discovered using the <a href="#ListResourceTypes">ListResourceTypes</a> request within the vocabEntry element. May be sorted. Available for formats: adn, dlese_collect.
</ul>

<br>

<ul>

<li>subject - The DLESE subject vocabularies verbatim as text, for example "DLESE:Atmospheric science." These values may be discovered using the <a href="#ListSubjects">ListSubjects</a> request within the vocabEntry element. May be sorted. Available for formats: adn, dlese_collect.
</ul>

<br>

<ul>

<li>contentStandard - The DLESE content standard vocabularies verbatim as text, for example "NSES:K-4:Unifying Concepts and Processes Standards:Change, constancy, and measurement." These values may be discovered using the <a href="#ListContentStandards">ListContentStandards</a> request within the vocabEntry element. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>itemAudienceBeneficiary - The intended audience beneficiary, for example "GEM:Bilingual students," as text. See "Beneficiaries" under <a href="http://www.dlese.org/Metadata/adn-item/0.6.50/docs/xml-info.htm#vocabs">http://www.dlese.org/Metadata/adn-item/0.6.50/docs/xml-info.htm#vocabs</a>. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>itemAudienceToolFor - The intended audience for this tool, for example "GEM:Middle school teachers," as text. See "Tool for educators" under <a href="http://www.dlese.org/Metadata/adn-item/0.6.50/docs/xml-info.htm#vocabs">http://www.dlese.org/Metadata/adn-item/0.6.50/docs/xml-info.htm#vocabs</a>. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>itemAudienceTeachingMethod - The intended audience teaching methods, for example "DLESE:Small group learning," as text. See "Teaching methods " under <a href="http://www.dlese.org/Metadata/adn-item/0.6.50/docs/xml-info.htm#vocabs">http://www.dlese.org/Metadata/adn-item/0.6.50/docs/xml-info.htm#vocabs</a>. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>itemannotypes - Indicates the type of annotation that this item has, for example "Teaching tip," "Information on challenging teaching and learning situations," as text. These values are shown in the <a href="http://www.dlese.org/Metadata/annotation/0.1.01/vocabs/type.xsd">types schema</a>. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>itemannostatus - Indicates the status of an annotation that this item has, for example "Text annotation completed," as text. These values are shown in the <a href="http://www.dlese.org/Metadata/annotation/0.1.01/vocabs/status.xsd">status schema</a>. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>itemannoformats - Indicates the format of an annotation that this item has. Values include 'text', 'audio', 'video' and 'graphical'.  May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>itemannopathways - Indicates the pathway of an annotation that this item has, for example "CRS (Community Review System)," as text. These values are shown in the <a href="http://www.dlese.org/Metadata/annotation/0.1.01/vocabs/pathway.xsd">pathway schema</a>. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>newsOppsannouncement - News & Opportunities announcements. May be sorted. Available for formats: news_opps.
</ul>

<br>

<ul>

<li>newsOppsaudience - News & Opportunities audience. May be sorted. Available for formats: news_opps.
</ul>

<br>

<ul>

<li>newsOppsdiversity - News & Opportunities diversity. May be sorted. Available for formats: news_opps.
</ul>

<br>

<ul>

<li>newsOppslocation - News & Opportunities locations. May be sorted. Available for formats: news_opps.
</ul>

<br>

<ul>

<li>newsOppstopic - News & Opportunities topics. May be sorted. Available for formats: news_opps.
</ul>

<br>

<b>Encoded vocabulary fields</b> - These fields contain DLESE controlled vocabularies that have encoded into keys. Corresponding textual vocabulary fields are listed above, e.g. the same information is indexed both as text and as keys for these fields: gr - gradeRanges; re - resourceTypes; su - subjects; cs - contentStandards.<br>

<br>

<ul>

<li>gr - The DLESE grade range vocabularies encoded as a two or three character key, for example "05." These values may be discovered using the <a href="#ListGradeRanges">ListGradeRanges</a> request within the searchKey element. May be sorted. Available for formats: adn. 
</ul>

<br>

<ul>

<li>re - The DLESE resource type vocabularies encoded as a two or three character key, for example "05." These values may be discovered using the <a href="#ListResourceTypes">ListResourceTypes</a> request within the searchKey element. May be sorted. Available for formats: adn. 
</ul>

<br>

<ul>

<li>su - The DLESE subject vocabularies encoded as a two or three character key, for example "05." These values may be discovered using the <a href="#ListSubjects">ListSubjects</a> request within the searchKey element. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>cs - The DLESE content standard vocabularies encoded as a two or three character key, for example "05." These values may be discovered using the <a href="#ListContentStandards">ListContentStandards</a> request within the searchKey element. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>ky - The DLESE collections vocabularies encoded as a two or three character key, for example "05." These values may be discovered using the <a href="#ListCollections">ListCollections</a> request within the searchKey element. May be sorted. Available for formats: adn.
</ul>

<br>

<b>Defined key fields</b> - These fields contain finite sets of key values such as "true" or "false" that may be used to limit searches to a sub-set of records.<br>

<br>

<ul>

<li>itemhasanno - Indicates whether an item has an annotation. Values are either "true" or "false." May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>partofdrc - Indicates whether the item or collection is part of the DLESE Reviewed Collection (DRC). Values are either "true" or "false." May be sorted. Available for formats: adn, dlese_collect.
</ul>

<br>

<ul>

<li>multirecord - Indicates whether the resource that the record catalogs is also cataloged by other records in other collections. Values are either "true" or "false." May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>collection - Contains the record's collection vocabulary entry pre-pended with a 0, for example "0dcc," "0comet.". These values may be discovered using the <a href="#ListCollections">ListCollections</a> request within the vocabEntry element. May be sorted. Available for all formats.
</ul>

<br>

<ul>

<li>wntype - Indicates the reason the item is new to the library, corresponding to the 'wndate' field. Possible values are: itemnew, itemannocomplete, itemannoinprogress, annocomplete, drcannocomplete, drcannoinprogress, collection. May be sorted. Available for all formats.
</ul>

<br>

    <b>Fields available for searching by value or range of value </b> - These fields may be searched by exact value or by range of value:<br>

<br>

<ul>

<li>itemannoaveragerating - Contains the average of all star ratings assigned to a given resource. Values range from 1.000 to 5.000. 

Example search syntax <em>itemannoaveragerating:[3.500 TO 5.000]</em> - returns all resources with an average star rating of 3.5 to 5.0. May be sorted. Available for formats:adn.
</ul>

<br>

<ul>

<li>itemannoratingvalues - Contains all star ratings assigned to a given resource. Values range from 1 to 5. 

Example search syntax <em>itemannoratingvalues:[3 TO 5]</em> - returns all resources that have one or more ratings of 3, 4, or 5 stars assigned to them. May be sorted. Available for formats:adn.
</ul>

<br>

<ul>

<li>itemannonumratings - Contains the number of star ratings that have been assigned to a given resource. Values are encoded to 5 digits, for example <em>00000</em> or <em>00014</em>. 

Example search syntax <em>itemannonumratings:[00004 TO 99999]</em> - returns all resources  that have from 4 to 99999 star ratings assigned to them. May be sorted. Available for formats:adn.
</ul>

<br>

<ul>

<li>annorating - Contains the star rating of a given annotation record. Values are integers from 1 to 5. 

Example search syntax <em>annorating:[3 TO 5]</em> - returns all annotations that have a start rating of 3 to 5. May be sorted. Available for formats: dlese_anno.
</ul>

<br>

<b>Fields available for searching by date</b> - These fields may be supplied in the 'dateField' argument of the <a href="#Search">Search</a> request:<br>

<br>

<ul>

<li>wndate - A date field that indicates the date the item was new to the library, corresponding to the 'wntype' field. May be sorted. Available for all formats.
</ul>

<br>

<ul>

<li>accessiondate - The ADN accession date for the record. May be sorted. Available for formats: adn.
</ul>

<br>

<ul>

<li>collaccessiondate - The dlese_collect accession date for the collection. May be sorted. Available for formats: dlese_collect.
</ul>

<br>

<ul>

<li>modtime - A date field that corresponds to the time the items file was last modified or touched. This does not necessarily indicate that the content in the record changed. May be sorted. Available for all formats.
</ul>

<br>

<ul>

<li>newsOppsapplyBydate - News & Opportunities applyBy date. May be sorted. Available for formats: news_opps.
</ul>

<br>

<ul>

<li>newsOppsarchivedate - News & Opportunities archive date. May be sorted. Available for formats: news_opps.
</ul>

<br>

<ul>

<li>newsOppsduedate - News & Opportunities due date. May be sorted. Available for formats: news_opps.
</ul>

<br>

<ul>

<li>newsOppseventStartdate - News & Opportunities eventStart date. May be sorted. Available for formats: news_opps.
</ul>

<br>

<ul>

<li>newsOppseventStopdate - News & Opportunities eventStop date. May be sorted. Available for formats: news_opps.
</ul>

<br>

<ul>

<li>newsOppspostdate - News & Opportunities post date. May be sorted. Available for formats: news_opps.
</ul>

<br>

<ul>

<li>newsOppsrecordCreationdate - News & Opportunities recordCreation date. May be sorted. Available for formats: news_opps.
</ul>

<br>

<ul>

<li>newsOppsrecordModifieddate - News & Opportunities recordModified date. May be sorted. Available for formats: news_opps.
</ul>

<br>

<br>

<a name="Example_search_queries" id="Example_search_queries"></a>

<h4>Example search queries</h4>
<p>
This section shows some examples of performing searches using the <a href="#Search">Search</a> or <a href="#UserSearch">UserSearch</a> request. To perform these searches, the values shown below should be supplied in the 'q' argument, using the <a href="http://jakarta.apache.org/lucene/docs/queryparsersyntax.html">Lucene Query Syntax</a> (LQS). Additional arguments may be supplied to the Search or UserSearch request to further limit the search, such as xmlFormat, dateField and the vocabulary fields gr, su, re and cs.</p>

<p>Search for the term 'ocean' in the default field: 
  <blockquote>     ocean    </blockquote>
  <p>Search for the term 'ocean' in the stems field. This will return documents containing morphologically similar terms including <i>ocean, oceans</i> and <i>oceanic</i></p>
  <blockquote>    stems:ocean</blockquote>
  <p>Search for resources that that have an average star rating of 3.5 to 5.0:</p>
  <blockquote> itemannoaveragerating:[3.500 TO 5.000]</blockquote>
  <p>    Search for resources that contain  'noaa.gov' in their URL:</p>
  <blockquote> url:http*noaa.gov*</blockquote>
  <p>
    
    Search for the term ocean within resources from 'noaa.gov':   </p>
  <blockquote>     url:http*noaa.gov* AND ocean</blockquote>
  <p>Search for term 'estuary' in the stems field, and limit the search to subject biological oceanography (subject key 02):</p>
  <blockquote> stems:estuary AND su:02</blockquote>
  <p>    Search for the term 'ocean' in the default field, and boost the ranking of results that contain 'ocean' in their title (stemmed) (uses the special clause allrecords:true to select the set of all records). Note that this clause returns the same number of results as if the search were performed  only over the word 'ocean' in the default field, but it applies additional boosting for records that contain the term 'ocean' in their title (stemmed), which augments the page rank of the results that are returned. This example illustrates the kind of page rank augmentation that is applied automatically in the UserSearch request.</p>
  <blockquote>    ocean AND (allrecords:true OR titlestems:ocean^2)</blockquote>
  <p>Show all records with subject biological oceanography, and boost results that contain florida in the title (stemmed), description or placeNames fields (uses the clause allrecords:true to select the set of all records):</p>
  <blockquote> su:02 AND (allrecords:true OR titlestems:florida*^20 
    
    <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;    OR description:florida*^20 OR placeNames:florida^20)</blockquote>
  <h3>Glossary</h3>
    <a name="whatsNewDate"></a>

<b>whatsNewDate</b> - A date that describes when an item was new to the library. Generally this corresponds to the item's accession date or the date in which the item first became accessible in the library.<br>

<br>





</div></td>

</tr></tbody></table>

  <!-- InstanceEndEditable -->

<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    
    


</body>
<!-- InstanceEnd --></html>


