<?xml version="1.0"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:d="http://adn.dlese.org"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    exclude-result-prefixes="xsi d" 
    version="1.0">

<!--ORGANIZATION OF THIS FILE-->
<!-- **************************************-->
<!--This file is organized into the following sections:
Purpose
License information and credits
Assumptions
Transformation code
Templates to apply (in alphabetical order)-->


<!--PURPOSE-->
<!-- **************************************-->
<!--To transform the Digital Library for Earth System Education (DLESE) ADN metadata records to simple Dublin Core that uses the Open Archives Initiative (OAI) namespace-->


<!--LICENSE INFORMATION and CREDITS-->
<!-- *****************************************************-->
<!--Date created: 2006-11-08 by Katy Ginger, DLESE Program Center, University Corporation for Atmospheric Research (UCAR)-->
<!--Last modified: 2006-11-13 by Katy Ginger-->
<!--License information:
		Copyright (c) 2002-2006, 2005 DLESE Program Center
		University Corporation for Atmospheric Research (UCAR)
		P.O. Box 3000, Boulder, CO 80307, United States of America
		email: support@dlese.org. 
		All rights reserved
These XML tranformation written in XSLT 1.0 and XPATH 1.0 are free software; you can redistribute them and/or modify them under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.  These XML instance documents are distributed in the hope that they will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this project; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA -->

    
<!--ASSUMPTIONS-->
<!-- **************************************-->
<!--Overarching assumption. The metadata field only appear in the ADN metadata record if it contains data-->
<!--1. Applies to DLESE ADN metadata format, version 0.6.50 records-->
<!--2. Unless otherwise indicated in this stylesheet, the transform applies to both ADN online and offline resources-->
<!--3. Transforms only ADN large bounding box and it associated placenames, not detailed geometries-->
<!--4. ADN relations.relation.kind=DLESE:Has thumbnail is not transformed-->
<!--5. ADN objectInSpace tags are not transformed-->
<!--6. Transforms only the first named time perion (temporalCoverages.timeAndPeriod.periods.name); all others are ignored because nsdl_dc does not allow for more than one named time period; so do the same for oai_dc-->
<!--7. ADN toolfor and beneficiary fields are not transformed-->
<!--8. ADN content standards are mapped to DC subject-->
<!--9. ADN grade ranges are concatenated onto the end of dc:description-->
<!--10. Assumes a mime type of text/html from the URL if the URL has content but does not match any of the listed mime types-->
<!--11. Does not assume ADN records are valid so there could be ADN elements with no content present-->
<!--12. Generally checks are made to ensure ADN elements have content before being transformed. In the case of ADN elements with controlled vocabularies, sometimes additional checks are performed to ensure the data is part of the controlled vocabulary (to help reduce the possibility of transforming nonsense). If these checks are not successful occassionally, nonsense is transformed and a DC element is written-->
<!--13. Complete data checking when ADN records are not valid is not possible within the scope of this transform so some nonsense that does not fit desired encoding schemes can creep into the transformed output-->
<!--14. ADN educational.audiences.audience.instructionalGoal  or typicalAgeRange is not transformed-->		
<!--15. When possible NSES standards are transformed to use the ASN identifiers; Geography standards are transformed and outputted as text-->
<!--16. Process and teaching standards are not transformed-->
<!--17. ADN simple places, event and temporal information are transformed-->
<!--18. Extra descriptions associated with each major ADN section are not transformed; only the general description-->
<!--19. For resource type, if content is not part of the ADN vocabulary, the content does not get transformed-->


<!--OTHER STYLESHEETS to include-->
<!--*****************************************-->
	<xsl:include href="mimetypes-wno-xsi-type.xsl"/>

	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

<!--VARIABLES used throughout the transform-->
<!-- **********************************************************-->
<!--variable for accessing DLESE webservice when given a DLESE library id-->
<!--webservice 1-1 has namespaces unlike 1-1 so account for them-->
	<xsl:variable name="DDSWSID">http://www.dlese.org/dds/services/ddsws1-1?verb=GetRecord&amp;id=</xsl:variable>	
	<xsl:variable name="reqVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/requirementTypeDLESE.xsd</xsl:variable>
	<xsl:variable name="typeVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/resourceTypeDLESE.xsd</xsl:variable>
	<xsl:variable name="subjVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/subjectDLESE.xsd</xsl:variable>
	<xsl:variable name="gradeVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/gradeRangeDLESE.xsd</xsl:variable>
	<xsl:variable name="NSESVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/standardsNSEScontent.xsd</xsl:variable>
	<xsl:variable name="NCGEVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/standardsNCGE.xsd</xsl:variable>
	<xsl:variable name="NCTMVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/standardsNCTMcontent.xsd</xsl:variable>
	<xsl:variable name="NETSVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/standardsNETScontent.xsd</xsl:variable>
	<xsl:variable name="commonVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/standardsAAASbenchmarksContentCommon.xsd</xsl:variable>
	<xsl:variable name="designVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/standardsAAASbenchmarksContentDesigned.xsd</xsl:variable>
	<xsl:variable name="habitsVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/standardsAAASbenchmarksContentHabits.xsd</xsl:variable>
	<xsl:variable name="historyocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/standardsAAASbenchmarksContentHistorical.xsd</xsl:variable>
	<xsl:variable name="humanVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/standardsAAASbenchmarksContentHuman.xsd</xsl:variable>
	<xsl:variable name="livingVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/standardsAAASbenchmarksContentLiving.xsd</xsl:variable>
	<xsl:variable name="mathVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/standardsAAASbenchmarksContentMath.xsd</xsl:variable>
	<xsl:variable name="physicalVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/standardsAAASbenchmarksContentPhysical.xsd</xsl:variable>
	<xsl:variable name="scienceVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/standardsAAASbenchmarksContentScience.xsd</xsl:variable>
	<xsl:variable name="techVocabURL">http://www.dlese.org/Metadata/adn-item/0.6.50/vocabs/standardsAAASbenchmarksContentTechnology.xsd</xsl:variable>


