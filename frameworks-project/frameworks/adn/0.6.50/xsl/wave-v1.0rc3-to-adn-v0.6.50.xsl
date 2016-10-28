<?xml version="1.0"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:wv="http://cow.lhs.berkeley.edu"
    xmlns:adn="http://adn.dlese.org"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="xsi wv xs"
    version="1.1">

<!--PURPOSE: to transform NASA Wavelength version 1.0rc3records into the DLESE ADC version 0.6.50 metadata records-->
<!--CREATION: 2013-09-09: by Katy Ginger, University Corporation for Atmospheric Research (UCAR)-->
<!--HISTORY: none-->

	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

<!--VARIABLES used throughout the transform-->
<!-- ************************************************** -->

<!--TRANSFORMATION CODE-->
<!-- ************************************************** -->
	<xsl:template match="*|/">
		<xsl:apply-templates select="wv:record"/>
	</xsl:template>

<!--TRANSFORMATION CODE for NASA Wavelenght to ADN-->
<!-- ********************************************************-->
	<xsl:template match="wv:record">
		<itemRecord  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://adn.dlese.org" xsi:schemaLocation="http://adn.dlese.org http://www.dlese.org/Metadata/adn-item/0.6.50/record.xsd">
			<general>
<!--title-->
			<xsl:apply-templates select="wv:general/wv:title" mode="process">
				<xsl:with-param name="tag">title</xsl:with-param>
			</xsl:apply-templates>

<!--description-->
		<xsl:apply-templates select="wv:general/wv:description" mode="process">
			<xsl:with-param name="tag">description</xsl:with-param>
		</xsl:apply-templates>

<!--language: wavelength uses a subset of the accepted terms of xs:language that ADN uses; so no vocab map; just apply template-->
		<xsl:apply-templates select="wv:general/wv:language" mode="process">
			<xsl:with-param name="tag">language</xsl:with-param>
		</xsl:apply-templates>

<!--subject using topicsSubjects; topicSubjects is required; so no need to test if present, just apply template-->
	<subjects>
		<xsl:apply-templates select="wv:general/wv:topicsSubjects" mode="subject">
			<xsl:with-param name="tag">subject</xsl:with-param>
		</xsl:apply-templates>
	</subjects>
<!--keywords  using topicsSubjects; topicSubjects is required; so no need to test if present, just apply template-->
	<keywords>
<!--keywords  using topicsSubjects; topicSubjects is required; so no need to test if present, just apply template-->
		<xsl:apply-templates select="wv:general/wv:topicsSubjects" mode="process">
			<xsl:with-param name="tag">keyword</xsl:with-param>
		</xsl:apply-templates>
<!--keywords using keywords-->
		<xsl:apply-templates select="wv:general/wv:keywords" mode="process">
			<xsl:with-param name="tag">keyword</xsl:with-param>
		</xsl:apply-templates>
<!--keywords  using missionOrProgram-->
		<xsl:apply-templates select="wv:general/wv:missionOrProgram" mode="process">
			<xsl:with-param name="tag">keyword</xsl:with-param>
		</xsl:apply-templates>
<!--keywords  using assessments-->
		<xsl:apply-templates select="wv:educational/wv:assessments" mode="process">
			<xsl:with-param name="tag">keyword</xsl:with-param>
		</xsl:apply-templates>
<!--keywords  using accessibility-->
		<xsl:apply-templates select="wv:diversity/wv:accessibility" mode="process">
			<xsl:with-param name="tag">keyword</xsl:with-param>
		</xsl:apply-templates>	
	</keywords>
		</general>
		<lifecycle>
			<contributors>
				<!--contributors  is required in NASA wavelenght; so just write the tag; no need to test if present, just apply template-->
				<xsl:apply-templates select="wv:authorshipRightsAccessRestrictions/wv:authorContact/wv:contributors/wv:person" mode="contributor">
					<xsl:with-param name="tag">contributor</xsl:with-param>
					<xsl:with-param name="subtag">person</xsl:with-param>
					<xsl:with-param name="att">role</xsl:with-param>
					<xsl:with-param name="att2">date</xsl:with-param>
				</xsl:apply-templates>	
				<xsl:apply-templates select="wv:authorshipRightsAccessRestrictions/wv:authorContact/wv:contributors/wv:organization" mode="contributor">
					<xsl:with-param name="tag">contributor</xsl:with-param>
					<xsl:with-param name="subtag">organization</xsl:with-param>
					<xsl:with-param name="att">role</xsl:with-param>
					<xsl:with-param name="att2">date</xsl:with-param>
				</xsl:apply-templates>	
			</contributors>
		</lifecycle>

		<metaMetadata>
		<!--the fields here don't fall into using the PROCESS template so they are just written directly here-->
