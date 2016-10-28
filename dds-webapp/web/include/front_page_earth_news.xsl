<?xml version="1.0" encoding="UTF-8" ?>
<!-- DLESE front page view of the Earth News RSS items -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" omit-xml-declaration="yes" />			
	<xsl:template match="/rss/channel">	
		<ul>
			<xsl:for-each select="item">		
				<!-- Restrict our output to the first 5 items -->
				<xsl:if test="position() &lt; 6">
					<xsl:variable name="itemLink">
						<xsl:value-of select="link" />
					</xsl:variable>			
					<li>
						<a>
							<xsl:attribute name="href">
								<xsl:value-of select="$itemLink" />
							</xsl:attribute>
							<!-- Target a new window when links go outside dlese.org -->
							<xsl:if test="not( contains( $itemLink, 'dlese.org'  ) )">
								<xsl:attribute name="target">_blank</xsl:attribute>
							</xsl:if>					
							<xsl:value-of disable-output-escaping="yes" select="title" />
						</a>
					</li>
				</xsl:if>
			</xsl:for-each>
		</ul>
	</xsl:template>
</xsl:stylesheet>