<!--TRANSFORMATION CODE-->
<!-- **********************************************************-->
	<xsl:template match="d:itemRecord">
		<xsl:element name="oai_dc:dc" namespace="http://www.openarchives.org/OAI/2.0/oai_dc/">
			<xsl:attribute name="xsi:schemaLocation">http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd</xsl:attribute>	


<!--dc:title-->
		<xsl:if test="string-length(d:general/d:title) > 0">
			<xsl:element name="dc:title">
				<xsl:value-of select="d:general/d:title"/>
			</xsl:element>	
		</xsl:if>

<!--dc:subject - from subject field-->
		<xsl:apply-templates select="d:general/d:subjects/d:subject" mode="subjects"/>
		<!--see template SUBJECT mode=subjects-->

<!--dc:subject - from keyword field-->
		<xsl:apply-templates select="d:general/d:keywords/d:keyword" mode="keywords"/> 
		<!--see template SUBJECT mode=KEYWORDS-->

<!--dc:subject - from content standards field-->
		<xsl:apply-templates select="d:educational/d:contentStandards/d:contentStandard"/>
		<!--see template CONTENTSTANDARD-->


<!--dc:date-->
<!--since ADN has many lifecycle.contributors.contributor.date values and these are not required, determine if any are present using a variable to grab all of them-->
<!--simple DC recommendation is to follow the W3CDTF of YYYY-MM-DD-->
 
<!--	variable for ADN lifecycle.contributors.contributor.date-->
		<xsl:variable name="alldates">
			<xsl:for-each select="d:lifecycle/d:contributors/d:contributor/@date">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		
		<xsl:if test="string-length($alldates)>0">
		<!--variable to grab the date of the first occurring date attribute only-->
			<xsl:variable name="dateStr" select="string(normalize-space(d:lifecycle/d:contributors/d:contributor/@date))"/>
			<xsl:variable name="lowercase" select="translate($dateStr, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>
<!-- variables for format checking -->
			<xsl:variable name="yearStr" select="substring($lowercase, 1, 4)"/>
			<xsl:variable name="firstSep" select="substring($lowercase, 5, 1)"/>
			<xsl:variable name="monthStr" select="substring($lowercase, 6, 2)"/>
			<xsl:variable name="secondSep" select="substring($lowercase, 8, 1)"/>
			<xsl:variable name="dayStr" select="substring($lowercase, 9, 2)"/>
	
<!--checking for xsd:gYear (but assuming in CCYY format because to hard to do more) -->
<!--checking for xsd:gYearMonth (but assuming in CCYY-MM format because to hard to do more) -->
<!--checking for xsd:date (but assuming in CCYY-MM-DD format because to hard to do more) -->
<!--NOTE: not checking for correct association of day with month (i.e. 2-30, 9-31 would be 'valid' here )-->
<!--NOTE: xsd:dateTime is valid, not checking for it -->	
<!--NOTE: anything else does not get transformed-->						
			<xsl:if test="(string-length($lowercase) = '4' and not(string(number($yearStr)) = 'NaN')) or 
							(string-length($lowercase) = '7' and not(string(number($yearStr)) = 'NaN') and ($firstSep = '-') and
											not (string(number($monthStr)) = 'NaN') and ($monthStr &gt; '0' ) and ($monthStr &lt; '13' )) or
							(string-length($lowercase) = '10' and	not (string(number($yearStr)) = 'NaN')  and 
											($firstSep = '-') and not (string(number($monthStr)) = 'NaN') and
											 ($monthStr &gt; '0' ) and ($monthStr &lt; '13' ) and ($secondSep = '-') and
											 not (string(number($dayStr)) = 'NaN') and ($dayStr &gt; '0' ) and ($dayStr &lt; '32' ))">
				<xsl:element name="dc:date">
					<xsl:value-of select="d:lifecycle/d:contributors/d:contributor/@date"/>
				</xsl:element>	
			</xsl:if>
		</xsl:if>	
<!--end: dc:date-->

<!--dc:description-->
<!--a single dc:description field is constructed from the following ADN fields: general.description, technical.offline.accessInformation, technical.offline.objectDescription and educational.audiences.audience.gradeRange-->

<!--use a variable to gather up the ADN fields that make description-->
		<xsl:variable name="descr">
			<xsl:choose>
<!--an offline resource with access information, a general description and an object description-->
			<xsl:when test="string-length(d:technical/d:offline/d:accessInformation)>0 and  string-length(d:technical/d:offline/d:objectDescription)>0 and string-length(d:general/d:description)>0">
				<xsl:element name="dc:description">
					<xsl:value-of select="concat(d:general/d:description,' ', d:technical/d:offline/d:objectDescription,' This is an offline resource with the following access information: ',d:technical/d:offline/d:accessInformation)"/>
				</xsl:element>
			</xsl:when>
<!--an offline resource with a general description and an object description-->
			<xsl:when test="string-length(d:technical/d:offline/d:objectDescription)>0 and string-length(d:general/d:description)>0">
				<xsl:element name="dc:description">
					<xsl:value-of select="concat(d:general/d:description,' ', d:technical/d:offline/d:objectDescription)"/>
				</xsl:element>
			</xsl:when>
<!--an offline resource with access information, a general description-->
			<xsl:when test="string-length(d:technical/d:offline/d:accessInformation)>0 and string-length(d:general/d:description)>0">
				<xsl:element name="dc:description">
					<xsl:value-of select="concat(d:general/d:description,' This is an offline resource with the following access information: ',d:technical/d:offline/d:accessInformation)"/>
				</xsl:element>
			</xsl:when>
<!--an offline resource with access information and an object description-->
			<xsl:when test="string-length(d:technical/d:offline/d:accessInformation)>0 and  string-length(d:technical/d:offline/d:objectDescription)>0">
				<xsl:element name="dc:description">
					<xsl:value-of select="concat(d:technical/d:offline/d:objectDescription,' This is an offline resource with the following access information: ',d:technical/d:offline/d:accessInformation)"/>
				</xsl:element>
			</xsl:when>
<!--an online or offline resource with just a general.description-->
			<xsl:when test="string-length(d:general/d:description)>0">
				<xsl:element name="dc:description">
					<xsl:value-of select="d:general/d:description"/>
				</xsl:element>
			</xsl:when>
