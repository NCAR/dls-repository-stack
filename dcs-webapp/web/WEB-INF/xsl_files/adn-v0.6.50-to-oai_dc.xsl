<?xml version="1.0"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:d="http://adn.dlese.org"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:nsdl_dc="http://ns.nsdl.org/nsdl_dc_v1.01"
    xmlns:dct="http://purl.org/dc/terms/"
    xmlns:ieee="http://www.ieee.org/xsd/LOMv1p0"
    exclude-result-prefixes="xsi d dc xsi nsdl_dc dct ieee" 
    version="1.0">
    
<!--ORGANIZATION OF THIS FILE-->
<!-- **************************************-->
<!--The top half of this file, before the break with 5 lines of blank comments, is the apply-templates logic. This section is organized in document order of an ADN  item-level metadata record. The bottom half, after the break with 5 lines of blank comments, are the templates. The templates are organized in alphabetical order.-->

<!--ASSUMPTIONS-->
<!-- **************************************-->
<!--1. The transform is run over only ADN version 0.6.50 records a collection considers to be accessioned-->
<!--2. Unless otherwise indicated in this stylesheet, the transform applies to both ADN online and offline resources-->
<!--3. Only the ADN large bounding boxand it associated places, not detailed geometries, are transformed-->
<!--4. ADN relations.relation.kind=DLESE:Has thumbnail is not transformed-->
<!--5. ADN objectInSpace tags are not transformed-->
<!--6. When ADN named time periods are transformed only the first named time period is transformed, all others are ignored; nsdl_dc does not allow for more than one named time period-->
<!--7. Any ADN timeAD.begin.date or timeAD.end.date with a value of 'Present' is not transformed for the time scheme of W3CDTF or dct:Period. It it transformed under the plain dct:temporal tags-->

	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

<!--VARIABLES used throughout the transform-->
<!-- **********************************************************-->
<!--variable for adding a line return-->
	<xsl:variable name="newline">
		<xsl:text>
		</xsl:text>
	</xsl:variable>


	<xsl:template match="d:itemRecord">
		<xsl:text disable-output-escaping="yes">&lt;oai_dc:dc xmlns:oai_dc=&quot;http://www.openarchives.org/OAI/2.0/oai_dc/&quot;
xsi:schemaLocation=&quot;http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd&quot; xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;&gt;
</xsl:text>
		<xsl:value-of select="$newline"/>

<!--dc:title-->
		<xsl:element name="dc:title">
			<xsl:value-of select="d:general/d:title"/>
		</xsl:element>	
		<xsl:value-of select="$newline"/>

<!--dc:creator - person-->
		<xsl:apply-templates select="d:lifecycle/d:contributors/d:contributor/d:person" mode="creator"/>
		<xsl:apply-templates select="d:lifecycle/d:contributors/d:contributor/d:organization" mode="creator"/>
		<!--see template PERSON mode=CREATOR-->
		<!--see template ORGANIZATION mode=CREATOR-->

<!--dc:subject - DLESE-->
		<xsl:apply-templates select="d:general/d:subjects/d:subject" mode="DLESE"/>
		<!--see template SUBJECT mode=DLESE-->

<!--dc:subject - from keywords-->
		<xsl:apply-templates select="d:general/d:keywords/d:keyword" mode="keywords"/>
		<!--see template SUBJECT mode=KEYWORDS-->

<!--dc:date-->
<!--since ADN has many lifecycle.contributors.contributor.date values and these are not required, determine if any are present using a variable to grab all of them-->
 
<!--	variable for ADN lifecycle.contributors.contributor.date-->
		<xsl:variable name="alldates">
			<xsl:for-each select="d:lifecycle/d:contributors/d:contributor/@date">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>

<!--test to see if any date information is present; if so, this code will grab the date of the first date attribute only-->
		<xsl:if test="string-length($alldates)>0">
			<xsl:element name="dc:date">
				<xsl:value-of select="d:lifecycle/d:contributors/d:contributor/@date"/>
			</xsl:element>	
			<xsl:value-of select="$newline"/>
		</xsl:if>
<!--end: dc:date-->

