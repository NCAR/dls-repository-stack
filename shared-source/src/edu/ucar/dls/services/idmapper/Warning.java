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

package edu.ucar.dls.services.idmapper;

import edu.ucar.dls.util.DpcErrors;


/**
 * Represents a single warning message.
 */

class Warning {



int msgType;
String id;
String filename;
String xpath;
String urllabel;
String url;
String msg;
String auxinfo;

Warning(
	int msgType,
	String id,
	String filename,
	String xpath,
	String urllabel,
	String url,
	String msg,
	String auxinfo)
{
	this.msgType = msgType;
	this.id = id;
	this.filename = filename;
	this.xpath = xpath;
	this.urllabel = urllabel;
	this.url = url;
	this.msg = msg;
	this.auxinfo = auxinfo;
}


public String toString() {
	String res = "msgType: " + msgType;
	res += "  " + DpcErrors.getMessage( msgType);
	res += "  id: " + id
		+ "  filename: " + filename
		+ "  xpath: " + xpath
		+ "  urllabel: " + urllabel
		+ "  url: " + url
		+ "  msg: " + msg
		+ "  auxinfo: " + auxinfo;
	return res;
}



boolean isSevere() {
	boolean bres = false;
	if (msgType < DpcErrors.IDMAP_SEVERE_LIMIT) bres = true;
	return bres;
}

} // end class

