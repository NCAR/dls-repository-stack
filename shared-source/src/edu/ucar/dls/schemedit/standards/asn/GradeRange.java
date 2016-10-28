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
package edu.ucar.dls.schemedit.standards.asn;

/**
 *  Bean to store the min and max grade levels associated with a controlled
 *  vocab expressing a grade range.
 *
 * @author    Jonathan Ostwald
 */
public class GradeRange {
	private int maxGrade;
	private int minGrade;


	/**
	 *  Constructor for the GradeRangeItem object
	 *
	 * @param  grade  NOT YET DOCUMENTED
	 */
	public GradeRange(int grade) {
		this(grade, grade);
	}


	/**
	 *  Constructor for the GradeRangeItem object
	 *
	 * @param  minGrade  NOT YET DOCUMENTED
	 * @param  maxGrade  NOT YET DOCUMENTED
	 */
	public GradeRange(int minGrade, int maxGrade) {
		this.minGrade = minGrade;
		this.maxGrade = maxGrade;
	}


	/**
	 *  Determines if the extents of this GradeRange contain those of provided gradeRange.
	 *
	 * @param  other  other GradeRange
	 * @return        true if this GradeRange contains other
	 */
	public boolean contains(GradeRange other) {
		return (this.minGrade <= other.minGrade &&
			this.maxGrade >= other.maxGrade);
	}


	/**
	 *  Sets the minGrade attribute of the GradeRange object
	 *
	 * @param  grade  The new minGrade value
	 */
	public void setMinGrade(int grade) {
		this.minGrade = grade;
	}


	/**
	 *  Gets the minGrade attribute of the GradeRange object
	 *
	 * @return    The minGrade value
	 */
	public int getMinGrade() {
		return this.minGrade;
	}


	/**
	 *  Sets the maxGrade attribute of the GradeRange object
	 *
	 * @param  grade  The new maxGrade value
	 */
	public void setMaxGrade(int grade) {
		this.maxGrade = grade;
	}


	/**
	 *  Gets the maxGrade attribute of the GradeRange object
	 *
	 * @return    The maxGrade value
	 */
	public int getMaxGrade() {
		return this.maxGrade;
	}



	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @return    NOT YET DOCUMENTED
	 */
	public String toStr() {
		return "min: " + minGrade + " max: " + maxGrade;
	}
}