<!--catalog entry using recordID-->
			<catalogEntries>
				<xsl:element name="catalog" namespace="http://adn.dlese.org">
					<xsl:attribute name="entry">
						<xsl:value-of select="wv:general/wv:recordID"/>				
					</xsl:attribute>
					<xsl:text>Digital Library for Earth System Education</xsl:text>
				</xsl:element>
			</catalogEntries>
<!-- dateInfo using recordDate; in NASA Wavelength recordDate can occur only once; so just write-->
			<xsl:element name="dateInfo" namespace="http://adn.dlese.org">
				<xsl:attribute name="created">
					<xsl:value-of select="wv:general/wv:recordDate"/>
				</xsl:attribute>
			</xsl:element>
<!--just assign status for ADN since it does not exist in NASA Wavelength-->	
			<statusOf status="Accessioned"/>
<!--metadata language using metadataLanguage; in NASA Wavelength metadataLanguage can occur only once; so just write-->
			<xsl:element name="language" namespace="http://adn.dlese.org">
				<xsl:value-of select="wv:general/wv:metadataLanguage"/>
			</xsl:element>
			<scheme>ADN (ADEPT/DLESE/NASA Alexandria Digital Earth Prototype/Digital Library for Earth System Education/National Aeronautics and Space Administration)</scheme>
			<copyright>Copyright (c) 2013 NASA and UCAR (University Corporation for Atmospheric Research)</copyright>
			<termsOfUse URI="http://www.dlese.org/documents/policy/terms_use_full.html">Terms of use consistent with DLESE (Digital Library for Earth System Education) policy.</termsOfUse>
			<contributors>
				<!--xxx-->
				<xsl:apply-templates select="wv:general/wv:cataloger" mode="cataloger">
					<xsl:with-param name="tag">contributor</xsl:with-param>
					<xsl:with-param name="subtag">person</xsl:with-param>
				</xsl:apply-templates>	
			</contributors>
			
		</metaMetadata>

		<technical>
<!--NASA Wavelength requires url so assume everything is online in order to use the ADN tag of online; ADN requires a choice between online or offline as the inital tag set-->
			<online>
<!--url using url-->
				<xsl:apply-templates select="wv:general/wv:url" mode="process">
					<xsl:with-param name="tag">primaryURL</xsl:with-param>
				</xsl:apply-templates>
			
<!--medium using format-->
<!--medium is not a controlled vocab in ADN and it is required in NASA Wavelength; so just write outer tag and apply template-->
				<mediums>
					<xsl:apply-templates select="wv:general/wv:format" mode="process"> 
						<xsl:with-param name="tag">medium</xsl:with-param>
					</xsl:apply-templates>
				</mediums>
				
<!--size, duration or mirror URLs are not present in NASA Wavelength-->
<!--NASA Wavelength has a relatedUrl of 'Foreign language version'; this has NOT been mapped to ADN mirror URL-->

<!--technical requirements are required by ADN but are not present in NASA Wavelength; write the generic value-->
				<requirements>
					<requirement>
						<reqType>DLESE:General:No specific technical requirements</reqType>
					</requirement>	
				</requirements>			
			</online>
		</technical>

		<educational>

<!--educationLevel-->
<!--gradeRange using educationalLevel; educationLevel is required in NASA Wavelength; so write outer ADN audiences tag and apply template for inner ADN audience tag-->
		<audiences>
			<xsl:apply-templates select="wv:general/wv:audience/wv:educationalLevel" mode="grade">
				<xsl:with-param name="tag">audience</xsl:with-param>
				<xsl:with-param name="subtag">gradeRange</xsl:with-param>
			</xsl:apply-templates>
<!--NASA wavelength has audience refinement but the ADN beneficiary does not work well; so not mapped-->	

<!--NASA wavelenth has 	instructionalStrategies and ADN has teachingMethod; but the teaching method should apply to the ADN gradeRange; so do NOT call a template. And the TEACH template below is not used but holds the mapping-->
		</audiences>

