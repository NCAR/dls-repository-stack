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
package edu.ucar.dls.schemedit.security.login;

import java.io.*;
import java.util.*;
import edu.ucar.dls.schemedit.security.auth.TypedPrincipal;

/**
 *  Class to represent a User for purposes of password (file) based
 *  authentication.
 *
 * @author    Jonathan Ostwald
 * @see       FileLogin
 */
public class LoginUser {
	/**  place holder for password */
	public char password[];
	/**  place holder for principals */
	public Vector principals;


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @return    NOT YET DOCUMENTED
	 */
	public String toPasswdFileEntry() {
		TypedPrincipal p = (TypedPrincipal) principals.get(0);
		return (p.getName() + ":" + new String(password));
	}
}

