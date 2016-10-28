<?xml version="1.0" encoding="UTF-8" ?>
<!-- DLESE front page view of the What's New RSS items -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" omit-xml-declaration="yes" />			
	<xsl:template match="/rss/channel">	
		<ul>
			<xsl:for-each select="item">		
				<xsl:variable name="itemLink">
					<xsl:value-of select="link" />
				</xsl:variable>			
				<li>
					<xsl:call-template name="highlightItem" />
					<a>
						<xsl:attribute name="href">
							<xsl:value-of select="$itemLink" />
						</xsl:attribute>
						<xsl:if test="not( contains( $itemLink, 'dlese.org'  ) )">
							<xsl:attribute name="target">_blank</xsl:attribute>
						</xsl:if>					
						<xsl:value-of disable-output-escaping="yes" select="title" />
					</a>
					<xsl:call-template name="highlightItem">
						<xsl:with-param name="closing">true</xsl:with-param>
					</xsl:call-template>
					<!-- xsl:if test="description">
						- <xsl:value-of disable-output-escaping="yes" select="description"/>
					</xsl:if -->
				</li>
			</xsl:for-each>
		</ul>
	</xsl:template>
	<xsl:template name="highlightItem">
		<xsl:param name="closing" />
		<!-- Render items in a category that contains the word 'HIGHLIGHTED' as bold: -->
		<xsl:for-each select="category">
			<xsl:if test="contains( ., 'HIGHLIGHTED' )">
				<xsl:choose>
					<xsl:when test="$closing = 'true'">
						<xsl:text disable-output-escaping="yes">&lt;/b&gt;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text disable-output-escaping="yes">&lt;b&gt;</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
