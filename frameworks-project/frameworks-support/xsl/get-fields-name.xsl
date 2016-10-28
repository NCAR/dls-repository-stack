<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:d="http://www.dlsciences.org/frameworks/fields"
    exclude-result-prefixes="xsi d" 
    version="1.0">

<!--2008-05-21: modified d namespace from http://www.dlese.org/Metadata/fields to http://www.dlsciences.org/frameworks/fields-->
<!--2005-04-18: modified d namespace from http://metadatafields.dlese.org to http://www.dlese.org/Metadata/fields-->
<!--creation date: 2004-12-06-->

	<xsl:output method="text" omit-xml-declaration="yes" />

<!--	<xsl:template match="d:metadataFieldInfo">
			&lt;field&gt;<xsl:value-of select="d:field/@name"/>&lt;/field&gt;
	</xsl:template>			-->

	<xsl:template match="d:metadataFieldInfo">
			<!--writes text of the format ':fieldname:xpath:'-->
			<!--by using xsl:text whitespace in the output file is eliminated-->
			<xsl:value-of select="d:field/@name"/>
			<xsl:text>:</xsl:text>
			<xsl:value-of select="d:field/@path"/>
	</xsl:template>

</xsl:stylesheet>
