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
package edu.ucar.dls.schemedit.standards;

import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.schemedit.*;

import java.io.*;
import java.util.*;

import java.net.*;

/**
 *  Manages information about a standards document in the context of a {@link
 *  SuggestionServiceHelper}. The standards within a document are represented as
 *  a {@link StandardsDocument}.
 *
 * @author    ostwald
 */
public interface StandardsManager {

	/**
	 *  Gets the xmlFormat attribute of the StandardsManager object
	 *
	 * @return    The xmlFormat value
	 */
	public String getXmlFormat();


	/**
	 *  Gets the xpath attribute of the StandardsManager object
	 *
	 * @return    The xpath value
	 */
	public String getXpath();


	/**
	 *  The name of the JSP tag that will render the standards hierarchy
	 *
	 * @return    The rendererTag value
	 */
	public String getRendererTag();

}

