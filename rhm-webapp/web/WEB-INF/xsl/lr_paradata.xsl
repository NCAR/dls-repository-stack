<xsl:stylesheet version="2.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xmlns:crsd="http://ns.nsdl.org/MRingest/crsd_v1.05/"
    xmlns:OAI2.0="http://www.openarchives.org/OAI/2.0/"
	xmlns:comm-para="http://ns.nsdl.org/ncs/comm_para"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    exclude-result-prefixes="OAI2.0 comm-para"
    >

        <xsl:template match="/">
        	<commParadata xmlns="http://ns.nsdl.org/ncs/comm_para" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://ns.nsdl.org/ncs/comm_para http://ns.nsdl.org/ncs/comm_para/1.00/schemas/comm_para.xsd">
				<xsl:apply-templates select="HashMap/activity/content"/>
				<xsl:apply-templates select="HashMap/collection/items/activity[1]/content"/>
        		<usageDataSummary>
        			<xsl:apply-templates/>
        		</usageDataSummary>
			</commParadata>
        </xsl:template>
        
      	<xsl:template match="content">
      		<xsl:element name="paradataDescription" namespace="http://ns.nsdl.org/ncs/comm_para">
      			<xsl:value-of select="node()"/>
      		</xsl:element>
      	</xsl:template>
      	
      	<xsl:template match="activity">
      		
      		<xsl:variable name="action"><xsl:value-of select ="verb/action"/></xsl:variable>
      		<xsl:variable name="measure_type"><xsl:value-of select ="verb/measure/measureType"/></xsl:variable>
      		
      		<xsl:variable name="tag">
     			<xsl:choose>
	      			<xsl:when test="$action='rated'">rating</xsl:when>
	      			<xsl:otherwise>integer</xsl:otherwise>
      			</xsl:choose>
      		</xsl:variable>
      		
      		<xsl:variable name="type">
     			<xsl:choose>
     				<xsl:when test="$tag='rating' and $measure_type=''">star</xsl:when>
	      			<xsl:when test="$tag='rating'"><xsl:value-of select ="replace($measure_type,' average','')"/></xsl:when>
	      			<xsl:otherwise><xsl:value-of select ="$action"/></xsl:otherwise>
      			</xsl:choose>
      		</xsl:variable>
      		
      		<xsl:variable name="date"><xsl:value-of select ="verb/date"/></xsl:variable>
 			<xsl:variable name="start_date">
     			<xsl:choose>
	      			<xsl:when test="$date=''"></xsl:when>
	      			<xsl:when test="contains($date, '/')"><xsl:value-of select="substring-before($date, '/')"/></xsl:when>
	      			<xsl:otherwise><xsl:value-of select="$date"/></xsl:otherwise>
      			</xsl:choose>
      		</xsl:variable>
 			<xsl:variable name="end_date">
     			<xsl:choose>
     				<xsl:when test="$date=''"></xsl:when>
	      			<xsl:when test="contains($date, '/')"><xsl:value-of select="substring-after($date, '/')"/></xsl:when>
	      			<xsl:otherwise></xsl:otherwise>
      			</xsl:choose>
      		</xsl:variable>
      		
      		<xsl:variable name="start_date_time">
     			<xsl:choose>
	      			<xsl:when test="$start_date=''"></xsl:when>
	      			<xsl:when test="contains($start_date, 'T')"><xsl:value-of select="$start_date"/></xsl:when>
	      			<xsl:otherwise><xsl:value-of select="$start_date"/>T00:00:00</xsl:otherwise>
      			</xsl:choose>
      		</xsl:variable>
 			<xsl:variable name="end_date_time">
     			<xsl:choose>
	      			<xsl:when test="$end_date=''"></xsl:when>
	      			<xsl:when test="contains($end_date, 'T')"><xsl:value-of select="$end_date"/></xsl:when>
	      			<xsl:otherwise><xsl:value-of select="$start_date"/>T00:00:00</xsl:otherwise>
      			</xsl:choose>
      		</xsl:variable>
      		
      		
 			<xsl:variable name="audience">
     			<xsl:choose>
     				<xsl:when test="actor/objectType"><xsl:value-of select="actor/objectType"/></xsl:when>
	      			<xsl:when test="actor"><xsl:value-of select="/actor"/></xsl:when>
	      			<xsl:otherwise></xsl:otherwise>
      			</xsl:choose>
      		</xsl:variable>
 			<xsl:variable name="edLevel">
     			<xsl:choose>
     				<xsl:when test="actor/description"><xsl:value-of select="actor/description[1]"/></xsl:when>
	      			<xsl:otherwise></xsl:otherwise>
      			</xsl:choose>
      		</xsl:variable>
 
      		<xsl:element name="{$tag}" namespace="http://ns.nsdl.org/ncs/comm_para">
      			<xsl:attribute name="type"><xsl:value-of select ="$type"/></xsl:attribute>
      			<xsl:if test="$tag='rating'">
      				<xsl:attribute name="min"><xsl:value-of select ="verb/measure/scaleMin/text()"/></xsl:attribute>
      				<xsl:attribute name="max"><xsl:value-of select ="verb/measure/scaleMax/text()"/></xsl:attribute>
      				<xsl:attribute name="total"><xsl:value-of select ="verb/measure/sampleSize/text()"/></xsl:attribute>
      			</xsl:if>
      			
      			<xsl:if test="$start_date_time!=''">
      				<xsl:attribute name="dateTimeStart"><xsl:value-of select ="$start_date_time"/></xsl:attribute>
      			</xsl:if>
   				<xsl:if test="$end_date_time!=''">
      				<xsl:attribute name="dateTimeEnd"><xsl:value-of select ="$end_date_time"/></xsl:attribute>
      			</xsl:if>
      			<xsl:if test="$audience!=''">
      				<xsl:attribute name="audience"><xsl:value-of select ="$audience"/></xsl:attribute>
      			</xsl:if>
      			<xsl:if test="$edLevel!=''">
      				<xsl:attribute name="edLevel"><xsl:value-of select ="$edLevel"/></xsl:attribute>
      			</xsl:if>
                <xsl:value-of select="verb/measure/value"/>
            </xsl:element>
      	</xsl:template>
      	
		<xsl:template match="text()" mode="#all" />
</xsl:stylesheet>