<!--dc:description-->
<!--determine if the resource is online or offline; if offline, concatenate ADN general.description and ADN technical.offline.accessInformation-->

		<xsl:choose>
			<xsl:when test="string-length(d:technical/d:offline/d:accessInformation)>0">
				<xsl:element name="dc:description">
					<xsl:value-of select="concat(d:general/d:description,' This is an offline resource with the following access information: ',d:technical/d:offline/d:accessInformation)"/>
				</xsl:element>
				<xsl:element name="dct:accessRights">
					<xsl:value-of select="d:technical/d:offline/d:accessInformation"/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="dc:description">
					<xsl:value-of select="d:general/d:description"/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>

<!--dc:description - using ADN standards information-->
		<xsl:apply-templates select="d:educational/d:contentStandards/d:contentStandard"/>
		<!--see template CONTENTSTANDARD-->

<!--dc:description - audience-->
		<xsl:apply-templates select="d:educational/d:audiences/d:audience/d:beneficiary"/>
		<!--see template BENEFICIARY-->

<!--dc:description - mediator-->
		<xsl:apply-templates select="d:educational/d:audiences/d:audience/d:toolFor"/>
		<!--see template TOOLFOR-->

<!--dc:description - educationalLevel-->
		<xsl:apply-templates select="d:educational/d:audiences/d:audience/d:gradeRange"/>
		<!--see template GRADERANGE-->
		
<!--dc:publisher-->
		<xsl:apply-templates select="d:lifecycle/d:contributors/d:contributor/d:person" mode="publisher"/>
		<xsl:apply-templates select="d:lifecycle/d:contributors/d:contributor/d:organization" mode="publisher"/>
		<!--see template PERSON mode=PUBLISHER-->
		<!--see template ORGANIZATION mode=PUBLISHER-->
	
<!--dc:contributor-->
		<xsl:apply-templates select="d:lifecycle/d:contributors/d:contributor/d:person" mode="contributor"/>
		<xsl:apply-templates select="d:lifecycle/d:contributors/d:contributor/d:organization" mode="contributor"/>
		<!--see template PERSON mode=CONTRIBUTOR-->
		<!--see template ORGANIZATION mode=CONTRIBUTOR-->
	
<!--dc:format - size-->
<!--no dc:format - size for ADN offline resources-->
		<xsl:if test="string-length(d:technical/d:online/d:size) > 0">
			<xsl:element name="dc:format">
				<xsl:value-of select="d:technical/d:online/d:size"/>
			</xsl:element>
		</xsl:if>

<!--dc:format - general information-->
<!--use the combination of technical.online.mediums.medium and the technical.online.primaryURL to determine dc:format-->
<!--no dc:format for ADN offline resources-->

<!--dc:format -->
<!--dc:format use these terms: text, multipart, message, application, image, audio, video or model and the mime type list from the NSDL mime type list at: http://ns.nsdl.org/schemas/mime_type/mime_type_v1.00.xsd, and any free text that the ADN record has-->	
<!--dc:format - using ADN mediums; allows dc:format to repeat-->
<!--since medium repeats use a template-->
		<xsl:apply-templates select="d:technical/d:online/d:mediums/d:medium" mode="plain"/>
		<xsl:apply-templates select="d:technical/d:online/d:mediums/d:medium" mode="adn"/>
		<!--see template MEDIUM mode PLAIN-->
		<!--see template MEDIUM mode ADN-->
		
<!--	dc:format - using ADN primaryURL-->
<!--use a template because may want to use the same code again -->
		<xsl:apply-templates select="d:technical/d:online" mode="plain"/>
		<!--see template ONLINE mode PLAIN-->
		

<!--dc:type-->
<!--no vocabulary mapping is necessary-->
<!--determine if the ADN metadata record refers to an online or offline resource-->
		<xsl:choose>
			<xsl:when test="string-length(d:technical/d:offline)>0">
				<xsl:element name="dc:type">
					<xsl:text>PhysicalObject</xsl:text>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="d:educational/d:resourceTypes/d:resourceType"/>
				<!--see template RESOURCETYPE -->
			</xsl:otherwise>
		</xsl:choose>

<!--dc:type-->
<!--vocabulary mapping is necessary-->
<!--determine if the ADN metadata record refers to an online or offline resource-->
<!--to prevent duplicate dc:type make a variable and test it-->

<!--	variable for ADN educational.resourceTypes.resourceType-->
		<xsl:variable name="allresourceTypes">
			<xsl:for-each select="d:educational/d:resourceTypes/d:resourceType">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
	
		<xsl:choose>

