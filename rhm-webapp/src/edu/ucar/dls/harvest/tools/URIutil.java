/* This code was copied directly from the previous version of the ingestor
 * to make sure the urls are exactly the same as they were before the switch
 * over
 */
// dushay - UCAR ingest automation project - august 2003

package edu.ucar.dls.harvest.tools;
/*
 * -----------------------------------------------------------------------------
 * 
 * the Educational Community License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.opensource.org/licenses/ecl1.txt">
 * http://www.opensource.org/licenses/ecl1.txt.</a></p>
 * 
 * <p>Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under the
 * License.</p>
 * 
 * <p>The entire file consists of original code. Copyright &copy; 2004-2007
 * Cornell University. All rights reserved.</p>
 * 
 * -----------------------------------------------------------------------------
 */


import java.io.UnsupportedEncodingException;
import java.net.*;


/** 
 * utility methods to encode and decode URI and URL strings, without
 * affecting the chars that need to be left alone per the URI/URL specs
 * 
 * This need arose because when we use Xerces 2.x (currently 2.3.0)
 * as our XML schema validator, we find we have metadata fields 
 * labeled as using the URI encoding scheme, but not making it 
 * through schema validation.  For example, the "unwise" characters
 * in RFC2396, the URI spec, (amended by RFC2732) are a problem.
 * 
 * So this class is an attempt to come up with a URL/URI string that
 * is properly encoded, including the unwise characters.
 * 
 * I was surprised that I couldn't find java util methods that did 
 * all this for me.  Perhaps I didn't look in the right place.  
 * 
 * @author naomi
 * 
 * @version $Id: URIutil.java,v 1.10 2008/07/09 14:03:22 tcornwell Exp $
 */
public abstract class URIutil
{

//FIXME:  URI spec was updated in January, 2005 -- modifications may be needed
	  
  /** given a string, determine if it is a URI */
  public static boolean isURI(String rawURI)
  {
		if (rawURI == null) 
			return false;
		return (getJavaURI(rawURI) != null);
  }


  /**
   * this is an attempt to match the URI spec as closely as 
   * the Xerces XML schema validator.  In particular, in July 2003,
   * we noted that Xerces did not accept underscores in domain names,
   * nor did it accept "unwise" characters as defined in the URI spec.
   * "unwise" characters are a subset of excluded characters, section
   * 2.4.3.  (RFC2396 and RFC2732)
   * 
   * If the URI is absolute, and not opaque, then we need the 
   * hostname to be valid per the URI spec.
   * @return true if URI is valid, false if not
   * @see java.net.URI.getHost()
   * */
  public static boolean URIisValid(URI javaURI)
  {
		// ensure it starts with a scheme
		if (!javaURI.isAbsolute())
		{
			return false;
		}

		// ensure URI is hierarchical
		if (javaURI.isOpaque())
		{
			return false;
		}
		
		// we only get here for absolute, hierarchical URI
		try
		{
			// we assume should have server based authority
			// this allows us to get appropriate diagnostic information
			javaURI.parseServerAuthority();
			return true;
		}
		catch (URISyntaxException e)
		{
			return false;
		}
  }


	/**
	 * given a string, URL decode it. If the string contains "%" but has illegal
	 * hex characters, then assume the string does NOT need to be URL decoded.
	 * 
	 * @param rawURL - a string containing the URL to be decoded
	 * @param encoding - a string containing the type of character encoding, such
	 *          as "UTF-8"
	 * @return a string containing the URL decoded
	 */
	public static String decodeURL(String rawURL, String encoding)
			throws UnsupportedEncodingException
	{
		if (rawURL == null)
			return null;
		String decoded = null;
		try
		{
			decoded = URLDecoder.decode(rawURL, encoding);
		}
		catch (IllegalArgumentException e)
		{
			String msg = e.getMessage();
			if (msg.indexOf("Illegal hex characters in escape (%) pattern") != 0)
				// implies it doesn't need URL decoding
				return rawURL;
			else
				throw e;
		}
		return decoded;
	}


  /** 
   * given a string, evaluate it as a URI. 
   * Return a string of the URI with characters quoted" as necessary, but
   * not encoded (for "other" characters -- see java doc for java.net.URI)
   * @param rawURI - the supposed URI string.  Expected NOT to have
   *  any XML encodings or URL encodings already present.
   * @param lgr - a Logger for error messages
   * @return URI as a string, quoted properly, but not encoded..  null if the argument
   *  is not a URI.
   * @throws NullPointerException if rawURI is null.
   * */
  public static String quoteURI (String rawURI) 
  {
		if (rawURI == null)
			return null;
		URI javaURI = getJavaURI(rawURI);
		if (javaURI != null)
			return javaURI.toString();
		else
			return null;
  }

