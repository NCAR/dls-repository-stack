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
package edu.ucar.dls.services.dds.toolkit;

import org.dom4j.*;
import java.util.*;
import java.text.*;

import edu.ucar.dls.xml.Dom4jUtils;

/**
 *  A List of Java Beans returned from a DDSWS Search or GetRecord request. Creates each Bean Object one time
 *  when it is first accessed and subsequent accesses return a saved instance of the Bean. For a Search
 *  response the List may contain zero or more Beans, for GetRecord the List may contain zero or one. Beans
 *  must have been encoded via the java.beans.XMLEncoder class.
 *
 * @author    John Weatherley
 */
public final class JavaBeanList extends AbstractList {
	private static boolean debug = true;

	private Document responseDocument = null;
	private List beanNodes = null;
	private Map javaBeansMap = null;


	/**
	 *  Constructor for the JavaBeanList object
	 *
	 * @param  responseDocument  The DDSWS response
	 */
	protected JavaBeanList(Document responseDocument) {
		this.responseDocument = responseDocument;
		if (responseDocument != null) {
			beanNodes = responseDocument.selectNodes("/DDSWebService/Search/results/record/metadata/java[@class='java.beans.XMLDecoder']");
			if (beanNodes.size() == 0)
				beanNodes = responseDocument.selectNodes("/DDSWebService/GetRecord/record/metadata/java[@class='java.beans.XMLDecoder']");
			javaBeansMap = new HashMap(beanNodes.size());
		}
	}


	/**
	 *  Gets the Bean at the requested index.
	 *
	 * @param  i                              The index into the list.
	 * @return                                The Bean or Null.
	 * @exception  IndexOutOfBoundsException  If out of bounds
	 */
	public Object get(int i) throws IndexOutOfBoundsException {
		if (beanNodes == null)
			throw new IndexOutOfBoundsException("This is an empty List. The index " + i + " is out of range (index < 0 || index >= size()");
		try {
			Object bean = javaBeansMap.get(i);
			if (bean == null) {
				bean = Dom4jUtils.dom4j2JavaBean((Node) beanNodes.get(i));
				if (bean != null)
					javaBeansMap.put(i, bean);
			}
			return bean;
		} catch (IndexOutOfBoundsException e) {
			throw e;
		} catch (Throwable t) {
			prtlnErr(t.toString());
			return null;
		}
	}


	/**
	 *  Gets the Bean as a dom4j Node.
	 *
	 * @param  i                              The index into the list.
	 * @return                                The as a Node
	 * @exception  IndexOutOfBoundsException  If out of bounds
	 */
	public Node getBeanAsNode(int i) throws IndexOutOfBoundsException {
		return (Node) beanNodes.get(i);
	}


	/**
	 *  Gets the Bean as an XML String.
	 *
	 * @param  i                              The index into the list.
	 * @return                                The as a XML
	 * @exception  IndexOutOfBoundsException  If out of bounds
	 */
	public String getBeanAsXML(int i) throws IndexOutOfBoundsException {
		return ((Node) beanNodes.get(i)).asXML();
	}


	/**
	 *  The size of the List.
	 *
	 * @return    Size
	 */
	public int size() {
		if (beanNodes == null)
			return 0;
		return beanNodes.size();
	}



	// ---------------------- Debug info --------------------

	/**
	 *  Return a string for the current time and date, sutiable for display in log files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	protected final static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final static void prtlnErr(String s) {
		System.err.println(getDateStamp() + " JavaBeanList Error: " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final static void prtln(String s) {
		if (debug) {
			System.out.println(getDateStamp() + " JavaBeanList: " + s);
		}
	}


	/**
	 *  Sets the debug attribute of the object
	 *
	 * @param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}
}

