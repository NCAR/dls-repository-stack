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

import edu.ucar.dls.repository.*;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.validator.ValidatorForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;

import javax.servlet.http.HttpServletRequest;

import java.io.Serializable;
import java.io.File;

/**
 *  Bean for values used in the OAI Identify request including repository name, description, admin e-mail, and
 *  namespace identifier.
 *
 * @author     John Weatherley
 * @version    $Id: RepositoryInfoForm.java,v 1.3 2009/03/20 23:33:54 jweather Exp $
 */
public final class RepositoryInfoForm extends ValidatorForm implements Serializable {

	private String repositoryName = null;
	private String repositoryDescription = null;
	private String namespaceIdentifier = null;
	private String adminEmail = null;


	/**  Constructor for the RepositoryInfoForm Bean object */
	public RepositoryInfoForm() { }


	/**
	 *  Gets the repositoryName attribute of the RepositoryInfoForm object
	 *
	 * @return    The repositoryName value
	 */
	public String getRepositoryName() {
		return repositoryName;
	}


	/**
	 *  Gets the repositoryDescription attribute of the RepositoryInfoForm object
	 *
	 * @return    The repositoryDescription value
	 */
	public String getRepositoryDescription() {
		return repositoryDescription;
	}


	/**
	 *  Gets the namespaceIdentifier attribute of the RepositoryInfoForm object
	 *
	 * @return    The namespaceIdentifier value
	 */
	public String getNamespaceIdentifier() {
		return namespaceIdentifier;
	}


	/**
	 *  Gets the adminEmail attribute of the RepositoryInfoForm object
	 *
	 * @return    The adminEmail value
	 */
	public String getAdminEmail() {
		return adminEmail;
	}


	/**
	 *  Sets the repositoryName attribute of the RepositoryInfoForm object
	 *
	 * @param  val  The new repositoryName value
	 */
	public void setRepositoryName(String val) {
		repositoryName = ( val == null ? null : val.trim() );
	}


	/**
	 *  Sets the repositoryDescription attribute of the RepositoryInfoForm object
	 *
	 * @param  val  The new repositoryDescription value
	 */
	public void setRepositoryDescription(String val) {
		repositoryDescription = ( val == null ? null : val.trim() );
	}


	/**
	 *  Sets the namespaceIdentifier attribute of the RepositoryInfoForm object
	 *
	 * @param  val  The new namespaceIdentifier value
	 */
	public void setNamespaceIdentifier(String val) {
		namespaceIdentifier = ( val == null ? null : val.trim() );
	}


	/**
	 *  Sets the adminEmail attribute of the RepositoryInfoForm object
	 *
	 * @param  val  The new adminEmail value
	 */
	public void setAdminEmail(String val) {
		adminEmail = ( val == null ? null : val.trim() );
	}

}


