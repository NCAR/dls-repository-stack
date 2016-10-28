<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<!--

	This stylesheet should be configured in Tomcat to display the directory listing
	displayed in the harvested records context. These get inserted via AJAX into
	the 'Harvested Records' tab for a given harvest.
	
	To configure, see the full instructions in the docs/INSTALL.txt file
-->


  <xsl:output method="html" encoding="iso-8859-1" indent="no"/>

  <xsl:template match="listing">
      <!-- <h1>Files Directory Listing For
            <xsl:value-of select="@directory"/>
      </h1> -->
      <hr size="1" />
      <table cellspacing="1"
                  width="100%"
                  align="center"
				  id="dirListTable">
        <tr class="headrow">
          <td align="left">Filename</td>
          <td align="center">Size</td>
          <td align="right">Last Modified</td>
        </tr>
        <xsl:apply-templates select="entries"/>
        </table>
      <xsl:apply-templates select="readme"/>
      <hr size="1" />
  </xsl:template>


  <xsl:template match="entries">
    <xsl:apply-templates select="entry"/>
  </xsl:template>

  <xsl:template match="readme">
    <hr size="1" />
    <pre><xsl:apply-templates/></pre>
  </xsl:template>

  <xsl:template match="entry">
    <tr class="bodyrow">
      <td align="left" class="dirListPathTd">
        <xsl:variable name="urlPath" select="@urlPath"/>
        <a href="javascript:void(0)" onclick="javascript:doRecordsDisplay('{$urlPath}')">
          <xsl:apply-templates/>
        </a>
      </td>
      <td align="center" class="dirListSizeTd">
        <xsl:value-of select="@size"/>
      </td>
      <td align="right" class="dirListDateTd">
        <xsl:value-of select="@date"/>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>


