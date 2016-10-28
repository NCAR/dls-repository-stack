<?xml version="1.0" encoding="utf-8"?>

<!--

  XSL Transform to convert OAI 2.0 responses into XHTML

  By Christopher Gutteridge, University of Southampton

-->

<!-- 
  
Copyright (c) 2000-2004 University of Southampton, UK. SO17 1BJ.

EPrints 2 is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

EPrints 2 is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with EPrints 2; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

-->

   
<!--
  
  All the elements really needed for EPrints are done but if
  you want to use this XSL for other OAI archive you may want
  to make some minor changes or additions.

  Not Done
    The 'about' section of 'record'
    The 'compession' part of 'identify'
    The optional attributes of 'resumptionToken'
    The optional 'setDescription' container of 'set'

  All the links just link to oai_dc versions of records.

-->
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:oai="http://www.openarchives.org/OAI/2.0/"
>

<xsl:output method="html"/>

<xsl:template name="style">
	<!-- Provided by external stylesheet -->
<xsl:call-template name='xmlstyle' />

</xsl:template>

<xsl:variable name='identifier' select="substring-before(concat(substring-after(/oai:OAI-PMH/oai:request,'identifier='),'&amp;'),'&amp;')" />

<xsl:template match="/">
    <p class="intro">You are viewing an HTML version of the XML OAI response.</p>
    <xsl:apply-templates select="/oai:OAI-PMH" />
</xsl:template>

<xsl:template match="/oai:OAI-PMH">

  <xsl:choose>
    <xsl:when test="oai:error">
      <h3 class="oaiH3">OAI Error(s)</h3>
      <p>The request could not be completed due to the following error or errors.</p>
      <div class="results">
        <xsl:apply-templates select="oai:error"/>
      </div>
    </xsl:when>
    <xsl:otherwise>
      <div class="results">
        <xsl:apply-templates select="oai:Identify" />
        <xsl:apply-templates select="oai:GetRecord"/>
        <xsl:apply-templates select="oai:ListRecords"/>
        <xsl:apply-templates select="oai:ListSets"/>
        <xsl:apply-templates select="oai:ListMetadataFormats"/>
        <xsl:apply-templates select="oai:ListIdentifiers"/>
      </div>
	  <!-- <p>Request was of type <xsl:value-of select="oai:request/@verb"/>.</p> -->
    </xsl:otherwise>
  </xsl:choose>
  <!-- <p>
  <table border="0" cellpadding="0" cellspacing="1" class="values">
    <tr class="bodyrow"><td class="key">Datestamp of response</td>
    <td class="value"><xsl:value-of select="oai:responseDate"/></td></tr>
    <tr class="bodyrow"><td class="key">Request URL</td>
    <td class="value"><xsl:value-of select="oai:request"/></td></tr>
  </table>
  verb: [<xsl:value-of select="oai:request/@verb" />]<br />
  </p> -->
</xsl:template>


<!-- ERROR -->

<xsl:template match="/oai:OAI-PMH/oai:error">
  <table border="0" cellpadding="0" cellspacing="1" class="values">
    <tr class="bodyrow"><td class="key">Error Code</td>
    <td class="value"><xsl:value-of select="@code"/></td></tr>
  </table>
  <p class="error"><xsl:value-of select="." /></p>
</xsl:template>

<!-- IDENTIFY -->

