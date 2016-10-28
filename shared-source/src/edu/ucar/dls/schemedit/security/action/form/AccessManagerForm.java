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

package edu.ucar.dls.schemedit.security.action.form;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


/**

 */

public final class AccessManagerForm extends ActionForm {

	private static boolean debug = false;
	
	public void reset (ActionMapping mapping, HttpServletRequest request) {
	}
	
	private String command;
	
	public String getCommand () {
		return command;
	}
	
	public void setCommand (String cmd) {
		command = cmd;
	}
	
	private boolean newPath = false;
	
	public boolean isNewPath () {
		return newPath;
	}
	
	public void setNewPath (boolean bool) {
		newPath = bool;
	}

	private List actionPaths;
	
	public List getActionPaths () {
		return actionPaths;
	}
	
	public void setActionPaths (List actionPaths) {
		this.actionPaths = actionPaths;
	}
	
	private List guardedPaths;
	
	public List getGuardedPaths () {
		return guardedPaths;
	}
	
	public void setGuardedPaths (List guardedPaths) {
		this.guardedPaths = guardedPaths;
	}
	
	private String path;
	
	public String getPath () {
		return path;
	}
	
	public void setPath (String path) {
		this.path = path;
	}
	
	private String description;
	
	public String getDescription () {
		return description;
	}
	
	public void setDescription (String description) {
		this.description = description;
	}
	
	private String role;
	
	public String getRole () {
		return role;
	}
	
	public void setRole (String role) {
		this.role = role;
	}
	
	private List roleOptions;
	
	public List getRoleOptions () {
		return roleOptions;
	}
	
	public void setRoleOptions (List options) {
		this.roleOptions = options;
	}
	
	static void prtln(String s) {
		while (s.length() > 0 && s.charAt(0) == '\n') {
			System.out.println ("");
			s = s.substring(1);
		}
		System.out.println("AccessManagerForm: " + s);
	}
		
	public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
		prtln ("validate()");
		prtln ("\t command: " + command);
		prtln ("\t path: " + path);
		prtln ("\t description: " + description);
		prtln ("\t role: " + role);
		
	return errors;
	}
	
}
