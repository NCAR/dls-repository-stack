<?xml version="1.0"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:c="http://collection.dlese.org"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:nsdl_dc="http://ns.nsdl.org/nsdl_dc_v1.02/"
    xmlns:dct="http://purl.org/dc/terms/"
    xmlns:ieee="http://www.ieee.org/xsd/LOMv1p0"
    xmlns:ws="http://www.dlese.org/Metadata/ddsws"
    exclude-result-prefixes="xsi d"
    version="1.0">

<!--ORGANIZATION OF THIS FILE-->
<!-- **************************************-->
<!--This file is organized into the following sections: Purpose, License information and credits, Assumptions, Other stylesheets, Variables, Transformation code, Templates-->

<!--PURPOSE-->
<!-- **************************************-->
<!--To transform Collection 1.0.00 metadata records to the NSDL_DC version 1.02 format-->

<!--LICENSE INFORMATION and CREDITS-->
<!-- *****************************************************-->
<!--Date created: 2006-11-07 by Katy Ginger, University Corporation for Atmospheric Research (UCAR)-->
<!--Last modified: 2006-11-07 by Katy Ginger-->
<!--License information:
		Copyright (c) 2006 University Corporation for Atmospheric Research (UCAR)
		P.O. Box 3000, Boulder, CO 80307, United States of America
		email: support@dlese.org. 
		All rights reserved
This XML tranformation, written in XSLT 1.0 and XPATH 1.0, are free software; you can redistribute them and/or modify them under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.  These XML instance documents are distributed in the hope that they will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this project; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA -->

<!--ASSUMPTIONS-->
<!-- **************************************-->
<!--1. Unless otherwise indicated in this stylesheet, the transform applies to both ADN online and offline resources-->
<!--2. Only the ADN large bounding box and its associated places and events, not detailed geometries, are transformed-->
<!--3. ADN relations.relation.kind=DLESE:Has thumbnail is not transformed-->
<!--4. ADN objectInSpace tags are not transformed-->
<!--5. When ADN named time periods are transformed only the first named time period is transformed, all others are ignored; the encoding scheme does not allow for more than one named time period-->
<!--6. Any ADN timeAD.begin.date or timeAD.end.date with a value of 'Present' are transformed using the dct:Period encoding scheme not the W3CDTF scheme-->
<!--7. Assumes a mime type of text/html from the URL if the URL has content but does not match any of the listed mime types-->
<!--8. Does not assume ADN records are valid so there could be ADN elements with no content present-->
<!--9 Generally checks are made to ensure ADN elements have content before being transformed. In the case of ADN elements with controlled vocabularies, additional checks for a starts-with 'DLESE:', 'LOM:' and 'GEM:' are performed to help reduce the possibility of transforming nonsense. If these checks are not successful (e.g. an ADN element has <subject>LOM:gobble</subject>), then this nonsense is transformed and a DC element is written-->
<!--10. Complete data checking when ADN records are not valid is not possible within the scope of this transform so some nonsense that does not fit desired encoding schemes can creep into the transformed output-->
<!--11. ADN educational.audiences.audience.instructionalGoal  or typicalAgeRange is not transformed-->		
<!--12. When possible NSES standards are transformed to use the ASN identifiers; Geography standards are transformed and outputted as text-->
<!--13. Process and teaching standards are not transformed-->
<!--14. ADN simple places, event and temporal information are transformed-->
<!--15. Extra descritions associated with each major ADN section are not transformed; only the general description-->

<!--OTHER STYLESHEETS to include-->
<!--*****************************************-->
	<xsl:include href="mimetypes.xsl"/>

	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>


<!--VARIABLES used throughout the transform-->
<!-- **********************************************************-->
<!--variable for accessing DLESE webservice when given a DLESE library id-->
<!--webservice 1-1 has namespaces unlike 1-1 so account for them-->
<xsl:variable name="DDSWSID">http://www.dlese.org/dds/services/ddsws1-1?verb=GetRecord&amp;id=</xsl:variable>	

<!--TRANSFORMATION CODE-->
<!-- **************************************-->
	<xsl:template match="c:collectionRecord">
		<xsl:element name="nsdl_dc:nsdl_dc" namespace="http://ns.nsdl.org/nsdl_dc_v1.02/">
			<xsl:attribute name="schemaVersion">1.02.010</xsl:attribute>
			<xsl:attribute name="xsi:schemaLocation">http://ns.nsdl.org/nsdl_dc_v1.02/ http://ns.nsdl.org/schemas/nsdl_dc/nsdl_dc_v1.02.xsd</xsl:attribute>

<!--dc:title-->
		<xsl:if test="string-length(c:general/c:shortTitle) > 0">
			<xsl:element name="dc:title">
				<xsl:value-of select="c:general/c:shortTitle"/>
			</xsl:element>	
		</xsl:if>
		
<!--dc:title using dct:alternative-->
		<xsl:if test="string-length(c:general/c:fullTitle) > 0">
			<xsl:element name="dct:alternative">
				<xsl:value-of select="c:general/c:fullTitle"/>
			</xsl:element>	
		</xsl:if>

<!--dc:subject - from subjects-->
<!--send DLESE subjects if using gem?-->
		<xsl:apply-templates select="c:general/c:subjects/c:subject" mode="subjects"/>
		<!--see template SUBJECT mode=SUBJECTS-->

<!--dc:subject - from keywords-->
		<xsl:apply-templates select="c:general/c:keywords/c:keyword" mode="keywords"/>
		<!--see template SUBJECT mode=KEYWORDS-->

<!--dc:subject - type=nsdl_dc:GEM-->
<!--to prevent nsdl_dc:GEM entries of 'Science', 'Earth science' and 'Physical science'  etc. from appearing so many times, grab all the contents of subject tags and if it contains any of the following listed below, then make a GEM entry-->
<!--NOTE: subjects not mapped to any GEM term: Forestry, Other-->

<!--	variable for ADN general.subjects.subject-->
		<xsl:variable name="allsubjects">
			<xsl:for-each select="c:general/c:subjects/c:subject">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
<!--for nsdl_dc:GEM: Science-->
		<xsl:if test="contains($allsubjects, 'Atmospheric') or 
						contains($allsubjects, 'Biology') or
						contains($allsubjects, 'oceanography') or
						contains($allsubjects, 'Chemistry') or
						contains($allsubjects, 'Climatology') or
						contains($allsubjects, 'Cryology') or
						contains($allsubjects, 'Environmental science') or
						contains($allsubjects, 'Geochemistry') or
						contains($allsubjects, 'Geology') or
						contains($allsubjects, 'Geophysics') or
						contains($allsubjects, 'Hydrology') or
						contains($allsubjects, 'Mineralogy') or
						contains($allsubjects, 'Natural hazards') or
						contains($allsubjects, 'Paleontology') or
						contains($allsubjects, 'Physics') or
						contains($allsubjects, 'Soil') or
						contains($allsubjects, 'Space') or
						contains($allsubjects, 'geology')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Science</xsl:text>
			</xsl:element>
		</xsl:if>
			
<!--for nsdl_dc:GEM: Earth science-->
		<xsl:if test="contains($allsubjects, 'Atmospheric') or 
						contains($allsubjects, 'Biology') or
						contains($allsubjects, 'oceanography') or
						contains($allsubjects, 'Climatology') or
						contains($allsubjects, 'Cryology') or
						contains($allsubjects, 'Environmental science') or
						contains($allsubjects, 'Geochemistry') or
						contains($allsubjects, 'Geologic time') or
						contains($allsubjects, 'Geology') or
						contains($allsubjects, 'Geophysics') or
						contains($allsubjects, 'Hydrology') or
						contains($allsubjects, 'Mineralogy') or
						contains($allsubjects, 'Natural hazards') or
						contains($allsubjects, 'Paleontology') or
						contains($allsubjects, 'Physical geography') or
						contains($allsubjects, 'Space') or
						contains($allsubjects, 'geology')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Earth science</xsl:text>
			</xsl:element>
		</xsl:if>