<xsl:template match="/oai:OAI-PMH/oai:Identify">
  <table border="0" cellpadding="0" cellspacing="1" class="values">
    <tr class="bodyrow"><td class="key">Repository Name</td>
    <td class="value"><xsl:value-of select="oai:repositoryName"/></td></tr>
    <tr class="bodyrow"><td class="key">Base URL</td>
    <td class="value"><xsl:value-of select="oai:baseURL"/></td></tr>
    <tr class="bodyrow"><td class="key">Protocol Version</td>
    <td class="value"><xsl:value-of select="oai:protocolVersion"/></td></tr>
    <tr class="bodyrow"><td class="key">Earliest Datestamp</td>
    <td class="value"><xsl:value-of select="oai:earliestDatestamp"/></td></tr>
    <tr class="bodyrow"><td class="key">Deleted Record Policy</td>
    <td class="value"><xsl:value-of select="oai:deletedRecord"/></td></tr>
    <tr class="bodyrow"><td class="key">Granularity</td>
    <td class="value"><xsl:value-of select="oai:granularity"/></td></tr>
    <xsl:apply-templates select="oai:adminEmail"/>
  </table>
  <xsl:apply-templates select="oai:description"/>
<!--no warning about unsupported descriptions -->
</xsl:template>

<xsl:template match="/oai:OAI-PMH/oai:Identify/oai:adminEmail">
    <tr class="bodyrow"><td class="key">Admin Email</td>
    <td class="value"><xsl:value-of select="."/></td></tr>
</xsl:template>

<!--
   Identify / Unsupported Description
-->

<xsl:template match="oai:description/*" priority="-100">
  <h3 class="oaiH3">Description</h3>
  <p>Description XML (unknown format):</p>
  <div class="xmlSource">
    <xsl:apply-templates select="." mode='xmlMarkup' />
  </div>
</xsl:template>


<!--
   Identify / OAI-Identifier
-->

<xsl:template match="id:oai-identifier" xmlns:id="http://www.openarchives.org/OAI/2.0/oai-identifier">
  <h3 class="oaiH3">OAI-Identifier</h3>
  <table border="0" cellpadding="0" cellspacing="1" class="values">
    <tr class="bodyrow"><td class="key">Scheme</td>
    <td class="value"><xsl:value-of select="id:scheme"/></td></tr>
    <tr class="bodyrow"><td class="key">Repository Identifier</td>
    <td class="value"><xsl:value-of select="id:repositoryIdentifier"/></td></tr>
    <tr class="bodyrow"><td class="key">Delimiter</td>
    <td class="value"><xsl:value-of select="id:delimiter"/></td></tr>
    <tr class="bodyrow"><td class="key">Sample OAI Identifier</td>
    <td class="value"><xsl:value-of select="id:sampleIdentifier"/></td></tr>
  </table>
</xsl:template>


<!--
   Identify / EPrints
-->

<xsl:template match="ep:eprints" xmlns:ep="http://www.openarchives.org/OAI/1.1/eprints">
  <h3 class="oaiH3">EPrints Description</h3>
  <h3 class="oaiH3">Content</h3>
  <xsl:apply-templates select="ep:content"/>
  <xsl:if test="ep:submissionPolicy">
    <h3 class="oaiH3">Submission Policy</h3>
    <xsl:apply-templates select="ep:submissionPolicy"/>
  </xsl:if>
  <h3 class="oaiH3">Metadata Policy</h3>
  <xsl:apply-templates select="ep:metadataPolicy"/>
  <h3 class="oaiH3">Data Policy</h3>
  <xsl:apply-templates select="ep:dataPolicy"/>
  <xsl:if test="ep:content">
    <h3 class="oaiH3">Content</h3>
    <xsl:apply-templates select="ep:content"/>
  </xsl:if>
  <xsl:apply-templates select="ep:comment"/>
</xsl:template>

<xsl:template match="ep:content|ep:dataPolicy|ep:metadataPolicy|ep:submissionPolicy" xmlns:ep="http://www.openarchives.org/OAI/1.1/eprints">
  <xsl:if test="ep:text">
    <p><xsl:value-of select="ep:text" /></p>
  </xsl:if>
  <xsl:if test="ep:URL">
    <div><a href="{ep:URL}" target="_blank"><xsl:value-of select="ep:URL" /></a></div>
  </xsl:if>
</xsl:template>

