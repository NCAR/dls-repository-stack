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

class MisMatch {
	
	public String id;
	public Result ncsResult;
	public Result nsdlResult;
	
	public MisMatch (String id, Result ncsResult, Result nsdlResult) {
		this.id = id;
		this.ncsResult = ncsResult;
		this.nsdlResult = nsdlResult;
	}
	
	public MisMatch (Element e) {
		this.id = e.attributeValue("ncsrecordid");
		this.ncsResult = new Result (e.element("ncs"));
		this.nsdlResult =  new Result (e.element("nsdl"));
	}
	
	public String toString () {
		String s = "MisMatch: " + id;
		if (ncsResult != null) {
			s += "\n\t ncs  url: " + this.ncsResult.resourceUrl +
				 "  handle: " + this.ncsResult.resourceHandle;
		}
		if (nsdlResult != null) {
			s += "\n\t nsdl  url: " + this.nsdlResult.resourceUrl +
				 "  handle: " + this.nsdlResult.resourceHandle;
		}
		return s;
	}
	
	public Element asElement () {
		Element e = DocumentHelper.createElement ("mismatch");
		e.addAttribute ("ncsrecordid", id);
		e.add (this.ncsResult.asElement("ncs"));
		e.add (this.nsdlResult.asElement("nsdl"));
		return e;
	}
}

