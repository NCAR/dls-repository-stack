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

package edu.ucar.dls.services.mmd;

import edu.ucar.dls.util.DpcErrors;



/**
 * Describes a single warning message for a metadata record.
 */

public class MmdWarning {

/** Message type: see edu.ucar.dls.util.DpcErrors */
private int msgType;

/** The metadata XML file name */
private String filename;

/** The xpath within the metadata XML file */
private String xpath;

/** The url type: "primary-url", etc. */
private String urllabel;

/** The url itself */
private String url;

/** Additional error description */
private String msg;

/** Additional error info */
private String auxinfo;


/** Creates an MmdWarning with the specified parms */

MmdWarning(
	int msgType,			// See edu.ucar.dls.util.DpcErrors
	String filename,		// metadata XML file
	String xpath,			// xpath within the metadata XML file
	String urllabel,		// url type: "primary-url", etc.
	String url,				// url itself
	String msg,				// additional error description
	String auxinfo)			// additional info
{
	this.msgType = msgType;
	this.filename = filename;
	this.xpath = xpath;
	this.urllabel = urllabel;
	this.url = url;
	this.msg = msg;
	this.auxinfo = auxinfo;
}


public String toString() {
	String res = "MmdWarning:\n"
		+ "    msgType: " + msgType + "\""
		+ DpcErrors.getMessage( msgType) + "\"\n"
		+ "    filename: \"" + filename + "\n"
		+ "    xpath: \"" + xpath + "\n"
		+ "    urllabel: \"" + urllabel + "\n"
		+ "    url: \"" + url + "\n"
		+ "    msg: \"" + msg + "\n"
		+ "    auxinfo: \"" + auxinfo + "\n";
	return res;
}


/** Returns the message type: see edu.ucar.dls.util.DpcErrors */
public int getMsgType() { return msgType; }

/** Returns the metadata XML file name */
public String getFilename() { return filename; }

/** Returns the xpath within the metadata XML file */
public String getXpath() { return xpath; }

/** Returns the url type: "primary-url", etc. */
public String getUrllabel() { return urllabel; }

/** Returns the url itself */
public String getUrl() { return url; }

/** Returns the additional error description */
public String getMsg() { return msg; }

/** Returns the additional error info */
public String getAuxinfo() { return auxinfo; }


} // end class MmdWarning

