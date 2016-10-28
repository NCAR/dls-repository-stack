package edu.ucar.dls.schemedit.security.auth.nsdl;

import edu.ucar.dls.ldap.*;
import edu.ucar.dls.util.strings.FindAndReplace;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 *  LdapClient for the NSDL Ldap Server. Extends NaiveLdapClient to get around issues recognizing the NSDL
 *  cert as certified rather than "self-signed".
 *
 * @author    Jonathan Ostwald
 */
public class NSDLLdapClient extends NaiveLdapClient {

	private static boolean debug = true;
	private String dnTemplate = null;

	public NSDLLdapClient (String pfile) throws LdapException {
		super(pfile);
		prtln ("hostUrl: " + getProperty( "hostUrl", props, pfile));
		dnTemplate = getProperty( "dnTemplate", props, pfile);
	}
	
	/*
	** make a DN (Distinguished Name) from the provided userName by inserting it into the dnTemplate
	*/
	public String mkUserDn (String userName) {
		return FindAndReplace.replace (dnTemplate, "{0}", userName, false);
	}

	/**
	* get the Uid from the provided dn (Distinguished Name);
	*/
	public String getUid (String dn) {
		// prtln ("getUid from " + dn);
		String uidPat = FindAndReplace.replace (dnTemplate, "{0}", "(.*?)", false);
		// prtln ("uidPat: " + uidPat);
		
		Pattern p = Pattern.compile(uidPat);
		Matcher m = p.matcher(dn);
		if (m.find()) {
			// prtln (" ... found it");
			return m.group(1);
		}
		else {
			// prtln (" ... didn't find it");
			return null;
		}
	}
	
	/**
	 *  The main program for the LdapTester class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		
		String path = "/Users/ostwald/Desktop/NSDL_NCS/auth/NsdlLdap.properties";
		NSDLLdapClient client = new NSDLLdapClient (path);
		
		String username = "xxx";
		String password = "xxx";
		
		boolean ok = false;
		try {
			ok = client.userAuthenticates (username, password);
		} catch (LdapException lex) {
			lex.printStackTrace();
			prtln (lex.getMessage());
			ok = false;
		}
		
		if (ok)
			prtln ("YES!");
		else
			prtln ("NO");
	}



	private static void prtln(String s) {
		if (debug) {
			// System.out.println("AsnDocument: " + s);
			System.out.println(s);
		}
	}
}