<!--if there is no no content in any of the above elements do nothing-->
			<xsl:otherwise/>
			</xsl:choose>
		</xsl:variable>

<!--use a variable to grab all grade ranges from the data and the controlled vocabulary-->
<!--first put the controlled vocabulary into a variable-->
		<xsl:variable name="gradeVocab">
			<xsl:for-each select="document($gradeVocabURL)//xsd:restriction/xsd:enumeration">
				<xsl:value-of select="@value"/>
			</xsl:for-each>
		</xsl:variable>

<!--then put the grade range data into a variable-->
<!--compare the grade range data to the controlled vocabulary terms-->
<!--make a concatenated list of grade range data if the data are in the controlled vocabulary; this list will be conctenated onto the end of the description field-->
<!--repeated grade ranges are not suppressed-->
		<xsl:variable name="allgradeRanges">
			<xsl:for-each select="d:educational/d:audiences/d:audience">
				<xsl:choose>
					<xsl:when test="contains($gradeVocab, d:gradeRange) and starts-with(d:gradeRange, 'DLESE:') and string-length(d:gradeRange) > 0">
						<xsl:value-of select="concat(substring-after(d:gradeRange, 'DLESE:'), '; ')"/>
					</xsl:when>
					<xsl:when test="contains($gradeVocab, d:gradeRange) and not(contains(d:gradeRange, 'DLESE:')) and string-length(d:gradeRange) > 0">
						<xsl:value-of select="concat(d:gradeRange, '; ')"/> <!--in case a data value is just 'Public'-->
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
		</xsl:variable>

<!--create description using the variables above but check to see if $allgradeRanges and $descr have data-->
			<xsl:choose>
				<xsl:when test="string-length($allgradeRanges) > 0 and string-length($descr) > 0">
					<xsl:element name="dc:description">
						<xsl:value-of select="concat($descr, ' Educational levels: ', $allgradeRanges)"/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="string-length($allgradeRanges) > 0 and string-length($descr) = 0">
					<xsl:element name="dc:description">
						<xsl:value-of select="concat('Educational levels: ', $allgradeRanges)"/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="string-length($allgradeRanges) = 0 and string-length($descr) > 0">
					<xsl:element name="dc:description">
						<xsl:value-of select="$descr"/>
					</xsl:element>					
				</xsl:when>
				<xsl:otherwise/>
			</xsl:choose>

<!--ADN has the notion of a contact in addition to creator, publisher or contributor; however, contact is not mapped-->
<!--dc:creator - person-->
		<xsl:apply-templates select="d:lifecycle/d:contributors/d:contributor/d:person" mode="creator"/>
		<xsl:apply-templates select="d:lifecycle/d:contributors/d:contributor/d:organization" mode="creator"/>
		<!--see template PERSON mode=CREATOR-->
		<!--see template ORGANIZATION mode=CREATOR-->

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
<!--dc:format is defined as the physical or digital manifestation of the resource. Format may include the media-type (mime), dimensions, size and duration and format may be used to determine the software, hardware or other equipment needed to display or operate the resource-->
<!--there is no dc:format ADN offline resources-->
<!--use the combination of technical.online.mediums.medium and the technical.online.primaryURL to determine dc:format concept of mime type and write it as a plaing dc:format element-->
<!--use technical.online.requirements and technical.online.otherRequirements to determine dc:format concept of software or hardware (or other equipment) needed to operate or display the resource and write it as a plain dc:format element-->
<!--use technical.online.size to determine dc:formt concept of size and write it as a plain dc:format element-->

<!--dc:format using size-->
		<xsl:if test="string-length(d:technical/d:online/d:size) > 0">
			<xsl:element name="dc:format">
				<xsl:value-of select="d:technical/d:online/d:size"/>
			</xsl:element>
		</xsl:if>

<!--dc:format - using ADN mediums--> 
<!--must select from NSDL list at http://ns.nsdl.org/schemas/mime_type/mime_type_v1.00.xsd-->
<!--since mediums is free text in ADN, create a variable and test and only write dc:format - type=dct:IMT as needed at the broad mime type category (i.e. text, application, video, audio, image, model, multipart or message) because do not know if the finer free text level (e.g. text/html) in ADN medium would be an accepted term in the mime type vocabulary-->

<!--	variable for ADN technical.online.mediums/medium-->
		<xsl:variable name="mediums">
			<xsl:for-each select="d:technical/d:online/d:mediums/d:medium">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>

		<xsl:variable name="allmediums" select="translate($mediums, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>

<!--test to see if ADN medium information is present, if so write the dc:format at a broad mime type category-->
<!--the set of if statements rather than a template eliminates repeating output elements with the same content but allows for repeating output elements with different content-->
<!--this has been verified as working-->
		<xsl:if test="contains($allmediums, 'application')">
			<xsl:element name="dc:format">
				<xsl:text>application</xsl:text>
			</xsl:element>
		</xsl:if> 
		<xsl:if test="contains($allmediums, 'message')">
			<xsl:element name="dc:format">
				<xsl:text>message</xsl:text>
			</xsl:element>
		</xsl:if> 
		<xsl:if test="contains($allmediums, 'video')">
			<xsl:element name="dc:format">
				<xsl:text>video</xsl:text>
			</xsl:element>
		</xsl:if> 
		<xsl:if test="contains($allmediums, 'audio')">
			<xsl:element name="dc:format">
				<xsl:text>audio</xsl:text>
			</xsl:element>
		</xsl:if> 
		<xsl:if test="contains($allmediums, 'image')">
			<xsl:element name="dc:format">
				<xsl:text>image</xsl:text>
			</xsl:element>
		</xsl:if> 
		<xsl:if test="contains($allmediums, 'model')">
			<xsl:element name="dc:format">
				<xsl:text>model</xsl:text>
			</xsl:element>
		</xsl:if> 
		<xsl:if test="contains($allmediums, 'multipart')">
			<xsl:element name="dc:format">
				<xsl:text>multipart</xsl:text>
			</xsl:element>
		</xsl:if> 
		<xsl:if test="contains($allmediums, 'text')">
			<xsl:element name="dc:format">
				<xsl:text>text</xsl:text>
			</xsl:element>
		</xsl:if> 


