<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:d="http://www.dlsciences.org/frameworks/fields"
    exclude-result-prefixes="xsi d" 
    version="1.0">

<!--HISTORY-->
<!--2010-08-16: modified d namespace from http://ns.nsdl.org/ncs/fields to http://www.dlsciences.org/frameworks/fields-->
<!--2005-10-14: took out id numbers if not present-->
<!--creation date: 2004-11-11-->

	<xsl:output method="html"/>

  	<xsl:template match="*|/">
		<xsl:apply-templates select="d:metadataFieldInfo"/>
	</xsl:template>

	<xsl:template match="d:metadataFieldInfo">
		<html>
		<body>
		<h3>
			<xsl:value-of select="d:field/@name"/>
		</h3>
		<p><strong>xpath</strong> - <xsl:value-of select="d:field/@path"/><br/>
		<strong>Framework version</strong> - <xsl:value-of select="d:field/@metaVersion"/><br/>
		<xsl:if test="string-length(d:field/@id) > 0">
			<strong>DLESE id</strong> - <xsl:value-of select="d:field/@id"/>		
		</xsl:if></p>
		<p><strong>Definition</strong> - <xsl:value-of select="d:field/d:definition"/><br/>
		<strong>Obligation</strong> - <xsl:value-of select="d:field/d:documentation/d:obligation"/><br/>
		<strong>Minimum occurrences</strong> - <xsl:value-of select="d:field/d:documentation/d:min"/><br/>
		<strong>Maximum occurrences</strong> - <xsl:value-of select="d:field/d:documentation/d:max"/><br/>

		<xsl:variable name="attributes">
			<xsl:for-each select="d:field/d:documentation/d:attributes/d:attribute">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="string-length($attributes)> 0">
			<strong>Attributes</strong> - 
			<xsl:apply-templates select="d:field/d:documentation/d:attributes/d:attribute[position()!=last()]"/>
			<!--see template ATTRIBUTE, CHILD and XMLDATATYPE [position()!=last()] - split because of making comma separated list-->
			<xsl:apply-templates select="d:field/d:documentation/d:attributes/d:attribute[last()]"/><br/>
			<!--see template ATTRIBUTE, CHILD and XMLDATATYPE [last()] - split because of making comma separated list-->
		</xsl:if>

		<xsl:variable name="children">
			<xsl:for-each select="d:field/d:documentation/d:children/d:child">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="string-length($children)> 0">
			<strong>Child elements</strong> - 
			<xsl:apply-templates select="d:field/d:documentation/d:children/d:child[position()!=last()]"/>
			<!--see template ATTRIBUTE, CHILD and XMLDATATYPE [position()!=last()] - split because of making comma separated list-->
			<xsl:apply-templates select="d:field/d:documentation/d:children/d:child[last()]"/><br/>
			<!--see template ATTRIBUTE, CHILD and XMLDATATYPE [last()] - split because of making comma separated list-->
		</xsl:if>

		<strong>Data types</strong> - 
		<xsl:apply-templates select="d:field/d:documentation/d:XMLdataTypes/d:XMLdataType[position()!=last()]"/>
		<!--see template ATTRIBUTE, CHILD and XMLDATATYPE [position()!=last()] - split because of making comma separated list-->
		<xsl:apply-templates select="d:field/d:documentation/d:XMLdataTypes/d:XMLdataType[last()]"/><br/>
		<!--see template ATTRIBUTE, CHILD and XMLDATATYPE [last()] - split because of making comma separated list-->

		<strong>Domain</strong> - <xsl:value-of select="d:field/d:documentation/d:domain"/><br/>
		<strong>Domain example</strong> - <xsl:value-of select="d:field/d:documentation/d:example"/><br/>

		<xsl:if test="string-length(d:field/d:documentation/d:default) > 0">
			<strong>Default value</strong> - <xsl:value-of select="d:field/d:documentation/d:default"/><br/>
		</xsl:if>

		<xsl:if test="string-length(d:field/d:documentation/d:other) > 0">
			<strong>Other occurrences in framework</strong> - <xsl:value-of select="d:field/d:documentation/d:other"/><br/>
		</xsl:if>
		
		</p>
		<!--Notes section-->
		<!--	variable to see if there are notes present in the fields file-->
		<xsl:variable name="noteInfo">
			<xsl:for-each select="d:field/d:documentation/d:notes/d:note">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="string-length($noteInfo)> 0">
			<h4>Notes</h4>
			<ul>
				<xsl:apply-templates select="d:field/d:documentation/d:notes/d:note"/>
				<!--see template NOTE-->
			</ul>
		</xsl:if>

		<!--Vocabulary Information section-->
		<xsl:if test="string-length(d:field/d:vocabLevels/@number) > 0">
			<h4>Controlled vocabulary information</h4>
			<ul>
				<li><strong>Number of levels</strong> - <xsl:value-of select="d:field/d:vocabLevels/@number"/></li>
				<xsl:apply-templates select="d:field/d:vocabLevels/d:levelAndDeftn"/>
				<!--see template LEVELANDDEFTN-->
			</ul>
		</xsl:if>
		<!--Terms and Definitions section-->
		<!--	variable to see if there are terms and definitions present in the fields file-->
		<xsl:variable name="terms">
			<xsl:for-each select="d:field/d:terms/d:termAndDeftn">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="string-length($terms)> 0">
			<h4>Term and definitions</h4>
			<ul>
				<xsl:apply-templates select="d:field/d:terms/d:termAndDeftn"/>
				<!--see template TERMANDDEFTN-->
			</ul>
		</xsl:if>

		<!--Cataloging Best Practices section - there are always some best practices-->
		<h4>Cataloging best practices</h4>
		
		<!--variable to see if there are things to do best practices-->
		<xsl:variable name="todo">
			<xsl:for-each select="d:field/d:bestPractices/d:dos/d:practice">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="string-length($todo)>0">
			<p><em><strong>Things to do</strong></em></p>
			<ul>
				<xsl:apply-templates select="d:field/d:bestPractices/d:dos/d:practice"/>
				<!--see template PRACTICE-->
			</ul>
		</xsl:if>

		<!--variable to see if there are things to avoid best practices-->
		<xsl:variable name="toavoid">
			<xsl:for-each select="d:field/d:bestPractices/d:donts/d:practice">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="string-length($toavoid)>0">
			<p><em><strong>Things to avoid</strong></em></p>
			<ul>
				<xsl:apply-templates select="d:field/d:bestPractices/d:donts/d:practice"/>
				<!--see template PRACTICE-->
			</ul>
		</xsl:if>

		<!--variable to see if there are example best practices-->
		<xsl:variable name="examples">
			<xsl:for-each select="d:field/d:bestPractices/d:examples/d:practice">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="string-length($examples)>0">
			<p><em><strong>Examples</strong></em></p>
			<ul>
				<xsl:apply-templates select="d:field/d:bestPractices/d:examples/d:practice"/>
				<!--see template PRACTICE-->
			</ul>
		</xsl:if>

		<!--variable to see if there are other practices best practices-->
		<xsl:variable name="others">
			<xsl:for-each select="d:field/d:bestPractices/d:otherPractices/d:otherPractice/d:practice">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="string-length($others)> 0">
			<xsl:apply-templates select="d:field/d:bestPractices/d:otherPractices/d:otherPractice"/>
			<!--see template OTHERPRACTICE-->
		</xsl:if>
		</body>
		</html>
	</xsl:template>

