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

import java.util.Iterator;
import java.util.LinkedList;



/**
 * Contains and formats a list of warning messages.
 */

class WarnBuf {

LinkedList warnlist = null;		// list of Warning


WarnBuf() {}



public String toString() {
	int imsg;
	StringBuffer resbuf = new StringBuffer();
	if (warnlist != null) {
		Iterator iter = warnlist.iterator();
		imsg = 1;
		while( iter.hasNext()) {
			resbuf.append( "" + imsg + ": " + iter.next() + "\n");
			imsg++;
		}
	}
	return resbuf.toString();
}



/**
 * Adds a warning message.
 *
 * @param msg The text message.
 */

void add( Warning wng)
{
	if (warnlist == null) warnlist = new LinkedList();
	warnlist.add( wng);
}




void addAll( WarnBuf wbuf) {
	if (wbuf != null && wbuf.length() > 0) {
		Iterator iter = wbuf.warnlist.iterator();
		while (iter.hasNext()) {
			add( (Warning) iter.next());
		}
	}
}



int length() {
	int ires = 0;
	if (warnlist != null) ires = warnlist.size();
	return ires;
}



Iterator iterator() {
	Iterator iter = null;
	if (warnlist != null) iter = warnlist.iterator();
	return iter;
}



boolean hasSevereError() {
	boolean bres = false;
	if (warnlist != null) {
		Iterator iter = warnlist.iterator();
		while (iter.hasNext()) {
			Warning wele = (Warning) iter.next();
			if (wele.isSevere()) {
				bres = true;
				break;
			}
		}
	}
	return bres;
}



} // end class WarnBuf
