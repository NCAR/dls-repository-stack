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

package edu.ucar.dls.schemedit.input;

import edu.ucar.dls.schemedit.action.form.SchemEditForm;
import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.XPathUtils;

import java.util.*;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;

/**
Extension of SchemEditValidator that is only concerned with a few msp2-specific fields.
 *
 * @author    ostwald
 *
 *
 */
public class Msp2EditValidator extends SchemEditValidator {

	private static boolean debug = false;
	private final String SUBJECTS_PATH = "/record/general/subjects";

	/**
	 *  Constructor for the Msp2EditValidator object
	 *
	 * @param  sef        Description of the Parameter
	 * @param  framework  Description of the Parameter
	 * @param  request    Description of the Parameter
	 * @param  mapping    NOT YET DOCUMENTED
	 */
	public Msp2EditValidator(SchemEditForm sef,
	                          MetaDataFramework framework,
	                          ActionMapping mapping,
	                          HttpServletRequest request) {
		super (sef, framework, mapping, request);

	}

	public SchemEditActionErrors validateForm() {
		prtln("validateForm()  currentPage: " + this.sef.getCurrentPage());
		SchemEditActionErrors errors = new SchemEditActionErrors(schemaHelper);
		
		// validation: we flag error at the SUBJECTS_PATH level if none of it's children have a value
		if (this.sef.getCurrentPage().equals ("general")) {
			boolean hasValue = false;
			
			prtln ("beginning custom validation");
			// iterate over all children of SUBJECTS_PATH, looking for a non-empty value
			for (Iterator i=this.im.getInputFields().iterator();i.hasNext();) {
				InputField field = (InputField)i.next();
				if (field.getNormalizedXPath().startsWith(SUBJECTS_PATH + "/")) {
					prtln ("looking at " + field.getXPath());
					if (field.getValue().trim().length() > 0) {
						prtln (" .. hasValue! (" + field.getValue() + ")");
						hasValue = true;
						break;
					}
				}
			}
			if (!hasValue) {
				prtln ("adding error!");
				String msg = "At least one child field requires a value";
				SchemEditErrors.addGenericError(errors, SUBJECTS_PATH, msg);
				// exposeField(subjectsField);  / no inputField for this element - should we create one?
			}
		}
		
		errors.add(super.validateForm());

		return errors;
	}
	
	/*
	* Suppress validation of all fields under SUBJECTS_PATH
	*/
	protected boolean skipFieldValidation (InputField inputField) {
		return inputField.getNormalizedXPath().startsWith (SUBJECTS_PATH);
	}
	
	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "Msp2EditValidator");
		}
	}

}

