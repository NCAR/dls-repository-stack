package edu.ucar.dls.harvestmanager.action;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.ucar.dls.harvestmanager.HarvestManager;

import javax.servlet.ServletContext;
import javax.servlet.http.*;

/**
 *  Action controller for re harvesting the collections
 */
public final class ReharvestCollectionsAction extends Action {
	
	/**
	 * Execute method for the action. which can do two things. Start a re-harvest
	 * and stop a re-harvest. It then returns the real time summary details of the
	 * current re-harvest
	 */
	public ActionForward execute(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,HttpServletResponse response) 
        throws Exception {
		
		ServletContext servletContext = getServlet().getServletContext();
		HarvestManager harvestManager = (HarvestManager)servletContext.getAttribute("harvestManager");

		String action = request.getParameter("action");
		String err = null;
		try
		{
			if (request.getMethod().equals("POST") && action!=null)
			{
				if(action.equals("reharvest"))
					harvestManager.reharvestCollections(true);
				else if(action.equals("stop_reharvest"))
					harvestManager.interruptAllIngestorThreads();
				// give a bit of time for the threads to start so the statuses will 
				// show up immediately in the summary
				Thread.sleep(2000);
			}
		}
		catch(Exception e)
		{
			err = "Unable to trigger reharvest: " + e;
		}
		
		if(err==null)
		{
			// Finally we want this attributes on the page but not the form so set them in request
			request.setAttribute("reharvesterSummary", 
					harvestManager.getReharvester().getSummaryMap());
		}
		else
			request.setAttribute("err", err);
		return mapping.findForward("success");
	}
}