<!--for nsdl_dc:GEM: Physical sciences-->
		<xsl:if test="contains($allsubjects, 'Atmospheric') or 
						contains($allsubjects, 'Climatology') or
						contains($allsubjects, 'Cryology') or
						contains($allsubjects, 'Environmental science') or
						contains($allsubjects, 'Geochemistry') or
						contains($allsubjects, 'Geologic time') or
						contains($allsubjects, 'Geology') or
						contains($allsubjects, 'Geophysics') or
						contains($allsubjects, 'Hydrology') or
						contains($allsubjects, 'Mineralogy') or
						contains($allsubjects, 'Natural hazards') or
						contains($allsubjects, 'Paleontology') or
						contains($allsubjects, 'Physical geography') or
						contains($allsubjects, 'Physical oceanography') or
						contains($allsubjects, 'Space') or
						contains($allsubjects, 'geology')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Physical sciences</xsl:text>
			</xsl:element>
		</xsl:if>

<!--for nsdl_dc:GEM: Meteorology-->
		<xsl:if test="contains($allsubjects, 'Atmospheric') or 
						contains($allsubjects, 'Climatology')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Meteorology</xsl:text>
			</xsl:element>
		</xsl:if>

<!--for nsdl_dc:GEM: Geology-->
<!--still test Geology, even though it will be present form the DLESE subject list, because now it is part of the GEM type-->
		<xsl:if test="contains($allsubjects, 'Geology') or
						contains($allsubjects, 'Geochemistry') or
						contains($allsubjects, 'Geophysics') or
						contains($allsubjects, 'Geologic time') or
						contains($allsubjects, 'Paleontology') or
						contains($allsubjects, 'Mineralogy') or
						contains($allsubjects, 'geology')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Geology</xsl:text>
			</xsl:element>
		</xsl:if>

<!--for nsdl_dc:GEM: Geography-->
		<xsl:if test="contains($allsubjects, 'geography')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Geography</xsl:text>
			</xsl:element>
		</xsl:if>
			
<!--for nsdl_dc:GEM: Oceanogrpahy-->
		<xsl:if test="contains($allsubjects, 'oceanography')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Oceanography</xsl:text>
			</xsl:element>
		</xsl:if>
				
<!--for nsdl_dc:GEM: Chemistry-->
<!--still test Chemistry, even though it will be present form the DLESE subject list, because now it is part of the GEM type-->
		<xsl:if test="contains($allsubjects, 'Chemistry') or
						contains($allsubjects, 'Chemical oceanography') or
						contains($allsubjects, 'Geochemistry')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Chemistry</xsl:text>
			</xsl:element>
		</xsl:if>

<!--for nsdl_dc:GEM: Agriculture-->
		<xsl:if test="contains(., 'Agriculture')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Agriculture</xsl:text>
			</xsl:element>
		</xsl:if>

<!--for nsdl_dc:GEM: Biology-->
		<xsl:if test="contains(., 'Biology')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Biology</xsl:text>
			</xsl:element>
		</xsl:if>

<!--for nsdl_dc:GEM: Ecology-->
		<xsl:if test="contains(., 'Ecology')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Ecology</xsl:text>
			</xsl:element>
		</xsl:if>

<!--for nsdl_dc:GEM: Education-->
		<xsl:if test="contains(., 'Educational')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Education (General)</xsl:text>
			</xsl:element>
		</xsl:if>

<!--for nsdl_dc:GEM: History of science-->
		<xsl:if test="contains(., 'History')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>History of science</xsl:text>
			</xsl:element>
		</xsl:if>

<!--for nsdl_dc:GEM: Mathematics-->
		<xsl:if test="contains(., 'Mathematics')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Mathematics</xsl:text>
			</xsl:element>
		</xsl:if>

<!--for nsdl_dc:GEM: Natural history-->
		<xsl:if test="contains(., 'Paleontology')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Natural history</xsl:text>
			</xsl:element>
		</xsl:if>

<!--for nsdl_dc:GEM: Paleontology-->
		<xsl:if test="contains(., 'Paleontology')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Paleontology</xsl:text>
			</xsl:element>
		</xsl:if>

<!--for nsdl_dc:GEM: Physics-->
		<xsl:if test="contains(., 'Physics')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Physics</xsl:text>
			</xsl:element>
		</xsl:if>

<!--for nsdl_dc:GEM: Astronomy-->
		<xsl:if test="contains(., 'Space')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Astronomy</xsl:text>
			</xsl:element>
		</xsl:if>

<!--for nsdl_dc:GEM: Space-->
		<xsl:if test="contains(., 'Space')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Space sciences</xsl:text>
			</xsl:element>
		</xsl:if>

<!--for nsdl_dc:GEM: Technology-->
		<xsl:if test="contains(., 'Technology')">
			<xsl:element name="dc:subject">
				<xsl:attribute name="xsi:type">nsdl_dc:GEM</xsl:attribute>
				<xsl:text>Technology</xsl:text>
			</xsl:element>
		</xsl:if>
<!--end: for nsdl_dc:GEM-->
<!--end dc:subject-->

<!--dc:date-->
<!--since there are many lifecycle.contributors.contributor.date values and these are not required, determine if any are present using a variable to grab all of them-->
 
<!--	variable for lifecycle.contributors.contributor.date-->
		<xsl:variable name="alldates">
			<xsl:for-each select="c:lifecycle/c:contributors/c:contributor/@date">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		
		<xsl:if test="string-length($alldates)>0">
		<!--variable to grab the date of the first occurring date attribute only-->
			<xsl:variable name="dateStr" select="string(normalize-space(c:lifecycle/c:contributors/c:contributor/@date))"/>
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
					<xsl:attribute name="xsi:type">dct:W3CDTF</xsl:attribute>
					<xsl:value-of select="c:lifecycle/c:contributors/c:contributor/@date"/>
				</xsl:element>	
			</xsl:if>
		</xsl:if>	
<!--end: dc:date-->

<!--dc:description-->
		<xsl:when test="string-length(c:general/c:description)>0">
			<xsl:element name="dc:description">
				<xsl:value-of select="c:general/c:description"/>
			</xsl:element>
		</xsl:when>

<!--collection has the notion of a contact and responsible party; map only responsible party as a contributor-->	
<!--dc:contributor-->
		<xsl:apply-templates select="c:lifecycle/c:contributors/c:contributor/c:person" mode="contributor"/>
		<xsl:apply-templates select="c:lifecycle/c:contributors/c:contributor/c:organization" mode="contributor"/>
		<!--see template PERSON mode=CONTRIBUTOR-->
		<!--see template ORGANIZATION mode=CONTRIBUTOR-->
	

<!--dc:format-->
<!--dc:format is defined as the physical or digital manifestation of the resource. Format may include the media-type (mime), dimensions, size and duration and format may be used to determine the software, hardware or other equipment needed to display or operate the resource-->
<!--use the combination of technical.online.mediums.medium and the technical.online.primaryURL to determine dc:format concept of mime type and write it as the dc:format element with a dct:IMT type attribute-->
<!--use technical.online.requirements and technical.online.otherRequirements to determine dc:format concept of software or hardware (or other equipment) needed to operate or display the resource and write it as a plain dc:format element-->
<!--use technical.online.size to determine dc:formt concept of size and write it as the dct:extent element-->

