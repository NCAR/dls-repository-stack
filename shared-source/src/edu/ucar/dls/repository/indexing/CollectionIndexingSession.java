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

package edu.ucar.dls.repository.indexing;

import edu.ucar.dls.repository.*;

import java.io.*;
import java.util.*;

import edu.ucar.dls.index.*;
import edu.ucar.dls.index.writer.*;
import java.text.*;


	
public class CollectionIndexingSession {
	private String sessionId = null;
	protected CollectionIndexingSession(String collectionKey){
		sessionId = System.currentTimeMillis() + "-" + collectionKey;
	}
	
	protected CollectionIndexingSession() {}

	protected void setSessionId(String sessionId){
		this.sessionId = sessionId;
	}
	
	public String getSessionId(){
		return sessionId;	
	}
	
	public String getCollection(){
		return sessionId.substring(sessionId.indexOf('-')+1,sessionId.length());
	}

	public String toString() {
		return sessionId;	
	}
	
	/**
	 *  Checks equality of two CollectionIndexingSession objects.
	 *
	 * @param  o  The CollectionIndexingSession to compare to this
	 * @return    True iff the compared object is equal
	 */
	public boolean equals(Object o) {
		if (o == null || !(o instanceof CollectionIndexingSession))
			return false;
		try {
			return this.toString().equals(o.toString());
		} catch (Throwable e) {
			// Catch null pointer...
			return false;
		}
	}		
}

