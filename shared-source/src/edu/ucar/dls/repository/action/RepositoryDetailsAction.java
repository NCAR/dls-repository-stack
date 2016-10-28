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
package edu.ucar.dls.repository.action;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import edu.ucar.dls.repository.RepositoryManager;
import edu.ucar.dls.repository.action.form.RepositoryDetailsForm;
import edu.ucar.dls.repository.validation.IndexDataBean;

/**
  Action class for rendering the repository details page. Very simple page that just loads the
  repositories indexSummary.xml
 */
public final class RepositoryDetailsAction extends Action {
	/**
	Pull up an index summary page for a given repositorys
	 */
	@Override
	public ActionForward execute(
	                             ActionMapping mapping,
	                             ActionForm form,
	                             HttpServletRequest req,
	                             HttpServletResponse response)
		 throws Exception {

		ServletContext servletContext = getServlet().getServletContext();
		String repositoryName = req.getParameter("repository_name");
		RepositoryDetailsForm rdf = (RepositoryDetailsForm) form;

		if(repositoryName==null)
		{
			String error = "Repository Name is required to get repository";
			return infoErrorResponse(req, error, mapping);
		}
		
		File repositoryBaseDir = new File(servletContext.getInitParameter("repositoryData"));
		File selectedRepositoryDir = new File(repositoryBaseDir, repositoryName);
		if(!selectedRepositoryDir.exists())
		{
			String error = String.format("Repository %s does not exist at %s", repositoryName, repositoryBaseDir.getAbsolutePath());
			return infoErrorResponse(req, error, mapping);
		}
		
		RepositoryManager rm =
			(RepositoryManager) servletContext.getAttribute("repositoryManager");
		IndexDataBean indexSummary = rm.getIndexSummary(selectedRepositoryDir);
		if(indexSummary==null)
		{
			String error = String.format("Repository %s was found but does not have a summaryReport.xml. Make sure this is a valid"+
					"repository that was commited and validated.", repositoryName);
			return infoErrorResponse(req, error, mapping);
		}
		rdf.setIndexSummary(indexSummary);
		return (mapping.findForward("display.repository.details"));
	}
	
	/**
	 * Return a error response to the page given an error
	 * @param req
	 * @param error
	 * @param mapping
	 * @return
	 */
	private ActionForward infoErrorResponse(HttpServletRequest req, String error, ActionMapping mapping)
	{
		ActionErrors errors = new ActionErrors();
		errors.add("error", new ActionMessage("generic.error", error));
		addErrors(req, errors);
		return (mapping.findForward("display.repository.details"));
	}

}

