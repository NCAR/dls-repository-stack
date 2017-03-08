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
package edu.ucar.dls.schemedit.security.util;

import edu.ucar.dls.util.Files;
// import edu.ucar.dls.schemedit.security.auth.SchemEditAuth;
import java.io.*;
import java.util.*;
import java.util.regex.*;


import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import sun.misc.BASE64Encoder;

public class Encrypter {
	
	private static boolean debug = true;
	
	static void encryptTester () throws Exception {
		String password = "let!me!in";
		String encrypted1 = Encrypter.encryptBaseSHA164(password);
		prtln ("password is encripted: " + isBase64(password));
		prtln ("encrypted1 is encripted: " + isBase64(encrypted1));

	}
	
	public static void main(String[] args) throws Exception {
		prtln ("Hello from Encrypter");
		
		File src = new File("/Users/ostwald/tmp/passwd");
		File dst = new File("/Users/ostwald/tmp/passwd-encrypted");
		convert(src, dst);
	}
	
	public static void convert (File src) throws Exception {
		Encrypter.convert (src, src);
	}

	public static void convert (File src, File dst) throws Exception {
		prtln ("convert() is encrypting passwords ...");
		if (src.equals(dst)) {
			File parent = src.getParentFile();
			File bak = new File (parent, dst.getName()+ "_BAK");
			Files.copy (src, bak);
		}
		// prtln (" - src: " + src);
		// prtln (" - dst: " + dst);
		Map<String, String> users = readPasswdFile(src);
		writePasswdFile (users, dst);
	}
	
	public static void writePasswdFile (Map<String, String> users, File dst) throws Exception{
		// open dst file and write from "data structure"
		StringBuffer s = new StringBuffer("# Passwords for edu.ucar.dls.schemedit.security.login.FileLogin");
		for (String username : users.keySet() ) {
			String password = users.get(username);
			String passwdFileEntry = username + ":" + Encrypter.encryptBaseSHA164(password);
			s.append("\n" + passwdFileEntry);
		}
		prtln("writing " + users.size() + " users to " + dst);
		
		Files.writeFile(s + "\n", dst);
		// prtln (s.toString());
	}
		
	public static char[] encryptBaseSHA164(char[] plainCharArray) throws NoSuchAlgorithmException {
		String encrypted = encryptBaseSHA164(new String(plainCharArray));
		return encrypted.toCharArray();
	}
	
	public static String encryptBaseSHA164(String plainText) throws NoSuchAlgorithmException {
		MessageDigest d = null;
		d = java.security.MessageDigest.getInstance("SHA-1");
		d.reset();
		d.update(plainText.getBytes());
		byte[] encrypedBytes = d.digest();
		BASE64Encoder encoder = new sun.misc.BASE64Encoder();
		String base64 = encoder.encode(encrypedBytes);
		return base64;
	}
	
	
	public static boolean isBase64 (String encoded) {
		String regEx = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
		return Pattern.matches (regEx, encoded);
	}
	
	static Map<String, String> readPasswdFile (File pwdFile) throws IOException {
		// read src into "data structure"
		BufferedReader r = new BufferedReader(new FileReader(pwdFile));
		Hashtable users = new Hashtable();
		String l = r.readLine();
		while (l != null) {
			int hash = l.indexOf('#');
			if (hash != -1)
				l = l.substring(0, hash);
			l = l.trim();
			if (l.length() != 0) {
				StringTokenizer t = new StringTokenizer(l, ":");
				String user = t.nextToken();
				String password = t.nextToken();
				users.put(user, password);
			}
			l = r.readLine();
		}
		r.close();
		return users;
	}

	
	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("JloTester: " + s);
			System.out.println(s);
		}
	}
}
