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

package edu.ucar.dls.util;

import java.io.*;
import java.util.*;

/**
 *  Read environment variables from an application.
 *  Code obtained from "Real's How To" at http://www.rgagnon.com/javadetails/java-0150.html
 *
 * @author     Jonathan Ostwald
 * @version    $Id: EnvReader.java,v 1.3 2009/03/20 23:34:00 jweather Exp $
 */
public class EnvReader {
	/**
	 *  Gets the envVars attribute of the EnvReader class
	 *
	 * @return                The envVars value
	 * @exception  Throwable  NOT YET DOCUMENTED
	 */
	public static Properties getEnvVars() throws Throwable {
		Process p = null;
		Properties envVars = new Properties();
		Runtime r = Runtime.getRuntime();
		String OS = System.getProperty("os.name").toLowerCase();
		// System.out.println("os.name: " + OS);
		if (OS.indexOf("windows 9") > -1) {
			p = r.exec("command.com /c set");
		}
		else if ((OS.indexOf("nt") > -1)
			 || (OS.indexOf("windows 2000") > -1)
			 || (OS.indexOf("windows xp") > -1)) {
			// thanks to JuanFran for the xp fix!
			p = r.exec("cmd.exe /c set");
		}
		else {
			// our last hope, we assume Unix (thanks to H. Ware for the fix)
			p = r.exec("env");
		}
		BufferedReader br = new BufferedReader
			(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = br.readLine()) != null) {
			int idx = line.indexOf('=');
			String key = line.substring(0, idx);
			String value = line.substring(idx + 1);
			envVars.setProperty(key, value);
			// System.out.println(key + " = " + value);
		}
		return envVars;
	}


	/**
	 *  Gets the property attribute of the EnvReader class
	 *
	 * @param  prop  NOT YET DOCUMENTED
	 * @return       The property value
	 */
	public static String getProperty(String prop) {
		String ret = "";
		try {
			Properties p = EnvReader.getEnvVars();
			ret = p.getProperty(prop);
			// System.out.println("the current value of " + prop + " is : " + ret);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return ret;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  args  NOT YET DOCUMENTED
	 */
	public static void main(String args[]) {
		// Properties p = EnvReader.getEnvVars();
		String prop = "HOST";
		if (args.length > 0)
			prop = args[0];

		EnvReader.getProperty(prop);
	}
}

