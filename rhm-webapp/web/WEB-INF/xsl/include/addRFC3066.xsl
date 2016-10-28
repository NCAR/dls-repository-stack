<?xml version="1.0" encoding="UTF-8"?>
<!-- =============================================================  
 "addRFC3066" stylesheet 

add RFC3066 encoding scheme to elements if appropriate

	Also 
		don't output anything if element is empty
		get rid of excess whitespace in value with normalize-space
		don't output extra namespace declarations
 
Author:  Naomi Dushay 
updated 03/07/07 add RFC3066 when appropriate rather than indiscriminately
updated: 02/11/20 add named template 
Created: 02/11/19  Naomi Dushay
================================================================ -->
<xsl:stylesheet version="1.0" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		>

	<xsl:template match="*" mode="addRFC3066">

		<!-- ensure element has a value -->
		<xsl:variable name="normSpaceStr" select="string(normalize-space(.))"/>
		<xsl:if test="$normSpaceStr">
			<xsl:variable name="lowercase" select="translate($normSpaceStr, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>

			<xsl:element name="{name()}" namespace="{namespace-uri()}">

				<xsl:choose>
					<!-- RFC3066 encoding scheme already specified -->
					<xsl:when test="self::node()[@xsi:type='dct:RFC3066']">
						<xsl:apply-templates select="@*|node()" mode="addRFC3066"/>
					</xsl:when>

					<!-- add RFC3066 encoding scheme if appropriate -->