  /** 
   * given a string, evaluate it as a URI. 
   * Return a string of the URI with characters properly URI encoded
   * @param rawURI - the supposed URI string.  Expected NOT to have
   *  any XML encodings or URL encodings already present.
   * @param lgr - a Logger for error messages
   * @return URI as a properly encoded string.  null if the argument
   *  is not a URI.
   * */
  public static String encodeURI (String rawURI) 
  {
		if (rawURI == null)
			return null;
		URI javaURI = getJavaURI(rawURI);
		if (javaURI != null)
			return javaURI.toASCIIString();
		else
			return null;
  }
	
	
  
  /** 
   * given a string, evaluate it as an absolute URI, 
   * then evaluate it as a URL. 
   * Return a string containing the URL with characters properly URL encoded
   * 
   * This method is necessary because URLEncoder.encode does NOT pay attention
   * to what part of a URL it's in -- ALL chars get encoded, even if they shouldn't.
   * @param rawURI - the supposed URI string.  Expected to contain an 
   *  absolute URI, not a relativeURI (starts w/ [scheme]: -- see RFC2396).  
   *  Also expected NOT to have any XML encodings or URL encodings 
   *  already present.
   * @param lgr - a Logger to receive error messages
   * @return URI as a properly encoded URL string.  null if the argument
   *  is not a URL.
   * @throws NullPointerException if rawURI is null.
   * */
  public static String encodeURLfromURI(String rawURI) 
  {
		if (rawURI == null)
			return null;

		URI javaURI = getJavaURI(rawURI);
		if (javaURI == null)
			return null;

    // we've got a valid URI -- make it a URL
		String lstrEncodedURL = getEncodedURL(javaURI);
    
    return lstrEncodedURL;
  }
    

	/**
	 * given a string that supposedly contains an XML encoded URI, scrub it. That
	 * is, do things such as: ensure there are no underscores in domain names
	 * ensure that "excluded" characters are properly escaped etc. We require a
	 * pretty strict adherence to the URI spec (RFC2396, amended by RFC2732)
	 * because anyURI types in our XML schema validator (currently Xerces 2.0.1
	 * and/or 2.3.0) are strictly validated. Using this approach allows us to fix
	 * some stuff automagically, and also tends to provide better error messages.
	 * 
	 * @param rawURI - the URI to be scrubbed. We use a crude check to determine 
	 *       if it's already URL encoded:  decode it first, and f we found a non-
	 *       valid URL encoding, then we assume it was NOT already URL encoded.
   * @param lgr - a Logger to receive error messages
	 * @return a String containing a valid URL, or null if the argument cannot be
	 *       scrubbed into a valid URL.
	 */
	public static String scrubURL(String rawURI)
	{
		if (rawURI == null)
			return null;
     
		rawURI = rawURI.trim();								// remove extra spaces
		String lstrOrigURI = rawURI;          //  Save a copy of the trimmed version.
        
    boolean lblnHasEscape = false;
    //  ANY instance of % indicates potential escaped chars in the original
    //  - some of these may be clobbered by the 'scrubURL' 
    //  process - as Naomi notes below.  TC 6/2007
    if (rawURI.indexOf("%")!= -1)
    {
      lblnHasEscape = true;
    }    

		try
		{
			// NOTE: if it was already URL encoded, then the decode-re-encode
			//   piece in some cases could remove correct encoding altogether (e.g.
			// %2F --> /)
			//    BUT the URL should be use-able with those particular encodings
			// removed.
      
      /*  
       *   Note on above:  "...should be use-able..."  unfortunately, not true.
       *   The problem is that the "encoded" %2F is not a directory separator, and
       *   some clients (BioOne) are not using it as such.  Because of this, the
       *   decoded version ("/") is wrong, and, indeed, this process breaks those URLs.   
       */
      
			//
			//  It has to do with java's own URLencode methods
			//   (which always turn %2F into /, for example) and
			//  with our imperfect way of detecting whether something is already URL
			// encoded.

      /* Actaully, the following will change certain encoded & escaped characters like slash, parens,
       *  square brackets, ampersand, ...
       *  into their character equivalents, and...
      */
			String decodedRawURI = decodeURL(rawURI, "UTF-8");         
   
      /*
       * ...this next step will NOT change them all to their previously escaped (%2F, %5C, ...)
       * equivalents.   
       * I believe that correctly encoding all Path (& perhaps Fragment) parts
       * of the URL should be acceptable.
       * 
      */
			String validEncodedURL = encodeURLfromURI(decodedRawURI);           
      
			if (validEncodedURL == null)
			{
				// try URL encoding it without first decoding it
				validEncodedURL = encodeURLfromURI(rawURI);
				if (validEncodedURL == null)
				{
					// try more scrubbing, as basic encoding/decoding approach didn't work
					URI javaURI = scrubbingURIparse(decodedRawURI);
					if (javaURI == null || !URIisValid(javaURI))
					{
						// try scrubbing it without first decoding it
						javaURI = scrubbingURIparse(rawURI);
						if (javaURI == null || !URIisValid(javaURI))
							// okay, give up
							return null;
					}
					// we've now got a valid URI -- make it a URL
					validEncodedURL = getEncodedURL(javaURI);
				}
			} else
      {
        //  URL passed testing, but ...
        //  Test to partially fix encoding/decoding bug for otherwise valid URLs
        if (lblnHasEscape)
        {
          //  URL passed decode-encode test successfully.
          //  If it had escaped chars, some may have been changed.
          //  Return original (trimmed) version.
          
          //  ...with encoded spaces   :-(         
          validEncodedURL = lstrOrigURI.replaceAll(" ", "%20");
        }        
      }      
      return validEncodedURL;
		}
		catch (UnsupportedEncodingException e)
		{	// should never get here -- the files should always be UTF-8.
			return null;
		}
	}

