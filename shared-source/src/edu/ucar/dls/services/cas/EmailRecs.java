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
 *  Represents one email address and the records that it is associated with *
 * @author     Sonal Bhushan
 * @created    October 21, 2004
 */
class EmailRecs {

		private String address;
		private String type;
		Rec[] recs;
		private int numRecs;




	/**
	 * Constructor 
	 */
	EmailRecs() {
		this.address = null;
		this.type = null;
		this.recs = null;
		this.numRecs = 0;
	}



	String getAddress() {
		return this.address;
	}


	void setAddress(String add) {
		this.address = add;
	}



	String getType() {
		return this.type;
	}



	void setType(String t) {
		this.type = t;
	}



	void addRec(Rec r) {
		if (numRecs == 0) {
			recs = new Rec[]{r};
			numRecs = 1;
		}
		else {
			Rec[] newrecs = new Rec[numRecs + 1];
			System.arraycopy(recs, 0, newrecs, 0, numRecs);
			newrecs[numRecs] = r;
			recs = newrecs;
			numRecs++;
		}
	}



	
	int getNumRecs(){
		return this.numRecs;	
	}
	


}

