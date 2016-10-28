<?xml version="1.0"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:osm="http://nldr.library.ucar.edu/metadata/osm"
	xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="xsi xs osm"
    version="1.1">

<!--PURPOSE: to transform OSM version 1.1 records into the Open Archives Initiative (OAI) base format of oai_dc-->
<!--CREATION: 2012-07-03 by Katy Ginger, University Corporation for Atmospheric Research (UCAR)-->
<!--HISTORY: none-->

	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

<!--TRANSFORMATION CODE-->
<!-- ************************************************** -->
	<xsl:template match="*|/">
		<xsl:apply-templates select="osm:record"/>
	</xsl:template>

<!--TRANSFORMATION CODE-->
<!-- ********************************************************-->
	<xsl:template match="osm:record">
		<oai_dc:dc xmlns="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">

<!--dc:title using title-->
<!--OSM title is required; so no need to determine if any titles are present, just write it-->
			<xsl:apply-templates select="osm:general/osm:title" mode="process">
				<xsl:with-param name="tag">dc:title</xsl:with-param>
			</xsl:apply-templates>
<!--dc:indentifier using only DOI and the citeable URL; no other values like ISBN or REPORT or ISSN are used; the DOI and the citeable URL are the most important-->
<!--dc:identifier using idNumber/@type = DOI-->
		<xsl:apply-templates select="osm:classify/osm:idNumber[@type='DOI'] " mode="process">
			<xsl:with-param name="tag">dc:identifier</xsl:with-param>
		</xsl:apply-templates>				

<!--dc:indentifier using citeable URL-->
		<xsl:apply-templates select="osm:general/osm:urlOfRecord" mode="process">
			<xsl:with-param name="tag">dc:identifier</xsl:with-param>
		</xsl:apply-templates>				

<!--dc:language using language-->
<!--OSM language is required and generally uses 2-letter encoding while oai_dc uses xs:language-->
<!--so just use the 2-letter encodings directly from OSM-->
<!--do not map the OSM value of 'not applicable'-->
<!--oai_dc and OSM language is unbounded-->
			<xsl:apply-templates select="osm:classify/osm:language[not(.='not applicable')]" mode="process">
				<xsl:with-param name="tag">dc:language</xsl:with-param>
			</xsl:apply-templates>


<!--dc:description using description and abstract-->
<!--OSM description is unbounded and the oai_dc description is unbounded-->
<!--OSM abstract occurs only once--> 
			<xsl:apply-templates select="osm:general/osm:description" mode="process">
				<xsl:with-param name="tag">dc:description</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select="osm:general/osm:abstract[not(.='needs abstract')]" mode="process">
				<xsl:with-param name="tag">dc:description</xsl:with-param>
			</xsl:apply-templates>	

<!--dc:subject using keyword, LCSHsubject, OSMsubject-->
			<xsl:apply-templates select="osm:general/osm:keyword | osm:general/osm:LCSHsubject | osm:general/osm:OSMsubject" mode="process">
				<xsl:with-param name="tag">dc:subject</xsl:with-param>
			</xsl:apply-templates>				

<!--dc:creator using osm:contributors/osm:person and organization[@role='Author']-->
<!--	variables to determine if any and which creators are present-->
		<xsl:variable name="allcreators">
			<xsl:for-each select="osm:contributors/osm:person/@role">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="allcreatororgs">
			<xsl:for-each select="osm:contributors/osm:organization/@role">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<!-- only OSM role that is a dc:creator is Author-->
		<!--3 cases are present-->
			<xsl:choose>
			<!--CASE 1: if there are OSM people authors, then do people authors only-->
				<xsl:when test="contains($allcreators, 'Author')">
					<xsl:apply-templates select="osm:contributors/osm:person[@role='Author'] " mode="name">
						<xsl:with-param name="tag">dc:creator</xsl:with-param>
					</xsl:apply-templates>
				</xsl:when>
			<!--CASE 2: if there are no OSM people authors, check to see if OSM org authors are present and process them-->
				<xsl:when test="not(contains($allcreators, 'Author')) and contains($allcreatororgs, 'Author')">
					<xsl:apply-templates select="osm:contributors/osm:organization[@role='Author'] " mode="org">
						<xsl:with-param name="tag">dc:creator</xsl:with-param>
					</xsl:apply-templates>
				</xsl:when>
			<!--CASE 3: if there are no OSM people authors and no OSM org authors, then assign UCAR as creator-->
				<xsl:otherwise>
					<dc:creator>University Corporation for Atmospheric Rsearch (UCAR)</dc:creator>
				</xsl:otherwise>
			</xsl:choose>

