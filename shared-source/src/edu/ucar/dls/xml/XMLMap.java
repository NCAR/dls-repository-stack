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

package edu.ucar.dls.xml;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Provides a hashmap-like interface into an XML-based metadata file,
 * hashed by user-defined named keys. The XMLMap stores objects, often
 * strings, which map to a user-defined label. The current version 
 * requires that the mapping be created using a mapping class that has
 * been defined and made available as part of the 
 * <tt>edu.ucar.dls.xml.maps</tt> package, and instantiable via the
 * <tt>XMLMapFactory</tt> class in this package. A future release will
 * utilize XML-based configuration files instead.
 * <p>
 * Although not strictly required, each implementing class should
 * utilize the <tt>init</tt> method for intialization and subsequently
 * call the <tt>destroy</tt> method in order to release resources.
 * 
 * @author	Dave Deniman
 * @version	0.9b, 05/20/02
 */
public interface XMLMap {

	/**
	 * Should initialize members as required.
	 * @return <tt>true</tt> if intialization successful, <tt>false</tt> otherwise
	 */
	public boolean init();

	/**
	 * Should release resources and call the finalize method.
	 */
	public void destroy();
	
	/**
	 * Use this method to populate the <tt>XMLMap</tt> with the desired named values.
	 */
	public void setMap();
	
	/**
	 * Method to retrieve the list of names used to identify desired values.
	 */
	public List getKeys();

	/**
	 * Method to retrieve the list of values stored in this map.
	 */
	public List getValues();

	/**
	 * Accessor method for retrieving a specific named value.
	 */
	public Object getValue(String name);

	/**
	 * Setter method for updating a specific named value.
	 */
	public void setValue(String name, Object xmlObject);
}