<!--resourceType using resourceType; resourceType is required in NASA Wavelength; so write outer ADN resourceTypes tag and apply template for inner ADN audience tag-->
		<resourceTypes>
			<xsl:apply-templates select="wv:general/wv:resourceType" mode="type">
				<xsl:with-param name="tag">resourceType</xsl:with-param>
			</xsl:apply-templates>
		</resourceTypes>

<!--NASA wavelenth has 	standards as ASN IDs but ADN only does Benchmarks an NSES as controlled vocab terms. ADN cannot handle ASN ids-->

		</educational>
		<rights>
		<!--cost using materialsCost but materialsCost is optional; both NASA and ADNhave different controlled vocabs-->
		<cost>
			<xsl:choose>
				<xsl:when test="string-length(wv:timeMaterialsCost/wv:materialsCost) = 0 ">
					<xsl:text>DLESE:Unknown</xsl:text>
				</xsl:when>
				<xsl:when test="wv:timeMaterialsCost/wv:materialsCost = 'Free' ">
					<xsl:text>DLESE:No</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>DLESE:Yes</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</cost>
		
			<description>
		<!--rights.description using rights.copyright; but the NASA Wavelength value is optional and ADN is required-->
				<xsl:choose>
					<xsl:when test="string-length(wv:authorshipRightsAccessRestrictions/wv:rights/wv:copyright) > 0">
						<xsl:value-of select="concat(wv:authorshipRightsAccessRestrictions/wv:rights/wv:copyright, '   ',  wv:authorshipRightsAccessRestrictions/wv:rights/wv:license/wv:name)"/>
					</xsl:when>
					<xsl:when test="string-length(wv:authorshipRightsAccessRestrictions/wv:rights/wv:copyright) = 0 and string-length(wv:authorshipRightsAccessRestrictions/wv:rights/wv:license/wv:name) > 0">
						<xsl:value-of select="wv:authorshipRightsAccessRestrictions/wv:rights/wv:license/wv:name"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Copyright and other restrictions are unknown.</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</description>
		</rights>
			
<!--relation using relatedUrl-->
<!--NASA Wavelength has a relation term 'Foreign language version' that cannot be mapped in relation-->
<!--	variable for NASA Wavelength; if allkinds only equals 'Foreign language version' do not write a ADN relation -->
		<xsl:variable name="allkinds">
			<xsl:for-each select="wv:general/wv:relatedUrl/@kind">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="contains($allkinds,  'Has part') or contains($allkinds, 'Is format of ') or contains($allkinds, 'Is part of') ">
			<relations>
				<xsl:apply-templates select="wv:general/wv:relatedUrl" mode="relation"> 
					<xsl:with-param name="tag">relation</xsl:with-param>
					<xsl:with-param name="subtag">urlEntry</xsl:with-param>
				</xsl:apply-templates>
			</relations>
		</xsl:if>
		

<!--access-->
<!--MSP2 uses the NSDL 1.02.020 vocab-->
		<xsl:apply-templates select="wv:lifecycle/wv:access" mode="process"> 
			<xsl:with-param name="tag">access</xsl:with-param>
		</xsl:apply-templates>

<!--contributor-->
		<xsl:apply-templates select="wv:lifecycle/wv:contributor " mode="process"> 
			<xsl:with-param name="tag">contributor</xsl:with-param>
			<xsl:with-param name="att">role</xsl:with-param>
		</xsl:apply-templates>

		</itemRecord><!--end record element-->
	</xsl:template>



<!--TEMPLATES for msp2 tp math_path-->
<!-- ****************************************-->
<!--PROCESS:writes all tag sets that are not a content standard, box or point-->


<!--PROCESS template-->
	<xsl:template match="node()" name="process" mode="process">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="att"/>
		<xsl:param name="string" select="."/>
		<xsl:param name="string2" select="./@node()"/><!--use node to pick up the value of whatever single attribute is present; this works because these elements do not have more than 1 attribute-->

		<xsl:if test="string-length($string) > 0">
			<xsl:element name="{$tag}" namespace="http://adn.dlese.org">
				<xsl:if test="string-length($att) > 0">
					<xsl:attribute name="{$att}">
						<xsl:value-of select="$string2"/>
					</xsl:attribute>	
				</xsl:if>
				<xsl:value-of select="$string"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>	

<!--CATALOGER template-->
	<xsl:template match="node()" mode="cataloger">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
