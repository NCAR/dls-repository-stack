/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package edu.ucar.dls.repository.indexing;

import javax.servlet.ServletContext;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 *  Normalizes a date String of the form 2012, 2012-02, or 2012-02-03 to day granularity of the form
 *  2012-02-03, suitable date range searches and sorting. Returns null if the input string can not be parsed
 *  as a date.
 *
 * @author    John Weatherley
 */
public class NormalizedDateToDayFieldPreprocessor implements IndexFieldPreprocessor {

	/**
	 *  Normalizes a date String of the form 2012, 2012-02, or 2012-02-03 to day granularity of the form
	 *  2012-02-03, suitable date range searches and sorting. Returns null if the input string can not be parsed
	 *  as a date.
	 *
	 * @param  content       The existing date String
	 * @param  newLuceneDoc  The new Lucene Document
	 * @param  xmlDoc        The XML Document
	 * @return               An array of length 1 with a date String in day granularity, or null
	 */
	public String[] processFieldContent(String content, org.apache.lucene.document.Document newLuceneDoc, org.dom4j.Document xmlDoc) {
		/* Date may come in in one of these forms: 2012, 2012-02, 2012-02-03 and should be output in form 2012-02-03 regardless */
		if (content == null)
			return null;

		SimpleDateFormat df = null;
		Date date = null;
		try {
			df = new SimpleDateFormat("yyyy-MM-dd");
			date = df.parse(content);
		} catch (ParseException pe) {}

		if (date == null) {
			try {
				df = new SimpleDateFormat("yyyy-MM");
				date = df.parse(content);
			} catch (ParseException pe) {}
		}

		if (date == null) {
			try {
				df = new SimpleDateFormat("yyyy");
				date = df.parse(content);
			} catch (ParseException pe) {}
		}

		if (date == null) {
			//System.out.println("Normalized date string from '" + content + "' to 'null'");
			return null;
		}

		df = new SimpleDateFormat("yyyy-MM-dd");
		String datestg = df.format(date);

		//System.out.println("Normalized date string from '" + content + "' to '" +  datestg + "'");

		String[] contentArray = new String[1];
		contentArray[0] = datestg;
		return contentArray;
	}


	/**
	 *  Not used.
	 *
	 * @param  contextConfig  The context configuration object, which is an instance of ServletContext when
	 *      running in a webapp.
	 */
	public void contextConfigListener(Object contextConfig) {
		if (contextConfig != null && contextConfig instanceof ServletContext) {
			// Grab settings as needed from the servlet context...
			//String myConfigSetting = ((ServletContext) contextConfig).getInitParameter("myConfigSetting");
		}
	}

}