<!--dc:format - using ADN primaryURL-->
		<xsl:if test="string-length(d:technical/d:online/d:primaryURL) > 0">
			<xsl:call-template name="mimetype"/>
		</xsl:if>

<!--dc:format - plain using ADN technical.requirements.requirement-->
<!--since ADN technical.requirements.requirement is a compound repeating tag, use a template-->
		<xsl:apply-templates select="d:technical/d:online/d:requirements/d:requirement"/>
		<!--see template REQUIREMENT-->

<!--dc:format - plain using ADN technical.otherRequirements.otherRequirement-->
<!--since ADN technical.otherRequirements.otherRequirement is a compound repeating tag, use a template-->
		<xsl:apply-templates select="d:technical/d:online/d:otherRequirements/d:otherRequirement"/>
		<!--see template OTHER REQUIREMENT-->
			

<!--dc:type-->
<!--uses ADN educational.resourceTypes.resourceType-->
<!--no vocabulary mapping is necessary because providing ADN terms-->
		<xsl:apply-templates select="d:educational/d:resourceTypes/d:resourceType"/>
		<!--see template RESOURCETYPE-->

<!--dc:type-->
<!--maps ADN vocabulary to the simple Dublin Core (DCMI) resource types-->
<!--determine if the ADN metadata record refers to an online or offline resource-->
<!--to prevent duplicate dc:type make a variable and test it-->

<!--	variable for ADN educational.resourceTypes.resourceType-->
		<xsl:variable name="allresourceTypes">
			<xsl:for-each select="d:educational/d:resourceTypes/d:resourceType">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
	
<!--dc:type: Collection-->
		<xsl:if test="contains($allresourceTypes, 'DLESE:Portal')">
			<xsl:element name="dc:type">
				<xsl:text>Collection</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dc:type: Dataset-->
		<xsl:if test="contains($allresourceTypes, 'DLESE:Data')">
			<xsl:element name="dc:type">
				<xsl:text>Dataset</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dc:type: Event-->
		<xsl:if test="contains($allresourceTypes, 'Webcast')">
			<xsl:element name="dc:type">
				<xsl:text>Event</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dc:type: Image-->
		<xsl:if test="contains($allresourceTypes, 'DLESE:Visual')">
			<xsl:element name="dc:type">
				<xsl:text>Image</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dc:type: InteractiveResource - Removed 10/2016 -->
<!--
		<xsl:if test="contains($allresourceTypes, 'DLESE:Learning materials') or contains($allresourceTypes, 'Calculation')">
			<xsl:element name="dc:type">
				<xsl:text>InteractiveResource</xsl:text>
			</xsl:element>
		</xsl:if>
-->
<!--dc:type: Service-->
		<xsl:if test="contains($allresourceTypes, 'DLESE:Service')">
			<xsl:element name="dc:type">
				<xsl:text>Service</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dc:type: Software-->
		<xsl:if test="contains($allresourceTypes, 'Code') or contains($allresourceTypes, 'Software')">
			<xsl:element name="dc:type">
				<xsl:text>Software</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dc:type: Sound-->
		<xsl:if test="contains($allresourceTypes, 'Audio book') or contains($allresourceTypes, 'Lecture') or contains($allresourceTypes, 'Music') or contains($allresourceTypes, 'Oral') or contains($allresourceTypes, 'Radio') or contains($allresourceTypes, 'Sound')">
			<xsl:element name="dc:type">
				<xsl:text>Sound</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dc:type: Text-->
		<xsl:if test="contains($allresourceTypes, 'DLESE:Text')">
			<xsl:element name="dc:type">
				<xsl:text>Text</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dc:type: PhysicalObject-->
		<xsl:if test="string-length(d:technical/d:offline/d:accessInformation)>0 or string-length(d:technical/d:offline/d:objectDescription)>0">
			<xsl:element name="dc:type">
				<xsl:text>PhysicalObject</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dc:identifier - using ADN catalog record id numbers-->
<!--all ADN records (online and offline) have a catalog record number that can be used as an indentifier for that collection-->
<!--but the catalog record number does not have meaning outside DLESE, so do not transform-->
<!--	<xsl:element name="dc:identifier">
			<xsl:value-of select="concat(d:metaMetadata/d:catalogEntries/d:catalog, ': ', d:metaMetadata/d:catalogEntries/d:catalog/@entry)"/>
		</xsl:element>-->

<!--dc:identifier - using ADN primary urls-->
<!--only online ADN resource will have a dc:identifier-->
<!--simple DC does not deal with the attribute dct:URI, so no worry about spaces in urls-->
<!-- do an if test to exclude offline resources-->
		<xsl:if test="string-length(d:technical/d:online/d:primaryURL) > 0">
			<xsl:element name="dc:identifier">
				<xsl:value-of select="d:technical/d:online/d:primaryURL"/>
			</xsl:element>
		</xsl:if>

<!--dc: source using ADN relations.relation.idEntry and relation.relations.urlEntry-->		
<!--dc:source definition is a reference to a resource form which the present resource is derived. The present resource may be derived from the source resource in whole or part. Include info about a resource that is related intellectually to the described resource but does not fit easily into a relation element-->
<!--dc:source can be text or a URL; the intent here is to have it be a URL-->
<!--ADN collects dc:source because it has the concept of 'Is based on' which is not part of the typical dc:relation concepts-->

<!--use ADN relations.relation.idEntry and urlEntry when the kind attribute has a value of 'Is based on' to output dc:source as a URL-->
<!--outputs only dc:source not dc:source - dct:URI because not doing any checks to ensure meeting dct:URI requirements-->
<!--use DLESE webservice to resolve id numbers to URLs-->

<!--ADN relations.relation.urlEntry can repeat, so use a template-->
		<xsl:apply-templates select="d:relations/d:relation/d:urlEntry" mode="source"/>
		
<!--ADN relations.relation.idEntry can repeat, so use a template-->
		<xsl:apply-templates select="d:relations/d:relation/d:idEntry" mode="source"/>

<!--dc:language-->
		<xsl:if test="string-length(d:general/d:language) > 0">
			<xsl:element name="dc:language">
				<xsl:value-of select="d:general/d:language"/>
			</xsl:element>
		</xsl:if>

