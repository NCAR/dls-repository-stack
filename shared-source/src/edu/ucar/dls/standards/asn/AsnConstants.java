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
package edu.ucar.dls.standards.asn;

import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;
import org.dom4j.*;
import java.net.URL;
import java.util.*;
import java.io.*;
import java.util.regex.*;

/**
 *  Class to read the ASN topics document from ASN web service and provide topic
 *  lookup by topic purl.
 *
 * @author    Jonathan Ostwald
 */
public class AsnConstants {
	private static boolean debug = true;
	
	public final static String ASN_PURL_BASE = "http://purl.org/ASN/resources/";
	public final static String ASN_JESANDCO_BASE = "http://asn.jesandco.org/resources/";
	public final static String ASN_DESIRE2LEARN_BASE = "http://asn.desire2learn.com/resources/";
	public final static String ASN_ID_BASE = ASN_JESANDCO_BASE;

	public final static String ASN_JURISDICTION_BASE = "http://purl.org/ASN/scheme/ASNJurisdiction/";
	public final static String ASN_TOPIC_BASE = "http://purl.org/ASN/scheme/ASNTopic/";

	/*
		Normalize ASN IDs for WMO
		use a pattern to split into domain and UID
		if UID is WMO, substitute desire2learn base
	*/
	final static List<String> WMO_IDS = 
		new ArrayList<String>(Arrays.asList("D2605823", "D2605824"));
		
	final static String DOMAINS_STR = 
		ASN_PURL_BASE + "|" + ASN_JESANDCO_BASE + "|" + ASN_DESIRE2LEARN_BASE;
		
	final static String ASN_UID_STR = "(D|S)[A-Z0-9]*";
	
	final static Pattern ASN_ID_PAT = Pattern.compile("(" + DOMAINS_STR + ")(" + ASN_UID_STR + ")");
	final static Pattern ASN_UID_STR_PAT = Pattern.compile(ASN_UID_STR);
	
	/**
	KLUDGE - necessary because WMO docs are only served by desire2learn base,
	and desire2learn serves nothing BUT WMO docs!
	
	For WMO_IDS ensure the asnId has desire2learn base, and all
	others use ASN_ID_BASE
	**/
	public static String normalizeForWMO (String asnId) {
		return normalizeForWMO (asnId, false);
	}
		
	/* This normalizes for DOCUMENTS */
	public static String normalizeForWMO (String asnId, boolean force) {
		Matcher m = ASN_ID_PAT.matcher(asnId);
		if (m.matches()) {
			String base = m.group(1);
			String uid = m.group(2);
			if (WMO_IDS.contains (uid) || force) {
				prtln (".. converting ASN to Desire2Learn base");
				return ASN_DESIRE2LEARN_BASE + uid;
			}
			else {
				return ASN_ID_BASE + uid;
			}
		}
		return asnId;
	}		
	
	public static String getAsnUid (String asnId) {
		String normalized = normalizeAsnId(asnId);
		Matcher m = ASN_ID_PAT.matcher(asnId);
		if (m.matches()) {
			return m.group(2);
		} else {
			return null;
		}
	}
	
	public static String normalizeAsnId (String asnId) {
		// prtln ("normalizing: " + asnId);
		Matcher m = ASN_ID_PAT.matcher(asnId);
		if (m.matches()) {
			String base = m.group(1);
			String uid = m.group(2);
			return ASN_ID_BASE + uid;
		}
		else {
			m = ASN_UID_STR_PAT.matcher(asnId);
			if (m.matches()) {
				// prtln ("UID MATCH: " + m.group());
				return ASN_ID_BASE + m.group();
			}
			else {
				// THIS should/could throw an exception since the asnId was not recognized ...
				prtln (" - WARN: NO UID match for " + asnId);
			}
		}
		return asnId;
	}

	public static boolean isNormalizedAsnId(String asnId) {
		return ASN_ID_PAT.matcher(asnId).matches();
	}
	
	public static void main (String [] args) {
		String uid = "D2605823";
		Matcher m = ASN_UID_STR_PAT.matcher(uid);
		if (m.matches()) {
			prtln ("UID MATCH: " + m.group());
			prtln ("NORmalized: " + ASN_ID_BASE + m.group());
		}
		else {
			// THIS should/could throw an exception since the asnId was not recognized ...
			prtln ("NO UID match");
		}
		
		uid = "http://asn.jesandco.org/resources/S10497A4";
		String normalized = normalizeAsnId(uid);
		prtln ("NORMALIZED: " + normalized);
		prtln ("- isNormalizedAsnId: " + isNormalizedAsnId(normalized));
	}
	
	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("AsnConstants: " + s);
		}
	}
}