<!--TEMPLATES TO APPLY-->
<!--organized in the following order-->
<!--1. ATTRIBUTE, CHILD and XMLDATATYPE writes the Data types list, Child elements and Attributes except the last item in each list-->
<!--1a. ATTRIBUTE, CHILD and XMLDATATYPE writes the last Data types list, Child elements and Attributes item-->
<!--2a. LEVELANDDEFTN writes Controlled vocabulary information-->
<!--2b. LEVELTERMSANDDEFTN for different vocabulary level, this write the level term and level term definition-->
<!--3. NOTE writes Notes regarding how to use the metadata element-->
<!--4. OTHERPRACTICE writes best practices for other practices-->
<!--5. PRACTICE writes best practices for things to do, things to avoid, examples-->
<!--6. TERMANDDEFTN writes terms and definition-->

<!--position and last information-->
<!--Returns the index number of the last node in the current context node-set. selects the penultimate child element of the context node: child::*[position() = last()-1]-->
<!--last()  returns the index of the last element. Ex: /HEAD[last()] selects the last HEAD element. 
position() returns the index position. Ex: /HEAD[position() <= 5] selects the first five HEAD elements 
count(...) returns the count of elements. Ex: /HEAD[count(HEAD)=0] selects all HEAD elements that have no subheads-->
<!--1. XMLDATATYPES  - NO WORK -->
<!--	<xsl:template match="d:XMLdataType">
		<xsl:choose>
			<xsl:when test="d:XMLdataType[last()]">
			<xsl:value-of select="concat(., ', ')"/>
				<xsl:value-of select="."/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat(., ', ')"/>
				<xsl:value-of select="."/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	-->		
	

