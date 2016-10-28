package edu.ucar.dls.harvestmanager.action;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.ucar.dls.harvest.scripts.Script;

import javax.servlet.http.*;

/**
 *  Action controller for running scripts using the server configuration
 */
public final class ScriptRunnerAction extends Action {
	
	private static String DEFAULT_SCRIPT_PACKAGE = "edu.ucar.dls.harvest.scripts";
	
	/**
	 * Execute method for the action. This method takes two parameters in 
	 * the request. The script package and script package. Initializes the
	 * script and runs it returning the returned message.
	 */
	public ActionForward execute(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,HttpServletResponse response) 
        throws Exception {
		
		String msg = null;
		
		String scriptPackage = request.getParameter("script_package");
		String scriptName = request.getParameter("script_name");
		if (request.getMethod().equals("POST"))
		{
			if(scriptPackage==null)
				msg = "Script package cannot be empty.";
			else if(scriptName==null)
				msg = "Script name cannot be empty.";
			else
				msg = Script.runScript(String.format("%s.%s", scriptPackage, scriptName));	
			
			request.setAttribute("msg", msg);
		}
		
		// Set it back into the request attributes so we can re-display it on the page
		if(scriptPackage==null)
			request.setAttribute("script_package", DEFAULT_SCRIPT_PACKAGE);
		else
			request.setAttribute("script_package", scriptPackage);
		
		if(scriptName!=null)
			request.setAttribute("script_name", scriptName);

		return mapping.findForward("success");
	}
}