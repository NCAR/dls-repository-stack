<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:o="http://www.dlese.org/Metadata/opml"
    xmlns:groups="http://www.dlese.org/Metadata/ui/groups"
    exclude-result-prefixes="xsi o" 
    version="1.0">

<!--ORGANIZATION OF THIS FILE-->
<!-- **************************************-->
<!--This file is organized into the following sections:
A. Purpose
B. License information and credits-->

<!--A. PURPOSE-->
<!-- **************************************-->
<!--To transform the Digital Library for Earth System Education (DLESE) schema-based groups OPML files to be non-schema based-->


<!--B. LICENSE INFORMATION and CREDITS-->
<!-- *****************************************************-->
<!--Date created: 2006-05-03 by Katy Ginger, DLESE Program Center, University Corporation for Atmospheric Research (UCAR)-->
<!--License information:
		Copyright (c) 2002-2006, 2005 DLESE Program Center
		University Corporation for Atmospheric Research (UCAR)
		P.O. Box 3000, Boulder, CO 80307, United States of America
		email: support@dlese.org. 
		All rights reserved
This XML tranformation, written in XSLT 1.0 and XPATH 1.0, are free software; you can redistribute them and/or modify them under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.  These XML instance documents are distributed in the hope that they will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this project; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA -->

<!--C. ASSUMPTIONS-->
<!-- **************************************-->
<!--Applies to groups OPML metadata format, version 1.0.00 records-->


	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

<!--D. TRANSFORMATION CODE mode one-->
<!-- **************************************-->
	<xsl:template match="*|/">
		<xsl:apply-templates/>
	</xsl:template>   

	<xsl:template match="o:opml">
		<opml xmlns:groups="http://www.dlese.org/Metadata/ui/groups" version="2.0">
			<xsl:apply-templates/>
		</opml>
	</xsl:template>

<!-- to preserve attributes in the input xml document; so that they appear in the output xml documet; e.g. URI -->     
	<xsl:template match="*|/">
		<xsl:copy>
			<xsl:for-each select="@*">
				<xsl:copy/>
			</xsl:for-each>
			<xsl:apply-templates/>
		</xsl:copy>		
	</xsl:template>

</xsl:stylesheet>
<!--	*** LICENSE INFORMATION *****
		Copyright 2002-2006 DLESE Program Center
		University Corporation for Atmospheric Research (UCAR)
		P.O. Box 3000, Boulder, CO 80307, United States of America
		email: support@dlese.org. 
This XML stylesheet is free software; you can redistribute them and/or modify them under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.  These XML instance documents are distributed in the hope that they will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this project; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA -->