	// =========================== Private methods ===============================

	/** create a java URI object from a string 
	 * 
	 * @param rawURI - the supposed URI string. Expected NOT to have any XML
	 *          encodings or URL encodings already present.
   * @param lgr - a Logger for error messages
	 * @return the java URI object, or null if we weren't able to create a valid 
	 * one from rawURI
	 */
	private static URI getJavaURI(String rawURI)
	{
		URI javaURI = null;
		try
		{
			javaURI = new URI(rawURI);
		}
		catch (URISyntaxException e)
		{
			javaURI = basicURIparse(rawURI);
		}

		if ((javaURI != null) && URIisValid(javaURI))
			return javaURI;
		else
			return null;
	}
	
	
  /**
   * intended for use when URISyntaxException is caught,
   * try to identify the scheme, schemeSpecificPart, and the
   * (possibly missing) fragment pieces of a URI in order
   * to use a multi-argument URI constructor (which will
   * automatically quote characters as necessary.)
   * @param rawURI - the supposed URI string.  Expected to contain an 
   *  absolute URI, not a relativeURI (starts w/ [scheme]: -- see RFC2396).  
   *  Also expected NOT to have any XML encodings or URL encodings 
   *  already present.
   * @param lgr - a Logger for error messages
   * @return derived java.net.URI, or null if one couldn't
   *   be derived from the argument
   * */
  private static URI basicURIparse(String rawURI) 
  {
    int schemeSeparatorIndex = rawURI.indexOf(':');
    if (schemeSeparatorIndex != -1)
    {
			// we believe we have a valid scheme
      String scheme = rawURI.substring(0, schemeSeparatorIndex);
      String afterScheme = rawURI.substring(schemeSeparatorIndex+1);
      try
			{
      	return buildURIFromParts(scheme, afterScheme);
      }
      catch (URISyntaxException e)
      {
      }
    }
    
    return null;
   }


  
  
	/** given a non-null scheme and afterScheme, search for a fragment part, 
	 * then build up URI from the parts
	 * @param scheme - a URI scheme, such as "http" or "ftp".  Must be non-null.
	 * @param afterScheme - what goes after scheme:// such as dns + path
	 * @return a URI built up from the parts
	 * @throws URISyntaxException
	 */
	private static URI buildURIFromParts(String scheme, String afterScheme)
			throws URISyntaxException
	{
//		Assert.assertNotNull("scheme argument is null", scheme);
//		Assert.assertNotNull("afterScheme argument is null", afterScheme);

		// not sure if should be looking for first, last, or only
		//  occurrence of # in order to find a fragment part ...
		//  my best guess is only occurrence
		int fragmentIndex = afterScheme.lastIndexOf('#');
		if (fragmentIndex == -1)
			return (new URI(scheme, afterScheme, null));
		else
			return (new URI(scheme, 
											afterScheme.substring(0, fragmentIndex),
											afterScheme.substring(fragmentIndex + 1)));
	}


