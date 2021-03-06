/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package edu.ucar.dls.ldap;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.security.MessageDigest;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.NoSuchAttributeException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;


public class NaiveLdapClient extends LdapClient {

	/**
	 *  Constructor for the NaiveLdapClient object
	 *
	 * @param  pfile              NOT YET DOCUMENTED
	 * @exception  LdapException  NOT YET DOCUMENTED
	 */
	public NaiveLdapClient(String pfile)
		 throws LdapException {
		super(pfile);

	} // end constructer



	/**
	 *  Gets the env attribute of the NaiveLdapClient object
	 *
	 * @param  authDn    NOT YET DOCUMENTED
	 * @param  password  NOT YET DOCUMENTED
	 * @return           The env value
	 */
	public Hashtable getEnv(String authDn, String password) {

		String INITCTX = "com.sun.jndi.ldap.LdapCtxFactory";

		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, INITCTX);
		env.put(Context.PROVIDER_URL, hostUrl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, authDn);
		env.put(Context.SECURITY_CREDENTIALS, password);
		env.put(Context.SECURITY_PROTOCOL, "ssl");
		env.put("java.naming.ldap.factory.socket", "edu.ucar.dls.ldap.NaiveSSLSocketFactory");
		return env;
	}

}



