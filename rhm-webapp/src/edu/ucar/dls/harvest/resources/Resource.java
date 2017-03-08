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
package edu.ucar.dls.harvest.resources;

import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 * Abstract class that should be extended to add new resources for the workspace to use
 */

public abstract class Resource {
	protected Workspace workspace = null;
	
	/**
	 * Clean method that should overwritten to make sure all dependcies are taken
	 * care of. Clean for a resource means that the system is doing using it and
	 * chances are no objects reference it anymore
	 */
	public void clean()
	{
		
	}
	
	/** 
	 * The main constructor for all resources
	 * @param workspace
	 */
	public Resource(Workspace workspace)
	{
		this.workspace = workspace;
	}
}