<!--1. ATTRIBUTE, CHILD and XMLDATATYPE - writes all except the last item in the respective lists -->
	<xsl:template match="d:XMLdataType[position()!=last()] | d:attribute[position()!=last()] | d:child[position()!=last()]">
		<xsl:value-of select="concat(., ', ')"/>
	</xsl:template>			

<!--1.a  ATTRIBUTE, CHILD and XMLDATATYPE - writes the the last item in the lists -->
	<xsl:template match="d:XMLdataType[last()] | d:attribute[last()] | d:child[last()]">
		<xsl:value-of select="."/>
	</xsl:template>			

<!--2a. LEVELANDDEFTN -->
	<xsl:template match="d:levelAndDeftn">
		<li><strong>Level <xsl:value-of select="./@level"/> is <xsl:value-of select="./@levelName"/></strong> - <xsl:value-of select="./@levelDeftn"/></li>
		<!--	variable to see if there are vocab level terms present in the fields file-->
		<xsl:variable name="vocablevelterms">
			<xsl:for-each select="d:levelTermsAndDeftns/d:levelTermAndDeftn">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="string-length($vocablevelterms) > 0" >
			<ul>
				<li><strong>Level terms and definitions:</strong></li>
				<ul>
					<xsl:apply-templates select="d:levelTermsAndDeftns/d:levelTermAndDeftn"/>
				</ul>	
				<!--see template LEVELTERMSANDDEFTN-->
			</ul>
		</xsl:if>
	</xsl:template>			

<!--2b. LEVELTERMSANDDEFTN-->
	<xsl:template match="d:levelTermAndDeftn">
		<li><strong><xsl:value-of select="./@term"/></strong> - <xsl:value-of select="."/></li>
	</xsl:template>			



<!--3. NOTE -->
	<xsl:template match="d:note">
		<li><xsl:value-of select="."/></li>
	</xsl:template>			

<!--4. OTHERPRACTICE -->
	<xsl:template match="d:otherPractice">
		<p><em><strong><xsl:value-of select="./@header"/></strong></em></p>
		<ul>
			<xsl:apply-templates select="./d:practice"/>
		</ul>
	</xsl:template>			

<!--5. PRACTICE -->
	<xsl:template match="d:practice">
				