<!--dc:rights-->
		<xsl:if test="string-length(d:rights/d:description) > 0">
			<xsl:element name="dc:rights">
				<xsl:value-of select="d:rights/d:description"/>
			</xsl:element>
		</xsl:if>
		
<!--dc:coverage -general spatial information-->
<!--only ADN large bounding box and associated placenames, not detailed geometries, are transformed-->
<!--put ADN large bound box placenames in dc:coverage-->
<!--put ADN large bound box coordinates in dc:coverage-->

<!--dc:coverage using places and events-->
		<xsl:apply-templates select="d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:bbPlaces/d:place"/>
		<xsl:apply-templates select="d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:bbEvents/d:event"/>
		<xsl:apply-templates select="d:geospatialCoverages/d:geospatialCoverage/d:detGeos/d:detGeo/d:detPlaces/d:place"/>
		<xsl:apply-templates select="d:geospatialCoverages/d:geospatialCoverage/d:detGeos/d:detGeo/d:detEvents/d:event"/>
		<!--see template PLACE and EVENT-->

		<xsl:apply-templates select="d:general/d:simplePlacesAndEvents/d:placeAndEvent"/>
		<!--see template PLACE and EVENT - SIMPLE-->

<!--dc:coverage - for ADN boundbox coordinates-->
<!--no template used since only occurs once in ADN-->
		<xsl:if test="string-length(d:geospatialCoverages/d:geospatialCoverage/d:boundBox)>0">
			<xsl:element name="dc:coverage">
				<xsl:value-of select="concat('northlimit=', d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:northCoord, '; southlimit=', d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:southCoord, '; westlimit=', d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:westCoord, '; eastlimit=', d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:eastCoord, '; units=signed decimal degrees')"/>
			</xsl:element>
		</xsl:if> 


<!--dc:coverage - general time information-->
<!--use dc:coverage for ADN timeAD and named time periods; template TIMEAD-->
<!--use dc:coverage for ADN timeBC and named time period in timeBC; template TIMEBC-->
<!--use dc:coverage for ADN timeRelative; see template TIMERELATIVE-->

<!--dc:coverage for ADN timeAD-->
		<xsl:apply-templates select="d:temporalCoverages/d:timeAndPeriod/d:timeInfo/d:timeAD"/>
		<!--see template TIMEAD-->

<!--dc:coverage for ADN timeBC-->
		<xsl:apply-templates select="d:temporalCoverages/d:timeAndPeriod/d:timeInfo/d:timeBC"/>
		<!--see template TIMEBC-->

<!--process ADN timeRelative-->		
		<xsl:apply-templates select="d:temporalCoverages/d:timeAndPeriod/d:timeInfo/d:timeRelative"/>
		<!--see template TIMERELATIVE-->

<!--dct:temporal for simpleTemporalCoverages-->
		<xsl:apply-templates select="d:general/d:simpleTemporalCoverages/d:description"/>
		<!--see template TEMPORAL DESCRIPTION-->

		
<!--dc:relation-->
<!--ADN DLESE:Has thumbnail is not transformed-->
<!--ADN relations.relation.urlEntry can repeat, so use a template-->
		<xsl:apply-templates select="d:relations/d:relation/d:urlEntry" mode="relations"/>
<!--see template URL ENTRY mode=RELATIONS-->
		
<!--ADN relations.relation.idEntry can repeat, so use a template-->
		<xsl:apply-templates select="d:relations/d:relation/d:idEntry" mode="relations"/>
<!--see template ID ENTRY mode=RELATIONS-->


<!--end oai_dc:dc-->
		</xsl:element>
	</xsl:template>


<!--TEMPLATES TO APPLY (alphabetical order)-->
<!--**********************************************************-->
<!--1.   CONTENTSTANDARD writes DC subject with ADN content standards information-->
<!--2.   ID-ENTRY template; mode=RELATIONS determines if ADN relation.relation.idEntry.entry exists in the library using webservices-->
<!--3.   ID-ENTRY template; mode=SOURCE writes dc:source when ADN relations.relation.idEntry.kind='Is based on'-->
<!--4.   KIND writes DC relation with the kind of relationship being established, isVersion, hasPart etc.-->
<!--5.	ORGANIZATION mode=contributor writes dc:contributor-->
<!--6. 	ORGANIZATION mode=creator writes dc:creator-->
<!--7. 	ORGANIZATION mode=publisher writes dc:publisher-->
<!--8. 	OTHER REQUIREMENT writes dc:format-->
<!--9.   PERSON mode=contributor writes dc:contributor-->
<!--10. PERSON mode=creator writes dc:creator-->
<!--11. PERSON mode=pubisher writes dc:publisher-->
<!--12.	PLACE  and EVENT writes dc:coverage from ADN bounding box place and event info-->
<!--13.	PLACE  and EVENT - SIMPLE writes dc:coverage from ADN simple (general) place and event info-->
<!--14. RELATIONS writes URL content (using existing or webservices) of dc:relation from ID template.-->
<!--15. REQUIREMENT writes dc:format from ADN technical requirements-->
<!--16. RESOURCETYPE writes DC type using the ADN resource type  -->
<!--17. SUBJECT template mode=DLESE writes DC subject from ADN subject but removes the leading 'DLESE:'-->
<!--18. SUBJECT template mode=KEYWORDS writes DC subject from ADN keywords -->
<!--19.	TEMPORAL DESCRIPTION writes dc:coverage from ADN simple temporal coverages-->
<!--20. TIMEAD writes DC coverage when time is AD-->
<!--21. TIMEBC writes DC coverage when time is BC-->
<!--22. TIMERELATIVE writes DC coverage when time is relative-->
<!--23.	URL-ENTRY template; mode=RELATIONS determines whether ADN relation.relation.iurlEntry.url has content-->
<!--24. URL-ENTRY template; mode=SOURCE writes dc:source when ADN relations.relation.urlEntry.kind='Is based on'-->

<!--1. CONTENTSTANDARD template-->
	<xsl:template match="d:contentStandard">
