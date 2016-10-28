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

package edu.ucar.dls.schemedit.security.util;

import java.io.Serializable;
import java.util.*;

import edu.ucar.dls.repository.RepositoryManager;
import edu.ucar.dls.repository.SetInfo;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.security.user.*;
import edu.ucar.dls.schemedit.security.access.Roles;


/**

 */

public final class AccessUtils {

	private static boolean debug = false;

	public static List getManagedUsers (User sessionUser, List authorizedSets, UserManager userManager) {
		return getManagableUsers (sessionUser, authorizedSets, userManager, null);
	}
	
	/**
	* build a list of users who are managed by the current sessionUser. This list contains
	* all the users who have permission to access any of the collections managed by the sessionUser.
	Admin users manage all other users, including other admins.
	*/
	public static List getManagedUsers (User sessionUser, List authorizedSets, 
										UserManager userManager,
										Comparator comparator) {
		List usernames = new ArrayList();
		List managedUsers = new ArrayList();
		List allUsers = userManager.getUsers();
		prtln ("\ngetManagedUsers() for " + sessionUser.getFirstName());
		if (sessionUser.isAdminUser()) {
			Iterator u = allUsers.iterator();
			while (u.hasNext()) {
				User user = (User)u.next();
				if (user != sessionUser)
					managedUsers.add (user);
			}
		}
		else {
			for (Iterator i=authorizedSets.iterator();i.hasNext();) {
				SetInfo set = (SetInfo)i.next();
				prtln ("\nSet: " + set.getName() + " (" + set.getSetSpec() + ")");
				Iterator u = allUsers.iterator();
				while (u.hasNext()) {
					User user = (User)u.next();
					String username = user.getUsername();
					prtln ("\t" + user.getFirstName());
					if (user == sessionUser || usernames.contains(username)) {
						prtln ("\t\t already on list");
						continue;
					}
					if (sessionUser.hasRole (user.getMaxRole()) && 
						user.hasRole (Roles.CATALOGER_ROLE, set.getSetSpec())) {
						prtln ("\t\t ADDING");
						usernames.add (username);
						managedUsers.add (user);
					}
					else {
						if (!sessionUser.hasRole (user.getMaxRole()))
							prtln ("\t\t sessionUser lacks power to manage this user");
						if (!user.hasRole (Roles.CATALOGER_ROLE, set.getSetSpec()))
							prtln ("\t\t this user has no acess to collection");
					}
						
				}
			}
		}
		if (comparator != null)
			Collections.sort (managedUsers, comparator);
		prtln ("managedUsers:");
		for (Iterator i=managedUsers.iterator();i.hasNext();)
			prtln ("\t" + ((User)i.next()).getFirstName());
		return managedUsers;
	}
	
	public static List getManagableUsers (User sessionUser, List managedUsers, UserManager userManager) {
		return getManagableUsers (sessionUser, managedUsers, userManager, null);
	}
	
	public static List getManagableUsers (User sessionUser, List managedUsers, 
										  UserManager userManager,
										  Comparator comparator) {
		List managableUsers = new ArrayList();
		Iterator allUsers = userManager.getUsers().iterator();
		prtln ("\ngetManagableUsers() for " + sessionUser.getFirstName());
		while (allUsers.hasNext()) {
			User user = (User)allUsers.next();
			String username = user.getUsername();
			if (user == sessionUser)
				continue;
			if (sessionUser.hasRole (user.getMaxRole()) && !managedUsers.contains(user)) {
				managableUsers.add (user);
			}
		}
		if (comparator != null)
			Collections.sort (managableUsers, comparator);
		for (Iterator i=managableUsers.iterator();i.hasNext();)
			prtln ("\t" + ((User)i.next()).getFirstName());
		return managableUsers;
	}

	public static SetInfo getAuthorizedSetInfo (String collection, List authorizedSets) throws Exception {
		
		if (collection == null)
			throw new Exception ("collection request parameter required");

		for (Iterator i=authorizedSets.iterator();i.hasNext();) {
			SetInfo setInfo = (SetInfo)i.next();
			if (setInfo.getSetSpec().equals (collection))
				return setInfo;
		}
		throw new Exception ("User not authorized to assign access to \"" + collection + "\"");
	}
	
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln (s, "AccessUtils");
		}
	}
}