<!--NASA Wavelength uses the same role controlled vocabulary as ADN; no mapping required-->
<!--NASA Wavelength requires organizationName for both person and organization; so data is present; so just write it-->
<!--NASA Wavelength requires date; so just write to ADN-->
<!--IN ADN, a person requires an email but in NASA Wavelength email for person is optional; so check if present and use the data; if not present write ADN email as unknown-->
		<xsl:param name="tag"/>
		<xsl:param name="subtag"/>
		<xsl:element name="{$tag}" namespace="http://adn.dlese.org">
			<xsl:attribute name="role">
				<xsl:text>Creator</xsl:text>
			</xsl:attribute>	
			<xsl:element name="{$subtag}" namespace="http://adn.dlese.org">
				<xsl:apply-templates select="wv:nameFirst" mode="process">
					<xsl:with-param name="tag">nameFirst</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates select="wv:nameLast" mode="process">
					<xsl:with-param name="tag">nameLast</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates select="wv:emailPrimary" mode="process">
					<xsl:with-param name="tag">emailPrimary</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates select="wv:institutionName" mode="process">
					<xsl:with-param name="tag">instName</xsl:with-param>
				</xsl:apply-templates>
			</xsl:element>
		</xsl:element>
	</xsl:template>	

<!--CONTRIBUTORS template-->
	<xsl:template match="node()" mode="contributor">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
<!--NASA Wavelength uses the same role controlled vocabulary as ADN; no mapping required-->
<!--NASA Wavelength requires organizationName for both person and organization; so data is present; so just write it-->
<!--NASA Wavelength requires date; so just write to ADN-->
<!--IN ADN, a person requires an email but in NASA Wavelength email for person is optional; so check if present and use the data; if not present write ADN email as unknown-->
		<xsl:param name="tag"/>
		<xsl:param name="subtag"/>
		<xsl:param name="att"/>
		<xsl:param name="att2"/>
		<xsl:param name="string" select="./wv:organizationName"/>
		<xsl:param name="string2" select="./wv:role"/>
		<xsl:param name="string3" select="./wv:nameFirst"/>
		<xsl:param name="string4" select="./wv:nameLast"/>
		<xsl:param name="string5" select="../../../wv:date"/>
		<xsl:param name="string6" select="./wv:emailPrimary"/>
		<xsl:element name="{$tag}" namespace="http://adn.dlese.org">
			<xsl:attribute name="{$att}">
				<xsl:value-of select="$string2"/>
			</xsl:attribute>	
			<xsl:attribute name="{$att2}">
				<xsl:value-of select="$string5"/>
			</xsl:attribute>	
			<xsl:element name="{$subtag}" namespace="http://adn.dlese.org">
				<xsl:if test="string-length($string3) > 0">
					<xsl:element name="nameFirst" namespace="http://adn.dlese.org">
						<xsl:value-of select="$string3"/>
					</xsl:element>
					<xsl:element name="nameLast" namespace="http://adn.dlese.org">
						<xsl:value-of select="$string4"/>
					</xsl:element>
					<xsl:element name="emailPrimary" namespace="http://adn.dlese.org">
						<xsl:choose>
							<xsl:when test="string-length($string6) > 0">
								<xsl:value-of select="$string6"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>Unknown</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:element>
				</xsl:if>
				<xsl:element name="instName" namespace="http://adn.dlese.org">
					<xsl:value-of select="$string"/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>	

