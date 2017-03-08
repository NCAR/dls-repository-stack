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
package edu.ucar.dls.harvest.scripts;

/**
 * Abstract class that should be extended when you want your script to be runnable 
 * through the script tool on the website
 */

public abstract class Script {
	public abstract String run() throws Exception;
	public static String runScript(String path)
	{
		String msg = null;
		try
		{
			msg = ((Script)Class.forName(path).newInstance()).run();
		}
		catch(Exception e)
		{
			msg = "Exception happened during the running of script: " + e;
		}
		return msg;
	}
}