<!--dc:type: Collection-->
			<xsl:when test="contains($allresourceTypes, 'DLESE:Portal')">
				<xsl:element name="dc:type">
					<xsl:text>Collection</xsl:text>
				</xsl:element>
			</xsl:when>

<!--dc:type: Dataset-->
			<xsl:when test="contains($allresourceTypes, 'DLESE:Data')">
				<xsl:element name="dc:type">
					<xsl:text>Dataset</xsl:text>
				</xsl:element>
			</xsl:when>

<!--dc:type: Event-->
			<xsl:when test="contains($allresourceTypes, 'Webcast')">
				<xsl:element name="dc:type">
					<xsl:text>Event</xsl:text>
				</xsl:element>
			</xsl:when>

<!--dc:type: Image-->
			<xsl:when test="contains($allresourceTypes, 'DLESE:Visual')">
				<xsl:element name="dc:type">
					<xsl:text>Image</xsl:text>
				</xsl:element>
			</xsl:when>

<!--dc:type: InteractiveResource - Removed 10/2016 -->
<!--
            <xsl:when test="contains($allresourceTypes, 'DLESE:Learning materials') or contains($allresourceTypes, 'Calculation')">
                <xsl:element name="dc:type">
                    <xsl:text>InteractiveResource</xsl:text>
                </xsl:element>
            </xsl:when>
-->

            <!--dc:type: Service-->
			<xsl:when test="contains($allresourceTypes, 'DLESE:Service')">
				<xsl:element name="dc:type">
					<xsl:text>Service</xsl:text>
				</xsl:element>
			</xsl:when>

<!--dc:type: Software-->
			<xsl:when test="contains($allresourceTypes, 'Code') or contains($allresourceTypes, 'Software')">
				<xsl:element name="dc:type">
					<xsl:text>Software</xsl:text>
				</xsl:element>
			</xsl:when>

<!--dc:type: Sound-->
			<xsl:when test="contains($allresourceTypes, 'Audio book') or contains($allresourceTypes, 'Lecture') or contains($allresourceTypes, 'Music') or contains($allresourceTypes, 'Oral') or contains($allresourceTypes, 'Radio') or contains($allresourceTypes, 'Sound')">
				<xsl:element name="dc:type">
					<xsl:text>Sound</xsl:text>
				</xsl:element>
			</xsl:when>

<!--dc:type: Text-->
			<xsl:when test="contains($allresourceTypes, 'DLESE:Text')">
				<xsl:element name="dc:type">
					<xsl:text>Text</xsl:text>
				</xsl:element>
			</xsl:when>

<!--dc:type: PhysicalObject-->
			<xsl:when test="string-length(d:technical/d:offline)>0">
				<xsl:element name="dc:type">
					<xsl:text>PhysicalObject</xsl:text>
				</xsl:element>
			</xsl:when>
		</xsl:choose>

<!--dc:identifier - using ADN id numbers-->
<!--all ADN records (online and offline) have a number that can be used as an indentifier for that collection-->
<!--		<xsl:element name="dc:identifier">
			<xsl:value-of select="concat(d:metaMetadata/d:catalogEntries/d:catalog, ': ', d:metaMetadata/d:catalogEntries/d:catalog/@entry)"/>
		</xsl:element> -->

<!--dc:identifier - using ADN primary urls-->
<!--only online ADN resource will have a dc:identifier-->
<!--simple DC does not deal with the attribute dct:URI, so no worry about spaces in urls-->
<!-- do an if test to exclude offline resources-->
		<xsl:if test="string-length(d:technical/d:online/d:primaryURL) > 0">
			<xsl:element name="dc:identifier">
				<xsl:value-of select="d:technical/d:online/d:primaryURL"/>
			</xsl:element>
		</xsl:if>

<!--dc:source-->		
<!--ADN does not collect dc:source information yet for either online or offline resources-->

<!--dc:language-->
<!--ADN does not collect dc:language- plain information-->

<!--dc:language-->
		<xsl:element name="dc:language">
			<xsl:value-of select="d:general/d:language"/>
		</xsl:element>

<!--dc:rights-->
		<xsl:element name="dc:rights">
			<xsl:value-of select="d:rights/d:description"/>
		</xsl:element>
	
