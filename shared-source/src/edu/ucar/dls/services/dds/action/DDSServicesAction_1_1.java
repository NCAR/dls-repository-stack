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

package edu.ucar.dls.services.dds.action;

import edu.ucar.dls.services.dds.action.form.*;
import edu.ucar.dls.dds.*;
import edu.ucar.dls.dds.action.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.repository.*;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.index.*;
import edu.ucar.dls.index.analysis.*;
import org.apache.lucene.queryParser.QueryParser;
import edu.ucar.dls.util.*;
import edu.ucar.dls.index.writer.*;
import edu.ucar.dls.webapps.servlets.filters.GzipFilter;
import edu.ucar.dls.vocab.MetadataVocab;
import edu.ucar.dls.schemedit.SchemEditServlet;

import org.apache.lucene.search.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.util.*;
import java.text.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Locale;
import javax.servlet.ServletContext;
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

/**
 *  An <strong>Action</strong> that handles DDS Web service requests. This class handles DDSWS service version
 *  1.1, overriding methods in the base class as needed.
 *
 * @author     John Weatherley
 * @version    $Id: DDSServicesAction_1_1.java,v 1.5 2009/03/20 23:33:59 jweather Exp $
 * @see        edu.ucar.dls.services.dds.action.form.DDSServicesForm_1_1
 */
public final class DDSServicesAction_1_1 extends DDSServicesAction {

	/**
	 *  Gets the key used for caching the ListCollections response in the application scope. Should be overwritten by
	 *  the subclasses and updated in the ListCollections.jsp page when a new version is created.
	 */
	 protected String getListCollectionsCacheKey() {
		 return "ListCollectionsResponse11";
	 }
	
	/**
	 *  Sets the record XML in the form bean using the stripped but not localized version of XML.
	 *
	 * @param  df          The form
	 * @param  resultDoc   The resultDoc
	 */
	protected void setRecordXml(DDSServicesForm df, ResultDoc resultDoc) {
		df.setRecordXml(((XMLDocReader) resultDoc.getDocReader()).getXmlStripped());
	}
}


