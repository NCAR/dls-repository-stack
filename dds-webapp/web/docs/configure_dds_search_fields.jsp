<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html><head>	
		<title>DDS Software: Configure Search</title>

		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<META NAME="description" CONTENT="This page describes how to configure standard and custom 
		search fields for a given XML framework in a Digital Discovery System (DDS) repository. 
		This information is provided for system administrators who are installing and managing a 
		DDS repository system, which includes the Digital Discovery System (DDS), 
		the Digital Catalog System (DCS) and the NSDL Catalog System (NCS).">		
				 
		<%@ include file="/nav/head.jsp" %>
		
	    </head>
	
<body>
<c:if test="${isDeploayedAtDL}">
	<div class="dlese_sectionTitle" id="dlese_sectionTitle">
	   DDS Search<br/>Fields
	</div>
	<div id="ddsLogo">
		<a href="${pageContext.request.contextPath}/dds_overview.jsp"><img border="0" alt="Digital Discovery System (DDS)" title="Digital Discovery System (DDS)" src="${pageContext.request.contextPath}/images/dds_logo_sm.gif"/></a>
	</div>
</c:if>
<%@ include file="/nav/top.jsp" %>

<div class="bodyContent">
	<h1>Configuring Search Fields, Facets, and Relationships</h1>
	
	<p>&nbsp;</p>
	<p>These instructions describe how to configure <em>standard</em> and <em>custom</em> search fields, <em>facet categories</em>, and <em>relationships</em>  for any XML framework that is made available through the <a href="${pageContext.request.contextPath}/services/ddsws1-1/service_specification.jsp">Search  API</a>. This information is provided for system administrators who are installing or managing a DDS repository system, which includes the <a href="${pageContext.request.contextPath}/services/dds_software.jsp">Digital Discovery System</a> (DDS) and the <a href="${pageContext.request.contextPath}/services/dcs_software.jsp">Digital Collection System</a> (DCS). While it is not necessary to configure a framework in order for it to be used effectively in the repository, doing so adds additional search functionality that may be useful.</p>
	<p>This document assumes familiarity with Apache Tomcat, Lucene, servlet configurations, and XML. </p>
	<h3>How search fields, facets, and relationships are generated </h3>
	<p>At index creation time, each record is inserted in the repository in it's native XML format. The indexer extracts <em>standard</em>, <em>custom</em> and <em>XPath</em> search fields and <em>facet categories</em> from the contents of the XML, establishes any relevant <em>relationships</em>, then generates a single entry containing each of the fields, facet categories, and data from related records and inserts it into the index. All records are guaranteed to contain certain fields such as the <code>default</code> and <code>stems</code> fields, as well as XPath fields for the native XML record and its related records, which are created automatically. </p>
	<p>For detailed information about search fields and the content within them, see the <a href="${pageContext.request.contextPath}/services/ddsws1-1/service_specification.jsp#availableSearchFields">Search Service documentation</a> (Search fields section). </p>
	<h2>How to configure search fields, facets, and relationships</h2>
	<p>Each XML framework in the DDS can have a corresponding configuration file that is used to define  <em>standard</em> and <em>custom</em> search fields, <em>facet categories</em>, and <em>relationships</em> for that framework. <em>Standard</em> search fields include title, description, ID, URL and geospatial bounding box coordinates. <em>Custom</em> search fields and <em>facet categories</em> can be defined for any content extracted from the XML document and/or it's related documents, and <em>relationships</em> can be defined to establish relations that connect records in one XML framework  with another for the purpose of optimized searching.</p>
	<p>&nbsp;</p>
	<p>To configure a specific XML framework, follow these  steps:</p>
	<ol>
	  <li>Add the given XML framework to the search fields configuration index file</li>
	  <li>Create a configuration file for the XML framework and define the <em>standard</em> and <em>custom</em> search fields, <em>facet categories</em>, and <em>relationships</em> as needed</li>
	  <li>Start or re-start Tomcat for changes to take place </li>
	</ol>
	<h3>1. Add XML frameworks to the configuration index file</h3>
	<p>Add the given XML framework to the search fields configuration index file, which contains a list of the individual configurations  files for each XML framework. Entries in the index may contain relative or absolute <a href="http://java.sun.com/j2se/1.5.0/docs/api/java/net/URI.html">URIs</a> to the individual framework configuration files that may be located on the local file system (file://) or anywhere on the Web (http://). </p>
	<p>The index file is named <code>xmlIndexerFieldsConfigIndex.xml</code> and in a typical DDS installation it can be found in the tomcat context at <code>$tomcat/$context/WEB-INF/conf/xmlIndexerFieldsConfigIndex.xml.</code> The exact location of the index file is indicated by the DDS/DCS/NCS web application's context-param <code>repositoryConfigDir</code> (found in web.xml or server.xml). </p>
	<p>Example index file:</p>
	<pre>
&lt;?xml version="1.0" encoding="ISO-8859-1"?&gt;
&lt;XMLIndexerFieldsConfigIndex&gt;
<span class="comment">	&lt;!-- List the location of each framework-specific configuration file --&gt;</span>
	&lt;configurationFiles&gt;
		&lt;configurationFile&gt;xmlIndexerFieldsConfigs/oai_dc_search_fields.xml&lt;/configurationFile&gt;
		&lt;configurationFile&gt;xmlIndexerFieldsConfigs/my_framework_search_fields.xml&lt;/configurationFile&gt;
	&lt;/configurationFiles&gt;		
&lt;/XMLIndexerFieldsConfigIndex&gt;
	</pre>
	<p>Each <code>configurationFile</code> element indicates a relative or absolute URI to the individual configuration for the XML framework. The above example points to two framework configuration files, <code>oai_dc_search_fields.xml</code> and <code>my_framework_search_fields.xml</code>, which reside in the directory <code>xmlIndexerFieldsConfigs</code> relative to the index configuration file. </p>
	<h3>2. Define  search fields, facets, and relationships for each XML framework </h3>
	<p>Each configuration file describes the <em>standard</em> and/or <em>custom</em> search fields and <em>facet categories</em> for an XML framework and where the content for those fields reside in the XML instance documents, as well as <em>relationships</em> across XML frameworks in the repository. For the following discussion, see the <a href="#config">example configuration file below</a>. </p>
	<p>The <code>xmlFormat</code> or <code>schema</code> attribute of the <code>XMLIndexerFieldsConfig</code> element defines which framework the configuration is for, and only one or the other may be used. The xmlFormat corresponds to the XML format key that the repository system is indexing, for example <code>oai_dc, nsdl_dc, comm_anno, adn,</code> etc. To provide a schema-specific configuration, for example if a given repository is working with two versions of the same framework, indicate the schema location in the <code>schema</code> attribute. If there are two configurations that operate over the same framework, one indicated by <code>xmlFormat</code> and the other <code>schema</code>, the schema definition takes precedence. </p>
	<h4>Standard search fields</h4>
	<p>Standard search fields are processed by the indexer in a uniform manner, allowing clients to search the fields in a consistent manner across frameworks. </p>
	<p>The standard fields are the following:</p>
	<table width="600px" border="0" cellpadding="6" cellspacing="1" class="bgTable">
      <tr>
        <th scope="col">Standard Search Field </th>
        <th scope="col">Description</th>
        <th scope="col">Index Fields Generated </th>
      </tr>
      <tr>
        <td><code>id</code></td>
        <td>Contains the ID for the record. If not defined, the ID is derived automatically by the indexer. </td>
        <td><code>idvalue</code></td>
      </tr>
      <tr>
        <td><code>url</code></td>
        <td>Contains  the URL for the resource described by the XML metadata. </td>
        <td><code>url</code></td>
      </tr>
      <tr>
        <td><code>title</code></td>
        <td>Contains  the title text for the item. </td>
        <td><code>title, titlestems </code></td>
      </tr>	  
      <tr>
        <td><code>description</code></td>
        <td>Contains the description text for the item. </td>
        <td><code>description, descriptionstems</code></td>
      </tr>
      <tr>
        <td><code>geoBBNorth, geoBBSouth, geoBBWest, geoBBEast</code></td>
        <td>Contains the north and south latitudes [-90, 90] and the west and east longitudes  [-180, 180] for the geographic bounding box footprint that represents this item. </td>
        <td>n/a - Handled internally by  the Search request. </td>
      </tr>
    </table>
	<p>&nbsp;</p>
	<p>To configure a standard search field for a framework, add a <code>standardField</code> element in the configuration field as shown in the example below. The attribute <code>name</code> defines the standard field name (id, url, title, etc). Inside <code>standardField</code>, nested <code>xpath</code> elements should contain XPaths that select the desired content. The <code>xpath</code> element can be repeated and the contents of <em>all</em> repeated elements in the instance documents will be included in the content for that field with the exception of the geographic bounding box fields, which must contain a single element only.</p>
	<h4>Custom fields and facet categories</h4>
	<p>Custom fields and facet categories can be  defined for any content extracted from the XML document.</p>
	<p>To define a custom field or facet category, add a <code>customField</code> element in the configuration field as shown in the example below. Then to define a regular custom field, create an attribute named <code>name</code> or to define a facet category instead, create and attribute named <code>facetCategory</code>. Add additional attributes as needed, which vary depending on whether a regular custom field or facet category is being defined as described in the table below.</p>
	<p>Attributes that may appear on the <code>customField</code> element:</p>
	<table width="600px" border="0" cellpadding="6" cellspacing="1" class="bgTable">
      <tr>
        <th scope="col">Attribute Name</th>
        <th scope="col">Description</th>
        <th scope="col">Valid Values </th>
        <th scope="col">Use in Conjunction With <code>name</code> or <code>facetCategory</code></th>
      </tr>
      <tr>
        <td><code>name</code> or <code>facetCategory</code></td>
        <td>Indicates the name of the custom search field or facet category that is being defined. Use the <code>name</code> attribute  to define a custom field or the <code>facetCategory</code> attribute to define a facet category. One or the other must be indicated but not both.</td>
        <td>The value of the <code>name</code> or <code>facetCategory</code> attribute should contain alpha-numeric characters without spaces.</td>
        <td>n/a</td>
      </tr>
      <tr>
        <td><code>store</code></td>
        <td>Indicates whether to store the content in the index. Stored fields are visible in the admin pages of the DDS/DCS/NCS repository system web application. </td>
        <td><code>yes, no </code></td>
        <td><code>name</code></td>
      </tr>
      <tr>
        <td><code>type</code></td>
        <td>Indicates the type of field this should be. If <code>type</code> is used, <code>analyzer</code> must not be. </td>
        <td><p><code>text</code> - Text is processed using the Lucene <a href="http://lucene.apache.org/java/1_4_3/api/org/apache/lucene/analysis/standard/StandardAnalyzer.html">StandardAnalyzer</a>.</p>
        <p><code>stems</code> - Text is processed using the Lucene <a href="http://lucene.apache.org/java/2_2_0/api/org/apache/lucene/analysis/snowball/SnowballAnalyzer.html">SnowballAnalyzer</a> for the english language.</p>
        <p><code>key</code> - Text is processed using the Lucene KeywordAnalyzer, which is case-sensitive and includes the entire element or attribute as a single token. </p></td>
        <td><code>name</code></td>
      </tr>	  
      <tr>
        <td><code>analyzer</code></td>
        <td>Indicates the specific Lucene Analyzer to use when processing this field. If <code>analyzer</code> is used, <code>type</code> must not be. </td>
        <td>Include the fully-qualified Java class that implements a Lucene Analyzer. The class must be in the classpath of the DDS web application. </td>
        <td><code>name</code></td>
      </tr>
      <tr>
        <td><code>indexFieldPreprocessor</code></td>
        <td><em>(Optional)</em> Indicates a concrete instance of <code>IndexFieldPreprocessor</code> that should be used to preprocess the content of this field prior to indexing. </td>
        <td>Indicate  the fully-qualified Java class that implements the <code><a href="javadoc/org/dlese/dpc/repository/indexing/IndexFieldPreprocessor.html">edu.ucar.dls.repository.indexing.IndexFieldPreprocessor</a></code> Interface. The class must be in the classpath of the DDS web application. Omit this attribute if no preprocessing is to be done.</td>
        <td><code>name</code> or <code>facetCategory</code></td>
      </tr>
      <tr>
        <td><code>facetPathDelimeter</code></td>
        <td>(Optional) Indicates a delimiter character used to split the input string into a facet path hierarchy. <br>
Examples: facetPathDelimeter=&quot;:&quot; to split on colon; facetPathDelimeter=&quot;/&quot; to split on a backward slash. <br>
If omitted, the facet category will be flat (e.g. one level deep only)</td>
        <td>A single character, for example <code>:</code> or <code>/</code></td>
        <td><code>facetCategory</code></td>
      </tr>
    </table>
<p>&nbsp; </p>
	<p>Note that the Lucene Analyzer that is defined for a given field is automatically applied both in the indexer and the searcher. </p>
	<p>Inside <code>customField</code>, nested <code>xpath</code> elements should contain XPaths to the content. The <code>xpath</code> element can be repeated and the contents of <em>all</em> repeated elements in the instance documents will be included in the content for that field.</p>
	<h4>Relationships</h4>
    <p>Relationships for a given XML framework can be defined that will connect the records written in that framework with other records in the repository. Related records my be connected by either record ID or  URL, as defined by the standard <code>id</code> and <code>url</code> fields for the target framework. When a relationship is established, the target record takes on searchable fields from the source record and acquires the given relationship, thereby establishing a link from the target record to the source record with the given relationship name. In addition, the <code>Search</code> and <code>GetRecord</code> API requests can return the related records in the same response, making them quickly and easily accessible. </p>
    <p>For example, an annotation framework might define the relationship <code>isAnnotatedBy</code> that attaches the annotation record with the relationship <code>isAnnotatedBy</code> to other records in the repository that contain a given  URL. The target records with that URL then acquire searchable XPath fields and optionally custom fields from the annotation record. Say for example that an annotation record were to contain a user-defined tag named<em> &quot;top pick.&quot;</em> It would then be possible to construct a <code>Search</code> query to return all the records in the repository that contain the given URL and that have been tagged, by way of the user annotation, as a <em>&quot;top pick,&quot;</em> and to retrieve in the same service response not only the resource metadata records but also the associated annotation record(s).</p>
    <p>To define a relationship for a given XML framework, add a <code>relationships</code> element with nested <code>relationship</code> elements in the configuration file as shown in the example below. For each <code>relationship</code> element there must be a <code>name</code> attribute that contains the name of the relationship. Nested inside the <code>relationship</code> element must be one <code>xpaths</code> element with one or more nested <code>xpath</code> elements. Each <code>xpath</code> element must contain an attribute <code>type</code> with the value of either <code>id</code> or <code>url</code>. The content of the <code>xpath</code> element must contain an xpath to the ID or URL within the source record that will be used to connect the target record to the source.</p>
<h4>XPaths</h4>
	<p>As the indexer processes the XML records,  it first removes  namespaces from the documents. This simplifies the XPath notation necessary to select the desired elements and attributes within. Therefore, do not include namespaces in your XPath notation.</p>
	<p>To specify the content elements that should be pulled from an  <code>oai_dc</code> Dublin Core record, for example, these XPaths would be used to select the given elements:</p>
	<ul>
	  <li><code>/dc/title</code> - Selects <em>all</em> title elements that are children to the dc element </li>
      <li><code>/dc/title[1]</code> - Selects <em>the first</em> title element that is a child to the dc element</li>
      <li><code>//title</code> - Selects <em>all</em> title elements <em>anywhere</em> in the XML document  </li>
  </ul>
	<p>It is also possible to pull in custom field content from related documents, e.g. a document that is associated with the one being indexed by way of a relation. To specify custom field 
content that should be pulled from a related document, add an XPath in the framework configuration file 
that starts with the relation prefix specifier (e.g. '<code>/relation.isAnnotatedBy/</code>') followed by the XPath into the 
related document to the content. </p>
	<p>For example, the following notation would be used to index content for the given record from all <code>comm_anno</code> records that assign the <code>isAnnotatedBy</code> relation:</p>
	<ul>
	  <li><code>/relation.isAnnotatedBy//comm_anno/ASNstandard</code></li>
  </ul>
<p>For more information about the XPath language, see <a href="http://www.w3.org/TR/xpath">XPath Language 1.0</a> and the <a href="http://www.zvon.org/xxl/XPathTutorial/General/examples.html">ZVON XPath Tutorial</a>.</p>
	<p><a name="config"></a></p>
  <p>Example search configuration for the <code>oai_dc</code> Dublin Core  framework:</p>
	<pre>
&lt;?xml version=&quot;1.0&quot; encoding=&quot;ISO-8859-1&quot;?&gt;
<span class="comment">&lt;!-- XMLIndexerFieldsConfig attributes: [xmlFormat OR schema] --&gt;</span>
&lt;XMLIndexerFieldsConfig xmlFormat=&quot;oai_dc&quot;&gt;
	&lt;standardFields&gt;
		<span class="comment">&lt;!-- standardField attributes include: 
			name=[id|url|title|description|geoBBNorth|geoBBSouth|geoBBWest|geoBBEast] --&gt;</span>
		&lt;standardField name=&quot;url&quot;&gt;
			&lt;xpaths&gt;
				&lt;xpath&gt;/dc/identifier&lt;/xpath&gt;
			&lt;/xpaths&gt;		
		&lt;/standardField&gt;
		&lt;standardField name=&quot;title&quot;&gt;
			&lt;xpaths&gt;
				&lt;xpath&gt;/dc/title&lt;/xpath&gt;
			&lt;/xpaths&gt;		
		&lt;/standardField&gt;
		&lt;standardField name=&quot;description&quot;&gt;
			&lt;xpaths&gt;
				&lt;xpath&gt;/dc/description&lt;/xpath&gt;
			&lt;/xpaths&gt;		
		&lt;/standardField&gt;	
	&lt;/standardFields&gt;
	&lt;customFields&gt;
		<span class="comment">&lt;!-- customField attributes include: [name OR facetCategory], [store], [type OR analyzer], 
        		[indexFieldPreprocessor], [facetCategory] --&gt;</span>
		
		<span class="comment">&lt;!-- Regular custom fields (use the name attribute) --&gt;</span>
		&lt;customField name=&quot;dcIdentifier&quot; store=&quot;yes&quot; type=&quot;key&quot;&gt;
			&lt;xpaths&gt;
				&lt;xpath&gt;/dc/identifier&lt;/xpath&gt;
			&lt;/xpaths&gt;
		&lt;/customField&gt;		
		&lt;customField name=&quot;dcType&quot; store=&quot;yes&quot; type=&quot;text&quot;&gt;
			&lt;xpaths&gt;
				&lt;xpath&gt;/dc/type&lt;/xpath&gt;
			&lt;/xpaths&gt;
		&lt;/customField&gt;
		&lt;customField name=&quot;dcMySubjectTags&quot; store=&quot;yes&quot; analyzer=&quot;org.example.MySubjectTagAnalyzer&quot; 
        			indexFieldPreprocessor=&quot;org.example.MySubjectTagIndexFieldPreprocessor&quot;&gt;
			&lt;xpaths&gt;
				&lt;xpath&gt;/dc/subject&lt;/xpath&gt;
			&lt;/xpaths&gt;
		&lt;/customField&gt;

<span class="comment">		&lt;!-- Facet category fields (use the facetCategory attribute) --&gt;</span>
		&lt;customField facetCategory=&quot;dcTypeFacets&quot;&gt; <br>			&lt;xpaths&gt;<br>				&lt;xpath&gt;/dc/type&lt;/xpath&gt;<br>			&lt;/xpaths&gt;		<br>		&lt;/customField&gt;
		
<span class="comment">		&lt;!-- Index standards found in comm_anno records that annotate this oai_dc record --&gt;</span>
		&lt;customField name=&quot;ASNIDFromAnno&quot; store=&quot;yes&quot; type=&quot;key&quot;&gt; <br>			&lt;xpaths&gt;<br>				&lt;xpath&gt;/relation.isAnnotatedBy//comm_anno/ASNstandard&lt;/xpath&gt;<br>			&lt;/xpaths&gt;		<br>		&lt;/customField&gt;
	&lt;/customFields&gt;

	<span class="comment">&lt;!-- Relationships that this format of record defines. --&gt;</span>	<br>	&lt;relationships&gt;<br>		<span class="comment">&lt;!-- 	Relationship of the target record to this.<br>		<br>				The given relationship name will be attached to the target record, not this record.<br>				<br>				Examples:<br>					target 'isAnnotatedBy' this<br>					target 'isRelatedTo' this<br>					etc.<br>					<br>				attributes: name=[relationship name] --&gt;</span><br>				<br>		<span class="comment">&lt;!--	Xpath where the source record's ID or URL is stored<br>				attribute type=[id|url] defines whether to look in the target record's <em>id</em> or <em>url</em> field
				to make the relationship.  --&gt;</span>				<br>		&lt;relationship name=&quot;isRelatedTo&quot;&gt;<br>			&lt;xpaths&gt;<br>				&lt;xpath type=&quot;url&quot;&gt;/dc/relation&lt;/xpath&gt;<br>			&lt;/xpaths&gt;		<br>		&lt;/relationship&gt;			<br>	&lt;/relationships&gt;

&lt;/XMLIndexerFieldsConfig&gt;	
	</pre>
	
<h2>How to verify it's working</h2>
    <p>Follow these  steps to verify that the desired content is being indexed for search as expected:</p>
    <ol>
      <li>Place the configuration files in the repository system and make sure Tomcat has been restarted.</li>
      <li>Index or re-index the files. </li>
      <li>Use the <a href="${pageContext.request.contextPath}/services/ddsws1-1/service_specification.jsp#ListFields"><code>ListFields</code></a> and <a href="${pageContext.request.contextPath}/services/ddsws1-1/service_specification.jsp#ListTerms"><code>ListTerms</code></a> service requests to verify that the fields are appearing in the index. <em>Standard</em> and <em>custom</em> fields should appear under the name for which they are defined. <em>XPath</em> fields should appear in the <a href="${pageContext.request.contextPath}/services/ddsws1-1?verb=ListFields"><code>ListFields response</code></a> with the field prefix of <code>/key//[xpath], /stems//[xpath], /text//[xpath]</code>.</li>
      <li>Facet categories should appear in the <code>ListTerms</code> <a href="${pageContext.request.contextPath}/services/ddsws1-1?field=$facets&verb=ListTerms"><code>$facets field</code></a> response.</li>
      <li>Established relationships  should appear in the <code>ListTerms</code> <a href="${pageContext.request.contextPath}/services/ddsws1-1?field=indexedRelations&verb=ListTerms"><code>indexedRelations</code></a> response. Relation XPath fields should appear in the <a href="${pageContext.request.contextPath}/services/ddsws1-1?verb=ListFields"><code>ListFields response</code></a>  with the field prefix of <code>/relation.[relationshipName]//key//[xpath], /relation.[relationshipName]//stems//[xpath], /relation.[relationshipName]//text//[xpath]</code>.</li>
      <li>Perform searches using the Search  API or admin search pages to verify the expected results are returned for specific queries against known data in one or more records. </li>
    </ol>
<p>&nbsp;</p>
    <p>Last revised: $Date: 2016/04/15 20:06:18 $</p>
</div>

<%@ include file="/nav/bottom.jsp" %>
</body>
</html>
