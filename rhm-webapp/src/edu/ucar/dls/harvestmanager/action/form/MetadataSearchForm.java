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
package edu.ucar.dls.harvestmanager.action.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 *  A Struts ActionForm for the Harvest Records tool
 */
public class MetadataSearchForm extends ActionForm{

	private String[] setSpec = null;
	private String keyword = null;
	private String showResourceInfo = null;
	private String searchOver = null;
	private String[] resultsPerPageOptions = {"10", "20", "50", "100"};
	private String resultsPerPage = null;
	private boolean reset = false;
	
	
	/**
	 * Get the evaluated version of resource Info, since unchecking the checkbox
	 * does not make the variable null again. We use a hidden to force it to be something
	 * besides true
	 * @return
	 */
	public boolean getEvalShowResourceInfo()
	{
		if(this.showResourceInfo==null || this.showResourceInfo.equals("false"))
			return false;
		return true;
	}

	public String getShowResourceInfo() {
		return showResourceInfo;
	}
	public void setShowResourceInfo(String showResourceInfo) {
		this.showResourceInfo = showResourceInfo;
	}
	
	/**
	 * Gets the list of setSpecs, ignoring that of false
	 * @return
	 */
	public String[] getSetSpec() {
		if(this.setSpec!=null && this.setSpec.length>0)
		{
			List<String> setSpecList = new ArrayList<String>(Arrays.asList(this.setSpec));
			if(setSpecList.contains("false"))
				setSpecList.remove("false");
			return setSpecList.toArray(new String[setSpecList.size()]);
		}
		return null;
	}
	public void setSetSpec(String[] setSpec) {
		this.setSpec = setSpec;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getSearchOver() {
		return searchOver;
	}

	public void setSearchOver(String searchOver) {
		this.searchOver = searchOver;
	}
	public String getResultsPerPage() {
		return resultsPerPage;
	}

	public void setResultsPerPage(String resultsPerPage) {
		this.resultsPerPage = resultsPerPage;
	}

	public String[] getResultsPerPageOptions() {
		return resultsPerPageOptions;
	}
	
	/** 
	 * Reset the form if reset is set to true, otherwise don't do anything
	 * since this is called for every request. We only want to reset the form
	 * on certain occasions. Otherwise keep it around for ease of filtering 
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request)
	{
		if(this.reset)
		{
			setSpec = null;
			keyword = null;
			showResourceInfo = null;
			searchOver = null;
			resultsPerPage = null;
		}
		this.reset = false;
		
	}
	public void setReset(boolean reset) {
		this.reset = reset;
	}
}