<!--dc:coverage and dc:spatial general information-->
<!--only ADN large bounding box and associated placenames, not detailed geometries, are transformed-->
<!--put ADN large bound box placenames in dc:coverage-->
<!--put ADN large bound box coordinates in dc:spatial - xsi:type=dct:Box-->
<!-- ADN larg bound box event names are not transformed -->

<!--dc:coverage -ADN boundbox placenames-->
		<xsl:apply-templates select="d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:bbPlaces/d:place"/>
		<!--see template PLACE-->

<!--dc:coverage - for ADN boundbox coordinates-->
<!--no template used since only occurs once in ADN-->
		<xsl:if test="string-length(d:geospatialCoverages/d:geospatialCoverage/d:boundBox)>0">
			<xsl:element name="dc:coverage">
				<xsl:value-of select="concat('northlimit=', d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:northCoord, '; southlimit=', d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:southCoord, '; westlimit=', d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:westCoord, '; eastlimit=', d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:eastCoord, '; units=signed decimal degrees')"/>
			</xsl:element>
		</xsl:if>


<!--dc:coverage time information-->
<!--use dc:coverage for ADN timeAD and named time periods; template TIMEAD mode PLAIN-->
<!--use dc:coverge for ADN timeBC and named time period in timeBC; template TIMEBC-->
<!--use dc:coverage for ADN timeRelative; see template TIMERELATIVE-->

<!--process ADN timeAD tags first-->
<!--dc:coverage-->
		<xsl:apply-templates select="d:temporalCoverages/d:timeAndPeriod/d:timeInfo/d:timeAD"/>
		<!--see template TIMEAD-->

<!--process ADN timeBC-->
<!--dc:coverage-->
		<xsl:apply-templates select="d:temporalCoverages/d:timeAndPeriod/d:timeInfo/d:timeBC"/>
		<!--see template TIMEBC-->

<!--process ADN timeRelative-->		
<!--dc:coverage-->
		<xsl:apply-templates select="d:temporalCoverages/d:timeAndPeriod/d:timeInfo/d:timeRelative"/>
		<!--see template TIMERELATIVE-->

		
<!--dc:relation-->
<!--ADN DLESE:Has thumbnail is not transformed-->
		<xsl:apply-templates select="d:relations/d:relation//@kind"/>
		<!--see template KIND-->


<!--end oai_dc:dc-->
		<xsl:value-of select="$newline"/>
		<xsl:text disable-output-escaping="yes">&lt;/oai_dc:dc&gt;</xsl:text>
	</xsl:template>


<!--begin TEMPLATES TO APPLY. In alphabetical order by ADN field name-->
<!--**********************************************************************************************-->
<!--**********************************************************************************************-->
<!--**********************************************************************************************-->
<!--**********************************************************************************************-->
<!--**********************************************************************************************-->
<!--**********************************************************************************************-->

<!--BENEFICIARY template-->
	<xsl:template match="d:beneficiary">
		<xsl:element name="dc:description">
			<xsl:value-of select="concat('Beneficiary is: ', substring-after(., ':'))"/>
		</xsl:element>
	</xsl:template>

<!--CONTENTSTANDARD template-->
	<xsl:template match="d:contentStandard">
		<xsl:element name="dc:description">
			<xsl:choose>
<!--NSES-->
				<xsl:when test="contains(., 'NSES:')">
					<xsl:value-of select="concat('Supports National Science Education Standards (NSES): ', substring-after(., ':' ))"/>
				</xsl:when>
<!--AAAS-->
				<xsl:when test="contains(., 'AAASbenchmarks:')">
					<xsl:value-of select="concat('Supports American Association for the Advancement of Science (AAAS) Benchmarks: ', substring-after(., ':' ))"/>
				</xsl:when>
<!--NCGE-->
				<xsl:when test="contains(., 'NCGE:')">
					<xsl:value-of select="concat('Supports National Council for Geographic Education (NCGE) standard: ', substring-after(., ':' ))"/>
				</xsl:when>
<!--NCTM-->
				<xsl:when test="contains(., 'NCTM:')">
					<xsl:value-of select="concat('Supports National Council of Teachers of Mathemeatics (NCTM) standardE: ', substring-after(., ':' ))"/>
				</xsl:when>
