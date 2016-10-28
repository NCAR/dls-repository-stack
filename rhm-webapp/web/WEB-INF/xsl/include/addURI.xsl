<?xml version="1.0" encoding="UTF-8"?>
<!-- =============================================================  
 "addURI" stylesheet 

add URI schema encoding to elements if it's not already present, and if it's appropriate.  

	Also 
		don't output anything if element is empty
		get rid of excess whitespace in value with normalize-space
		don't output extra namespace declarations
 
Author:  Naomi Dushay 
updated: 05/12/20 add www. and url: as valid cases (will be corrected to proper URI in java post-processing)
updated: 03/08/15 don't convert spaces to %20 - java routine will scrub these
updated: 03/07/09 correct value when it starts httphttp or http:http or http://http
updated: 03/07/08 only replace spaces with %20 if it's a URI value
updated: 03/06/23 non-default port fix;  add double slash ...
updated: 03/06/03 add telnet, use more elegant code from addDCMIType
updated: 02/11/22 add https, ftp and gopher for valid cases
updated: 02/11/20 add named template 
Created: 02/11/19  Naomi Dushay
================================================================ -->
<xsl:stylesheet version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    >
     
	<xsl:include href="include/replaceString.xsl"/>

	<xsl:template match="*" mode="addURI">
		<!-- ensure element has a value -->
		<xsl:variable name="normSpace" select="normalize-space(.)"/>
		<xsl:if test="string($normSpace)">
			<xsl:variable name="lowercase" select="translate($normSpace, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>

			<xsl:element name="{name()}" namespace="{namespace-uri()}">

				<xsl:choose>
					<!-- URI encoding scheme already specified -->
					<xsl:when test="self::node()[@xsi:type='dct:URI']">
						<xsl:apply-templates select="@*|node()" mode="addURI"/>
					</xsl:when>

					<!-- add URI encoding scheme if appropriate -->
					
					<!-- NOTE:  a better way to do this would be to use an xsl extension function to call our 
					java routine util.URIutil.scrubURL(), which does a better job of determining if it's a URL
					and ensuring the output is a working URL. There are similar methods to see if it's a URI
					instead, for expediency in getting done with this code, we use this stylesheet and then scrub the
					designated URIs afterwards -->
					
					<!-- NOTE: URI can be absolute or relative URL (http, ftp, gopher, telnet, etc), URN ... XML schema will accept virtually any value
					so we are only adding it for some obvious cases.  Also we do not check for "http://" b/c of oft occurring typos such as
					"httphttp" or "http://https"-->

					<!-- NOTE: it is not valid to start "www." but that will be corrected to "http://www." by post-processing
					 nor it is not valid to start "url: blah" but that will be corrected to "blah" by post-processing -->
					<xsl:when test="starts-with($lowercase, 'http') or
									starts-with($lowercase, 'https') or
									starts-with($lowercase, 'www.') or
									starts-with($lowercase, 'url:') or
									starts-with($lowercase, 'ftp://') or
									starts-with($lowercase, 'telnet://') or
									starts-with($lowercase, 'gopher://')">
						<xsl:attribute name="xsi:type">dct:URI</xsl:attribute>
						<xsl:apply-templates select="@*|node()" mode="addURI"/>
					</xsl:when>
					
					<!-- we didn't find match to add URI -->
					<xsl:otherwise>
						<xsl:apply-templates select="@* | * | comment() | processing-instruction()" mode="addURI"/>
						<xsl:apply-templates select="text()" mode="withoutURI"/>
					</xsl:otherwise>

				</xsl:choose>

			</xsl:element>
		</xsl:if>
	</xsl:template>

	
	<xsl:template name="outputURIvalue" match="text()" mode="addURI">
		<xsl:param name="value" select="."/>

		<!-- deal with known typos -->
		<xsl:variable name="normSpace" select="normalize-space($value)"/>
		<xsl:variable name="lowercase" select="translate($normSpace, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>
		<xsl:variable name="outputValue">
			<xsl:choose>
				<xsl:when test="starts-with($lowercase, 'http://http')">
					<xsl:value-of select="substring($normSpace, 8)"/>
				</xsl:when>
				<xsl:when test="starts-with($lowercase, 'http:http')">
					<xsl:value-of select="substring($normSpace, 6)"/>
				</xsl:when>
				<xsl:when test="starts-with($lowercase, 'httphttp')">
					<xsl:value-of select="substring($normSpace, 5)"/>
				</xsl:when>
				<!-- NOTE: might want to remove useless values here, though that hasn't been a problem with these fields -->
				<xsl:otherwise>
					<xsl:value-of select="$normSpace"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- NOTE:  further URI scrubbing can be done with java util.URIutil class -->

		<!-- normalize output, and replace spaces with %20 -->
		<xsl:value-of select="$outputValue"/>
	</xsl:template>


	<xsl:template match="text()" mode="withoutURI">
		<!-- NOTE:: might want to remove useless values here, though that hasn't been a problem with these fields  -->
		<xsl:value-of select="normalize-space(.)"/>
	</xsl:template>


	<xsl:template match="@*" mode="addURI">
		<xsl:copy>
			<xsl:apply-templates select="node()" mode="addURI"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="comment()" mode="addURI">
		<xsl:comment><xsl:value-of select="."/></xsl:comment>
	</xsl:template>
	

	<!-- named template assumes no other child nodes (attributes, elements, comments, etc) -->
	<xsl:template name="addURI">
		<xsl:param name="qname" select="name()"/>
		<xsl:param name="nsURI" select="namespace-uri()"/>
		<xsl:param name="value" select="."/>

		<!-- ensure element has a value -->
		<xsl:variable name="normSpace" select="normalize-space($value)"/>
		<xsl:if test="string($normSpace)">
			<xsl:variable name="lowercase" select="translate($normSpace, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>

			<xsl:element name="{$qname}" namespace="{$nsURI}">

				<xsl:choose>
					<!-- NOTE:  would need to pass in attribs to know if URI was already specified -->

					<!-- add URI encoding scheme if appropriate -->

					<!-- NOTE:  a better way to do this would be to use an xsl extension function to call our 
					java routine util.URIutil.scrubURL(), which does a better job of determining if it's a URL
					and ensuring the output is a working URL. There are similar methods to see if it's a URI
					instead, for expediency in getting done with this code, we use this stylesheet and then scrub the
					designated URIs afterwards -->
					
					<!-- NOTE: URI can be absolute or relative URL (http, ftp, gopher, telnet, etc), URN ... XML schema will accept virtually any value
					so we are only adding it for some obvious cases.  Also we do not check for "http://" b/c of oft occurring typos such as
					"httphttp" or "http://https"-->

					<!-- NOTE: it is not valid to start "www." but that will be corrected to "http://www." by post-processing
					 nor it is not valid to start "url: blah" but that will be corrected to "blah" by post-processing -->
					<xsl:when test="starts-with($lowercase, 'http') or
									starts-with($lowercase, 'www.') or
									starts-with($lowercase, 'url:') or
									starts-with($lowercase, 'ftp://') or
									starts-with($lowercase, 'telnet://') or
									starts-with($lowercase, 'gopher://')">
						<xsl:attribute name="xsi:type">dct:URI</xsl:attribute>
						<xsl:call-template name="outputURIvalue">
							<xsl:with-param name="value" select="$normSpace"/>
						</xsl:call-template>
					</xsl:when>
					
					<!-- we didn't find match to add URI -->
					<xsl:otherwise>
						<xsl:value-of select="$normSpace"/>
					</xsl:otherwise>

				</xsl:choose>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>


