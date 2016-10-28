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

package edu.ucar.dls.webapps.tools;



import java.util.*;
import java.io.*;

/**
 *  This class contains tools for validating form input from users. 
 *
 * @author    John Weatherley
 */
public final class FormValidationTools
{

	/**
	 *  Validates the format of an e-mail address.
	 *
	 * @param  email  The e-mail address to validate.
	 * @return        True iff this e-mail has a valid format.
	 */
	public final static boolean isValidEmail(String email) {
		if (email == null)
			return false;
		
		return (email != null && email.matches("\\S+@(\\S+\\.)+\\S+"));
	}

}