<!--GRADE LEVEL template-->
	<xsl:template match="node()" name="grade" mode="grade">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="subtag"/>
		<xsl:param name="att"/>
		<xsl:param name="string" select="."/>
		<xsl:param name="string2" select="./@node()"/><!--use node to pick up the value of whatever single attribute is present; this works because these elements do not have more than 1 attribute-->

		<xsl:if test="string-length($string) > 0">
			<xsl:element name="{$tag}" namespace="http://adn.dlese.org">
			<xsl:element name="{$subtag}" namespace="http://adn.dlese.org">
				<xsl:if test="string-length($att) > 0">
					<xsl:attribute name="{$att}">
						<xsl:value-of select="$string2"/>
					</xsl:attribute>	
				</xsl:if>
				<xsl:choose>
					<xsl:when test="contains(., 'Primary elementary') or . = 'Pre-kindergarten' ">
						<xsl:text>DLESE:Primary elementary</xsl:text>
					</xsl:when>
					<xsl:when test="contains(., 'Upper elementary')">
						<xsl:text>DLESE:Intermediate elementary</xsl:text>
					</xsl:when>
					<xsl:when test="contains(., 'Middle school')">
						<xsl:text>DLESE:Middle school</xsl:text>
					</xsl:when>
					<xsl:when test="contains(., 'High school')">
						<xsl:text>DLESE:High school</xsl:text>
					</xsl:when>
					<xsl:when test=".= 'Higher education' or .= 'Higher education:Undergraduate' or contains(., 'Non-majors') or contains(., 'lower division') or contains(., 'Technical (lower)')">
						<xsl:text>DLESE:Undergraduate lower division</xsl:text>
					</xsl:when>
					<xsl:when test="contains(., 'Majors, upper division') or contains(., 'Pre-service teachers') or contains(., 'Technical (upper)')">
						<xsl:text>DLESE:Undergraduate upper division</xsl:text>
					</xsl:when>
					<xsl:when test="contains(., 'Graduate/professional') or contains(., 'professional development')">
						<xsl:text>DLESE:Graduate or professional</xsl:text>
					</xsl:when>
					<xsl:when test="contains(., 'Informal education')">
						<xsl:text>DLESE:Informal education</xsl:text>
					</xsl:when>
					<xsl:when test="contains(., 'public')">
						<xsl:text>DLESE:General public</xsl:text>
					</xsl:when>
					<xsl:when test=". = 'Higher education' ">
						<xsl:text>DLESE:Undergraduate lower division</xsl:text>
					</xsl:when>
				</xsl:choose>
			</xsl:element>
			</xsl:element>
		</xsl:if>
	</xsl:template>	

<!--RELATION template-->
	<xsl:template match="node()" name="relation" mode="relation">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="subtag"/>
		<xsl:param name="string" select="."/>
		<xsl:param name="string2" select="./@kind"/>
		<xsl:param name="string3" select="./@title"/>
		<!--NASA Wavelength relatedUrl/@title is optional.... will need to check if present-->

		<xsl:if test="$string2 != 'Foreign language version' ">
			<xsl:element name="{$tag}" namespace="http://adn.dlese.org">
				<xsl:element name="{$subtag}" namespace="http://adn.dlese.org">
					<xsl:choose>
						<xsl:when test="$string2 = 'Has part'  and string-length($string3) > 0">
							<xsl:attribute name="kind">
								<xsl:text>DC:Has part</xsl:text>
							</xsl:attribute>	
							<xsl:attribute name="url">
								<xsl:value-of select="$string"/>
							</xsl:attribute>	
							<xsl:attribute name="title">
								<xsl:value-of select="$string3"/>
							</xsl:attribute>	
						</xsl:when>
						<xsl:when test="$string2 = 'Has part'  and string-length($string3) = 0">
							<xsl:attribute name="kind">
								<xsl:text>DC:Has part</xsl:text>
							</xsl:attribute>	
							<xsl:attribute name="url">
								<xsl:value-of select="$string"/>
							</xsl:attribute>	
						</xsl:when>
						<xsl:when test="$string2 = 'Is format of'  and string-length($string3) > 0">
							<xsl:attribute name="kind">
								<xsl:text>DC:Is format of</xsl:text>
							</xsl:attribute>	
							<xsl:attribute name="url">
								<xsl:value-of select="$string"/>
							</xsl:attribute>	
							<xsl:attribute name="title">
								<xsl:value-of select="$string3"/>
							</xsl:attribute>	
						</xsl:when>
						<xsl:when test="$string2 = 'Is format of'  and string-length($string3)= 0">
							<xsl:attribute name="kind">
								<xsl:text>DC:Is format of</xsl:text>
							</xsl:attribute>	
							<xsl:attribute name="url">
								<xsl:value-of select="$string"/>
							</xsl:attribute>	
						</xsl:when>
						<xsl:when test="$string2 = 'Is part of'  and string-length($string3) > 0">
							<xsl:attribute name="kind">
								<xsl:text>DC:Is part of</xsl:text>
							</xsl:attribute>	
							<xsl:attribute name="url">
								<xsl:value-of select="$string"/>
							</xsl:attribute>	
							<xsl:attribute name="title">
								<xsl:value-of select="$string3"/>
							</xsl:attribute>	
						</xsl:when>
						<xsl:when test="$string2 = 'Is part of'  and string-length($string3) = 0">
							<xsl:attribute name="kind">
								<xsl:text>DC:Is part of</xsl:text>
							</xsl:attribute>	
							<xsl:attribute name="url">
								<xsl:value-of select="$string"/>
							</xsl:attribute>	
						</xsl:when>
					</xsl:choose>
				</xsl:element>
			</xsl:element>
		</xsl:if>
	</xsl:template>