<!-- NOTE: only converting single language on short list  to encoded value -->
<!-- would like to tokenize with break ' ' or ', ' and covert each token to the proper encoding -->

				        <!-- sources: -->
				        <!-- RFC1766:  http://www.faqs.org/rfcs/rfc1766.html -->
				        <!-- RFC3066:  http://www.ietf.org/rfc/rfc3066.txt -->
				            <!--    excerpt:  " When a language has both an ISO 639-1 2-character code and an ISO 639-2 3-character code, -->
				            <!--                      you MUST use the tag derived from the ISO 639-1 2-character code. -->
				
				        <!-- ISO639:        http://lcweb.loc.gov/standards/iso639-2/iso639jac.html -->
				        <!-- ISO639-2  3 and 2 letter codes:  http://lcweb.loc.gov/standards/iso639-2/langhome.html -->
				        <!-- ISO639  2 letter codes:   http://palimpsest.stanford.edu/lex/iso639.html -->

					<xsl:when test="$lowercase = 'english'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>en</xsl:when>
					<xsl:when test="$lowercase = 'en'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>en</xsl:when>
					<!--NOTE:  en-US is a valid value -->
					<xsl:when test="$lowercase = 'en-us'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>en-US</xsl:when>

					<xsl:when test="$lowercase = 'catalan'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>ca</xsl:when>
					<xsl:when test="$lowercase = 'ca'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>ca</xsl:when>
					<xsl:when test="$lowercase = 'chinese'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>zh</xsl:when>
					<xsl:when test="$lowercase = 'zh'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>zh</xsl:when>
					<xsl:when test="$lowercase = 'croatian'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>hr</xsl:when>
					<xsl:when test="$lowercase = 'hr'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>hr</xsl:when>
					<xsl:when test="$lowercase = 'danish'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>da</xsl:when>
					<xsl:when test="$lowercase = 'da'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>da</xsl:when>
					<xsl:when test="$lowercase = 'dutch'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>nl</xsl:when>
					<xsl:when test="$lowercase = 'nl'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>nl</xsl:when>
					<xsl:when test="$lowercase = 'french'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>fr</xsl:when>
					<xsl:when test="$lowercase = 'fr'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>fr</xsl:when>
					<xsl:when test="$lowercase = 'german'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>de</xsl:when>
					<xsl:when test="$lowercase = 'de'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>de</xsl:when>
					<xsl:when test="$lowercase = 'greek'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>el</xsl:when>
					<xsl:when test="$lowercase = 'el'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>el</xsl:when>
					<xsl:when test="$lowercase = 'hungarian'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>hu</xsl:when>
					<xsl:when test="$lowercase = 'hu'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>hu</xsl:when>
					<xsl:when test="$lowercase = 'italian'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>it</xsl:when>
					<xsl:when test="$lowercase = 'it'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>it</xsl:when>
					<xsl:when test="$lowercase = 'japanese'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>ja</xsl:when>
					<xsl:when test="$lowercase = 'ja'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>ja</xsl:when>
					<xsl:when test="$lowercase = 'norwegian'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>no</xsl:when>
					<xsl:when test="$lowercase = 'no'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>no</xsl:when>
					<xsl:when test="$lowercase = 'polish'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>pl</xsl:when>
					<xsl:when test="$lowercase = 'pl'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>pl</xsl:when>
					<xsl:when test="$lowercase = 'portuguese'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>pt</xsl:when>
					<xsl:when test="$lowercase = 'pt'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>pt</xsl:when>
					<xsl:when test="$lowercase = 'russian'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>ru</xsl:when>
					<xsl:when test="$lowercase = 'ru'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>ru</xsl:when>
					<xsl:when test="$lowercase = 'serbian'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>sr</xsl:when>
					<xsl:when test="$lowercase = 'sr'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>sr</xsl:when>
					<xsl:when test="$lowercase = 'spanish'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>es</xsl:when>
					<xsl:when test="$lowercase = 'es'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>es</xsl:when>
					<xsl:when test="$lowercase = 'swedish'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>sv</xsl:when>
					<xsl:when test="$lowercase = 'sv'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>sv</xsl:when>
	
					<!-- didn't recognize RFC3066 language/code -->
					<xsl:otherwise>
						<xsl:apply-templates select="@*|node()" mode="addRFC3066"/>
					</xsl:otherwise>
				</xsl:choose> 
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:template match="text()" mode="addRFC3066">
<!-- NOTE:: would like to remove useless values here.  Can't use "removeFieldsWithoutInfo b/c "na" is a valid language code ... -->
		<xsl:value-of select="normalize-space(.)"/>
	</xsl:template>

	<xsl:template match="@*" mode="addRFC3066">
		<xsl:copy>
			<xsl:apply-templates select="node()" mode="addRFC3066"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="comment()" mode="addRFC3066">
		<xsl:comment>
			<xsl:value-of select="."/>
		</xsl:comment>
	</xsl:template>

	<!-- named template assumes no other child nodes (attributes, elements, comments, etc) -->
	<xsl:template name="addRFC3066">
		<xsl:param name="qname" select="name()"/>
		<xsl:param name="nsURI" select="namespace-uri()"/>
		<xsl:param name="value" select="."/>

		<!-- ensure element has a value -->
		<xsl:variable name="normSpaceStr" select="string(normalize-space($value))"/>
		<xsl:if test="$normSpaceStr">
			<xsl:variable name="lowercase" select="translate($normSpaceStr, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>

			<xsl:element name="{$qname}" namespace="{$nsURI}">

				<xsl:choose>
					<!-- NOTE:  would need to pass in attribs to know if RFC3066 was already specified -->

					<!-- add RFC3066 encoding scheme if appropriate -->
