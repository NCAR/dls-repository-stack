<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    version="1.0">

<!--ORGANIZATION OF THIS FILE-->
<!-- **************************************-->
<!--This file is organized into the following sections:
A. Purpose
B. License information and credits
C. Transformation code
D. Templates to apply (in alphabetical order)-->

<!--A. PURPOSE-->
<!-- **************************************-->
<!--To transform http://www.dlese.org/Metadata/documents/xml/frameworks.xml to a text file.-->
<!--The frameworks.xml directory contains a list of the metadata frameworks that DLESE uses or continues to support. It also provide the URL to the schema of each metadata framework. The list includes more than the current operational metadata framework versions in order to provide support to past versions.-->


<!--B. LICENSE INFORMATION and CREDITS-->
<!-- *****************************************************-->
<!--Date created: 2006-07-21 by Katy Ginger, DLESE Program Center, University Corporation for Atmospheric Research (UCAR)-->
<!--License information:
		Copyright (c) 2002-2006, 2005 DLESE Program Center
		University Corporation for Atmospheric Research (UCAR)
		P.O. Box 3000, Boulder, CO 80307, United States of America
		email: support@dlese.org. 
		All rights reserved
This XML tranformation, written in XSLT 1.0 and XPATH 1.0, are free software; you can redistribute them and/or modify them under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.  These XML instance documents are distributed in the hope that they will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this project; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA -->


	<xsl:output method="text"/>

<!--C. TRANSFORMATION CODE-->
<!-- **************************************-->
	<xsl:variable name="newline">
<xsl:text>
</xsl:text>
	</xsl:variable>

	<xsl:template match="*|/">
		<xsl:apply-templates select="frameworks/framework"/>
	</xsl:template>
	
<!--D. TEMPLATES TO APPLY-->
<!--*********************************-->

<!--1. framework-->
	<xsl:template match="framework">
		<xsl:value-of select="concat(./@dir, '*', ./@uri, '*')"/>
		<xsl:value-of select="$newline"/>
	</xsl:template>



</xsl:stylesheet>
<!--	*** LICENSE INFORMATION *****
		Copyright 2002, 2003, 2004, 2005 DLESE Program Center
		University Corporation for Atmospheric Research (UCAR)
		P.O. Box 3000, Boulder, CO 80307, United States of America
		email: support@dlese.org. 
This XML stylesheet is free software; you can redistribute them and/or modify them under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.  These XML instance documents are distributed in the hope that they will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this project; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA -->
