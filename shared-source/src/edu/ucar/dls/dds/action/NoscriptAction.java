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

package edu.ucar.dls.dds.action;
import edu.ucar.dls.dds.action.form.*;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.text.*;
import java.util.Hashtable;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import edu.ucar.dls.vocab.*;
/**
 *  Implementation of <strong>Action</strong> that handles display of vocabulary
 *  selections for users with scripting turned off
 *
 * @author    Ryan Deardorff
 */
public final class NoscriptAction extends Action {
	private static boolean debug = false;

	// --------------------------------------------------------- Public Methods

	/**
	 *  Process the specified HTTP request, and create the corresponding HTTP
	 *  response (or forward to another web component that will create it). Return
	 *  an <code>ActionForward</code> instance describing where and how control
	 *  should be forwarded, or <code>null</code> if the response has already been
	 *  completed.
	 *
	 * @param  mapping        The ActionMapping used to select this instance
	 * @param  response       The HTTP response we are creating
	 * @param  form           The ActionForm for the given page
	 * @param  req            The HTTP request.
	 * @return                The ActionForward instance describing where and how
	 *      control should be forwarded
	 * @exception  Exception  If error.
	 */
	public ActionForward execute(
	                              ActionMapping mapping,
	                              ActionForm form,
	                              HttpServletRequest req,
	                              HttpServletResponse response )
		 throws Exception {
		/*
		 *  Design note:
		 *  Only one instance of this class gets created for the app and shared by
		 *  all threads. To be thread-safe, use only local variables, not instance
		 *  variables (the JVM will handle these properly using the stack). Pass
		 *  all variables via method signatures rather than instance vars.
		 */
		DDSQueryForm noscriptForm = (DDSQueryForm)form;
		ActionErrors errors = new ActionErrors();
		MetadataVocab vocab = (MetadataVocab)getServlet().getServletContext().getAttribute( "MetadataVocab" );
		noscriptForm.setVocab( vocab );
		try {
			// Handle software actions:
			if ( req.getParameter( "showVocab" ) != null ) {// Vocab selection display
				String vocabField = req.getParameter( "showVocab" );
				return mapping.findForward( "showVocab_" + vocabField );
			}
			// Default (non-matched) forwarding:
			return mapping.findForward( "error" );
		}
		catch ( NullPointerException e ) {
			prtln( "NoscriptAction caught exception." );
			e.printStackTrace();
			return mapping.findForward( "error" );
		}
		catch ( Throwable e ) {
			prtln( "NoscriptAction caught exception: " + e );
			return mapping.findForward( "error" );
		}
	}


	// ---------------------- Debug info --------------------

	/**
	 *  Return a string for the current time and date, sutiable for display in log
	 *  files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	protected final static String getDateStamp() {
		return
			new SimpleDateFormat( "MMM d, yyyy h:mm:ss a zzz" ).format( new Date() );
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final void prtlnErr( String s ) {
		System.err.println( getDateStamp() + " " + s );
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln( String s ) {
		if ( debug ) {
			System.out.println( getDateStamp() + " " + s );
		}
	}


	/**
	 *  Sets the debug attribute of the object
	 *
	 * @param  db  The new debug value
	 */
	public static void setDebug( boolean db ) {
		debug = db;
	}
}

