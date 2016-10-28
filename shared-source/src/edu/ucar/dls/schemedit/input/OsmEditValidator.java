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

import java.util.*;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;

/**
 *  Extension of SchemEditValidator that implements special (non-schema)
 *  validation for OSM framework.
 *
 * @author    ostwald
 */
public class OsmEditValidator extends SchemEditValidator {

	private static boolean debug = false;
	final String PUBNAME_PATH = "/record/general/pubName";


	/**
	 *  Constructor for the OsmEditValidator object
	 *
	 * @param  sef        Description of the Parameter
	 * @param  framework  Description of the Parameter
	 * @param  request    Description of the Parameter
	 * @param  mapping    NOT YET DOCUMENTED
	 */
	public OsmEditValidator(SchemEditForm sef,
	                        MetaDataFramework framework,
	                        ActionMapping mapping,
	                        HttpServletRequest request) {
		super(sef, framework, mapping, request);
	}


	/**
	 *  Suppress validation of the pubName field, since it is handled in
	 *  validateForm
	 *
	 * @param  inputField  Description of the Parameter
	 * @return             Description of the Return Value
	 */
	protected boolean skipFieldValidation(InputField inputField) {
		return inputField.getNormalizedXPath().equals(PUBNAME_PATH);
	}


	/**
	 *  Custom validator for pubName field that throws a validation error when this
	 *  field is empty, even though the schema allows it to be empty.
	 *
	 * @return    Description of the Return Value
	 */
	public SchemEditActionErrors validateForm() {
		prtln("validateForm()  currentPage: " + this.sef.getCurrentPage());
		SchemEditActionErrors errors = new SchemEditActionErrors(schemaHelper);
		boolean doValidate = false;

		if (this.sef.getCurrentPage().equals("general")) {
			prtln("beginning custom validation");

			// find cases where a url (optional) is defined but the corresponding name (required) is not.
			for (Iterator i = this.im.getInputFields().iterator(); i.hasNext(); ) {
				InputField field = (InputField) i.next();
				prtln(" .. " + field.getXPath() + "\n\t (" + field.getNormalizedXPath() + ")");
				if (field.getNormalizedXPath().equals(PUBNAME_PATH)) {
					prtln("processing: " + field.getXPath());

					String value = field.getValue().trim();
					String xpath = field.getXPath();

					if ((value == null) || (value.length() == 0)) {
						SchemEditErrors.addError(errors, field, "field.required");
						exposeField(field);
					}
					else {
						// there is a value -  validate against schema
						try {
							im.checkValidValue(field);
						} catch (Exception cv) {
							SchemEditErrors.addXSDdatatypeError(errors, field, "invalid.value", cv.getMessage());
							exposeField(field);
						}
					}
				}
			}

		}

		// now call normal validation methods
		errors.add(super.validateForm());

		return errors;
	}


	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "OsmEditValidator");
		}
	}

}

