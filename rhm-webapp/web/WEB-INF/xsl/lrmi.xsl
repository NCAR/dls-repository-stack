<xsl:stylesheet version="2.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xmlns:crsd="http://ns.nsdl.org/MRingest/crsd_v1.05/"
    xmlns:OAI2.0="http://www.openarchives.org/OAI/2.0/"
    xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
    xmlns:native_nsdl_dc_v1.00="http://ns.nsdl.org/nsdl_dc_v1.00"     
    xmlns:native_nsdl_dc_v1.01="http://ns.nsdl.org/nsdl_dc_v1.01"
    xmlns:nsdl_dc="http://ns.nsdl.org/nsdl_dc_v1.02/"
    xmlns:dc="http://purl.org/dc/elements/1.1/" 
    xmlns:dct="http://purl.org/dc/terms/"
    xmlns:ieee="http://www.ieee.org/xsd/LOMv1p0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

        <xsl:template match="/">
        	<nsdl_dc:nsdl_dc schemaVersion="1.02.000" xsi:schemaLocation="http://ns.nsdl.org/nsdl_dc_v1.02/ http://ns.nsdl.org/schemas/nsdl_dc/nsdl_dc_v1.02.xsd">
        		<xsl:apply-templates/>
			</nsdl_dc:nsdl_dc>
        </xsl:template>
        
      	
      	<xsl:template match="HashMap/items/*[name()='id']">
      		<xsl:call-template name="display-tag">
    			<xsl:with-param name="tag" select="'identifier'"/>
    			<xsl:with-param name="value" select="node()"/>
    			<xsl:with-param name="prefix" select="'dc'"/>
    			<xsl:with-param name="xsi-url-attr" select="'false'"/>
          </xsl:call-template>
      	</xsl:template>
      	      	
      	<xsl:template match="HashMap/items/properties/*[name()='name' or name='about' or name()='author' or name()='description' or name()='subject' or name()='inLanguage' or name()='learningResourceType' or name()='intendedEndUserRole' or name()='contributor' or name()='isBasedOnURL' or name()='useRightsURL' or name()='url' or name()='encodings' or name()='id' or name()='publisher' or name()='keywords']">


      			<xsl:variable name="tag">
      				<xsl:choose>
		      			<xsl:when test="name() = 'url' or name()='id'">identifier</xsl:when>
					    <xsl:when test="name() = 'isBasedOnURL' ">source</xsl:when>
					    <xsl:when test="name() = 'learningResourceType' ">type</xsl:when>
					    <xsl:when test="name() = 'useRightsURL' ">rights</xsl:when>
					    <xsl:when test="name() = 'encodings' ">format</xsl:when>
					    <xsl:when test="name() = 'author' ">creator</xsl:when>
					    <xsl:when test="name() = 'about' ">subject</xsl:when>
					    <xsl:when test="name() = 'keywords' ">subject</xsl:when>
					    <xsl:when test="name() = 'inLanguage' ">language </xsl:when>
					    <xsl:when test="name() = 'name' ">title</xsl:when>
						<xsl:otherwise><xsl:value-of select ="name()"/></xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="value">
      				<xsl:choose>
					    <xsl:when test="name()='publisher' or name()='author' ">
							<xsl:value-of select ="properties/name"/>
					    </xsl:when>
						<xsl:otherwise>
							<xsl:value-of select ="node()"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				
				<xsl:variable name="xsi-url-attr">
      				<xsl:choose>
					    <xsl:when test="name() = 'useRightsURL' ">true</xsl:when>
					    <xsl:when test="name() = 'url' ">true</xsl:when>
						<xsl:otherwise>false</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				
			<xsl:call-template name="display-tag">
    			<xsl:with-param name="tag" select="$tag"/>
    			<xsl:with-param name="value" select="$value"/>
    			<xsl:with-param name="prefix" select="'dc'"/>
    			<xsl:with-param name="xsi-url-attr" select="$xsi-url-attr"/>
          </xsl:call-template>
      	</xsl:template>
      	
      	<xsl:template match="HashMap/items/properties/*[name()='intendedEndUserRole' or name()='contentLocation' or name()='educationalAlignment' or name()='educationalUse' or name()='contentSize' or name()='dateCreated' or name()='datePublished']">

      			<xsl:variable name="tag">
      				<xsl:choose>
		      			<xsl:when test="name() = 'intendedEndUserRole' ">audience</xsl:when>
					    <xsl:when test="name() = 'educationalAlignment' ">conformsTo</xsl:when>
					    <xsl:when test="name() = 'contentLocation' ">coverage</xsl:when>
					    <xsl:when test="name() = 'educationalUse' ">instructionalMethod</xsl:when>
					    <xsl:when test="name() = 'contentSize' ">extent</xsl:when>
					    <xsl:when test="name() = 'datePublished' ">issued</xsl:when>
					    <xsl:when test="name() = 'dateCreated' ">created</xsl:when>
						<xsl:otherwise><xsl:value-of select ="name()"/></xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="value">
      				<xsl:choose>
					    <xsl:when test="name() = 'educationalAlignment' ">
					    	<!-- For educational alignment we have order of priority
					    	first targetUrl is the best, then the name then description.
					    	If its common core it will hopefully be converted to an ASn
					    	thats why is so important to have one of these present because
					    	all can be used to find the ASN id -->
					    	<xsl:choose>
					    		<xsl:when test="properties/targetUrl!=''">
					    			<xsl:value-of select ="properties/targetUrl"/>
					    		</xsl:when>
					    		<xsl:when test="properties/targetName!=''">
					    			<xsl:value-of select ="properties/targetName"/>
					    		</xsl:when>
					    		<xsl:when test="properties/description!=''">
					    			<xsl:value-of select ="properties/description"/>
					    		</xsl:when>
					    	</xsl:choose>
					    </xsl:when>
						<xsl:otherwise>
							<xsl:value-of select ="node()"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
			    <xsl:variable name="xsi-url-attr">
      				<xsl:choose>
					    <xsl:when test="name() = 'educationalAlignment' ">true</xsl:when>
						<xsl:otherwise>false</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
			<xsl:call-template name="display-tag">
    			<xsl:with-param name="tag" select="$tag"/>
    			<xsl:with-param name="value" select="$value"/>
    			<xsl:with-param name="prefix" select="'dct'"/>
    			<xsl:with-param name="xsi-url-attr" select="$xsi-url-attr"/>
          </xsl:call-template>
      	</xsl:template>
      	
      	
      	<xsl:template match="HashMap/items/properties/*[name()='typicalAgeRange']">
      		<xsl:variable name="value">
      				<xsl:choose>
      					<xsl:when test="text() = '5-' ">Pre-Kindergarten</xsl:when>
		      			<xsl:when test="text() = '6-11' ">Elementary School</xsl:when>
		      			<xsl:when test="text() = '6-8' ">Early Elementary</xsl:when>
		      			<xsl:when test="text() = '5' ">Kindergarten</xsl:when>
		      			<xsl:when test="text() = '6' ">Grade 1</xsl:when>
		      			<xsl:when test="text() = '7' ">Grade 2</xsl:when>
		      			<xsl:when test="text() = '8-11'">Upper Elementary</xsl:when>
		      			<xsl:when test="text() = '8' ">Grade 3</xsl:when>
		      			<xsl:when test="text() = '9' ">Grade 4</xsl:when>
		      			<xsl:when test="text() = '10' ">Grade 5</xsl:when>
		      			<xsl:when test="text() = '11-13' ">Middle School</xsl:when>
		      			<xsl:when test="text() = '11' ">Grade 6</xsl:when>
		      			<xsl:when test="text() = '12' ">Grade 7</xsl:when>
		      			<xsl:when test="text() = '13' ">Grade 8</xsl:when>
		      			<xsl:when test="text() = '14-18' ">High School</xsl:when>
		      			<xsl:when test="text() = '14-15' ">Grade 9</xsl:when>
		      			<xsl:when test="text() = '15-16' ">Grade 10</xsl:when>
		      			<xsl:when test="text() = '16-17' ">Grade 11</xsl:when>
		      			<xsl:when test="text() = '17-18' ">Grade 12</xsl:when>
		      			<xsl:when test="text() = '18+' ">Higher Education</xsl:when>
		      			<xsl:when test="text() = '19+' ">Undergraduate (Lower Division)</xsl:when>
		      			<xsl:when test="text() = '21+' ">Graduate/Professional</xsl:when>
		      			<xsl:when test="text() = '16+' ">Informal Education</xsl:when>
		      			<xsl:when test="text() = '1-15' ">Youth Public</xsl:when>
		      			<xsl:otherwise><xsl:value-of select ="node()"/></xsl:otherwise>
		      		</xsl:choose>
		      	</xsl:variable>
		      	
		      <xsl:call-template name="display-tag">
    			<xsl:with-param name="tag" select="'educationLevel'"/>
    			<xsl:with-param name="value" select="$value"/>
    			<xsl:with-param name="prefix" select="'dct'"/>
    			<xsl:with-param name="xsi-url-attr" select="'false'"/>
             </xsl:call-template>
      	</xsl:template>
      	
      	<xsl:template match="HashMap/items/properties/*[name()='interactivityType' or name()='typicalLearningTime']">
			<xsl:variable name="tag">
    			<xsl:choose>
	      			<xsl:when test="name() = 'timeRequired' ">typicalLearningTime</xsl:when>
					<xsl:otherwise><xsl:value-of select ="name()"/></xsl:otherwise>
				</xsl:choose>
		   </xsl:variable>
			
		  <xsl:variable name="value">
		  	<xsl:value-of select ="name()"/>
		  </xsl:variable>
		  
		  <xsl:call-template name="display-tag">
    			<xsl:with-param name="tag" select="$tag"/>
    			<xsl:with-param name="value" select="$value"/>
    			<xsl:with-param name="prefix" select="'ieee'"/>
    			<xsl:with-param name="xsi-url-attr" select="'false'"/>
          </xsl:call-template>
      	</xsl:template>
      	
      	<xsl:template name="display-tag">
		    <xsl:param name="tag"/>
   			<xsl:param name="value"/>
   			<xsl:param name="prefix"/>
   			<xsl:param name="xsi-url-attr"/>
   			
   			<xsl:element name="{$prefix}:{$tag}">
   				<xsl:if test="$xsi-url-attr='true'">
                	<xsl:attribute name="xsi:type">dct:URI</xsl:attribute>
				</xsl:if>
                <xsl:value-of select="$value"/>
            </xsl:element>
		</xsl:template>
      	
      	<xsl:template match="text()" mode="#all" />


</xsl:stylesheet>