<!--NETS-->
				<xsl:when test="contains(., 'NETS:')">
					<xsl:value-of select="concat('Supports National Educational Technology Standards (NETS): ', substring-after(., ':' ))"/>
				</xsl:when>
			</xsl:choose>
		</xsl:element>
	</xsl:template>

<!--GRADERANGE template-->
	<xsl:template match="d:gradeRange">
		<xsl:choose>
			<xsl:when test="contains(., 'supplied') or contains(., 'Not applicable')"/>
			<xsl:otherwise>
				<xsl:element name="dc:description">
					<xsl:value-of select="concat('Educational level is: ', substring-after(., ':'))"/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

<!-- KIND template-->
	<xsl:template match="d:relations/d:relation//@kind">

		<xsl:choose>
<!--call-template halts processing of current template in order to call and complete anther template; then processing of the current template is resumed; if did not do this would get the unwnted result of all the mediums (mime types) being in a single dc:format tag-->

<!--dc:relation - isVersionOf-->
			<xsl:when test="contains(., 'Is version of')">
				<xsl:element name="dc:relation">
					<xsl:text>isVersionOf: </xsl:text>
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dc:relation  hasVersion-->
			<xsl:when test="contains(., 'Has version')">
				<xsl:element name="dc:relation">
					<xsl:text>hasVersion: </xsl:text>
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dc:relation- isReplacedBy-->
			<xsl:when test="contains(., 'Is replaced by')">
				<xsl:element name="dc:relation">
					<xsl:text>isReplacedBy: </xsl:text>
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dc:relation - replaces-->
			<xsl:when test="contains(., 'Replaces')">
				<xsl:element name="dc:relation">
					<xsl:text>replaces: </xsl:text>
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dc:relation - isRequiredBy-->
			<xsl:when test="contains(., 'Is required by')">
				<xsl:element name="dc:relation">
					<xsl:text>isRequiredBy: </xsl:text>
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dc:relation - requires-->
			<xsl:when test="contains(., 'Requires')">
				<xsl:element name="dc:relation">
					<xsl:text>requires: </xsl:text>
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dc:relation - isPartOf-->
			<xsl:when test="contains(., 'Is part of')">
				<xsl:element name="dc:relation">
					<xsl:text>isPartOf: </xsl:text>
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dc:relation - hasPart-->
			<xsl:when test="contains(., 'Has part')">
				<xsl:element name="dc:relation">
					<xsl:text>hasPart: </xsl:text>
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dc:relation - isReferencedby-->
			<xsl:when test="contains(., 'Is referenced by') or contains(., 'Is basis for')">
				<xsl:element name="dc:relation">
					<xsl:text>isReferencedBy: </xsl:text>
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dc:relation - references-->
			<xsl:when test="contains(., 'References') or contains(., 'Is based on') or contains(., 'Is associated with')">
				<xsl:element name="dc:relation">
					<xsl:text>references: </xsl:text>				
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dc:relation - isFormatOf-->
			<xsl:when test="contains(., 'Is format of')">
				<xsl:element name="dc:relation">
					<xsl:text>isFormatOf: </xsl:text>
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dc:relation - hasFormat-->
			<xsl:when test="contains(., 'Has format')">
				<xsl:element name="dc:relation">
					<xsl:text>hasFormat: </xsl:text>
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dc:relation - conformsTo-->
			<xsl:when test="contains(., 'Conforms to')">
				<xsl:element name="dc:relation">
					<xsl:text>conformsTo: </xsl:text>
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>

		</xsl:choose>
	</xsl:template>

<!--MEDIUM template-->
<!--since ADN technical.online.mediums.medium is free text, only an educated guess for dc:format is possible-->
<!--does not assume default value if not match is found-->	
	<xsl:template name="d:medium">
		<xsl:choose>
<!--application test-->
			<xsl:when test="contains(., 'application')">
				<xsl:text>application</xsl:text>
			</xsl:when>
<!--message test-->
			<xsl:when test="contains(., 'message')">
				<xsl:text>message</xsl:text>
			</xsl:when>
<!--video test-->
			<xsl:when test="contains(., 'video')">
				<xsl:text>video</xsl:text>
			</xsl:when>
<!--audio test-->
			<xsl:when test="contains(., 'audio')">
				<xsl:text>audio</xsl:text>
			</xsl:when>
