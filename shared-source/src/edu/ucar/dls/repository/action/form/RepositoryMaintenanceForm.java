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
package edu.ucar.dls.repository.action.form;

import java.util.ArrayList;

import edu.ucar.dls.repository.validation.IndexDataBean;

/**
 *  Form class the provides a bean interface to send variables to the repository
 *  Mainteneance jsp page as well as issuing commands via command paramter.
 */
public final class RepositoryMaintenanceForm extends RepositoryForm {

	private ArrayList<IndexDataBean> indexSummaryReports;
	private String currentRepositoryPath;
	private String commandRepositoryPath;
	private boolean  actionRequired;
	private String makeImportedRepositoriesCurrentRepository = null;
	private String errorMsgsRepositoryPath = null;
	
	public String getErrorMsgsRepositoryPath() {
		return errorMsgsRepositoryPath;
	}

	public void setErrorMsgsRepositoryPath(String errorMsgsRepositoryPath) {
		this.errorMsgsRepositoryPath = errorMsgsRepositoryPath;
	}

	public String getCurrentRepositoryPath() {
		return currentRepositoryPath;
	}

	public void setCurrentRepositoryPath(String currentRepositoryPath) {
		this.currentRepositoryPath = currentRepositoryPath;
	}

	public String getCommandRepositoryPath() {
		return commandRepositoryPath;
	}

	public void setCommandRepositoryPath(String commandRepositoryPath) {
		this.commandRepositoryPath = commandRepositoryPath;
	}

	public boolean isActionRequired() {
		return actionRequired;
	}

	public void setActionRequired(boolean actionRequired) {
		this.actionRequired = actionRequired;
	}
	public ArrayList<IndexDataBean> getIndexSummaryReports() {
		return indexSummaryReports;
	}

	public void setIndexDataSummaryReports(
			ArrayList<IndexDataBean> indexSummaryReports) {
		this.indexSummaryReports = indexSummaryReports;
		
	}
	public String getMakeImportedRepositoriesCurrentRepository() {
		return makeImportedRepositoriesCurrentRepository;
	}

	public void setMakeImportedRepositoriesCurrentRepository(
			String makeImportedRepositoriesCurrentRepository) {
		this.makeImportedRepositoriesCurrentRepository = makeImportedRepositoriesCurrentRepository;
	}
	
}


