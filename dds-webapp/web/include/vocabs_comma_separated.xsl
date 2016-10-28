<?xml version="1.0" encoding="UTF-8"?>
<!-- Transform an OPML document into a comma-seperated list of the outline "text" attribute values -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" omit-xml-declaration="yes" />	

	<xsl:template match="/opml">	
		<xsl:variable name="commaList">
			<xsl:apply-templates select="body"/>
		</xsl:variable>
		<xsl:if test="string-length( $commaList ) > 0"><!-- Trim off the tailing comma -->
			<xsl:copy-of select="substring( $commaList, 0, string-length( $commaList ) - 1 )"/>
		</xsl:if>
	</xsl:template>	
	
	<xsl:template match="body">	
		<xsl:apply-templates select="outline"/>			
	</xsl:template>
	
	<xsl:template match="outline">
		<xsl:if test="not( outline ) and not( contains( @display, 'false' ) )"><!-- Don't render grouping sub-headers for this view -->
			<xsl:choose>
				<!-- Display "textBrief" attribute if it is available... -->
				<xsl:when test="@textBrief"><xsl:value-of select="@textBrief"/>, </xsl:when>
				<!-- ...otherwise default to displaying "text" -->
				<xsl:otherwise><xsl:value-of select="@text"/>, </xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:apply-templates select="outline"/>
	</xsl:template>	

</xsl:stylesheet>
