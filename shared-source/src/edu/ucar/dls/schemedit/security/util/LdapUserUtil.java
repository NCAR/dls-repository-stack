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

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.test.TesterUtils;
import edu.ucar.dls.schemedit.security.auth.AuthUtils;
import edu.ucar.dls.schemedit.security.user.*;

import edu.ucar.dls.xml.*;
import edu.ucar.dls.util.Files;

import java.io.*;
import java.util.*;
import org.dom4j.*;

/**
 *  We want to compare the current ncs users with the NSDL Ldap.
 *
 * @author    Jonathan Ostwald
 */
public class LdapUserUtil {

	private static boolean debug = true;
	UserManager userManager = null;
	List ncsUsers = null;
	List userInfos = new ArrayList();
	
	LdapUserUtil (String userDataPath) throws Exception {
		UserManager.setDebug (false);
		try {
			userManager = new UserManager (new File (userDataPath));
		} catch (Exception e) {
			throw new Exception ("could not instantiate userManager: " + e.getMessage());
		}
		ncsUsers = userManager.getUsers();
		for (Iterator i=ncsUsers.iterator();i.hasNext();) {
			User ncsUser = (User)i.next();
			UserInfo userInfo = new UserInfo (ncsUser);
			userInfos.add (userInfo);
			userInfo.report();
			prtln (userInfo.asTabDelimited());
		}
	}
	
	static void getLdapUserInfo(String[] args) throws Exception {
		String username = "ostwald";
		if (args.length > 0)
			username = args[0];
		// verifyUser (username);
		prtln(AuthUtils.getLdapUserInfo(username, "uid").asXML());
	}

	void report () {
		for (Iterator i=this.ncsUsers.iterator();i.hasNext();){
			User ncsUser = (User)i.next();
			String username = ncsUser.getUsername();
			String ncsEmail = ncsUser.getEmail();
			prtln (username + " (" + ncsEmail + ")");
		}
	}

	void writeTabDelimited () {
		List lines = new ArrayList ();
		String header = "username\tncsFullname\tncsEmail\tldapFullName\tldapEmail";
		lines.add (header);
		for (Iterator i=this.userInfos.iterator();i.hasNext();) {
			UserInfo userInfo = (UserInfo)i.next();
			lines.add (userInfo.asTabDelimited());
		}
		String report = AuthUtils.joinTokens(lines, "\n");
		try {
			Files.writeFile (report, "C:/tmp/MgrUserInfo.txt");
		} catch (Exception e) {
			prtln ("write ERROR: " + e.getMessage());
		}
	}
		
		
	
	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  args           NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String args[]) throws Exception {
		// getLdapUserInfo(args);
		String userDataPath = "H:/Documents/NSDL/TransitionToLdap/ncs-users-2010_03_03";
		LdapUserUtil util = new LdapUserUtil (userDataPath);
		// util.report();
		util.writeTabDelimited();
		
/* 		UserInfo userInfo = util.new UserInfo ("ostwald", "ostwald@ucar.edu");
		userInfo.report (); */
	}
		
	
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "");
		}
	}
	
	private static void pp (Node node) {
		prtln (Dom4jUtils.prettyPrint(node));
	}
	
	class UserInfo {
		public String username;
		public String ncsFullname;
		public String ncsEmail;
		public String ldapFullname;
		public String ldapEmail;
		public Element ldapEntry = null;
		
		UserInfo (User ncsUser) {
			this (ncsUser.getUsername(), ncsUser.getEmail());
			this.ncsFullname = ncsUser.getFullName();
		}
		
		UserInfo (String username, String ncsEmail) {
			this.username = username;
			this.ncsEmail = ncsEmail;
			try {
				initFromLdap ();
			} catch (Exception e) {
				prtln ("ldap: " + e.getMessage());
			}
		}
		
		void initFromLdap () throws Exception {
			Document doc = AuthUtils.getLdapUserInfo(username, "uid");
			ldapEntry = (Element) doc.selectSingleNode ("ldapInfo/entry");
			if (ldapEntry != null) {
				ldapFullname = ldapEntry.element("name").getTextTrim();
				ldapEmail = ldapEntry.element("email").getTextTrim();
			}
		}
		
		void report () {
			prtln ("\n" + username + " (" + ncsEmail + ")");
			if (ldapEntry == null)
				prtln ("  ldap: no entry found");
			else
				prtln ("  ldap: " + ldapFullname + " (" + ldapEmail + ")");
		}
		
		/* username | ncsFullName | ncsEmail | ldapFullName | ldapEmail */
		String asTabDelimited () {
			List fields = new ArrayList ();
			fields.add (username);
			fields.add (ncsFullname);
			fields.add (ncsEmail);
			fields.add (ldapFullname != null ? ldapFullname : "");
			fields.add (ldapEmail != null ? ldapEmail : "");
			
			String ret = "";
			for (Iterator i = fields.iterator();i.hasNext();) {
				ret += (String)i.next();
				if (i.hasNext())
					ret += "\t";
			}
			return ret;
		}
		
	}
}