<!--only process current best practices-->
		<xsl:if test="contains(./@status, 'current')">
			<!--	case 1: s1, s2, s3, s4-->
			<!--	case 2: s1, s2, s3, u4 -->
			<!--	case 3: u1, s2, u3, s4 -->
			<!--	case 4: s1, u2, u3, u4 -->
			<!--	case 5: s1, s2, u3, u4 -->
			<!--	case 6: s1, u2, s3, u4 -->
			<!--	case 7: s1, u2, s3, s4 -->
			<!--	case 8: s1, s2, u3, s4 -->
			<!--	case 9: s1, u2, u3, s4 -->
			<!--	case 10: u1, u2, u3, s4 -->
			<!--	case 11: u1, u2, s3, s4 -->
			<!--	case 12: u1, s2, s3, s4 -->
			<!--	case 13: u1, s2, s3, u4 -->
			<!--	case 14: u1, s2, u3, u4 -->
			<!--	case 15: u1, u2, s3, u4 -->
			<!--	case 16: u1, u2, u3, u4 -->
			<!--	case 17: u1; goes with case 16-->
			<!--	case 18: s1; goes with case 1-->
			<!--	case 19: u1, u2; goes with case 16 -->
			<!--	case 20: u1, s2; goes with case 3 -->
			<!--	case 21: s1, s2; goes with case 1 -->
			<!--	case 22: s1, u2 ; goes with case case 4-->
			<!--	case 23: u1, u2, u3; goes with case 16 -->
			<!--	case 24: u1, u2, s3; goes with case 15-->
			<!--	case 25: u1, s2, s3; goes with case 12 -->
			<!--	case 26: u1, s2, u3; goes with case 14 -->
			<!--	case 27: s1, s2, s3; goes with case 1 -->
			<!--	case 28: s1, u2, s3; goes with case 6 -->
			<!--	case 29: s1, u2, u3; goes with case case 4 -->
			<!--	case 30: s1, s2, u3; goes with case case 5 -->

			<!--assumes the only style being used is 'bold'; does not account for anything else-->
			
			<xsl:choose>
			<!--	case 1: s1, s2, s3, s4-->
			<!--	case 18: s1; goes with case 1-->
			<!--	case 21: s1, s2; goes with case 1 -->
			<!--	case 27: s1, s2, s3; goes with case 1 -->
				<xsl:when test="(string-length(./@style1) > 0 and string-length(./@style2) > 0 and string-length(./@style3) > 0 and string-length(./@style4) >0) or (string-length(./@style1) > 0 and string-length(./@link2) = 0 and string-length(./@link3) = 0 and string-length(./@link4) = 0) or (string-length(./@style1) > 0 and string-length(./@style2) > 0 and string-length(./@link3) = 0 and string-length(./@link4) = 0) or (string-length(./@style1) > 0 and string-length(./@style2) > 0 and string-length(./@style3) > 0 and string-length(./@link4) = 0)">
					<!--assumes the only style being used is 'bold'; does not account for anything else-->
					<xsl:variable name="link1">
						<xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/>
					</xsl:variable>

					<xsl:variable name="link2">
						<xsl:value-of select="substring-before($link1, ./@link2)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link2"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link1, ./@link2)"/>
					</xsl:variable>
							
					<xsl:variable name="link3">
						<xsl:value-of select="substring-before($link2, ./@link3)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link3"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link2, ./@link3)"/>
					</xsl:variable>
							
					<xsl:variable name="link4">
						<xsl:value-of select="substring-before($link3, ./@link4)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link4"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link3, ./@link4)"/>
					</xsl:variable>

					<li><xsl:value-of select="$link4" disable-output-escaping="yes"/></li>