<!-- NOTE: only converting single language on short list  to encoded value -->
<!-- would like to tokenize with break ' ' or ', ' and covert each token to the proper encoding -->

				        <!-- sources: -->
				        <!-- RFC1766:  http://www.faqs.org/rfcs/rfc1766.html -->
				        <!-- RFC3066:  http://www.ietf.org/rfc/rfc3066.txt -->
				            <!--    excerpt:  " When a language has both an ISO 639-1 2-character code and an ISO 639-2 3-character code, -->
				            <!--                      you MUST use the tag derived from the ISO 639-1 2-character code. -->
				
				        <!-- ISO639:        http://lcweb.loc.gov/standards/iso639-2/iso639jac.html -->
				        <!-- ISO639-2  3 and 2 letter codes:  http://lcweb.loc.gov/standards/iso639-2/langhome.html -->
				        <!-- ISO639  2 letter codes:   http://palimpsest.stanford.edu/lex/iso639.html -->

					<xsl:when test="$lowercase = 'english'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>en</xsl:when>
					<xsl:when test="$lowercase = 'en'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>en</xsl:when>
					<!--NOTE:  en-US is a valid value -->
					<xsl:when test="$lowercase = 'en-us'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>en-US</xsl:when>

					<xsl:when test="$lowercase = 'catalan'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>ca</xsl:when>
					<xsl:when test="$lowercase = 'ca'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>ca</xsl:when>
					<xsl:when test="$lowercase = 'chinese'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>zh</xsl:when>
					<xsl:when test="$lowercase = 'zh'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>zh</xsl:when>
					<xsl:when test="$lowercase = 'croatian'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>hr</xsl:when>
					<xsl:when test="$lowercase = 'hr'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>hr</xsl:when>
					<xsl:when test="$lowercase = 'danish'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>da</xsl:when>
					<xsl:when test="$lowercase = 'da'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>da</xsl:when>
					<xsl:when test="$lowercase = 'dutch'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>nl</xsl:when>
					<xsl:when test="$lowercase = 'nl'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>nl</xsl:when>
					<xsl:when test="$lowercase = 'french'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>fr</xsl:when>
					<xsl:when test="$lowercase = 'fr'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>fr</xsl:when>
					<xsl:when test="$lowercase = 'german'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>de</xsl:when>
					<xsl:when test="$lowercase = 'de'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>de</xsl:when>
					<xsl:when test="$lowercase = 'greek'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>el</xsl:when>
					<xsl:when test="$lowercase = 'el'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>el</xsl:when>
					<xsl:when test="$lowercase = 'hungarian'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>hu</xsl:when>
					<xsl:when test="$lowercase = 'hu'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>hu</xsl:when>
					<xsl:when test="$lowercase = 'italian'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>it</xsl:when>
					<xsl:when test="$lowercase = 'it'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>it</xsl:when>
					<xsl:when test="$lowercase = 'japanese'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>ja</xsl:when>
					<xsl:when test="$lowercase = 'ja'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>ja</xsl:when>
					<xsl:when test="$lowercase = 'norwegian'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>no</xsl:when>
					<xsl:when test="$lowercase = 'no'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>no</xsl:when>
					<xsl:when test="$lowercase = 'polish'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>pl</xsl:when>
					<xsl:when test="$lowercase = 'pl'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>pl</xsl:when>
					<xsl:when test="$lowercase = 'portuguese'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>pt</xsl:when>
					<xsl:when test="$lowercase = 'pt'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>pt</xsl:when>
					<xsl:when test="$lowercase = 'russian'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>ru</xsl:when>
					<xsl:when test="$lowercase = 'ru'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>ru</xsl:when>
					<xsl:when test="$lowercase = 'serbian'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>sr</xsl:when>
					<xsl:when test="$lowercase = 'sr'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>sr</xsl:when>
					<xsl:when test="$lowercase = 'spanish'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>es</xsl:when>
					<xsl:when test="$lowercase = 'es'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>es</xsl:when>
					<xsl:when test="$lowercase = 'swedish'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>sv</xsl:when>
					<xsl:when test="$lowercase = 'sv'"><xsl:attribute name="xsi:type">dct:RFC3066</xsl:attribute>sv</xsl:when>
	
					<!-- didn't recognize RFC3066 language/code -->
					<xsl:otherwise>
						<xsl:value-of select="normalize-space($value)"/>
					</xsl:otherwise>
				</xsl:choose> 
			</xsl:element>
		</xsl:if>

	</xsl:template>

</xsl:stylesheet>