<!--TYPE template-->
	<xsl:template match="node()" mode="type">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:element name="{$tag}" namespace="http://adn.dlese.org">
			<xsl:choose>
				<xsl:when test="contains(. , 'Assessment materials') ">
					<xsl:text>DLESE:Learning materials:Assessment</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Activity' ">
					<xsl:text>DLESE:Learning materials:Classroom activity</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Animation/movie' ">
					<xsl:text>DLESE:Visual:Video</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Article' ">
					<xsl:text>DLESE:Text:Journal article</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Case study' ">
					<xsl:text>DLESE:Learning materials:Case study</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Course' ">
					<xsl:text>DLESE:Learning materials:Course</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Curriculum' ">
					<xsl:text>DLESE:Learning materials:Curriculum</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Data' ">
					<xsl:text>DLESE:Data:In situ dataset</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Demonstration' ">
					<xsl:text>DLESE:Learning materials:Presentation or demonstration</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Documentary' ">
					<xsl:text>DLESE:Visual:Video</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Experiment/lab' ">
					<xsl:text>DLESE:Learning materials:Lab activity</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Exhibit' ">
					<xsl:text>DLESE:Learning materials:Case study</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Field trip' ">
					<xsl:text>DLESE:Learning materials:Field activity</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Graph' ">
					<xsl:text>DLESE:Visual:Scientific illustration</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Illustration' ">
					<xsl:text>DLESE:Visual:Artistic illustration</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Instructor guide/manual' ">
					<xsl:text>DLESE:Learning materials:Instructor guide</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Informative text' ">
					<xsl:text>DLESE:Text:Reference</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Interactive simulation' ">
					<xsl:text>DLESE:Visual:Scientific visualization</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Lecture' ">
					<xsl:text>DLESE:Audio:Lecture</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Lesson or lesson plan' ">
					<xsl:text>DLESE:Learning materials:Lesson plan</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Map' ">
					<xsl:text>DLESE:Visual:Map</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Model' ">
					<xsl:text>DLESE:Visual:Scientific visualization</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Music' ">
					<xsl:text>DLESE:Audio:Music</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Numerical/computer model' ">
					<xsl:text>DLESE:Tool:Software</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Photograph' ">
					<xsl:text>DLESE:Visual:Photograph</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Presentation' ">
					<xsl:text>DLESE:Learning materials:Presentation or demonstration</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Problem set' ">
					<xsl:text>DLESE:Learning materials:Problem set</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Project' ">
					<xsl:text>DLESE:Learning materials:Project</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Simulation' ">
					<xsl:text>DLESE:Visual:Scientific visualization</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Sound' ">
					<xsl:text>DLESE:Audio:Sound</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Specimen' ">
					<xsl:text>DLESE:Offline:Physical object</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Syllabus' ">
					<xsl:text>DLESE:Learning materials:Syllabus</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Textbook' ">
					<xsl:text>DLESE:Text:Book</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Tutorial' ">
					<xsl:text>DLESE:Learning materials:Tutorial</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Unit' ">
					<xsl:text>DLESE:Learning materials:Module or unit</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Instructional materials:Voice recording' ">
					<xsl:text>DLESE:Audio:Audio webcast</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>DLESE:To be supplied</xsl:text>
					<!-- used for: 
Instructional materials:Game, 
Instructional materials:Image/image set, 
Instructional materials:Model,
Instructional materials:Planetarium show,
Instructional materials:Professional development,
Instructional materials:Student guide,
Instructional materials:Writing assignment
-->
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
<!-- not mapped: 
-->
	</xsl:template>	

