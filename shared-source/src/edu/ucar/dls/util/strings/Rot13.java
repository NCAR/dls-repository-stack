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

package edu.ucar.dls.util.strings;

/**
 *  Basic Rot13 implementation. Rot13 is a two-way encryption algorithm, so the
 *  same method is used for encrypting and decrypting.
 *
 * @author    Ryan Deardorff
 */
public class Rot13 {
	/**
	 *  Return an encrypted/decrypted version of the given string
	 *
	 * @param  str
	 * @return
	 */
	public static String crypt( String str ) {
		StringBuffer ret = new StringBuffer();
		for ( int i = 0; i < str.length(); i++ ) {
			char c = str.charAt( i );
			if ( c >= 'a' && c <= 'm' ) {
				c += 13;
			}
			else if ( c >= 'n' && c <= 'z' ) {
				c -= 13;
			}
			else if ( c >= 'A' && c <= 'M' ) {
				c += 13;
			}
			else if ( c >= 'A' && c <= 'Z' ) {
				c -= 13;
			}
			ret.append( c );
		}
		return ret.toString();
	}
}

