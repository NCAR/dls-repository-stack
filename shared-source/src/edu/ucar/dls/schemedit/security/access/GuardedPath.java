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

package edu.ucar.dls.schemedit.security.access;

import java.util.*;

import org.dom4j.Element;

import org.dom4j.DocumentHelper;
import edu.ucar.dls.schemedit.security.access.UrlPatternMatcher;
import edu.ucar.dls.schemedit.security.access.Roles;
import edu.ucar.dls.schemedit.security.access.Roles.Role;

/**
 *  Wrapper for ActionMapping
 *
 *@author     ostwald
 *@created    August 27, 2006
 */
public class GuardedPath {
	private static boolean debug = true;

	protected String description = "";
	protected UrlPatternMatcher matcher = null;
	protected Role role = Roles.NO_ROLE;
	private String name = null;
	private String urlMapping = null;
	private boolean isActionMapping = false;

	String elementName = "guarded-path";
	List actions = new ArrayList();


	/**
	 *  Constructor for the GuardedPath object
	 *
	 *@param  mapping      Description of the Parameter
	 *@param  description  Description of the Parameter
	 *@param  role        Description of the Parameter
	 */
	public GuardedPath() {
	}

	public GuardedPath (String path) {
		this.matcher = new UrlPatternMatcher (path);
	}

	public GuardedPath (UrlPatternMatcher matcher) {
		setMatcher(matcher);
	}

	public void setMatcher (UrlPatternMatcher matcher) {
		this.matcher = matcher;
	}

	public boolean match (String path) {
		return matcher.match  (path);
	}

/* 	public String getUrlMapping () {
		if (urlMapping == null)
			urlMapping = UrlPattern.getUrlMapping (path);
		return urlMapping;
	} */

	protected String getElementName () {
		return this.elementName;
	}

	/**
	 *  Gets the path attribute of the GuardedPath object
	 *
	 *@return    The path value
	 */
	public String getPath() {
		if (matcher != null)
			return matcher.urlPattern;
		else
			return null;
	}

	public List getActions () {
		return actions;
	}

	public void setActions (List actions) {
		// prtln ("setActions: " + getPath());
		this.actions = actions;
		setIsActionMapping (false);
		if (actions != null && actions.size() == 1) {
			String apPath = ((ActionPath)actions.get(0)).getPath();
			// prtln ("\t singleton: " + apPath);
			if (apPath != null && this.getPath().equals(apPath+".do"))
				setIsActionMapping (true);
		}
/* 		if (getIsActionMapping())
			prtln ("\t IS ActionMapping: ");
		else
			prtln ("\t is NOT  ActionMapping: "); */
	}

/* 	public void setPath (String path) {
		this.matcher = new UrlPatternMatcher;
	} */

	/**
	 *  Gets the role attribute of the GuardedPath object
	 *
	 *@return    The role value
	 */
	public Role getRole() {
		return role;
	}

	/**
	 *  Sets the role attribute of the GuardedPath object
	 *
	 *@param  role  The new role value
	 */
	public synchronized void setRole(Role role) {
		this.role = role;
	}

	public String getName () {
		return name;
	}

	/**
	* Returns true if this GuardedPath is aligned with a single actionMapping
	*/
	public boolean getIsActionMapping () {
		return isActionMapping;
	}

	public void setIsActionMapping (boolean b) {
		isActionMapping = b;
	}

	/**
	 *  Gets the description attribute of the GuardedPath object
	 *
	 *@return    The description value
	 */
	public String getDescription() {
		return description;
	}

	/**
	 *  Sets the description attribute of the GuardedPath object
	 *
	 *@param  d  The new description value
	 */
	public synchronized void setDescription(String d) {
		description = d;
	}

	public Element asElement () {
		Element guardedpath = DocumentHelper.createElement ("guarded-path");
		Element path = guardedpath.addElement ("path");
		path.setText (getPath());
		Element description = guardedpath.addElement ("description");
		description.setText (getDescription());
		Element role = guardedpath.addElement ("role");
		String roleStr = getRole() == null ? "" : getRole().toString();
		role.setText (roleStr);
		return guardedpath;
	}

	protected static void prtln(String s) {
		if (debug) {
			while (s.length() > 0 && s.charAt(0) == '\n') {
				System.out.println("");
				s = s.substring(1);
			}
			System.out.println("GuardedPath: " + s);
		}
	}

}
