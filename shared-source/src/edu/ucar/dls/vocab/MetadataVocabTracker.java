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

package edu.ucar.dls.vocab;

import java.util.*;

/**
 *  Interface for creation of controlled vocabulary system Ids, tracking changes
 *  in metadata names and UI labels, and ensuring consistency of all three.
 *  Implementations should follow these rules:<p>
 *
 *
 *  <ol>
 *    <li> Error checking, encoded Id assignment, and UI label change tracking
 *    are OPTIONAL, as reflected in the isUsingDatabase() method</li>
 *    <li> Once an encoded Id has been assigned, it may never be re-used, but
 *    can only be retired when/if its vocab value is removed</li>
 *    <li> Only vocab VALUE encoded Ids are automatically assigned. FIELD Ids
 *    must be assigned manually, so a tracker will assume that they already
 *    exist.</li>
 *  </ol>
 *
 *
 *@author    Ryan Deardorff
 */
public interface MetadataVocabTracker {

	/**
	 *  Is the system using this tracker connected to a database? If not, no error
	 *  checking will be performed, no new encoded system Ids can be assigned, and
	 *  changes in UI labels will not be registered.
	 */
	public boolean isUsingDatabase();

	/**
	 *  Assign a unique system Id for a new vocabulary value. See rule #2 above.
	 *
	 *@param  metadataFieldId  encoded field Id
	 *@param  metadataValue    metadata value name
	 *@return                  the new Id
	 */
	public String assignNewSystemId( String metadataFieldId, String metadataValue );

	/**
	 *  Does the current fieldId/value pair already exist in the database? If so,
	 *  return the Id, if not, return "" (empty string).
	 *
	 *@param  metadataFieldId  encoded field Id
	 *@param  metadataValue    metadata value name
	 */
	public String getId( String metadataFieldId, String metadataValue );

	/**
	 *  Register the current state of UI labels in the database
	 *
	 *@param  uiSystems           map whose keys indicate all the current ui_label
	 *      "system" attributes
	 *@param  uiLabelOfSystemIds  map encoded Ids to UI labels
	 */
	public void registerUiLabels( HashMap uiSystems, HashMap uiLabelOfSystemIds );

	/**
	 *  If a relational database is being used, this should be used to close its
	 *  connection once the vocabulary has been loaded
	 */
	public void closeConnection();
}

