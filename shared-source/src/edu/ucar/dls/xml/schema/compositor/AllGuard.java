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

package edu.ucar.dls.xml.schema.compositor;

import edu.ucar.dls.xml.schema.SchemaUtils;
import java.util.*;

import edu.ucar.dls.xml.Dom4jUtils;

import org.dom4j.Element;
import org.dom4j.Node;

/**
 *  CompositorGuard for the All Compositor class.
 *
 * @author     ostwald
 */
public class AllGuard extends CompositorGuard {

	private static boolean debug = false;
	
	/**
	 *  Constructor for the AllGuard object
	 *
	 * @param  compositor       NOT YET DOCUMENTED
	 * @param  instanceElement  NOT YET DOCUMENTED
	 */
	public AllGuard(Compositor compositor, Element instanceElement) {
		this (compositor, instanceElement.elements());
	}

	public AllGuard(Compositor compositor, List instanceList) {
		super(compositor, instanceList);
	}
	
	
	/**
	 *  Returns a list of occurrence instances that can be used to determine whether an instance document element
	 *  satisfies the occurrence constraints of the schema.
	 *
	 * @return                The occurrences value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	protected List getOccurrences() throws Exception {
		prtln ("getOccurrences()");
		occurrenceCounter = new OccurrenceCounter();
		occurrences = new ArrayList();
		List membersSeen = new ArrayList();
		List elements = instanceMembers;

		// walk down the elements
		// we don't want to see any member twice, and no group should have more than one element
		Occurrence occurrence = null;
		if (elements.size() > 0) {
			occurrence = new Occurrence ();
			for (Iterator i = instanceMembers.iterator(); i.hasNext(); ) {
				Element element = (Element) i.next();
				// String name = element.getName();
				String name = element.getQualifiedName();
				if (compositor.getMember(name) == null || membersSeen.contains(name))
					throw new Exception ("Unexpected child element: \"" + name + "\"");
				
				membersSeen.add (name);
				occurrence.elements.add (element);
			}
			occurrences.add (occurrence);
		}
		prtln ("getOccurrences returning " + this.occurrences.size());
		return occurrences;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  node  NOT YET DOCUMENTED
	 * @return       NOT YET DOCUMENTED
	 */
	static String pp(Node node) {
		try {
			return (Dom4jUtils.prettyPrint(node));
		} catch (Exception e) {
			return (e.getMessage());
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	static void prtln(String s) {
		if (debug)
			SchemaUtils.prtln(s, "AllGuard");
	}

}
