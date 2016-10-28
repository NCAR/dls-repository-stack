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

package edu.ucar.dls.index;

import java.io.*;
import java.net.*;
import java.util.*;

class LuceneIndexChanger implements Serializable {

	String reader;

	private ArrayList listeners;
	
	/**
	 * Construct the DataEvent object and invoke the appropriate method
	 * of all the listeners to this DataManager.
	 *
	 * @param dataToAdd	The list of OIDs for data added to this manager
	 */
	void notifyListeners(String newReader) {
		// make LuceneIndexChangedEvent with a new reader file using long data
		LuceneIndexChangedEvent event = new LuceneIndexChangedEvent(this);
		
		for (int i=0; i<listeners.size(); i++) {
			try {
				((LuceneIndexChangeListener)listeners.get(i)).indexChanged(event);
			}
			catch (Throwable t) {
				System.err.println("Unexpected exception occurred while notifying listeners...");
			}
		}
	}

	
	/**
	 * Add a <code>DataListener</code> to this <code>DataManager</code>.
	 *
	 * @param listener	The listener to add
	 */
	void addListener(LuceneIndexChangeListener listener) {
		if (listener != null) {
			if (listeners == null) {
				listeners = new ArrayList();
			}
			else if (listeners.contains(listener)) {
				return;
			}

			listeners.add(listener);
		}
	}

	
	/*********************************************************************************/
	
	void removeListener(LuceneIndexChangeListener listener) {
		if (listener != null) {
			int index = listeners.indexOf(listener);
			if (index > -1) {
				try {
					listeners.remove(index);
				}
				catch (IndexOutOfBoundsException ioobe) {
					return;
				}
			}
		}
	}



}
