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

import java.io.*;

/**
 *  A FileFilter for xml files. Filters for files that end in '.xml' or '.XML'. Instances
 *  of this class may be passed to the {@link java.io.File#listFiles(FileFilter)} method
 *  of the {@link java.io.File} class.
 *
 * @author    John Weatherley
 */
public class XMLFileFilter implements FileFilter {
	/**
	 *  A FileFilter for xml files. Filters for files that end in '.xml' or '.XML'.
	 *
	 * @param  file  The file in question.
	 * @return       True if the file ends in '.xml' or '.XML'.
	 */
	public boolean accept(File file) {
		return (file.getName().endsWith(".xml") || file.getName().endsWith(".XML"));
	}
}


