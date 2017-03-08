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
package org.dlese.dpc.dds;

import java.io.Serializable;

/**
 * Bean that holds info about the DDS repository. {@see edu.ucar.dls.dds.RepositoryInfoBean} instead.
 *
 * @author John Weatherley
 * @deprecated
 */
public class RepositoryInfoBean implements Serializable {
    private String currentRepository = null;
    private String backgroundIndexerRepository = null;
    private int nextBackgroundRepositoryNumber = 2;
    private boolean makeImportedRepositoriesCurrentRepository = true;


    /**
     * Constructor for the RepositoryInfoBean object
     */
    public RepositoryInfoBean() {
    }


    /**
     * Constructor for the RepositoryInfoBean object
     *
     * @param currentRepository           Description of the Parameter
     * @param backgroundIndexerRepository Description of the Parameter
     */
    public RepositoryInfoBean(String currentRepository, String backgroundIndexerRepository) {
        this.currentRepository = currentRepository;
        this.backgroundIndexerRepository = backgroundIndexerRepository;
    }


    /**
     * Returns the value of currentRepository.
     *
     * @return The currentRepository value
     */
    public String getCurrentRepository() {
        return currentRepository;
    }


    /**
     * Sets the value of currentRepository.
     *
     * @param currentRepository The value to assign currentRepository.
     */
    public void setCurrentRepository(String currentRepository) {
        this.currentRepository = currentRepository;
    }


    /**
     * Returns the value of backgroundIndexerRepository.
     *
     * @return The backgroundIndexerRepository value
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

    public void incrementNextBackgroundRepositoryNumber() {
        this.nextBackgroundRepositoryNumber++;
    }

    /**
     * Sets the value of backgroundIndexerRepository.
     *
     * @param backgroundIndexerRepository The value to assign backgroundIndexerRepository.
     */
    public void setBackgroundIndexerRepository(String backgroundIndexerRepository) {
        this.backgroundIndexerRepository = backgroundIndexerRepository;
    }

    public String toString() {
        return "currentRepository:" + currentRepository + " backgroundIndexerRepository:" + backgroundIndexerRepository;
    }

    public boolean isMakeImportedRepositoriesCurrentRepository() {
        return makeImportedRepositoriesCurrentRepository;
    }


    public void setMakeImportedRepositoriesCurrentRepository(
            boolean makeImportedRepositoriesCurrentRepository) {
        this.makeImportedRepositoriesCurrentRepository = makeImportedRepositoriesCurrentRepository;
    }


}