<!--image test-->
			<xsl:when test="contains(., 'image')">
				<xsl:text>image</xsl:text>
			</xsl:when>
<!--model test-->
			<xsl:when test="contains(., 'model')">
				<xsl:text>model</xsl:text>
			</xsl:when>
<!--multipart test-->
			<xsl:when test="contains(., 'multipart')">
				<xsl:text>multipart</xsl:text>
			</xsl:when>
<!--text test-->
			<xsl:when test="contains(., 'text')">
				<xsl:text>text</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>			

<!--MEDIUM template mode=ADN-->
	<xsl:template match="d:medium" mode="adn">
		<xsl:element name="dc:format">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>


<!--MEDIUM template mode=PLAIN-->
	<xsl:template match="d:medium" mode="plain">
<!--this template is intended to use the NSDL mimetype list. So if the data of the mimetype is not on the NSDL list, the resultant tag should not be blank. So check to make sure there is valid content to work with first. If not valid, the new dc:format-plain tag does not get written-->
		<xsl:if test="contains(., 'application') or contains(., 'message') or contains(., 'video') or contains(., 'audio') or contains(., 'image') or contains(., 'model') or contains(., 'multipart') or contains(., 'text')">
			<xsl:element name="dc:format">
			<!--call-template halts processing of current template in order to call and complete anther template; then processing of the current template is resumed; if did not do this would get the unwanted result of all the mediums (mime types) being in a single dc:format tag-->
				<xsl:call-template name="d:medium"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>


<!--ONLINE template mode=PLAIN-->
	<xsl:template match="d:technical/d:online" mode="plain">
		<xsl:element name="dc:format">
<!--use apply-templates here rather than call-template; when tried to use call-template, the primaryURL template could not be found. Since there is only 1 primaryURL and you can not use / in call-template, apply-templates works fine then.-->
			<xsl:apply-templates select="d:primaryURL"/>
			<!--see template PRIMARYURL-->
		</xsl:element>
	</xsl:template>


<!--ORGANIZATION template mode=CONTRIBUTOR-->
	<xsl:template match="d:organization" mode="contributor">
		<xsl:if test="../@role='Contributor' or ../@role='Editor'">
			<xsl:element name="dc:contributor">
				<xsl:choose>
					<xsl:when test="string-length(d:instDept)>0">
						<xsl:value-of select="concat(d:instDept,', ',d:instName)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="d:instName"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
		</xsl:if>
	</xsl:template>			

<!--ORGANIZATION template mode=CREATOR-->
	<xsl:template match="d:organization" mode="creator">
		<xsl:if test="../@role='Author' or ../@role='Principal Investigator'">
			<xsl:element name="dc:creator">
				<xsl:choose>
					<xsl:when test="string-length(d:instDept)>0">
						<xsl:value-of select="concat(d:instDept,', ',d:instName)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="d:instName"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
		</xsl:if>
	</xsl:template>			

<!--ORGANIZATION template mode=PUBLISHER-->
	<xsl:template match="d:organization" mode="publisher">
		<xsl:if test="../@role='Publisher'">
			<xsl:element name="dc:publisher">
				<xsl:choose>
					<xsl:when test="string-length(d:instDept)>0">
						<xsl:value-of select="concat(d:instDept,', ',d:instName)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="d:instName"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
		</xsl:if>
	</xsl:template>			

<!--PERSON template mode=CONTRIBUTOR-->
	<xsl:template match="d:person" mode="contributor">
		<xsl:if test="../@role='Contributor' or ../@role='Editor'">
			<xsl:element name="dc:contributor">
				<xsl:value-of select="concat(d:nameFirst,' ',d:nameLast)"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>			

<!--PERSON template mode=CREATOR-->
	<xsl:template match="d:person" mode="creator">
		<xsl:if test="../@role='Author' or ../@role='Principal Investigator'">
			<xsl:element name="dc:creator">
				<xsl:value-of select="concat(d:nameFirst,' ',d:nameLast)"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>			

<!--PERSON template mode=PUBLISHER-->
	<xsl:template match="d:person" mode="publisher">
		<xsl:if test="../@role='Publisher'">
			<xsl:element name="dc:publisher">
				<xsl:value-of select="concat(d:nameFirst,' ',d:nameLast)"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>	

