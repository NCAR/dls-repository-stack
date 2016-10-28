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
 *  Copyright 2002-20011 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */
package edu.ucar.dls.util;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 *  A {@link java.net.URLConnection} wrapper that allows a connection timeout to be set, support for gzip
 *  streaming, GET and POST data. A timout is useful for applications that need to retrieve content from a URL
 *  without hanging if the remote server does not respond within a given period of time. Throws a {@link
 *  URLConnectionTimedOutException} if the connection is not made within the allotted time or a {@link
 *  java.io.IOException} if the connection fails for some other reason, such as an HTTP type 500 or 403 error.
 *  <p>
 *
 *  Default http User-Agent is set to 'DLS TimedURLConnection' unless otherwise specified. <p>
 *
 *  The static methods {@link #importURL} and {@link #getInputStream} are provided for convenience. <p>
 *
 *  Example that uses the static getInputStream method:<p>
 *
 *  <code>
 *  import edu.ucar.dls.util.TimedURLConnection;<br>
 *  import edu.ucar.dls.util.URLConnectionTimedOutException;<br>
 *  import org.dom4j.Document;<br>
 *  import org.dom4j.DocumentException;<br>
 *  import org.dom4j.io.SAXReader;<br>
 *  // Plus other imports... <p>
 *
 *  try {<br>
 *  <blockquote> <p>
 *
 *  // Get an input stream for the remote content (throws exeception if timeout occurs):<br>
 *  InputStream istm = TimedURLConnection.getInputStream("http://example.org/remoteData.xml", 2000); <br>
 *  <p>
 *
 *  // Process the InputStream as desired. In this example, the InputStream is used to create a dom4j XML DOM:
 *  <br>
 *  ... <br>
 *  try{<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SAXReader reader = new SAXReader();<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Document document = reader.read(istm);<br>
 *  } catch ( DocumentException e ) {<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Handle the Exception as desired... <br>
 *  }<p>
 *
 *  // Now the DOM is ready for use... <br>
 *  ... <br>
 *  </blockquote> } catch (URLConnectionTimedOutException exc) { <br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// The URLConnection timed out...<br>
 *  } catch (IOException ioe) { <br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// The URLConnection threw an IOException while attempting to connect...<br>
 *  }<br>
 *  </code>
 *
 * @author    John Weatherley
 * @see       java.net.URLConnection
 */
public class TimedURLConnection {

	private final static String defaultUserAgent = "DLS TimedURLConnection";


	/**
	 *  Imports the content of a given URL into a String using the default character encoding, timing out if the
	 *  remote server does not respond within the given number of milliseconds. A timeout set to 0 disables the
	 *  timeout (e.g. timeout of infinity). If the connection is http, then redirects are followed. Uses gzip
	 *  encoding if the server supports it. Throws an IOException if an http connection returns something other
	 *  than status 200.
	 *
	 * @param  url                                 The URL to import
	 * @param  timeOutPeriod                       Milliseconds to wait before timing out
	 * @return                                     A String containing the contents of the URL
	 * @exception  IOException                     If IO Error
	 * @exception  URLConnectionTimedOutException  If timeout occurs before the server responds
	 */
	public static String importURL(String url, int timeOutPeriod)
		 throws IOException, URLConnectionTimedOutException {
		return importURL(url, null, null, timeOutPeriod, null);
	}


	/**
	 *  Imports the content of a given URL into a String using the given character encoding timing out if the
	 *  remote server does not respond within the given number of milliseconds. A timeout set to 0 disables the
	 *  timeout (e.g. timeout of infinity). If the connection is http, then redirects are followed. Uses gzip
	 *  encoding if the server supports it. Throws an IOException if an http connection returns something other
	 *  than status 200.
	 *
	 * @param  url                                 The URL to import
	 * @param  timeOutPeriod                       Milliseconds to wait before timing out
	 * @param  characterEncoding                   The character encoding to use, for example 'UTF-8'
	 * @return                                     A String containing the contents of the URL
	 * @exception  IOException                     If IO Error
	 * @exception  URLConnectionTimedOutException  If timeout occurs before the server responds
	 */
	public static String importURL(String url, String characterEncoding, int timeOutPeriod)
		 throws IOException, URLConnectionTimedOutException {
		return importURL(url, null, characterEncoding, timeOutPeriod, null);
	}


	/**
	 *  Imports the content of a given URL into a String using the given character encoding and http User-Agent,
	 *  timing out if the remote server does not respond within the given number of milliseconds. A timeout set
	 *  to 0 disables the timeout (e.g. timeout of infinity). If the connection is http, then redirects are
	 *  followed. Uses gzip encoding if the server supports it. Throws an IOException if an http connection
	 *  returns something other than status 200.
	 *
	 * @param  url                                 The URL to import
	 * @param  timeOutPeriod                       Milliseconds to wait before timing out
	 * @param  characterEncoding                   The character encoding to use, for example 'UTF-8'
	 * @param  userAgent                           The http User-Agent header sent with the request, or null to
	 *      use default
	 * @return                                     A String containing the contents of the URL
	 * @exception  IOException                     If IO Error
	 * @exception  URLConnectionTimedOutException  If timeout occurs before the server responds
	 */
	public static String importURL(String url, String characterEncoding, int timeOutPeriod, String userAgent)
		 throws IOException, URLConnectionTimedOutException {
		return importURL(url, null, characterEncoding, timeOutPeriod, userAgent);
	}


	/**
	 *  Imports the content of a given URL into a String using the given character encoding, using POST data if
	 *  indicated or GET, timing out if the remote server does not respond within the given number of
	 *  milliseconds. A timeout set to 0 disables the timeout (e.g. timeout of infinity). If the connection is
	 *  http, then redirects are followed. Uses gzip encoding if the server supports it. Throws an IOException if
	 *  an http connection returns something other than status 200.
	 *
	 * @param  url                                 The URL to import
	 * @param  timeOutPeriod                       Milliseconds to wait before timing out
	 * @param  characterEncoding                   The character encoding to use, for example 'UTF-8'
	 * @param  postData                            Data to POST in the request of the form
	 *      parm1=value1&amp;param2=value2 or null to use GET (pass all params in the url)
	 * @param  userAgent                           The http User-Agent header sent with the request, or null to
	 *      use default
	 * @return                                     A String containing the contents of the URL
	 * @exception  IOException                     If IO Error
	 * @exception  URLConnectionTimedOutException  If timeout occurs before the server responds
	 */
	public static String importURL(String url, String postData, String characterEncoding, int timeOutPeriod, String userAgent)
		 throws IOException, URLConnectionTimedOutException {

		InputStream istm = null;
		InputStreamReader inr=null;
		BufferedReader in = null;
		try {
			istm = getInputStream(new URL(url), postData, timeOutPeriod, userAgent);

			if (characterEncoding == null)
				inr = new InputStreamReader(istm);
			else
				inr = new InputStreamReader(istm, characterEncoding);
			in = new BufferedReader(inr);
			int c;
			StringBuffer content = new StringBuffer();
			while ((c = in.read()) != -1)
				content.append((char) c);

			return content.toString();
		} catch (Throwable t) {
			throw new IOException("Error fetching content from URL '" + url + "'. Details: " + t.getMessage());
		}
		finally
		{
			if(in!=null)
				in.close();
			if(inr!=null)
				inr.close();
			if(istm!=null)
				istm.close();
		}
	}


	/**
	 *  Gets an InputStream for the given URL, timing out if the remote server does not respond within the given
	 *  number of milliseconds. A timeout set to 0 disables the timeout (e.g. timeout of infinity). Supports gzip
	 *  compression (returns a GZIPInputStream if the server supports it). If the connection is http, then
	 *  redirects are followed. Throws an IOException if an http connection returns something other than status
	 *  200.
	 *
	 * @param  url                                 The URL to import
	 * @param  timeOutPeriod                       Milliseconds to wait before timing out
	 * @param  userAgent                           The http User-Agent header sent with the request, or null to
	 *      use default
	 * @return                                     An InputStream for the URL
	 * @exception  IOException                     If IO Error
	 * @exception  URLConnectionTimedOutException  If timeout occurs before the server responds
	 */
	public static InputStream getInputStream(String url, int timeOutPeriod, String userAgent)
		 throws IOException, URLConnectionTimedOutException {
		return getInputStream(new URL(url), null, timeOutPeriod, userAgent);
	}


	/**
	 *  Gets an InputStream for the given URL, timing out if the remote server does not respond within the given
	 *  number of milliseconds. A timeout set to 0 disables the timeout (e.g. timeout of infinity). Supports gzip
	 *  compression (returns a GZIPInputStream if the server supports it). If the connection is http, then
	 *  redirects are followed. Throws an IOException if an http connection returns something other than status
	 *  200.
	 *
	 * @param  url                                 The URL to import
	 * @param  timeOutPeriod                       Milliseconds to wait before timing out
	 * @param  userAgent                           The http User-Agent header sent with the request, or null to
	 *      use default
	 * @return                                     An InputStream for the URL
	 * @exception  IOException                     If IO Error
	 * @exception  URLConnectionTimedOutException  If timeout occurs before the server responds
	 */
	public static InputStream getInputStream(URL url, int timeOutPeriod, String userAgent)
		 throws IOException, URLConnectionTimedOutException {
		return getInputStream(url, null, timeOutPeriod, userAgent);
	}


	/**
	 *  Gets an InputStream for the given URL, timing out if the remote server does not respond within the given
	 *  number of milliseconds. A timeout set to 0 disables the timeout (e.g. timeout of infinity). Supports gzip
	 *  compression (returns a GZIPInputStream if the server supports it). If the connection is http, then
	 *  redirects are followed. Throws an IOException if an http connection returns something other than status
	 *  200.
	 *
	 * @param  url                                 The URL to import
	 * @param  timeOutPeriod                       Milliseconds to wait before timing out
	 * @param  postData                            Data to POST in the request of the form
	 *      parm1=value1&amp;param2=value2 or null to use GET (pass all params in the url)
	 * @param  userAgent                           The http User-Agent header sent with the request, or null to
	 *      use default
	 * @return                                     An InputStream for the URL
	 * @exception  IOException                     If IO Error
	 * @exception  URLConnectionTimedOutException  If timeout occurs before the server responds
	 */
	public static InputStream getInputStream(URL url, String postData, int timeOutPeriod, String userAgent)
		 throws IOException, URLConnectionTimedOutException {
		try {

			URLConnection conn = url.openConnection();

			if (userAgent == null)
				userAgent = defaultUserAgent;
			conn.setRequestProperty("User-Agent", userAgent);

			boolean isHttp = false;
			if (conn instanceof HttpURLConnection)
				isHttp = true;

			// Setup the connection:
			conn.setRequestProperty("Connection", "close");
			// Indicate we want gzip if supported:
			conn.setRequestProperty("Accept-Encoding",
				"gzip;q=1.0, identity;q=0.5, *;q=0");

			// Follow HTTP redirects:
			if (isHttp)
				((HttpURLConnection) conn).setFollowRedirects(true);

			// Make the connection and post data if needed:
			try {
				if (timeOutPeriod > 0)
					conn.setReadTimeout(timeOutPeriod);

				// Post data, if requested:
				if (postData != null) {
					conn.setDoOutput(true);
					OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
					wr.write(postData);
					wr.flush();
				}
				conn.connect();
			} catch (SocketTimeoutException ste) {
				throw new URLConnectionTimedOutException("Reached connection time-out period (" + timeOutPeriod + "ms) for URL '" + url + "'. Details: " + ste.getMessage());
			}

			// Check the HTTP response code:
			if (isHttp) {
				int respcode = ((HttpURLConnection) conn).getResponseCode();
				if (respcode > 300 && respcode < 310) {
					System.out.println ("RESPCODE IS " + respcode);
					String newUrl = conn.getHeaderField("Location");
					System.out.println (" - connecting now to " + newUrl);
					return getInputStream(new URL (newUrl), postData, timeOutPeriod, userAgent);
				}
				else if (respcode < 200 || respcode > 299)
					throw new IOException("Invalid HTTP response code: " + respcode);
			}

			// Since we requested qzip output above, check to see if the content was returned
			// in gzip format and use a GZIPInputStream if it was
			InputStream istm = conn.getInputStream();
			String encd = conn.getContentEncoding();
			if (encd != null && encd.equalsIgnoreCase("gzip"))
				istm = new GZIPInputStream(istm);

			return istm;
		} catch (Throwable t) {
			throw new IOException("Error connecting to URL '" + url + "'. Details: " + t.getMessage());
		}
	}



	/**
	 *  Gets the last modified time of the URL, timing out if the remote server does not respond within the given
	 *  number of milliseconds. A timeout set to 0 disables the timeout (e.g. timeout of infinity). Throws an
	 *  IOException if an http connection returns something other than status 200.
	 *
	 * @param  url                                 The URL to check
	 * @param  timeOutPeriod                       Milliseconds to wait before timing out
	 * @param  userAgent                           The http User-Agent header sent with the request, or null to
	 *      use default
	 * @return                                     The last modified time of the URL
	 * @exception  IOException                     If IO Error
	 * @exception  URLConnectionTimedOutException  If timeout occurs before the server responds
	 */
	public static long getUrlLastModifiedTime(URL url, int timeOutPeriod, String userAgent)
		 throws IOException, URLConnectionTimedOutException {
		HttpURLConnection connection = null;

		if (userAgent == null)
			userAgent = defaultUserAgent;
		connection.setRequestProperty("User-Agent", userAgent);

		//Set up the initial connection
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.setReadTimeout(timeOutPeriod);
		connection.connect();
		long lastMod = connection.getLastModified();
		connection.disconnect();
		return lastMod;
	}

}


