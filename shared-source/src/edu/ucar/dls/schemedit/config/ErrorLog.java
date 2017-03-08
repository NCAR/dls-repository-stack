/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package edu.ucar.dls.schemedit.config;

import java.util.*;

public class ErrorLog {
	private static boolean debug = true;
	
	private List entries = new ArrayList();
	
	public boolean isEmpty () {
		return entries.isEmpty();
	}
	
	public int size () {
		return entries.size();
	}
	
	public void add (String name, String msg) {
		entries.add (new LogEntry (name, msg));
	}
	
	public List getEntries () {
		return entries;
	}
	
	public void clear() {
		entries.clear();
	}
	
	public  class LogEntry {
		private String name;
		private String msg;
		
		LogEntry (String name, String msg) {
			this.name = name;
			this.msg = msg;
		}
		
		public String getName () {
			return name;
		}
		
		public String getMsg () {
			return msg;
		}
	}
	
}
		