<!--							<li><xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/></li> -->
				</xsl:when>

			<!--	case 2: s1, s2, s3, u4 -->
				<xsl:when test="(string-length(./@style1) > 0 and string-length(./@style2) > 0 and string-length(./@style3) > 0 and string-length(./@url4) >0)" >

					<xsl:variable name="link1">
						<xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/>
					</xsl:variable>
					<xsl:value-of select="$link1"/>
					<xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>

					<xsl:variable name="link2">
						<xsl:value-of select="substring-before($link1, ./@link2)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link2"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link1, ./@link2)"/>
					</xsl:variable>
						<xsl:value-of select="$link2"/>
					<xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
						
					<xsl:variable name="link3">
						<xsl:value-of select="substring-before($link2, ./@link3)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link3"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link2, ./@link3)"/>
					</xsl:variable>
					<xsl:value-of select="$link3"/>
					<xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>

					<xsl:variable name="link4">
						<xsl:value-of select="substring-before($link3, ./@link4)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url4"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link4"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link3, ./@link4)"/>
					</xsl:variable>

					<xsl:value-of select="$link4"/>
					<xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>

					<li><xsl:value-of select="$link4" disable-output-escaping="yes"/></li>
				</xsl:when>

			<!--	case 3: u1, s2, u3, s4 -->
			<!--	case 20: u1, s2; goes with case 3 -->
				<xsl:when test="(string-length(./@url1) > 0 and string-length(./@style2) > 0 and string-length(./@url3) > 0 and string-length(./@style4) >0) or (string-length(./@url1) > 0 and string-length(./@style2) > 0 and string-length(./@link3) = 0 and string-length(./@link4) =0)" >

					<xsl:variable name="link1">
						<xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url1"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/>
					</xsl:variable>

					<xsl:variable name="link2">
						<xsl:value-of select="substring-before($link1, ./@link2)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link2"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link1, ./@link2)"/>
					</xsl:variable>

					<xsl:variable name="link3">
						<xsl:value-of select="substring-before($link2, ./@link3)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url3"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link3"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link2, ./@link3)"/>
					</xsl:variable>

					<xsl:variable name="link4">
						<xsl:value-of select="substring-before($link3, ./@link4)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link4"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link3, ./@link4)"/>
					</xsl:variable>

					<li><xsl:value-of select="$link4" disable-output-escaping="yes"/></li>
				</xsl:when>

			<!--	case 4: s1, u2, u3, u4 -->
			<!--	case 22: s1, u2 ; goes with case case 4-->
			<!--	case 29: s1, u2, u3; goes with case case 4 -->
				<xsl:when test="(string-length(./@style1) > 0 and string-length(./@url2) > 0 and string-length(./@url3) > 0 and string-length(./@url4) >0) or (string-length(./@style1) > 0 and string-length(./@url2) > 0 and string-length(./@link3) = 0 and string-length(./@link4) =0) or (string-length(./@style1) > 0 and string-length(./@url2) > 0 and string-length(./@url3) > 0 and string-length(./@link4) =0)" >

					<xsl:variable name="link1">
						<xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/>
					</xsl:variable>

					<xsl:variable name="link2">
						<xsl:value-of select="substring-before($link1, ./@link2)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url2"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link2"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link1, ./@link2)"/>
					</xsl:variable>

					<xsl:variable name="link3">
						<xsl:value-of select="substring-before($link2, ./@link3)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url3"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link3"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link2, ./@link3)"/>
					</xsl:variable>

					<xsl:variable name="link4">
						<xsl:value-of select="substring-before($link3, ./@link4)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url4"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link4"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link3, ./@link4)"/>
					</xsl:variable>

					<li><xsl:value-of select="$link4" disable-output-escaping="yes"/></li>
				</xsl:when>

			<!--	case 5: s1, s2, u3, u4 -->
			<!--	case 30: s1, s2, u3; goes with case case 5 -->
				<xsl:when test="(string-length(./@style1) > 0 and string-length(./@style2) > 0 and string-length(./@url3) > 0 and string-length(./@url4) >0) or (string-length(./@style1) > 0 and string-length(./@style2) > 0 and string-length(./@url3) > 0 and string-length(./@link4) =0)" >

					<xsl:variable name="link1">
						<xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/>
					</xsl:variable>

					<xsl:variable name="link2">
						<xsl:value-of select="substring-before($link1, ./@link2)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link2"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link1, ./@link2)"/>
					</xsl:variable>

					<xsl:variable name="link3">
						<xsl:value-of select="substring-before($link2, ./@link3)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url3"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link3"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link2, ./@link3)"/>
					</xsl:variable>

					<xsl:variable name="link4">
						<xsl:value-of select="substring-before($link3, ./@link4)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url4"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link4"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link3, ./@link4)"/>
					</xsl:variable>

					<li><xsl:value-of select="$link4" disable-output-escaping="yes"/></li>
				</xsl:when>

			<!--	case 6: s1, u2, s3, u4 -->
			<!--	case 28: s1, u2, s3; goes with case 6 -->
				<xsl:when test="(string-length(./@style1) > 0 and string-length(./@url2) > 0 and string-length(./@style3) > 0 and string-length(./@url4) >0) or (string-length(./@style1) > 0 and string-length(./@url2) > 0 and string-length(./@style3) > 0 and string-length(./@link4) =0)" >

					<xsl:variable name="link1">
						<xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/>
					</xsl:variable>

					<xsl:variable name="link2">
						<xsl:value-of select="substring-before($link1, ./@link2)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url2"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link2"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link1, ./@link2)"/>
					</xsl:variable>

					<xsl:variable name="link3">
						<xsl:value-of select="substring-before($link2, ./@link3)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link3"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link2, ./@link3)"/>
					</xsl:variable>

					<xsl:variable name="link4">
						<xsl:value-of select="substring-before($link3, ./@link4)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url4"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link4"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link3, ./@link4)"/>
					</xsl:variable>

					<li><xsl:value-of select="$link4" disable-output-escaping="yes"/></li>
				</xsl:when>

			<!--	case 7: s1, u2, s3, s4 -->
				<xsl:when test="string-length(./@style1) > 0 and string-length(./@url2) > 0 and string-length(./@style3) > 0 and string-length(./@style4) >0" >

					<xsl:variable name="link1">
						<xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/>
					</xsl:variable>

					<xsl:variable name="link2">
						<xsl:value-of select="substring-before($link1, ./@link2)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url2"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link2"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link1, ./@link2)"/>
					</xsl:variable>

					<xsl:variable name="link3">
						<xsl:value-of select="substring-before($link2, ./@link3)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link3"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link2, ./@link3)"/>
					</xsl:variable>

					<xsl:variable name="link4">
						<xsl:value-of select="substring-before($link3, ./@link4)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link4"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link3, ./@link4)"/>
					</xsl:variable>

					<li><xsl:value-of select="$link4" disable-output-escaping="yes"/></li>
				</xsl:when>

			<!--	case 8: s1, s2, u3, s4 -->
				<xsl:when test="string-length(./@style1) > 0 and string-length(./@style2) > 0 and string-length(./@url3) > 0 and string-length(./@style4) >0" >

					<xsl:variable name="link1">
						<xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/>
					</xsl:variable>

					<xsl:variable name="link2">
						<xsl:value-of select="substring-before($link1, ./@link2)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link2"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link1, ./@link2)"/>
					</xsl:variable>


					<xsl:variable name="link3">
						<xsl:value-of select="substring-before($link2, ./@link3)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url3"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link3"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link2, ./@link3)"/>
					</xsl:variable>

					<xsl:variable name="link4">
						<xsl:value-of select="substring-before($link3, ./@link4)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link4"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link3, ./@link4)"/>
					</xsl:variable>

					<li><xsl:value-of select="$link4" disable-output-escaping="yes"/></li>
				</xsl:when>

			<!--	case 9: s1, u2, u3, s4 -->
				<xsl:when test="string-length(./@style1) > 0 and string-length(./@url2) > 0 and string-length(./@url3) > 0 and string-length(./@style4) >0" >

					<xsl:variable name="link1">
						<xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/>
					</xsl:variable>

					<xsl:variable name="link2">
						<xsl:value-of select="substring-before($link1, ./@link2)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url2"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link2"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link1, ./@link2)"/>
					</xsl:variable>

					<xsl:variable name="link3">
						<xsl:value-of select="substring-before($link2, ./@link3)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url3"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link3"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link2, ./@link3)"/>
					</xsl:variable>

					<xsl:variable name="link4">
						<xsl:value-of select="substring-before($link3, ./@link4)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link4"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link3, ./@link4)"/>
					</xsl:variable>

					<li><xsl:value-of select="$link4" disable-output-escaping="yes"/></li>
				</xsl:when>

			<!--	case 10: u1, u2, u3, s4 -->
				<xsl:when test="string-length(./@url1) > 0 and string-length(./@url2) > 0 and string-length(./@url3) > 0 and string-length(./@style4) >0" >

					<xsl:variable name="link1">
						<xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url1"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/>
					</xsl:variable>

					<xsl:variable name="link2">
						<xsl:value-of select="substring-before($link1, ./@link2)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url2"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link2"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link1, ./@link2)"/>
					</xsl:variable>

					<xsl:variable name="link3">
						<xsl:value-of select="substring-before($link2, ./@link3)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url3"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link3"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link2, ./@link3)"/>
					</xsl:variable>

					<xsl:variable name="link4">
						<xsl:value-of select="substring-before($link3, ./@link4)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link4"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link3, ./@link4)"/>
					</xsl:variable>

					<li><xsl:value-of select="$link4" disable-output-escaping="yes"/></li>
				</xsl:when>

			<!--	case 11: u1, u2, s3, s4 -->
				<xsl:when test="string-length(./@url1) > 0 and string-length(./@url2) > 0 and string-length(./@style3) > 0 and string-length(./@style4) >0" >

					<xsl:variable name="link1">
						<xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url1"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/>
					</xsl:variable>

					<xsl:variable name="link2">
						<xsl:value-of select="substring-before($link1, ./@link2)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url2"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link2"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link1, ./@link2)"/>
					</xsl:variable>

					<xsl:variable name="link3">
						<xsl:value-of select="substring-before($link2, ./@link3)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link3"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link2, ./@link3)"/>
					</xsl:variable>

					<xsl:variable name="link4">
						<xsl:value-of select="substring-before($link3, ./@link4)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link4"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link3, ./@link4)"/>
					</xsl:variable>

					<li><xsl:value-of select="$link4" disable-output-escaping="yes"/></li>
				</xsl:when>

			<!--	case 12: u1, s2, s3, s4 -->
			<!--	case 25: u1, s2, s3; goes with case 12 -->
				<xsl:when test="(string-length(./@url1) > 0 and string-length(./@style2) > 0 and string-length(./@style3) > 0 and string-length(./@style4) >0) or (string-length(./@url1) > 0 and string-length(./@style2) > 0 and string-length(./@style3) > 0 and string-length(./@link4) =0)" >

					<xsl:variable name="link1">
						<xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url1"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/>
					</xsl:variable>

					<xsl:variable name="link2">
						<xsl:value-of select="substring-before($link1, ./@link2)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link2"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link1, ./@link2)"/>
					</xsl:variable>

					<xsl:variable name="link3">
						<xsl:value-of select="substring-before($link2, ./@link3)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link3"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link2, ./@link3)"/>
					</xsl:variable>

					<xsl:variable name="link4">
						<xsl:value-of select="substring-before($link3, ./@link4)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link4"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link3, ./@link4)"/>
					</xsl:variable>

					<li><xsl:value-of select="$link4" disable-output-escaping="yes"/></li>
				</xsl:when>

			<!--	case 13: u1, s2, s3, u4 -->
				<xsl:when test="string-length(./@url1) > 0 and string-length(./@style2) > 0 and string-length(./@style3) > 0 and string-length(./@url4) >0" >

					<xsl:variable name="link1">
						<xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url1"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/>
					</xsl:variable>

					<xsl:variable name="link2">
						<xsl:value-of select="substring-before($link1, ./@link2)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link2"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link1, ./@link2)"/>
					</xsl:variable>

					<xsl:variable name="link3">
						<xsl:value-of select="substring-before($link2, ./@link3)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link3"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link2, ./@link3)"/>
					</xsl:variable>

					<xsl:variable name="link4">
						<xsl:value-of select="substring-before($link3, ./@link4)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url4"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link4"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link3, ./@link4)"/>
					</xsl:variable>

					<li><xsl:value-of select="$link4" disable-output-escaping="yes"/></li>
				</xsl:when>

			<!--	case 14: u1, s2, u3, u4 -->
			<!--	case 26: u1, s2, u3; goes with case 14 -->
				<xsl:when test="(string-length(./@url1) > 0 and string-length(./@style2) > 0 and string-length(./@url3) > 0 and string-length(./@url4) >0) or (string-length(./@url1) > 0 and string-length(./@style2) > 0 and string-length(./@url3) > 0 and string-length(./@link4) =0)" >

					<xsl:variable name="link1">
						<xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url1"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/>
					</xsl:variable>

					<xsl:variable name="link2">
						<xsl:value-of select="substring-before($link1, ./@link2)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link2"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link1, ./@link2)"/>
					</xsl:variable>

					<xsl:variable name="link3">
						<xsl:value-of select="substring-before($link2, ./@link3)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url3"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link3"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link2, ./@link3)"/>
					</xsl:variable>

					<xsl:variable name="link4">
						<xsl:value-of select="substring-before($link3, ./@link4)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url4"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link4"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link3, ./@link4)"/>
					</xsl:variable>

					<li><xsl:value-of select="$link4" disable-output-escaping="yes"/></li>
				</xsl:when>

			<!--	case 15: u1, u2, s3, u4 -->
			<!--	case 24: u1, u2, s3; goes with case 15-->
				<xsl:when test="(string-length(./@url1) > 0 and string-length(./@url2) > 0 and string-length(./@style3) > 0 and string-length(./@url4) >0) or (string-length(./@url1) > 0 and string-length(./@url2) > 0 and string-length(./@style3) > 0 and string-length(./@link4) =0)" >

					<xsl:variable name="link1">
						<xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url1"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/>
					</xsl:variable>

					<xsl:variable name="link2">
						<xsl:value-of select="substring-before($link1, ./@link2)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url2"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link2"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link1, ./@link2)"/>
					</xsl:variable>

					<xsl:variable name="link3">
						<xsl:value-of select="substring-before($link2, ./@link3)"/><xsl:text disable-output-escaping="yes">&lt;strong&gt;</xsl:text><xsl:value-of select="./@link3"/><xsl:text disable-output-escaping="yes">&lt;/strong&gt;</xsl:text><xsl:value-of select="substring-after($link2, ./@link3)"/>
					</xsl:variable>

					<xsl:variable name="link4">
						<xsl:value-of select="substring-before($link3, ./@link4)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url4"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link4"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link3, ./@link4)"/>
					</xsl:variable>

					<li><xsl:value-of select="$link4" disable-output-escaping="yes"/></li>
				</xsl:when>

			<!--	case 16: u1, u2, u3, u4 -->
			<!--	case 17: u1; goes with case 16-->
			<!--	case 19: u1, u2; goes with case 16 -->
			<!--	case 23: u1, u2, u3; goes with case 16 -->
				<xsl:when test="(string-length(./@url1) > 0 and string-length(./@url2) > 0 and string-length(./@url3) > 0 and string-length(./@url4) >0) or (string-length(./@url1) and string-length(./@link2) = 0 and string-length(./@link3) = 0 and string-length(./@link4) =0) or (string-length(./@url1) > 0 and string-length(./@url2) > 0 and string-length(./@link3) = 0 and string-length(./@link4) = 0) or (string-length(./@url1) > 0 and string-length(./@url2) > 0 and string-length(./@url3) > 0 and string-length(./@link4) =0)" >

					<xsl:variable name="link1">
						<xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url1"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/>
					</xsl:variable>

					<xsl:variable name="link2">
						<xsl:value-of select="substring-before($link1, ./@link2)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url2"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link2"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link1, ./@link2)"/>
					</xsl:variable>

					<xsl:variable name="link3">
						<xsl:value-of select="substring-before($link2, ./@link3)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url3"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link3"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link2, ./@link3)"/>
					</xsl:variable>

					<xsl:variable name="link4">
						<xsl:value-of select="substring-before($link3, ./@link4)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url4"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link4"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after($link3, ./@link4)"/>
					</xsl:variable>

					<li><xsl:value-of select="$link4" disable-output-escaping="yes"/></li>


