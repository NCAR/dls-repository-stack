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

package edu.ucar.dls.services.dds.action.form;

import edu.ucar.dls.propertiesmgr.*;
import edu.ucar.dls.webapps.tools.*;
import edu.ucar.dls.index.*;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.oai.*;
import edu.ucar.dls.repository.*;
import edu.ucar.dls.dds.action.form.VocabForm;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import java.util.*;
import java.io.*;
import java.text.*;
import java.net.URLEncoder;

/**
 *  A ActionForm bean that holds data for DDS web services and has access to vocab info. For DDSWS v1.1.
 *
 * @author     John Weatherley
 * @version    $Id: DDSServicesForm_1_1.java,v 1.2 2009/03/20 23:33:59 jweather Exp $
 * @see        edu.ucar.dls.services.dds.action.DDSServicesAction_1_1
 */
public class DDSServicesForm_1_1 extends DDSServicesForm {

	// For now, all methods are the same as DDSServicesForm

	/**  Constructor for the RepositoryForm object */
	public DDSServicesForm_1_1() { }

}


