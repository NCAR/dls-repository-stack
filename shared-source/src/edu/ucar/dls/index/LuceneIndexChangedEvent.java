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

import java.util.*;

/**
 * Indicates that an event occurred affecting data managed by a DataManager source.
 * <p>
 * @author	Dave Deniman
 * @version	1.0,	9/30/02
 */
public class LuceneIndexChangedEvent extends EventObject {

	private LuceneIndexChanger changer;	

    /**
     * Contruct a LuceneIndexEvent
     *
     * @param changer	The object source of the event
     */
    public LuceneIndexChangedEvent(LuceneIndexChanger changer) {
        super(changer);
		this.changer = changer;
    }

	/**
	 * Listeners must retrieve the new path information for the reader
	 *
	 * @return <code>String</code> representing the new IndexReader path
	 */
	public String newReader() {	
		return changer.reader;
	}

}

