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