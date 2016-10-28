<?xml version="1.0" encoding="UTF-8"?>
<!-- DLESE front page view of the latest Resources of Interest RSS item -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" omit-xml-declaration="yes" />	
	<xsl:param name="itemIndex">1</xsl:param>
	<xsl:template match="/rss/channel">	
		<xsl:for-each select="item">	
			<xsl:if test="position() = $itemIndex">
				<h3><a>
					<xsl:attribute name="href">
						<xsl:value-of select="link" />
					</xsl:attribute>
					<xsl:value-of disable-output-escaping="yes" select="title" />
				</a></h3>
				<xsl:value-of disable-output-escaping="yes" select="description" />
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
