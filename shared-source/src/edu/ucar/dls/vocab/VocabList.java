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

package edu.ucar.dls.vocab;

import java.util.*;

/**
 *  List/map combination for accessing VocabNodes
 *
 *@author    ryandear
 */
public class VocabList {
	public ArrayList item = new ArrayList();         // List of nodes
	public HashMap map = new HashMap();              // Hash into spots within the list
	public VocabList parent;                         // Parent list of this list
	public String definition;                        // Definition of this list (or sub-list)
	public int groupType = 0;                        // 0 = flyout, 1 = drop-down, 2 = indented
	public String jsVar;                             // treeMenu JS var name
}

