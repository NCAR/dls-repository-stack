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
package edu.ucar.dls.schemedit.url;

import java.util.*;
import java.net.*;
import java.io.UnsupportedEncodingException;

/**
 *  NOT YET DOCUMENTED
 *
 * @author    Jonathan Ostwald
 */
public class QueryString {

	private Map parameters;


	/**
	 *  Constructor for the QueryString object
	 *
	 * @param  qs  NOT YET DOCUMENTED
	 */
	public QueryString(String qs) {
		parameters = new TreeMap();

		// Parse query string
		String pairs[] = qs.split("&");
		for (int i = 0; i < pairs.length; i++) {
			String pair = pairs[i];
			String name;
			String value;
			int pos = pair.indexOf('=');
			// for "n=", the value is "", for "n", the value is null
			if (pos == -1) {
				name = pair;
				value = null;
			}
			else {
				try {
					name = URLDecoder.decode(pair.substring(0, pos), "UTF-8");
					value = URLDecoder.decode(pair.substring(pos + 1, pair.length()), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// Not really possible, throw unchecked
					throw new IllegalStateException("No UTF-8");
				}
			}
			List list = (List) parameters.get(name);
			if (list == null) {
				list = new ArrayList();
				parameters.put(name, list);
			}
			list.add(value);
		}
	}


	/**
	 *  Gets the parameter attribute of the QueryString object
	 *
	 * @param  name  NOT YET DOCUMENTED
	 * @return       The parameter value
	 */
	public String getParameter(String name) {
		List values = (List) parameters.get(name);
		if (values == null)
			return null;

		if (values.size() == 0)
			return "";

		return (String) values.get(0);
	}


	/**
	 *  Gets the parameterValues attribute of the QueryString object
	 *
	 * @param  name  NOT YET DOCUMENTED
	 * @return       The parameterValues value
	 */
	public String[] getParameterValues(String name) {
		List values = (List) parameters.get(name);
		if (values == null)
			return null;

		return (String[]) values.toArray(new String[values.size()]);
	}

	public String getParameterValue (String name) {
		String [] values = getParameterValues(name);
		if (values == null || values.length == 0)
			return null;
		return (String)values[0];
	}

	/**
	 *  Gets the parameterNames attribute of the QueryString object
	 *
	 * @return    The parameterNames value
	 */
	public Enumeration getParameterNames() {
		return Collections.enumeration(parameters.keySet());
	}


	/**
	 *  Gets the parameterMap attribute of the QueryString object
	 *
	 * @return    The parameterMap value
	 */
	public Map getParameterArrayMap() {
		prtln ("getParameterMap()");
		Map map = new TreeMap();
		for (Iterator i = parameters.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry entry = (Map.Entry) i.next();
			prtln ("name: " + entry.getKey());
			List list = (List) entry.getValue();
			String[] values;
			if (list == null)
				values = null;
			else
				values = (String[]) list.toArray(new String[list.size()]);
			map.put(entry.getKey(), values);
		}
		return map;
	}
	
	public Map getParameterMap () {
		return parameters;
	}
	
	private static void prtln (String s) {
		System.out.println (s);
	}
}

