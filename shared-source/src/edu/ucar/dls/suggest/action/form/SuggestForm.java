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

package edu.ucar.dls.suggest.action.form;

import edu.ucar.dls.suggest.SuggestionRecord;
import edu.ucar.dls.xml.schema.SchemaHelper;
import edu.ucar.dls.vocab.MetadataVocab;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 *  Base ActionForm for Suggestor controllers that access a {@link edu.ucar.dls.suggest.SuggestionRecord}.
 *
 * @author     ostwald
 * @version    $Id: SuggestForm.java,v 1.5 2009/03/20 23:34:00 jweather Exp $
 */
public class SuggestForm extends ActionForm {

	private static boolean debug = true;
	
	/**  NOT YET DOCUMENTED */
	protected SchemaHelper schemaHelper = null;

	/**  NOT YET DOCUMENTED */
	protected MetadataVocab vocab = null;
	protected boolean coppa = false;
	protected boolean popup = false;


	/**  Constructor  */
	public SuggestForm() {}


	/**
	 *  Stub for reseting form variables
	 *
	 * @param  mapping  Description of the Parameter
	 * @param  request  Description of the Parameter
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		this.coppa = false;
	}


	/**  resets the bean's key attributes  */
	public void clear() {
		this.coppa = false;
	}

	
	// ------- objects required by jsp -------------
	
	/**
	 *  Sets the vocab attribute of the SuggestForm object
	 *
	 * @param  vocab  The new vocab value
	 */
	public void setVocab(MetadataVocab vocab) {
		this.vocab = vocab;
	}


	/**
	 *  Sets the schemaHelper attribute of the SuggestForm object
	 *
	 * @param  schemaHelper  The new schemaHelper value
	 */
	public void setSchemaHelper(SchemaHelper schemaHelper) {
		this.schemaHelper = schemaHelper;
	}

	public boolean getPopup () {
		return this.popup;
	}
	
	public void setPopup (boolean b) {
		this.popup = b;
	}
	
	public boolean getCoppa () {
		return coppa;
	}

	public void setCoppa (boolean val) {
		this.coppa = val;
	}


	private static void prtln (String s) {
		if (debug)
			edu.ucar.dls.schemedit.SchemEditUtils.prtln(s, "SuggestForm");
	}
	
}

