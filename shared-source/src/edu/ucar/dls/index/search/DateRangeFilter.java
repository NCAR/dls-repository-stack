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

package edu.ucar.dls.index.search;

import java.util.BitSet;
import java.util.Date;
import java.io.IOException;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.search.DocIdSet;

/**
 *  Filters Lucene search results based on a range of Dates or times. This implements similar functionality as
 *  the the deprecated Lucene 1.4 DateFilter. Assumes that fields are indexed using the Lucene DateTools
 *  format of yyyyMMddHHmmss, to a granularity of seconds.
 *
 * @author    John Weatherley
 */
public class DateRangeFilter extends Filter {

	private Filter _myFilter = null;


	/**
	 *  Constructs a filter for field f matching dates between from and to inclusively. Uses time resolution to
	 *  seconds.
	 *
	 * @param  f     The field name
	 * @param  from  From Date
	 * @param  to    To Date
	 */
	public DateRangeFilter(String f, Date from, Date to) {
		String lowerTerm = DateTools.dateToString(from, DateTools.Resolution.SECOND);
		String upperTerm = DateTools.dateToString(to, DateTools.Resolution.SECOND);
		_myFilter = new TermRangeFilter(f, lowerTerm, upperTerm, true, true);
	}


	private DateRangeFilter(Filter myFilter) {
		_myFilter = myFilter;
	}


	/**
	 *  Constructs a filter for field f matching dates on or before date. Uses time resolution to seconds.
	 *
	 * @param  field  Field name
	 * @param  date   The Date
	 * @return        The Filter
	 */
	public static DateRangeFilter Before(String field, Date date) {
		return new DateRangeFilter(TermRangeFilter.Less(field, DateTools.dateToString(date, DateTools.Resolution.SECOND)));
	}


	/**
	 *  Constructs a filter for field f matching dates on or after date. Uses time resolution to seconds.
	 *
	 * @param  field  Field name
	 * @param  date   The Date
	 * @return        The Filter
	 */
	public static DateRangeFilter After(String field, Date date) {
		return new DateRangeFilter(TermRangeFilter.More(field, DateTools.dateToString(date, DateTools.Resolution.SECOND)));
	}


	/**
	 *  Constructs a filter for field f matching times on or before time. Uses time resolution to seconds.
	 *
	 * @param  field  Field name
	 * @param  time   The time
	 * @return        The Filter
	 */
	public static DateRangeFilter Before(String field, long time) {
		return new DateRangeFilter(Before(field, new Date(time)));
	}


	/**
	 *  Constructs a filter for field f matching times on or after time. Uses time resolution to seconds.
	 *
	 * @param  field  Field name
	 * @param  time   The time
	 * @return        The Filter
	 */
	public static DateRangeFilter After(String field, long time) {
		return new DateRangeFilter(After(field, new Date(time)));
	}
	
	public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
		return _myFilter.getDocIdSet(reader);	
	}

}

