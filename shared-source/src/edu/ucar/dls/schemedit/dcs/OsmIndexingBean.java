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
package edu.ucar.dls.schemedit.dcs;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.test.TesterUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import org.dom4j.*;
import edu.ucar.dls.util.MetadataUtils;

import java.io.*;
import java.util.*;
import java.text.*;
import java.util.regex.*;

/**
*  Computes values from OSM records to be indexed by {@link DcsDataFileIndexingPlugin.java}
 *
 *@author    ostwald
 */
public class OsmIndexingBean {

	protected static boolean debug = false;
	private Document doc = null;
	private int fiscalYear = -1;

	Pattern letterPat = Pattern.compile("[a-zA-Z]");


	/**
	 *  Constructor for the OsmIndexingBean object
	 *
	 *@param  source         file containing osm metadata
	 *@exception  Exception  if file cannot be parsed as XML
	 */
	public OsmIndexingBean(File source) throws Exception {
		prtln("Initializing OsmIndexingBean");
		try {
			doc = Dom4jUtils.localizeXml(Dom4jUtils.getXmlDocument(source));
		} catch (Exception e) {
			throw new Exception(" ERROR: Dom4jUtils.localizeXml could not initialize: " + e.getMessage());
		}
	}


	/**
	 *  Gets the flattenedTitle attribute of the OsmIndexingBean object
	 *
	 *@return    The flattenedTitle value
	 */
	public String getFlattenedTitle() {
		prtln("\ngetFlattenedTitle()");
		String title = doc.selectSingleNode("/record/general/title").getText();
		prtln("title found: " + title);
		if (title == null || title.trim().length() == 0) {
			return "";
		}

		StringBuffer flattened = new StringBuffer();
		for (int i = 0; i < title.length(); i++) {
			String ch = title.substring(i, i + 1);
			if (Pattern.matches("[A-Za-z]", ch)) {
				flattened.append(ch);
			}
		}
		return flattened.toString().toLowerCase();
	}




	/**
	 *  Gets the fiscalYear attribute of the OsmIndexingBean object
	 *
	 *@return    The fiscalYear value
	 */
	public int getFiscalYear() {
		prtln("\ngetFiscalYear()");
		try {
			String metadataValue = doc.selectSingleNode("/record/coverage/date[@type=\'Fiscal\']").getText().trim();
			prtln("metadataValue found: " + metadataValue);

			if (metadataValue.length() == 4) {
				try {
					prtln("trying to return a 4 digit value as int");
					return Integer.parseInt(metadataValue);
				} catch (Throwable t) {}
			}

			Date parsedDate = SchemEditUtils.parseUnionDateType(metadataValue);
			prtln("parsedDate: " + parsedDate.toString());
			Calendar cal = Calendar.getInstance();
			cal.setTime(parsedDate);

			int year = cal.get(Calendar.YEAR);
			prtln("year: " + year);

			Calendar thresholdCal = Calendar.getInstance();
			thresholdCal.set(year, Calendar.OCTOBER, 1, 0, 0, 0);
			Date threshold = thresholdCal.getTime();
			prtln("threshold: " + threshold.toString());

			return (parsedDate.before(threshold) ? year : year + 1);
		} catch (Throwable t) {
			prtln("getFiscalYear error: " + t.getMessage());
			return -1;
		}
	}


	/**
	 *  The main program for the OsmIndexingBean class
	 *
	 *@param  args           The command line arguments
	 *@exception  Exception  Description of the Exception
	 */
	public static void main(String[] args) throws Exception {
		String path = "/Users/ostwald/devel/backpack-data/records/osm/osm1/OSM-000-000-000-002.xml";
		OsmIndexingBean bean = new OsmIndexingBean(new File(path));
		int fiscalYear = bean.getFiscalYear();
		prtln(" ==> " + fiscalYear);

		String title = bean.getFlattenedTitle();
		prtln("title: " + title);
	}


	/**
	 *  Print a line to standard out.
	 *
	 *@param  s  The String to print.
	 */
	protected static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "");
			// SchemEditUtils.prtln(s, "OsmIndexingBean");
		}
	}
}