<!--dc:publisher using organization[@role='Publisher']/affiliation/instName-->
			<xsl:apply-templates select="osm:contributors/osm:organization[@role='Publisher']/osm:affiliation/osm:instName" mode="process">
				<xsl:with-param name="tag">dc:publisher</xsl:with-param>
			</xsl:apply-templates>	

<!--dc:contributor using organization[@role='Editor OR Contributor']/affiliation/instName-->
			<xsl:apply-templates select="osm:contributors/osm:organization[@role='Editor']/osm:affiliation/osm:instName | osm:contributors/osm:organization[@role='Contributor']/osm:affiliation/osm:instName" mode="process">
				<xsl:with-param name="tag">dc:contributor</xsl:with-param>
			</xsl:apply-templates>	

<!--dc:contributor using person[@role='Editor OR Contributor']-->
			<xsl:apply-templates select="osm:contributors/osm:person[@role='Editor'] | osm:contributors/osm:person[@role='Contributor'] " mode="name">
				<xsl:with-param name="tag">dc:contributor</xsl:with-param>
			</xsl:apply-templates>

<!--dc:date using coverage.date-->
<!--date is required in OSM so something will be present-->
<!--only mapping OSM date types of created, published, circa, presented; no other date types are mapped-->
		<xsl:apply-templates select="osm:coverage/osm:date[@type='Created'] | osm:coverage/osm:date[@type='Circa'] | osm:coverage/osm:date[@type='Published'] | osm:coverage/osm:date[@type='Presented']" mode="process">
			<xsl:with-param name="tag">dc:date</xsl:with-param>
		</xsl:apply-templates>				

<!--dc:type using general.title@type-->
<!--OSM title type is required and only occurs once so something will be present and no template is required-->
		<xsl:element name="dc:type" namespace="http://purl.org/dc/elements/1.1/">
			<xsl:value-of select="osm:general/osm:title/@type"/>
		</xsl:element>				
<!--map to DC vocab too-->
		<xsl:choose>
			<xsl:when test="osm:general/osm:title[@type='Animation/moving image']">
				<dc:type>MovingImage</dc:type>
			</xsl:when>
			<xsl:when test="osm:general/osm:title[@type='Artifact/physical object']">
				<dc:type>PhysicalObject</dc:type>
			</xsl:when>
			<xsl:when test="osm:general/osm:title[@type='Bibliography/index'] |osm:general/osm:title[@type='Collection'] ">
				<dc:type>Collection</dc:type>
			</xsl:when>
			<xsl:when test="osm:general/osm:title[@type='Blog/forum/email list'] | osm:general/osm:title[@type='Book chapter'] | osm:general/osm:title[@type='Book review'] | osm:general/osm:title[@type='Book/monograph'] | osm:general/osm:title[@type='Correspondence'] | osm:general/osm:title[@type='Encyclopedia'] | osm:general/osm:title[@type='Journal/magazine/periodical'] | osm:general/osm:title[@type='Manual'] | osm:general/osm:title[@type='Manuscript'] | osm:general/osm:title[@type='Newsletter'] | osm:general/osm:title[@type='Notes'] | osm:general/osm:title[@type='Poster'] | osm:general/osm:title[@type='Report'] | osm:general/osm:title[@type='Transcript'] | osm:general/osm:title[@type='Article'] ">
				<dc:type>Text</dc:type>
			</xsl:when>
			<xsl:when test="osm:general/osm:title[@type='Presentation/webcast'] ">
				<dc:type>Event</dc:type>
			</xsl:when>
			<xsl:when test="osm:general/osm:title[@type='Image']">
				<dc:type>StillImage</dc:type>
			</xsl:when>
			<xsl:when test="osm:general/osm:title[@type='Interactive resource']">
				<dc:type>InteractiveResource</dc:type>
			</xsl:when>
			<xsl:when test="osm:general/osm:title[@type='Software']">
				<dc:type>Software</dc:type>
			</xsl:when>
			<xsl:when test="osm:general/osm:title[@type='Sound']">
				<dc:type>Sound</dc:type>
			</xsl:when>
		</xsl:choose>

