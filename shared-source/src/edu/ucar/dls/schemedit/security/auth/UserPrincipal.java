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

import java.io.*;
import java.security.Principal;

/**
 * TypedPrincipals are derived from, and can be treated like Principals
 * but they also contain extra information about the type of the Principal
 * which can be USER, GROUP or DOMAIN. I'm not 100% certain that this is a
 * good way of doing things. Suggestions welcome.
 *
 * @author Andy Armstrong
 * @version 1.0.3
 */
public class UserPrincipal extends TypedPrincipal implements Principal, Serializable
{
	public UserPrincipal(String name, int type) {
		super (name, type);
	}

	/**
	 * Create a UserPrincipal with a name.
	 *
	 * <p>
	 *
	 * @param name the name for this Principal.
	 * @exception NullPointerException if the <code>name</code>
	 * 			is <code>null</code>.
	 */
	 public UserPrincipal(String name) {
		this(name, USER);
	}


	/**
	 * Create a UserPrincipal with a blank name.
	 *
	 * <p>
	 *
	 */
	public UserPrincipal()
	{
		this("", USER);
	}

}