<!--					<li><xsl:value-of select="substring-before(., ./@link1)"/><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="./@url1"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="./@link1"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:value-of select="substring-after(., ./@link1)"/></li> -->
				</xsl:when>
				<xsl:otherwise>
					<li><xsl:value-of select="." disable-output-escaping="no"/></li>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>			

<!--6. TERMANDDEFTN@vocab -->
	<xsl:template match="d:termAndDeftn">
		<xsl:choose>

			<xsl:when  test="string-length(./@id) > 0">
				<xsl:choose>
					<xsl:when test="string-length(./@attribution) > 0">
						<li><strong><xsl:value-of select="./@vocab"/></strong> - <xsl:value-of disable-output-escaping="no" select="concat(., ' [', ./@attribution, ']')"/>[DLESE term id: <xsl:value-of select="./@id"/>]</li>
					</xsl:when>
					<xsl:otherwise>
						<li><strong><xsl:value-of select="./@vocab"/></strong> - <xsl:value-of disable-output-escaping="no" select="."/> [DLESE term id: <xsl:value-of select="./@id"/>]</li>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>

			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="string-length(./@attribution) > 0">
						<li><strong><xsl:value-of select="./@vocab"/></strong> - <xsl:value-of disable-output-escaping="no" select="concat(., ' [', ./@attribution, ']')"/></li>
					</xsl:when>
					<xsl:otherwise>
						<li><strong><xsl:value-of select="./@vocab"/></strong> - <xsl:value-of disable-output-escaping="no" select="."/></li>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>

		</xsl:choose>
	</xsl:template>			

</xsl:stylesheet>
