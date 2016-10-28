<?xml version="1.0" encoding="UTF-8"?>
<!-- =============================================================  
 "addIMT" stylesheet 

add IMT encoding scheme encoding to elements if appropriate

	Also 
		don't output anything if element is empty
		get rid of excess whitespace in value with normalize-space
		don't output extra namespace declarations
 
Author:  Naomi Dushay 
updated 03/07/07 apply IMT when appropriate rather than indiscriminately
updated: 02/11/21 don't apply IMT if value has "/x-"  (experimental mime type) 
updated: 02/11/20 add named template 
Created: 02/11/19  Naomi Dushay
================================================================ -->
<xsl:stylesheet version="1.0" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		>

	<xsl:template match="*" mode="addIMT">
		<xsl:variable name="normSpaceStr" select="string(normalize-space(.))"/>

		<!-- ensure element has a value -->
		<xsl:if test="$normSpaceStr">
			<xsl:variable name="lowercase" select="translate($normSpaceStr, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>

			<xsl:element name="{name()}" namespace="{namespace-uri()}">

				<xsl:choose>
					<!-- IMT encoding scheme already specified -->
					<xsl:when test="self::node()[@xsi:type='dct:IMT']">
						<xsl:apply-templates select="@*|node()" mode="addIMT"/>
					</xsl:when>

					<!-- don't apply IMT if value has "/x-"  (experimental mime type) -->
					<xsl:when test="contains($lowercase, '/x-')">
						<xsl:apply-templates select="@*|node()" mode="addIMT"/>
					</xsl:when>

					<!-- add IMT encoding scheme if appropriate -->
				        <!-- sources: -->
				        <!-- http://ns.nsdl.org/schemas/mime_type/mime_type_v1.00.xsd -->

					<!-- text and its known variants -->
					<xsl:when test="$lowercase = 'text'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text</xsl:when>
					<xsl:when test="$lowercase = 'txt'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text</xsl:when>
	
					<!-- text/html and its known variants -->
					<xsl:when test="$lowercase = 'text/html'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text/html</xsl:when>
					<xsl:when test="$lowercase = 'text.html'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text/html</xsl:when>

					<!-- other text values -->
					<xsl:when test="$lowercase = 'text/enriched'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text/enriched</xsl:when>
					<xsl:when test="$lowercase = 'text/plain'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text/plain</xsl:when>
					<xsl:when test="$lowercase = 'text/richtext'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text/richtext</xsl:when>
					<xsl:when test="$lowercase = 'text/rtf'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text/rtf</xsl:when>
					<xsl:when test="$lowercase = 'text/sgml'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text/sgml</xsl:when>
					<xsl:when test="$lowercase = 'text/xml'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text/xml</xsl:when>
	
					
					<!-- application/pdf and its known variants -->
					<xsl:when test="$lowercase = 'application/pdf'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>application/pdf</xsl:when>
					<xsl:when test="$lowercase = 'text/pdf'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>application/pdf</xsl:when>

					<!-- application/postscript and its known variants -->
					<xsl:when test="$lowercase = 'application/postscript'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>application/postscript</xsl:when>
					<xsl:when test="$lowercase = 'text/postscript'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>application/postscript</xsl:when>

					<!-- other application values -->
					<xsl:when test="$lowercase = 'application/sgml'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>application/sgml</xsl:when>
					<xsl:when test="$lowercase = 'application/xml'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>application/xml</xsl:when>
					<xsl:when test="$lowercase = 'application/xml-dtd'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>application/xml-dtd</xsl:when>


					<!-- image/jpeg and its known variants -->
					<xsl:when test="$lowercase = 'image/jpeg'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>image/jpeg</xsl:when>
					<xsl:when test="$lowercase = 'image/jpg'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>image/jpeg</xsl:when>

					<!-- image/tiff and its known variants -->
					<xsl:when test="$lowercase = 'image/tiff'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>image/tiff</xsl:when>
					<xsl:when test="$lowercase = 'iimage/tiff'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>image/tiff</xsl:when>
					<xsl:when test="$lowercase = 'image/tif'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>image/tiff</xsl:when>
	
					<!-- other image values -->
					<xsl:when test="$lowercase = 'image'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>image</xsl:when>
					<xsl:when test="$lowercase = 'image/gif'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>image/gif</xsl:when>
					<xsl:when test="$lowercase = 'image/png'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>image/png</xsl:when>
					
					<!-- audio values -->
					<xsl:when test="$lowercase = 'audio'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>audio</xsl:when>
					<xsl:when test="$lowercase = 'audio/mpeg'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>audio/mpeg</xsl:when>
	

					<!-- video/quicktime and its known variants -->
					<xsl:when test="$lowercase = 'video/quicktime'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>video/quicktime</xsl:when>
					<xsl:when test="$lowercase = 'quicktime'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>video/quicktime</xsl:when>
					<xsl:when test="$lowercase = 'quicktime movie'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>video/quicktime</xsl:when>
					<xsl:when test="$lowercase = 'quick time movie'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>video/quicktime</xsl:when>
					<xsl:when test="$lowercase = 'quick time forma'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>video/quicktime</xsl:when>
					<xsl:when test="$lowercase = 'quicktime forma'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>video/quicktime</xsl:when>

					<!-- video values -->
					<xsl:when test="$lowercase = 'video'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>video</xsl:when>
					<xsl:when test="$lowercase = 'video/mpeg'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>video/mpeg</xsl:when>


					<!-- model values -->
					<xsl:when test="$lowercase = 'model'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>model</xsl:when>


					<!-- didn't recognize IMT value -->
					<xsl:otherwise>
						<xsl:apply-templates select="@*|node()" mode="addIMT"/>
					</xsl:otherwise>
				</xsl:choose>  <!-- whether or not to apply IMT encoding scheme -->
			</xsl:element>
		</xsl:if>
	</xsl:template>


	<xsl:template match="text()" mode="addIMT">
