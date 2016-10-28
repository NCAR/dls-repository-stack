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
package edu.ucar.dls.schemedit.repository.collection;

import java.util.*;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.Element;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.repository.RepositoryService;
import edu.ucar.dls.schemedit.dcs.DcsDataRecord;
import edu.ucar.dls.schemedit.ndr.util.NCSCollectReader;
import edu.ucar.dls.index.reader.XMLDocReader;
import edu.ucar.dls.xml.Dom4jUtils;

public class OsmCollectCollectionRecord extends AbstractCollectionRecord {
	
	private static boolean debug = false;
	private Document localizedDoc = null;
	
	public OsmCollectCollectionRecord (Document doc, String handleServiceBaseUrl) {
		super (doc, handleServiceBaseUrl);
		this.localizedDoc = Dom4jUtils.localizeXml(doc);
		prtln ("localizedDoc: ");
		prtln (Dom4jUtils.prettyPrint(this.localizedDoc));
	}

	public String getSetSpec() {
		try {
			Element el = (Element)this.localizedDoc.selectSingleNode("/collectionRecord/key");
			if (el == null)
				throw new Exception ("node not found at /collectionRecord/key");
			return el.getTextTrim();
		} catch (Throwable t) {
			prtln ("ERROR: could not get setSpec for '" + this.getId() + "': " + t.getMessage());
			prtln (Dom4jUtils.prettyPrint(this.localizedDoc));
		}
		return null;
	}
	
	public String getId() {
		return this.localizedDoc.valueOf("/collectionRecord/recordID");
	}
	
	private static void prtln (String s) {
		System.out.println (s);
	}
}
