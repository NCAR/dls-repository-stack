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

import java.security.*;
import javax.security.auth.*;
import java.util.*;

/** 
 * Assigns an integer "value" to a role to support "inheritance" of role-based permissions
*/

public class Roles {	
	

	public final static Role NO_ROLE = Role.NONE;
	// public final static Role GUEST_ROLE = Role.GUEST;
	public final static Role CATALOGER_ROLE = Role.CATALOGER;
	public final static Role MANAGER_ROLE = Role.MANAGER;
	public final static Role ADMIN_ROLE = Role.ADMIN;
	// public final static Role ROOT_ROLE = Role.ROOT;
	
	
	public enum Role {
		NONE		( "none" ),
		// GUEST 	( "guest" ), // guest is a User, NOT a role ...
		CATALOGER 	( "cataloger" ),
		MANAGER 	( "manager" ),
		ADMIN 		( "admin" );
		// ROOT     ( "root" );
		
		String role;
		
		Role (String _role) {
			this.role = _role;
		}
		
		String _getRole () { 
			return this.role; 
		}
		
		public String toString () {
			return this.role;
		}
		
		public boolean satisfies (Role roleToCheck) {
			return (this.compareTo(roleToCheck) >= 0);
		}
		
		public boolean controls (Role roleToCheck) {
			return (this.compareTo(roleToCheck) > 0);
		}
	}

	public static Role toRole (String str) {
		for (Role r : Role.values())
			if (r.toString().equals (str))
				return r;
		return NO_ROLE;
	}
	
	public static String toString (Role r) {
		return r.toString();
	}
	
	public static EnumSet roles = EnumSet.allOf (Role.class);
	private static Roles instance = null;
	
	public static Roles getInstance() {
		if (instance == null)
			instance = new Roles();
		return instance;
	}
		
	public static void main (String [] args) {
		Role maxRole = Roles.MANAGER_ROLE;
		
		EnumSet roles = getSatisfyingRoles (maxRole);
		for (Iterator i=roles.iterator();i.hasNext();)
			prtln ("\t\"" + (String)i.next() + "\"");
	}
	
	public static EnumSet getSatisfyingRoles(Role maxRole) {
		return EnumSet.range(Role.NONE, maxRole);
	}
	
	static void prtln (String s) {
		System.err.println("\n" + s);
	}
	

}

