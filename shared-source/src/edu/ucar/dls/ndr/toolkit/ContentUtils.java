/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package edu.ucar.dls.ndr.toolkit;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.test.TesterUtils;
import edu.ucar.dls.schemedit.ndr.util.ServiceDescription;

import edu.ucar.dls.util.Files;
import edu.ucar.dls.ndr.NdrUtils;
import edu.ucar.dls.ndr.DleseAsUseCaseHelper;
import edu.ucar.dls.ndr.request.*;
import edu.ucar.dls.ndr.reader.*;
import edu.ucar.dls.ndr.apiproxy.InfoXML;
import edu.ucar.dls.ndr.apiproxy.NDRConstants;
import edu.ucar.dls.ndr.apiproxy.NDRConstants.NDRObjectType;
import edu.ucar.dls.xml.Dom4jUtils;
import org.dom4j.*;
import org.apache.axis.encoding.Base64;

import java.util.*;
import java.io.*;

import org.apache.struts.upload.FormFile;

import java.lang.reflect.Method;

public class ContentUtils {

	private static boolean debug = true;
	
	public static String encodeToString (byte[] in) throws Exception {
		return encodeToString (in, -1);
	}
	
	public static String encodeToString (byte[] in, int width) throws Exception {
		// return  Base64.encode(in);
		String raw = Base64.encode(in);
		if (width > 0) {
			StringBuffer buf = new StringBuffer();
			for (int i=0;i<raw.length();i++) {
				buf.append(raw.charAt(i));
				if (i > 0 && (i % 72 == 0))
					buf.append ("=\n");
			}
			return buf.toString();
		}
		else {
			return raw;
		}
	}

	public static byte[] decodeString (String in) {
		return Base64.decode (in);
	}

	public static byte[] readBinaryFile (File file) throws Exception {
		byte [] fileBytes = null;
		try {
			// Wrap the FileInputStream with a DataInputStream
			FileInputStream file_input = new FileInputStream (file);
			DataInputStream data_in    = new DataInputStream (file_input );
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream ();
			
			while (true) {
				try {
					byteStream.write(data_in.readByte());
				} catch (EOFException eof) {
					// prtln ("End of File");
					break;
				}
			}
			data_in.close ();
			fileBytes = byteStream.toByteArray();
		} catch  (IOException e) {
			System.out.println ( "IO Exception =: " + e );
		}		
		return fileBytes;
	}
	
	private static void  pp (Node node) {
		prtln (Dom4jUtils.prettyPrint(node));
	}
	
	
	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "");
		}
	}
}