<!--PLACE template-->
	<xsl:template match="d:place">
		<xsl:element name="dc:coverage">
			<xsl:value-of select="d:name"/>
		</xsl:element>
	</xsl:template>
	
<!--PRIMARYURL template -->
<!--makes an assumption of text/html if no match is found-->
	<xsl:template match="d:primaryURL">
		<xsl:choose>
			<xsl:when test="contains(., '.pdf')">
				<xsl:text>application/pdf</xsl:text>
			</xsl:when>											
			<xsl:when test="contains(., '.mov')">
				<xsl:text>video/quicktime</xsl:text>
			</xsl:when>											
			<xsl:when test="contains(., '.mpg')">
				<xsl:text>video/quicktime</xsl:text>
			</xsl:when>											
			<xsl:when test="contains(., '.jpg')">
				<xsl:text>image/jpeg</xsl:text>
			</xsl:when>											
			<xsl:when test="contains(., '.jpeg')">
				<xsl:text>image/jpeg</xsl:text>
			</xsl:when>											
			<xsl:when test="contains(., '.JPG')">
				<xsl:text>image/jpeg</xsl:text>
			</xsl:when>											
			<xsl:when test="contains(., '.gif')">
				<xsl:text>image/gif</xsl:text>
			</xsl:when>											
			<xsl:when test="contains(., '.txt')">
				<xsl:text>text/plain</xsl:text>
			</xsl:when>											
			<xsl:otherwise>
				<xsl:text>text/html</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>			

<!--RELATIONS template-->
	<xsl:template name="relations">
		<xsl:value-of select="../@url"/>
		<xsl:value-of select="../@entry"/>
	</xsl:template>	

<!--RESOURCETYPE template-->
	<xsl:template match="d:resourceType">
		<xsl:choose>
			<xsl:when test="contains(.,'supplied')"/>
			<xsl:otherwise>
				<xsl:element name="dc:type">
					<xsl:value-of select="substring-after(., 'DLESE:')"/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

<!--SUBJECT template mode=DLESE-->
	<xsl:template match="d:subject" mode="DLESE">
		<xsl:choose>
			<xsl:when test="contains(.,'Other') or contains(.,'supplied')"/>
			<xsl:otherwise>
				<xsl:element name="dc:subject">
					<xsl:value-of select="substring-after(.,'DLESE:')"/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
</xsl:template>			

<!--SUBJECT template mode=KEYWORDS-->
	<xsl:template match="d:keyword" mode="keywords">
		<xsl:element name="dc:subject">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>			

<!--SUBJECT template mode=NSDL_DC:GEM-->
	<xsl:template match="d:subject" mode="nsdl_dc:GEM">
		<xsl:if test="contains(., 'Agriculture') or 
						contains(., 'Biology') or
						contains(., 'Ecology') or
						contains(., 'Educational') or
						contains(., 'History') or
						contains(., 'Mathematics') or
						contains(., 'Paleontology') or
						contains(., 'Physics') or
						contains(., 'Space') or
						contains(., 'Technology')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">
					<xsl:text>nsdl_dc:GEM</xsl:text>
				</xsl:attribute>
				<xsl:choose>

<!--NOTE: not mapped: Forestry, Policy issues, soil science-->
<!--still test for Agriculture, Biology, Mathematics, Paleontology, Physics,  Space and Technology even though they are already be present from the DLESE subject list, because now they will be associated with the GEM type-->
<!--Agriculture-->
					<xsl:when test="contains(., 'Agriculture')">
						<xsl:text>Agriculture</xsl:text>
					</xsl:when>
<!--Biology-->
					<xsl:when test="contains(., 'Biology')">
						<xsl:text>Biology</xsl:text>
					</xsl:when>
<!--Ecology-->
					<xsl:when test="contains(., 'Ecology')">
						<xsl:text>Ecology</xsl:text>
					</xsl:when>
<!--Education-->
					<xsl:when test="contains(., 'Educational')">
						<xsl:text>Education (General)</xsl:text>
					</xsl:when>
<!--History of science-->
					<xsl:when test="contains(., 'History')">
						<xsl:text>History of science</xsl:text>
					</xsl:when>
<!--Mathematics-->
					<xsl:when test="contains(., 'Mathematics')">
						<xsl:text>Mathematics</xsl:text>
					</xsl:when>
