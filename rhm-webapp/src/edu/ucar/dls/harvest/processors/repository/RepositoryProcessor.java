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
package edu.ucar.dls.harvest.processors.repository;

import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.processors.Processor;
import edu.ucar.dls.harvest.repository.Repository;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 * Abstract Processor class that must extended by any record processors that
 * need to be ran. Run is the only method that is abstract and must be implemented
 * in subclasses. This is a subclass of Processor and exists because processors
 * need an extra parameter of repository in order to useful to validating the 
 * repository
 */
public abstract class RepositoryProcessor extends Processor{
	/**
	 * Abstract method Run which must be implemented
	 * @param repository
	 * @param workspace
	 * @throws HarvestException
	 */
	public abstract void run(Repository repository) throws HarvestException;
	
}
