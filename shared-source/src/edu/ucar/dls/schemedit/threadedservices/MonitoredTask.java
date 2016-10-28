
/**
 *  Copyright 2002, 2003 DLESE Program Center/University Corporation for
 *  Atmospheric Research (UCAR), P.O. Box 3000, Boulder, CO 80307,
 *  support@dlese.org.<p>
 *
 *  This file is part of the DLESE Tools Project.<p>
 *
 *  The DLESE Tools Project is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or (at your
 *  option) any later version.<p>
 *
 *  The DLESE Tools Project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 *  Public License for more details.<p>
 *
 *  You should have received a copy of the GNU General Public License along with
 *  The DLESE System; if not, write to the Free Software Foundation, Inc., 59
 *  Temple Place, Suite 330, Boston, MA 02111-1307 USA
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

