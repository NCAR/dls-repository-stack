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
