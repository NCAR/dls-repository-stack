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
package edu.ucar.dls.schemedit.vocab;

import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;

import org.dom4j.*;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;


public class FieldInfoComparator implements Comparator {
	/**  Used to sort in ascending order. */
	public final static int ASCENDING = 0;
	/**  Used to sort in descending order. */
	public final static int DESCENDING = 1;

	private String fieldName = null;
	private int order;


	/**
	 *  Constructor for this LuceneFieldComparator object.
	 *
	 * @param  fieldName  Field to sort by
	 * @param  sortOrder  ASCENDING or DESCENDING
	 * @deprecated        Sorting should now be done by supplying a {@link org.apache.lucene.search.Sort} object
	 *      at search time. Sorting on returned ResultDocs is less efficient and may cause OutOfMemory errors on
	 *      large result sets.
	 */
	public FieldInfoComparator(String fieldName, int sortOrder) {
		if (fieldName == null)
			fieldName = "";
		else
			this.fieldName = fieldName;
		order = sortOrder;
	}


	/**
	 *  Compares two {@link edu.ucar.dls.index.ResultDoc}s for sorting by a Lucene field, indicated at
	 *  construction time.<p>
	 *
	 *  Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the
	 *  first argument is less than, equal to, or greater than the second.
	 *
	 * @param  o1  The first Object.
	 * @param  o2  The second Object.
	 * @return     An int indicating sort order.
	 */
	public int compare(Object o1, Object o2) {
		String string1 = null;
		String string2 = null;

		// wrap this part in try since getName and getPath throw exceptions (but do they really need to?)
		try {
			if (fieldName.equals("name")) {
				string1 = ((FieldInfoReader) o1).getName();
				string2 = ((FieldInfoReader) o2).getName();
			} else if (fieldName.equals("path")) {
				string1 = ((FieldInfoReader) o1).getPath();
				string2 = ((FieldInfoReader) o2).getPath();
			} else {
				throw new Exception ("WARNING: FieldInfoComparator got unknown fieldName to sort by: " + fieldName);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
		if (string1 == null)
			string1 = "";
		if (string2 == null)
			string2 = "";

		if (order == ASCENDING)
			return string1.compareTo(string2);
		else
			return string2.compareTo(string1);
	}
}