<!-- NOTE:: might want to remove useless values here, though that hasn't been a big problem with format field ...  -->
		<xsl:value-of select="normalize-space(.)"/>
	</xsl:template>

	<xsl:template match="@*" mode="addIMT">
		<xsl:copy>
			<xsl:apply-templates select="node()" mode="addIMT"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="comment()" mode="addIMT">
		<xsl:comment>
			<xsl:value-of select="."/>
		</xsl:comment>
	</xsl:template>


	<!-- named template assumes no other child nodes (attributes, elements, comments, etc) -->
	<xsl:template name="addIMT">
		<xsl:param name="qname" select="name()"/>
		<xsl:param name="nsURI" select="namespace-uri()"/>
		<xsl:param name="value" select="."/>

		<!-- ensure element has a value -->
		<xsl:variable name="normSpaceStr" select="string(normalize-space($value))"/>
		<xsl:if test="$normSpaceStr">
			<xsl:variable name="lowercase" select="translate($normSpaceStr, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>

			<xsl:element name="{$qname}" namespace="{$nsURI}">

				<xsl:choose>
					<!-- NOTE:  would need to pass in attribs to know if IMT was already specified -->

					<!-- don't apply IMT if value has "/x-"  (experimental mime type) -->
					<xsl:when test="contains($lowercase, '/x-')">
						<xsl:apply-templates select="@*|node()" mode="addIMT"/>
					</xsl:when>

					<!-- add IMT encoding scheme if appropriate -->
				        <!-- sources: -->
				        <!-- http://ns.nsdl.org/schemas/mime_type/mime_type_v1.00.xsd -->

					<!-- text and its known variants -->
					<xsl:when test="$lowercase = 'text'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text</xsl:when>
					<xsl:when test="$lowercase = 'txt'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text</xsl:when>
	
					<!-- text/html and its known variants -->
					<xsl:when test="$lowercase = 'text/html'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text/html</xsl:when>
					<xsl:when test="$lowercase = 'text.html'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text/html</xsl:when>

					<!-- other text values -->
					<xsl:when test="$lowercase = 'text/enriched'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text/enriched</xsl:when>
					<xsl:when test="$lowercase = 'text/plain'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text/plain</xsl:when>
					<xsl:when test="$lowercase = 'text/richtext'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text/richtext</xsl:when>
					<xsl:when test="$lowercase = 'text/rtf'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text/rtf</xsl:when>
					<xsl:when test="$lowercase = 'text/sgml'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text/sgml</xsl:when>
					<xsl:when test="$lowercase = 'text/xml'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>text/xml</xsl:when>
	
					
					<!-- application/pdf and its known variants -->
					<xsl:when test="$lowercase = 'application/pdf'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>application/pdf</xsl:when>
					<xsl:when test="$lowercase = 'text/pdf'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>application/pdf</xsl:when>

					<!-- application/postscript and its known variants -->
					<xsl:when test="$lowercase = 'application/postscript'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>application/postscript</xsl:when>
					<xsl:when test="$lowercase = 'text/postscript'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>application/postscript</xsl:when>

					<!-- other application values -->
					<xsl:when test="$lowercase = 'application/sgml'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>application/sgml</xsl:when>
					<xsl:when test="$lowercase = 'application/xml'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>application/xml</xsl:when>
					<xsl:when test="$lowercase = 'application/xml-dtd'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>application/xml-dtd</xsl:when>


					<!-- image/jpeg and its known variants -->
					<xsl:when test="$lowercase = 'image/jpeg'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>image/jpeg</xsl:when>
					<xsl:when test="$lowercase = 'image/jpg'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>image/jpeg</xsl:when>

					<!-- image/tiff and its known variants -->
					<xsl:when test="$lowercase = 'image/tiff'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>image/tiff</xsl:when>
					<xsl:when test="$lowercase = 'iimage/tiff'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>image/tiff</xsl:when>
					<xsl:when test="$lowercase = 'image/tif'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>image/tiff</xsl:when>
	
					<!-- other image values -->
					<xsl:when test="$lowercase = 'image'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>image</xsl:when>
					<xsl:when test="$lowercase = 'image/gif'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>image/gif</xsl:when>
					<xsl:when test="$lowercase = 'image/png'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>image/png</xsl:when>
					
					<!-- audio values -->
					<xsl:when test="$lowercase = 'audio'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>audio</xsl:when>
					<xsl:when test="$lowercase = 'audio/mpeg'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>audio/mpeg</xsl:when>
	

					<!-- video/quicktime and its known variants -->
					<xsl:when test="$lowercase = 'video/quicktime'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>video/quicktime</xsl:when>
					<xsl:when test="$lowercase = 'quicktime'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>video/quicktime</xsl:when>
					<xsl:when test="$lowercase = 'quicktime movie'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>video/quicktime</xsl:when>
					<xsl:when test="$lowercase = 'quick time movie'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>video/quicktime</xsl:when>
					<xsl:when test="$lowercase = 'quick time forma'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>video/quicktime</xsl:when>
					<xsl:when test="$lowercase = 'quicktime forma'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>video/quicktime</xsl:when>

					<!-- video values -->
					<xsl:when test="$lowercase = 'video'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>video</xsl:when>
					<xsl:when test="$lowercase = 'video/mpeg'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>video/mpeg</xsl:when>


					<!-- model values -->
					<xsl:when test="$lowercase = 'model'"><xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>model</xsl:when>


					<!-- didn't recognize IMT value -->
					<xsl:otherwise>
						<xsl:value-of select="normalize-space($value)"/>
					</xsl:otherwise>
				</xsl:choose>  <!-- whether or not to apply IMT encoding scheme -->

			</xsl:element>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