<xsl:template match="ep:comment" xmlns:ep="http://www.openarchives.org/OAI/1.1/eprints">
  <h3 class="oaiH3">Comment</h3>
  <div><xsl:value-of select="."/></div>
</xsl:template>


<!--
   Identify / Friends
-->

<xsl:template match="fr:friends" xmlns:fr="http://www.openarchives.org/OAI/2.0/friends/">
  <h3 class="oaiH3">Friends</h3>
  <ul>
    <xsl:apply-templates select="fr:baseURL"/>
  </ul>
</xsl:template>

<xsl:template match="fr:baseURL" xmlns:fr="http://www.openarchives.org/OAI/2.0/friends/">
  <li><xsl:value-of select="."/> 
<xsl:text> </xsl:text>
<a class="link" href="{.}?verb=Identify" target="_blank">Identify</a></li>
</xsl:template>


<!--
   Identify / Branding
-->

<xsl:template match="br:branding" xmlns:br="http://www.openarchives.org/OAI/2.0/branding/">
  <h3 class="oaiH3">Branding</h3>
  <xsl:apply-templates select="br:collectionIcon"/>
  <xsl:apply-templates select="br:metadataRendering"/>
</xsl:template>

<xsl:template match="br:collectionIcon" xmlns:br="http://www.openarchives.org/OAI/2.0/branding/">
  <h3 class="oaiH3">Icon</h3>
  <xsl:choose>
    <xsl:when test="link!=''">
      <a href="{br:link}" target="_blank"><img src="{br:url}" alt="{br:title}" width="{br:width}" height="{br:height}" border="0" /></a>
    </xsl:when>
    <xsl:otherwise>
      <img src="{br:url}" alt="{br:title}" width="{br:width}" height="{br:height}" border="0" />
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="br:metadataRendering" xmlns:br="http://www.openarchives.org/OAI/2.0/branding/">
  <h3 class="oaiH3">Metadata Rendering Rule</h3>
  <table border="0" cellpadding="0" cellspacing="1" class="values">
    <tr class="bodyrow"><td class="key">URL</td>
    <td class="value"><xsl:value-of select="."/></td></tr>
    <tr class="bodyrow"><td class="key">Namespace</td>
    <td class="value"><xsl:value-of select="@metadataNamespace"/></td></tr>
    <tr class="bodyrow"><td class="key">Mime Type</td>
    <td class="value"><xsl:value-of select="@mimetype"/></td></tr>
  </table>
</xsl:template>



<!--
   Identify / Gateway
-->

<xsl:template match="gw:gateway" xmlns:gw="http://www.openarchives.org/OAI/2.0/gateway/x">
  <h3 class="oaiH3">Gateway Information</h3>
  <table border="0" cellpadding="0" cellspacing="1" class="values">
    <tr class="bodyrow"><td class="key">Source</td>
    <td class="value"><xsl:value-of select="gw:source"/></td></tr>
    <tr class="bodyrow"><td class="key">Description</td>
    <td class="value"><xsl:value-of select="gw:gatewayDescription"/></td></tr>
    <xsl:apply-templates select="gw:gatewayAdmin"/>
    <xsl:if test="gw:gatewayURL">
      <tr class="bodyrow"><td class="key">URL</td>
      <td class="value"><xsl:value-of select="gw:gatewayURL"/></td></tr>
    </xsl:if>
    <xsl:if test="gw:gatewayNotes">
      <tr class="bodyrow"><td class="key">Notes</td>
      <td class="value"><xsl:value-of select="gw:gatewayNotes"/></td></tr>
    </xsl:if>
  </table>
</xsl:template>

<xsl:template match="gw:gatewayAdmin" xmlns:gw="http://www.openarchives.org/OAI/2.0/gateway/">
  <tr class="bodyrow"><td class="key">Admin</td>
  <td class="value"><xsl:value-of select="."/></td></tr>
</xsl:template>


<!-- GetRecord -->

<xsl:template match="oai:GetRecord">
  <xsl:apply-templates select="oai:record" />
