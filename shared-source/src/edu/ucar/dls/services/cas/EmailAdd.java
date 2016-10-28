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

package edu.ucar.dls.services.cas;


/**
 *  Contains info about the email addresses present
 *  in a Record , and the type of email address it is
 *  (person or institute)
 *
 * @author     Sonal Bhushan
 * @created    October 13, 2004
 */
class EmailAdd {



	String email_type;
	//should be an enum
	String email_address;


	/**
	 *Constructor for the EmailAdd object
	 *
	 * @param  type     Person or Institute
	 * @param  address  Email Address
	 */
	EmailAdd(String type, String address) {
		this.email_type = type;
		this.email_address = address;
	}


	/**
	 *  Output the EmailAdd object on System.out for debugging
	 *
	 * @return    Description of the Return Value
	 */
	public String toString() {

		String email = "Email Address : " + this.email_address;
		if (this.email_type.equalsIgnoreCase("INST")){
			email += "  Email Type : Institute ";
		}
		else if (this.email_type.equalsIgnoreCase("PERSON")){
			email += "  Email Type : Person ";
		}

		return email;
	}


	/**
	 *  Gets the emailAddress attribute of the EmailAdd object
	 *
	 * @return    The emailAddress value
	 */
	String getEmailAddress() {
		return this.email_address;
	}


	/**
	 *  Gets the emailType attribute of the EmailAdd object
	 *
	 * @return    The emailType value
	 */
	String getEmailType() {
		return this.email_type;
	}
	

}