	/** 
	 * given a java URI, evaluate it as a URL.  
	 * @param javaURI - a supposedly valid java URI.
   * @param lgr - a Logger for error messages
	 * @return URI as a properly encoded URL string or null if the argument is not
	 *         a URL.
	 */
	private static String getEncodedURL(URI javaURI)
	{

		// we've got a valid URI -- make it a URL
		try
		{
      URL javaURL = null;
			// We want result to have "encoded" as well as "quoted" characters,
			// so we need to start with toASCIIString().

/*      
      String lstrScheme = javaURI.getScheme();
      String lstrHost = javaURI.getHost();
      int lintPort = javaURI.getPort();
      
      String lstrPath = javaURI.getPath();
      String lstrFragment = javaURI.getFragment();
      String lstrQuery = javaURI.getQuery();

      //  MAke all these something we can use - convert nulls to empty string.
      lstrPath = (lstrPath == null) ? "" : lstrPath;
      lstrQuery = (lstrQuery == null) ? "" : ("?" + lstrQuery);
      lstrFragment = (lstrFragment==null) ? "" : ("#" + lstrFragment);

      //  Encode each separately - testing
      //lstrPath = "/" + URLEncoder.encode(lstrPath.substring(1),"UTF-8");  //  This puts a "real" slash at the beginning
      //lstrQuery = URLEncoder.encode(lstrQuery,"UTF-8");
      //lstrFragment = URLEncoder.encode(lstrFragment,"UTF-8");      
      
      //  Put them together
      String lstrEnd = "";
      lstrEnd += lstrPath;
      lstrEnd += lstrQuery;
      lstrEnd += lstrFragment;
      
      javaURL = new URL(lstrScheme,lstrHost,lintPort,lstrEnd);
*/
      
      /*
       *   Problem with encoding here, the toASCIIString will NOT
       *   encode the path or fragment part for values that are already ASCII
       *   like /, [, ], (, ), 
      */      
      String asciiStr = javaURI.toASCIIString();
      
			javaURL = new URL(asciiStr);
			return javaURL.toExternalForm();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	
	/**
	 * intended for use when URISyntaxException is caught when creating a java URI
	 * object.  This method tries to turn the rawURI string into a URI with some
	 * manual parsing. Tries to identify the scheme, schemeSpecificPart, and the 
	 * (possibly missing) fragment pieces of a URI in order to use a 
	 * multi-argument URI constructor (which will automatically quote characters 
	 * as necessary.)
	 * 
	 * @param rawURI - the supposed URI string. Expected to contain an absolute
	 *          URI, not a relativeURI (starts w/ [scheme]: -- see RFC2396). Also
	 *          expected NOT to have any XML encodings or URL encodings already
	 *          present.  Must not be null.
   * @param lgr - a Logger for error messages
	 * @return derived java.net.URI, or null if one couldn't be derived from the
	 *         argument
	 */
	private static URI scrubbingURIparse(String rawURI)
	{

		URI javaURI = null;
		int schemeSeparatorIndex = rawURI.indexOf(":");
		if (schemeSeparatorIndex != -1)
		{
			// we believe we have a valid scheme
			String scheme = rawURI.substring(0, schemeSeparatorIndex);
			String afterScheme = rawURI.substring(schemeSeparatorIndex + 1);
			try
			{
				javaURI = buildURIFromParts(scheme, afterScheme);

				if (!URIisValid(javaURI))
					// perhaps our scheme wasn't valid, but was a silly prefix string 
					// (e.g. "URL: ") in front of a URL.
					javaURI = seekURLinString(afterScheme);
			}
			catch (URISyntaxException e)
			{
				javaURI = seekURLinString(afterScheme);
			}
		}
		else
		// no scheme specified; let's see if we can make a decent guess
		{
			String guessedScheme = null;
			if (rawURI.startsWith("www."))
				guessedScheme = "http";
			else if (rawURI.startsWith("ftp."))
				guessedScheme = "ftp";
			if (guessedScheme != null)
			{
				try
				{
					javaURI = buildURIFromParts(guessedScheme, "//" + rawURI);
				}
				catch (URISyntaxException e)
				{
					// okay, we did our best. we're giving up now.
					// NOTE: could use URL constructor(s), but they don't
					//  help with the URL encoding issues.

				}
			}
		}

		if (javaURI == null)
		{

		}

		return javaURI;
	}

	
	/** given a string that potentially contains a URL, look for likely starting
	 * points of a url, such as "http://" or "www." and try to scrub a URL out 
	 * from that point forward.  	
	 * @param potentialUrlString
   * @param lgr - a Logger for error messages
	 * @return derived java.net.URI, or null if one couldn't be derived from the
	 *         argument
	 */
	private static URI seekURLinString(String potentialUrlString)
	{
		// look for likely starting substrings of URLs
		int additionalSeparatorIndex = potentialUrlString.indexOf("http://");
		if (additionalSeparatorIndex == -1)
		{
			additionalSeparatorIndex = potentialUrlString.indexOf("www.");
			if (additionalSeparatorIndex == -1)
			{
				additionalSeparatorIndex = potentialUrlString.indexOf("ftp://");
				if (additionalSeparatorIndex == -1)
					additionalSeparatorIndex = potentialUrlString.indexOf("ftp.");
			}
		}
		if (additionalSeparatorIndex != -1)
			return scrubbingURIparse(potentialUrlString.substring(additionalSeparatorIndex));
		else
			return null;
	}


  
  
  

  // ===============  unit test (main)  =========================

  


}