<!--dc:format using size-->
		<xsl:apply-templates select="osm:resources/osm:primaryAsset/osm:size" mode="process">
			<xsl:with-param name="tag">dc:format</xsl:with-param>
		</xsl:apply-templates>				

<!--dc:format using mimetype-->
		<xsl:apply-templates select="osm:resources/osm:primaryAsset/osm:mimeType" mode="process">
			<xsl:with-param name="tag">dc:format</xsl:with-param>
		</xsl:apply-templates>				

<!--dc:source - no map-->

<!--dc:relation using only relation URLs-->
		<xsl:apply-templates select="osm:resources/osm:relation " mode="relation">
			<xsl:with-param name="tag">dc:relation</xsl:with-param>
		</xsl:apply-templates>				

<!--dc:coverage using eventName-->
			<xsl:apply-templates select="osm:general/osm:eventName" mode="process">
				<xsl:with-param name="tag">dc:coverage</xsl:with-param>
			</xsl:apply-templates>				

<!--dc:coverage using dateRange-->
			<xsl:apply-templates select="osm:coverage/osm:dateRange" mode="coverage">
				<xsl:with-param name="tag">dc:coverage</xsl:with-param>
			</xsl:apply-templates>				

<!--dc: rights using copyrightNotice-->
<!--for OSM, copyrightNotice and holder are required and occur only once-->
<!--4 cases-->
		<xsl:element name="dc:rights" namespace="http://purl.org/dc/elements/1.1/">
		<xsl:choose>
			<xsl:when test="string-length(osm:rights/osm:copyrightNotice/@url) = 0 and string-length(osm:rights/osm:copyrightNotice/@type) > 0">
			<!--don't want to write opt out type-->
				<xsl:choose>
					<xsl:when test="osm:rights/osm:copyrightNotice/@type ='Opt out' ">
<!--CASE 1 type has content and url is empty-->
						<xsl:value-of select="concat('The rights holder is: ', osm:rights/osm:copyrightNotice/@holder, '. The rights notice is: ', osm:rights/osm:copyrightNotice)"/>
					</xsl:when>
					<xsl:otherwise>
<!--CASE 1 type has content and url is empty-->
						<xsl:value-of select="concat('The rights holder is: ', osm:rights/osm:copyrightNotice/@holder, '. The type of right license is: ', osm:rights/osm:copyrightNotice/@type, '. The rights notice is: ', osm:rights/osm:copyrightNotice)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="string-length(osm:rights/osm:copyrightNotice/@url) > 0 and string-length(osm:rights/osm:copyrightNotice/@type) > 0">
			<!--don't want to write opt out type-->
				<xsl:choose>
					<xsl:when test="osm:rights/osm:copyrightNotice/@type = 'Opt out' ">
<!--CASE 2 type has content and url has content-->
						<xsl:value-of select="concat('The rights holder is: ', osm:rights/osm:copyrightNotice/@holder, '. The rights notice is: ', osm:rights/osm:copyrightNotice, ' More information can be found at: ', osm:rights/osm:copyrightNotice/@url, '.')"/>
					</xsl:when>
					<xsl:otherwise>
