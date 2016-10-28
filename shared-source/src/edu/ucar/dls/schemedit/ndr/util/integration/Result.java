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

package edu.ucar.dls.schemedit.ndr.util.integration;

import edu.ucar.dls.xml.Dom4jUtils;

import org.dom4j.*;
import java.util.*;

class Result {
	public String resourceHandle;
	public String resourceUrl;
	
	String nonNullValue (String s) {
		return (s == null ? "" : s);
	}
	
	Result (String resourceUrl, String resourceHandle) {
		this.resourceHandle = nonNullValue(resourceHandle);
		this.resourceUrl = nonNullValue(resourceUrl);
	}
	
	Result (Element e) {
		this.resourceHandle = CollectionIntegrator.getElementText(e, "resourcehandle");
		this.resourceUrl = CollectionIntegrator.getElementText(e, "resourceurl");
	}
	
	public String toString() {
		return "ncsUrl: " + resourceUrl +   "  handle: " + resourceHandle;
	}
	
	public Element asElement (String tag) {
		Element e = DocumentHelper.createElement (tag);
		Element url = e.addElement ("resourceurl");
		url.setText(resourceUrl);
		Element handle = e.addElement ("resourcehandle");
		handle.setText(resourceHandle);
		return e;
	}
}

