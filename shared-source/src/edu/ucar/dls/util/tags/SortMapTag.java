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

package edu.ucar.dls.util.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 *  Tag for displaying the contents of a system file
 *
 *@author    ryandear
 */
public class SortMapTag extends TagSupport {
	private String sortKey;
	private String var;
	private Map<String, Map<String, String>> map = null;
	
	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}


	public void setVar(String var) {
		this.var = var;
	}
	public void setMap(Map<String, Map<String, String>> map) {
		this.map = map;
	}

	/**
	 *  Read the file, spit it out
	 *
	 *@exception  JspException  Description of the Exception
	 *@return                   Description of the Return Value
	 */
	public int doStartTag() throws JspException {
		class MapComparator implements Comparator{
			private String key=null;
			public MapComparator(String key)
			{
				this.key = key;
			}
			
			public int compare(Object o1, Object o2) {
				//Map.Entry<String, Map<String, String>> e1 = (Map.Entry<String, Map<String, String>>) o1;
				//Map.Entry<String, Map<String, String>> e2 = (Map.Entry<String, Map<String, String>>) o2; 
				
				Map.Entry<String, Object> e1 = (Map.Entry<String,Object>) o1;
				Map.Entry<String, Object> e2 = (Map.Entry<String, Object>) o2; 
				
				String v1=null;
				String v2=null;
				
				
				if(this.key.equals("key"))
				{
					v1 = e1.getKey();
	                v2 = e2.getKey();
					
				}
				else if(this.key.equals("value"))
				{
					v1 = (String)e1.getValue();
	                v2 = (String)e2.getValue();
				}
				else
				{
					v1 = ((Map<String, String>)e1.getValue()).get(this.key);  
	                v2 = ((Map<String, String>)e2.getValue()).get(this.key);
				}

                return v1.compareTo(v2);
				
			}  
		}
		try
		{
			List<Map<String, String>> entries = new ArrayList(map.entrySet()); 
			Collections.sort(entries, new MapComparator(this.sortKey) );
			pageContext.setAttribute(var, entries);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			pageContext.setAttribute(var, new ArrayList());
		}
		return SKIP_BODY;
	}
}