<!--dc:format using dct:extent (size)-->
<!--no dct:extent - size for ADN offline resources-->
		<xsl:if test="string-length(d:technical/d:online/d:size) > 0">
			<xsl:element name="dct:extent">
				<xsl:value-of select="d:technical/d:online/d:size"/>
			</xsl:element>
		</xsl:if>

<!--dc:format - type=dct:IMT using ADN mediums--> 
<!--must select from NSDL list at http://ns.nsdl.org/schemas/mime_type/mime_type_v1.00.xsd-->
<!--since mediums is free text in ADN, create a variable and test and only write dc:format - type=dct:IMT as needed at the broad mime type category (i.e. text, application, video, audio, image, model, multipart or message) because do not know if the finer free text level (e.g. text/html) in ADN medium would be an accepted term in the mime type vocabulary-->

<!--	variable for ADN technical.online.mediums/medium-->
		<xsl:variable name="allmediums">
			<xsl:for-each select="d:technical/d:online/d:mediums/d:medium">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>

<!--test to see if ADN medium information is present, if so write the dc:format type=dct:IMT at a broad mime type category-->
<!--the set of if statements rather than a template eliminates repeating output elements with the same content but allows for repeating output elements with different content-->
<!--this has been verified as working-->
		<xsl:if test="contains($allmediums, 'application')">
			<xsl:element name="dc:format">
				<xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>
				<xsl:text>application</xsl:text>
			</xsl:element>
		</xsl:if> 
		<xsl:if test="contains($allmediums, 'message')">
			<xsl:element name="dc:format">
				<xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>
				<xsl:text>message</xsl:text>
			</xsl:element>
		</xsl:if> 
		<xsl:if test="contains($allmediums, 'video')">
			<xsl:element name="dc:format">
				<xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>
				<xsl:text>video</xsl:text>
			</xsl:element>
		</xsl:if> 
		<xsl:if test="contains($allmediums, 'audio')">
			<xsl:element name="dc:format">
				<xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>
				<xsl:text>audio</xsl:text>
			</xsl:element>
		</xsl:if> 
		<xsl:if test="contains($allmediums, 'image')">
			<xsl:element name="dc:format">
				<xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>
				<xsl:text>image</xsl:text>
			</xsl:element>
		</xsl:if> 
		<xsl:if test="contains($allmediums, 'model')">
			<xsl:element name="dc:format">
				<xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>
				<xsl:text>model</xsl:text>
			</xsl:element>
		</xsl:if> 
		<xsl:if test="contains($allmediums, 'multipart')">
			<xsl:element name="dc:format">
				<xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>
				<xsl:text>multipart</xsl:text>
			</xsl:element>
		</xsl:if> 
		<xsl:if test="contains($allmediums, 'text')">
			<xsl:element name="dc:format">
				<xsl:attribute name="xsi:type">dct:IMT</xsl:attribute>
				<xsl:text>text</xsl:text>
			</xsl:element>
		</xsl:if> 
		
		
<!--dc:format - type=dct:IMT using ADN primaryURL-->
		<xsl:if test="string-length(d:technical/d:online/d:primaryURL) > 0">
			<xsl:call-template name="mimetype"/>
		</xsl:if>

<!--dc:format - plain using ADN technical.requirements.requirement-->
<!--since ADN technical.requirements.requirement is a compound repeating tag, use a template-->
		<xsl:apply-templates select="d:technical/d:online/d:requirements/d:requirement"/>

<!--dc:format - plain using ADN technical.otherRequirements.otherRequirement-->
<!--since ADN technical.otherRequirements.otherRequirement is a compound repeating tag, use a template-->
		<xsl:apply-templates select="d:technical/d:online/d:otherRequirements/d:otherRequirement"/>
	
<!--dc:type-->
<!--use ADN educational.resourceTypes.resourceType and map to NSDL vocab; this excludes ADN terms from output-->
<!--vocabulary mapping is necessary-->
<!--to prevent duplicate tags from appearing, make a variable and test it-->

<!--	variable for ADN educational.resourceTypes.resourceType-->
		<xsl:variable name="allresourceTypes">
			<xsl:for-each select="d:educational/d:resourceTypes/d:resourceType">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
	
<!--dc:type - plain-->
<!--includes those ADN resource type terms that do not map to the DCMI type or NSDL_DC type-->
<!--this includes the ADN terms of Calculation or conversion tool, Code, Software, Scientific visualization because they do not map to NSDL_DC-->
<!--think about including sound, text:book and audio:book here  because they do not map well in NSDL_DC either-->

<!--dc:type - plain: Calculation or conversion tool-->
		<xsl:if test="contains($allresourceTypes, 'Calculation or conversion tool')">
			<xsl:element name="dc:type">
				<xsl:text>Calculation or Conversion Tool</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - plain: Code-->
		<xsl:if test="contains($allresourceTypes, 'Code')">
			<xsl:element name="dc:type">
				<xsl:text>Code</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - plain: Software-->
		<xsl:if test="contains($allresourceTypes, 'Software')">
			<xsl:element name="dc:type">
				<xsl:text>Software</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - plain: Scientific visualization-->
		<xsl:if test="contains($allresourceTypes, 'Scientific visualization')">
			<xsl:element name="dc:type">
				<xsl:text>Scientific Visualization</xsl:text>
			</xsl:element>
		</xsl:if>
		<xsl:if test="string-length(d:technical/d:offline)>0">
			<xsl:element name="dc:type">
				<xsl:text>Physical Object</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dc:type - dct:DCMI: Collection-->
<!--maps ADN resource type terms to the DCMI list of terms-->
		<xsl:if test="contains($allresourceTypes, 'DLESE:Portal')">
			<xsl:element name="dc:type">
				<xsl:attribute name="xsi:type">dct:DCMIType</xsl:attribute>
				<xsl:text>Collection</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - dct:DCMI: Dataset-->
		<xsl:if test="contains($allresourceTypes, 'DLESE:Data')">
			<xsl:element name="dc:type">
				<xsl:attribute name="xsi:type">dct:DCMIType</xsl:attribute>
				<xsl:text>Dataset</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - dct:DCMI: Event-->
		<xsl:if test="contains($allresourceTypes, 'Webcast')">
			<xsl:element name="dc:type">
				<xsl:attribute name="xsi:type">dct:DCMIType</xsl:attribute>
				<xsl:text>Event</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - dct:DCMI: Image-->
		<xsl:if test="contains($allresourceTypes, 'DLESE:Visual')">
			<xsl:element name="dc:type">
				<xsl:attribute name="xsi:type">dct:DCMIType</xsl:attribute>
				<xsl:text>Image</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - dct:DCMI: InteractiveResource - Removed 10/2016 -->
<!--
		<xsl:if test="contains($allresourceTypes, 'DLESE:Learning') or contains($allresourceTypes, 'Calculation')">
			<xsl:element name="dc:type">
				<xsl:attribute name="xsi:type">dct:DCMIType</xsl:attribute>
				<xsl:text>InteractiveResource</xsl:text>
			</xsl:element>
		</xsl:if>