<!--compares the ADN value against the controlled vocabulary and only transforms data if the content is in the vocabulary-->
		<xsl:variable name="current">
			<xsl:value-of select="."/>
		</xsl:variable>
		<xsl:for-each select="document($NSESVocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="."/>
			</xsl:variable>
			<xsl:if test="$current = $vocab">
				<xsl:element name="dc:subject">
						<xsl:value-of select="concat('Supports National Science Educationa Standards: ', substring-after($current, ':' ))"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>

		<xsl:for-each select="document($NCGEVocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="@value"/>
			</xsl:variable>
			<xsl:if test="$current = $vocab">
				<xsl:element name="dc:subject">
						<xsl:value-of select="concat('Supports National Council for Geographic Education (NCGE) standard: ', substring-after($current, ':' ))"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="document($NETSVocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="@value"/>
			</xsl:variable>
			<xsl:if test="$current = $vocab">
				<xsl:element name="dc:subject">
						<xsl:value-of select="concat('Supports National Educational Technology Standards (NETS): ', substring-after($current, ':' ))"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="document($NCTMVocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="@value"/>
			</xsl:variable>
			<xsl:if test="$current = $vocab">
				<xsl:element name="dc:subject">
						<xsl:value-of select="concat('Supports National Council of Teachers of Mathemeatics (NCTM): ', substring-after($current, ':' ))"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="document($commonVocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="@value"/>
			</xsl:variable>
			<xsl:if test="$current = $vocab">
				<xsl:element name="dc:subject">
						<xsl:value-of select="concat('Supports American Association for the Advancement of Science (AAAS) Benchmarks: ', substring-after($current, ':' ))"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="document($designVocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="@value"/>
			</xsl:variable>
			<xsl:if test="$current = $vocab">
				<xsl:element name="dc:subject">
						<xsl:value-of select="concat('Supports American Association for the Advancement of Science (AAAS) Benchmarks: ', substring-after($current, ':' ))"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="document($habitsVocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="@value"/>
			</xsl:variable>
			<xsl:if test="$current = $vocab">
				<xsl:element name="dc:subject">
						<xsl:value-of select="concat('Supports American Association for the Advancement of Science (AAAS) Benchmarks: ', substring-after($current, ':' ))"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="document($historyocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="@value"/>
			</xsl:variable>
			<xsl:if test="$current = $vocab">
				<xsl:element name="dc:subject">
						<xsl:value-of select="concat('Supports American Association for the Advancement of Science (AAAS) Benchmarks: ', substring-after($current, ':' ))"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="document($humanVocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="@value"/>
			</xsl:variable>
			<xsl:if test="$current = $vocab">
				<xsl:element name="dc:subject">
						<xsl:value-of select="concat('Supports American Association for the Advancement of Science (AAAS) Benchmarks: ', substring-after($current, ':' ))"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="document($livingVocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="@value"/>
			</xsl:variable>
			<xsl:if test="$current = $vocab">
				<xsl:element name="dc:subject">
						<xsl:value-of select="concat('Supports American Association for the Advancement of Science (AAAS) Benchmarks: ', substring-after($current, ':' ))"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="document($mathVocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="@value"/>
			</xsl:variable>
			<xsl:if test="$current = $vocab">
				<xsl:element name="dc:subject">
						<xsl:value-of select="concat('Supports American Association for the Advancement of Science (AAAS) Benchmarks: ', substring-after($current, ':' ))"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="document($physicalVocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="@value"/>
			</xsl:variable>
			<xsl:if test="$current = $vocab">
				<xsl:element name="dc:subject">
						<xsl:value-of select="concat('Supports American Association for the Advancement of Science (AAAS) Benchmarks: ', substring-after($current, ':' ))"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="document($scienceVocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="@value"/>
			</xsl:variable>
			<xsl:if test="$current = $vocab">
				<xsl:element name="dc:subject">
						<xsl:value-of select="concat('Supports American Association for the Advancement of Science (AAAS) Benchmarks: ', substring-after($current, ':' ))"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="document($techVocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="@value"/>
			</xsl:variable>
			<xsl:if test="$current = $vocab">
				<xsl:element name="dc:subject">
						<xsl:value-of select="concat('Supports American Association for the Advancement of Science (AAAS) Benchmarks: ', substring-after($current, ':' ))"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

<!--2. ID-ENTRY template; mode=RELATIONS-->
<!--assumes the id numbers the resource is based on are also using ADN metadata records that are online resources that have URLs-->
<!--need to verify that the id number is actually in the library and returns content; use the webservice-->
<!--webservice 1-1 has namespaces unlike 1-1 so account for them-->
	<xsl:template match="d:idEntry" mode="relations">
		<xsl:if test="(starts-with(./@kind, 'DLESE:') or starts-with(./@kind, 'DC:')) and string-length(document(concat($DDSWSID, ./@entry))//d:technical/d:online/d:primaryURL)>0">
			<xsl:apply-templates select="./@kind"/>
		</xsl:if>		
	</xsl:template>
	
<!--3. ID-ENTRY template; mode=SOURCE-->
<!--determine if the kind attriubte has a value of 'Is based on'-->
<!--assumes the id numbers the resource is based on are also using ADN metadata records that are online resources that have URLs-->
<!--webservice 1-1 has namespaces unlike 1-1 so account for them-->
	<xsl:template match="d:idEntry" mode="source">
		<xsl:if test="contains(./@kind, 'Is based on') and string-length(./@entry) > 0">
			<xsl:element name="dc:source">
				<xsl:value-of select="document(concat($DDSWSID, ./@entry))//d:technical/d:online/d:primaryURL"/>
			</xsl:element>
		</xsl:if>		
	</xsl:template>

<!--4. KIND template-->
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


<!--5. ORGANIZATION template mode=CONTRIBUTOR-->
	<xsl:template match="d:organization" mode="contributor">
		<xsl:if test="(../@role='Contributor' or ../@role='Editor') and string-length(d:instName) > 0">
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

<!--6. ORGANIZATION template mode=CREATOR-->
	<xsl:template match="d:organization" mode="creator">
		<xsl:if test="(../@role='Author' or ../@role='Principal Investigator') and string-length(d:instName) > 0">
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