</xsl:template>

<!-- ListRecords -->

<xsl:template match="oai:ListRecords">
  <xsl:apply-templates select="oai:record" />
  <xsl:apply-templates select="oai:resumptionToken" />
</xsl:template>

<!-- ListIdentifiers -->

<xsl:template match="oai:ListIdentifiers">
  <xsl:apply-templates select="oai:header" />
  <xsl:apply-templates select="oai:resumptionToken" />
</xsl:template>

<!-- ListSets -->

<xsl:template match="oai:ListSets">
  <p>This is a list of sets available from this archive with links to view the Identifiers or Records in oai_dc format.</p>
  <xsl:apply-templates select="oai:set" />
  <xsl:apply-templates select="oai:resumptionToken" />
</xsl:template>

<xsl:template match="oai:set">
  <h3 class="oaiH3">Set</h3>
  <table border="0" cellpadding="0" cellspacing="1" class="values">
    <tr class="bodyrow"><td class="key">setName</td>
    <td class="value"><xsl:value-of select="oai:setName"/></td></tr>
    <xsl:apply-templates select="oai:setSpec" />
  </table>
</xsl:template>

<!-- ListMetadataFormats -->

<xsl:template match="oai:ListMetadataFormats">
  <xsl:choose>
    <xsl:when test="$identifier">
      <p>This is a list of metadata formats available for the record "<xsl:value-of select='$identifier' />". Use these links to view the metadata: <xsl:apply-templates select="oai:metadataFormat/oai:metadataPrefix" /></p>
    </xsl:when>
    <xsl:otherwise>
      <p>This is a list of metadata formats available from this archive.</p>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:apply-templates select="oai:metadataFormat" />
</xsl:template>

<xsl:template match="oai:metadataFormat">
  <h3 class="oaiH3">Metadata Format</h3>
  <table border="0" cellpadding="0" cellspacing="1" class="values">
    <tr class="bodyrow"><td class="key">metadataPrefix</td>
    <td class="value"><xsl:value-of select="oai:metadataPrefix"/></td></tr>
    <tr class="bodyrow"><td class="key">metadataNamespace</td>
    <td class="value"><xsl:value-of select="oai:metadataNamespace"/></td></tr>
    <tr class="bodyrow"><td class="key">schema</td>
    <td class="value"><a href="{oai:schema}" target="_blank"><xsl:value-of select="oai:schema"/></a></td></tr>
  </table>
</xsl:template>

<xsl:template match="oai:metadataPrefix">
      <xsl:text> </xsl:text><a class="link" href="?verb=GetRecord&amp;metadataPrefix={.}&amp;identifier={$identifier}" target="_blank"><xsl:value-of select='.' /></a>
</xsl:template>

<!-- record object -->

<xsl:template match="oai:record">
  <h3 class="oaiRecordTitle">OAI Record: <xsl:value-of select="oai:header/oai:identifier"/></h3>
  <div class="oaiRecord">
    <xsl:apply-templates select="oai:header" />
    <xsl:apply-templates select="oai:metadata" />
    <xsl:apply-templates select="oai:about" />
  </div>
</xsl:template>

<xsl:template match="oai:header">
  <h3 class="oaiH3">OAI Record Header</h3>
  <table border="0" cellpadding="0" cellspacing="1" class="values">
    <tr class="bodyrow"><td class="key">OAI Identifier</td>
    <td class="value">
      <xsl:value-of select="oai:identifier"/>
      <xsl:text> </xsl:text><a class="link" href="?verb=GetRecord&amp;metadataPrefix=oai_dc&amp;identifier={oai:identifier}" target="_blank">oai_dc</a>
      <xsl:text> </xsl:text><a class="link" href="?verb=ListMetadataFormats&amp;identifier={oai:identifier}" target="_blank">formats</a>
    </td></tr>
    <tr class="bodyrow"><td class="key">Datestamp</td>
    <td class="value"><xsl:value-of select="oai:datestamp"/></td></tr>
  <xsl:apply-templates select="oai:setSpec" />
  </table>
  <xsl:if test="@status='deleted'">
    <p>This record has been deleted.</p>
  </xsl:if>
