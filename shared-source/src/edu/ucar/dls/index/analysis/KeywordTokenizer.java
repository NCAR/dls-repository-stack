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

package edu.ucar.dls.index.analysis;

import java.io.Reader;
import org.apache.lucene.analysis.*;

/**
 *  Includes all characters as part of the token.
 *
 * @author    John Weatherley
 */
public class KeywordTokenizer extends CharTokenizer {
	/**
	 *  Constructor for the KeywordTokenizer object
	 *
	 * @param  in  The Reader
	 */
	public KeywordTokenizer(Reader in) {
		super(in);
	}


	/**
	 *  Accepts all characters.
	 *
	 * @param  c  The c
	 * @return    true
	 */
	protected boolean isTokenChar(char c) {
		return true;
	}
}