<!--7. ORGANIZATION template mode=PUBLISHER-->
	<xsl:template match="d:organization" mode="publisher">
		<xsl:if test="(../@role='Publisher') and string-length(d:instName) > 0">
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

<!--8. OTHER REQUIREMENT template-->
<!--putting the dc:format tag inside the when tag eliminates the possibility of writing a blank tag, but it does not eliminate writing the same tag multiple times if the same ADN data occurs multiple times-->
	<xsl:template match="d:otherRequirement">
		<xsl:choose>
			<xsl:when test="string-length(./d:otherType)>0 and string-length(./d:minimumVersion)>0 and string-length(./d:maximumVersion)>0">
				<xsl:element name="dc:format">
					<xsl:value-of select="concat(./d:otherType, ' with the following min/max version information: ', ./d:minimumVersion, ', ', ./d:maximumVersion)"/>	
				</xsl:element>
			</xsl:when>
			<xsl:when test="string-length(./d:otherType) > 0 and string-length(./d:minimumVersion) > 0">
				<xsl:element name="dc:format">
					<xsl:value-of select="concat(./d:otherType, ' with the following minimum version information: ', ./d:minimumVersion)"/>	
				</xsl:element>
			</xsl:when>
			<xsl:when test="string-length(./d:otherType) > 0 and string-length(./d:maximumVersion) > 0">
				<xsl:element name="dc:format">
					<xsl:value-of select="concat(./d:otherType, ' with the following maximum version information: ', ./d:maximumVersion)"/>	
				</xsl:element>
			</xsl:when>
			<xsl:when test="string-length(./d:otherType) > 0">
				<xsl:element name="dc:format">
					<xsl:value-of select="./d:otherType"/>	
				</xsl:element>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

<!--9. PERSON template mode=CONTRIBUTOR-->
	<xsl:template match="d:person" mode="contributor">
		<xsl:if test="(../@role='Contributor' or ../@role='Editor') and string-length(d:nameFirst) > 0 and string-length(d:nameLast) > 0">
			<xsl:element name="dc:contributor">
				<xsl:value-of select="concat(d:nameFirst,' ',d:nameLast)"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>			

<!--10. PERSON template mode=CREATOR-->
	<xsl:template match="d:person" mode="creator">
		<xsl:if test="(../@role='Author' or ../@role='Principal Investigator') and string-length(d:nameFirst) >0 and string-length(d:nameLast) >0">
			<xsl:element name="dc:creator">
				<xsl:value-of select="concat(d:nameFirst,' ',d:nameLast)"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>			

<!--11. PERSON template mode=PUBLISHER-->
	<xsl:template match="d:person" mode="publisher">
		<xsl:if test="(../@role='Publisher') and string-length(d:nameFirst) > 0 and string-length(d:nameLast) > 0">
			<xsl:element name="dc:publisher">
				<xsl:value-of select="concat(d:nameFirst,' ',d:nameLast)"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>	

<!--12. PLACE template-->
	<xsl:template match="d:place | d:event">
		<xsl:if test="string-length(d:name)>0">
			<xsl:element name="dc:coverage">
				<xsl:value-of select="d:name"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
<!--13. PLACE  and EVENT - SIMPLE template-->
	<xsl:template match="d:placeAndEvent">
		<xsl:if test="string-length(d:place)>0">
			<xsl:element name="dc:coverage">
				<xsl:value-of select="d:place"/>
			</xsl:element>
		</xsl:if>
		<xsl:if test="string-length(d:event)>0">
			<xsl:element name="dc:coverage">
				<xsl:value-of select="d:event"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>

<!--14. RELATIONS template-->
	<xsl:template name="relations">
		<xsl:value-of select="../@url"/>
		<!--catalog record numbers do not have meaning as stand alone text; so convert them to a URL that corresponds to the content of the related resource (that is in the library) by using webservices. This conversion will not work if the related resource is not in the library.-->
		<xsl:value-of select="document(concat($DDSWSID, ../@entry))//d:technical/d:online/d:primaryURL"/>

		<!--catalog record numbers do not have meaning as stand alone text; convert them to a URL that references the metadata record of the related resource. This conversion will not work if the related resource is not in the library.-->
	<!--	<xsl:value-of select="concat('http://www.dlese.org/dds/catalog_', ../@entry, '.htm')"/>-->
	</xsl:template>	

<!--15. REQUIREMENT template-->
<!--putting the dc:format tag inside the when tag eliminates the possibility of writing a blank tag, but it does not eliminate writing the same tag multiple times if the same ADN data occurs multiple times-->
<!--from the ADN terms only write the leaf part - the text after the 2nd colon; so use substrings-->
<!--do not include the ADN generic leaf terms like the one listed in the when statement below-->
	<xsl:template match="d:requirement">
<!--have to use variables or the content and comparison tests won't work-->
		<xsl:variable name="current">
			<xsl:value-of select="d:reqType"/>
		</xsl:variable>
		<xsl:variable name="min">
			<xsl:value-of select="d:minimumVersion"/>
		</xsl:variable>
		<xsl:variable name="max">
			<xsl:value-of select="d:maximumVersion"/>
		</xsl:variable>

