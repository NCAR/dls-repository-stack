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

package edu.ucar.dls.dds.action.form;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;

import edu.ucar.dls.dds.action.form.DDSViewResourceForm;
import edu.ucar.dls.repository.*;
import edu.ucar.dls.vocab.*;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.index.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.io.*;
import java.text.*;
import java.net.URLEncoder;
import edu.ucar.dls.dds.action.DDSQueryAction;

/**
 *  A Struts Form bean for handling DDS requests that use controlled vocab
 *  caching (for redisplay in proper order/groupings)
 *
 * @author    Ryan Deardorff
 */
public class VocabCachingActionForm extends ActionForm implements Serializable {
	protected MetadataVocab vocab = null;
	protected Map vocabCache = new HashMap();
	protected String vocabCacheGroup = "";
	protected String vocabInterface = "dds.banner.en-us";// default in case UI person forgets, but they should set it!
	protected String system = "dds.banner.en-us";

	/**
	 *  Sets the vocabInterface attribute of the HistogramForm object
	 *
	 * @param  vocabInterface  The new vocabInterface value
	 */
	public void setVocabInterface( String vocabInterface ) {
		this.vocabInterface = vocabInterface;
		this.system = vocabInterface;
	}

	/**
	 *  Sets the system attribute of the VocabCachingActionForm object
	 *
	 * @param  system  The new system value
	 */
	public void setSystem( String system ) {
		this.system = system;
		this.vocabInterface = system;
	}

	/**
	 *  Gets the system attribute of the VocabCachingActionForm object
	 *
	 * @return    The system value
	 */
	public String getSystem() {
		return system;
	}

	/**
	 *  Sets the vocab attribute of the VocabCachingActionForm object
	 *
	 * @param  vocab  The new vocab value
	 */
	public void setVocab( MetadataVocab vocab ) {
		this.vocab = vocab;
	}

	/**
	 *  Description of the Method
	 */
	public void clearVocabCache() {
		vocabCache.clear();
	}

	/**
	 *  Description of the Method
	 *
	 * @param  vocabValue
	 */
	public void setVocabCacheValue( String vocabValue ) {
		vocabCache.put( vocab.getMetaNameOfId( vocabInterface, "gr", vocabValue ), new Boolean( true ) );
	}

	/**
	 *  Gets the cachedVocabValuesInOrder attribute of the DDSViewResourceForm
	 *  object
	 *
	 * @return    The cachedVocabValuesInOrder value
	 */
	public ArrayList getCachedVocabValuesInOrder() {
		return vocab.getCacheValuesInOrder( vocabInterface, "gradeRange", vocabCache );
	}

	/**
	 *  Gets the cachedVocabValuesLastIndex attribute of the HistogramForm object
	 *
	 * @return    The cachedVocabValuesLastIndex value
	 */
	public String getCachedVocabValuesLastIndex() {
		return new Integer( vocabCache.size() - 1 ).toString();
	}

}

