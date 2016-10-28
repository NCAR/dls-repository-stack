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

package edu.ucar.dls.util;

import java.io.*;

/**
 *  A FileFilter for zip files. Filters for files that end in '.zip' or '.ZIP' and are files, not directories.
 *  Instances of this class may be passed to the {@link java.io.File#listFiles(FileFilter)} method of the
 *  {@link java.io.File} class.
 *
 * @author     John Weatherley
 * @version    $Id: ZipFileFilter.java,v 1.2 2009/03/20 23:34:00 jweather Exp $
 */
public class ZipFileFilter implements FileFilter {
	/**
	 *  A FileFilter for zip files. Filters for files that end in '.zip' or '.ZIP'.
	 *
	 * @param  file  The file in question
	 * @return       True if isFile() is true and the file ends in '.zip' or '.ZIP'
	 */
	public boolean accept(File file) {
		return (file.isFile() && (file.getName().endsWith(".zip") || file.getName().endsWith(".ZIP")));
	}
}