</xsl:template>


<xsl:template match="oai:about">
  <p>"about" part of record container not supported by the XSL</p>
</xsl:template>

<xsl:template match="oai:metadata">
  &#160;
  <div class="metadata">
    <xsl:apply-templates select="*" />
  </div>
</xsl:template>


<!-- oai setSpec object -->

<xsl:template match="oai:setSpec">
  <tr class="bodyrow"><td class="key">setSpec</td>
  <td class="value"><xsl:value-of select="."/>
    <xsl:text> </xsl:text><a class="link" target="_blank" href="{/oai:OAI-PMH/oai:request}?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set={.}">Identifiers</a>
    <xsl:text> </xsl:text><a class="link" target="_blank" href="{/oai:OAI-PMH/oai:request}?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set={.}">Records</a>
  </td></tr>
</xsl:template>


<!-- oai resumptionToken -->

<xsl:template match="oai:resumptionToken">
   <p>There are more results.</p>
   <table border="0" cellpadding="0" cellspacing="1" class="values">
     <tr class="bodyrow"><td class="key">resumptionToken:</td>
     <td class="value"><xsl:value-of select="."/>
<xsl:text> </xsl:text>
<a class="link" href="?verb={/oai:OAI-PMH/oai:request/@verb}&amp;resumptionToken={.}" target="_blank">Resume</a></td></tr>
   </table>
</xsl:template>

<!-- unknown metadata format -->

<xsl:template match="oai:metadata/*" priority='-100'>
  <h3 class="oaiH3">Unknown Metadata Format</h3>
  <div class="xmlSource">
    <xsl:apply-templates select="." mode='xmlMarkup' />
  </div>
</xsl:template>

<!-- oai_dc record -->

<xsl:template match="oai_dc:dc"  xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" >
  <div class="dcdata">
    <h3 class="oaiH3">Dublin Core Metadata (oai_dc)</h3>
    <table border="0" cellpadding="0" cellspacing="1" class="dcdata">
      <xsl:apply-templates select="*" />
    </table>
  </div>
</xsl:template>

<xsl:template match="dc:title" xmlns:dc="http://purl.org/dc/elements/1.1/">
<tr class="bodyrow"><td class="key">Title</td><td class="value"><xsl:value-of select="."/></td></tr></xsl:template>

<xsl:template match="dc:creator" xmlns:dc="http://purl.org/dc/elements/1.1/">
<tr class="bodyrow"><td class="key">Author or Creator</td><td class="value"><xsl:value-of select="."/></td></tr></xsl:template>

<xsl:template match="dc:subject" xmlns:dc="http://purl.org/dc/elements/1.1/">
<tr class="bodyrow"><td class="key">Subject and Keywords</td><td class="value"><xsl:value-of select="."/></td></tr></xsl:template>

<xsl:template match="dc:description" xmlns:dc="http://purl.org/dc/elements/1.1/">
<tr class="bodyrow"><td class="key">Description</td><td class="value"><xsl:value-of select="."/></td></tr></xsl:template>

<xsl:template match="dc:publisher" xmlns:dc="http://purl.org/dc/elements/1.1/">
<tr class="bodyrow"><td class="key">Publisher</td><td class="value"><xsl:value-of select="."/></td></tr></xsl:template>

<xsl:template match="dc:contributor" xmlns:dc="http://purl.org/dc/elements/1.1/">
<tr class="bodyrow"><td class="key">Other Contributor</td><td class="value"><xsl:value-of select="."/></td></tr></xsl:template>

