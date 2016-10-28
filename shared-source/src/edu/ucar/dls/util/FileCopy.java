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
//import java.util.*;
//import java.text.*;


public class FileCopy {

	static char dirSep = System.getProperty("file.separator").charAt(0);

	public static String getIDFromFilename(String filename) {
		String id = null;
		if (filename != null) {
			int period = filename.indexOf('.');
			int underscore = filename.indexOf('_');
			if ( (period > 0) && (underscore > 0) ) {
				int pos = (period > underscore) ? underscore : period;
				id = filename.substring(0, pos);
			}
			else if (period > 0) {
				id = filename.substring(0, period);
			}
			else {
				id = filename;
			}
		}	
		return id;
	}


	public static String getIDFromFilename(File file) {
		if (file != null) {
			return getIDFromFilename(file.getName());
		}
		return null;
	}


	public static boolean copyFile(File infile, File outfile) throws Exception {
		if (!infile.equals(outfile)) {
			try {
				FileInputStream in = new FileInputStream(infile);
				FileOutputStream out = new FileOutputStream(outfile);

				long size = infile.length();
				int bytes = 0;
				while (size > 0) {
					bytes = (size > 65536) ? 65536 : (int)size;
					byte [] b = new byte[bytes];
					in.read(b);
					out.write(b);
					size -= bytes;
					b = null;
				}
				in.close();
				out.close();
				in = null;
				out = null;
				return true;
			}
			catch (Exception e) {
				throw e;
			}
		}
		return false;
	}


	public static boolean moveFile(File infile, File outfile) throws Exception {
		try {
			if (copyFile(infile, outfile)) {
				infile.delete();
				return true;
			}
		}
		catch (Exception e) { 
			throw e;
		}
		return false;
	}
	
}
