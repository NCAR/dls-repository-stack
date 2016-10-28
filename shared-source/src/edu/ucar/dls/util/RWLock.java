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

package edu.ucar.dls.util;

public class RWLock {

	/*
	 * The lock variable.
	 * If == 0, there are no readers or writers.
	 * If > 0, there are n readers and no writers;
	 * If < 0, there are -n writers (at most 1).
	 */
	private int numusers;
	private int bugs = 0;

	
	public int getNumusers()
	{
		return numusers;
	}
	
	
	public synchronized void getRead() {
		if (bugs >= 1) prtln("RWLock.getRead: before wait");
		while (numusers < 0) {
			try { wait(); }
			catch( InterruptedException iex) {}
		}
		numusers++;
		if (bugs >= 1) prtln(
			"RWLock.getRead: got it.  New numusers: " + numusers);
	}


	public synchronized void freeRead() {
		numusers--;
		if (bugs >= 1) prtln(
			"RWLock.freeRead: New numusers: " + numusers);
		notifyAll();
	}


	public synchronized void getWrite() {
		if (bugs >= 1) prtln("RWLock.getWrite: before wait");
		while (numusers != 0) {
			try { wait(); }
			catch( InterruptedException iex) {}
		}
		numusers--;
		if (bugs >= 1) prtln(
			"RWLock.getWrite: got it.  New numusers: " + numusers);
	}


	public synchronized void freeWrite() {
		numusers++;
		if (bugs >= 1) prtln(
			"RWLock.freeWrite: New numusers: " + numusers);
		notifyAll();
	}


	private void prtln( String msg) {
		System.out.println( msg);
	}
}