-->
<!--dc:type - dct:DCMI: Service-->
		<xsl:if test="contains($allresourceTypes, 'DLESE:Service')">
			<xsl:element name="dc:type">
				<xsl:attribute name="xsi:type">dct:DCMIType</xsl:attribute>
				<xsl:text>Service</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - dct:DCMI: Software-->
		<xsl:if test="contains($allresourceTypes, 'Code') or contains($allresourceTypes, 'Software')">
			<xsl:element name="dc:type">
				<xsl:attribute name="xsi:type">dct:DCMIType</xsl:attribute>
				<xsl:text>Software</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - dct:DCMI: Sound-->
		<xsl:if test="contains($allresourceTypes, 'Audio book') or contains($allresourceTypes, 'Lecture') or contains($allresourceTypes, 'Music') or contains($allresourceTypes, 'Oral') or contains($allresourceTypes, 'Radio') or contains($allresourceTypes, 'Sound')">
			<xsl:element name="dc:type">
				<xsl:attribute name="xsi:type">dct:DCMIType</xsl:attribute>
				<xsl:text>Sound</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - dct:DCMI: Text-->
		<xsl:if test="contains($allresourceTypes, 'DLESE:Text')">
			<xsl:element name="dc:type">
				<xsl:attribute name="xsi:type">dct:DCMIType</xsl:attribute>
				<xsl:text>Text</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - dct:DCMI: PhysicalPbject-->
		<xsl:if test="string-length(d:technical/d:offline)>0">
			<xsl:element name="dc:type">
				<xsl:attribute name="xsi:type">dct:DCMIType</xsl:attribute>
				<xsl:text>PhysicalObject</xsl:text>
			</xsl:element>
		</xsl:if>
<!--end dc:type - dct:DCMI-->

<!--dc:type - nsdl_dc:-->
<!--maps ADN resource type terms to the NSDL list of terms-->
<!--vocabulary mapping is necessary-->
<!--to prevent duplicate dc:type - nsdl_dc tags from appearing, test the $allresourceTypes variable-->
<!--if this becomes required then need to map terms that appear in dc:type - plain somehow)-->

<!--dc:type - nsdl_dc: Webcast-->
		<xsl:if test="contains($allresourceTypes, 'webcast') or contains($allresourceTypes, 'Audio book')">
			<xsl:element name="dc:type">
				<xsl:text>Webcast</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Radio Broadcast-->
		<xsl:if test="contains($allresourceTypes, 'Radio broadcast')">
			<xsl:element name="dc:type">
				<xsl:text>Radio Broadcast</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Video-->
		<xsl:if test="contains($allresourceTypes, 'Video')">
			<xsl:element name="dc:type">
				<xsl:text>Video</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Lecture-->
		<xsl:if test="contains($allresourceTypes, 'Lecture')">
			<xsl:element name="dc:type">
				<xsl:text>Lecture</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Music-->
		<xsl:if test="contains($allresourceTypes, 'Music') or contains($allresourceTypes, 'Sound')">
			<xsl:element name="dc:type">
				<xsl:text>Music</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Oral History-->
		<xsl:if test="contains($allresourceTypes, 'Oral history')">
			<xsl:element name="dc:type">
				<xsl:text>Oral History</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Data Set-->
		<xsl:if test="contains($allresourceTypes, 'dataset')">
			<xsl:element name="dc:type">
				<xsl:text>Data Set</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Assessment-->
		<xsl:if test="contains($allresourceTypes, 'Assessment')">
			<xsl:element name="dc:type">
				<xsl:text>Assessment</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Case Study-->
		<xsl:if test="contains($allresourceTypes, 'Case study')">
			<xsl:element name="dc:type">
				<xsl:text>Case Study</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Lesson or activity-->
		<xsl:if test="contains($allresourceTypes, 'Lesson') or contains($allresourceTypes, 'activity')">
			<xsl:element name="dc:type">
				<xsl:text>Lesson</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Course-->
		<xsl:if test="contains($allresourceTypes, 'Course') or contains($allresourceTypes, 'Module or unit')">
			<xsl:element name="dc:type">
				<xsl:text>Course</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Curriculum-->
		<xsl:if test="contains($allresourceTypes, 'Curriculum')">
			<xsl:element name="dc:type">
				<xsl:text>Curriculum</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Student Guide-->
		<xsl:if test="contains($allresourceTypes, 'Field trip guide')">
			<xsl:element name="dc:type">
				<xsl:text>Student Guide</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Instructor guide-->
		<xsl:if test="contains($allresourceTypes, 'Instructor guide')">
			<xsl:element name="dc:type">
				<xsl:text>Instructor Guide</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Presentation-->
		<xsl:if test="contains($allresourceTypes, 'Presentation or demonstration')">
			<xsl:element name="dc:type">
				<xsl:text>Presentation</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Problem set-->
		<xsl:if test="contains($allresourceTypes, 'Problem set')">
			<xsl:element name="dc:type">
				<xsl:text>Problem Set</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Project-->
		<xsl:if test="contains($allresourceTypes, 'Project')">
			<xsl:element name="dc:type">
				<xsl:text>Project</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Syllabus-->
		<xsl:if test="contains($allresourceTypes, 'Syllabus')">
			<xsl:element name="dc:type">
				<xsl:text>Syllabus</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Tutorial-->
		<xsl:if test="contains($allresourceTypes, 'Tutorial')">
			<xsl:element name="dc:type">
				<xsl:text>Tutorial</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Virtual Field Trip-->
		<xsl:if test="contains($allresourceTypes, 'Virtual field trip')">
			<xsl:element name="dc:type">
				<xsl:text>Virtual Field Trip</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Artifact-->
		<xsl:if test="contains($allresourceTypes, 'Physical object')">
			<xsl:element name="dc:type">
				<xsl:text>Artifact</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dc:type - nsdl_dc: Portal-->
		<xsl:if test="contains($allresourceTypes, 'portal')">
			<xsl:element name="dc:type">
				<xsl:text>Portal</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Ask-an-Expert-->
		<xsl:if test="contains($allresourceTypes, 'Ask an expert')">
			<xsl:element name="dc:type">
				<xsl:text>Ask-an-Expert</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Clearinghouse-->
		<xsl:if test="contains($allresourceTypes, 'Clearinghouse')">
			<xsl:element name="dc:type">
				<xsl:text>Clearinghouse</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Forum-->
		<xsl:if test="contains($allresourceTypes, 'Forum')">
			<xsl:element name="dc:type">
				<xsl:text>Forum</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Listserv-->
		<xsl:if test="contains($allresourceTypes, 'Listserv')">
			<xsl:element name="dc:type">
				<xsl:text>Listserv</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Message Board-->
		<xsl:if test="contains($allresourceTypes, 'Message board')">
			<xsl:element name="dc:type">
				<xsl:text>Message Board</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Search Engine-->
		<xsl:if test="contains($allresourceTypes, 'Search engine')">
			<xsl:element name="dc:type">
				<xsl:text>Search Engine</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Abstract-->
		<xsl:if test="contains($allresourceTypes, 'Abstract')">
			<xsl:element name="dc:type">
				<xsl:text>Abstract</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Textbook-->
		<xsl:if test="contains($allresourceTypes, 'Text:Book')">
			<xsl:element name="dc:type">
				<xsl:text>Textbook</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Reference-->
		<xsl:if test="contains($allresourceTypes, 'Glossary') or contains($allresourceTypes, 'Reference') or contains($allresourceTypes, 'Index or bibliography')">
			<xsl:element name="dc:type">
				<xsl:text>Reference</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Article-->
		<xsl:if test="contains($allresourceTypes, 'Journal article')">
			<xsl:element name="dc:type">
				<xsl:text>Article</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Periodical-->
		<xsl:if test="contains($allresourceTypes, 'Periodical')">
			<xsl:element name="dc:type">
				<xsl:text>Periodical</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Policy-->
		<xsl:if test="contains($allresourceTypes, 'Policy')">
			<xsl:element name="dc:type">
				<xsl:text>Policy</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Proceedings-->
		<xsl:if test="contains($allresourceTypes, 'Proceedings')">
			<xsl:element name="dc:type">
				<xsl:text>Proceedings</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Proposal-->
		<xsl:if test="contains($allresourceTypes, 'Proposal')">
			<xsl:element name="dc:type">
				<xsl:text>Proposal</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Report-->
		<xsl:if test="contains($allresourceTypes, 'Report')">
			<xsl:element name="dc:type">
				<xsl:text>Report</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Thesis-->
		<xsl:if test="contains($allresourceTypes, 'Thesis')">
			<xsl:element name="dc:type">
				<xsl:text>Thesis</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Illustration-->
		<xsl:if test="contains($allresourceTypes, 'illustration')">
			<xsl:element name="dc:type">
				<xsl:text>Illustration</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Map-->
		<xsl:if test="contains($allresourceTypes, 'Map')">
			<xsl:element name="dc:type">
				<xsl:text>Map</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Photograph-->
		<xsl:if test="contains($allresourceTypes, 'Photograph')">
			<xsl:element name="dc:type">
				<xsl:text>Photograph</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Remotely Sensed Imagery-->
		<xsl:if test="contains($allresourceTypes, 'Remotely sensed imagery')">
			<xsl:element name="dc:type">
				<xsl:text>Remotely Sensed Imagery</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Demonstration-->
		<xsl:if test="contains($allresourceTypes, 'demonstration')">
			<xsl:element name="dc:type">
				<xsl:text>Demonstration</xsl:text>
			</xsl:element>
		</xsl:if>
