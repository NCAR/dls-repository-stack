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
import java.security.Principal;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.schemedit.security.auth.TypedPrincipal;
import edu.ucar.dls.schemedit.security.auth.UserPrincipal;
import edu.ucar.dls.schemedit.security.util.Encrypter;


/**
 *  Manages password entries in the password file.
 *
 * @author     Jonathan Ostwald
 */
public class PasswordHelper {
	private static boolean debug = false;

	private File pwdFile;
	private static String pwdFilePath;

	private static PasswordHelper instance = null;


	/**
	 *  All calls to this method must supply the same "pwdPath" argument. <p>
	 *
	 *  This is necessary since FileLogin must call this each time through
	 *  initialize
	 *
	 * @param  pwdPath  NOT YET DOCUMENTED
	 * @return          The instance value
	 */
	public static PasswordHelper getInstance(String pwdPath) {
		prtln("getInstance() with " + pwdPath);
		if (instance == null) {
			if (pwdPath != null) {
				instance = new PasswordHelper(new File(pwdPath));
				pwdFilePath = pwdPath;
			}
			else
				prtlnErr("ERROR: can't instantiate PasswordHelper with null pwdPath!");
		}
		else if (!pwdFilePath.equals(pwdPath)) {
			prtlnErr("ERROR: passwordHelper is already initialized with a different pwdFile - returning null");
			return null;
		}
		return instance;
	}


	/**
	 *  Gets the instance attribute of the PasswordHelper class
	 *
	 * @return    The instance value
	 */
	public static PasswordHelper getInstance() {
		if (instance == null) {
			// prtlnErr ("ERROR: passwordHelper must first be initialized with a password file");
			prtln("passwordHelper has not been initialized with a password file");
			return null;
		}
		return instance;
	}


	/* 	private class User
	{
		char        password[];
		Vector      principals;
	} */
	/**
	 *  Constructor for the PasswordHelper object
	 *
	 * @param  pwdFile  NOT YET DOCUMENTED
	 */
	private PasswordHelper(File pwdFile) {
		prtln ("\n PasswordHelper with file: " + pwdFile.getAbsolutePath());
		this.pwdFile = pwdFile;
	}


	/**
	 *  Creates hashtable of users
	 *
	 * @return                NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public synchronized Hashtable load() throws Exception {
		Hashtable users = readUsersFromFile();
		
		// Test all passwords and encript if one is found that is NOT BASE64
		Iterator userIter = users.values().iterator();
		while (userIter.hasNext()) {
			LoginUser user = (LoginUser)userIter.next();
			String userpass = new String(user.password);
			if (!Encrypter.isBase64(userpass)) {
				// encript and reload
				Encrypter.convert(pwdFile);
				users = readUsersFromFile();
				break;
			}
		}
		return users;
	}
		

	private Hashtable readUsersFromFile () throws Exception {
		BufferedReader r = new BufferedReader(new FileReader(pwdFile));
		Hashtable users = new Hashtable();
		String l = r.readLine();
		while (l != null) {
			int hash = l.indexOf('#');
			if (hash != -1)
		 		l = l.substring(0, hash);
			l = l.trim();
			if (l.length() != 0) {
				StringTokenizer t = new StringTokenizer(l, ":");
				LoginUser u = new LoginUser();
				u.principals = new Vector();
				String user = t.nextToken();
				u.password = t.nextToken().toCharArray();
				u.principals.add(new UserPrincipal(user));
				users.put(user, u);
			}
			l = r.readLine();
		}
		r.close();
		return users;
	}

	/**
	 *  The main program for the PasswordHelper class
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) {
		prtln("hello from password helper: " + args.length + " args provided");
		String username = "jonathan";
		String password = "fooberry";
		if (args.length > 0)
			username = args[0];
		if (args.length > 1)
			password = args[1];
		String path = "/Users/ostwald/Documents/Work/CCS/BSCS_Integration/5-18/5-30-data/REORG/curriculum/dcs_conf/auth/passwd";
		PasswordHelper helper = PasswordHelper.getInstance(path);
		try {
			helper.update(username, Encrypter.encryptBaseSHA164(password));
		} catch (Exception e) {
			prtln("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}


	/**
	 *  Gets the password for supplied username.
	 *
	 * @param  username       username for which to retrieve password.
	 * @return                The password value
	 * @exception  Exception  if User not found for supplied username
	 */
	public String getPassword(String username) throws Exception {
		Hashtable users = load();
		LoginUser user = (LoginUser) users.get(username);
		if (user == null)
			throw new Exception("User not found for \"" + username + "\"");
		return new String(user.password);
	}


	/**
	 *  Updates password file with username and password.
	 *
	 * @param  username       username to be updated
	 * @param  password       password of user
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public synchronized void update (String username, String password) throws Exception {
		prtln("update");
		if (username == null || username.trim().length() == 0) {
			prtlnErr("username not supplied, can't update password file");
			return;
		}

		if (password == null || password.trim().length() == 0) {
			prtlnErr("password not supplied, can't update password file");
			return;
		}

		Hashtable users = load();
		prtln("read " + users.size() + " users");
		LoginUser user = (LoginUser) users.get(username);
		if (user != null) {
			prtln("user exists");
			user.password = password.toCharArray();
		}
		else {
			prtln("user doesn't exist");
			user = new LoginUser();
			user.principals = new Vector();
			// user.principals.add(new UserPrincipal(username, TypedPrincipal.USER));
			user.principals.add(new UserPrincipal(username));
			user.password = password.toCharArray();
			prtln("adding user to users: " + user.toPasswdFileEntry());
			users.put(username, user);
		}
		StringBuffer s = new StringBuffer("# Passwords for edu.ucar.dls.schemedit.security.login.FileLogin");
		for (Iterator i = users.values().iterator(); i.hasNext(); ) {
			LoginUser u = (LoginUser) i.next();
			s.append("\n" + u.toPasswdFileEntry());
		}
		prtln("writing " + users.size() + " users to " + pwdFile);
		Files.writeFile(s + "\n", pwdFile);
	}


	/**
	 *  Writes password file with current user information.
	 *
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public synchronized void remove(String username) throws Exception {
		prtln("update");
		if (username == null || username.trim().length() == 0) {
			prtlnErr("username not supplied, can't update password file");
			return;
		}

		Hashtable users = load();

		users.remove (username);

		StringBuffer s = new StringBuffer("# Passwords for edu.ucar.dls.schemedit.security.login.FileLogin");
		for (Iterator i = users.values().iterator(); i.hasNext(); ) {
			LoginUser u = (LoginUser) i.next();
			s.append("\n" + u.toPasswdFileEntry());
		}
		prtln("writing " + users.size() + " users to " + pwdFile);
		Files.writeFile(s + "\n", pwdFile);
	}



	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	static void prtln(String s) {
		if (debug)
			System.out.println("PasswordHelper: " + s);
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	static void prtlnErr(String s) {
		System.out.println("PasswordHelper: " + s);
	}
}