<!--compare current requirement (i.e. the one being processed against each term in the ADN vocabulary list by using a for-each loop-->
		<xsl:for-each select="document($reqVocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="@value"/>
			</xsl:variable>

			<xsl:choose>
				<xsl:when test="contains($current, 'No specific technical requirements') or contains($current, 'More specific technical requirements') or contains($current, 'Technical information not easily determined')"/>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="$current=$vocab and string-length($min)>0 and string-length($max)>0">
							<xsl:element name="dc:format">
								<xsl:value-of select="concat(substring-after(substring-after($current, 'DLESE:'), ':'), ' with the following min/max version information: ', $min, ', ', $max)"/>	
							</xsl:element>
						</xsl:when>
						<xsl:when test="$current = $vocab and string-length($min) > 0">
							<xsl:element name="dc:format">
								<xsl:value-of select="concat(substring-after(substring-after($current, 'DLESE:'), ':'), ' with the following minimum version information: ', $min)"/>	
							</xsl:element>
						</xsl:when>
						<xsl:when test="$current = $vocab and string-length($max) > 0">
							<xsl:element name="dc:format">
								<xsl:value-of select="concat(substring-after(substring-after($current, 'DLESE:'), ':'), ' with the following maximum version information: ', $max)"/>	
							</xsl:element>
						</xsl:when>
						<xsl:when test="$current = $vocab and string-length($min)=0 and string-length($max)=0">
							<xsl:element name="dc:format">
								<xsl:value-of select="substring-after(substring-after($current, 'DLESE:'), ':')"/>	
							</xsl:element>
						</xsl:when>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

<!--16. RESOURCETYPE template-->
<!--use  when providing the ADN resource types terms--> 
	<xsl:template match="d:resourceType">
<!--have to use variables or the content and comparison tests won't work-->
		<xsl:variable name="current">
			<xsl:value-of select="."/>
		</xsl:variable>
<!--compare current type (i.e. the one being processed against each term in the ADN vocabulary list by using a for-each loop-->
		<xsl:for-each select="document($typeVocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="@value"/>
			</xsl:variable>
			<xsl:if test="$current = $vocab">
				<xsl:element name="dc:type">
					<xsl:value-of select="concat(substring-before(substring-after($current, 'DLESE:'), ':'), ' ', substring-after(substring-after($current, 'DLESE:'), ':'))"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

<!--17. SUBJECT template mode=SUBJECTS-->
	<xsl:template match="d:subject" mode="subjects">
		<xsl:variable name="current">
			<xsl:value-of select="."/>
		</xsl:variable>
		<xsl:for-each select="document($subjVocabURL)//xsd:restriction/xsd:enumeration">
			<xsl:variable name="vocab">
				<xsl:value-of select="@value"/>
			</xsl:variable>
			<xsl:if test="$current = $vocab and not(contains($current, 'Other'))">
				<xsl:element name="dc:subject">
					<xsl:value-of select="substring-after($current, ':')"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>			


<!--18. SUBJECT template mode=KEYWORDS-->
	<xsl:template match="d:keyword" mode="keywords">
		<xsl:element name="dc:subject">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>			

<!--19. TEMPORAL DESCRIPTION template-->
	<xsl:template match="d:description">
		<xsl:if test="string-length(.)>0">
			<xsl:element name="dc:coverage">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:if>
	</xsl:template>

<!--20. TIMEAD template-->
	<xsl:template match="d:timeAD">
		<xsl:if test="string-length(d:begin/@date)>0 and string-length(d:end/@date)>0">
			<xsl:element name="dc:coverage">
<!--determine if there is a named time period -->
<!--when ADN named time periods are transformed only the first named time period is transformed, all others are ignored; nsdl_dc does not allow for more than one named time period; so do the same for oai_dc-->
				<xsl:choose>
					<!--testing only the first name for data because only be transforming the first name-->
					<xsl:when test="string-length(../../d:periods/d:period/d:name)>0">
						<xsl:value-of select="concat('start=', d:begin/@date, '; end=', d:end/@date, '; name=', ../../d:periods/d:period/d:name)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat('start=', d:begin/@date, '; end=', d:end/@date)"/>
					</xsl:otherwise>
				</xsl:choose>	
			</xsl:element>
		</xsl:if>
	</xsl:template>

<!--21. TIMEBC template-->
	<xsl:template match="d:timeBC">
		<xsl:if test="string-length(d:begin)>0 and string-length(d:end)>0">
			<xsl:element name="dc:coverage">
<!--determine if there is a named time period-->
<!--when ADN named time periods are transformed only the first named time period is transformed, all others are ignored; nsdl_dc does not allow for more than one named time period; so do the same for oai_dc-->
				<xsl:choose>
					<!--testing only the first name for data because only be transforming the first name-->
					<xsl:when test="string-length(../../d:periods/d:period/d:name)>0">
						<xsl:value-of select="concat('start=', d:begin, ' BC; end=', d:end, ' BC; name=', ../../d:periods/d:period/d:name)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat('start=', d:begin, ' BC; end=', d:end, ' BC')"/>
					</xsl:otherwise>
				</xsl:choose>	
			</xsl:element>
		</xsl:if>
	</xsl:template>

<!--22. TIMERELATIVE template-->
	<xsl:template match="d:timeRelative">
		<xsl:if test="string-length(d:begin)>0 and string-length(d:end)>0">
			<xsl:element name="dc:coverage">
<!--determine if there is a named time period in order to make the tag content correct-->
<!--when ADN named time periods are transformed only the first named time period is transformed, all others are ignored; nsdl_dc does not allow for more than one named time period-->
				<xsl:choose>
					<xsl:when test="string-length(../../d:periods/d:period/d:name)>0">
						<xsl:value-of select="concat('start=', d:begin, ' ', d:begin/@units, '; end=', d:end, ' ', d:end/@units, '; name=', ../../d:periods/d:period/d:name)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat('start=', d:begin, ' ', d:begin/@units, '; end=', d:end, ' ', d:end/@units)"/>
					</xsl:otherwise>
				</xsl:choose>	
			</xsl:element>
		</xsl:if>
	</xsl:template>

<!--23. URL-ENTRY template; mode=RELATIONS-->
<!--determine if the kind and url attriubte has content; if both have content, then output the appropriate dc:relation using dct:terms element-->
<!--outputs only dct:terms not dct:tems with the dct:URI attribute because not doing any checks to ensure meeting dct:URI requirements-->
<!--NSDL has checks in place to convert URLs to dct:URI-->
	<xsl:template match="d:urlEntry" mode="relations">
		<xsl:if test="(starts-with(./@kind, 'DLESE:') or starts-with(./@kind, 'DC:')) and string-length(./@url) > 0">
			<xsl:apply-templates select="./@kind"/>
		</xsl:if>
	</xsl:template>

<!--24. URL-ENTRY template; mode=SOURCE-->
<!--determine if the kind attriubte has a value of 'Is based on'-->
	<xsl:template match="d:urlEntry" mode="source">
		<xsl:if test="contains(./@kind, 'Is based on') and string-length(./@url) > 0">
			<xsl:element name="dc:source">
				<xsl:value-of select="./@url"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