<!--dc:type - nsdl_dc: Simulation-->
		<xsl:if test="contains($allresourceTypes, 'Scientific visualization')">
			<xsl:element name="dc:type">
				<xsl:text>Simulation</xsl:text>
			</xsl:element>
		</xsl:if>


<!--dc:identifier - using ADN id numbers-->
<!--all ADN records (online and offline) have a number that can be used as an indentifier for that collection-->
<!--		<xsl:element name="dc:identifier">
			<xsl:value-of select="concat(d:metaMetadata/d:catalogEntries/d:catalog, ': ', d:metaMetadata/d:catalogEntries/d:catalog/@entry)"/>
		</xsl:element> -->

<!--dc:identifier - using ADN technical.online.primaryURL field-->
<!--outputs only dc:identifier not dc:identifier - dct:URI because not doing any checks to ensure meeting dct:URI requirements-->
<!--that is, no checking for spaces, double protocols (e.g. http:http://), lowercase etc.-->
<!--NSDL has a fairly robust process using stylesheets and java code to convert to dct:URI; therefore let NSDL do it-->
<!--only online ADN resources have dc:identifier-->
<!--do an if test to exclude offline resources-->
		<xsl:if test="string-length(d:technical/d:online/d:primaryURL) > 0">
			<xsl:element name="dc:identifier">
				<xsl:value-of select="d:technical/d:online/d:primaryURL"/>
			</xsl:element>
		</xsl:if>
		
<!--dc:identifier - dct:ISBN-->
<!--ADN does not collect-->

<!--dc:identifier - dct:ISSN-->
<!--ADN does not collect-->

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


<!--dc:language plain-->
<!--ADN does not collect dc:language- plain information-->

<!--dc:language - dct:ISO639-2-->
<!--only checks to see if data is present; does not verify if it is in the ISO format; if hte ADN record is valid the data will be in the ISO format-->
		<xsl:if test="string-length(d:general/d:language) > 0">
			<xsl:element name="dc:language">
				<xsl:attribute name="xsi:type">dct:ISO639-2</xsl:attribute>
				<xsl:value-of select="d:general/d:language"/>
			</xsl:element>
		</xsl:if>

<!--dc:language - dct:RFC3066-->
<!--ADN does not collect-->
	
<!--dc:rights-->
		<xsl:if test="string-length(d:rights/d:description) > 0">
			<xsl:element name="dc:rights">
				<xsl:value-of select="d:rights/d:description"/>
			</xsl:element>
		</xsl:if>
	
<!--dc:coverage and dc:spatial general information-->
<!--only ADN large bounding box and associated placenames, not detailed geometries, are transformed-->
<!--put ADN large bound box placenames in dc:coverage-->
<!--put ADN large bound box coordinates in dc:spatial - xsi:type=dct:Box-->

<!--dc:coverage-->
		<xsl:apply-templates select="d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:bbPlaces/d:place"/>
		<xsl:apply-templates select="d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:bbEvents/d:event"/>
		<xsl:apply-templates select="d:geospatialCoverages/d:geospatialCoverage/d:detGeos/d:detGeo/d:detPlaces/d:place"/>
		<xsl:apply-templates select="d:geospatialCoverages/d:geospatialCoverage/d:detGeos/d:detGeo/d:detEvents/d:event"/>
		<!--see template PLACE and EVENT-->

		<xsl:apply-templates select="d:general/d:simplePlacesAndEvents/d:placeAndEvent"/>
		<!--see template PLACE and EVENT - SIMPLE-->

<!--dct:spatial - dct:Box-->
<!--no template used since only occurs once in ADN-->
<!--only a test to see if data is present not to see if data is correct-->
		<xsl:if test="string-length(d:geospatialCoverages/d:geospatialCoverage/d:boundBox)>0">
			<xsl:element name="dct:spatial">
				<xsl:attribute name="xsi:type">dct:Box</xsl:attribute>
				<xsl:value-of select="concat('northlimit=', d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:northCoord, '; southlimit=', d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:southCoord, '; westlimit=', d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:westCoord, '; eastlimit=', d:geospatialCoverages/d:geospatialCoverage/d:boundBox/d:eastCoord, '; units=signed decimal degrees')"/>
			</xsl:element>
		</xsl:if>


<!--dct:temporal general information-->
<!--use dct:temporal xsi:type=dct:Period for ADN timeAD and named time period; template TIMEAD mode DCT:PERIOD-->
<!--use dct:temporal xsi:type=dct:W3CDTF for ADN timeAD; template TIMEAD mode DCT:W3CDTF-->
<!--use dct:temporal xsi:type=dct:Period for ADN timeBC and named time period in timeBC; template TIMEBC-->
<!--use dct:temporal xsi:type=dct:Period for ADN timeRelative; see template TIMERELATIVE-->

<!--dct:temporal xsi:type=dct:Period for timeAD-->
		<xsl:apply-templates select="d:temporalCoverages/d:timeAndPeriod/d:timeInfo/d:timeAD" mode="dct:Period"/>
		<!--see template TIMEAD mode DCT:PERIOD-->

<!--dct:temporal xsi:type=dct:W3CDTF for timeAD-->
		<xsl:apply-templates select="d:temporalCoverages/d:timeAndPeriod/d:timeInfo/d:timeAD" mode="dct:W3CDTF"/>
		<!--see template TIMEAD mode DCT:W3CDTF-->
		
<!--dct:temporal - xsi:type=dct:Period for timeBC-->
		<xsl:apply-templates select="d:temporalCoverages/d:timeAndPeriod/d:timeInfo/d:timeBC"/>
		<!--see template TIMEBC-->

<!--dct:temporal - xsi:type=dct:Period for timeRelative-->
		<xsl:apply-templates select="d:temporalCoverages/d:timeAndPeriod/d:timeInfo/d:timeRelative"/>
		<!--see template TIMERELATIVE-->

<!--dct:temporal for simpleTemporalCoverages-->
		<xsl:apply-templates select="d:general/d:simpleTemporalCoverages/d:description"/>
		<!--see template TEMPORAL DESCRIPTION-->


<!--dct relation information-->
<!--ADN DLESE:Has thumbnail is not transformed-->

<!--ADN relations.relation.urlEntry can repeat, so use a template-->
<!--see template KIND-->
		<xsl:apply-templates select="d:relations/d:relation/d:urlEntry" mode="relations"/>
		
<!--ADN relations.relation.idEntry can repeat, so use a template-->
<!--see template KIND-->
		<xsl:apply-templates select="d:relations/d:relation/d:idEntry" mode="relations"/>


<!--dct:conformsTo - using ADN standards information-->
		<xsl:apply-templates select="d:educational/d:contentStandards/d:contentStandard"/>
		<!--see template CONTENTSTANDARD-->

<!--dct:audience-->
		<xsl:apply-templates select="d:educational/d:audiences/d:audience/d:beneficiary"/>
		<!--see template BENEFICIARY-->

<!--dct:mediator-->
		<xsl:apply-templates select="d:educational/d:audiences/d:audience/d:toolFor"/>
		<!--see template TOOLFOR-->

<!--dct:educationalLevel-->
<!--maps ADN gradeRange terms to the NSDL list of terms-->

<!--	variable for ADN educational.audiences.audience.gradeRange-->
		<xsl:variable name="allgrades">
			<xsl:for-each select="d:educational/d:audiences/d:audience/d:gradeRange">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>

<!--to prevent duplicate dc:educationLevel - nsdl_dc tags from appearing, test the $allgrades variable-->
<!--dct:educationLevel - nsdl_dc: Elementary School-->
		<xsl:if test="contains($allgrades, 'Elementary') or contains($allgrades, 'elementary')">
			<xsl:element name="dct:educationLevel">
				<xsl:attribute name="xsi:type">nsdl_dc:NSDLEdLevel</xsl:attribute>
				<xsl:text>Elementary School</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dct:educationLevel - nsdl_dc: Middle School-->
		<xsl:if test="contains($allgrades, 'Middle') or contains($allgrades, 'middle')">
			<xsl:element name="dct:educationLevel">
				<xsl:attribute name="xsi:type">nsdl_dc:NSDLEdLevel</xsl:attribute>
				<xsl:text>Middle School</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dct:educationLevel - nsdl_dc: High School-->
		<xsl:if test="contains($allgrades, 'High') or contains($allgrades, 'high')">
			<xsl:element name="dct:educationLevel">
				<xsl:attribute name="xsi:type">nsdl_dc:NSDLEdLevel</xsl:attribute>
				<xsl:text>High School</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dct:educationLevel - nsdl_dc: Undergraduate Lower Division-->
		<xsl:if test="contains($allgrades, 'Lower') or contains($allgrades, 'lower')">
			<xsl:element name="dct:educationLevel">
				<xsl:attribute name="xsi:type">nsdl_dc:NSDLEdLevel</xsl:attribute>
				<xsl:text>Undergraduate (Lower Division)</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dct:educationLevel - nsdl_dc: Undergraduate Lower Division-->
		<xsl:if test="contains($allgrades, 'Upper') or contains($allgrades, 'upper')">
			<xsl:element name="dct:educationLevel">
				<xsl:attribute name="xsi:type">nsdl_dc:NSDLEdLevel</xsl:attribute>
				<xsl:text>Undergraduate (Upper Division)</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dct:educationLevel - nsdl_dc: Graduate -->
		<xsl:if test="contains($allgrades, 'Graduate') or contains($allgrades, 'graduate') or contains($allgrades, 'professional') or contains($allgrades, 'Professional') ">
			<xsl:element name="dct:educationLevel">
				<xsl:attribute name="xsi:type">nsdl_dc:NSDLEdLevel</xsl:attribute>
				<xsl:text>Graduate</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dct:educationLevel - plain - Informal education-->
		<xsl:if test="contains($allgrades, 'Informal') or contains($allgrades, 'informal')">
			<xsl:element name="dct:educationLevel">
				<xsl:text>Informal education</xsl:text>
			</xsl:element>
		</xsl:if>

<!--dct:educationLevel - plain - General public-->
		<xsl:if test="contains($allgrades, 'general') or contains($allgrades, 'General') or contains($allgrades, 'public') or contains($allgrades, 'Public')">
			<xsl:element name="dct:educationLevel">
				<xsl:text>General public</xsl:text>
			</xsl:element>
		</xsl:if>
		
<!--dct:instructionalMethod-->
		<xsl:apply-templates select="d:educational/d:audiences/d:audience/d:teachingMethods/d:teachingMethod"/>
		<!--see template TEACHINGMETHOD-->
		

<!--ieee:interactivityType-->
		<xsl:if test="string-length(d:educational/d:interactivityType) >0 and starts-with(d:educational/d:interactivityType, 'LOM:')">
			<xsl:element name="ieee:interactivityType">
				<xsl:value-of select="substring-after(d:educational/d:interactivityType, 'LOM:')"/>
			</xsl:element>
		</xsl:if>
<!--ieee:interactivityLevel-->
		<xsl:if test="string-length(d:educational/d:interactivityLevel) >0 and starts-with(d:educational/d:interactivityLevel, 'LOM:')">
			<xsl:element name="ieee:interactivityLevel">
				<xsl:value-of select="substring-after(d:educational/d:interactivityLevel, 'LOM:')"/>
			</xsl:element>
		</xsl:if>

<!--ieee:typicalLearningTime-->
		<xsl:if test="string-length(d:educational/d:audiences/d:audience/d:typicalUseTime) >0">
			<xsl:element name="ieee:typicalLearningTime">
				<xsl:value-of select="d:educational/d:audiences/d:audience/d:typicalUseTime"/>
			</xsl:element>
		</xsl:if>
		
<!--end nsdl:dc-->
		</xsl:element>
	</xsl:template>


<!--TEMPLATES. In alphabetical order generally by ADN element name-->
<!--1.   BENEFICARY writes dct:audience-->
<!--2.   CONTENTSTANDARD writes dct:conformsTo-->
<!--3.   ID-ENTRY template; mode=RELATIONS determines if ADN relation.relation.idEntry.entry exists in the library using webservices-->
<!--4.   ID-ENTRY template; mode=SOURCE writes dc:source when ADN relations.relation.idEntry.kind='Is based on'-->
<!--5.   KIND writes dct:isVersionOf, dct:hasVersion, etc.-->
<!--5.   MEDIUM writes the tag content for templates MEDIUM mode=ADN, MEDIUM mode=plain, MEDIUM mode=DCT:IMT-->
<!--6.	ORGANIZATION mode=contributor writes dc:contributor-->
<!--7. 	ORGANIZATION mode=creator writes dc:creator-->
<!--8. 	ORGANIZATION mode=publisher writes dc:publisher-->
<!--9. 	OTHER REQUIREMENT writes dc:format-->
<!--10. PERSON mode=contributor writes dc:contributor-->
<!--11. PERSON mode=creator writes dc:creator-->
<!--12. PERSON mode=pubisher writes dc:publisher-->
<!--13.	PLACE  and EVENT writes dc:coverage from ADN bounding box place and event info-->
<!--14.	PLACE  and EVENT - SIMPLE writes dc:coverage and dct:temporal from ADN simple (general) place and event info-->
<!--15. RELATIONS writes URL content (using existing or webservices) of dct:IsVersionOf, dct:hasVersion etc. from IND template.-->
<!--16. REQUIREMENT writes dc:format from ADN technical requirements-->
<!--17. SUBJECT mode=subjects writes dc:subject from ADN subjects--> 
<!--18. SUBJECT mode=keywords writes dc:subject from ADN keywords-->
<!--19.	TEACHINGMETHOD writes dct:intructionalMethod-->
<!--20.	TEMPORAL DESCRIPTION writes dct:temporal from ADN simple temporal coverages-->
<!--21. TIMEAD mode=dct:period writes dct:temporal xsi:type="dct:Period"-->
<!--22. TIMEAD mode=dct:W3CDTF writes dct:temporal xsi:type="dct:W3CDTF"-->
<!--23. TIMEBC writes dct:temporal xsi:type="dct:Period"-->
<!--24. TIMERELATIVE writes dct:temporal xsi:type="dct:Period"-->
<!--26. TOOLFOR writes dct:mediator-->
<!--27.	URL-ENTRY template; mode=RELATIONS determines whether ADN relation.relation.iurlEntry.url has content-->
<!--28. URL-ENTRY template; mode=SOURCE writes dc:source when ADN relations.relation.urlEntry.kind='Is based on'-->


<!--1. BENEFICIARY template-->
	<xsl:template match="d:beneficiary">
		<xsl:if test="starts-with(., 'DLESE:') or starts-with(., 'GEM:')">
			<xsl:element name="dct:audience">
				<xsl:value-of select="substring-after(., ':')"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>

<!--2. CONTENTSTANDARD template-->
	<xsl:template match="d:contentStandard">
		<xsl:if test="string-length(.) > 0 ">
			<xsl:element name="dct:conformsTo">
				<xsl:choose>
	<!--NSES-->
					<xsl:when test="contains(., 'NSES:')">
					<!--use ASN identifiers-->
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
		</xsl:if>
	</xsl:template>

<!--3. ID-ENTRY template; mode=RELATIONS-->
<!--assumes the id numbers the resource is based on are also using ADN metadata records that are online resources that have URLs-->
<!--need to verify that the id number is actually in the library and returns content; use the webservice-->
<!--webservice 1-1 has namespaces unlike 1-1 so account for them-->
	<xsl:template match="d:idEntry" mode="relations">
		<xsl:if test="(starts-with(./@kind, 'DLESE:') or starts-with(./@kind, 'DC:')) and string-length(document(concat($DDSWSID, ./@entry))//d:technical/d:online/d:primaryURL)>0">
			<xsl:apply-templates select="./@kind"/>
		</xsl:if>		
	</xsl:template>
	
<!--4. ID-ENTRY template; mode=SOURCE-->
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

<!--5. KIND template-->
<!--does not map ADN relations.relation//@kind when kind = 'Is based on' because Is based on is mapped into the dc:source element-->
<!--does not map ADN relations.relation//@kind when kind = 'Has thumbnail'-->
<!--call-template halts processing of current template in order to call and complete anther template; then processing of the current template is resumed; if did not do this would get the unwnted result of all the mediums (mime types) being in a single dc:format tag-->
	<xsl:template match="d:relations/d:relation//@kind">
		<xsl:choose>
<!--dct:isVersionOf-->
			<xsl:when test="contains(., 'Is version of')">
				<xsl:element name="dct:isVersionOf">
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dct:hasVersion-->
			<xsl:when test="contains(., 'Has version')">
				<xsl:element name="dct:hasVersion">
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dct:isReplacedBy-->
			<xsl:when test="contains(., 'Is replaced by')">
				<xsl:element name="dct:isReplacedBy">
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dct:replaces-->
			<xsl:when test="contains(., 'Replaces')">
				<xsl:element name="dct:replaces">
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dct:isRequiredBy-->
			<xsl:when test="contains(., 'Is required by')">
				<xsl:element name="dct:isRequiredBy">
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dct:requires-->
			<xsl:when test="contains(., 'Requires')">
				<xsl:element name="dct:requires">
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dct:isPartOf-->
			<xsl:when test="contains(., 'Is part of')">
				<xsl:element name="dct:isPartOf">
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dct:hasPart-->
			<xsl:when test="contains(., 'Has part')">
				<xsl:element name="dct:hasPart">
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dct:isReferencedby-->
			<xsl:when test="contains(., 'Is referenced by') or contains(., 'Is basis for')">
				<xsl:element name="dct:isReferencedBy">
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dct:references-->
			<xsl:when test="contains(., 'References') or contains(., 'Is associated with')">
				<xsl:element name="dct:references">
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dct:isFormatOf-->
			<xsl:when test="contains(., 'Is format of')">
				<xsl:element name="dct:isFormatOf">
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dct:hasFormat-->
			<xsl:when test="contains(., 'Has format')">
				<xsl:element name="dct:hasFormat">
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
<!--dct:conformsTo-->
			<xsl:when test="contains(., 'Conforms to')">
				<xsl:element name="dct:conformsTo">
					<xsl:call-template name="relations"/>
				</xsl:element>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

<!--6. ORGANIZATION template mode=CONTRIBUTOR-->
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

<!--7. ORGANIZATION template mode=CREATOR-->
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

<!--8. ORGANIZATION template mode=PUBLISHER-->
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

<!--9. OTHER REQUIREMENT template-->
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

<!--10. PERSON template mode=CONTRIBUTOR-->
	<xsl:template match="d:person" mode="contributor">
		<xsl:if test="../@role='Contributor' or ../@role='Editor'">
			<xsl:element name="dc:contributor">
				<xsl:value-of select="concat(d:nameFirst,' ',d:nameLast)"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>			

<!--11. PERSON template mode=CREATOR-->
	<xsl:template match="d:person" mode="creator">
		<xsl:if test="../@role='Author' or ../@role='Principal Investigator'">
			<xsl:element name="dc:creator">
				<xsl:value-of select="concat(d:nameFirst,' ',d:nameLast)"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>			

<!--12. PERSON template mode=PUBLISHER-->
	<xsl:template match="d:person" mode="publisher">
		<xsl:if test="../@role='Publisher'">
			<xsl:element name="dc:publisher">
				<xsl:value-of select="concat(d:nameFirst,' ',d:nameLast)"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>	

<!--13. PLACE  and EVENT template-->
	<xsl:template match="d:place | d:event">
		<xsl:if test="string-length(d:name)>0">
			<xsl:element name="dc:coverage">
				<xsl:value-of select="d:name"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>

<!--14. PLACE  and EVENT - SIMPLE template-->
	<xsl:template match="d:placeAndEvent">
		<xsl:if test="string-length(d:place)>0">
			<xsl:element name="dc:coverage">
				<xsl:value-of select="d:place"/>
			</xsl:element>
		</xsl:if>
		<xsl:if test="string-length(d:event)>0">
			<xsl:element name="dct:temporal">
				<xsl:value-of select="d:event"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
<!--15. RELATIONS template-->
<!--webservice 1-1 has namespaces unlike 1-1 so account for them-->
	<xsl:template name="relations">
		<xsl:value-of select="../@url"/>
		<xsl:value-of select="document(concat($DDSWSID, ../@entry))//d:technical/d:online/d:primaryURL"/>
	</xsl:template>

<!--16. REQUIREMENT template-->
<!--putting the dc:format tag inside the when tag eliminates the possibility of writing a blank tag, but it does not eliminate writing the same tag multiple times if the same ADN data occurs multiple times-->
<!--from the ADN terms only write the leaf part - the text after the 2nd colon; so use substrings-->
<!--do not include the ADN generic leaf terms like the one listed in the when statement below-->
	<xsl:template match="d:requirement">
		<xsl:choose>
			<xsl:when test="contains(substring-after(substring-after(./d:reqType, 'DLESE:'), ':'), 'No specific technical requirements') or contains(substring-after(substring-after(./d:reqType, 'DLESE:'), ':'), 'More specific technical requirements') or contains(substring-after(substring-after(./d:reqType, 'DLESE:'), ':'), 'Technical information not easily determined') or contains(substring-after(substring-after(./d:reqType, 'DLESE:'), ':'), 'To be supplied') or string-length(./d:reqType) = 0"/>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="string-length(./d:reqType)>0 and string-length(./d:minimumVersion)>0 and string-length(./d:maximumVersion)>0">
						<xsl:element name="dc:format">
							<xsl:value-of select="concat(substring-after(substring-after(./d:reqType, 'DLESE:'), ':'), ' with the following min/max version information: ', ./d:minimumVersion, ', ', ./d:maximumVersion)"/>	
						</xsl:element>
					</xsl:when>
					<xsl:when test="string-length(./d:reqType) > 0 and string-length(./d:minimumVersion) > 0">
						<xsl:element name="dc:format">
							<xsl:value-of select="concat(substring-after(substring-after(./d:reqType, 'DLESE:'), ':'), ' with the following minimum version information: ', ./d:minimumVersion)"/>	
						</xsl:element>
					</xsl:when>
					<xsl:when test="string-length(./d:reqType) > 0 and string-length(./d:maximumVersion) > 0">
						<xsl:element name="dc:format">
							<xsl:value-of select="concat(substring-after(substring-after(./d:reqType, 'DLESE:'), ':'), ' with the following maximum version information: ', ./d:maximumVersion)"/>	
						</xsl:element>
					</xsl:when>
					<xsl:when test="string-length(./d:reqType) > 0">
						<xsl:element name="dc:format">
							<xsl:value-of select="substring-after(substring-after(./d:reqType, 'DLESE:'), ':')"/>	
						</xsl:element>
					</xsl:when>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	

<!--17. SUBJECT template mode=SUBJECTS-->
	<xsl:template match="d:subject" mode="subjects">
		<xsl:choose>
			<xsl:when test="contains(.,'Other') or contains(.,'supplied') or not(starts-with(., 'DLESE:'))"/>
			<xsl:otherwise>
				<xsl:element name="dc:subject">
					<xsl:value-of select="substring-after(.,'DLESE:')"/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
</xsl:template>			

<!--18. SUBJECT template mode=KEYWORDS-->
	<xsl:template match="d:keyword" mode="keywords">
		<xsl:if test="string-length(.) > 0">
			<xsl:element name="dc:subject">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:if>
	</xsl:template>			

<!--19. TEACHINGMETHOD template-->
	<xsl:template match="d:teachingMethod">
		<xsl:if test="starts-with(., 'DLESE:') or starts-with(., 'GEM:')">
			<xsl:element name="dct:instructionalMethod">
				<xsl:value-of select="substring-after(., ':')"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>

<!--20. TEMPORAL DESCRIPTION template-->
	<xsl:template match="d:description">
		<xsl:if test="string-length(.)>0">
			<xsl:element name="dct:temporal">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:if>
	</xsl:template>

<!--21. TIMEAD template mode DCT:PERIOD-->
	<xsl:template match="d:timeAD" mode="dct:Period">
<!--if the ADN vocabulary term 'Present' is used for begin and end date, write the value without a W3CDTF scheme because 'Present' is not W3CDTF compliant-->
<!--only the first occurrence of a name element is transformed because the encoding scheme of DCT:PERIOD does not allow multiple names. So a template is not used to pick up all occurrences of name-->
		<xsl:if test="string-length(d:begin/@date)>0 and string-length(d:end/@date)>0">
			<xsl:element name="dct:temporal">
				<xsl:attribute name="xsi:type">dct:Period</xsl:attribute>
				<xsl:choose>
					<!--testing only the first name for data because only be transforming the first name-->
					<xsl:when test="string-length(../../d:periods/d:period/d:name)>0">
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
		</xsl:if>
	</xsl:template>

<!--22. TIMEAD template mode DCT:W3CDTF-->
	<xsl:template match="d:timeAD" mode="dct:W3CDTF">
<!--if the ADN vocabulary term 'Present' is used for begin and end date, do not transform because 'Present' is not W3CDTF compliant-->
<!--if data is present, this aassumes it is W3CDTF compliant and transforms it-->
		<xsl:choose>
			<xsl:when test="d:begin/@date='Present' or d:end/@date='Present' or string-length(d:begin/@date)=0 or string-length(d:end/@date)=0"/>
			<xsl:otherwise>
				<xsl:element name="dct:temporal">
					<xsl:attribute name="xsi:type">dct:W3CDTF</xsl:attribute>
					<xsl:value-of select="d:begin/@date"/>
				</xsl:element>
				<xsl:element name="dct:temporal">
					<xsl:attribute name="xsi:type">dct:W3CDTF</xsl:attribute>
					<xsl:value-of select="d:end/@date"/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

<!--23. TIMEBC template-->
	<xsl:template match="d:timeBC">
<!--only the first occurrence of a name element is transformed because the encoding scheme of DCT:PERIOD does not allow multiple names. So a template is not used to pick up all occurrences of name-->
		<xsl:if test="string-length(d:begin)>0 and string-length(d:end)>0">
			<xsl:element name="dct:temporal">
				<xsl:attribute name="xsi:type">dct:Period</xsl:attribute>
				<xsl:choose>
					<!--testing only the first name for data because only be transforming the first name-->
					<xsl:when test="string-length(../../d:periods/d:period/d:name)>0">
						<xsl:value-of select="concat('start=', d:begin, ' BC;end=', d:end, ' BC;name=', ../../d:periods/d:period/d:name)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat('start=', d:begin, ' BC;end=', d:end, ' BC')"/>
					</xsl:otherwise>
				</xsl:choose>	
			</xsl:element>
		</xsl:if>
	</xsl:template>

<!--24. TIMERELATIVE template-->
	<xsl:template match="d:timeRelative">
<!--only the first occurrence of a name element is transformed because the encoding scheme of DCT:PERIOD does not allow multiple names. So a template is not used to pick up all occurrences of name-->
		<xsl:if test="string-length(d:begin)>0 and string-length(d:end)>0">
			<xsl:element name="dct:temporal">
				<xsl:attribute name="xsi:type">dct:Period</xsl:attribute>
				<xsl:choose>
					<xsl:when test="string-length(../../d:periods/d:period/d:name)>0">
						<xsl:value-of select="concat('start=', d:begin, ' ', d:begin/@units, ';end=', d:end, ' ', d:end/@units, ';name=', ../../d:periods/d:period/d:name)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat('start=', d:begin, ' ', d:begin/@units, ';end=', d:end, ' ', d:end/@units)"/>
					</xsl:otherwise>
				</xsl:choose>	
			</xsl:element>
		</xsl:if>
	</xsl:template>

<!--25. TOOLFOR template-->
	<xsl:template match="d:toolFor">
		<xsl:if test="starts-with(., 'DLESE:') or starts-with(., 'GEM:')">
			<xsl:element name="dct:mediator">
				<xsl:value-of select="substring-after(., ':')"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>

<!--26. URL-ENTRY template; mode=RELATIONS-->
<!--determine if the kind and url attriubte has content; if both have content, then output the appropriate dc:relation using dct:terms element-->
<!--outputs only dct:terms not dct:tems with the dct:URI attribute because not doing any checks to ensure meeting dct:URI requirements-->
<!--NSDL has checks in place to convert URLs to dct:URI-->
	<xsl:template match="d:urlEntry" mode="relations">
		<xsl:if test="(starts-with(./@kind, 'DLESE:') or starts-with(./@kind, 'DC:')) and string-length(./@url) > 0">
			<xsl:apply-templates select="./@kind"/>
		</xsl:if>
	</xsl:template>

<!--x. URL-ENTRY template; mode=SOURCE-->
<!--determine if the kind attriubte has a value of 'Is based on'-->
	<xsl:template match="d:urlEntry" mode="source">
		<xsl:if test="contains(./@kind, 'Is based on') and string-length(./@url) > 0">
			<xsl:element name="dc:source">
				<xsl:value-of select="./@url"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>


<!--3. GRADERANGE template-->
<!--get rid of after updating the OAI one-->
<!--	<xsl:template match="d:gradeRange">
		<xsl:choose>
			<xsl:when test="contains(., 'supplied') or contains(., 'Not applicable') or not(starts-with(., 'DLESE:'))"/>
			<xsl:otherwise>
				<xsl:element name="dct:educationLevel">
					<xsl:value-of select="substring-after(., ':')"/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>-->
</xsl:stylesheet>
