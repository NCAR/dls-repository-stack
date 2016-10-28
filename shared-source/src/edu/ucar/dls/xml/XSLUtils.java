/*
 *  License and Copyright:
 *
 *  The contents of this file are subject to the Educational Community License v1.0 (the "License"); you may
 *  not use this file except in compliance with the License. You should have received a copy of the License
 *  along with this software; if not, you may obtain a copy of the License at
 *  http://www.opensource.org/licenses/ecl1.php.
 *
 *  Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 *  either express or implied. See the License for the specific language governing rights and limitations
 *  under the License.
 *
 *  Copyright 2002-2009 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */

package edu.ucar.dls.xml;

/**
 *  Utilities for working with XSL.
 *
 * @author     John Weatherley
 */
public class XSLUtils {
	
	private static final String removeNamespacesXSL =
		"<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>" +
			"<xsl:output method='xml' indent='no'/>" +
			
			"<xsl:template match='/|comment()|processing-instruction()'>" +
				"<xsl:copy>" +
				  "<xsl:apply-templates/>" +
				"</xsl:copy>" +
			"</xsl:template>" +
			
			"<xsl:template match='*'>" +
				"<xsl:element name='{local-name()}'>" +
				  "<xsl:apply-templates select='@*|node()'/>" +
				"</xsl:element>" +
			"</xsl:template>" +
			
			"<xsl:template match='@*'>" +
				"<xsl:attribute name='{local-name()}'>" +
				  "<xsl:value-of select='.'/>" +
				"</xsl:attribute>" +
			"</xsl:template>" +
		"</xsl:stylesheet>";

		
	// This one works but throws an error "The child axis starting at an attribute node will never select anything" from the saxon xslt 2 processor
	private static final String removeNamespacesXSLOFF =
		"<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0' >\n" +
		
			"<xsl:template match='@*' >\n" +
				"<xsl:attribute name='{local-name()}' >\n" +
					"<xsl:value-of select='.' />\n" +
				"</xsl:attribute>\n" +
				"<xsl:apply-templates/>\n" +
			"</xsl:template>\n" +
			
			"<xsl:template match ='*' >\n" +
				"<xsl:element name='{local-name()}' >\n" +
					"<xsl:apply-templates select='@* | node()' />\n" +
				"</xsl:element>\n" +
			"</xsl:template>\n" +
			
		"</xsl:stylesheet>";		

	/**
	 *  Gets an XSL style sheet that removes all namespaces from an XML document. With namespaces removed, the
	 *  XPath syntax necessary to work with the document is greatly simplified.
	 *
	 * @return    An XSL style sheet that removes all namespaces from an XML document
	 */
	public final static String getRemoveNamespacesXSL() {
		return removeNamespacesXSL;
	}
}

