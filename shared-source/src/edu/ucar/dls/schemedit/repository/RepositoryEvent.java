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
package edu.ucar.dls.schemedit.repository;

import java.util.*;

/**
 *  Event indicating that a repository event occurred, exposes an event name (e.g.,
 *  'recordMoved') as well eventData in the form of a map.
 *
 *@author    ostwald
 */
public class RepositoryEvent extends EventObject {

	private String eventName;


	/**
	 *  Contruct a RepositoryEvent
	 *
	 *@param  eventName  Description of the Parameter
	 *@param  eventData  Description of the Parameter
	 */
	public RepositoryEvent(String eventName, Map eventData) {
		super(eventData);
		this.eventName = eventName;
	}


	/**
	 *  Gets the name attribute of the RepositoryEvent object
	 *
	 *@return    The name value
	 */
	public String getName() {
		return this.eventName;
	}


	/**
	 *  Gets the eventData attribute of the RepositoryEvent object
	 *
	 *@return    The eventData value
	 */
	public Map getEventData() {
		return (Map) this.getSource();
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public String toString() {
		String NL = "\n\t";
		String s = eventName;
		Map eventData = this.getEventData();
		for (Iterator i = eventData.keySet().iterator(); i.hasNext(); ) {
			String key = (String) i.next();
			s += NL + "  " + key + ": " + (String) eventData.get(key);
		}
		return s;
	}

}

