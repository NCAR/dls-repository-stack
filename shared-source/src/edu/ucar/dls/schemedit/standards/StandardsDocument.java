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
 *  Interface for classes representing an educational standard (e.g., an ASN
 *  Standard Document) as a heirarchy of standards nodes in support of display
 *  and selection in the metadata editor.
 *
 * @author    ostwald
 */
public interface StandardsDocument {

	public String getAuthor();
	
	public String getAuthorName();
	
	public String getTopic();
	
	/**
	 *  Get a StandardNode by id
	 *
	 * @param  id  the standard id
	 * @return     The standard for this id or null if a standard is not found
	 */
	public StandardsNode getStandard(String id);

	/**
	 *  Gets the rootNode attribute of the StandardsDocument object
	 *
	 * @return    The rootNode value
	 */
	public StandardsNode getRootNode();


	/**
	 *  Returns a flat list containing all DleseStandardsNodes in the
	 *  standardsTree.
	 *
	 * @return    The nodeList value
	 */
	public List getNodeList();


	/**
	 *  The number of nodes in this tree.
	 *
	 * @return    the size of the tree
	 */
	public int size();


	/**  NOT YET DOCUMENTED */
	public void destroy();

}

