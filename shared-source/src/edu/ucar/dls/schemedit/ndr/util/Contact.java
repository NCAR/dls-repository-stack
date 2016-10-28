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

package edu.ucar.dls.schemedit.ndr.util;

import org.dom4j.*;
import java.util.*;



/**
 *  Class representing an Contact attribute of the serviceDescription
 *
 * @author     Jonathan Ostwald
 * @version    $Id: Contact.java,v 1.3 2009/03/20 23:33:56 jweather Exp $
 */
public class Contact {
	public String name = null;
	public String email = null;
	public String info = null;
	

	/**
	 *  Constructor for the Contact object
	 *
	 * @param  name   NOT YET DOCUMENTED
	 * @param  email  NOT YET DOCUMENTED
	 * @param  info   NOT YET DOCUMENTED
	 */
	public Contact(String name, String email, String info) {
		this.name = name;
		this.email = email;
		this.info = info;
	}
	
	public Contact (Element e) {
		this.name = e.attributeValue("name", null);
		this.email = e.attributeValue("email", null);
	}

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @return    NOT YET DOCUMENTED
	 */
	public Element asElement() {
		Element contact = DocumentHelper.createElement("contact");
		if (name != null)
			contact.addElement("name").setText(name);
		if (email != null)
			contact.addElement("email").setText(email);
		if (info != null)
			contact.addElement("info").setText(info);
		return contact;
	}
	
	private static String getChildText (Element parent, String tag) {
		try {
			return parent.element(tag).getTextTrim();
		} catch (Exception e) {}
		return "";
	}
	
	public static Contact getInstance (Element element) {
		String name = getChildText (element, "name");
		String email = getChildText (element, "email");
		String info = getChildText (element, "info");
		return new Contact (name, email, info);
	}
	
}