<!--SUBJECT template-->
	<xsl:template match="node()" name="subject" mode="subject">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="att"/>
		<xsl:param name="string" select="."/>
		<xsl:param name="string2" select="./@node()"/><!--use node to pick up the value of whatever single attribute is present; this works because these elements do not have more than 1 attribute-->

		<xsl:if test="string-length($string) > 0">
			<xsl:element name="{$tag}" namespace="http://adn.dlese.org">
				<xsl:if test="string-length($att) > 0">
					<xsl:attribute name="{$att}">
						<xsl:value-of select="$string2"/>
					</xsl:attribute>	
				</xsl:if>
				<xsl:choose>
					<xsl:when test=". = 'Earth and space science'">
						<xsl:text>DLESE:Environmental science</xsl:text>
					</xsl:when>
					<xsl:when test="contains(., 'Earth and space science:Astronomy')">
						<xsl:text>DLESE:Space science</xsl:text>
					</xsl:when>
					<xsl:when test=". = 'Earth and space science:Earth processes' ">
						<xsl:text>DLESE:Atmospheric science</xsl:text>
						<xsl:text>DLESE:Geophysics</xsl:text>
						<xsl:text>DLESE:Geology</xsl:text>
						<xsl:text>DLESE:Natural hazards</xsl:text>
						<xsl:text>DLESE:Physical geography</xsl:text>
					</xsl:when>
					<xsl:when test=". = 'Earth and space science:Earth processes:Climate' ">
						<xsl:text>DLESE:Climatology</xsl:text>
					</xsl:when>

					<xsl:when test="contains(., 'energy budget')  or . = 'Earth and space science:Earth processes:Weather' or . = 'Earth and space science:Earth structure:Atmosphere' ">
						<xsl:text>DLESE:Atmospheric science</xsl:text>
					</xsl:when>
					<xsl:when test=". = 'Earth and space science:Earth processes:Geochemical cycles' ">
						<xsl:text>DLESE:Geochemistry</xsl:text>
					</xsl:when>
					<xsl:when test=". = 'Earth and space science:Earth processes:Geologic processes' or . = 'Earth and space science:Earth structure:Geology' ">
						<xsl:text>DLESE:Geology</xsl:text>
					</xsl:when>
					<xsl:when test=". = 'Earth and space science:Earth structure' ">
						<xsl:text>DLESE:Atmospheric science</xsl:text>
						<xsl:text>DLESE:Geophysics</xsl:text>
						<xsl:text>DLESE:Geology</xsl:text>
						<xsl:text>DLESE:Natural hazards</xsl:text>
						<xsl:text>DLESE:Physical geography</xsl:text>
					</xsl:when>
					<xsl:when test=". = 'Earth and space science:Earth structure:Cryosphere' ">
						<xsl:text>DLESE:Cryology</xsl:text>
					</xsl:when>
					<xsl:when test="contains(.,  'magnetic field') ">
						<xsl:text>DLESE:Environmental science</xsl:text>
					</xsl:when>
					<xsl:when test=". = 'Earth and space science:Earth structure:Landforms/geography' or . = 'Earth and space science:Earth structure:Vegetation/land cover' ">
						<xsl:text>DLESE:Physical geography</xsl:text>
					</xsl:when>
					<xsl:when test=". = 'Earth and space science:Earth structure:Ocean and water' ">
						<xsl:text>DLESE:Physical oceanography</xsl:text>
						<xsl:text>DLESE:Hydrology</xsl:text>
					</xsl:when>
					<xsl:when test=" . = 'Earth and space science:Earth history:Archeology' or . = 'Earth and space science:Earth history:History of life'">
						<xsl:text>DLESE:Other</xsl:text>
					</xsl:when>
					<xsl:when test=". = 'Earth and space science:Earth history'  or . = 'Earth and space science:Earth history:Geologic time' ">
						<xsl:text>DLESE:Geologic time</xsl:text>
					</xsl:when>
					<xsl:when test=". = 'Earth and space science:Earth, moon and sun' or . = 'Earth and space science:Earth, moon and sun:Eclipses' or . = 'Earth and space science:Earth, moon and sun:Days'  or . = 'Earth and space science:Earth, moon and sun:Seasons'">
						<xsl:text>DLESE:Environmental science</xsl:text>
					</xsl:when>
					<xsl:when test=". = 'Earth and space science:Earth, moon and sun:Tides' ">
						<xsl:text>DLESE:Physical oceanography</xsl:text>
					</xsl:when>
					<xsl:when test="contains(.,  'Earth and space science:Solar system')">
						<xsl:text>DLESE:Space science</xsl:text>
					</xsl:when>
					<xsl:when test="contains(., 'Engineering and technology') or contains(., 'The nature of technology') ">
						<xsl:text>DLESE:Technology</xsl:text>
					</xsl:when>
					<xsl:when test=". = 'Life sciences' or . = 'Life sciences:Astrobiology' or . = 'Earth and space science:Earth structure:Biosphere'">
						<xsl:text>DLESE:Biology</xsl:text>
					</xsl:when>
					<xsl:when test=". = 'Life sciences:Ecology and ecosystems' ">
						<xsl:text>DLESE:Ecology</xsl:text>
					</xsl:when>
					<xsl:when test="contains(., 'Mathematics')">
						<xsl:text>DLESE:Mathematics</xsl:text>
					</xsl:when>
					<xsl:when test="contains(., 'Physical sciences')">
						<xsl:text>DLESE:Physics</xsl:text>
					</xsl:when>
					<xsl:when test="contains(., 'The nature of science')">
						<xsl:text>DLESE:History and philosophy of science</xsl:text>
					</xsl:when>
				</xsl:choose>
			</xsl:element>
		</xsl:if>
	</xsl:template>	

