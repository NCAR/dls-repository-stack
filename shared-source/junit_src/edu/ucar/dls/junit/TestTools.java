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

package edu.ucar.dls.junit;

//import junit.framework.*;
import java.util.Date;
import java.util.Random;
import java.util.Enumeration;
import java.util.*;

/**
This class holds a number of handy static methods to aid in testing.
*/
public class TestTools
{
	static final Random randgen = new Random(new Date().getTime());
	
	/**
	 * Print the elapsed time that occured beween two points of time as recorded
	 * in java Date objects.
	 * 
	 * @param	start	The start Date.
	 * @param 	end		The end Date.
	 * @param	msg		A message inserted in front of the elapsed time string.
	 */
	public static void printElapsedTime(String msg,Date start,Date end)
	{
		long ms = (end.getTime() - start.getTime())%1000;
		long sec1 = (long)Math.floor((end.getTime() - start.getTime())/1000);
		long min = (long)Math.floor(sec1/60);
		long sec = sec1 - 60*min;		
		long tms = end.getTime() - start.getTime();
		
		prtln(msg + min + " minutes and " + sec + "." + ms + " seconds");
		prtln( "(" + tms + " total milliseconds)");
	}
	
	
	/**
	 * Generate a random alpha string of the given length
	 */
	public static String getRandomAlphaString(int length)
	{
		StringBuffer ret = new StringBuffer(length);
		int i;
		for(int j = 0; j < length; ) 
		{
			i = ((int)(Math.abs( randgen.nextLong()%256)));
			if ( i >= 97 && i <= 122 )
			{
				ret.append((char)i);
				j++;
			}
		}
		return ret.toString();
	}
	
	/**
	 * Generate a random integer >= low and < high
	 */
	public static int getRandomIntBetween(int low, int high)
	{
		int total = high - low;
		return low + ((int)(Math.abs( randgen.nextLong()%total)));
	}
	
	/**
	 * Generate a random string containing extended chars of the given length
	 */
	public static String getRandomCharsString(int length)
	{
		StringBuffer ret = new StringBuffer(length);
		char c;
		for(int j = 0; j < length; ) 
		{
			c = (char)((int)(Math.abs( randgen.nextLong()%256)));
			if ( !Character.isISOControl(c) && !Character.isWhitespace(c) )
			{
				ret.append(c);
				j++;
			}
		}
		return ret.toString();
	}
	
	
	public static void print_char_values()
	{
		StringBuffer ret = new StringBuffer();
		char c;
		for(int j = 0; j < 256; j++) 
		{
			c = (char)j;
			prtln("index: " + j + " is: " + c);
		}
	}
	
	private static int recNum = 0;
	public static String getUniqueID()
	{
	   	recNum++;	
		return "DLESE-" + String.valueOf( new Date().getTime() ) + String.valueOf( recNum ); // ID is time in milliseconds
	}
	
	
	
	private static void prtln(String s)
	{
		System.out.println(s);
	}
	
}
