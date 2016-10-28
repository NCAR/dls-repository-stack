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

package edu.ucar.dls.vocab;

import edu.ucar.dls.vocab.*;

/**
 *  NOT YET DOCUMENTED
 *
 * @author     John Weatherley
 * @version    $Id: VocabUtils.java,v 1.3 2009/03/20 23:34:00 jweather Exp $
 */
public class VocabUtils {

	private static boolean debug = true;


	/**
	 *  Gets the vocab encoded keys for the given values, separated by the '+' symbol.
	 *
	 * @param  metaFormat       The metadata format, for example 'adn'
	 * @param  values           The valuse to encode.
	 * @param  useVocabMapping  The mapping to use, for example 'contentStandards'
	 * @param  vocab            The MetadataVocab instance
	 * @return                  The encoded vocab keys.
	 * @exception  Exception    If error.
	 */
	public static String getFieldContent(String metaFormat, String[] values, String useVocabMapping, MetadataVocab vocab)
		 throws Exception {
		if (values == null || values.length == 0) {
			return "";
		}

		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			String str = values[i].trim();
			if (str.length() > 0) {
				// Use vocabMgr mapping if available, otherwise output unchanged
				if (useVocabMapping != null && vocab != null) {
					ret.append(vocab.getTranslatedValue(metaFormat, useVocabMapping, str));
				}
				else {
					ret.append(str);
				}

				// Separate each term with +
				if (i < (values.length - 1)) {
					ret.append("+");
				}
			}
		}
		//prtln("Field content: " + ret.toString());
		return ret.toString();
	}


	/**
	 *  Gets the encoded vocab key for the given content.
	 *
	 * @param  metaFormat       The metadata format, for example 'adn'
	 * @param  value            The value to encode.
	 * @param  useVocabMapping  The vocab mapping to use, for example "contentStandard".
	 * @param  vocab            The MetadataVocab instance
	 * @return                  The encoded value.
	 * @exception  Exception    If error.
	 */
	public static String getFieldContent(String metaFormat, String value, String useVocabMapping, MetadataVocab vocab)
		 throws Exception {
		if (value == null || value.trim().length() == 0) {
			return "";
		}

		// Use vocabMgr mapping if available, otherwise output unchanged
		if (useVocabMapping != null && vocab != null) {
			return vocab.getTranslatedValue(metaFormat, useVocabMapping, value);
		}
		else {
			return value;
		}
	}


	private static void prtln(String s) {
		System.out.println(s);
	}
}

