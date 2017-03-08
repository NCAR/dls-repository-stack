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
package edu.ucar.dls.schemedit.threadedservices;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.repository.RepositoryService;
import edu.ucar.dls.schemedit.dcs.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.index.*;
import edu.ucar.dls.index.reader.*;

import edu.ucar.dls.util.*;
import edu.ucar.dls.datamgr.*;
import edu.ucar.dls.repository.*;

import java.util.*;
import java.text.*;
import java.io.*;

import javax.servlet.ServletContext;

import org.dom4j.Document;

/**
 *  Provides a monitoring mechanism for threaded tasks
 *
 *@author    ostwald <p>
 *
 */
public abstract class MonitoredTask {

	private TaskProgress progress = null;


	/**
	 *  Constructor for the MonitoredTask object
	 *
	 *@param  index                    Description of the Parameter
	 *@param  dcsDataManager           Description of the Parameter
	 *@param  validatingServiceDataDir  Description of the Parameter
	 */
	public MonitoredTask() {
		// set up
		this.progress = new TaskProgress(this);
	}
	

	public TaskProgress getTaskProgress () {
		return this.progress;
	}
	
	public void setTaskProgress (TaskProgress progress) {
		this.progress = progress;
	}
	
	/**
	 *  Gets the isProcessing attribute of the MonitoredTask object
	 *
	 *@return    The isProcessing value
	 */
	public abstract boolean getIsProcessing();

/* 	public void initProgress (int total) {
		this.progress.init(total);
	}
	
	public void updateProgress (int done) {
		this.progress.update(done);
	}
	
	public float getPercentComplete () {
		return this.progress.getPercentComplete();
	}
	
	public void resetTaskProgress () {
		this.progress.reset();
	} */
	
}