<!--TEACH template-->
	<xsl:template match="node()" mode="teach">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:element name="{$tag}" namespace="http://adn.dlese.org">
			<xsl:choose>
				<xsl:when test=". = 'Brainstorming' ">
					<xsl:text>GEM:Brainstorming</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Computer assisted instruction' ">
					<xsl:text>GEM:Computer assisted instruction</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Computer simulations/models/visualizations' ">
					<xsl:text>GEM:Computer simulations</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Cooperative learning' ">
					<xsl:text>GEM:Cooperative learning</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Cues, questions, and advanced organizers' ">
					<xsl:text>GEM:Advanced organizers</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Demonstrations' ">
					<xsl:text>GEM:Demonstrations</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Dialog journals' ">
					<xsl:text>GEM:Dialog journals</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Discussions' ">
					<xsl:text>GEM:Discussions</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Dramatic play drills' ">
					<xsl:text>GEM:Dramatic play drills</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Generating and testing hypotheses' ">
					<xsl:text>GEM:Questioning techniques</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Guided inquiry' ">
					<xsl:text>GEM:Guided design</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Hands-on learning' ">
					<xsl:text>GEM:Hands-on learning</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Integrated instruction' ">
					<xsl:text>GEM:Integrated instruction</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Lab procedures'">
					<xsl:text>GEM:Lab procedures</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Lecture' ">
					<xsl:text>GEM:Lecture</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Project-based learning' ">
					<xsl:text>GEM:Project-based learning</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Role playing' ">
					<xsl:text>GEM:Role playing</xsl:text>
				</xsl:when>
				<xsl:when test=". = 'Tutorial program' ">
					<xsl:text>GEM:Tutorial programs</xsl:text>
				</xsl:when>
			</xsl:choose>
		</xsl:element>
<!-- not mapped: 
Activating prior knowledge
Homework and practice
Identifying similarities and differences
Nonlinguistic representations
Open inquiry
Nonlinguistic representations
Problem-based learning/solving real-world problems
Reinforcing effort and providing recognition
Setting objectives and providing feedback
Summarizing and note taking
Teamwork
OTHER-->
	</xsl:template>	


<!--RELATED RESOURCE template-->
	<xsl:template match="node()" mode="related">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="string" select="."/>
		<xsl:param name="string2" select="./@url"/>
		<xsl:param name="string3" select="./@title"/>
		<xsl:param name="string4" select="./@type"/>
		<xsl:if test="string-length($string2) > 0">
			<xsl:element name="{$tag}" namespace="http://adn.dlese.org">
				<xsl:attribute name="type">
					<xsl:value-of select="$string4"/>
				</xsl:attribute>	
				<xsl:attribute name="url">
					<xsl:value-of select="$string2"/>
				</xsl:attribute>	
				<xsl:attribute name="title">
					<xsl:value-of select="$string3"/>
				</xsl:attribute>	
			</xsl:element>
		</xsl:if>
	</xsl:template>	

</xsl:stylesheet>
<!--LICENSE AND COPYRIGHT
The contents of this file are subject to the Educational Community License v1.0 (the "License"); you may not use this file except in compliance with the License. You should obtain a copy of the License from http://www.opensource.org/licenses/ecl1.php. Files distributed under the License are distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the specific language governing rights and limitations under the License. Copyright 2002-2009 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR). All rights reserved.-->