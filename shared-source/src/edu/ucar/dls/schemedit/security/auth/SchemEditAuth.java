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
package edu.ucar.dls.schemedit.security.auth;

import javax.security.auth.login.*;
import javax.security.auth.*;
import javax.security.auth.callback.*;
import java.io.*;

/**
 *  Auth module for SchemEdit authentication
 *
 * @author    Jonathan Ostwald
 */
public class SchemEditAuth implements Auth {
	private static boolean debug = true;
	private String username;
	private String password;
	private LoginContext lc = null;


	/**
	 *  Constructor for the SchemEditAuth object
	 *
	 * @param  username  the username
	 * @param  password  the password
	 */
	public SchemEditAuth(String username, String password) {
		this.username = username;
		this.password = password;
	}


	/**
	 *  Perform Authentication
	 *
	 * @return    true if authentication succeeds
	 */
	public boolean authenticate() {
		if (username == null || username.trim().length() == 0) {
			return false;
		}
		if (password == null || password.trim().length() == 0) {
			return false;
		}

		try {
			lc = new LoginContext("NCS", new MyCallBackHandler(username, password));
			lc.login();
			// AuthUtils.showSubject(getSubject(), "upon return from LoginContext.login");
		} catch (LoginException le) {
			prtln("loginException: " + le.getMessage());
			// le.printStackTrace();
			return false;
		}
		return true;
	}


	/**
	 *  Gets the subject attribute of the SchemEditAuth object
	 *
	 * @return    The subject value
	 */
	public Subject getSubject() {
		if (lc == null) {
			// either login failed or the authenticate method hasn't been called
			throw new IllegalStateException("either login failed or the authenticate method hasn't been called.");
		}
		else {
			return lc.getSubject();
		}

	}


	private static void prtln(String s) {
		if (debug) {
			edu.ucar.dls.schemedit.SchemEditUtils.prtln(s, "SchemEditAuth");
		}
	}
}

