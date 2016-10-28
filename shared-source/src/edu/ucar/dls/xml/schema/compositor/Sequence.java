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

package edu.ucar.dls.xml.schema.compositor;

import edu.ucar.dls.xml.schema.ComplexType;
import java.util.*;
import org.dom4j.Element;

/**
 *  Description of the Interface
 *
 *@author    ostwald
 */
public class Sequence extends Compositor {
	

	public Sequence (ComplexType parent) {
		super (parent);
	}
	
	public Sequence (ComplexType parent, Element e) {
		super (parent, e);
	}
	
		
	/**
	* Does the the given instanceElement accept ANY new member?
	
	NOTE: this method does not make sense! What we are interested in, is
	which of the CompositorMembers can accept another occurrence!
	*/
	public boolean acceptsNewMember (Element instanceElement) { 
		SequenceGuard guard;
		try {
			guard = new SequenceGuard (this, instanceElement);
		} catch (Exception e) {
			return false;
		}
		return guard.acceptsNewMember();
	}

	/**
	* Does the the given instanceElement accept a specifical new member at a specific
	location?
	
	WHEN would this be called? Compositors only care whether they can accept another
	OCCURRENCE!
	*/	
	public boolean acceptsNewMember (Element instanceElement, String memberName, int memberIndex) { 
		SequenceGuard guard;
		try {
			guard = new SequenceGuard (this, instanceElement);
		} catch (Exception e) {
			return false;
		}
		return guard.acceptsNewMember(memberName, memberIndex);
	}
	
	public int getType () {
		return Compositor.SEQUENCE;
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
 	 public String toString() {
		 String s = super.toString();
		 // s += "\n\tMY TYPE: " + getType();
		 return s;
	 }


}