<!--CASE 2 type has content and url has content-->
						<xsl:value-of select="concat('The rights holder is: ', osm:rights/osm:copyrightNotice/@holder, '. The type of right license is: ', osm:rights/osm:copyrightNotice/@type, '. The rights notice is: ', osm:rights/osm:copyrightNotice, ' More information can be found at: ', osm:rights/osm:copyrightNotice/@url, '.')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="string-length(osm:rights/osm:copyrightNotice/@url) > 0 and string-length(osm:rights/osm:copyrightNotice/@type) = 0">
<!--CASE 3 type is empty and url has content-->
				<xsl:value-of select="concat('The rights holder is: ', osm:rights/osm:copyrightNotice/@holder, '. The rights notice is: ', osm:rights/osm:copyrightNotice, ' More information can be found at: ', osm:rights/osm:copyrightNotice/@url, '.')"/>
			</xsl:when>
			<xsl:otherwise>
<!--CASE 4 both type and url are empty-->
				<xsl:value-of select="concat('The rights holder is: ', osm:rights/osm:copyrightNotice/@holder, '. The rights notice is: ', osm:rights/osm:copyrightNotice)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:element> <!--end rights element-->


	</oai_dc:dc><!--end oai_dc element-->

</xsl:template>




<!--TEMPLATES for lar to nsdl_dc-->
<!-- ****************************************-->
<!--PROCESS write all tag sets that do not have their own templates-->

<!--COVERAGE template without type-->
	<xsl:template match="node()" name="coverage" mode="coverage">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="start" select="./@start"/>
		<xsl:param name="end" select="./@end"/>
		<xsl:param name="type" select="./@type"/>
	
		<xsl:element name="{$tag}" namespace="http://purl.org/dc/elements/1.1/">
			<xsl:choose>
				<xsl:when test="string-length($start) > 0 and string-length($end) > 0">
					<xsl:value-of select="concat($type, ': ', $start, ' to ', $end)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($type, '- started: ', $start)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>	

<!--NAME template without type-->
	<xsl:template match="node()" name="name" mode="name">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="stringLast" select="osm:lastName"/>
		<xsl:param name="stringFirst" select="osm:firstName"/>
	
		<xsl:element name="{$tag}" namespace="http://purl.org/dc/elements/1.1/">
			<xsl:value-of select="concat($stringLast, ', ', $stringFirst)"/>
		</xsl:element>
	</xsl:template>	

<!--ORG template without type-->
	<xsl:template match="node()" name="org" mode="org">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="string" select="osm:affiliation/osm:instName"/>
		
		<xsl:element name="{$tag}" namespace="http://purl.org/dc/elements/1.1/">
			<xsl:value-of select="$string"/>
		</xsl:element>
	</xsl:template>	

<!--PROCESS template without type-->
	<xsl:template match="node()" name="process" mode="process">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="string" select="."/>

			<xsl:element name="{$tag}" namespace="http://purl.org/dc/elements/1.1/">
				<xsl:value-of select="$string"/>
			</xsl:element>
	</xsl:template>	

<!--RELATION template-->
	<xsl:template match="node()" name="relation" mode="relation">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="string" select="./@url"/>

		<xsl:if test="contains($string, 'http')">
			<xsl:element name="{$tag}" namespace="http://purl.org/dc/elements/1.1/">
				<xsl:value-of select="$string"/>
			</xsl:element>
		</xsl:if>		
	</xsl:template>	

</xsl:stylesheet>
<!--LICENSE AND COPYRIGHT
The contents of this file are subject to the Educational Community License v1.0 (the "License"); you may not use this file except in compliance with the License. You should obtain a copy of the License from http://www.opensource.org/licenses/ecl1.php. Files distributed under the License are distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the specific language governing rights and limitations under the License. Copyright 2002-2009 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR). All rights reserved.-->