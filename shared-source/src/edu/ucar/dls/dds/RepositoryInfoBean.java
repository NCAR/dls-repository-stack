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
package edu.ucar.dls.dds;

import java.io.*;

/**
 *  Bean that holds info about the DDS repository.
 *
 * @author    John Weatherley
 */
public class RepositoryInfoBean implements Serializable {
	private String currentRepository = null;
	private String backgroundIndexerRepository = null;
	private int nextBackgroundRepositoryNumber = 2;
	private boolean makeImportedRepositoriesCurrentRepository = true;
	

	

	/**  Constructor for the RepositoryInfoBean object */
	public RepositoryInfoBean() { }


	/**
	 *  Constructor for the RepositoryInfoBean object
	 *
	 * @param  currentRepository            Description of the Parameter
	 * @param  backgroundIndexerRepository  Description of the Parameter
	 */
	public RepositoryInfoBean(String currentRepository, String backgroundIndexerRepository) {
		this.currentRepository = currentRepository;
		this.backgroundIndexerRepository = backgroundIndexerRepository;
	}


	/**
	 *  Returns the value of currentRepository.
	 *
	 * @return    The currentRepository value
	 */
	public String getCurrentRepository() {
		return currentRepository;
	}


	/**
	 *  Sets the value of currentRepository.
	 *
	 * @param  currentRepository  The value to assign currentRepository.
	 */
	public void setCurrentRepository(String currentRepository) {
		this.currentRepository = currentRepository;
	}


	/**
	 *  Returns the value of backgroundIndexerRepository.
	 *
	 * @return    The backgroundIndexerRepository value
	 */
	public String getBackgroundIndexerRepository() {
		return backgroundIndexerRepository;
	}

	
	public int getNextBackgroundRepositoryNumber() {
		return nextBackgroundRepositoryNumber;
	}


	public void setNextBackgroundRepositoryNumber(int nextBackgroundRepositoryNumber) {
		this.nextBackgroundRepositoryNumber = nextBackgroundRepositoryNumber;
	}

	public void incrementNextBackgroundRepositoryNumber()
	{
		this.nextBackgroundRepositoryNumber++;
	}
	/**
	 *  Sets the value of backgroundIndexerRepository.
	 *
	 * @param  backgroundIndexerRepository  The value to assign backgroundIndexerRepository.
	 */
	public void setBackgroundIndexerRepository(String backgroundIndexerRepository) {
		this.backgroundIndexerRepository = backgroundIndexerRepository;
	}
	
	public String toString() {
		return "currentRepository:" + currentRepository + 	" backgroundIndexerRepository:" + backgroundIndexerRepository;
	}
	public boolean isMakeImportedRepositoriesCurrentRepository() {
		return makeImportedRepositoriesCurrentRepository;
	}


	public void setMakeImportedRepositoriesCurrentRepository(
			boolean makeImportedRepositoriesCurrentRepository) {
		this.makeImportedRepositoriesCurrentRepository = makeImportedRepositoriesCurrentRepository;
	}

	
	
	
}

