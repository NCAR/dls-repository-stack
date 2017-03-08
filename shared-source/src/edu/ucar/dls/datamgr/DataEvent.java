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
package edu.ucar.dls.datamgr;

import java.util.*;

/**
 *  Indicates that an event occurred affecting data managed by a DataManager
 *  source. <p>
 *
 *
 *
 * @author     Dave Deniman
 * @version    1.0, 9/30/02
 */
public class DataEvent extends EventObject
{

	private List dataList;


	/**
	 *  Contruct a DataEvent
	 *
	 * @param  dataMgr  The data event source
	 */
	public DataEvent(DataManager dataMgr) {
		super(dataMgr);
	}


	/**
	 *  Convenience method for listeners to retrieve the DataManager event source
	 *
	 * @return          DESCRIPTION
	 */
	public DataManager dataManager() {
		return (DataManager)getSource();
	}


	/**
	 *  Listeners must retrieve the exact data associated with this specific event
	 *
	 * @return    <code>List</code> of added data - generally list of OIDs as
	 *      Strings
	 */
	public List getDataList() {
		return dataList;
	}


	/**
	 *  DataManager should set this list before notifying DataListeners @ data
	 *  <code>List</code> of data - generally list of OIDs as Strings
	 *
	 * @param  data  The new dataList value
	 */
	void setDataList(List data) {
		dataList = data;
	}

}

