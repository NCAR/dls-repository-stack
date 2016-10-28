<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:d="http://adn.dlese.org"
    exclude-result-prefixes="d xsi"
    version="1.0">

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:variable name="newline">
<xsl:text>
</xsl:text>
	</xsl:variable>

	<xsl:template match="d:itemRecord">
		<xsl:text disable-output-escaping="yes">&lt;itemRecord xmlns=&quot;http://adn.dlese.org&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xsi:schemaLocation=&quot;http://adn.dlese.org http://www.dlese.org/Metadata/adn-item/0.7.00/record.xsd&quot;&gt;</xsl:text>
		<xsl:value-of select="$newline"/>

<!--		<xsl:copy-of select="*|/"/>-->
<!-- can't do this *|/ because the schema line with version 0.6.50 gets copied over as well; so must do each individual section-->
<!--		<xsl:copy-of select="d:itemRecord"/>-->
<!-- can't do this d:itemRecord because on the line itemRecord is copied with no other elements; so must do each individual section-->

		<xsl:copy-of select="d:general"/>
		<xsl:copy-of select="d:lifecycle"/>
		<xsl:copy-of select="d:metaMetadata"/>
		<xsl:copy-of select="d:technical"/>
		<xsl:copy-of select="d:educational"/>
		<xsl:copy-of select="d:rights"/>
		<xsl:copy-of select="d:relations"/>
		<xsl:copy-of select="d:geospatialCoverages"/>
		<xsl:copy-of select="d:temporalCoverages"/>
		<xsl:copy-of select="d:objectsInSpace"/>
		
		<xsl:value-of select="$newline"/>
		<xsl:text disable-output-escaping="yes">&lt;/itemRecord&gt;</xsl:text>
	</xsl:template>

</xsl:stylesheet>
