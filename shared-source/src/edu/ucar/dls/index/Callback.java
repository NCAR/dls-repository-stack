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
 *  Copyright 2002-2012 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */

package edu.ucar.dls.index;

import org.apache.lucene.document.*;

/**
 *  Interface for callbacks to handle processing Documents.
 *
 * @author    Timo Proescholdt
 */
public interface Callback {

	/**
	 *  Callback method for processing Documents.
	 *
	 * @param  doc            The Document to process
	 * @exception  Exception  If error
	 */
	public void doWithDocument(Document doc) throws Exception;

}