<!--Natural history-->
					<xsl:when test="contains(., 'Paleontology')">
						<xsl:text>Natural history</xsl:text>
					</xsl:when>
<!--Paleontology-->
					<xsl:when test="contains(., 'Paleontology')">
						<xsl:text>Paleontology</xsl:text>
					</xsl:when>
<!--Physics-->
					<xsl:when test="contains(., 'Physics')">
						<xsl:text>Physics</xsl:text>
					</xsl:when>
<!--Astronomy-->
					<xsl:when test="contains(., 'Space')">
						<xsl:text>Astronomy</xsl:text>
					</xsl:when>
<!--Space-->
					<xsl:when test="contains(., 'Space')">
						<xsl:text>Space sciences</xsl:text>
					</xsl:when>
<!--Technology-->
					<xsl:when test="contains(., 'Technology')">
						<xsl:text>Technology</xsl:text>
					</xsl:when>
				</xsl:choose>
			</xsl:element>
			<xsl:value-of select="$newline"/>
		</xsl:if>
	</xsl:template>	 		

<!--TIMEAD template-->
	<xsl:template match="d:timeAD">
		<xsl:element name="dc:coverage">
<!--determine if there is a named time period in order to make the tag content correct-->
<!--when ADN named time periods are transformed only the first named time period is transformed, all others are ignored; nsdl_dc does not allow for more than one named time period-->
			<xsl:choose>
				<xsl:when test="string-length(../../d:periods)>0">
<!--has the term 'Present' been used in the ADN fields of begin and end date; if so need to write the value without a W3CDTF  scheme because 'Present' is not W3CDTF compliant-->
					<xsl:choose>
						<xsl:when test="d:begin/@date='Present' or d:end/@date='Present'">
							<xsl:value-of select="concat('start=', d:begin/@date, ';end=', d:end/@date, ';name=', ../../d:periods/d:period/d:name)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat('start=', d:begin/@date, ';end=', d:end/@date, ';scheme=W3CDTF;name=', ../../d:periods/d:period/d:name)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
<!--has the term 'Present' been used in the ADN fields of begin and end date; if so need to write the value without a W3CDTF  scheme because 'Present' is not W3CDTF compliant-->
					<xsl:choose>
						<xsl:when test="d:begin/@date='Present' or d:end/@date='Present'">
							<xsl:value-of select="concat('start=', d:begin/@date, ';end=', d:end/@date)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat('start=', d:begin/@date, ';end=', d:end/@date, ';scheme=W3CDTF')"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>	
		</xsl:element>
	</xsl:template>

<!--TIMEBC template-->
	<xsl:template match="d:timeBC">
		<xsl:element name="dc:coverage">
<!--determine if there is a named time period in order to make the tag content correct-->
<!--when ADN named time periods are transformed only the first named time period is transformed, all others are ignored; nsdl_dc does not allow for more than one named time period-->
			<xsl:choose>
				<xsl:when test="string-length(../../d:periods)>0">
					<xsl:value-of select="concat('start=', d:begin, ' BC;end=', d:end, ' BC;name=', ../../d:periods/d:period/d:name)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat('start=', d:begin, ' BC;end=', d:end, ' BC')"/>
				</xsl:otherwise>
			</xsl:choose>	
		</xsl:element>
	</xsl:template>

<!--TIMERELATIVE template-->
	<xsl:template match="d:timeRelative">
		<xsl:element name="dc:coverage">
<!--determine if there is a named time period in order to make the tag content correct-->
<!--when ADN named time periods are transformed only the first named time period is transformed, all others are ignored; nsdl_dc does not allow for more than one named time period-->
			<xsl:choose>
				<xsl:when test="string-length(../../d:periods)>0">
					<xsl:value-of select="concat('start=', d:begin, ' ', d:begin/@units, ';end=', d:end, ' ', d:end/@units, ';name=', ../../d:periods/d:period/d:name)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat('start=', d:begin, ' ', d:begin/@units, ';end=', d:end, ' ', d:end/@units)"/>
				</xsl:otherwise>
			</xsl:choose>	
		</xsl:element>
	</xsl:template>

<!--TOOLFOR template-->
	<xsl:template match="d:toolFor">
		<xsl:element name="dc:description">
			<xsl:value-of select="concat('Tool for: ', substring-after(., ':'))"/>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>
