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
package edu.ucar.dls.repository.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import edu.ucar.dls.dds.RepositoryInfoBean;
import edu.ucar.dls.repository.action.form.*;
import edu.ucar.dls.repository.*;
import edu.ucar.dls.repository.validation.IndexDataBean;

import javax.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 Action class that handles rendering the maintenance page and executing the commands
 that can be ran in the page.
 */
public final class RepositoryMaintenanceAction extends Action {

	@Override
	public ActionForward execute(
	                             ActionMapping mapping,
	                             ActionForm form,
	                             HttpServletRequest req,
	                             HttpServletResponse response)
		 throws Exception {

		RepositoryMaintenanceForm rmf = (RepositoryMaintenanceForm) form;
		ServletContext servletContext = getServlet().getServletContext();

		String command = req.getParameter("command");
		Maintenance repositoryMaintenance = (Maintenance) servletContext.getAttribute("repositoryMaintenance");
		
		// If there is a command run it before we try to render the jsp
		if(command!=null)
		{
			String commandRepositoryPath = rmf.getCommandRepositoryPath();
			if(commandRepositoryPath!=null)
			{
				// If a path was sent in, it must be either set to current or ignore
				// a repository
				RepositoryManager rm =
					(RepositoryManager) servletContext.getAttribute("repositoryManager");
				IndexDataBean indexSummary = rm.getIndexSummary(new File(commandRepositoryPath));
				
				if(commandRepositoryPath!=null && command.equals("setToCurrentIndex"))
				{
					// We only want to increment the index status if they are moving the index to an 
					// errored out background index
					if(indexSummary.getIndexStatus().equals(IndexDataBean.STATUS_ERROR))
					{
						// If its a commit to valid repos. We need to mark the repos as
						// valid set it, export and archive it. Which is the exact same
						// thing as commit background, except for the fact that we don't 
						// want to validate the index again. Also this logic
						// might chnage, might not want to export an errored out index
						// that was said to be good
						indexSummary.setIndexStatus(IndexDataBean.STATUS_VALID);
						rm.writeIndexSummary(indexSummary, new File(commandRepositoryPath));
						repositoryMaintenance.setCurrentRepository(commandRepositoryPath, 
								true, false);
						repositoryMaintenance.exportRepository(commandRepositoryPath);
					}
					else
					{
						repositoryMaintenance.setCurrentRepository(commandRepositoryPath, 
							false, true);
					}
				}
				else if(commandRepositoryPath!=null && command.equals("setToIgnored"))
				{
					indexSummary.setIndexStatus(IndexDataBean.STATUS_IGNORE);
					rm.writeIndexSummary(indexSummary, new File(commandRepositoryPath));
					repositoryMaintenance.setCurrentRepository(null, 
							true, true);
				}
				
			}
			else if(command.equals("changeMakeImportedRepositoriesCurrentRepository"))
			{
				// This is a field in the bean that is only used when the indexer imports
				// repositories from external sources instead of indexing itelf
				RepositoryInfoBean infoBean = repositoryMaintenance.getRepositoryInfoBean();
				infoBean.setMakeImportedRepositoriesCurrentRepository(
						Boolean.valueOf(rmf.getMakeImportedRepositoriesCurrentRepository()));
				repositoryMaintenance.saveRepositoryInfoBean(infoBean);
			}
		}
		
		RepositoryManager rm =
			(RepositoryManager) servletContext.getAttribute("repositoryManager");
		
		File repositoryBaseDir = new File(servletContext.getInitParameter("repositoryData"));
		File[] repositoryDirectories = repositoryBaseDir.listFiles();
		ArrayList<IndexDataBean> indexSummaryReports = new ArrayList<IndexDataBean>();
		boolean actionRequired = false;
		// Sort the repositories for display and set wether or not there is an errored
		// out repos
		if(repositoryDirectories!=null && repositoryDirectories.length!=0)
		{
		
			Arrays.sort(repositoryDirectories, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			for(File repositoryDirectory:repositoryDirectories)
			{
				if(!repositoryDirectory.isDirectory() || !repositoryDirectory.getName().startsWith("repository_"))
					continue;

				IndexDataBean indexSummary = rm.getIndexSummary(repositoryDirectory);
				if(indexSummary!=null)
				{
					if(indexSummary.getIndexStatus().equals(IndexDataBean.STATUS_ERROR))
						actionRequired = true;	
					indexSummaryReports.add(indexSummary);
				}
			}
		}
		// Set fields in form bean so they can be seen in the jsp
		rmf.setMakeImportedRepositoriesCurrentRepository(
				String.valueOf(repositoryMaintenance.getRepositoryInfoBean().isMakeImportedRepositoriesCurrentRepository()));
		rmf.setIndexDataSummaryReports(indexSummaryReports);
		rmf.setCurrentRepositoryPath(rm.getRepositoryDirectory().getAbsolutePath());
		rmf.setActionRequired(actionRequired);
		return (mapping.findForward("display.repository.maintenance"));
	}

}
