<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>


<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>

<html>
<head>
	<title>ODL Search Service Documentation</title>
	
	<%-- template header (CSS styles and JavaScript references) --%>
	<%@ include file="/nav/head.jsp" %> 

	

</head>

<body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066">

	<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   ODL Search <br/>Service
</div>
	

<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>


<h1>ODL Search Service Documentation</h1>

</p>
<p>The DDS <a href="http://oai.dlib.vt.edu/odl/" target="_blank">Open Digital Library</a> (ODL) Search Service is an API that extends the <a href="http://openarchives.org/" target="_blank">Open Archives Initiative Protocol for Metadata Harvesting</a> (OAI-PMH) to allow for textual searching of the DDS content. This service is similar, but not equivalent, to the <A 
href="http://oai.dlib.vt.edu/odl/">Open Digital Library (ODL)</A> search specification (odlsearch1). </p>
<p>Note that the DDS ODL Search Service is offered here for compatibility with existing applications that use the API, however developers should consider using the <a href="../ddsws1-1/index.jsp">DDS Search Web Service</a> (DDSWS), which offers additional features, for new application development. </p>
<p>The service allows clients to use the OAI-PMH to perform keyword and fielded Information Retrieval (IR) search queries over the metadata repository. Because ODL search (and OAI in general) leverages existing Internet technologies such as HTTP, it may be defined as a REpresentational State Transfer (REST) style web service. After issuing a search request, clients receive raw metadata back as an ordered set of results within the standard <A 
href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords" target="_blank">ListRecords</A> or<A 
href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers" target="_blank"> ListIdentifiers</A> response containers. Results are ordered by relevance, which is determined by relative term frequency and proximity. The metadata may then be used to render custom interfaces or be embedded in remote clients on the fly.</p>
<h3>Search query syntax</h3>


  <P>To perform a search, clients <EM>must</EM> provide a search query in the set argument of either a <A 
href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords" target="_blank">ListRecords</A> or<A 
href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers" target="_blank"> ListIdentifiers</A> request. The set argument <EM>must</EM> conform to the following syntax: dleseodlsearch/[query string]/[set]/[offset]/[length] where "dleseodlsearch" is the exact string dleseodlsearch, "query string" indicates a list of keywords upon which to search, "set" indicates the set over which to search, "offset" indicates the offset into the results list to begin the results, and "length" indicates the total number of results to return. To search over all sets, clients <EM>must</EM> supply the string "null" in the set field. Clients <EM>must</EM> escape all spaces in queries with a plus (+) symbol. The default boolean logic is AND. To request a query using boolean OR, clients <EM>must</EM> supply the exact string "OR" between each term in the query string.</P>
  <P>Examples: </P>
  <P>"BASE_URL?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=dleseodlsearch/ocean/null/0/10" - Performs a text search for the term "ocean" across all sets in the repository, returning matching results numbers 0 through 10.</P>
  <P>"BASE_URL?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=dleseodlsearch/ocean/null/10/10" - Performs a text search for the word "ocean" across all sets in the repository, returning matching results numbers 10 through 20.</P>
  <P>"BASE_URL?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=dleseodlsearch/ocean+weather/dcc/0/10" - Performs a text search for the terms "ocean" AND "weather" across the set dcc, returning matching results numbers 0 through 10.</P>
  <P>"BASE_URL?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=dleseodlsearch/ocean+OR+weather/dcc/0/10" - Performs a text search for the terms "ocean" OR "weather" across the set dcc, returning matching results numbers 0 through 10.</P>
  <P>Tip: See the <a href="service_explorer.jsp">ODL Search Service Explorer</a> to issue requests and view the XML response in your web browser. </P>


<h3>Flow control</h3>
<P>All state is embedded in the search query string, giving clients control over response flow. Clients can "page through" a set of results by issuing the same request in succession and incrementing the offset parameter by the desired quanta. For example, a client wishing to iterate through three pages of results for a search on the term ocean, retrieving ten records per page, would issue the following three queries:</P>
<P>"BASE_URL?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=dleseodlsearch/ocean/null/0/10"</P>
<P>"BASE_URL?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=dleseodlsearch/ocean/null/10/10"</P>
<P>"BASE_URL?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=dleseodlsearch/ocean/null/20/10"</P>
<P>At the end of each response an empty resumptionToken element is provided for the client that contains the attributes completeListSize and cursor. The completeListSize attribute shows the total number of records in the repository that match the given query. The cursor reflects the offset into the result set where the current response container begins.</P>

<c:if test="${isDeploayedAtDL}">
		<h3>Contact us </h3>
			<p>If you have questions or comments regarding this service, please send e-mail to <a href="mailto:support@dlese.org">support@dlese.org</a>.</p>
</c:if>
	
<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    


</body>
</html>


