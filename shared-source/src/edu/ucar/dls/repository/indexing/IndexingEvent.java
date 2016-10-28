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

/**
 *  An event that describes an indexing action that has been requested.
 *
 * @author    John Weatherley
 */
public class IndexingEvent {
	private int type = 0;
	private String collectionKey = null;
	private CollectionIndexer collectionIndexer = null;

	/**  Indicates the index is ready to recieve indexing actions. */
	public final static int INDEXER_READY = 0;
	/**  Indicates the watcher should update it's list of collections. */
	public final static int UPDATE_COLLECTIONS = 1;
	/**  Indicates the watcher should begin indexing all collections. */
	public final static int BEGIN_INDEXING_ALL_COLLECTIONS = 2;
	/**  Indicates the watcher should abort indexing immediately. */
	public final static int ABORT_INDEXING = 3;
	/**  Indicates the watcher should update a given collection. */
	public final static int BEGIN_INDEXING_COLLECTION = 4;
	/**  Indicates the watcher should update its configuration to get any changes and (re)initialize. */
	public final static int CONFIGURE_AND_INITIALIZE = 5;	


	/**
	 *  Constructor for the IndexingEvent object
	 *
	 * @param  type               The event type
	 * @param  collectionKey      The collectionKey
	 * @param  collectionIndexer  The CollectionIndexer instance
	 */
	protected IndexingEvent(int type, String collectionKey, CollectionIndexer collectionIndexer) {
		this.collectionIndexer = collectionIndexer;
		this.type = type;
		this.collectionKey = collectionKey;
	}


	/**
	 *  Constructor for the IndexingEvent object
	 *
	 * @param  type               The event type
	 */
	public IndexingEvent(int type) {
		this.type = type;
	}


	/**
	 *  Gets the event type.
	 *
	 * @return    The type value
	 */
	public int getType() {
		return type;
	}


	/**
	 *  Gets the collectionKey attribute of the IndexingEvent object
	 *
	 * @return    The collectionKey value
	 */
	public String getCollectionKey() {
		return collectionKey;
	}


	/**
	 *  Gets the collectionIndexer attribute of the IndexingEvent object
	 *
	 * @return    The collectionIndexer value
	 */
	public CollectionIndexer getCollectionIndexer() {
		return collectionIndexer;
	}


	/**
	 *  A String representation of this event.
	 *
	 * @return    A String representation of this event.
	 */
	public String toString() {
		switch (type) {
						case ABORT_INDEXING:
							return "Abort indexing";
						case BEGIN_INDEXING_ALL_COLLECTIONS:
							return "Begin indexing all collections";
						case BEGIN_INDEXING_COLLECTION:
							return "Begin indexing collection '" + collectionKey + "'";
						case INDEXER_READY:
							return "Indexer ready";
						case UPDATE_COLLECTIONS:
							return "Update collections";
						case CONFIGURE_AND_INITIALIZE:
							return "Configure and initialize";							
		}

		return "";
	}
}