<xsl:template match="dc:date" xmlns:dc="http://purl.org/dc/elements/1.1/">
<tr class="bodyrow"><td class="key">Date</td><td class="value"><xsl:value-of select="."/></td></tr></xsl:template>

<xsl:template match="dc:type" xmlns:dc="http://purl.org/dc/elements/1.1/">
<tr class="bodyrow"><td class="key">Resource Type</td><td class="value"><xsl:value-of select="."/></td></tr></xsl:template>

<xsl:template match="dc:format" xmlns:dc="http://purl.org/dc/elements/1.1/">
<tr class="bodyrow"><td class="key">Format</td><td class="value"><xsl:value-of select="."/></td></tr></xsl:template>

<xsl:template match="dc:identifier" xmlns:dc="http://purl.org/dc/elements/1.1/">
<tr class="bodyrow"><td class="key">Resource Identifier</td><td class="value"><xsl:value-of select="."/></td></tr></xsl:template>

<xsl:template match="dc:source" xmlns:dc="http://purl.org/dc/elements/1.1/">
<tr class="bodyrow"><td class="key">Source</td><td class="value"><xsl:value-of select="."/></td></tr></xsl:template>

<xsl:template match="dc:language" xmlns:dc="http://purl.org/dc/elements/1.1/">
<tr class="bodyrow"><td class="key">Language</td><td class="value"><xsl:value-of select="."/></td></tr></xsl:template>

<xsl:template match="dc:relation" xmlns:dc="http://purl.org/dc/elements/1.1/">
<tr class="bodyrow"><td class="key">Relation</td><td class="value">
  <xsl:choose>
    <xsl:when test='starts-with(.,"http" )'>
      <xsl:choose>
        <xsl:when test='string-length(.) &gt; 50'>
          <a class="link" href="{.}" target="_blank">URL</a>
          <i> URL not shown as it is very long.</i>
        </xsl:when>
        <xsl:otherwise>
          <a href="{.}" target="_blank"><xsl:value-of select="."/></a>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="."/>
    </xsl:otherwise>
  </xsl:choose>
</td></tr></xsl:template>

<xsl:template match="dc:coverage" xmlns:dc="http://purl.org/dc/elements/1.1/">
<tr class="bodyrow"><td class="key">Coverage</td><td class="value"><xsl:value-of select="."/></td></tr></xsl:template>

<xsl:template match="dc:rights" xmlns:dc="http://purl.org/dc/elements/1.1/">
<tr class="bodyrow"><td class="key">Rights Management</td><td class="value"><xsl:value-of select="."/></td></tr></xsl:template>

<!-- XML Pretty Maker -->

<xsl:template match="node()" mode='xmlMarkup'>
  <div class="xmlBlock">
    &lt;<span class="xmlTagName"><xsl:value-of select='name(.)' /></span><xsl:apply-templates select="@*" mode='xmlMarkup'/>&gt;<xsl:apply-templates select="node()" mode='xmlMarkup' />&lt;/<span class="xmlTagName"><xsl:value-of select='name(.)' /></span>&gt;
  </div>
</xsl:template>

<xsl:template match="text()" mode='xmlMarkup'><span class="xmlText"><xsl:value-of select='.' /></span></xsl:template>

<xsl:template match="@*" mode='xmlMarkup'>
  <xsl:text> </xsl:text><span class="xmlAttrName"><xsl:value-of select='name()' /></span>="<span class="xmlAttrValue"><xsl:value-of select='.' /></span>"
</xsl:template>

<xsl:template name="xmlstyle">
.xmlSource {
	font-size: 70%;
	border: solid #c0c0a0 1px;
	background-color: #ffffe0;
	padding: 2em 2em 2em 0em;
}
.xmlBlock {
	padding-left: 2em;
}
.xmlTagName {
	color: #800000;
	font-weight: bold;
}
.xmlAttrName {
	font-weight: bold;
}
.xmlAttrValue {
	color: #0000c0;
}
</xsl:template>

</xsl:stylesheet>

