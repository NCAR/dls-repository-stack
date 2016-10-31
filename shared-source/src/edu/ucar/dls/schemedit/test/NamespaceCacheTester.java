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

package edu.ucar.dls.schemedit.test;

import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.*;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.util.Files;
import org.dom4j.*;
import org.dom4j.tree.*;
import org.dom4j.io.*;


/**
 *  Class for testing dom manipulation with help from {@link edu.ucar.dls.xml.schema.SchemaHelper}
 *
 * @author     ostwald<p>
 *
 *      $Id $
 * @version    $Id: NamespaceCacheTester.java,v 1.3 2009/03/20 23:33:58 jweather Exp $
 */
public class NamespaceCacheTester {

	private static boolean debug = true;

	Document doc = null;
	DocumentFactory df = null;
	NamespaceRegistry namespaces = null;
	MyCache namespaceCache = null;
	MyStack stack = null;
	
	/**
	 *  Constructor for the NamespaceCacheTester object
	 *
	 * @param  path  NOT YET DOCUMENTED
	 */
	NamespaceCacheTester() throws Exception {
		df = DocumentFactory.getInstance();
		stack = new MyStack (df);
		namespaces = new NamespaceRegistry ();
		namespaceCache = new MyCache();
		namespaceCache.getCache().clear();

	}


	
	/**
	 *  The main program for the NamespaceCacheTester class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {

		prtln ("\n-----------------------------------\n");
		NamespaceCacheTester t = new NamespaceCacheTester ();

		t.cacheTest ();
		
	}
	
	static String nsToString (Namespace ns) {
		return ns.getPrefix() + " : " + ns.getURI();
	}
	
	void cacheTest () throws Exception {
			
		// URL url2 = new URL ("file:/devel/ostwald/metadata-frameworks/collection-v1.0.00/collection.xsd");
		parseWithSAX (new URL ("http://www.dpc.ucar.edu/people/ostwald/Metadata/NameSpacesPlay/tape.xsd"));
		parseWithSAX (new URL ("http://www.dpc.ucar.edu/people/ostwald/Metadata/NameSpacesPlay/track.xsd"));
		parseWithSAX (new URL ("http://www.dpc.ucar.edu/people/ostwald/Metadata/NameSpacesPlay/cd.xsd"));
		parseWithSAX (new URL ("http://www.dpc.ucar.edu/people/ostwald/Metadata/NameSpacesPlay/album.xsd"));
			
		Map cache = namespaceCache.getCache();
		prtln ("Cache");
		showCache (cache, "\t");
		
		Map uriCache = namespaceCache.getURICache("http://www.newInstance.com/cd");
		prtln ("URI Cache");
		showMap (uriCache, "\t");
		
		Map noPrefixCache = namespaceCache.getNoPrefixCache();
		if (noPrefixCache != null) {
			prtln ("No prefix Cache");
			showMap (noPrefixCache, "\t");
		}
	}
	
	static void showCache (Map cache, String indent) {
		prtln ("Cache has " + cache.size() + " entries");
		for (Iterator i=cache.keySet().iterator();i.hasNext();) {
			String uri = (String)i.next();
			Map subCache = (Map) cache.get (uri);
			prtln (indent + uri + "(" + subCache.size() + ")");
			for (Iterator ii=subCache.keySet().iterator();ii.hasNext();) {
				String prefix = (String)ii.next();
				Namespace ns = (Namespace)subCache.get (prefix);
				prtln (indent + indent + prefix + " -- " + nsToString (ns));
			}
		}
	}
			
	
	static void showMap (Map map, String indent) {
		prtln (indent + "map has " + map.size() + " entries");
		for (Iterator i=map.keySet().iterator();i.hasNext();) {
			String uri = (String)i.next();
			// prtln ("\t" + uri);
			Object o = map.get (uri);
			// prtln (indent + uri + " : " + o.getClass().getName());
			if (o instanceof Namespace)
				prtln (indent + uri + " -- " + nsToString( (Namespace)o));
			if (o instanceof Map) 
				showMap ((Map) o, indent+indent);
		}
	}
	
	private Map getUrisFromScratch () {
		Map uris = new HashMap ();
		Namespace namedDefaultNamespace = namespaces.getNamedDefaultNamespace();
		uris.put (namedDefaultNamespace.getPrefix(), namedDefaultNamespace.getURI());
		return uris;
	}
	
	private Map getUrisFromNR () {
		Map uris = new HashMap ();
		for (Iterator i=namespaces.getPrefixMap().keySet().iterator();i.hasNext();) {
			String prefix = (String)i.next();
			Namespace ns = namespaces.getNSforPrefix(prefix);
/* 			if (prefix.trim().length() > 0)
				uris.put(prefix, ns.getURI()); */
			uris.put(prefix, ns.getURI());
		}
		return uris;
	}
	
	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  n  NOT YET DOCUMENTED
	 */
	private static void pp(Node n) {
		prtln(Dom4jUtils.prettyPrint(n));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("NamespaceCacheTester: " + s);
			System.out.println(s);
		}
	}

	class MyCache extends NamespaceCache {
		public MyCache () {
			super();
		}
		
		protected Map getURICache (String uri) {
			return super.getURICache(uri);
		}
		
		Map getCache () {
			return super.cache;
		}

		Map getNoPrefixCache () {
			return super.noPrefixCache;
		}
	}
	
	class MyStack extends NamespaceStack {
		public MyStack () {
			super();
		}
		
		public Namespace makeNamespace(String prefix, String uri) {
			return super.createNamespace(prefix, uri);
		}
		
		public MyStack (DocumentFactory df) {
			super(df);
		}
		
		public void push (Namespace ns) {
			super.push(ns);
			prtln ("  ... stack (" + size() + ") PUSHED namespace: "  + ns.getPrefix() + ": " + ns.getURI());
		}
	
		public Namespace pop () {
			Namespace ns = super.pop();
			prtln ("  ... stack (" + size() + ") POPPED namespace: "  + ns.getPrefix() + ": " + ns.getURI());
			return ns;
		}
			
		public String toString () {
			String s = "\nNamespace Stack";
			for (int i=0;i<size();i++) {
				Namespace ns = getNamespace (i);
				s += "\n\t" + i + ":  " + ns.getPrefix() + ": " + ns.getURI();
			}
			return s;
		}
		
		Map getCache () {
			return getNamespaceCache();
		}

	}
		
	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  aFile                  NOT YET DOCUMENTED
	 * @return                        NOT YET DOCUMENTED
	 * @exception  DocumentException  NOT YET DOCUMENTED
	 */
	public static Document parseWithSAX(File aFile) throws Exception {
		SAXReader xmlReader = new SAXReader();
		return xmlReader.read(aFile);
	}
	public static Document parseWithSAX(URL url) throws Exception {
		SAXReader xmlReader = new SAXReader();
		return xmlReader.read(url);
	}	
}

